package pmcsn;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;


public class Util {

    public static String[] titlesTran = new String[]{"seed","stop","cloudlet", "cloudlet_task1", "cloudlet_task2",
            "cloud", "cloud_task1", "cloud_task2",
            "system", "system_task1", "system_task2"};

    public static String[] titlesEstimate = new String[]{"cloudlet", "+/-", "cloudlet_task1", "+/-", "cloudlet_task2", "+/-",
            "cloud", "+/-", "cloud_task1", "+/-", "cloud_task2", "+/-",
            "system", "+/-", "system_task1", "+/-", "system_task2", "+/-"};



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
