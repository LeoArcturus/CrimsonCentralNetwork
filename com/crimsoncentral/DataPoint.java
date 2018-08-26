package com.crimsoncentral;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class DataPoint {

	public DataPoint() {

	}

	public static ArrayList<Double> one = new ArrayList<Double>();
	public static ArrayList<Double> two = new ArrayList<Double>();

	static int a;
	static int b;

	public static double findCoorelation(ArrayList<Double> x, ArrayList<Double> y) {

		HashMap<Double, Number> map = new HashMap<Double, Number>();
		double cc = -1;

		double x_mean = 0;
		double y_mean = 0;
		double x_sum = 0;
		double y_sum = 0;
		double other = 0;

		/**
		 * 
		 * Mean Calculations for x and y
		 * 
		 */

		x_mean = calculateMean(x);

		y_mean = calculateMean(y);

		/**
		 * 
		 * Standard Deviation Calculations
		 * 
		 */

		for (Double i : x) {
			x_sum = x_sum + Math.pow(i - x_mean, 2);

		}

		for (Double i : y) {
			y_sum = y_sum + Math.pow(i - y_mean, 2);

		}

		for (Double i : x) {
			++a;
			for (Double i2 : y) {
				++b;

				if (a == b) {
					map.put((double) (map.size() + 1), new Number(i, i2));
				}

			}

			b = 0;
		}

		Iterator<Entry<Double, Number>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Double, Number> pair = it.next();

			other = other + (pair.getValue().n1 - x_mean) * (pair.getValue().n2 - y_mean);

		}

		cc = other / (Math.pow(y_sum * x_sum, 0.5));

	

		return cc;

	}

	public static double calculateMean(ArrayList<Double> numbers) {

		double mean = 0;
		for (Double i : numbers) {

			mean = mean + i;
		}

		return mean = mean / numbers.size();

	}

}
