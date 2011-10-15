/*
 * @(#)ProgressivePairMaker.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy.pairing;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.pixy.limiting_mag.DefaultLimitingValueEstimator;
import net.aerith.misao.pixy.ExaminationFailedException;

/**
 * The <code>ProgressivePairMaker</code> is a class to make pairs 
 * among two lists of stars progressively. First, it makes pairs 
 * roughly only based on the brightness. Then it makes pairs based on 
 * the similarity calculated using both the brightness and the 
 * position. Finally, it makes pairs precisly based on the position. 
 * <p>
 * The (x,y) position and magnitude of stars in the both lists must be
 * set properly. The (x,y) position in the first list and the 
 * magnitude in the second list are regarded as the base value. The 
 * (x,y) position in the second list and the magnitude in the first 
 * list will be changed after the operation.
 * <p>
 * After the operation, the two lists will be sorted. 
 * <p>
 * The elements in the first list must be <code>StarImage</code> 
 * objects.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 17
 */

public class ProgressivePairMaker extends Operation {
	/**
	 * The first list of stars.
	 */
	protected StarImageList first_list;

	/**
	 * The second list of stars.
	 */
	protected StarList second_list;

	/**
	 * The position map of the image.
	 */
	protected PositionMap image_map;

	/**
	 * The position map of the catalog.
	 */
	protected PositionMap catalog_map;

	/**
	 * The chart composition of catalog stars.
	 */
	protected ChartMapFunction cmf;

	/**
	 * The list of pairs.
	 */
	protected Vector pair_list = null;

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
	 * The type of a pair maker which represents the 
	 * <code>DefaultPairMaker</code>.
	 */
	private final static int PAIR_MAKER_DEFAULT = 0;

	/**
	 * The type of a pair maker which represents the 
	 * <code>BrightnessBasedPairMaker</code>.
	 */
	private final static int PAIR_MAKER_BRIGHTNESS_BASED = 1;

	/**
	 * The type of a pair maker which represents the 
	 * <code>SimilarityBasedPairMaker</code>.
	 */
	private final static int PAIR_MAKER_SIMILARITY_BASED = 2;

	/**
	 * The type of a pair maker which represents the 
	 * <code>PositionBasedPairMaker</code>.
	 */
	private final static int PAIR_MAKER_POSITION_BASED = 3;

	/**
	 * The type of a pair maker which means the pairing step is 
	 * final.
	 */
	private final static int PAIR_MAKER_FINAL = 4;

	/**
	 * Constructs a <code>ProgressivePairMaker</code> with two lists 
	 * of stars. 
	 * @param first_list  the first list of stars.
	 * @param second_list the second list of stars.
	 * @param cmf         the chart composition of catalog stars.
	 * @param image_map   the position map of the image.
	 * @param catalog_map the position map of the catalog.
	 */
	public ProgressivePairMaker ( StarImageList first_list, StarList second_list, ChartMapFunction cmf, PositionMap image_map, PositionMap catalog_map ) {
		this.first_list = first_list;
		this.second_list = second_list;
		this.cmf = cmf;
		this.image_map = image_map;
		this.catalog_map = catalog_map;
	}

	/**
	 * Sets the flag not to calculate the ditortion field.
	 */
	public void assumeFlat ( ) {
		assume_flat = true;
	}

