/*
 * @(#)MagnitudeRecordTable.java
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
import java.util.ArrayList;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.table.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.database.*;
import net.aerith.misao.pixy.Resource;
import net.aerith.misao.io.FileManager;
import net.aerith.misao.io.XmlDBHolderCache;

/**
 * The <code>MagnitudeRecordTable</code> represents a table which 
 * contains magnitude data in the database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 6
 */

public class MagnitudeRecordTable extends SortableCheckTable {
	/**
	 * The star data.
	 */
	protected Star star;

	/**
	 * The list of magnitude data records.
	 */
	protected Vector record_list;

	/**
	 * The list of image information.
	 */
	protected Vector info_list;

	/**
	 * The list of XML files.
	 */
	protected Vector xml_list;

	/**
	 * The columns.
	 */
	protected final static String[] column_names = { "", "Image", "Date", "Mag", "VSNET", "VSOLJ", "Unofficial", "Discarded", "Preempted", "Imported", "Filter", "Chip", "Catalog", "Observer", "Instruments", "XML Status", "XML File", "ID", "Position", "Pixels from Edge", "Note"  };

	/**
	 * The table model.
	 */
	protected DefaultTableModel model;

	/**
	 * The table column model.
	 */
	protected DefaultTableColumnModel column_model;

	/**
	 * The parent desktop.
	 */
	protected BaseDesktop desktop;

	/**
	 * The container pane.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>MagnitudeRecordTable</code> with a list of 
	 * magnitude data records.
	 * @param star        the star.
	 * @param record_list the list of magnitude data records.
	 * @param desktop     the desktop.
	 */
	public MagnitudeRecordTable ( Star star, Vector record_list, BaseDesktop desktop ) {
		this.star = star;
		this.record_list = record_list;
		this.desktop = desktop;

		InformationDBManager manager = null;
		try {
			manager = desktop.getDBManager().getInformationDBManager();
		} catch ( IOException exception ) {
			// Makes no problem.
		}

		info_list = new Vector();
		xml_list = new Vector();

		try {
			// Starts using the disk cache.
			XmlDBHolderCache.enable(true);
		} catch ( IOException exception ) {
			// Makes no problem.
		}

		for (int i = 0 ; i < record_list.size() ; i++) {
			XmlMagRecord record = (XmlMagRecord)record_list.elementAt(i);

			XmlInformation info = null;
			try {
				info = desktop.getFileManager().readInformation(record, manager);
			} catch ( Exception exception ) {
			}
			info_list.addElement(info);

			File file = null;
			try {
				file = desktop.getFileManager().newFile(record.getImageXmlPath());
			} catch ( Exception exception ) {
			}
			xml_list.addElement(file);
		}

		try {
			// Stops using the disk cache, and flushes to the files.
			XmlDBHolderCache.enable(false);
		} catch ( IOException exception ) {
			// Makes no problem.
		}

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
		column_model.getColumn(1).setCellRenderer(new StatusRenderer("Image"));
		column_model.getColumn(15).setCellRenderer(new StatusRenderer("XML Status"));
		for (int i = 2 ; i < column_names.length ; i++) {
			if (i != 15)
				column_model.getColumn(i).setCellRenderer(new StringRenderer(column_names[i], LabelTableCellRenderer.MODE_MULTIPLE_SELECTION));
		}

		AttributesEditor attributes_editor = new AttributesEditor();
		for (int i = 4 ; i < 10 ; i++)
			column_model.getColumn(i).setCellEditor(attributes_editor);

		initializeCheckColumn();

		setTableHeader(new TableHeader(column_model));

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		initializeColumnWidth();

		pane = this;

		initPopupMenu();

		// Starts the polling thread.
		new PollingThread().start();
	}

	/**
	 * Initializes the column width.
	 */
	protected void initializeColumnWidth ( ) {
		column_model.getColumn(0).setPreferredWidth(20);
		column_model.getColumn(1).setPreferredWidth(80);
		column_model.getColumn(2).setPreferredWidth(150);
		column_model.getColumn(3).setPreferredWidth(60);
		column_model.getColumn(4).setPreferredWidth(20);
		column_model.getColumn(5).setPreferredWidth(20);
		column_model.getColumn(6).setPreferredWidth(20);
		column_model.getColumn(7).setPreferredWidth(20);
		column_model.getColumn(8).setPreferredWidth(20);
		column_model.getColumn(9).setPreferredWidth(20);
		column_model.getColumn(10).setPreferredWidth(40);
		column_model.getColumn(11).setPreferredWidth(60);
		column_model.getColumn(12).setPreferredWidth(60);
		column_model.getColumn(13).setPreferredWidth(120);
		column_model.getColumn(14).setPreferredWidth(80);
		column_model.getColumn(15).setPreferredWidth(80);
		column_model.getColumn(16).setPreferredWidth(180);
		column_model.getColumn(17).setPreferredWidth(80);
		column_model.getColumn(18).setPreferredWidth(120);
		column_model.getColumn(19).setPreferredWidth(40);
		column_model.getColumn(20).setPreferredWidth(200);
	}

