/*
 * @(#)InformationTable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.gui.table.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.io.*;
import net.aerith.misao.io.filechooser.XmlFilter;
import net.aerith.misao.util.*;
import net.aerith.misao.xml.*;

/**
 * The <code>InformationTable</code> represents a table where the XML
 * report documents are added for an operation. It shows the status of
 * the XML files and the progress of the operation.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 December 21
 */

public class InformationTable extends FileOperationTable {
	/**
	 * The default file manager.
	 */
	protected final static FileManager default_file_manager = new FileManager();

	/**
	 * Constructs an <code>InformationTable</code>.
	 */
	public InformationTable ( ) {
	}

	/**
	 * Initializes a popup menu. A <tt>popup</tt> must be created 
	 * previously.
	 */
	protected void initPopupMenu ( ) {
		super.initPopupMenu();

		popup.addSeparator();

		JMenuItem item = new JMenuItem("Delete XML files");
		item.addActionListener(new DeleteXmlFilesListener());
		popup.add(item);
	}

	/**
	 * Gets the column names. This method must be overrided in the 
	 * subclasses.
	 * @return the column names.
	 */
	protected String[] getColumnNames ( ) {
		String[] column_names = { "Status", "XML File", "Date", "Limiting Mag.", "Upper-Limit Mag.", "Pixel Size", "Filter", "Chip", "Instruments", "Base Catalog", "Photometric Catalog", "STRs", "NEWs", "ERRs", "NEGs", "Unofficial", "Center R.A.", "Center Decl.", "Field of View", "Image Status", "Image File", "Image Size", "Observer", "Note" };
		return column_names;
	}

	/**
	 * Creates the column model. This method must be overrided in the 
	 * subclasses.
	 * @return the column model.
	 */
	protected DefaultTableColumnModel createColumnModel ( ) {
		column_model = (DefaultTableColumnModel)getColumnModel();
		column_model.getColumn(0).setCellRenderer(new StatusRenderer());
		column_model.getColumn(19).setCellRenderer(new StatusRenderer("Image Status"));

		String[] column_names = getColumnNames();
		for (int i = 1 ; i < column_names.length ; i++) {
			if (i != 19)
				column_model.getColumn(i).setCellRenderer(new StringRenderer(column_names[i], LabelTableCellRenderer.MODE_MULTIPLE_SELECTION));
		}

		return column_model;
	}

	/**
	 * Gets the file drop target listener. This method must be 
	 * overrided in the subclasses.
	 * @return the file drop target listener.
	 */
	protected FileDropTargetAdapter getFileDropTargetListener ( ) {
		return new XmlFileDropTargetListener();
	}

	/**
	 * Initializes the column width.
	 */
	protected void initializeColumnWidth ( ) {
		int columns = column_model.getColumnCount();

		column_model.getColumn(0).setPreferredWidth(100);
		column_model.getColumn(1).setPreferredWidth(250);
		column_model.getColumn(2).setPreferredWidth(200);
		column_model.getColumn(3).setPreferredWidth(60);
		column_model.getColumn(4).setPreferredWidth(60);
		column_model.getColumn(5).setPreferredWidth(60);
		column_model.getColumn(6).setPreferredWidth(40);
		column_model.getColumn(7).setPreferredWidth(80);
		column_model.getColumn(8).setPreferredWidth(80);
		column_model.getColumn(9).setPreferredWidth(120);
		column_model.getColumn(10).setPreferredWidth(120);
		column_model.getColumn(11).setPreferredWidth(60);
		column_model.getColumn(12).setPreferredWidth(60);
		column_model.getColumn(13).setPreferredWidth(60);
		column_model.getColumn(14).setPreferredWidth(60);
		column_model.getColumn(15).setPreferredWidth(40);
		column_model.getColumn(16).setPreferredWidth(100);
		column_model.getColumn(17).setPreferredWidth(100);
		column_model.getColumn(18).setPreferredWidth(100);
		column_model.getColumn(19).setPreferredWidth(80);
		column_model.getColumn(20).setPreferredWidth(200);
		column_model.getColumn(21).setPreferredWidth(100);
		column_model.getColumn(22).setPreferredWidth(120);
		column_model.getColumn(23).setPreferredWidth(120);
	}

	/**
	 * Adds an XML file.
	 * @param file the XML file.
	 * @exception IOException if I/O error occurs.
	 * @exception FileNotFoundException if a file does not exists.
	 */
	public void addXmlFile ( File file )
		throws IOException, FileNotFoundException
	{
		addXmlFile(file, default_file_manager);
	}

