/*
 * @(#)Cubics.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.StringTokenizer;

/**
 * The <code>Cubics</code> represents a cubic function of x and y. It
 * consists of 10 coefficients from A to J which represent:
 * <pre>
 *     z = A + Bx + Cy + Dxx + Exy + Fyy + Gxxx + Hxxy + Ixyy + Jyyy
 * </pre>
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 November 7
 */

public class Cubics {
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
	 * The coefficient of x^3.
	 */
	private double G = 0.0;

	/**
	 * The coefficient of x^2 y.
	 */
	private double H = 0.0;

	/**
	 * The coefficient of x y^2.
	 */
	private double I = 0.0;

	/**
	 * The coefficient of y^3.
	 */
	private double J = 0.0;

	/**
	 * The simultaneous equation to solve this cubic function.
	 */
	private SimultaneousEquation se;

	/**
	 * The number of (x,y,z) data to solve the simultaneous equation.
	 */
	private int data_count = 0;

	/**
	 * Constructs an empty <code>Cubics</code>.
	 */
	public Cubics ( ) {
		A = B = C = D = E = F = G = H = I = J = 0.0;

		se = new SimultaneousEquation(10);
		data_count = 0;
	}

	/**
	 * Gets z value of the specified (x,y).
	 * @param x the x value.
	 * @param y the y value.
	 * @return z value of the specified (x,y).
	 */
	public double getValue ( double x, double y ) {
		return A + B * x + C * y + D * x * x + E * x * y + F * y * y + G * x * x * x + H * x * x * y + I * x * y * y + J * y * y * y;
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
		double x4 = x3 * x;
		double x5 = x4 * x;
		double x6 = x5 * x;
		double y2 = y * y;
		double y3 = y2 * y;
		double y4 = y3 * y;
		double y5 = y4 * y;
		double y6 = y5 * y;

		se.coef[0][1] += x;
		se.coef[0][2] += y;
		se.coef[0][3] += x2;
		se.coef[0][4] += x * y;
		se.coef[0][5] += y2;
		se.coef[0][6] += x3;
		se.coef[0][7] += x2 * y;
		se.coef[0][8] += x * y2;
		se.coef[0][9] += y3;
		se.coef[0][10] += - z;
		se.coef[1][6] += x4;
		se.coef[1][7] += x3 * y;
		se.coef[1][8] += x2 * y2;
		se.coef[1][9] += x * y3;
		se.coef[1][10] += - x * z;
		se.coef[2][9] += y4;
		se.coef[2][10] += - y * z;
		se.coef[3][6] += x5;
		se.coef[3][7] += x4 * y;
		se.coef[3][8] += x3 * y2;
		se.coef[3][9] += x2 * y3;
		se.coef[3][10] += - x2 * z;
		se.coef[4][9] += x * y4;
		se.coef[4][10] += - x * y * z;
		se.coef[5][9] += y5;
		se.coef[5][10] += - y2 * z;
		se.coef[6][6] += x6;
		se.coef[6][7] += x5 * y;
		se.coef[6][8] += x4 * y2;
		se.coef[6][9] += x3 * y3;
		se.coef[6][10] += - x3 * z;
		se.coef[7][9] += x2 * y4;
		se.coef[7][10] += - x2 * y * z;
		se.coef[8][9] += x * y5;
		se.coef[8][10] += - x * y2 * z;
		se.coef[9][9] += y6;
		se.coef[9][10] += - y3 * z;
	}

