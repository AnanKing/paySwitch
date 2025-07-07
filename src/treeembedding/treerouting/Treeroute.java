package treeembedding.treerouting;

import gtna.data.Single;
import gtna.graph.Edge;
import gtna.graph.Graph;
import gtna.graph.Node;
import gtna.graph.spanningTree.ParentChild;
import gtna.graph.spanningTree.SpanningTree;
import gtna.graph.weights.EdgeWeights;
import gtna.io.DataWriter;
import gtna.metrics.Metric;
import gtna.networks.Network;
import gtna.transformation.spanningtree.MultipleSpanningTree;
import gtna.util.Distribution;

import java.util.*;

import treeembedding.Util;
import treeembedding.credit.CreditLinks;
import treeembedding.other.SwitchPayment;
import treeembedding.other.WriteToTxt;

public abstract class Treeroute extends Metric {
    int trials;
    double fraction_root;
    double traffic_max;
    double[] traffic;
    private Distribution hopDistribution;
    double avHops = 0;
    int[][] coords;
    Random rand;
    SpanningTree sp;
    int trees;
    int t;
    //String routeFailPath = "D:\\code\\speedymurmur-experiments\\speedy\\data\\Results\\routeFail-t0-graph-root3-dyn-ChangeCredit21-2022-01-06-Test1-OriginalWeight.txt";
//    String routeFailPath = "D:\\code\\speedymurmur-experiments\\speedy\\data\\Results\\Mydataset-routeFail-t0-graph-root3-dyn-test21.txt";
    String routeFailPath = "./data/Results/Mydataset-routeFail-t0-graph-root3-dyn-test21-0-7-0.txt";
    Map<Edge, Long> routeFail = new HashMap<>();

    public Treeroute(String key, int trials, int trees, int t) {
        super(key);
        this.trials = trials;
        this.trees = trees;
        this.t = t;
    }

    public Treeroute(String key, int trials) {
        this(key, trials, 1, 1);
    }

    public Treeroute(String key) {
        super(key);
        rand = new Random(1);//modify by anan
    }

    @Override
    public void computeData(Graph g, Network n, HashMap<String, Metric> m) {
        //get coordinates and root, init distributions
        this.fraction_root = 0;
        this.traffic = new double[g.getNodeCount()];
        long[] hops = new long[1];
        rand = new Random(1);//modify by anan
        Node[] nodes = g.getNodes();
        //route trail times
        for (int i = 0; i < trials; i++) {
            int src = rand.nextInt(nodes.length);
            int dest = rand.nextInt(nodes.length);
            while (dest == src) {
                dest = rand.nextInt(nodes.length);
            }
            //System.out.println("src " + src + "dest " + dest);
            int[] l = Util.getkOfN(trees, t, rand);
            int h = Integer.MAX_VALUE;
            for (int j = 0; j < l.length; j++) {
                int[] path = this.getRoute(src, dest, l[j], g, nodes);
                int root = this.sp.getSrc();
                for (int s = 0; s < path.length; s++) {
                    traffic[path[s]]++;
                    if (path[s] == root) {
                        this.fraction_root = this.fraction_root + 1 / (double) (l.length);
                    }
                }
                if (path.length - 1 < h) {
                    h = path.length - 1;
                }
            }
            hops = this.inc(hops, h);
//			int r = rand.nextInt(this.trees);
//			coords = ((TreeCoordinates)g.getProperty("TREE_COORDINATES_"+r)).coords;
//			sp = (SpanningTree) g.getProperty("SPANNINGTREE_"+r);
//			int root = sp.getSrc();
//			int[] destC = this.coords[dest];
//			int s=0;
//			while (src != dest){
//				s++;
//				if (src == root){
//					this.fraction_root++;
//				}
//				traffic[src]++;
//				src = this.nextHop(src, nodes,destC,dest);
//			}
//			if (src == root){
//				this.fraction_root++;
//			}
//			traffic[src]++;
//			hops = this.inc(hops, s);
        }
        this.hopDistribution = new Distribution(hops, trials);
        this.avHops = this.hopDistribution.getAverage();
        this.fraction_root = this.fraction_root / (double) trials;
        this.traffic_max = 0;
        for (int i = 0; i < traffic.length; i++) {
            traffic[i] = traffic[i] / trials;
            if (traffic[i] > traffic_max) {
                traffic_max = traffic[i];
            }
        }
    }

