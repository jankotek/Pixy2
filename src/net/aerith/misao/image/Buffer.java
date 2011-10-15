/*
 * @(#)Buffer.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image;
import java.io.*;
import net.aerith.misao.util.*;

/**
 * The <code>Buffer</code> is an abstract class of an image buffer.
 * This is the base class of ByteBuffer, IntBuffer, etc.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 28
 */

public abstract class Buffer implements Statisticable {
	/**
	 * The image size.
	 */
	protected Size size = new Size();

	/**
	 * If the value is to be considered as signed, it is true.
	 */
	protected boolean signed_flag = false;

	/**
	 * Gets the image size.
	 * @return the image size.
	 */
	public Size getSize ( ) {
		if (size != null)
			return size;
		return new Size(0, 0);
	}

	/**
	 * Sets the flag to be signed. It only effects in the case of 
	 * short type FITS image to be read and saved.
	 */
	public void setSigned ( ) {
		signed_flag = true;
	}

	/**
	 * Gets the number of data.
	 * @return the number of data.
	 */
	public int getDataCount ( ) {
		return size.getWidth() * size.getHeight();
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
		int y = index / size.getWidth();
		int x = index - y * size.getWidth();
		return getValue(x, y);
	}

	/**
	 * Gets pixel value of a specified position.
	 * @param x the x position.
	 * @param y the y position.
	 * @return the pixel value of the position.
	 * @exception IndexOutOfBoundsException if specified position is
	 * out of the image area.
	 */
	public abstract double getValue ( int x, int y )
		throws IndexOutOfBoundsException;

	/**
	 * Sets pixel value of a specified position. The value is forcedly
	 * converted into the data type of image buffer.
	 * @param x     the x position.
	 * @param y     the y position.
	 * @param value the value to set.
	 * @exception IndexOutOfBoundsException if specified position is
	 * out of the image area.
	 */
	public abstract void setValue ( int x, int y, double value )
		throws IndexOutOfBoundsException;

	/**
	 * Reads pixel values and set pixel values into the buffer.
	 * @param input the input stream pointing to the start of int
	 * data.
	 * @param size  the size of image.
	 * @exception IOException if I/O error occurs.
	 */
	public abstract void read ( DataInput input, Size size )
		throws IOException;

	/**
	 * Write pixel values into the specified stream.
	 * @param output the output stream.
	 * @exception IOException if I/O error occurs.
	 */
	public abstract void write ( DataOutput output )
		throws IOException;

	/**
	 * Inverses white and black.
	 */
	public abstract void inverse();

	/**
	 * Reverses upside down.
	 */
	public abstract void reverseVertically();

	/**
	 * Reverses left to the right.
	 */
	public abstract void reverseHorizontally();

	/**
	 * Creates a clone image buffer.
	 * @return the new clone image buffer.
	 */
	public abstract Buffer cloneBuffer();
}
