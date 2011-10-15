/*
 * @(#)HtmlImageGalleryTable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.HtmlImageGallery;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.gui.table.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.io.FileManager;
import net.aerith.misao.xml.*;
import net.aerith.misao.database.*;

/**
 * The <code>HtmlImageGalleryTable</code> represents a table which 
 * shows the images and XML files, and the progress to create HTML
 * image gallery.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 6
 */

public class HtmlImageGalleryTable extends FileOperationTable {
	/**
	 * The parent desktop.
	 */
	protected BaseDesktop desktop;

	/**
	 * Constructs a <code>HtmlImageGalleryTable</code>.
	 * @param variability_list the list of variability records.
	 * @param mode             the mode to create HTML image gallery.
	 * @param desktop          the parent desktop.
	 */
	public HtmlImageGalleryTable ( Vector variability_list, int mode, BaseDesktop desktop ) {
		this.desktop = desktop;

		InformationDBManager manager = null;
		try {
			manager = desktop.getDBManager().getInformationDBManager();
		} catch ( IOException exception ) {
			// Makes no problem.
		}

		for (int i = 0 ; i < variability_list.size() ; i++) {
			Variability variability = (Variability)variability_list.elementAt(i);

			XmlMagRecord[] records = variability.getMagnitudeRecords();

			// Sorts in order of date.
			Array array = new Array(records.length);
			for (int j = 0 ; j < records.length ; j++) {
				JulianDay jd = JulianDay.create(records[j].getDate());
				array.set(j, jd.getJD());
			}
			ArrayIndex index = array.sortAscendant();
			XmlMagRecord[] new_records = new XmlMagRecord[records.length];
			for (int j = 0 ; j < records.length ; j++)
				new_records[j] = records[index.get(j)];
			records = new_records;

			array = new Array(records.length);
			for (int j = 0 ; j < records.length ; j++) {
				String value = ((XmlMag)records[j].getMag()).getOutputString();
				double mag_value = 0.0;
				if ('0' <= value.charAt(0)  &&  value.charAt(0) <= '9'  ||
					value.charAt(0) == '-'  ||  value.charAt(0) == '+') {
					mag_value = net.aerith.misao.util.Format.doubleValueOf(value);
				} else {
					mag_value = 100 + net.aerith.misao.util.Format.doubleValueOf(value.substring(1));
				}
				array.set(j, mag_value);
			}

			index = array.sortAscendant();

			XmlMagRecord[] image_records = records;
			if (mode == HtmlImageGallerySettingDialog.MODE_BRIGHTEST) {
				image_records = new XmlMagRecord[1];
				image_records[0] = records[index.get(0)];
			} else if (mode == HtmlImageGallerySettingDialog.MODE_BRIGHTEST_FAINTEST) {
				image_records = new XmlMagRecord[2];
				image_records[0] = records[index.get(0)];
				image_records[1] = records[index.get(records.length - 1)];
			}

			for (int j = 0 ; j < image_records.length ; j++) {
				try {
					XmlInformation info = desktop.getFileManager().readInformation(image_records[j], manager);

					ImageRecord record = new ImageRecord(variability, image_records[j], info);
					record_list.addElement(record);
				} catch ( Exception exception ) {
				}
			}
		}

		setRows();
	}

	/**
	 * Gets the column names. This method must be overrided in the 
	 * subclasses.
	 * @return the column names.
	 */
	protected String[] getColumnNames ( ) {
		String[] column_names = { "Status", "R.A.", "Decl.", "ID", "Date", "Image", "XML Status", "XML File" };
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
		column_model.getColumn(6).setCellRenderer(new StatusRenderer("XML Status"));

		String[] column_names = getColumnNames();
		for (int i = 1 ; i < column_names.length ; i++) {
			if (i != 6)
				column_model.getColumn(i).setCellRenderer(new StringRenderer(column_names[i], LabelTableCellRenderer.MODE_NO_SELECTION));
		}

		return column_model;
	}

	/**
	 * Gets the file drop target listener. This method must be 
	 * overrided in the subclasses.
	 * @return the file drop target listener.
	 */
	protected FileDropTargetAdapter getFileDropTargetListener ( ) {
		// Impossible to drop a file.
		return null;
	}

	/**
	 * Initializes the column width.
	 */
	protected void initializeColumnWidth ( ) {
		int columns = column_model.getColumnCount();

		column_model.getColumn(0).setPreferredWidth(100);
		column_model.getColumn(1).setPreferredWidth(100);
		column_model.getColumn(2).setPreferredWidth(100);
		column_model.getColumn(3).setPreferredWidth(100);
		column_model.getColumn(4).setPreferredWidth(200);
		column_model.getColumn(5).setPreferredWidth(250);
		column_model.getColumn(6).setPreferredWidth(80);
		column_model.getColumn(7).setPreferredWidth(250);
	}

