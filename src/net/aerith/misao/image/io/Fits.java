/*
 * @(#)Fits.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.io;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.Size;
import net.aerith.misao.image.*;
import net.aerith.misao.io.*;
import net.aerith.misao.pixy.Resource;

/**
 * The <code>Fits</code> is a class to read and save FITS file. It is
 * just an access interface to FITS file.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 April 13
 */

public class Fits extends Format {
	/**
	 * Constructs a <code>Fits</code> with URL.
	 * @param url the URL of the FITS file.
	 */
	public Fits ( URL url ) {
		this.url = url;
	}

	/**
	 * Gets the name of the image format.
	 * @return the name of the image format.
	 */
	public String getName ( ) {
		return "FITS";
	}

	/**
	 * Returns true if the format is FITS.
	 * @return true if the format is FITS.
	 */
	public boolean isFits ( ) {
		return true;
	}

	/**
	 * True if the 16-bit FITS image contains signed data.
	 * @return true if the 16-bit FITS image contains signed data.
	 */
	protected boolean signed ( ) {
		return true;
	}

	/**
	 * Reads FITS file and creates image buffer. The url of the FITS
	 * file must be set previously. It only supports BITPIX 8, 16 and
	 * 32 bits images.
	 * @return the monochrome image buffer.
	 * @exception IOException if I/O error occurs.
	 * @exception UnsupportedBufferTypeException if the data type is 
	 * unsupported.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	public MonoImage read ( )
		throws IOException, UnsupportedBufferTypeException, UnsupportedFileTypeException
	{
		FitsDocument document = readDocument();
		return document.getImage();
	}

	/**
	 * Reads FITS file and creates the FITS document. The url of the 
	 * FITS file must be set previously. It only supports BITPIX 8, 16
	 * and 32 bits images.
	 * @return the FITS document.
	 * @exception IOException if I/O error occurs.
	 * @exception UnsupportedBufferTypeException if the data type is 
	 * unsupported.
	 */
	public FitsDocument readDocument ( )
		throws IOException, UnsupportedBufferTypeException
	{
		if (url == null)
			throw new IOException();

		DataInputStream stream = Decoder.newInputStream(url);

		FitsHeader header = FitsHeader.read(stream);

		Size size = new Size(Integer.parseInt(header.getProperty("NAXIS1")), 
							 Integer.parseInt(header.getProperty("NAXIS2")));

		MonoImage image = new MonoImage();

		int bitpix = Integer.parseInt(header.getProperty("BITPIX"));
		if (Math.abs(bitpix) == 8) {
			image.readByteImage(stream, size);
		} else if (Math.abs(bitpix) == 16) {
			// Short type buffer keeps value as unsigned, but in FITS 
			// file, short value is considered as signed.
			if (signed())
				image.setSigned();
			image.readShortImage(stream, size);
		} else if (bitpix == 32) {
			image.readIntImage(stream, size);
		} else if (bitpix == -32) {
			image.readFloatImage(stream, size);
		} else {
			throw new UnsupportedBufferTypeException();
		}

		double bzero = 0.0;
		double bscale = 1.0;
		if (header.getProperty("BZERO") != null)
			bzero = net.aerith.misao.util.Format.doubleValueOf(header.getProperty("BZERO"));
		if (header.getProperty("BSCALE") != null)
			bscale = net.aerith.misao.util.Format.doubleValueOf(header.getProperty("BSCALE"));
		if (bscale == 0)
			bscale = 1;
		image.setPixelValueConvertParameters(bzero, bscale);

		stream.close();

		// Reverses upside down in the case the FITS data order is
		// configured to be world standard.
		if (Resource.getDefaultFitsOrder().equals("standard"))
			image.reverseVertically();

		return new FitsDocument(image, header);
	}

	/**
	 * Saves image buffer into a FITS file. The url of the FITS file 
	 * must be set previously. The type of FITS file is properly 
	 * determined based on the buffer type. It only supports BITPIX 8,
	 * 16 and 32 bits images.
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
		FitsHeader header = new FitsHeader(image);
		saveDocument(new FitsDocument(image, header));
	}

	/**
	 * Saves the FITS document into a FITS file. The url of the FITS
	 * file must be set previously.
	 * @param document the FITS document.
	 * @exception IOException if I/O error occurs.
	 * @exception UnsupportedBufferTypeException if the data type is 
	 * unsupported.
	 */
	public void saveDocument ( FitsDocument document )
		throws IOException, UnsupportedBufferTypeException
	{
		if (url == null)
			throw new IOException();

		DataOutputStream stream = Encoder.newOutputStream(url);

		document.getHeader().write(stream);

		document.getImage().write(stream);

		stream.close();
	}
}
