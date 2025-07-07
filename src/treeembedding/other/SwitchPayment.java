package treeembedding.other;

import gtna.graph.Edge;
import gtna.graph.Node;
import treeembedding.credit.CreditLinks;

import java.util.HashMap;
import java.util.Map;

public class SwitchPayment {

    //    private static int[] bigSrc = new int[]{0, 9, 14, 18, 28, 32, 147, 184, 185, 186,
//            187, 226, 231, 264, 271, 274, 275, 277, 280, 286, 317, 322, 1121, 18450,
//            23072, 24183, 48260, 54011};
//    private static int[] bigSrc = new int[]{1};

    public static boolean judgePathSwitchPayment(int[] path, Node[] nodes, CreditLinks edgeWeights, double weight) {
        for (int i = 0; i < path.length - 2; i++) {
            boolean switchPayment = judgeSwitchPayment(path[i], path[i + 1], nodes, edgeWeights, weight);
            if (!switchPayment) {
                return false;
            }
        }
        return true;
    }

    private static boolean judgeSwitchPayment(int cur, int add, Node[] nodes, CreditLinks edgeWeights, double weight) {
        double lessWeight = weight - edgeWeights.getPot(cur, add);
        if (lessWeight <= 0) {
            return true;
        }
        int[] out2 = nodes[cur].getIncomingEdges();
        if (out2.length == 1) {
            return false;
        }
        for (int j = 0; j < out2.length; j++) {
            if (add == out2[j]) {
                continue;
            }
            lessWeight -= edgeWeights.getPot(cur, out2[j]);
            if (lessWeight <= 0) {
                return true;
            }
        }
        if (lessWeight <= 0) {
            return true;
        }
        return false;
    }

    public static boolean switchPayment(int cur, int add, Node[] nodes, CreditLinks edgeWeights, double weight) {
        double lessWeight = weight - edgeWeights.getPot(cur, add);
        int[] out2 = nodes[cur].getIncomingEdges();
        if (out2.length == 1) {
            return false;
        }
        //排序=============
        int[] out = new int[out2.length - 1];
        double[] value = new double[out2.length - 1];
        int index = 0;
        for (int j = 0; j < out2.length; j++) {
            if (add == out2[j]) {
                continue;
            }
            out[index] = out2[j];
            value[index] = edgeWeights.getPot(cur, out[index]);
            index++;
        }
        //        quickSort(out, 0, out.length - 1, value);
        //        shellSort(out, value);
        heapSort(out, value);
        if (value[0] == 0) {
            return false;
        }
        //排序结束=============
        Map<Edge, Double> switchWeight = new HashMap<>();
        Map<Edge, Double> debtorWeight = new HashMap<>();
        for (int j = 0; j < out.length; j++) {
            lessWeight -= value[j];
            switchWeight.put(new Edge(cur, out[j]), value[j]);
            if (lessWeight <= 0) {
                switchWeight.put(new Edge(cur, out[j]), lessWeight + value[j]);
                debtorWeight.put(new Edge(cur, add), weight - edgeWeights.getPot(cur, add));
                updateEdgeWeights(edgeWeights, switchWeight);
                updateDebtorEdgeWeights(edgeWeights, debtorWeight);
                return true;
            }
        }
        return false;
    }

    /* public static boolean switchPayment(int cur, int add, Node[] nodes, CreditLinks edgeWeights, double weight) {
         double lessWeight = weight - edgeWeights.getPot(cur, add);
         Map<Edge, Double> switchWeight = new HashMap<>();
         Map<Edge, Double> debtorWeight = new HashMap<>();

         for (int i = 0; i < bigSrc.length; i++) {
             int src = bigSrc[i];
             if (cur == src) {
                 //排序=============
                 int[] out2 = nodes[src].getIncomingEdges();
                 int[] out = new int[out2.length - 1];
                 double[] value = new double[out2.length - 1];
                 for (int j = 0; j < out.length; j++) {
                     if (add == out2[j]) {
                         continue;
                     }
                     out[j] = out2[j];
                     value[j] = edgeWeights.getPot(src, out[j]);
                 }
                 //                quickSort(out, 0, out.length - 1, value);
                 //                shellSort(out, value);
                 heapSort(out, value);
                 //排序结束=============

                 for (int j = 0; j < out.length; j++) {
                     if ((cur == out[j] && add == src) || (cur == src && add == out[j])) {
                         continue;
                     }
                     lessWeight -= edgeWeights.getPot(src, out[j]);
                     switchWeight.put(new Edge(src, out[j]), edgeWeights.getPot(src, out[j]));
                     if (lessWeight <= 0) {
                         switchWeight.put(new Edge(src, out[j]), lessWeight + edgeWeights.getPot(src, out[j]));
                         debtorWeight.put(new Edge(cur, add), weight - edgeWeights.getPot(cur, add));
                         updateEdgeWeights(edgeWeights, switchWeight);
                         updateDebtorEdgeWeights(edgeWeights, debtorWeight);
                         return true;
                     }
                 }
             }
         }
         return false;
     }
 */
    private static void updateEdgeWeights(CreditLinks edgeWeights, Map<Edge, Double> switchWeight) {
        for (Map.Entry<Edge, Double> entry : switchWeight.entrySet()) {
            int src = entry.getKey().getSrc();
            int dst = entry.getKey().getDst();
            Double value = entry.getValue();
            edgeWeights.setWeight2(src, dst, value);
        }
    }

