/*
 * @(#)VariabilityRecordTable.java
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
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.table.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.io.*;
import net.aerith.misao.io.filechooser.XmlFilter;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.database.*;
import net.aerith.misao.pixy.Resource;

/**
 * The <code>VariabilityRecordTable</code> represents a table which 
 * contains variability of variable stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 May 20
 */

public class VariabilityRecordTable extends SortableCheckTable {
	/**
	 * The parent desktop.
	 */
	protected net.aerith.misao.gui.Desktop desktop;

	/**
	 * The list of variability records.
	 */
	protected Vector record_list;

	/**
	 * The columns.
	 */
	protected final static String[] column_names = { "", "R.A.", "Decl.", "ID", "Max Mag", "Min Mag", "Mag Range", "Observations", "Positive Observations", "Arc", "First Date", "Last Date" };

	/**
	 * The table model.
	 */
	protected DefaultTableModel model;

	/**
	 * The table column model.
	 */
	protected DefaultTableColumnModel column_model;

	/**
	 * The container pane.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>VariabilityRecordTable</code> with a list of 
	 * variability records.
	 * @param record_list the list of variability records.
	 * @param desktop     the parent desktop.
	 */
	public VariabilityRecordTable ( Vector record_list, net.aerith.misao.gui.Desktop desktop ) {
		this.record_list = record_list;
		this.desktop = desktop;

		index = new ArrayIndex(record_list.size());

		model = new DefaultTableModel(column_names, 0);
		Object[] objects = new Object[column_names.length];
		objects[0] = new Boolean(true);
		for (int i = 1 ; i < column_names.length ; i++)
			objects[i] = "";
		for (int i = 0 ; i < record_list.size() ; i++)
			model.addRow(objects);
		setModel(model);

		column_model = (DefaultTableColumnModel)getColumnModel();
		for (int i = 1 ; i < column_names.length ; i++)
			column_model.getColumn(i).setCellRenderer(new StringRenderer(column_names[i], LabelTableCellRenderer.MODE_MULTIPLE_SELECTION));

		initializeCheckColumn();

		setTableHeader(new TableHeader(column_model));

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		initializeColumnWidth();

		pane = this;

		initPopupMenu();
	}

	/**
	 * Initializes the column width.
	 */
	protected void initializeColumnWidth ( ) {
		column_model.getColumn(0).setPreferredWidth(20);
		column_model.getColumn(1).setPreferredWidth(100);
		column_model.getColumn(2).setPreferredWidth(100);
		column_model.getColumn(3).setPreferredWidth(100);
		column_model.getColumn(4).setPreferredWidth(60);
		column_model.getColumn(5).setPreferredWidth(60);
		column_model.getColumn(6).setPreferredWidth(60);
		column_model.getColumn(7).setPreferredWidth(40);
		column_model.getColumn(8).setPreferredWidth(40);
		column_model.getColumn(9).setPreferredWidth(40);
		column_model.getColumn(10).setPreferredWidth(160);
		column_model.getColumn(11).setPreferredWidth(160);
	}

	/**
	 * Initializes a popup menu. A <tt>popup</tt> must be created 
	 * previously.
	 */
	protected void initPopupMenu ( ) {
		super.initPopupMenu();

		popup.addSeparator();

		JMenuItem item = new JMenuItem("Save Package File");
		item.addActionListener(new SaveListener());
		popup.add(item);

		item = new JMenuItem("Save As Astrometrica Format");
		item.addActionListener(new SaveAsAstrometricaFormatListener());
		popup.add(item);

		popup.addSeparator();

		item = new JMenuItem("Export Package");
		item.addActionListener(new ExportListener());
		popup.add(item);
	}

