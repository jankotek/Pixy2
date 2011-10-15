/*
 * @(#)Tiff.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.io;
import java.io.*;
import java.net.*;
import net.aerith.misao.image.*;

/**
 * The <code>Tiff</code> is a class to read and save TIFF file. It is
 * just an access interface to the
 * file.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class Tiff extends ImageIOFormat {
	/**
	 * Constructs a <code>Tiff</code> with URL.
	 * @param url the URL of the TIFF file.
	 */
	public Tiff ( URL url ) {
		this.url = url;
	}

	/**
	 * Gets the name of the image format.
	 * @return the name of the image format.
	 */
	public String getName ( ) {
		return "TIFF";
	}

	/**
	 * Gets the MIME type ID.
	 * @return the MIME type ID.
	 */
	public String getMimeType ( ) {
		return "image/tiff";
	}

	/**
	 * Saves image buffer into an image file. The url of the image file 
	 * must be set previously.
	 * @param image the monochrome image buffer to save.
	 * @exception IOException if I/O error occurs.
	 * @exception UnsupportedBufferTypeException if the data type is 
	 * unsupported.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	public void save ( MonoImage image )
		throws IOException, UnsupportedBufferTypeException, UnsupportedFileTypeException
	{
		throw new UnsupportedFileTypeException(url, getName(), "save");
	}

	/**
	 * Saves an image buffer into an image file, using the specified
	 * <code>LevelAdjustmentSet</code>. The url of the image file 
	 * must be set previously.
	 * @param image the monochrome image buffer to save.
	 * @exception IOException if I/O error occurs.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	public void save ( MonoImage image, LevelAdjustmentSet set )
		throws IOException, UnsupportedFileTypeException
	{
		throw new UnsupportedFileTypeException(url, getName(), "save");
	}
}
