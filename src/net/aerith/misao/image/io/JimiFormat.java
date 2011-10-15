/*
 * @(#)JimiFormat.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.io;
import java.io.*;
import java.net.*;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.lang.reflect.*;
import net.aerith.misao.image.*;
import net.aerith.misao.io.filechooser.*;
import net.aerith.misao.util.Size;

/**
 * The <code>JimiFormat</code> is an abstract class to read and save 
 * image file supported by JIMI. It is just an access interface to the
 * file.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public abstract class JimiFormat extends Format implements Bitmap {
	/**
	 * Gets the MIME type ID.
	 * @return the MIME type ID.
	 */
	public abstract String getMimeType ( );

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

		try {
			Class[] arg_class = new Class[1];
			arg_class[0] = Class.forName("java.net.URL");

			Class t = Class.forName("com.sun.jimi.core.Jimi");
			Method m = t.getMethod("getRasterImage", arg_class);

			Object[] args = new Object[1];
			args[0] = url;

			Object raster_image = m.invoke(null, args);

			t = Class.forName("com.sun.jimi.core.raster.JimiRasterImage");

			m = t.getMethod("waitInfoAvailable", null);
			m.invoke(raster_image, null);
			m = t.getMethod("waitFinished", null);
			m.invoke(raster_image, null);

			m = t.getMethod("getWidth", null);
			int w = ((Integer)m.invoke(raster_image, null)).intValue();
			m = t.getMethod("getHeight", null);
			int h = ((Integer)m.invoke(raster_image, null)).intValue();

			m = t.getMethod("getColorModel", null);
			ColorModel model = (ColorModel)m.invoke(raster_image, null);

			ByteBuffer buffer = new ByteBuffer(new Size(w, h));

			arg_class = new Class[2];
			arg_class[0] = Integer.TYPE;
			arg_class[1] = Integer.TYPE;

			m = t.getMethod("getPixelRGB", arg_class);

			args = new Object[2];

			for (int y = 0 ; y < h ; y++) {
				args[1] = new Integer(y);
				for (int x = 0 ; x < w ; x++) {
					args[0] = new Integer(x);
					int value = ((Integer)m.invoke(raster_image, args)).intValue();
					int r = (value >> 16) & 255;
					int g = (value >> 8) & 255;
					int b = value & 255;
					int gray = MonoImage.convertRGBToGray(r, g, b);
					buffer.setValue(x, y, gray);
				}
			}

			return new MonoImage(buffer);
		} catch ( ClassNotFoundException exception ) {
		} catch ( NoSuchMethodException exception ) {
		} catch ( SecurityException exception ) {
		} catch ( IllegalAccessException exception ) {
		} catch ( IllegalArgumentException exception ) {
		} catch ( InvocationTargetException exception ) {
		} catch ( Exception exception ) {
		}

		throw new UnsupportedBufferTypeException();
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
		if (image.getBufferType() != MonoImage.TYPE_BYTE)
			throw new UnsupportedBufferTypeException();

		save(image.getImage(0, 256));
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
		try {
			save(image.getImage(set.current_minimum, set.current_maximum));
		} catch ( UnsupportedBufferTypeException exception ) {
			// Never happens.
		}
	}

	/**
	 * Saves a <code>java.awt.Image</code> into an image file.
	 * @param image the <code>java.awt.Image</code>.
	 * @exception IOException if I/O error occurs.
	 * @exception UnsupportedBufferTypeException if the data type is 
	 * unsupported.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	private void save ( Image image )
		throws IOException, UnsupportedBufferTypeException, UnsupportedFileTypeException
	{
		if (url == null)
			throw new IOException();

		DataOutputStream stream = null;
		if (url.getProtocol().equals("file")) {
			stream = new DataOutputStream(new FileOutputStream(new File(url.getFile())));
		} else {
			URLConnection uc = url.openConnection();
			uc.setDoOutput(true);
			stream = new DataOutputStream(uc.getOutputStream());
		}

		try {
			Class[] arg_class = new Class[3];
			arg_class[0] = Class.forName("java.lang.String");
			arg_class[1] = Class.forName("java.awt.Image");
			arg_class[2] = Class.forName("java.io.OutputStream");

			Class t = Class.forName("com.sun.jimi.core.Jimi");
			Method m = t.getMethod("putImage", arg_class);

			Object[] args = new Object[3];
			args[0] = getMimeType();
			args[1] = image;
			args[2] = stream;

			m.invoke(null, args);

			stream.close();

			return;
		} catch ( ClassNotFoundException exception ) {
		} catch ( NoSuchMethodException exception ) {
		} catch ( SecurityException exception ) {
		} catch ( IllegalAccessException exception ) {
		} catch ( IllegalArgumentException exception ) {
		} catch ( InvocationTargetException exception ) {
		} catch ( Exception exception ) {
		}

		stream.close();

		throw new UnsupportedFileTypeException(url, getName(), "save");
	}
}
