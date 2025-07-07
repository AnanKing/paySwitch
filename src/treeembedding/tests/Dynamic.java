package treeembedding.tests;

import gtna.data.Series;
import gtna.metrics.Metric;
import gtna.networks.Network;
import gtna.networks.util.ReadableFile;
import gtna.util.Config;

import java.io.File;
import java.io.FilenameFilter;

import treeembedding.credit.*;
import treeembedding.credit.partioner.Partitioner;
import treeembedding.credit.partioner.RandomPartitioner;
import treeembedding.treerouting.Treeroute;
import treeembedding.treerouting.TreerouteSilentW;
import treeembedding.treerouting.TreerouteTDRAP;

public class Dynamic {

    /**
     * @param args 0: run
     *             1: config (0- SilentWhispers, 7- SpeedyMurmurs, 10-MaxFlow)
     *             2: steps previously completed
     */
    public static void main(String[] args) {
        // General parameters
        Config.overwrite("SKIP_EXISTING_DATA_FOLDERS", "false");
        String results = "./data/";
        Config.overwrite("MAIN_DATA_FOLDER", results);
        String path = "./resources/";

        int run = Integer.parseInt(args[0]);
        int config = Integer.parseInt(args[1]);
        int step = Integer.parseInt(args[2]);
        String prefix;
        switch (config) {
            case 0:
                prefix = "SW";
                break;
            case 1:
                prefix = "SS";
                break;//最短路径
            case 2:
                prefix = "DS";
                break;//两条不相交路径
            case 7:
                prefix = "SM";
                break;
            case 8:
                prefix = "F";
                break;//Flare算法
            case 10:
                prefix = "M";
                break;
            default:
                throw new IllegalArgumentException("Routing algoithm not supported");
        }
        String graph, trans, newlinks;
        if (step == 0) {
//            graph = path + "finalSets/dynamic/jan2013-lcc-t0.graph";
//            trans = path + "finalSets/dynamic/jan2013-trans-lcc-noself-uniq-merged-AllE8Nonull.txt";
            graph = path + "finalSets/dynamic/jan2013-lcc-t0-5.graph";
            trans = path + "finalSets/dynamic/jan2013-trans-lcc-noself-uniq-1M0-5-new.txt";
//            newlinks = path + "finalSets/dynamic/jan2013-newlinks-lcc-sorted-uniq-t0-nul.txt";
            newlinks = path + "finalSets/dynamic/jan2013-newlinks-lcc-sorted-uniq-merge_All.txt";
        } else {
            graph = results + "READABLE_FILE_" + prefix + "-P" + step + "-93502/" + run + "/";
            FilenameFilter fileNameFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.contains("CREDIT_NETWORK") || name.contains("CREDIT_MAX")) {
                        return true;
                    }
                    return false;
                }
            };
            String[] files = (new File(graph)).list(fileNameFilter);
            graph = graph + files[0] + "/graph.txt";
            trans = path + "finalSets/dynamic/jan2013-trans-lcc-noself-uniq-" + (step + 1) + ".txt";
            newlinks = path + "finalSets/dynamic/jan2013-newlinks-lcc-sorted-uniq-t" + (step) + ".txt";
        }
        switch (config) {
            case 0:
                runDynSWSM(new String[]{graph, "SW-P" + (step + 1), trans, newlinks, "0", "" + run});
                break;
            case 1:
                runDynSWSM(new String[]{graph, "SS-P" + (step + 1), trans, newlinks, "1", "" + run});
                break;//最短路径
            case 2:
                runDynSWSM(new String[]{graph, "DS-P" + (step + 1), trans, newlinks, "2", "" + run});
                break;//两条不相交路径
            case 7:
                runDynSWSM(new String[]{graph, "SM-P" + (step + 1), trans, newlinks, "7", "" + run});
                break;
            case 8:
                runFlare(graph, trans, "F-P" + (step + 1), newlinks, 165);
                break;//Flare算法
            case 10:
                runMaxFlow(graph, trans, "M-P" + (step + 1), newlinks, 165.55245497208898 * 1000);
                break;
        }

    }

    public static void runDynSWSM(String[] args) {
        Config.overwrite("SERIES_GRAPH_WRITE", "" + true);
        Config.overwrite("SKIP_EXISTING_DATA_FOLDERS", "false");
        String graph = args[0];
        String name = args[1];
        String trans = args[2];
        String add = args[3];
        int type = Integer.parseInt(args[4]);
        int i = Integer.parseInt(args[5]);

        double epoch = 165.55245497208898 * 1000;
        Treeroute ra;
        boolean dyn;
        boolean multi;
        boolean shortest;
        boolean disjoint;
        if (type == 0) {
            ra = new TreerouteSilentW();
            multi = true;
            dyn = false;
            shortest = false;
            disjoint = false;
        } else if (type == 1) {
            ra = new TreerouteTDRAP();
            multi = false;
            dyn = false;
            shortest = true;
            disjoint = false;
        } else if (type == 2) {
            ra = new TreerouteTDRAP();
            multi = false;
            dyn = false;
            shortest = false;
            disjoint = true;
        } else {
            ra = new TreerouteTDRAP();
            multi = false;
            dyn = true;
            shortest = false;
            disjoint = false;
        }
        int max = 1;
        double req = 165.55245497208898 * 2;
        //int[] roots ={64,36,43};
        int[] roots = {1};
//		int[] roots ={0};
//		int[] roots ={186,14,275};
        Partitioner part = new RandomPartitioner();

        Network net = new ReadableFile(name, name, graph, null);
        CreditNetwork2 cred = new CreditNetwork2(trans, name, epoch, ra,
                dyn, multi, shortest, disjoint, req, part, roots, max, add);
        Series.generate(net, new Metric[]{cred}, i, i);
    }

    public static void runMaxFlow(String graph, String transList, String name, String links,
                                  double epoch) {
        Config.overwrite("SERIES_GRAPH_WRITE", "" + true);
        Config.overwrite("SKIP_EXISTING_DATA_FOLDERS", "false");
        Config.overwrite("MAIN_DATA_FOLDER", "./data/");
        CreditMaxFlow5 m = new CreditMaxFlow5(transList, name,
                0, 0, links, epoch);
        Network test = new ReadableFile(name, name, graph, null);
        Series.generate(test, new Metric[]{m}, 1);
    }

    public static void runFlare(String graph, String transList, String name, String links,
                                double epoch) {
        Config.overwrite("SERIES_GRAPH_WRITE", "" + true);
        Config.overwrite("SKIP_EXISTING_DATA_FOLDERS", "false");
        Config.overwrite("MAIN_DATA_FOLDER", "./data/");
        Partitioner partitioner = new RandomPartitioner();
        CreditFlare c = new CreditFlare(transList, name, 3, 10, 10,
                10, epoch, partitioner, false);
        Network test = new ReadableFile(name, name, graph, null);
        Series.generate(test, new Metric[]{c}, 1);
    }

}
