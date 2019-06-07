package Transient;

import pmcsn.Rngs;
import StruttureDiSistema.*;
import pmcsn.Util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static pmcsn.Configuration.*;

public class Simulator2_Tran extends GeneralSimulator {

    private ArrayList<EventNode> system_events;
    private SystemClock clock;
    private GlobalNode global_node;
    private Cloudlet cloudlet;
    private Cloud cloud;


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
    public void RunSimulation(Rngs r, double STOP, String selected_seed, String algoritmo,ArrayList<ArrayList<Double>> estimateTempi ) {

        PrintWriter instant_writer = null;
        try {
            instant_writer=new PrintWriter(new FileWriter("Matlab/transient/" + "Alg2_"+ selected_seed + ".csv"));
            Util.print_on_file(instant_writer, new String[]{"current", "cloudlet", "cloud", "system"});
        } catch (IOException e) {
            e.printStackTrace();
        }
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

            global_node.setTotalTask( cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2() + cloud.getProcessed_task1() + cloud.getProcessed_task2() );

            Util.print_on_file(instant_writer, new String[]{String.valueOf(clock.getCurrent()),
                    String.valueOf(global_node.getComplete_time_cloudlet() / (cloudlet.getWorking_task1() + cloudlet.getWorking_task2()) ),
                    String.valueOf(global_node.getComplete_time_cloud() /  (cloud.getWorking_task1() + cloud.getWorking_task2()) ),
                    String.valueOf(global_node.getComplete_time_system() / global_node.getTotalTask()) });



            clock.setCurrent(clock.getNext());

            if (e == 0) { // processo un arrivo

                int temp_task =cloudlet.getWorking_task1() + cloudlet.getWorking_task2();
                if ( (temp_task < SERVERS) && (( temp_task < LIMIT ) || (system_events.get(e).getType() == 1) )) {

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

                    // salvo il tempo di servizio generato per il task in esecuzione sul server
                    cloudlet.getServers().get(cloudlet_server_selected).setLast_service(clock.getCurrent() + service);
                    // aggiorno il server i-esimo ( indice ) con i nuovi valori di tempo e type
                    system_events.get(cloudlet_server_selected).setTemp(clock.getCurrent() + service);
                    system_events.get(cloudlet_server_selected).setType(system_events.get(e).getType());


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

        //Salvo tutti i numeri per calcolare l'intervallo di confidenza in Estimate()
        statisticTimesValues(estimateTempi, global_node, cloudlet, cloud);

        //stampo i valori sul terminale
        printTranResults( global_node,  cloudlet,  cloud,  clock, STOP);

        assert (instant_writer!=null);
        instant_writer.close();
    }

    @Override
    public ArrayList<ArrayList<Double>> RunBatch(Rngs r, double STOP) {
        return null;}

    public static void main(String[] args) {
        Rngs r = new Rngs();
        r.plantSeeds(Long.parseLong(seed));

        Simulator2_Tran s_algorith2 = new Simulator2_Tran();
        //s_algorith2.RunSimulation(r, STOP_BATCH, Long.toString(r.getSeed()), "Alg2");

    }
}
