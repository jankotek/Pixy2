/*
 * @(#)PairingOperator.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy.pairing;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.image.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.catalog.io.CatalogReader;
import net.aerith.misao.pixy.Resource;
import net.aerith.misao.pixy.InteractiveCatalogReader;
import net.aerith.misao.pixy.ExaminationFailedException;

/**
 * The <code>PairingOperator</code> is a class to operate pairing
 * process. This invokes the <code>ProgressivePairMaker#run</code> and
 * <code>ExperiencedPairRefiner#run</code>. Finally it creates the XML
 * document.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 November 24
 */

public class PairingOperator extends Operation {
	/**
	 * The content pane. In the case of null, the pairing process is
	 * operated without GUI components.
	 */
	protected Container pane = null;

	/**
	 * The XML document.
	 */
	protected XmlReport report = null;

	/**
	 * The image.
	 */
	protected MonoImage image;

	/**
	 * The catalog reader.
	 */
	protected CatalogReader catalog_reader;

	/**
	 * The chart map function.
	 */
	protected ChartMapFunction cmf;

	/**
	 * The list of detected stars.
	 */
	protected StarImageList list_detected;

	/**
	 * The list of catalog stars.
	 */
	protected StarList list_catalog;

	/**
	 * The list of pairs between the detected stars and the catalog 
	 * data.
	 */
	protected Vector pair_list;

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
	protected double limit_mag;

	/**
	 * The upper-limit magnitude.
	 */
	protected double upper_limit_mag;

	/**
	 * The astrometric error.
	 */
	protected AstrometricError astrometric_error;

	/**
	 * The photometric error.
	 */
	protected PhotometricError photometric_error;

	/**
	 * The image file.
	 */
	protected XmlImage image_file;

	/**
	 * True if the image is an SBIG ST-4/6 image.
	 */
	protected boolean sbig_image = false;

	/**
	 * True if the image is a reversed image.
	 */
	protected boolean reversed_image = false;

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
	 * True when the limiting magnitude to read stars from the catalog
	 * is specified.
	 */
	protected boolean catalog_limiting_mag_specified = false;

	/**
	 * The limiting magnitude to read stars from the catalog.
	 */
	protected double catalog_limiting_mag = 0.0;

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
	 * Constructs a <code>PairingOperator</code>.
	 * @param image          the image.
	 * @param catalog_reader the catalog reader.
	 * @param cmf            the chart map function.
	 * @param list_detected  the list of detected stars.
	 * @param image_file     the image file.
	 * @param sbig_image     true if the image is an SBIG ST-4/6 image.
	 * @param reversed_image true if the image is a reversed image.
	 */
	public PairingOperator ( MonoImage image,
							 CatalogReader catalog_reader,
							 ChartMapFunction cmf,
							 StarImageList list_detected,
							 XmlImage image_file,
							 boolean sbig_image,
							 boolean reversed_image )
	{
		this.image = image;
		this.catalog_reader = catalog_reader;
		this.cmf = cmf;
		this.list_detected = list_detected;
		this.image_file = image_file;
		this.sbig_image = sbig_image;
		this.reversed_image = reversed_image;
	}

