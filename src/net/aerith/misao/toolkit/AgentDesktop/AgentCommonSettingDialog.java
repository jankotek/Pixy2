/*
 * @(#)AgentCommonSettingDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.AgentDesktop;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;

/**
 * The <code>AgentCommonSettingDialog</code> represents a dialog to 
 * configure the common setting for operation using the agent.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class AgentCommonSettingDialog extends net.aerith.misao.gui.dialog.Dialog {
	/**
	 * The check box to save the batch XML file.
	 */
	protected JCheckBox checkbox_save_batch;

	/**
	 * The text field to input the batch XML file.
	 */
	protected JTextField text_batch_file;

	/**
	 * The button to select batch XML file.
	 */
	protected JButton button_batch_file;

	/**
	 * The check box to search new stars.
	 */
	protected JCheckBox checkbox_new_star_search;

	/**
	 * The check box to save the package file.
	 */
	protected JCheckBox checkbox_save_package;

	/**
	 * The text field to input the package file.
	 */
	protected JTextField text_package_file;

	/**
	 * The button to select package file.
	 */
	protected JButton button_package_file;

	/**
	 * The check box to create HTML image gallery.
	 */
	protected JCheckBox checkbox_create_gallery;

	/**
	 * The text field to input the gallery directory.
	 */
	protected JTextField text_gallery_directory;

	/**
	 * The button to select gallery directory.
	 */
	protected JButton button_gallery_directory;

	/**
	 * The check box to reject single detections.
	 */
	protected JCheckBox checkbox_reject_single;

	/**
	 * The check box to reject identified stars.
	 */
	protected JCheckBox checkbox_reject_identified;

	/**
	 * Constructs an <code>AgentCommonSettingDialog</code>.
	 */
	public AgentCommonSettingDialog ( ) {
		components = new Object[1];

		CheckboxUpdatedListener checkbox_listener = new CheckboxUpdatedListener();

		checkbox_save_batch = new JCheckBox("Save batch XML file.");
		checkbox_save_batch.setSelected(false);
		checkbox_save_batch.addActionListener(checkbox_listener);
		text_batch_file = new JTextField("");
		text_batch_file.setColumns(30);
		button_batch_file = new JButton("Browse");
		button_batch_file.addActionListener(new BrowseBatchListener());

		JPanel panel = new JPanel();
		panel.add(new JLabel("        "));
		panel.add(text_batch_file);
		panel.add(button_batch_file);

		JPanel panel_batch = new JPanel();
		panel_batch.setLayout(new BorderLayout());
		panel_batch.add(checkbox_save_batch, BorderLayout.WEST);
		panel_batch.add(panel, BorderLayout.SOUTH);
		panel_batch.setBorder(new TitledBorder("Examination"));

		checkbox_new_star_search = new JCheckBox("Search new stars.");
		checkbox_new_star_search.setSelected(false);
		checkbox_new_star_search.addActionListener(checkbox_listener);

		checkbox_save_package = new JCheckBox("Save package file.");
		checkbox_save_package.setSelected(false);
		checkbox_save_package.addActionListener(checkbox_listener);
		text_package_file = new JTextField("");
		text_package_file.setColumns(30);
		button_package_file = new JButton("Browse");
		button_package_file.addActionListener(new BrowsePackageListener());

		panel = new JPanel();
		panel.add(new JLabel("        "));
		panel.add(text_package_file);
		panel.add(button_package_file);

		JPanel panel_package = new JPanel();
		panel_package.setLayout(new BorderLayout());
		panel_package.add(checkbox_save_package, BorderLayout.WEST);
		panel_package.add(panel, BorderLayout.SOUTH);

		checkbox_create_gallery = new JCheckBox("Create HTML image gallery.");
		checkbox_create_gallery.setSelected(false);
		checkbox_create_gallery.addActionListener(checkbox_listener);
		text_gallery_directory = new JTextField("");
		text_gallery_directory.setColumns(30);
		button_gallery_directory = new JButton("Browse");
		button_gallery_directory.addActionListener(new BrowseGalleryListener());

		panel = new JPanel();
		panel.add(new JLabel("        "));
		panel.add(text_gallery_directory);
		panel.add(button_gallery_directory);

		JPanel panel_gallery = new JPanel();
		panel_gallery.setLayout(new BorderLayout());
		panel_gallery.add(checkbox_create_gallery, BorderLayout.WEST);
		panel_gallery.add(panel, BorderLayout.SOUTH);

		checkbox_reject_single = new JCheckBox("Reject single detections.");
		checkbox_reject_single.setSelected(false);
		checkbox_reject_single.addActionListener(checkbox_listener);

		JPanel panel_single = new JPanel();
		panel_single.setLayout(new BorderLayout());
		panel_single.add(checkbox_reject_single, BorderLayout.WEST);

		checkbox_reject_identified = new JCheckBox("Reject identified stars.");
		checkbox_reject_identified.setSelected(false);
		checkbox_reject_identified.addActionListener(checkbox_listener);

		JPanel panel_identified = new JPanel();
		panel_identified.setLayout(new BorderLayout());
		panel_identified.add(checkbox_reject_identified, BorderLayout.WEST);

		JPanel panel_new_star_search = new JPanel();
		panel_new_star_search.setLayout(new BoxLayout(panel_new_star_search, BoxLayout.Y_AXIS));
		panel_new_star_search.add(panel_package);
		panel_new_star_search.add(panel_gallery);
		panel_new_star_search.add(panel_single);
		panel_new_star_search.add(panel_identified);

		panel = new JPanel();
		panel.add(new JLabel("        "));
		panel.add(panel_new_star_search);

		JPanel panel_new_star_search2 = new JPanel();
		panel_new_star_search2.setLayout(new BorderLayout());
		panel_new_star_search2.add(checkbox_new_star_search, BorderLayout.WEST);
		panel_new_star_search2.add(panel, BorderLayout.SOUTH);
		panel_new_star_search2.setBorder(new TitledBorder("New Star Search"));

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(panel_batch);
		panel.add(panel_new_star_search2);

		components[0] = panel;

		checkbox_listener.actionPerformed(null);

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Common Setting";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
	}

	/**
	 * Returns true when to save the batch XML file.
	 * @return true when to save the batch XML file.
	 */
	public boolean savesBatchXmlFile ( ) {
		return checkbox_save_batch.isSelected();
	}

	/**
	 * Gets the batch XML file.
	 * @return the batch XML file.
	 */
	public File getBatchXmlFile ( ) {
		return new File(text_batch_file.getText());
	}

	/**
	 * Returns true when to search new stars.
	 * @return true when to search new stars.
	 */
	public boolean searchesNewStars ( ) {
		return checkbox_new_star_search.isSelected();
	}

	/**
	 * Returns true when to save package file.
	 * @return true when to save package file.
	 */
	public boolean savesPackageFile ( ) {
		return checkbox_save_package.isSelected();
	}

	/**
	 * Gets the package file.
	 * @return the package file.
	 */
	public File getPackageFile ( ) {
		return new File(text_package_file.getText());
	}

	/**
	 * Returns true when to create HTML image gallery.
	 * @return true when to create HTML image gallery.
	 */
	public boolean createsHtmlImageGallery ( ) {
		return checkbox_create_gallery.isSelected();
	}

	/**
	 * Gets the directory to create HTML image gallery.
	 * @return the directory to create HTML image gallery.
	 */
	public File getHtmlImageGalleryDirectory ( ) {
		return new File(text_gallery_directory.getText());
	}

	/**
	 * Returns true when to reject single detections.
	 * @return true when to reject single detections.
	 */
	public boolean rejectsSingleDetections ( ) {
		return checkbox_reject_single.isSelected();
	}

	/**
	 * Returns true when to reject identified stars.
	 * @return true when to reject identified stars.
	 */
	public boolean rejectsIdentifiedStars ( ) {
		return checkbox_reject_identified.isSelected();
	}

	/**
	 * The <code>CheckboxUpdatedListener</code> is a listener class 
	 * invoked when a check box is updated.
	 */
	protected class CheckboxUpdatedListener implements ActionListener {
		/**
		 * Invoked when one of the radio button menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			boolean flag = checkbox_save_batch.isSelected();
			text_batch_file.setEnabled(flag);
			button_batch_file.setEnabled(flag);

			flag = checkbox_new_star_search.isSelected();
			checkbox_save_package.setEnabled(flag);
			checkbox_create_gallery.setEnabled(flag);
			checkbox_reject_single.setEnabled(flag);
			checkbox_reject_identified.setEnabled(flag);

			flag = (checkbox_new_star_search.isSelected() & checkbox_save_package.isSelected());
			text_package_file.setEnabled(flag);
			button_package_file.setEnabled(flag);

			flag = (checkbox_new_star_search.isSelected() & checkbox_create_gallery.isSelected());
			text_gallery_directory.setEnabled(flag);
			button_gallery_directory.setEnabled(flag);
		}
	}

	/**
	 * The <code>BrowseBatchListener</code> is a listener class of 
	 * button push to open a file chooser dialog to search catalog 
	 * file or directory.
	 */
	protected class BrowseBatchListener implements ActionListener {
		/**
		 * Invoked when one of the radio button menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			CommonFileChooser file_chooser = new CommonFileChooser();

			if (file_chooser.showOpenDialog(button_batch_file) == JFileChooser.APPROVE_OPTION) {
				File file = file_chooser.getSelectedFile();
				text_batch_file.setText(file.getPath());
			}
		}
	}

	/**
	 * The <code>BrowsePackageListener</code> is a listener class of 
	 * button push to open a file chooser dialog to search catalog 
	 * file or directory.
	 */
	protected class BrowsePackageListener implements ActionListener {
		/**
		 * Invoked when one of the radio button menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			CommonFileChooser file_chooser = new CommonFileChooser();

			if (file_chooser.showOpenDialog(button_package_file) == JFileChooser.APPROVE_OPTION) {
				File file = file_chooser.getSelectedFile();
				text_package_file.setText(file.getPath());
			}
		}
	}

	/**
	 * The <code>BrowseGalleryListener</code> is a listener class of 
	 * button push to open a file chooser dialog to search catalog 
	 * file or directory.
	 */
	protected class BrowseGalleryListener implements ActionListener {
		/**
		 * Invoked when one of the radio button menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			CommonFileChooser file_chooser = new CommonFileChooser();
			file_chooser.setDialogTitle("Choose a directory.");
			file_chooser.setMultiSelectionEnabled(false);
			file_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			if (file_chooser.showOpenDialog(button_gallery_directory) == JFileChooser.APPROVE_OPTION) {
				File file = file_chooser.getSelectedFile();
				text_gallery_directory.setText(file.getPath());
			}
		}
	}
}
