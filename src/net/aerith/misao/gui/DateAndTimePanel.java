/*
 * @(#)DateAndTimePanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Hashtable;
import net.aerith.misao.util.*;

/**
 * The <code>DateAndTimePanel</code> represents a panel which consists
 * of components to input date and time.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 17
 */

public class DateAndTimePanel extends JPanel {
	/**
	 * The default values.
	 */
	protected static Hashtable default_values = new Hashtable();

	/**
	 * The text field to input year.
	 */
	protected JTextField text_year;

	/**
	 * The combo box to select month;
	 */
	protected JComboBox combo_month;

	/**
	 * The combo box to select day;
	 */
	protected JComboBox combo_day;

	/**
	 * The combo box to select hour;
	 */
	protected JComboBox combo_hour;

	/**
	 * The combo box to select minute;
	 */
	protected JComboBox combo_minute;

	/**
	 * The combo box to select second;
	 */
	protected JComboBox combo_second;

	/**
	 * Constructs a <code>DateAndTimePanel</code>.
	 */
	public DateAndTimePanel ( ) {
		this(true);
	}

	/**
	 * Constructs a <code>DateAndTimePanel</code>.
	 * @param time_flag true when to input the time, too.
	 */
	public DateAndTimePanel ( boolean time_flag ) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();

		text_year = new JTextField("2000");
		text_year.setColumns(6);
		panel.add(text_year);

		String[] months = new String[12];
		for (int i = 1 ; i <= 12 ; i++)
			months[i-1] = JulianDay.getFullSpellMonthString(i);
		combo_month = new JComboBox(months);
		panel.add(combo_month);

		String[] days = new String[31];
		for (int i = 1 ; i <= 31 ; i++)
			days[i-1] = String.valueOf(i);
		combo_day = new JComboBox(days);
		panel.add(combo_day);

		add(panel);

		panel = new JPanel();

		String[] hours = new String[24];
		for (int i = 0 ; i < 24 ; i++)
			hours[i] = String.valueOf(i);
		combo_hour = new JComboBox(hours);
		panel.add(combo_hour);
		panel.add(new JLabel(":"));

		String[] minutes = new String[60];
		for (int i = 0 ; i < 60 ; i++)
			minutes[i] = String.valueOf(i);
		combo_minute = new JComboBox(minutes);
		panel.add(combo_minute);
		panel.add(new JLabel(":"));

		String[] seconds = new String[60];
		for (int i = 0 ; i < 60 ; i++)
			seconds[i] = String.valueOf(i);
		combo_second = new JComboBox(seconds);
		panel.add(combo_second);
		panel.add(new JLabel("UT"));

		if (time_flag)
			add(panel);

		setDefaultValues();
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("jd") != null)
			setDay((JulianDay)default_values.get("jd"));
	}

	/**
	 * Saves the default values.
	 */
	public void saveDefaultValues ( ) {
		default_values.put("jd", getDay());
	}

	/**
	 * Sets whether or not this component is enabled.
	 * @param enabled true when to enable this panel.
	 */
	public void setEnabled ( boolean enabled ) {
		super.setEnabled(enabled);

		text_year.setEnabled(enabled);
		combo_month.setEnabled(enabled);
		combo_day.setEnabled(enabled);
		combo_hour.setEnabled(enabled);
		combo_minute.setEnabled(enabled);
		combo_second.setEnabled(enabled);
	}

	/**
	 * Gets the input date and time as Julian Day.
	 * @return input date and time as Julian Day.
	 */
	public JulianDay getDay ( ) {
		int year = Integer.parseInt(text_year.getText());
		int month = combo_month.getSelectedIndex() + 1;
		int day = combo_day.getSelectedIndex() + 1;
		int hour = combo_hour.getSelectedIndex();
		int minute = combo_minute.getSelectedIndex();
		int second = combo_second.getSelectedIndex();

		return new JulianDay(year, month, day, hour, minute, second);
	}

	/**
	 * Sets the date and time.
	 * @param jd the date and time as Julian Day.
	 */
	public void setDay ( JulianDay jd ) {
		if (jd == null)
			return;

		text_year.setText(String.valueOf(jd.getYear()));
		combo_month.setSelectedIndex(jd.getMonth() - 1);
		combo_day.setSelectedIndex(jd.getDay() - 1);
		combo_hour.setSelectedIndex(jd.getHour());
		combo_minute.setSelectedIndex(jd.getMinute());
		combo_second.setSelectedIndex(jd.getSecond());
	}
}
