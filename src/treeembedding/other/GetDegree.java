package treeembedding.other;

import gtna.graph.Edge;
import gtna.graph.Edges;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GetDegree {
    // obtain original graph
    public static void graphicHandle(String readPath, String writePath) {
        Map<Edge, Integer> edgeMap = new HashMap<>();
        Map<Edge, Integer> tempMap = new HashMap<>();
        File file = new File(readPath);
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(readPath));//构造一个BufferedReader类来读取文件
                String s = null;
                int num = 0;
                while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                    String[] s1 = s.split(":");
                    int src = Integer.valueOf(s1[0]);
                    if (s1[1].contains(";")) {
                        String[] strs = s1[1].split(";");
                        num = num + strs.length;
                        for (int i = 0; i < strs.length; i++) {
                            int dst = Integer.valueOf(strs[i]);
                            Edge edge = new Edge(src, dst);
                            Edge edge1 = new Edge(dst, src);
                            if (!tempMap.containsKey(edge)) {
                                edgeMap.put(edge, 1);
                                tempMap.put(edge1, 1);
                            }
                        }
                    } else {
                        num++;
                        int dst = Integer.valueOf(s1[1]);
                        Edge edge = new Edge(src, dst);
                        Edge edge1 = new Edge(dst, src);
                        if (!tempMap.containsKey(edge)) {
                            edgeMap.put(edge, 1);
                            tempMap.put(edge1, 1);
                        }
                    }
                }
                br.close();
                System.out.println(num);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /* 写入Txt文件  结点*/
        File writefile = new File(writePath); // 相对路径，如果没有则要建立一个新的output。txt文件
        if (writefile.exists() == false) {  // 判断文件是否存在，不存在则生成
            try {
                writefile.createNewFile();
                writefile = new File(writePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(writefile));
            for (Edge key : edgeMap.keySet()) {
                String res = key.getSrc() + " " + key.getDst();
                out.write(res + "\r\n"); // \r\n即为换行
                out.flush(); // 把缓存区内容压入文件
            }
            out.close(); // 最后记得关闭文件
            System.out.println(edgeMap.size());
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    // obtain degrees
    public static void calculateDegrees(String readPath, String writePath) {
        Map<Integer, Integer> deepMap = new HashMap<>();
        File file = new File(readPath);
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(readPath));//构造一个BufferedReader类来读取文件
                String s = null;
                while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                    String[] s1 = s.split(";");
                    int src = Integer.valueOf(s1[0]);
                    int dst = Integer.valueOf(s1[1]);
                    if (deepMap.containsKey(src)) {
                        deepMap.put(src, deepMap.get(src) + 1);
                    } else {
                        deepMap.put(src, 1);
                    }
                    if (deepMap.containsKey(dst)) {
                        deepMap.put(dst, deepMap.get(dst) + 1);
                    } else {
                        deepMap.put(dst, 1);
                    }
                }
                // }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /* 写入Txt文件  深度*/
        File writefile1 = new File(writePath); // 相对路径，如果没有则要建立一个新的output。txt文件
        if (writefile1.exists() == false) {  // 判断文件是否存在，不存在则生成
            try {
                writefile1.createNewFile();
                writefile1 = new File(writePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter out1 = new BufferedWriter(new FileWriter(writefile1));
            for (Integer key : deepMap.keySet()) {
                String res = key + " " + deepMap.get(key);
                out1.write(res + "\r\n"); // \r\n即为换行
                out1.flush(); // 把缓存区内容压入文件
            }
            out1.close(); // 最后记得关闭文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getDegrees(String readPath, String writePath) {
        //Map<Edge, Integer> edgeMap = new HashMap<>();
        Map<Edge, Integer> deepMap = new HashMap<>();
        File file = new File(readPath);
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(readPath));//构造一个BufferedReader类来读取文件
                String s = null;
                while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                    String[] s1 = s.split(";");
                    int src = Integer.valueOf(s1[0]);
                    int dst = Integer.valueOf(s1[1]);
                    Edge edge = new Edge(src, dst);
                    //edgeMap.put(edge, 1);
                    if (deepMap.containsKey(edge)) {
                        deepMap.put(edge, deepMap.get(edge) + 1);
                    } else {
                        deepMap.put(edge, 1);
                    }
                    if (deepMap.containsKey(edge)) {
                        deepMap.put(edge, deepMap.get(edge) + 1);
                    } else {
                        deepMap.put(edge, 1);
                    }

                }
                // }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /* 写入Txt文件  边+深度*/
        File writefile1 = new File(writePath); // 相对路径，如果没有则要建立一个新的output。txt文件
        if (writefile1.exists() == false) {  // 判断文件是否存在，不存在则生成
            try {
                writefile1.createNewFile();
                writefile1 = new File(writePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter out1 = new BufferedWriter(new FileWriter(writefile1));
            for (Edge key : deepMap.keySet()) {
                if (deepMap.get(key) > 1) {//写入深度大于2的边
                    String res = key.getSrc() + " " + key.getDst() + " " + deepMap.get(key);
                    out1.write(res + "\r\n"); // \r\n即为换行
                    out1.flush(); // 把缓存区内容压入文件
                }
            }
            out1.close(); // 最后记得关闭文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String readPath1 = "D:\\code\\speedymurmur-experiments\\speedy\\resources\\finalSets\\dynamic\\Graph\\jan2013-lcc-t0-graph.txt";
        String readPath2 = "D:\\code\\speedymurmur-experiments\\speedy\\resources\\finalSets\\dynamic\\Graph\\jan2013-lcc-t0-root3dyn-graph_SPANNINGTREE_0.txt";
        //String readPath1_1 = "D:\\code\\speedymurmur-experiments\\speedy\\resources\\finalSets\\dynamic\\Graph\\Payment_Graph_Result_1-1.txt";
//        String readPathDesk = "C:/Users/Anan Jin/Desktop/Payment_Graph_Result_1.txt";
//        String writePathDesk = "C:/Users/Anan Jin/Desktop/Payment_Graph_Result_1-Test.txt";
        //String writePath1_1 = "D:\\code\\speedymurmur-experiments\\speedy\\resources\\finalSets\\dynamic\\Graph\\Graph_Result_OriginalEdge_1-1.txt";
        String writePath1 = "D:\\code\\speedymurmur-experiments\\speedy\\resources\\finalSets\\dynamic\\Graph\\jan2013-lcc-t0-graph-result-Original-Graph.txt";
        String writePath2 = "D:\\code\\speedymurmur-experiments\\speedy\\resources\\finalSets\\dynamic\\Graph\\jan2013-lcc-t0-root3dyn-graph_SPANNINGTREE_2-Degrees.txt";
        String writePath3 = "D:\\code\\speedymurmur-experiments\\speedy\\resources\\finalSets\\dynamic\\Graph\\jan2013-lcc-t0-root3dyn-graph_SPANNINGTREE_0-Degrees-large2.txt";
        String readPath_1 = "D:/code/speedymurmur-experiments/speedy/resources/Mydata/Test/Payment_Graph_Result.txt";
        String writePath_1 = "D:/code/speedymurmur-experiments/speedy/resources/Mydata/Test/Payment_Graph_Result_OriginalEdge.txt";
        graphicHandle(readPath_1, writePath_1);
        calculateDegrees(readPath2, writePath2);
        //getDegrees(readPath2, writePath3);
    }
}
