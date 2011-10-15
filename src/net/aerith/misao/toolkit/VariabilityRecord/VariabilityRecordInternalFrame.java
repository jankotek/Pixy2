/*
 * @(#)VariabilityRecordInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.VariabilityRecord;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.catalog.CatalogManager;
import net.aerith.misao.catalog.io.CatalogReader;
import net.aerith.misao.database.*;
import net.aerith.misao.pixy.Resource;

/**
 * The <code>VariabilityRecordInternalFrame</code> represents a frame 
 * which consists of the table of variability records, and buttons to 
 * show the variability information panel, and to create the HTML 
 * image gallery.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 March 11
 */

public class VariabilityRecordInternalFrame extends BaseInternalFrame {
	/**
	 * The parent desktop.
	 */
	protected BaseDesktop desktop;

	/**
	 * The table.
	 */
	protected VariabilityRecordTable table;

	/**
	 * The button to show the variability information panel.
	 */
	protected JButton button_panel;

	/**
	 * The button to create the HTML image gallery.
	 */
	protected JButton button_html_gallery;

	/**
	 * The container pane.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>VariabilityRecordInternalFrame</code>.
	 * @param record_list the list of variability records.
	 * @param desktop     the parent desktop.
	 */
	public VariabilityRecordInternalFrame ( Vector record_list, BaseDesktop desktop ) {
		this.desktop = desktop;

		pane = getContentPane();

		table = new VariabilityRecordTable(record_list, desktop);

		pane.setLayout(new BorderLayout());

		button_panel = new JButton("Show Panel");
		button_html_gallery = new JButton("HTML Image Gallery");
		button_panel.addActionListener(new PanelListener());
		button_html_gallery.addActionListener(new HtmlGalleryListener());

		JPanel panel_button = new JPanel();
		panel_button.setLayout(new GridLayout(1, 2));
		panel_button.add(button_panel);
		panel_button.add(button_html_gallery);

		pane.add(panel_button, BorderLayout.NORTH);
		pane.add(new JScrollPane(table), BorderLayout.CENTER);
	}

	/**
	 * Initializes menu bar. A <code>JMenuBar</code> must be set to 
	 * this <code>JFrame</code> previously.
	 */
	public void initMenu ( ) {
		addFileMenu();

		super.initMenu();
	}

	/**
	 * Adds the <tt>File</tt> menus to the menu bar.
	 */
	public void addFileMenu ( ) {
		JMenu menu = addMenu("File");

		JMenuItem item = new JMenuItem("Identify");
		item.addActionListener(new IdentifyListener());
		menu.add(item);

		item = new JMenuItem("Identify from Database");
		item.addActionListener(new IdentifyFromDatabaseListener());
		menu.add(item);
	}

	/**
	 * The <code>PanelListener</code> is a listener class of menu 
	 * selection to open the variability information panel.
	 */
	protected class PanelListener implements ActionListener, Runnable {
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
			Variability[] records = table.getSelectedRecords();

			for (int i = 0 ; i < records.length ; i++)
				desktop.showVariabilityPanel(records[i]);
		}
	}

	/**
	 * The <code>HtmlGalleryListener</code> is a listener class of 
	 * menu selection to create the HTML image gallery.
	 */
	protected class HtmlGalleryListener implements ActionListener, Runnable {
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
			HtmlImageGallerySettingDialog dialog = new HtmlImageGallerySettingDialog();
			int answer = dialog.show(pane);

			if (answer == 0) {
				Variability[] records = table.getSelectedRecords();

				desktop.showHtmlImageGalleryTable(records, dialog.getMode(), dialog.createsFitsImages(), dialog.getPastImageMode(), dialog.addsDssImage());
			}
		}
	}

	/**
	 * The <code>IdentifyListener</code> is a listener class of menu 
	 * selection to identify.
	 */
	protected class IdentifyListener implements ActionListener, Runnable {
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
			Vector catalog_list = CatalogManager.getIdentificationCatalogReaderList();
			OpenCatalogDialog dialog = new OpenCatalogDialog(catalog_list);

			int answer = dialog.show(pane);
			if (answer == 0) {
				CatalogReader reader = dialog.getSelectedCatalogReader();

				String[] paths = net.aerith.misao.util.Format.separatePath(dialog.getCatalogPath());
				for (int i = 0 ; i < paths.length ; i++) {
					try {
						reader.addURL(new File(paths[i]).toURL());
					} catch ( MalformedURLException exception ) {
						System.err.println(exception);
					}
				}

				try {
					table.identify(reader);

					String message = "Completed.";
					JOptionPane.showMessageDialog(pane, message);
				} catch ( IOException exception ) {
					String message = "Failed.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				} catch ( QueryFailException exception ) {
					String message = "Failed.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * The <code>IdentifyFromDatabaseListener</code> is a listener 
	 * class of menu selection to identify.
	 */
	protected class IdentifyFromDatabaseListener implements ActionListener, Runnable {
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
			try {
				CatalogReader reader = new CatalogDBReader(desktop.getDBManager().getCatalogDBManager());

				table.identify(reader);

				String message = "Completed.";
				JOptionPane.showMessageDialog(pane, message);
			} catch ( IOException exception ) {
				String message = "Failed.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			} catch ( QueryFailException exception ) {
				String message = "Failed.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
