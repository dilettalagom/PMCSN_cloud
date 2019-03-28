package restructed;

import pmcsn.Rngs;

import static restructed.Configuration.*;


abstract class GeneralSimulator {


    private double exponential(double m, Rngs r) {
        /* ------------------------------
         * generate an Exponential random variate, use m > 0.0
         * ------------------------------
         */
        return (-m * Math.log(1.0 - r.random()));
    }

    private double hyperExponential(double mu, Rngs r) {
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

    int getType(Rngs r) {
        double pA = lambda1 / lambda;
        double random = r.random();
        if (random < pA) {
            return 1;
        } else return 2;
    }

    double getArrival(double lambda, Rngs r) {
        /* ------------------------------
         * generate the next arrival time
         * ------------------------------
         */
        r.selectStream(0);
        return exponential(1.0 / lambda, r);
    }

    double getServiceCloudlet(double mu, Rngs r) {
        /* ------------------------------
         * generate the next service time
         * ------------------------------
         */
        r.selectStream(1);
        return (hyperExponential(mu, r));
    }

    double getServiceCloud(double mu, Rngs r) {
        /* ------------------------------
         * generate the next service time
         * ------------------------------
         */
        r.selectStream(2);
        return (exponential(mu, r));
    }


}
