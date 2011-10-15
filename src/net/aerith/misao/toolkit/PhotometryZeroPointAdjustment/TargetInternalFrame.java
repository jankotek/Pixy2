/*
 * @(#)TargetInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.PhotometryZeroPointAdjustment;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.io.*;
import net.aerith.misao.io.filechooser.*;
import net.aerith.misao.util.*;
import net.aerith.misao.database.GlobalDBManager;
import net.aerith.misao.io.FileManager;
import net.aerith.misao.xml.XmlInformation;

/**
 * The <code>TargetInternalFrame</code> represents a frame to select 
 * target XML report documents to adjust zero-point of photometry.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 July 15
 */

public class TargetInternalFrame extends BaseInternalFrame {
	/*
	 * The parent desktop.
	 */
	protected PhotometryZeroPointAdjustmentDesktop desktop;

	/*
	 * The table.
	 */
	protected InformationTable table;

	/*
	 * The operation.
	 */
	protected PhotometryZeroPointAdjustmentOperation operation;

	/*
	 * The control panel.
	 */
	protected XmlReportControlPanel control_panel;

	/**
	 * The set of monitors.
	 */
	protected MonitorSet monitor_set = new MonitorSet();

	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>TargetInternalFrame</code>.
	 * @param desktop the desktop.
	 */
	public TargetInternalFrame ( PhotometryZeroPointAdjustmentDesktop desktop ) {
		this.desktop = desktop;

		pane = getContentPane();

		table = new InformationTable();
		operation = new PhotometryZeroPointAdjustmentOperation(table, desktop);
		control_panel = new XmlReportControlPanel(operation, table);

		try {
			operation.setDBManager(desktop.getDBManager());
		} catch ( IOException exception ) {
			// Makes no problem.
		}
		operation.setFileManager(desktop.getFileManager());

		operation.addObserver(new PhotometryZeroPointAdjustmentObserver());

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
	 * Adds a monitor.
	 * @param monitor the monitor.
	 */
	public void addMonitor ( Monitor monitor ) {
		monitor_set.addMonitor(monitor);

		operation.addMonitor(monitor_set);
	}

	/**
	 * Adds XML information documents. The XML file path must be 
	 * recorded in the information documents.
	 * @param infos the XML information documents.
	 * @exception IOException if I/O error occurs.
	 * @exception FileNotFoundException if a file does not exists.
	 */
	public void addInformations ( XmlInformation[] infos )
		throws IOException, FileNotFoundException
	{
		table.addInformations(infos, desktop.getFileManager());
	}

	/**
	 * The <code>PhotometryZeroPointAdjustmentObserver</code> is an 
	 * observer of an operation to adjust zero-point of photometry.
	 */
	protected class PhotometryZeroPointAdjustmentObserver implements OperationObserver {
		/**
		 * The list of succeeded targets.
		 */
		private ArrayList list_succeeded;

		/**
		 * The list of failed targets.
		 */
		private ArrayList list_failed;

		/**
		 * Invoked when the operation starts.
		 */
		public void notifyStart ( ) {
			list_succeeded = new ArrayList();
			list_failed = new ArrayList();
		}

		/**
		 * Invoked when the operation ends.
		 * @param exception the exception if an error occurs, or null if
		 * succeeded.
		 */
		public void notifyEnd ( Exception exception ) {
			if (list_succeeded.size() > 0) {
				XmlInformation[] info_ref = new XmlInformation[list_succeeded.size()];
				info_ref = ((XmlInformation[])list_succeeded.toArray(info_ref));
				XmlInformation[] info_target = new XmlInformation[list_failed.size()];
				info_target = ((XmlInformation[])list_failed.toArray(info_target));
				desktop.createNewFrames(info_ref, info_target);
			}
		}

		/**
		 * Invoked when a task is succeeded.
		 * @param arg the argument.
		 */
		public void notifySucceeded ( Object arg ) {
			list_succeeded.add(arg);
		}

		/**
		 * Invoked when a task is failed.
		 * @param arg the argument.
		 */
		public void notifyFailed ( Object arg ) {
			list_failed.add(arg);
		}

		/**
		 * Invoked when a task is warned.
		 * @param arg the argument.
		 */
		public void notifyWarned ( Object arg ) {
			list_failed.add(arg);
		}
	}
}
