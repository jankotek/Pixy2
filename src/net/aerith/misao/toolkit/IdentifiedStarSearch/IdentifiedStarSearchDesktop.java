/*
 * @(#)IdentifiedStarSearchDesktop.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.IdentifiedStarSearch;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.util.MonitorSet;

/**
 * The <code>IdentifiedStarSearchDesktop</code> represents a desktop
 * to search stars identified with a specified catalog.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 March 11
 */

public class IdentifiedStarSearchDesktop extends VariabilityDesktop {
	/**
	 * This desktop.
	 */
	protected IdentifiedStarSearchDesktop desktop;

	/**
	 * Constructs a <code>IdentifiedStarSearchDesktop</code>.
	 */
	public IdentifiedStarSearchDesktop ( ) {
		desktop = this;

		addWindowListener(new OpenWindowListener());
	}

	/**
	 * Returns true when the magnitude database is read only. This 
	 * method must be overrided in the subclasses.
	 * @return true when the magnitude database is read only.
	 */
	public boolean isMagnitudeDatabaseReadOnly ( ) {
		return true;
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

			IdentifiedStarSearchInternalFrame frame = new IdentifiedStarSearchInternalFrame(desktop);
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
