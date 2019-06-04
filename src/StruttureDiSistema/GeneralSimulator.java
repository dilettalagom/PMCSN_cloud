package StruttureDiSistema;

import pmcsn.Rngs;
import java.util.ArrayList;

import static pmcsn.Configuration.*;


public abstract class GeneralSimulator {

    public double exponential(double m, Rngs r) {
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

    public int getTaskType(Rngs r) {
        double p1 = lambda1 / lambda;
        double random = r.random();
        if (random <= p1) {
            //è stato generato un task1
            return 1;
        } else
            //è stato generato un task2
            return 2;
    }

    public double getArrival(double lambda, Rngs r) {
        r.selectStream(0);
        return exponential(1.0 / lambda, r);
    }

    public double getServiceCloudlet(double mu, Rngs r) {
        r.selectStream(1);
        return (hyperExponential(mu, r));
    }

    public double getServiceCloud(double mu, Rngs r) {
        r.selectStream(2);
        return (exponential(mu, r));
    }

    public int nextEvent(ArrayList<EventNode> list_events) {
        int min_event;
        int i = 0;

        while (list_events.get(i).getType() == 0)
            i++;
        min_event = i;
        while (i < list_events.size() - 1) {
            i++;
            if ((list_events.get(i).getType() > 0) &&
                    (list_events.get(i).getTemp() < list_events.get(min_event).getTemp()))
                min_event = i;
        }
        return (min_event);
    }


    public boolean check_system_servers(ArrayList<EventNode> system_events) {

        for (EventNode e : system_events) {
            if (e.getType() != 0){
                return false;
            }
        }
        return true;

    }

    public int findOneCloud(ArrayList<EventNode> system_events) {

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
    public abstract ArrayList<ArrayList<Double>> RunBatch(Rngs r, double STOP);


}
