/*
 * @(#)Pair.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>Pair</code> represents a pair of two <code>Position</code>s.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 July 23
 */

public class Pair {
	/**
	 * The first position.
	 */
	protected Position first_position = null;

	/**
	 * The second position.
	 */
	protected Position second_position = null;

	/**
	 * Constructs an empty <code>Pair</code>.
	 */
	protected Pair ( ) {
	}

	/**
	 * Constructs a <code>Pair</code>.
	 * @param position1 the first position of the pair.
	 * @param position2 the second position of the pair.
	 */
	public Pair ( Position position1, Position position2 ) {
		first_position = position1;
		second_position = position2;
	}

	/**
	 * Gets the first position.
	 * @return the first position.
	 */
	public Position getFirstPosition ( ) {
		return first_position;
	}

	/**
	 * Gets the second position.
	 * @return the second position.
	 */
	public Position getSecondPosition ( ) {
		return second_position;
	}
}
