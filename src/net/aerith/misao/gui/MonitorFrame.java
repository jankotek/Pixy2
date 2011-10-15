/*
 * @(#)MonitorFrame.java
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
 * The <code>MonitorFrame</code> represents a frame with a 
 * <code>JTextArea</code>. It has a funciton to add messages to the
 * text area.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 January 18
 */

public class MonitorFrame extends JInternalFrame implements Monitor {
	/**
	 * The text area.
	 */
	protected MonitorArea text_area;

	/**
	 * Constructs a <code>MonitorFrame</code>.
	 */
	public MonitorFrame ( ) {
		super();

		text_area = new MonitorArea();
		text_area.setEditable(false);
		getContentPane().add(new JScrollPane(text_area));
	}

	/**
	 * Stops logging.
	 */
	public void stop ( ) {
		text_area.stop();
	}

	/**
	 * Restarts logging.
	 */
	public void restart ( ) {
		text_area.restart();
	}

	/**
	 * Shows the specified message.
	 * @param string a one line message to show.
	 */
	public void addMessage ( String string ) {
		text_area.addMessage(string);
		if (text_area.isStopped() == false)
			scrollToBottom();
	}

	/**
	 * Shows the specified messages.
	 * @param strings a set of messages to show.
	 */
	public void addMessages ( String[] strings ) {
		text_area.addMessages(strings);
		if (text_area.isStopped() == false)
			scrollToBottom();
	}

	/**
	 * Shows a separator.
	 */
	public void addSeparator ( ) {
		text_area.addSeparator();
		if (text_area.isStopped() == false)
			scrollToBottom();
	}

	/**
	 * Scrolls to the bottom.
	 */
	public void scrollToBottom ( ) {
		Rectangle rect = text_area.getVisibleRect();
		rect.y = text_area.getHeight() - 2;
		text_area.scrollRectToVisible(rect);
	}
}
