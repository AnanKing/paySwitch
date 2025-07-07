package treeembedding.other;

import gtna.graph.Edge;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WriteToTxt {

    public static void writeTxt(String path, String[] s) {
        File writefile = new File(path); // Relative path, if not, a new output txt file will be created.
        if (writefile.exists() == false) {  // Check whether the file exists, if not to create a new file.
            try {
                writefile.createNewFile();
                writefile = new File(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(writefile));
            for (String str : s) {
                out.write(str + "\r\n"); //
                out.flush(); // Push the cache contents into the file
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<Edge, Long> readTxt(String path) {
        Map<Edge, Long> res = new HashMap<>();
        File file = new File(path);
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(path));//Construct a BufferedReader class to read file
                String s = null;
                while ((s = br.readLine()) != null) {//used readLine method，every time read one line
                    String[] s1 = s.split(" ");
                    res.put(new Edge(Integer.valueOf(s1[0]), Integer.valueOf(s1[1])), Long.valueOf(s1[2]));
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public static int readDepth(int nodes) {
        String path = "E:\\Intellij IDEA\\WorkShop\\speedy\\data\\t0-4graph.txt";
        File file = new File(path);
        int depth = 0;
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(path));
                String s = null;
                while ((s = br.readLine()) != null) {
                    String[] s1 = s.split(";");
                    if (nodes == Integer.valueOf(s1[1])) {
                        depth = Integer.valueOf(s1[2]);
                    }
                    //depth = Integer.valueOf(s1[2]);
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return depth;
    }

    public static void writeEdgeToTxt(String path, Map<Edge, Long> map) {
        /* write in Txt */
        File writefile = new File(path); // Relative path, if not, a new output txt file will be created.
        if (writefile.exists() == false) {  // Check whether the file exists, if not to create a new file.
            try {
                writefile.createNewFile();
                writefile = new File(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(writefile));
            Iterator<Map.Entry<Edge, Long>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Edge, Long> entry = iterator.next();
                Edge key = entry.getKey();
                Long value = entry.getValue();
                //System.out.println(key + ":" + value);
                String str = key.getSrc() + " " + key.getDst() + " " + value;
                //String str = key + " " + value;
                out.write(str + "\r\n"); //
                out.flush(); // Push the cache contents into the file
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
