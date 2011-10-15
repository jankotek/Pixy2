/*
 * @(#)StarPosition.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util.star;
import net.aerith.misao.util.*;

/**
 * The <code>StarPosition</code> represents a star which consists of 
 * (x,y) position and magnitude. The position is expressed in float
 * data.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 November 7
 */

public class StarPosition extends Position {
	/**
	 * The magnitude value.
	 */
	protected float mag = (float)99.9;

	/**
	 * Constructs an empty <code>StarPosition</code>. The position is
	 * set at (0,0).
	 */
	public StarPosition ( ) {
		super();
		setMag(0.0);
	}

	/**
	 * Constructs a <code>StarPosition</code> with specified position
	 * and magnitude.
	 * @param initial_x   the x position.
	 * @param initial_y   the y position.
	 * @param initial_mag the magnitude.
	 */
	public StarPosition ( double initial_x, double initial_y, double initial_mag ) {
		super(initial_x, initial_y);
		setMag(initial_mag);
	}

	/**
	 * Gets the magnitude value.
	 * @return the magnitude value.
	 */
	public double getMag ( ) {
		return (double)mag;
	}

	/**
	 * Sets the magnitude value.
	 * @param new_mag the new magnitude value.
	 */
	public void setMag ( double new_mag ) {
		mag = (float)new_mag;
	}

	/**
	 * Returns a string representation of the state of this object.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputString ( ) {
		return paramString();
	}

	/**
	 * Returns an array of string representations of the state of 
	 * stars contained in this object. In general, it only returns a
	 * string representation of the state of only this object itself, 
	 * except for <code>UnifiedStar</code> and <code>BlendingStar</code>,
	 * the subclasses of <code>Star</code>.
	 * @return an array of string representations.
	 */
	public String[] getOutputStrings ( ) {
		String[] s = new String[1];
		s[0] = getOutputString();
		return s;
	}

	/**
	 * Returns a raw string representation of the state of this object,
	 * for debugging use. It should be invoked from <code>toString</code>
	 * method of the subclasses.
	 * @return a string representation of the state of this object.
	 */
	protected String paramString ( ) {
		return super.paramString() + ",mag=" + mag;
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
