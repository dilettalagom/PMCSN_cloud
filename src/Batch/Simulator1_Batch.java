package Batch;

import pmcsn.Rngs;
import StruttureDiSistema.GeneralSimulator;
import StruttureDiSistema.*;
import pmcsn.Util;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static pmcsn.Configuration.*;

public class Simulator1_Batch extends GeneralSimulator {

    private ArrayList<EventNode> system_events;
    private SystemClock clock;
    private GlobalNode global_node;
    private Cloudlet cloudlet;
    private Cloud cloud;
    private ArrayList<Server> clet_servers;


    //init delle strutture caratteristiche del simulatore
    Simulator1_Batch() {

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
    public ArrayList<String> RunSimulation(Rngs r, double STOP, String selected_seed, String algoritmo) {
        return null;
    }

    @Override
    public void RunBatch(Rngs r, double STOP, PrintWriter batchWriter) {

        int batch = 1;
        DecimalFormat f = new DecimalFormat("###0.000000");
        f.setGroupingUsed(false);
        f.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));



        // primo arrivo
        system_events.get(0).setTemp(getArrival(lambda, r) + clock.getCurrent());
        system_events.get(0).setType(getType(r));          // devo decidere se il primo arrivo è di tipo A o B

        // simulazione termina quando :
        // se il type del prossimo arrivo è 0 -> vuol dire che gli arrivi sono terminati
        // e termina quando tutti i server dentro il cloudlet e il cloud hanno type = 0
        while (system_events.get(0).getType() != 0 ) {

            if(clock.getCurrent()> batch_interval && batch*batch_interval < STOP ){
                System.out.println(batch);

                ArrayList<String> batchValues = new ArrayList<>(Arrays.asList( Integer.toString(batch),
                        f.format(global_node.getComplete_time_cloudlet() / (cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2() + cloud.getProcessed_task1() + cloud.getProcessed_task2())),
                        f.format(cloudlet.getArea_task1() / (cloudlet.getProcessed_task1()+ cloud.getProcessed_task1())),
                        f.format(cloudlet.getArea_task2() / (cloudlet.getProcessed_task2()+cloud.getProcessed_task2())),

                        f.format(global_node.getComplete_time_cloud() / (cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2() + cloud.getProcessed_task1() + cloud.getProcessed_task2())),
                        f.format(cloud.getArea_task1() / (cloudlet.getProcessed_task1()+ cloud.getProcessed_task1())),
                        f.format(cloud.getArea_task2() / ( cloudlet.getProcessed_task2()+ cloud.getProcessed_task2())),

                        f.format(global_node.getComplete_time_system() / (cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2() + cloud.getProcessed_task1() + cloud.getProcessed_task2())),
                        f.format(global_node.getComplete_time_task1()  / (cloudlet.getProcessed_task1() + cloud.getProcessed_task1())),
                        f.format(global_node.getComplete_time_task2()  / (cloudlet.getProcessed_task2() + cloud.getProcessed_task2()))
                ));


                Util.print_on_file(batchWriter, Util.convertArrayList(batchValues));

                //riporto la struttura EventNode a clock.Current = 0
                for (EventNode event: system_events){
                    if( event.getTemp() - clock.getCurrent()<0 || event.getType() == 0)
                        event.setTemp(0);
                    else
                        event.setTemp(event.getTemp() - clock.getCurrent());
                }
                cloud.resetCloud(system_events);
                cloudlet.resetCloudlet(system_events);
                global_node.setEmpty();
                clock.setEmpty();

                system_events.get(0).setTemp(getArrival(lambda, r));
                system_events.get(0).setType(getType(r));

                batch++;
            }

            if (system_events.get(0).getTemp() > STOP ) {
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

        //ultimo Batch che svuota le code, bloccando gli arrivi
        ArrayList<String> batchValues = new ArrayList<>(Arrays.asList(Integer.toString(batch),
                f.format(global_node.getComplete_time_cloudlet() / (cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2() + cloud.getProcessed_task1() + cloud.getProcessed_task2())),
                f.format(cloudlet.getArea_task1() / (cloudlet.getProcessed_task1()+ cloud.getProcessed_task1())),
                f.format(cloudlet.getArea_task2() / (cloudlet.getProcessed_task2()+cloud.getProcessed_task2())),

                f.format(global_node.getComplete_time_cloud() / (cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2() + cloud.getProcessed_task1() + cloud.getProcessed_task2())),
                f.format(cloud.getArea_task1() / (cloudlet.getProcessed_task1()+ cloud.getProcessed_task1())),
                f.format(cloud.getArea_task2() / ( cloudlet.getProcessed_task2()+ cloud.getProcessed_task2())),

                f.format(global_node.getComplete_time_system() / (cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2() + cloud.getProcessed_task1() + cloud.getProcessed_task2())),
                f.format(global_node.getComplete_time_task1()  / (cloudlet.getProcessed_task1() + cloud.getProcessed_task1())),
                f.format(global_node.getComplete_time_task2()  / (cloudlet.getProcessed_task2() + cloud.getProcessed_task2()))
        ));
        Util.print_on_file(batchWriter, Util.convertArrayList(batchValues));

        assert (batchWriter!=null);
        batchWriter.close();

    }

}
