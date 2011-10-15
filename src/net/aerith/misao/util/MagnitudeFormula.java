/*
 * @(#)MagnitudeFormula.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>MagnitudeFormula</code> represents a formula to calculate
 * the magnitude of a comet or an asteroid.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 October 3
 */

public interface MagnitudeFormula {
	/**
	 * Calculates the magnitude.
	 * @param record the ephemeris record.
	 * @param orbit  the orbital elements.
	 */
	public abstract double calculate ( EphemerisRecord record, Orbit orbit );
}
