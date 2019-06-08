package Transient;
import pmcsn.Estimate;
import pmcsn.Rngs;
import pmcsn.Statistics;
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

            for(int j=0; j<STOP_T.length; j++){

                PrintWriter estimateTempiWriter = null;
                PrintWriter estimateTaskWriter = null;
                PrintWriter estimateThoughtputWriter = null;

                /*store per i risultati
                ArrayList<ArrayList<Double>> estimateTempi = new ArrayList<>();
                for(int i=0;i<9;i++)
                    estimateTempi.add(new ArrayList<Double>());*/
                Statistics statistics = new Statistics();

                for (int i = 0; i < tran_replications; i++) {

                    switch (selected) {

                        case 1: {

                            estimateTempiWriter = Util.createFiles(ROOTTRA1 , "estimateTempi/estimateTempi" +  String.valueOf(j) + "Alg" + selected + ".csv");
                            estimateTaskWriter = Util.createFiles(ROOTTRA1 , "estimateTask/estimateTaskFile" + String.valueOf(j) + "Alg" + selected + ".csv");
                            estimateThoughtputWriter = Util.createFiles(ROOTTRA1 , "estimateThroughput/estimateThoughtputFile" + String.valueOf(j) + "Alg" + selected + ".csv");


                            Simulator1_Tran s_algorith1 = new Simulator1_Tran();
                            s_algorith1.RunSimulation(r, STOP_T[j], Long.toString(r.getSeed()), "Alg1", statistics);

                            break;
                        }
                        case 2: {

                            estimateTempiWriter = Util.createFiles(ROOTTRA2 , "estimateTempi/estimateTempiFile" +  String.valueOf(j)  + "Alg" + selected + ".csv");
                            estimateTaskWriter = Util.createFiles(ROOTTRA2 , "estimateTask/estimateTaskFile" + String.valueOf(j) + "Alg" + selected + ".csv");
                            estimateThoughtputWriter = Util.createFiles(ROOTTRA2 , "estimateThroughput/estimateThoughtputFile" + String.valueOf(j) + "Alg" + selected + ".csv");

                            Simulator2_Tran s_algorith2 = new Simulator2_Tran();
                            s_algorith2.RunSimulation(r, STOP_T[j], Long.toString(r.getSeed()), "Alg2", statistics);

                            break;
                        }
                        default:
                            System.out.print("Inserire un valore significativo!\n\n ");
                            break;

                    }
                    r.plantSeeds(r.getSeed());
                }
                Estimate e = new Estimate();
                //intervallo di confidenza dei tempi
                e.calcolateConfidenceByArrays(statistics.getEstimateTempi(), "tempo medio di risposta",  String.valueOf(j) , estimateTempiWriter);
                //intervallo di confidenza dei task
                e.calcolateConfidenceByArrays(statistics.getEstimatePacchetti(), "numero medio di task",  String.valueOf(j) , estimateTaskWriter);
                //intervallo di confidenza dei thoughtput
                e.calcolateConfidenceByArrays(statistics.getEstimateThroughput(),"Throughput ",  String.valueOf(j) , estimateThoughtputWriter);

                System.out.flush();

                closeFile(estimateTempiWriter);
                closeFile(estimateTaskWriter);
                closeFile(estimateThoughtputWriter);
            }

            /*assert (writer != null);
            writer.close();*/

        }
    }

}
