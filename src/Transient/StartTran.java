package Transient;
import pmcsn.Estimate;
import pmcsn.Rngs;
import pmcsn.Util;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import static pmcsn.Configuration.*;
import static pmcsn.Util.*;


public class StartTran {

    public static void main(String[] args) {

        if(!Util.createDirectoriesTree("transient")){
            System.out.println("Errore durante la creazione delle cartelle per lo store dei risulatati");
            System.exit(1);
        };


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

           /* TODO: Per creare i file dello stazionario -> non cancellare
           PrintWriter writer = null;
            try {
                writer = new PrintWriter(new FileWriter("Matlab/transient/" +"IntervalloConfidenza" + "Alg" + selected + ".csv"));
                Util.print_on_file(writer, Util.titlesTran);


            } catch (IOException e) {
                e.printStackTrace();
            }*/

            // for (double stop_i : STOP_T) {
            for(int j=0; j<STOP_T.length; j++){

                PrintWriter estimateTempiWriter = null;
                PrintWriter estimatePacchettiWriter = null;
                PrintWriter estimateThoughtputWriter = null;


               /* PrintWriter writerConfidenzaPlot = null;
                try {
                    writerConfidenzaPlot = new PrintWriter(new FileWriter("Matlab/transient/estimate/" + "estimateFile" + stop_i + "Alg" + selected + ".csv"));
                    Util.print_on_file(writerConfidenzaPlot, Util.titlesEstimate);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                */

                //store per i risultati
                ArrayList<ArrayList<Double>> estimateTempi = new ArrayList<>();
                for(int i=0;i<9;i++)
                    estimateTempi.add(new ArrayList<Double>());

                for (int i = 0; i < tran_replications; i++) {

                    switch (selected) {

                        case 1: {

                             estimateTempiWriter = Util.createFiles(ROOTTRA1 , "estimateTempi/estimateTempi" +  String.valueOf(j) + "Alg" + selected + ".csv");
                            //estimatePacchettiWriter = Util.createFiles(ROOTTRA1 , "estimatePacchetti/estimatePacchettiFile" + stop_i + "Alg" + selected + ".csv");
                            //estimateThoughtputWriter = Util.createFiles(ROOTTRA1 , "estimateThroughput/estimateThoughtputFile" + stop_i + "Alg" + selected + ".csv");


                            Simulator1_Tran s_algorith1 = new Simulator1_Tran();
                            s_algorith1.RunSimulation(r, STOP_T[j], Long.toString(r.getSeed()), "Alg1", estimateTempi);

                            break;
                        }
                        case 2: {

                            estimateTempiWriter = Util.createFiles(ROOTTRA2 , "estimateTempi/estimateTempiFile" +  String.valueOf(j)  + "Alg" + selected + ".csv");
                            //estimatePacchettiWriter = Util.createFiles(ROOTTRA2 , "estimatePacchetti/estimatePacchettiFile" + stop_i + "Alg" + selected + ".csv");
                            //estimateThoughtputWriter = Util.createFiles(ROOTTRA2 , "estimateThroughput/estimateThoughtputFile" + stop_i + "Alg" + selected + ".csv");

                            Simulator2_Tran s_algorith2 = new Simulator2_Tran();
                            s_algorith2.RunSimulation(r, STOP_T[j], Long.toString(r.getSeed()), "Alg2", estimateTempi);

                            break;
                        }
                        default:
                            System.out.print("Inserire un valore significativo!\n\n ");
                            break;

                    }
                    r.plantSeeds(r.getSeed());
                }
                Estimate e = new Estimate();
                e.calcolateConfidenceByArrays(estimateTempi, Double.toString(STOP_T[j]), estimateTempiWriter);

                System.out.flush();

                assert (estimateTempiWriter != null);
                estimateTempiWriter.close();
            }

            /*assert (writer != null);
            writer.close();*/

        }
    }

}
