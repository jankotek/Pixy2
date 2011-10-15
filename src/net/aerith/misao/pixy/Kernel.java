/*
 * @(#)Kernel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy;

/**
 * The <code>Agent</code> is an interface of kernel to run the PIXY
 * System 2.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 17
 */

public interface Kernel {
	/**
	 * Returns the name of this kernel.
	 * @return the name of this kernel.
	 */
	public abstract String getName ( );

	/**
	 * Runs the PIXY System 2.
	 * @param args the options.
	 * @exception Exception if an exception occurs.
	 */
	public abstract void run ( String[] args )
		throws Exception;
}
