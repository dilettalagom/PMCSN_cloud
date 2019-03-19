package main;

import pmcsn.Rngs;
import static main.Configuration.*;
import java.util.ArrayList;

public class Start {

    public static void main(String[] args) {

        // per ogni seme dentro l'array seed lancia una simulazione
        // N.B. = funzionerà bene solo quando il metodo runSimulation restituira dei file e non stampe
        // ToDo: invece che far fare tutte le simulazioni ad un processo , si potrebbe far fare una simulazione per thread.

        int j = 0;
        while ( j < seeds.length ){
            Simulator s = new Simulator();

            // inizializzo struttura per contenere lo stato dei singoli server + l'arrivo
            ArrayList<EventNode> system_events = new ArrayList<>();
            for (int i = 0; i< SERVERS +1 ; i++){
                system_events.add(new EventNode(START,0));
            }

            SystemClock clock = new SystemClock(START, START);

            GlobalNode global_node = new GlobalNode(START, START, 0 ,0);

            Rngs r = new Rngs();
            r.plantSeeds(Integer.parseInt(seeds[j]));

            //lancio la simulazione
            s.RunSimulation(seeds[j], system_events, global_node, clock,r);

            j++;
        }

    }
}
