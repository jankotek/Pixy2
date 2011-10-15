/*
 * @(#)Png.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.io;
import java.net.*;

/**
 * The <code>Png</code> is a class to read and save PNG file. It is
 * just an access interface to the
 * file.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class Png extends ImageIOFormat {
	/**
	 * Constructs a <code>Png</code> with URL.
	 * @param url the URL of the PNG file.
	 */
	public Png ( URL url ) {
		this.url = url;
	}

	/**
	 * Gets the name of the image format.
	 * @return the name of the image format.
	 */
	public String getName ( ) {
		return "PNG";
	}

	/**
	 * Gets the MIME type ID.
	 * @return the MIME type ID.
	 */
	public String getMimeType ( ) {
		return "image/png";
	}
}
