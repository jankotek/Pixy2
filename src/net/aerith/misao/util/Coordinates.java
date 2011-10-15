/*
 * @(#)Coordinates.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>Coordinates</code> is an interface with accessor methods
 * to R.A. and Decl. The R.A. and Decl. must be expressed in degree.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 June 24
 */

public interface Coordinates {
	/**
	 * Gets R.A. 
	 * @return the R.A.
	 */
	public double getRA();

	/**
	 * Gets Decl.
	 * @return the Decl.
	 */
	public double getDecl();
}
