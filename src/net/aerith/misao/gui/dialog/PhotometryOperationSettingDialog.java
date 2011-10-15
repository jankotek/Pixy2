/*
 * @(#)PhotometryOperationSettingDialog.java
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
import java.util.*;
import net.aerith.misao.util.*;

/**
 * The <code>PhotometryOperationSettingDialog</code> represents a 
 * dialog to set parameters for photometry operation.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 November 4
 */

public class PhotometryOperationSettingDialog extends Dialog {
	/**
	 * The text field to input minimum number of stars.
	 */
	protected JTextField text_minimum_count;

	/**
	 * The text field to input mean error threshold to accept.
	 */
	protected JTextField text_mean_threshold_to_accept;

	/**
	 * The text field to input error threshold to reject each star.
	 */
	protected JTextField text_threshold_to_reject;

	/**
	 * The text field to input the brighter limiting magnitude to use 
	 * for photometry.
	 */
	protected JTextField text_limit_mag_brighter;

	/**
	 * The text field to input the fainter limiting magnitude to use 
	 * for photometry.
	 */
	protected JTextField text_limit_mag_fainter;

	/**
	 * Constructs a <code>PhotometryOperationSettingDialog</code>.
	 */
	public PhotometryOperationSettingDialog ( ) {
		components = new Object[4];

		text_minimum_count = new JTextField("3");
		text_mean_threshold_to_accept = new JTextField("0.3");
		text_threshold_to_reject = new JTextField("0.6");
		text_limit_mag_brighter = new JTextField("0.0");
		text_limit_mag_fainter = new JTextField("20.0");

		text_minimum_count.setColumns(4);
		text_mean_threshold_to_accept.setColumns(8);
		text_threshold_to_reject.setColumns(8);
		text_limit_mag_brighter.setColumns(8);
		text_limit_mag_fainter.setColumns(8);

		JPanel panel = new JPanel();
		panel.add(new JLabel("Minimum number of stars: "));
		panel.add(text_minimum_count);
		components[0] = panel;

		panel = new JPanel();
		panel.add(new JLabel("Mean error threshold to accept: "));
		panel.add(text_mean_threshold_to_accept);
		panel.add(new JLabel("mag"));
		components[1] = panel;

		panel = new JPanel();
		panel.add(new JLabel("Error threshold to reject each star: "));
		panel.add(text_threshold_to_reject);
		panel.add(new JLabel("mag"));
		components[2] = panel;

		panel = new JPanel();
		panel.add(new JLabel("Magnitude range to use for photometry: "));
		panel.add(new JLabel("Between"));
		panel.add(text_limit_mag_brighter);
		panel.add(new JLabel("and"));
		panel.add(text_limit_mag_fainter);
		panel.add(new JLabel("mag"));
		components[3] = panel;
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Photometry Operation Setting";
	}

	/**
	 * Gets the minimum number of stars.
	 * @return the minimum number of stars.
	 */
	public int getMinimumStarCount ( ) {
		return Integer.parseInt(text_minimum_count.getText());
	}

	/**
	 * Gets the mean error threshold to accept.
	 * @return the mean error threshold to accept.
	 */
	public double getMeanThresholdToAccept ( ) {
		return Double.parseDouble(text_mean_threshold_to_accept.getText());
	}

	/**
	 * Gets the error threshold to reject each star.
	 * @return the error threshold to reject each star.
	 */
	public double getThresholdToReject ( ) {
		return Double.parseDouble(text_threshold_to_reject.getText());
	}

	/**
	 * Gets the brighter limiting magnitude to use for photometry.
	 * @return the brighter limiting magnitude to use for photometry.
	 */
	public double getBrighterLimitingMagnitude ( ) {
		return Double.parseDouble(text_limit_mag_brighter.getText());
	}

	/**
	 * Gets the fainter limiting magnitude to use for photometry.
	 * @return the fainter limiting magnitude to use for photometry.
	 */
	public double getFainterLimitingMagnitude ( ) {
		return Double.parseDouble(text_limit_mag_fainter.getText());
	}
}
