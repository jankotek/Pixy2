/*
 * @(#)HtmlImageGallerySettingDialog.java
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

/**
 * The <code>HtmlImageGallerySettingDialog</code> represents a dialog 
 * to configure how to put the images on the gallery.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2005 May 22
 */

public class HtmlImageGallerySettingDialog extends Dialog {
	/**
	 * The radio button to put all images.
	 */
	protected JRadioButton radio_all;

	/**
	 * The radio button to put the brightest image.
	 */
	protected JRadioButton radio_brightest;

	/**
	 * The radio button to put the brightest and faintest images.
	 */
	protected JRadioButton radio_brightest_faintest;

	/**
	 * The check box to create FITS thumbnail images.
	 */
	protected JCheckBox checkbox_fits;

	/**
	 * The check box to add past images from the database.
	 */
	protected JCheckBox checkbox_past;

	/**
	 * The radio button to add a past image from the database, where
	 * the target position is nearest by the center.
	 */
	protected JRadioButton radio_past_center;

	/**
	 * The radio button to add all past images from the database.
	 */
	protected JRadioButton radio_past_all;

	/**
	 * The check box to add a DSS image.
	 */
	protected JCheckBox checkbox_dss;

	/**
	 * The number which implies to put all images.
	 */
	public final static int MODE_ALL = 0;

	/**
	 * The number which implies to put the brightest image.
	 */
	public final static int MODE_BRIGHTEST = 1;

	/**
	 * The number which implies to put the brightest and faintest 
	 * images.
	 */
	public final static int MODE_BRIGHTEST_FAINTEST = 2;

	/**
	 * The number which implies not to add past images.
	 */
	public final static int PAST_MODE_NONE = 0;

	/**
	 * The number which implies to add a past image from the database, 
	 * where the target position is nearest by the center.
	 */
	public final static int PAST_MODE_CENTER = 1;

	/**
	 * The number which implies to add all past images from the 
	 * database.
	 */
	public final static int PAST_MODE_ALL = 2;