	/**
	 * Enables the interactive catalog reading.
	 * @param pane the pane.
	 */
	public void enableInteractive ( Container pane ) {
		this.pane = pane;
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
	 * Sets the limiting magnitude to read stars from the catalog.
	 * @param limit_mag the limiting magnitude to fix.
	 */
	public void setCatalogLimitingMagnitude ( double limit_mag ) {
		catalog_limiting_mag_specified = true;

		catalog_limiting_mag = limit_mag;
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
	 * Gets the XML report document.
	 * @return the XML report document.
	 */
	public XmlReport getXmlReportDocument ( ) {
		return report;
	}

	/**
	 * Gets the magnitude translation formula.
	 * @return the magnitude translation formula.
	 */
	public MagnitudeTranslationFormula getMagnitudeTranslationFormula ( ) {
		return mag_formula;
	}

	/**
	 * Gets the limiting magnitude.
	 * @return the limiting magnitude.
	 */
	public double getLimitingMag ( ) {
		return limit_mag;
	}

	/**
	 * Gets the upper-limit magnitude. This method must be invoked
	 * after <code>operate</code> method is invoked.
	 * @return the upper-limit magnitude.
	 */
	public double getUpperLimitMag ( ) {
		return upper_limit_mag;
	}

	/**
	 * Gets the list of detected stars.
	 * @return the list of detected stars.
	 */
	public StarImageList getDetectedList ( ) {
		return list_detected;
	}

	/**
	 * Gets the list of catalog stars.
	 * @return the list of catalog stars.
	 */
	public StarList getCatalogList ( ) {
		return list_catalog;
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

		// Determines the catalog field of view for making pairs.
		int pixels = (width > height ? width : height);
		double fov = (double)pixels / cmf.getScaleUnitPerDegree();

		if (catalog_limiting_mag_specified)
			limit_mag = catalog_limiting_mag;
		else
			limit_mag = Astro.getProperLimitingMagnitude(fov / (double)pixels);

		monitor_set.addMessage("[Reading catalog for making pairs]");
		monitor_set.addMessage(new Date().toString());
		monitor_set.addMessage("Limiting magnitude for making pairs: " + Format.formatDouble(limit_mag, 6, 3) + " mag");

		Exception running_exception = null;

		// Reads star data for making pairs.
		catalog_reader.setLimitingMagnitude(limit_mag);
		try {
			if (pane == null) {
				list_catalog = catalog_reader.read(cmf.getCenterCoor(), fov * 1.5);
			} else {
				InteractiveCatalogReader interactive_reader = new InteractiveCatalogReader(catalog_reader);
				list_catalog = interactive_reader.read(pane, cmf.getCenterCoor(), fov * 1.5);
			}
			monitor_set.addMessage("Original catalog data: " + list_catalog.size() + " stars");

			// Unifies some data with the same name.
			list_catalog = list_catalog.unify(10.0 / 3600.0);

			// Sorts in order of magnitude.
			list_catalog.sort();

			// Sets the (x,y) position of the catalog data.
			list_catalog.mapCoordinatesToXY(cmf);

			monitor_set.addMessage("Catalog data: " + list_catalog.size() + " stars");
			monitor_set.addMessage(new Date().toString());
			monitor_set.addSeparator();

			PositionMap image_map = new PositionMap(new Position(- fwidth / 2.0, - fheight / 2.0), new Position(fwidth / 2.0, fheight / 2.0));
			PositionMap catalog_map = new PositionMap(new Position(- (double)pixels / 2.0 * 1.5, - (double)pixels / 2.0 * 1.5), new Position((double)pixels / 2.0 * 1.5, (double)pixels / 2.0 * 1.5));

			// Makes pairs between the detected stars and the catalog data.
			ProgressivePairMaker maker = new ProgressivePairMaker(list_detected, list_catalog, cmf, image_map, catalog_map);
			maker.addMonitor(monitor_set);
			if (assume_flat)
				maker.assumeFlat();
			if (fix_limiting_mag)
				maker.fixLimitingMagnitude(fixed_limit_mag);
			try {
				maker.operate();
			} catch ( ExaminationFailedException exception ) {
				if (assume_flat)
					throw exception;

				assume_flat = true;

				// Sets the (x,y) position of the catalog data.
				list_catalog.mapCoordinatesToXY(cmf);

				// Makes pairs between the detected stars and the catalog data
				// again without calculating the distortion field.
				maker = new ProgressivePairMaker(list_detected, list_catalog, cmf, image_map, catalog_map);
				maker.addMonitor(monitor_set);
				maker.assumeFlat();
				if (fix_limiting_mag)
					maker.fixLimitingMagnitude(fixed_limit_mag);
				maker.operate();
			}
			pair_list = maker.getPairList();
			cmf = maker.getChartMapFunction();

			// At this point, pairing between the detected stars and catalog data
			// is completed. The distortion is also considered.
			// The list of pairs contains all data. The limiting magnitude is not
			// considered at all.

			// Refines the pairs. that is to calculate the limiting magnitude,
			// remove too faint stars, calculate R.A., Decl. and magnitude of all
			// detected stars, and calculate (x,y) of all catalog data.
			ExperiencedPairRefiner refiner = new ExperiencedPairRefiner(pair_list, cmf, image_map);
			refiner.addMonitor(monitor_set);
			if (assume_flat)
				refiner.assumeFlat();
			if (fix_limiting_mag)
				refiner.fixLimitingMagnitude(fixed_limit_mag, fixed_upper_limit_mag);
			if (fix_mag_formula_gradient)
				refiner.fixMagnitudeTranslationFormulaGradient();
			refiner.operate();
			pair_list = refiner.getPairList();
			cmf = refiner.getChartMapFunction();
			df = refiner.getDistortionField();
			mag_formula = refiner.getMagnitudeTranslationFormula();
			limit_mag = refiner.getLimitingMagnitude();
			upper_limit_mag = refiner.getUpperLimitMagnitude();
			astrometric_error = refiner.getAstrometricError();
			photometric_error = refiner.getPhotometricError();

			// Reconstructs the list of detected stars and that of catalog data
			// based on the refined list of pairs.
			// Counts number of stars depending on the type.
			list_detected = new StarImageList();
			list_catalog = new StarList();
			int STR_count = 0;
			int NEW_count = 0;
			int ERR_count = 0;
			for (int i = 0 ; i < pair_list.size() ; i++) {
				StarPair pair = (StarPair)pair_list.elementAt(i);
				Star star1 = pair.getFirstStar();
				Star star2 = pair.getSecondStar();
				if (star1 != null  &&  star2 != null) {
					list_detected.addElement(star1);
					list_catalog.addElement(star2);
					STR_count++;
				} else if (star1 != null) {
					list_detected.addElement(star1);
					NEW_count++;
				} else {
					list_catalog.addElement(star2);
					ERR_count++;
				}
			}
			monitor_set.addMessage("[Number of stars]");
			monitor_set.addMessage("Detected stars: " + list_detected.size() + " stars");
			monitor_set.addMessage("Catalog data: " + list_catalog.size() + " stars");
			monitor_set.addMessage("STR: " + STR_count);
			monitor_set.addMessage("NEW: " + NEW_count);
			monitor_set.addMessage("ERR: " + ERR_count);
			monitor_set.addSeparator();

			/*
			if (fix_limiting_mag == false) {
				if (NEW_count > STR_count * 2  ||  ERR_count > STR_count * 2)
					throw new ExaminationFailedException();
			}
			*/

			// Shifts (x,y) position of detected stars and catalog data
			// so that (0,0) is at the top-left corner.
			list_detected.shift(new Position(fwidth / 2.0, fheight / 2.0));
			list_catalog.shift(new Position(fwidth / 2.0, fheight / 2.0));

			// All merged catalog stars must be unpacked here,
			// in order to plot the stars with a proper color.
			// Note that the merged star has no color information.
			list_catalog = list_catalog.flatten();

			// Creates the XML report document.
			createXmlReportDocument();

			return;
		} catch ( Exception exception ) {
			running_exception = exception;
		}

		// Shifts (x,y) position of detected stars and catalog data
		// so that (0,0) is at the top-left corner.
		list_detected.shift(new Position(fwidth / 2.0, fheight / 2.0));

		if (running_exception != null)
			throw running_exception;
	}

	/**
	 * Creates the XML report document.
	 */
	private void createXmlReportDocument ( ) {
		report = new XmlReport();

		JulianDay date = JulianDay.create(new Date());

		XmlSystem system = new XmlSystem();
		system.setVersion(Resource.getVersion());
		system.setExaminedJD(date);
		system.setModifiedJD(date);
		report.setSystem(system);

		// The name of the base catalog.
		Star star = (Star)list_catalog.elementAt(0);
		if (star instanceof MergedStar)
			star = ((MergedStar)star).getStarAt(0);
		String base_catalog = ((CatalogStar)star).getCatalogName();

		XmlInformation info = new XmlInformation();

		XmlImage xml_image = new XmlImage();
		xml_image.setContent(image_file.getContent());
		xml_image.setFormat(image_file.getFormat());
		xml_image.setOrder(image_file.getOrder());
		info.setImage(xml_image);

		info.setInformation(image.getSize(), cmf);
		info.setFormattedLimitingMag(limit_mag);
		info.setFormattedUpperLimitMag(upper_limit_mag);
		info.setAstrometricError(astrometric_error);
		info.setPhotometricError(photometric_error);
		info.setDistortionField(df);
		info.setMagnitudeTranslationFormula(mag_formula);
		info.setBaseCatalog(base_catalog);
		if (reversed_image)
			info.setReversedImage();
		if (sbig_image)
			info.setSbigImage();
		report.setInformation(info);

		XmlData data = new XmlData();
		data.setStar(new XmlStar[0]);

		int STR_count = 0;
		int NEW_count = 0;
		int ERR_count = 0;
		for (int i = 0 ; i < pair_list.size() ; i++) {
			StarPair pair = (StarPair)pair_list.elementAt(i);
			Star star1 = pair.getFirstStar();
			Star star2 = pair.getSecondStar();

			XmlStar s = new XmlStar();
			if (star1 != null  &&  star2 != null) {
				STR_count++;
				s.setName("STR", STR_count);
			} else if (star1 != null) {
				NEW_count++;
				s.setName("NEW", NEW_count);
			} else {
				ERR_count++;
				s.setName("ERR", ERR_count);
			}

			if (star1 != null)
				s.addStar(star1);
			if (star2 != null)
				s.addStar(star2);

			data.addStar(s);
		}

		data.createStarMap(image.getSize());

		report.setData(data);

		report.countStars();
	}
}
