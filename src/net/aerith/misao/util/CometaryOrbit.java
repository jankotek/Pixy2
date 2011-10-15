/*
 * @(#)CometaryOrbit.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>CometaryOrbit</code> represents a set of orbital elements 
 * of a comet.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 October 3
 */

public class CometaryOrbit extends Orbit {
	/**
	 * The perihelion passage date in Julian Day.
	 */
	protected JulianDay T = new JulianDay(Astro.JULIUS_2000);

	/**
	 * The argument of perihelion.
	 */
	protected double peri = 0.0;

	/**
	 * The longitude of ascending node.
	 */
	protected double node = 0.0;

	/**
	 * The inclination.
	 */
	protected double incl = 0.0;

	/**
	 * The eccentricity.
	 */
	protected double e = 1.0;

	/**
	 * The perihelion distance.
	 */
	protected double q = 1.0;

	/**
	 * Gets the perihelion passage date in Julian Day.
	 * @return the perihelion passage date in Julian Day.
	 */
	public JulianDay getPerihelionDate ( ) {
		return T;
	}

	/**
	 * Sets the perihelion passage date in Julian Day.
	 * @param T the perihelion passage date in Julian Day.
	 */
	public void setPerihelionDate ( JulianDay T ) {
		this.T = T;
	}

	/**
	 * Gets the argument of perihelion.
	 * @return the argument of perihelion.
	 */
	public double getArgumentOfPerihelion ( ) {
		return peri;
	}

	/**
	 * Sets the argument of perihelion.
	 * @param peri the argument of perihelion.
	 */
	public void setArgumentOfPerihelion ( double peri ) {
		this.peri = peri;
	}

	/**
	 * Gets the longitude of ascending node.
	 * @return the longitude of ascending node.
	 */
	public double getAscendingNode ( ) {
		return node;
	}

	/**
	 * Sets the longitude of ascending node.
	 * @param node the longitude of ascending node.
	 */
	public void setAscendingNode ( double node ) {
		this.node = node;
	}

	/**
	 * Gets the inclination.
	 * @return the inclination.
	 */
	public double getInclination ( ) {
		return incl;
	}

	/**
	 * Sets the inclination.
	 * @param incl the inclination.
	 */
	public void setInclination ( double incl ) {
		this.incl = incl;
	}

	/**
	 * Gets the eccentricity.
	 * @return the eccentricity.
	 */
	public double getEccentricity ( ) {
		return e;
	}

	/**
	 * Sets the eccentricity.
	 * @param e the eccentricity.
	 */
	public void setEccentricity ( double e ) {
		this.e = e;
	}

	/**
	 * Gets the perihelion distance.
	 * @return the perihelion distance.
	 */
	public double getPerihelionDistance ( ) {
		return q;
	}

	/**
	 * Sets the perihelion distance.
	 * @param q the perihelion distance.
	 */
	public void setPerihelionDistance ( double q ) {
		this.q = q;
	}

	/**
	 * Gets the aphelion distance.
	 * @return the aphelion distance.
	 */
	public double getAphelionDistance ( ) {
		if (e < 1.0)
			return getSemiMajorAxis() * (1.0 + e);

		return 0.0;
	}

	/**
	 * Gets the semi major axis.
	 * @return the semi major axis.
	 */
	public double getSemiMajorAxis ( ) {
		if (e == 1.0)
			return 0.0;

		return q / (1.0 - e);
	}

	/**
	 * Gets the semi minor axis.
	 * @return the semi minor axis.
	 */
	public double getSemiMinorAxis ( ) {
		if (e >= 1.0)
			return getSemiMajorAxis() * Math.sqrt(e * e - 1.0);

		return getSemiMajorAxis() * Math.sqrt(1.0 - e * e);
	}
}
