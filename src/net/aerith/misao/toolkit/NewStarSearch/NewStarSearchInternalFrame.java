/*
 * @(#)NewStarSearchInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.NewStarSearch;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.util.*;
import net.aerith.misao.xml.Variability;
import net.aerith.misao.pixy.Resource;

/**
 * The <code>NewStarSearchInternalFrame</code> represents a frame to 
 * select XML report documents to search new stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 March 11
 */

public class NewStarSearchInternalFrame extends BaseInternalFrame implements OperationObserver {
	/**
	 * The parent desktop.
	 */
	protected BaseDesktop desktop;

	/**
	 * The table.
	 */
	protected InformationTable table;

	/*
	 * The operation.
	 */
	protected NewStarSearchOperation operation;

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
	 * Constructs a <code>NewStarSearchInternalFrame</code>.
	 * @param desktop the parent desktop.
	 */
	public NewStarSearchInternalFrame ( BaseDesktop desktop ) {
		this.desktop = desktop;

		pane = getContentPane();

		table = new InformationTable();
		try {
			operation = new NewStarSearchOperation(table, desktop.getDBManager().getCatalogDBManager());
		} catch ( IOException exception ) {
		}
		control_panel = new XmlReportControlPanel(operation, table);

		operation.addObserver(this);

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
		addFileMenu();

		super.initMenu();

		addOperationMenu();
	}

	/**
	 * Adds the <tt></tt> menus to the menu bar.
	 */
	public void addFileMenu ( ) {
		JMenu menu = addMenu("File");

		JMenuItem item = new JMenuItem("Restore XML Files");
		item.addActionListener(new RestoreXmlListener());
		menu.add(item);

		item = new JMenuItem("Restore Images");
		item.addActionListener(new RestoreImageListener());
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
	protected void setOperation ( NewStarSearchOperation operation ) {
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
	}

	/**
	 * Invoked when the operation ends.
	 * @param exception the exception if an error occurs, or null if
	 * succeeded.
	 */
	public void notifyEnd ( Exception exception ) {
		Variability[] records = operation.getVariabilityRecords();
		desktop.showVariabilityTable(records);
	}

	/**
	 * Invoked when a task is succeeded.
	 * @param arg the argument.
	 */
	public void notifySucceeded ( Object arg ) {
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
	 * The <code>RestoreImageListener</code> is a listener class of 
	 * menu selection to restore the images.
	 */
	protected class RestoreImageListener implements ActionListener, Runnable {
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
			FileCopyDialog dialog = new FileCopyDialog();
			int answer = dialog.show(pane);

			if (answer == 0) {
				table.restoreImages(dialog.getDirectoryFrom(), dialog.getDirectoryTo());
			}
		}
	}

	/**
	 * The <code>RestoreXmlListener</code> is a listener class of 
	 * menu selection to restore the XML files.
	 */
	protected class RestoreXmlListener implements ActionListener, Runnable {
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
			FileCopyDialog dialog = new FileCopyDialog();
			int answer = dialog.show(pane);

			if (answer == 0) {
				table.restoreXmlFiles(dialog.getDirectoryFrom(), dialog.getDirectoryTo());
			}
		}
	}
}
