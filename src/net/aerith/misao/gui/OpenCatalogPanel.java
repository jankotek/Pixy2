/*
 * @(#)OpenCatalogPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;
import java.util.Vector;
import java.util.Hashtable;
import net.aerith.misao.util.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.pixy.Resource;
import net.aerith.misao.gui.dialog.CommonFileChooser;

/**
 * The <code>OpenCatalogPanel</code> represents a panel which consists
 * of components to open a catalog file or directory.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 October 5
 */

public class OpenCatalogPanel extends JPanel {
	/**
	 * The default values.
	 */
	protected static Hashtable default_values = new Hashtable();

	/**
	 * The button to show the help message of the selected catalog.
	 */
	protected JButton button_catalog_help;

	/**
	 * The combo box to select a catalog.
	 */
	protected JComboBox combo_catalog;

	/**
	 * The text field to input catalog path.
	 */
	protected JTextField text_path;

	/**
	 * The button to browser.
	 */
	protected JButton button_browse;

	/**
	 * True when to set the default path when a catalog is selected.
	 */
	protected boolean default_path = false;

	/**
	 * The pane of this component.
	 */
	protected Container pane;

	/**
	 * The identifier of this panel.
	 */
	protected int id = -1;

	/**
	 * Constructs a <code>OpenCatalogPanel</code>.
	 * @param catalog_list the list of catalog readers.
	 */
	public OpenCatalogPanel ( Vector catalog_list ) {
		this(catalog_list, -1);
	}

	/**
	 * Constructs a <code>OpenCatalogPanel</code>.
	 * @param catalog_list the list of catalog readers.
	 * @param id           the identifier of this panel.
	 */
	public OpenCatalogPanel ( Vector catalog_list, int id ) {
		this.id = id;

		pane = this;

		CatalogSelectionComboBoxModel model = new CatalogSelectionComboBoxModel();
		for (int i = 0 ; i < catalog_list.size() ; i++) {
			CatalogReader reader = (CatalogReader)catalog_list.elementAt(i);
			model.addCatalogReader(reader);
		}

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		button_catalog_help = new JButton("Help");
		combo_catalog = new JComboBox(model);
		combo_catalog.setSelectedIndex(0);
		combo_catalog.addActionListener(new CatalogSelectionListener());
		panel.add(combo_catalog);
		button_catalog_help.addActionListener(new CatalogHelpListener());
		panel.add(button_catalog_help);
		panel.setBorder(new TitledBorder("Catalog"));
		add(panel);

		String message = getSelectedCatalogReader().getHelpMessage();
		if (message == null) {
			button_catalog_help.setEnabled(false);
		} else {
			button_catalog_help.setEnabled(true);
		}

		panel = new JPanel();
		text_path = new JTextField();
		text_path.setColumns(30);
		panel.add(text_path);
		button_browse = new JButton("Browse");
		button_browse.addActionListener(new BrowseListener());
		panel.add(button_browse);

		JPanel panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
		panel2.add(panel);
		panel2.add(new JLabel("Several paths can be described separating with path separator " + File.pathSeparator));
		panel2.setBorder(new TitledBorder("Path"));
		add(panel2);

		setDefaultValues();
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
		String catalog = "catalog";
		String path = "path";
		if (id >= 0) {
			catalog = "catalog" + id;
			path = "path" + id;
		}

		if (default_values.get(catalog) != null)
			selectCatalogReader((CatalogReader)default_values.get(catalog));
		if (default_values.get(path) != null)
			setCatalogPath((String)default_values.get(path));
	}

	/**
	 * Saves the default values.
	 */
	public void saveDefaultValues ( ) {
		String catalog = "catalog";
		String path = "path";
		if (id >= 0) {
			catalog = "catalog" + id;
			path = "path" + id;
		}

		default_values.put(catalog, getSelectedCatalogReader());
		default_values.put(path, getCatalogPath());
	}

	/**
	 * Sets whether or not this component is enabled.
	 * @param enabled true when to enable this panel.
	 */
	public void setEnabled ( boolean enabled ) {
		super.setEnabled(enabled);

		button_catalog_help.setEnabled(enabled);
		combo_catalog.setEnabled(enabled);
		text_path.setEnabled(enabled);
		button_browse.setEnabled(enabled);
	}

	/**
	 * Adds a catalog reader.
	 * @param reader the catalog reader to add to the combo box.
	 */
	public void addCatalogReader ( CatalogReader reader ) {
		CatalogSelectionComboBoxModel model = (CatalogSelectionComboBoxModel)combo_catalog.getModel();
		model.addCatalogReader(reader);
	}

	/**
	 * Gets the selected catalog reader.
	 * @return the selected catalog reader.
	 */
	public CatalogReader getSelectedCatalogReader ( ) {
		CatalogSelectionComboBoxModel model = (CatalogSelectionComboBoxModel)combo_catalog.getModel();
		return model.getCatalogReaderAt(combo_catalog.getSelectedIndex());
	}