	/**
	 * Gets the output string of the cell.
	 * @param header_value the header value of the column.
	 * @param row          the index of row in original order.
	 * @return the output string of the cell.
	 */
	protected String getCellString ( String header_value, int row ) {
		Variability record = (Variability)record_list.elementAt(row);

		if (header_value.equals("R.A.")) {
			String coor = record.getStar().getCoor().getOutputString();
			int p = coor.indexOf(' ');
			return coor.substring(0, p);
		}
		if (header_value.equals("Decl.")) {
			String coor = record.getStar().getCoor().getOutputString();
			int p = coor.indexOf(' ');
			return coor.substring(p + 1);
		}
		if (header_value.equals("ID")) {
			if (record.getIdentifiedStar() != null)
				return record.getIdentifiedStar().getName();
		}
		if (header_value.equals("Max Mag")) {
			return ((XmlMag)record.getBrightestMagnitude().getMag()).getOutputString();
		}
		if (header_value.equals("Min Mag")) {
			return ((XmlMag)record.getFaintestMagnitude().getMag()).getOutputString();
		}
		if (header_value.equals("Mag Range")) {
			return Format.formatDouble(record.getMagnitudeRange(), 5, 2);
		}
		if (header_value.equals("Observations")) {
			return String.valueOf(record.getObservations());
		}
		if (header_value.equals("Positive Observations")) {
			return String.valueOf(record.getPositiveObservations());
		}
		if (header_value.equals("Arc")) {
			return String.valueOf(record.getArcInDays());
		}
		if (header_value.equals("First Date")) {
			String s = record.getFirstDate();
			if (s != null)
				return s;
		}
		if (header_value.equals("Last Date")) {
			String s = record.getLastDate();
			if (s != null)
				return s;
		}
		return "";
	}

	/**
	 * Gets the sortable array of the specified column.
	 * @param header_value the header value of the column to sort.
	 */
	protected SortableArray getSortableArray ( String header_value ) {
		if (header_value.length() == 0)
			return null;

		SortableArray array = null;
		if (header_value.equals("R.A.")  ||  header_value.equals("Decl.")  ||  header_value.equals("ID"))
			array = new StringArray(record_list.size());
		else 
			array = new Array(record_list.size());

		for (int i = 0 ; i < record_list.size() ; i++) {
			String value = getCellString(header_value, i);

			if (header_value.equals("R.A.")  ||  header_value.equals("Decl.")  ||  header_value.equals("ID")) {
				((StringArray)array).set(i, value);
			} else if (header_value.equals("Max Mag")  ||  header_value.equals("Min Mag")) {
				double mag_value = 0.0;
				if ('0' <= value.charAt(0)  &&  value.charAt(0) <= '9'  ||
					value.charAt(0) == '-'  ||  value.charAt(0) == '+') {
					mag_value = Format.doubleValueOf(value);
				} else {
					mag_value = 100 + Format.doubleValueOf(value.substring(1));
				}
				((Array)array).set(i, mag_value);
			} else if (header_value.equals("First Date")  ||  header_value.equals("Last Date")) {
				double jd = 0.0;
				if (value.length() > 0)
					jd = JulianDay.create(value).getJD();
				((Array)array).set(i, jd);
			} else {
				((Array)array).set(i, Double.parseDouble(value));
			}
		}

		return array;
	}

	/**
	 * Gets the list of selected records.
	 * @return the list of selected records.
	 */
	public Variability[] getSelectedRecords ( ) {
		ArrayList list = new ArrayList();

		int check_column = getCheckColumn();
		for (int i = 0 ; i < model.getRowCount() ; i++) {
			if (((Boolean)getValueAt(i, check_column)).booleanValue()) {
				Variability record = (Variability)record_list.elementAt(index.get(i));
				list.add(record);
			}
		}

		Variability[] records = new Variability[list.size()];
        return (Variability[])list.toArray(records);
	}

	/**
	 * Identifies.
	 * @param reader the catalog reader.
	 * @exception IOException if the catalog cannot be accessed.
	 * @exception QueryFailException if the query to the server is 
	 * failed.
	 */
	public void identify ( CatalogReader reader ) throws IOException, QueryFailException {
		// Starts the polling thread.
		PollingThread thread = new PollingThread();
		thread.start();

		try {
			Variability[] records = getSelectedRecords();

			for (int i = 0 ; i < records.length ; i++) {
				CatalogStar star = records[i].getStar();

				StarList l = reader.read(star.getCoor(), 0.1);
				for (int j = 0 ; j < l.size() ; j++) {
					CatalogStar s = (CatalogStar)l.elementAt(j);

					double radius = star.getMaximumPositionErrorInArcsec() + s.getMaximumPositionErrorInArcsec();
					double distance = star.getCoor().getAngularDistanceTo(s.getCoor());

					if (distance < radius / 3600.0)
						// Overwrites the already identified star.
						records[i].setIdentifiedStar(s);
				}
			}
		} finally {
			// Ends the polling thread.
			thread.end();
		}
	}

