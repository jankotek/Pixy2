/*
 * @(#)MapFunction.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.Vector;

/**
 * The <code>MapFunction</code> represents a function to convert (x,y)
 * position into (x',y') on another system. It consists of (x,y)
 * position on another system where the (0,0) position will be mapped,
 * the magnification ratio and the rotational angle.
 * <p>
 * The mapping calculation is:
 * <pre>
 *     (x', y') = r * R_angle (x, y) + (x0, y0)
 * </pre>
 * when <i>r</i> represents <tt>ratio</tt>, <i>R_angle</i> represents
 * rotation by <tt>angle</tt> in the anti-clockwise direction when the
 * positive directions of (x,y) are to the right and to the top, and
 * <i>(x0, y0)</i> represents the position where the (0,0) will be
 * mapped.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 May 20
 */

public class MapFunction {
	/**
	 * The position where (0,0) will be mapped.
	 */
	protected Position base_position = new Position();

	/**
	 * The magnification ratio.
	 */
	protected double ratio = 1.0;

	/**
	 * The rotational angle in the anti-clockwise direction.
	 */
	protected double angle = 0.0;

	/**
	 * The acceptable accuracy of ratio.
	 */
	protected final static double acceptable_ratio = 1.025;

	/**
	 * The acceptable accuracy of angle.
	 */
	protected final static double acceptable_angle = 2.0;

	/**
	 * Constructs a default <code>MapFunction</code>, which maps onto
	 * the same system.
	 */
	public MapFunction ( ) {
		base_position = new Position();
		ratio = 1.0;
		angle = 0.0;
	}

	/**
	 * Constructs a <code>MapFunction</code>.
	 * @param base_position the base position.
	 * @param ratio         the magnification ratio.
	 * @param angle         the rotation angle.
	 */
	public MapFunction ( Position base_position, double ratio, double angle ) {
		this.base_position = base_position;
		this.ratio = ratio;
		this.angle = angle;
	}

	/**
	 * Constructs a <code>MapFunction</code> to map the first position
	 * of the specified pair list into the second position of the
	 * specified pair list.
	 * @param pair_list the list of pairs of (x,y) positions.
	 */
	public MapFunction ( Vector pair_list ) {
		this(pair_list, null);
	}

	/**
	 * Constructs a <code>MapFunction</code> to map the first position
	 * of the specified pair list into the second position of the
	 * specified pair list.
	 * @param pair_list the list of pairs of (x,y) positions.
	 * @param df        the distortion field.
	 */
	public MapFunction ( Vector pair_list, DistortionField df ) {
		if (pair_list.size() == 0)
			return;

		if (pair_list.size() == 1) {
			Pair pair = (Pair)pair_list.elementAt(0);
			Position position1 = pair.getFirstPosition();
			Position position2 = pair.getSecondPosition();

			base_position = new Position(position2.getX() - position1.getX(), position2.getY() - position1.getY());
			return;
		}

		if (pair_list.size() == 2) {
			Pair pair = (Pair)pair_list.elementAt(0);
			Position position1a = pair.getFirstPosition();
			Position position2a = pair.getSecondPosition();

			pair = (Pair)pair_list.elementAt(1);
			Position position1b = pair.getFirstPosition();
			Position position2b = pair.getSecondPosition();

			double r1 = position1b.getDistanceFrom(position1a);
			double r2 = position2b.getDistanceFrom(position2a);
			ratio = r2 / r1;

			double angle1 = Math.atan2(position1b.getY() - position1a.getY(), position1b.getX() - position1a.getX()) / Astro.RAD;
			double angle2 = Math.atan2(position2b.getY() - position2a.getY(), position2b.getX() - position2a.getX()) / Astro.RAD;
			angle = Astro.normalize(angle2 - angle1, 360.0);

			double st = Math.sin(angle * Astro.RAD);
			double ct = Math.cos(angle * Astro.RAD);

			double x = ratio * (position1a.getX() * ct - position1a.getY() * st);
			double y = ratio * (position1a.getX() * st + position1a.getY() * ct);
			base_position = new Position(position2a.getX() - x, position2a.getY() - y);

			return;
		}

		SimultaneousEquation sex = new SimultaneousEquation(3);
		SimultaneousEquation sey = new SimultaneousEquation(3);

		for (int i = 0 ; i < pair_list.size() ; i++) {
			Pair pair = (Pair)pair_list.elementAt(i);
			Position position1 = pair.getFirstPosition();
			Position position2 = pair.getSecondPosition();

			if (position1 != null  &&  position2 != null) {
				if (df != null) {
					Position df_pos = df.getValue(position1);
					position1 = new Position(position1.getX() + df_pos.getX(), position1.getY() + df_pos.getY());
				}

				sex.coef[0][0] += position1.getX() * position1.getX();
				sex.coef[0][1] += position1.getX() * position1.getY();
				sex.coef[0][2] += position1.getX();
				sex.coef[0][3] -= position1.getX() * position2.getX();
				sex.coef[1][0] += position1.getY() * position1.getX();
				sex.coef[1][1] += position1.getY() * position1.getY();
				sex.coef[1][2] += position1.getY();
				sex.coef[1][3] -= position1.getY() * position2.getX();
				sex.coef[2][0] += position1.getX();
				sex.coef[2][1] += position1.getY();
				sex.coef[2][2] += 1.0;
				sex.coef[2][3] -= position2.getX();

				sey.coef[0][0] += position1.getX() * position1.getX();
				sey.coef[0][1] += position1.getX() * position1.getY();
				sey.coef[0][2] += position1.getX();
				sey.coef[0][3] -= position1.getX() * position2.getY();
				sey.coef[1][0] += position1.getY() * position1.getX();
				sey.coef[1][1] += position1.getY() * position1.getY();
				sey.coef[1][2] += position1.getY();
				sey.coef[1][3] -= position1.getY() * position2.getY();
				sey.coef[2][0] += position1.getX();
				sey.coef[2][1] += position1.getY();
				sey.coef[2][2] += 1.0;
				sey.coef[2][3] -= position2.getY();
			}			
		}
		sex.solve();
		sey.solve();

		// Parameters of map function.
		// X = fA x + fB y + fC
		// Y = fD x + fE y + fF
		double fA = sex.getAnswer(0);
		double fB = sex.getAnswer(1);
		double fC = sex.getAnswer(2);
		double fD = sey.getAnswer(0);
		double fE = sey.getAnswer(1);
		double fF = sey.getAnswer(2);

		base_position = new Position(fC, fF);

		ratio = (Math.sqrt(fA * fA + fB * fB) + Math.sqrt(fD * fD + fE * fE)) / 2.0;

		angle = Math.atan2(- fB, fA) / Astro.RAD;
		double dt = Astro.normalize(angle - Math.atan2(fD, fE) / Astro.RAD, 360.0);
		if (dt >= 180.0)
			dt -= 360.0;
		angle = Astro.normalize(angle - dt / 2.0, 360.0);
	}

