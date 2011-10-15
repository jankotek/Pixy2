/*
 * @(#)StarDetectionSettingDialog.java
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
import net.aerith.misao.pixy.star_detection.DefaultStarDetector;

/**
 * The <code>StarDetectionSettingDialog</code> represents a dialog to
 * configure the setting of star detection.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 25
 */

public class StarDetectionSettingDialog extends Dialog {
	/**
	 * The radio button to regard the amount of pixel values over the 
	 * threshold as brightness of stars.
	 */
	protected JRadioButton radio_amount;

	/**
	 * The radio button to regard the peak value as brightness of 
	 * stars.
	 */
	protected JRadioButton radio_peak;

	/**
	 * The radio button for aperture photometry.
	 */
	protected JRadioButton radio_aperture;

	/**
	 * The text field to input the inner aperture size.
	 */
	protected JTextField text_inner_aperture;

	/**
	 * The text field to input the outer aperture size.
	 */
	protected JTextField text_outer_aperture;

	/**
	 * The check box to correct the positions of blooming stars.
	 */
	protected JCheckBox checkbox_correct_blooming;

	/**
	 * Constructs a <code>StarDetectionSettingDialog</code>.
	 */
	public StarDetectionSettingDialog ( ) {
		components = new Object[2];

		ButtonGroup bg_mode = new ButtonGroup();
		radio_amount = new JRadioButton("", true);
		radio_peak = new JRadioButton("");
		radio_aperture = new JRadioButton("");
		bg_mode.add(radio_amount);
		bg_mode.add(radio_peak);
		bg_mode.add(radio_aperture);

		radio_amount.addActionListener(new ModeListener());
		radio_peak.addActionListener(new ModeListener());
		radio_aperture.addActionListener(new ModeListener());

		JPanel panel_amount = new JPanel();
		panel_amount.add(radio_amount);
		panel_amount.add(new JLabel("Regard amount of pixel values over threshold as brightness."));

		JPanel panel_amount2 = new JPanel();
		panel_amount2.setLayout(new BorderLayout());
		panel_amount2.add(panel_amount, BorderLayout.WEST);

		JPanel panel_peak = new JPanel();
		panel_peak.add(radio_peak);
		panel_peak.add(new JLabel("Regard peak value as brightness."));

		JPanel panel_peak2 = new JPanel();
		panel_peak2.setLayout(new BorderLayout());
		panel_peak2.add(panel_peak, BorderLayout.WEST);

		JPanel panel_aperture = new JPanel();
		panel_aperture.add(radio_aperture);
		panel_aperture.add(new JLabel("Aperture photometry."));

		text_inner_aperture = new JTextField("2");
		text_inner_aperture.setColumns(5);
		text_outer_aperture = new JTextField("4");
		text_outer_aperture.setColumns(5);

		JPanel panel_inner_aperture = new JPanel();
		panel_inner_aperture.add(new JLabel("    Inner aperture: "));
		panel_inner_aperture.add(text_inner_aperture);
		panel_inner_aperture.add(new JLabel("pixels."));

		JPanel panel_outer_aperture = new JPanel();
		panel_outer_aperture.add(new JLabel("    Outer aperture: "));
		panel_outer_aperture.add(text_outer_aperture);
		panel_outer_aperture.add(new JLabel("pixels."));

		JPanel panel_aperture2 = new JPanel();
		panel_aperture2.setLayout(new BoxLayout(panel_aperture2, BoxLayout.Y_AXIS));
		panel_aperture2.add(panel_inner_aperture);
		panel_aperture2.add(panel_outer_aperture);

		JPanel panel_aperture3 = new JPanel();
		panel_aperture3.setLayout(new BorderLayout());
		panel_aperture3.add(panel_aperture, BorderLayout.WEST);

		JPanel panel_aperture4 = new JPanel();
		panel_aperture4.setLayout(new BorderLayout());
		panel_aperture4.add(panel_aperture2, BorderLayout.WEST);

		JPanel panel_mode = new JPanel();
		panel_mode.setLayout(new BoxLayout(panel_mode, BoxLayout.Y_AXIS));
		panel_mode.add(panel_amount2);
		panel_mode.add(panel_peak2);
		panel_mode.add(panel_aperture3);
		panel_mode.add(panel_aperture4);
		panel_mode.setBorder(new TitledBorder("Mode"));
		components[0] = panel_mode;

		checkbox_correct_blooming = new JCheckBox("Correct positions of blooming stars.");
		components[1] = checkbox_correct_blooming;

		setDefaultValues();

		updateComponents();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Star Detection Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("star-detection-mode") != null) {
			int mode = ((Integer)default_values.get("star-detection-mode")).intValue();
			radio_peak.setSelected(mode == DefaultStarDetector.MODE_PEAK);
			radio_aperture.setSelected(mode == DefaultStarDetector.MODE_APERTURE);

			radio_amount.setSelected(mode != DefaultStarDetector.MODE_PEAK  &&  mode != DefaultStarDetector.MODE_APERTURE);
		}
		if (default_values.get("correct-blooming") != null)
			setCorrectBloomingPosition(((Boolean)default_values.get("correct-blooming")).booleanValue());
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("star-detection-mode", new Integer(getMode()));
		default_values.put("correct-blooming", new Boolean(correctsBloomingPosition()));
	}

	/**
	 * Gets the mode.
	 * @return the mode.
	 */
	public int getMode ( ) {
		if (radio_peak.isSelected())
			return DefaultStarDetector.MODE_PEAK;
		if (radio_aperture.isSelected())
			return DefaultStarDetector.MODE_APERTURE;
		return DefaultStarDetector.MODE_PIXEL_AMOUNT_OVER_THRESHOLD;
	}

	/**
	 * Gets the inner aperture size.
	 * @return the inner aperture size.
	 */
	public int getInnerApertureSize ( ) {
		return Format.intValueOf(text_inner_aperture.getText());
	}

	/**
	 * Gets the outer aperture size.
	 * @return the outer aperture size.
	 */
	public int getOuterApertureSize ( ) {
		return Format.intValueOf(text_outer_aperture.getText());
	}

	/**
	 * Returns true when to correct positions of blooming stars.
	 * @return true when to correct positions of blooming stars.
	 */
	public boolean correctsBloomingPosition ( ) {
		return checkbox_correct_blooming.isSelected();
	}

	/**
	 * Sets the flag to correct positions of blooming stars.
	 * @param f true when to correct positions of blooming stars.
	 */
	public void setCorrectBloomingPosition ( boolean f ) {
		checkbox_correct_blooming.setSelected(f);
	}

	/**
	 * Updates the components.
	 */
	private void updateComponents ( ) {
		text_inner_aperture.setEnabled(getMode() == DefaultStarDetector.MODE_APERTURE);
		text_outer_aperture.setEnabled(getMode() == DefaultStarDetector.MODE_APERTURE);
	}

	/**
	 * The <code>ModeListener</code> is a listener class to select a
	 * mode.
	 */
	protected class ModeListener implements ActionListener {
		/**
		 * Invoked when one of the check box is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			updateComponents();
		}
	}
}
