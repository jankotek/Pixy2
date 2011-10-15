/*
 * @(#)VariableStarSearchDesktop.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.VariableStarSearch;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.xml.*;

/**
 * The <code>VariableStarSearchDesktop</code> represents a desktop to 
 * search variable stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class VariableStarSearchDesktop extends VariabilityDesktop {
	/**
	 * The frame to show XML report documents in groups.
	 */
	protected GroupInternalFrame group_frame = null;

	/**
	 * This desktop.
	 */
	protected VariableStarSearchDesktop desktop;

	/**
	 * Constructs a <code>VariableStarSearchDesktop</code>.
	 */
	public VariableStarSearchDesktop ( ) {
		desktop = this;

		addWindowListener(new OpenWindowListener());
	}

	/**
	 * Creates a new group from the specified XML image information 
	 * elements, and shows a frame of XML report documents classified
	 * into some groups.
	 * @param infos the XML image information elements.
	 */
	public void createGroup ( XmlInformation[] infos ) {
		if (group_frame == null) {
			group_frame = new GroupInternalFrame(this);
			group_frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			group_frame.setSize(700,500);
			group_frame.setTitle("Groups of XML Files");
			group_frame.setVisible(true);
			group_frame.setMaximizable(true);
			group_frame.setIconifiable(true);
			group_frame.setResizable(true);
			addFrame(group_frame);

			group_frame.addMonitor(monitor_set);
		}

		group_frame.createGroup(infos);
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
			VariableStarSearchInternalFrame frame = new VariableStarSearchInternalFrame(desktop);
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
