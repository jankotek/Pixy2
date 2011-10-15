/*
 * @(#)Agent.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy;
import java.awt.Container;
import net.aerith.misao.image.MonoImage;
import net.aerith.misao.image.ImageConverter;
import net.aerith.misao.xml.XmlInstruction;
import net.aerith.misao.xml.XmlInformation;
import net.aerith.misao.util.Size;
import net.aerith.misao.gui.dialog.HtmlImageGallerySettingDialog;

/**
 * The <code>Agent</code> is an interface of agent to operate image 
 * examination in a specific situation with peculiar rules and styles.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2005 May 22
 */

public abstract class Agent implements ImageConverter {
	/**
	 * Returns the name of this agent.
	 * @return the name of this agent.
	 */
	public abstract String getName ( );

	/**
	 * Initializes. This method can be overrided in the sub class if 
	 * needed. For example, it shows a dialog to select image files 
	 * and input parameters.
	 * @param pane the container.
	 * @return true when ready.
	 */
	public boolean initialize ( Container pane ) {
		return true;
	}

	/**
	 * Returns the list of image files and instruction parameters. 
	 * @return the list of instruction parameters. 
	 */
	public abstract XmlInstruction[] getInstructions ( );

	/**
	 * Does something for an image before operation. This method can 
	 * be overrided in the sub class if needed. For example, it 
	 * downloads the image file and saves in the hard disk.
	 * @param instruction a set of instruction parameters.
	 * @return a new set of instruction parameters.
	 * @exception Exception if an exception occurs.
	 */
	public XmlInstruction preOperation ( XmlInstruction instruction )
		throws Exception
	{
		return instruction;
	}

	/**
	 * Applies some image filters to the specified image. This method 
	 * can be overrided in the sub class if needed. For example, it
	 * corrects the width/height ratio of a SBIG ST-4/ST-6 image.
	 * @param image an image.
	 * @return a new image.
	 * @exception Exception if an exception occurs.
	 */
	public MonoImage convertImage ( MonoImage image )
		throws Exception
	{
		return image;
	}

	/**
	 * Creates the examination operator.
	 * @param instruction the XML instruction element.
	 * @return the examination operator.
	 */
	public abstract ExaminationOperator createExaminationOperator ( XmlInstruction instruction );

	/**
	 * Gets the limiting magnitude for new star search.
	 * @param info the image information.
	 * @return the limiting magnitude for new star search.
	 */
	public abstract double getNewStarSearchLimitingMagnitude ( XmlInformation info );

	/**
	 * Gets the amplitude for new star search.
	 * @param info the image information.
	 * @return the amplitude for new star search.
	 */
	public abstract double getNewStarSearchAmplitude ( XmlInformation info );

	/**
	 * Gets the image size for HTML image gallery.
	 * @return the image size for HTML image gallery.
	 */
	public Size getHtmlGalleryImageSize ( ) {
		return new Size(200, 200);
	}

	/**
	 * Returns true when to unify the resolution of images in the HTML 
	 * image gallery.
	 * @return true when to unify the resolution of images in the HTML 
	 * image gallery.
	 */
	public boolean unifiesHtmlGalleryImageResolution ( ) {
		return false;
	}

	/**
	 * Gets the resolution in arcsec/pixel of images in the HTML image 
	 * gallery.
	 * @return the resolution in arcsec/pixel of images in the HTML 
	 * image gallery.
	 */
	public double getHtmlGalleryImageResolution ( ) {
		return 1.0;
	}

	/**
	 * Returns true when to rotate images north up at right angles in 
	 * the HTML image gallery.
	 * @return true when to rotate images north up at right angles in 
	 * the HTML image gallery.
	 */
	public boolean rotatesHtmlGalleryImageNorthUpAtRightAngles ( ) {
		return false;
	}

	/**
	 * Returns the mode of HTML image gallery for new star search.
	 * @return the mode of HTML image gallery for new star search.
	 */
	public int getNewStarSearchHtmlImageGalleryMode ( ) {
		return HtmlImageGallerySettingDialog.MODE_BRIGHTEST;
	}

	/**
	 * Returns the mode of HTML image gallery for new star search to 
	 * add past images from the database.
	 * @return the mode of HTML image gallery for new star search to 
	 * add past images from the database.
	 */
	public int getNewStarSearchHtmlImageGalleryPastImageMode ( ) {
		return HtmlImageGallerySettingDialog.PAST_MODE_NONE;
	}

	/**
	 * Returns true when to add a DSS image in the HTML image gallery.
	 * @return true when to add a DSS image in the HTML image gallery.
	 */
	public boolean addsDssImageInHtmlImageGallery ( ) {
		return false;
	}
}
