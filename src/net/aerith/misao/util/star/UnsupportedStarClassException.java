/*
 * @(#)UnsupportedStarClassException.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util.star;

/**
 * The <code>UnsupportedMagnitudeSystemException</code> is an 
 * exception thrown if the specified star class is not supported by 
 * the catalog.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 August 21
 */

public class UnsupportedStarClassException extends Exception {
	/**
	 * The unsupported star object.
	 */
	protected CatalogStar star = null;

	/**
	 * Constructs an <code>UnsupportedStarClassException</code>.
	 * @param star the unsupported star object.
	 */
	public UnsupportedStarClassException ( CatalogStar star ) {
		this.star = star;
	}
}