	/**
	 * Adds a file. It is impossible in this class.
	 * @param file the file.
	 */
	protected void addFile ( File file ) {
	}

	/**
	 * Returns true when sorting is acceptable.
	 * @return true when sorting is acceptable.
	 */
	protected boolean acceptsSorting ( ) {
		return false;
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

		ImageRecord record = null;
		try {
			record = (ImageRecord)record_list.elementAt(row);
		} catch ( ArrayIndexOutOfBoundsException exception ) {
			return "";
		}

		if (header_value.equals("XML Status")) {
			if (record.getXmlFile().exists())
				return "Exists";
			return "Not Found";
		}

		Variability variability = record.getVariability();

		if (header_value.equals("R.A.")) {
			String coor = variability.getStar().getCoor().getOutputString();
			int p = coor.indexOf(' ');
			return coor.substring(0, p);
		}
		if (header_value.equals("Decl.")) {
			String coor = variability.getStar().getCoor().getOutputString();
			int p = coor.indexOf(' ');
			return coor.substring(p + 1);
		}
		if (header_value.equals("ID")) {
			if (variability.getIdentifiedStar() != null)
				return variability.getIdentifiedStar().getName();
		}

		XmlInformation info = record.getInformation();
		XmlMagRecord mag_record = record.getMagRecord();

		if (header_value.equals("Date")) {
			if (mag_record.getDate() != null)
				return mag_record.getDate();
		}
		if (header_value.equals("Image")) {
			if (info.getImage() != null)
				return info.getImage().getContent();
		}
		if (header_value.equals("XML File")) {
			if (mag_record.getImageXmlPath() != null)
				return mag_record.getImageXmlPath();
		}

		return "";
	}

	/**
	 * Restores the images.
	 * @param directory_from the directory to copy files from.
	 * @param directory_to   the directory to copy files to.
	 */
	public void restoreImages ( File directory_from, File directory_to ) {
		Vector copied_list = new Vector();

		for (int i = 0 ; i < record_list.size() ; i++) {
			ImageRecord record = (ImageRecord)record_list.elementAt(i);
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
	 * Restores the XML files.
	 * @param directory_from the directory to copy files from.
	 * @param directory_to   the directory to copy files to.
	 */
	public void restoreXmlFiles ( File directory_from, File directory_to ) {
		Vector copied_list = new Vector();

		for (int i = 0 ; i < record_list.size() ; i++) {
			ImageRecord record = (ImageRecord)record_list.elementAt(i);
			File file = record.getXmlFile();
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
	 * The <code>ImageRecord</code> represents a record for one image.
	 */
	protected class ImageRecord extends TableRecord {
		/**
		 * The variability.
		 */
		protected Variability variability;

		/**
		 * The magnitude record.
		 */
		protected XmlMagRecord mag_record;

		/**
		 * The image information.
		 */
		protected XmlInformation info;

		/**
		 * The XML file.
		 */
		protected File xml_file;

		/**
		 * Construct an <code>ImageRecord</code>.
		 * @param variability the variability.
		 * @param mag_record  the magnitude record.
		 * @param info        the image information.
		 */
		public ImageRecord ( Variability variability, XmlMagRecord mag_record, XmlInformation info ) {
			super(null);

			this.variability = variability;
			this.mag_record = mag_record;
			this.info = info;

			this.xml_file = desktop.getFileManager().newFile(mag_record.getImageXmlPath());
		}

		/**
		 * Gets the file.
		 * @return the file.
		 */
		public File getFile ( ) {
			return info.getImageFile(desktop.getFileManager());
		}

		/**
		 * Gets the variability.
		 * @return the variability.
		 */
		public Variability getVariability ( ) {
			return variability;
		}

		/**
		 * Gets the magnitude record.
		 * @return the magnitude record.
		 */
		public XmlMagRecord getMagRecord ( ) {
			return mag_record;
		}

		/**
		 * Gets the image information.
		 * @return the image information.
		 */
		public XmlInformation getInformation ( ) {
			return info;
		}

		/**
		 * Gets the XML file.
		 * @return the XML file.
		 */
		public File getXmlFile ( ) {
			return xml_file;
		}

		/**
		 * Gets the target of operation.
		 * @return the target of operation.
		 */
		public Object getOperationTarget ( ) {
			return this;
		}
	}
}
