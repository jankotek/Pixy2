/*
 * @(#)Pixel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>Pixel</code> represents a pixel on an image. It consists
 * of (x,y) position and the value.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 June 24
 */

public class Pixel {
	/**
	 * The x position;
	 */
	protected int x;

	/**
	 * The y position;
	 */
	protected int y;

	/**
	 * The pixel value;
	 */
	protected float value;

	/**
	 * Constructs a <code>Pixel</code>.
	 */
	public Pixel ( int initial_x, int initial_y, double initial_value ) {
		x = initial_x;
		y = initial_y;
		value = (float)initial_value;
	}

	/**
	 * Gets the x position.
	 * @return the x position.
	 */
	public int getX ( ) {
		return x;
	}

	/**
	 * Gets the y position.
	 * @return the y position.
	 */
	public int getY ( ) {
		return y;
	}

	/**
	 * Gets the pixel value.
	 * @return the pixel value.
	 */
	public double getValue ( ) {
		return (double)value;
	}
}
