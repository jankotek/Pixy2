/*
 * @(#)ValidAreaSettingDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;

/**
 * The <code>ValidAreaSettingDialog</code> represents a dialog to 
 * specify the valid area on the image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class ValidAreaSettingDialog extends Dialog {
	/**
	 * The text field to input the pixels from edge to exclude.
	 */
	protected JTextField text_pixels_from_edge;

	/**
	 * Constructs a <code>ValidAreaSettingDialog</code>.
	 */
	public ValidAreaSettingDialog ( ) {
		components = new Object[1];

		text_pixels_from_edge = new JTextField("10");
		text_pixels_from_edge.setColumns(6);
		JPanel panel = new JPanel();
		panel.add(new JLabel("Exclude"));
		panel.add(text_pixels_from_edge);
		panel.add(new JLabel("pixels from edge."));
		components[0] = panel;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Valid Area Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("pixels-from-edge") != null)
			text_pixels_from_edge.setText((String)default_values.get("pixels-from-edge"));
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("pixels-from-edge", text_pixels_from_edge.getText());
	}

	/**
	 * Gets the pixels from edge to exclude.
	 * @return the pixels from edge to exclude.
	 */
	public int getPixelsFromEdge ( ) {
		return Integer.parseInt(text_pixels_from_edge.getText());
	}
}
