package main;

public class Configuration {

    static double mu1_cloudlet = 0.45;
    static double mu2_cloudlet = 0.27;
    static double mu1_cloud = 0.25;
    static double mu2_cloud = 0.22;
    static double lambda = 12.25;
    static double lambda1 = 6.0;

    static String[] seeds = {"215487963"};
    //public static String[] seeds = {"215487963","123456789","987654321"};

    static double START   = 0.0;            /* initial (open the door)        */
    static double STOP    = 2000;          /* terminal (close the door) time */
    static int    SERVERS = 20;             /* number of servers              */
}
