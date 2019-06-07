package pmcsn;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;


public class Util {


    public static final String ROOTBATCH1 = "Matlab/batch/batch1/";
    public static final String ROOTBATCH2 = "Matlab/batch/batch2/";
    public static final String ROOTTRA1 = "Matlab/transient/transient1/";
    public static final String ROOTTRA2 = "Matlab/transient/transient2/";


    public static boolean createDirectoriesTree(String dir){
        try {
            Path path = Paths.get("../PMCSN_cloud/Matlab/"+ dir);
            Files.createDirectories(path);
            for(int i=1; i<3;i++){
                Path alg = Paths.get("../PMCSN_cloud/Matlab/"+dir+"/"+dir+i);
                Files.createDirectories(alg);
                Path pathEstimateTempi = Paths.get("../PMCSN_cloud/Matlab/"+dir+"/"+dir+i+"/estimateTempi");
                Path pathEstimatePacchetti = Paths.get("../PMCSN_cloud/Matlab/"+dir+"/"+dir+i+"/estimatePacchetti");
                Path pathEstimateThroughput = Paths.get("../PMCSN_cloud/Matlab/"+dir+"/"+dir+i+"/estimateThroughput");
                Files.createDirectories(pathEstimateTempi);
                Files.createDirectories(pathEstimatePacchetti);
                Files.createDirectories(pathEstimateThroughput);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print("C'è stato un errore durante la creazione delle cartelle\n");
            return false;
        }
        return true;
    }

    public static String[] titlesTran = new String[]{"seed","stop","cloudlet", "cloudlet_task1", "cloudlet_task2",
            "cloud", "cloud_task1", "cloud_task2",
            "system", "system_task1", "system_task2"};

    public static String[] titlesEstimate = new String[]{"cloudlet", "+/-", "cloudlet_task1", "+/-", "cloudlet_task2", "+/-",
            "cloud", "+/-", "cloud_task1", "+/-", "cloud_task2", "+/-",
            "system", "+/-", "system_task1", "+/-", "system_task2", "+/-"};

    public static PrintWriter createFiles(String path, String filename){

        PrintWriter newWriter = null;
        try {
            newWriter = new PrintWriter(new FileWriter(path + filename));
            Util.print_on_file(newWriter, Util.titlesEstimate);

        } catch (IOException e) {
            System.out.print("C'è stato un errore durante la creazione del file\n");
            System.exit(1);

        }
        return newWriter;
    }


    public static void print_on_file(PrintWriter writer, String[] row) {

        for (String s : row) {
            writer.write(s);
            writer.write(';');
        }
        writer.write(System.getProperty("line.separator"));
    }

    public static void print_on_file_column(PrintWriter writer, String[] row) {

        for (String s : row) {
            writer.write(s);
            writer.write(';');
            writer.write(System.getProperty("line.separator"));
        }
    }

    public static String[] convertArrayList(ArrayList<String>arrayList) {
        Object[] temp = arrayList.toArray();
        return Arrays.copyOf(temp,
                temp.length,
                String[].class);
    }

    public static String[] convertMatrixList(ArrayList<ArrayList<Double>> arrayList) {
        String[] temp = new String[arrayList.size()];
        for (int i=0; i<arrayList.size(); i++)
            temp[i] = String.valueOf(arrayList.get(i).get(arrayList.get(i).size()-1));

        return temp;
    }


}
