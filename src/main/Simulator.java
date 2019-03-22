package main;

import pmcsn.Rngs;

import java.io.*;
import java.util.ArrayList;
import static main.Configuration.*;


public class Simulator {

    public double exponential(double m, Rngs r) {
        /* ---------------------------------------------------
         * generate an Exponential random variate, use m > 0.0
         * ---------------------------------------------------
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

    private double getArrival(double lambda, Rngs r) {
        /* --------------------------------------------------------------
         * generate the next arrival time
         * --------------------------------------------------------------
         */
        r.selectStream(0);
        return exponential(1.0 / lambda, r);
    }

    private double getServiceCloudlet(double mu, Rngs r) {
        /* ------------------------------
         * generate the next service time
         * ------------------------------
         */
        r.selectStream(1);
        return (hyperExponential(mu, r));
    }

    private double getServiceCloud(double mu, Rngs r) {
        /* ------------------------------
         * generate the next service time
         * ------------------------------
         */
        r.selectStream(2);
        return (exponential(mu, r));
    }

    public void RunSimulation(String seed, ArrayList<EventNode> system_events, GlobalNode global_node, SystemClock clock, Rngs r) {

        //create file first time
        PrintWriter globalNode_writer = createNewResultFile("globalNode_results" ,seed);

        // primo arrivo
        system_events.get(0).setTemp(this.getArrival(lambda, r) + clock.getCurrent());
        system_events.get(0).setType(this.getType(r));          // devo decidere se il primo arrivo è di tipo A o B

        // type = 0 -> simulazione terminata
        while (system_events.get(0).getType() != 0) {

            int e = this.nextEvent(system_events);

            clock.setNext(system_events.get(e).getTemp());
            double istant =clock.getNext() - clock.getCurrent();

            // calcola il tempo istantaneo di attraversamento del cloudlet
            global_node.setComplete_time_cloudlet(global_node.getComplete_time_cloudlet() + istant * (global_node.getWorking_cloudlet_taskA()+global_node.getWorking_cloudlet_taskB()));
            // calcola il tempo istantaneo di attraversamento del cloud
            global_node.setComplete_time_cloud(global_node.getComplete_time_cloud() + istant *  (global_node.getWorking_cloud_taskA()+global_node.getWorking_cloud_taskB()));
            // calcola il tempo istantaneo di attraversamento del sistema
            global_node.setComplete_time_system(global_node.getComplete_time_system() + istant * ( global_node.getWorking_cloudlet_taskA()+ global_node.getWorking_cloudlet_taskB()+ global_node.getWorking_cloud_taskA()+ global_node.getWorking_cloud_taskA()));

            //calcola il tempo di attraversamento nel sistema per un task di tipo A
            global_node.setComplete_time_taskA(global_node.getComplete_time_taskA() + istant * ( global_node.getWorking_cloudlet_taskA() +  global_node.getWorking_cloud_taskA()));
            //calcola il tempo di attraversamento nel sistema per un task di tipo B
            global_node.setComplete_time_taskB(global_node.getComplete_time_taskB() + istant * ( global_node.getWorking_cloudlet_taskB() +  global_node.getWorking_cloud_taskB()));

            //calcola il tempo di attraversamento nel cloudlet per un task di tipo A
            global_node.setArea_cloudlet_taskA(global_node.getArea_cloudlet_taskA() + istant *  global_node.getWorking_cloudlet_taskA() );
            //calcola il tempo di attraversamento nel cloudlet per un task di tipo B
            global_node.setArea_cloudlet_taskB(global_node.getComplete_time_taskB() + istant *  global_node.getWorking_cloudlet_taskB() );

            //calcola il tempo di attraversamento nel cloud per un task di tipo A
            global_node.setArea_cloud_taskA(global_node.getArea_cloud_taskA() + istant * global_node.getWorking_cloud_taskA());
            //calcola il tempo di attraversamento nel cloud per un task di tipo B
            global_node.setArea_cloud_taskB(global_node.getArea_cloud_taskB() + istant * global_node.getWorking_cloud_taskB());


            /*

            global_node.setComplete_time_taskA(global_node.getComplete_time_taskA() + (clock.getNext() - clock.getCurrent()) * global_node.getWorking_server_taskA() );
            global_node.setComplete_time_taskB(global_node.getComplete_time_taskB() + (clock.getNext() - clock.getCurrent()) * global_node.getWorking_server_taskB() );
*/


            print_on_file(globalNode_writer, new String[]{String.valueOf(clock.getCurrent() ),
                    String.valueOf(global_node.getComplete_time_cloudlet()  / (global_node.getProcessed_cloudlet_taskA() + global_node.getProcessed_cloudlet_taskB() ) ),
                    String.valueOf(global_node.getComplete_time_cloud()     / ( global_node.getProcessed_cloud_taskA() + global_node.getProcessed_cloud_taskB() )),
                    String.valueOf(global_node.getComplete_time_system()    / ( global_node.getProcessed_cloudlet_taskA() + global_node.getProcessed_cloudlet_taskB() +global_node.getProcessed_cloud_taskA() + global_node.getProcessed_cloud_taskB() ))});


            clock.setCurrent(clock.getNext());

            if (e == 0) { // processo un arrivo


                int type = system_events.get(e).getType();

                system_events.get(0).setTemp(this.getArrival(lambda, r) + clock.getCurrent());
                system_events.get(0).setType(this.getType(r));

                // termino esecuzione al prossimo while
                if (system_events.get(0).getTemp() > STOP)
                    system_events.get(0).setType(0);

                // se ho server disponibili assegno il task
                if (global_node.getWorking_cloudlet_taskA()+global_node.getWorking_cloudlet_taskB() < SERVERS) { // ho dei server liberi -> ( arrivo cloudlet )

                    // genero il tempo di servizio
                    double service = 0;
                    if (type == 1) {
                        global_node.setWorking_cloudlet_taskA(global_node.getWorking_cloudlet_taskA() + 1);

                        global_node.setProcessed_cloudlet_taskA(global_node.getProcessed_cloudlet_taskA() + 1);
                        service = this.getServiceCloudlet(mu1_cloudlet, r);

                    } else if (type == 2) {
                        global_node.setWorking_cloudlet_taskB(global_node.getWorking_cloudlet_taskB() + 1);

                        global_node.setProcessed_cloudlet_taskB(global_node.getProcessed_cloudlet_taskB() + 1);
                        service = this.getServiceCloudlet(mu2_cloudlet, r);
                    }

                    //trovo il server libero da più tempo inattivo
                    int cloudlet_server_selected = this.findOneCloudlet(system_events);

                    // aggiorno il server i-esimo ( indice ) con i nuovi valori di tempo e type
                    system_events.get(cloudlet_server_selected).setTemp(clock.getCurrent() + service);
                    system_events.get(cloudlet_server_selected).setType(type);


                } else { // non ho server liberi -> mando al cloud  ( arrivo cloud)

                    /* ToDO: se il job che mi è arrivato è di classe 1 (per l'algoritmo2
                    *       se ci sono job di classe 2 in esecuzione nel cloudlet
                    *       -> prendo uno di classe 2 e lo sposto nel cloud
                    *       -> genero un nuovo tempo di servizio
                    *       -> assegno il job di classe 1 al server
                    *
                    *       */

                    //trovo il server libero ( se non esiste lo creo )
                    int cloud_server_selected = findOneCloud(system_events);

                    int typeCloud = system_events.get(0).getType();

                    double service = 0;
                    if (system_events.get(e).getType() == 1) {
                        global_node.setWorking_cloud_taskA(global_node.getWorking_cloud_taskA() + 1);

                        global_node.setProcessed_cloud_taskA(global_node.getProcessed_cloud_taskA() + 1);

                        // genero un servizio secondo la distribuzione del tempo di servizio per  task A
                        service = this.getServiceCloud(mu1_cloud, r);
                    } else {
                        global_node.setWorking_cloud_taskB(global_node.getWorking_cloud_taskB() + 1);

                        global_node.setProcessed_cloud_taskB(global_node.getProcessed_cloud_taskB() + 1);

                        // genero un servizio secondo la distribuzione del tempo di servizio per  task B
                        service = this.getServiceCloud(mu2_cloud, r);
                    }

                    system_events.get(cloud_server_selected).setTemp(clock.getCurrent() + service);
                    system_events.get(cloud_server_selected).setType(typeCloud);

                }


            } else { // processo una partenza


                if (e <= SERVERS) { // processo una partenza cloudlet

                    if (system_events.get(e).getType() == 1) {
                        global_node.setWorking_cloudlet_taskA(global_node.getWorking_cloudlet_taskA() - 1);
                    } else if(system_events.get(e).getType() == 2) {
                        global_node.setWorking_cloudlet_taskB(global_node.getWorking_cloudlet_taskB() - 1);
                    }
                    system_events.get(e).setType(0);


                } else { //processo una partenza del cloud

                    if (system_events.get(e).getType() == 1) {
                        global_node.setWorking_cloud_taskA(global_node.getWorking_cloud_taskA() - 1);
                    } else if(system_events.get(e).getType() == 2) {
                        global_node.setWorking_cloud_taskB(global_node.getWorking_cloud_taskB() - 1);
                    }
                    system_events.get(e).setType(0);
                }



            }
        }

        /*TEST:
        for (EventNode i : system_events)
            System.err.println(i.toString());*/


        System.err.println("Risultati prodotti dal seed: "+seed);

        System.err.println("n1_cloudlet: "+ global_node.getProcessed_cloudlet_taskA()
                + "\t\tn2_cloudlet: " + global_node.getProcessed_cloudlet_taskB() + "\n"
                + "n1_cloud: " + global_node.getProcessed_cloud_taskA()
                + "\t\tn2_cloud "+ global_node.getProcessed_cloud_taskB());


        double totalTask = global_node.getProcessed_cloudlet_taskA() + global_node.getProcessed_cloudlet_taskB() + global_node.getProcessed_cloud_taskA() + global_node.getProcessed_cloud_taskB();

        double lambdaToT = 1.0 / (clock.getCurrent() / totalTask);
        double lambdaA = 1.0 / (clock.getCurrent() / (global_node.getProcessed_cloudlet_taskA() + global_node.getProcessed_cloud_taskA()));
        double lambdaB = 1.0 / (clock.getCurrent() / (global_node.getProcessed_cloudlet_taskB() + global_node.getProcessed_cloud_taskB()));

        double pq = (global_node.getProcessed_cloud_taskA() + global_node.getProcessed_cloud_taskB()) / totalTask;

        System.err.println("lambda stimato " + lambdaToT);
        System.err.println("lambda task A stimato " + lambdaA);
        System.err.println("lambda task B stimato " + lambdaB + "\n");

        //System.err.println( "area totale, area task1 = " + global_node.getComplete_time_system()+","+ global_node.getComplete_time_taskA() );

        System.err.println("numero medio di task del cloudlet " + global_node.getComplete_time_cloudlet() / clock.getCurrent());
        System.err.println("numero medio di taskA del cloudlet " + global_node.getArea_cloudlet_taskA() / clock.getCurrent());
        System.err.println("numero medio di taskB del cloudlet " + global_node.getArea_cloudlet_taskB() / clock.getCurrent()+"\n");


        System.err.println("numero medio di task del cloud " + global_node.getComplete_time_cloud() / clock.getCurrent());
        System.err.println("numero medio di taskA del cloud " + global_node.getArea_cloud_taskA() / clock.getCurrent());
        System.err.println("numero medio di taskB del cloud " + global_node.getArea_cloud_taskB() / clock.getCurrent()+"\n");


        System.err.println("tempo di risposta del cloudlet " + global_node.getComplete_time_cloudlet() / (global_node.getProcessed_cloudlet_taskA() + global_node.getProcessed_cloudlet_taskB()));
        System.err.println("tempo di risposta del cloudlet per taskA " + global_node.getArea_cloudlet_taskA() / global_node.getProcessed_cloudlet_taskA() );
        System.err.println("tempo di risposta del cloudlet per taskB " + global_node.getArea_cloudlet_taskB() / global_node.getProcessed_cloudlet_taskB() +"\n");

        System.err.println("tempo di risposta del cloud " + global_node.getComplete_time_cloud() / (global_node.getProcessed_cloud_taskA() + global_node.getProcessed_cloud_taskB()));
        System.err.println("tempo di risposta del cloud per taskA " + global_node.getArea_cloud_taskA() / global_node.getProcessed_cloud_taskA() );
        System.err.println("tempo di risposta del cloud per taskB " + global_node.getArea_cloud_taskB() / global_node.getProcessed_cloud_taskB() +"\n");

        System.err.println("tempo medio di risposta del sistema " + global_node.getComplete_time_system() / (global_node.getProcessed_cloud_taskA() + global_node.getProcessed_cloud_taskB() + global_node.getProcessed_cloudlet_taskA()+ global_node.getProcessed_cloudlet_taskB()));
        System.err.println("tempo di risposta sistema per taskA " + global_node.getComplete_time_taskA() / (global_node.getProcessed_cloudlet_taskA()+global_node.getProcessed_cloud_taskA()));
        System.err.println("tempo di risposta sistema per taskB " + global_node.getComplete_time_taskB() / (global_node.getProcessed_cloudlet_taskB()+global_node.getProcessed_cloud_taskB()) +"\n");

        System.err.println("Throughtput A per il cloudlet " +  (lambdaA*(1-pq)));
        System.err.println("Throughtput B per il cloudlet " +  (lambdaB*(1-pq)));

        System.err.println("Throughtput A per il cloud " +  (lambdaA*(pq)));
        System.err.println("Throughtput B per il cloud " +  (lambdaB*(pq))+"\n");

        System.err.println(" pq " + pq);

        System.err.println("------------------------------------------------------------");

        assert globalNode_writer != null;
        globalNode_writer.close();

    }




    private int nextEvent(ArrayList<EventNode> list_events) {
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

    private int findOneCloud(ArrayList<EventNode> system_events) {
        /* -----------------------------------------------------
         * return the index of the first available server
         * -----------------------------------------------------
         */
        // se non ci sono serventi liberi nel cloud, ne creo 1

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

    private int findOneCloudlet(ArrayList<EventNode> listNode) {
        /* -----------------------------------------------------
         * return the index of the available server idle longest
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

    private int getType(Rngs r) {
        double pA = lambda1 / lambda;
        double random = r.random();
        if (random < pA) {
            return 1;
        } else return 2;
    }

    private PrintWriter createNewResultFile (String filename, String seed){
        PrintWriter printWriter = null;

        try {
            printWriter = new PrintWriter(new FileWriter("./temp/"+ filename + seed + ".csv"));
            print_on_file(printWriter, new String[]{"istante","cloudlet","cloud","sistema"});
        } catch (IOException e) {
            e.printStackTrace();
        }

        return printWriter;
    }

    private void print_on_file(PrintWriter writer, String[] row) {

        for (String s : row) {
            writer.write(s);
            writer.write(';');
        }
        writer.write(System.getProperty("line.separator"));
    }
}



