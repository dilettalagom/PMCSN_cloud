package restructed;
import pmcsn.Rngs;

import java.util.ArrayList;
import java.util.Scanner;

import static restructed.Configuration.*;



public class Start {

    public static void main(String[] args) {


        for (String selected_seed : seeds) {

            Rngs r = new Rngs();
            r.plantSeeds(Integer.parseInt(selected_seed));


            Simulator_alg1 s_algorith1 = new Simulator_alg1();
            if (!s_algorith1.runSimulator(r,selected_seed ))
                System.out.print("error");
            // s_algorith1.RunSimulation(selected_seed);


        }
    }
}
