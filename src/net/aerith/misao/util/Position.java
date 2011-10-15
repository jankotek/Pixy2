/*
 * @(#)Position.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>Position</code> represents a position which consists of
 * (x,y) position. The position is expressed in float data.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 February 26
 */

public class Position {
	/**
	 * The x value.
	 */
	protected float x = (float)0.0;

	/**
	 * The y value.
	 */
	protected float y = (float)0.0;

	/**
	 * Constructs an empty <code>Position</code>. The position is set
	 * at (0,0).
	 */
	public Position ( ) {
		setX(0.0);
		setY(0.0);
	}

	/**
	 * Constructs a <code>Position</code> with specified position.
	 * @param initial_x the x position.
	 * @param initial_y the y position.
	 */
	public Position ( double initial_x, double initial_y ) {
		setX(initial_x);
		setY(initial_y);
	}

	/**
	 * Constructs a <code>Position</code> by copy.
	 * @param source_position the source position to copy.
	 */
	public Position ( Position source_position ) {
		x = source_position.x;
		y = source_position.y;
	}

	/**
	 * Gets the x value.
	 * @return the x value.
	 */
	public double getX ( ) {
		return (double)x;
	}

	/**
	 * Sets the x value.
	 * @param new_x the new x value.
	 */
	public void setX ( double new_x ) {
		x = (float)new_x;
	}

	/**
	 * Gets the y value.
	 * @return the y value.
	 */
	public double getY ( ) {
		return (double)y;
	}

	/**
	 * Sets the y value.
	 * @param new_y the new y value.
	 */
	public void setY ( double new_y ) {
		y = (float)new_y;
	}

	/**
	 * Sets the position.
	 * @param new_position the new position.
	 */
	public void setPosition ( Position new_position ) {
		setX(new_position.getX());
		setY(new_position.getY());
	}

	/**
	 * Adds the specified <code>Position</code> to this.
	 * @param position the value to add.
	 */
	public void add ( Position position ) {
		x += position.getX();
		y += position.getY();
	}

	/**
	 * Rescales the position by magnifying the specified value.
	 * @param ratio the value to magnify.
	 */
	public void rescale ( double ratio ) {
		x *= ratio;
		y *= ratio;
	}

	/**
	 * Gets the distance from the specified position.
	 * @param position the position.
	 * @return the distance from the specified position.
	 */
	public double getDistanceFrom ( Position position ) {
		return Math.sqrt((getX() - position.getX()) * (getX() - position.getX()) + (getY() - position.getY()) * (getY() - position.getY()));
	}

	/**
	 * Gets the position angle from this position to the specified 
	 * position. The 0 degree is in the direction of x axis, 90 degree 
	 * is in the direction of y axis.
	 * @param position the position.
	 * @return the position angle.
	 */
	public double getPositionAngleTo ( Position position ) {
		double r = getDistanceFrom(position);

		if (r == 0.0)
			return 0.0;

		double sinA = (position.y - y) / r;
		double cosA = (position.x - x) / r;

		double A = Math.atan2(sinA, cosA) / Astro.RAD;
		if (A < 0.0)
			A += 360.0;
		return A;
	}

	/**
	 * Returns a raw string representation of the state of this object,
	 * for debugging use. It should be invoked from <code>toString</code>
	 * method of the subclasses.
	 * @return a string representation of the state of this object.
	 */
	protected String paramString ( ) {
		return "x=" + x + ",y=" + y;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * for debugging use.
	 * @return a string representation of the state of this object.
	 */
	public String toString ( ) {
		return getClass().getName() + "[" + paramString() + "]";
	}
}
