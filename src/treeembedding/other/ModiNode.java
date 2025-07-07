package treeembedding.other;

public class ModiNode {

    private int index;

    private int[] incomingEdges;

    private int[] outgoingEdges;

    public ModiNode(int index, int[] incomingEdges, int[] outgoingEdges) {
        this.index = index;
        this.incomingEdges = incomingEdges;
        this.outgoingEdges = outgoingEdges;
    }

    public int[] getIncomingEdges() {
        return this.incomingEdges;
    }

    public int[] getOutgoingEdges() {
        return this.outgoingEdges;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setIncomingEdges(int[] incomingEdges) {
        this.incomingEdges = incomingEdges;
    }

    public void setOutgoingEdges(int[] outgoingEdges) {
        this.outgoingEdges = outgoingEdges;
    }
}
