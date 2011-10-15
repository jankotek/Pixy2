/*
 * @(#)VariableStarSearchSettingDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.VariableStarSearch;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.dialog.Dialog;
import net.aerith.misao.pixy.VariabilityChecker;

/**
 * The <code>VariableStarSearchSettingDialog</code> represents a 
 * dialog to set up parameters to search variable stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class VariableStarSearchSettingDialog extends Dialog {
	/**
	 * The text field to input the threshold of magnitude difference.
	 */
	protected JTextField text_mag_threshold;

	/**
	 * The text field to input the limiting magnitude of the peak.
	 */
	protected JTextField text_limiting_mag;

	/**
	 * The text field to input the period window size in days.
	 */
	protected JTextField text_period_window_size;

	/**
	 * The text field to input the pixels from edge to ignore.
	 */
	protected JTextField text_ignore_edge;

	/**
	 * The radio button for the blending policy to search variability 
	 * based on measured magnitude.
	 */
	protected JRadioButton radio_blending_not_considered;

	/**
	 * The radio button for the blending policy to search variability 
	 * based on blended magnitude.
	 */
	protected JRadioButton radio_blending_blended;

	/**
	 * The radio button for the blending policy to reject blending 
	 * stars.
	 */
	protected JRadioButton radio_blending_rejected;

	/**
	 * Constructs a <code>VariableStarSearchSettingDialog</code>.
	 */
	public VariableStarSearchSettingDialog ( ) {
		components = new Object[5];

		text_mag_threshold = new JTextField("1.0");
		text_mag_threshold.setColumns(6);
		JPanel panel = new JPanel();
		panel.add(new JLabel("Pick up data whose magnitude difference is larger than "));
		panel.add(text_mag_threshold);
		components[0] = panel;

		text_limiting_mag = new JTextField("15.0");
		text_limiting_mag.setColumns(6);
		panel = new JPanel();
		panel.add(new JLabel("Pick up data whose peak magnitude is brighter than "));
		panel.add(text_limiting_mag);
		components[1] = panel;

		text_period_window_size = new JTextField("1.0");
		text_period_window_size.setColumns(6);
		panel = new JPanel();
		panel.add(new JLabel("Ignore variability within"));
		panel.add(text_period_window_size);
		panel.add(new JLabel("days."));
		components[2] = panel;

		text_ignore_edge = new JTextField("10");
		text_ignore_edge.setColumns(6);
		panel = new JPanel();
		panel.add(new JLabel("Ignore data within"));
		panel.add(text_ignore_edge);
		panel.add(new JLabel("pixels from edge."));
		components[3] = panel;

		ButtonGroup bg = new ButtonGroup();
		radio_blending_not_considered = new JRadioButton("Search variables based on measured mag.", true);
		radio_blending_blended = new JRadioButton("Search variables based on blended mag.");
		radio_blending_rejected = new JRadioButton("Reject blending stars.");
		bg.add(radio_blending_not_considered);
		bg.add(radio_blending_blended);
		bg.add(radio_blending_rejected);

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(radio_blending_not_considered);
		panel.add(radio_blending_blended);
		panel.add(radio_blending_rejected);
		panel.setBorder(new TitledBorder("Blending stars"));
		components[4] = panel;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Variable Star Survey Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("mag-threshold") != null)
			text_mag_threshold.setText((String)default_values.get("mag-threshold"));
		if (default_values.get("limiting-mag") != null)
			text_limiting_mag.setText((String)default_values.get("limiting-mag"));
		if (default_values.get("period-window-size") != null)
			text_period_window_size.setText((String)default_values.get("period-window-size"));
		if (default_values.get("ignore-edge") != null)
			text_ignore_edge.setText((String)default_values.get("ignore-edge"));
		if (default_values.get("blending-policy") != null)
			setBlendingPolicy(((Integer)default_values.get("blending-policy")).intValue());
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("mag-threshold", text_mag_threshold.getText());
		default_values.put("limiting-mag", text_limiting_mag.getText());
		default_values.put("period-window-size", text_period_window_size.getText());
		default_values.put("ignore-edge", text_ignore_edge.getText());
		default_values.put("blending-policy", new Integer(getBlendingPolicy()));
	}

	/**
	 * Gets the threshold of magnitude difference.
	 * @return the threshold of magnitude difference.
	 */
	public double getMagnitudeThreshold ( ) {
		return Double.parseDouble(text_mag_threshold.getText());
	}

	/**
	 * Gets the limiting magnitude of the peak.
	 * @return the limiting magnitude of the peak.
	 */
	public double getLimitingMagnitude ( ) {
		return Double.parseDouble(text_limiting_mag.getText());
	}

	/**
	 * Gets the period window size in days.
	 * @return the period window size in days.
	 */
	public double getPeriodWindowSize ( ) {
		return Double.parseDouble(text_period_window_size.getText());
	}

	/**
	 * Gets the pixels from edge to ignore.
	 * @return the pixels from edge to ignore.
	 */
	public int getIgnoreEdge ( ) {
		return Integer.parseInt(text_ignore_edge.getText());
	}

	/**
	 * Gets the number of policy on blending.
	 * @return the number of policy on blending.
	 */
	public int getBlendingPolicy ( ) {
		if (radio_blending_blended.isSelected())
			return VariabilityChecker.BLENDING_BLENDED;
		if (radio_blending_rejected.isSelected())
			return VariabilityChecker.BLENDING_REJECTED;
		return VariabilityChecker.BLENDING_NOT_CONSIDERED;
	}

	/**
	 * Sets the number of policy on blending.
	 * @param policy the number of policy on blending.
	 */
	public void setBlendingPolicy ( int policy ) {
		radio_blending_not_considered.setSelected(policy == VariabilityChecker.BLENDING_NOT_CONSIDERED);
		radio_blending_blended.setSelected(policy == VariabilityChecker.BLENDING_BLENDED);
		radio_blending_rejected.setSelected(policy == VariabilityChecker.BLENDING_REJECTED);
	}
}
