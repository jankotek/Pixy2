/*
 * @(#)FileCopyDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
import java.io.File;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.gui.*;

/**
 * The <code>FileCopyDialog</code> represents a dialog to set the 
 * directory to copy files from.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 August 4
 */

public class FileCopyDialog extends Dialog {
	/**
	 * The panel to select a directory to copy from.
	 */
	protected JPanel panel_from;

	/**
	 * The panel to select a directory to copy to.
	 */
	protected JPanel panel_to;

	/**
	 * The text field to input directory path to copy from.
	 */
	protected JTextField text_path_from;

	/**
	 * The text field to input directory path to copy to.
	 */
	protected JTextField text_path_to;

	/**
	 * The button to browser the directory to copy from.
	 */
	protected JButton button_browse_from;

	/**
	 * The button to browser the directory to copy to.
	 */
	protected JButton button_browse_to;

	/**
	 * Constructs a <code>FileCopyDialog</code>.
	 */
	public FileCopyDialog ( ) {
		components = new Object[2];

		panel_from = new JPanel();
		text_path_from = new JTextField();
		text_path_from.setColumns(30);
		panel_from.add(text_path_from);
		button_browse_from = new JButton("Browse");
		button_browse_from.addActionListener(new BrowseListener(true));
		panel_from.add(button_browse_from);
		panel_from.setBorder(new TitledBorder("Directory to copy from:"));

		panel_to = new JPanel();
		text_path_to = new JTextField();
		text_path_to.setColumns(30);
		panel_to.add(text_path_to);
		button_browse_to = new JButton("Browse");
		button_browse_to.addActionListener(new BrowseListener(false));
		panel_to.add(button_browse_to);
		panel_to.setBorder(new TitledBorder("Directory to copy to:"));

		components[0] = panel_from;
		components[1] = panel_to;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Select Directory";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		if (default_values.get("path-from") != null)
			text_path_from.setText((String)default_values.get("path-from"));
		if (default_values.get("path-to") != null)
			text_path_to.setText((String)default_values.get("path-to"));
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
		default_values.put("path-from", text_path_from.getText());
		default_values.put("path-to", text_path_to.getText());
	}

	/**
	 * Gets the directory path to copy from.
	 * @return the directory path to copy from.
	 */
	public File getDirectoryFrom ( ) {
		return new File(text_path_from.getText());
	}

	/**
	 * Gets the directory path to copy to.
	 * @return the directory path to copy to.
	 */
	public File getDirectoryTo ( ) {
		return new File(text_path_to.getText());
	}

	/**
	 * The <code>BrowseListener</code> is a listener class of button
	 * push to open a file chooser dialog to search directory.
	 */
	protected class BrowseListener implements ActionListener {
		/**
		 * True when to browse the directory to copy from.
		 */
		private boolean copy_from = true;

		/**
		 * Constructs a <code>BrowseListener</code>.
		 * @param f true when to browse the directory to copy from.
		 */
		public BrowseListener ( boolean copy_from ) {
			this.copy_from = copy_from;
		}

		/**
		 * Invoked when one of the radio button menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			CommonFileChooser file_chooser = new CommonFileChooser();
			file_chooser.setDialogTitle("Choose a directory.");
			file_chooser.setMultiSelectionEnabled(false);
			file_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			JPanel panel = panel_from;
			JTextField text = text_path_from;
			if (copy_from == false) {
				panel = panel_to;
				text = text_path_to;
			}

			if (file_chooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
				File file = file_chooser.getSelectedFile();
				text.setText(file.getPath());
			}
		}
	}
}
