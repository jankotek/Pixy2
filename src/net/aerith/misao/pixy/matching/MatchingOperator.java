/*
 * @(#)MatchingOperator.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy.matching;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.image.*;
import net.aerith.misao.catalog.io.CatalogReader;
import net.aerith.misao.pixy.InteractiveCatalogReader;

/**
 * The <code>MatchingOperator</code> is a class to operate matching
 * process. This invokes the <code>DefaultMatchingSolver#run</code>.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 17
 */

public class MatchingOperator extends Operation {
	/**
	 * The content pane. In the case of null, the matching process is
	 * operated without GUI components.
	 */
	protected Container pane = null;

	/**
	 * The image.
	 */
	protected MonoImage image;

	/**
	 * The catalog reader.
	 */
	protected CatalogReader catalog_reader;

	/**
	 * The R.A. and Decl. of the center.
	 */
	protected Coor center_coor;

	/**
	 * The horizontal field of view in degree.
	 */
	protected double fov_width = 1.0;

	/**
	 * The vertical field of view in degree.
	 */
	protected double fov_height = 1.0;

	/**
	 * The position angle of up.
	 */
	protected double position_angle_of_up = 0.0;

	/**
	 * The list of detected stars.
	 */
	protected StarImageList list_detected;

	/**
	 * The list of catalog stars.
	 */
	protected StarList list_catalog;

	/**
	 * The chart composition of catalog stars.
	 */
	protected ChartMapFunction cmf;

	/**
	 * The limiting_magnitude.
	 */
	protected double limit_mag;

	/**
	 * The matching operation mode.
	 */
	protected int mode = MODE_UNCERTAIN;

	/**
	 * The matching operation mode number which indicates the initial
	 * R.A. and Decl., field of view, position angle are uncertain.
	 */
	public final static int MODE_UNCERTAIN = 0;

	/**
	 * The matching operation mode number which indicates the initial
	 * R.A. and Decl., field of view, position angle are semi accurate.
	 */
	public final static int MODE_SEMI_ACCURATE = 1;

	/**
	 * The matching operation mode number which indicates the initial
	 * R.A. and Decl., field of view, position angle are accurate.
	 */
	public final static int MODE_ACCURATE = 2;

	/**
	 * The judgement mode.
	 */
	protected int judge = JUDGEMENT_NORMAL;

	/**
	 * The judgement mode number which indicates the normal threshold.
	 */
	public final static int JUDGEMENT_NORMAL = 0;

	/**
	 * The judgement mode number which indicates to loosen the 
	 * condition to accept the matching result.
	 */
	public final static int JUDGEMENT_LOOSE = 1;

	/**
	 * Constructs a <code>MatchingOperator</code>.
	 * @param image                the image.
	 * @param catalog_reader       the catalog reader.
	 * @param catalog_path         the catalog path separated by the 
	 * system dependent path separator.
	 * @param center_coor          the R.A. and Decl. of the center.
	 * @param fov_width            the horizontal field of view in 
	 * degree.
	 * @param fov_height           the vertical field of view in 
	 * degree.
	 * @param position_angle_of_up the position angle of up.
	 * @param list_detected        the list of detected stars.
	 */
	public MatchingOperator ( MonoImage image,
							  CatalogReader catalog_reader,
							  String catalog_path,
							  Coor center_coor,
							  double fov_width,
							  double fov_height,
							  double position_angle_of_up,
							  StarImageList list_detected )
	{
		this.image = image;
		this.catalog_reader = catalog_reader;
		this.center_coor = center_coor;
		this.fov_width = fov_width;
		this.fov_height = fov_height;
		this.position_angle_of_up = position_angle_of_up;
		this.list_detected = list_detected;

		if (catalog_path != null) {
			String[] paths = Format.separatePath(catalog_path);
			for (int i = 0 ; i < paths.length ; i++) {
				try {
					catalog_reader.addURL(new File(paths[i]).toURI().toURL());
				} catch ( MalformedURLException exception ) {
					System.err.println(exception);
				}
			}
		}
	}

	/**
	 * Enables the interactive catalog reading.
	 * @param pane the pane.
	 */
	public void enableInteractive ( Container pane ) {
		this.pane = pane;
	}

	/**
	 * Gets the list of catalog stars.
	 * @return the list of catalog stars.
	 */
	public StarList getCatalogList ( ) {
		return list_catalog;
	}

	/**
	 * Gets the chart composition of catalog stars.
	 * @param return the chart composition of catalog stars.
	 */
	public ChartMapFunction getChartMapFunction ( ) {
		return cmf;
	}

	/**
	 * Gets the limiting_magnitude.
	 * @return the limiting_magnitude.
	 */
	public double getLimitingMag ( ) {
		return limit_mag;
	}

	/**
	 * Sets the matching operation mode.
	 * @param mode the mode number.
	 */
	public void setMode ( int mode ) {
		this.mode = mode;
	}