	/**
	 * Initializes a popup menu. A <tt>popup</tt> must be created 
	 * previously.
	 */
	protected void initPopupMenu ( ) {
		super.initPopupMenu();

		popup.addSeparator();

		JMenuItem item = new JMenuItem("Save");
		item.addActionListener(new SaveListener());
		popup.add(item);

		item = new JMenuItem("Save As JD and Magnitude File");
		item.addActionListener(new SaveAsJdAndMagnitudeFileListener());
		popup.add(item);
	}

	/**
	 * Gets the list of selected records.
	 * @return the list of selected records.
	 */
	public XmlMagRecord[] getSelectedRecords ( ) {
		ArrayList list = new ArrayList();

		int check_column = getCheckColumn();
		for (int i = 0 ; i < model.getRowCount() ; i++) {
			if (((Boolean)getValueAt(i, check_column)).booleanValue()) {
				XmlMagRecord record = (XmlMagRecord)record_list.elementAt(index.get(i));
				list.add(record);
			}
		}

		XmlMagRecord[] records = new XmlMagRecord[list.size()];
        return (XmlMagRecord[])list.toArray(records);
	}

	/**
	 * Gets the output string of the cell.
	 * @param header_value the header value of the column.
	 * @param row          the index of row in original order.
	 * @return the output string of the cell.
	 */
	protected String getCellString ( String header_value, int row ) {
		XmlMagRecord record = (XmlMagRecord)record_list.elementAt(row);

		if (header_value.equals("Image")) {
			XmlInformation info = (XmlInformation)info_list.elementAt(row);
			if (info != null) {
				File file = info.getImageFile(desktop.getFileManager());
				if (file.exists())
					return "Exists";
			}
			return "Not Found";
		}

		if (header_value.equals("XML Status")) {
			File file = (File)xml_list.elementAt(row);
			if (file != null) {
				if (file.exists())
					return "Exists";
				return "Not Found";
			}
		}

		if (header_value.equals("Date")) {
			JulianDay jd = JulianDay.create(record.getDate());
			return jd.getOutputString(JulianDay.FORMAT_MONTH_IN_REDUCED, JulianDay.getAccuracy(record.getDate()));
		}
		if (header_value.equals("Mag")) {
			return ((XmlMag)record.getMag()).getOutputString();
		}
		if (header_value.equals("VSNET")) {
			if (record.isReportedToVsnet())
				return "Reported";
		}
		if (header_value.equals("VSOLJ")) {
			if (record.isReportedToVsolj())
				return "Reported";
		}
		if (header_value.equals("Unofficial")) {
			return (record.getUnofficial() == null ? "" : "Unofficial");
		}
		if (header_value.equals("Discarded")) {
			return (record.getDiscarded() == null ? "" : "Discarded");
		}
		if (header_value.equals("Preempted")) {
			return (record.getPreempted() == null ? "" : "Preempted");
		}
		if (header_value.equals("Imported")) {
			return (record.getImported() == null ? "" : "Imported");
		}
		if (header_value.equals("Filter")) {
			if (record.getFilter() != null)
				return record.getFilter();
		}
		if (header_value.equals("Chip")) {
			if (record.getChip() != null)
				return record.getChip();
		}
		if (header_value.equals("Catalog")) {
			if (record.getCatalog() != null)
				return record.getCatalog();
		}
		if (header_value.equals("Observer")) {
			return record.getObserver();
		}
		if (header_value.equals("Instruments")) {
			if (record.getInstruments() != null)
				return record.getInstruments();
		}
		if (header_value.equals("XML File")) {
			if (record.getImageXmlPath() != null)
				return record.getImageXmlPath();
		}
		if (header_value.equals("ID")) {
			if (record.getName() != null)
				return record.getName();
		}
		if (header_value.equals("Position")) {
			if (record.getPosition() != null) {
				XmlPosition position = (XmlPosition)record.getPosition();
				return position.getOutputString();
			}
		}
		if (header_value.equals("Pixels from Edge")) {
			if (record.getPixelsFromEdge() != null)
				return String.valueOf(record.getPixelsFromEdge().intValue());
		}
		if (header_value.equals("Note")) {
			if (record.getNote() != null)
				return record.getNote();
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
		if (header_value.equals("Date")  ||  header_value.equals("Mag")  ||  header_value.equals("Pixels from Edge"))
			array = new Array(record_list.size());
		else 
			array = new StringArray(record_list.size());

		for (int i = 0 ; i < record_list.size() ; i++) {
			String value = getCellString(header_value, i);

			if (header_value.equals("Date")) {
				double jd = JulianDay.create(value).getJD();
				((Array)array).set(i, jd);
			} else if (header_value.equals("Mag")) {
				double mag_value = 0.0;
				if ('0' <= value.charAt(0)  &&  value.charAt(0) <= '9'  ||
					value.charAt(0) == '-'  ||  value.charAt(0) == '+') {
					mag_value = Format.doubleValueOf(value);
				} else {
					mag_value = 100 + Format.doubleValueOf(value.substring(1));
				}
				((Array)array).set(i, mag_value);
			} else if (header_value.equals("Pixels from Edge")) {
				int pixels = -1;
				if (value.length() > 0)
					pixels = Integer.parseInt(value);
				((Array)array).set(i, pixels);
			} else {
				((StringArray)array).set(i, value);
			}
		}

		return array;
	}

	/**
	 * Restores the images.
	 * @param directory_from the directory to copy files from.
	 * @param directory_to   the directory to copy files to.
	 */
	public void restoreImages ( File directory_from, File directory_to ) {
		Vector copied_list = new Vector();

		int check_column = getCheckColumn();
		for (int i = 0 ; i < model.getRowCount() ; i++) {
			if (((Boolean)getValueAt(i, check_column)).booleanValue()) {
				XmlInformation info = (XmlInformation)info_list.elementAt(index.get(i));
				if (info != null) {
					File file = info.getImageFile(desktop.getFileManager());
					if (file.exists() == false) {
						try {
							String src_relative_path = FileManager.relativatePathFromDirectory(file.getPath(), directory_to);
							File src_file = FileManager.find(directory_from, new File(src_relative_path));
							FileManager.copy(src_file, file);

							String message = src_file.getAbsolutePath() + " -> " + file.getPath();
							copied_list.addElement(message);
						} catch ( FileNotFoundException exception ) {
						} catch ( IOException exception ) {
						}
					}
				}
			}
		}

		if (copied_list.size() == 0) {
			JOptionPane.showMessageDialog(pane, "Failed.", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			String header = "Succeeded to copy:";
			MessagesDialog dialog = new MessagesDialog(header, copied_list);
			dialog.show(pane);
		}
	}

	/**
	 * Restores the XML files.
	 * @param directory_from the directory to copy files from.
	 * @param directory_to   the directory to copy files to.
	 */
	public void restoreXmlFiles ( File directory_from, File directory_to ) {
		Vector copied_list = new Vector();

		int check_column = getCheckColumn();
		for (int i = 0 ; i < model.getRowCount() ; i++) {
			if (((Boolean)getValueAt(i, check_column)).booleanValue()) {
				File file = (File)xml_list.elementAt(index.get(i));
				if (file != null  &&  file.exists() == false) {
					try {
						String src_relative_path = FileManager.relativatePathFromDirectory(file.getPath(), directory_to);
						File src_file = FileManager.find(directory_from, new File(src_relative_path));
						FileManager.copy(src_file, file);

						String message = src_file.getAbsolutePath() + " -> " + file.getPath();
						copied_list.addElement(message);
					} catch ( FileNotFoundException exception ) {
					} catch ( IOException exception ) {
					}
				}
			}
		}

		if (copied_list.size() == 0) {
			JOptionPane.showMessageDialog(pane, "Failed.", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			String header = "Succeeded to copy:";
			MessagesDialog dialog = new MessagesDialog(header, copied_list);
			dialog.show(pane);
		}
	}

	/**
	 * Updates the attributes of the selected records.
	 * @param attributes the attributes.
	 */
	public void updateAttributes ( MagnitudeRecordAttributes attributes ) {
		try {
			// Starts using the disk cache.
			XmlDBHolderCache.enable(true);

			int check_column = getCheckColumn();
			for (int i = 0 ; i < model.getRowCount() ; i++) {
				if (((Boolean)getValueAt(i, check_column)).booleanValue()) {
					XmlMagRecord record = (XmlMagRecord)record_list.elementAt(index.get(i));

					record.setAttributes(attributes);

					if (desktop.isMagnitudeDatabaseReadOnly() == false) {
						try {
							MagnitudeDBManager manager = desktop.getDBManager().getMagnitudeDBManager();
							manager.deleteElement((CatalogStar)star, record);
							manager.addElement((CatalogStar)star, record);
						} catch ( ClassCastException exception ) {
							String message = "Failed to record the attributes in the database.";
							JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
						} catch ( IOException exception ) {
							String message = "Failed to record the attributes in the database.";
							JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}

			// Stops using the disk cache, and flushes to the files.
			XmlDBHolderCache.enable(false);
		} catch ( IOException exception ) {
			String message = "Failed to record the attributes in the database.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}

		repaint();
	}

	/**
	 * The <code>StatusRenderer</code> is a renderer for the status
	 * column.
	 */
	protected class StatusRenderer extends LabelTableCellRenderer {
		/**
		 * The header value.
		 */
		protected String header_value = "Status";

		/**
		 * Constructs a <code>StatusRenderer</code>.
		 */
		public StatusRenderer ( ) {
			super(LabelTableCellRenderer.MODE_MULTIPLE_SELECTION);
		}

		/**
		 * Constructs a <code>StatusRenderer</code>.
		 * @param header_value the header value.
		 */
		public StatusRenderer ( String header_value ) {
			super(LabelTableCellRenderer.MODE_MULTIPLE_SELECTION);

			this.header_value = header_value;
		}

		/**
		 * Gets the string of the specified row. It must be overrided in
		 * the subclasses.
		 * @param row the row.
		 * @return the string to show.
		 */
		public String getStringAt ( int row ) {
			if (getSortingIndex() == null)
				return null;

			return getCellString(header_value, getSortingIndex().get(row));
		}

		/**
		 * Gets the icon of the specified row. It must be overrided in the
		 * subclasses.
		 * @param row the row.
		 * @return the icon to show.
		 */
		public Icon getIconAt ( int row ) {
			if (getSortingIndex() == null)
				return null;

			String status = getCellString(header_value, getSortingIndex().get(row));

			if (status.equals("Exists"))
				return Resource.getStatusSuccessIcon();
			if (status.equals("Not Found"))
				return Resource.getStatusErrorIcon();

			return null;
		}
	}

	/**
	 * The <code>SaveListener</code> is a listener class of menu 
	 * selection to save the report in a file.
	 */
	protected class SaveListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			ArrayIndex index = getSortingIndex();
			if (index == null)
				return;

			try {
				CommonFileChooser file_chooser = new CommonFileChooser();
				file_chooser.setDialogTitle("Save selected data.");
				file_chooser.setMultiSelectionEnabled(false);

				if (file_chooser.showSaveDialog(pane) == JFileChooser.APPROVE_OPTION) {
					File file = file_chooser.getSelectedFile();
					if (file.exists()) {
						String message = "Overwrite to " + file.getPath() + " ?";
						if (0 != JOptionPane.showConfirmDialog(pane, message, "Confirmation", JOptionPane.YES_NO_OPTION)) {
							return;
						}
					}

					PrintStream stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)));

					int check_column = getCheckColumn();

					int[] length = new int[column_names.length];
					for (int i = 0 ; i < model.getRowCount() ; i++) {
						if (((Boolean)getValueAt(i, check_column)).booleanValue()) {
							for (int j = 0 ; j < column_names.length ; j++) {
								String value = getCellString(column_names[j], index.get(i));
								if (length[j] < value.length())
									length[j] = value.length();
							}
						}
					}

					for (int i = 0 ; i < model.getRowCount() ; i++) {
						if (((Boolean)getValueAt(i, check_column)).booleanValue()) {
							String[] values = new String[6];
							for (int j = 0 ; j < 6 ; j++) {
								int column = j;
								switch (j) {
									case 0:	column = 2;	break;
									case 1:	column = 3;	break;
									case 2:	column = 10;	break;
									case 3:	column = 11;	break;
									case 4:	column = 12;	break;
									case 5:	column = 14;	break;
								}

								values[j] = getCellString(column_names[column], index.get(i));
								if (j < 5) {
									while (values[j].length() < length[column]) {
										if (column == 3)	// Mag
											values[j] = " " + values[j];
										else
											values[j] += " ";
									}
								}
							}

							String data = values[0] + "  " + values[1] + values[2] + "  " + values[3] + "  " + values[4] + "  " + values[5];
							stream.println(data);
						}
					}

					stream.println("");

					for (int i = 0 ; i < model.getRowCount() ; i++) {
						if (((Boolean)getValueAt(i, check_column)).booleanValue()) {
							String[] values = new String[3];
							for (int j = 0 ; j < 3 ; j++) {
								int column = j;
								switch (j) {
									case 0:	column = 2;	break;
									case 1:	column = 18;	break;
									case 2:	column = 16;	break;
								}

								values[j] = getCellString(column_names[column], index.get(i));
								if (j < 2) {
									while (values[j].length() < length[column])
										values[j] += " ";
								}
							}

							String data = values[0] + "  " + values[1] + "  " + values[2];
							stream.println(data);
						}
					}

					stream.close();

					String message = "Succeeded to save " + file.getPath();
					JOptionPane.showMessageDialog(pane, message);
				}
			} catch ( IOException exception ) {
				String message = "Failed to save file.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * The <code>SaveAsJdAndMagnitudeFileListener</code> is a listener 
	 * class of menu selection to save the JD and magnitude into a 
	 * file.
	 */
	protected class SaveAsJdAndMagnitudeFileListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			ArrayIndex index = getSortingIndex();
			if (index == null)
				return;

			try {
				CommonFileChooser file_chooser = new CommonFileChooser();
				file_chooser.setDialogTitle("Save selected data.");
				file_chooser.setMultiSelectionEnabled(false);

				if (file_chooser.showSaveDialog(pane) == JFileChooser.APPROVE_OPTION) {
					File file = file_chooser.getSelectedFile();
					if (file.exists()) {
						String message = "Overwrite to " + file.getPath() + " ?";
						if (0 != JOptionPane.showConfirmDialog(pane, message, "Confirmation", JOptionPane.YES_NO_OPTION)) {
							return;
						}
					}

					PrintStream stream = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)));

					int check_column = getCheckColumn();

					for (int i = 0 ; i < model.getRowCount() ; i++) {
						if (((Boolean)getValueAt(i, check_column)).booleanValue()) {
							XmlMagRecord record = (XmlMagRecord)record_list.elementAt(index.get(i));

							JulianDay jd = JulianDay.create(record.getDate());
							String date = jd.getOutputString(JulianDay.FORMAT_JD, JulianDay.getAccuracy(record.getDate()));

							String mag = ((XmlMag)record.getMag()).getOutputString();

							stream.println(date + "\t" + mag);
						}
					}

					stream.close();

					String message = "Succeeded to save " + file.getPath();
					JOptionPane.showMessageDialog(pane, message);
				}
			} catch ( IOException exception ) {
				String message = "Failed to save file.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * The <code>AttributesEditor</code> represents a table cell 
	 * editor to set the attributes.
	 */
	protected class AttributesEditor extends ButtonCellEditor {
		/**
		 * Gets the cell editor listener. It must be overrided in the
		 * subclasses.
		 * @param object the object to edit.
		 * @param editor the cell editor.
		 * @return the cell editor listener.
		 */
		protected ButtonCellEditorListener getListener ( ) {
			return new AttributesListener();
		}

		/**
		 * Gets the object to edit.
		 * @param row    the row to edit.
		 * @param column the column to edit.
		 * @return the object to edit.
		 */
		protected Object getEditingObject ( int row, int column ) {
			ArrayIndex index = getSortingIndex();
			if (index == null)
				return null;

			try {
				return record_list.elementAt(index.get(row));
			} catch ( ArrayIndexOutOfBoundsException exception ) {
				return null;
			}
		}
	}

	/**
	 * The <code>AttributesListener</code> is a listener class of 
	 * button selection to set the attributes.
	 */
	protected class AttributesListener extends ButtonCellEditorListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			XmlMagRecord record = (XmlMagRecord)object;

			if (object != null) {
				AttributesDialog dialog = new AttributesDialog(record.getAttributes());

				int answer = dialog.show(pane);

				if (answer == 0) {
					MagnitudeRecordAttributes attributes = dialog.getAttributes();
					record.setAttributes(attributes);

					if (desktop.isMagnitudeDatabaseReadOnly() == false) {
						try {
							MagnitudeDBManager manager = desktop.getDBManager().getMagnitudeDBManager();
							manager.deleteElement((CatalogStar)star, record);
							manager.addElement((CatalogStar)star, record);
						} catch ( ClassCastException exception ) {
							String message = "Failed to record the attributes in the database.";
							JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
						} catch ( IOException exception ) {
							String message = "Failed to record the attributes in the database.";
							JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}

			editor.stopCellEditing();
			repaint();
		}
	}

	/**
	 * The <code>PollingThread</code> is a thread to poll files and
	 * update the status column.
	 */
	protected class PollingThread extends Thread {
		/**
		 * Runs this thread and update the status column.
		 */
		public void run ( ) {
			while (true) {
				repaint();

				try {
					Thread.sleep(1000);
				} catch ( InterruptedException exception ) {
					break;
				}
			}
		}
	}
}
