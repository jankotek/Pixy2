/*
 * @(#)PhotometryZeroPointAdjustmentOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.PhotometryZeroPointAdjustment;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JOptionPane;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.InformationTable;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.database.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.catalog.CatalogManager;
import net.aerith.misao.catalog.io.CatalogReader;
import net.aerith.misao.io.Encoder;
import net.aerith.misao.pixy.identification.DefaultIdentifier;
import net.aerith.misao.toolkit.ReportBatch.ReportBatchOperation;

/**
 * The <code>PhotometryZeroPointAdjustmentOperation</code> represents 
 * a batch operation to identify XML report documents with stars in 
 * the reference database, and adjust zero-point of photometry.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 July 15
 */

public class PhotometryZeroPointAdjustmentOperation extends ReportBatchOperation {
	/*
	 * The parent desktop.
	 */
	protected PhotometryZeroPointAdjustmentDesktop desktop;

	/**
	 * Constructs a <code>PhotometryZeroPointAdjustmentOperation</code>.
	 * @param conductor the conductor of multi task operation.
	 * @param desktop   the parent desktop.
	 */
	public PhotometryZeroPointAdjustmentOperation ( MultiTaskConductor conductor, PhotometryZeroPointAdjustmentDesktop desktop ) {
		this.conductor = conductor;

		this.desktop = desktop;
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		if (desktop.getReferenceDBManager() == null) {
			String message = "Reference database is not created yet.";
			JOptionPane.showMessageDialog(conductor.getPane(), message, "Error", JOptionPane.ERROR_MESSAGE);

			return 2;
		}

		return desktop.getSettingDialog().show(conductor.getPane());
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

		try {
			// Checks if any stars can be identified.
			// Note that acceptNegative() is required because the document is empty.
			XmlReport report = info.cloneEmptyReport();

			CatalogReader reader = new CatalogDBReader(desktop.getReferenceDBManager().getCatalogDBManager());

			DefaultIdentifier identifier = new DefaultIdentifier(report, reader);
			identifier.acceptNegative();
			IdentifiedStarCounter counter = new IdentifiedStarCounter();
			identifier.addObserver(counter);
			identifier.operate();

			// Reads the XML report document if needed, and identifies.
			if (counter.getIdentifiedStarCount() < desktop.getSettingDialog().getMinimumStarCount())
				throw new TooFewStarsException();

			// Identifies with stars in the reference database.

			File xml_file = file_manager.newFile(info.getPath());
			report = file_manager.readReport(info);
			info = (XmlInformation)report.getInformation();

			identifier = new DefaultIdentifier(report, reader);
//			identifier.addMonitor(monitor_set);
			identifier.exceptNegative();
			identifier.operate();

			// Adjusts zero-point of photometry.

			Cubics magnitude_correction = null;
			if (info.getMagnitudeCorrection() != null)
				magnitude_correction = Cubics.create(info.getMagnitudeCorrection());

			String class_name = new DetectedStar().getClass().getName();

			// Selects pairs used for photometry.
			Vector pair_list = new Vector();
			XmlStar[] xml_stars = (XmlStar[])report.getData().getStar();
			for (int i = 0 ; i < xml_stars.length ; i++) {
				StarImage star_image = xml_stars[i].getStarImage();
				if (star_image != null) {
					Vector catalog_stars = xml_stars[i].getAllRecords();

					// Selects stars which has the proper magnitude data.
					Vector proper_stars = new Vector();
					for (int j = 0 ; j < catalog_stars.size() ; j++) {
						Star star = (Star)catalog_stars.elementAt(j);
						if (star.getClass().getName().equals(class_name))
							proper_stars.addElement(star);
					}

					// Only one star must be identified.
					if (proper_stars.size() == 1) {
						CatalogStar star = (CatalogStar)proper_stars.elementAt(0);
						StarPair pair = new StarPair(star_image, star);
						pair_list.addElement(pair);
					}
				}
			}

			// Creates a list of temporary pairs, whose catalog stars have
			// the proper magnitude defined in the photometry setting.
			Vector tmp_pair_list =  new Vector();
			StarImageList list_detected = new StarImageList();
			for (int i = 0 ; i < pair_list.size() ; i++) {
				StarPair pair = (StarPair)pair_list.elementAt(i);
				StarImage star_image = (StarImage)pair.getFirstStar();
				CatalogStar catalog_star = (CatalogStar)pair.getSecondStar();
				
				double catalog_mag = catalog_star.getMag();

				if (desktop.getSettingDialog().getBrighterLimitingMagnitude() <= catalog_mag  &&
					catalog_mag <= desktop.getSettingDialog().getFainterLimitingMagnitude()) {
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
			}

			int original_count = tmp_pair_list.size();
			double threshold = 99.9;

			MagnitudeTranslationFormula formula = null;
			PhotometricError photometric_error = null;

			// Selects only good pairs for photometry.
			while (true) {
				// In the case the number of stars are too small.
				if (tmp_pair_list.size() < desktop.getSettingDialog().getMinimumStarCount())
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
						if (err < threshold  &&  err < desktop.getSettingDialog().getThresholdToReject()) {
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
					if (photometric_error.getError() < desktop.getSettingDialog().getMeanThresholdToAccept()) {
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

			report = file_manager.readReport(info);
			info = (XmlInformation)report.getInformation();

			MagnitudeTranslationFormula old_formula = MagnitudeTranslationFormula.create(info.getMagnitudeTranslationFormula());
			double value = old_formula.convertToPixelValue(info.getLimitingMag());
			double mag = formula.convertToMagnitude(value);
			info.setFormattedLimitingMag(mag);

			value = old_formula.convertToPixelValue(info.getProperUpperLimitMag());
			mag = formula.convertToMagnitude(value);
			info.setFormattedUpperLimitMag(mag);

			info.setPhotometricError(photometric_error);

			info.setMagnitudeTranslationFormula(formula);

			xml_stars = (XmlStar[])report.getData().getStar();
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

			notifySucceeded(info);
		} catch ( Exception exception ) {
			notifyFailed(info);

			throw exception;
		}
	}

	/**
	 * The <code>IdentifiedStarCounter</code> is an observer to count
	 * the number of identified stars.
	 */
	protected class IdentifiedStarCounter implements OperationObserver {
		/**
		 * The number of identified stars.
		 */
		protected int identified_count = 0;

		/**
		 * Constructs an <code>IdentifiedStarCounter</code>.
		 */
		public IdentifiedStarCounter ( ) {
			identified_count = 0;
		}

		/**
		 * Gets the number of identified stars.
		 * @return the number of identified stars.
		 */
		public int getIdentifiedStarCount ( ) {
			return identified_count;
		}

		/**
		 * Invoked when the operation starts.
		 */
		public void notifyStart ( ) {
		}

		/**
		 * Invoked when the operation ends.
		 * @param exception the exception if an error occurs, or null if
		 * succeeded.
		 */
		public void notifyEnd ( Exception exception ) {
		}

		/**
		 * Invoked when a task is succeeded.
		 * @param arg the argument.
		 */
		public void notifySucceeded ( Object arg ) {
			identified_count++;
		}

		/**
		 * Invoked when a task is failed.
		 * @param arg the argument.
		 */
		public void notifyFailed ( Object arg ) {
		}

		/**
		 * Invoked when a task is warned.
		 * @param arg the argument.
		 */
		public void notifyWarned ( Object arg ) {
		}
	}
}
