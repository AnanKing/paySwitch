/*
 * ===========================================================
 * GTNA : Graph-Theoretic Network Analyzer
 * ===========================================================
 *
 * (C) Copyright 2009-2011, by Benjamin Schiller (P2P, TU Darmstadt)
 * and Contributors
 *
 * Project Info:  http://www.p2p.tu-darmstadt.de/research/gtna/
 *
 * GTNA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GTNA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * ---------------------------------------
 * Graph.java
 * ---------------------------------------
 * (C) Copyright 2009-2011, by Benjamin Schiller (P2P, TU Darmstadt)
 * and Contributors
 *
 * Original Author: Benjamin Schiller;
 * Contributors:    -;
 *
 * Changes since 2011-05-17
 * ---------------------------------------
 */
package gtna.graph;

import java.util.*;

public class Graph {

    /**
     * @param name name of the graph
     */
    public Graph(String name) {
        this.name = name;
    }

    public Graph(String name, int nodes) {
        this.name = name;
        this.nodes = Node.init(nodes, this);
    }

    public String toString() {
        return this.name + " (" + this.nodes.length + ")";
    }

    /*
     * NAME
     */

    private String name;

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * NODES
     */

    private Node[] nodes = new Node[0];

    /**
     * @return the nodes
     */
    public Node[] getNodes() {
        return this.nodes;
    }

    /**
     * @param nodes the nodes to set
     */
    public void setNodes(Node[] nodes) {
        this.nodes = nodes;
    }

    /**
     * @param nodeIndex
     * @return the node with index nodeIndex
     */
    public Node getNode(int nodeIndex) {
        return this.nodes[nodeIndex];
    }

    public int getNodeCount() {
        return this.nodes.length;
    }

    /*
     * EDGES
     */

    private Edges edges = null;

    public int computeNumberOfEdges() {
        int E = 0;
        for (Node n : this.nodes) {
            E += n.getOutDegree();
        }
        return E;
    }

    public Edge[] generateEdges() {
        int E = 0;
        for (Node n : this.nodes) {
            E += n.getOutDegree();
        }
        Edge[] edges = new Edge[E];
        int index = 0;
        for (Node n : this.nodes) {
            for (int out : n.getOutgoingEdges()) {
                edges[index++] = new Edge(n.getIndex(), out);
            }
        }
        return edges;
    }

    public Edges getEdges() {
        if (this.edges != null) {
            return this.edges;
        }
        int E = 0;
        for (Node n : this.nodes) {
            E += n.getOutDegree();
        }
        this.edges = new Edges(this.nodes, E);
        for (Node n : this.nodes) {
            for (int out : n.getOutgoingEdges()) {
                this.edges.add(n.getIndex(), out);
            }
        }
        return this.edges;
    }

    /*
     * PROPERTIES
     */

    private HashMap<String, GraphProperty> properties = new HashMap<String, GraphProperty>();

    public void addProperty(String key, GraphProperty property) {
        this.properties.put(key, property);
    }

    public void removeProperty(String key) {
        this.properties.remove(key);
    }

    @SuppressWarnings("rawtypes")
    public boolean hasProperty(String key, Class type) {
        return this.hasProperty(key) && type.isInstance(this.getProperty(key));
    }

    public boolean hasProperty(String key) {
        return this.properties.containsKey(key);
    }

    public GraphProperty getProperty(String key) {
        return this.properties.get(key);
    }

    public GraphProperty[] getProperties(String type) {
        ArrayList<GraphProperty> list = new ArrayList<GraphProperty>();
        int index = 0;
        while (this.hasProperty(type + "_" + index)) {
            list.add(this.getProperty(type + "_" + index));
            index++;
        }
        GraphProperty[] array = new GraphProperty[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = (GraphProperty) list.get(i);
        }
        return array;
    }

    public String getNextKey(String type) {
        int index = 0;
        while (this.hasProperty(type + "_" + index)) {
            index++;
        }
        return type + "_" + index;
    }

    public HashMap<String, GraphProperty> getProperties() {
        return this.properties;
    }

    public void removeNode(Node nodeToRemove) {
        ArrayList<Node> nodeList = new ArrayList<>(Arrays.asList(nodes));
        nodeList.remove(nodeToRemove);
        Node[] updatedNodes = nodeList.toArray(new Node[0]);
        setNodes(updatedNodes);
    }

    public static void main(String[] args) {
        Graph graph = new Graph("MyGraph", 6);

        // 创建节点
        Node node0 = new Node(0, graph);
        Node node1 = new Node(1, graph);
        Node node2 = new Node(2, graph);
        Node node3 = new Node(3, graph);
        Node node4 = new Node(4, graph);
        Node node5 = new Node(5, graph);

        Node[] nodes = {node0, node1, node2, node3, node4, node5};
        graph.setNodes(nodes);

        Edge edge0 = new Edge(node0, node1);
        Edge edge1 = new Edge(node1, node2);
        Edge edge2 = new Edge(node2, node3);
        Edge edge3 = new Edge(node3, node4);

//        Edge[] edges = {edge0, edge1, edge2, edge3};
        graph.getEdges().add(edge0);
        graph.getEdges().add(edge1);
        graph.getEdges().add(edge2);
        graph.getEdges().add(edge3);

        // 打印图的信息
        System.out.println(graph.toString());
        for (Node n : graph.getNodes())
            System.out.println(n.getOutgoingEdges());
        for (int n : graph.getNode(1).getOutgoingEdges())
            System.out.println("6555" + n);

    }

//    @Override
//    public Graph clone() {
//        Graph clonedGraph = new Graph(this.name);
//        clonedGraph.setNodes(this.nodes.clone());
//        clonedGraph.setEdges(this.edges.clone());
//        clonedGraph.setProperties(new HashMap<>(this.properties));
//        return clonedGraph;
//    }
//
//    public void removeEdge(int src, int dst) {
//        if (this.edges != null) {
//            Edge edgeToRemove = null;
//            for (Edge edge : this.edges.getEdges()) {
//                if (edge.getSrc() == src && edge.getDst() == dst) {
//                    edgeToRemove = edge;
//                    break;
//                }
//            }
//            if (edgeToRemove != null) {
//                this.edges.getEdges().remove(edgeToRemove);
//                this.edges.getOutDegree()[src]--;
//                this.edges.getInDegree()[dst]--;
//                updateNodeEdges(src);
//                updateNodeEdges(dst);
//            }
//        }
//    }
//
//    private void updateNodeEdges(int nodeIndex) {
//        Node node = this.nodes[nodeIndex];
//        node.setIncomingEdges(getNodeEdges(node, true));
//        node.setOutgoingEdges(getNodeEdges(node, false));
//    }
//
//    private int[] getNodeEdges(Node node, boolean isIncoming) {
//        List<Integer> edgesList = new ArrayList<>();
//        for (Edge edge : this.edges.getEdges()) {
//            if ((isIncoming && edge.getDst() == node.getIndex()) || (!isIncoming && edge.getSrc() == node.getIndex())) {
//                edgesList.add(isIncoming ? edge.getSrc() : edge.getDst());
//            }
//        }
//        return edgesList.stream().mapToInt(Integer::intValue).toArray();
//    }

}
