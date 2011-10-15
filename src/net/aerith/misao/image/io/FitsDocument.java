/*
 * @(#)FitsDocument.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.io;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>FitsDocument</code> is a set of the image buffer and the
 * FITS header.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 May 10
 */

public class FitsDocument {
	/**
	 * The image buffer.
	 */
	protected MonoImage image;

	/**
	 * The FITS header.
	 */
	protected FitsHeader header;

	/**
	 * Constructs a <code>FitsDocument</code>.
	 * @param image  the image buffer.
	 * @param header the FITS header.
	 */
	public FitsDocument ( MonoImage image, FitsHeader header ) {
		this.image = image;
		this.header = header;
	}

	/**
	 * Gets the image buffer.
	 * @return the image buffer.
	 */
	public MonoImage getImage ( ) {
		return image;
	}

	/**
	 * Gets the FITS header.
	 * @return the FITS header.
	 */
	public FitsHeader getHeader ( ) {
		return header;
	}
}
