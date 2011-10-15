/*
 * @(#)ImageConverter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image;

/**
 * The <code>ImageConverter</code> is an interface to convert an image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 June 18
 */

public interface ImageConverter {
	/**
	 * Converts an image.
	 * @param image an image.
	 * @return a new image.
	 * @exception Exception if an exception occurs.
	 */
	public MonoImage convertImage ( MonoImage image )
		throws Exception;
}
