package Batch;

import pmcsn.Estimate;
import pmcsn.Rngs;
import pmcsn.Util;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import static pmcsn.Configuration.*;
import static pmcsn.Util.*;


public class StartBatch {

    public static void main(String[] args) {

        if(!Util.createDirectoriesTree("batch")){
            System.out.println("Errore durante la creazione delle cartelle per lo store dei risulatati");
            System.exit(1);
        };

        while (true) {

            Rngs r = new Rngs();
            r.plantSeeds(Long.parseLong(seed));

            int selected = 0;
            System.out.print("\n\t\t\tBenvenuto nella simulazione Batch.\nQuale dei due simulatori vuoi eseguire? [1 or 2] \t (Inserire 0 per terminare): ");
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

            for(String seed : seeds_collection) {

                r.plantSeeds(Long.parseLong(seed));

                PrintWriter estimateTempiWriter = null;
                PrintWriter estimatePacchettiWriter =  null;
                PrintWriter estimateThoughtputWriter = null;

                switch (selected) {
                    case 1: {
                        estimateTempiWriter = Util.createFiles(ROOTBATCH1 , "estimateTempi/estimateTempiFile" + seed + "Alg" + selected + ".csv");
                        //estimatePacchettiWriter = Util.createFiles(ROOTBATCH1 , "estimatePacchetti/estimatePacchettiFile" + seed + "Alg" + selected + ".csv");
                        //estimateThoughtputWriter = Util.createFiles(ROOTBATCH1 , "estimateThroughput/estimateThoughtputFile" + seed + "Alg" + selected + ".csv");


                        Simulator1_Batch s1Batch = new Simulator1_Batch();

                        ArrayList<ArrayList<Double>> simulatorDatas = s1Batch.RunBatch(r, STOP_BATCH);
                        Estimate e = new Estimate();
                        e.calcolateConfidenceByArrays(simulatorDatas, seed, estimateTempiWriter);
                        estimateTempiWriter.flush();

                        break;
                    }
                    case 2: {
                        estimateTempiWriter = Util.createFiles(ROOTBATCH2 , "estimateTempi/estimateTempiFile" + seed + "Alg" + selected + ".csv");
                        //estimatePacchettiWriter = Util.createFiles(ROOTBATCH2 , "estimatePacchetti/estimatePacchettiFile" + seed + "Alg" + selected + ".csv");
                        //estimateThoughtputWriter = Util.createFiles(ROOTBATCH2 , "estimateThroughput/estimateThoughtputFile" + seed + "Alg" + selected + ".csv");


                        Simulator2_Batch s2Batch = new Simulator2_Batch();

                        ArrayList<ArrayList<Double>> simulatorDatas = s2Batch.RunBatch(r, STOP_BATCH);
                        Estimate e = new Estimate();
                        e.calcolateConfidenceByArrays(simulatorDatas, seed, estimateTempiWriter);
                        estimateTempiWriter.flush();

                        break;
                    }
                    default:
                        System.out.print("Inserire un valore significativo!\n\n ");
                        break;

                }
            }
        }
    }
}

