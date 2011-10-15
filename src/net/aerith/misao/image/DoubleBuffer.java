/*
 * @(#)DoubleBuffer.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image;
import java.io.*;
import net.aerith.misao.util.*;

/**
 * The <code>DoubleBuffer</code> is a class of double array for an 
 * image buffer.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 May 15
 */

public class DoubleBuffer extends Buffer {
	/**
	 * The buffer to keep double values of the image.
	 */
	private double[] pixels;

	/**
	 * Constructs a <code>DoubleBuffer</code>, allocates the buffer.
	 * @param allocate_size the buffer size.
	 */
	public DoubleBuffer ( Size allocate_size ) {
		pixels = new double[ allocate_size.getWidth() * allocate_size.getHeight() ];
		size = allocate_size;
	}

	/**
	 * Gets pixel value of a specified position.
	 * @param x the x position.
	 * @param y the y position.
	 * @return the pixel value of the position.
	 * @exception IndexOutOfBoundsException if specified position is
	 * out of the image area.
	 */
	public double get ( int x, int y )
		throws IndexOutOfBoundsException
	{
		if (pixels == null  ||
			x < 0  ||  x >= size.getWidth()  ||
			y < 0  ||  y >= size.getHeight()) {
			throw new IndexOutOfBoundsException();
		}

		return pixels[ y * size.getWidth() + x ];
	}

	/**
	 * Sets pixel value of a specified position.
	 * @param x     the x position.
	 * @param y     the y position.
	 * @param value the value to set.
	 * @exception IndexOutOfBoundsException if specified position is
	 * out of the image area.
	 */
	public void set ( int x, int y, double value )
		throws IndexOutOfBoundsException
	{
		if (pixels == null  ||
			x < 0  ||  x >= size.getWidth()  ||
			y < 0  ||  y >= size.getHeight()) {
			throw new IndexOutOfBoundsException();
		}

		pixels[ y * size.getWidth() + x ] = value;
	}

	/**
	 * Gets pixel value of a specified position.
	 * @param x the x position.
	 * @param y the y position.
	 * @return the pixel value of the position.
	 * @exception IndexOutOfBoundsException if specified position is
	 * out of the image area.
	 */
	public double getValue ( int x, int y )
		throws IndexOutOfBoundsException
	{
		return (double)get(x, y);
	}

	/**
	 * Sets pixel value of a specified position. The value is forcedly
	 * converted into the data type of image buffer.
	 * @param x     the x position.
	 * @param y     the y position.
	 * @param value the value to set.
	 * @exception IndexOutOfBoundsException if specified position is
	 * out of the image area.
	 */
	public void setValue ( int x, int y, double value )
		throws IndexOutOfBoundsException
	{
		set(x, y, value);
	}

	/**
	 * Reads pixel values and set pixel values into the buffer.
	 * @param input the input stream pointing to the start of int
	 * data.
	 * @param size  the size of image.
	 * @exception IOException if I/O error occurs.
	 */
	public void read ( DataInput input, Size size )
		throws IOException
	{
		int x, y;
		int ptr = 0;
		for (y = 0 ; y < size.getHeight() ; y++) {
			for (x = 0 ; x < size.getWidth() ; x++) {
				pixels[ptr] = input.readDouble();
				ptr++;
			}
		}
	}

	/**
	 * Write pixel values into the specified stream.
	 * @param output the output stream.
	 * @exception IOException if I/O error occurs.
	 */
	public void write ( DataOutput output )
		throws IOException
	{
		if (pixels == null)
			throw new IOException();

		int x, y;
		int ptr = 0;
		for (y = 0 ; y < size.getHeight() ; y++) {
			for (x = 0 ; x < size.getWidth() ; x++) {
				output.writeDouble(pixels[ptr]);
				ptr++;
			}
		}
	}

	/**
	 * Inverses white and black.
	 */
	public void inverse ( ) {
		if (pixels != null) {
			int ptr = 0;
			for (int y = 0 ; y < size.getHeight() ; y++) {
				for (int x = 0 ; x < size.getWidth() ; x++) {
					pixels[ptr] = - pixels[ptr];
					ptr++;
				}
			}
		}
	}

	/**
	 * Reverses upside down.
	 */
	public void reverseVertically ( ) {
		if (pixels != null) {
			for (int y = 0, y2 = size.getHeight() - 1 ; y < size.getHeight() / 2 ; y++, y2--) {
				int ptr = y * size.getWidth();
				int ptr2 = y2 * size.getWidth();
				for (int x = 0 ; x < size.getWidth() ; x++) {
					double swap = pixels[ptr];
					pixels[ptr] = pixels[ptr2];
					pixels[ptr2] = swap;
					ptr++;
					ptr2++;
				}
			}
		}
	}

	/**
	 * Reverses left to the right.
	 */
	public void reverseHorizontally ( ) {
		if (pixels != null) {
			for (int y = 0 ; y < size.getHeight() ; y++) {
				int ptr = y * size.getWidth();
				int ptr2 = (y + 1) * size.getWidth() - 1;
				for (int x = 0 ; x < size.getWidth() / 2 ; x++) {
					double swap = pixels[ptr];
					pixels[ptr] = pixels[ptr2];
					pixels[ptr2] = swap;
					ptr++;
					ptr2--;
				}
			}
		}
	}

	/**
	 * Creates a clone image buffer.
	 * @return the new clone image buffer.
	 */
	public Buffer cloneBuffer ( ) {
		DoubleBuffer new_buffer = new DoubleBuffer(size);
		new_buffer.signed_flag = signed_flag;

		int ptr = 0;
		for (int y = 0 ; y < size.getHeight() ; y++) {
			for (int x = 0 ; x < size.getWidth() ; x++) {
				new_buffer.pixels[ptr] = pixels[ptr];
				ptr++;
			}
		}

		return new_buffer;
	}
}