	/**
	 * Fixes the limiting magnitude.
	 * @param limit_mag the limiting magnitude to fix.
	 */
	public void fixLimitingMagnitude ( double limit_mag ) {
		fix_limiting_mag = true;
		fixed_limit_mag = limit_mag;
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
	 * Gets the list of pairs.
	 * @return the list of pairs.
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
		pair_list = new Vector();

		first_list.sort();
		second_list.sort();

		double df_value = 0.0;
		double last_df_value = 0.0;
		int loop_count = 1;

		int pair_maker_type = PAIR_MAKER_DEFAULT;

		while (true) {
			switch (pair_maker_type) {
				case PAIR_MAKER_DEFAULT: {
					// First of all, it makes pairs roughly only in order to 
					// calculate magnitude of detected stars.

					double search_radius = (image_map.getWidth() > image_map.getHeight() ? image_map.getWidth() : image_map.getHeight()) / 40.0;
					if (search_radius < 15.0)
						search_radius = 15.0;

						// In order to avoid to make false pairs, only bright
						// catalog data are used.
					second_list.sort();
					StarList tmp_list = new StarList();
					int catalog_count = (int)((double)(50 * 2) * (catalog_map.getArea() / image_map.getArea()));
					if (catalog_count > second_list.size())
						catalog_count = second_list.size();
					for (int i = 0 ; i < catalog_count ; i++)
						tmp_list.addElement(second_list.elementAt(i));

					DefaultPairMaker maker = new DefaultPairMaker(first_list, tmp_list);
					maker.setSearchRadius(search_radius);
					maker.addMonitor(monitor_set);
					maker.operate();
					pair_list = maker.getPairList();
					break;
				}
				case PAIR_MAKER_BRIGHTNESS_BASED: {
					// First, it makes pairs roughly only based on the brightness.

					double search_radius = (image_map.getWidth() > image_map.getHeight() ? image_map.getWidth() : image_map.getHeight()) / 120.0;
					if (search_radius < 5.0)
						search_radius = 5.0;

					BrightnessBasedPairMaker maker = new BrightnessBasedPairMaker(first_list, second_list);
					maker.setSearchRadius(search_radius);
					maker.setAcceptableMagnitudeDifference(4.0);
					maker.addMonitor(monitor_set);
					maker.operate();
					pair_list = maker.getPairList();
					break;
				}
				case PAIR_MAKER_SIMILARITY_BASED: {
					// Then it makes pairs based on the similarity calculated 
					// using both the brightness and the position.

					double search_radius = (image_map.getWidth() > image_map.getHeight() ? image_map.getWidth() : image_map.getHeight()) / 200.0;
					if (search_radius < 3.0)
						search_radius = 3.0;

					SimilarityBasedPairMaker maker = new SimilarityBasedPairMaker(first_list, second_list);
					maker.setPositionError(search_radius);
					maker.setMagnitudeError(3.0);
					maker.addMonitor(monitor_set);
					maker.operate();
					pair_list = maker.getPairList();
					break;
				}
				case PAIR_MAKER_POSITION_BASED:
				case PAIR_MAKER_FINAL: {
					// Finally, it makes pairs precisly based on the position. 

					double search_radius = (image_map.getWidth() > image_map.getHeight() ? image_map.getWidth() : image_map.getHeight()) / 400.0;
					if (search_radius < 1.5)
						search_radius = 1.5;
					if (pair_maker_type == PAIR_MAKER_POSITION_BASED) {
						if (search_radius < 2.0)
							search_radius = 2.0;
					}

					PositionBasedPairMaker maker = new PositionBasedPairMaker(first_list, second_list);
					maker.setSearchRadius(search_radius);
					maker.addMonitor(monitor_set);
					maker.operate();
					pair_list = maker.getPairList();
					break;
				}
			}

			// Makes pairs between the bright single detected stars and
			// the single catalog data within the star image radius.
			if (pair_maker_type == PAIR_MAKER_FINAL) {
				// Copies only the pairs.
				Vector pair_list2 = new Vector();
				StarList first_list2 = new StarList();
				StarList second_list2 = new StarList();
				for (int i = 0 ; i < pair_list.size() ; i++) {
					StarPair pair = (StarPair)pair_list.elementAt(i);
					Star star1 = pair.getFirstStar();
					Star star2 = pair.getSecondStar();
					if (star1 != null  &&  star2 != null) {
						pair_list2.addElement(pair);
					} else if (star1 != null) {
						first_list2.addElement(star1);
					} else {
						second_list2.addElement(star2);
					}
				}

				// Makes pairs between only the single data.
				StarImageRadiusBasedPairMaker maker = new StarImageRadiusBasedPairMaker(first_list2, second_list2);
				maker.setSearchRadiusCoefficient(0.7);
				maker.setMinimumRadius(1.5);
				maker.setAcceptableMagnitudeDifference(4.0);
				maker.addMonitor(monitor_set);
				maker.operate();
				pair_list = maker.getPairList();

				// Adds the original pairs to the list.
				for (int i = 0 ; i < pair_list2.size() ; i++)
					pair_list.addElement(pair_list2.elementAt(i));
			}

			// Makes pairs between the bright single detected stars and
			// the bright single catalog data.
			if (pair_maker_type == PAIR_MAKER_POSITION_BASED  ||  pair_maker_type == PAIR_MAKER_FINAL) {
				// Calculates the limiting pixel value to detect based on the pairs.
				DefaultLimitingValueEstimator estimator = new DefaultLimitingValueEstimator(first_list);
				estimator.setMovingAverageWindowSize(19);
				estimator.setMinimumWindowSize(5);
				estimator.setThresholdRatio(0.7);
				estimator.setMode(DefaultLimitingValueEstimator.MODE_BASED_ON_MAGNITUDE);
				estimator.addMonitor(monitor_set);
				estimator.operate();
				double limit_mag = estimator.getThreshold();

				// Fixes the limiting magnitude if neccessary.
				if (fix_limiting_mag) {
					monitor_set.addMessage("[Fixed limiting magnitude]");
					monitor_set.addMessage("Limiting magnitude: " + Format.formatDouble(limit_mag, 5, 2) + " -> " + Format.formatDouble(fixed_limit_mag, 5, 2));
					monitor_set.addSeparator();

					limit_mag = fixed_limit_mag;
				}

				SupplementaryPairMaker maker = new SupplementaryPairMaker(first_list, second_list, cmf);
				maker.setThreshold(limit_mag - 2.0);
				maker.setMinimumSearchRadius(5.0);
				maker.addMonitor(monitor_set);
				maker.operate();
				pair_list = maker.getPairList();
			}

			// Counts number of pairs.
			int pair_count = 0;
			for (int i = 0 ; i < pair_list.size() ; i++) {
				StarPair pair = (StarPair)pair_list.elementAt(i);
				Star star1 = pair.getFirstStar();
				Star star2 = pair.getSecondStar();
				if (star1 != null  &&  star2 != null)
					pair_count++;
			}

			monitor_set.addMessage("[Pairing result]");
			monitor_set.addMessage("Number of pairs: " + pair_count);
			monitor_set.addSeparator();

			// Ends the progressive pair making.
			if (pair_maker_type == PAIR_MAKER_FINAL) {
				return;
			}

			// In order to avoid bad influence of false pairs, 
			// only some bright pairs are remained.
			if (pair_maker_type == PAIR_MAKER_DEFAULT) {
				if (pair_count > 50)
					pair_count = 50;
			} else {
				pair_count /= 3;
				if (pair_count < 100)
					pair_count = 100;
			}
			int count = 0;
			Vector new_pair_list = new Vector();
			for (int i = 0 ; i < pair_list.size() ; i++) {
				StarPair pair = (StarPair)pair_list.elementAt(i);
				Star star1 = pair.getFirstStar();
				Star star2 = pair.getSecondStar();
				if (star1 != null  &&  star2 != null  &&  count < pair_count) {
					new_pair_list.addElement(pair);
					count++;
					if (count >= pair_count)
						break;
				}
			}
			pair_list = new_pair_list;

			// Converts the value of the detected stars to the magnitude.
			MagnitudeTranslationFormula formula = new MagnitudeTranslationFormula(pair_list, MagnitudeTranslationFormula.MODE_CALCULATE_GRADIENT);
			monitor_set.addMessage("[Magnitude translation formula]");
			monitor_set.addMessage("Calculated formula: " + formula.getOutputString());
			if (formula.getGradient() > -0.5  ||  formula.getGradient() < -1.5) {
				formula = new MagnitudeTranslationFormula(pair_list, MagnitudeTranslationFormula.MODE_FIX_GRADIENT);
				monitor_set.addMessage("Fixed formula: " + formula.getOutputString());
			}
			monitor_set.addSeparator();
			first_list.setMagnitude(formula);

			// Calculates image mapping.
			MapFunction map_function = new MapFunction(pair_list);
			cmf = cmf.map(map_function.inverse());
			monitor_set.addMessage("[Image mapping]");
			monitor_set.addMessage("R.A. and Decl. of the center: " + cmf.getCenterCoor().getOutputStringTo100mArcsecWithUnit());
			monitor_set.addMessage("Field of view: " + Format.formatAngularSize(image_map.getWidth() / cmf.getScaleUnitPerDegree(), image_map.getHeight() / cmf.getScaleUnitPerDegree()));
			monitor_set.addMessage("Position angle of up: " + Format.formatDouble(cmf.getPositionAngle(), 6, 3));

			// Resets the (x,y) position of the catalog data without adjusting the distortion field.
			second_list.mapCoordinatesToXY(cmf);

			// Calculates the distortion field.
			DistortionField df = new DistortionField();
			if (pair_maker_type != PAIR_MAKER_DEFAULT  &&  assume_flat == false) {
				df = new DistortionField(pair_list);
				Position d_pos1 = df.getValue(image_map.getTopLeftCorner());
				Position d_pos2 = df.getValue(image_map.getTopRightCorner());
				Position d_pos3 = df.getValue(new Position());
				Position d_pos4 = df.getValue(image_map.getBottomLeftCorner());
				Position d_pos5 = df.getValue(image_map.getBottomRightCorner());

				monitor_set.addMessage("[Distortion Field]");
				monitor_set.addMessage(Format.formatDouble(d_pos1.getX(), 4, 2) + "," + Format.formatDouble(d_pos1.getY(), 4, 2) + "         " + Format.formatDouble(d_pos2.getX(), 4, 2) + "," + Format.formatDouble(d_pos2.getY(), 4, 2));
				monitor_set.addMessage("         " + Format.formatDouble(d_pos3.getX(), 4, 2) + "," + Format.formatDouble(d_pos3.getY(), 4, 2));
				monitor_set.addMessage(Format.formatDouble(d_pos4.getX(), 4, 2) + "," + Format.formatDouble(d_pos4.getY(), 4, 2) + "         " + Format.formatDouble(d_pos5.getX(), 4, 2) + "," + Format.formatDouble(d_pos5.getY(), 4, 2));

				// Calculates the largest value of distortion.
				df_value = d_pos1.getX() * d_pos1.getX() + d_pos1.getY() * d_pos1.getY();
				double df_value2 = d_pos2.getX() * d_pos2.getX() + d_pos2.getY() * d_pos2.getY();
				if (df_value < df_value2)
					df_value = df_value2;
				df_value2 = d_pos3.getX() * d_pos3.getX() + d_pos3.getY() * d_pos3.getY();
				if (df_value < df_value2)
					df_value = df_value2;
				df_value2 = d_pos4.getX() * d_pos4.getX() + d_pos4.getY() * d_pos4.getY();
				if (df_value < df_value2)
					df_value = df_value2;
				df_value2 = d_pos5.getX() * d_pos5.getX() + d_pos5.getY() * d_pos5.getY();
				if (df_value < df_value2)
					df_value = df_value2;

				// When the distortion field is extremely big, it must be erroneous.
				// Then the distortion correction is not applied.
				// The threshold to judge to be erroneous is 3 times of experiential value.
				double fov = image_map.getWidth() / cmf.getScaleUnitPerDegree();
				if (fov < image_map.getHeight() / cmf.getScaleUnitPerDegree())
					fov = image_map.getHeight() / cmf.getScaleUnitPerDegree();
				double dv_limit = Math.sqrt(fov * fov * fov) / 21.3 * cmf.getScaleUnitPerDegree();
				if (dv_limit < 2)
					dv_limit = 2;
				if (df_value > dv_limit * dv_limit) {
					df = new DistortionField();
					df_value = 0;
					monitor_set.addMessage("Bad distortion field -- canceled.");

					monitor_set.addSeparator();

					throw new ExaminationFailedException();
				}

				monitor_set.addSeparator();
			}

			// Sets the R.A. and Decl. of the detected stars.
			first_list.mapXYToCoordinates(cmf, df);
			first_list.enableOutputCoordinates();

			// Sets the (x,y) position of the catalog data.
			second_list.mapCoordinatesToXY(cmf, df);

			// Calculates errors of astrometry.
			AstrometricError err = new AstrometricError(pair_list);
			monitor_set.addMessage("Astrometric errors: " + Format.formatDouble(err.getRA() * 3600, 5, 3).trim() + "\" " + Format.formatDouble(err.getDecl() * 3600, 5, 3).trim() + "\"");

			monitor_set.addSeparator();

			switch (pair_maker_type) {
				case PAIR_MAKER_DEFAULT: {
					pair_maker_type = PAIR_MAKER_BRIGHTNESS_BASED;
					break;
				}
				case PAIR_MAKER_BRIGHTNESS_BASED: {
					pair_maker_type = PAIR_MAKER_SIMILARITY_BASED;
					break;
				}
				case PAIR_MAKER_SIMILARITY_BASED: {
					pair_maker_type = PAIR_MAKER_POSITION_BASED;
					break;
				}
				case PAIR_MAKER_POSITION_BASED: {
					if (df_value / 1.2 < last_df_value  &&  last_df_value < df_value * 1.2)
						pair_maker_type = PAIR_MAKER_FINAL;
					break;
				}
				default: {
					return;
				}
			}

			loop_count++;
			if (loop_count >= 8) {
				if (assume_flat) {
					pair_maker_type = PAIR_MAKER_FINAL;
				} else {
					if (df_value / 2.4 < last_df_value  &&  last_df_value < df_value * 2.4) {
						if (loop_count >= 15)
							pair_maker_type = PAIR_MAKER_FINAL;
					} else {
						monitor_set.addMessage("Bad distortion field -- canceled.");
						monitor_set.addSeparator();

						throw new ExaminationFailedException();
					}
				}
			}

			last_df_value = df_value;
		}
	}
}
