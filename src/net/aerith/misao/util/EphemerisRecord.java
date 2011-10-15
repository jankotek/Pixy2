/*
 * @(#)EphemerisRecord.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>EphemerisRecord</code> represents a set of parameters of 
 * the ephemeris, such as R.A. and Decl., heliocentric distance, 
 * geocentric distance, magnitude, etc.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 October 3
 */

public class EphemerisRecord {
	/**
	 * The Julian Day.
	 */
	protected JulianDay jd;

	/**
	 * The R.A. and Decl.
	 */
	protected Coor coor;

	/**
	 * The heliocentric distance.
	 */
	protected double r;

	/**
	 * The geocentric distance.
	 */
	protected double delta;

	/**
	 * The elongation.
	 */
	protected double elongation;

	/**
	 * The phase angle.
	 */
	protected double phase_angle;

	/**
	 * The magnitude.
	 */
	protected double magnitude;

	/**
	 * Constructs an <code>EphemerisRecord</code>.
	 * @param jd  the Julian Day.
	 * @param xyz the equatorial coordinates in J2000.0.
	 */
	public EphemerisRecord ( JulianDay jd, Xyz xyz ) {
		this.jd = jd;

		r = xyz.getRadius();

		Xyz solar_xyz = Astro.getSolarCoordinates(jd.getJD());
		solar_xyz.precession(jd, new JulianDay(Astro.JULIUS_2000));

		Xyz geocentric_xyz = new Xyz();
		geocentric_xyz.setX(xyz.getX() + solar_xyz.getX());
		geocentric_xyz.setY(xyz.getY() + solar_xyz.getY());
		geocentric_xyz.setZ(xyz.getZ() + solar_xyz.getZ());

		delta = geocentric_xyz.getRadius();

		coor = geocentric_xyz.convertToCoor();

		elongation = Math.acos((solar_xyz.getRadius() * solar_xyz.getRadius() + delta * delta - r * r) / (2.0 * solar_xyz.getRadius() * delta)) / Astro.RAD;

		phase_angle = Math.acos((r * r + delta * delta - solar_xyz.getRadius() * solar_xyz.getRadius()) / (2.0 * r * delta)) / Astro.RAD;

		magnitude = 0.0;
	}

	/**
	 * Gets the Julian Day.
	 * @return the Julian Day.
	 */
	public JulianDay getJD ( ) {
		return jd;
	}

	/**
	 * Gets the R.A. and Decl.
	 * @return the R.A. and Decl.
	 */
	public Coor getCoor ( ) {
		return coor;
	}

	/**
	 * Gets the heliocentric distance.
	 * @return the heliocentric distance.
	 */
	public double getHeliocentricDistance ( ) {
		return r;
	}

	/**
	 * Gets the geocentric distance.
	 * @return the geocentric distance.
	 */
	public double getGeocentricDistance ( ) {
		return delta;
	}

	/**
	 * Gets the elongation.
	 * @return the elongation.
	 */
	public double getElongation ( ) {
		return elongation;
	}

	/**
	 * Gets the phase angle.
	 * @return the phase angle.
	 */
	public double getPhaseAngle ( ) {
		return phase_angle;
	}

	/**
	 * Gets the magnitude.
	 * @return the magnitude.
	 */
	public double getMagnitude ( ) {
		return magnitude;
	}

	/**
	 * Sets the magnitude.
	 * @param magnitude the magnitude.
	 */
	public void setMagnitude ( double magnitude ) {
		this.magnitude = magnitude;
	}
}
