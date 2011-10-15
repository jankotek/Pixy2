/*
 * @(#)Jpeg.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.io;
import java.io.*;
import java.net.*;
import net.aerith.misao.image.*;
import net.aerith.misao.io.filechooser.*;

/**
 * The <code>Jpeg</code> is a class to read and save JPEG file. It is
 * just an access interface to the
 * file.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class Jpeg extends JimiFormat {
	/**
	 * Constructs a <code>Jpeg</code> with URL.
	 * @param url the URL of the JPEG file.
	 */
	public Jpeg ( URL url ) {
		this.url = url;
	}

	/**
	 * Gets the name of the image format.
	 * @return the name of the image format.
	 */
	public String getName ( ) {
		return "JPEG";
	}

	/**
	 * Gets the MIME type ID.
	 * @return the MIME type ID.
	 */
	public String getMimeType ( ) {
		return "image/jpeg";
	}
}
