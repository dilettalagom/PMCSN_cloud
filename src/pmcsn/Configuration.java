package pmcsn;

public class Configuration {

    public static double mu1_cloudlet = 0.45;
    public static double mu2_cloudlet = 0.27;
    public static double mu1_cloud = 0.25;
    public static double mu2_cloud = 0.22;
    public static double lambda = 12.25;
    public static double lambda1 = 6.0;
    public static double lambda2 = 6.25;

    //Seed iniziali usati per i test della bontà dei seeds
    //public static String[] seeds_collection={"215487963","123456789","987654321","993727181","568439202", "349587492", "230938316", "018765678", "888888888"};

    //Seed effettivamente usati dal simulatore
    public static String[] seeds_collection={"123456789","987654321","993727181","568439202", "349587492", "018765678", "888888888"};
    public static String seed = "215487963";

    public static int tran_replications = 10;
    public static int batch_interval = 10000;


    public static double START   = 0.0;
    public static double STOP_BATCH = 1000000;
    public static double STOP_SEED = 1000000;
    public static double[] STOP_T = {5.00,10.00,50.00,100.00,500.00,1000.00,2000.00, 5000.00};
    //Valore di clock per la generazione dello stazionario
    //public static double[] STOP_T = {10000.00};
    public static int    SERVERS = 20;
    public static int    LIMIT = 10;

    public static double LOC = 0.95;

}