    public int[] getRoute(int src, int dest, int r, Graph g, Node[] nodes) {
        coords = ((TreeCoordinates) g.getProperty("TREE_COORDINATES_" + r)).coords;
        sp = (SpanningTree) g.getProperty("SPANNINGTREE_" + r);
        int root = sp.getSrc();
        int[] destC = this.coords[dest];
        Vector<Integer> route = new Vector<Integer>();
        route.add(src);
        initRoute();
        while (src != dest) {
            src = this.nextHop(src, nodes, destC, dest);
            route.add(src);
        }
        int[] path = new int[route.size()];
        for (int i = 0; i < route.size(); i++) {
            path[i] = route.get(i);
        }
        return path;
    }

    public int[] getRoute(int src, int dest, int r, Graph g, Node[] nodes, boolean[] exclude) {
        coords = ((TreeCoordinates) g.getProperty("TREE_COORDINATES_" + r)).coords;
        sp = (SpanningTree) g.getProperty("SPANNINGTREE_" + r);
        int root = sp.getSrc();
        int[] destC = this.coords[dest];
        Vector<Integer> route = new Vector<Integer>();
        route.add(src);
        initRoute();
        int pre = -1;
        boolean done = false;
        while (!done) {
            int next = this.nextHop(src, nodes, destC, dest, exclude, pre);
            route.add(next);
            pre = src;
            src = next;
            if (root == src) {
                this.fraction_root++;
            }
            if (src == -1) {
                done = true;
            }
            if (src == dest) {
                if (!(this instanceof TreerouteSilentW) || !((TreerouteSilentW) this).up) {
                    done = true;
                }
            }

        }
        int[] path = new int[route.size()];
        for (int i = 0; i < route.size(); i++) {
            path[i] = route.get(i);
        }
        return path;
    }

    //    public int[] getRoute(int src, int dest, int r, Graph g, Node[] nodes, boolean[] exclude, CreditLinks edgeweights, double weight, int edgeFailCount, Map<Edge, Long> routeFailCount) {
//    public int[] getRoute(int src, int dest, int r, Graph g, Node[] nodes, boolean[] exclude, CreditLinks edgeweights, double weight, int edgeFailCount) {
    public int[] getRoute(int src, int dest, int r, Graph g, Node[] nodes, boolean[] exclude, CreditLinks edgeweights, double weight, boolean pay) {
        coords = ((TreeCoordinates) g.getProperty("TREE_COORDINATES_" + r)).coords;
        sp = (SpanningTree) g.getProperty("SPANNINGTREE_" + r);
        int root = sp.getSrc();
        int[] destC = this.coords[dest];
        Vector<Integer> route = new Vector<Integer>();
        route.add(src);
        initRoute();
        int pre = -1;
        boolean done = false;
        while (!done) {
            int next = this.nextHopWeight(edgeweights, src, nodes, destC, dest, exclude, pre, weight, pay);
//            int next = this.nextHopWeight(edgeweights, src, nodes, destC, dest, exclude, pre, weight, edgeFailCount);
//            int next = this.nextHopWeight(edgeweights, src, nodes, destC, dest, exclude, pre, weight, edgeFailCount, routeFailCount);
            route.add(next);
            pre = src;
            src = next;
            if (root == src) {
                this.fraction_root++;
            }
            if (src == -1) {
                done = true;
            }
            if (src == dest) {
                if (!(this instanceof TreerouteSilentW) || !((TreerouteSilentW) this).up) {
                    done = true;
                }
            }
        }
        int[] path = new int[route.size()];
        for (int i = 0; i < route.size(); i++) {
            path[i] = route.get(i);

        }
        return path;
    }

