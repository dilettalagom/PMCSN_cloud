package batch;

import pmcsn.Rngs;
import pmcsn.Util;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import static trantient.Configuration.STOP_Stazionario;
import static trantient.Configuration.seed;


public class StartBatch {

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

            String filename = seed + "_Batch_Alg1" + selected;
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new FileWriter("Matlab/" + filename + ".csv"));
                Util.print_on_file(writer, new String[]{"seed", "stop", "cloudlet", "cloudlet_task1", "cloudlet_task2",
                        "cloud", "cloud_task1", "cloud_task2",
                        "system", "system_task1", "system_task2"});
            } catch (IOException e) {
                e.printStackTrace();
            }


            // for (double stop : STOP_T) {
            //   for (int i = 0; i < replications; i++) {

            //TODO: errore in cloud--> a volte negativi
            switch (selected) {
                case 1: {
                    Simulator1_Batch s1Batch = new Simulator1_Batch();
                    ArrayList<String> values = s1Batch.RunSimulation(r, STOP_Stazionario, Long.toString(r.getSeed()), "Alg1");
                    Util.print_on_file(writer, Util.convertArrayList(values));
                    break;
                }
                case 2: {
                    Simulator2_Batch s_algorith2 = new Simulator2_Batch();
                    ArrayList<String> values = s_algorith2.RunSimulation(r, STOP_Stazionario, Long.toString(r.getSeed()), "Alg2_");

                    Util.print_on_file(writer, Util.convertArrayList(values));
                    break;
                }
                default:
                    System.out.print("Inserire un valore significativo!\n\n ");
                    break;

            }

            //       r.plantSeeds(r.getSeed());
            //     }
            //  }

            assert (writer != null);
            writer.close();

        }

    }
}
