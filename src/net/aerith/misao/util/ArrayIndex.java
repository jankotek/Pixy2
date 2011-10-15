/*
 * @(#)ArrayIndex.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>ArrayIndex</code>
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 May 15
 */

public class ArrayIndex {
	/**
	 * The buffer of index.
	 */
	protected int index[];

	/**
	 * The size of buffer.
	 */
	protected int size;

	/**
	 * Constructs an <code>ArrayIndex</code> with a specified size of 
	 * buffer. The buffer is initialized so that i-th index indicates
	 * the i-th location.
	 * @param buffer_size the size of buffer to create.
	 */
	public ArrayIndex ( int buffer_size ) {
		size = buffer_size;

		index = new int[size];

		for (int i = 0 ; i < size ; i++)
			index[i] = i;
    }

	/**
	 * Gets the indicated location of the specified index.
	 * @param i the index.
	 * @return the indicated location.
	 */
	public int get ( int i ) {
		return index[i];
	}

	/**
	 * Swaps two indices.
	 * @param i one of the indices to swap.
	 * @param j one of the indices to swap.
	 */
	public void swap ( int i, int j ) {
		int k = index[i];
		index[i] = index[j];
		index[j] = k;
	}
}