    public int[] getRouteBacktrack(int src, int dest, int r, Graph g, Node[] nodes, boolean[] exclude) {
        coords = ((TreeCoordinates) g.getProperty("TREE_COORDINATES_" + r)).coords;
        sp = (SpanningTree) g.getProperty("SPANNINGTREE_" + r);
        int root = sp.getSrc();
        int[] destC = this.coords[dest];
        Vector<Integer> route = new Vector<Integer>();
        route.add(src);
        int pre = -1;
        HashMap<Integer, LinkedList<Integer>> nexts = new HashMap<Integer, LinkedList<Integer>>();
        HashMap<Integer, Integer> pres = new HashMap<Integer, Integer>();
        boolean newNode = true;
        initRoute();
        while (src != dest && src != -1) {
            if (newNode) {
                LinkedList<Integer> nextL = this.nextHops(src, nodes, destC, dest, exclude, pre);
                if (nextL.size() > 0) {
                    nexts.put(src, nextL);
                }
                pres.put(src, pre);
            }
            Integer next;
            LinkedList<Integer> nextL = nexts.get(src);
            if (nextL != null) {
                next = nextL.removeFirst();
                if (nextL.size() == 0) {
                    nexts.remove(src);
                }
                if (pres.containsKey(next)) {
                    route.add(next);
                    next = src;
                    newNode = false;
                } else {
                    newNode = true;
                }
            } else {
                next = pres.get(src);
                newNode = false;
                if (next == null) {
                    next = -1;
                }
            }

            pre = src;
            src = next;
            if (root == src) {
                this.fraction_root++;
            }
            route.add(next);
        }
        int[] path = new int[route.size()];
        for (int i = 0; i < route.size(); i++) {
            path[i] = route.get(i);
        }
        return path;
    }

   /* public int[] getRouteBacktrack(int src, int dest, int r, Graph g, Node[] nodes, boolean[] exclude,
                                   CreditLinks edgeweights, double weight, int edgeFailCount, Map<Edge, Long> routeFailCount) {
        coords = ((TreeCoordinates) g.getProperty("TREE_COORDINATES_" + r)).coords;
        sp = (SpanningTree) g.getProperty("SPANNINGTREE_" + r);
        int root = sp.getSrc();
        int[] destC = this.coords[dest];
        Vector<Integer> route = new Vector<Integer>();
        route.add(src);
        int pre = -1;
        HashMap<Integer, LinkedList<Integer>> nexts = new HashMap<Integer, LinkedList<Integer>>();
        HashMap<Integer, Integer> pres = new HashMap<Integer, Integer>();
        boolean newNode = true;
        initRoute();
        while (src != dest && src != -1) {
            if (newNode) {
                LinkedList<Integer> nextL = this.nextHopsWeight(edgeweights, src, nodes, destC, dest, exclude, pre, weight, false);
                if (nextL.size() > 0) nexts.put(src, nextL);
                pres.put(src, pre);
            }
            Integer next;
            LinkedList<Integer> nextL = nexts.get(src);
            if (nextL != null) {
                next = nextL.removeFirst();
                if (nextL.size() == 0) nexts.remove(src);
                if (pres.containsKey(next)) {
                    route.add(next);
                    next = src;
                    newNode = false;
                } else {
                    newNode = true;
                }
            } else {
                next = pres.get(src);
                newNode = false;
                if (next == null) {
                    next = -1;
                }
            }

            pre = src;
            src = next;
            if (root == src) {
                this.fraction_root++;
            }
            route.add(next);
        }
        int[] path = new int[route.size()];
        for (int i = 0; i < route.size(); i++) {
            path[i] = route.get(i);
        }
        return path;
    }
*/

    /**
     * @return the fraction_root
     */
    public double getFraction_root() {
        return fraction_root;
    }

    @Override
    public boolean writeData(String folder) {
        boolean success = true;
        success &= DataWriter.writeWithIndex(
                this.hopDistribution.getDistribution(),
                this.key + "_HOP_DISTRIBUTION", folder);
        success &= DataWriter.writeWithIndex(
                this.hopDistribution.getCdf(),
                this.key + "_HOP_DISTRIBUTION_CDF", folder);
        success &= DataWriter.writeWithIndex(
                this.traffic,
                this.key + "_TRAFFIC", folder);
        for (int i = 0; i < traffic.length; i++) {
            traffic[i] = -1 * traffic[i];
        }
        Arrays.sort(traffic);
        for (int i = 0; i < traffic.length; i++) {
            traffic[i] = -1 * traffic[i];
        }
        success &= DataWriter.writeWithIndex(
                this.traffic,
                this.key + "_TRAFFIC_SORTED", folder);
        return success;
    }

