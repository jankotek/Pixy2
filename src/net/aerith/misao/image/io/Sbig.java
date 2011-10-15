/*
 * @(#)Sbig.java
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

/**
 * The <code>Sbig</code> is a class to read and save SBIG file. It is
 * just an access interface to SBIG file.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 April 13
 */

public class Sbig extends Format {
	/**
	 * The number which represents the next two bytes show the pixel
	 * value.
	 */
	protected final static int READ_VALUE = 1;

	/**
	 * The number which represents the next one byte show the 
	 * difference of the pixel value.
	 */
	protected final static int READ_DIFFERENCE = 2;

	/**
	 * Constructs a <code>Sbig</code> with URL.
	 * @param url the URL of the SBIG file.
	 */
	public Sbig ( URL url ) {
		this.url = url;
	}

	/**
	 * Gets the name of the image format.
	 * @return the name of the image format.
	 */
	public String getName ( ) {
		return "SBIG";
	}

	/**
	 * Reads SBIG file and creates image buffer. The url of the SBIG
	 * file must be set previously.
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
		if (url == null)
			throw new IOException();

		DataInputStream stream = Decoder.newInputStream(url);

		Header header = readHeader(stream);

		Size size = new Size(Integer.parseInt(header.getProperty("Width")), 
							 Integer.parseInt(header.getProperty("Height")));

		ShortBuffer buffer = new ShortBuffer(size);

		if (header.isCompressed()) {
			byte b_word[] = new byte[2];
			byte b_word2[] = new byte[2];

			for (int y = 0 ; y < size.getHeight() ; y++) {
				stream.readFully(b_word, 0, 2);
				b_word2[0] = b_word[1];
				b_word2[1] = b_word[0];
				int width = net.aerith.misao.util.Format.intValueOf(b_word2, 0, 2);

				byte b_array[] = new byte[width];

				stream.readFully(b_array, 0, width);

				if (width == size.getWidth() * 2) {
					// Not compressed.
					for (int x = 0 ; x < size.getWidth() ; x++) {
						b_word[0] = b_array[x * 2 + 1];
						b_word[1] = b_array[x * 2];
						int g = net.aerith.misao.util.Format.intValueOf(b_word, 0, 2);
						buffer.set(x, y, g);
					}
				} else {
					int pointer = 0;
					int state = READ_VALUE;
					int x = 0;
					int last_value = 0;
					while (x < size.getWidth()) {
						if (state == READ_VALUE) {
							b_word[0] = b_array[pointer + 1];
							b_word[1] = b_array[pointer];
							int g = net.aerith.misao.util.Format.intValueOf(b_word, 0, 2);
							buffer.set(x, y, g);

							pointer += 2;
							x++;
							state = READ_DIFFERENCE;
							last_value = g;
						} else {
							if (b_array[pointer] == -128) {
								pointer++;
								state = READ_VALUE;
							} else {
								int g = (int)b_array[pointer];
								buffer.set(x, y, last_value + g);

								pointer++;
								x++;
								state = READ_DIFFERENCE;
								last_value += g;
							}
						}
					}
				}
			}
		} else {
			byte b_array[] = new byte[size.getWidth() * 2];
			byte b_word[] = new byte[2];

			for (int y = 0 ; y < size.getHeight() ; y++) {
				stream.readFully(b_array, 0, size.getWidth() * 2);

				for (int x = 0 ; x < size.getWidth() ; x++) {
					b_word[0] = b_array[x * 2 + 1];
					b_word[1] = b_array[x * 2];
					int g = net.aerith.misao.util.Format.intValueOf(b_word, 0, 2);
					buffer.set(x, y, g);
				}
			}
		}

		MonoImage image = new MonoImage(buffer);

		stream.close();

		return image;
	}

	/**
	 * Reads header of SBIG file and creates a hash table.
	 * @param input the stream for data input.
	 * @return a hash table of SBIG header keys.
	 * @exception IOException if I/O error occurs.
	 */
	private Header readHeader ( DataInput input )
		throws IOException
	{
		Properties properties = new Properties();
		Header header = new Header(properties);

		byte buffer[] = new byte[2048];
		input.readFully(buffer, 0, 2048);

		// Reads until ^Z(=26) appears.
		int p = 0;
		while (buffer[p] != 26)
			p++;

		StringBuffer sb = new StringBuffer();
		for (int i = 0 ; i < p ; i++)
			sb.append((char)buffer[i]);
		String strings = sb.toString();

		StringTokenizer st = new StringTokenizer(strings, "\n\r");
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			StringTokenizer st2 = new StringTokenizer(str, "=");
			if (st2.hasMoreTokens()) {
				String key = st2.nextToken().trim();
				if (st2.hasMoreTokens()) {
					String value = st2.nextToken().trim();
					properties.setProperty(key, value);
				} else {
					if (key.substring(0, 3).equals("ST-")) {
						if (key.indexOf("Compressed") > 0)
							header.setCompressed();
					}
				}
			}
		}

		return header;
	}

	/**
	 * Saves image buffer into a SBIG file. The url of the SBIG file 
	 * must be set previously.
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
		throw new UnsupportedFileTypeException(url, getName(), "save");
	}

	/**
	 * The <code>Header</code> is a set of the hash table of SBIG 
	 * header keys and the flag whether if the image is compressed or
	 * not.
	 */
	protected class Header {
		/**
		 * The hash table of SBIG header keys.
		 */
		protected Properties properties;

		/**
		 * True if the image is compressed.
		 */
		protected boolean compressed = false;

		/**
		 * Constructs a <code>Header</code>.
		 * @param properties the hash table of SBIG header keys.
		 */
		public Header ( Properties properties ) {
			this.properties = properties;
		}

		/**
		 * Set the flag as the image is compressed.
		 */
		public void setCompressed ( ) {
			compressed = true;
		}

		/**
		 * Returns if the image is compressed or not.
		 * @return true if the image is compressed.
		 */
		public boolean isCompressed ( ) {
			return compressed;
		}

		/**
		 * Gets the value of the specified key.
		 * @param key the key.
		 * @return the value of the specified key.
		 */
		public String getProperty ( String key ) {
			return properties.getProperty(key);
		}
	}
}
