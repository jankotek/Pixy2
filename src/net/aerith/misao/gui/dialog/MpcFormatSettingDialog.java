/*
 * @(#)MpcFormatSettingDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import net.aerith.misao.util.*;

/**
 * The <code>MpcFormatSettingDialog</code> represents a dialog to set
 * up parameters to make an astrometric report in the MPC format.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 October 5
 */

public class MpcFormatSettingDialog extends Dialog {
	/**
	 * The designation.
	 */
	protected Designation designation;

	/**
	 * The panel to show the designation.
	 */
	protected JPanel panel_designation;

	/**
	 * The text field to show the designation.
	 */
	protected JTextField text_designation;

	/**
	 * The button to edit the designation.
	 */
	protected JButton button_edit;

	/**
	 * The radio button for the photographic observation.
	 */
	protected JRadioButton radio_photographic;

	/**
	 * The radio button for the CCD observation.
	 */
	protected JRadioButton radio_ccd;

	/**
	 * The combo box to select the magnitude accuracy.
	 */
	protected JComboBox combo_accuracy;

	/**
	 * The radio button for the B-band magnitude.
	 */
	protected JRadioButton radio_b;

	/**
	 * The radio button for the V-band magnitude.
	 */
	protected JRadioButton radio_v;

	/**
	 * The radio button for the R-band magnitude.
	 */
	protected JRadioButton radio_r;

	/**
	 * The radio button for the I-band magnitude.
	 */
	protected JRadioButton radio_i;

	/**
	 * The radio button for the total magnitude.
	 */
	protected JRadioButton radio_total;

	/**
	 * The radio button for the nuclear magnitude.
	 */
	protected JRadioButton radio_nuclear;

	/**
	 * The text field to input the observatory code.
	 */
	protected JTextField text_code;

