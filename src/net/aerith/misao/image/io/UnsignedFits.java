/*
 * @(#)UnsignedFits.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.io;
import java.net.*;

/**
 * The <code>UnsignedFits</code> is a class to read and save unsigned 
 * FITS file. It is just an access interface to unsigned FITS file.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 June 3
 */

public class UnsignedFits extends Fits {
	/**
	 * Constructs an <code>UnsignedFits</code> with URL.
	 * @param url the URL of the FITS file.
	 */
	public UnsignedFits ( URL url ) {
		super(url);
	}

	/**
	 * Gets the name of the image format.
	 * @return the name of the image format.
	 */
	public String getName ( ) {
		return "Unsigned FITS";
	}

	/**
	 * True if the 16-bit FITS image contains signed data.
	 * @return true if the 16-bit FITS image contains signed data.
	 */
	protected boolean signed ( ) {
		return false;
	}
}
