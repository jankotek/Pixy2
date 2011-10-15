/*
 * @(#)SightScope.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>SightScope</code> represents a sight scope which consists
 * of the base position angle and the range of the scope.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 February 26
 */

public class SightScope {
	/**
	 * The base position angle.
	 */
	protected double base_angle = 0.0;

	/**
	 * The range of the scope.
	 */
	protected double range = 0.0;

	/**
	 * Constructs an empty <code>SightScope</code>.
	 */
	public SightScope ( ) {
	}

	/**
	 * Constructs a <code>SightScope</code> with specified sight scope.
	 * @param base_angle the base position angle.
	 * @param range      the range of angle.
	 */
	public SightScope ( double base_angle, double range ) {
		this.base_angle = base_angle;
		this.range = range;
	}

	/**
	 * Judges if the specified angle is within this sight scope.
	 * @param angle the angle.
	 * @return true if the specified angle is within this sight scope.
	 */
	public boolean inRange ( double angle ) {
		while (angle >= base_angle)
			angle -= 360.0;
		while (angle < base_angle)
			angle += 360.0;

		if (angle - base_angle < range)
			return true;

		return false;
	}
}