	/**
	 * Constructs an <code>HtmlImageGallerySettingDialog</code>.
	 */
	public HtmlImageGallerySettingDialog ( ) {
		components = new Object[1];

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		ButtonGroup bg = new ButtonGroup();
		radio_all = new JRadioButton("", true);
		radio_brightest = new JRadioButton("");
		radio_brightest_faintest = new JRadioButton("");
		bg.add(radio_all);
		bg.add(radio_brightest);
		bg.add(radio_brightest_faintest);

		JPanel panel_all = new JPanel();
		panel_all.add(radio_all);
		panel_all.add(new JLabel("All images."));
		JPanel panel_all2 = new JPanel();
		panel_all2.setLayout(new BorderLayout());
		panel_all2.add(panel_all, BorderLayout.WEST);

		JPanel panel_brightest = new JPanel();
		panel_brightest.add(radio_brightest);
		panel_brightest.add(new JLabel("Brightest image."));
		JPanel panel_brightest2 = new JPanel();
		panel_brightest2.setLayout(new BorderLayout());
		panel_brightest2.add(panel_brightest, BorderLayout.WEST);

		JPanel panel_brightest_faintest = new JPanel();
		panel_brightest_faintest.add(radio_brightest_faintest);
		panel_brightest_faintest.add(new JLabel("Brightest and faintest image."));
		JPanel panel_brightest_faintest2 = new JPanel();
		panel_brightest_faintest2.setLayout(new BorderLayout());
		panel_brightest_faintest2.add(panel_brightest_faintest, BorderLayout.WEST);

		checkbox_fits = new JCheckBox("Create FITS thumbnail images.");
		checkbox_fits.setSelected(false);
		JPanel panel_fits = new JPanel();
		panel_fits.setLayout(new BorderLayout());
		panel_fits.add(checkbox_fits, BorderLayout.WEST);

		checkbox_past = new JCheckBox("Add past images from database.");
		checkbox_past.setSelected(false);
		checkbox_past.addActionListener(new PastListener());
		JPanel panel_past = new JPanel();
		panel_past.setLayout(new BorderLayout());
		panel_past.add(checkbox_past, BorderLayout.WEST);

		ButtonGroup bg_past = new ButtonGroup();
		radio_past_center = new JRadioButton("", true);
		radio_past_all = new JRadioButton("");
		bg_past.add(radio_past_center);
		bg_past.add(radio_past_all);
		radio_past_center.setEnabled(false);
		radio_past_all.setEnabled(false);

		JPanel panel_past_center = new JPanel();
		panel_past_center.add(new JLabel("    "));
		panel_past_center.add(radio_past_center);
		panel_past_center.add(new JLabel("One image around center."));
		JPanel panel_past_center2 = new JPanel();
		panel_past_center2.setLayout(new BorderLayout());
		panel_past_center2.add(panel_past_center, BorderLayout.WEST);

		JPanel panel_past_all = new JPanel();
		panel_past_all.add(new JLabel("    "));
		panel_past_all.add(radio_past_all);
		panel_past_all.add(new JLabel("All images."));
		JPanel panel_past_all2 = new JPanel();
		panel_past_all2.setLayout(new BorderLayout());
		panel_past_all2.add(panel_past_all, BorderLayout.WEST);

		checkbox_dss = new JCheckBox("Add DSS image.");
		checkbox_dss.setSelected(false);
		JPanel panel_dss = new JPanel();
		panel_dss.setLayout(new BorderLayout());
		panel_dss.add(checkbox_dss, BorderLayout.WEST);

		panel.add(panel_all2);
		panel.add(panel_brightest2);
		panel.add(panel_brightest_faintest2);
		panel.add(panel_fits);
		panel.add(panel_past);
		panel.add(panel_past_center2);
		panel.add(panel_past_all2);
		panel.add(panel_dss);

		components[0] = panel;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "HTML Image Gallery Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("radio-all") != null)
			radio_all.setSelected(((Boolean)default_values.get("radio-all")).booleanValue());
		if (default_values.get("radio-brightest") != null)
			radio_brightest.setSelected(((Boolean)default_values.get("radio-brightest")).booleanValue());
		if (default_values.get("radio-brightest-faintest") != null)
			radio_brightest_faintest.setSelected(((Boolean)default_values.get("radio-brightest-faintest")).booleanValue());

		if (default_values.get("create-fits-image") != null)
			checkbox_fits.setSelected(((Boolean)default_values.get("create-fits-image")).booleanValue());
		if (default_values.get("add-past-image") != null)
			checkbox_past.setSelected(((Boolean)default_values.get("add-past-image")).booleanValue());
		if (default_values.get("radio-past-center") != null)
			radio_past_center.setSelected(((Boolean)default_values.get("radio-past-center")).booleanValue());
		if (default_values.get("radio-past-all") != null)
			radio_past_all.setSelected(((Boolean)default_values.get("radio-past-all")).booleanValue());
		if (default_values.get("add-dss-image") != null)
			checkbox_dss.setSelected(((Boolean)default_values.get("add-dss-image")).booleanValue());
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("radio-all", new Boolean(radio_all.isSelected()));
		default_values.put("radio-brightest", new Boolean(radio_brightest.isSelected()));
		default_values.put("radio-brightest-faintest", new Boolean(radio_brightest_faintest.isSelected()));

		default_values.put("create-fits-image", new Boolean(checkbox_fits.isSelected()));
		default_values.put("add-past-image", new Boolean(checkbox_past.isSelected()));
		default_values.put("radio-past-center", new Boolean(radio_past_center.isSelected()));
		default_values.put("radio-past-all", new Boolean(radio_past_all.isSelected()));
		default_values.put("add-dss-image", new Boolean(checkbox_dss.isSelected()));
	}

	/**
	 * Gets the mode.
	 * @return the mode.
	 */
	public int getMode ( ) {
		if (radio_brightest.isSelected())
			return MODE_BRIGHTEST;
		if (radio_brightest_faintest.isSelected())
			return MODE_BRIGHTEST_FAINTEST;
		return MODE_ALL;
	}

	/**
	 * Returns true when to create FITS thumbnail images.
	 * @return true when to create FITS thumbnail images.
	 */
	public boolean createsFitsImages ( ) {
		return checkbox_fits.isSelected();
	}

	/**
	 * Gets the mode to add past images.
	 * @return the mode to add past images.
	 */
	public int getPastImageMode ( ) {
		if (checkbox_past.isSelected()) {
			if (radio_past_all.isSelected())
				return PAST_MODE_ALL;
			return PAST_MODE_CENTER;
		}

		return PAST_MODE_NONE;
	}

	/**
	 * Returns true when to add a DSS image.
	 * @return true when to add a DSS image.
	 */
	public boolean addsDssImage ( ) {
		return checkbox_dss.isSelected();
	}

	/**
	 * The <code>PastListener</code> is a listener class of check box
	 * selection to add past images.
	 */
	protected class PastListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			radio_past_center.setEnabled(checkbox_past.isSelected());
			radio_past_all.setEnabled(checkbox_past.isSelected());
		}
	}
}
