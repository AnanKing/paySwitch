package treeembedding.other;

import gtna.graph.Graph;
import gtna.graph.Node;

import java.util.*;

public class EdgeDisjointPaths {

    // 通过BFS查找从源节点到目标节点的路径
    private static List<Integer> findPath(Graph graph, int source, int target) {
        int n = graph.getNodeCount();
//        int n = graph.length;
        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) {
            visited[i] = false;
            parent[i] = -1;
        }

        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int currentNode = queue.poll();
            Set<Integer> neighbors = new HashSet<>();
            for (int out : graph.getNode(currentNode).getOutgoingEdges()) {
                neighbors.add(out);
            }
            for (Integer neighbor : neighbors) {
                if (!visited[neighbor]) {
                    queue.add(neighbor);
                    visited[neighbor] = true;
                    parent[neighbor] = currentNode;
                }
            }
//            for (int neighbor = 0; neighbor < n; neighbor++) {
//                if (!visited[neighbor] && graph[currentNode][neighbor] > 0) {
//                    queue.add(neighbor);
//                    visited[neighbor] = true;
//                    parent[neighbor] = currentNode;
//                }
//            }
        }

        List<Integer> path = new ArrayList<>();
        int current = target;
        while (current != source) {
            path.add(0, current);
            if (current == -1) {
                break;
            }
            current = parent[current];
        }
        path.add(0, source);
        return path;
    }

    // 查找edge-disjoint路径的方法
    public static List<List<Integer>> findEdgeDisjointPaths(Graph graph, int source, int target, int numPaths) {
        List<List<Integer>> paths = new ArrayList<>();

        for (int i = 0; i < numPaths; i++) {
            List<Integer> path = findPath(graph, source, target);
            if (path.isEmpty()) {
                // 所有的路径都已经找到了
                break;
            }

            // 在图中删除已经找到的路径
            for (int j = 0; j < path.size() - 1; j++) {
                int u = path.get(j);
                int v = path.get(j + 1);
                if (u != -1 && v != -1) {
                    Node[] nodes = graph.getNodes();
//                    graph.removeNode(u);
//                    graph[u][v] = 0;
//                    graph[v][u] = 0;
                }
            }

            paths.add(path);
        }

        return paths;
    }

    /*public static void main(String[] args) {
        // 一个简单的邻接矩阵表示的图
        int[][] graph = {
                {0, 1, 0, 0, 0},
                {1, 0, 0, 1, 1},
                {0, 0, 0, 1, 1},
                {0, 1, 1, 0, 1},
                {0, 1, 1, 1, 0}
        };

        int source = 0;
        int target = 4;
        int numPaths = 2;

        List<List<Integer>> edgeDisjointPaths = findEdgeDisjointPaths(graph, source, target, numPaths);
        System.out.println("Edge-disjoint paths:");
        for (List<Integer> path : edgeDisjointPaths) {
            if (path.contains(-1)) {
                break;
            }
            System.out.println(path);
        }
    }*/
}
