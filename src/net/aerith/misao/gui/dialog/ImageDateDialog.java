/*
 * @(#)ImageDateDialog.java
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
 * The <code>ImageDateDialog</code> represents a dialog to input the
 * image exposure date and time.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class ImageDateDialog extends Dialog {
	/**
	 * The panel to input date and time;
	 */
	protected DateAndTimePanel date_panel;

	/**
	 * The combo box to select the accuracy of date and time.
	 */
	protected JComboBox combo_date_accuracy;

	/**
	 * The text field to input the exposure time.
	 */
	protected JTextField text_exposure;

	/**
	 * The combo box to select the unit of the exposure time.
	 */
	protected JComboBox combo_exposure_unit;

	/**
	 * Constructs a <code>ImageDateDialog</code>.
	 */
	public ImageDateDialog ( ) {
		components = new Object[2];

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		date_panel = new DateAndTimePanel();
		panel.add(date_panel);

		JPanel panel_accuracy = new JPanel();
		panel_accuracy.add(new JLabel("Accuracy "));
		DateAccuracyComboBoxModel model = new DateAccuracyComboBoxModel();
		combo_date_accuracy = new JComboBox(model);
		combo_date_accuracy.setSelectedIndex(0);
		panel_accuracy.add(combo_date_accuracy);
		panel.add(panel_accuracy);
		panel.setBorder(new TitledBorder("Exposure Start Date and time"));
		components[0] = panel;

		panel = new JPanel();
		text_exposure = new JTextField();
		text_exposure.setColumns(10);
		panel.add(text_exposure);
		String[] units = { "second", "minute", "hour" };
		combo_exposure_unit = new JComboBox(units);
		combo_exposure_unit.setSelectedIndex(0);
		panel.add(combo_exposure_unit);
		panel.setBorder(new TitledBorder("Exposure"));
		components[1] = panel;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Image Exposure Date and Time";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("date-accuracy") != null)
			setAccuracy(((Integer)default_values.get("date-accuracy")).intValue());
		if (default_values.get("exposure") != null)
			text_exposure.setText((String)default_values.get("exposure"));
		if (default_values.get("exposure-unit") != null)
			combo_exposure_unit.setSelectedItem((String)default_values.get("exposure-unit"));
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		date_panel.saveDefaultValues();

		default_values.put("date-accuracy", new Integer(getAccuracy()));
		default_values.put("exposure", text_exposure.getText());
		default_values.put("exposure-unit", combo_exposure_unit.getSelectedItem());
	}

	/**
	 * Gets the date and time.
	 * @return the date and time.
	 */
	public JulianDay getDay ( ) {
		return date_panel.getDay();
	}

	/**
	 * Sets the date and time.
	 * @param jd the date and time as Julian Day.
	 */
	public void setDay ( JulianDay jd ) {
		date_panel.setDay(jd);
	}

	/**
	 * Gets the accuracy of the date and time.
	 * @return the format number of the accuracy of the date and time.
	 */
	public int getAccuracy ( ) {
		if (((String)combo_date_accuracy.getSelectedItem()).equals("0.00001 day")) {
			return JulianDay.FORMAT_DECIMALDAY_100000TH;
		} else if (((String)combo_date_accuracy.getSelectedItem()).equals("0.0001 day")) {
			return JulianDay.FORMAT_DECIMALDAY_10000TH;
		} else if (((String)combo_date_accuracy.getSelectedItem()).equals("0.001 day")) {
			return JulianDay.FORMAT_DECIMALDAY_1000TH;
		} else if (((String)combo_date_accuracy.getSelectedItem()).equals("0.01 day")) {
			return JulianDay.FORMAT_DECIMALDAY_100TH;
		} else if (((String)combo_date_accuracy.getSelectedItem()).equals("0.1 day")) {
			return JulianDay.FORMAT_DECIMALDAY_10TH;
		} else if (((String)combo_date_accuracy.getSelectedItem()).equals("1 day")) {
			return JulianDay.FORMAT_DECIMALDAY_1TH;
		} else if (((String)combo_date_accuracy.getSelectedItem()).equals("second")) {
			return JulianDay.FORMAT_TO_SECOND;
		} else if (((String)combo_date_accuracy.getSelectedItem()).equals("minute")) {
			return JulianDay.FORMAT_TO_MINUTE;
		} else if (((String)combo_date_accuracy.getSelectedItem()).equals("hour")) {
			return JulianDay.FORMAT_TO_HOUR;
		} else if (((String)combo_date_accuracy.getSelectedItem()).equals("day")) {
			return JulianDay.FORMAT_TO_DAY;
		}
		return JulianDay.FORMAT_DEFAULT;
	}

	/**
	 * Sets the accuracy of the date and time.
	 * @param date_accuracy the format number of the accuracy of the 
	 * date and time.
	 */
	public void setAccuracy ( int date_accuracy ) {
		String item = "";
		switch (date_accuracy) {
			case JulianDay.FORMAT_DECIMALDAY_100000TH:
				item = "0.00001 day";
				break;
			case JulianDay.FORMAT_DECIMALDAY_10000TH:
				item = "0.0001 day";
				break;
			case JulianDay.FORMAT_DECIMALDAY_1000TH:
				item = "0.001 day";
				break;
			case JulianDay.FORMAT_DECIMALDAY_100TH:
				item = "0.01 day";
				break;
			case JulianDay.FORMAT_DECIMALDAY_10TH:
				item = "0.1 day";
				break;
			case JulianDay.FORMAT_DECIMALDAY_1TH:
				item = "1 day";
				break;
			case JulianDay.FORMAT_TO_SECOND:
				item = "second";
				break;
			case JulianDay.FORMAT_TO_MINUTE:
				item = "minute";
				break;
			case JulianDay.FORMAT_TO_HOUR:
				item = "hour";
				break;
			case JulianDay.FORMAT_TO_DAY:
				item = "day";
				break;
		}

		DateAccuracyComboBoxModel model = (DateAccuracyComboBoxModel)combo_date_accuracy.getModel();
		for (int i = 0 ; i < model.getSize() ; i++) {
			if (item.equals((String)model.get(i))) {
				combo_date_accuracy.setSelectedIndex(i);
				return;
			}
		}
	}

	/**
	 * Gets the exposure time.
	 * @return the exposure time.
	 */
	public double getExposure ( ) {
		return Double.parseDouble(text_exposure.getText());
	}

	/**
	 * Gets the exposure unit.
	 * @return the exposure unit.
	 */
	public String getExposureUnit ( ) {
		return (String)combo_exposure_unit.getSelectedItem();
	}

	/**
	 * Sets the exposure time and unit.
	 * @param exposure the exposure time.
	 * @param unit     the unit of the exposure time.
	 */
	public void setExposure ( double exposure, String unit ) {
		text_exposure.setText("" + exposure);

		for (int i = 0 ; i < combo_exposure_unit.getItemCount() ; i++) {
			if (unit.equals((String)combo_exposure_unit.getItemAt(i))) {
				combo_exposure_unit.setSelectedIndex(i);
				return;
			}
		}
	}

	/**
	 * The <code>DateAccuracyComboBoxModel</code> is a model of the
	 * combo box to select date accuracy.
	 */
	protected class DateAccuracyComboBoxModel extends DefaultListModel implements ComboBoxModel {
		/**
		 * The selected item.
		 */
		private Object selected_item;

		/**
		 * Constructs a <code>DateAccuracyComboBoxModel</code>. At the
		 * beginning, the VSNET is selected as the society.
		 */
		public DateAccuracyComboBoxModel ( ) {
			String[] accuracies = { "second", "minute", "hour", "day", "0.00001 day", "0.0001 day", "0.001 day", "0.01 day", "0.1 day", "1 day" };
			for (int i = 0 ; i < accuracies.length ; i++)
				addElement(accuracies[i]);
		}

		/**
		 * Gets the selected item.
		 * @return the selected item.
		 */
		public Object getSelectedItem ( ) {
			return selected_item;
		}

		/**
		 * Sets the item to be selected.
		 * @param item the item to be selected.
		 */
		public void setSelectedItem ( Object item ) {
			selected_item = item;
			fireContentsChanged(this, -1, -1);
		}
	}
}
