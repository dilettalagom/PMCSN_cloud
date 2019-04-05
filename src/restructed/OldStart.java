package restructed;
import pmcsn.Rngs;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import static restructed.Configuration.*;



public class OldStart {

    public static void main(String[] args) {

        Rngs r = new Rngs();
        r.plantSeeds(Integer.parseInt(seed));

        while (true){

            int selected = 0;
            System.out.print("Quale dei due simulatori vuoi runnare? [1 or 2] \t (Inserire 0 per terminare): ");
            Scanner reader = new Scanner(new InputStreamReader(System.in));
            try {
               selected = reader.nextInt();
            }catch (Exception e){
                System.out.print("Inserire un valore significativo!\n\n");
            }



            //TODO: inserire il put_seed() + test di chiquadro
            switch (selected) {
                case 1: {
                    Simulator_alg1 s_algorith1 = new Simulator_alg1();
                    ArrayList<String> values = s_algorith1.RunSimulation(r, STOP_Stazionario ,seed, "Alg1_");
                    break;
                }
                case 2: {
                    Simulator_alg2 s_algorith2 = new Simulator_alg2();
                    ArrayList<String> values = s_algorith2.RunSimulation(r, STOP_Stazionario ,seed, "Alg1_");
                    break;
                }
                case 0: {
                    System.exit(0);
                    break;
                }
                default:
                    System.out.print("Inserire un valore significativo!\n\n ");
                    break;

            }
        }


    }
}