	/**
	 * The <code>SaveListener</code> is a listener class of menu 
	 * selection to save the package file.
	 */
	protected class SaveListener implements ActionListener, Runnable {
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
			file_chooser.setDialogTitle("Save package file.");
			file_chooser.setMultiSelectionEnabled(false);
			file_chooser.addChoosableFileFilter(new XmlFilter());
			file_chooser.setSelectedFile(new File("package.xml"));

			if (file_chooser.showSaveDialog(pane) == JFileChooser.APPROVE_OPTION) {
				try {
					File file = file_chooser.getSelectedFile();
					if (file.exists()) {
						String message = "Overwrite to " + file.getPath() + " ?";
						if (0 != JOptionPane.showConfirmDialog(pane, message, "Confirmation", JOptionPane.YES_NO_OPTION)) {
							return;
						}
					}

					// Outputs the variability XML file.

					XmlVariabilityHolder holder = new XmlVariabilityHolder();

					Variability[] records = getSelectedRecords();
					for (int i = 0 ; i < records.length ; i++) {
						XmlVariability variability = new XmlVariability(records[i]);
						holder.addVariability(variability);
					}

					holder.write(file);

					String message = "Completed.";
					JOptionPane.showMessageDialog(pane, message);
				} catch ( IOException exception ) {
					String message = "Failed.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * The <code>SaveAsAstrometricaFormatListener</code> is a listener
	 * class of menu selection to save in Astrometrica format.
	 */
	protected class SaveAsAstrometricaFormatListener implements ActionListener, Runnable {
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
			file_chooser.setDialogTitle("Save selected data.");
			file_chooser.setMultiSelectionEnabled(false);

			if (file_chooser.showSaveDialog(pane) == JFileChooser.APPROVE_OPTION) {
				try {
					File file = file_chooser.getSelectedFile();
					if (file.exists()) {
						String message = "Overwrite to " + file.getPath() + " ?";
						if (0 != JOptionPane.showConfirmDialog(pane, message, "Confirmation", JOptionPane.YES_NO_OPTION)) {
							return;
						}
					}

					AstrometricaWriter writer = new AstrometricaWriter(file);
					writer.open();

					int check_column = getCheckColumn();
					for (int i = 0 ; i < model.getRowCount() ; i++) {
						if (((Boolean)getValueAt(i, check_column)).booleanValue()) {
							Variability record = (Variability)record_list.elementAt(index.get(i));
							writer.write(record.getStar());
						}
					}

					writer.close();

					String message = "Completed.";
					JOptionPane.showMessageDialog(pane, message);
				} catch ( IOException exception ) {
					String message = "Failed to save file.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				} catch ( UnsupportedStarClassException exception ) {
					String message = "Failed to save file.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * The <code>ExportListener</code> is a listener class of menu 
	 * selection to export the package.
	 */
	protected class ExportListener implements ActionListener, Runnable {
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
			file_chooser.setDialogTitle("Choose a directory.");
			file_chooser.setMultiSelectionEnabled(false);
			file_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			if (file_chooser.showSaveDialog(pane) == JFileChooser.APPROVE_OPTION) {
				try {
					File directory = file_chooser.getSelectedFile();
					directory.mkdirs();

					// Outputs the variability XML file.

					String path = FileManager.unitePath(directory.getAbsolutePath(), "package.xml");
					File file = new File(path);
					if (file.exists()) {
						String message = "Overwrite to " + file.getPath() + " ?";
						if (0 != JOptionPane.showConfirmDialog(pane, message, "Confirmation", JOptionPane.YES_NO_OPTION)) {
							return;
						}
					}

					XmlVariabilityHolder holder = new XmlVariabilityHolder();

					Variability[] records = getSelectedRecords();
					for (int i = 0 ; i < records.length ; i++) {
						XmlVariability variability = new XmlVariability(records[i]);
						holder.addVariability(variability);
					}

					holder.write(file);

					// Copies the report XML document files.

					Hashtable hash_xml = new Hashtable();

					for (int i = 0 ; i < records.length ; i++) {
						XmlMagRecord[] mag_records = records[i].getMagnitudeRecords();
						for (int j = 0 ; j < mag_records.length ; j++)
							hash_xml.put(mag_records[j].getImageXmlPath(), this);
					}

					Vector failed_list = new Vector();

					Enumeration keys = hash_xml.keys();
					while (keys.hasMoreElements()) {
						String xml_path = (String)keys.nextElement();
						try {
							File src_file = desktop.getFileManager().newFile(xml_path);
							File dst_file = new File(FileManager.unitePath(directory.getAbsolutePath(), xml_path));
							if (dst_file.exists() == false)
								FileManager.copy(src_file, dst_file);
						} catch ( Exception exception ) {
							failed_list.addElement(xml_path);
						}
					}

					if (failed_list.size() > 0) {
						String header = "Failed to copy the following XML files:";
						MessagesDialog dialog = new MessagesDialog(header, failed_list);
						dialog.show(pane, "Error", JOptionPane.ERROR_MESSAGE);
					}

					// Copies the image files.

					failed_list = new Vector();

					keys = hash_xml.keys();
					while (keys.hasMoreElements()) {
						path = (String)keys.nextElement();
						try {
							XmlInformation info = XmlReport.readInformation(desktop.getFileManager().newFile(path));
							path = info.getImage().getContent();

							File src_file = desktop.getFileManager().newFile(path);
							File dst_file = new File(FileManager.unitePath(directory.getAbsolutePath(), path));
							if (dst_file.exists() == false)
								FileManager.copy(src_file, dst_file);
						} catch ( Exception exception ) {
							failed_list.addElement(path);
						}
					}

					if (failed_list.size() > 0) {
						String header = "Failed to copy the following image files:";
						MessagesDialog dialog = new MessagesDialog(header, failed_list);
						dialog.show(pane, "Error", JOptionPane.ERROR_MESSAGE);
					}

					// Creates the sub catalog database.

					try {
						DiskFileSystem file_system = new DiskFileSystem(new File(directory.getAbsolutePath(), net.aerith.misao.pixy.Properties.getDatabaseDirectoryName()));
						CatalogDBManager new_manager = new GlobalDBManager(file_system).getCatalogDBManager();

						Hashtable hash_stars = new Hashtable();
						for (int i = 0 ; i < records.length ; i++) {
							CatalogStar star = records[i].getStar();

							CatalogDBReader reader = new CatalogDBReader(desktop.getDBManager().getCatalogDBManager());
							StarList list = reader.read(star.getCoor(), 0.5);
							for (int j = 0 ; j < list.size() ; j++) {
								CatalogStar s = (CatalogStar)list.elementAt(j);
								hash_stars.put(s.getOutputString(), s);
							}
						}

						keys = hash_stars.keys();
						while (keys.hasMoreElements()) {
							String string = (String)keys.nextElement();
							CatalogStar star = (CatalogStar)hash_stars.get(string);
							new_manager.addElement(star);
						}
					} catch ( Exception exception ) {
						String message = "Failed to create sub catalog database.";
						JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
					}

					String message = "Completed.";
					JOptionPane.showMessageDialog(pane, message);
				} catch ( IOException exception ) {
					String message = "Failed.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * The <code>PollingThread</code> is a thread to poll identified
	 * stars and update the ID column.
	 */
	protected class PollingThread extends Thread {
		/**
		 * True while polling.
		 */
		private boolean polling = true;

		/**
		 * Ends polling.
		 */
		public void end ( ) {
			polling = false;
		}

		/**
		 * Runs this thread and update the ID column.
		 */
		public void run ( ) {
			while (polling) {
				repaint();

				try {
					Thread.sleep(1000);
				} catch ( InterruptedException exception ) {
					break;
				}
			}

			repaint();
		}
	}
}
