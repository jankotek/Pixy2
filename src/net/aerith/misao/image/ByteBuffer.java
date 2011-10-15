/*
 * @(#)ByteBuffer.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image;
import java.io.*;
import net.aerith.misao.util.*;

/**
 * The <code>ByteBuffer</code> is a class of byte array for an image
 * buffer.
 * <P>
 * The return value of <tt>get</tt> and the value for <code>set</code>
 * is between 0 and 255. If 257 is passed to <tt>set</tt>, it is 
 * converted to 1. If -1 is passed, it is converted to 255.
 * <P>
 * The value in the buffer 0 represents 0, 127 does 127, -128 does
 * 128, and -1 does 255.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 November 28
 */

public class ByteBuffer extends Buffer {
	/**
	 * The buffer to keep byte values of the image.
	 */
	private byte[] pixels;

	/**
	 * Constructs a <code>ByteBuffer</code>, allocates the buffer.
	 * @param allocate_size the buffer size.
	 */
	public ByteBuffer ( Size allocate_size ) {
		pixels = new byte[ allocate_size.getWidth() * allocate_size.getHeight() ];
		size = allocate_size;
	}

	/**
	 * Gets pixel value of a specified position. The return value is
	 * between 0 and 255.
	 * @param x the x position.
	 * @param y the y position.
	 * @return the pixel value of the position.
	 * @exception IndexOutOfBoundsException if specified position is
	 * out of the image area.
	 */
	public int get ( int x, int y )
		throws IndexOutOfBoundsException
	{
		if (pixels == null  ||
			x < 0  ||  x >= size.getWidth()  ||
			y < 0  ||  y >= size.getHeight()) {
			throw new IndexOutOfBoundsException();
		}

		int value = (int)pixels[ y * size.getWidth() + x ];

		// The value in the buffer 0 represents 0, 127 does 127, -128 does
		// 128, and -1 does 255.
		if (value < 0)
			value += 256;

		return value;
	}

	/**
	 * Sets pixel value of a specified position. The value is must be
	 * between 0 and 255. If 257 is passed to <tt>set</tt>, it is 
	 * converted to 1. If -1 is passed, it is converted to 255.
	 * @param x     the x position.
	 * @param y     the y position.
	 * @param value the value to set.
	 * @exception IndexOutOfBoundsException if specified position is
	 * out of the image area.
	 */
	public void set ( int x, int y, int value )
		throws IndexOutOfBoundsException
	{
		if (pixels == null  ||
			x < 0  ||  x >= size.getWidth()  ||
			y < 0  ||  y >= size.getHeight()) {
			throw new IndexOutOfBoundsException();
		}

		// The value in the buffer 0 represents 0, 127 does 127, -128 does
		// 128, and -1 does 255.
		pixels[ y * size.getWidth() + x ] = (byte)value;
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
		set(x, y, (int)value);
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
				pixels[ptr] = (byte)input.readUnsignedByte();
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
				output.writeByte((int)pixels[ptr]);
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
					// The value in the buffer 0 represents 0, 127 does 127, -128 does
					// 128, and -1 does 255.
					pixels[ptr] = (byte)(- pixels[ptr] - 1);
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
					byte swap = pixels[ptr];
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
					byte swap = pixels[ptr];
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
		ByteBuffer new_buffer = new ByteBuffer(size);
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