    @Override
    public Single[] getSingles() {
        Single r = new Single(this.key + "_ROOT_TRAFFIC", this.fraction_root);
        Single mt = new Single(this.key + "_MAX_TRAFFIC", this.traffic_max);
        Single av = new Single(this.key + "_HOPS_AVERAGE", this.avHops);
        return new Single[]{r, mt, av};
    }

    @Override
    public boolean applicable(Graph g, Network n, HashMap<String, Metric> m) {
        return g.hasProperty("TREE_COORDINATES_0");
    }

    private long[] inc(long[] values, int index) {
        try {
            values[index]++;
            return values;
        } catch (ArrayIndexOutOfBoundsException e) {
            long[] valuesNew = new long[index + 1];
            System.arraycopy(values, 0, valuesNew, 0, values.length);
            valuesNew[index] = 1;
            return valuesNew;
        }
    }

    protected abstract int nextHop(int cur, Node[] nodes, int[] destID, int dest);

    protected abstract int nextHop(int cur, Node[] nodes, int[] destID, int dest, boolean[] exclude, int pre);

    protected abstract void initRoute();

    protected LinkedList<Integer> nextHops(int cur, Node[] nodes, int[] destID, int dest, boolean[] exclude, int pre) {
        LinkedList<Integer> list = new LinkedList<Integer>();
        int add = this.nextHop(cur, nodes, destID, dest, exclude, pre);
        while (add != -1) {
            list.add(add);
            exclude[add] = true;
            add = this.nextHop(cur, nodes, destID, dest, exclude, pre);
        }
        for (int i = 0; i < list.size(); i++) {
            exclude[list.get(i)] = false;
        }
        //System.out.println("The next nodes is:"+list.toArray());// add Anan
        return list;
    }