	/**
	 * Adds XML files.
	 * @param files the XML files.
	 * @exception IOException if I/O error occurs.
	 * @exception FileNotFoundException if a file does not exists.
	 */
	public void addXmlFiles ( File[] files )
		throws IOException, FileNotFoundException
	{
		addXmlFiles(files, default_file_manager);
	}

	/**
	 * Adds an XML file.
	 * @param file         the XML file.
	 * @param file_manager the file manager.
	 * @exception IOException if I/O error occurs.
	 * @exception FileNotFoundException if a file does not exists.
	 */
	public void addXmlFile ( File file, FileManager file_manager )
		throws IOException, FileNotFoundException
	{
		XmlInformation info = XmlReport.readInformation(file);

		InformationRecord record = new InformationRecord(info, file_manager);
		record_list.addElement(record);

		setRows();
	}

	/**
	 * Adds XML files.
	 * @param files        the XML files.
	 * @param file_manager the file manager.
	 * @exception IOException if I/O error occurs.
	 * @exception FileNotFoundException if a file does not exists.
	 */
	public void addXmlFiles ( File[] files, FileManager file_manager )
		throws IOException, FileNotFoundException
	{
		for (int i = 0 ; i < files.length ; i++) {
			XmlInformation info = XmlReport.readInformation(files[i]);

			InformationRecord record = new InformationRecord(info, file_manager);
			record_list.addElement(record);
		}

		setRows();
	}

	/**
	 * Adds an XML information document. The XML file path must be 
	 * recorded in the information document.
	 * @param info         the XML information document.
	 * @param file_manager the file manager.
	 * @exception IOException if I/O error occurs.
	 * @exception FileNotFoundException if a file does not exists.
	 */
	public void addInformation ( XmlInformation info, FileManager file_manager )
		throws IOException, FileNotFoundException
	{
		if (info.getPath() == null)
			throw new FileNotFoundException();

		InformationRecord record = new InformationRecord(info, file_manager);
		record_list.addElement(record);

		setRows();
	}

	/**
	 * Adds XML information documents. The XML file path must be 
	 * recorded in the information documents.
	 * @param infos        the XML information documents.
	 * @param file_manager the file manager.
	 * @exception IOException if I/O error occurs.
	 * @exception FileNotFoundException if a file does not exists.
	 */
	public void addInformations ( XmlInformation[] infos, FileManager file_manager )
		throws IOException, FileNotFoundException
	{
		for (int i = 0 ; i < infos.length ; i++) {
			if (infos[i].getPath() == null)
				throw new FileNotFoundException();
		}

		for (int i = 0 ; i < infos.length ; i++) {
			InformationRecord record = new InformationRecord(infos[i], file_manager);
			record_list.addElement(record);
		}

		setRows();
	}

	/**
	 * Gets the output string of the cell.
	 * @param header_value the header value of the column.
	 * @param row          the index of row in original order.
	 * @return the output string of the cell.
	 */
	protected String getCellString ( String header_value, int row ) {
		String s = super.getCellString(header_value, row);
		if (s.length() > 0)
			return s;

		InformationRecord record = null;
		try {
			record = (InformationRecord)record_list.elementAt(row);
		} catch ( ArrayIndexOutOfBoundsException exception ) {
			return "";
		}

		if (header_value.equals("Image Status")) {
			if (record.getImageFile().exists())
				return "Exists";
			return "Not Found";
		}

		if (header_value.equals("Unofficial")) {
			if (record.info.getUnofficial() == null)
				return "";
			return "Unofficial";
		}

		String key = header_value;
		if (header_value.equals("STRs")  ||  header_value.equals("NEWs")  ||  header_value.equals("ERRs")  ||  header_value.equals("NEGs"))
			key = "Number of " + key;

		return record.getValue(key);
	}

