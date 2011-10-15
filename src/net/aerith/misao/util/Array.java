/*
 * @(#)Array.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>Array</code> represents an array of data values with a
 * funciton to sort.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 28
 */

public class Array extends SortableArray implements Statisticable {
	/**
	 * The buffer of data array.
	 */
	protected double[] array;

	/**
	 * The size of buffer.
	 */
	protected int size;

	/**
	 * Constructs an <code>Array</code> with a specified size of 
	 * buffer. The buffer is set as 0.
	 * @param buffer_size the size of buffer to create.
	 */
	public Array ( int buffer_size ) {
		size = buffer_size;
		if (size > 0) {
			array = new double[size];

			for (int i = 0 ; i < size ; i++)
				array[i] = 0;
		}
	}

	/**
	 * Gets the size of this array.
	 * @return the size of this array.
	 */
	public int getArraySize ( ) {
		return size;
	}

	/**
	 * Gets the number of data.
	 * @return the number of data.
	 */
	public int getDataCount ( ) {
		return size;
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
		return array[index];
	}

	/**
	 * Sets the value of data at the specified location.
	 * @param index the location to store the data.
	 * @param value the value of data.
	 * @exception IndexOutOfBoundsException if specified location is
	 * out of the data buffer.
	 */
	public void set ( int index, double value )
		throws IndexOutOfBoundsException
	{
		array[index] = value;
	}

	/**
	 * Gets the location of the largest value. If there are some 
	 * elements which have the same largest value, the location of the
	 * first element among them is returned.
	 * @return the location of the largest value.
	 */
	public int getPeakIndex ( ) {
		if (size <= 0)
			return -1;

		int index = 0;
		double value = getValueAt(0);
		for (int i = 1 ; i < size ; i++) {
			if (value < getValueAt(i)) {
				index = i;
				value = getValueAt(i);
			}
		}

		return index;
	}

	/**
	 * Gets the mean location where the value is expected to go across 
	 * the specified value. When all elements are smaller or larger
	 * than the specified value, which means the value never go across
	 * the specified value, -1 is returned.
	 * @param value the value.
	 * @return the mean location where the value is expected to go 
	 * across the specified value.
	 */
	public int meanIndexOf ( double value ) {
		if (size <= 0)
			return -1;

		double index = 0.0;
		int count = 0;
		for (int i = 0 ; i < size - 1 ; i++) {
			if ((getValueAt(i) - value) * (getValueAt(i+1) - value) <= 0.0) {
				index += (double)i;
				count++;
			}
		}

		if (count == 0)
			return -1;

		index /= (double)count;
		return (int)index;
	}

	/**
	 * Calculates the moving average and sets the result into this
	 * array.
	 * @param window_size the number of data to caluclate the moving
	 * average.
	 */
	public void setMovingAverage ( int window_size ) {
		Array tmp_array = new Array(size);

		for (int i = 0 ; i < size ; i++) {
			int base_index = i - (window_size - 1) / 2;
			double value = 0.0;
			int count = 0;
			for (int j = 0 ; j < window_size ; j++) {
				if (0 <= base_index + j  &&  base_index + j < size) {
					value += getValueAt(base_index + j);
					count++;
				}
			}
			tmp_array.set(i, value / (double)count);
		}

		for (int i = 0 ; i < size ; i++)
			set(i, tmp_array.getValueAt(i));
	}

	/**
	 * Compares the two data represented by the specified index and 
	 * returns which is smaller. If the first data is smaller, it 
	 * returns COMPARE_SMALLER. If the first data is larger, it 
	 * returns COMPARE_LARGER. If both the data are equal, it returns
	 * COMPARE_EQUAL.
	 * @param index1 the index to represent the first data to compare.
	 * @param index2 the index to represent the second data to compare.
	 * @return the number to represent the comparison result.
	 */
	protected int compare ( int index1, int index2 ) {
		if (array[index1] < array[index2])
			return COMPARE_SMALLER;
		if (array[index1] > array[index2])
			return COMPARE_LARGER;
		return COMPARE_EQUAL;
	}

	/**
	 * Sorts this array itself based on the sorted index. It is 
	 * invoked in the sorting functions.
	 */
	protected void sort ( ) {
		Array work_array = new Array(size);
		for (int i = 0 ; i < size ; i++)
			work_array.set(i, getValueAt(sort_index.get(i)));
		for (int i = 0 ; i < size ; i++)
			set(i, work_array.getValueAt(i));
	}

	/**
	 * Creates a clone array.
	 * @return the new clone array.
	 */
	public Array cloneArray ( ) {
		return cloneArray(size);
	}

	/**
	 * Creates a clone array.
	 * @param new_size the size of new array.
	 * @return the new clone array.
	 */
	public Array cloneArray ( int new_size ) {
		if (new_size > size)
			new_size = size;
		if (new_size < 0)
			new_size = 0;

		Array array = new Array(new_size);
		for (int i = 0 ; i < new_size ; i++)
			array.set(i, getValueAt(i));

		return array;
	}
}
