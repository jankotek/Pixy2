/*
 * @(#)PairingSettingDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.PixyDesktop;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.Dialog;

/**
 * The <code>PairingSettingDialog</code> represents a dialog to set up
 * parameters to operate pairing.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class PairingSettingDialog extends Dialog {
	/**
	 * The check box which represents to calculate the distortion 
	 * field.
	 */
	protected JCheckBox checkbox_distortion;

	/**
	 * The radio button to calculate limiting magnitude automatically.
	 */
	protected JRadioButton radio_limiting_mag_auto;

	/**
	 * The radio button to fix the limiting magnitude. 
	 */
	protected JRadioButton radio_limiting_mag_fixed;

	/**
	 * The text field to input the limiting magnitude.
	 */
	protected JTextField text_limit_mag;

	/**
	 * The text field to input the upper-limit magnitude.
	 */
	protected JTextField text_upper_limit_mag;

	/**
	 * Constructs a <code>PairingSettingDialog</code>.
	 */
	public PairingSettingDialog ( ) {
		components = new Object[2];

		JPanel panel_option = new JPanel();
		panel_option.setLayout(new BoxLayout(panel_option, BoxLayout.Y_AXIS));

		checkbox_distortion = new JCheckBox("Calculate distortion field.");
		panel_option.add(checkbox_distortion);
		checkbox_distortion.setSelected(false);

		panel_option.setBorder(new TitledBorder("Options"));
		components[0] = panel_option;

		ButtonGroup bg = new ButtonGroup();
		radio_limiting_mag_auto = new JRadioButton("Calculate automatically.", true);
		radio_limiting_mag_fixed = new JRadioButton("Fix as specified.");
		bg.add(radio_limiting_mag_auto);
		bg.add(radio_limiting_mag_fixed);

		text_limit_mag = new JTextField("20.0");
		text_limit_mag.setColumns(10);
		text_upper_limit_mag = new JTextField("20.0");
		text_upper_limit_mag.setColumns(10);

		JPanel panel_fix_limit_mag = new JPanel();
		panel_fix_limit_mag.add(new JLabel("    Limiting magnitude: Detects down to"));
		panel_fix_limit_mag.add(text_limit_mag);
		panel_fix_limit_mag.add(new JLabel("mag."));
		JPanel panel_fix_upper_limit_mag = new JPanel();
		panel_fix_upper_limit_mag.add(new JLabel("    Upper-limit magnitude: Records as fainter than"));
		panel_fix_upper_limit_mag.add(text_upper_limit_mag);
		panel_fix_upper_limit_mag.add(new JLabel("mag if not detected."));

		JPanel panel_radio_limiting_mag_auto = new JPanel();
		panel_radio_limiting_mag_auto.setLayout(new BorderLayout());
		panel_radio_limiting_mag_auto.add(radio_limiting_mag_auto, BorderLayout.WEST);

		JPanel panel_radio_limiting_mag_fixed = new JPanel();
		panel_radio_limiting_mag_fixed.setLayout(new BorderLayout());
		panel_radio_limiting_mag_fixed.add(radio_limiting_mag_fixed, BorderLayout.WEST);

		JPanel panel_fix_limit_mag2 = new JPanel();
		panel_fix_limit_mag2.setLayout(new BorderLayout());
		panel_fix_limit_mag2.add(panel_fix_limit_mag, BorderLayout.WEST);

		JPanel panel_fix_upper_limit_mag2 = new JPanel();
		panel_fix_upper_limit_mag2.setLayout(new BorderLayout());
		panel_fix_upper_limit_mag2.add(panel_fix_upper_limit_mag, BorderLayout.WEST);

		JPanel panel_limiting_mag = new JPanel();
		panel_limiting_mag.setLayout(new BoxLayout(panel_limiting_mag, BoxLayout.Y_AXIS));
		panel_limiting_mag.add(panel_radio_limiting_mag_auto);
		panel_limiting_mag.add(panel_radio_limiting_mag_fixed);
		panel_limiting_mag.add(panel_fix_limit_mag2);
		panel_limiting_mag.add(panel_fix_upper_limit_mag2);
		panel_limiting_mag.setBorder(new TitledBorder("Limiting magnitude"));
		components[1] = panel_limiting_mag;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Pairing Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("distortion") != null)
			setCalculateDistortionField(((Boolean)default_values.get("distortion")).booleanValue());

		if (default_values.get("calculate-limiting-mag") != null) {
			if (((Boolean)default_values.get("calculate-limiting-mag")).booleanValue()) {
				radio_limiting_mag_auto.setSelected(true);
				radio_limiting_mag_fixed.setSelected(false);
			} else {
				radio_limiting_mag_auto.setSelected(false);
				radio_limiting_mag_fixed.setSelected(true);
			}
		}
		if (default_values.get("limiting-mag") != null)
			text_limit_mag.setText((String)default_values.get("limiting-mag"));
		if (default_values.get("upper-limit-mag") != null)
			text_upper_limit_mag.setText((String)default_values.get("upper-limit-mag"));
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("distortion", new Boolean(calculatesDistortionField()));

		default_values.put("calculate-limiting-mag", new Boolean(radio_limiting_mag_auto.isSelected()));
		default_values.put("limiting-mag", text_limit_mag.getText());
		default_values.put("upper-limit-mag", text_upper_limit_mag.getText());
	}

	/**
	 * Returns true to calculate the distortion field.
	 * @return true to calculate the distortion field.
	 */
	public boolean calculatesDistortionField ( ) {
		return checkbox_distortion.isSelected();
	}

	/**
	 * Sets the flag to calculate the distortion field.
	 * @param flag true when to calculate the distortion field.
	 */
	public void setCalculateDistortionField ( boolean flag ) {
		checkbox_distortion.setSelected(flag);
	}

	/**
	 * Returns true when not to calculate the limiting magnitude 
	 * automatically.
	 * @return true when not to calculate the limiting magnitude 
	 * automatically.
	 */
	public boolean fixesLimitingMagnitude ( ) {
		return radio_limiting_mag_fixed.isSelected();
	}

	/**
	 * Gets the limiting magnitude.
	 * @return the limiting magnitude.
	 */
	public double getLimitingMag ( ) {
		return Format.doubleValueOf(text_limit_mag.getText());
	}

	/**
	 * Gets the upper-limit magnitude.
	 * @return the upper-limit magnitude.
	 */
	public double getUpperLimitMag ( ) {
		return Format.doubleValueOf(text_upper_limit_mag.getText());
	}
}
