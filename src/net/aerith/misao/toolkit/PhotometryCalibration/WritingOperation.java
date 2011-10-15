/*
 * @(#)WritingOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.PhotometryCalibration;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.io.Encoder;
import net.aerith.misao.toolkit.ReportBatch.ReportBatchOperation;

/**
 * The <code>WritingOperation</code> represents an operation to update 
 * the magnitude and save the XML files.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 January 1
 */

public class WritingOperation extends ReportBatchOperation {
	/**
	 * The frame.
	 */
	protected PhotometryCalibrationInternalFrame frame;

	/**
	 * Constructs a <code>WritingOperation</code>.
	 * @param conductor the conductor of multi task operation.
	 * @param frame     the frame.
	 */
	public WritingOperation ( MultiTaskConductor conductor, PhotometryCalibrationInternalFrame frame ) {
		this.conductor = conductor;

		this.frame = frame;
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		return 0;
	}

	/**
	 * Notifies when the operation ends.
	 * @param exception the exception if an error occurs, or null if
	 * succeeded.
	 */
	protected void notifyEnd ( Exception exception ) {
		frame.writingOperationCompleted();

		super.notifyEnd(exception);
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

		XmlInformation template_info = (XmlInformation)frame.getGlobalReport().getInformation();
		MagnitudeTranslationFormula formula = MagnitudeTranslationFormula.create(template_info.getMagnitudeTranslationFormula());

		// Corrects the exposure.
		if (info.getExposure() != null  &&  template_info.getExposure() != null) {
			double exposure_original = ((XmlExposure)template_info.getExposure()).getValueInSecond();
			double exposure_this = ((XmlExposure)info.getExposure()).getValueInSecond();
			if (exposure_original > 0  &&  exposure_this > 0) {
				double base_magnitude = Math.pow(Astro.MAG_STEP, - formula.getBaseMagnitude());
				base_magnitude *= exposure_original / exposure_this;
				formula.setBaseMagnitude(- Math.log(base_magnitude) / Math.log(Astro.MAG_STEP));
			}
		}

		// Modifies the XML report document.

		File xml_file = file_manager.newFile(info.getPath());
		XmlReport report = file_manager.readReport(info);

		info = (XmlInformation)report.getInformation();

		Cubics magnitude_correction = null;
		if (info.getMagnitudeCorrection() != null)
			magnitude_correction = Cubics.create(info.getMagnitudeCorrection());

		MagnitudeTranslationFormula old_formula = MagnitudeTranslationFormula.create(info.getMagnitudeTranslationFormula());
		double value = old_formula.convertToPixelValue(info.getLimitingMag());
		double mag = formula.convertToMagnitude(value);
		info.setFormattedLimitingMag(mag);

		value = old_formula.convertToPixelValue(info.getProperUpperLimitMag());
		mag = formula.convertToMagnitude(value);
		info.setFormattedUpperLimitMag(mag);

		info.setMagnitudeTranslationFormula(formula);

		info.setPhotometricError(template_info.getPhotometricError());
		info.setPhotometry(template_info.getPhotometry());

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
		message += Format.formatDouble(shift, 5, 2).trim() + " mag shift. ";
		this.monitor_set.addMessage(message);

		((XmlSystem)report.getSystem()).setModifiedJD(JulianDay.create(new Date()));

		report.write(xml_file);
	}
}