	/**
	 * Selects a catalog reader in the combo box.
	 * @param reader the catalog reader to select.
	 */
	public void selectCatalogReader ( CatalogReader reader ) {
		CatalogSelectionComboBoxModel model = (CatalogSelectionComboBoxModel)combo_catalog.getModel();
		combo_catalog.setSelectedIndex(model.getIndexOf(reader));
	}

	/**
	 * Gets the catalog path.
	 * @return the catalog path.
	 */
	public String getCatalogPath ( ) {
		return text_path.getText();
	}

	/**
	 * Sets the catalog path.
	 * @param path the catalog path.
	 */
	public void setCatalogPath ( String path ) {
		 text_path.setText(path);
	}

	/**
	 * Sets the flag to set the default path when a catalog is 
	 * selected.
	 */
	public void enableDefaultPath ( ) {
		default_path = true;

		setCatalogPath(Resource.getDefaultCatalogPath(getSelectedCatalogReader().getName()));
	}

	/**
	 * The <code>CatalogSelectionListener</code> is a listener class
	 * of item selection in combo box to select the catalog reader.
	 */
	protected class CatalogSelectionListener implements ActionListener {
		/**
		 * Invoked when one of the radio button menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			String message = getSelectedCatalogReader().getHelpMessage();
			if (message == null) {
				button_catalog_help.setEnabled(false);
			} else {
				button_catalog_help.setEnabled(true);
			}

			if (getSelectedCatalogReader().isFile() == false  &&
				getSelectedCatalogReader().isInDirectory() == false) {
				text_path.setEnabled(false);
				text_path.setEditable(false);
				button_browse.setEnabled(false);
			} else {
				text_path.setEnabled(true);
				text_path.setEditable(true);
				button_browse.setEnabled(true);
			}

			if (default_path)
				setCatalogPath(Resource.getDefaultCatalogPath(getSelectedCatalogReader().getName()));
		}
	}

	/**
	 * The <code>CatalogHelpListener</code> is a listener class of 
	 * button push to show the help message of the selected catalog.
	 */
	protected class CatalogHelpListener implements ActionListener {
		/**
		 * Invoked when the button is pushed.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			String message = getSelectedCatalogReader().getHelpMessage();
			if (message != null) {
				JLabel label = new JLabel(message);
				label.setSize(400,300);
				JOptionPane.showMessageDialog(pane, label);
			}
		}
	}

	/**
	 * The <code>BrowseListener</code> is a listener class of button
	 * push to open a file chooser dialog to search catalog file or
	 * directory.
	 */
	protected class BrowseListener implements ActionListener {
		/**
		 * Invoked when one of the radio button menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			CatalogReader reader = getSelectedCatalogReader();

			CommonFileChooser file_chooser = new CommonFileChooser();
			if (reader.isInDirectory()) {
				file_chooser.setDialogTitle("Choose a directory.");
				file_chooser.setMultiSelectionEnabled(false);
				file_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			}

			if (file_chooser.showOpenDialog(pane) == JFileChooser.APPROVE_OPTION) {
				File file = file_chooser.getSelectedFile();
				text_path.setText(file.getPath());
			}
		}
	}

	/**
	 * The <code>CatalogSelectionComboBoxModel</code> is a model of 
	 * the combo box to select a catalog.
	 */
	protected class CatalogSelectionComboBoxModel extends DefaultListModel implements ComboBoxModel {
		/**
		 * The list of catalog readers to be selected.
		 */
		private Vector catalog_list;

		/**
		 * The selected item.
		 */
		private Object selected_item;

		/**
		 * Constructs an empty <code>CatalogSelectionComboBoxModel</code>.
		 */
		public CatalogSelectionComboBoxModel ( ) {
			catalog_list = new Vector();
		}

		/**
		 * Gets the selected item.
		 * @return the selected item.
		 */
		public Object getSelectedItem ( ) {
			return selected_item;
		}

		/**
		 * Sets the item to be selected.
		 * @param item the item to be selected.
		 */
		public void setSelectedItem ( Object item ) {
			selected_item = item;
			fireContentsChanged(this, -1, -1);
		}

		/**
		 * Adds a catalog reader.
		 * @param reader a catalog reader to add to this combo box.
		 */
		public void addCatalogReader ( CatalogReader reader ) {
			catalog_list.addElement(reader);
			addElement(reader.getName());
		}

		/**
		 * Gets the catalog reader at the specified location.
		 * @param index the location of the catalog reader to get.
		 * @return the catalog reader at the specified location.
		 */
		public CatalogReader getCatalogReaderAt ( int index ) {
			return (CatalogReader)catalog_list.elementAt(index);
		}

		/**
		 * Gets the location of the specified catalog reader.
		 * @param reader a catalog reader to get the location.
		 * @return the location of the specified catalog reader.
		 */
		public int getIndexOf ( CatalogReader reader ) {
			for (int i = 0 ; i < catalog_list.size() ; i++) {
				if (((CatalogReader)catalog_list.elementAt(i)).getName().equals(reader.getName()))
					return i;
			}
			return 0;
		}
	}
}