	/**
	 * Constructs a <code>MapFunction</code> to map the specified 
	 * first triangle into the second triangle.
	 * @param triangle1 the first triangle.
	 * @param triangle2 the second triangle.
	 * @exception ArithmeticException if the two triangles are not
	 * similar.
	 */
	public MapFunction ( Triangle triangle1, Triangle triangle2 )
		throws ArithmeticException
	{
		SimultaneousEquation sex = new SimultaneousEquation(3);
		SimultaneousEquation sey = new SimultaneousEquation(3);

		// sex: r cos(t) x - r sin(t) y + x0 - x' = 0
		// sey: r sin(t) x + r cos(t) y + y0 - y' = 0
		for (int i = 0 ; i < 3 ; i++) {
			sex.coef[i][0] += triangle1.element(i).getX();
			sex.coef[i][1] -= triangle1.element(i).getY();
			sex.coef[i][2] += 1.0;
			sex.coef[i][3] -= triangle2.element(i).getX();

			sey.coef[i][0] += triangle1.element(i).getX();
			sey.coef[i][1] += triangle1.element(i).getY();
			sey.coef[i][2] += 1.0;
			sey.coef[i][3] -= triangle2.element(i).getY();
		}
		sex.solve();
		sey.solve();

		double r_cost = sex.getAnswer(0);
		double r_sint = sex.getAnswer(1);
		double x0 = sex.getAnswer(2);
		double r1 = Math.sqrt(r_cost * r_cost + r_sint * r_sint);
		double theta1 = Math.atan2(r_sint, r_cost) / Astro.RAD;

		r_sint = sey.getAnswer(0);
		r_cost = sey.getAnswer(1);
		double y0 = sey.getAnswer(2);
		double r2 = Math.sqrt(r_cost * r_cost + r_sint * r_sint);
		double theta2 = Math.atan2(r_sint, r_cost) / Astro.RAD;

		if (1.0 / acceptable_ratio < r1 / r2  &&  r1 / r2 < acceptable_ratio  &&  
			Astro.getDistanceOfAngles(theta1, theta2) < acceptable_ratio) {

			base_position = new Position(x0, y0);
			ratio = Math.sqrt(r1 * r2);
			angle = Astro.getMeanAngle(theta1, theta2);
		} else {
			throw new ArithmeticException();
		}
	}

	/**
	 * Maps a position into the (x,y) on the another system.
	 * @param position the (x,y) on the source system.
	 * @return the position on the another system.
	 */
	public Position map ( Position position ) {
		double st = Math.sin(angle * Astro.RAD);
		double ct = Math.cos(angle * Astro.RAD);

		double x = ratio * (position.getX() * ct - position.getY() * st) + base_position.getX();
		double y = ratio * (position.getX() * st + position.getY() * ct) + base_position.getY();

		return new Position(x, y);
	}

	/**
	 * Gets the inverse function of this map function.
	 * @return the inverse function of this map function.
	 */
	public MapFunction inverse ( ) {
		MapFunction inv = new MapFunction();
		inv.ratio = 1.0 / ratio;
		inv.angle = 360.0 - angle;
		if (inv.angle == 360.0)
			inv.angle = 0.0;
		double st = Math.sin(angle * Astro.RAD);
		double ct = Math.cos(angle * Astro.RAD);
		inv.base_position = new Position(- (base_position.getX() * ct + base_position.getY() * st) / ratio, (base_position.getX() * st - base_position.getY() * ct) / ratio);
		return inv;
	}

	/**
	 * Gets the magnification ratio.
	 * @return the magnification ratio.
	 */
	public double getRatio ( ) {
		return ratio;
	}

	/**
	 * Gets the rotational angle.
	 * @return the rotational angle.
	 */
	public double getAngle ( ) {
		return angle;
	}

	/**
	 * Returns a string representation of the state of this object.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputString ( ) {
		return " (X, Y) = " + Format.formatDouble(ratio, 7, 3).trim() + " * Rotate_[" + Format.formatDouble(angle, 8, 3) + "] (x, y) + (" + Format.formatDouble(base_position.getX(), 7, 4) + ", " + Format.formatDouble(base_position.getY(), 7, 4) + ")";
	}
}
