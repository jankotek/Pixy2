/*
 * @(#)SortableArray.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>SortableArray</code> represents a virtual array of data
 * values with a function to sort data.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 May 15
 */

public abstract class SortableArray {
	/**
	 * The index of sorting result. It is used in the recurrsive 
	 * <tt>quick_sort</tt> method.
	 */
	protected ArrayIndex sort_index;

	/**
	 * The sorting direction number used in <tt>quick_sort</tt>
	 * indicating the ascending order.
	 */
	private final static int SORT_ASCENDANT = 0;

	/**
	 * The sorting direction number used in <tt>quick_sort</tt>
	 * indicating the descending order.
	 */
	private final static int SORT_DESCENDANT = 1;

	/**
	 * The number which represents the comparison result is the first
	 * data and the second data are equal.
	 */
	protected final static int COMPARE_EQUAL = 0;

	/**
	 * The number which represents the comparison result is the first
	 * data is smaller than the second data.
	 */
	protected final static int COMPARE_SMALLER = 1;

	/**
	 * The number which represents the comparison result is the first
	 * data is larger than the second data.
	 */
	protected final static int COMPARE_LARGER = 2;

	/**
	 * Gets the size of this array.
	 * @return the size of this array.
	 */
	public abstract int getArraySize();

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
	protected abstract int compare ( int index1, int index2 );

	/**
	 * Sorts this array itself based on the sorted index. It is 
	 * invoked in the sorting functions. It must be overrided in the
	 * subclasses.
	 */
	protected abstract void sort();

	/**
	 * Sorts data in ascendant order. It returns a table indicating 
	 * the original location. For example,
	 * <p><pre>
	 *     ArrayIndex index = array.sortAscendant();
	 * </pre></p>
	 * then the k-th value in the sorted buffer was originally at the
	 * index.get(k)-th location.
	 * @return the table indicating the original location.
	 */
	public ArrayIndex sortAscendant ( ) {
		sort_index = new ArrayIndex(0);

		if (getArraySize() > 0) {
			sort_index = new ArrayIndex(getArraySize());

			quick_sort(0, getArraySize() - 1, SORT_ASCENDANT);

			sort();
		}

		return sort_index;
	}

	/**
	 * Sorts data in descendant order. It returns a table indicating 
	 * the original location. For example,
	 * <p><pre>
	 *     ArrayIndex index = array.sortDescendant();
	 * </pre></p>
	 * then the k-th value in the sorted buffer was originally at the
	 * index.get(k)-th location.
	 * @return the table indicating the original location.
	 */
	public ArrayIndex sortDescendant ( ) {
		sort_index = new ArrayIndex(0);

		if (getArraySize() > 0) {
			sort_index = new ArrayIndex(getArraySize());

			quick_sort(0, getArraySize() - 1, SORT_DESCENDANT);

			sort();
		}

		return sort_index;
	}

	/**
	 * Sorts part of the data between the specified first and last 
	 * location in order of the specified direction. It is invoked
	 * recurrsively. The data in the buffer is not really changed
	 * by this process. Only <tt>sort_index</tt>, indicating the
	 * location after sorted, is changed.
	 * @param first     the first index of part to sort.
	 * @param last      the last index of part to sort.
	 * @param direction the sorting direction.
	 */
	private void quick_sort ( int first, int last, int direction ) {
		int m = sort_index.get((first + last) / 2);
		int i = first;
		int j = last;

		while (true) {
			while (i < getArraySize()  &&  ((direction == SORT_ASCENDANT  &&  compare(sort_index.get(i), m) == COMPARE_SMALLER)  ||  (direction == SORT_DESCENDANT  &&  compare(sort_index.get(i), m) == COMPARE_LARGER)))
				i++;
			while (j >= 0  &&  ((direction == SORT_ASCENDANT  &&  compare(sort_index.get(j), m) == COMPARE_LARGER)  ||  (direction == SORT_DESCENDANT  &&  compare(sort_index.get(j), m) == COMPARE_SMALLER)))
				j--;
			if (i >= j)
				break;

			sort_index.swap(i, j);

			i++;
			j--;
		}

		if (first < i - 1)
			quick_sort(first, i - 1, direction);
		if (last > j + 1)
			quick_sort(j + 1, last, direction);
	}
}
