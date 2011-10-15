/*
 * @(#)ExaminationOperator.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.image.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.io.*;
import net.aerith.misao.catalog.CatalogManager;
import net.aerith.misao.catalog.io.CatalogReader;
import net.aerith.misao.pixy.image_loading.XmlImageLoader;
import net.aerith.misao.pixy.star_detection.DefaultStarDetector;
import net.aerith.misao.pixy.matching.MatchingOperator;
import net.aerith.misao.pixy.matching.RetryManager;
import net.aerith.misao.pixy.pairing.PairingOperator;

/**
 * The <code>ExaminationOperator</code> is a class to operate the
 * whole process of image examination. 
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 November 24
 */

public class ExaminationOperator extends Operation {
	/**
	 * The content pane. In the case of null, the interactive catalog
	 * reading is disabled.
	 */
	protected Container pane = null;

	/**
	 * The instruction.
	 */
	protected XmlInstruction instruction;

	/**
	 * The result XML report document.
	 */
	protected XmlReport report = null;

	/**
	 * The image converter.
	 */
	protected ImageConverter image_converter = null;

	/**
	 * The star detection mode.
	 */
	protected int star_detection_mode = DefaultStarDetector.MODE_PIXEL_AMOUNT_OVER_THRESHOLD;

	/**
	 * The aperture size of a star. For example, 4 means 4x4 pixels 
	 * around the star position.
	 */
	protected int inner_aperture_size = 2;

	/**
	 * The aperture size of sky around a star. For example, 4 means 
	 * 4x4 pixels around the star position.
	 */
	protected int outer_aperture_size = 3;

	/**
	 * True when to correct the positions of blooming stars.
	 */
	protected boolean correct_blooming_position = false;

	/**
	 * The matching mode.
	 */
	protected int matching_mode = MatchingOperator.MODE_UNCERTAIN;

	/**
	 * The judgement mode of matching.
	 */
	protected int matching_judge = MatchingOperator.JUDGEMENT_NORMAL;

	/**
	 * True when to retry 9 times at most.
	 */
	protected boolean retry_flag = true;

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
	 * The minimum number of paired stars to be succeeded.
	 */
	protected int minimum_STR_count = 4;

	/**
	 * Constructs an <code>ExaminationOperator</code>.
	 * @param instruction the instruction.
	 */
	public ExaminationOperator ( XmlInstruction instruction ) {
		this.instruction = instruction;
	}

	/**
	 * Enables the interactive catalog reading.
	 * @param pane the pane.
	 */
	public void enableInteractive ( Container pane ) {
		this.pane = pane;
	}

	/**
	 * Sets the image converter.
	 * @param converter the image converter.
	 */
	public void setImageConverter ( ImageConverter converter ) {
		this.image_converter = converter;
	}

	/**
	 * Sets the star detection mode.
	 * @param mode the star detection mode.
	 */
	public void setStarDetectionMode ( int mode ) {
		this.star_detection_mode = mode;
	}

	/**
	 * Sets the aperture sizes. For example, 4 means 4x4 pixels around 
	 * the star position.
	 * @param inner_size the aperture size of a star.
	 * @param outer_size the aperture size of sky around a star.
	 */
	public void setApertureSize ( int inner_size, int outer_size ) {
		this.inner_aperture_size = inner_size;
		this.outer_aperture_size = outer_size;
	}

	/**
	 * Sets the flag to correct the positions of blooming stars.
	 */
	public void setCorrectBloomingPosition ( ) {
		correct_blooming_position = true;
	}

	/**
	 * Sets the matching mode.
	 * @param mode the matching mode.
	 */
	public void setMatchingMode ( int mode ) {
		this.matching_mode = mode;
	}

	/**
	 * Sets the judgement mode of matching.
	 * @param mode the mode number.
	 */
	public void setMatchingJudgementMode ( int mode ) {
		matching_judge = mode;
	}

