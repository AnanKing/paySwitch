package treeembedding.credit;

import java.util.*;
import java.util.Map.Entry;

import gtna.graph.Edge;
import gtna.graph.GraphProperty;
import gtna.graph.spanningTree.ParentChild;
import gtna.io.Filereader;
import gtna.io.Filewriter;
import treeembedding.other.WriteToTxt;

public class CreditLinks extends GraphProperty {

    private static Vector<HashMap<Integer, ParentChild>> parentChildMapDepths;
    private Map<Edge, double[]> weights;

    private Map<Edge, Long> times;

    public static void setParentChildMapDepths(Vector<HashMap<Integer, ParentChild>> parentChildMapDepths) {
        CreditLinks.parentChildMapDepths = parentChildMapDepths;
    }

    public Vector<HashMap<Integer, ParentChild>> getParentChildMapDepths() {
        return parentChildMapDepths;
    }

    public void setTimes(Map<Edge, Long> times) {
        this.times = times;
    }

    public void setWeights(Map<Edge, double[]> weights) {
        this.weights = weights;
    }

    public CreditLinks() {
        this.weights = new HashMap<Edge, double[]>();
    }

    public double[] getWeights(int src, int dst) {
        if (src < dst) {
            return this.getWeights(new Edge(src, dst));
        } else {
            return this.getWeights(new Edge(dst, src));
        }
    }

    public double getPot(int src, int dst) {
        if (src < dst) {
            double[] weight = this.getWeights(new Edge(src, dst));
            return weight[2] - weight[1];
        } else {
            double[] weight = this.getWeights(new Edge(dst, src));
            return weight[1] - weight[0];
        }
    }

    public Map<Edge, Long> getTimes(int src, int dst, Map<Edge, Long> map) {
        Edge edge = new Edge(src, dst);
        if (map.containsKey(edge)) {
            map.put(edge, map.get(edge) + 1);
        } else {
            map.put(edge, 1L);
        }
        return map;
    }

    public Set<Entry<Edge, double[]>> getWeights() {
        return this.weights.entrySet();
    }

    public void setWeight(Edge edge, double[] weight) {
        this.weights.put(edge, weight);
    }

    //加
    public void setWeight1(int src, int dst, double weight) {
        if (src < dst) {
            double[] ws = this.weights.get(new Edge(src, dst));
            ws[2] += weight;
        } else {
            double[] ws = this.weights.get(new Edge(dst, src));
            ws[0] -= weight;
        }
    }

    //减
    public void setWeight2(int src, int dst, double weight) {
        if (src < dst) {
            double[] ws = this.weights.get(new Edge(src, dst));
            ws[2] -= weight;
        } else {
            double[] ws = this.weights.get(new Edge(dst, src));
            ws[0] += weight;
        }
    }

    public boolean setWeight(int src, int dst, double sub) {
        if (src < dst) {
            double[] ws = this.weights.get(new Edge(src, dst));
            double dn = ws[1] + sub;
            if (dn <= ws[2]) {
                ws[1] = dn;
                return true;
            } else {
                return false;
            }
        } else {
            double[] ws = this.weights.get(new Edge(dst, src));
            double dn = ws[1] - sub;
            if (dn >= ws[0]) {
                ws[1] = dn;
                return true;
            } else {
                return false;
            }
        }
    }

    public void setBound(int src, int dst, double val) {
        if (src < dst) {
            double[] ws = this.weights.get(new Edge(src, dst));
            ws[2] = val;
        } else {
            double[] ws = this.weights.get(new Edge(dst, src));
            ws[0] = -val;
        }
    }

    public void setWeight(Edge e, double val) {
        double[] ws = this.weights.get(e);
        ws[1] = val;
    }

    public double[] getWeights(Edge edge) {
        try {
            return this.weights.get(edge);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public double getWeight(int src, int dst) {
        Edge edge = src < dst ? new Edge(src, dst) : new Edge(dst, src);
        try {
            double[] ws = this.weights.get(edge);
            return ws[1];
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public double getWeight(Edge edge) {
        try {
            double[] ws = this.weights.get(edge);
            return ws[1];
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public Edge makeEdge(int src, int dst) {
        Edge edge = src < dst ? new Edge(src, dst) : new Edge(dst, src);
        return edge;
    }

    @Override
    public boolean write(String filename, String key) {
        Filewriter fw = new Filewriter(filename);

        this.writeHeader(fw, this.getClass(), key);

        Iterator<Entry<Edge, double[]>> it = this.weights.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Edge, double[]> entry = it.next();
            double[] w = entry.getValue();
            String ws = w[0] + " " + w[1] + " " + w[2];
            fw.writeln(entry.getKey().getSrc() + " " + entry.getKey().getDst() + " " + ws);
        }

        return fw.close();
    }

    @Override
    public String read(String filename) {
        Filereader fr = new Filereader(filename);

        String key = this.readHeader(fr);
        this.weights = new HashMap<Edge, double[]>();
        String line = null;
        while ((line = fr.readLine()) != null) {
            String[] parts = line.split(" ");
            if (parts.length < 2) continue;
            Edge e = new Edge(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            double val_low = Double.parseDouble(parts[2]);
            double val = Double.parseDouble(parts[3]);
            double val_high = Double.parseDouble(parts[4]);
            this.weights.put(e, new double[]{val_low, val, val_high});
        }

        fr.close();

        return key;
    }

}
