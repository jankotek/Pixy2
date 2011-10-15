/*
 * @(#)CircumArea.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>CircumArea</code> represents a circumscribed area around
 * the specified circle on the celestial sphere, which consists of 
 * the maximum and minimum value of R.A. and Decl. of the specified 
 * circle.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 February 26
 */

public class CircumArea {
	/**
	 * The R.A. and Decl. of the center.
	 */
	private Coor center_coor;

	/**
	 * The Decl. of the center point on the line near to the pole.
	 */
	private double center_decl_near_pole;

	/**
	 * The Decl. of the corner near to the pole.
	 */
	private double corner_decl_near_pole;

	/**
	 * The Decl. of the corner far from the pole.
	 */
	private double corner_decl_far_pole;

	/**
	 * The Decl. of most to the north.
	 */
	private double decl_most_north;

	/**
	 * The Decl. of most to the south.
	 */
	private double decl_most_south;

	/**
	 * The difference between maximum R.A. and minimum R.A.
	 */
	private double width_in_RA;

	/**
	 * The sqrt(2).
	 */
	private final static double sqrt2 = Math.sqrt(2.0);

	/**
	 * Constructs a <code>CircumArea</code> of the specified circle.
	 * All the parameters required to judge area overlapping, etc.
	 * are calculated in this constructor.
	 * @param coor   the R.A. and Decl. of the center.
	 * @param radius the radius of the circle in degree.
	 */
	public CircumArea ( Coor coor, double radius ) {
		center_coor = coor;

		double decl = coor.getDecl();
		if (decl >= 89.99999)
			decl = 89.99999;
		if (decl <= -89.99999)
			decl = -89.99999;

		double sinb, cosb, sinC, cosC;

		if (decl >= 0.0) {
			center_decl_near_pole = decl + radius;

			cosb = Math.cos((90.0 - decl) * Astro.RAD) * Math.cos(radius * sqrt2 * Astro.RAD) + Math.sin((90.0 - decl) * Astro.RAD) * Math.sin(radius * sqrt2 * Astro.RAD) * sqrt2 / 2.0;
			sinb = Math.sqrt(1.0 - cosb * cosb);
			sinC = Math.sin(radius * sqrt2 * Astro.RAD) * sqrt2 / 2.0 / sinb;
			cosC = (Math.cos(radius * sqrt2 * Astro.RAD) - Math.cos((90.0 - decl) * Astro.RAD) * cosb) / (Math.sin((90.0 - decl) * Astro.RAD) * sinb);
			width_in_RA = Math.atan2(sinC, cosC) / Astro.RAD;
			corner_decl_near_pole = 90.0 - Math.atan2(sinb, cosb) / Astro.RAD;

			cosb = Math.cos((90.0 - decl) * Astro.RAD) * Math.cos(radius * sqrt2 * Astro.RAD) - Math.sin((90.0 - decl) * Astro.RAD) * Math.sin(radius * sqrt2 * Astro.RAD) * sqrt2 / 2.0;
			sinb = Math.sqrt(1.0 - cosb * cosb);
			corner_decl_far_pole = 90.0 - Math.atan2(sinb, cosb) / Astro.RAD;

			decl_most_north = center_decl_near_pole;
			decl_most_south = corner_decl_far_pole;
		} else {
			center_decl_near_pole = decl - radius;

			cosb = Math.cos((90.0 - decl) * Astro.RAD) * Math.cos(radius * sqrt2 * Astro.RAD) - Math.sin((90.0 - decl) * Astro.RAD) * Math.sin(radius * sqrt2 * Astro.RAD) * sqrt2 / 2.0;
			sinb = Math.sqrt(1.0 - cosb * cosb);
			sinC = Math.sin(radius * sqrt2 * Astro.RAD) * sqrt2 / 2.0 / sinb;
			cosC = (Math.cos(radius * sqrt2 * Astro.RAD) - Math.cos((90.0 - decl) * Astro.RAD) * cosb) / (Math.sin((90.0 - decl) * Astro.RAD) * sinb);
			width_in_RA = Math.atan2(sinC, cosC) / Astro.RAD;
			corner_decl_near_pole = 90.0 - Math.atan2(sinb, cosb) / Astro.RAD;

			cosb = Math.cos((90.0 - decl) * Astro.RAD) * Math.cos(radius * sqrt2 * Astro.RAD) + Math.sin((90.0 - decl) * Astro.RAD) * Math.sin(radius * sqrt2 * Astro.RAD) * sqrt2 / 2.0;
			sinb = Math.sqrt(1.0 - cosb * cosb);
			corner_decl_far_pole = 90.0 - Math.atan2(sinb, cosb) / Astro.RAD;

			decl_most_north = corner_decl_far_pole;
			decl_most_south = center_decl_near_pole;
		}
	}

