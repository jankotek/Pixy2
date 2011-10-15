/*
 * @(#)InformationDBUpdateSettingDialog.java
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
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;

/**
 * The <code>InformationDBUpdateSettingDialog</code> represents a 
 * dialog to set up parameters to update XML report documents in the 
 * database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class InformationDBUpdateSettingDialog extends Dialog {
	/**
	 * The mode.
	 */
	protected int mode = MODE_REPLACE;

	/**
	 * The mode number which implies to replace.
	 */
	public final static int MODE_REPLACE = 0;

	/**
	 * The mode number which implies to delete.
	 */
	public final static int MODE_DELETE = 1;

	/**
	 * The check box to update already reported magnitude.
	 */
	protected JCheckBox checkbox_update_reported_mag;

	/**
	 * Constructs an <code>InformationDBUpdateSettingDialog</code>.
	 * @param mode the mode.
	 */
	public InformationDBUpdateSettingDialog ( int mode ) {
		this.mode = mode;

		components = new Object[1];

		String s = "Replace";
		if (mode == MODE_DELETE)
			s = "Delete";

		checkbox_update_reported_mag = new JCheckBox(s + " magnitude even if already reported.");

		components[0] = checkbox_update_reported_mag;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Replace/Delete XML Report Document Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("update-reported-mag") != null)
			checkbox_update_reported_mag.setSelected(((Boolean)default_values.get("update-reported-mag")).booleanValue());
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("update-reported-mag", new Boolean(checkbox_update_reported_mag.isSelected()));
	}

	/**
	 * Returns true when to update already reported magnitude.
	 * @return true when to update already reported magnitude.
	 */
	public boolean updatesReportedMagnitude ( ) {
		return checkbox_update_reported_mag.isSelected();
	}
}
