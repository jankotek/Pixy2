/*
 * @(#)Resource.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy;
import java.io.*;
import javax.swing.*;
import net.aerith.misao.util.*;
import net.aerith.misao.xml.*;

/**
 * The <code>Resource</code> consists of string messages and HTML 
 * messages for the PIXY system.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 July 15
 */

public class Resource {
	/**
	 * The configuration of the system.
	 */
	private static XmlConfiguration configuration;

	/**
	 * The image icon of the PIXY System 2.
	 */
	private final static ImageIcon pixy2_icon = new ImageIcon(ClassLoader.getSystemResource("net/aerith/misao/resources/images/pixy2.gif"));

	/**
	 * The icon to run the image examination.
	 */
	private final static ImageIcon single_pixy_icon = new ImageIcon(ClassLoader.getSystemResource("net/aerith/misao/resources/images/single.gif"));

	/**
	 * The icon to run the image examination in the tutorial mode.
	 */
	private final static ImageIcon tutorial_pixy_icon = new ImageIcon(ClassLoader.getSystemResource("net/aerith/misao/resources/images/tutorial.gif"));

	/**
	 * The icon to run the review.
	 */
	private final static ImageIcon review_icon = new ImageIcon(ClassLoader.getSystemResource("net/aerith/misao/resources/images/review.gif"));

	/**
	 * The icon to run the multiple image examination.
	 */
	private final static ImageIcon multiple_pixy_icon = new ImageIcon(ClassLoader.getSystemResource("net/aerith/misao/resources/images/multiple.gif"));

	/**
	 * The icon to run the batch examination.
	 */
	private final static ImageIcon batch_pixy_icon = new ImageIcon(ClassLoader.getSystemResource("net/aerith/misao/resources/images/batch.gif"));

	/**
	 * The image icon of the success status.
	 */
	private final static ImageIcon status_success_icon = new ImageIcon(ClassLoader.getSystemResource("net/aerith/misao/resources/images/success-i.gif"));

	/**
	 * The image icon of the midway status.
	 */
	private final static ImageIcon status_midway_icon = new ImageIcon(ClassLoader.getSystemResource("net/aerith/misao/resources/images/midway-i.gif"));

	/**
	 * The image icon of the error status.
	 */
	private final static ImageIcon status_error_icon = new ImageIcon(ClassLoader.getSystemResource("net/aerith/misao/resources/images/error-i.gif"));

	/**
	 * Initializes the system configuration.
	 */
	public static void initialize ( ) {
		net.aerith.misao.xml.relaxer.PixyFactory.setFactory(new DefaultPixyFactory());

		readConfiguration();
	}

	/**
	 * Gets the image icon of the PIXY System 2.
	 * return the image icon of the PIXY System 2.
	 */
	public static ImageIcon getSystemIcon ( ) {
		return pixy2_icon;
	}

	/**
	 * Gets the icon to run the image examination.
	 * return the icon to run the image examination.
	 */
	public static ImageIcon getSinglePixyIcon ( ) {
		return single_pixy_icon;
	}

	/**
	 * Gets the icon to run the image examination in the tutorial mode.
	 * return the icon to run the image examination in the tutorial 
	 * mode.
	 */
	public static ImageIcon getTutorialPixyIcon ( ) {
		return tutorial_pixy_icon;
	}

	/**
	 * Gets the icon to run the review.
	 * return the icon to run the review.
	 */
	public static ImageIcon getReviewIcon ( ) {
		return review_icon;
	}

	/**
	 * Gets the icon to run the multiple image examination.
	 * return the icon to run the multiple image examination.
	 */
	public static ImageIcon getMultiplePixyIcon ( ) {
		return multiple_pixy_icon;
	}

	/**
	 * Gets the icon to run the batch examination.
	 * return the icon to run the batch examination.
	 */
	public static ImageIcon getBatchPixyIcon ( ) {
		return batch_pixy_icon;
	}

	/**
	 * Returns the version.
	 * @return the version.
	 */
	public static String getVersion ( ) {
		return "PIXY System 2 (2007 July 15)";
	}

	/**
	 * Gets the icon of the success status.
	 * @return the icon of the success status.
	 */
	public static ImageIcon getStatusSuccessIcon ( ) {
		return status_success_icon;
	}

	/**
	 * Gets the icon of the midway status.
	 * @return the icon of the midway status.
	 */
	public static ImageIcon getStatusMidwayIcon ( ) {
		return status_midway_icon;
	}

