/*
 * @(#)AsteroidalOrbit.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>AsteroidalOrbit</code> represents a set of orbital 
 * elements of an asteroid.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 October 3
 */

public class AsteroidalOrbit extends Orbit {
	/**
	 * The epoch in Julian Day.
	 */
	protected JulianDay epoch = new JulianDay(Astro.JULIUS_2000);

	/**
	 * The mean anomaly.
	 */
	protected double M = 0.0;

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
	protected double e = 0.0;

	/**
	 * The semi major axis.
	 */
	protected double a = 1.0;

	/**
	 * Sets the epoch in Julian Day.
	 * @param epoch the epoch in Julian Day.
	 */
	public void setEpoch ( JulianDay epoch ) {
		this.epoch = epoch;
	}

	/**
	 * Sets the mean anomaly.
	 * @param M the mean anomaly.
	 */
	public void setMeanAnomaly ( double M ) {
		this.M = M;
	}

	/**
	 * Gets the perihelion passage date in Julian Day.
	 * @return the perihelion passage date in Julian Day.
	 */
	public JulianDay getPerihelionDate ( ) {
		double M1 = M;
		if (M1 >= 180.0)
			M1 -= 360.0;

		double T = epoch.getJD() - M1 / getMeanMotion();
		return new JulianDay(T);
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
		return a * (1.0 - e);
	}

	/**
	 * Gets the aphelion distance.
	 * @return the aphelion distance.
	 */
	public double getAphelionDistance ( ) {
		return a * (1.0 + e);
	}

	/**
	 * Gets the semi major axis.
	 * @return the semi major axis.
	 */
	public double getSemiMajorAxis ( ) {
		return a;
	}

	/**
	 * Sets the semi major axis.
	 * @param a the semi major axis.
	 */
	public void setSemiMajorAxis ( double a ) {
		this.a = a;
	}

	/**
	 * Gets the semi minor axis.
	 * @return the semi minor axis.
	 */
	public double getSemiMinorAxis ( ) {
		return a * Math.sqrt(1.0 - e * e);
	}
}
