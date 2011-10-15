/*
 * @(#)OutputDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ImageConversion;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.dialog.Dialog;
import net.aerith.misao.gui.dialog.CommonFileChooser;

/**
 * The <code>OutputDialog</code> represents a dialog to select the 
 * directory to create converted images into.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class OutputDialog extends Dialog {
	/**
	 * The text field to input catalog path.
	 */
	protected JTextField text_path;

	/**
	 * The button to browser.
	 */
	protected JButton button_browse;

	/**
	 * The check box to create index.html.
	 */
	protected JCheckBox checkbox_create_index;

	/**
	 * The parent pane.
	 */
	protected Container parent_pane;

	/**
	 * Constructs a <code>OutputDialog</code>.
	 */
	public OutputDialog ( ) {
		components = new Object[2];

		JPanel panel = new JPanel();
		text_path = new JTextField();
		text_path.setColumns(30);
		panel.add(text_path);
		button_browse = new JButton("Browse");
		button_browse.addActionListener(new BrowseListener());
		panel.add(button_browse);
		panel.setBorder(new TitledBorder("Directory to create converted images"));
		components[0] = panel;

		checkbox_create_index = new JCheckBox("Create index.html.");
		checkbox_create_index.setSelected(true);
		components[1] = checkbox_create_index;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Output Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("path") != null)
			text_path.setText((String)default_values.get("path"));
		if (default_values.get("index") != null)
			checkbox_create_index.setSelected(((Boolean)default_values.get("index")).booleanValue());
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("path", text_path.getText());
		default_values.put("index", new Boolean(checkbox_create_index.isSelected()));
	}

	/**
	 * Shows this dialog.
	 * @param pane the parent pane of this dialog.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int show ( Container pane ) {
		parent_pane = pane;

		return super.show(pane);
	}

	/**
	 * Returns true when to create index.html.
	 * @return true when to create index.html.
	 */
	public boolean createsIndex ( ) {
		return checkbox_create_index.isSelected();
	}

	/**
	 * Gets the directory path.
	 * @return the directory path.
	 */
	public String getPath ( ) {
		return text_path.getText();
	}

	/**
	 * The <code>BrowseListener</code> is a listener class of button
	 * push to open a file chooser dialog to select a directory.
	 */
	protected class BrowseListener implements ActionListener {
		/**
		 * Invoked when one of the radio button menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			CommonFileChooser file_chooser = new CommonFileChooser();
			file_chooser.setDialogTitle("Choose a directory.");
			file_chooser.setMultiSelectionEnabled(false);
			file_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			if (file_chooser.showOpenDialog(parent_pane) == JFileChooser.APPROVE_OPTION) {
				File file = file_chooser.getSelectedFile();
				text_path.setText(file.getPath());
			}
		}
	}
}
