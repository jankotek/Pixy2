/*
 * @(#)AsteroidalMagnitudeFormula.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>AsteroidalMagnitudeFormula</code> represents a formula to 
 * calculate the magnitude of an asteroid.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 October 3
 */

public class AsteroidalMagnitudeFormula implements MagnitudeFormula {
	/**
	 * The standard magnitude.
	 */
	protected double H = 0.0;

	/**
	 * The reflection ratio.
	 */
	protected double G = 0.15;

	/**
	 * Constructs an <code>AsteroidalMagnitudeFormula</code>.
	 * @param H the standard magnitude.
	 * @param G the reflection ratio.
	 */
	public AsteroidalMagnitudeFormula ( double H, double G ) {
		this.H = H;
		this.G = G;
	}

	/**
	 * Calculates the magnitude.
	 * @param record the ephemeris record.
	 * @param orbit  the orbital elements.
	 */
	public double calculate ( EphemerisRecord record, Orbit orbit ) {
		double phi1 = Math.exp(- 3.33 * Math.pow(Math.tan(record.getPhaseAngle() / 2.0 * Astro.RAD), 0.63));
		double phi2 = Math.exp(- 1.87 * Math.pow(Math.tan(record.getPhaseAngle() / 2.0 * Astro.RAD), 1.22));
		double mag = H + 5.0 * Math.log(record.getHeliocentricDistance() * record.getGeocentricDistance()) / Math.log(10.0);
		mag -= 2.5 * Math.log((1.0 - G) * phi1 + G * phi2) / Math.log(10.0);
		return mag;
	}
}
