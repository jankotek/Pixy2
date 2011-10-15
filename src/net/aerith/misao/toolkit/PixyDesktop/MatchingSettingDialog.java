/*
 * @(#)MatchingSettingDialog.java
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
 * The <code>MatchingSettingDialog</code> represents a dialog to select
 * the matching operation mode.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 17
 */

public class MatchingSettingDialog extends Dialog {
	/**
	 * The radio button for default matching process.
	 */
	protected JRadioButton radio_default;

	/**
	 * The radio button to retry at most 9 times.
	 */
	protected JRadioButton radio_retry;

	/**
	 * The radio button to skip matching process.
	 */
	protected JRadioButton radio_skip_matching;

	/**
	 * The radio button to search the position.
	 */
	protected JRadioButton radio_search;

	/**
	 * The text field to input the position angle of up.
	 */
	protected JTextField text_rotation;

	/**
	 * The text field to input the search radius.
	 */
	protected JTextField text_search_radius;

	/**
	 * The check box which represents the loose judgement.
	 */
	protected JCheckBox checkbox_loose;

	/**
	 * The mode number which indicates the default matching process.
	 */
	public final static int MODE_DEFAULT = 0;

	/**
	 * The mode number which indicates to retry at most 9 times.
	 */
	public final static int MODE_RETRY = 1;

	/**
	 * The mode number which indicates to skip matching process.
	 */
	public final static int MODE_SKIP_MATCHING = 2;

	/**
	 * The mode number which indicates to search the position.
	 */
	public final static int MODE_SEARCH = 3;

	/**
	 * Constructs a <code>MatchingSettingDialog</code>.
	 */
	public MatchingSettingDialog ( ) {
		components = new Object[2];

		ButtonGroup bg_mode = new ButtonGroup();
		radio_default = new JRadioButton("", true);
		radio_retry = new JRadioButton("");
		radio_skip_matching = new JRadioButton("");
		radio_search = new JRadioButton("");
		bg_mode.add(radio_default);
		bg_mode.add(radio_retry);
		bg_mode.add(radio_skip_matching);
		bg_mode.add(radio_search);

		JPanel panel_default = new JPanel();
		panel_default.add(radio_default);
		panel_default.add(new JLabel("Default matching."));

		JPanel panel_default2 = new JPanel();
		panel_default2.setLayout(new BorderLayout());
		panel_default2.add(panel_default, BorderLayout.WEST);

		JPanel panel_retry = new JPanel();
		panel_retry.add(radio_retry);
		panel_retry.add(new JLabel("Retry at most 9 times."));

		JPanel panel_retry2 = new JPanel();
		panel_retry2.setLayout(new BorderLayout());
		panel_retry2.add(panel_retry, BorderLayout.WEST);

		JPanel panel_skip_matching = new JPanel();
		panel_skip_matching.add(radio_skip_matching);
		panel_skip_matching.add(new JLabel("Skip matching process. The center R.A. and Decl., the image field of view, and the position angle of up must be accurate."));

		JPanel panel_rotation = new JPanel();
		text_rotation = new JTextField("0.0");
		text_rotation.setColumns(10);
		panel_rotation.add(new JLabel("Position angle of up"));
		panel_rotation.add(text_rotation);
		panel_rotation.add(new JLabel("degree"));

		JPanel panel_skip_matching2 = new JPanel();
		panel_skip_matching2.setLayout(new BorderLayout());
		panel_skip_matching2.add(panel_skip_matching, BorderLayout.WEST);
		panel_skip_matching2.add(panel_rotation, BorderLayout.SOUTH);

		JPanel panel_search = new JPanel();
		panel_search.add(radio_search);
		panel_search.add(new JLabel("Search position. When the center R.A. and Decl. are very uncertain."));

		JPanel panel_search_radius = new JPanel();
		text_search_radius = new JTextField("1");
		text_search_radius.setColumns(10);
		panel_search_radius.add(new JLabel("Search radius"));
		panel_search_radius.add(text_search_radius);
		panel_search_radius.add(new JLabel("degree"));

		JPanel panel_search2 = new JPanel();
		panel_search2.setLayout(new BorderLayout());
		panel_search2.add(panel_search, BorderLayout.WEST);
		panel_search2.add(panel_search_radius, BorderLayout.SOUTH);

		JPanel panel_mode = new JPanel();
		panel_mode.setLayout(new BoxLayout(panel_mode, BoxLayout.Y_AXIS));
		panel_mode.add(panel_default2);
		panel_mode.add(panel_retry2);
		panel_mode.add(panel_skip_matching2);
		panel_mode.add(panel_search2);
		panel_mode.setBorder(new TitledBorder("Mode"));
		components[0] = panel_mode;

		text_rotation.setEnabled(false);
		text_search_radius.setEnabled(false);

		ModeListener mode_listener = new ModeListener();
		radio_default.addActionListener(mode_listener);
		radio_retry.addActionListener(mode_listener);
		radio_skip_matching.addActionListener(mode_listener);
		radio_search.addActionListener(mode_listener);

		checkbox_loose = new JCheckBox("Loose judgement.");
		checkbox_loose.setSelected(false);
		components[1] = checkbox_loose;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Matching Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("mode") != null) {
			int mode = ((Integer)default_values.get("mode")).intValue();
			radio_retry.setSelected(mode == MODE_RETRY);
			radio_skip_matching.setSelected(mode == MODE_SKIP_MATCHING);
			radio_search.setSelected(mode == MODE_SEARCH);

			radio_default.setSelected((mode != MODE_RETRY)  &&  (mode != MODE_SKIP_MATCHING)  &&  (mode != MODE_SEARCH));
		}
		if (default_values.get("rotation") != null)
			text_rotation.setText((String)default_values.get("rotation"));
		if (default_values.get("radius") != null)
			text_search_radius.setText((String)default_values.get("radius"));

		if (default_values.get("loose-matching") != null)
			checkbox_loose.setSelected(((Boolean)default_values.get("loose-matching")).booleanValue());
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("mode", new Integer(getMode()));
		default_values.put("rotation", text_rotation.getText());
		default_values.put("radius", text_search_radius.getText());

		default_values.put("loose-matching", new Boolean(checkbox_loose.isSelected()));
	}

	/**
	 * Gets the matching operation mode.
	 * @return the matching operation mode.
	 */
	public int getMode ( ) {
		if (radio_retry.isSelected())
			return MODE_RETRY;
		if (radio_skip_matching.isSelected())
			return MODE_SKIP_MATCHING;
		if (radio_search.isSelected())
			return MODE_SEARCH;
		return MODE_DEFAULT;
	}

	/**
	 * Gets the position angle of up.
	 * @return the position angle of up.
	 */
	public double getPositionAngleOfUp ( ) {
		return Format.doubleValueOf(text_rotation.getText());
	}

	/**
	 * Gets the search radius.
	 * @return the search radius.
	 */
	public double getSearchRadius ( ) {
		return Format.doubleValueOf(text_search_radius.getText());
	}

	/**
	 * Returns true when loose judgement is selected.
	 * @return true when loose judgement is selected.
	 */
	public boolean isLooseJudgementSelected ( ) {
		return checkbox_loose.isSelected();
	}

	/**
	 * The <code>ModeListener</code> is a listener class of 
	 * selection of a radio button in the matching operation mode.
	 */
	protected class ModeListener implements ActionListener {
		/**
		 * Invoked when one of the check box is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			text_rotation.setEnabled(radio_skip_matching.isSelected());
			text_search_radius.setEnabled(radio_search.isSelected());
		}
	}
}
