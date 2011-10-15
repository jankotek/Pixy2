/*
 * @(#)SimultaneousEquation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>SimultaneousEquation</code> represents a simultaneous
 * equation. It has a function to solve the equation.
 * <p>
 * For example, in the case of the simultaneous equations:
 * <pre>
 *     a x + b y + c = 0
 *     d x + e y + f = 0
 * </pre>
 * the coefficient matrix will be set as:
 * <pre>
 *     SimultaneousEquation se = new SimultaneousEquation(2);
 *     se.coef[0][0] = a;
 *     se.coef[0][1] = b;
 *     se.coef[0][2] = c;
 *     se.coef[1][0] = d;
 *     se.coef[1][1] = e;
 *     se.coef[1][2] = f;
 * </pre>
 * and the answer will be obtained as:
 * <pre>
 *     se.solve();
 *     x = se.getAnswer(0);
 *     y = se.getAnswer(1);
 * </pre>
 * <p>
 * Note that this class does not support the case that a coefficient 
 * is 0.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 February 26
 */

public class SimultaneousEquation {
	/**
	 * The coefficient matrix. The number of rows is <code>dimension</code>
	 * and the number of columns is <i><code>dimension</code> + 1</i>.
	 * The right edge term of each equation <i>i</i>, 
	 * <tt>coef[i][<code>dimension</code>]</tt>, is a constant term.
	 */
	public double[][] coef;

	/**
	 * The dimension of the equations, which is the number of 
	 * variables, or the number of equations.
	 */
	protected int dimension;

	/**
	 * Constructs a default <code>SimultaneousEquation</code>, with
	 * the number of equations. All the values in the coefficient
	 * matrix are set as 0.
	 * @param dimension the number of equations.
	 */
	public SimultaneousEquation ( int dimension ) {
		this.dimension = dimension;
		coef = new double[dimension][dimension + 1];

		for (int i = 0 ; i < dimension ; i++) {
			for (int j = 0 ; j < dimension + 1 ; j++)
				coef[i][j] = 0.0;
		}
	}

	/**
	 * Gets the answer of i-th variable. Before to invoke this method,
	 * the method <tt>solve</tt> must be invoked.
	 * @param i the index of variable to get the answer.
	 */
	public double getAnswer ( int i ) {
		return coef[i][i+1];
	}

	/**
	 * Solves this simultaneous equation. The answer of the i-th
	 * equation will be in <tt>coef[i][i+1]</tt>.
	 * <p>
	 * Note that this method does not support the case that a 
	 * coefficient is 0.
	 */
	public void solve () {
		solve(dimension);
	}

	/**
	 * Solves the specified equation in this simultaneous equation. 
	 * This method is invoked recurrsively.
	 * @param k the index of equation to solve.
	 */
	private void solve (int k) {
		int i, j;

		if (k == 1) {
			if (coef[0][0] == 0.0) {
				coef[0][1] = 0.0;
				return;
			}

			coef[0][1] = - coef[0][1] / coef[0][0];
			return;
		}

		// Replaces the equations so that the coefficient of 
		// the bottomm equation must not be 0.
		int nonzero_index = -1;
		for (i = 0 ; i < k ; i++) {
			if (coef[i][k - 1] != 0.0)
				nonzero_index = i;
		}
		if (nonzero_index == -1) {
			for (i = 0 ; i < k - 1 ; i++)
				coef[i][k - 1] = coef[i][k];

			solve(k - 1);

			coef[k - 1][k] = 0.0;
			return;
		}
		if (nonzero_index < k - 1) {
			for (j = 0 ; j < k + 1 ; j++) {
				double swap = coef[nonzero_index][j];
				coef[nonzero_index][j] = coef[k - 1][j];
				coef[k - 1][j] = swap;
			}
		}

		for (i = 0 ; i < k ; i++) {
			if (coef[i][k - 1] != 0) {
				for (j = 0 ; j < k + 1 ; j++) {
					if (j != k - 1)
						coef[i][j] /= coef[i][k - 1];
				}
			}
		}
		for (i = 0 ; i < k - 1 ; i++) {
			if (coef[i][k - 1] == 0) {
				coef[i][k - 1] = coef[i][k];
			} else {
				for (j = 0 ; j < k - 1 ; j++)
					coef[i][j] -= coef[k - 1][j];
				coef[i][k - 1] = coef[i][k] - coef[k - 1][k];
			}
		}

		solve(k - 1);

		for (j = 0 ; j < k - 1 ; j++) {
			coef[k - 1][j] *= coef[j][j + 1];
			coef[k - 1][k] += coef[k - 1][j];
		}
		coef[k - 1][k] = - coef[k - 1][k];
	}
}
