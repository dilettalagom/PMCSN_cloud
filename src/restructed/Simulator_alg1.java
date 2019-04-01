package restructed;

import pmcsn.Rngs;
import restructed.StruttureDiSistema.*;
import static restructed.Configuration.*;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class Simulator_alg1 extends GeneralSimulator {

    private ArrayList<EventNode> system_events;
    private SystemClock clock;
    private GlobalNode global_node;
    private Cloudlet cloudlet;
    private Cloud cloud;
    private ArrayList<Server> clet_servers;


    //init delle strutture caratteristiche del simulatore
    Simulator_alg1() {

        this.clet_servers = new ArrayList<>();
        for (int i = 0; i < SERVERS + 1; i++) {
            clet_servers.add(new Server());
        }
        this.system_events = new ArrayList<>();
        for (int i = 0; i < SERVERS + 1; i++) {
            system_events.add(new EventNode(START, 0));
        }
        this.clock = new SystemClock(START, START);
        this.global_node = new GlobalNode(START, START);
        this.cloudlet = new Cloudlet();
        this.cloud = new Cloud();

    }

    @Override
    public boolean RunSimulation(Rngs r, String selected_seed, String algoritmo) {


        PrintWriter instant_writer = createFile("instant_writer", algoritmo, selected_seed);
        if(instant_writer == null)
            return false;
        PrintWriter mean_writer = createFile("mean_writer", algoritmo, selected_seed);
        if(mean_writer == null) {
            return false;
        }

        // primo arrivo
        system_events.get(0).setTemp(getArrival(lambda, r) + clock.getCurrent());
        system_events.get(0).setType(getType(r));          // devo decidere se il primo arrivo è di tipo A o B

        // simulazione termina quando :
        // se il type del prossimo arrivo è 0 -> vuol dire che gli arrivi sono terminati
        // e termina quando tutti i server dentro il cloudlet e il cloud hanno type = 0
        while (system_events.get(0).getType() != 0 ) {

            if (system_events.get(0).getTemp() > STOP ) {
                //System.out.println(system_events);
                if ( check_system_servers(this.system_events) ){
                    break;
                }
            }


            int e = this.nextEvent(system_events);

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


            print_on_file(instant_writer, new String[]{String.valueOf(clock.getCurrent()),
                    String.valueOf(global_node.getComplete_time_cloudlet() / (cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2())),
                    String.valueOf(global_node.getComplete_time_cloud() / (cloud.getProcessed_task1() + cloud.getProcessed_task2())),
                    String.valueOf(global_node.getComplete_time_system() / (cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2() + cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2()))});


            clock.setCurrent(clock.getNext());


            if (e == 0) { // processo un arrivo

                int type = system_events.get(e).getType();

                if (system_events.get(0).getTemp() <= STOP) {
                    system_events.get(0).setTemp(getArrival(lambda, r) + clock.getCurrent());
                    system_events.get(0).setType(getType(r));
                }
                else {
                    system_events.get(0).setType(0);
                }

                // se ho server disponibili assegno il task
                if (cloudlet.getWorking_task1() + cloudlet.getWorking_task2() < SERVERS) { // ho dei server liberi -> ( arrivo cloudlet )

                    //trovo il server libero da più tempo inattivo
                    int cloudlet_server_selected = findOneCloudlet(system_events);

                    double service = 0;
                    if (type == 1) {
                        cloudlet.setWorking_task1(cloudlet.getWorking_task1() + 1);
                        service = getServiceCloudlet(mu1_cloudlet, r);


                    } else if (type == 2) {
                        cloudlet.setWorking_task2(cloudlet.getWorking_task2() + 1);

                        service = getServiceCloudlet(mu2_cloudlet, r);
                    }


                    clet_servers.get(cloudlet_server_selected).setTotal_service(clet_servers.get(cloudlet_server_selected).getTotal_service() + service );


                    // aggiorno il server i-esimo ( indice ) con i nuovi valori di tempo e type
                    system_events.get(cloudlet_server_selected).setTemp(clock.getCurrent() + service);
                    system_events.get(cloudlet_server_selected).setType(type);


                } else { // non ho server liberi -> mando al cloud  ( arrivo cloud)

                    //trovo il server libero ( se non esiste lo creo )
                    int cloud_server_selected = findOneCloud(system_events);

                    int typeCloud = system_events.get(e).getType();

                    double service = 0;
                    if (system_events.get(e).getType() == 1) {
                        cloud.setWorking_task1(cloud.getWorking_task1() + 1);

                        // genero un servizio secondo la distribuzione del tempo di servizio per  task A
                        service = getServiceCloud(mu1_cloud, r);

                    } else {
                        cloud.setWorking_task2(cloud.getWorking_task2() + 1);

                        // genero un servizio secondo la distribuzione del tempo di servizio per  task B
                        service = getServiceCloud(mu2_cloud, r);
                    }

                    system_events.get(cloud_server_selected).setTemp(clock.getCurrent() + service);
                    system_events.get(cloud_server_selected).setType(typeCloud);

                }

            } else { // processo una partenza



                if (e <= SERVERS) { // processo una partenza cloudlet

                    if (system_events.get(e).getType() == 1) {
                        cloudlet.setWorking_task1(cloudlet.getWorking_task1() - 1);
                        cloudlet.setProcessed_task1(cloudlet.getProcessed_task1() + 1);
                        clet_servers.get(e).setProcessed_task1(clet_servers.get(e).getProcessed_task1() + 1 );

                    } else if (system_events.get(e).getType() == 2) {
                        cloudlet.setWorking_task2(cloudlet.getWorking_task2() - 1);
                        cloudlet.setProcessed_task2(cloudlet.getProcessed_task2() + 1);
                        clet_servers.get(e).setProcessed_task2(clet_servers.get(e).getProcessed_task2() + 1 );
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

        ArrayList<String> allResults = new ArrayList<>();

        System.out.println("Risultati prodotti dal seed: " + selected_seed);
        allResults.add(selected_seed);

        System.out.println("n1_cloudlet: " + cloudlet.getProcessed_task1()
                + "\t\tn2_cloudlet: " + cloudlet.getProcessed_task2() + "\n"
                + "n1_cloud: " + cloud.getProcessed_task1()
                + "\t\tn2_cloud " + cloud.getProcessed_task2()+ "\n");

        allResults.addAll(Arrays.asList(Integer.toString(cloudlet.getProcessed_task1()), Integer.toString(cloudlet.getProcessed_task2()), Integer.toString(cloud.getProcessed_task1()), Integer.toString(cloud.getProcessed_task2())));


        double totalTask = cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2() + cloud.getProcessed_task1() + cloud.getProcessed_task2();

        System.out.println("total task : " +totalTask + "\n");

        System.out.println("clock: " + clock.getCurrent() + "\n");

        int task1_system = cloudlet.getProcessed_task1() + cloud.getProcessed_task1();
        int tasl2_system=cloudlet.getProcessed_task2() + cloud.getProcessed_task2();

        System.out.println(task1_system + "\t" +tasl2_system +"\n" );

        double lambdaToT = totalTask / clock.getCurrent();
        double lambda1 = (cloudlet.getProcessed_task1() + cloud.getProcessed_task1()) / clock.getCurrent();
        double lambda2 = (cloudlet.getProcessed_task2() + cloud.getProcessed_task2()) / clock.getCurrent();

        double pq = (cloud.getProcessed_task1() + cloud.getProcessed_task2()) / totalTask;

        System.out.println("lambda stimato " + lambdaToT);
        System.out.println("lambda task 1 stimato " + lambda1);
        System.out.println("lambda task 2 stimato " + lambda2 + "\n");
        allResults.addAll(Arrays.asList(String.format("%.6f", lambdaToT), String.format("%.6f",lambda1), String.format("%.6f",lambda2)));


        System.out.println("numero medio di task del cloudlet " + global_node.getComplete_time_cloudlet() / clock.getCurrent());
        System.out.println("numero medio di task1 del cloudlet " + cloudlet.getArea_task1() / clock.getCurrent());
        System.out.println("numero medio di task2 del cloudlet " + cloudlet.getArea_task2() / clock.getCurrent() + "\n");
        allResults.addAll(Arrays.asList( String.format("%.6f",global_node.getComplete_time_cloudlet() / clock.getCurrent()),
                String.format("%.6f",cloudlet.getArea_task1() / clock.getCurrent()),
                String.format("%.6f",cloudlet.getArea_task2() / clock.getCurrent())));

        System.out.println("numero medio di task del cloud " + global_node.getComplete_time_cloud() / clock.getCurrent());
        System.out.println("numero medio di task1 del cloud " + cloud.getArea_task1() / clock.getCurrent());
        System.out.println("numero medio di task2 del cloud " + cloud.getArea_task2() / clock.getCurrent() + "\n");
        allResults.addAll(Arrays.asList( String.format("%.6f", global_node.getComplete_time_cloud() / clock.getCurrent()),
                String.format("%.6f", cloud.getArea_task1() / clock.getCurrent()),
                String.format("%.6f", cloud.getArea_task2() / clock.getCurrent())));


        System.out.println("tempo di risposta del cloudlet " + global_node.getComplete_time_cloudlet() / (cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2()));
        System.out.println("tempo di risposta del cloudlet per task1 " + cloudlet.getArea_task1() / cloudlet.getProcessed_task1());
        System.out.println("tempo di risposta del cloudlet per task2 " + cloudlet.getArea_task2() / cloudlet.getProcessed_task2() + "\n");
        allResults.addAll(Arrays.asList( String.format("%.6f", global_node.getComplete_time_cloudlet() / (cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2())),
                String.format("%.6f", cloudlet.getArea_task1() / cloudlet.getProcessed_task1()),
                String.format("%.6f", cloudlet.getArea_task2() / cloudlet.getProcessed_task2())));

        System.out.println("tempo di risposta del cloud " + global_node.getComplete_time_cloud() / (cloud.getProcessed_task1() + cloud.getProcessed_task2()));
        System.out.println("tempo di risposta del cloud per task1 " + cloud.getArea_task1() / cloud.getProcessed_task1());
        System.out.println("tempo di risposta del cloud per task2 " +  cloud.getArea_task2() / cloud.getProcessed_task2() + "\n");
        allResults.addAll(Arrays.asList( String.format("%.6f", global_node.getComplete_time_cloud() / (cloud.getProcessed_task1() + cloud.getProcessed_task2())),
                String.format("%.6f", cloud.getArea_task1() / cloud.getProcessed_task1()),
                String.format("%.6f", cloud.getArea_task2() / cloud.getProcessed_task2())));

        System.out.println("tempo medio di risposta del sistema " + global_node.getComplete_time_system() / totalTask);
        System.out.println("tempo di risposta sistema per task1 " + global_node.getComplete_time_task1() / (cloudlet.getProcessed_task1() + cloud.getProcessed_task1()));
        System.out.println("tempo di risposta sistema per task2 " + global_node.getComplete_time_task2() / (cloudlet.getProcessed_task2() + cloud.getProcessed_task2()) + "\n");
        allResults.addAll(Arrays.asList( String.format("%.6f", global_node.getComplete_time_system() / totalTask),
                String.format("%.6f", global_node.getComplete_time_task1() / (cloudlet.getProcessed_task1() + cloud.getProcessed_task1())),
                String.format("%.6f",global_node.getComplete_time_task2() / (cloudlet.getProcessed_task2() + cloud.getProcessed_task2()))));

        System.out.println("Throughtput per il cloudlet " + ( cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2()) / (clock.getCurrent()  ) );
        System.out.println("Throughtput per il cloud " + ( cloud.getProcessed_task1() + cloud.getProcessed_task2()) / (clock.getCurrent()   )+"\n" );

        System.out.println("Throughtput Task1 per il sistema " + ( cloudlet.getProcessed_task1() + cloud.getProcessed_task1()) / (clock.getCurrent()  ) );
        System.out.println("Throughtput Task2 per il sistema " + ( cloudlet.getProcessed_task2() + cloud.getProcessed_task2()) / (clock.getCurrent()   )+"\n"  );

        System.out.println("Throughtput Task1 per il cloudlet " + ( cloudlet.getProcessed_task1() ) / (clock.getCurrent()  )  );
        System.out.println("Throughtput Task2 per il cloudlet " + ( cloudlet.getProcessed_task2() ) / (clock.getCurrent()  )  );
        System.out.println("Throughtput Task1 per il cloudlet (secondo modo)" + (lambda1 * (1 - pq)));
        System.out.println("Throughtput Task2 per il cloudlet (secondo modo)" + (lambda2 * (1 - pq))+"\n");

        System.out.println("Throughtput Task1 per il cloud " + ( cloud.getProcessed_task1() ) / (clock.getCurrent()  ) );
        System.out.println("Throughtput Task2 per il cloud " + ( cloud.getProcessed_task2() ) / (clock.getCurrent()  )  );
        System.out.println("Throughtput Task1 per il cloud (secondo modo)" + (lambda1 * (pq)));
        System.out.println("Throughtput Task2 per il cloud (secondo modo)" + (lambda2 * (pq)) + "\n");

        System.out.println(" pq " + pq +"\n");

        DecimalFormat g = new DecimalFormat("###0.000000000");

        System.out.println("server"+ "\t"+"utilization"+ "\t"+"Task1Processed"+ "\t"+"Task2Processed" + "\n");

        for (int s = 1; s <= SERVERS; s++) {
            System.out.print(s + "\t\t" +
                    g.format(clet_servers.get(s).getTotal_service() / clock.getCurrent())+ "\t\t" +
                    clet_servers.get(s).getProcessed_task1()+ "\t\t" + clet_servers.get(s).getProcessed_task2()+ "\n" );
        }

        allResults.addAll(Arrays.asList( Double.toString(lambda1 * (pq)), Double.toString(lambda2 * (pq)),  Double.toString(pq)));


        System.out.println("------------------------------------------------------------");


        Object[] temp = allResults.toArray();
        String[] str = Arrays.copyOf(temp,
                temp.length,
                String[].class);

        print_on_file(mean_writer, str);

        instant_writer.close();
        mean_writer.close();

        return true;
    }

}
