package restructed;

import pmcsn.Rngs;
import restructed.StruttureDiSistema.EventNode;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static restructed.Configuration.*;


abstract class GeneralSimulator {


    private double exponential(double m, Rngs r) {
        /* ------------------------------
         * generate an Exponential random variate, use m > 0.0
         * ------------------------------
         */
        return (-m * Math.log(1.0 - r.random()));
    }

    private double hyperExponential(double mu, Rngs r) {
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

    int getType(Rngs r) {
        double pA = lambda1 / lambda;
        double random = r.random();
        if (random < pA) {
            return 1;
        } else return 2;
    }

    double getArrival(double lambda, Rngs r) {
        /* ------------------------------
         * generate the next arrival time
         * ------------------------------
         */
        r.selectStream(0);
        return exponential(1.0 / lambda, r);
    }

    double getServiceCloudlet(double mu, Rngs r) {
        /* ------------------------------
         * generate the next service time
         * ------------------------------
         */
        r.selectStream(1);
        return (hyperExponential(mu, r));
    }

    double getServiceCloud(double mu, Rngs r) {
        /* ------------------------------
         * generate the next service time
         * ------------------------------
         */
        r.selectStream(2);
        return (exponential(mu, r));
    }

    int nextEvent(ArrayList<EventNode> list_events) {
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

    void print_on_file(PrintWriter writer, String[] row) {

        for (String s : row) {
            writer.write(s);
            writer.write(';');
        }
        writer.write(System.getProperty("line.separator"));
    }

    boolean check_system_servers(ArrayList<EventNode> system_events) {

        for (EventNode e : system_events) {
            if (e.getType() != 0){
                return false;
            }
        }
        return true;

    }

    PrintWriter createFile(String filename, String algoritmo, String selected_seed){

        PrintWriter writer = null;
        try {
            switch (filename) {
                case "instant_writer":

                    writer = new PrintWriter(new FileWriter("temp/" + filename + algoritmo + selected_seed + ".csv"));
                    print_on_file(writer, new String[]{"istante", "cloudlet", "cloud", "sistema"});
                    break;
                case "mean_writer":
                    writer = new PrintWriter(new FileWriter("temp/" + filename + algoritmo + selected_seed + ".csv"));
                    //TODO: decidere quali valori vogliamo sul file delle statistiche medie
                    print_on_file(writer, new String[]{"seed", "n1_cloudlet", "n2_cloudlet", "n1_cloud", "n2_cloud"});
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer;
    }


}
