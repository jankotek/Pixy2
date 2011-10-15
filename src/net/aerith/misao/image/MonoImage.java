/*
 * @(#)MonoImage.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;

/**
 * The <code>MonoImage</code> is a class of monochrome image. The real
 * pixel values are stored in the field <tt>buffer</tt>, a 
 * <code>Buffer</code> object. To invoke methods of this class, the 
 * data type (byte, int, etc.) is not need to consider.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 February 26
 */

public class MonoImage implements ImageContent, Statisticable {
	/**
	 * The type number of buffer indicating byte.
	 */
	public final static int TYPE_BYTE = 1;

	/**
	 * The type number of buffer indicating short.
	 */
	public final static int TYPE_SHORT = 2;

	/**
	 * The type number of buffer indicating int.
	 */
	public final static int TYPE_INT = 3;

	/**
	 * The type number of buffer indicating float.
	 */
	public final static int TYPE_FLOAT = 11;

	/**
	 * The type number of buffer indicating double.
	 */
	public final static int TYPE_DOUBLE = 12;

	/**
	 * The buffer of pixel values.
	 */
	protected Buffer buffer;

	/**
	 * The zero point value to get true pixel value.
	 */
	protected double bzero = 0.0;

	/**
	 * The magnification value to get true pixel value.
	 */
	protected double bscale = 1.0;

	/**
	 * If the value is to be considered as signed, it is true.
	 */
	protected boolean signed_flag = false;

	/**
	 * Converts the RGB value into gray value.
	 * @param r the R value.
	 * @param g the G value.
	 * @param b the B value.
	 * @return the gray value.
	 */
	public static int convertRGBToGray ( int r, int g, int b ) {
		return (299 * r + 587 * g + 114 * b) / 1000;
	}

	/**
	 * Constructs an empty monochrome image.
	 */
	public MonoImage ( ) {
		buffer = null;
	}

	/**
	 * Constructs a <code>MonoImage</code> from a buffer.
	 * @param image_buffer the buffer to create a
	 * <code>MonoImage</code>.
	 */
	public MonoImage ( Buffer image_buffer ) {
		buffer = image_buffer;
	}

	/**
	 * Gets the image size.
	 * @return the image size.
	 */
	public Size getSize ( ) {
		if (buffer != null)
			return buffer.getSize();
		return new Size(0, 0);
	}

	/**
	 * Sets the zero point value and the magnification value to get 
	 * true pixel value.
	 * @param zero_point    the zero point value.
	 * @param magnification the magnification value.
	 */
	public void setPixelValueConvertParameters ( double zero_point, double magnification ) {
		bzero += zero_point;
		bscale *= magnification;
	}

	/**
	 * Gets the data type of buffer.
	 * @return the data type.
	 * @exception UnsupportedBufferTypeException if the data type is 
	 * unsupported.
	 */
	public int getBufferType ( )
		throws UnsupportedBufferTypeException
	{
		if (buffer != null) {
			if (buffer instanceof ByteBuffer)
				return TYPE_BYTE;
			if (buffer instanceof ShortBuffer)
				return TYPE_SHORT;
			if (buffer instanceof IntBuffer)
				return TYPE_INT;
			if (buffer instanceof FloatBuffer)
				return TYPE_FLOAT;
			if (buffer instanceof DoubleBuffer)
				return TYPE_DOUBLE;
		}
		throw new UnsupportedBufferTypeException();
	}

	/**
	 * Sets the flag to be signed. It only effects in the case of 
	 * short type FITS image to be read and saved.
	 */
	public void setSigned ( ) {
		signed_flag = true;
		if (buffer != null)
			buffer.setSigned();
	}

	/**
	 * Gets the number of data.
	 * @return the number of data.
	 */
	public int getDataCount ( ) {
		return getSize().getWidth() * getSize().getHeight();
	}

	/**
	 * Gets the value of data at the specified location.
	 * @param index the location of the data.
	 * @return the value of data at the specified location.
	 * @exception IndexOutOfBoundsException if specified location is
	 * out of the data buffer.
	 */
	public double getValueAt ( int index )
		throws IndexOutOfBoundsException
	{
		int y = index / getSize().getWidth();
		int x = index - y * getSize().getWidth();
		return getValue(x, y);
	}

