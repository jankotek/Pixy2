/*
 * @(#)VariabilityDesktop.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.io.*;
import net.aerith.misao.image.filter.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.util.*;
import net.aerith.misao.pixy.*;
import net.aerith.misao.io.*;
import net.aerith.misao.io.filechooser.*;
import net.aerith.misao.database.*;
import net.aerith.misao.xml.*;

/**
 * The <code>VariabilityDesktop</code> represents a desktop with a
 * function to import the package of variability records.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class VariabilityDesktop extends BaseDesktop {
	/**
	 * Constructs a <code>VariabilityDesktop</code>.
	 */
	public VariabilityDesktop ( ) {
	}

	/**
	 * Initializes menu bar. A <code>JMenuBar</code> must be set to 
	 * this <code>JFrame</code> previously.
	 */
	public void initMenu ( ) {
		JMenu menu = addMenu("File");
		if (menu.getItemCount() > 0)
			menu.addSeparator();

		JMenuItem item = new JMenuItem("Import Package");
		item.addActionListener(new ImportListener());
		menu.add(item);

		super.initMenu();
	}

	/**
	 * The <code>ImportListener</code> is a listener class of menu 
	 * selection to import the package.
	 */
	protected class ImportListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			try {
				CommonFileChooser file_chooser = new CommonFileChooser();
				file_chooser.setDialogTitle("Import the package.");
				file_chooser.setMultiSelectionEnabled(false);
				file_chooser.addChoosableFileFilter(new XmlFilter());
				file_chooser.setSelectedFile(new File("package.xml"));

				if (file_chooser.showOpenDialog(pane) == JFileChooser.APPROVE_OPTION) {
					File file = file_chooser.getSelectedFile();

					// Changes the home directory.
					File home_directory = file.getParentFile();
					File database_directory = new File(FileManager.unitePath(file.getParent(), net.aerith.misao.pixy.Properties.getDatabaseDirectoryName()));
					DiskFileSystem file_system = new DiskFileSystem(database_directory);
					setDBManager(new GlobalDBManager(file_system));
					setFileManager(new FileManager(home_directory));

					XmlVariabilityHolder holder = new XmlVariabilityHolder();
					holder.read(file);

					XmlVariability[] xml_records = (XmlVariability[])holder.getVariability();
					Variability[] records = new Variability[xml_records.length];
					for (int i = 0 ; i < xml_records.length ; i++)
						records[i] = xml_records[i].getVariability();

					showVariabilityTable(records);
				}
			} catch ( Exception exception ) {
				String message = "Failed.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
