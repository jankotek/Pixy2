/*
 * @(#)MeanStarImageRadius.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.*;

/**
 * The <code>MeanStarImageRadius</code> represents the mean radius of
 * the star images. It has an equation which represents the relation
 * between the peak value and the radius.
 * <p>
 * The equation is:
 * <pre>
 *     radius = base_radius + gradient * log ( peak )
 * </pre>
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 January 9
 */

public class MeanStarImageRadius  {
	/**
	 * The base radius of the equation.
	 */
	protected double base_radius = 0.0;

	/**
	 * The gradient of the equation.
	 */
	protected double gradient = 0.0;

	/**
	 * Constructs an empry <code>MeanStarImageRadius</code>.
	 */
	public MeanStarImageRadius ( ) {
	}

	/**
	 * Constructs a <code>MeanStarImageRadius</code>.
	 * @param ap_list the list of adjoining pixels.
	 */
	public MeanStarImageRadius ( Vector ap_list ) {
		if (ap_list.size() >= 10) {
			// se[0]: base_radius * N + gradient * \sum log ( peak ) - \sum radius = 0
			// se[1]: base_radius * \sum log (peak) + gradient * \sum ( log ( peak ) )^2 - \sum log (peak) * radius = 0
			SimultaneousEquation se = new SimultaneousEquation(2);
			for (int i = 0 ; i < ap_list.size() ; i++) {
				AdjoiningPixel ap = (AdjoiningPixel)ap_list.elementAt(i);
				double peak = ap.getPeakValue();
				double radius = Math.sqrt((double)ap.getPixelCount() / Math.PI);

				se.coef[0][0] += 1.0;
				se.coef[0][1] += Math.log(peak) / Math.log(10.0);
				se.coef[0][2] -= radius;
				se.coef[1][0] += Math.log(peak) / Math.log(10.0);
				se.coef[1][1] += Math.log(peak) / Math.log(10.0) * Math.log(peak) / Math.log(10.0);
				se.coef[1][2] -= Math.log(peak) / Math.log(10.0) * radius;
			}			
			se.solve();
			base_radius = se.getAnswer(0);
			gradient = se.getAnswer(1);
		} else {
			base_radius = 0.0;
			gradient = 0.0;

			for (int i = 0 ; i < ap_list.size() ; i++) {
				AdjoiningPixel ap = (AdjoiningPixel)ap_list.elementAt(i);
				double radius = Math.sqrt((double)ap.getPixelCount() / Math.PI);
				base_radius += radius;
			}

			base_radius /= (double)ap_list.size();
		}
	}

	/**
	 * Gets the mean radius of the specified peak.
	 * @param peak the peak value.
	 * @return the mean radius of the specified peak.
	 */
	public double getRadius ( double peak ) {
		return base_radius + gradient * Math.log(peak) / Math.log(10.0);
	}

	/**
	 * Returns a string representation of the state of this object.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputString ( ) {
		return paramString();
	}

	/**
	 * Returns a raw string representation of the state of this object,
	 * for debugging use. It should be invoked from <code>toString</code>
	 * method of the subclasses.
	 * @return a string representation of the state of this object.
	 */
	protected String paramString ( ) {
		return "radius = " + base_radius + " + " + gradient + " log_10 ( peak )";
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
