/*
 * @(#)PhotometryCatalogSettingDialog.java
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
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.star.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.gui.*;

/**
 * The <code>PhotometryCatalogSettingDialog</code> represents a dialog
 * to select the method of photometry and the reference catalog.
 * <p>
 * Note that the catalog for photometry can be selected from the star
 * classes identified in the specified XML report document.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class PhotometryCatalogSettingDialog extends Dialog {
	/**
	 * The tab.
	 */
	protected JTabbedPane tab;

	/**
	 * The panel for regular photometry.
	 */
	protected RegularPhotometrySettingPanel regular_panel;

	/**
	 * The panel for simple photometry.
	 */
	protected SimplePhotometrySettingPanel simple_panel;

	/**
	 * Constructs a <code>PhotometryCatalogSettingDialog</code>.
	 */
	public PhotometryCatalogSettingDialog ( ) {
		regular_panel = new RegularPhotometrySettingPanel();
		simple_panel = new SimplePhotometrySettingPanel();

		initialize();
	}

	/**
	 * Constructs a <code>PhotometryCatalogSettingDialog</code>.
	 * @param report the XML report document
	 */
	public PhotometryCatalogSettingDialog ( XmlReport report ) {
		regular_panel = new RegularPhotometrySettingPanel(report);
		simple_panel = new SimplePhotometrySettingPanel(report);

		initialize();
	}

	/**
	 * Initializes.
	 */
	protected void initialize ( ) {
		components = new Object[1];

		JPanel simple_panel2 = new JPanel();
		simple_panel2.setLayout(new BorderLayout());
		simple_panel2.add(simple_panel, BorderLayout.NORTH);
		simple_panel2.add(new JLabel(""), BorderLayout.CENTER);

		tab = new JTabbedPane();
		tab.addTab("Regular Photometry", regular_panel);
		tab.addTab("Simple Comparison", simple_panel2);

		components[0] = tab;
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Photometry Catalog Setting";
	}

	/**
	 * Gets the setting.
	 * @return the setting.
	 */
	public PhotometrySetting getPhotometrySetting ( ) {
		PhotometrySetting setting = null;

		if (tab.getSelectedIndex() == 0) {
			setting = regular_panel.getPhotometrySetting();
		} else {
			setting = simple_panel.getPhotometrySetting();
		}

		return setting;
	}
}
