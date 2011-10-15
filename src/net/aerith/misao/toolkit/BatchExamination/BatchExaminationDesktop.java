/*
 * @(#)BatchExaminationDesktop.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.BatchExamination;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.util.MonitorSet;

/**
 * The <code>BatchExaminationDesktop</code> represents a desktop of 
 * batch examination.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class BatchExaminationDesktop extends BaseDesktop {
	/**
	 * This desktop.
	 */
	protected BatchExaminationDesktop desktop;

	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>BatchExaminationDesktop</code>.
	 */
	public BatchExaminationDesktop ( ) {
		desktop = this;

		pane = getContentPane();

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
			BatchExaminationInternalFrame frame = new BatchExaminationInternalFrame(desktop);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(700,500);
			frame.setTitle("Image Instructions");
			frame.setVisible(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setResizable(true);
			addFrame(frame);

			frame.addMonitor(getMonitorSet());
		}
	}
}
