/*
 * @(#)DefaultLimitingValueEstimator.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy.limiting_mag;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;

/**
 * The <code>DefaultLimitingValueEstimator</code> is a class to 
 * estimate the threshold of the amount of pixel values between the
 * real star images and the noises, based on the pairing ratio between
 * detected stars and catalog data.
 * <p>
 * The <code>Star</code> data in the specified list must have the 
 * link to the <code>StarPair</code> which contains the data itself.
 * The list is sorted in the constructor.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class DefaultLimitingValueEstimator extends Operation {
	/**
	 * The list of stars.
	 */
	protected StarList list;

	/**
	 * The window size to calculate the moving average.
	 */
	protected int average_window_size = 19;

	/**
	 * The minimum window size to calculate the moving average.
	 */
	protected int minimum_window_size = 5;

	/**
	 * The threshold ratio to determine the limiting pixel values.
	 */
	protected double threshold_ratio = 0.7;

	/**
	 * The threshold ratio to limit the array size for mean index calculation.
	 */
	protected double limiting_ratio = 0.5;

	/**
	 * The threshold of the amount of pixel values.
	 */
	protected double threshold = 0.0;

	/**
	 * The number of mode which represents to process based on the
	 * magnitude.
	 */
	public final static int MODE_BASED_ON_MAGNITUDE = 1;

	/**
	 * The number of mode which represents to process based on the
	 * pixel value.
	 */
	public final static int MODE_BASED_ON_PIXEL_VALUE = 2;

	/**
	 * The mode.
	 */
	protected int mode = MODE_BASED_ON_MAGNITUDE;

	/**
	 * The number of policy which represents to estimate the limiting
	 * magnitude.
	 */
	public final static int POLICY_LIMITING = 0;

	/**
	 * The number of policy which represents to estimate the upper-limit
	 * magnitude.
	 */
	public final static int POLICY_UPPERLIMIT = 1;

	/**
	 * The policy.
	 */
	protected int policy = POLICY_LIMITING;

	/**
	 * Constructs a <code>DefaultLimitingValueEstimator</code> with the
	 * list of <code>Star</code> data.
	 * @param list the list of stars.
	 */
	public DefaultLimitingValueEstimator ( StarList list ) {
		this.list = list;
		this.list.sort();
	}

	/**
	 * Sets the mode.
	 * @param mode the mode.
	 */
	public void setMode ( int mode ) {
		this.mode = mode;
	}

	/**
	 * Sets the policy.
	 * @param policy the policy.
	 */
	public void setPolicy ( int policy ) {
		this.policy = policy;
	}

	/**
	 * Sets the window size to calculate the moving average.
	 * @param new_window_size the new window size to calculate the 
	 * moving average.
	 */
	public void setMovingAverageWindowSize ( int new_window_size ) {
		average_window_size = new_window_size;
	}

	/**
	 * Sets the minimum window size to calculate the moving average.
	 * @param new_window_size the new minimum window size to calculate
	 * the moving average.
	 */
	public void setMinimumWindowSize ( int new_window_size ) {
		minimum_window_size = new_window_size;
	}

	/**
	 * Sets the threshold ratio to determine the limiting pixel values.
	 * @param new_threshold_ratio the new threshold ratio to determine
	 * the limiting pixel values.
	 */
	public void setThresholdRatio ( double new_threshold_ratio ) {
		threshold_ratio = new_threshold_ratio;
	}

	/**
	 * Gets the threshold of the amount of pixel values.
	 * @return the threshold of the amount of pixel values.
	 */
	public double getThreshold ( ) {
		return threshold;
	}

	/**
	 * Returns true if the operation is ready to start.
	 * @return true if the operation is ready to start.
	 */
	public boolean ready ( ) {
		return true;
	}

	/**
	 * Operates.
	 * @exception Exception if an error occurs.
	 */
	public void operate ( )
		throws Exception
	{
		if (mode == MODE_BASED_ON_PIXEL_VALUE)
			monitor_set.addMessage("[Limiting value estimation]");
		else
			monitor_set.addMessage("[Limiting magnitude estimation]");

		// Rejects the single data which is out of the area on the other image.
		int count = 0;
		for (int i = 0 ; i < list.size() ; i++) {
			Star star = (Star)list.elementAt(i);
			StarPair pair = star.getPair();
			if (pair.getFirstStar() != null  &&  pair.getSecondStar() != null) {
				count++;
			} else if (pair.isOutOfArea() == false) {
				count++;
			}
		}

		Array yes_no_array = new Array(count);
		Array value_array = new Array(count);
		int index = 0;
		for (int i = 0 ; i < list.size() ; i++) {
			Star star = (Star)list.elementAt(i);
			double value = star.getMag();
			if (mode == MODE_BASED_ON_PIXEL_VALUE)
				value = ((StarImage)star).getValue();

			StarPair pair = star.getPair();
			if (pair.getFirstStar() != null  &&  pair.getSecondStar() != null) {
				yes_no_array.set(index, 1);
				value_array.set(index, value);
				index++;
			} else if (pair.isOutOfArea() == false) {
				yes_no_array.set(index, 0);
				value_array.set(index, value);
				index++;
			}
		}

		int limiting_index = count - 1;
		if (count >= average_window_size) {
			monitor_set.addMessage("Number of data: " + count);

			yes_no_array.setMovingAverage(average_window_size);
			yes_no_array.setMovingAverage(average_window_size);

			int peak_index = yes_no_array.getPeakIndex();
			double peak_ratio = yes_no_array.getValueAt(peak_index);
			monitor_set.addMessage("Peak index: " + peak_index);
			monitor_set.addMessage("Peak value: " + value_array.getValueAt(peak_index));
			monitor_set.addMessage("Peak ratio: " + peak_ratio);

			int limiting_count = count - peak_index;
			for (int i = 1 ; i < count - peak_index ; i++) {
				if (yes_no_array.getValueAt(i + peak_index) < limiting_ratio * peak_ratio) {
					limiting_count = i;
					break;
				}
			}

			Array ratio_array = new Array(limiting_count);
			for (int i = 0 ; i < limiting_count ; i++)
				ratio_array.set(i, yes_no_array.getValueAt(i + peak_index));

			limiting_index = ratio_array.meanIndexOf(threshold_ratio);
			if (limiting_index < 0) {
				monitor_set.addMessage("Mean index of the threshold ratio: null");
				if (ratio_array.getValueAt(0) >= threshold_ratio)
					limiting_index = count - 1;
				else
					limiting_index = average_window_size - 1;
			} else {
				limiting_index = ratio_array.meanIndexOf(threshold_ratio) + peak_index;
				monitor_set.addMessage("Mean index of the threshold ratio: " + limiting_index);
			}
		}

		if (limiting_index < average_window_size) {
			int max_limiting_index = average_window_size - 1;
			if (policy == POLICY_UPPERLIMIT)
				max_limiting_index = limiting_index;

			limiting_index = -1;

			for (int window_size = minimum_window_size ; window_size <= max_limiting_index  &&  window_size < list.size() ; window_size++) {
				int pair_count = 0;
				int total_count = 0;
				for (int i = 0 ; i < list.size()  &&  total_count < window_size ; i++) {
					Star star = (Star)list.elementAt(i);
					StarPair pair = star.getPair();
					if (pair.getFirstStar() != null  &&  pair.getSecondStar() != null) {
						pair_count++;
						total_count++;
					} else if (pair.isOutOfArea() == false) {
						total_count++;
					}
				}

				double ratio = (double)pair_count / (double)total_count;
				if (ratio >= threshold_ratio)
					limiting_index = window_size;
			}

			if (limiting_index < 0)
				limiting_index = minimum_window_size - 1;

			if (limiting_index >= list.size())
				limiting_index = list.size() - 1;
		}

		threshold = value_array.getValueAt(limiting_index);
		monitor_set.addMessage("Limiting index: " + limiting_index);
		monitor_set.addMessage("Limiting value: " + threshold);
		monitor_set.addMessage("Limiting ratio: " + yes_no_array.getValueAt(limiting_index));
		monitor_set.addSeparator();
	}
}
