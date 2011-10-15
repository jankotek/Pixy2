/*
 * @(#)VsnetReportSettingDialog.java
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
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.*;

/**
 * The <code>VsnetReportSettingDialog</code> represents a dialog to 
 * set up parameters to make a report of magnitude to VSNET or VSOLJ.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 November 24
 */

public class VsnetReportSettingDialog extends Dialog {
	/**
	 * The text field to input the star name.
	 */
	protected JTextField text_name = null;

	/**
	 * The combo box to select a society to report to.
	 */
	protected JComboBox combo_society;

	/**
	 * The radio button for the original format.
	 */
	protected JRadioButton radio_original;

	/**
	 * The radio button for the extended format.
	 */
	protected JRadioButton radio_extended;

	/**
	 * The radio button for the extended format, with the instruments.
	 */
	protected JRadioButton radio_extended_instruments;

	/**
	 * The combo box to select the magnitude accuracy.
	 */
	protected JComboBox combo_accuracy;

	/**
	 * The text field to input the observer's code.
	 */
	protected JTextField text_code;

	/**
	 * Constructs a <code>VsnetReportSettingDialog</code> for setting
	 * to report magnitude of several stars to VSNET/VSOLJ.
	 */
	public VsnetReportSettingDialog ( ) {
		this(null);
	}

	/**
	 * Constructs a <code>VsnetReportSettingDialog</code> for setting
	 * to report magnitude of one star to VSNET/VSOLJ.
	 * @param star_name the star name.
	 */
	public VsnetReportSettingDialog ( String star_name ) {
		if (star_name == null)
			components = new Object[3];
		else
			components = new Object[4];

		int component_count = 0;

		String[] societies = { "VSNET (Variable Star Network)", "VSOLJ (Variable Star Observers League in Japan)" };
		combo_society = new JComboBox(societies);

		ButtonGroup bg = new ButtonGroup();
		radio_original = new JRadioButton("Original format.", true);
		radio_extended = new JRadioButton("Extended format.");
		radio_extended_instruments = new JRadioButton("Extended format with instruments.");
		bg.add(radio_original);
		bg.add(radio_extended);
		bg.add(radio_extended_instruments);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(combo_society);
		panel.add(radio_original);
		panel.add(radio_extended);
		panel.add(radio_extended_instruments);

		panel.setBorder(new TitledBorder("Format"));
		components[component_count++] = panel;

		if (star_name != null) {
			panel = new JPanel();
			text_name = new JTextField();
			text_name.setColumns(20);
			text_name.setText(star_name);
			panel.add(text_name);
			panel.setBorder(new TitledBorder("Star name"));
			components[component_count++] = panel;
		}

		String[] accuracies = { "0.1", "0.01" };
		combo_accuracy = new JComboBox(accuracies);
		combo_accuracy.setSelectedItem("0.01");

		panel = new JPanel();
		panel.add(combo_accuracy);
		panel.add(new JLabel("mag"));
		panel.setBorder(new TitledBorder("Magnitude accuracy"));
		components[component_count++] = panel;

		panel = new JPanel();
		text_code = new JTextField();
		text_code.setColumns(10);
		panel.add(text_code);
		panel.setBorder(new TitledBorder("Observer's code"));
		components[component_count++] = panel;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Report to VSNET/VSOLJ Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("society") != null) {
			if (((Boolean)default_values.get("society")).booleanValue())
				selectVsnet();
			else
				selectVsolj();
		}
		if (default_values.get("format") != null)
			setFormat(((Integer)default_values.get("format")).intValue());
		if (default_values.get("magnitude-accuracy") != null)
			combo_accuracy.setSelectedIndex(((Integer)default_values.get("magnitude-accuracy")).intValue());
		if (default_values.get("observer") != null)
			setObserverCode((String)default_values.get("observer"));
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("society", new Boolean(isVsnetSelected()));
		default_values.put("format", new Integer(getFormat()));
		default_values.put("magnitude-accuracy", new Integer(combo_accuracy.getSelectedIndex()));
		default_values.put("observer", getObserverCode());
	}

	/**
	 * Selects the VSNET.
	 */
	public void selectVsnet ( ) {
		combo_society.setSelectedItem("VSNET (Variable Star Network)");
	}

	/**
	 * Selects the VSOLJ.
	 */
	public void selectVsolj ( ) {
		combo_society.setSelectedItem("VSOLJ (Variable Star Observers League in Japan)");
	}

	/**
	 * Selects the VSNET and disables the combo box.
	 */
	public void fixToVsnet ( ) {
		combo_society.setSelectedItem("VSNET (Variable Star Network)");
		combo_society.setEnabled(false);
	}

	/**
	 * Selects the VSOLJ and disables the combo box.
	 */
	public void fixToVsolj ( ) {
		combo_society.setSelectedItem("VSOLJ (Variable Star Observers League in Japan)");
		combo_society.setEnabled(false);
	}

	/**
	 * True if the VSNET is selected as the society.
	 * @return true if the VSNET is selected as the society.
	 */
	public boolean isVsnetSelected ( ) {
		if (((String)combo_society.getSelectedItem()).equals("VSNET (Variable Star Network)"))
			return true;
		return false;
	}

	/**
	 * Gets the format number.
	 * @return the format number.
	 */
	public int getFormat ( ) {
		if (radio_extended.isSelected())
			return VsnetRecord.FORMAT_EXTENDED;
		if (radio_extended_instruments.isSelected())
			return VsnetRecord.FORMAT_EXTENDED_INSTRUMENTS;
		return VsnetRecord.FORMAT_ORIGINAL;
	}

	/**
	 * Sets the format number.
	 * @param format the format number.
	 */
	public void setFormat ( int format ) {
		radio_extended.setSelected(format == VsnetRecord.FORMAT_EXTENDED);
		radio_extended_instruments.setSelected(format == VsnetRecord.FORMAT_EXTENDED_INSTRUMENTS);

		radio_original.setSelected((format != VsnetRecord.FORMAT_EXTENDED)  &&  (format != VsnetRecord.FORMAT_EXTENDED_INSTRUMENTS));
	}

	/**
	 * Gets the star name.
	 * @return the star name.
	 */
	public String getStarName ( ) {
		if (text_name != null)
			return text_name.getText().replace(' ', '_');

		return null;
	}

	/**
	 * Gets the magnitude accuracy number.
	 * @return the magnitude accuracy number.
	 */
	public int getAccuracy ( ) {
		if (combo_accuracy.getSelectedItem().equals("0.1"))
			return VsnetRecord.ACCURACY_10TH;
		return VsnetRecord.ACCURACY_100TH;
	}

	/**
	 * Sets the magnitude accuracy number.
	 * @param accuracy the magnitude accuracy number.
	 */
	public void setAccuracy ( int accuracy ) {
		if (accuracy == VsnetRecord.ACCURACY_10TH)
			combo_accuracy.setSelectedItem("0.1");
		combo_accuracy.setSelectedItem("0.01");
	}

	/**
	 * Gets the observer's code.
	 * @return the observer's code.
	 */
	public String getObserverCode ( ) {
		return text_code.getText();
	}

	/**
	 * Sets the observer's code.
	 * @param code the observer's code.
	 */
	public void setObserverCode ( String code ) {
		text_code.setText(code);
	}
}
