package treeembedding.other;

import java.util.*;
import java.util.Map.Entry;

public class Flash {

    public static void addEdge(Map<List<Integer>, Map<String, Double>> G, List<Integer> edge, double capacity, double cost) {
        Map<String, Double> attributes = new HashMap<>();
        attributes.put("capacity", capacity);
        attributes.put("cost", cost);
        G.put(edge, attributes);
    }

    public static double[] routing(Map<List<Integer>, Map<String, Double>> G, List<Integer> payment) {
        int src = payment.get(0);
        int dst = payment.get(1);
        double d = payment.get(2);

        Map<List<Integer>, Map<String, Double>> local_G = createLocalGraph(G);
        Map<List<Integer>, Map<String, Double>> sub_G = new HashMap<>();

        int probing_messages = 0;
        int max_path_length = 0;
        List<List<Integer>> pathSet = new ArrayList<>();
        List<Double> capSet = new ArrayList<>();

        List<Integer> path = bfsPath(local_G, src, dst);
        while (!path.isEmpty()) {
            pathSet.add(path);

            double pathCap = Double.MAX_VALUE;
            for (int i = 0; i < path.size() - 1; i++) {
                probing_messages++;
                double capacity = local_G.get(Arrays.asList(path.get(i), path.get(i + 1))).get("capacity");
                pathCap = Math.min(pathCap, capacity);

                addEdge(sub_G, Arrays.asList(path.get(i), path.get(i + 1)),
                        G.get(Arrays.asList(path.get(i), path.get(i + 1))).get("capacity"),
                        G.get(Arrays.asList(path.get(i), path.get(i + 1))).get("cost"));
                addEdge(sub_G, Arrays.asList(path.get(i + 1), path.get(i)),
                        G.get(Arrays.asList(path.get(i + 1), path.get(i))).get("capacity"),
                        G.get(Arrays.asList(path.get(i + 1), path.get(i))).get("cost"));
            }
            capSet.add(pathCap);

            max_path_length = Math.max(max_path_length, path.size() - 1);

            for (int i = 0; i < path.size() - 1; i++) {
                double capacity = local_G.get(Arrays.asList(path.get(i), path.get(i + 1))).get("capacity");
                local_G.get(Arrays.asList(path.get(i), path.get(i + 1))).put("capacity", capacity - pathCap);
                local_G.get(Arrays.asList(path.get(i + 1), path.get(i))).put("capacity", capacity - pathCap);
            }

            path = bfsPath(local_G, src, dst);
        }

        double throughput = d / capSet.stream().mapToDouble(Double::doubleValue).sum();
        double fee = capSet.stream().mapToDouble(cap -> cap * G.get(payment).get("cost")).sum();

        double[] result = new double[4];
        result[0] = throughput;
        result[1] = fee;
        result[2] = probing_messages;
        result[3] = max_path_length;
        return result;
    }

    public static Map<List<Integer>, Map<String, Double>> createLocalGraph(Map<List<Integer>, Map<String, Double>> G) {
        Map<List<Integer>, Map<String, Double>> local_G = new HashMap<>(G);

        for (Entry<List<Integer>, Map<String, Double>> entry : G.entrySet()) {
            List<Integer> edge = entry.getKey();
            int u = edge.get(0);
            int v = edge.get(1);
            double capacity = entry.getValue().get("capacity");
            double cost = entry.getValue().get("cost");

            if (capacity == 0) {
                local_G.remove(Arrays.asList(u, v));
                local_G.remove(Arrays.asList(v, u));
            } else {
                local_G.get(Arrays.asList(u, v)).put("cost", cost + (1 / capacity));
                local_G.get(Arrays.asList(v, u)).put("cost", cost + (1 / capacity));
            }
        }

        return local_G;
    }

    public static List<Integer> bfsPath(Map<List<Integer>, Map<String, Double>> G, int src, int dst) {
        List<Integer> path = new ArrayList<>();
        Map<Integer, Integer> parent = new HashMap<>();

        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        queue.offer(src);
        visited.add(src);
        parent.put(src, -1);

        while (!queue.isEmpty()) {
            int node = queue.poll();
            if (node == dst) {
                path = constructPath(parent, node);
                break;
            }

            for (Entry<List<Integer>, Map<String, Double>> entry : G.entrySet()) {
                List<Integer> edge = entry.getKey();
                int u = edge.get(0);
                int v = edge.get(1);

                if (u == node && !visited.contains(v) && G.get(edge).get("capacity") > 0) {
                    queue.offer(v);
                    visited.add(v);
                    parent.put(v, u);
                }
            }
        }

        return path;
    }

    public static List<Integer> constructPath(Map<Integer, Integer> parent, int node) {
        List<Integer> path = new ArrayList<>();
        while (node != -1) {
            path.add(0, node);
            node = parent.get(node);
        }
        return path;
    }
}
