/*
 * @(#)MagnitudeRecordInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.MagnitudeRecord;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.io.*;
import net.aerith.misao.database.MagnitudeDBManager;
import net.aerith.misao.pixy.Resource;
import net.aerith.misao.toolkit.PixyDesktop.PixyReviewDesktop;

/**
 * The <code>MagnitudeRecordInternalFrame</code> represents a frame to 
 * show the table of magnitude data records.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class MagnitudeRecordInternalFrame extends BaseInternalFrame {
	/**
	 * The star.
	 */
	protected Star star;

	/**
	 * The parent desktop.
	 */
	protected BaseDesktop desktop;

	/**
	 * The table.
	 */
	protected MagnitudeRecordTable table;

	/**
	 * The position data.
	 */
	protected Hashtable hash_position_records;

	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>MagnitudeRecordInternalFrame</code>.
	 * @param star        the star.
	 * @param record_list the list of magnitude data records.
	 * @param desktop     the parent desktop.
	 */
	public MagnitudeRecordInternalFrame ( Star star, Vector record_list, BaseDesktop desktop ) {
		this.star = star;
		this.desktop = desktop;

		pane = getContentPane();

		table = new MagnitudeRecordTable(star, record_list, desktop);

		hash_position_records = new Hashtable();

		pane.setLayout(new BorderLayout());

		JPanel panel_vsnet = new JPanel();
		panel_vsnet.setLayout(new GridLayout(2, 1));
		JButton button_vsnet = new JButton("Report to VSNET");
		JButton button_vsolj = new JButton("Report to VSOLJ");
		button_vsnet.addActionListener(new ReportToVsnetListener(true));
		button_vsolj.addActionListener(new ReportToVsnetListener(false));
		panel_vsnet.add(button_vsnet);
		panel_vsnet.add(button_vsolj);

		JPanel panel_gallery = new JPanel();
		panel_gallery.setLayout(new GridLayout(2, 1));
		JButton button_gallery = new JButton("Image Gallery");
		JButton button_position_table = new JButton("Mean R.A. and Decl.");
		button_gallery.addActionListener(new GalleryListener());
		button_position_table.addActionListener(new ShowPositionTableListener());
		panel_gallery.add(button_gallery);
		panel_gallery.add(button_position_table);

		JPanel panel_button = new JPanel();
		panel_button.setLayout(new GridLayout(1, 2));
		panel_button.add(panel_vsnet);
		panel_button.add(panel_gallery);

		pane.add(panel_button, BorderLayout.NORTH);
		pane.add(new JScrollPane(table), BorderLayout.CENTER);
	}

	/**
	 * Initializes menu bar. A <code>JMenuBar</code> must be set to 
	 * this <code>JFrame</code> previously.
	 */
	public void initMenu ( ) {
		addFileMenu();

		addEditMenu();

		super.initMenu();
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

		menu.addSeparator();

		item = new JMenuItem("Review Examination");
		item.addActionListener(new ReviewListener());
		menu.add(item);
	}

	/**
	 * Adds the <tt>Edit</tt> menus to the menu bar.
	 */
	public void addEditMenu ( ) {
		JMenu menu = addMenu("Edit");

		JMenuItem item = new JMenuItem("Attributes");
		item.addActionListener(new EditAttributesListener());
		menu.add(item);
	}

	/**
	 * Adds a position data record.
	 * @param record the position data record.
	 */
	public void addPositionRecord ( XmlPositionRecord record ) {
		hash_position_records.put(record.getImageXmlPath(), record);
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
	 * The <code>ReviewListener</code> is a listener class of menu 
	 * selection to open the PIXY review desktops.
	 */
	protected class ReviewListener implements ActionListener, Runnable {
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
			XmlMagRecord[] records = table.getSelectedRecords();
			for (int i = 0 ; i < records.length ; i++) {
				PixyReviewDesktop new_desktop = new PixyReviewDesktop();
				new_desktop.setHelpMessageEnabled(false);
				new_desktop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				new_desktop.setSize(800,600);
				new_desktop.setTitle("PIXY System Desktop");
				new_desktop.setVisible(true);

				File file = desktop.getFileManager().newFile(records[i].getImageXmlPath());
				new_desktop.operate(file);
			}
		}
	}

	/**
	 * The <code>EditAttributesListener</code> is a listener class of 
	 * menu selection to edit the attributes of selected records.
	 */
	protected class EditAttributesListener implements ActionListener, Runnable {
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
			AttributesDialog dialog = new AttributesDialog();

			int answer = dialog.show(pane);

			if (answer == 0) {
				MagnitudeRecordAttributes attributes = dialog.getAttributes();
				table.updateAttributes(attributes);
			}
		}
	}

	/**
	 * The <code>ReportToVsnetListener</code> is a listener class of
	 * menu selection to show the VSNET report table.
	 */
	protected class ReportToVsnetListener implements ActionListener, Runnable {
		/**
		 * True when to report to VSNET, false to VSOLJ.
		 */
		protected boolean vsnet = true;

		/**
		 * Constructs a <code>ReportToVsnetListener</code>.
		 * @param vsnet true when to report to VSNET, false to VSOLJ.
		 */
		public ReportToVsnetListener ( boolean vsnet ) {
			this.vsnet = vsnet;
		}

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
			XmlMagRecord[] records = table.getSelectedRecords();
			if (records.length == 0)
				return;

			Vector reported_list = new Vector();
			Vector not_reported_list = new Vector();
			for (int i = 0 ; i < records.length ; i++) {
				if (vsnet) {
					if (records[i].isReportedToVsnet())
						reported_list.addElement(records[i]);
					else
						not_reported_list.addElement(records[i]);
				} else {
					if (records[i].isReportedToVsolj())
						reported_list.addElement(records[i]);
					else
						not_reported_list.addElement(records[i]);
				}
			}

			if (desktop.isMagnitudeDatabaseReadOnly() == false) {
				if (reported_list.size() > 0) {
					String header = "Following data have been already reported:";
					MessagesDialog dialog = new MessagesDialog(header, reported_list);
					dialog.show(pane, "Warning", JOptionPane.WARNING_MESSAGE);
				}
			}

			JTable vsnet_table = desktop.showVsnetReportTable(star, records, vsnet);

			if (desktop.isMagnitudeDatabaseReadOnly() == false) {
				if (vsnet_table != null) {
					if (not_reported_list.size() > 0) {
						try {
							// Starts using the disk cache.
							XmlDBHolderCache.enable(true);

							MagnitudeDBManager manager = desktop.getDBManager().getMagnitudeDBManager();

							for (int i = 0 ; i < not_reported_list.size() ; i++) {
								XmlMagRecord record = (XmlMagRecord)not_reported_list.elementAt(i);
								manager.deleteElement((CatalogStar)star, record);

								if (vsnet)
									record.setReportedToVsnet();
								else
									record.setReportedToVsolj();

								manager.addElement((CatalogStar)star, record);
							}

							// Stops using the disk cache, and flushes to the files.
							XmlDBHolderCache.enable(false);
						} catch ( ClassCastException exception ) {
							String message = "Failed to record the reported status in the database.";
							JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
						} catch ( IOException exception ) {
							String message = "Failed to record the reported status in the database.";
							JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
						}
					}

					repaint();
				}
			}
		}
	}

	/**
	 * The <code>GalleryListener</code> is a listener class of menu 
	 * selection to open the image gallery.
	 */
	protected class GalleryListener implements ActionListener, Runnable {
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
			XmlMagRecord[] records = table.getSelectedRecords();

			desktop.showImageGallery(star, records);
		}
	}

	/**
	 * The <code>ShowPositionTableListener</code> is a listener class
	 * of menu selection to show the position record table.
	 */
	protected class ShowPositionTableListener implements ActionListener, Runnable {
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
			XmlMagRecord[] records = table.getSelectedRecords();

			ArrayList record_list = new ArrayList();
			Vector failed_list = new Vector();
			Vector nodata_list = new Vector();

			for (int i = 0 ; i < records.length ; i++) {
				XmlPositionRecord position_record = (XmlPositionRecord)hash_position_records.get(records[i].getImageXmlPath());

				if (position_record == null) {
					try {
						XmlInformation info = desktop.getFileManager().readInformation(records[i]);
						XmlStar star = desktop.getFileManager().readStar(info, records[i].getName());
						position_record = new XmlPositionRecord(info, star);
					} catch ( IOException exception ) {
						failed_list.addElement(records[i].getImageXmlPath());
					} catch ( DocumentIncompleteException exception ) {
						failed_list.addElement(records[i].getImageXmlPath());
					} catch ( NoDataException exception ) {
						nodata_list.addElement(records[i].getImageXmlPath());
					}
				}

				if (position_record != null)
					record_list.add(position_record);
			}

			if (failed_list.size() > 0) {
				String header = "Failed to read the following XML files:";
				MessagesDialog dialog = new MessagesDialog(header, failed_list);
				dialog.show(pane, "Warning", JOptionPane.WARNING_MESSAGE);
			}

			if (nodata_list.size() > 0) {
				String header = "Not detected in the following XML files:";
				MessagesDialog dialog = new MessagesDialog(header, nodata_list);
				dialog.show(pane, "Warning", JOptionPane.WARNING_MESSAGE);
			}

			XmlPositionRecord[] position_records = new XmlPositionRecord[record_list.size()];
			position_records = (XmlPositionRecord[])record_list.toArray(position_records);
			desktop.showPositionTable(position_records);
		}
	}
}
