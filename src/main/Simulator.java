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

    public double getArrival(double lambda, Rngs r) {
        /* --------------------------------------------------------------
         * generate the next arrival time
         * --------------------------------------------------------------
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

            global_node.setComplete_time_cloudlet(global_node.getComplete_time_cloudlet() + (clock.getNext() - clock.getCurrent()) * global_node.getWorking_server_cloudlet());
            global_node.setComplete_time_cloud(global_node.getComplete_time_cloud() + (clock.getNext() - clock.getCurrent()) * global_node.getWorking_server_cloud());

            global_node.setComplete_time_task1(global_node.getComplete_time_task1() + (clock.getNext() - clock.getCurrent()) * global_node.getWorking_server_task1() );
            global_node.setComplete_time_task2(global_node.getComplete_time_task2() + (clock.getNext() - clock.getCurrent()) * global_node.getWorking_server_task2() );

            global_node.setComplete_time_system(global_node.getComplete_time_system() + (clock.getNext() - clock.getCurrent()) * ( global_node.getWorking_server_cloudlet()));


            global_node.setComplete_time_system(global_node.getComplete_time_system() + (clock.getNext() - clock.getCurrent()) * ( global_node.getWorking_server_cloud() +global_node.getWorking_server_cloudlet()));


            print_on_file(globalNode_writer, new String[]{String.valueOf(clock.getCurrent()), //istante
                    String.valueOf(global_node.getComplete_time_cloudlet()/(global_node.getCloudlet_number1()+global_node.getCloudlet_number2())),                                //area_cloudlet
                    String.valueOf(global_node.getComplete_time_cloud()/(global_node.getCloud_number1()+global_node.getCloud_number2()))});                                 //area_cloud

            clock.setCurrent(clock.getNext());                              /* advance the clock*/

            if (e == 0) { // processo un arrivo


                if (system_events.get(e).getType() == 1) {
                    global_node.setWorking_server_task1(global_node.getWorking_server_task1() + 1);
                } else if(system_events.get(e).getType() == 2) {
                    global_node.setWorking_server_task2(global_node.getWorking_server_task2() + 1);
                }

                int type = system_events.get(e).getType();

                system_events.get(0).setTemp(this.getArrival(lambda, r) + clock.getCurrent());
                system_events.get(0).setType(this.getType(r));

                // termino esecuzione al prossimo while
                if (system_events.get(0).getTemp() > STOP)
                    system_events.get(0).setType(0);

                // se ho server disponibili assegno il task
                if (global_node.getWorking_server_cloudlet() < SERVERS) { // ho dei server liberi -> ( arrivo cloudlet )

                    global_node.setWorking_server_cloudlet(global_node.getWorking_server_cloudlet() + 1);

                    // genero il tempo di servizio
                    double service = 0;
                    if (type == 1) {
                        global_node.setCloudlet_number1(global_node.getCloudlet_number1() + 1);
                        service = this.getServiceCloudlet(mu1_cloudlet, r);

                    } else if (type == 2) {
                        global_node.setCloudlet_number2(global_node.getCloudlet_number2() + 1);
                        service = this.getServiceCloudlet(mu2_cloudlet, r);
                    }

                    //trovo il server libero da più tempo inattivo
                    int cloudlet_server_selected = this.findOneCloudlet(system_events);

                    // aggiorno il server i-esimo ( indice ) con i nuovi valori di tempo e type
                    system_events.get(cloudlet_server_selected).setTemp(clock.getCurrent() + service);
                    system_events.get(cloudlet_server_selected).setType(type);


                } else { // non ho server liberi -> mando al cloud  ( arrivo cloud)

                    global_node.setWorking_server_cloud(global_node.getWorking_server_cloud() + 1);

                    //trovo il server libero ( se non esiste lo creo )
                    int cloud_server_selected = findOneCloud(system_events);

                    int typeCloud = system_events.get(0).getType();

                    double service = 0;
                    if (system_events.get(e).getType() == 1) {
                        global_node.setCloud_number1(global_node.getCloud_number1() + 1);

                        // genero un servizio secondo la distribuzione del tempo di servizio per  task A
                        service = this.getServiceCloud(mu1_cloud, r);
                    } else {
                        global_node.setCloud_number2(global_node.getCloud_number2() + 1);

                        // genero un servizio secondo la distribuzione del tempo di servizio per  task B
                        service = this.getServiceCloud(mu2_cloud, r);
                    }

                    system_events.get(cloud_server_selected).setTemp(clock.getCurrent() + service);
                    system_events.get(cloud_server_selected).setType(typeCloud);

                }


            } else { // processo una partenza

                if (system_events.get(e).getType() == 1) {
                    global_node.setWorking_server_task1(global_node.getWorking_server_task1() - 1);
                } else if(system_events.get(e).getType() == 2) {
                    global_node.setWorking_server_task2(global_node.getWorking_server_task2() - 1);
                }

                if (e <= SERVERS) { // processo una partenza cloudlet

                    global_node.setWorking_server_cloudlet(global_node.getWorking_server_cloudlet() - 1);
                    system_events.get(e).setType(0);


                } else { //processo una partenza del cloud

                    global_node.setWorking_server_cloud(global_node.getWorking_server_cloud() - 1);
                    system_events.get(e).setType(0);
                }



            }
        }

        /*TEST:
        for (EventNode i : system_events)
            System.err.println(i.toString());*/


        System.err.println("Risultati prodotti dal seed: "+seed);

        System.err.println("n1_cloudlet: "+ global_node.getCloudlet_number1()
                + "\t\tn2_cloudlet: " + global_node.getCloudlet_number2() + "\n"
                + "n1_cloud: " + global_node.getCloud_number1()
                + "\t\tn2_cloud "+ global_node.getCloud_number2());


        double totalTask = global_node.getCloudlet_number1() + global_node.getCloudlet_number2() + global_node.getCloud_number1() + global_node.getCloud_number2();

        double lambdaToT = 1.0 / (clock.getCurrent() / totalTask);
        double lambdaA = 1.0 / (clock.getCurrent() / (global_node.getCloudlet_number1() + global_node.getCloud_number1()));
        double lambdaB = 1.0 / (clock.getCurrent() / (global_node.getCloudlet_number2() + global_node.getCloud_number2()));

        double pq = (global_node.getCloud_number1() + global_node.getCloud_number2()) / totalTask;

        System.err.println(" lambda stimato = " + lambdaToT);
        System.err.println(" lambda task A stimato = " + lambdaA);
        System.err.println(" lambda task B stimato = " + lambdaB);

        //System.err.println( "area totale, area task1 = " + global_node.getComplete_time_system()+","+ global_node.getComplete_time_task1() );

        System.err.println(" numero medio di task del cloudlet " + global_node.getComplete_time_cloudlet() / clock.getCurrent());
        System.err.println(" numero medio di task del cloud " + global_node.getComplete_time_cloud() / clock.getCurrent());

        System.err.println(" tempo medio di risposta del cloudlet " + global_node.getComplete_time_cloudlet() / (global_node.getCloudlet_number1() + global_node.getCloudlet_number2()));
        System.err.println(" tempo medio di risposta del cloud " + global_node.getComplete_time_cloud() / (global_node.getCloud_number1() + global_node.getCloud_number2()));

        System.err.println(" tempo medio di risposta del sistema " + global_node.getComplete_time_system() / (global_node.getCloud_number1() + global_node.getCloud_number2() + global_node.getCloudlet_number1()+ global_node.getCloudlet_number2()));

        System.err.println("tempo di risposta sistema per classe 1= " + global_node.getComplete_time_task1() / (global_node.getCloudlet_number1()+global_node.getCloud_number1()));
        System.err.println("tempo di risposta sistema per classe 2= " + global_node.getComplete_time_task2() / (global_node.getCloudlet_number2()+global_node.getCloud_number2()));

        System.err.println("Throughtput A per il cloudlet= " +  (lambdaA*(1-pq)));
        System.err.println("Throughtput B per il cloudlet= " +  (lambdaB*(1-pq)));

        System.err.println("= " +  (lambdaA*(1-pq)));


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
            print_on_file(printWriter, new String[]{"istante","cloudlet","cloud"});
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



