/*
 * @(#)SummaryPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.Photometry;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.aerith.misao.util.*;

/**
 * The <code>SummaryPanel</code> represents a panel which shows the 
 * summary of photometry, and which has a button to apply the result 
 * to the XML report document.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class SummaryPanel extends JPanel {
	/**
	 * The parent pane.
	 */
	protected PhotometryPane parent_pane;

	/**
	 * The label to show the number of stars.
	 */
	protected JLabel label_number_of_stars;

	/**
	 * The label to show the photometric error.
	 */
	protected JLabel label_error;

	/**
	 * The label to show the magnitude translation formula.
	 */
	protected JLabel label_magnitude_translation_formula;

	/**
	 * The label to show the magnitude system formula.
	 */
	protected JLabel label_magnitude_system_formula;

	/**
	 * The number of stars.
	 */
	protected int number_of_stars = 0;

	/**
	 * The photometric error.
	 */
	protected double photometric_error = 0.0;

	/**
	 * The magnitude translation formula.
	 */
	protected MagnitudeTranslationFormula magnitude_translation_formula = new MagnitudeTranslationFormula();

	/**
	 * The gradient of (B-V) of the magnitude system formula.
	 */
	protected double gradient_bv = 0.0;

	/**
	 * Constructs a <code>SummaryPanel</code>.
	 * @param parent_pane the parent pane.
	 */
	public SummaryPanel ( PhotometryPane parent_pane ) {
		this.parent_pane = parent_pane;

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		label_number_of_stars = new JLabel("");
		label_error = new JLabel("");
		label_magnitude_translation_formula = new JLabel("");
		label_magnitude_system_formula = new JLabel("");

		panel.add(label_number_of_stars);
		panel.add(label_error);
		panel.add(label_magnitude_translation_formula);
		panel.add(label_magnitude_system_formula);

		JButton button = new JButton("Apply");
		button.addActionListener(new ApplyListener());

		setLayout(new BorderLayout());

		add(panel, BorderLayout.WEST);
		add(button, BorderLayout.EAST);

		updateLabels();
	}

	/**
	 * Updates the labels.
	 */
	public void updateLabels ( ) {
		label_number_of_stars.setText("Number of stars: " + number_of_stars);
		label_error.setText("Photometric error: " + Format.formatDouble(photometric_error, 5, 2));
		label_magnitude_translation_formula.setText(magnitude_translation_formula.getOutputString());
		label_magnitude_system_formula.setText("Instrumental mag = V + " + gradient_bv + " * (B-V)");

		repaint();
	}

	/**
	 * Sets the number of stars.
	 * @param count the number of stars.
	 */
	public void setNumberOfStars ( int count ) {
		number_of_stars = count;
	}

	/**
	 * Sets the photometric error.
	 * @param err the photometric error.
	 */
	public void setPhotometricError ( double err ) {
		photometric_error = err;
	}

	/**
	 * Sets the magnitude translation formula.
	 * @param formula the magnitude translation formula.
	 */
	public void setMagnitudeTranslationFormula ( MagnitudeTranslationFormula formula ) {
		magnitude_translation_formula = formula;
	}

	/**
	 * Sets the gradient of (B-V) of the magnitude system formula.
	 * @param gradient the gradient of (B-V).
	 */
	public void setGradientBV ( double gradient ) {
		gradient_bv = gradient;
	}

	/**
	 * The <code>ApplyListener</code> is a listener class of button 
	 * push to apply the result of photometry to the XML report
	 * document.
	 */
	protected class ApplyListener implements ActionListener {
		/**
		 * Invoked when the button is pushed.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			parent_pane.applyPhotometry();
		}
	}
}
