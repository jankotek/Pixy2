/*
 * @(#)Format.java
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
 * The <code>Format</code> is an abstract class to read and save image
 * file. It is just an access interface to the file.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public abstract class Format {
	/**
	 * The url of the image file.
	 */
	protected URL url;

	/**
	 * Sets the url of the image file.
	 * @param url the url of the image file.
	 */
	public void setURL ( URL url ) {
		this.url = url;
	}

	/**
	 * Returns true if the format is FITS.
	 * @return true if the format is FITS.
	 */
	public boolean isFits ( ) {
		return false;
	}

	/**
	 * Creates the proper image file format object based on the 
	 * specified file object.
	 * @param file the image file.
	 * @return the image file format object.
	 * @exception FileNotFoundException if the file does not exist.
	 * @exception MalformedURLException if an unknown protocol is 
	 * specified.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	public static Format create ( File file )
		throws FileNotFoundException, MalformedURLException, UnsupportedFileTypeException
	{
		if (file.exists() == false)
			throw new FileNotFoundException(file.getPath());

		// If the file extension is ".fts" or so, the FITS format
		// should be selected, not the unsigned FITS format.
		if (new FitsFilter().accept(file))
			return new Fits(file.toURI().toURL());

		ImageFileFilter[] filters = ImageFileFilter.getSupportedFilters();
		for (int i = 0 ; i < filters.length ; i++) {
			if (filters[i].accept(file))
				return filters[i].getFormat(file);
		}

		throw new UnsupportedFileTypeException(file.toURI().toURL(), "", "create format");
	}

	/**
	 * Creates the proper image file format object based on the 
	 * specified file object and the file format ID recorded in an XML
	 * document.
	 * @param file      the image file.
	 * @param format_id the file format ID.
	 * @return the image file format object.
	 * @exception FileNotFoundException if the file does not exist.
	 * @exception MalformedURLException if an unknown protocol is 
	 * specified.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	public static Format create ( File file, String format_id )
		throws FileNotFoundException, MalformedURLException, UnsupportedFileTypeException
	{
		if (format_id == null)
			return create(file);

		ImageFileFilter[] filters = ImageFileFilter.getSupportedFilters();
		for (int i = 0 ; i < filters.length ; i++) {
			if (filters[i].getFormatID().equals(format_id))
				return filters[i].getFormat(file);
		}

		return create(file);
	}

	/**
	 * Gets the file name without extension.
	 * @param file the image file.
	 * @return the file name without extension.
	 */
	public static String getTruncatedFilename ( File file ) {
		ImageFileFilter[] filters = ImageFileFilter.getSupportedFilters();
		for (int i = 0 ; i < filters.length ; i++) {
			if (filters[i].accept(file))
				return filters[i].getTruncatedFilename(file);
		}

		return file.getName();
	}

	/**
	 * Gets the name of the image format.
	 * @return the name of the image format.
	 */
	public abstract String getName ( );

	/**
	 * Reads image file and creates image buffer. The url of the image
	 * file must be set previously.
	 * @return the monochrome image buffer.
	 * @exception IOException if I/O error occurs.
	 * @exception UnsupportedBufferTypeException if the data type is 
	 * unsupported.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	public abstract MonoImage read ( )
		throws IOException, UnsupportedBufferTypeException, UnsupportedFileTypeException;

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
	public abstract void save ( MonoImage image )
		throws IOException, UnsupportedBufferTypeException, UnsupportedFileTypeException;
}
