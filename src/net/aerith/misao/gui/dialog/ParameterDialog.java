/*
 * @(#)ParameterDialog.java
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
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.*;

/**
 * The <code>ParameterDialog</code> represents a dialog to intput
 * the string parameter.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class ParameterDialog extends Dialog {
	/**
	 * The text field to input the parameter.
	 */
	protected JTextField text;

	/**
	 * The dialog window title.
	 */
	protected String window_title = "Parameter";

	/**
	 * Constructs a <code>ParameterDialog</code>.
	 */
	public ParameterDialog ( ) {
		components = new Object[1];

		text = new JTextField();
		text.setColumns(20);
		components[0] = text;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return window_title;
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get(window_title) != null)
			setParameter((String)default_values.get(window_title));
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put(window_title, getParameter());
	}

	/**
	 * Gets the parameter.
	 * @return the parameter.
	 */
	public String getParameter ( ) {
		return text.getText();
	}

	/**
	 * Sets the parameter.
	 * @param string the parameter.
	 */
	public void setParameter ( String string ) {
		text.setText(string);
	}

	/**
	 * Sets the window title.
	 * @param title the title.
	 */
	public void setWindowTitle ( String title ) {
		window_title = title;
	}

	/**
	 * Sets the border title.
	 * @param title the title.
	 */
	public void setBorderTitle ( String title ) {
		text.setBorder(new TitledBorder(title));
	}
}
