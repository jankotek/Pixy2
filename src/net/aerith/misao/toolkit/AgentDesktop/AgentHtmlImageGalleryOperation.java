/*
 * @(#)AgentHtmlImageGalleryOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.AgentDesktop;
import javax.swing.*;
import java.io.*;
import net.aerith.misao.util.*;
import net.aerith.misao.io.FileManager;
import net.aerith.misao.database.GlobalDBManager;
import net.aerith.misao.toolkit.HtmlImageGallery.HtmlImageGalleryOperation;

/**
 * The <code>AgentHtmlImageGalleryOperation</code> represents an 
 * operation to create HTML image gallery using the agent.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2005 May 22
 */

public class AgentHtmlImageGalleryOperation extends HtmlImageGalleryOperation {
	/**
	 * The image size.
	 */
	protected Size image_size = new Size(200, 200);

	/**
	 * True when to unify the resolution.
	 */
	protected boolean unify_resolution = false;

	/**
	 * The resolution in arcsec/pixel.
	 */
	protected double resolution = 1.0;

	/**
	 * True when to rotate north up at right angles.
	 */
	protected boolean ratate_north_up_at_right_angles = false;

	/**
	 * Constructs an <code>AgentHtmlImageGalleryOperation</code>.
	 * @param conductor    the conductor of multi task operation.
	 * @param file_manager the file manager.
	 * @param db_manager   the database manager.
	 * @param fits         true when to create FITS thumbnail images.
	 * @param past_mode    the mode to add past images from the 
	 * database.
	 * @param dss          true when to add a DSS image.
	 */
	public AgentHtmlImageGalleryOperation ( MultiTaskConductor conductor, FileManager file_manager, GlobalDBManager db_manager, boolean fits, int past_mode, boolean dss ) {
		super(conductor, file_manager, db_manager, fits, past_mode, dss);

		setWarningMessageEnabled(false);
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
	 * Sets the directory to create the HTML image gallery.
	 * @param directory the directory.
	 */
	public void setDirectory ( File directory ) {
		this.directory = directory;
	}

	/**
	 * Sets the image size.
	 * @param size the image size.
	 */
	public void setImageSize ( Size size ) {
		image_size = size;
	}

	/**
	 * Gets the image size.
	 * @return the image size.
	 */
	protected Size getImageSize ( ) {
		return image_size;
	}

	/**
	 * Sets the flag to unify the resolution.
	 * @param flag true when to unify the resolution.
	 */
	public void setResolutionUnified ( boolean flag ) {
		unify_resolution = flag;
	}

	/**
	 * Returns true when to unify the resolution.
	 * @return true when to unify the resolution.
	 */
	protected boolean unifiesResolution ( ) {
		return unify_resolution;
	}

	/**
	 * Sets the resolution in arcsec/pixel.
	 * @param resolution the resolution in arcsec/pixel.
	 */
	public void setResolution ( double resolution ) {
		this.resolution = resolution;
	}

	/**
	 * Gets the resolution in arcsec/pixel.
	 * @return the resolution in arcsec/pixel.
	 */
	protected double getResolution ( ) {
		return resolution;
	}

	/**
	 * Sets the flag to rotate north up at right angles.
	 * @param flag true when to rotate north up at right angles.
	 */
	public void setRotatedNorthUpAtRightAngles ( boolean flag ) {
		ratate_north_up_at_right_angles = flag;
	}

	/**
	 * Returns true when to rotate north up at right angles.
	 * @return true when to rotate north up at right angles.
	 */
	protected boolean rotatesNorthUpAtRightAngles ( ) {
		return ratate_north_up_at_right_angles;
	}
}
