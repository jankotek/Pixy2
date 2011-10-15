/*
 * @(#)RawGrayFilter.java
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
 * The <code>RawGrayFilter</code> represents a <code>FileFilter</code>
 * to select 8-bit raw gray image files.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class RawGrayFilter extends ImageFileFilter {
	/**
	 * Constructs a <code>RawGrayFilter</code>.
	 */
	public RawGrayFilter ( ) {
	}

	/**
	 * Returns the description of this filter.
	 * @return the description of this filter.
	 */
	public String getDescription ( ) {
		return "8-bit raw gray images";
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
		return new RawGray(file.toURI().toURL());
	}

	/**
	 * Gets the format ID recorded in an XML document.
	 * @return the format ID recorded in an XML document.
	 */
	public String getFormatID ( ) {
		return "raw";
	}

	/**
	 * Gets the typical file extension.
	 * @return the typical file extension.
	 */
	public String getTypicalExtension ( ) {
		return "raw";
	}
}
