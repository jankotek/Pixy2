/*
 * @(#)MagnitudeTranslationFormula.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.Vector;
import java.util.StringTokenizer;
import net.aerith.misao.util.star.*;

/**
 * The <code>MagnitudeTranslationFormula</code> represents a formula
 * to convert pixel value into magnitude.
 * <p>
 * The parameters represents the following formula.
 * <p><pre>
 *     magnitude = base_magnitude + gradient * log_2.5 ( value )
 * </pre></p>
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 February 17
 */

public class MagnitudeTranslationFormula {
	/**
	 * The base magnitude.
	 */
	protected double base_magnitude = 0.0;

	/**
	 * The gradient.
	 */
	protected double gradient = -1.0;

	/**
	 * The mode which represents to fix the gradient as -1.
	 */
	public final static int MODE_FIX_GRADIENT = 0;

	/**
	 * The mode which represents to calculate both the base magnitude 
	 * and the gradient.
	 */
	public final static int MODE_CALCULATE_GRADIENT = 1;

	/**
	 * Constructs a <code>MagnitudeTranslationFormula</code>.
	 */
	public MagnitudeTranslationFormula ( ) {
		base_magnitude = 0.0;
		gradient = -1.0;
	}

	/**
	 * Constructs a <code>MagnitudeTranslationFormula</code> based on 
	 * the specified list of <code>StarPair</code> which consists of
	 * a detected star image and a catalog data. Depending on the 
	 * specified mode number, the gradient can be fixed as -1, or 
	 * calculated to fit the data in the specified list.
	 * @param pair_list the list of pairs of a detected star image and
	 * a catalog data.
	 * @param mode      the mode to calculate the formula, to fix the
	 * gradient as -1 or not.
	 */
	public MagnitudeTranslationFormula ( Vector pair_list, int mode ) {
		if (mode == MODE_FIX_GRADIENT) {
			// se: base_magnitude * N + \sum (-1 * log_2.5 ( value ) - magnitude) = 0
			SimultaneousEquation se = new SimultaneousEquation(1);

			for (int i = 0 ; i < pair_list.size() ; i++) {
				StarPair pair = (StarPair)pair_list.elementAt(i);
				StarImage star1 = (StarImage)pair.getFirstStar();
				Star star2 = pair.getSecondStar();

				if (star1 != null  &&  star2 != null) {
					se.coef[0][0] += 1.0;
					se.coef[0][1] += (-1.0 * Math.log(star1.getValue()) / Math.log(Astro.MAG_STEP) - star2.getMag());
				}
			}
			se.solve();

			base_magnitude = se.getAnswer(0);
			gradient = -1.0;
		} else {
			// se[0]: base_magnitude * N + gradient * \sum log_2.5 ( value ) - \sum magnitude = 0
			// se[1]: base_magnitude * \sum log_2.5 (value) + gradient * \sum ( log_2.5 ( value ) )^2 - \sum log_2.5 (value) * magnitude = 0
			SimultaneousEquation se = new SimultaneousEquation(2);

			for (int i = 0 ; i < pair_list.size() ; i++) {
				StarPair pair = (StarPair)pair_list.elementAt(i);
				StarImage star1 = (StarImage)pair.getFirstStar();
				Star star2 = pair.getSecondStar();

				if (star1 != null  &&  star2 != null) {
					se.coef[0][0] += 1.0;
					se.coef[0][1] += Math.log(star1.getValue()) / Math.log(Astro.MAG_STEP);
					se.coef[0][2] -= star2.getMag();
					se.coef[1][0] += Math.log(star1.getValue()) / Math.log(Astro.MAG_STEP);
					se.coef[1][1] += (Math.log(star1.getValue()) / Math.log(Astro.MAG_STEP)) * (Math.log(star1.getValue()) / Math.log(Astro.MAG_STEP));
					se.coef[1][2] -= Math.log(star1.getValue()) / Math.log(Astro.MAG_STEP) * star2.getMag();
				}
			}
			se.solve();

			base_magnitude = se.getAnswer(0);
			gradient = se.getAnswer(1);
		}
	}

	/**
	 * Gets the base magnitude.
	 * @return the base magnitude.
	 */
	public double getBaseMagnitude ( ) {
		return base_magnitude;
	}

	/**
	 * Sets the base magnitude.
	 * @param new_magnitude the new base magnitude.
	 */
	public void setBaseMagnitude ( double new_magnitude ) {
		base_magnitude = new_magnitude;
	}

	/**
	 * Gets the gradient.
	 * @return the gradient.
	 */
	public double getGradient ( ) {
		return gradient;
	}

	/**
	 * Sets the gradient.
	 * @param new_gradient the new gradient.
	 */
	public void setGradient ( double new_gradient ) {
		gradient = new_gradient;
	}

	/**
	 * Converts the specified pixel value into magnitude.
	 * @param value the pixel value to convert.
	 * @return the converted magnitude.
	 */
	public double convertToMagnitude ( double value ) {
		return base_magnitude + gradient * Math.log(value) / Math.log(Astro.MAG_STEP);
	}

	/**
	 * Converts the specified magnitude into pixel value.
	 * @param magnitude the magnitude to convert.
	 * @return the converted pixel value.
	 */
	public double convertToPixelValue ( double magnitude ) {
		return Math.pow(Astro.MAG_STEP, (magnitude - base_magnitude) / gradient);
	}

	/**
	 * Creates a <code>MagnitudeTranslationFormula</code> object from
	 * the <code>String</code> object which represents the formula.
	 * @param string the string which represents the formula.
	 * @return a new <code>MagnitudeTranslationFormula</code> object
	 * of the specified string.
	 */
	public static MagnitudeTranslationFormula create ( String string ) {
		MagnitudeTranslationFormula formula = new MagnitudeTranslationFormula();

		StringTokenizer st = new StringTokenizer(string);
		st.nextToken();		// magnitude
		st.nextToken();		// =
		formula.setBaseMagnitude(Double.parseDouble(st.nextToken()));
		st.nextToken();		// +
		formula.setGradient(Double.parseDouble(st.nextToken()));
		return formula;
	}

	/**
	 * Returns a string representation of the state of this object.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputString ( ) {
		return paramString();
	}

	/**
	 * Returns a raw string representation of the state of this object,
	 * for debugging use. It should be invoked from <code>toString</code>
	 * method of the subclasses.
	 * @return a string representation of the state of this object.
	 */
	protected String paramString ( ) {
		return "magnitude = " + base_magnitude + " + " + gradient + " log_2.5 ( value )";
	}

	/**
	 * Returns a string representation of the state of this object,
	 * for debugging use.
	 * @return a string representation of the state of this object.
	 */
	public String toString ( ) {
		return getClass().getName() + "[" + paramString() + "]";
	}
}
