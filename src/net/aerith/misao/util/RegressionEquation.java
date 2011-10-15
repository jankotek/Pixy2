/*
 * @(#)RegressionEquation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.StringTokenizer;

/**
 * The <code>RegressionEquation</code> represents a regression 
 * equation:
 * <pre>
 *     y = gradient * x + constant
 * </pre>
 * 
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 December 29
 */

public class RegressionEquation {
	/**
	 * The constant.
	 */
	private double constant = 0.0;

	/**
	 * The gradient.
	 */
	private double gradient = 0.0;

	/**
	 * The simultaneous equation to solve this regression equation.
	 */
	private SimultaneousEquation se;

	/**
	 * The number of (x,y) data to solve the simultaneous equation.
	 */
	private int data_count = 0;

	/**
	 * Constructs an empty <code>RegressionEquation</code>.
	 */
	public RegressionEquation ( ) {
		constant = 0.0;
		gradient = 0.0;

		se = new SimultaneousEquation(2);
		data_count = 0;
	}

	/**
	 * Gets y value of the specified x.
	 * @param x the x value.
	 * @return y value of the specified x.
	 */
	public double getValue ( double x ) {
		return constant + gradient * x;
	}

	/**
	 * Adds a (x,y) data to solve the simultaneous equation.
	 * @param x the x value.
	 * @param y the y value.
	 */
	public void setData ( double x, double y ) {
		data_count++;

		// se[0]: constant * N + gradient * \sum x + \sum (- y) = 0
		// se[1]: constant * \sum x + gradient * \sum x^2 + \sum x * (- y) = 0
		se.coef[0][0] += 1.0;
		se.coef[0][1] += x;
		se.coef[0][2] += (- y);
		se.coef[1][0] += x;
		se.coef[1][1] += x * x;
		se.coef[1][2] += x * (- y);
	}

	/**
	 * Solves the simultaneous equation and calculates this regression
	 * equation in the least square method.
	 */
	public void solve ( ) {
		if (data_count > 0) {
			se.solve();

			constant = se.getAnswer(0);
			gradient = se.getAnswer(1);
		}
	}

	/**
	 * Returns a string representation of the state of this object.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputString ( ) {
		return "y = " + constant + " + " + gradient + " x";
	}
}