	/**
	 * Enables/disables the retry 9 times at most.
	 * @param flag true when to enable retry.
	 */
	public void setRetryEnabled ( boolean flag ) {
		retry_flag = flag;
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
	 * Sets the minimum number of paired stars to be succeeded.
	 * @param count the minimum number of paired stars to be succeeded.
	 */
	public void setMinimumStarCount ( int count ) {
		minimum_STR_count = count;
	}

	/**
	 * Gets the XML report document.
	 * @return the XML report document.
	 */
	public XmlReport getXmlReportDocument ( ) {
		return report;
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
		PrintStream log_stream = null;

		try {
			// Outputs the log.
			if (instruction.getOutput("log") != null) {
				log_stream = new PrintStream(Encoder.newOutputStream(new File(instruction.getOutput("log"))));
				monitor_set.addMonitor(new PrintStreamMonitor(log_stream));

				monitor_set.addMessages(Resource.getVersionAndCopyright());
				monitor_set.addSeparator();
			}

			// Creates the image loader.
			XmlImageLoader image_loader = new XmlImageLoader(instruction, new FileManager());
			image_loader.addMonitor(monitor_set);

			// Opens the image.
			image_loader.operate();
			MonoImage image = image_loader.getMonoImage();

			// Converts the image.
			if (image_converter != null)
				image = image_converter.convertImage(image);

			// Detects stars.
			DefaultStarDetector detector = new DefaultStarDetector(image);
			detector.setMode(star_detection_mode);
			detector.setApertureSize(inner_aperture_size, outer_aperture_size);
			if (correct_blooming_position)
				detector.setCorrectBloomingPosition();
			detector.addMonitor(monitor_set);
			detector.operate();
			StarImageList list_detected = detector.getStarList();

			list_detected.setMagnitude(new MagnitudeTranslationFormula());
			list_detected.sort();

			CatalogReader catalog_reader = CatalogManager.getStarCatalogReader(instruction.getBaseCatalog().getContent());
			if (instruction.getBaseCatalog().getContent().equals(CatalogManager.getSampleStarCatalogReader().getName())) {
				catalog_reader = CatalogManager.getSampleStarCatalogReader();
			}
			if (catalog_reader == null) {
				monitor_set.addMessage("No such catalog: " + instruction.getBaseCatalog().getContent());
			} else {
				// Retry 9 times at most.
				RetryManager retry_manager = new RetryManager(
					((XmlCenter)instruction.getCenter()).getCoor(),
					((XmlFov)instruction.getFov()).getWidthInDegree(),
					((XmlFov)instruction.getFov()).getHeightInDegree());
				retry_manager.setPolicy(RetryManager.POLICY_POSITION_UNCERTAIN);
				if (retry_flag == false)
					retry_manager.setPolicy(RetryManager.POLICY_NO_RETRY);
				if (matching_mode != MatchingOperator.MODE_UNCERTAIN)
					retry_manager.setPolicy(RetryManager.POLICY_NO_RETRY);

				// Operates matching.
				ChartMapFunction cmf = null;
				while (true) {
					try {
						double position_angle_of_up = 0.0;
						if (instruction.getRotation() != null)
							position_angle_of_up = instruction.getRotation().getContent();

						MatchingOperator matching_operator = new MatchingOperator(
							image, catalog_reader, instruction.getBaseCatalog().getPath(),
							retry_manager.getCenterCoor(),
							retry_manager.getHorizontalFov(),
							retry_manager.getVerticalFov(),
							position_angle_of_up,
							list_detected);
						matching_operator.setMode(matching_mode);
						matching_operator.setJudgementMode(matching_judge);
						matching_operator.addMonitor(monitor_set);
						matching_operator.operate();

						cmf = matching_operator.getChartMapFunction();
						break;
					} catch ( Exception exception ) {
						// Here an exception is thrown when it reaches to the 
						// maximum retry count.
						retry_manager.increment();

						monitor_set.addMessage("Matching failed. Retries.");
					}
				}

				// Operates pairing.
				PairingOperator operator = new PairingOperator(
					image, catalog_reader, cmf, list_detected, 
					(XmlImage)instruction.getImage(),
					instruction.getSbigImage() == null ? false : true,
					instruction.getReversedImage() == null ? false : true);
				operator.addMonitor(monitor_set);
				if (assume_flat)
					operator.assumeFlat();
				if (fix_mag_formula_gradient)
					operator.fixMagnitudeTranslationFormulaGradient();
				if (((XmlBaseCatalog)instruction.getBaseCatalog()).isLimitingMagnitudeSpecified())
					operator.setCatalogLimitingMagnitude(((XmlBaseCatalog)instruction.getBaseCatalog()).getProperLimitingMag());
				if (instruction.fixesLimitingMagnitude())
					operator.fixLimitingMagnitude(instruction.getProperLimitingMag(), instruction.getProperUpperLimitMag());
				operator.operate();

				// Creates the XML report document.
				report = operator.getXmlReportDocument();
				XmlInformation info = (XmlInformation)report.getInformation();

				if (info.getStarCount().getStr() < minimum_STR_count)
					throw new ExaminationFailedException();

				if (instruction.getDate() != null)
					info.setDate(instruction.getDate());
				if (instruction.getExposure() != null) {
					XmlExposure exposure = new XmlExposure();
					exposure.setContent(instruction.getExposure().getContent());
					exposure.setUnit(instruction.getExposure().getUnit());
					info.setExposure(exposure);
				}
				if (instruction.getObserver() != null)
					info.setObserver(instruction.getObserver());
				if (instruction.getFilter() != null)
					info.setFilter(instruction.getFilter());
				if (instruction.getChip() != null)
					info.setChip(instruction.getChip());
				if (instruction.getInstruments() != null)
					info.setInstruments(instruction.getInstruments());
				if (instruction.getUnofficial() != null)
					info.setUnofficial(instruction.getUnofficial());

				// Saves As XML file.
				if (instruction.getOutput("xml") != null) {
					// Converts the absolute image file path in the XML document into
					// the relative path from the XML file if possible.
					File file = new File(instruction.getOutput("xml")).getAbsoluteFile();
					XmlImage xml_image = (XmlImage)report.getInformation().getImage();
					String image_path = xml_image.getContent();
					image_path = FileManager.relativatePathFrom(image_path, file);
					xml_image.setContent(image_path);

					file = new File(instruction.getOutput("xml"));
					report.write(file);

					monitor_set.addMessage("Succeeded to save " + file.getPath());
				}

				// Saves As PXF file.
				if (instruction.getOutput("pxf") != null) {
					File file = new File(instruction.getOutput("pxf"));

					PrintWriter out = Encoder.newWriter(file);
					new PxfWriter(report).write(out);
					out.close();

					monitor_set.addMessage("Succeeded to save " + file.getPath());
				}
			}
		} catch ( Exception exception ) {
			if (log_stream != null)
				log_stream.close();

			throw exception;
		}

		if (log_stream != null)
			log_stream.close();
	}
}
