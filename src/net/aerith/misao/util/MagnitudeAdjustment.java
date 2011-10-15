/*
 * @(#)MagnitudeAdjustment.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.Vector;
import net.aerith.misao.util.star.*;

/**
 * The <code>MagnitudeAdjustment</code> represents a formula to 
 * convert magnitude of a star in the first list to that of a star in
 * the second list based on a cubic function.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 July 23
 */

public class MagnitudeAdjustment {
	/**
	 * The cubic funciton.
	 */
	protected SimultaneousEquation se;

	/**
	 * Constructs a <code>MagnitudeAdjustment</code> based on the 
	 * specified list of <code>StarPair</code>.
	 * @param pair_list the list of pairs of two stars.
	 */
	public MagnitudeAdjustment ( Vector pair_list ) {
		// x: mag1
		// y: mag2
		// se: A + B x + C x^2 + D x^3 - y = 0
		se = new SimultaneousEquation(4);

		for (int i = 0 ; i < pair_list.size() ; i++) {
			StarPair pair = (StarPair)pair_list.elementAt(i);
			Star star1 = pair.getFirstStar();
			Star star2 = pair.getSecondStar();

			if (star1 != null  &&  star2 != null) {
				double x = star1.getMag();
				double y = star2.getMag();

				double x2 = x * x;
				double x3 = x2 * x;
				double x4 = x3 * x;
				double x5 = x4 * x;
				double x6 = x5 * x;

				se.coef[0][0] += 1.0;
				se.coef[0][1] += x;
				se.coef[0][2] += x2;
				se.coef[0][3] += x3;
				se.coef[0][4] -= y;
				se.coef[1][0] += x;
				se.coef[1][1] += x2;
				se.coef[1][2] += x3;
				se.coef[1][3] += x4;
				se.coef[1][4] -= y * x;
				se.coef[2][0] += x2;
				se.coef[2][1] += x3;
				se.coef[2][2] += x4;
				se.coef[2][3] += x5;
				se.coef[2][4] -= y * x2;
				se.coef[3][0] += x3;
				se.coef[3][1] += x4;
				se.coef[3][2] += x5;
				se.coef[3][3] += x6;
				se.coef[3][4] -= y * x3;
			}
		}
		se.solve();
	}

	/**
	 * Returns true if the solved cubic function is monotone 
	 * increasing. If false, it implies the magnitude adjustment will
	 * be incorrect.
	 * @return true if the solved cubic function is monotone 
	 * increasing.
	 */
	public boolean isProper ( ) {
		// se: A + B x + C x^2 + D x^3 - y = 0
		boolean flag = true;
		if (se.getAnswer(3) <= 0.0)
			flag = false;
		if (se.getAnswer(2) * se.getAnswer(2) - 3.0 * se.getAnswer(3) * se.getAnswer(1) > 0.0)
			flag = false;
		return flag;
	}

	/**
	 * Adjusts the specified magnitude and returns the new magnitude.
	 * @param mag the magnitude to be adjusted.
	 * @return the adjusted magnitude.
	 */
	public double adjust ( double mag ) {
		// se: A + B x + C x^2 + D x^3 - y = 0
		return se.getAnswer(0) + se.getAnswer(1) * mag + se.getAnswer(2) * mag * mag + se.getAnswer(3) * mag * mag * mag;
	}
}