	/**
	 * Gets the icon of the error status.
	 * @return the icon of the error status.
	 */
	public static ImageIcon getStatusErrorIcon ( ) {
		return status_error_icon;
	}

	/**
	 * Gets the priority of a thread.
	 * @return the priority of a thread.
	 */
	public static int getThreadPriority ( ) {
		return 2;
	}

	/**
	 * Reads the configuration of the system. This method must be
	 * invoked only once when the system launches.
	 */
	private static void readConfiguration ( ) {
		configuration = new XmlConfiguration();

		try {
			File file = new File("pixy2.xml");
			configuration.read(file);
		} catch ( IOException exception ) {
			// It's OK even if the configuration file does not exist.
		}
	}

	/**
	 * Saves the configuration of the system.
	 */
	private static void saveConfiguration ( ) {
		try {
			File file = new File("pixy2.xml");
			configuration.write(file);
		} catch ( IOException exception ) {
			// It's OK even if the configuration file cannot be saved.
		}
	}

	/**
	 * Sets the default catalog path. Several paths can be described 
	 * separating with path separator.
	 * @param catalog_name the catalog name.
	 * @param path         the default path.
	 */
	public static void setDefaultCatalogPath ( String catalog_name, String path ) {
		XmlCatalogPath[] catalog_paths = (XmlCatalogPath[])configuration.getCatalogPath();
		for (int i = 0 ; i < catalog_paths.length ; i++) {
			if (catalog_paths[i].getCatalog().equals(catalog_name)) {
				catalog_paths[i].setPath(path);
				saveConfiguration();
				return;
			}
		}

		XmlCatalogPath catalog_path = new XmlCatalogPath();
		catalog_path.setCatalog(catalog_name);
		catalog_path.setPath(path);
		configuration.addCatalogPath(catalog_path);
		saveConfiguration();
	}

	/**
	 * Gets the default catalog path. Several paths can be described 
	 * separating with path separator.
	 * @param catalog_name the catalog name.
	 * @return the default catalog path of the specified catalog.
	 */
	public static String getDefaultCatalogPath ( String catalog_name ) {
		XmlCatalogPath[] catalog_paths = (XmlCatalogPath[])configuration.getCatalogPath();
		for (int i = 0 ; i < catalog_paths.length ; i++) {
			if (catalog_paths[i].getCatalog().equals(catalog_name))
				return catalog_paths[i].getPath();
		}

		return "";
	}

	/**
	 * Sets the default FITS data order.
	 * @param order the default FITS data order.
	 */
	public static void setDefaultFitsOrder ( String order ) {
		XmlImageConfig config = (XmlImageConfig)configuration.getImageConfig();
		if (config == null)
			config = new XmlImageConfig();

		config.setFitsOrder(order);
		configuration.setImageConfig(config);
		saveConfiguration();
	}

	/**
	 * Gets the default FITS data order.
	 * @return the default FITS data order.
	 */
	public static String getDefaultFitsOrder ( ) {
		String order = "japanese";

		if (configuration.getImageConfig() != null) {
			if (configuration.getImageConfig().getFitsOrder() != null)
				order = configuration.getImageConfig().getFitsOrder();
		}

		return order;
	}

	/**
	 * Returns the version and copyright message.
	 * @return the version and copyright message.
	 */
	public static String[] getVersionAndCopyright ( ) {
		String[] messages = new String[4];
		messages[0] = getVersion();
		messages[1] = "Copyright (C) 1997-2007 Seiichi Yoshida (comet@aerith.net)";
		messages[2] = "All rights reserved.";
		messages[3] = "MISAO Project  http://www.aerith.net/misao/";
		return messages;
	}

