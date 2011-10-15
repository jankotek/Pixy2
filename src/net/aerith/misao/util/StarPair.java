/*
 * @(#)StarPair.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import net.aerith.misao.util.star.*;

/**
 * The <code>StarPair</code> represents a pair of two <code>Star</code>s.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 July 23
 */

public class StarPair extends Pair {
	/**
	 * True if the pair contains only a star of one side and it is out
	 * of area on the other map.
	 */
	protected boolean out_of_area_flag = false;

	/**
	 * Constructs a <code>StarPair</code>.
	 * @param star1 the first star of the pair.
	 * @param star2 the second star of the pair.
	 */
	public StarPair ( Star star1, Star star2 ) {
		first_position = star1;
		second_position = star2;

		if (star1 != null)
			star1.setPair(this);
		if (star2 != null)
			star2.setPair(this);
	}

	/**
	 * Gets the first star.
	 * @return the first star.
	 */
	public Star getFirstStar ( ) {
		return (Star)first_position;
	}

	/**
	 * Gets the second star.
	 * @return the second star.
	 */
	public Star getSecondStar ( ) {
		return (Star)second_position;
	}

	/**
	 * True if this pair contains only a star of one side and it is 
	 * out of area on the other map.
	 */
	public boolean isOutOfArea ( ) {
		return out_of_area_flag;
	}

	/**
	 * Sets the flag to represent this pair contains only a star of 
	 * one side and it is out of area on the other map.
	 */
	public void setOutOfArea ( ) {
		out_of_area_flag = true;
	}
}
