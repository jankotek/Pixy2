/*
 * @(#)BatchExaminationSettingDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.BatchExamination;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.Dialog;
import net.aerith.misao.image.filter.FilterSet;
import net.aerith.misao.pixy.star_detection.DefaultStarDetector;
import net.aerith.misao.toolkit.FilterSelection.FilterSelectionDialog;

/**
 * The <code>BatchExaminationSettingDialog</code> represents a dialog 
 * to configure the setting of batch examination.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 25
 */

public class BatchExaminationSettingDialog extends Dialog {
	/**
	 * The set of image processing filters.
	 */
	protected FilterSet filter_set = new FilterSet();

	/**
	 * The button to select image processing filters.
	 */
	protected JButton button_filter;

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
	 * The check box which represents the loose judgement in matching.
	 */
	protected JCheckBox checkbox_loose_matching;

	/**
	 * The check box which represents to calculate the distortion 
	 * field.
	 */
	protected JCheckBox checkbox_distortion;

	/**
	 * Constructs a <code>BatchExaminationSettingDialog</code>.
	 */
	public BatchExaminationSettingDialog ( ) {
		components = new Object[5];

		button_filter = new JButton("Image processing filters");
		button_filter.addActionListener(new FilterListener());
		components[0] = button_filter;

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
		panel_mode.setBorder(new TitledBorder("Star detection mode"));
		components[1] = panel_mode;

		checkbox_correct_blooming = new JCheckBox("Correct positions of blooming stars.");
		checkbox_correct_blooming.setSelected(false);
		components[2] = checkbox_correct_blooming;

		checkbox_loose_matching = new JCheckBox("Loose judgement in matching.");
		checkbox_loose_matching.setSelected(false);
		components[3] = checkbox_loose_matching;

		checkbox_distortion = new JCheckBox("Calculate distortion field.");
		checkbox_distortion.setSelected(false);
		components[4] = checkbox_distortion;

		setDefaultValues();

		updateComponents();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Batch Examination Setting";
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
		if (default_values.get("loose-matching") != null)
			checkbox_loose_matching.setSelected(((Boolean)default_values.get("loose-matching")).booleanValue());
		if (default_values.get("distortion") != null)
			setCalculateDistortionField(((Boolean)default_values.get("distortion")).booleanValue());
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("star-detection-mode", new Integer(getStarDetectionMode()));
		default_values.put("correct-blooming", new Boolean(correctsBloomingPosition()));
		default_values.put("loose-matching", new Boolean(checkbox_loose_matching.isSelected()));
		default_values.put("distortion", new Boolean(calculatesDistortionField()));
	}

	/**
	 * Gets a set of selected image processing filters.
	 * @return a set of selected image processing filters.
	 */
	public FilterSet getFilterSet ( ) {
		return filter_set;
	}

	/**
	 * Gets the star detection mode.
	 * @return the star detection mode.
	 */
	public int getStarDetectionMode ( ) {
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
	 * Returns true when loose judgement is selected.
	 * @return true when loose judgement is selected.
	 */
	public boolean isLooseJudgementSelected ( ) {
		return checkbox_loose_matching.isSelected();
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
	 * Updates the components.
	 */
	private void updateComponents ( ) {
		text_inner_aperture.setEnabled(getStarDetectionMode() == DefaultStarDetector.MODE_APERTURE);
		text_outer_aperture.setEnabled(getStarDetectionMode() == DefaultStarDetector.MODE_APERTURE);
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

	/**
	 * The <code>FilterListener</code> is a listener class of menu 
	 * selection to select image processing filters.
	 */
	protected class FilterListener implements ActionListener {
		/**
		 * Invoked when one of the radio button menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			FilterSelectionDialog dialog = new FilterSelectionDialog(filter_set);

			int answer = dialog.show(button_filter);
			if (answer == 0)
				filter_set = dialog.getFilterSet();
		}
	}
}
