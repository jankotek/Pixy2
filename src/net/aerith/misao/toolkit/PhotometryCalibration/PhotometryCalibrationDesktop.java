/*
 * @(#)PhotometryCalibrationDesktop.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.PhotometryCalibration;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.util.MonitorSet;

/**
 * The <code>PhotometryCalibrationDesktop</code> represents a desktop 
 * for photometry calibration.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class PhotometryCalibrationDesktop extends BaseDesktop {
	/**
	 * This desktop.
	 */
	protected PhotometryCalibrationDesktop desktop;

	/**
	 * Constructs a <code>PhotometryCalibrationDesktop</code>.
	 */
	public PhotometryCalibrationDesktop ( ) {
		desktop = this;

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
	 * The <code>OpenWindowListener</code> is a listener class of
	 * opening this window.
	 */
	protected class OpenWindowListener extends WindowAdapter {
		/**
		 * Invoked when this window is opened.
		 * @param e contains the event status.
		 */
		public void windowOpened ( WindowEvent e ) {
			openLogWindow();

			PhotometryCalibrationInternalFrame frame = new PhotometryCalibrationInternalFrame(desktop);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(700,500);
			frame.setTitle("XML Files");
			frame.setVisible(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setResizable(true);
			addFrame(frame);

			frame.addMonitor(getMonitorSet());
		}
	}
}
