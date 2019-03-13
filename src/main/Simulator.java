package main;

import pmcsn.Rngs;

import java.util.ArrayList;


public class Simulator {

    public double START   = 0.0;            /* initial (open the door)        */
    public double STOP    = 100;        /* terminal (close the door) time */
    public static int    SERVERS = 20;              /* number of servers              */

    public double sarrival = START;



    public double exponential(double m, Rngs r) {
        /* ---------------------------------------------------
         * generate an Exponential random variate, use m > 0.0
         * ---------------------------------------------------
         */
        return (-m * Math.log(1.0 - r.random()));
    }

    public double hyperExponential(double mu1, double mu2, Rngs r) {
        double p = 0.2;
        r.selectStream(10);
        double random = r.random();
        if (random < p){
            r.selectStream(30);
            return exponential(1/mu1, r);
        }
        else {
            r.selectStream(60);
            return exponential(1/mu2, r);
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

    public double getService(double mu1, double mu2, Rngs r) {
        /* ------------------------------
         * generate the next service time, with rate 1/6
         * ------------------------------
         */
        r.selectStream(1);
        return (hyperExponential(mu1,mu2, r));
    }
    public double getServiceCloud(double mu, Rngs r) {
        /* ------------------------------
         * generate the next service time, with rate 1/6
         * ------------------------------
         */
        r.selectStream(2);
        return (exponential(mu, r));
    }

    public void RunSimulation(ArrayList<EventNode> cloudlet_events, SystemClock clock, Rngs r){
        int cloudlet_number1= 0, cloudlet_number2 = 0;  // numero di job processati dal cloudlet per tipo
        int cloud_number1 = 0, cloud_number2 = 0;       // numero di job processati dal cloud per tipo
        int working_server = 0 ;                          // numero di job presenti nei server del cloudlet


        // primo arrivo
        cloudlet_events.get(0).setTemp( this.getArrival(12.25, r));
        // devo decidere se il primo arrivo è di tipo A o B
        cloudlet_events.get(0).setType( this.getType(r));
        // type = 0 -> simulazione terminata
        while ((cloudlet_events.get(0).getType() != 0)) {
            int e = this.nextEvent(cloudlet_events);
            clock.setNext( cloudlet_events.get(e).getTemp() );               /* next event index */
            clock.setCurrent(clock.getNext());                              /* advance the clock*/

            if ( e == 0 ) { // processo un arrivo
                if ( cloudlet_events.get(e).getType() == 1) {
                    cloudlet_number1++;
                }else {
                    cloudlet_number2++;
                }
                working_server++;
                int type = cloudlet_events.get(0).getType();

                // genero un nuovo task :(
                cloudlet_events.get(0).setTemp( this.getArrival(12.25, r));
                cloudlet_events.get(0).setType( this.getType(r));
                // fine generazione nuovo task :)

                // termino esecuzione al prossimo while
                if (cloudlet_events.get(0).getTemp() > STOP)
                    cloudlet_events.get(0).setType(0);

                // se ho server disponibili assegno il task
                if (working_server <= SERVERS) { // ho dei server liberi
                    System.err.println("Sono nel cloudlet D:");
                    // genero il tempo di servizio
                    double service = this.getService(0.45,0.27,r);

                    //trovo il server libero da più tempo inattivo
                    int s = this.findOne(cloudlet_events);

                    // aggiorno il clock con il tempo di servizio
                    clock.setCurrent( clock.getCurrent() + service);

                    // aggiorno il server i-esimo ( indice s ) con i nuovi valori di tempo e type
                    cloudlet_events.get(s).setTemp(clock.getCurrent());
                    cloudlet_events.get(s).setType(type);
                }else { // non ho server liberi -> mando al cloud
                    System.err.println("Sono nel cloud :D");
                    if ( cloudlet_events.get(e).getType() == 1) {
                        cloud_number1++;

                        // genero un servizio secondo la distribuzione del tempo di servizio per  task A
                        double service = this.getServiceCloud(0.25,r);

                        // aggiorno il clock
                        clock.setCurrent( clock.getCurrent() + service);
                    }else {
                        cloud_number2++;

                        // genero un servizio secondo la distribuzione del tempo di servizio per  task B
                        double service = this.getServiceCloud(0.23,r);

                        // aggiorno il clock
                        clock.setCurrent( clock.getCurrent() + service);
                    }


                }
            } else { // processo una partenza
                working_server--;
                cloudlet_events.get(e).setType(0);
            }

        }


    }

    private int nextEvent(ArrayList<EventNode> cloudlet_events) {
        int event;
        int i = 0;

        while (cloudlet_events.get(i).getType() == 0)       /* find the index of the first 'active' */
            i++;                                            /* element in the event list            */
        event = i;
        while (i < SERVERS) {         /* now, check the others to find which  */
            i++;                        /* event type is most imminent          */
            if ((cloudlet_events.get(i).getType() > 0) &&
                    (cloudlet_events.get(i).getTemp() < cloudlet_events.get(event).getTemp() ))
                event = i;
        }
        return (event);
    }

    int findOne(ArrayList<EventNode> listNode ) {
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
        ArrayList<EventNode> cloudlet_events = new ArrayList<>();
        for ( int i = 0 ; i< SERVERS +1 ; i++){
            cloudlet_events.add(new EventNode(s.START,0));
        }
        SystemClock clock = new SystemClock(s.START,0.0);
        Rngs r = new Rngs();
        r.plantSeeds(Integer.parseInt(args[0]));
        s.RunSimulation(cloudlet_events,clock,r);


    }
}


