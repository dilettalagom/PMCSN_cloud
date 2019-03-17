package main;

public class configuration {

    public static double mu1_cloudlet = 0.45;
    public static double mu2_cloudlet = 0.27;
    public static double mu1_cloud = 0.25;
    public static double mu2_cloud = 0.22;
    public static double lambda = 12.25;
    public static String seed = "215487963"; // 215487963

    public static String[] seeds = {"215487963","123456789","987654321"}; // 215487963

    public static double START   = 0.0;            /* initial (open the door)        */
    public static double STOP    = 20000;        /* terminal (close the door) time */
    public static int    SERVERS = 20;              /* number of servers              */
}
