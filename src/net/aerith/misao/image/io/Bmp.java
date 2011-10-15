/*
 * @(#)Bmp.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.io;
import java.net.*;

/**
 * The <code>Bmp</code> is a class to read and save BMP file. It is
 * just an access interface to the
 * file.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class Bmp extends ImageIOFormat {
	/**
	 * Constructs a <code>Bmp</code> with URL.
	 * @param url the URL of the BMP file.
	 */
	public Bmp ( URL url ) {
		this.url = url;
	}

	/**
	 * Gets the name of the image format.
	 * @return the name of the image format.
	 */
	public String getName ( ) {
		return "BMP";
	}

	/**
	 * Gets the MIME type ID.
	 * @return the MIME type ID.
	 */
	public String getMimeType ( ) {
		return "image/bmp";
	}
}
