/*
 * @(#)AgentHtmlImageGalleryInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.AgentDesktop;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import net.aerith.misao.util.*;
import net.aerith.misao.database.GlobalDBManager;
import net.aerith.misao.gui.*;
import net.aerith.misao.toolkit.HtmlImageGallery.HtmlImageGalleryInternalFrame;

/**
 * The <code>AgentHtmlImageGalleryInternalFrame</code> represents a 
 * frame which shows the images and XML files, and the progress to 
 * create HTML image gallery, using the agent.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2005 May 22
 */

public class AgentHtmlImageGalleryInternalFrame extends HtmlImageGalleryInternalFrame {
	/**
	 * Constructs an <code>AgentHtmlImageGalleryInternalFrame</code>.
	 * @param variability_list the list of variability records.
	 * @param mode             the mode to create HTML image gallery.
	 * @param fits             true when to create FITS thumbnail 
	 * images.
	 * @param past_mode        the mode to add past images from the
	 * database.
	 * @param dss              true when to add a DSS image.
	 * @param desktop          the parent desktop.
	 */
	public AgentHtmlImageGalleryInternalFrame ( Vector variability_list, int mode, boolean fits, int past_mode, boolean dss, BaseDesktop desktop ) {
		super(variability_list, mode, fits, past_mode, dss, desktop);

		GlobalDBManager db_manager = null;
		try {
			db_manager = desktop.getDBManager();
		} catch ( IOException exception ) {
			// Makes no problem.
		}

		setOperation(new AgentHtmlImageGalleryOperation(table, desktop.getFileManager(), db_manager, fits, past_mode, dss));

		// Disables due to proceeded to the next operation
		// except for interruption.
		control_panel.setSucceededMessageEnabled(false);
		control_panel.setInterruptedMessageEnabled(true);
		control_panel.setFailedMessageEnabled(false);
	}

	/**
	 * Sets an observer.
	 * @param observer an observer
	 */
	public void setAgentOperationObserver ( OperationObserver observer ) {
		operation.addObserver(observer);
	}

	/**
	 * Sets the directory to create the HTML image gallery.
	 * @param directory the directory.
	 */
	public void setDirectory ( File directory ) {
		((AgentHtmlImageGalleryOperation)operation).setDirectory(directory);
	}

	/**
	 * Sets the image size.
	 * @param size the image size.
	 */
	public void setImageSize ( Size size ) {
		((AgentHtmlImageGalleryOperation)operation).setImageSize(size);
	}

	/**
	 * Sets the flag to unify the resolution.
	 * @param flag true when to unify the resolution.
	 */
	public void setResolutionUnified ( boolean flag ) {
		((AgentHtmlImageGalleryOperation)operation).setResolutionUnified(flag);
	}

	/**
	 * Sets the resolution in arcsec/pixel.
	 * @param resolution the resolution in arcsec/pixel.
	 */
	public void setResolution ( double resolution ) {
		((AgentHtmlImageGalleryOperation)operation).setResolution(resolution);
	}

	/**
	 * Sets the flag to rotate north up at right angles.
	 * @param flag true when to rotate north up at right angles.
	 */
	public void setRotatedNorthUpAtRightAngles ( boolean flag ) {
		((AgentHtmlImageGalleryOperation)operation).setRotatedNorthUpAtRightAngles(flag);
	}
}
