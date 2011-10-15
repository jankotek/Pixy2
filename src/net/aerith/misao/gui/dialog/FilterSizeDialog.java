/*
 * @(#)FilterSizeDialog.java
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
 * The <code>FilterSizeDialog</code> represents a dialog to intput the
 * filter size.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 November 26
 */

public class FilterSizeDialog extends Dialog {
	/**
	 * The text field to input the filter size.
	 */
	protected JTextField text_filter_size;

	/**
	 * The title of the dialog.
	 */
	protected String title = "";

	/**
	 * Constructs a <code>FilterSizeDialog</code>.
	 * @param title the title of the dialog.
	 */
	public FilterSizeDialog ( String title ) {
		this.title = title;

		components = new Object[1];

		JPanel panel = new JPanel();
		text_filter_size = new JTextField("1");
		text_filter_size.setColumns(5);
		panel.add(text_filter_size);
		panel.setBorder(new TitledBorder("Filter size"));
		components[0] = panel;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return title;
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("filter") != null)
			setFilterSize(((Integer)default_values.get("filter")).intValue());
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("filter", new Integer(getFilterSize()));
	}

	/**
	 * Gets the filter size.
	 * @return the filter size.
	 */
	public int getFilterSize ( ) {
		return Integer.parseInt(text_filter_size.getText());
	}

	/**
	 * Sets the filter size.
	 * @param size the filter size.
	 */
	public void setFilterSize ( int size ) {
		text_filter_size.setText("" + size);
	}
}
