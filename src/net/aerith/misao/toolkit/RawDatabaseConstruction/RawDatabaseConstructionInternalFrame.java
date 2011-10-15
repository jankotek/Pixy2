/*
 * @(#)RawDatabaseConstructionInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.RawDatabaseConstruction;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.FileCopyDialog;
import net.aerith.misao.io.*;
import net.aerith.misao.database.*;
import net.aerith.misao.pixy.Resource;

/**
 * The <code>RawDatabaseConstructionInternalFrame</code> represents a 
 * frame to select XML report documents to construct a database of
 * detected stars from the XML report documents.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class RawDatabaseConstructionInternalFrame extends BaseInternalFrame {
	/**
	 * The temporary database of detected stars.
	 */
	protected GlobalDBManager db_manager = null;

	/**
	 * The table.
	 */
	protected InformationTable table;

	/*
	 * The operation to construct an information database.
	 */
	protected InformationDBConstructionOperation info_operation;

	/*
	 * The operation to construct a catalog database.
	 */
	protected CatalogDBConstructionOperation catalog_operation;

	/*
	 * The operation to construct a magnitude database.
	 */
	protected MagnitudeDBConstructionOperation mag_operation;

	/*
	 * The control panel.
	 */
	protected RawDatabaseConstructionControlPanel control_panel;

	/**
	 * The set of monitors.
	 */
	protected MonitorSet monitor_set = new MonitorSet();

	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>RawDatabaseConstructionInternalFrame</code>.
	 * @param desktop the parent desktop.
	 */
	public RawDatabaseConstructionInternalFrame ( BaseDesktop desktop ) {
		pane = getContentPane();

		table = createTable(desktop);
		info_operation = new InformationDBConstructionOperation(table, this);
		catalog_operation = new CatalogDBConstructionOperation(table, this);
		mag_operation = new MagnitudeDBConstructionOperation(table);
		control_panel = createControlPanel(info_operation, table);

		initializeDatabase();

		info_operation.setFileManager(desktop.getFileManager());
		catalog_operation.setFileManager(desktop.getFileManager());
		mag_operation.setFileManager(desktop.getFileManager());

		info_operation.addObserver(new InformationDBObserver());
		catalog_operation.addObserver(new CatalogDBObserver());
		mag_operation.addObserver(new MagnitudeDBObserver());

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
	 * Adds the <tt>File</tt> menus to the menu bar.
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
	 * Creates the table. This is invoked at construction.
	 * @param desktop the desktop.
	 * @return the table.
	 */
	protected InformationTable createTable ( BaseDesktop desktop ) {
		return new InformationTable();
	}

	/**
	 * Creates the control panel. This is invoked at construction.
	 * @param operation the operation.
	 * @param table     the table.
	 * @return the control panel.
	 */
	protected RawDatabaseConstructionControlPanel createControlPanel ( MultiTaskOperation operation, InformationTable table ) {
		return new RawDatabaseConstructionControlPanel(operation, table, this);
	}

	/**
	 * Adds a monitor.
	 * @param monitor the monitor.
	 */
	public void addMonitor ( Monitor monitor ) {
		monitor_set.addMonitor(monitor);

		info_operation.addMonitor(monitor_set);
		catalog_operation.addMonitor(monitor_set);
		mag_operation.addMonitor(monitor_set);
	}

	/**
	 * Starts the operation.
	 */
	public void startOperation ( ) {
		control_panel.start();
	}

	/**
	 * Initializes the database.
	 */
	public void initializeDatabase ( ) {
		// Temporary database of detected stars.
		try {
			db_manager = new GlobalDBManager(new MemoryFileSystem());
			db_manager.setRawDatabase(true);

			info_operation.setDBManager(db_manager);
			catalog_operation.setDBManager(db_manager);
			mag_operation.setDBManager(db_manager);
		} catch ( IOException exception ) {
			// Never happens.
		}
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		return 0;
	}

	/**
	 * Returns the limiting magnitude of the catalog database.
	 * @return the limiting magnitude of the catalog database.
	 */
	public double getLimitingMagnitude ( ) {
		return 99.9;
	}

	/**
	 * Returns true when to construct the magnitude database, too. 
	 * This method must be overrided in the subclasses.
	 * @return true when to construct the magnitude database, too. 
	 */
	public boolean constructsMagnitudeDatabase ( ) {
		return true;
	}

	/**
	 * Invoked when the raw database construction is started.
	 */
	protected void operationStarted ( ) {
	}

	/**
	 * Invoked when the raw database construction is succeeded.
	 */
	protected void operationSucceeded ( ) {
		String message = "Succeeded.";
		JOptionPane.showMessageDialog(pane, message);
	}

	/**
	 * Invoked when the raw database construction is failed.
	 */
	protected void operationFailed ( ) {
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

	/**
	 * The <code>InformationDBObserver</code> is an observer of an 
	 * operation to construct the information database.
	 */
	protected class InformationDBObserver implements OperationObserver {
		/**
		 * Invoked when the operation starts.
		 */
		public void notifyStart ( ) {
			operationStarted();
		}

		/**
		 * Invoked when the operation ends.
		 * @param exception the exception if an error occurs, or null if
		 * succeeded.
		 */
		public void notifyEnd ( Exception exception ) {
			if (exception != null) {
				control_panel.setOperation(info_operation);

				operationFailed();
			} else {
				control_panel.proceedOperation(catalog_operation);
			}
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
	}

	/**
	 * The <code>CatalogDBObserver</code> is an observer of an 
	 * operation to construct the catalog database.
	 */
	protected class CatalogDBObserver implements OperationObserver {
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
			if (exception != null) {
				control_panel.setOperation(info_operation);

				operationFailed();
			} else {
				if (constructsMagnitudeDatabase()) {
					control_panel.proceedOperation(mag_operation);
				} else {
					control_panel.setOperation(info_operation);

					operationSucceeded();
				}
			}
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
	}

	/**
	 * The <code>MagnitudeDBObserver</code> is an observer of an 
	 * operation to construct the magnitude database.
	 */
	protected class MagnitudeDBObserver implements OperationObserver {
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
			control_panel.setOperation(info_operation);

			if (exception == null) {
				operationSucceeded();
			} else {
				operationFailed();
			}
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
	}
}
