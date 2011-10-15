/*
 * @(#)PhotometryCalibrationInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.PhotometryCalibration;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.toolkit.Photometry.PhotometryPane;

/**
 * The <code>PhotometryCalibrationInternalFrame</code> represents a 
 * frame to select XML report documents for photometry calibration.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class PhotometryCalibrationInternalFrame extends BaseInternalFrame implements ReportDocumentUpdatedListener {
	/*
	 * The parent desktop.
	 */
	protected BaseDesktop desktop;

	/**
	 * The table.
	 */
	protected InformationTable table;

	/*
	 * The operation to select the method for photometry and read 
	 * data of the selected catalog from the XML files.
	 */
	protected ReadingOperation read_operation;

	/*
	 * The operation to update the magnitude and save the XML files.
	 */
	protected WritingOperation write_operation;

	/*
	 * The control panel.
	 */
	protected PhotometryCalibrationControlPanel control_panel;

	/**
	 * This frame.
	 */
	protected PhotometryCalibrationInternalFrame frame;

	/**
	 * The set of monitors.
	 */
	protected MonitorSet monitor_set = new MonitorSet();

	/**
	 * The dummy XML report document which contains the pairs of 
	 * detected stars and catalog data used in photometry read from 
	 * all XML report document files.
	 */
	protected XmlReport global_report = null;

	/**
	 * Constructs a <code>PhotometryCalibrationInternalFrame</code>.
	 * @param desktop the parent desktop.
	 */
	public PhotometryCalibrationInternalFrame ( BaseDesktop desktop ) {
		this.desktop = desktop;

		pane = getContentPane();

		table = new InformationTable();
		read_operation = new ReadingOperation(table, this);
		write_operation = new WritingOperation(table, this);
		control_panel = new PhotometryCalibrationControlPanel(read_operation, table);

		try {
			read_operation.setDBManager(desktop.getDBManager());
			write_operation.setDBManager(desktop.getDBManager());
		} catch ( IOException exception ) {
			// Makes no problem.
		}
		read_operation.setFileManager(desktop.getFileManager());
		write_operation.setFileManager(desktop.getFileManager());

		pane.setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 1));
		panel.add(control_panel);
		panel.add(new JLabel("Drag XML files and drop on the table."));

		pane.add(panel, BorderLayout.NORTH);
		pane.add(new JScrollPane(table), BorderLayout.CENTER);

		frame = this;

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

		read_operation.addMonitor(monitor_set);
		write_operation.addMonitor(monitor_set);
	}

	/**
	 * Starts the operation.
	 */
	public void startOperation ( ) {
		control_panel.start();
	}

	/**
	 * Gets the global XML report document.
	 * @return the global XML report document.
	 */
	protected XmlReport getGlobalReport ( ) {
		return global_report;
	}

	/**
	 * Sets the global XML report document.
	 * @param report the global XML report document.
	 */
	protected void setGlobalReport ( XmlReport report ) {
		global_report = report;
	}

	/**
	 * Invoked when the measured magnitude of the detected stars are
	 * updated.
	 * @param report the XML report document.
	 */
	public void photometryUpdated ( XmlReport report ) {
		global_report = report;

		control_panel.setOperation(write_operation);

		if (control_panel.start() == false)
			control_panel.setOperation(read_operation);
	}

	/**
	 * Invoked when the reading operation is completed.
	 * @param setting the photometry setting.
	 */
	protected void readingOperationCompleted ( PhotometrySetting setting ) {
		JInternalFrame photometry_frame = new JInternalFrame();
		photometry_frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		photometry_frame.setSize(700,500);
		photometry_frame.setTitle("Photometry Table");
		photometry_frame.setVisible(true);
		photometry_frame.setMaximizable(true);
		photometry_frame.setIconifiable(true);
		photometry_frame.setResizable(true);
		photometry_frame.setClosable(true);
		PhotometryPane photometry_pane = new PhotometryPane(global_report, setting);
		photometry_frame.getContentPane().add(photometry_pane);
		desktop.addFrame(photometry_frame);

		photometry_pane.addReportDocumentUpdatedListener(this);
	}

	/**
	 * Invoked when the writing operation is completed.
	 */
	protected void writingOperationCompleted ( ) {
		control_panel.setOperation(read_operation);
	}

	/**
	 * Invoked when the measured position of the detected stars are
	 * updated.
	 * @param report the XML report document.
	 */
	public void astrometryUpdated ( XmlReport report ) {
	}

	/**
	 * Invoked when some stars are added, removed or replaced.
	 * @param report the XML report document.
	 */
	public void starsUpdated ( XmlReport report ) {
	}

	/**
	 * Invoked when the image date is updated.
	 * @param report the XML report document.
	 */
	public void dateUpdated ( XmlReport report ) {
	}

	/**
	 * Invoked when a secondary record, like instruments, is updated.
	 * @param report the XML report document.
	 */
	public void recordUpdated ( XmlReport report ) {
	}
}