	/**
	 * Gets pixel value of a specified position. The return value is
	 * converted into true pixel value with the zero point value and
	 * the magnification value.
	 * @param x the x position.
	 * @param y the y position.
	 * @return the pixel value of the position.
	 * @exception IndexOutOfBoundsException if specified position is
	 * out of the image area.
	 */
	public double getValue ( int x, int y )
		throws IndexOutOfBoundsException
	{
		if (buffer == null)
			throw new IndexOutOfBoundsException();
		double value = buffer.getValue(x,y);
		return value * bscale + bzero;
	}

	/**
	 * Gets pixel value of a specified position. The return value is
	 * converted into true pixel value with the zero point value and
	 * the magnification value. If the specified position is out of 
	 * the image area, it shifts the (x,y) into the image area. So
	 * this method never throw IndexOutOfBoundsException.
	 * @param x the x position.
	 * @param y the y position.
	 * @return the pixel value of the position.
	 */
	public double getValueOnRepeatedTiling ( int x, int y ) {
		while (x < 0)
			x += getSize().getWidth();
		while (x >= getSize().getWidth())
			x -= getSize().getWidth();
		while (y < 0)
			y += getSize().getHeight();
		while (y >= getSize().getHeight())
			y -= getSize().getHeight();
		double value = buffer.getValue(x,y);
		return value * bscale + bzero;
	}

	/**
	 * Gets pixel value of a specified position. The return value is
	 * converted into true pixel value with the zero point value and
	 * the magnification value. If the specified position is out of 
	 * the image area, it converts the (x,y) into the image area by
	 * reversing tiling. So this method never throw
	 * IndexOutOfBoundsException.
	 * @param x the x position.
	 * @param y the y position.
	 * @return the pixel value of the position.
	 */
	public double getValueOnReversingTiling ( int x, int y ) {
		int count = 0;
		while (x < 0) {
			x += getSize().getWidth();
			count++;
		}
		if (count % 2 == 1)
			x = getSize().getWidth() - 1 - x;

		count = 0;
		while (x >= getSize().getWidth()) {
			x -= getSize().getWidth();
			count++;
		}
		if (count % 2 == 1)
			x = getSize().getWidth() - 1 - x;

		count = 0;
		while (y < 0) {
			y += getSize().getHeight();
			count++;
		}
		if (count % 2 == 1)
			y = getSize().getHeight() - 1 - y;

		count = 0;
		while (y >= getSize().getHeight()) {
			y -= getSize().getHeight();
			count++;
		}
		if (count % 2 == 1)
			y = getSize().getHeight() - 1 - y;

		double value = buffer.getValue(x,y);
		return value * bscale + bzero;
	}

	/**
	 * Gets pixel value of a specified position. The return value is
	 * converted into true pixel value with the zero point value and
	 * the magnification value. If the specified position is out of 
	 * the image area, it returns the value of the pixel on the image
	 * closest from the specified position. So this method never throw
	 * IndexOutOfBoundsException.
	 * @param x the x position.
	 * @param y the y position.
	 * @return the pixel value of the position.
	 */
	public double getValueOnFlatExtension ( int x, int y ) {
		if (x < 0)
			x = 0;
		if (x >= getSize().getWidth())
			x = getSize().getWidth() - 1;
		if (y < 0)
			y = 0;
		if (y >= getSize().getHeight())
			y = getSize().getHeight() - 1;
		double value = buffer.getValue(x,y);
		return value * bscale + bzero;
	}

	/**
	 * Gets pixel value of a specified position exactly, calculated
	 * from the pixels around the position. The return value is 
	 * converted into true pixel value with the zero point value and
	 * the magnification value.
	 * @param x the x position.
	 * @param y the y position.
	 * @return the pixel value of the position.
	 */
	public double getAveragedValue ( double x, double y ) {
		int ix = (int)x;
		int iy = (int)y;

		double wx = x - (double)ix;
		double wy = y - (double)iy;

		if (wx < 0.5) {
			ix--;
			wx += 1.0;
		}
		if (wy < 0.5) {
			iy--;
			wy += 1.0;
		}
		wx -= 0.5;
		wy -= 0.5;

		double value1 = 0.0;
		value1 += getValueOnFlatExtension(ix, iy) * (1.0 - wx);
		value1 += getValueOnFlatExtension(ix + 1, iy) * wx;
		double value2 = 0.0;
		value2 += getValueOnFlatExtension(ix, iy + 1) * (1.0 - wx);
		value2 += getValueOnFlatExtension(ix + 1, iy + 1) * wx;

		double value = 0.0;
		value += value1 * (1.0 - wy);
		value += value2 * wy;

		return value;
	}

