/*
 * @(#)UnsignedFitsFilter.java
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
 * The <code>UnsignedFitsFilter</code> represents a 
 * <code>FileFilter</code> to select unsigned 16-bit FITS files.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class UnsignedFitsFilter extends FitsFilter {
	/**
	 * Constructs an <code>UnsignedFitsFilter</code>.
	 */
	public UnsignedFitsFilter ( ) {
	}

	/**
	 * Returns the description of this filter.
	 * @return the description of this filter.
	 */
	public String getDescription ( ) {
		return "Unsigned FITS images";
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
		return new UnsignedFits(file.toURI().toURL());
	}

	/**
	 * Gets the format ID recorded in an XML document.
	 * @return the format ID recorded in an XML document.
	 */
	public String getFormatID ( ) {
		return "unsigned-fits";
	}
}
