package pmcsn;

public class Configuration {

    public static double mu1_cloudlet = 0.45;
    public static double mu2_cloudlet = 0.27;
    public static double mu1_cloud = 0.25;
    public static double mu2_cloud = 0.22;
    public static double lambda = 12.25;
    public static double lambda1 = 6.0;

    public static String[] seeds_collection = {"215487963","123456789","987654321"};
    public static String seed = "215487963";
    public static int tran_replications = 10;
    public static int batch_interval = 1000;


    public static double START   = 0.0;                        /* initial (open the door)        */
    public static double STOP = 10000;                        /* terminal (close the door) time */
    public static double[] STOP_T = {100.0,200.0,300.0,400.0,500.0,600.0,700.0,800.0,900.0,1000.0,1100.0,1200.0,1300.0,1400.0,1500.0,1600.0};
    public static int    SERVERS = 20;                         /* number of servers */
    public static double S = 15;

    public static double LOC = 0.95;                           /* level of confidence, use 0.95 for 95% confidence */

}
