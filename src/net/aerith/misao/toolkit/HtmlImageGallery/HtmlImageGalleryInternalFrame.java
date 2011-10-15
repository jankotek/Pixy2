/*
 * @(#)HtmlImageGalleryInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.HtmlImageGallery;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.CatalogStar;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.FileCopyDialog;
import net.aerith.misao.xml.*;
import net.aerith.misao.database.GlobalDBManager;
import net.aerith.misao.pixy.Resource;

/**
 * The <code>HtmlImageGalleryInternalFrame</code> represents a frame 
 * which shows the images and XML files, and the progress to create 
 * HTML image gallery.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2005 May 22
 */

public class HtmlImageGalleryInternalFrame extends BaseInternalFrame {
	/*
	 * The parent desktop.
	 */
	protected BaseDesktop desktop;

	/*
	 * The table.
	 */
	protected HtmlImageGalleryTable table;

	/*
	 * The operation.
	 */
	 protected HtmlImageGalleryOperation operation;

	/*
	 * The control panel.
	 */
	protected HtmlImageGalleryControlPanel control_panel;

	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>HtmlImageGalleryInternalFrame</code>.
	 * @param variability_list the list of variability records.
	 * @param mode             the mode to create HTML image gallery.
	 * @param fits             true when to create FITS thumbnail 
	 * images.
	 * @param past_mode        the mode to add past images from the
	 * database.
	 * @param dss              true when to add a DSS image.
	 * @param desktop          the parent desktop.
	 */
	public HtmlImageGalleryInternalFrame ( Vector variability_list, int mode, boolean fits, int past_mode, boolean dss, BaseDesktop desktop ) {
		this.desktop = desktop;

		pane = getContentPane();

		GlobalDBManager db_manager = null;
		try {
			db_manager = desktop.getDBManager();
		} catch ( IOException exception ) {
			// Makes no problem.
		}

		table = new HtmlImageGalleryTable(variability_list, mode, desktop);
		operation = new HtmlImageGalleryOperation(table, desktop.getFileManager(), db_manager, fits, past_mode, dss);
		control_panel = new HtmlImageGalleryControlPanel(operation, table);

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
	 * Sets the operation.
	 * @param operation the operation.
	 */
	protected void setOperation ( HtmlImageGalleryOperation operation ) {
		this.operation = operation;

		control_panel.setOperation(operation);
	}

	/**
	 * Starts the operation.
	 */
	public void startOperation ( ) {
		control_panel.start();
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
