

import pmcsn.Rngs;
import pmcsn.Rvms;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import static pmcsn.Configuration.*;

public class Test {


    public static double exponential(double m, Rngs r) {
        /* ------------------------------
         * generate an Exponential random variate, use m > 0.0
         * ------------------------------
         */
        return (-m * Math.log(1.0 - r.random()));
    }

    public static double hyperExponential(double mu, Rngs r) {
        double p = 0.2;
        double m1 = 2 * p * mu;
        double m2 = 2 * (1 - p) * mu;
        r.selectStream(10);
        double random = r.random();
        if (random < p) {
            r.selectStream(30);
            return exponential(1 / m1, r);
        } else {
            r.selectStream(60);
            return exponential(1 / m2, r);
        }
    }

    public static void mean(ArrayList<Double> temp) {

        long number = 0; // numer of element of file
        double data = 0;

        double sum = temp.stream()
                .mapToDouble( a-> a)
                .sum();

        System.out.println(1 / (sum/temp.size()) );
    }


    public static ArrayList<Double> test(double mu, Rngs r, int DistSelected){
        ArrayList<Double> list = new ArrayList<>();

        if ( DistSelected == 1 ) {
            for (int i = 0; i < STOP; i++) {
              list.add(exponential(1 / mu, r));
            }
        }
        else {
            for (int i = 0; i < STOP; i++) {
                list.add(hyperExponential(mu, r));
            }
        }
        return list;
    }

    public static void main(String[] args) {
        Rngs r = new Rngs();
        r.plantSeeds(Long.parseLong(seed));

        double m;
        ArrayList<Double> temp;

        System.out.println("Exponential" + "_mu1Cloudlet_" + Long.parseLong(seed) );
        m = mu1_cloudlet;
        temp = test(m, r, 1);
        mean(temp);

        System.out.println("Exponential" + "_mu2Cloudlet_" + Long.parseLong(seed) );
        m = mu2_cloudlet;
        temp = test( m, r, 1);
        mean(temp);

        System.out.println("Hyperexponential" + "_mu1Cloudlet_" + Long.parseLong(seed) );
        m = mu1_cloudlet;
        temp = test( m, r, 2);
        mean(temp);

        System.out.println("Hyperexponential" + "_mu2Cloudlet_" + Long.parseLong(seed)) ;
        m = mu2_cloudlet;
        temp = test( m, r, 2);
        mean(temp);



    }
}
