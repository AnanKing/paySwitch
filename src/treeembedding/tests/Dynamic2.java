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

public class Dynamic2 {

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
//        String path = "D:/code/speedymurmur-experiments/speedy/resources/";
//        String path = "X:/project/java/speedymurmur-experiments/speedy/resources/";

        String path = "./resources/";
        //test update
        int run = Integer.parseInt(args[0]);
        int config = Integer.parseInt(args[1]);
        int step = Integer.parseInt(args[2]);
        int stepCount = Integer.parseInt(args[3]);
        String prefix;
        switch (config) {
            case 0:
                prefix = "SW";
                break;
            case 1:
                prefix = "SS";
                break;
            case 2:
                prefix = "DS";
                break;
            case 7:
                prefix = "SM";
                break;
            case 8:
                prefix = "F";
                break;
            case 10:
                prefix = "M";
                break;
            default:
                throw new IllegalArgumentException("Routing algoithm not supported");
        }
        String graph, trans, newlinks;
        if (step == 0) {
            graph = path + "finalSets/dynamic/jan2013-lcc-t0.graph";
            trans = path + "finalSets/dynamic/jan2013-trans-lcc-noself-uniq-merged-AllE8Nonull.txt";
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
            //graph = path + "finalSets/dynamic/jan2013-lcc-t0.graph";
            trans = path + "finalSets/dynamic/jan2013-trans-lcc-noself-uniq-" + (step + 1) + ".txt";
            newlinks = path + "finalSets/dynamic/jan2013-newlinks-lcc-sorted-uniq-t" + (step) + ".txt";
        }
        switch (config) {
            case 0:
                runDynSWSM(new String[]{graph, "SW-P" + (step + 1), trans, newlinks, "0", "" + run, "" + stepCount});
                break;
            case 1:
                runDynSWSM(new String[]{graph, "SS-P" + (step + 1), trans, newlinks, "1", "" + run, "" + stepCount});
                break;
            case 2:
                runDynSWSM(new String[]{graph, "DS-P" + (step + 1), trans, newlinks, "2", "" + run, "" + stepCount});
                break;
            case 7:
                runDynSWSM(new String[]{graph, "SM-P" + (step + 1), trans, newlinks, "7", "" + run, "" + stepCount});
                break;
            case 10:
                runMaxFlow(graph, trans, "M-P" + (step + 1), newlinks, 165.55245497208898 * 1000, stepCount);
                break;
            case 8:
                runFlare(graph, trans, "F-P" + (step + 1), newlinks, 165.55245497208898 * 1000, stepCount);
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
        int stepCount = Integer.parseInt(args[6]);

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
//        int[] roots = {64, 36, 43};
        //int[] roots = {1, 16, 39};
        int[] roots = {1};
        //int[] roots ={16,8,26};
        //int[] roots = {64};
        //int[] roots ={16,91,31,26,8};
        //int[] roots = {0, 1};
        Partitioner part = new RandomPartitioner();

        Network net = new ReadableFile(name, name, graph, null);
        CreditNetwork5 cred = new CreditNetwork5(trans, name, epoch, ra,
                dyn, multi, shortest, disjoint, req, part, roots, max, add, stepCount);
        Series.generate(net, new Metric[]{cred}, i, i);
    }

    public static void runMaxFlow(String graph, String transList, String name, String links,
                                  double epoch, int stepCount) {
        Config.overwrite("SERIES_GRAPH_WRITE", "" + true);
        Config.overwrite("SKIP_EXISTING_DATA_FOLDERS", "false");
        Config.overwrite("MAIN_DATA_FOLDER", "./data/");
        CreditMaxFlow4 m = new CreditMaxFlow4(transList, name,
                0, 0, links, epoch, stepCount);
        Network test = new ReadableFile(name, name, graph, null);
        Series.generate(test, new Metric[]{m}, 1);
    }

    public static void runFlare(String graph, String transList, String name, String links,
                                double epoch, int stepCount) {
        Config.overwrite("SERIES_GRAPH_WRITE", "" + true);
        Config.overwrite("SKIP_EXISTING_DATA_FOLDERS", "false");
        Config.overwrite("MAIN_DATA_FOLDER", "./data/");
        Partitioner partitioner = new RandomPartitioner();
        CreditFlare4 c = new CreditFlare4(transList, name, 3, 10, 10,
                10, epoch, partitioner, false, stepCount);
        Network test = new ReadableFile(name, name, graph, null);
        Series.generate(test, new Metric[]{c}, 1);
    }

}