	/**
	 * Gets the sortable array of the specified column.
	 * @param header_value the header value of the column to sort.
	 */
	protected SortableArray getSortableArray ( String header_value ) {
		SortableArray array = null;
		if (header_value.equals("Date")  ||  header_value.equals("Limiting Mag.")  ||  header_value.equals("Upper-Limit Mag.")  ||  header_value.equals("STRs")  ||  header_value.equals("NEWs")  ||  header_value.equals("ERRs")  ||  header_value.equals("NEGs")) {
			array = new Array(record_list.size());
		} else {
			array = new StringArray(record_list.size());
		}

		for (int i = 0 ; i < record_list.size() ; i++) {
			String value = getCellString(header_value, i);

			if (header_value.equals("Date")) {
				double jd = JulianDay.create(value).getJD();
				((Array)array).set(i, jd);
			} else if (header_value.equals("Limiting Mag.")  ||  header_value.equals("Upper-Limit Mag.")) {
				((Array)array).set(i, Format.doubleValueOf(value));
			} else if (header_value.equals("STRs")  ||  header_value.equals("NEWs")  ||  header_value.equals("ERRs")  ||  header_value.equals("NEGs")) {
				((Array)array).set(i, Integer.parseInt(value));
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

		for (int i = 0 ; i < record_list.size() ; i++) {
			InformationRecord record = (InformationRecord)record_list.elementAt(i);
			File file = record.getImageFile();
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

		for (int i = 0 ; i < record_list.size() ; i++) {
			InformationRecord record = (InformationRecord)record_list.elementAt(i);
			File file = record.getFile();
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

		if (copied_list.size() == 0) {
			JOptionPane.showMessageDialog(pane, "Failed.", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			String header = "Succeeded to copy:";
			MessagesDialog dialog = new MessagesDialog(header, copied_list);
			dialog.show(pane);
		}
	}

	/**
	 * The <code>XmlFileDropTargetListener</code> is a listener class
	 * of drop event from native filer application.
	 */
	protected class XmlFileDropTargetListener extends FileDropTargetAdapter {
		/**
		 * The xml file filter.
		 */
		private XmlFilter filter = new XmlFilter();

		/**
		 * Invoked when files are dropped.
		 * @param files the dropped files.
		 */
		public void dropFiles ( File[] files ) {
			if (mode == MODE_OPERATING)
				return;

			ArrayList list = new ArrayList();
			for (int i = 0 ; i < files.length ; i++) {
				if (files[i].isDirectory()) {
					File[] files2 = files[i].listFiles();
					dropFiles(files2);
				} else {
					if (filter.accept(files[i]))
						list.add(files[i]);
				}
			}

			files = new File[list.size()];
			files = (File[])list.toArray(files);

			try {
				addXmlFiles(files);
			} catch ( FileNotFoundException exception ) {
			} catch ( IOException exception ) {
			}
		}
	}

	/**
	 * The <code>DeleteXmlFilesListener</code> is a listener class of 
	 * menu selection to delete selected XML files.
	 */
	protected class DeleteXmlFilesListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			ArrayIndex index = getSortingIndex();
			if (index == null)
				return;

			int[] rows = getSelectedRows();
			for (int i = 0 ; i < rows.length ; i++) {
				InformationRecord record = (InformationRecord)record_list.elementAt(index.get(rows[i]));
				if (record.getFile().exists())
					record.getFile().delete();
			}

			String message = "Completed.";
			JOptionPane.showMessageDialog(getPane(), message);
		}
	}

	/**
	 * The <code>InformationRecord</code> is a set of XML file and the
	 * status.
	 */
	protected class InformationRecord extends TableRecord {
		/**
		 * The image information.
		 */
		protected XmlInformation info;

		/**
		 * The file manager.
		 */
		protected FileManager file_manager;

		/**
		 * The hash table of keys and values.
		 */
		protected Hashtable hash;

		/**
		 * Construct an <code>InformationRecord</code>.
		 * @param info         the image information.
		 * @param file_manager the file manager.
		 */
		public InformationRecord ( XmlInformation info, FileManager file_manager ) {
			super(new File(info.getPath()));

			this.info = info;
			this.file_manager = file_manager;

			hash = new Hashtable();
			KeyAndValue[] key_and_values = info.getKeyAndValues();
			for (int i = 0 ; i < key_and_values.length ; i++)
				hash.put(key_and_values[i].getKey(), key_and_values[i].getValue());

			if (info.getDate() != null)
				hash.put("Date", info.getMidDate().getOutputString(JulianDay.FORMAT_MONTH_IN_REDUCED, JulianDay.getAccuracy(info.getDate())));
		}

		/**
		 * Gets the image information.
		 * @return the image information.
		 */
		public XmlInformation getInformation ( ) {
			return info;
		}

		/**
		 * Gets the image file.
		 * @return the image file.
		 */
		public File getImageFile ( ) {
			return info.getImageFile(file_manager);
		}

		/**
		 * Gets the value of the specified key.
		 * @param key the key.
		 * @return the value of the specified key.
		 */
		public String getValue ( String key ) {
			String s = (String)hash.get(key);
			if (s != null)
				return s;
			return "";
		}

		/**
		 * Gets the target of operation.
		 * @return the target of operation.
		 */
		public Object getOperationTarget ( ) {
			return info;
		}
	}
}
