/*
 * @(#)PhotometryOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ReportBatch;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.InformationTable;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.database.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.catalog.CatalogManager;
import net.aerith.misao.catalog.io.CatalogReader;
import net.aerith.misao.io.Encoder;

/**
 * The <code>PhotometryOperation</code> represents a batch operation 
 * of photometry based on the specified catalog.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 6
 */

public class PhotometryOperation extends ReportBatchOperation {
	/**
	 * The dialog to select the catalog and method for photometry.
	 */
	protected PhotometryCatalogSettingDialog dialog;

	/**
	 * The dialog to set parameters for photometry operation.
	 */
	protected PhotometryOperationSettingDialog dialog2;

	/**
	 * Constructs an <code>PhotometryOperation</code>.
	 * @param conductor the conductor of multi task operation.
	 */
	public PhotometryOperation ( MultiTaskConductor conductor ) {
		this.conductor = conductor;
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		dialog = new PhotometryCatalogSettingDialog();
		dialog2 = new PhotometryOperationSettingDialog();
		int answer = dialog.show(conductor.getPane());
		if (answer == 0)
			answer = dialog2.show(conductor.getPane());
		return answer;
	}

	/**
	 * Operates on one item. This is invoked from the conductor of 
	 * multi task operation.
	 * @param object the target object to operate.
	 * @exception Exception if an error occurs.
	 */
	public void operate ( Object object )
		throws Exception
	{
		XmlInformation info = (XmlInformation)object;

		File xml_file = file_manager.newFile(info.getPath());
		XmlReport report = file_manager.readReport(info);

		info = (XmlInformation)report.getInformation();

		PhotometrySetting setting = dialog.getPhotometrySetting();

		if (setting.getCatalogName() == null  ||  setting.getCatalogName().length() == 0)
			throw new Exception();

		Cubics magnitude_correction = null;
		if (info.getMagnitudeCorrection() != null)
			magnitude_correction = Cubics.create(info.getMagnitudeCorrection());

		// Selects pairs used for photometry.
		Vector pair_list = ((XmlData)report.getData()).extractPairs(setting);

		// Creates a list of temporary pairs, whose catalog stars have
		// the proper magnitude defined in the photometry setting.
		Vector tmp_pair_list =  new Vector();
		StarImageList list_detected = new StarImageList();
		for (int i = 0 ; i < pair_list.size() ; i++) {
			StarPair pair = (StarPair)pair_list.elementAt(i);
			StarImage star_image = (StarImage)pair.getFirstStar();
			CatalogStar catalog_star = (CatalogStar)pair.getSecondStar();
				
			try {
				double catalog_mag = 0.0;
				if (setting.getMethod() == PhotometrySetting.METHOD_INSTRUMENTAL_PHOTOMETRY  ||
					setting.getMethod() == PhotometrySetting.METHOD_FREE_PHOTOMETRY) {
					catalog_mag = catalog_star.getMagnitude(setting.getMagnitudeSystem());
				} else {
					catalog_mag = catalog_star.getMagnitude(setting.getMagnitudeSystem().getSystemCode());
				}

				if (dialog2.getBrighterLimitingMagnitude() <= catalog_mag  &&
					catalog_mag <= dialog2.getFainterLimitingMagnitude()) {
					// Clones the detected stars because the value is adjusted
					// based on the magnitude correction formula.
					if (magnitude_correction != null) {
						double x = star_image.getX() - (double)info.getSize().getWidth() / 2.0;
						double y = star_image.getY() - (double)info.getSize().getHeight() / 2.0;
						double delta_mag = magnitude_correction.getValue(x, y);
						double delta_value = Math.pow(Astro.MAG_STEP, delta_mag);

						star_image = new StarImage(star_image);
						star_image.setValue(star_image.getValue() / delta_value);
					}

					// Clones the catalog stars because the magnitude of the
					// specified system is recorded.
					Star star = new Star();
					star.setMag(catalog_mag);

					list_detected.addElement(star_image);

					StarPair pair2 = new StarPair(star_image, star);
					tmp_pair_list.addElement(pair2);
				}
			} catch ( UnsupportedMagnitudeSystemException exception ) {
			}
		}

		int original_count = tmp_pair_list.size();
		double threshold = 99.9;

		MagnitudeTranslationFormula formula = null;
		PhotometricError photometric_error = null;

		// Selects only good pairs for photometry.
		while (true) {
			// In the case the number of stars are too small.
			if (tmp_pair_list.size() < dialog2.getMinimumStarCount())
				throw new TooFewStarsException();

			formula = new MagnitudeTranslationFormula(tmp_pair_list, MagnitudeTranslationFormula.MODE_FIX_GRADIENT);
			list_detected.setMagnitude(formula);

			photometric_error = new PhotometricError(tmp_pair_list);

			boolean rejected = true;

			threshold = photometric_error.getError() * 2.0;
			if (threshold < 0.1) {
				rejected = false;
			} else {
				// Rejects erroneous stars.
				Vector tmp_pair_list2 = new Vector();
				for (int i = 0 ; i < tmp_pair_list.size() ; i++) {
					StarPair pair = (StarPair)tmp_pair_list.elementAt(i);
					StarImage star_image = (StarImage)pair.getFirstStar();
					Star catalog_star = (Star)pair.getSecondStar();

					double err = Math.abs(star_image.getMag() - catalog_star.getMag());
					if (err < threshold  &&  err < dialog2.getThresholdToReject()) {
						StarPair pair2 = new StarPair(star_image, catalog_star);
						tmp_pair_list2.addElement(pair2);
					}
				}

				if (tmp_pair_list2.size() == 0  ||  tmp_pair_list2.size() < original_count / 2 + 1) {
					rejected = false;
				} else if (tmp_pair_list.size() == tmp_pair_list2.size()) {
					rejected = false;
				} else {
					tmp_pair_list = tmp_pair_list2;
				}
			}

			if (rejected == false) {
				if (photometric_error.getError() < dialog2.getMeanThresholdToAccept()) {
					// OK.
					break;
				} else {
					// Continues because the error is still too large.

					// Rejects one which has the largest error.
					double max_error = 0.0;
					int max_error_index = 0;
					for (int i = 0 ; i < tmp_pair_list.size() ; i++) {
						StarPair pair = (StarPair)tmp_pair_list.elementAt(i);
						StarImage star_image = (StarImage)pair.getFirstStar();
						Star catalog_star = (Star)pair.getSecondStar();

						double err = Math.abs(star_image.getMag() - catalog_star.getMag());
						if (max_error < err) {
							max_error = err;
							max_error_index = i;
						}
					}

					Vector tmp_pair_list2 = new Vector();
					for (int i = 0 ; i < tmp_pair_list.size() ; i++) {
						if (i != max_error_index) {
							StarPair pair = (StarPair)tmp_pair_list.elementAt(i);
							StarImage star_image = (StarImage)pair.getFirstStar();
							Star catalog_star = (Star)pair.getSecondStar();
							StarPair pair2 = new StarPair(star_image, catalog_star);
							tmp_pair_list2.addElement(pair2);
						}
					}
					tmp_pair_list = tmp_pair_list2;
				}
			}
		}

		// Modifies the XML report documents.

		MagnitudeTranslationFormula old_formula = MagnitudeTranslationFormula.create(info.getMagnitudeTranslationFormula());
		double value = old_formula.convertToPixelValue(info.getLimitingMag());
		double mag = formula.convertToMagnitude(value);
		info.setFormattedLimitingMag(mag);

		value = old_formula.convertToPixelValue(info.getProperUpperLimitMag());
		mag = formula.convertToMagnitude(value);
		info.setFormattedUpperLimitMag(mag);

		info.setPhotometricError(photometric_error);

		info.setMagnitudeTranslationFormula(formula);

		XmlPhotometry photometry = new XmlPhotometry(setting);
		info.setPhotometry(photometry);

		XmlStar[] xml_stars = (XmlStar[])report.getData().getStar();
		for (int i = 0 ; i < xml_stars.length ; i++) {
			StarImage star_image = xml_stars[i].getStarImage();
			if (star_image != null) {
				mag = formula.convertToMagnitude(star_image.getValue());
				if (magnitude_correction != null) {
					double x = star_image.getX() - (double)info.getSize().getWidth() / 2.0;
					double y = star_image.getY() - (double)info.getSize().getHeight() / 2.0;
					mag += magnitude_correction.getValue(x, y);
				}
				star_image.setMag(mag);
			}
		}

		// Verifies the results.

		double shift = formula.getBaseMagnitude() - old_formula.getBaseMagnitude();

		String message = xml_file.getPath() + ": ";
		message += tmp_pair_list.size() + " stars. ";
		message += Format.formatDouble(shift, 5, 2).trim() + " mag shift. ";
		message += Format.formatDouble(formula.getBaseMagnitude(), 5, 2).trim() + " mag/count. ";
		message += "+/- " + Format.formatDouble(photometric_error.getError(), 5, 2).trim() + " mag. ";
		monitor_set.addMessage(message);

		((XmlSystem)report.getSystem()).setModifiedJD(JulianDay.create(new Date()));

		report.write(xml_file);
	}
}
