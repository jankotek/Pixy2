/*
 * @(#)FitsFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io.filechooser;
import java.io.File;
import java.net.*;
import javax.swing.filechooser.FileFilter;
import net.aerith.misao.image.io.*;
import net.aerith.misao.image.UnsupportedFileTypeException;

/**
 * The <code>FitsFilter</code> represents a <code>FileFilter</code>
 * to select FITS files.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class FitsFilter extends ImageFileFilter {
	/**
	 * Constructs a <code>FitsFilter</code>.
	 */
	public FitsFilter ( ) {
	}

	/**
	 * Returns the acceptable extensions.
	 * @return the acceptable extensions.
	 */
	public String[] getAcceptableExtensions ( ) {
		String[] exts = new String[3];
		exts[0] = "fts";
		exts[1] = "fit";
		exts[2] = "fits";
		return exts;
	}

	/**
	 * Returns the description of this filter.
	 * @return the description of this filter.
	 */
	public String getDescription ( ) {
		return "FITS images";
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
		return new Fits(file.toURI().toURL());
	}

	/**
	 * Gets the format ID recorded in an XML document.
	 * @return the format ID recorded in an XML document.
	 */
	public String getFormatID ( ) {
		return "fits";
	}

	/**
	 * Gets the typical file extension.
	 * @return the typical file extension.
	 */
	public String getTypicalExtension ( ) {
		return "fts";
	}
}
