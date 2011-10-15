/*
 * @(#)MonitorArea.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.aerith.misao.util.*;

/**
 * The <code>MonitorArea</code> represents a text area where the user
 * can stop and restart logging by popup menu.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 January 18
 */

public class MonitorArea extends JTextArea implements Monitor {
	/**
	 * True when not to show messages.
	 */
	protected boolean stopped = false;

	/**
	 * The popup menu.
	 */
	protected JPopupMenu popup;

	/**
	 * The menu item to stop logging.
	 */
	protected JMenuItem menu_stop;

	/**
	 * The menu item to restart logging.
	 */
	protected JMenuItem menu_restart;

	/**
	 * Constructs a <code>MonitorArea</code>.
	 */
	public MonitorArea ( ) {
		super();

		popup = new JPopupMenu();
		menu_stop = new JMenuItem("Stop logging");
		menu_stop.addActionListener(new StopListener());
		menu_restart = new JMenuItem("Restart logging");
		menu_restart.addActionListener(new RestartListener());
		popup.add(menu_stop);
		popup.add(menu_restart);
		menu_restart.setEnabled(false);

		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}

	/**
	 * Invoked when mouse event occurs. It is to show a popup menu.
	 * @param e contains the click position.
	 */
	protected void processMouseEvent ( MouseEvent e ) {
		if (e.isPopupTrigger()) {
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
		super.processMouseEvent(e);
	}

	/**
	 * Returns true when the logging is stopped.
	 * @return true when the logging is stopped.
	 */
	public boolean isStopped ( ) {
		return stopped;
	}

	/**
	 * Stops logging.
	 */
	public void stop ( ) {
		menu_stop.setEnabled(false);
		menu_restart.setEnabled(true);

		stopped = true;
	}

	/**
	 * Restarts logging.
	 */
	public void restart ( ) {
		menu_stop.setEnabled(true);
		menu_restart.setEnabled(false);

		stopped = false;
	}

	/**
	 * Shows the specified message.
	 * @param string a one line message to show.
	 */
	public void addMessage ( String string ) {
		if (isStopped() == false)
			append(string + System.getProperty("line.separator"));
	}

	/**
	 * Shows the specified messages.
	 * @param strings a set of messages to show.
	 */
	public void addMessages ( String[] strings ) {
		if (isStopped() == false) {
			for (int i = 0 ; i < strings.length ; i++)
				append(strings[i] + System.getProperty("line.separator"));
		}
	}

	/**
	 * Shows a separator.
	 */
	public void addSeparator ( ) {
		if (isStopped() == false)
			append(System.getProperty("line.separator"));
	}

	/**
	 * The <code>StopListener</code> is a listener class of menu 
	 * selection to stop logging.
	 */
	protected class StopListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			stop();
		}
	}

	/**
	 * The <code>RestartListener</code> is a listener class of menu 
	 * selection to restart logging.
	 */
	protected class RestartListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			restart();
		}
	}
}
