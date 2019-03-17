package main;

import pmcsn.Rngs;

import java.util.ArrayList;

public class main {

    public static void main(String[] args) {

        // per ogni seme dentro l'array seed lancia una simulazione
        // N.B. = funzionerà bene solo quando il metodo runSimulation restituira dei file e non stampe
        // ToDo: invece che far fare tutte le simulazioni ad un processo , si potrebbe far fare una simulazione per thread.

        int j = 0;
        while ( j < configuration.seeds.length ){
            Simulator s = new Simulator();

            // inizializzo struttura per contenere lo stato dei singoli server + l'arrivo
            ArrayList<EventNode> system_events = new ArrayList<>();

            for ( int i = 0 ; i< configuration.SERVERS +1 ; i++){
                system_events.add(new EventNode(configuration.START,0));
            }
            SystemClock clock = new SystemClock(configuration.START,configuration.START);
            Rngs r = new Rngs();
            r.plantSeeds(Integer.parseInt(configuration.seeds[j]));
            s.RunSimulation(system_events,clock,r);
        }

    }
}
