/*
 * @(#)Statisticable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>Statisticable</code> is an interface which has data
 * accessor methods needed to calculate statistics of data.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 June 24
 */

public interface Statisticable {
	/**
	 * Gets the number of data.
	 * @return the number of data.
	 */
	public abstract int getDataCount();

	/**
	 * Gets the value of data at the specified location.
	 * @param index the location of the data.
	 * @return the value of data at the specified location.
	 * @exception IndexOutOfBoundsException if specified location is
	 * out of the data buffer.
	 */
	public abstract double getValueAt ( int index )
		throws IndexOutOfBoundsException;
}