    //    protected LinkedList<Integer> nextHopsWeight(CreditLinks edgeWeights, int cur, Node[] nodes, int[] destID, int dest, boolean[] exclude, int pre, double weight, int edgeFailCount, Map<Edge, Long> routeFailCount) {
//    protected LinkedList<Integer> nextHopsWeight(CreditLinks edgeWeights, int cur, Node[] nodes, int[] destID, int dest, boolean[] exclude, int pre, double weight, int edgeFailCount) {
    protected LinkedList<Integer> nextHopsWeight(CreditLinks edgeWeights, int cur, Node[] nodes, int[] destID, int dest, boolean[] exclude, int pre, double weight, boolean pay) {
        LinkedList<Integer> list = new LinkedList<Integer>();
        LinkedList<Integer> listall = new LinkedList<Integer>();
        int add = this.nextHop(cur, nodes, destID, dest, exclude, pre);// Find next hop (next nodes)
        // String path = "D:\\code\\speedymurmur-experiments\\speedy\\data\\routeFail-t0-graph-dyn.txt";
        while (add != -1) {
            // System.out.println("---The weight (" + cur + "," + add + ") is:" + edgeWeights.getWeight(cur, add));//
            // System.out.println("---The balance (" + cur + "," + add + ") is:" + edgeWeights.getPot(cur, add));
            if (edgeWeights.getPot(cur, add) >= weight - 0.0000001) {//calculate whether the balance of (src,next nodes) is greater than weight -0.00000001
                list.add(add);
//                Vector<HashMap<Integer, ParentChild>> parentChildrenMapDepths = edgeWeights.getParentChildMapDepths();
//                ParentChild parent = parentChildrenMapDepths.get(0).get(add);
//                System.out.println("1------The node " + add + " of depth is : " + parent.getDepth() + "------1");
                // System.out.println("---The balance (" + cur + "," + add + ") is:" + edgeWeights.getPot(cur, add) + "is enough");
            } else {
                // System.out.println("The balance of (" + cur + "," + add + ") is:" + edgeWeights.getPot(cur, add) + " is insufficient");

//                if (edgeWeights.getPot(cur, add) - weight > edgeFailCount) {
//                Edge edge = new Edge(cur, add);
//                if (routeFailCount.containsKey(edge)) {
//                    routeFailCount.put(edge, routeFailCount.get(edge) + 1);
//                } else {
//                    routeFailCount.put(edge, 1L);
//                }
//                if (routeFailCount.get(edge) >= edgeFailCount) {
                if (pay) {
                    boolean switchSucc = SwitchPayment.switchPayment(cur, add, nodes, edgeWeights, weight);
                    if (switchSucc) {
                        list.add(add);
                    } else {
                        routeFail = edgeWeights.getTimes(cur, add, routeFail);
                    }
                } else {
                    routeFail = edgeWeights.getTimes(cur, add, routeFail);
                }

//                routeFail = edgeWeights.getTimes(cur, add, routeFail);
//                Vector<HashMap<Integer, ParentChild>> parentChildrenMapDepths = edgeWeights.getParentChildMapDepths();
//                //HashMap<Integer, ParentChild> map = parentChildrenMapDepths.get(0);
//                ParentChild parent = parentChildrenMapDepths.get(0).get(add);
//                // System.out.println("The depth is" + parentChildrenMapDepths);
//                System.out.println("2------The node " + add + " of depth is : " + parent.getDepth() + "------2");

//                Map<Edge, Long> edgeMap = Arg.edgeMap;
//                Edge edge = new Edge(cur, add);
//                if (edgeMap != null) {
//                    if (edgeMap.containsKey(edge)) {
//                        edgeMap.put(edge, edgeMap.get(edge) + 1);
//                    } else {
//                        edgeMap.put(edge, 1L);
//                    }
//                } else {
//                    edgeMap.put(edge, 1L);
//                }
//                Arg.edgeMap = edgeMap;
//                TxtHandle.writeEdgeToTxt(path, edgeMap);
//                Map<Edge, Long> edgeMap = TxtHandle.readTxt(path);
//                Edge edge = new Edge(cur, add);
//                if (edgeMap.containsKey(edge)) {
//                    edgeMap.put(edge, edgeMap.get(edge) + 1);
//                } else {
//                    edgeMap.put(edge, 1L);
//                }
//                TxtHandle.writeEdgeToTxt(path, edgeMap);
            }
            listall.add(add);
            exclude[add] = true;
            //add = -1;
            add = this.nextHop(cur, nodes, destID, dest, exclude, pre);// remove by Anan. If remove will not exit while loop
        }
        for (
                int i = 0; i < listall.size(); i++) {
            exclude[listall.get(i)] = false;
        }
        //System.out.println("The next nodes is:"+list.toArray());// add Anan
        WriteToTxt.writeEdgeToTxt(routeFailPath, routeFail);
        return list;
    }

    //    protected int nextHopWeight(CreditLinks edgeWeights, int cur, Node[] nodes, int[] destID, int dest,boolean[] exclude, int pre, double weight, int edgeFailCount, Map<Edge, Long> routeFailCount) {
//    protected int nextHopWeight(CreditLinks edgeWeights, int cur, Node[] nodes, int[] destID, int dest, boolean[] exclude, int pre, double weight, int edgeFailCount) {
    protected int nextHopWeight(CreditLinks edgeWeights, int cur, Node[] nodes, int[] destID, int dest, boolean[] exclude, int pre, double weight, boolean pay) {
        LinkedList<Integer> list = this.nextHopsWeight(edgeWeights, cur, nodes, destID, dest, exclude, pre, weight, pay);
//        LinkedList<Integer> list = this.nextHopsWeight(edgeWeights, cur, nodes, destID, dest, exclude, pre, weight, edgeFailCount);
//        LinkedList<Integer> list = this.nextHopsWeight(edgeWeights, cur, nodes, destID, dest, exclude, pre, weight, edgeFailCount, routeFailCount);
        if (list.isEmpty()) {
            return -1;
        } else {
            return list.poll();
        }
    }

}