    private static void updateDebtorEdgeWeights(CreditLinks edgeWeights, Map<Edge, Double> switchWeight) {
        for (Map.Entry<Edge, Double> entry : switchWeight.entrySet()) {
            int src = entry.getKey().getSrc();
            int dst = entry.getKey().getDst();
            edgeWeights.setWeight1(src, dst, entry.getValue());
        }
    }

    private static void quickSort(int[] arr, int low, int high, double[] value) {
        int i, j, temp1, t;
        double temp2, t2;
        if (low > high) {
            return;
        }
        i = low;
        j = high;
        temp1 = arr[low];
        temp2 = value[low];
        while (i < j) {
            while (temp2 >= value[j] && i < j) {
                j--;
            }
            while (temp2 <= value[i] && i < j) {
                i++;
            }
            if (i < j) {
                t = arr[j];
                arr[j] = arr[i];
                arr[i] = t;
                t2 = value[j];
                value[j] = value[i];
                value[i] = t2;
            }
        }
        arr[low] = arr[i];
        value[low] = value[i];
        arr[i] = temp1;
        value[i] = temp2;
        //递归调用左半数组
        quickSort(arr, low, j - 1, value);
        //递归调用右半数组
        quickSort(arr, j + 1, high, value);
    }

    private static void shellSort(int[] numArray, double[] value) {
        if (value.length < 2) {
            return;
        }
        // 第一次分为numArray.length/2组，步长也是numArray.length/2,每次缩减为原来的一半
        for (int group = value.length / 2; group > 0; ) {
            groupSort(numArray, group, value);
            group /= 2;
        }
    }

    // 希尔排序对分组后的每组进行排序
    private static void groupSort(int[] numArray, int group, double[] value) {
        // 对分组后的每一组元素进行排序
        for (int i = 0; i < group; i++) {
            insertSortInner(numArray, group, i, value);
        }
    }

    // 希尔排序内部使用的插入排序
    private static void insertSortInner(int[] numArray, int group, int groupIndex, double[] value) {
//        System.out.println("开始排序，分组数:" + group + "，开始第" + (groupIndex + 1) + "组排序");
        // 插入排序,注意步长
        for (int i = groupIndex + group; i < value.length; ) {
            for (int k = i; k >= 0; ) {
                if (k - group < 0) {
                    break;
                }

                if (value[k] > value[k - group]) {
                    int tmp = numArray[k];
                    numArray[k] = numArray[k - group];
                    numArray[k - group] = tmp;
                    double tmp2 = value[k];
                    value[k] = value[k - group];
                    value[k - group] = tmp2;
                } else {
                    break;
                }
                k -= group;
            }
            i += group;
        }
//        System.out.println("排序后:" + arrayToString(numArray));
    }

    private static void heapSort(int[] arr, double[] value) {
        int len = arr.length;

        // 从最后一个非叶子节点开始调整堆
        for (int i = len / 2 - 1; i >= 0; i--) {
            adjustHeap(arr, i, len, value);
        }

        // 交换堆顶元素和堆底元素，并重新调整堆
        for (int i = len - 1; i > 0; i--) {
            int tmp = arr[0];
            arr[0] = arr[i];
            arr[i] = tmp;
            double tmp2 = value[0];
            value[0] = value[i];
            value[i] = tmp2;
            adjustHeap(arr, 0, i, value);
        }
    }

    /**
     * 调整堆
     *
     * @param arr 待调整的堆
     * @param i   要调整的节点
     * @param len 堆的长度
     */
    private static void adjustHeap(int[] arr, int i, int len, double[] value) {
        double temp = value[i];
        int temp2 = arr[i];
        for (int k = 2 * i + 1; k < len; k = 2 * k + 1) {
            if (k + 1 < len && value[k] > value[k + 1]) {
                k++;
            }
            if (value[k] < temp) {
                value[i] = value[k];
                arr[i] = arr[k];
                i = k;
            } else {
                break;
            }
        }
        value[i] = temp;
        arr[i] = temp2;
    }
}
