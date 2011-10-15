/*
 * @(#)PhotometryZeroPointAdjustmentDesktop.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.PhotometryZeroPointAdjustment;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.PhotometryOperationSettingDialog;
import net.aerith.misao.xml.*;
import net.aerith.misao.database.GlobalDBManager;

/**
 * The <code>PhotometryZeroPointAdjustmentDesktop</code> represents a 
 * desktop to adjust zero-point of photometry.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 July 15
 */

public class PhotometryZeroPointAdjustmentDesktop extends BaseDesktop {
	/**
	 * This desktop.
	 */
	protected PhotometryZeroPointAdjustmentDesktop desktop;

	/**
	 * The dialog to set parameters of zero-point adjustment.
	 */
	protected PhotometryOperationSettingDialog dialog;

	/**
	 * The reference database manager.
	 */
	protected GlobalDBManager ref_db_manager;

	/**
	 * Constructs a <code>PhotometryZeroPointAdjustmentDesktop</code>.
	 */
	public PhotometryZeroPointAdjustmentDesktop ( ) {
		desktop = this;

		dialog = new PhotometryOperationSettingDialog();

		addWindowListener(new OpenWindowListener());
	}

	/*
	 * Gets the monitor set.
	 * @return the monitor set.
	 */
	protected MonitorSet getMonitorSet ( ) {
		return monitor_set;
	}

	/**
	 * Gets the setting dialog.
	 * @return the setting dialog.
	 */
	public PhotometryOperationSettingDialog getSettingDialog ( ) {
		return dialog;
	}

	/**
	 * Sets the reference database manager.
	 * @param db_manager the reference database manager.
	 */
	public void setReferenceDBManager ( GlobalDBManager db_manager ) {
		this.ref_db_manager = db_manager;
	}

	/**
	 * Gets the reference database manager.
	 * @return the reference database manager.
	 */
	public GlobalDBManager getReferenceDBManager ( ) {
		return ref_db_manager;
	}

	/**
	 * Creates new reference and target frames.
	 * @info_ref    the list of reference image informations.
	 * @info_target the list of target image informations.
	 */
	public void createNewFrames ( XmlInformation[] info_ref, XmlInformation[] info_target ) {
		ReferenceInternalFrame frame_ref = new ReferenceInternalFrame(desktop);
		frame_ref.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame_ref.setSize(700,500);
		frame_ref.setTitle("Reference XML Files");
		frame_ref.setVisible(true);
		frame_ref.setMaximizable(true);
		frame_ref.setIconifiable(true);
		frame_ref.setResizable(true);
		addFrame(frame_ref);

		TargetInternalFrame frame_target = new TargetInternalFrame(desktop);
		frame_target.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame_target.setSize(700,500);
		frame_target.setTitle("Target XML Files");
		frame_target.setVisible(true);
		frame_target.setMaximizable(true);
		frame_target.setIconifiable(true);
		frame_target.setResizable(true);
		addFrame(frame_target);

		frame_ref.addMonitor(getMonitorSet());
		frame_target.addMonitor(getMonitorSet());

		Rectangle rect = frame_ref.getBounds();
		frame_target.setLocation((int)(rect.getX() + 20), (int)(rect.getY() + 20));

		try {
			frame_ref.addInformations(info_ref);
			frame_target.addInformations(info_target);
		} catch ( Exception exception ) {
			String message = "Some XML report documents are not added to the table.";
			JOptionPane.showMessageDialog(pane, message, "Warning", JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * The <code>OpenWindowListener</code> is a listener class of
	 * opening this window.
	 */
	protected class OpenWindowListener extends WindowAdapter {
		/**
		 * Invoked when this window is opened.
		 * @param e contains the event status.
		 */
		public void windowOpened ( WindowEvent e ) {
			XmlInformation[] empty_info = new XmlInformation[0];
			createNewFrames(empty_info, empty_info);
		}
	}
}
