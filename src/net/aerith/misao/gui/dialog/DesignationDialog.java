/*
 * @(#)DesignationDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import net.aerith.misao.util.*;

/**
 * The <code>DesignationDialog</code> represents a dialog to input the
 * designation of an asteroid, comet or satellite.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 October 5
 */

public class DesignationDialog extends Dialog {
	/**
	 * The designation.
	 */
	protected Designation designation;

	/**
	 * The text field to show the designation.
	 */
	protected JTextField text_designation;

	/**
	 * The radio button for an asteroid.
	 */
	protected JRadioButton radio_asteroid;

	/**
	 * The radio button for a comet.
	 */
	protected JRadioButton radio_comet;

	/**
	 * The radio button for a satellite.
	 */
	protected JRadioButton radio_satellite;

	/**
	 * The combo box to select the orbit type of an asteroid.
	 */
	protected JComboBox combo_asteroid_orbit;

	/**
	 * The radio button for a numbered asteroid.
	 */
	protected JRadioButton radio_asteroid_numbered;

	/**
	 * The radio button for a provisional asteroid.
	 */
	protected JRadioButton radio_asteroid_provisional;

	/**
	 * The radio button for an asteroid of a special survey.
	 */
	protected JRadioButton radio_asteroid_survey;

	/**
	 * The text field to input the number of a numbered asteroid.
	 */
	protected JTextField text_asteroid_number;

	/**
	 * The text field to input the year of provisional designation of
	 * an asteroid.
	 */
	protected JTextField text_asteroid_year;

	/**
	 * The combo box to select the month code of provisional 
	 * designation of an asteroid.
	 */
	protected JComboBox combo_asteroid_month_code;

	/**
	 * The combo box to select the sub code in month of provisional 
	 * designation of an asteroid.
	 */
	protected JComboBox combo_asteroid_sub_code;

	/**
	 * The text field to input the sequential number in month of 
	 * provisional designation of an asteroid.
	 */
	protected JTextField text_asteroid_sequential;

	/**
	 * The combo box to select the survey.
	 */
	protected JComboBox combo_asteroid_survey;

	/**
	 * The text field to input the number in survey.
	 */
	protected JTextField text_asteroid_survey_number;

	/**
	 * The combo box to select the orbit type of a comet.
	 */
	protected JComboBox combo_comet_orbit;

	/**
	 * The radio button for a numbered periodic comet.
	 */
	protected JRadioButton radio_comet_numbered;

	/**
	 * The radio button for a provisional comet.
	 */
	protected JRadioButton radio_comet_provisional;

	/**
	 * The text field to input the number of a numbered periodic comet.
	 */
	protected JTextField text_comet_number;

	/**
	 * The text field to input the year of provisional designation of
	 * a comet.
	 */
	protected JTextField text_comet_year;

	/**
	 * The combo box to select the month code of provisional 
	 * designation of a comet.
	 */
	protected JComboBox combo_comet_month_code;

	/**
	 * The text field to input the sequential number in month of 
	 * provisional designation of a comet.
	 */
	protected JTextField text_comet_sequential;

	/**
	 * The combo box to select the fragment.
	 */
	protected JComboBox combo_comet_fragment;

	/**
	 * The combo box to select the planet.
	 */
	protected JComboBox combo_satellite_planet;

	/**
	 * The radio button for a numbered satellite.
	 */
	protected JRadioButton radio_satellite_numbered;

	/**
	 * The radio button for a provisional satellite.
	 */
	protected JRadioButton radio_satellite_provisional;

	/**
	 * The text field to input the number of a numbered satellite.
	 */
	protected JTextField text_satellite_number;

	/**
	 * The text field to input the year of provisional designation of
	 * a satellite.
	 */
	protected JTextField text_satellite_year;

	/**
	 * The text field to input the sequential number of provisional 
	 * designation of a satellite.
	 */
	protected JTextField text_satellite_sequential;

