package Transient;

import pmcsn.Rngs;
import StruttureDiSistema.*;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static pmcsn.Configuration.*;

public class Simulator2_Tran extends GeneralSimulator {

    private ArrayList<EventNode> system_events;
    private SystemClock clock;
    private GlobalNode global_node;
    private Cloudlet cloudlet;
    private Cloud cloud;
    //private ArrayList<Server> clet_servers;


    //init delle strutture caratteristiche del simulatore
    Simulator2_Tran() {

        this.system_events = new ArrayList<>();
        for (int i = 0; i < SERVERS + 1; i++) {
            system_events.add(new EventNode(START, 0));
        }
        this.clock = new SystemClock(START, START);
        this.global_node = new GlobalNode(START, START);

        this.cloudlet = new Cloudlet();
        this.cloudlet.setServers(new ArrayList<>());
        for (int i = 0; i < SERVERS + 1; i++) {
            this.cloudlet.getServers().add(new Server());
        }

        this.cloud = new Cloud();

    }

    @Override
    public ArrayList<String> RunSimulation(Rngs r, double STOP,String selected_seed, String algoritmo) {

        //PrintWriter instant_writer = createFile("instantCompleteTime", algoritmo, selected_seed);

        // primo arrivo
        system_events.get(0).setTemp(getArrival(lambda, r) + clock.getCurrent());
        system_events.get(0).setType(getTaskType(r));          // devo decidere se il primo arrivo è di tipo A o B

        while (system_events.get(0).getType() != 0) {

            if (system_events.get(0).getTemp() > STOP) {
                if (check_system_servers(this.system_events)) {
                    break;
                }
            }

            int e = nextEvent(system_events);

            clock.setNext(system_events.get(e).getTemp());
            double instant = clock.getNext() - clock.getCurrent();

            // calcola il tempo istantaneo di attraversamento del cloudlet
            global_node.setComplete_time_cloudlet(global_node.getComplete_time_cloudlet() + instant * (cloudlet.getWorking_task1() + cloudlet.getWorking_task2()));
            // calcola il tempo istantaneo di attraversamento del cloud
            global_node.setComplete_time_cloud(global_node.getComplete_time_cloud() + instant * (cloud.getWorking_task1() + cloud.getWorking_task2()));
            // calcola il tempo istantaneo di attraversamento del sistema
            global_node.setComplete_time_system(global_node.getComplete_time_system() + instant *
                    (cloudlet.getWorking_task1() + cloudlet.getWorking_task2() + cloud.getWorking_task1() + cloud.getWorking_task2()));

            //calcola il tempo di attraversamento nel sistema per un task di tipo 1
            global_node.setComplete_time_task1(global_node.getComplete_time_task1() + instant * (cloudlet.getWorking_task1() + cloud.getWorking_task1()));
            //calcola il tempo di attraversamento nel sistema per un task di tipo 2
            global_node.setComplete_time_task2(global_node.getComplete_time_task2() + instant * (cloudlet.getWorking_task2() + cloud.getWorking_task2()));

            //calcola il tempo di attraversamento nel cloudlet per un task di tipo 1
            cloudlet.setArea_task1(cloudlet.getArea_task1() + instant * cloudlet.getWorking_task1());
            //calcola il tempo di attraversamento nel cloudlet per un task di tipo 2
            cloudlet.setArea_task2(cloudlet.getArea_task2() + instant * cloudlet.getWorking_task2());

            //calcola il tempo di attraversamento nel cloud per un task di tipo 1
            cloud.setArea_task1(cloud.getArea_task1() + instant * cloud.getWorking_task1());
            //calcola il tempo di attraversamento nel cloud per un task di tipo 2
            cloud.setArea_task2(cloud.getArea_task2() + instant * cloud.getWorking_task2());


           /* Util.print_on_file(instant_writer, new String[]{String.valueOf(clock.getCurrent()),
                    String.valueOf(global_node.getComplete_time_cloudlet() / (cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2())),
                    String.valueOf(global_node.getComplete_time_cloud() / (cloud.getProcessed_task1() + cloud.getProcessed_task2())),
                    String.valueOf(global_node.getComplete_time_system() / (cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2() + cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2()))});

            */

            clock.setCurrent(clock.getNext());

            if (e == 0) { // processo un arrivo

                if (cloudlet.getWorking_task1() + cloudlet.getWorking_task2() < SERVERS) {

                    //trovo il server libero da più tempo inattivo
                    int cloudlet_server_selected = findOneCloudlet(system_events);

                    double service = 0;
                    if (system_events.get(e).getType() == 1) {
                        cloudlet.setWorking_task1(cloudlet.getWorking_task1() + 1);
                        service = getServiceCloudlet(mu1_cloudlet, r);

                    } else {
                        cloudlet.setWorking_task2(cloudlet.getWorking_task2() + 1);
                        service = getServiceCloudlet(mu2_cloudlet, r);
                    }


                    double temp = cloudlet.getServers().get(cloudlet_server_selected).getTotal_service() + service;
                    cloudlet.getServers().get(cloudlet_server_selected).setTotal_service(temp);
                    //System.out.println(temp);

                    // salvo il tempo di servizio generato per il task in esecuzione sul server
                    cloudlet.getServers().get(cloudlet_server_selected).setLast_service(clock.getCurrent() + service);
                    // aggiorno il server i-esimo ( indice ) con i nuovi valori di tempo e type
                    system_events.get(cloudlet_server_selected).setTemp(clock.getCurrent() + service);
                    system_events.get(cloudlet_server_selected).setType(system_events.get(e).getType());



                } else if (system_events.get(e).getType() == 1 && cloudlet.getWorking_task2() > 0) {
                    // caso in cui mi arriva un task di tipo 1 ed ho dei task di tipo 2 nel cloudelt -> cambio
                    int cloud_server_selected = findOneCloud(system_events);
                    int switched_server = findType2ToSwitch(system_events);


                    system_events.get(cloud_server_selected).setTemp((clock.getCurrent() + getServiceCloud(mu2_cloud, r)));
                    system_events.get(cloud_server_selected).setType(system_events.get(switched_server).getType());


                    cloud.setWorking_task2(cloud.getWorking_task2() + 1);
                    cloudlet.setWorking_task2(cloudlet.getWorking_task2() - 1);


                    //scambio
                    double service = getServiceCloudlet(mu1_cloudlet, r);
                    system_events.get(switched_server).setTemp( service + clock.getCurrent());
                    system_events.get(switched_server).setType(system_events.get(e).getType());

                    double temp =  cloudlet.getServers().get(switched_server).getLast_service() - clock.getCurrent() ;

                    //System.out.println(temp);
                    cloudlet.getServers().get(switched_server).setTotal_service(cloudlet.getServers().get(switched_server).getTotal_service()
                           -temp + service);


                    cloudlet.setWorking_task1(cloudlet.getWorking_task1() + 1);


                } else {
                    int cloud_server_selected = findOneCloud(system_events);
                    int typeCloud = system_events.get(e).getType();


                    double service = 0;
                    if (system_events.get(e).getType() == 1) {
                        cloud.setWorking_task1(cloud.getWorking_task1() + 1);

                        // genero un servizio secondo la distribuzione del tempo di servizio per  task A
                        service = this.getServiceCloud(mu1_cloud, r);
                    } else {
                        cloud.setWorking_task2(cloud.getWorking_task2() + 1);

                        // genero un servizio secondo la distribuzione del tempo di servizio per  task B
                        service = this.getServiceCloud(mu2_cloud, r);
                    }

                    system_events.get(cloud_server_selected).setTemp(clock.getCurrent() + service);
                    system_events.get(cloud_server_selected).setType(typeCloud);

                }
                if (system_events.get(0).getTemp() <= STOP) {
                    system_events.get(0).setTemp(getArrival(lambda, r) + clock.getCurrent());
                    system_events.get(0).setType(getTaskType(r));
                }
                else {
                    system_events.get(0).setType(0);
                }
            } else { //partenze

                if (e <= SERVERS) { // processo una partenza cloudlet

                    if (system_events.get(e).getType() == 1) {
                        cloudlet.setWorking_task1(cloudlet.getWorking_task1() - 1);
                        cloudlet.setProcessed_task1(cloudlet.getProcessed_task1() + 1);

                        cloudlet.getServers().get(e).setProcessed_task1(cloudlet.getServers().get(e).getProcessed_task1() + 1 );


                    } else if (system_events.get(e).getType() == 2) {
                        cloudlet.setWorking_task2(cloudlet.getWorking_task2() - 1);
                        cloudlet.setProcessed_task2(cloudlet.getProcessed_task2() + 1);

                        cloudlet.getServers().get(e).setProcessed_task2(cloudlet.getServers().get(e).getProcessed_task2() + 1 );
                    }

                    system_events.get(e).setType(0);


                } else { //processo una partenza del cloud

                    if (system_events.get(e).getType() == 1) {
                        cloud.setWorking_task1(cloud.getWorking_task1() - 1);
                        cloud.setProcessed_task1(cloud.getProcessed_task1() + 1);

                    } else if (system_events.get(e).getType() == 2) {
                        cloud.setWorking_task2(cloud.getWorking_task2() - 1);
                        cloud.setProcessed_task2(cloud.getProcessed_task2() + 1);
                    }
                    system_events.get(e).setType(0);
                }

            }

        }


        DecimalFormat f = new DecimalFormat("###0.000000");
        f.setGroupingUsed(false);
        f.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));

        System.out.println("\n\n------------------------Risultati prodotti dal seed: " + selected_seed+ " ------------------------\n");
        ArrayList<String> allResults = new ArrayList<>(Arrays.asList(selected_seed, Double.toString(STOP)));

        System.out.println("n1_cloudlet: " + cloudlet.getProcessed_task1()
                + "\t\tn2_cloudlet: " + cloudlet.getProcessed_task2() + "\n"
                + "n1_cloud: " + cloud.getProcessed_task1()
                + "\t\tn2_cloud " + cloud.getProcessed_task2()+ "\n");

        double totalTask = cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2() + cloud.getProcessed_task1() + cloud.getProcessed_task2();
        double lambdaToT = totalTask / clock.getCurrent();
        double lambda1 = (cloudlet.getProcessed_task1() + cloud.getProcessed_task1()) / clock.getCurrent();
        double lambda2 = (cloudlet.getProcessed_task2() + cloud.getProcessed_task2()) / clock.getCurrent();

        double pq = (cloud.getProcessed_task1() + cloud.getProcessed_task2()) / totalTask;

        System.out.println("lambda stimato " + f.format(lambdaToT));
        System.out.println("lambda task 1 stimato " + f.format(lambda1));
        System.out.println("lambda task 2 stimato " + f.format(lambda2) + "\n");

        System.out.println("numero medio di task  del cloudlet " + f.format(global_node.getComplete_time_cloudlet() / clock.getCurrent()));
        System.out.println("numero medio di task1 del cloudlet " + f.format(cloudlet.getArea_task1() / clock.getCurrent()));
        System.out.println("numero medio di task2 del cloudlet " + f.format(cloudlet.getArea_task2() / clock.getCurrent()) + "\n");

        System.out.println("numero medio di task  del cloud " + f.format(global_node.getComplete_time_cloud() / clock.getCurrent()));
        System.out.println("numero medio di task1 del cloud " + f.format(cloud.getArea_task1() / clock.getCurrent()));
        System.out.println("numero medio di task2 del cloud " + f.format(cloud.getArea_task2() / clock.getCurrent()) + "\n");

        System.out.println("tempo di risposta del cloudlet " + f.format(global_node.getComplete_time_cloudlet() /  totalTask ));
        System.out.println("tempo di risposta del cloudlet per task1 " + f.format(cloudlet.getArea_task1() / (  cloudlet.getProcessed_task1() +cloud.getProcessed_task1()  )) );
        System.out.println("tempo di risposta del cloudlet per task2 " + f.format(cloudlet.getArea_task2() / (  cloudlet.getProcessed_task2()+cloud.getProcessed_task2()) )    + "\n");
        allResults.addAll(Arrays.asList( f.format( global_node.getComplete_time_cloudlet() / totalTask),
                f.format(cloudlet.getArea_task1() / cloudlet.getProcessed_task1()),
                f.format(cloudlet.getArea_task2() / cloudlet.getProcessed_task2())));

        System.out.println("tempo di risposta del cloud " + f.format(global_node.getComplete_time_cloud() / totalTask ));
        System.out.println("tempo di risposta del cloud per task1 " + f.format(cloud.getArea_task1() / (cloudlet.getProcessed_task1()+cloud.getProcessed_task1())) );
        System.out.println("tempo di risposta del cloud per task2 " +  f.format(cloud.getArea_task2() / (cloudlet.getProcessed_task2()+cloud.getProcessed_task2()) )+ "\n");
        allResults.addAll(Arrays.asList( f.format( global_node.getComplete_time_cloud() / totalTask ),
                f.format( cloud.getArea_task1() / cloud.getProcessed_task1()),
                f.format( cloud.getArea_task2() / cloud.getProcessed_task2())));

        System.out.println("tempo medio di risposta del sistema " + f.format(global_node.getComplete_time_system() / totalTask) );
        System.out.println("tempo di risposta sistema per task1 " + f.format(global_node.getComplete_time_task1() / (cloudlet.getProcessed_task1() + cloud.getProcessed_task1())) );
        System.out.println("tempo di risposta sistema per task2 " + f.format(global_node.getComplete_time_task2() / (cloudlet.getProcessed_task2() + cloud.getProcessed_task2())) + "\n");
        allResults.addAll(Arrays.asList( f.format( global_node.getComplete_time_system() / totalTask),
                f.format( global_node.getComplete_time_task1() / (cloudlet.getProcessed_task1() + cloud.getProcessed_task1())),
                f.format(global_node.getComplete_time_task2() / (cloudlet.getProcessed_task2() + cloud.getProcessed_task2()))));

        System.out.println("Throughput simulato per il cloudlet " + f.format((cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2()) / clock.getCurrent()) );
        System.out.println("Throughput simulato per il cloud " + f.format(( cloud.getProcessed_task1() + cloud.getProcessed_task2()) / clock.getCurrent()) +"\n");

        System.out.println("Throughput task1 per il cloudlet " + f.format(cloudlet.getProcessed_task1() / clock.getCurrent()) );
        System.out.println("Throughput task2 per il cloudlet " + f.format(cloudlet.getProcessed_task2() / clock.getCurrent()) + "\n");

        System.out.println("Throughput task1 per il cloud " + f.format(cloud.getProcessed_task1() / clock.getCurrent()) );
        System.out.println("Throughput task2 per il cloud " + f.format(cloud.getProcessed_task2() / clock.getCurrent()) + "\n");

        System.out.println(" pq " + f.format(pq)+ "\n");



        System.out.println("server"+ "\t"+"utilization"+ "\t"+"Task1Processed"+ "\t"+"Task2Processed" + "\n");
        for (int s = 1; s <= SERVERS; s++) {
            //System.out.println(clet_servers.get(s).getTotal_service());
            System.out.print(s + "\t\t" +
                    f.format(cloudlet.getServers().get(s).getTotal_service() / clock.getCurrent())+ "\t\t" +
                    cloudlet.getServers().get(s).getProcessed_task1()+ "\t\t" + cloudlet.getServers().get(s).getProcessed_task2()+ "\n" );
        }

        System.out.println("\n\n");

        //instant_writer.close();

        return allResults;
    }


    private int findType2ToSwitch(ArrayList<EventNode> system_events) {
        int event;
        int i = 1;

        while (system_events.get(i).getType() == 1)
            i++;
        event = i;
        while (i < SERVERS) {
            i++;
            if ((system_events.get(i).getType() == 2) &&
                    (system_events.get(i).getTemp() > system_events.get(event).getTemp()))
                event = i;
        }
        return (event);
    }

    @Override
    public ArrayList<ArrayList<Double>> RunBatch(Rngs r, double STOP, PrintWriter writer) {
    return null;}

    public static void main(String[] args) {
        Rngs r = new Rngs();
        r.plantSeeds(Long.parseLong(seed));

        Simulator2_Tran s_algorith2 = new Simulator2_Tran();
        ArrayList<String> values = s_algorith2.RunSimulation(r, STOP_BATCH, Long.toString(r.getSeed()), "Alg2");

    }
}
