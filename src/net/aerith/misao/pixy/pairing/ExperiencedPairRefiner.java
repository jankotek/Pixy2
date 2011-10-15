/*
 * @(#)ExperiencedPairRefiner.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy.pairing;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.pixy.limiting_mag.DefaultLimitingValueEstimator;

/**
 * The <code>ExperiencedPairRefiner</code> is a class to refine the 
 * specified list of pairs at the very last step of the examination of
 * an image with star catalog. It calculates the limiting magnitude,
 * removes too faint stars, calculates R.A., Decl. and magnitude of 
 * all detected stars, and calculates (x,y) of all catalog data.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class ExperiencedPairRefiner extends Operation {
	/**
	 * The list of pairs.
	 */
	protected Vector pair_list;

	/**
	 * The position map of the image.
	 */
	protected PositionMap image_map;

	/**
	 * The chart composition of catalog stars.
	 */
	protected ChartMapFunction cmf;

	/**
	 * The distortion field.
	 */
	protected DistortionField df;

	/**
	 * The magnitude translation formula.
	 */
	protected MagnitudeTranslationFormula mag_formula;

	/**
	 * The limiting magnitude.
	 */
	protected double limit_mag = 0.0;

	/**
	 * The upper-limit magnitude.
	 */
	protected double upper_limit_mag = 0.0;

	/**
	 * The astrometric error.
	 */
	protected AstrometricError astrometric_error;

	/**
	 * The photometric error.
	 */
	protected PhotometricError photometric_error;

	/**
	 * True when to fix the gradient of the magnitude translation 
	 * formula as -1.
	 */
	protected boolean fix_mag_formula_gradient = false;

	/**
	 * True when not to calculate the distortion field.
	 */
	protected boolean assume_flat = false;

	/**
	 * True when not to calculate the limiting magnitude automatically.
	 */
	protected boolean fix_limiting_mag = false;

	/**
	 * The fixed limiting magnitude.
	 */
	protected double fixed_limit_mag = 0.0;

	/**
	 * The fixed upper-limit magnitude.
	 */
	protected double fixed_upper_limit_mag = 0.0;

	/**
	 * Constructs a <code>ExperiencedPairRefiner</code> with a list of 
	 * pairs between detected stars and catalog data.
	 * @param pair_list the list of pairs.
	 * @param cmf       the chart composition of catalog stars.
	 * @param map       the position map of the image.
	 */
	public ExperiencedPairRefiner ( Vector pair_list, ChartMapFunction cmf, PositionMap map ) {
		this.pair_list = pair_list;
		this.cmf = cmf;
		this.image_map = map;
	}

	/**
	 * Sets the flag to fix the gradient of the magnitude translation 
	 * formula as -1.
	 */
	public void fixMagnitudeTranslationFormulaGradient ( ) {
		fix_mag_formula_gradient = true;
	}

	/**
	 * Sets the flag not to calculate the ditortion field.
	 */
	public void assumeFlat ( ) {
		assume_flat = true;
	}

	/**
	 * Fixes the limiting magnitude.
	 * @param limit_mag       the limiting magnitude to fix.
	 * @param upper_limit_mag the upper-limit magnitude to fix.
	 */
	public void fixLimitingMagnitude ( double limit_mag, double upper_limit_mag ) {
		fix_limiting_mag = true;

		fixed_limit_mag = limit_mag;
		fixed_upper_limit_mag = upper_limit_mag;

		if (fixed_limit_mag < fixed_upper_limit_mag) {
			double mag = fixed_limit_mag;
			fixed_limit_mag = fixed_upper_limit_mag;
			fixed_upper_limit_mag = mag;
		}
	}

	/**
	 * Gets the refined chart map function. This method must be 
	 * invoked after <code>operate</code> method is invoked.
	 * @return the refined chart map function.
	 */
	public ChartMapFunction getChartMapFunction ( ) {
		return cmf;
	}

	/**
	 * Gets the refined distortion field. This method must be invoked
	 * after <code>operate</code> method is invoked.
	 * @return the refined distortion field.
	 */
	public DistortionField getDistortionField ( ) {
		return df;
	}

	/**
	 * Gets the refined magnitude translation formula. This method 
	 * must be invoked after <code>operate</code> method is invoked.
	 * @return the refined magnitude translation formula.
	 */
	public MagnitudeTranslationFormula getMagnitudeTranslationFormula ( ) {
		return mag_formula;
	}

	/**
	 * Gets the refined limiting magnitude. This method must be
	 * invoked after <code>operate</code> method is invoked.
	 * @return the refined limiting magnitude.
	 */
	public double getLimitingMagnitude ( ) {
		return limit_mag;
	}

	/**
	 * Gets the upper-limit magnitude. This method must be invoked
	 * after <code>operate</code> method is invoked.
	 * @return the upper-limit magnitude.
	 */
	public double getUpperLimitMagnitude ( ) {
		return upper_limit_mag;
	}

	/**
	 * Gets the astrometric error. This method must be invoked after
	 * <code>operate</code> method is invoked.
	 * @return the astrometric error.
	 */
	public AstrometricError getAstrometricError ( ) {
		return astrometric_error;
	}

	/**
	 * Gets the photometric error. This method must be invoked after
	 * <code>operate</code> method is invoked.
	 * @return the photometric error.
	 */
	public PhotometricError getPhotometricError ( ) {
		return photometric_error;
	}

	/**
	 *Gets the refined list of pairs.
	 * @return the refined list of pairs.
	 */
	public Vector getPairList ( ) {
		return pair_list;
	}

	/**
	 * Returns true if the operation is ready to start.
	 * @return true if the operation is ready to start.
	 */
	public boolean ready ( ) {
		return true;
	}

	/**
	 * Operates.
	 * @exception Exception if an error occurs.
	 */
	public void operate ( )
		throws Exception
	{
		if (pair_list == null  ||  image_map == null)
			return;

		// Creates the list of detected stars and that of catalog data.
		StarImageList list_detected = new StarImageList();
		StarList list_catalog = new StarList();
		for (int i = 0 ; i < pair_list.size() ; i++) {
			StarPair pair = (StarPair)pair_list.elementAt(i);
			Star star1 = pair.getFirstStar();
			Star star2 = pair.getSecondStar();
			if (star1 != null  &&  star2 != null) {
				list_detected.addElement(star1);
				list_catalog.addElement(star2);
			} else if (star1 != null) {
				list_detected.addElement(star1);
			} else {
				list_catalog.addElement(star2);
			}
		}
		list_detected.sort();
		list_catalog.sort();

		// Calculates the limiting pixel value to detect based on the pairs.
		DefaultLimitingValueEstimator estimator = new DefaultLimitingValueEstimator(list_detected);
		estimator.setMovingAverageWindowSize(19);
		estimator.setMinimumWindowSize(5);
		estimator.setThresholdRatio(0.7);
		estimator.setMode(DefaultLimitingValueEstimator.MODE_BASED_ON_PIXEL_VALUE);
		estimator.addMonitor(monitor_set);
		estimator.operate();
		double limit_value = estimator.getThreshold();

		// Creates a pair list of only bright detected stars,
		// just in order to calculate the magnitude translation formula.
		Vector bright_pair_list = new Vector();
		for (int i = 0 ; i < pair_list.size() ; i++) {
			StarPair pair = (StarPair)pair_list.elementAt(i);
			StarImage star1 = (StarImage)pair.getFirstStar();
			Star star2 = pair.getSecondStar();
			if (star1 != null  &&  star2 != null  &&  star1.getValue() >= limit_value)
				bright_pair_list.addElement(pair);
		}
		if (bright_pair_list.size() < 5) {
			bright_pair_list = new Vector();
			for (int i = 0 ; i < list_detected.size()  &&  bright_pair_list.size() < 5 ; i++) {
				Star star = (Star)list_detected.elementAt(i);
				StarPair pair = star.getPair();
				if (pair.getFirstStar() != null  &&  pair.getSecondStar() != null)
					bright_pair_list.addElement(pair);
			}
		}

		// Converts the value of the detected stars to the magnitude.
		mag_formula = new MagnitudeTranslationFormula(bright_pair_list, MagnitudeTranslationFormula.MODE_CALCULATE_GRADIENT);
		monitor_set.addMessage("[Magnitude translation formula]");
		monitor_set.addMessage("Calculated formula: " + mag_formula.getOutputString());
		if (fix_mag_formula_gradient  ||  mag_formula.getGradient() > -0.5  ||  mag_formula.getGradient() < -1.5) {
			mag_formula = new MagnitudeTranslationFormula(bright_pair_list, MagnitudeTranslationFormula.MODE_FIX_GRADIENT);
			monitor_set.addMessage("Fixed formula: " + mag_formula.getOutputString());
		}
		monitor_set.addSeparator();
		list_detected.setMagnitude(mag_formula);

		// Adjusts systematic shift of magnitude.
/*
		MagnitudeAdjustment ma = new MagnitudeAdjustment(bright_pair_list);
		if (ma.isProper())
			list_detected.adjustMagnitude(ma);
		monitor_set.addMessage("[Magnitude adjustment]");
		double brightest_mag = mag_formula.convertToMagnitude(((StarImage)list_detected.elementAt(0)).getValue());
		double faintest_mag = mag_formula.convertToMagnitude(((StarImage)list_detected.elementAt(list_detected.size() - 1)).getValue());
		for (int m = (int)(brightest_mag * 2.0) ; m <= (int)(faintest_mag * 2.0) + 1 ; m++) {
			double mag = (double)m / 2.0;
			double adjusted_mag = ma.adjust(mag);
			monitor_set.addMessage(Format.formatDouble(mag, 4, 2) + " -> " + Format.formatDouble(adjusted_mag, 4, 2) + " : " + Format.formatDouble(adjusted_mag - mag, 4, 2));
		}
		if (ma.isProper())
			monitor_set.addMessage("Properly adjusted.");
		else
			monitor_set.addMessage("Incorrect. Not adjusted.");
		monitor_set.addSeparator();
*/

		// Converts the limiting pixel value to the limiting magnitude.
		limit_mag = mag_formula.convertToMagnitude(limit_value);
/*
		if (ma.isProper())
			limit_mag = ma.adjust(limit_mag);
*/

		// Creates a temporary list of catalog data only within the image field,
		// just in order to calculate the upper-limit magnitude.
		StarList temp_list_catalog = new StarList();
		for (int i = 0 ; i < list_catalog.size() ; i++) {
			Star star = (Star)list_catalog.elementAt(i);
			if (star.getX() < image_map.getTopLeftCorner().getX()  ||
				star.getX() > image_map.getBottomRightCorner().getX()  ||
				star.getY() < image_map.getTopLeftCorner().getY()  ||
				star.getY() > image_map.getBottomRightCorner().getY()) {
			} else {
				temp_list_catalog.addElement(star);
			}
		}

		// Calculates the upper-limit magnitude based on the pairs.
		estimator = new DefaultLimitingValueEstimator(temp_list_catalog);
		estimator.setMovingAverageWindowSize(19);
		estimator.setMinimumWindowSize(5);
		estimator.setThresholdRatio(0.7);
		estimator.setPolicy(DefaultLimitingValueEstimator.POLICY_UPPERLIMIT);
		estimator.setMode(DefaultLimitingValueEstimator.MODE_BASED_ON_MAGNITUDE);
		estimator.addMonitor(monitor_set);
		estimator.operate();
		temp_list_catalog = null;
		upper_limit_mag = estimator.getThreshold();

		monitor_set.addMessage("[Limiting magnitude]");
		monitor_set.addMessage("Limiting pixel value: " + limit_value);
		if (limit_mag < upper_limit_mag) {
			// Checks if the limiting magnitude can be fainter, or the upper limit
			// magnitude is too faint. When more than half of the faint data between
			// the two magnitudes are paired, the limiting magnitude can be fainter.
			// Otherwise the upper limit is too faint.
			int NEW_count = 0;
			int STR_count = 0;
			for (int i = 0 ; i < pair_list.size() ; i++) {
				StarPair pair = (StarPair)pair_list.elementAt(i);
				StarImage star1 = (StarImage)pair.getFirstStar();
				Star star2 = pair.getSecondStar();
				if (star1 != null  &&  star1.getMag() >= limit_mag  &&  star1.getMag() <= upper_limit_mag) {
					if (star2 == null)
						NEW_count++;
					else
						STR_count++;
				}
			}

			if (NEW_count < STR_count) {
				monitor_set.addMessage("Limiting magnitude: " + Format.formatDouble(limit_mag, 5, 2) + " -> " + Format.formatDouble(upper_limit_mag, 5, 2));
				monitor_set.addMessage("Upper-limit magnitude: " + Format.formatDouble(upper_limit_mag, 5, 2));

				limit_mag = upper_limit_mag;
			} else {
				monitor_set.addMessage("Limiting magnitude: " + Format.formatDouble(limit_mag, 5, 2));
				monitor_set.addMessage("Upper-limit magnitude: " + Format.formatDouble(upper_limit_mag, 5, 2) + " -> " + Format.formatDouble(limit_mag, 5, 2));

				upper_limit_mag = limit_mag;
			}
		} else {
			monitor_set.addMessage("Limiting magnitude: " + Format.formatDouble(limit_mag, 5, 2));
			monitor_set.addMessage("Upper-limit magnitude: " + Format.formatDouble(upper_limit_mag, 5, 2));
		}

		// When the number of NEWs becomes too much larger than
		// the number of STRs, the limiting magnitude must be 
		// illegally too faint, and will be set brighter.
		int NEW_count = 0;
		int STR_count = 0;
		Vector tmp_list = new Vector();
		for (int i = 0 ; i < pair_list.size() ; i++) {
			StarPair pair = (StarPair)pair_list.elementAt(i);
			StarImage star1 = (StarImage)pair.getFirstStar();
			Star star2 = pair.getSecondStar();
			if (star1 != null  &&  star1.getMag() < limit_mag) {
				tmp_list.addElement(star1);
				if (star2 == null)
					NEW_count++;
				else
					STR_count++;
			}
		}
		if (NEW_count >= STR_count  &&  STR_count >= 10) {
			monitor_set.addMessage("Number of NEWs: " + NEW_count);
			monitor_set.addMessage("Number of STRs: " + STR_count);

			Array array = new Array(tmp_list.size());
			for (int i = 0 ; i < tmp_list.size() ; i++) {
				StarImage star_image = (StarImage)tmp_list.elementAt(i);
				array.set(i, star_image.getMag());
			}
			ArrayIndex index = array.sortDescendant();
			for (int i = 0 ; i < tmp_list.size() ; i++) {
				StarImage star_image = (StarImage)tmp_list.elementAt(index.get(i));
				if (star_image.getPair().getSecondStar() == null)
					NEW_count--;
				else
					STR_count--;
				limit_mag = star_image.getMag();
				if (NEW_count < STR_count  ||  STR_count < 10)
					break;
			}

			monitor_set.addMessage("Limiting magnitude: " + Format.formatDouble(limit_mag, 5, 2));

			if (upper_limit_mag > limit_mag) {
				upper_limit_mag = limit_mag;
				monitor_set.addMessage("Upper-limit magnitude: " + Format.formatDouble(upper_limit_mag, 5, 2));
			}

			monitor_set.addMessage("Number of NEWs: " + NEW_count);
			monitor_set.addMessage("Number of STRs: " + STR_count);
		}
		tmp_list = null;

		// Calculates errors of photometry.
		photometric_error = new PhotometricError(bright_pair_list);
		monitor_set.addMessage("Photometric errors: " + Format.formatDouble(photometric_error.getError(), 5, 2));
		monitor_set.addSeparator();

		// Fixes the limiting magnitude if neccessary.
		if (fix_limiting_mag) {
			monitor_set.addMessage("[Fixed limiting magnitude]");
			monitor_set.addMessage("Limiting magnitude: " + Format.formatDouble(limit_mag, 5, 2) + " -> " + Format.formatDouble(fixed_limit_mag, 5, 2));
			monitor_set.addMessage("Upper-limiting magnitude: " + Format.formatDouble(upper_limit_mag, 5, 2) + " -> " + Format.formatDouble(fixed_upper_limit_mag, 5, 2));
			monitor_set.addSeparator();

			limit_mag = fixed_limit_mag;
			upper_limit_mag = fixed_upper_limit_mag;
		}

		// Removes detected stars fainter than the limiting magnitude.
		list_detected = new StarImageList();
		Vector l = new Vector();
		for (int i = 0 ; i < pair_list.size() ; i++) {
			StarPair pair = (StarPair)pair_list.elementAt(i);
			StarImage star1 = (StarImage)pair.getFirstStar();
			Star star2 = pair.getSecondStar();
			if (star1 != null  &&  star1.getMag() > limit_mag) {
				if (star2 != null)
					l.addElement(new StarPair(null, star2));
			} else {
				l.addElement(pair);
				if (star1 != null)
					list_detected.addElement(star1);
			}
		}
		pair_list = l;

		// Removes catalog data fainter than the upper-limit magnitude.
		list_catalog = new StarList();
		l = new Vector();
		StarList list_single_catalog = new StarList();
		StarList list_paired_catalog = new StarList();
		for (int i = 0 ; i < pair_list.size() ; i++) {
			StarPair pair = (StarPair)pair_list.elementAt(i);
			Star star1 = pair.getFirstStar();
			Star star2 = pair.getSecondStar();
			if (star1 == null  &&  star2 != null  &&  star2.getMag() > upper_limit_mag) {
			} else {
				l.addElement(pair);
				if (star2 != null) {
					list_catalog.addElement(star2);
					if (star1 == null)
						list_single_catalog.addElement(star2);
					else
						list_paired_catalog.addElement(star2);
				}
			}
		}
		pair_list = l;

		// Blends close catalog data.
		PositionMap map = new PositionMap(list_paired_catalog);
		map.divide(40,40);
		list_catalog = new StarList();
		for (int i = 0 ; i < list_single_catalog.size() ; i++) {
			Star star2 = (Star)list_single_catalog.elementAt(i);
			try {
				l = map.getPartialListWithinRadius(star2, 10.0);

				Star star1 = null;
				double best_dist = 99.9;
				for (int j = 0 ; j < l.size() ; j++) {
					Star s = (Star)l.elementAt(j);
					double dist = Math.sqrt((star2.getX() - s.getX()) * (star2.getX() - s.getX()) + (star2.getY() - s.getY()) * (star2.getY() - s.getY()));

					double radius = 0.0;
					double dist2 = 999.9;
					if (s.getPair() != null  &&  s.getPair().getFirstStar() != null) {
						StarImage star_image = (StarImage)s.getPair().getFirstStar();
						radius = star_image.getRadius();
						dist2 = Math.sqrt((star2.getX() - star_image.getX()) * (star2.getX() - star_image.getX()) + (star2.getY() - star_image.getY()) * (star2.getY() - star_image.getY()));
					}

					if (dist < 1.4  ||  dist < radius * 1.5  ||  dist2 < radius * 1.5) {
						if (star1 == null  ||  best_dist > dist) {
							star1 = s;
							best_dist = dist;
						}
					}
				}

				if (star1 == null) {
					map.addPosition(star2);
				} else {
					pair_list.remove(star2.getPair());

					if (star1 instanceof BlendingStar) {
						((BlendingStar)star1).append(star2);
					} else {
						BlendingStar blending_star = new BlendingStar(star1);
						blending_star.append(star2);
						map.removePosition(star1);
						map.addPosition(blending_star);

						StarPair pair = star1.getPair();
						StarPair new_pair = new StarPair(pair.getFirstStar(), blending_star);
						pair_list.remove(pair);
						pair_list.addElement(new_pair);
					}
				}
			} catch ( OutOfBoundsException exception ) {
				list_catalog.addElement(star2);
			}
		}
		l = map.getAllPositions();
		for (int i = 0 ; i < l.size() ; i++)
			list_catalog.addElement(l.elementAt(i));

		// Calculates accurate image mapping.
		MapFunction map_function = new MapFunction(pair_list, df);
		cmf = cmf.map(map_function.inverse());
		monitor_set.addMessage("[Accurate image mapping]");
		monitor_set.addMessage("R.A. and Decl. of the center: " + cmf.getCenterCoor().getOutputStringTo100mArcsecWithUnit());
		monitor_set.addMessage("Field of view: " + Format.formatAngularSize(image_map.getWidth() / cmf.getScaleUnitPerDegree(), image_map.getHeight() / cmf.getScaleUnitPerDegree()));
		monitor_set.addMessage("Position angle of up: " + Format.formatDouble(cmf.getPositionAngle(), 6, 3));

		// Resets the (x,y) position of the catalog data without adjusting the distortion field.
		list_catalog.mapCoordinatesToXY(cmf);

		// Calculates the distortion field.
		if (assume_flat)
			df = new DistortionField();
		else
			df = new DistortionField(pair_list);
		monitor_set.addMessage("[Distortion Field]");
		Position[] d_pos = new Position[5];
		d_pos[1] = df.getValue(image_map.getTopLeftCorner());
		d_pos[2] = df.getValue(image_map.getTopRightCorner());
		monitor_set.addMessage(Format.formatDouble(d_pos[1].getX(), 4, 2) + "," + Format.formatDouble(d_pos[1].getY(), 4, 2) + "         " + Format.formatDouble(d_pos[2].getX(), 4, 2) + "," + Format.formatDouble(d_pos[2].getY(), 4, 2));
		d_pos[0] = df.getValue(new Position());
		monitor_set.addMessage("         " + Format.formatDouble(d_pos[0].getX(), 4, 2) + "," + Format.formatDouble(d_pos[0].getY(), 4, 2));
		d_pos[3] = df.getValue(image_map.getBottomLeftCorner());
		d_pos[4] = df.getValue(image_map.getBottomRightCorner());
		monitor_set.addMessage(Format.formatDouble(d_pos[3].getX(), 4, 2) + "," + Format.formatDouble(d_pos[3].getY(), 4, 2) + "         " + Format.formatDouble(d_pos[4].getX(), 4, 2) + "," + Format.formatDouble(d_pos[4].getY(), 4, 2));
		monitor_set.addSeparator();

		// Sets the R.A. and Decl. of the detected stars.
		list_detected.mapXYToCoordinates(cmf, df);
		list_detected.enableOutputCoordinates();

		// Sets the (x,y) position of the catalog data.
		list_catalog.mapCoordinatesToXY(cmf, df);

		// Calculates errors of astrometry.
		astrometric_error = new AstrometricError(pair_list);
		monitor_set.addMessage("Astrometric errors: " + Format.formatDouble(astrometric_error.getRA() * 3600, 5, 3).trim() + "\" " + Format.formatDouble(astrometric_error.getDecl() * 3600, 5, 3).trim() + "\"");

		monitor_set.addSeparator();

		// Removes catalog data out of the image.
		list_catalog = new StarList();
		l = new Vector();
		for (int i = 0 ; i < pair_list.size() ; i++) {
			StarPair pair = (StarPair)pair_list.elementAt(i);
			Star star1 = pair.getFirstStar();
			Star star2 = pair.getSecondStar();
			if (star1 == null  &&  star2 != null  &&
				(star2.getX() < image_map.getTopLeftCorner().getX()  ||
				 star2.getX() > image_map.getBottomRightCorner().getX()  ||
				 star2.getY() < image_map.getTopLeftCorner().getY()  ||
				 star2.getY() > image_map.getBottomRightCorner().getY())) {
			} else {
				l.addElement(pair);
				if (star2 != null)
					list_catalog.addElement(star2);
			}
		}
		pair_list = l;

		// Makes pairs between the single detected stars and the single
		// catalog data.
		SupplementaryPairMaker maker = new SupplementaryPairMaker(list_detected, list_catalog, cmf);
		maker.setThreshold(99.9);
		maker.setMinimumSearchRadius(1.5);
		maker.addMonitor(monitor_set);
		maker.operate();
		pair_list = maker.getPairList();
	}
}
