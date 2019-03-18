package main;

import pmcsn.Rngs;

import java.util.ArrayList;


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
        double m1 = 2* p * mu;
        double m2 = 2* (1-p) * mu;
        r.selectStream(10);
        double random = r.random();
        if (random < p){
            r.selectStream(30);
            return exponential(1/m1, r);
        }
        else {
            r.selectStream(60);
            return exponential(1/m2, r);
        }
    }

    public double getArrival(double lambda, Rngs r){
        /* --------------------------------------------------------------
         * generate the next arrival time, with rate 1/2
         * --------------------------------------------------------------
         */
        r.selectStream(25);
        return exponential(1.0/lambda, r);
    }

    /*public double getServiceHyperexponential(double mu, Rngs r) {

        //r.selectStream(1);
        return (hyperExponential(mu , r));
    }*/


    public double getServiceExponential(double mu, Rngs r) {
        /* ------------------------------
         * generate the next service time, with rate 1/6
         * ------------------------------
         */
        //r.selectStream(2);
        return (exponential(mu, r));
    }

    public void RunSimulation(ArrayList<EventNode> system_events, SystemClock clock, Rngs r) {
        int cloudlet_number1 = 0, cloudlet_number2 = 0;  // numero di job processati dal cloudlet per tipo
        int cloud_number1 = 0, cloud_number2 = 0;       // numero di job processati dal cloud per tipo

        int cloudlet_number=0;   // numero di job nel cloudlet all'istante t
        int cloud_number=0;       // numero di job nel cloud all'istante t

        double area_cloudlet   = 0.0;
        double area_cloud   = 0.0;

        // primo arrivo
        system_events.get(0).setTemp( system_events.get(0).getTemp() + this.getArrival(Configuration.lambda, r));
        // devo decidere se il primo arrivo è di tipo A o B
        system_events.get(0).setType(this.getType(r));
        // type = 0 -> simulazione terminata
        while ((system_events.get(0).getType() != 0)) {
            int e = this.nextEvent(system_events);


            clock.setNext(system_events.get(e).getTemp());               /* next event index */

            area_cloudlet += (clock.getNext() - clock.getCurrent()) * ( cloudlet_number );
            area_cloud += (clock.getNext() - clock.getCurrent()) * ( cloud_number );

            clock.setCurrent(clock.getNext());                              /* advance the clock*/

            if (e == 0) { // processo un arrivo


                int type = system_events.get(e).getType();

                // genero un nuovo task :(
                system_events.get(0).setTemp(system_events.get(0).getTemp() + this.getArrival(Configuration.lambda, r));
                system_events.get(0).setType(this.getType(r));
                // fine generazione nuovo task :)

                // termino esecuzione al prossimo while
                if (system_events.get(0).getTemp() > Configuration.STOP)
                    system_events.get(0).setType(0);

                // se ho server disponibili assegno il task
                if (cloudlet_number <= Configuration.SERVERS) { // ho dei server liberi -> ( arrivo cludlet )
                    cloudlet_number++;

                    // genero il tempo di servizio
                    double service = 0;
                    if (system_events.get(e).getType() == 1) {
                        cloudlet_number1++;
                        service = this.hyperExponential(Configuration.mu1_cloudlet, r);


                    } else if (system_events.get(0).getType() == 2) {
                        cloudlet_number2++;
                        service = this.hyperExponential(Configuration.mu2_cloudlet, r);
                    }

                    //trovo il server libero da più tempo inattivo
                    int cloudlet_server_selected = this.findOneCloudlet(system_events);


                    // aggiorno il server i-esimo ( indice s ) con i nuovi valori di tempo e type
                    system_events.get(cloudlet_server_selected).setTemp(clock.getCurrent() + service);
                    system_events.get(cloudlet_server_selected).setType(type);

                } else { // non ho server liberi -> mando al cloud  ( arrivo cloud)
                    //System.err.println("Sono nel cloud :D");

                    cloud_number++;

                    //trovo il server libero ( se non esiste lo creo )
                    int cloud_server_selected = findOneCloud(system_events);

                    int typeCloud = system_events.get(0).getType();

                    double service = 0;
                    if (system_events.get(e).getType() == 1) {
                        cloud_number1++;
                        // genero un servizio secondo la distribuzione del tempo di servizio per  task A
                        service = this.getServiceExponential(Configuration.mu1_cloud, r);
                    } else {
                        cloud_number2++;
                        // genero un servizio secondo la distribuzione del tempo di servizio per  task B
                        service = this.getServiceExponential(Configuration.mu2_cloud, r);
                    }

                    // aggiorno il tempo nell'i-esimo server con il tempo di servizio generato
                    system_events.get(cloud_server_selected).setTemp(clock.getCurrent() + service);
                    system_events.get(cloud_server_selected).setType(typeCloud);

                }
            } else { // processo una partenza

                if (e <= Configuration.SERVERS) { // processo una partenza cloudlet
                    cloudlet_number--;
                    system_events.get(e).setType(0);
                } else { //processo una partenza del cloud
                    cloud_number--;
                    system_events.get(e).setType(0);

                }

            }


        }
        for ( EventNode i: system_events)
            System.err.println(i.toString());
        System.err.println(cloudlet_number1 + "\t" + cloudlet_number2 +"\t" + cloud_number1 +"\t" + cloud_number2 );

        double totalTask = cloudlet_number1+cloudlet_number2+cloud_number1+cloud_number2 ;

        double lambdaToT = 1.0 / ( clock.getCurrent() / (cloudlet_number1+cloudlet_number2+cloud_number1+cloud_number2));
        double lambdaA = 1.0 / (clock.getCurrent() / (cloudlet_number1+cloud_number1));
        double lambdaB = 1.0 / (clock.getCurrent() / (cloudlet_number2+cloud_number2));

        double pq = (cloud_number1+cloud_number2) / totalTask ;

        System.err.println( "lambda stimato = " + lambdaToT);
        System.err.println( "lambda task A stimato = " + lambdaA );
        System.err.println( "lambda task B stimato = " + lambdaB);

        System.err.println( "tempo di risposta medio cloudlet " + area_cloudlet / (cloudlet_number1+cloudlet_number2));

        System.err.println( "tempo di risposta medio cloud " + area_cloud/   (cloud_number1+cloud_number2) );

        System.err.println( "numero medio di job nel cloudlet " + area_cloudlet / clock.getCurrent());
        System.err.println( "numero medio di job nel cloud " + area_cloud/  clock.getCurrent() );
        //System.err.println( " pq " + pq);
        //System.err.println(" tempo di risposta cloud " + (cloud_number1+cloud_number2)/ (lambdaToT* pq));

    }

    private int findOneCloud(ArrayList<EventNode> system_events) {


        // se non ci sono serventi attivi nel cloud, ne creo 1
        //
        int i = Configuration.SERVERS +1;
        if ( system_events.size() == Configuration.SERVERS ){
            system_events.add(new EventNode());
            return i;

        } else {
            for ( ; i < system_events.size() ; i++){
                if ( system_events.get(i).getType() == 0 ) {
                    return i;
                }
            }
            system_events.add(new EventNode());
            return i;
        }




    }

    private int nextEvent(ArrayList<EventNode> list_events) {
        int event;
        int i = 0;

        while (list_events.get(i).getType() == 0)       /* find the index of the first 'active' */
            i++;                                            /* element in the event list            */
        event = i;
        while (i < list_events.size()-1) {         /* now, check the others to find which  */
            i++;                        /* event type is most imminent          */
            if ((list_events.get(i).getType() > 0) &&
                    (list_events.get(i).getTemp() < list_events.get(event).getTemp() ))
                event = i;
        }
        return (event);
    }

    int findOneCloudlet(ArrayList<EventNode> listNode ) {
        /* -----------------------------------------------------
         * return the index of the available server idle longest
         * -----------------------------------------------------
         */
        int server;
        int i = 1;

        while (listNode.get(i).getType() == 1)       /* find the index of the first available */
            i++;                                       /* (idle) server                         */
        server = i;
        while (i < Configuration.SERVERS) {         /* now, check the others to find which   */
            i++;                        /* has been idle longest                 */
            if ((listNode.get(i).getType() == 0) &&
                    (listNode.get(i).getTemp() < listNode.get(server).getTemp()))
                server = i;
        }
        return (server);
    }

    private int getType(Rngs r) {
        double pA = 6.0/12.25;
        double random = r.random();
        if (random < pA){
            return 1;
        }
        else return 2;
    }


    public static void main(String[] args) {

        Simulator s = new Simulator();
        // inizializzo struttura per contenere lo stato dei singoli server + l'arrivo
        ArrayList<EventNode> system_events = new ArrayList<>();
        for (int i = 0; i< Configuration.SERVERS +1 ; i++){
            system_events.add(new EventNode(Configuration.START,0));
        }
        SystemClock clock = new SystemClock(Configuration.START, Configuration.START);
        Rngs r = new Rngs();
        r.plantSeeds(Integer.parseInt(Configuration.seed));
        s.RunSimulation(system_events,clock,r);


    }
}


