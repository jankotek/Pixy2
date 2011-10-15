/*
 * @(#)Quadrics.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.StringTokenizer;

/**
 * The <code>Quadrics</code> represents a quadric function of x and y.
 * It consists of 6 coefficients from A to F which represent:
 * <pre>
 *     z = A + Bx + Cy + Dxx + Exy + Fyy
 * </pre>
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 February 26
 */

public class Quadrics {
	/**
	 * The constant item.
	 */
	private double A = 0.0;

	/**
	 * The coefficient of x.
	 */
	private double B = 0.0;

	/**
	 * The coefficient of y.
	 */
	private double C = 0.0;

	/**
	 * The coefficient of x^2.
	 */
	private double D = 0.0;

	/**
	 * The coefficient of x y.
	 */
	private double E = 0.0;

	/**
	 * The coefficient of y^2.
	 */
	private double F = 0.0;

	/**
	 * The simultaneous equation to solve this quadric function.
	 */
	private SimultaneousEquation se;

	/**
	 * The number of (x,y,z) data to solve the simultaneous equation.
	 */
	private int data_count = 0;

	/**
	 * Constructs an empty <code>Quadrics</code>.
	 */
	public Quadrics ( ) {
		A = B = C = D = E = F = 0.0;

		se = new SimultaneousEquation(6);
		data_count = 0;
	}

	/**
	 * Gets z value of the specified (x,y).
	 * @param x the x value.
	 * @param y the y value.
	 * @return z value of the specified (x,y).
	 */
	public double getValue ( double x, double y ) {
		return A + B * x + C * y + D * x * x + E * x * y + F * y * y;
	}

	/**
	 * Adds a (x,y,z) data to solve the simultaneous equation.
	 * @param x the x value.
	 * @param y the y value.
	 * @param z the z value.
	 */
	public void setData ( double x, double y, double z ) {
		data_count++;

		double x2 = x * x;
		double x3 = x2 * x;
		double y2 = y * y;
		double y3 = y2 * y;

		se.coef[0][1] += x;
		se.coef[0][2] += y;
		se.coef[0][3] += x2;
		se.coef[0][4] += y2;
		se.coef[0][5] += x * y;
		se.coef[0][6] += - z;
		se.coef[1][3] += x3;
		se.coef[1][4] += x * y2;
		se.coef[1][5] += x2 * y;
		se.coef[1][6] += - x * z;
		se.coef[2][4] += y3;
		se.coef[2][6] += - y * z;
		se.coef[3][3] += x2 * x2;
		se.coef[3][4] += x2 * y2;
		se.coef[3][5] += x3 * y;
		se.coef[3][6] += - x2 * z;
		se.coef[4][4] += y2 * y2;
		se.coef[4][5] += x * y3;
		se.coef[4][6] += - y2 * z;
		se.coef[5][6] += - x * y * z;
	}

	/**
	 * Solves the simultaneous equation and calculates this quadric
	 * function in the least square method.
	 */
	public void solve ( ) {
		if (data_count > 0) {
			se.coef[0][0] = (double)data_count;
			se.coef[1][0] = se.coef[0][1];
			se.coef[1][1] = se.coef[0][3];
			se.coef[1][2] = se.coef[0][5];
			se.coef[2][0] = se.coef[0][2];
			se.coef[2][1] = se.coef[0][5];
			se.coef[2][2] = se.coef[0][4];
			se.coef[2][3] = se.coef[1][5];
			se.coef[2][5] = se.coef[1][4];
			se.coef[3][0] = se.coef[0][3];
			se.coef[3][1] = se.coef[1][3];
			se.coef[3][2] = se.coef[1][5];
			se.coef[4][0] = se.coef[0][4];
			se.coef[4][1] = se.coef[1][4];
			se.coef[4][2] = se.coef[2][4];
			se.coef[4][3] = se.coef[3][4];
			se.coef[5][0] = se.coef[0][5];
			se.coef[5][1] = se.coef[1][5];
			se.coef[5][2] = se.coef[1][4];
			se.coef[5][3] = se.coef[3][5];
			se.coef[5][4] = se.coef[4][5];
			se.coef[5][5] = se.coef[3][4];

			se.solve();

			A = se.getAnswer(0);
			B = se.getAnswer(1);
			C = se.getAnswer(2);
			D = se.getAnswer(3);
			E = se.getAnswer(5);
			F = se.getAnswer(4);
		}
	}

	/**
	 * Creates a negative quadric function of this one.
	 * @return a negative quadric function of this one.
	 */
	public Quadrics negative ( ) {
		Quadrics c = new Quadrics();
		c.A = - A;
		c.B = - B;
		c.C = - C;
		c.D = - D;
		c.E = - E;
		c.F = - F;
		return c;
	}

	/**
	 * Creates a <code>Quadrics</code> object from the <code>String</code>
	 * object which represents the quadric function.
	 * @param string the string which represents the quadric function.
	 * @return a new <code>Quadrics</code> of the specified string.
	 */
	public static Quadrics create ( String string ) {
		Quadrics quadrics = new Quadrics();

		StringTokenizer st = new StringTokenizer(string);
		st.nextToken();		// z
		st.nextToken();		// =
		quadrics.A = Double.parseDouble(st.nextToken());
		st.nextToken();		// +
		quadrics.B = Double.parseDouble(st.nextToken());
		st.nextToken();		// x
		st.nextToken();		// +
		quadrics.C = Double.parseDouble(st.nextToken());
		st.nextToken();		// y
		st.nextToken();		// +
		quadrics.D = Double.parseDouble(st.nextToken());
		st.nextToken();		// x^2
		st.nextToken();		// +
		quadrics.E = Double.parseDouble(st.nextToken());
		st.nextToken();		// x
		st.nextToken();		// y
		st.nextToken();		// +
		quadrics.F = Double.parseDouble(st.nextToken());

		return quadrics;
	}

	/**
	 * Calculates the peak (x,y).
	 * @return the peak (x,y).
	 */
	public Position getPeak ( ) {
		// dz/dx = B + 2Dx + Ey = 0
		// dz/dy = C + Ex + 2Fy = 0
		SimultaneousEquation se2 = new SimultaneousEquation(2);
		se2.coef[0][0] = 2.0 * D;
		se2.coef[0][1] = E;
		se2.coef[0][2] = B;
		se2.coef[1][0] = E;
		se2.coef[1][1] = 2.0 * F;
		se2.coef[1][2] = C;

		se2.solve();

		double x = se2.getAnswer(0);
		double y = se2.getAnswer(1);
		return new Position(x, y);
	}

	/**
	 * Returns a string representation of the state of this object.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputString ( ) {
		return "z = " + A + " + " + B + " x + " + C + " y + " + D + " x^2 + " + E + " x y + " + F + " y^2";
	}
}
