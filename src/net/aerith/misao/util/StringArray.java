/*
 * @(#)StringArray.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>StringArray</code> represents an array of string data 
 * with a funciton to sort.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 January 27
 */

public class StringArray extends SortableArray {
	/**
	 * The buffer of data array.
	 */
	protected String[] array;

	/**
	 * The size of buffer.
	 */
	protected int size;

	/**
	 * Constructs a <code>StringArray</code> with a specified size of 
	 * buffer. The each value of buffer is set as an empty string.
	 * @param buffer_size the size of buffer to create.
	 */
	public StringArray ( int buffer_size ) {
		size = buffer_size;
		if (size > 0) {
			array = new String[size];

			for (int i = 0 ; i < size ; i++)
				array[i] = "";
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
	public String getValueAt ( int index )
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
	public void set ( int index, String value )
		throws IndexOutOfBoundsException
	{
		array[index] = value;
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
		int answer = array[index1].compareTo(array[index2]);
		if (answer < 0)
			return COMPARE_SMALLER;
		if (answer > 0)
			return COMPARE_LARGER;
		return COMPARE_EQUAL;
	}

	/**
	 * Sorts this array itself based on the sorted index. It is 
	 * invoked in the sorting functions.
	 */
	protected void sort ( ) {
		StringArray work_array = new StringArray(size);
		for (int i = 0 ; i < size ; i++)
			work_array.set(i, getValueAt(sort_index.get(i)));
		for (int i = 0 ; i < size ; i++)
			set(i, work_array.getValueAt(i));
	}
}
