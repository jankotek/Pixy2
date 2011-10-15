/*
 * @(#)PhotometricError.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.Vector;
import net.aerith.misao.util.star.*;

/**
 * The <code>PhotometricError</code> represents the error of 
 * photometry.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 July 23
 */

public class PhotometricError {
	/**
	 * The error.
	 */
	protected double err;

	/**
	 * Constructs an <code>PhotometricError</code> based on the list
	 * of pairs.
	 * @param pair_list the list of pairs.
	 */
	public PhotometricError ( Vector pair_list ) {
		int pair_count = 0;
		for (int i = 0 ; i < pair_list.size() ; i++) {
			StarPair pair = (StarPair)pair_list.elementAt(i);
			Star star1 = pair.getFirstStar();
			Star star2 = pair.getSecondStar();

			if (star1 != null  &&  star2 != null)
				pair_count++;
		}

		Array array = new Array(pair_count);

		pair_count = 0;
		for (int i = 0 ; i < pair_list.size() ; i++) {
			StarPair pair = (StarPair)pair_list.elementAt(i);
			Star star1 = pair.getFirstStar();
			Star star2 = pair.getSecondStar();

			if (star1 != null  &&  star2 != null) {
				array.set(pair_count, star1.getMag() - star2.getMag());
				pair_count++;
			}
		}

		Statistics stat = new Statistics(array);
		stat.calculate();

		err = stat.getDeviation();
	}


	/**
	 * Gets the photometric error.
	 * @return the photometric error.
	 */
	public double getError ( ) {
		return err;
	}
}
