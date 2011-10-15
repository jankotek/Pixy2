/*
 * @(#)SbigFilter.java
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
import net.aerith.misao.io.Decoder;

/**
 * The <code>SbigFilter</code> represents a <code>FileFilter</code>
 * to select SBIG files.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class SbigFilter extends ImageFileFilter {
	/**
	 * Constructs a <code>SbigFilter</code>.
	 */
	public SbigFilter ( ) {
	}

	/**
	 * Gets the file name without extension.
	 * @param file the image file.
	 * @return the file name without extension.
	 */
	public String getTruncatedFilename ( File file ) {
		String filename = file.getName();
		String filename_orig = filename;

		if (file.isFile()) {
			int p = filename.lastIndexOf('.');
			if (p >= 0) {
				int period_index = p;

				String ext = filename.substring(p+1);

				if (accept_compressed) {
					String[] compressed_exts = Decoder.getCompressedExtensions();
					for (int j = 0 ; j < compressed_exts.length ; j++) {
						if (ext.equalsIgnoreCase(compressed_exts[j])) {
							filename = filename.substring(0, p);
							p = filename.lastIndexOf('.');
							if (p >= 0) {
								ext = filename.substring(p+1);
								period_index = p;
							}
							break;
						}
					}
				}

				if (ext.length() == 3) {
					if (ext.substring(0, 2).equalsIgnoreCase("ST")  &&
						'0' <= ext.charAt(2)  &&  ext.charAt(2) <= '9')
						return filename.substring(0, p);
					if ('0' <= ext.charAt(0)  &&  ext.charAt(0) <= '9'  &&
						'0' <= ext.charAt(1)  &&  ext.charAt(1) <= '9'  &&
						'0' <= ext.charAt(2)  &&  ext.charAt(2) <= '9')
						return filename.substring(0, p);
				}
			}
		}

		return filename_orig;
	}

	/**
	 * Returns the description of this filter.
	 * @return the description of this filter.
	 */
	public String getDescription ( ) {
		return "SBIG images";
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
		return new Sbig(file.toURI().toURL());
	}

	/**
	 * Gets the format ID recorded in an XML document.
	 * @return the format ID recorded in an XML document.
	 */
	public String getFormatID ( ) {
		return "sbig";
	}

	/**
	 * Gets the typical file extension.
	 * @return the typical file extension.
	 */
	public String getTypicalExtension ( ) {
		return "st8";
	}
}
