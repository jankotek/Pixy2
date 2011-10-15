/*
 * @(#)Ppm.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.io;
import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import net.aerith.misao.image.*;
import net.aerith.misao.io.*;
import net.aerith.misao.io.filechooser.*;
import net.aerith.misao.util.Size;

/**
 * The <code>Ppm</code> is a class to read and save ppm file. It is
 * just an access interface to ppm file.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class Ppm extends Format implements Bitmap {
	/**
	 * Constructs an <code>Ppm</code> with URL.
	 * @param url the URL of the ppm file.
	 */
	public Ppm ( URL url ) {
		this.url = url;
	}

	/**
	 * Gets the name of the image format.
	 * @return the name of the image format.
	 */
	public String getName ( ) {
		return "PPM";
	}

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
	public MonoImage read ( )
		throws IOException, UnsupportedBufferTypeException, UnsupportedFileTypeException
	{
		if (url == null)
			throw new IOException();

		String s = "";
		byte[] b = new byte[100];

		DataInputStream stream = Decoder.newInputStream(url);

		for (int i = 0 ; i < 100 ; i++) {
			b[i] = stream.readByte();
			if (b[i] == '\n') {
				s = new String(b, 0, i);
				break;
			}
		}
		if (s.equals("P5") == false)
			throw new UnsupportedBufferTypeException();

		for (int i = 0 ; i < 100 ; i++) {
			b[i] = stream.readByte();
			if (b[i] == '\n') {
				s = new String(b, 0, i);
				break;
			}
		}
		StringTokenizer st = new StringTokenizer(s);
		int width = Integer.parseInt(st.nextToken());
		int height = Integer.parseInt(st.nextToken());
		
		for (int i = 0 ; i < 100 ; i++) {
			b[i] = stream.readByte();
			if (b[i] == '\n') {
				s = new String(b, 0, i);
				break;
			}
		}
		if (s.equals("255") == false)
			throw new UnsupportedBufferTypeException();

		Size size = new Size(width, height);

		MonoImage image = new MonoImage();
		image.readByteImage(stream, size);

		stream.close();

		return image;
	}

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
	public void save ( MonoImage image )
		throws IOException, UnsupportedBufferTypeException, UnsupportedFileTypeException
	{
		if (url == null)
			throw new IOException();

		if (image.getBufferType() != MonoImage.TYPE_BYTE)
			throw new UnsupportedBufferTypeException();

		DataOutputStream stream = Encoder.newOutputStream(url);

		String s = "";
		byte[] b = null;

		s = "P5";
		try {
			b = s.getBytes("ASCII");
		} catch ( UnsupportedEncodingException exception ) {
			b = s.getBytes();
		}
		stream.write(b, 0, s.length());
		stream.writeByte('\n');
		
		s = "" + image.getSize().getWidth() + " " + image.getSize().getHeight();
		try {
			b = s.getBytes("ASCII");
		} catch ( UnsupportedEncodingException exception ) {
			b = s.getBytes();
		}
		stream.write(b, 0, s.length());
		stream.writeByte('\n');
		
		s = "255";
		try {
			b = s.getBytes("ASCII");
		} catch ( UnsupportedEncodingException exception ) {
			b = s.getBytes();
		}
		stream.write(b, 0, s.length());
		stream.writeByte('\n');
		
		image.write(stream);

		stream.close();
	}

	/**
	 * Saves an image buffer into an image file, using the specified
	 * <code>LevelAdjustmentSet</code>. The url of the image file 
	 * must be set previously.
	 * @param image the monochrome image buffer to save.
	 * @exception IOException if I/O error occurs.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	public void save ( MonoImage image, LevelAdjustmentSet set )
		throws IOException, UnsupportedFileTypeException
	{
		if (url == null)
			throw new IOException();

		ByteBuffer buffer = new ByteBuffer(image.getSize());
		MonoImage byte_image = new MonoImage(buffer);

		for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				int value = (int)((image.getValue(x, y) - set.current_minimum) / (set.current_maximum - set.current_minimum) * 256);
				if (value < 0)
					value = 0;
				if (value > 255)
					value = 255;
				byte_image.setValue(x, y, value);
			}
		}

		try {
			save(byte_image);
		} catch ( UnsupportedBufferTypeException exception ) {
			// Never happens.
		}
	}
}
