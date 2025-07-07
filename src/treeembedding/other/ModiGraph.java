package treeembedding.other;

import gtna.graph.Graph;
import gtna.graph.Node;

public class ModiGraph {

    private ModiNode[] nodes;

    public ModiGraph(ModiNode[] nodes) {
        this.nodes = nodes;

    }

    public int getNodeCount() {
        return this.nodes.length;
    }

    public ModiNode getNode(int nodeIndex) {
        return this.nodes[nodeIndex];
    }

    public ModiNode[] getNodes() {
        return nodes;
    }

    public void setNodes(ModiNode[] nodes) {
        this.nodes = nodes;
    }

    public static ModiGraph graphToModi(Graph graph) {
        Node[] nodes = graph.getNodes();
        ModiNode[] modiNodes = new ModiNode[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            modiNodes[i] = new ModiNode(nodes[i].getIndex(), nodes[i].getIncomingEdges(), nodes[i].getOutgoingEdges());
        }
        return new ModiGraph(modiNodes);
    }

}
