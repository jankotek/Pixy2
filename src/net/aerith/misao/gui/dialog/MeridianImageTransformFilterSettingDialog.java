/*
 * @(#)MeridianImageTransformFilterSettingDialog.java
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
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;

/**
 * The <code>MeridianImageTransformFilterSettingDialog</code> 
 * represents a dialog to set the parameters for the image processing
 * filter to transform a meridian image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 17
 */

public class MeridianImageTransformFilterSettingDialog extends Dialog {
	/**
	 * The text field to input the declination of the center.
	 */
	protected JTextField text_decl;

	/**
	 * The text field to input the pixel size in arcsec.
	 */
	protected JTextField text_pixel_size;

	/**
	 * The text field to input the R.A. interval in second.
	 */
	protected JTextField text_ra_interval;

	/**
	 * Constructs a <code>MeridianImageTransformFilterSettingDialog</code>.
	 */
	public MeridianImageTransformFilterSettingDialog ( ) {
		components = new Object[3];

		JPanel panel = new JPanel();
		text_decl = new JTextField("0.0");
		text_decl.setColumns(10);
		panel.add(new JLabel("Decl. of the center: "));
		panel.add(text_decl);
		components[0] = panel;

		panel = new JPanel();
		text_pixel_size = new JTextField("1.0");
		text_pixel_size.setColumns(10);
		panel.add(new JLabel("Pixel size in arcsec: "));
		panel.add(text_pixel_size);
		components[1] = panel;

		panel = new JPanel();
		text_ra_interval = new JTextField("1.0");
		text_ra_interval.setColumns(10);
		panel.add(new JLabel("R.A. interval in second: "));
		panel.add(text_ra_interval);
		components[2] = panel;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Meridian Image Transform Filter Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("center-decl") != null)
			text_decl.setText((String)default_values.get("center-decl"));
		if (default_values.get("pixel-size") != null)
			text_pixel_size.setText((String)default_values.get("pixel-size"));
		if (default_values.get("ra-interval") != null)
			text_ra_interval.setText((String)default_values.get("ra-interval"));
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("center-decl", text_decl.getText());
		default_values.put("pixel-size", text_pixel_size.getText());
		default_values.put("ra-interval", text_ra_interval.getText());
	}

	/**
	 * Gets the declination of the center.
	 * @return the declination of the center.
	 */
	public double getDeclAtCenter ( ) {
		return Format.doubleValueOf(text_decl.getText());
	}

	/**
	 * Gets the pixel size in arcsec.
	 * @return the pixel size in arcsec.
	 */
	public double getPixelSizeInArcsec ( ) {
		return Format.doubleValueOf(text_pixel_size.getText());
	}

	/**
	 * Gets the R.A. interval in second.
	 * @return the R.A. interval in second.
	 */
	public double getRAIntervalInSecond ( ) {
		return Format.doubleValueOf(text_ra_interval.getText());
	}
}
