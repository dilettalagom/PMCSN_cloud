package Batch;

import pmcsn.Estimate;
import pmcsn.Rngs;
import pmcsn.Statistics;
import pmcsn.Util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import static pmcsn.Configuration.*;
import static pmcsn.Util.*;
import static pmcsn.Util.closeFile;


public class StartBatch2 {

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
            r.plantSeeds(Long.parseLong(seed));
            for(int i = 0 ; i<8;i++) {

                long newSeed= r.getSeed();
                r.plantSeeds(newSeed);

                PrintWriter estimateTempiWriter = null;
                PrintWriter estimateTaskWriter =  null;
                PrintWriter estimateThoughtputWriter = null;

                switch (selected) {
                    case 1: {
                        estimateTempiWriter = Util.createFiles(ROOTBATCH1 , "estimateTempi/estimateTempiFile" +  String.valueOf(i)  + "Alg" + selected + ".csv");
                        estimateTaskWriter = Util.createFiles(ROOTBATCH1 , "estimateTask/estimateTaskFile" +  String.valueOf(i)  + "Alg" + selected + ".csv");
                        estimateThoughtputWriter = Util.createFiles(ROOTBATCH1 , "estimateThroughput/estimateThoughtputFile" +  String.valueOf(i)  + "Alg" + selected + ".csv");

                        Simulator1_Batch s1Batch = new Simulator1_Batch();

                        Statistics statisticsALG1 = s1Batch.RunBatch(r, STOP_BATCH);
                        Estimate e = new Estimate();
                        //intervallo di confidenza dei tempi
                        e.calcolateConfidenceByArrays(statisticsALG1.getEstimateTempi(), "tempo medio di risposta", seed, estimateTempiWriter);
                        //intervallo di confidenza dei pacchetti
                        e.calcolateConfidenceByArrays(statisticsALG1.getEstimatePacchetti(), "numero medio di task", seed, estimateTaskWriter);
                        //intervallo di confidenza dei thoughtput
                        e.calcolateConfidenceByArrays(statisticsALG1.getEstimateThroughput(),"Throughput ", seed, estimateThoughtputWriter);

                        estimateTempiWriter.flush();
                        estimateTaskWriter.flush();
                        estimateThoughtputWriter.flush();
                        break;
                    }
                    case 2: {
                        estimateTempiWriter = Util.createFiles(ROOTBATCH2 , "estimateTempi/estimateTempiFile" + seed + "Alg" + selected + ".csv");
                        estimateTaskWriter = Util.createFiles(ROOTBATCH2 , "estimateTask/estimateTaskFile" + seed + "Alg" + selected + ".csv");
                        estimateThoughtputWriter = Util.createFiles(ROOTBATCH2 , "estimateThroughput/estimateThoughtputFile" + seed + "Alg" + selected + ".csv");

                        Simulator2_Batch s2Batch = new Simulator2_Batch();

                        Statistics statisticsALG2 = s2Batch.RunBatch(r, STOP_BATCH);
                        Estimate e = new Estimate();
                        e.calcolateConfidenceByArrays(statisticsALG2.getEstimateTempi(), "tempo medio di risposta", seed, estimateTempiWriter);
                        //intervallo di confidenza dei pacchetti
                        e.calcolateConfidenceByArrays(statisticsALG2.getEstimatePacchetti(), "numero medio di task", seed, estimateTaskWriter);
                        //intervallo di confidenza dei thoughtput
                        e.calcolateConfidenceByArrays(statisticsALG2.getEstimateThroughput(),"Throughput ", seed, estimateThoughtputWriter);

                        estimateTempiWriter.flush();
                        estimateTaskWriter.flush();
                        estimateThoughtputWriter.flush();
                        break;
                    }
                    default:
                        System.out.print("Inserire un valore significativo!\n\n ");
                        break;

                }
                closeFile(estimateTempiWriter);
                closeFile(estimateTaskWriter);
                closeFile(estimateThoughtputWriter);
            }
        }
    }
}

