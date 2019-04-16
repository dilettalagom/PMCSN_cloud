package pmcsn;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class Util {

    public static ArrayList<String> titles = new ArrayList<>(Arrays.asList("Batch", "cloudlet", "cloudlet_task1", "cloudlet_task2",
            "cloud", "cloud_task1", "cloud_task2",
            "system", "system_task1", "system_task2"));

    public static void print_on_file(PrintWriter writer, String[] row) {

        for (String s : row) {
            writer.write(s);
            writer.write(';');
        }
        writer.write(System.getProperty("line.separator"));
    }


    public static String[] convertArrayList(ArrayList<String>arrayList) {
        Object[] temp = arrayList.toArray();
        return Arrays.copyOf(temp,
                temp.length,
                String[].class);
    }


}