	/**
	 * Returns the HTML string of special thanks messages.
	 * @return the HTML string of special thanks messages.
	 */
	public static String getSpecialThanksHtmlMessage ( ) {
		String html = "<html><body>";
		html += "<p>The copyright of the sample image is reserved by Ken-ichi Kadota.</p>";
		html += "<p><center><h3><font color=\"#ff00aa\">";
		html += "Special Thanks To:";
		html += "</font></h3></center></p>";
		html += "<p>Ken-ichi Kadota, Akimasa Nakamura, and all image contributors.</p>";
		html += "<p>Ken'ichi Torii, Makoto Yoshikawa, and all misao-j ML members.</p>";
		html += "<p>Taichi Kato, and the VSOLJ (Variable Star Observers League in Japan).</p>";
		html += "<p>Bruce Koehn, David G. Monet, and the U.S. Naval Observatory.</p>";
		html += "<p>Bill J. Gray, and the Project Pluto.</p>";
		html += "<p>John Greaves, and all bug reporters.</p>";
		html += "<p>... and everyone for informative advices and comments.</p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Gets the filename of the sample image.
	 * @return the filename of the sample image.
	 */
	public final static String getSampleImageFilename ( ) {
		return "sample.fts";
	}

	/**
	 * Gets the R.A. and Decl. of the center of the sample image.
	 * @return the R.A. and Decl. of the center of the sample image.
	 */
	public final static Coor getSampleImageCoor ( ) {
		return new Coor(17, 52, 0, true, 17, 40, 0);
	}

	/**
	 * Gets the date and time of the sample image.
	 * @return the date and time of the sample image.
	 */
	public final static JulianDay getSampleImageDate ( ) {
		return new JulianDay(1999, 3, 31, 18, 8, 50);
	}

	/**
	 * Gets the exposure time of the sample image in second.
	 * @return the exposure time of the sample image in second.
	 */
	public final static double getSampleImageExposureInSecond ( ) {
		return 60.0;
	}

	/**
	 * Gets the horizontal field of view of the sample image in degree.
	 * @return the horizontal field of view of the sample image in 
	 * degree.
	 */
	public final static double getSampleImageHorizontalFieldOfView ( ) {
		return 0.3;
	}

	/**
	 * Gets the vertical field of view of the sample image in degree.
	 * @return the vertical field of view of the sample image in 
	 * degree.
	 */
	public final static double getSampleImageVerticalFieldOfView ( ) {
		return 0.3;
	}

	/**
	 * Gets the filename of the sample star catalog.
	 * @return the filename of the sample star catalog.
	 */
	public final static String getSampleStarCatalogFilename ( ) {
		return "sample.cat";
	}

	/**
	 * Gets the magnitude system code of the sample.
	 * @return the magnitude system code of the sample.
	 */
	public final static String getSampleImageMagSystemCode ( ) {
		return "C";
	}

	/**
	 * Gets the CCD chip of the sample.
	 * @return the CCD chip of the sample.
	 */
	public final static String getSampleImageChipCode ( ) {
		return "KAF 1600";
	}

	/**
	 * Returns the help message of the PIXY system when the step is at
	 * the beginning.
	 * @return the HTML string of help message.
	 */
	public static String getHelpHtmlMessageAtBeginning ( ) {
		String html = "<html><body>";
		html += "<p><center><h1><font color=\"#ff00aa\">";
		html += "Welcome to the PIXY System 2.";
		html += "</font></h1></center></p>";
		html += "<p>";
		html += "Now let's begin. First of all, let's open a FITS image. ";
		html += "Please open the <b><font color=\"#0000ff\">Operations</font></b> menu ";
		html += "and select <b><font color=\"#0000ff\">Open Image</font></b>.";
		html += "</p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Returns the tutorial message of the PIXY system when the step 
	 * is at the beginning.
	 * @return the HTML string of tutorial message.
	 */
	public static String getTutorialHtmlMessageAtBeginning ( ) {
		String html = "<html><body>";
		html += "<p>Now you are running the PIXY System 2 in the tutorial mode.</p>";
		html += "<p>";
		html += "When you select the menu <b><font color=\"#0000ff\">Open Image</font></b>, ";
		html += "a dialog will appear to select an image to open. ";
		html += "Please select the sample image file attached to the PIXY System 2, ";
		html += "<b><font color=\"#ff00aa\">sample.fts</font></b>. ";
		html += "The sample image was taken by Ken-ichi Kadota, Ageo, Japan, on 1999 Mar. 31. ";
		html += "The field is around V4334 Sgr, the Sakurai's object. ";
		html += "</p>";
		html += "<p>In the dialog, the filename is set properly in advance.</p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Returns the help message of the PIXY system when the step is
	 * after the image is opened.
	 * @return the HTML string of help message.
	 */
	public static String getHelpHtmlMessageAfterImageOpened ( ) {
		String html = "<html><body>";
		html += "<p>Now the image is open.</p>";
		html += "<p>";
		html += "Before running the star detection and matching process, ";
		html += "some image processing functions can be applied to the image. ";
		html += "please open the <b><font color=\"#0000ff\">Image</font></b> menu ";
		html += "and select items you would like to run.";
		html += "</p>";
		html += "<blockquote><ul>";
		html += "<li><b><font color=\"#aa00ff\">Inverse White and Black</font></b><br>";
		html += "When the background of the image is white and the stars are black. ";
		html += "<li><b><font color=\"#aa00ff\">Reverse Upside Down</font></b><br>";
		html += "When the east locates in the clockwise direction from the north. ";
		html += "<li><b><font color=\"#aa00ff\">Rescale ST-4/6 Image</font></b><br>";
		html += "When the image is taken with SBIG ST-4/ST-6 CCD cameras. ";
		html += "<li><b><font color=\"#aa00ff\">Fill Illegal Rows and Columns</font></b><br>";
		html += "When some pixels from the edges have illegal value (maybe 0). ";
		html += "<li><b><font color=\"#aa00ff\">Equalize</font></b><br>";
		html += "When there are band like patterns on the image (often at the edge). ";
		html += "</ul></blockquote>";
		html += "<p>";
		html += "When everything is OK, let's run the operation. ";
		html += "Please open the <b><font color=\"#0000ff\">Operations</font></b> menu ";
		html += "and select <b><font color=\"#0000ff\">Detect Stars</font></b>.";
		html += "</p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Returns the help message of the PIXY system when the step is
	 * after stars are detected from the image.
	 * @return the HTML string of help message.
	 */
	public static String getHelpHtmlMessageAfterStarsDetected ( ) {
		String html = "<html><body>";
		html += "<p>Now stars are detected from the image.</p>";
		html += "<p>";
		html += "Next step is matching detected stars with star data in the catalog. ";
		html += "In this process, the R.A. Decl. and magnitude of all detected stars are measured. ";
		html += "</p><p>";
		html += "Please open the <b><font color=\"#0000ff\">Operations</font></b> menu ";
		html += "and select <b><font color=\"#0000ff\">Matching</font></b>.";
		html += "</p><p>";
		html += "After the menu is selected, a dialog opens. ";
		html += "Please select the star catalog and set the CD-ROMs in the drive, ";
		html += "Please also set the approximate R.A. and Decl. of the center, ";
		html += "and the approximate field of view properly.";
		html += "</p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Returns the tutorial message of the PIXY system when the step 
	 * is after stars are detected from the image.
	 * @return the HTML string of tutorial message.
	 */
	public static String getTutorialHtmlMessageAfterStarsDetected ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "When you select the menu <b><font color=\"#0000ff\">Matching</font></b>, ";
		html += "a dialog will appear to set parameters for matching operation. ";
		html += "Now that running in the tutorial mode, all parameters are typed properly in advance. ";
		html += "</p><p>";
		html += "In the case of the sample image, you can use the sample star catalog attached to the PIXY System 2, ";
		html += "<b><font color=\"#ff00aa\">sample.cat</font></b>, in matching. ";
		html += "By default, the <b><font color=\"#ff00aa\">Sample Star Catalog</font></b> is selected in the";
		html += "<b><font color=\"#00aa33\">Catalog</font></b> field and ";
		html += "<b><font color=\"#ff00aa\">sample.cat</font></b> is typed in the <b><font color=\"#00aa33\">Path</font></b> field. ";
		html += "You can also select some other catalogs if you have the files or CD-ROMs. ";
		html += "</p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Returns the help message of the PIXY system when the step is
	 * after matching between detected stars and catalog data.
	 * @param failed_flag true if matching was failed.
	 * @return the HTML string of help message.
	 */
	public static String getHelpHtmlMessageAfterMatching ( boolean failed_flag ) {
		String html = "<html><body>";
		html += "<p>";
		if (failed_flag == true) {
			html += "Matching between the detected stars and the catalog data was probably failed.";
		} else {
			html += "Matching between the detected stars and the catalog data was succeeded.";
		}
		html += "</p><p>";
		html += "When you confirm the field of chart of catalog stars coincides with that of detected stars, ";
		html += "please open the <b><font color=\"#0000ff\">Operations</font></b> menu ";
		html += "and select <b><font color=\"#0000ff\">Make Pairs</font></b>. ";
		html += "Then the system makes pairs between each detected star and catalog data, ";
		html += "and R.A. and Decl., and magnitude of all detected stars will be measured. ";
		html += "</p><p>";
		html += "When the field of chart of catalog stars does not coincide with that of detected stars, ";
		html += "unfortunately, matching between the detected stars and the catalog data was failed. ";
		html += "please change some of the parameters and retry. ";
		html += "</p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Returns the help message of the PIXY system when the step is
	 * after making pairs between detected stars and catalog data.
	 * @return the HTML string of help message.
	 */
	public static String getHelpHtmlMessageAfterPairing ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "Making pairs between the detected stars and the catalog data was succeeded.";
		html += "</p><p>";
		html += "Now R.A. and Decl., and magnitude of all detected stars are measured. ";
		html += "Please click a star on the chart windows, and you can see the information of the star.";
		html += "</p><p>";
		html += "Stars are plotted colorfully, which represents:";
		html += "<ul>";
		html += "<li><b><font color=\"#00ff00\">Green</font></b> represents a star recorded in the catalog and also detected from the image.";
		html += "<li><b><font color=\"#ff0000\">Red</font></b> represents a star recorded in the catalog but not detected from the image, ";
		html += "or a star detected from the image but not recorded in the catalog.";
		html += "</ul>";
		html += "</p><p>";
		html += "You had better set some parameters in the <b><font color=\"#0000ff\">Parameter</font></b> menu. ";
		html += "</p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Returns the help message of the PIXY system when the step is
	 * after making pairs between detected stars and catalog data.
	 * @return the HTML string of help message.
	 */
	public static String getSecondHelpHtmlMessageAfterPairing ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "If you would like to know what comets, asteroids or variable stars are ";
		html += "in your image, please open the <b><font color=\"#0000ff\">Analysis</font></b> menu ";
		html += "and select <b><font color=\"#0000ff\">Identify</font></b>. ";
		html += "The result of identification can be reviewed by ";
		html += "<blockquote><ul>";
		html += "<li><b><font color=\"#aa00ff\">Identification Report</font></b>";
		html += "<li><b><font color=\"#aa00ff\">Identification Overview Chart</font></b>";
		html += "</ul></blockquote>";
		html += "</p><p>";
		html += "You can make a magnitude report to the VSNET/VSOLJ by the ";
		html += "<b><font color=\"#0000ff\">Magnitude Report to VSNET/VSOLJ</font></b> menu. ";
		html += "</p><p>";
		html += "You can save the examination result in the XML file. ";
		html += "please open the <b><font color=\"#0000ff\">File</font></b> menu ";
		html += "and select <b><font color=\"#0000ff\">Save Result As XML</font></b>. ";
		html += "You can review the examination from the XML file in the ";
		html += "<b><font color=\"#00aa33\">review</font></b> mode. ";
		html += "</p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Returns the tutorial message of the PIXY system when the step 
	 * is after making pairs between detected stars and catalog data.
	 * @return the HTML string of tutorial message.
	 */
	public static String getTutorialHtmlMessageAfterPairing ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "In the case of the sample image, you can review the result of identification ";
		html += "by opening the <b><font color=\"#ff00aa\">sample.xml</font></b>, ";
		html += "attached to the PIXY System 2, in the <b><font color=\"#00aa33\">review</font></b> mode. ";
		html += "</p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Returns the help message of the PIXY system to review at the
	 * beginning.
	 * @return the HTML string of help message.
	 */
	public static String getReviewHelpHtmlMessageAtBeginning ( ) {
		String html = "<html><body>";
		html += "<p><center><h1><font color=\"#ff00aa\">";
		html += "Welcome to the PIXY System 2.";
		html += "</font></h1></center></p>";
		html += "<p>";
		html += "Now let's review the result of image examination. ";
		html += "Please open the <b><font color=\"#0000ff\">File</font></b> menu ";
		html += "and select <b><font color=\"#0000ff\">Open XML File</font></b>. ";
		html += "Then the image window, the chart of detected stars and the ";
		html += "chart of catalog stars are opened. ";
		html += "After that, you can continue any kinds of analysis. ";
		html += "</p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Returns the help message of the PIXY system to review when the
	 * image is not found.
	 * @param path the path of the image.
	 * @return the HTML string of help message.
	 */
	public static String getReviewHelpHtmlMessageWhenImageNotFound ( String path ) {
		String html = "<html><body>";
		html += "<p>The image<blockquote>";
		html += path;
		html += "</blockquote>is not found. ";
		html += "Please open the <b><font color=\"#0000ff\">File</font></b> menu, ";
		html += "select <b><font color=\"#0000ff\">Open Image File</font></b> ";
		html += "and find the image file. ";
		html += "</p><p>";
		html += "Even if the image is not available now, ";
		html += "you can continue any kinds of analysis without the image. ";
		html += "</p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Initializes the system configuration.
	 */
	static {
		initialize();
	}
}
