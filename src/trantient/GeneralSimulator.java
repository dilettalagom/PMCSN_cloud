package trantient;

import pmcsn.Rngs;
import pmcsn.Util;
import StruttureDiSistema.EventNode;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static trantient.Configuration.*;


public abstract class GeneralSimulator {


    public double exponential(double m, Rngs r) {
        /* ------------------------------
         * generate an Exponential random variate, use m > 0.0
         * ------------------------------
         */
        return (-m * Math.log(1.0 - r.random()));
    }

    public double hyperExponential(double mu, Rngs r) {
        double p = 0.2;
        double m1 = 2 * p * mu;
        double m2 = 2 * (1 - p) * mu;
        r.selectStream(10);
        double random = r.random();
        if (random < p) {
            r.selectStream(30);
            return exponential(1 / m1, r);
        } else {
            r.selectStream(60);
            return exponential(1 / m2, r);
        }
    }

    public int getType(Rngs r) {
        double pA = lambda1 / lambda;
        double random = r.random();
        if (random < pA) {
            return 1;
        } else return 2;
    }

    public double getArrival(double lambda, Rngs r) {
        /* ------------------------------
         * generate the next arrival time
         * ------------------------------
         */
        r.selectStream(0);
        return exponential(1.0 / lambda, r);
    }

    public double getServiceCloudlet(double mu, Rngs r) {
        /* ------------------------------
         * generate the next service time
         * ------------------------------
         */
        r.selectStream(1);
        return (hyperExponential(mu, r));
    }

    public double getServiceCloud(double mu, Rngs r) {
        /* ------------------------------
         * generate the next service time
         * ------------------------------
         */
        r.selectStream(2);
        return (exponential(mu, r));
    }

    public int nextEvent(ArrayList<EventNode> list_events) {
        int event;
        int i = 0;

        while (list_events.get(i).getType() == 0)       /* find the index of the first 'active' */
            i++;                                        /* element in the event list            */
        event = i;
        while (i < list_events.size() - 1) {             /* now, check the others to find which  */
            i++;                                         /* event type is most imminent          */
            if ((list_events.get(i).getType() > 0) &&
                    (list_events.get(i).getTemp() < list_events.get(event).getTemp()))
                event = i;
        }
        return (event);
    }


    public boolean check_system_servers(ArrayList<EventNode> system_events) {

        for (EventNode e : system_events) {
            if (e.getType() != 0){
                return false;
            }
        }
        return true;

    }

    public PrintWriter createFile(String filename, String algoritmo, String selected_seed){

        PrintWriter writer = null;
        try {
            switch (filename) {
                case "instantCompleteTime":
                    //Tempi di Completamento istantanei per : Cloudlet, Cloud, Sistema
                    writer = new PrintWriter(new FileWriter("Matlab/" + filename + algoritmo + selected_seed + ".csv"));
                    Util.print_on_file(writer, new String[]{"istante", "cloudlet", "cloud", "sistema"});
                    break;
                case "meanResponseTime":
                    //Tempi di Risposta medi per : Cloudlet, Cloud, Sistema
                    writer = new PrintWriter(new FileWriter("Matlab/" + filename + algoritmo + selected_seed + ".csv"));
                    Util.print_on_file(writer, new String[]{"seed", "cloudlet", "cloudlet_task1", "cloudlet_task2",
                                                                "cloud", "cloud_task1", "cloud_task2",
                                                                "system", "system_task1", "system_task2"});
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer;
    }

    public int findOneCloud(ArrayList<EventNode> system_events) {
        /* -----------------------------------------------------
         * return the index of the first available server
         * -----------------------------------------------------
         */
        // se non ci sono serventi liberi nel cloud, ne creo uno nuovo

        int i = SERVERS + 1;
        if (system_events.size() == SERVERS) {
            system_events.add(new EventNode());
            return i;

        } else {
            for (; i < system_events.size(); i++) {
                if (system_events.get(i).getType() == 0) {
                    return i;
                }
            }
            system_events.add(new EventNode());
            return i;
        }
    }

    public int findOneCloudlet(ArrayList<EventNode> listNode) {
        /* -----------------------------------------------------
         * return the index of the available server with longest idle period
         * -----------------------------------------------------
         */
        int server;
        int i = 1;

        while (listNode.get(i).getType() == 1)          /* find the index of the first available */
            i++;                                        /* (idle) server                         */
        server = i;
        while (i < SERVERS) {                           /* now, check the others to find which   */
            i++;                                        /* has been idle longest                 */
            if ((listNode.get(i).getType() == 0) &&
                    (listNode.get(i).getTemp() < listNode.get(server).getTemp()))
                server = i;
        }
        return (server);
    }

    public abstract ArrayList<String> RunSimulation(Rngs r, double STOP,String selected_seed, String algoritmo);
    public abstract void RunBatch(Rngs r, double STOP, PrintWriter batchWriter);


}
