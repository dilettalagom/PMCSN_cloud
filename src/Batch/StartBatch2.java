package Batch;

import pmcsn.Estimate;
import pmcsn.Rngs;
import pmcsn.Util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import static pmcsn.Configuration.*;


public class StartBatch2 {

    public static void main(String[] args) {

        Path path = Paths.get("../PMCSN_cloud/Matlab/batch");
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

                PrintWriter estimateWriter = null;
                try {
                    estimateWriter = new PrintWriter(new FileWriter("Matlab/batch/" + "estimateFile" + Long.toString(newSeed) + "Alg" + selected + ".csv"));
                    Util.print_on_file(estimateWriter, Util.titlesEstimate);

                } catch (IOException e) {
                    System.out.print("C'è stato un errore durante la creazione del file\n");
                    System.exit(1);
                }

                switch (selected) {
                    case 1: {
                        Simulator1_Batch s1Batch = new Simulator1_Batch();

                        ArrayList<ArrayList<Double>> simulatorDatas = s1Batch.RunBatch(r, STOP_BATCH);
                        Estimate e = new Estimate();
                        e.calcolateConfidenceByArrays(simulatorDatas, Long.toString(newSeed), estimateWriter);
                        estimateWriter.flush();

                        break;
                    }
                    case 2: {
                        Simulator2_Batch s2Batch = new Simulator2_Batch();

                        ArrayList<ArrayList<Double>> simulatorDatas = s2Batch.RunBatch(r, STOP_BATCH);
                        Estimate e = new Estimate();
                        e.calcolateConfidenceByArrays(simulatorDatas, Long.toString(newSeed), estimateWriter);
                        estimateWriter.flush();

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

