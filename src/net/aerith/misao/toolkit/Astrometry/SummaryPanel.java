/*
 * @(#)SummaryPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.Astrometry;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.aerith.misao.util.*;

/**
 * The <code>SummaryPanel</code> represents a panel which shows the 
 * summary of astrometry, and which has a button to apply the result 
 * to the XML report document.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class SummaryPanel extends JPanel {
	/**
	 * The parent pane.
	 */
	protected AstrometryPane parent_pane;

	/**
	 * The label to show the number of stars.
	 */
	protected JLabel label_number_of_stars;

	/**
	 * The label to show the astrometric error.
	 */
	protected JLabel label_error;

	/**
	 * The label to show the distortion field of top-left.
	 */
	protected JLabel label_df_top_left;

	/**
	 * The label to show the distortion field of top-right.
	 */
	protected JLabel label_df_top_right;

	/**
	 * The label to show the distortion field of center.
	 */
	protected JLabel label_df_center;

	/**
	 * The label to show the distortion field of bottom-left.
	 */
	protected JLabel label_df_bottom_left;

	/**
	 * The label to show the distortion field of bottom-right.
	 */
	protected JLabel label_df_bottom_right;

	/**
	 * The number of stars.
	 */
	protected int number_of_stars = 0;

	/**
	 * The astrometric error.
	 */
	protected AstrometricError astrometric_error = null;

	/**
	 * The distortion field results at four corners and the center.
	 */
	protected Position[] d_pos = new Position[5];

	/**
	 * Constructs an <code>SummaryPanel</code>.
	 * @param parent_pane the parent pane.
	 */
	public SummaryPanel ( AstrometryPane parent_pane ) {
		this.parent_pane = parent_pane;

		for (int i = 0 ; i < 5 ; i++)
			d_pos[i] = new Position();

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		label_number_of_stars = new JLabel("");
		label_error = new JLabel("");

		panel.add(label_number_of_stars);
		panel.add(label_error);

		JPanel panel_df = new JPanel();
		panel_df.setLayout(new GridLayout(3, 3));

		label_df_top_left = new JLabel("");
		label_df_top_right = new JLabel("");
		label_df_center = new JLabel("");
		label_df_bottom_left = new JLabel("");
		label_df_bottom_right = new JLabel("");

		panel_df.add(label_df_top_left);
		panel_df.add(new JLabel(""));
		panel_df.add(label_df_top_right);
		panel_df.add(new JLabel(""));
		panel_df.add(label_df_center);
		panel_df.add(new JLabel(""));
		panel_df.add(label_df_bottom_left);
		panel_df.add(new JLabel(""));
		panel_df.add(label_df_bottom_right);

		JPanel panel_df2 = new JPanel();
		panel_df2.setLayout(new BoxLayout(panel_df2, BoxLayout.Y_AXIS));
		panel_df2.add(new JLabel("Distortion field:", JLabel.LEFT));
		panel_df2.add(panel_df);

		JPanel panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

		panel2.add(panel);
		panel2.add(new JLabel("    "));
		panel2.add(panel_df2);

		JButton button = new JButton("Apply");
		button.addActionListener(new ApplyListener());

		setLayout(new BorderLayout());

		add(panel2, BorderLayout.WEST);
		add(button, BorderLayout.EAST);

		updateLabels();
	}

	/**
	 * Updates the labels.
	 */
	public void updateLabels ( ) {
		label_number_of_stars.setText("Number of stars: " + number_of_stars);

		String s_ra = "0.0";
		String s_decl = "0.0";
		if (astrometric_error != null) {
			s_ra = Format.formatDouble(astrometric_error.getRA() * 3600.0, 8, 5).trim();
			s_decl = Format.formatDouble(astrometric_error.getDecl() * 3600.0, 8, 5).trim();
		}
		label_error.setText("Astrometric error: " + s_ra + " x " + s_decl + " arcsec");

		label_df_top_left.setText(Format.formatDouble(d_pos[1].getX(), 4, 2) + "," + Format.formatDouble(d_pos[1].getY(), 4, 2));
		label_df_top_right.setText(Format.formatDouble(d_pos[2].getX(), 4, 2) + "," + Format.formatDouble(d_pos[2].getY(), 4, 2));
		label_df_center.setText(Format.formatDouble(d_pos[0].getX(), 4, 2) + "," + Format.formatDouble(d_pos[0].getY(), 4, 2));
		label_df_bottom_left.setText(Format.formatDouble(d_pos[3].getX(), 4, 2) + "," + Format.formatDouble(d_pos[3].getY(), 4, 2));
		label_df_bottom_right.setText(Format.formatDouble(d_pos[4].getX(), 4, 2) + "," + Format.formatDouble(d_pos[4].getY(), 4, 2));

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
	 * Sets the astrometric error.
	 * @param err the astrometric error.
	 */
	public void setAstrometricError ( AstrometricError err ) {
		astrometric_error = err;
	}

	/**
	 * Sets the distortion field results at four corners and the 
	 * center.
	 * @param d_pos the distortion field results at four corners and 
	 * the center.
	 */
	public void setDistortionFieldResults ( Position[] d_pos ) {
		for (int i = 0 ; i < 5 ; i++)
			this.d_pos[i] = d_pos[i];
	}

	/**
	 * The <code>ApplyListener</code> is a listener class of button 
	 * push to apply the result of astrometry to the XML report
	 * document.
	 */
	protected class ApplyListener implements ActionListener {
		/**
		 * Invoked when the button is pushed.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			parent_pane.applyAstrometry();
		}
	}
}
