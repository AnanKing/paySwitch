package treeembedding.other;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OutGraph {

    public static String txt2String(File file) {
        String result = "";
        Map<Integer, String> map = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {   //使用readLine方法，一次读一行
                String[] s1 = s.split(" ");
                if (!s1[0].equals(s1[1])) {
                    if (map.containsKey(Integer.parseInt(s1[0]))) {
                        String s2 = map.get(Integer.parseInt(s1[0]));
                        String[] num = s2.split(";");
                        int flag = 0;
                        for (int i = 0; i < num.length; i++) {
                            if (s1[1].equals(num[i])) {
                                flag = 1;
                                break;
                            } else {
                                flag = 0;
                            }
                        }
                        if (flag == 0) {
                            s2 = s2 + ";" + s1[1];
                            map.put(Integer.parseInt(s1[0]), s2);
                        }
                    } else {
                        map.put(Integer.parseInt(s1[0]), s1[1]);
                    }
                    if (map.containsKey(Integer.parseInt(s1[1]))) {
                        String s2 = map.get(Integer.parseInt(s1[1]));
                        String[] num = s2.split(";");
                        int flag = 0;
                        for (int i = 0; i < num.length; i++) {
                            if (s1[0].equals(num[i])) {
                                flag = 1;
                                break;
                            } else {
                                flag = 0;
                            }
                        }
                        if (flag == 0) {
                            s2 = s2 + ";" + s1[0];
                            map.put(Integer.parseInt(s1[1]), s2);
                        }
                    } else {
                        map.put(Integer.parseInt(s1[1]), s1[0]);
                    }
                }

            }
            br.close();
            writeToTxt("D:/code/speedymurmur-experiments/speedy/resources/Mydata/Test/Payment_Graph_Result-my-new-2.txt", map);
            int num = 0;
            for (Integer key : map.keySet()) {
                String value = map.get(key);
                String[] split = value.split(";");
                num = num + split.length;
            }
            System.out.println("num:" + num);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void writeToTxt(String path, Map<Integer, String> map) {
        /* 写入Txt文件 */
        File writefile = new File(path); // 相对路径，如果没有则要建立一个新的output。txt文件
        if (writefile.exists() == false) {  // 判断文件是否存在，不存在则生成
            try {
                writefile.createNewFile();
                writefile = new File(path);
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }
        try {
            //int num = 0;
            BufferedWriter out = new BufferedWriter(new FileWriter(writefile));
            Iterator<Map.Entry<Integer, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, String> entry = iterator.next();
                Integer key = entry.getKey();
                String value = entry.getValue();
                String str = key + ":" + value;
                out.write(str + "\r\n"); // \r\n即为换行
                //num++;
                out.flush(); // 把缓存区内容压入文件
            }
            out.close(); // 最后记得关闭文件
            //System.out.println(num);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        File file = new File("D:/code/speedymurmur-experiments/speedy/resources/Mydata/Test/TTransaction_Payment_NoSame_color_Del_Num_Nodelists_1.txt");
        File file1 = new File("D:/code/speedymurmur-experiments/speedy/resources/Mydata/Test/jan2013-lcc-t0-5-Edge.txt");

        System.out.println(txt2String(file));
    }

}
