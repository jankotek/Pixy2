/*
 * @(#)Histogram.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>Histogram</code> represents a histogram of the specified
 * statisticable buffer.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 September 16
 */

public class Histogram implements Statisticable {
	/**
	 * The buffer.
	 */
	protected int[] buffer;

	/**
	 * Constructs an empty <code>Histogram</code>.
	 */
	protected Histogram ( ) {
	}

	/**
	 * Constructs a <code>Histogram</code> of the specified 
	 * statisticable buffer.
	 * @param target_buffer  the statisticable buffer to calculate 
	 * histogram.
	 * @param minimum_value  the minimum value of the histogram range.
	 * @param maximum_value  the maximum value of the histogram range.
	 * @param division_count the division_count;
	 */
	public Histogram ( Statisticable target_buffer, double minimum_value, double maximum_value, int division_count ) {
		buffer = new int[division_count + 2];
		for (int i = 0 ; i < buffer.length ; i++)
			buffer[i] = 0;

		double block_size = (maximum_value - minimum_value) / (double)division_count;

		for (int i = 0 ; i < target_buffer.getDataCount() ; i++) {
			try {
				double value = target_buffer.getValueAt(i);
				int index = (int)((value - minimum_value) / block_size);
				if (index < 0)
					index = -1;
				if (index >= division_count)
					index = division_count;
				buffer[index+1]++;
			} catch ( IndexOutOfBoundsException exception ) {
			}
		}
	}

	/**
	 * Gets the number of data.
	 * @return the number of data.
	 */
	public int getDataCount ( ) {
		return buffer.length;
	}

	/**
	 * Gets the value of data at the specified location.
	 * @param index the location of the data.
	 * @return the value of data at the specified location.
	 * @exception IndexOutOfBoundsException if specified location is
	 * out of the data buffer.
	 */
	public double getValueAt ( int index )
		throws IndexOutOfBoundsException
	{
		return (double)buffer[index];
	}

	/**
	 * Gets the amount of pixels whose value is smaller than the 
	 * specified minimum value.
	 * @return the amount.
	 */
	public int getUnderflow ( ) {
		return buffer[0];
	}

	/**
	 * Gets the amount of pixels whose value is greater than the 
	 * specified maximum value.
	 * @return the amount.
	 */
	public int getOverflow ( ) {
		return buffer[buffer.length - 1];
	}

	/**
	 * Gets the amount of pixels at the block of the specified index.
	 * @param index the index.
	 * @return the amount.
	 */
	public int getAt ( int index ) {
		return buffer[index];
	}

	/**
	 * Looks up this histogram and gets the amount value of the
	 * specified index.
	 * @param the index of buffer to look up.
	 * @return the amount value.
	 */
	public double lookup ( double index ) {
		int index1 = (int)index;
		int index2 = index1 + 1;

		return (index - (double)index1) * (double)buffer[index2] + ((double)index2 - index) * (double)buffer[index1];
	}

	/**
	 * Looks up the inversed histogram and gets the index of the 
	 * specified amount value.
	 * @param value the amount value to look up.
	 * @return the index of buffer which contains the specified
	 * amount value.
	 */
	public double lookupInversed ( double value ) {
		if (value < buffer[0])
			return 0;

		for (int i = 0 ; i < buffer.length - 1 ; i++) {
			if (buffer[i] <= value  &&  value < buffer[i+1])
				return (double)(value - buffer[i]) / (double)(buffer[i+1] - buffer[i]) + (double)i;
		}

		return buffer.length - 1;
	}

	/**
	 * Accumulates the amount values in this histogram and creates a 
	 * new <code>Histogram</code>.
	 * @return the new histogram.
	 */
	public Histogram accumulate ( ) {
		Histogram histogram = new Histogram();

		histogram.buffer = new int[buffer.length];
		histogram.buffer[0] = buffer[0];
		for (int i = 1 ; i < buffer.length ; i++)
			histogram.buffer[i] = histogram.buffer[i-1] + buffer[i];

		return histogram;
	}

	/**
	 * Creates a clone <code>Array</code>.
	 * @return the new array.
	 */
	public Array createCloneArray ( ) {
		Array array = new Array(buffer.length);

		for (int i = 0 ; i < buffer.length ; i++)
			array.set(i, (double)buffer[i]);

		return array;
	}
}
