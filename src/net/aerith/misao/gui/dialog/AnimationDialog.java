/*
 * @(#)AnimationDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * The <code>AnimationDialog</code> represents a dialog to set the
 * parameters for animation.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 November 26
 */

public class AnimationDialog extends Dialog {
	/**
	 * The text field to input the animation interval.
	 */
	protected JTextField text_interval;

	/**
	 * Constructs a <code>AnimationDialog</code>.
	 */
	public AnimationDialog ( ) {
		components = new Object[1];

		JPanel panel = new JPanel();
		panel.add(new JLabel("Interval"));
		text_interval = new JTextField("1000");
		text_interval.setColumns(7);
		panel.add(text_interval);
		panel.add(new JLabel("milliseconds"));
		components[0] = panel;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Animation Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("interval") != null)
			setInterval(((Integer)default_values.get("interval")).intValue());
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("interval", new Integer(getInterval()));
	}

	/**
	 * Gets the interval in milliseconds.
	 * @return the interval in milliseconds.
	 */
	public int getInterval ( ) {
		return Integer.parseInt(text_interval.getText());
	}

	/**
	 * Sets the interval in milliseconds.
	 * @param msec the interval in milliseconds.
	 */
	public void setInterval ( int msec ) {
		text_interval.setText("" + msec);
	}
}
