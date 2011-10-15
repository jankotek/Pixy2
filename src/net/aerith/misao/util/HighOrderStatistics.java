/*
 * @(#)HighOrderStatistics.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>HighOrderStatistics</code> represents statistics such as
 * the median value, which are not obtained in order O(n).
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 October 20
 */

public class HighOrderStatistics extends Statistics {
	/**
	 * The median value;
	 */
	protected double median = 0.0;

	/**
	 * Constructs a <code>HighOrderStatistics</code> with a 
	 * <code>Statisticable</code>.
	 * @param target the target to calculate the statistics.
	 */
	public HighOrderStatistics ( Statisticable target ) {
		super(target);
	}

	/**
	 * Calculates the statistics of the target.
	 */
	public void calculate ( ) {
		super.calculate();

		median = 0.0;

		if (target != null) {
			try {
				// Checks if the target is already sorted.
				boolean ascendant = false;
				boolean descendant = false;
				for (int i = 1 ; i < data_count ; i++) {
					if (target.getValueAt(i-1) < target.getValueAt(i))
						ascendant = true;
					if (target.getValueAt(i-1) > target.getValueAt(i))
						descendant = true;
				}

				Statisticable current_target = target;

				// If both flags are true, the target is not sorted.
				// So here it is sorted.
				if (ascendant  &&  descendant) {
					Array array = new Array(data_count);
					for (int i = 0 ; i < data_count ; i++)
						array.set(i, target.getValueAt(i));
					array.sortAscendant();
					current_target = array;
				}

				if (data_count % 2 == 1)
					median = current_target.getValueAt(data_count / 2);
				else
					median = (current_target.getValueAt(data_count / 2 - 1) + current_target.getValueAt(data_count / 2)) / 2.0;
			} catch ( IndexOutOfBoundsException exception ) {
				System.err.println(exception);
			}
		}
	}

	/**
	 * Gets the median value.
	 * @return the median value.
	 */
	public double getMedian ( ) {
		return median;
	}

	/**
	 * Returns a raw string representation of the state of this object,
	 * for debugging use. It should be invoked from <code>toString</code>
	 * method of the subclasses.
	 * @return a string representation of the state of this object.
	 */
	protected String paramString ( ) {
		return super.paramString() + ",median=" + median;
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