	/**
	 * Sets pixel value of a specified position. The value is forcedly
	 * converted into the data type of image buffer. The input value
	 * must be true pixel value. It is converted with the zero point
	 * value and the magnification value, then stored in the buffer.
	 * @param x     the x position.
	 * @param y     the y position.
	 * @param value the value to set.
	 * @exception IndexOutOfBoundsException if specified position is
	 * out of the image area.
	 */
	public void setValue ( int x, int y, double value )
		throws IndexOutOfBoundsException
	{
		if (buffer == null)
			throw new IndexOutOfBoundsException();
		buffer.setValue(x, y, (value - bzero) / bscale);
	}

	/**
	 * Reads pixel values, allocates memory buffer and set pixel 
	 * values into the buffer.
	 * @param input the input stream pointing to the start of byte
	 * data.
	 * @param size  the size of image.
	 * @exception IOException if I/O error occurs.
	 */
	public void readByteImage ( DataInput input, Size size )
		throws IOException
	{
		try {
			read(input, size, TYPE_BYTE);
		} catch ( UnsupportedBufferTypeException exception ) {
			// never happens.
			System.err.println(exception);
		}
	}

	/**
	 * Reads pixel values, allocates memory buffer and set pixel 
	 * values into the buffer.
	 * @param input the input stream pointing to the start of short
	 * data.
	 * @param size  the size of image.
	 * @exception IOException if I/O error occurs.
	 */
	public void readShortImage ( DataInput input, Size size )
		throws IOException
	{
		try {
			read(input, size, TYPE_SHORT);
		} catch ( UnsupportedBufferTypeException exception ) {
			// never happens.
			System.err.println(exception);
		}
	}

	/**
	 * Reads pixel values, allocates memory buffer and set pixel 
	 * values into the buffer.
	 * @param input the input stream pointing to the start of int
	 * data.
	 * @param size  the size of image.
	 * @exception IOException if I/O error occurs.
	 */
	public void readIntImage ( DataInput input, Size size )
		throws IOException
	{
		try {
			read(input, size, TYPE_INT);
		} catch ( UnsupportedBufferTypeException exception ) {
			// never happens.
			System.err.println(exception);
		}
	}

	/**
	 * Reads pixel values, allocates memory buffer and set pixel 
	 * values into the buffer.
	 * @param input the input stream pointing to the start of int
	 * data.
	 * @param size  the size of image.
	 * @exception IOException if I/O error occurs.
	 */
	public void readFloatImage ( DataInput input, Size size )
		throws IOException
	{
		try {
			read(input, size, TYPE_FLOAT);
		} catch ( UnsupportedBufferTypeException exception ) {
			// never happens.
			System.err.println(exception);
		}
	}

	/**
	 * Reads pixel values, allocates memory buffer and set pixel 
	 * values into the buffer.
	 * @param input the input stream pointing to the start of int
	 * data.
	 * @param size  the size of image.
	 * @exception IOException if I/O error occurs.
	 */
	public void readDoubleImage ( DataInput input, Size size )
		throws IOException
	{
		try {
			read(input, size, TYPE_DOUBLE);
		} catch ( UnsupportedBufferTypeException exception ) {
			// never happens.
			System.err.println(exception);
		}
	}

	/**
	 * Reads pixel values, allocates memory buffer and set pixel 
	 * values into the buffer.
	 * @param input the input stream pointing to the start of int
	 * data.
	 * @param size  the size of image.
	 * @param type  the type of data (byte, short or int).
	 * @exception IOException if I/O error occurs.
	 * @exception UnsupportedBufferTypeException if the data type is 
	 * unsupported.
	 */
	private void read ( DataInput input, Size size, int type )
		throws IOException, UnsupportedBufferTypeException
	{
		switch (type) {
			case TYPE_BYTE:
				buffer = new ByteBuffer(size);
				break;
			case TYPE_SHORT:
				buffer = new ShortBuffer(size);
				break;
			case TYPE_INT:
				buffer = new IntBuffer(size);
				break;
			case TYPE_FLOAT:
				buffer = new FloatBuffer(size);
				break;
			case TYPE_DOUBLE:
				buffer = new DoubleBuffer(size);
				break;
			default:
				buffer = null;
				throw new UnsupportedBufferTypeException();
		}

		// Because the image buffer keeps the pixel value as unsigned
		// in the case of byte and short data, the signed data is stored
		// after shifted. So in the case of signed data, the bzero value 
		// must be set properly.
		if (signed_flag) {
			buffer.setSigned();
			switch (type) {
				case TYPE_BYTE:
					// not supported now.
					break;
				case TYPE_SHORT:
					bzero = bzero - 32768;
					break;
			}
		}

		buffer.read(input, size);
	}