	/**
	 * Solves the simultaneous equation and calculates this cubic 
	 * function in the least square method.
	 */
	public void solve ( ) {
		if (data_count > 0) {
			se.coef[0][0] = (double)data_count;
			se.coef[1][0] = se.coef[0][1];
			se.coef[1][1] = se.coef[0][3];
			se.coef[1][2] = se.coef[0][4];
			se.coef[1][3] = se.coef[0][6];
			se.coef[1][4] = se.coef[0][7];
			se.coef[1][5] = se.coef[0][8];
			se.coef[2][0] = se.coef[0][2];
			se.coef[2][1] = se.coef[0][4];
			se.coef[2][2] = se.coef[0][5];
			se.coef[2][3] = se.coef[0][7];
			se.coef[2][4] = se.coef[0][8];
			se.coef[2][5] = se.coef[0][9];
			se.coef[2][6] = se.coef[1][7];
			se.coef[2][7] = se.coef[1][8];
			se.coef[2][8] = se.coef[1][9];
			se.coef[3][0] = se.coef[0][3];
			se.coef[3][1] = se.coef[0][6];
			se.coef[3][2] = se.coef[0][7];
			se.coef[3][3] = se.coef[1][6];
			se.coef[3][4] = se.coef[1][7];
			se.coef[3][5] = se.coef[1][8];
			se.coef[4][0] = se.coef[0][4];
			se.coef[4][1] = se.coef[0][7];
			se.coef[4][2] = se.coef[0][8];
			se.coef[4][3] = se.coef[1][7];
			se.coef[4][4] = se.coef[1][8];
			se.coef[4][5] = se.coef[1][9];
			se.coef[4][6] = se.coef[3][7];
			se.coef[4][7] = se.coef[3][8];
			se.coef[4][8] = se.coef[3][9];
			se.coef[5][0] = se.coef[0][5];
			se.coef[5][1] = se.coef[0][8];
			se.coef[5][2] = se.coef[0][9];
			se.coef[5][3] = se.coef[1][8];
			se.coef[5][4] = se.coef[1][9];
			se.coef[5][5] = se.coef[2][9];
			se.coef[5][6] = se.coef[3][8];
			se.coef[5][7] = se.coef[3][9];
			se.coef[5][8] = se.coef[4][9];
			se.coef[6][0] = se.coef[0][6];
			se.coef[6][1] = se.coef[1][6];
			se.coef[6][2] = se.coef[1][7];
			se.coef[6][3] = se.coef[3][6];
			se.coef[6][4] = se.coef[3][7];
			se.coef[6][5] = se.coef[3][8];
			se.coef[7][0] = se.coef[0][7];
			se.coef[7][1] = se.coef[1][7];
			se.coef[7][2] = se.coef[1][8];
			se.coef[7][3] = se.coef[3][7];
			se.coef[7][4] = se.coef[3][8];
			se.coef[7][5] = se.coef[3][9];
			se.coef[7][6] = se.coef[6][7];
			se.coef[7][7] = se.coef[6][8];
			se.coef[7][8] = se.coef[6][9];
			se.coef[8][0] = se.coef[0][8];
			se.coef[8][1] = se.coef[1][8];
			se.coef[8][2] = se.coef[1][9];
			se.coef[8][3] = se.coef[3][8];
			se.coef[8][4] = se.coef[3][9];
			se.coef[8][5] = se.coef[4][9];
			se.coef[8][6] = se.coef[6][8];
			se.coef[8][7] = se.coef[6][9];
			se.coef[8][8] = se.coef[7][9];
			se.coef[9][0] = se.coef[0][9];
			se.coef[9][1] = se.coef[1][9];
			se.coef[9][2] = se.coef[2][9];
			se.coef[9][3] = se.coef[3][9];
			se.coef[9][4] = se.coef[4][9];
			se.coef[9][5] = se.coef[5][9];
			se.coef[9][6] = se.coef[6][9];
			se.coef[9][7] = se.coef[7][9];
			se.coef[9][8] = se.coef[8][9];

			se.solve();

			A = se.getAnswer(0);
			B = se.getAnswer(1);
			C = se.getAnswer(2);
			D = se.getAnswer(3);
			E = se.getAnswer(4);
			F = se.getAnswer(5);
			G = se.getAnswer(6);
			H = se.getAnswer(7);
			I = se.getAnswer(8);
			J = se.getAnswer(9);
		}
	}

	/**
	 * Creates a negative cubic function of this one.
	 * @return a negative cubic function of this one.
	 */
	public Cubics negative ( ) {
		Cubics c = new Cubics();
		c.A = - A;
		c.B = - B;
		c.C = - C;
		c.D = - D;
		c.E = - E;
		c.F = - F;
		c.G = - G;
		c.H = - H;
		c.I = - I;
		c.J = - J;
		return c;
	}

	/**
	 * Creates a <code>Cubics</code> object from the <code>String</code>
	 * object which represents the cubic function.
	 * @param string the string which represents the cubic function.
	 * @return a new <code>Cubics</code> of the specified string.
	 */
	public static Cubics create ( String string ) {
		Cubics cubics = new Cubics();

		StringTokenizer st = new StringTokenizer(string);
		st.nextToken();		// z
		st.nextToken();		// =
		cubics.A = Double.parseDouble(st.nextToken());
		st.nextToken();		// +
		cubics.B = Double.parseDouble(st.nextToken());
		st.nextToken();		// x
		st.nextToken();		// +
		cubics.C = Double.parseDouble(st.nextToken());
		st.nextToken();		// y
		st.nextToken();		// +
		cubics.D = Double.parseDouble(st.nextToken());
		st.nextToken();		// x^2
		st.nextToken();		// +
		cubics.E = Double.parseDouble(st.nextToken());
		st.nextToken();		// x
		st.nextToken();		// y
		st.nextToken();		// +
		cubics.F = Double.parseDouble(st.nextToken());
		st.nextToken();		// y^2
		st.nextToken();		// +
		cubics.G = Double.parseDouble(st.nextToken());
		st.nextToken();		// x^3
		st.nextToken();		// +
		cubics.H = Double.parseDouble(st.nextToken());
		st.nextToken();		// x^2
		st.nextToken();		// y
		st.nextToken();		// +
		cubics.I = Double.parseDouble(st.nextToken());
		st.nextToken();		// x
		st.nextToken();		// y^2
		st.nextToken();		// +
		cubics.J = Double.parseDouble(st.nextToken());

		return cubics;
	}

	/**
	 * Returns a string representation of the state of this object.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputString ( ) {
		return "z = " + A + " + " + B + " x + " + C + " y + " + D + " x^2 + " + E + " x y + " + F + " y^2 + " + G + " x^3 + " + H + " x^2 y + " + I + " x y^2 + " + J + " y^3";
	}
}
