package restructed;
import pmcsn.Rngs;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static restructed.Configuration.*;



public class Start {

    public static void main(String[] args) {

        Rngs r = new Rngs();
        r.plantSeeds(Long.parseLong(seed));

        while (true) {

            int selected = 0;
            System.out.print("Quale dei due simulatori vuoi runnare? [1 or 2] \t (Inserire 0 per terminare): ");
            Scanner reader = new Scanner(new InputStreamReader(System.in));
            try {
                selected = reader.nextInt();
            } catch (Exception e) {
                System.out.print("Inserire un valore significativo!\n\n");
                System.exit(1);
            }
            if (selected == 0) {
                System.exit(0);
            }
            String filename = seed + "_Alg" + selected;
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new FileWriter("Matlab/" + filename + ".csv"));
                Util.print_on_file(writer, new String[]{"seed", "stop", "cloudlet", "cloudlet_task1", "cloudlet_task2",
                        "cloud", "cloud_task1", "cloud_task2",
                        "system", "system_task1", "system_task2"});
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (String selected_seed : seeds) {

                for (int j = 0; j < STOP_T.length; j++) {


                    for (int i = 0; i < replications; i++) {

                        switch (selected) {
                            case 1: {
                                Simulator_alg1 s_algorith1 = new Simulator_alg1();
                                ArrayList<String> values = s_algorith1.RunSimulation(r, STOP_T[j], Long.toString(r.getSeed()), "Alg1");

                                Object[] temp = values.toArray();
                                String[] str = Arrays.copyOf(temp,
                                        temp.length,
                                        String[].class);

                                Util.print_on_file(writer, str);
                                break;
                            }
                            case 2: {
                                Simulator_alg2 s_algorith2 = new Simulator_alg2();
                                ArrayList<String> values = s_algorith2.RunSimulation(r, STOP_T[j], Long.toString(r.getSeed()), "Alg2_");


                                Object[] temp = values.toArray();
                                String[] str = Arrays.copyOf(temp,
                                        temp.length,
                                        String[].class);

                                Util.print_on_file(writer, str);
                                break;
                            }
                            default:
                                System.out.print("Inserire un valore significativo!\n\n ");
                                break;

                        }

                        r.plantSeeds(r.getSeed());
                        // calcolo intervallo di confidenza

                    }
                }

                assert (writer != null);
                writer.close();


            }
        }
    }
}
