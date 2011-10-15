/*
 * @(#)NewStarSearchSettingDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.NewStarSearch;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Vector;
import net.aerith.misao.util.Format;
import net.aerith.misao.gui.dialog.Dialog;

/**
 * The <code>NewStarSearchSettingDialog</code> represents a dialog to 
 * set the parameters to search new stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 March 13
 */

public class NewStarSearchSettingDialog extends Dialog {
	/**
	 * The text field to input the limiting magnitude.
	 */
	protected JTextField text_limiting_mag;

	/**
	 * The text field to input the amplitude.
	 */
	protected JTextField text_amplitude;

	/**
	 * The text field to input the pixels from edge to ignore.
	 */
	protected JTextField text_ignore_edge;

	/**
	 * Constructs a <code>NewStarSearchSettingDialog</code>.
	 */
	public NewStarSearchSettingDialog ( ) {
		components = new Object[3];

		JPanel panel = new JPanel();
		text_limiting_mag = new JTextField("10.0");
		text_limiting_mag.setColumns(6);
		panel.add(new JLabel("Brighter than"));
		panel.add(text_limiting_mag);
		panel.add(new JLabel("mag"));
		components[0] = panel;

		panel = new JPanel();
		text_amplitude = new JTextField("5.0");
		text_amplitude.setColumns(6);
		panel.add(new JLabel("With amplitude larger than"));
		panel.add(text_amplitude);
		panel.add(new JLabel("mag"));
		components[1] = panel;

		panel = new JPanel();
		text_ignore_edge = new JTextField("10");
		text_ignore_edge.setColumns(6);
		panel.add(new JLabel("Ignore data within"));
		panel.add(text_ignore_edge);
		panel.add(new JLabel("pixels from edge."));
		components[2] = panel;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "New Star Search Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("limiting-mag") != null)
			text_limiting_mag.setText((String)default_values.get("limiting-mag"));
		if (default_values.get("amplitude") != null)
			text_amplitude.setText((String)default_values.get("amplitude"));
		if (default_values.get("ignore-edge") != null)
			text_ignore_edge.setText((String)default_values.get("ignore-edge"));
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("limiting-mag", text_limiting_mag.getText());
		default_values.put("amplitude", text_amplitude.getText());
		default_values.put("ignore-edge", text_ignore_edge.getText());
	}

	/**
	 * Gets the limiting magnitude.
	 * @return the limiting magnitude.
	 */
	public double getLimitingMag ( ) {
		return Format.doubleValueOf(text_limiting_mag.getText());
	}

	/**
	 * Gets the amplitude.
	 * @return the amplitude.
	 */
	public double getAmplitude ( ) {
		return Format.doubleValueOf(text_amplitude.getText());
	}

	/**
	 * Gets the pixels from edge to ignore.
	 * @return the pixels from edge to ignore.
	 */
	public int getIgnoreEdge ( ) {
		return Integer.parseInt(text_ignore_edge.getText());
	}
}
