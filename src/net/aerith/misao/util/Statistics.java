/*
 * @(#)Statistics.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>Statistics</code> represents statistics such as the
 * minimum value, maximum value, average, etc., which are obtained
 * in order O(n).
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 November 28
 */

public class Statistics {
	/**
	 * The target to calculate the statistics.
	 */
	protected Statisticable target;

	/**
	 * The number of data.
	 */
	protected int data_count = 0;

	/**
	 * The minimum value;
	 */
	protected double minimum = 0.0;

	/**
	 * The maximum value;
	 */
	protected double maximum = 0.0;

	/**
	 * The average;
	 */
	protected double average = 0.0;

	/**
	 * The standard deviation.
	 */
	protected double deviation = 0.0;

	/**
	 * The minimum value to use in calculation.
	 */
	protected Double minimum_limit;

	/**
	 * The maximum value to use in calculation.
	 */
	protected Double maximum_limit;

	/**
	 * Constructs a <code>Statistics</code> with a 
	 * <code>Statisticable</code>.
	 * @param target the target to calculate the statistics.
	 */
	public Statistics ( Statisticable target ) {
		this.target = target;
	}

	/**
	 * Sets the minimum limit of value to use in calculation.
	 * @param value the minimum limit.
	 */
	public void setMinimumLimit ( double value ) {
		minimum_limit = new Double(value);
	}

	/**
	 * Sets the maximum limit of value to use in calculation.
	 * @param value the maximum limit.
	 */
	public void setMaximumLimit ( double value ) {
		maximum_limit = new Double(value);
	}

	/**
	 * Calculates the statistics of the target.
	 */
	public void calculate ( ) {
		data_count = 0;
		minimum = 0.0;
		maximum = 0.0;
		average = 0.0;
		deviation = 0.0;
		
		if (target != null) {
			try {
				data_count = 0;

				double amount = 0.0;
				double amount2 = 0.0;

				for (int i = 0 ; i < target.getDataCount() ; i++) {
					double value = target.getValueAt(i);

					boolean valid = true;
					if (minimum_limit != null  &&  minimum_limit.doubleValue() > value)
						valid = false;
					if (maximum_limit != null  &&  maximum_limit.doubleValue() < value)
						valid = false;

					if (valid) {
						if (data_count == 0) {
							minimum = value;
							maximum = value;
						} else {
							if (minimum > value)
								minimum = value;
							if (maximum < value)
								maximum = value;
						}
						amount += value;
						amount2 += value * value;

						data_count++;
					}
				}

				if (data_count > 0) {
					average = amount / (double)data_count;
					deviation = Math.sqrt(amount2 / (double)data_count - average * average);
				}
			} catch ( IndexOutOfBoundsException exception ) {
				System.err.println(exception);
			}
		}
	}

	/**
	 * Gets the minimum value.
	 * @return the minimum value.
	 */
	public double getMin ( ) {
		return minimum;
	}

	/**
	 * Gets the maximum value.
	 * @return the maximum value.
	 */
	public double getMax ( ) {
		return maximum;
	}

	/**
	 * Gets the average value.
	 * @return the average value.
	 */
	public double getAverage ( ) {
		return average;
	}

	/**
	 * Gets the standard deviation value.
	 * @return the standard deviation value.
	 */
	public double getDeviation ( ) {
		return deviation;
	}

	/**
	 * Gets the variance value.
	 * @return the variance value.
	 */
	public double getVariance ( ) {
		return deviation * deviation;
	}

	/**
	 * Returns a string representation of the state of this object.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputString ( ) {
		return "data_count=" + data_count + ",minimum=" + minimum + ",maximum=" + maximum + ",average=" + average + ",deviation=" + deviation;
	}

	/**
	 * Returns a raw string representation of the state of this object,
	 * for debugging use. It should be invoked from <code>toString</code>
	 * method of the subclasses.
	 * @return a string representation of the state of this object.
	 */
	protected String paramString ( ) {
		return getOutputString();
	}

	/**
	 * Returns a string representation of the state of this object,
	 * for debugging use.
	 * @return a string representation of the state of this object.
	 */
	public String toString ( ) {
		return getClass().getName() + "[" + paramString() + "]";
	}
}
