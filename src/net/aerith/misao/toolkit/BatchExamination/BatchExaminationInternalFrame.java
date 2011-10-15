/*
 * @(#)BatchExaminationInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.BatchExamination;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.util.*;
import net.aerith.misao.io.filechooser.XmlFilter;
import net.aerith.misao.xml.*;
import net.aerith.misao.pixy.Resource;

/**
 * The <code>BatchExaminationInternalFrame</code> represents a frame 
 * to select image files, edit the batch XML file for examination, and
 * operate the image examination on the selected images.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class BatchExaminationInternalFrame extends BaseInternalFrame implements OperationObserver {
	/**
	 * The parent desktop.
	 */
	protected BaseDesktop desktop;

	/**
	 * The table.
	 */
	protected InstructionTable table;

	/*
	 * The operation.
	 */
	protected BatchExaminationOperation operation;

	/*
	 * The control panel.
	 */
	protected BatchExaminationControlPanel control_panel;

	/*
	 * The list of image information elements.
	 */
	protected ArrayList list_info;

	/**
	 * The set of monitors.
	 */
	protected MonitorSet monitor_set = new MonitorSet();

	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>BatchExaminationInternalFrame</code>.
	 * @param desktop the parent desktop.
	 */
	public BatchExaminationInternalFrame ( BaseDesktop desktop ) {
		this.desktop = desktop;

		pane = getContentPane();

		table = new InstructionTable();
		operation = new BatchExaminationOperation(table);
		control_panel = new BatchExaminationControlPanel(operation, table);

		operation.addObserver(this);

		pane.setLayout(new BorderLayout());
		pane.add(control_panel, BorderLayout.NORTH);
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
		addFileMenu();

		super.initMenu();

		addOperationMenu();
	}

	/**
	 * Adds the <tt></tt> menus to the menu bar.
	 */
	public void addFileMenu ( ) {
		JMenu menu = addMenu("File");

		JMenuItem item = new JMenuItem("Import Batch XML File");
		item.addActionListener(new ImportListener());
		menu.add(item);
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

		operation.addMonitor(monitor_set);
	}

	/**
	 * Sets the operation.
	 * @param operation the operation.
	 */
	protected void setOperation ( BatchExaminationOperation operation ) {
		this.operation = operation;

		control_panel.setOperation(operation);

		operation.addObserver(this);
	}

	/**
	 * Starts the operation.
	 */
	public void startOperation ( ) {
		control_panel.start();
	}

	/**
	 * Invoked when the operation starts.
	 */
	public void notifyStart ( ) {
		list_info = new ArrayList();
	}

	/**
	 * Invoked when the operation ends.
	 * @param exception the exception if an error occurs, or null if
	 * succeeded.
	 */
	public void notifyEnd ( Exception exception ) {
		XmlInformation[] infos = new XmlInformation[list_info.size()];
		desktop.showInformationTable((XmlInformation[])list_info.toArray(infos));
	}

	/**
	 * Invoked when a task is succeeded.
	 * @param arg the argument.
	 */
	public void notifySucceeded ( Object arg ) {
		XmlReport report = (XmlReport)arg;
		XmlInformation info = new XmlInformation((XmlInformation)report.getInformation());
		list_info.add(info);
	}

	/**
	 * Invoked when a task is failed.
	 * @param arg the argument.
	 */
	public void notifyFailed ( Object arg ) {
	}

	/**
	 * Invoked when a task is warned.
	 * @param arg the argument.
	 */
	public void notifyWarned ( Object arg ) {
	}

	/**
	 * The <code>ImportListener</code> is a listener class of menu 
	 * selection to import batch XML file.
	 */
	protected class ImportListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			Thread thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			CommonFileChooser file_chooser = new CommonFileChooser();
			file_chooser.setDialogTitle("Open a batch XML file.");
			file_chooser.setMultiSelectionEnabled(false);
			file_chooser.addChoosableFileFilter(new XmlFilter());

			if (file_chooser.showOpenDialog(pane) == JFileChooser.APPROVE_OPTION) {
				try {
					File file = file_chooser.getSelectedFile();

					XmlBatch batch = new XmlBatch();
					batch.read(file);

					XmlInstruction[] instructions = (XmlInstruction[])batch.getInstruction();
					table.addInstructions(instructions, desktop.getFileManager());
				} catch ( IOException exception ) {
				}
			}
		}
	}
}
