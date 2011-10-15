/*
 * @(#)NewStarSearchDesktop.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.NewStarSearch;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.util.MonitorSet;

/**
 * The <code>NewStarSearchDesktop</code> represents a desktop to 
 * search new stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class NewStarSearchDesktop extends VariabilityDesktop {
	/**
	 * This desktop.
	 */
	protected NewStarSearchDesktop desktop;

	/**
	 * Constructs a <code>NewStarSearchDesktop</code>.
	 */
	public NewStarSearchDesktop ( ) {
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

			NewStarSearchInternalFrame frame = new NewStarSearchInternalFrame(desktop);
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
