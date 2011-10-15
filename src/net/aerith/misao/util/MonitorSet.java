/*
 * @(#)MonitorSet.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.Vector;

/**
 * The <code>MonitorSet</code> represents a set of <code>Monitor</code>.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 January 9
 */

public class MonitorSet implements Monitor {
	/**
	 * The list of monitors.
	 */
	protected Vector monitors;

	/**
	 * Constructs a <code>MonitorSet</code>.
	 */
	public MonitorSet ( ) {
		monitors = new Vector();
	}

	/**
	 * Sets a monitor.
	 * @param monitor the monitor to set.
	 */
	public void setMonitor ( Monitor monitor ) {
		monitors = new Vector();
		monitors.addElement(monitor);
	}

	/**
	 * Adds a monitor.
	 * @param monitor the monitor to add.
	 */
	public void addMonitor ( Monitor monitor ) {
		monitors.addElement(monitor);
	}

	/**
	 * Adds the specified message too all monitors.
	 * @param string a one line message to show.
	 */
	public void addMessage ( String string ) {
		for (int i = 0 ; i < monitors.size() ; i++) {
			Monitor monitor = (Monitor)monitors.elementAt(i);
			monitor.addMessage(string);
		}
	}

	/**
	 * Adds the specified messages to all monitors.
	 * @param strings a set of messages to show.
	 */
	public void addMessages ( String[] strings ) {
		for (int i = 0 ; i < monitors.size() ; i++) {
			Monitor monitor = (Monitor)monitors.elementAt(i);
			monitor.addMessages(strings);
		}
	}

	/**
	 * Adds a separator.
	 */
	public void addSeparator ( ) {
		for (int i = 0 ; i < monitors.size() ; i++) {
			Monitor monitor = (Monitor)monitors.elementAt(i);
			monitor.addSeparator();
		}
	}
}
