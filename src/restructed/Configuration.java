package restructed;

public class Configuration {

    public static double mu1_cloudlet = 0.45;
    public static double mu2_cloudlet = 0.27;
    public static double mu1_cloud = 0.25;
    public static double mu2_cloud = 0.22;
    public static double lambda = 12.25;
    public static double lambda1 = 6.0;

    public static String[] seeds = {"215487963", "987654321"};
    public static String seed = "215487963";
    public static int replications = 20;

    //public static String[] seeds = {"215487963","123456789","987654321"};

    public static double START   = 0.0;            /* initial (open the door)        */
    //public static double STOP    = 50;          /* terminal (close the door) time */
    public static double[] STOP_T = {20.0,50.0,100.0,150.0,200.0,500.0};
    public static int    SERVERS = 20;             /* number of servers              */
    public static double S = 15;
}