	/**
	 * Constructs a <code>DesignationDialog</code>.
	 */
	public DesignationDialog ( ) {
		components = new Object[4];

		String[] asteroid_orbits = { " ", "C", "P", "D", "X" };
		combo_asteroid_orbit = new JComboBox(asteroid_orbits);
		String[] comet_orbits = { "C", "P", "D", "A", "X" };
		combo_comet_orbit = new JComboBox(comet_orbits);

		text_designation = new JTextField("");
		text_designation.setColumns(30);
		text_designation.setEditable(false);

		text_asteroid_number = new JTextField("1");
		text_asteroid_number.setColumns(10);
		text_comet_number = new JTextField("1");
		text_comet_number.setColumns(10);
		text_satellite_number = new JTextField("1");
		text_satellite_number.setColumns(10);

		text_asteroid_year = new JTextField("2000");
		text_asteroid_year.setColumns(8);
		text_comet_year = new JTextField("2000");
		text_comet_year.setColumns(8);
		text_satellite_year = new JTextField("2000");
		text_satellite_year.setColumns(8);

		text_asteroid_sequential = new JTextField("");
		text_asteroid_sequential.setColumns(6);
		text_comet_sequential = new JTextField("1");
		text_comet_sequential.setColumns(6);
		text_satellite_sequential = new JTextField("1");
		text_satellite_sequential.setColumns(6);

		text_asteroid_survey_number = new JTextField("1");
		text_asteroid_survey_number.setColumns(10);

		String[] month_codes = { "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y" };
		combo_asteroid_month_code = new JComboBox(month_codes);
		combo_comet_month_code = new JComboBox(month_codes);

		String[] sub_codes = { "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
		combo_asteroid_sub_code = new JComboBox(sub_codes);

		String[] surveys = { "Palomar-Leiden Survey (1960)", "First Trojan Survey (1971)", "Second Trojan Survey (1973)", "Third Trojan Survey (1977)" };
		combo_asteroid_survey = new JComboBox(surveys);

		String[] fragments = { " ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
		combo_comet_fragment = new JComboBox(fragments);

		String[] planets = { "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto" };
		combo_satellite_planet = new JComboBox(planets);
		combo_satellite_planet.setSelectedItem("Jupiter");

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("Designation"));
		panel.add(text_designation);
		components[0] = panel;

		ButtonGroup bg = new ButtonGroup();
		radio_asteroid = new JRadioButton("", true);
		radio_comet = new JRadioButton("");
		radio_satellite = new JRadioButton("");
		bg.add(radio_asteroid);
		bg.add(radio_comet);
		bg.add(radio_satellite);

		JPanel panel_asteroid = new JPanel();
		panel_asteroid.setLayout(new BoxLayout(panel_asteroid, BoxLayout.Y_AXIS));
		panel_asteroid.setBorder(new TitledBorder("Asteroid"));

		ButtonGroup bg_asteroid = new ButtonGroup();
		radio_asteroid_numbered = new JRadioButton("Numbered", true);
		radio_asteroid_provisional = new JRadioButton("Provisional");
		radio_asteroid_survey = new JRadioButton("Survey");
		bg_asteroid.add(radio_asteroid_numbered);
		bg_asteroid.add(radio_asteroid_provisional);
		bg_asteroid.add(radio_asteroid_survey);

		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("Orbit Type"));
		panel.add(combo_asteroid_orbit);
		panel_asteroid.add(panel);

		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(radio_asteroid_numbered);
		panel.add(text_asteroid_number);
		panel_asteroid.add(panel);

		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(radio_asteroid_provisional);
		panel.add(text_asteroid_year);
		panel.add(combo_asteroid_month_code);
		panel.add(combo_asteroid_sub_code);
		panel.add(text_asteroid_sequential);
		panel_asteroid.add(panel);

		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(radio_asteroid_survey);
		panel.add(combo_asteroid_survey);
		panel.add(text_asteroid_survey_number);
		panel_asteroid.add(panel);

		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(radio_asteroid);
		panel.add(panel_asteroid);
		components[1] = panel;

		JPanel panel_comet = new JPanel();
		panel_comet.setLayout(new BoxLayout(panel_comet, BoxLayout.Y_AXIS));
		panel_comet.setBorder(new TitledBorder("Comet"));

		ButtonGroup bg_comet = new ButtonGroup();
		radio_comet_numbered = new JRadioButton("Numbered", true);
		radio_comet_provisional = new JRadioButton("Provisional");
		bg_comet.add(radio_comet_numbered);
		bg_comet.add(radio_comet_provisional);

		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("Orbit Type"));
		panel.add(combo_comet_orbit);
		panel_comet.add(panel);

		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(radio_comet_numbered);
		panel.add(text_comet_number);
		panel_comet.add(panel);

		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(radio_comet_provisional);
		panel.add(text_comet_year);
		panel.add(combo_comet_month_code);
		panel.add(text_comet_sequential);
		panel_comet.add(panel);

		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("Fragment"));
		panel.add(combo_comet_fragment);
		panel_comet.add(panel);

		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(radio_comet);
		panel.add(panel_comet);
		components[2] = panel;

		JPanel panel_satellite = new JPanel();
		panel_satellite.setLayout(new BoxLayout(panel_satellite, BoxLayout.Y_AXIS));
		panel_satellite.setBorder(new TitledBorder("Satellite"));

		ButtonGroup bg_satellite = new ButtonGroup();
		radio_satellite_numbered = new JRadioButton("Numbered", true);
		radio_satellite_provisional = new JRadioButton("Provisional");
		bg_satellite.add(radio_satellite_numbered);
		bg_satellite.add(radio_satellite_provisional);

		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("Planet"));
		panel.add(combo_satellite_planet);
		panel_satellite.add(panel);

		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(radio_satellite_numbered);
		panel.add(text_satellite_number);
		panel_satellite.add(panel);

		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(radio_satellite_provisional);
		panel.add(text_satellite_year);
		panel.add(text_satellite_sequential);
		panel_satellite.add(panel);

		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(radio_satellite);
		panel.add(panel_satellite);
		components[3] = panel;

		TypeChangedListener type_listener = new TypeChangedListener();
		radio_asteroid.addActionListener(type_listener);
		radio_asteroid_numbered.addActionListener(type_listener);
		radio_asteroid_provisional.addActionListener(type_listener);
		radio_asteroid_survey.addActionListener(type_listener);
		radio_comet.addActionListener(type_listener);
		radio_comet_numbered.addActionListener(type_listener);
		radio_comet_provisional.addActionListener(type_listener);
		radio_satellite.addActionListener(type_listener);
		radio_satellite_numbered.addActionListener(type_listener);
		radio_satellite_provisional.addActionListener(type_listener);

		DesignationChangedListener designation_listener = new DesignationChangedListener();
		combo_asteroid_orbit.addActionListener(designation_listener);
		combo_asteroid_month_code.addActionListener(designation_listener);
		combo_asteroid_sub_code.addActionListener(designation_listener);
		combo_asteroid_survey.addActionListener(designation_listener);
		combo_comet_orbit.addActionListener(designation_listener);
		combo_comet_month_code.addActionListener(designation_listener);
		combo_comet_fragment.addActionListener(designation_listener);
		combo_satellite_planet.addActionListener(designation_listener);
		text_asteroid_number.addCaretListener(designation_listener);
		text_asteroid_year.addCaretListener(designation_listener);
		text_asteroid_sequential.addCaretListener(designation_listener);
		text_asteroid_survey_number.addCaretListener(designation_listener);
		text_comet_number.addCaretListener(designation_listener);
		text_comet_year.addCaretListener(designation_listener);
		text_comet_sequential.addCaretListener(designation_listener);
		text_satellite_number.addCaretListener(designation_listener);
		text_satellite_year.addCaretListener(designation_listener);
		text_satellite_sequential.addCaretListener(designation_listener);

		setDefaultValues();

		updateEnabled();
		updateDesignation();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Designation Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("designation-type") != null) {
			int type = ((Integer)default_values.get("designation-type")).intValue();
			radio_asteroid.setSelected(type == 0);
			radio_comet.setSelected(type == 1);
			radio_satellite.setSelected(type == 2);
		}
		if (default_values.get("designation-asteroid-type") != null) {
			int type = ((Integer)default_values.get("designation-asteroid-type")).intValue();
			radio_asteroid_numbered.setSelected(type == 0);
			radio_asteroid_provisional.setSelected(type == 1);
			radio_asteroid_survey.setSelected(type == 2);
		}
		if (default_values.get("designation-comet-type") != null) {
			int type = ((Integer)default_values.get("designation-comet-type")).intValue();
			radio_comet_numbered.setSelected(type == 0);
			radio_comet_provisional.setSelected(type == 1);
		}
		if (default_values.get("designation-satellite-type") != null) {
			int type = ((Integer)default_values.get("designation-satellite-type")).intValue();
			radio_satellite_numbered.setSelected(type == 0);
			radio_satellite_provisional.setSelected(type == 1);
		}

		if (default_values.get("asteroid-orbit-type") != null)
			combo_asteroid_orbit.setSelectedIndex(((Integer)default_values.get("asteroid-orbit-type")).intValue());
		if (default_values.get("asteroid-month-code") != null)
			combo_asteroid_month_code.setSelectedIndex(((Integer)default_values.get("asteroid-month-code")).intValue());
		if (default_values.get("asteroid-sub-code") != null)
			combo_asteroid_sub_code.setSelectedIndex(((Integer)default_values.get("asteroid-sub-code")).intValue());
		if (default_values.get("asteroid-survey") != null)
			combo_asteroid_survey.setSelectedIndex(((Integer)default_values.get("asteroid-survey")).intValue());
		if (default_values.get("comet-orbit-type") != null)
			combo_comet_orbit.setSelectedIndex(((Integer)default_values.get("comet-orbit-type")).intValue());
		if (default_values.get("comet-month-code") != null)
			combo_comet_month_code.setSelectedIndex(((Integer)default_values.get("comet-month-code")).intValue());
		if (default_values.get("comet-fragment") != null)
			combo_comet_fragment.setSelectedIndex(((Integer)default_values.get("comet-fragment")).intValue());
		if (default_values.get("satellite-planet") != null)
			combo_satellite_planet.setSelectedIndex(((Integer)default_values.get("satellite-planet")).intValue());

		if (default_values.get("asteroid-number") != null)
			text_asteroid_number.setText((String)default_values.get("asteroid-number"));
		if (default_values.get("asteroid-year") != null)
			text_asteroid_year.setText((String)default_values.get("asteroid-year"));
		if (default_values.get("asteroid-sequential") != null)
			text_asteroid_sequential.setText((String)default_values.get("asteroid-sequential"));
		if (default_values.get("asteroid-survey-number") != null)
			text_asteroid_survey_number.setText((String)default_values.get("asteroid-survey-number"));
		if (default_values.get("comet-number") != null)
			text_comet_number.setText((String)default_values.get("comet-number"));
		if (default_values.get("comet-year") != null)
			text_comet_year.setText((String)default_values.get("comet-year"));
		if (default_values.get("comet-sequential") != null)
			text_comet_sequential.setText((String)default_values.get("comet-sequential"));
		if (default_values.get("satellite-number") != null)
			text_satellite_number.setText((String)default_values.get("satellite-number"));
		if (default_values.get("satellite-year") != null)
			text_satellite_year.setText((String)default_values.get("satellite-year"));
		if (default_values.get("satellite-sequential") != null)
			text_satellite_sequential.setText((String)default_values.get("satellite-sequential"));
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		if (radio_asteroid.isSelected())
			default_values.put("designation-type", new Integer(0));
		else if (radio_comet.isSelected())
			default_values.put("designation-type", new Integer(1));
		else if (radio_satellite.isSelected())
			default_values.put("designation-type", new Integer(2));

		if (radio_asteroid_numbered.isSelected())
			default_values.put("designation-asteroid-type", new Integer(0));
		else if (radio_asteroid_provisional.isSelected())
			default_values.put("designation-asteroid-type", new Integer(1));
		else if (radio_asteroid_survey.isSelected())
			default_values.put("designation-asteroid-type", new Integer(2));

		if (radio_comet_numbered.isSelected())
			default_values.put("designation-comet-type", new Integer(0));
		else if (radio_comet_provisional.isSelected())
			default_values.put("designation-comet-type", new Integer(1));

		if (radio_satellite_numbered.isSelected())
			default_values.put("designation-satellite-type", new Integer(0));
		else if (radio_satellite_provisional.isSelected())
			default_values.put("designation-satellite-type", new Integer(1));

		default_values.put("asteroid-orbit-type", new Integer(combo_asteroid_orbit.getSelectedIndex()));
		default_values.put("asteroid-month-code", new Integer(combo_asteroid_month_code.getSelectedIndex()));
		default_values.put("asteroid-sub-code", new Integer(combo_asteroid_sub_code.getSelectedIndex()));
		default_values.put("asteroid-survey", new Integer(combo_asteroid_survey.getSelectedIndex()));
		default_values.put("comet-orbit-type", new Integer(combo_comet_orbit.getSelectedIndex()));
		default_values.put("comet-month-code", new Integer(combo_comet_month_code.getSelectedIndex()));
		default_values.put("comet-fragment", new Integer(combo_comet_fragment.getSelectedIndex()));
		default_values.put("satellite-planet", new Integer(combo_satellite_planet.getSelectedIndex()));

		default_values.put("asteroid-number", text_asteroid_number.getText());
		default_values.put("asteroid-year", text_asteroid_year.getText());
		default_values.put("asteroid-sequential", text_asteroid_sequential.getText());
		default_values.put("asteroid-survey-number", text_asteroid_survey_number.getText());
		default_values.put("comet-number", text_comet_number.getText());
		default_values.put("comet-year", text_comet_year.getText());
		default_values.put("comet-sequential", text_comet_sequential.getText());
		default_values.put("satellite-number", text_satellite_number.getText());
		default_values.put("satellite-year", text_satellite_year.getText());
		default_values.put("satellite-sequential", text_satellite_sequential.getText());
	}

	/**
	 * Gets the designation.
	 * @return the designation.
	 */
	public Designation getDesignation ( ) {
		return designation;
	}

	/**
	 * Updates the designation.
	 */
	private void updateDesignation ( ) {
		designation = null;

		if (radio_asteroid.isSelected()) {
			if (radio_asteroid_numbered.isSelected()) {
				designation = new Designation(Designation.TYPE_ASTEROID_NUMBERED);
				designation.setNumber(Format.intValueOf(text_asteroid_number.getText()));
			} else if (radio_asteroid_provisional.isSelected()) {
				designation = new Designation(Designation.TYPE_ASTEROID_PROVISIONAL);

				designation.setOrbit(Designation.ORBIT_ASTEROID);
				if (((String)combo_asteroid_orbit.getSelectedItem()).equals("C"))
					designation.setOrbit(Designation.ORBIT_COMET);
				else if (((String)combo_asteroid_orbit.getSelectedItem()).equals("P"))
					designation.setOrbit(Designation.ORBIT_PERIODIC);
				else if (((String)combo_asteroid_orbit.getSelectedItem()).equals("D"))
					designation.setOrbit(Designation.ORBIT_LOST);
				else if (((String)combo_asteroid_orbit.getSelectedItem()).equals("X"))
					designation.setOrbit(Designation.ORBIT_UNCERTAIN);

				designation.setYear(Format.intValueOf(text_asteroid_year.getText()));
				designation.setMonthCode((String)combo_asteroid_month_code.getSelectedItem());
				designation.setSubCode((String)combo_asteroid_sub_code.getSelectedItem());
				designation.setSequentialNumber(Format.intValueOf(text_asteroid_sequential.getText()));
			} else if (radio_asteroid_survey.isSelected()) {
				designation = new Designation(Designation.TYPE_ASTEROID_SURVEY);

				switch (combo_asteroid_survey.getSelectedIndex()) {
					case 0:
						designation.setSurvey(Designation.SURVEY_PL);
						break;
					case 1:
						designation.setSurvey(Designation.SURVEY_T1);
						break;
					case 2:
						designation.setSurvey(Designation.SURVEY_T2);
						break;
					case 3:
						designation.setSurvey(Designation.SURVEY_T3);
						break;
				}

				designation.setNumber(Format.intValueOf(text_asteroid_survey_number.getText()));
			}
		}

		if (radio_comet.isSelected()) {
			if (radio_comet_numbered.isSelected()) {
				designation = new Designation(Designation.TYPE_COMET_NUMBERED);
				designation.setNumber(Format.intValueOf(text_comet_number.getText()));
			} else if (radio_comet_provisional.isSelected()) {
				designation = new Designation(Designation.TYPE_COMET_PROVISIONAL);
				designation.setYear(Format.intValueOf(text_comet_year.getText()));
				designation.setMonthCode((String)combo_comet_month_code.getSelectedItem());
				designation.setSequentialNumber(Format.intValueOf(text_comet_sequential.getText()));
			}

			if (designation != null) {
				if (((String)combo_comet_orbit.getSelectedItem()).equals("C"))
					designation.setOrbit(Designation.ORBIT_COMET);
				else if (((String)combo_comet_orbit.getSelectedItem()).equals("P"))
					designation.setOrbit(Designation.ORBIT_PERIODIC);
				else if (((String)combo_comet_orbit.getSelectedItem()).equals("D"))
					designation.setOrbit(Designation.ORBIT_LOST);
				else if (((String)combo_comet_orbit.getSelectedItem()).equals("A"))
					designation.setOrbit(Designation.ORBIT_ASTEROID);
				else if (((String)combo_comet_orbit.getSelectedItem()).equals("X"))
					designation.setOrbit(Designation.ORBIT_UNCERTAIN);

				if (combo_comet_fragment.getSelectedIndex() == 0)
					designation.setFragment("");
				else
					designation.setFragment((String)combo_comet_fragment.getSelectedItem());
			}
		}

		if (radio_satellite.isSelected()) {
			if (radio_satellite_numbered.isSelected()) {
				designation = new Designation(Designation.TYPE_SATELLITE_NUMBERED);
				designation.setNumber(Format.intValueOf(text_satellite_number.getText()));
			} else if (radio_satellite_provisional.isSelected()) {
				designation = new Designation(Designation.TYPE_SATELLITE_PROVISIONAL);
				designation.setYear(Format.intValueOf(text_satellite_year.getText()));
				designation.setSequentialNumber(Format.intValueOf(text_satellite_sequential.getText()));
			}

			if (designation != null) {
				switch (combo_satellite_planet.getSelectedIndex()) {
					case 0:
						designation.setPlanet(Designation.PLANET_MERCURY);
						break;
					case 1:
						designation.setPlanet(Designation.PLANET_VENUS);
						break;
					case 2:
						designation.setPlanet(Designation.PLANET_EARTH);
						break;
					case 3:
						designation.setPlanet(Designation.PLANET_MARS);
						break;
					case 4:
						designation.setPlanet(Designation.PLANET_JUPITER);
						break;
					case 5:
						designation.setPlanet(Designation.PLANET_SATURN);
						break;
					case 6:
						designation.setPlanet(Designation.PLANET_URANUS);
						break;
					case 7:
						designation.setPlanet(Designation.PLANET_NEPTUNE);
						break;
					case 8:
						designation.setPlanet(Designation.PLANET_PLUTO);
						break;
				}
			}
		}

		if (designation == null)
			text_designation.setText("");
		else
			text_designation.setText(designation.getOutputString());
	}

	/**
	 * Enables and disables the components properly.
	 */
	private void updateEnabled ( ) {
		radio_asteroid_numbered.setEnabled(radio_asteroid.isSelected());
		radio_asteroid_provisional.setEnabled(radio_asteroid.isSelected());
		radio_asteroid_survey.setEnabled(radio_asteroid.isSelected());

		text_asteroid_number.setEnabled(radio_asteroid.isSelected()  &&  radio_asteroid_numbered.isSelected());

		combo_asteroid_orbit.setEnabled(radio_asteroid.isSelected()  &&  radio_asteroid_provisional.isSelected());
		text_asteroid_year.setEnabled(radio_asteroid.isSelected()  &&  radio_asteroid_provisional.isSelected());
		combo_asteroid_month_code.setEnabled(radio_asteroid.isSelected()  &&  radio_asteroid_provisional.isSelected());
		combo_asteroid_sub_code.setEnabled(radio_asteroid.isSelected()  &&  radio_asteroid_provisional.isSelected());
		text_asteroid_sequential.setEnabled(radio_asteroid.isSelected()  &&  radio_asteroid_provisional.isSelected());

		combo_asteroid_survey.setEnabled(radio_asteroid.isSelected()  &&  radio_asteroid_survey.isSelected());
		text_asteroid_survey_number.setEnabled(radio_asteroid.isSelected()  &&  radio_asteroid_survey.isSelected());

		combo_comet_orbit.setEnabled(radio_comet.isSelected());
		radio_comet_numbered.setEnabled(radio_comet.isSelected());
		radio_comet_provisional.setEnabled(radio_comet.isSelected());
		combo_comet_fragment.setEnabled(radio_comet.isSelected());

		text_comet_number.setEnabled(radio_comet.isSelected()  &&  radio_comet_numbered.isSelected());

		text_comet_year.setEnabled(radio_comet.isSelected()  &&  radio_comet_provisional.isSelected());
		combo_comet_month_code.setEnabled(radio_comet.isSelected()  &&  radio_comet_provisional.isSelected());
		text_comet_sequential.setEnabled(radio_comet.isSelected()  &&  radio_comet_provisional.isSelected());

		combo_satellite_planet.setEnabled(radio_satellite.isSelected());
		radio_satellite_numbered.setEnabled(radio_satellite.isSelected());
		radio_satellite_provisional.setEnabled(radio_satellite.isSelected());

		text_satellite_number.setEnabled(radio_satellite.isSelected()  &&  radio_satellite_numbered.isSelected());

		text_satellite_year.setEnabled(radio_satellite.isSelected()  &&  radio_satellite_provisional.isSelected());
		text_satellite_sequential.setEnabled(radio_satellite.isSelected()  &&  radio_satellite_provisional.isSelected());
	}

	/**
	 * The <code>TypeChangedListener</code> is a listener class of the 
	 * radio button selection.
	 */
	protected class TypeChangedListener implements ActionListener {
		/**
		 * Invoked when one of the radio buttons is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			updateEnabled();
			updateDesignation();
		}
	}

	/**
	 * The <code>DesignationChangedListener</code> is a listener class 
	 * to change the designation.
	 */
	protected class DesignationChangedListener implements ActionListener, CaretListener {
		/**
		 * Invoked when one of the combo boxes is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			updateDesignation();
		}

		/**
		 * Invoked when the caret position is updated.
		 * @param e contains the caret information.
		 */
		public void caretUpdate ( CaretEvent e ) {
			updateDesignation();
		}
	}
}
