/*
 * @(#)IdentificationSettingDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.gui.*;

/**
 * The <code>IdentificationSettingDialog</code> represents a dialog to
 * set up parameters for identification.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class IdentificationSettingDialog extends Dialog {
	/**
	 * The check box to ignore negative data.
	 */
	protected JCheckBox checkbox_ignore_negative;

	/**
	 * Constructs an <code>IdentificationSettingDialog</code>. 
	 */
	public IdentificationSettingDialog ( ) {
		components = new Object[1];

		checkbox_ignore_negative = new JCheckBox("Ignore negative data.");
		components[0] = checkbox_ignore_negative;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Identification Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("ignore-negative") != null)
			setNegativeDataIgnored(((Boolean)default_values.get("ignore-negative")).booleanValue());
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("ignore-negative", new Boolean(isNegativeDataIgnored()));
	}

	/**
	 * Returns true if the negative data must be ignored.
	 * @return true if the negative data must be ignored.
	 */
	public boolean isNegativeDataIgnored ( ) {
		return checkbox_ignore_negative.isSelected();
	}

	/**
	 * Sets the flag to ignore the negative data if true specified.
	 * @param f if true, the negative data is ignored.
	 */
	public void setNegativeDataIgnored ( boolean f ) {
		checkbox_ignore_negative.setSelected(f);
	}
}
