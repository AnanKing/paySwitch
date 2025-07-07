package treeembedding.other;

import gtna.graph.Edge;
import gtna.graph.Edges;
import gtna.graph.Graph;
import gtna.graph.Node;

import java.util.*;

public class OthersAlgorithm {

    public static int[] getDijkstraPaths(Graph graph, int start, int end) {
        int[] dist = new int[graph.getNodeCount()];
        int[] previous = new int[graph.getNodeCount()];
        Set<Integer> Q = new HashSet<>();

        for (int i = 0; i < graph.getNodeCount(); i++) {
            dist[i] = Integer.MAX_VALUE;
            previous[i] = -1;
            Q.add(i);
        }
        dist[start] = 0;
        while (!Q.isEmpty()) {
            int u = getMinElement(Q, dist);
            Q.remove(u);

            if (u == end) {
                break;
            }

            if (dist[u] == Integer.MAX_VALUE) {
                break;
            }

            for (int v : graph.getNode(u).getOutgoingEdges()) {
                int alt = dist[u] + 1;
                if (alt < dist[v]) {
                    dist[v] = alt;
                    previous[v] = u;
                }
            }
        }

        if (previous[end] == -1) {
            // No path found from start to end
            return new int[0];
        }

        // Reconstruct the shortest path
        List<Integer> pathList = new ArrayList<>();
        int current = end;
        while (current != -1) {
            pathList.add(current);
            current = previous[current];
        }

        // Convert the list to an array
        int[] path = new int[pathList.size()];
        for (int i = pathList.size() - 1, j = 0; i >= 0; i--, j++) {
            path[j] = pathList.get(i);
        }

        return path;
    }

    private static int getMinElement(Set<Integer> Q, int[] dist) {
        int min = Q.iterator().next();
        for (int element : Q) {
            if (dist[element] < dist[min]) {
                min = element;
            }
        }
        return min;
    }

    public static int[][] getDisjointPaths(ModiGraph graph, int source, int target) {
        int[] dist = new int[graph.getNodeCount()];
        int[] prev = new int[graph.getNodeCount()];

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[source] = 0;

        Set<Integer> Q = new HashSet<>();
        for (int i = 0; i < graph.getNodeCount(); i++) {
            Q.add(i);
        }

        while (!Q.isEmpty()) {
            int u = getMinElement(Q, dist);
            Q.remove(u);

            if (u == target) {
                break;
            }

            ModiNode nodeU = graph.getNode(u);
            for (int v : nodeU.getOutgoingEdges()) {
                int alt = dist[u] + 1;
                if (alt < dist[v]) {
                    dist[v] = alt;
                    prev[v] = u;
                }
            }
        }

        List<Integer> pathList1 = new ArrayList<>();
        List<Integer> pathList2 = new ArrayList<>();
        int current = target;
        while (prev[current] != -1) {
            pathList1.add(current);
            current = prev[current];
        }
        pathList1.add(current);
        Collections.reverse(pathList1);

        int[] path1 = new int[pathList1.size()];
        for (int i = 0; i < pathList1.size(); i++) {
            path1[i] = pathList1.get(i);
        }

        if (path1.length < 2) {
            return new int[][]{path1, new int[0]};
        }

        // Modify the graph to remove the edges from the first path
        ModiGraph modifiedGraph = modifyGraph(graph, path1);
        prev[source] = -1;

        // Perform Dijkstra's algorithm on the modified graph to find the second shortest path
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[source] = 0;

        Q.clear();
        for (int i = 0; i < modifiedGraph.getNodeCount(); i++) {
            Q.add(i);
        }

        while (!Q.isEmpty()) {
            int u = getMinElement(Q, dist);
            Q.remove(u);

            if (u == target) {
                break;
            }

            ModiNode nodeU = modifiedGraph.getNode(u);
            for (int v : nodeU.getOutgoingEdges()) {
                int alt = dist[u] + 1;
                if (alt < dist[v]) {
                    dist[v] = alt;
                    prev[v] = u;
                }
            }
        }

        current = target;
        boolean[] visited = new boolean[graph.getNodeCount()];
        while (prev[current] != -1 && !visited[current]) {
            visited[current] = true; // 将当前节点标记为已访问
            pathList2.add(current);
            current = prev[current];
        }
        pathList2.add(current);
        Collections.reverse(pathList2);

        int[] path2 = new int[pathList2.size()];
        for (int i = 0; i < pathList2.size(); i++) {
            path2[i] = pathList2.get(i);
        }

        return new int[][]{path1, path2};
    }

    private static ModiGraph modifyGraph(ModiGraph graph, int[] path) {
        ModiNode[] nodes = graph.getNodes();
        for (int i = 0; i < path.length - 1; i++) {
            int u = path[i];
            int v = path[i + 1];
            ModiNode node = graph.getNode(u);
            int[] ints = removeValueFromArray(node.getOutgoingEdges(), v);
            node.setOutgoingEdges(ints);
            nodes[u] = node;

            ModiNode node1 = graph.getNode(v);
            node1.setIncomingEdges(removeValueFromArray(node1.getIncomingEdges(), u));
            nodes[v] = node1;
        }
        graph.setNodes(nodes);
        return graph;
    }

    public static int[] removeValueFromArray(int[] arr, int valueToRemove) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != valueToRemove) {
                list.add(arr[i]);
            }
        }
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

}
