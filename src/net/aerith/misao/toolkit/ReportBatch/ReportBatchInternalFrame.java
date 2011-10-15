/*
 * @(#)ReportBatchInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ReportBatch;
import net.aerith.misao.gui.Desktop;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Vector;
import javax.swing.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.io.*;
import net.aerith.misao.io.filechooser.*;
import net.aerith.misao.util.Monitor;
import net.aerith.misao.util.MonitorSet;

/**
 * The <code>ReportBatchInternalFrame</code> represents a frame to 
 * select XML report document files for batch operation.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class ReportBatchInternalFrame extends BaseInternalFrame {
	/*
	 * The menu item to identify.
	 */
	protected JRadioButtonMenuItem menu_identify;

	/*
	 * The menu item to identify from the catalog database.
	 */
	protected JRadioButtonMenuItem menu_identify_database;

	/*
	 * The menu item to operate the photometry.
	 */
	protected JRadioButtonMenuItem menu_photometry;

	/*
	 * The menu item to register to the database.
	 */
	protected JRadioButtonMenuItem menu_register_db;

	/*
	 * The menu item to delete from the database.
	 */
	protected JRadioButtonMenuItem menu_delete_db;

	/**
	 * The set of monitors.
	 */
	protected MonitorSet monitor_set = new MonitorSet();

	/**
	 * The parent desktop.
	 */
	protected Desktop desktop;

	/*
	 * The table.
	 */
	protected InformationTable table;

	/*
	 * The operation.
	 */
	 protected ReportBatchOperation operation;

	/*
	 * The control panel.
	 */
	protected ReportBatchControlPanel control_panel;

	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>ReportBatchInternalFrame</code>.
	 * @param desktop the desktop.
	 */
	public ReportBatchInternalFrame ( Desktop desktop ) {
		this.desktop = desktop;

		pane = getContentPane();

		table = new InformationTable();
		operation = new IdentificationOperation(table);
		control_panel = new ReportBatchControlPanel(operation, table, this);

		pane.setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 1));
		panel.add(control_panel);
		panel.add(new JLabel("Drag XML files and drop on the table."));

		pane.add(panel, BorderLayout.NORTH);
		pane.add(new JScrollPane(table), BorderLayout.CENTER);

		initMenu();
	}

	/**
	 * Initializes this window. This is invoked at construction.
	 */
	public void initialize ( ) {
		// Never invoke initMenu() here. It must be invoked after the 
		// control panel is created in the constructor of this class.
//		initMenu();
	}

	/**
	 * Initializes menu bar. A <code>JMenuBar</code> must be set to 
	 * this <code>JFrame</code> previously.
	 */
	public void initMenu ( ) {
		super.initMenu();

		addOperationMenu();
	}

	/**
	 * Adds the <tt>Operaton</tt> menus to the menu bar.
	 */
	public void addOperationMenu ( ) {
		JMenu menu = addMenu("Operation");

		ButtonGroup bg = new ButtonGroup();
		OperationSelectionListener listener = new OperationSelectionListener();

		menu_identify = new JRadioButtonMenuItem("Identify");
		menu_identify.addActionListener(listener);
		menu_identify.setSelected(true);
		menu.add(menu_identify);
		bg.add(menu_identify);

		menu_identify_database = new JRadioButtonMenuItem("Identify from Database");
		menu_identify_database.addActionListener(listener);
		menu_identify_database.setSelected(false);
		menu.add(menu_identify_database);
		bg.add(menu_identify_database);

		menu_photometry = new JRadioButtonMenuItem("Photometry");
		menu_photometry.addActionListener(listener);
		menu_photometry.setSelected(false);
		menu.add(menu_photometry);
		bg.add(menu_photometry);

		menu_register_db = new JRadioButtonMenuItem("Register to Database");
		menu_register_db.addActionListener(listener);
		menu_register_db.setSelected(false);
		menu.add(menu_register_db);
		bg.add(menu_register_db);

		menu_delete_db = new JRadioButtonMenuItem("Delete from Database");
		menu_delete_db.addActionListener(listener);
		menu_delete_db.setSelected(false);
		menu.add(menu_delete_db);
		bg.add(menu_delete_db);

		menu.addSeparator();

		JMenuItem[] menu_items = control_panel.getMenuItems();
		for (int i = 0 ; i < menu_items.length ; i++)
			menu.add(menu_items[i]);
	}

	/**
	 * Adds a monitor.
	 * @param monitor the monitor.
	 */
	public void addMonitor ( Monitor monitor ) {
		monitor_set.addMonitor(monitor);

		operation.addMonitor(monitor_set);
	}

	/**
	 * Enables/disables operation selection menus.
	 * @param flag true when to enable.
	 */
	public void enableOperationSelection ( boolean flag ) {
		menu_identify.setEnabled(flag);
		menu_identify_database.setEnabled(flag);
		menu_photometry.setEnabled(flag);
		menu_register_db.setEnabled(flag);
		menu_delete_db.setEnabled(flag);
	}

	/**
	 * The <code>OperationSelectionListener</code> is a listener class
	 * of menu selection to change the operation.
	 */
	protected class OperationSelectionListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (menu_identify.isSelected())
				operation = new IdentificationOperation(table);
			if (menu_identify_database.isSelected())
				operation = new DBIdentificationOperation(table);
			if (menu_photometry.isSelected())
				operation = new PhotometryOperation(table);
			if (menu_register_db.isSelected())
				operation = new DBRegistrationOperation(table);
			if (menu_delete_db.isSelected())
				operation = new DBDeletionOperation(table);
			control_panel.setOperation(operation);

			try {
				operation.setDBManager(desktop.getDBManager());
			} catch ( IOException exception ) {
				// Makes no problem.
			}
			operation.setFileManager(desktop.getFileManager());

			operation.addMonitor(monitor_set);
		}
	}
}
