/*
 * @(#)ImageConfigurationDialog.java
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
import net.aerith.misao.pixy.Resource;

/**
 * The <code>ImageConfigurationDialog</code> represents a dialog to 
 * configure the image configuration.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 November 26
 */

public class ImageConfigurationDialog extends Dialog {
	/**
	 * The radio button for standard FITS order.
	 */
	protected JRadioButton radio_fits_standard;

	/**
	 * The radio button for Japanese FITS order.
	 */
	protected JRadioButton radio_fits_japanese;

	/**
	 * Constructs an <code>ImageConfigurationDialog</code>.
	 */
	public ImageConfigurationDialog ( ) {
		components = new Object[1];

		ButtonGroup bg_fits = new ButtonGroup();
		radio_fits_standard = new JRadioButton("", Resource.getDefaultFitsOrder().equals("standard"));
		radio_fits_japanese = new JRadioButton("", Resource.getDefaultFitsOrder().equals("japanese"));
		bg_fits.add(radio_fits_standard);
		bg_fits.add(radio_fits_japanese);

		JPanel panel_fits_standard = new JPanel();
		panel_fits_standard.add(radio_fits_standard);
		panel_fits_standard.add(new JLabel("Standard"));

		JPanel panel_fits_japanese = new JPanel();
		panel_fits_japanese.add(radio_fits_japanese);
		panel_fits_japanese.add(new JLabel("Japanese"));

		JPanel panel_fits = new JPanel();
		panel_fits.add(panel_fits_standard);
		panel_fits.add(panel_fits_japanese);
		panel_fits.setBorder(new TitledBorder("Default FITS data order"));
		components[0] = panel_fits;

//		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Image Configuration";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
	}

	/**
	 * Gets the default FITS data order.
	 * @return the default FITS data order.
	 */
	public String getFitsOrder ( ) {
		return (radio_fits_standard.isSelected() ? "standard" : "japanese");
	}
}
