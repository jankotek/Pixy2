/*
 * @(#)Orbit.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.Random;

/**
 * The <code>Orbit</code> represents a set of orbital elements and 
 * magnitude formulas.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 November 3
 */

public abstract class Orbit {
	/**
	 * The equinox number which indicates J2000.0.
	 */
	public final static int EQUINOX_J2000 = 0;

	/**
	 * The equinox number which indicates B1950.0.
	 */
	public final static int EQUINOX_B1950 = 1;

	/**
	 * The equinox.
	 */
	protected int equinox = EQUINOX_J2000;

	/**
	 * The threshold to converge in radian.
	 */
	protected final static double threshold_radian = 0.0000001;

	/**
	 * The threshold to converge in day.
	 */
	protected final static double threshold_day = 0.00001;

	/**
	 * The magnitude formula
	 */
	protected MagnitudeFormula magnitude_formula;

	/**
	 * Gets the perihelion passage date in Julian Day.
	 * @return the perihelion passage date in Julian Day.
	 */
	public abstract JulianDay getPerihelionDate ( );

	/**
	 * Gets the argument of perihelion.
	 * @return the argument of perihelion.
	 */
	public abstract double getArgumentOfPerihelion ( );

	/**
	 * Gets the longitude of ascending node.
	 * @return the longitude of ascending node.
	 */
	public abstract double getAscendingNode ( );

	/**
	 * Gets the inclination.
	 * @return the inclination.
	 */
	public abstract double getInclination ( );

	/**
	 * Gets the eccentricity.
	 * @return the eccentricity.
	 */
	public abstract double getEccentricity ( );

	/**
	 * Gets the perihelion distance.
	 * @return the perihelion distance.
	 */
	public abstract double getPerihelionDistance ( );

	/**
	 * Gets the aphelion distance.
	 * @return the aphelion distance.
	 */
	public abstract double getAphelionDistance ( );

	/**
	 * Gets the semi major axis.
	 * @return the semi major axis.
	 */
	public abstract double getSemiMajorAxis ( );

	/**
	 * Gets the semi minor axis.
	 * @return the semi minor axis.
	 */
	public abstract double getSemiMinorAxis ( );

	/**
	 * Gets the mean motion in degree.
	 * @return the mean motion in degree.
	 */
	public double getMeanMotion ( ) {
		if (getEccentricity() < 1.0)
			return Astro.GAUSSIAN_CONSTANT / getPeriod() / Astro.RAD;

		return 0.0;
	}

	/**
	 * Gets the period in year.
	 * @return the period in year.
	 */
	public double getPeriod ( ) {
		if (getEccentricity() < 1.0) {
			double a = getSemiMajorAxis();
			return Math.sqrt(a * a * a);
		}

		return 0.0;
	}

	/**
	 * Gets the ecliptic coordinates of the perihelion.
	 * @return the ecliptic coordinates of the perihelion.
	 */
	public Coor getEclipticCoordinatesOfPerihelion ( ) {
		Coor coor = new Coor(0.0, getArgumentOfPerihelion());
		coor = coor.rotate(90.0 - getInclination());

		double L = Astro.normalize(coor.getRA() + getAscendingNode(), 360.0);
		double B = coor.getDecl();

		return new Coor(L, B);
	}

	/**
	 * Sets the magnitude formula.
	 * @param formula the magnitude formula.
	 */
	public void setMagnitudeFormula ( MagnitudeFormula formula ) {
		this.magnitude_formula = formula;
	}

