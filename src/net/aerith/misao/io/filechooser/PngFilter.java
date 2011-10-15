/*
 * @(#)PngFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io.filechooser;
import java.io.*;
import java.net.*;

import net.aerith.misao.image.io.Format;
import net.aerith.misao.image.io.Png;
import net.aerith.misao.image.UnsupportedFileTypeException;

/**
 * The <code>PngFilter</code> represents a <code>FileFilter</code> to
 * select PNG files.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class PngFilter extends ImageIOFileFilter {
	/**
	 * Constructs a <code>PngFilter</code>.
	 */
	public PngFilter ( ) {
	}

	/**
	 * Returns the description of this filter.
	 * @return the description of this filter.
	 */
	public String getDescription ( ) {
		return "PNG images";
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
		return new Png(file.toURI().toURL());
	}

	/**
	 * Gets the format ID recorded in an XML document.
	 * @return the format ID recorded in an XML document.
	 */
	public String getFormatID ( ) {
		return "png";
	}

	/**
	 * Gets the typical file extension.
	 * @return the typical file extension.
	 */
	public String getTypicalExtension ( ) {
		return "png";
	}
}
