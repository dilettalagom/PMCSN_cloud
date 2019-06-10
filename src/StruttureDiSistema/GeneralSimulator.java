package StruttureDiSistema;

import pmcsn.Rngs;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import static pmcsn.Configuration.*;


public abstract class GeneralSimulator {

    public double exponential(double m, Rngs r) {
        return (-m * Math.log(1.0 - r.random()));
    }
    public double getArrival(double lambda, Rngs r) {
        r.selectStream(0);
        return exponential(1.0 / lambda, r);
    }


    public double hyperExponential(double mu, Rngs r) {
        double p = 0.2;
        double m1 = 2 * p * mu;
        double m2 = 2 * (1 - p) * mu;
        r.selectStream(3);
        double random = r.random();
        r.selectStream(4);
        if (random < p) {
            return exponential(1 / m1, r);
        } else {
            return exponential(1 / m2, r);
        }
    }

    public int getTaskType(Rngs r) {
        double p1 = lambda1 / lambda;
        r.selectStream(5);
        double random = r.random();
        if (random <= p1) {
            //è stato generato un task1
            return 1;
        } else
            //è stato generato un task2
            return 2;
    }



    public double getServiceCloudlet(double mu, Rngs r) {
        return (hyperExponential(mu, r));
    }

    public double getServiceCloud(double mu, Rngs r) {
        r.selectStream(1);
        return (exponential(1 / mu, r));
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
            if (e.getType() != 0) {
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


    public void printTranResults(GlobalNode global_node, Cloudlet cloudlet, Cloud cloud, SystemClock clock, double STOP){
        DecimalFormat f = new DecimalFormat("###0.000000");
        f.setGroupingUsed(false);
        f.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));

        //stampo i risultati sul terminale
        System.out.println("\n\n------------------------Risultati prodotti dal valore di stop: "+ STOP +" ------------------------\n");

        System.out.println("n1_cloudlet: "+cloudlet.getProcessed_task1()
                +"\t\tn2_cloudlet: "+cloudlet.getProcessed_task2()+"\n"
                +"n1_cloud: "+cloud.getProcessed_task1()
                +"\t\tn2_cloud "+cloud.getProcessed_task2()+"\n");

        double lambdaToT = global_node.getTotalTask() / clock.getCurrent();
        double lambda1 = (cloudlet.getProcessed_task1() + cloud.getProcessed_task1()) / clock.getCurrent();
        double lambda2 = (cloudlet.getProcessed_task2() + cloud.getProcessed_task2()) / clock.getCurrent();

        double lambdaClet = (cloudlet.getProcessed_task1() + cloudlet.getProcessed_task2()) / clock.getCurrent();
        double lambdaClet_task1 = cloudlet.getProcessed_task1() / clock.getCurrent();
        double lambdaClet_task2 = cloudlet.getProcessed_task2() / clock.getCurrent();

        double lambdaCloud = (cloud.getProcessed_task1() + cloud.getProcessed_task2()) / clock.getCurrent();
        double lambdaCloud_task1 = cloud.getProcessed_task1() / clock.getCurrent();
        double lambdaCloud_task2 = cloud.getProcessed_task2() / clock.getCurrent();

        double pq = lambdaCloud / lambdaToT;
        double pq_1 = lambdaCloud_task1 / lambda1;
        double pq_2 = lambdaCloud_task2 / lambda2;

        System.out.println("lambda stimato "+f.format(lambdaToT));
        System.out.println("lambda task1 stimato "+f.format(lambda1));
        System.out.println("lambda task2 stimato "+f.format(lambda2)+"\n");

        System.out.println("numero medio di task  del cloudlet "+f.format(global_node.getComplete_time_cloudlet()/clock.getCurrent()));
        System.out.println("numero medio di task1 del cloudlet "+f.format(cloudlet.getArea_task1()/clock.getCurrent()));
        System.out.println("numero medio di task2 del cloudlet "+f.format(cloudlet.getArea_task2()/clock.getCurrent())+"\n");

        System.out.println("numero medio di task  del cloud "+f.format(global_node.getComplete_time_cloud()/clock.getCurrent()));
        System.out.println("numero medio di task1 del cloud "+f.format(cloud.getArea_task1()/clock.getCurrent()));
        System.out.println("numero medio di task2 del cloud "+f.format(cloud.getArea_task2()/clock.getCurrent())+"\n");

        System.out.println("numero medio di task  del sistema "+f.format(global_node.getComplete_time_system()/clock.getCurrent()));
        System.out.println("numero medio di task1 del sistema "+f.format(global_node.getComplete_time_task1()/clock.getCurrent()));
        System.out.println("numero medio di task2 del sistema "+f.format(global_node.getComplete_time_task2()/clock.getCurrent())+"\n");

        System.out.println("Throughput per il cloudlet "+f.format(lambdaClet));
        System.out.println("Throughput task1 per il cloudlet "+f.format(lambdaClet_task1 ));
        System.out.println("Throughput task2 per il cloudlet "+f.format(lambdaClet_task2 )+"\n");

        System.out.println("Throughput per il cloud "+f.format(lambdaCloud) );
        System.out.println("Throughput task1 per il cloud "+f.format(lambdaCloud_task1));
        System.out.println("Throughput task2 per il cloud "+f.format(lambdaCloud_task2)+"\n");

        System.out.println("Throughput per il sistema " + global_node.getTotalTask() / clock.getCurrent() );
        System.out.println("Throughput task1 per il sistema " + cloudlet.getProcessed_task1() + cloud.getProcessed_task1() / clock.getCurrent() );
        System.out.println("Throughput task2 per il sistema " + cloudlet.getProcessed_task2() + cloud.getProcessed_task2() / clock.getCurrent() );

        System.out.println("pq "+f.format(pq) );
        System.out.println("pq_1 "+f.format(pq_1) );
        System.out.println("pq_2 "+f.format(pq_2)+ "\n" );

        double clet = global_node.getComplete_time_cloudlet()/(cloudlet.getProcessed_task1()+cloudlet.getProcessed_task2());
        double cl = global_node.getComplete_time_cloud()/(cloud.getProcessed_task1()+cloud.getProcessed_task2());

        System.out.println("tempo di risposta del cloudlet "+f.format(clet));
        System.out.println("tempo di risposta del cloudlet per task1 "+f.format(cloudlet.getArea_task1()/(cloudlet.getProcessed_task1() )));
        System.out.println("tempo di risposta del cloudlet per task2 "+f.format(cloudlet.getArea_task2()/(cloudlet.getProcessed_task2() ))+"\n");

        System.out.println("tempo di risposta del cloud "+f.format(cl));
        System.out.println("tempo di risposta del cloud per task1 "+f.format(cloud.getArea_task1()/ (cloud.getProcessed_task1())));
        System.out.println("tempo di risposta del cloud per task2 "+f.format(cloud.getArea_task2()/ (cloud.getProcessed_task2()))+"\n");

        System.out.println("PRIMO METODO");
        System.out.println("tempo medio di risposta del sistema "+f.format((1-pq)*clet + pq*cl));
        System.out.println("tempo di risposta sistema per task1 "+f.format(
                (1-pq_1)*(global_node.getComplete_time_task1()/(cloudlet.getProcessed_task1()+cloudlet.getProcessed_task2()))  +
                pq_1*(global_node.getComplete_time_task1()/ (cloud.getProcessed_task1()+ cloud.getProcessed_task2()) ) ));

        System.out.println("tempo di risposta sistema per task2 "+f.format(
                (1-pq_2)*(global_node.getComplete_time_task2()/(cloudlet.getProcessed_task1()+cloudlet.getProcessed_task2() ))  +
                pq_2*(global_node.getComplete_time_task2()/ (cloud.getProcessed_task1()+ cloud.getProcessed_task2()) ) )+"\n");

        System.out.println("SECONDO METODO");
        System.out.println("tempo medio di risposta del sistema_2 "+f.format(global_node.getComplete_time_system()/global_node.getTotalTask()));
        System.out.println("tempo di risposta sistema per task1_2 "+f.format(global_node.getComplete_time_task1()/(cloudlet.getProcessed_task1()+cloud.getProcessed_task1())));
        System.out.println("tempo di risposta sistema per task2_2 "+f.format(global_node.getComplete_time_task2()/(cloudlet.getProcessed_task2()+cloud.getProcessed_task2()))+"\n");

        System.out.println("server"+"\t"+"utilization"+"\t"+"Task1Processed"+"\t"+"Task2Processed"+"\n");
        for(int s=1; s <=SERVERS;s++){
            System.out.print(s + "\t\t" +
                    f.format(cloudlet.getServers().get(s).getTotal_service() / clock.getCurrent()) + "\t\t" +
                    cloudlet.getServers().get(s).getProcessed_task1() + "\t\t" + cloudlet.getServers().get(s).getProcessed_task2() + "\n");
        }
        System.out.println("\n\n");
    }
}