	/**
	 * Calculates the ephemeris.
	 * @param jd the Julian Day.
	 * @return the ephemeris.
	 */
	public EphemerisRecord calculateEphemeris ( JulianDay jd ) {
		Xyz solar_xyz = Astro.getSolarCoordinates(jd.getJD());
		solar_xyz.precession(jd, new JulianDay(Astro.JULIUS_2000));

		double dt = jd.getJD() - getPerihelionDate().getJD();

		double peri = getArgumentOfPerihelion() * Astro.RAD;
		double node = getAscendingNode() * Astro.RAD;
		double incl = getInclination() * Astro.RAD;

		double a1 = Math.sin(peri) * Math.sin(node);
		double a2 = Math.sin(peri) * Math.cos(node);
		double a3 = Math.sin(peri) * Math.sin(incl);
		double a4 = Math.cos(peri) * Math.sin(node);
		double a5 = Math.cos(peri) * Math.cos(node);
		double a6 = Math.cos(peri) * Math.sin(incl);
		double a7 = a5 * Math.cos(incl) - a1;
		double a8 = a2 * Math.cos(incl) + a4;

		double epsilon = Astro.EPSILON_2000;
		if (equinox == EQUINOX_B1950)
			epsilon = Astro.EPSILON_1950;

		double px = a5 - a1 * Math.cos(incl);
		double py = a8 * Math.cos(epsilon * Astro.RAD) - a3 * Math.sin(epsilon * Astro.RAD);
		double pz = a3 * Math.cos(epsilon * Astro.RAD) + a8 * Math.sin(epsilon * Astro.RAD);
		double qx = - a2 - a4 * Math.cos(incl);
		double qy = a7 * Math.cos(epsilon * Astro.RAD) - a6 * Math.sin(epsilon * Astro.RAD);
		double qz = a6 * Math.cos(epsilon * Astro.RAD) + a7 * Math.sin(epsilon * Astro.RAD);

		double ax = 0.0;
		double ay = 0.0;
		double az = 0.0;
		double bx = 0.0;
		double by = 0.0;
		double bz = 0.0;

		double e = getEccentricity();
		double q = getPerihelionDistance();

		if (e == 1.0) {
			ax = q * px;
			ay = q * py;
			az = q * pz;
			bx = 2.0 * q * qx;
			by = 2.0 * q * qy;
			bz = 2.0 * q * qz;
		} else {
			double a = Math.abs(getSemiMajorAxis());
			double b = Math.abs(getSemiMinorAxis());

			ax = a * px;
			ay = a * py;
			az = a * pz;
			bx = b * qx;
			by = b * qy;
			bz = b * qz;
		}

		double dt0 = dt;
		double delay = 0.0;

		Xyz xyz = new Xyz();
		Random random = new Random();

		while (true) {
			dt = dt0 - delay;

			if (e < 1.0) {
				double a = getSemiMajorAxis();
				double M = Astro.GAUSSIAN_CONSTANT / Math.pow(a, 1.5) * dt;
				double M1 = 0.0;

				double E = M / (1.0 - e);
				double E0 = E;
				int count = 0;
				do {
					if (count > 20) {
						double r = random.nextDouble();
						r = r * r * r * r + 1.0;
						if (random.nextInt() % 2 == 0)
							E = E0 * r;
						else
							E = E0 / r;
						while (E < - 180.0 * Astro.RAD)
							E += 360.0 * Astro.RAD;
						while (E > 180.0 * Astro.RAD)
							E -= 360.0 * Astro.RAD;
						count = 0;
					}
					M1 = E - e * Math.sin(E);
					E += (M - M1) / (1.0 - e * Math.cos(E));
					count++;
				} while (Math.abs(M - M1) > threshold_radian);

				double r = a * (1.0 - e * Math.cos(E));
				xyz.setX(ax * (Math.cos(E) - e) + bx * Math.sin(E));
				xyz.setY(ay * (Math.cos(E) - e) + by * Math.sin(E));
				xyz.setZ(az * (Math.cos(E) - e) + bz * Math.sin(E));
			} else if (e > 1.0) {
				double a = Math.abs(getSemiMajorAxis());
				double M = Astro.GAUSSIAN_CONSTANT / Math.pow(a, 1.5) * dt;
				double M1 = 0.0;

				double E = Astro.GAUSSIAN_CONSTANT * dt / (Math.sqrt(2.0) * Math.pow(q, 1.5));
				double E0 = E;
				int count = 0;
				do {
					if (count > 20) {
						double r = random.nextDouble();
						r = r * r * r * r + 1.0;
						if (random.nextInt() % 2 == 0)
							E = E0 * r;
						else
							E = E0 / r;
						while (E < - 180.0 * Astro.RAD)
							E += 360.0 * Astro.RAD;
						while (E > 180.0 * Astro.RAD)
							E -= 360.0 * Astro.RAD;
						count = 0;
					}
					M1 = e * Astro.sinh(E) - E;
					E += (M - M1) / (e * Astro.cosh(E) - 1.0);
					count++;
				} while (Math.abs(M - M1) > threshold_radian);

				double r = a * (e * Astro.cosh(E) - 1.0);
				xyz.setX(ax * (e - Astro.cosh(E)) + bx * Astro.sinh(E));
				xyz.setY(ay * (e - Astro.cosh(E)) + by * Astro.sinh(E));
				xyz.setZ(az * (e - Astro.cosh(E)) + bz * Astro.sinh(E));
			} else {
				double M = Astro.GAUSSIAN_CONSTANT * dt / (Math.sqrt(2.0) * Math.pow(q, 1.5));
				double tanv = M;
				while (true) {
					double M1 = tanv * tanv * tanv + 3.0 * tanv - 3.0 * M;
					tanv -= M1 / (3.0 * tanv * tanv + 3.0);
					if (Math.abs(M1) < threshold_radian)
						break;
				}

				double r = q * (1.0 + tanv * tanv);
				xyz.setX(ax * (1.0 - tanv * tanv) + bx * tanv);
				xyz.setY(ay * (1.0 - tanv * tanv) + by * tanv);
				xyz.setZ(az * (1.0 - tanv * tanv) + bz * tanv);
			}

			if (equinox == EQUINOX_B1950)
				xyz.precession(new JulianDay(Astro.BESSELL_1950), new JulianDay(Astro.JULIUS_2000));

			Xyz geocentric_xyz = new Xyz();
			geocentric_xyz.setX(xyz.getX() + solar_xyz.getX());
			geocentric_xyz.setY(xyz.getY() + solar_xyz.getY());
			geocentric_xyz.setZ(xyz.getZ() + solar_xyz.getZ());
			double delta = geocentric_xyz.getRadius();

			double delay_old = delay;
			delay = delta * Astro.LIGHT_TIME / 3600.0 / 24.0;
			if (Math.abs(delay - delay_old) < threshold_day)
				break;
		}

		EphemerisRecord record = new EphemerisRecord(jd, xyz);

		if (magnitude_formula != null) {
			double mag = magnitude_formula.calculate(record, this);
			record.setMagnitude(mag);
		}

		return record;
	}
}
