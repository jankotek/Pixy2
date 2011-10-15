/*
 * @(#)Triangle.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>Triangle</code> represents a triangle which consists of
 * three <code>Position</code>s.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 June 24
 */

public class Triangle {
	/**
	 * The three positions.
	 */
	protected Position[] positions = new Position[3];

	/**
	 * Constructs a <code>Triangle</code> with three positions.
	 * @param initial_position1 the first (x,y) position.
	 * @param initial_position2 the second (x,y) position.
	 * @param initial_position3 the third (x,y) position.
	 */
	public Triangle ( Position initial_position1, Position initial_position2, Position initial_position3 ) {
		positions[0] = initial_position1;
		positions[1] = initial_position2;
		positions[2] = initial_position3;
	}

	/**
	 * Gets a position.
	 * @param n the n-th position to be returned.
	 */
	public Position element ( int n ) {
		return positions[n];
	}

	/**
	 * Returns a raw string representation of the state of this object,
	 * for debugging use. It should be invoked from <code>toString</code>
	 * method of the subclasses.
	 * @return a string representation of the state of this object.
	 */
	protected String paramString ( ) {
		String s = "";
		for (int i = 0 ; i < 3 ; i++) {
			s += "(x=" + positions[i].getX() + ",y=" + positions[i].getY() + ")";
			if (i < 2)
				s += ",";
		}
		return s;
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
