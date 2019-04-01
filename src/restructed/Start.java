package restructed;
import pmcsn.Rngs;

import java.io.InputStreamReader;
import java.util.Scanner;

import static restructed.Configuration.*;



public class Start {

    public static void main(String[] args) {

        while (true){

            for (String selected_seed : seeds) {

                Rngs r = new Rngs();
                r.plantSeeds(Integer.parseInt(selected_seed));

                System.out.print("Quale dei due simulatori vuoi runnare? [1 or 2] \t (Inserire 0 per terminare): ");
                Scanner reader = new Scanner(new InputStreamReader(System.in));

                try {
                    switch (reader.nextInt()) {
                        case 1: {
                            Simulator_alg1 s_algorith1 = new Simulator_alg1();
                            if (!s_algorith1.RunSimulation(r, selected_seed, "Alg1_"))
                                System.out.print("error");
                            break;
                        }
                        case 2: {
                            Simulator_alg2 s_algorith2 = new Simulator_alg2();
                            if (!s_algorith2.RunSimulation(r, selected_seed, "Alg2_"))
                                System.out.print("error");
                            break;
                        }
                        case 0: {
                            System.exit(1);
                            break;
                        }
                        default:
                            System.out.print("Inserire un valore significativo!\n\n ");
                            break;

                    }
                }catch (Exception e){
                    System.out.print("Inserire un valore significativo!\n\n");
                }

            }

        }
    }
}
