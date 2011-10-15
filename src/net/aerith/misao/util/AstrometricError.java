/*
 * @(#)AstrometricError.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.Vector;
import net.aerith.misao.util.star.*;

/**
 * The <code>AstrometricError</code> represents the error of 
 * astrometry, both of R.A. and Decl.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 July 23
 */

public class AstrometricError extends Coor {
	/**
	 * Constructs an <code>AstrometricError</code> based on the list
	 * of pairs.
	 * @param pair_list the list of pairs.
	 */
	public AstrometricError ( Vector pair_list ) {
		double err_ra = 0.0;
		double err_decl = 0.0;

		for (int i = 0 ; i < pair_list.size() ; i++) {
			StarPair pair = (StarPair)pair_list.elementAt(i);
			Star star1 = pair.getFirstStar();
			Star star2 = pair.getSecondStar();

			if (star1 != null  &&  star2 != null) {
				// O-C (position of star1 - position of star2).
				Coor err = star1.getCoor().residual(star2.getCoor());

				err_ra += err.getRA() * err.getRA();
				err_decl += err.getDecl() * err.getDecl();
			}
		}

		if (pair_list.size() > 0) {
			err_ra = Math.sqrt(err_ra / (double)pair_list.size());
			err_decl = Math.sqrt(err_decl / (double)pair_list.size());
		}

		setRA(err_ra);
		setDecl(err_decl);
	}
}
