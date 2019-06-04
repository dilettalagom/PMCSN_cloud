package pmcsn;/* ----------------------------------------------------------------------
 * This program reads a data sample from a text file in the format
 *                         one data point per line
 * and calculates an interval estimate for the mean of that (unknown) much
 * larger set of data from which this sample was drawn.  The data can be
 * either discrete or continuous.  A compiled version of this program
 * supports redirection and can used just like program uvs.c.
 *
 * Name              : Estimate.java (Interval Estimation)
 * Authors           : Steve Park & Dave Geyer
 * Translated By     : Richard Dutton & Jun Wang
 * Language          : Java
 * Latest Revision   : 6-16-06
 * ----------------------------------------------------------------------
 */

import java.lang.Math;
import java.io.*;
import java.util.ArrayList;
import static pmcsn.Configuration.LOC;



public class Estimate {

	public Estimate() {}


	public void calcolateConfidenceByArrays(ArrayList<ArrayList<Double>> simulatorDatas, String seed, PrintWriter estimateWriter) {

		Rvms rvms = new Rvms();
		ArrayList<Double> confidences = new ArrayList<>();
		ArrayList<Double> width = new ArrayList<>();

		for (ArrayList<Double> line : simulatorDatas) {

			long n = 0;                        /* counts data points */
			double sum = 0.0;
			double mean = 0.0;
			double stdev;
			double u, t, w;
			double diff;


			for (Double elem : line) {         /* use Welford's one-pass method */
				n++;                           /* and standard deviation        */
				diff = elem - mean;
				sum += diff * diff * (n - 1.0) / n;
				mean += diff / n;

			}

			stdev = Math.sqrt(sum / n);

			if (n > 1) {
				u = 1.0 - 0.5 * (1.0 - LOC);                  /* interval parameter  */
				t = rvms.idfStudent(n - 1, u);            /* critical value of t */
				w = t * stdev / Math.sqrt(n - 1);             /* interval half width */

				confidences.add(mean);
				width.add(w);
			} else
				System.out.print("ERROR - insufficient data\n");

		}


		ArrayList<String> temp = new ArrayList<>();
		for (int i=0; i<9; i++){
			temp.add(String.valueOf(confidences.get(i)));
			temp.add(String.valueOf(width.get(i)));
		}
		Util.print_on_file(estimateWriter, Util.convertArrayList(temp));

		assert (estimateWriter!=null);
		estimateWriter.close();

		System.out.println("\n\n------------------------Risultati prodotti dal seed: " + seed + " ------------------------");

		System.out.println("\nUsando il campione di elementi e un " + (int) (100.0 * LOC + 0.5) + "% di confidenza " +
				"i valori degli intervalli di confidenza sono:\n");

		System.out.printf("tempo di risposta del cloudlet %.6f +/- %.6f\n", confidences.get(0), width.get(0));
		System.out.printf("tempo di risposta del cloudlet per task1 %.6f +/- %.6f\n", confidences.get(1), width.get(1));
		System.out.printf("tempo di risposta del cloudlet per task2 %.6f +/- %.6f\n\n", confidences.get(2), width.get(2));

		System.out.printf("tempo di risposta del cloud %.6f +/- %.6f\n", confidences.get(3), width.get(3));
		System.out.printf("tempo di risposta del cloud per task1 %.6f +/- %.6f\n", confidences.get(4), width.get(4));
		System.out.printf("tempo di risposta del cloud per task2 %.6f +/- %.6f\n\n", confidences.get(5), width.get(5));

		System.out.printf("tempo medio di risposta del sistema %.6f +/- %.6f\n", confidences.get(6), width.get(6));
		System.out.printf("tempo di risposta sistema per task1 %.6f +/- %.6f\n", confidences.get(7), width.get(7));
		System.out.printf("tempo di risposta sistema per task2 %.6f +/- %.6f\n\n", confidences.get(8), width.get(8));

	}

}
