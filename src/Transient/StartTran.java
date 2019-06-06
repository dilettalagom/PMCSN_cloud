package Transient;
import pmcsn.Estimate;
import pmcsn.Rngs;
import pmcsn.Util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import static pmcsn.Configuration.*;



public class StartTran {

    public static void main(String[] args) {

        Path path = Paths.get("../PMCSN_cloud/Matlab/transient");
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            System.out.print("C'è stato un errore durante la creazione della cartella\n");
            System.exit(1);
        }


        while (true) {

            Rngs r = new Rngs();
            r.plantSeeds(Long.parseLong(seed));

            int selected = 0;

            System.out.print("\n\t\t\tBenvenuto nella simulazione transiente.\nQuale dei due simulatori vuoi eseguire? [1 or 2] \t (Inserire 0 per terminare): ");
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

           /* PrintWriter writer = null;
            try {
                writer = new PrintWriter(new FileWriter("Matlab/transient/" +"IntervalloConfidenza" + "Alg" + selected + ".csv"));
                Util.print_on_file(writer, Util.titlesTran);


            } catch (IOException e) {
                e.printStackTrace();
            }*/

            for (double stop_i : STOP_T) {

                PrintWriter writerConfidenzaPlot = null;
                try {
                    writerConfidenzaPlot = new PrintWriter(new FileWriter("Matlab/transient/" + "estimateFile" + stop_i + "Alg" + selected + ".csv"));
                    Util.print_on_file(writerConfidenzaPlot, Util.titlesEstimate);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //store per i risultati
                ArrayList<ArrayList<Double>> values = new ArrayList<>();

                for (int i = 0; i < tran_replications; i++) {

                    switch (selected) {

                        case 1: {
                            Simulator1_Tran s_algorith1 = new Simulator1_Tran();
                            values.add( s_algorith1.RunSimulation(r, stop_i, Long.toString(r.getSeed()), "Alg1"));

                            break;
                        }
                        case 2: {
                            Simulator2_Tran s_algorith2 = new Simulator2_Tran();
                            values.add( s_algorith2.RunSimulation(r, stop_i, Long.toString(r.getSeed()), "Alg2" ));

                            break;
                        }
                        default:
                            System.out.print("Inserire un valore significativo!\n\n ");
                            break;

                    }
                    r.plantSeeds(r.getSeed());
                }
                Estimate e = new Estimate();
                e.calcolateConfidenceByArrays(values, Double.toString(stop_i), writerConfidenzaPlot);

                System.out.flush();

                assert (writerConfidenzaPlot != null);
                writerConfidenzaPlot.close();
            }

            /*assert (writer != null);
            writer.close();*/

        }
    }

}