	/**
	 * Write pixel values into the specified stream. The data type is
	 * determined properly based on the type of image buffer.
	 * @param output the output stream.
	 * @exception IOException if I/O error occurs.
	 */
	public void write ( DataOutput output )
		throws IOException
	{
		if (buffer == null)
			throw new IOException();

		buffer.write(output);
	}

	/**
	 * Creates an <code>java.awt.Image</code> from the image buffer.
	 * The range of pixel values is expanded so that the minimum value
	 * becomes 0 and the maximum value becomes 255.
	 * @return an <code>java.awt.Image</code>.
	 */
	public Image getImage ( ) {
		Statistics stat = new Statistics(this);
		stat.calculate();
		return getImage(stat.getMin(), stat.getMax());
	}

	/**
	 * Creates an <code>java.awt.Image</code> from the image buffer.
	 * The range of pixel values is expanded so that the specified
	 * minimum value becomes 0 and the specified maximum value becomes 
	 * 255.
	 * @param minimum the pixel value to be 0.
	 * @param maximum the pixel value to be 255.
	 * @return an <code>java.awt.Image</code>.
	 */
	public Image getImage ( double minimum, double maximum ) {
		Size size = buffer.getSize();
		int pixels[] = new int[size.getWidth() * size.getHeight()];
		int ptr = 0;
		for (int y = 0 ; y < size.getHeight() ; y++) {
			for (int x = 0 ; x < size.getWidth() ; x++) {
				int value = (int)((getValue(x, y) - minimum) / (maximum - minimum) * 256);
				if (value < 0)
					value = 0;
				if (value > 255)
					value = 255;
				pixels[ptr] = (255 << 24) | (value << 16) | (value << 8) | value;
				ptr++;
			}
		}
		Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(size.getWidth(), size.getHeight(), pixels, 0, size.getWidth()));
		return image;
	}

	/**
	 * Inverses white and black.
	 */
	public void inverse ( ) {
		buffer.inverse();
	}

	/**
	 * Reverses upside down.
	 */
	public void reverseVertically ( ) {
		buffer.reverseVertically();
	}

	/**
	 * Reverses left to the right.
	 */
	public void reverseHorizontally ( ) {
		buffer.reverseHorizontally();
	}

	/**
	 * Subtracts the specified image from this image.
	 * @param image the image to subtract.
	 * @exception IndexOutOfBoundsException if the size of the two 
	 * images are not same.
	 */
	public void subtract ( MonoImage image )
		throws IndexOutOfBoundsException
	{
		for (int y = 0 ; y < getSize().getHeight() ; y++) {
			for (int x = 0 ; x < getSize().getWidth() ; x++) {
				setValue(x, y, getValue(x, y) - image.getValue(x, y));
			}
		}
	}

	/**
	 * Creates a clone image buffer.
	 * @return the new clone image buffer.
	 */
	public MonoImage cloneImage ( ) {
		MonoImage new_image = new MonoImage(buffer.cloneBuffer());

		new_image.bzero = bzero;
		new_image.bscale = bscale;
		new_image.signed_flag = signed_flag;

		return new_image;
	}

	/**
	 * Creates a clone image buffer of the new size. Although the 
	 * parameters of the images are copied, the content of the buffer
	 * is not copied.
	 * @param new_size the new size.
	 * @return the new clone image buffer.
	 */
	public MonoImage cloneImage ( Size new_size ) {
		if (buffer == null)
			return null;

		MonoImage new_image = null;

		if (buffer instanceof ByteBuffer)
			new_image = new MonoImage(new ByteBuffer(new_size));
		if (buffer instanceof ShortBuffer)
			new_image = new MonoImage(new ShortBuffer(new_size));
		if (buffer instanceof IntBuffer)
			new_image = new MonoImage(new IntBuffer(new_size));
		if (buffer instanceof FloatBuffer)
			new_image = new MonoImage(new FloatBuffer(new_size));

		if (new_image != null) {
			new_image.bzero = bzero;
			new_image.bscale = bscale;
			new_image.signed_flag = signed_flag;
		}

		return new_image;
	}
}
