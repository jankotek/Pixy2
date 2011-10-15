/*
 * @(#)GifFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io.filechooser;
import java.io.*;
import java.net.*;

import net.aerith.misao.image.io.Format;
import net.aerith.misao.image.io.Gif;
import net.aerith.misao.image.UnsupportedFileTypeException;

/**
 * The <code>GifFilter</code> represents a <code>FileFilter</code> to
 * select GIF files.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class GifFilter extends ImageIOFileFilter {
	/**
	 * Constructs a <code>GifFilter</code>.
	 */
	public GifFilter ( ) {
	}

	/**
	 * Returns the description of this filter.
	 * @return the description of this filter.
	 */
	public String getDescription ( ) {
		return "GIF images";
	}

	/**
	 * Gets the proper format for the image type.
	 * @return the image file format.
	 * @exception MalformedURLException if an unknown protocol is 
	 * specified.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	public Format getFormat ( File file )
		throws MalformedURLException, UnsupportedFileTypeException
	{
		return new Gif(file.toURI().toURL());
	}

	/**
	 * Gets the format ID recorded in an XML document.
	 * @return the format ID recorded in an XML document.
	 */
	public String getFormatID ( ) {
		return "gif";
	}

	/**
	 * Gets the typical file extension.
	 * @return the typical file extension.
	 */
	public String getTypicalExtension ( ) {
		return "gif";
	}
}