	/**
	 * Sets the judgement mode.
	 * @param mode the mode number.
	 */
	public void setJudgementMode ( int mode ) {
		judge = mode;
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
		int width = image.getSize().getWidth();
		int height = image.getSize().getHeight();
		double fwidth = (double)width;
		double fheight = (double)height;

		// Shifts (x,y) position of detected stars
		// so that (0,0) is at the center of the image.
		list_detected.shift(new Position(- fwidth / 2.0, - fheight / 2.0));

		// Determines the catalog field of view for matching.
		double fov = (fov_width > fov_height ? fov_width : fov_height);
		int pixels = (width > height ? width : height);

		limit_mag = Astro.getProperLimitingMagnitude(fov / (double)pixels) - 3.0;

		monitor_set.addMessage("[Reading catalog for matching]");
		monitor_set.addMessage(new Date().toString());
		monitor_set.addMessage("Catalog: " + catalog_reader.getName());
		monitor_set.addMessage("R.A. and Decl. of the center: " + center_coor.getOutputStringTo100mArcsecWithUnit());
		monitor_set.addMessage("Image field of view: " + Format.formatAngularSize(fov_width, fov_height));
		monitor_set.addMessage("Limiting magnitude for matching: " + Format.formatDouble(limit_mag, 6, 3) + " mag");

		Exception running_exception = null;

		// Reads star data for matching.
		catalog_reader.setLimitingMagnitude(limit_mag);
		try {
			if (pane == null) {
				list_catalog = catalog_reader.read(center_coor, fov * 2.0);
			} else {
				InteractiveCatalogReader interactive_reader = new InteractiveCatalogReader(catalog_reader);
				list_catalog = interactive_reader.read(pane, center_coor, fov * 2.0);
			}
			monitor_set.addMessage("Original catalog data: " + list_catalog.size() + " stars");

			// Unifies some data with the same name.
			list_catalog = list_catalog.unify(10.0 / 3600.0);

			// Sorts in order of magnitude.
			list_catalog.sort();

			// Sets the (x,y) position of catalog data.
			ChartMapFunction reading_cmf = new ChartMapFunction(center_coor, (double)pixels / fov, position_angle_of_up);
			list_catalog.mapCoordinatesToXY(reading_cmf);

			monitor_set.addMessage("Catalog data: " + list_catalog.size() + " stars");
			monitor_set.addMessage(new Date().toString());
			monitor_set.addSeparator();

			MapFunction map_function = new MapFunction();

			if (mode != MODE_ACCURATE) {
				PositionMap map_detected = new PositionMap(new Position(- fwidth / 2.0, - fheight / 2.0), new Position(fwidth / 2.0, fheight / 2.0));
				PositionMap map_catalog = new PositionMap(new Position(- (double)pixels, - (double)pixels), new Position((double)pixels, (double)pixels));
				map_detected.addPosition(list_detected);
				map_catalog.acceptOutOfBounds();
				map_catalog.addPosition(list_catalog);

				// Calculates the map function to convert (x,y) on the image
				// to (x,y) on the catalog chart.
				try {
					DefaultMatchingSolver solver = new DefaultMatchingSolver(map_detected, map_catalog);
					solver.setMode(DefaultMatchingSolver.MODE_IMAGE_TO_CATALOG);
					if (judge == JUDGEMENT_LOOSE)
						solver.setCheckAccuracy(4.0);
					solver.addMonitor(monitor_set);
					solver.operate();
					map_function = solver.getMapFunction();
				} catch ( MatchingFailedException exception ) {
					map_function = exception.getMapFunction();

					if (mode == MODE_SEMI_ACCURATE) {
						Position center = map_function.map(new Position());
						if (center.getDistanceFrom(new Position()) < 100  &&  
							0.95 < map_function.getRatio()  &&  map_function.getRatio() < 1.05  &&
							Astro.getDistanceOfAngles(map_function.getAngle(), 0.0) < 5.0) {
							// Accepts the result although the score is small.
						} else {
							running_exception = exception;
						}
					} else {
						running_exception = exception;
					}
				}
			}

			// Calculates the R.A. and Decl. of the center, the field
			// of view, and the position angle of up direction.
			cmf = reading_cmf.map(map_function.inverse());

			monitor_set.addMessage("[Provisional image mapping]");
			monitor_set.addMessage("R.A. and Decl. of the center: " + cmf.getCenterCoor().getOutputStringTo100mArcsecWithUnit());
			monitor_set.addMessage("Field of view: " + Format.formatAngularSize(fwidth / cmf.getScaleUnitPerDegree(), fheight / cmf.getScaleUnitPerDegree()));
			monitor_set.addMessage("Position angle of up: " + Format.formatDouble(cmf.getPositionAngle(), 6, 3));
			monitor_set.addSeparator();

			// Sets the (x,y) position of the catalog data.
			list_catalog.mapCoordinatesToXY(cmf);

			// Shifts (x,y) position of detected stars and catalog data
			// so that (0,0) is at the top-left corner.
			list_catalog.shift(new Position(fwidth / 2.0, fheight / 2.0));
		} catch ( Exception exception ) {
			running_exception = exception;
		}

		// Shifts (x,y) position of detected stars and catalog data
		// so that (0,0) is at the top-left corner.
		list_detected.shift(new Position(fwidth / 2.0, fheight / 2.0));

		if (running_exception != null)
			throw running_exception;
	}
}
