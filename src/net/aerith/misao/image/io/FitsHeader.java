/*
 * @(#)FitsHeader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.io;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.image.*;
import net.aerith.misao.util.*;

/**
 * The <code>FitsHeader</code> represents the keys and values of the
 * FITS header.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 May 10
 */

public class FitsHeader {
	/**
	 * The header string.
	 */
	protected String header_string;

	/**
	 * The hash table of FITS header keys.
	 */
	protected Properties properties;

	/**
	 * Constructs an empty <code>FitsHeader</code>.
	 */
	protected FitsHeader ( ) {
		header_string = "";
		properties = new Properties();
	}

	/**
	 * Constructs a simple <code>FitsHeader</code> of the specified 
	 * image. The type of FITS file is properly determined based on 
	 * the buffer type. It only supports BITPIX 8, 16 and 32 bits 
	 * images.
	 * @param image the image.
	 * @exception UnsupportedBufferTypeException if the data type is 
	 * unsupported.
	 */
	public FitsHeader ( MonoImage image )
		throws UnsupportedBufferTypeException
	{
		header_string = "";
		properties = new Properties();

		int bitpix = 8;
		if (image.getBufferType() == MonoImage.TYPE_BYTE) {
			bitpix = 8;
		} else if (image.getBufferType() == MonoImage.TYPE_SHORT) {
			bitpix = 16;
		} else if (image.getBufferType() == MonoImage.TYPE_INT) {
			bitpix = 32;
		} else {
			throw new UnsupportedBufferTypeException();
		}

		header_string += "SIMPLE  =                    T                                                  ";
		properties.setProperty("SIMPLE", "T");

		header_string += "BITPIX  = " + net.aerith.misao.util.Format.formatInt(bitpix, 20) + "                                                  ";
		properties.setProperty("BITPIX", net.aerith.misao.util.Format.formatInt(bitpix, 20));

		header_string += "NAXIS   =                    2                                                  ";
		properties.setProperty("NAXIS", "2");

		header_string += "NAXIS1  = " + net.aerith.misao.util.Format.formatInt(image.getSize().getWidth(), 20) + "                                                  ";
		properties.setProperty("NAXIS1", net.aerith.misao.util.Format.formatInt(image.getSize().getWidth(), 20));

		header_string += "NAXIS2  = " + net.aerith.misao.util.Format.formatInt(image.getSize().getHeight(), 20) + "                                                  ";
		properties.setProperty("NAXIS2", net.aerith.misao.util.Format.formatInt(image.getSize().getHeight(), 20));

		if (bitpix == 16) {
			// Short type buffer keeps value as unsigned, but in FITS 
			// file, short value is considered as signed.
			image.setSigned();
			header_string += "BZERO   =              32768.0                                                  ";
			properties.setProperty("BZERO", "32768.0");
		} else {
			header_string += "BZERO   =                  0.0                                                  ";
			properties.setProperty("BZERO", "0.0");
		}

		header_string += "BSCALE  =                  1.0                                                  ";
		properties.setProperty("BSCALE", "1.0");

		header_string += "END                                                                             ";

		for (int i = 0 ; i < 28 ; i++)
			header_string += "                                                                                ";
	}

	/**
	 * Gets the value of the specified key.
	 * @param key the key.
	 * @return the value of the specified key.
	 */
	public String getProperty ( String key ) {
		return properties.getProperty(key);
	}

	/**
	 * Reads the header of FITS file and creates a new 
	 * <code>FitsHeader</code>.
	 * @param input the stream for data input.
	 * @return the FITS header.
	 * @exception IOException if I/O error occurs.
	 */
	public static FitsHeader read ( DataInput input )
		throws IOException
	{
		FitsHeader header = new FitsHeader();

		byte[] buffer = new byte[80];
		int lines = 0;

		while (true) {
			input.readFully(buffer, 0, 80);
			header.header_string += new String(buffer);
			lines++;

			String s = new String(buffer);
			if (s.trim().equals("END")) {
				int blocks = lines / 36;
				int rest = 36 - (lines - blocks * 36);
				if (rest < 36) {
					for (int i = 0 ; i < rest ; i++) {
						input.readFully(buffer, 0, 80);
						header.header_string += new String(buffer);
					}
				}
				return header;
			}

			if (s.charAt(8) == '=') {
				String key = s.substring(0,8).trim();
				String value = s.substring(9,30).trim();
				if (key.length() > 0  &&  value.length() > 0)
					header.properties.setProperty(key, value);
			}
		}
	}

	/**
	 * Writes the FITS header.
	 * @param output the output target.
	 * @exception IOException if I/O error occurs.
	 */
	public void write ( DataOutput output )
		throws IOException
	{
		output.writeBytes(header_string);
	}
}
