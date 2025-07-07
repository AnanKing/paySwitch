package treeembedding.treerouting;

import gtna.graph.Node;

import java.util.Arrays;
import java.util.Vector;

public abstract class TreerouteNH extends Treeroute {

    public TreerouteNH(String key, int trials, int trees, int t) {
        super(key);
        this.trials = trials;
        this.trees = trees;
        this.t = t;
    }

    public TreerouteNH(String key, int trials) {
        this(key, trials, 1, 1);
    }

    public TreerouteNH(String key) {
        super(key);
    }

    @Override
    protected int nextHop(int cur, Node[] nodes, int[] destID, int dest) {
        int[] out = nodes[cur].getIncomingEdges();
        double dbest = this.dist(cur, cur, dest);
        Vector<Integer> closest = new Vector<Integer>();
        for (int i = 0; i < out.length; i++) {
            double dcur = this.dist(cur, out[i], dest);
            if (dcur <= dbest) {
                if (dcur < dbest) {
                    dbest = dcur;
                    closest = new Vector<Integer>();
                }
                closest.add(out[i]);
            }
        }
        int index;
        if (closest.size() == 0) {
            index = -1;
        } else {
            index = closest.get(rand.nextInt(closest.size()));
        }
        return index;
    }

    @Override
    protected int nextHop(int cur, Node[] nodes, int[] destID, int dest,
                          boolean[] exclude, int pre) {
        int[] out = nodes[cur].getIncomingEdges();// It is obtain the incoming edges is from the original graph rather than spanning trees.
        double dbest = this.dist(cur, cur, dest);
        Vector<Integer> closest = new Vector<Integer>();// defined the closest coordinate of dest node
        for (int i = 0; i < out.length; i++) {
            if (!exclude[out[i]] && pre != out[i]) {
                int absdepth = Math.abs(sp.getDepth(cur) - sp.getDepth(out[i]));
                //if (absdepth > 1) continue;

                double dcur = this.dist(cur, out[i], dest);
                if (dcur <= dbest) {
                    if (closest.size() == 0 && dcur == dbest) continue;
                    if (dcur < dbest) {
                        dbest = dcur;
                        closest = new Vector<Integer>();
                    }
                    closest.add(out[i]);//storage the closest coordinate of dest node
                }
            }
        }
        //System.out.println("The Node:"+cur+" of Children Nodes is:"+ Arrays.toString(out));// print children nodes by anan
        int index;
        if (closest.size() == 0) {
            index = -1; // Not find the closest coordinate of dest nodes
            System.out.println("---No find the closest coordinate of dest nodes---");
        } else {
            index = closest.get(rand.nextInt(closest.size())); // obtain the closest node of dest node by random
            //System.out.println("---Find the closest coordinate of dest nodes is:" + index + " nodes---");
        }
        //System.out.println("The index is:"+ index);// print index
        return index;
    }

    protected abstract double dist(int node, int neighbor, int dest);

    @Override
    protected void initRoute() {


    }

}