	/**
	 * Constructs a <code>MpcFormatSettingDialog</code>.
	 */
	public MpcFormatSettingDialog ( ) {
		components = new Object[4];

		panel_designation = new JPanel();
		text_designation = new JTextField("");
		text_designation.setColumns(30);
		text_designation.setEditable(false);
		button_edit = new JButton("Edit");
		button_edit.addActionListener(new EditDesignationListener());
		panel_designation.add(new JLabel("Designation"));
		panel_designation.add(text_designation);
		panel_designation.add(button_edit);

		ButtonGroup bg_type = new ButtonGroup();
		radio_photographic = new JRadioButton("Photographic", true);
		radio_ccd = new JRadioButton("CCD");
		bg_type.add(radio_photographic);
		bg_type.add(radio_ccd);

		JPanel panel_type = new JPanel();
		panel_type.add(radio_photographic);
		panel_type.add(radio_ccd);
		panel_type.setBorder(new TitledBorder("Observation Type"));

		JPanel panel_mag_accuracy = new JPanel();
		String[] accuracies = { "0.1", "0.01" };
		combo_accuracy = new JComboBox(accuracies);
		combo_accuracy.setSelectedItem("0.1");
		panel_mag_accuracy.add(new JLabel("Accuracy"));
		panel_mag_accuracy.add(combo_accuracy);

		ButtonGroup bg_band = new ButtonGroup();
		radio_b = new JRadioButton("B-band", true);
		radio_v = new JRadioButton("V-band");
		radio_r = new JRadioButton("R-band");
		radio_i = new JRadioButton("I-band");
		radio_total = new JRadioButton("Total");
		radio_nuclear = new JRadioButton("Nuclear");
		bg_band.add(radio_b);
		bg_band.add(radio_v);
		bg_band.add(radio_r);
		bg_band.add(radio_i);
		bg_band.add(radio_total);
		bg_band.add(radio_nuclear);

		JPanel panel_mag_band1 = new JPanel();
		panel_mag_band1.add(radio_b);
		panel_mag_band1.add(radio_v);
		panel_mag_band1.add(radio_r);
		panel_mag_band1.add(radio_i);

		JPanel panel_mag_band2 = new JPanel();
		panel_mag_band2.add(radio_total);
		panel_mag_band2.add(radio_nuclear);

		JPanel panel_mag = new JPanel();
		panel_mag.setLayout(new BoxLayout(panel_mag, BoxLayout.Y_AXIS));
		panel_mag.add(panel_mag_accuracy);
		panel_mag.add(panel_mag_band1);
		panel_mag.add(panel_mag_band2);
		panel_mag.setBorder(new TitledBorder("Magnitude"));

		JPanel panel_code = new JPanel();
		text_code = new JTextField("XXX");
		text_code.setColumns(10);
		panel_code.add(new JLabel("Observatory Code"));
		panel_code.add(text_code);

		components[0] = panel_designation;
		components[1] = panel_type;
		components[2] = panel_mag;
		components[3] = panel_code;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "MPC Format Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("observation-type") != null) {
			int type = ((Integer)default_values.get("observation-type")).intValue();
			radio_photographic.setSelected(type == MpcFormatRecord.TYPE_PHOTOGRAPHIC);
			radio_ccd.setSelected(type == MpcFormatRecord.TYPE_CCD);
		}
		if (default_values.get("magnitude-accuracy") != null)
			combo_accuracy.setSelectedIndex(((Integer)default_values.get("magnitude-accuracy")).intValue());
		if (default_values.get("magnitude-band") != null) {
			int band = ((Integer)default_values.get("magnitude-band")).intValue();
			radio_b.setSelected(band == MpcFormatRecord.BAND_B);
			radio_v.setSelected(band == MpcFormatRecord.BAND_V);
			radio_r.setSelected(band == MpcFormatRecord.BAND_R);
			radio_i.setSelected(band == MpcFormatRecord.BAND_I);
			radio_total.setSelected(band == MpcFormatRecord.BAND_TOTAL);
			radio_nuclear.setSelected(band == MpcFormatRecord.BAND_NUCLEAR);
		}
		if (default_values.get("observatory-code") != null)
			text_code.setText((String)default_values.get("observatory-code"));
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("observation-type", new Integer(getObservationType()));
		default_values.put("magnitude-accuracy", new Integer(combo_accuracy.getSelectedIndex()));
		default_values.put("magnitude-band", new Integer(getMagnitudeBand()));
		default_values.put("observatory-code", text_code.getText());
	}

	/**
	 * Gets the designation.
	 * @return the designation.
	 */
	public Designation getDesignation ( ) {
		return designation;
	}

	/**
	 * Gets the observation type.
	 * @return the observation type.
	 */
	public int getObservationType ( ) {
		if (radio_ccd.isSelected())
			return MpcFormatRecord.TYPE_CCD;
		return MpcFormatRecord.TYPE_PHOTOGRAPHIC;
	}

	/**
	 * Gets the magnitude accuracy number.
	 * @return the magnitude accuracy number.
	 */
	public int getMagnitudeAccuracy ( ) {
		if (combo_accuracy.getSelectedItem().equals("0.1"))
			return MpcFormatRecord.ACCURACY_10TH;
		return MpcFormatRecord.ACCURACY_100TH;
	}

	/**
	 * Gets the magnitude band number.
	 * @return the magnitude band number.
	 */
	public int getMagnitudeBand ( ) {
		if (radio_v.isSelected())
			return MpcFormatRecord.BAND_V;
		else if (radio_r.isSelected())
			return MpcFormatRecord.BAND_R;
		else if (radio_i.isSelected())
			return MpcFormatRecord.BAND_I;
		else if (radio_total.isSelected())
			return MpcFormatRecord.BAND_TOTAL;
		else if (radio_nuclear.isSelected())
			return MpcFormatRecord.BAND_NUCLEAR;
		return MpcFormatRecord.BAND_B;
	}

	/**
	 * Gets the observatory code.
	 * @return the observatory code.
	 */
	public String getObservatoryCode ( ) {
		return text_code.getText();
	}

	/**
	 * The <code>EditDesignationListener</code> is a listener class to
	 * edit the designation.
	 */
	protected class EditDesignationListener implements ActionListener {
		/**
		 * Invoked when one of the check box is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			DesignationDialog dialog = new DesignationDialog();
			int answer = dialog.show(panel_designation);

			if (answer == 0) {
				designation = dialog.getDesignation();
				if (designation == null)
					text_designation.setText("");
				else
					text_designation.setText(designation.getOutputString());
			}
		}
	}
}