	/**
	 * Gets the Decl. of most to the north.
	 * @return the Decl. of most to the north.
	 */
	public double getDeclMostNorth ( ) {
		return decl_most_north;
	}

	/**
	 * Gets the Decl. of most to the south.
	 * @return the Decl. of most to the south.
	 */
	public double getDeclMostSouth ( ) {
		return decl_most_south;
	}

	/**
	 * Gets the difference between maximum R.A. and minimum R.A.
	 * @return the difference between maximum R.A. and minimum R.A.
	 */
	public double getWidthInRA ( ) {
		return width_in_RA * 2.0;
	}

	/**
	 * Judges if the specified rectangle area is overlapping on this
	 * <code>CircumArea</code>.
	 * @param start_coor the R.A. and Decl. of the minimal corner.
	 * @param end_coor   the R.A. and Decl. of the maximal corner.
	 * @return true if the specified rectangle is overlapping.
	 */
	public boolean overlapsArea ( Coor start_coor, Coor end_coor ) {
		double RA_start = start_coor.getRA();
		double RA_end = end_coor.getRA();
		double Decl_start = start_coor.getDecl();
		double Decl_end = end_coor.getDecl();

		if (RA_start > RA_end)
			RA_end += 360.0;
		if (Decl_end < Decl_start) {
			double swap_tmp = Decl_start;
			Decl_start = Decl_end;
			Decl_end = swap_tmp;
		}

		if (decl_most_north >= Decl_start  &&  decl_most_south <= Decl_end) {
			if ((decl_most_north >= 90  &&  corner_decl_near_pole <= Decl_end)  ||  (decl_most_south <= -90  &&  corner_decl_near_pole >= Decl_start)  ||
				(center_coor.getRA() - width_in_RA <= RA_end  &&  RA_start <= center_coor.getRA() + width_in_RA)  ||  (center_coor.getRA() - width_in_RA <= RA_end + 360.0  &&  RA_start + 360 <= center_coor.getRA() + width_in_RA)  ||  (center_coor.getRA() - width_in_RA <= RA_end - 360.0  &&  RA_start - 360 <= center_coor.getRA() + width_in_RA)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Judges if the specified R.A. and Decl. is within this
	 * <code>CircumArea</code>.
	 * @param coor the R.A. and Decl. to judge.
	 * @return true if the specified R.A. and Decl. is within this
	 * area.
	 */
	public boolean inArea ( Coor coor ) {
		double diff_RA = Math.abs(center_coor.getRA() - coor.getRA());
		if (diff_RA > 180.0)
			diff_RA = 360.0 - diff_RA;
		if (decl_most_north >= coor.getDecl()  &&  decl_most_south <= coor.getDecl()  &&
			((decl_most_north >= 90.0  &&  corner_decl_near_pole <= coor.getDecl())  ||  (decl_most_south <= -90.0  &&  corner_decl_near_pole >= coor.getDecl())  ||  diff_RA < width_in_RA)) {
			return true;
		}

		return false;
	}

	/**
	 * Returns a raw string representation of the state of this object,
	 * for debugging use.
	 * @return a string representation of the state of this object.
	 */
	protected String paramString ( ) {
		return "center_coor=" + center_coor.getOutputString() + ",center_decl_near_pole=" + center_decl_near_pole + ",corner_decl_near_pole=" + corner_decl_near_pole + ",corner_decl_far_pole=" + corner_decl_far_pole + ",decl_most_north=" + decl_most_north + ",decl_most_south=" + decl_most_south + ",width_in_RA=" + width_in_RA;
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
