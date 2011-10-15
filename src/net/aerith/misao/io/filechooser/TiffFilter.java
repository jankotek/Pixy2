/*
 * @(#)TiffFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io.filechooser;
import java.io.*;
import java.net.*;
import javax.swing.filechooser.FileFilter;
import net.aerith.misao.image.io.Format;
import net.aerith.misao.image.io.Tiff;
import net.aerith.misao.image.UnsupportedFileTypeException;

/**
 * The <code>TiffFilter</code> represents a <code>FileFilter</code> to
 * select TIFF files.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class TiffFilter extends JimiFileFilter {
	/**
	 * Constructs a <code>TiffFilter</code>.
	 */
	public TiffFilter ( ) {
	}

	/**
	 * Returns the acceptable extensions.
	 * @return the acceptable extensions.
	 */
	public String[] getAcceptableExtensions ( ) {
		String[] exts = new String[2];
		exts[0] = "tif";
		exts[1] = "tiff";
		return exts;
	}

	/**
	 * Returns the description of this filter.
	 * @return the description of this filter.
	 */
	public String getDescription ( ) {
		return "TIFF images";
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
		return new Tiff(file.toURL());
	}

	/**
	 * Gets the format ID recorded in an XML document.
	 * @return the format ID recorded in an XML document.
	 */
	public String getFormatID ( ) {
		return "tiff";
	}

	/**
	 * Gets the typical file extension.
	 * @return the typical file extension.
	 */
	public String getTypicalExtension ( ) {
		return "tif";
	}
}
