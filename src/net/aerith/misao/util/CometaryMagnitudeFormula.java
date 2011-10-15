/*
 * @(#)CometaryMagnitudeFormula.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>CometaryMagnitudeFormula</code> represents a formula to 
 * calculate the magnitude of a comet.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 October 3
 */

public class CometaryMagnitudeFormula implements MagnitudeFormula {
	/**
	 * The standard magnitude.
	 */
	protected double m0 = 0.0;

	/**
	 * The coefficient of log r.
	 */
	protected double k = 10.0;

	/**
	 * Constructs a <code>CometaryMagnitudeFormula</code>.
	 * @param m0 the standard magnitude.
	 * @param k  the coefficient of log r.
	 */
	public CometaryMagnitudeFormula ( double m0, double k ) {
		this.m0 = m0;
		this.k = k;
	}

	/**
	 * Calculates the magnitude.
	 * @param record the ephemeris record.
	 * @param orbit  the orbital elements.
	 */
	public double calculate ( EphemerisRecord record, Orbit orbit ) {
		return m0 + 5.0 * Math.log(record.getGeocentricDistance()) / Math.log(10.0) + k * Math.log(record.getHeliocentricDistance()) / Math.log(10.0);
	}
}
