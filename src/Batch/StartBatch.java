package Batch;

import pmcsn.Rngs;
import pmcsn.Util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static pmcsn.Configuration.*;

//TODO: creare utilizzazione sul simulatore2_transiente e simulatore2_batch

public class StartBatch {

    public static void main(String[] args) {

        Rngs r = new Rngs();

        Path path = Paths.get("../PMCSN_cloud/Matlab/Batch");
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            System.out.print("C'è stato un errore durante la creazione della cartella\n");
            System.exit(1);
        }

        while (true) {

            int selected = 0;
            System.out.print("\n\t\t\tBenvenuto nella simulazione Batch.\nQuale dei due simulatori vuoi runnare? [1 or 2] \t (Inserire 0 per terminare): ");
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

                PrintWriter batchWriter = null;
                try {
                    batchWriter = new PrintWriter(new FileWriter("Matlab/Batch/" + "batchFile" + seed + "_Alg" + selected + ".csv"));
                    Util.print_on_file(batchWriter, new String[]{"Batch", "cloudlet", "cloudlet_task1", "cloudlet_task2",
                            "cloud", "cloud_task1", "cloud_task2",
                            "system", "system_task1", "system_task2"});
                } catch (IOException e) {
                    System.out.print("C'è stato un errore durante la creazione del file\n");
                    System.exit(1);
                }

                switch (selected) {
                    case 1: {
                        Simulator1_Batch s1Batch = new Simulator1_Batch();
                        s1Batch.RunBatch(r, STOP, batchWriter);

                        break;
                    }
                    case 2: {
                        Simulator2_Batch s2Batch = new Simulator2_Batch();
                        s2Batch.RunBatch(r, STOP, batchWriter);

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

