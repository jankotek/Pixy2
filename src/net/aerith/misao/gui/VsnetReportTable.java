/*
 * @(#)VsnetReportTable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.io.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.table.*;
import net.aerith.misao.gui.dialog.CommonFileChooser;
import net.aerith.misao.xml.*;

/**
 * The <code>VsnetReportTable</code> represents a table which contains
 * magnitude data to be reported to VSNET/VSOLJ.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 18
 */

public class VsnetReportTable extends SortableCheckTable implements ReportDocumentUpdatedListener {
	/**
	 * The list of magnitude data records.
	 */
	protected Vector record_list;

	/**
	 * The columns.
	 */
	protected final static String[] column_names = { "", "Star", "Date", "Mag", "Observer", "Chip", "Catalog", "Image Observer", "Instruments" };

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
	 * Constructs a <code>VsnetReportTable</code> with a list of 
	 * magnitude data records.
	 * @param record_list the list of magnitude data records.
	 */
	public VsnetReportTable ( Vector record_list ) {
		this.record_list = record_list;

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
		column_model.getColumn(1).setPreferredWidth(180);
		column_model.getColumn(2).setPreferredWidth(120);
		column_model.getColumn(3).setPreferredWidth(60);
		column_model.getColumn(4).setPreferredWidth(60);
		column_model.getColumn(5).setPreferredWidth(60);
		column_model.getColumn(6).setPreferredWidth(120);
		column_model.getColumn(7).setPreferredWidth(100);
		column_model.getColumn(8).setPreferredWidth(300);
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
	}

	/**
	 * Invoked when the measured magnitude of the detected stars are
	 * updated.
	 * @param report the XML report document.
	 */
	public void photometryUpdated ( XmlReport report ) {
	}

	/**
	 * Invoked when the measured position of the detected stars are
	 * updated.
	 * @param report the XML report document.
	 */
	public void astrometryUpdated ( XmlReport report ) {
	}

	/**
	 * Invoked when some stars are added, removed or replaced.
	 * @param report the XML report document.
	 */
	public void starsUpdated ( XmlReport report ) {
	}

	/**
	 * Invoked when the image date is updated.
	 * @param report the XML report document.
	 */
	public void dateUpdated ( XmlReport report ) {
	}

	/**
	 * Invoked when a secondary record, like instruments, is updated.
	 * @param report the XML report document.
	 */
	public void recordUpdated ( XmlReport report ) {
	}

	/**
	 * Gets the output string of the cell.
	 * @param header_value the header value of the column.
	 * @param row          the index of row in original order.
	 * @return the output string of the cell.
	 */
	protected String getCellString ( String header_value, int row ) {
		VsnetRecord record = (VsnetRecord)record_list.elementAt(row);

		if (header_value.equals("Star")) {
			return record.getName();
		}
		if (header_value.equals("Date")) {
			return record.getDate();
		}
		if (header_value.equals("Mag")) {
			return record.getMag();
		}
		if (header_value.equals("Observer")) {
			return record.getObserverCode();
		}
		if (header_value.equals("Chip")) {
			return record.getChip();
		}
		if (header_value.equals("Catalog")) {
			return record.getCatalog();
		}
		if (header_value.equals("Image Observer")) {
			return record.getImageObserver();
		}
		if (header_value.equals("Instruments")) {
			return record.getInstruments();
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
		if (header_value.equals("Mag"))
			array = new Array(record_list.size());
		else 
			array = new StringArray(record_list.size());

		for (int i = 0 ; i < record_list.size() ; i++) {
			String value = getCellString(header_value, i);

			if (header_value.equals("Mag")) {
				double mag_value = 0.0;
				if ('0' <= value.charAt(0)  &&  value.charAt(0) <= '9'  ||
					value.charAt(0) == '-'  ||  value.charAt(0) == '+') {
					mag_value = Format.doubleValueOf(value);
				} else {
					mag_value = 100 + Format.doubleValueOf(value.substring(1));
				}
				((Array)array).set(i, mag_value);
			} else {
				((StringArray)array).set(i, value);
			}
		}

		return array;
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
			try {
				CommonFileChooser file_chooser = new CommonFileChooser();
				file_chooser.setDialogTitle("Save a magnitude report.");
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

					ArrayIndex index = getSortingIndex();

					int check_column = getCheckColumn();
					for (int i = 0 ; i < model.getRowCount() ; i++) {
						if (((Boolean)getValueAt(i, check_column)).booleanValue()) {
							VsnetRecord record = (VsnetRecord)record_list.elementAt(index.get(i));
							stream.println(record.getOutputString());
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
}
