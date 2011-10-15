/*
 * @(#)Desktop.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Vector;
import net.aerith.misao.database.GlobalDBManager;
import net.aerith.misao.io.FileManager;
import net.aerith.misao.util.Monitor;
import net.aerith.misao.util.MonitorSet;
import net.aerith.misao.util.PrintStreamMonitor;
import net.aerith.misao.pixy.Resource;

/**
 * The <code>Desktop</code> represents a desktop. It has a function to
 * control internal frames.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class Desktop extends BaseFrame {
	/**
	 * The pane of the desktop.
	 */
	protected JDesktopPane desktop_pane;

	/**
	 * The menu of opening internal frames.
	 */
	protected JMenu menu_windows;

	/**
	 * The list of opening internal frames.
	 */
	protected Vector frame_list;

	/**
	 * The listener to bring a window to the front.
	 */
	protected SelectWindowListener select_window_listener;

	/**
	 * The listener to close a frame.
	 */
	protected CloseWindowListener close_window_listener;

	/**
	 * The database manager.
	 */
	private GlobalDBManager db_manager = null;

	/**
	 * The file manager.
	 */
	protected FileManager file_manager;

	/**
	 * The set of monitors.
	 */
	protected MonitorSet monitor_set;

	/**
	 * Constructs a <code>Desktop</code>.
	 */
	public Desktop ( ) {
		select_window_listener = new SelectWindowListener();
		close_window_listener = new CloseWindowListener();

		try {
			db_manager = new GlobalDBManager();
		} catch ( IOException exception ) {
		}
		file_manager = new FileManager();

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		desktop_pane = new JDesktopPane();
		pane.add(desktop_pane, BorderLayout.CENTER);

		frame_list = new Vector();

		monitor_set = new MonitorSet();
		addMonitor(new PrintStreamMonitor(System.out));
	}

	/**
	 * Initializes this window. This is invoked at construction.
	 */
	public void initialize ( ) {
		initMenu();
	}

	/**
	 * Initializes menu bar. A <code>JMenuBar</code> must be set to 
	 * this <code>JFrame</code> previously.
	 */
	public void initMenu ( ) {
		JMenu menu = addMenu("File");
		if (menu.getItemCount() > 0)
			menu.addSeparator();

		JMenuItem[] items = createFileMenus();
		for (int i = 0 ; i < items.length ; i++)
			menu.add(items[i]);

		super.initMenu();

		menu_windows = addMenu("Window");
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * file menus. Items are newly created when invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createFileMenus ( ) {
		JMenuItem[] items = new JMenuItem[1];
		items[0] = new JMenuItem("Exit");
		items[0].addActionListener(new ExitListener());
		return items;
	}

	/**
	 * Adds an internal frame. The added frame appears open in the
	 * desktop.
	 * @param frame a new frame to open.
	 */
	public void addFrame ( JInternalFrame frame ) {
		desktop_pane.add(frame);
		frame.moveToFront();

		JMenuItem item = (JMenuItem)menu_windows.add(new JMenuItem(frame.getTitle()));
		item.addActionListener(select_window_listener);
		frame_list.addElement(frame);
		frame.addInternalFrameListener(close_window_listener);
	}

	/**
	 * Adds a monitor.
	 */
	public void addMonitor ( Monitor monitor ) {
		monitor_set.addMonitor(monitor);
	}

	/**
	 * Opens the log window. It is registered as one of the monitors.
	 */
	public void openLogWindow ( ) {
		MonitorFrame log_window = new MonitorFrame();
		log_window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		log_window.setSize(500,400);
		log_window.setTitle("Log Window");
		log_window.setVisible(true);
		log_window.setMaximizable(true);
		log_window.setIconifiable(true);
		log_window.setResizable(true);
		addFrame(log_window);

		addMonitor(log_window);

		monitor_set.addMessages(Resource.getVersionAndCopyright());
		monitor_set.addSeparator();

		log_window.addMessage("Please click the right button of your mouse and select \"Restart logging\" menu to show the logs.");
		log_window.addSeparator();

		log_window.stop();
	}

	/**
	 * Gets the database manager.
	 * @return the database manager.
	 * @exception IOException if I/O error occurs.
	 */
	public GlobalDBManager getDBManager ( )
		throws IOException
	{
		if (db_manager == null)
			throw new IOException();

		return db_manager;
	}

	/**
	 * Sets the database manager.
	 * @param db_manager the database manager.
	 */
	protected void setDBManager ( GlobalDBManager db_manager ) {
		this.db_manager = db_manager;
	}

	/**
	 * Gets the file manager.
	 * @return the file manager.
	 */
	public FileManager getFileManager ( ) {
		return file_manager;
	}

	/**
	 * Sets the file manager.
	 * @param file_manager the file manager.
	 */
	protected void setFileManager ( FileManager file_manager ) {
		this.file_manager = file_manager;
	}

	/**
	 * The <code>ExitListener</code> is a listener class of menu 
	 * selection to exit.
	 */
	protected class ExitListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			dispose();
		}
	}

	/**
	 * The <code>SelectWindowListener</code> is a listener class of 
	 * menu selection to bring a window to the front.
	 */
	protected class SelectWindowListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			String command = e.getActionCommand();
			for (int i = 0 ; i < frame_list.size() ; i++) {
				JInternalFrame frame = (JInternalFrame)frame_list.elementAt(i);
				if (frame.getTitle().equals(command)) {
					frame.moveToFront();
				}
			}
		}
	}

	/**
	 * The <code>CloseWindowListener</code> is a listener class to
	 * close an internal frame.
	 */
	protected class CloseWindowListener extends InternalFrameAdapter {
		/**
		 * Invoked when an internal frame has been closed.
		 * @param e contains the event.
		 */
		public void internalFrameClosed ( InternalFrameEvent e ) {
			JInternalFrame closed_frame = (JInternalFrame)e.getSource();

			Vector list = new Vector();
			for (int i = 0 ; i < frame_list.size() ; i++) {
				JInternalFrame frame = (JInternalFrame)frame_list.elementAt(i);
				if (frame != closed_frame)
					list.addElement(frame);
			}
			frame_list = list;

			menu_windows.removeAll();
			for (int i = 0 ; i < frame_list.size() ; i++) {
				JInternalFrame frame = (JInternalFrame)frame_list.elementAt(i);
				JMenuItem item = (JMenuItem)menu_windows.add(new JMenuItem(frame.getTitle()));
				item.addActionListener(select_window_listener);
			}
		}
	}
}
