package main;

import pmcsn.Rngs;

import java.util.ArrayList;


public class Simulator {

    public static double mu1_cloudlet = 0.45;
    public static double mu2_cloudlet = 0.27;

    public static double mu1_cloud = 0.25;
    public static double mu2_cloud = 0.22;

    public static double lambda = 12.25;


    public double START   = 0.0;            /* initial (open the door)        */
    public double STOP    = 500000;        /* terminal (close the door) time */
    public static int    SERVERS = 20;              /* number of servers              */

    public double sarrival = START;



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
        r.selectStream(0);
        sarrival += exponential(1.0/lambda, r);
        return (sarrival);
    }

    public double getService(double mu, Rngs r) {
        /* ------------------------------
         * generate the next service time, with rate 1/6
         * ------------------------------
         */
        r.selectStream(1);
        return (hyperExponential(mu , r));
    }
    public double getServiceCloud(double mu, Rngs r) {
        /* ------------------------------
         * generate the next service time, with rate 1/6
         * ------------------------------
         */
        r.selectStream(2);
        return (exponential(mu, r));
    }

    public void RunSimulation(ArrayList<EventNode> system_events, SystemClock clock, Rngs r) {
        int cloudlet_number1 = 0, cloudlet_number2 = 0;  // numero di job processati dal cloudlet per tipo
        int cloud_number1 = 0, cloud_number2 = 0;       // numero di job processati dal cloud per tipo
        int working_server = 0;                          // numero di job presenti nei server del cloudlet


        // primo arrivo
        system_events.get(0).setTemp(this.getArrival(lambda, r));
        // devo decidere se il primo arrivo è di tipo A o B
        system_events.get(0).setType(this.getType(r));
        // type = 0 -> simulazione terminata
        while ((system_events.get(0).getType() != 0)) {
            int e = this.nextEvent(system_events);

            //System.err.println( clock.getCurrent() );

            //double current = clock.getCurrent();

            clock.setNext(system_events.get(e).getTemp());               /* next event index */

            clock.setCurrent(clock.getNext());                              /* advance the clock*/
            //double next = clock.getCurrent();

            //System.err.println(current + "\t" + "\t" + next + "\t" + (current <= next));
            //System.err.println( clock.getNext() + "\n----------------------" );

            if (e == 0) { // processo un arrivo


                int type = system_events.get(e).getType();

                // genero un nuovo task :(
                system_events.get(0).setTemp(this.getArrival(lambda, r));
                system_events.get(0).setType(this.getType(r));
                // fine generazione nuovo task :)

                // termino esecuzione al prossimo while
                if (system_events.get(0).getTemp() > STOP)
                    system_events.get(0).setType(0);

                // se ho server disponibili assegno il task
                if (working_server < SERVERS) { // ho dei server liberi -> ( arrivo cludlet )
                    working_server++;

                    // genero il tempo di servizio
                    double service = 0;
                    if (system_events.get(e).getType() == 1) {
                        cloudlet_number1++;
                        service = this.getService(mu1_cloudlet, r);
                    } else if (system_events.get(0).getType() == 2) {
                        cloudlet_number2++;
                        service = this.getService(mu2_cloudlet, r);
                    }

                    //trovo il server libero da più tempo inattivo
                    int cloudlet_server_selected = this.findOneCloudlet(system_events);


                    // aggiorno il server i-esimo ( indice s ) con i nuovi valori di tempo e type
                    system_events.get(cloudlet_server_selected).setTemp(clock.getCurrent() + service);
                    system_events.get(cloudlet_server_selected).setType(type);

                } else { // non ho server liberi -> mando al cloud  ( arrivo cloud)
                    //System.err.println("Sono nel cloud :D");

                    //trovo il server libero ( se non esiste lo creo )
                    int cloud_server_selected = findOneCloud(system_events);

                    int typeCloud = system_events.get(0).getType();

                    double service = 0;
                    if (system_events.get(e).getType() == 1) {
                        cloud_number1++;
                        // genero un servizio secondo la distribuzione del tempo di servizio per  task A
                        service = this.getServiceCloud(mu1_cloud, r);
                    } else {
                        cloud_number2++;
                        // genero un servizio secondo la distribuzione del tempo di servizio per  task B
                        service = this.getServiceCloud(mu2_cloud, r);
                    }

                    system_events.get(cloud_server_selected).setTemp(clock.getCurrent() + service);
                    system_events.get(cloud_server_selected).setType(typeCloud);

                }
            } else { // processo una partenza

                if (e <= 20) { // processo una partenza cloudlet
                    working_server--;
                    system_events.get(e).setType(0);
                } else { //processo una partenza del cloud
                    system_events.get(e).setType(0);

                }

            }


        }
        for ( EventNode i: system_events)
            System.err.println(i.toString());

    }

    private int findOneCloud(ArrayList<EventNode> system_events) {


        // se non ci sono serventi attivi nel cloud, ne creo 1
        //
        int i = 21;
        if ( system_events.size() == 20 ){
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
        while (i < SERVERS) {         /* now, check the others to find which   */
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
        for ( int i = 0 ; i< SERVERS +1 ; i++){
            system_events.add(new EventNode(s.START,0));
        }
        SystemClock clock = new SystemClock(s.START,s.START);
        Rngs r = new Rngs();
        r.plantSeeds(Integer.parseInt(args[0]));
        s.RunSimulation(system_events,clock,r);


    }
}


