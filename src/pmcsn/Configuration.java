package pmcsn;

public class Configuration {

    public static double mu1_cloudlet = 0.45;
    public static double mu2_cloudlet = 0.27;
    public static double mu1_cloud = 0.25;
    public static double mu2_cloud = 0.22;
    public static double lambda = 12.25;
    public static double lambda1 = 6.0;
    public static double lambda2 = 6.25;

    //Seed iniziali usati per i test dellabontà dei seeds
    //public static String[] seeds_collection={"215487963","123456789","987654321","993727181","568439202", "349587492", "230938316", "018765678", "888888888"};

    //Seed effettivamente usati dal simulatore
    public static String[] seeds_collection={"123456789","987654321","993727181","568439202", "349587492", "018765678", "888888888"};
    public static String seed = "215487963";

   //public static int tran_replications = 10;
    public static int tran_replications = 3;
    public static int batch_interval = 1000;


    public static double START   = 0.0;                         /* initial (open the door)        */
    public static double STOP_BATCH = 100000;                  /* terminal (close the door) time */
    public static double STOP_SEED = 1000000;                   /* terminal (close the door) time */
    //public static double[] STOP_T = {2.0,4.0,6.0,8.0,10.0,20.0,40.0,60.0,80.0,100.0};
    public static double[] STOP_T = { 100.00,200.00, 300.00, 400.00, 500.00, 600.00, 700.00, 800.00, 900.00, 1000.00};
    //public static double[] STOP_T = { 10000.00};
    public static int    SERVERS = 20;                         /* number of servers */
    public static int    LIMIT = 10;                           /* number of servers */

    public static double LOC = 0.95;                           /* level of confidence, use 0.95 for 95% confidence */

}
