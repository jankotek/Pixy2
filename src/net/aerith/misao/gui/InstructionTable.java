/*
 * @(#)InstructionTable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.gui.table.*;
import net.aerith.misao.util.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.io.FileManager;

/**
 * The <code>InstructionTable</code> represents a table where the 
 * image files and the parameters for examination are added. It shows 
 * the status of the image files and the progress of the examination.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 6
 */

public class InstructionTable extends FileOperationTable {
	/**
	 * Constructs an <code>InstructionTable</code>.
	 */
	public InstructionTable ( ) {
	}

	/**
	 * Gets the column names. This method must be overrided in the 
	 * subclasses.
	 * @return the column names.
	 */
	protected String[] getColumnNames ( ) {
		String[] column_names = { "Status", "Image" };
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

		String[] column_names = getColumnNames();
		for (int i = 1 ; i < column_names.length ; i++)
			column_model.getColumn(i).setCellRenderer(new StringRenderer(column_names[i], LabelTableCellRenderer.MODE_MULTIPLE_SELECTION));

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
		column_model.getColumn(1).setPreferredWidth(250);
	}

	/**
	 * Adds a set of instruction parameters.
	 * @param instruction  a set of instruction parameters.
	 * @param file_manager the file manager.
	 */
	public void addInstruction ( XmlInstruction instruction, FileManager file_manager ) {
		InstructionRecord record = new InstructionRecord(instruction, file_manager);
		record_list.addElement(record);

		setRows();
	}

	/**
	 * Adds somes sets of instruction parameters.
	 * @param instructions some sets of instruction parameters.
	 * @param file_manager the file manager.
	 */
	public void addInstructions ( XmlInstruction[] instructions, FileManager file_manager ) {
		for (int i = 0 ; i < instructions.length ; i++) {
			InstructionRecord record = new InstructionRecord(instructions[i], file_manager);
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

		InstructionRecord record = null;
		try {
			record = (InstructionRecord)record_list.elementAt(row);
		} catch ( ArrayIndexOutOfBoundsException exception ) {
			return "";
		}

		if (header_value.equals("Image")) {
			return record.getImagePath();
		}

		return "";
	}

	/**
	 * Gets the sortable array of the specified column.
	 * @param header_value the header value of the column to sort.
	 */
	protected SortableArray getSortableArray ( String header_value ) {
		SortableArray array = new StringArray(record_list.size());

		for (int i = 0 ; i < record_list.size() ; i++) {
			String value = getCellString(header_value, i);

			((StringArray)array).set(i, value);
		}

		return array;
	}

	/**
	 * The <code>InstructionRecord</code> is a set of an image file 
	 * and the parameters for examination.
	 */
	protected class InstructionRecord extends TableRecord {
		/**
		 * The instruction.
		 */
		protected XmlInstruction instruction;

		/**
		 * The file manager.
		 */
		protected FileManager file_manager;

		/**
		 * Construct an <code>InstructionRecord</code>.
		 * @param instruction  the instruction.
		 * @param file_manager the file manager.
		 */
		public InstructionRecord ( XmlInstruction instruction, FileManager file_manager ) {
			super(null);

			this.instruction = instruction;
			this.file_manager = file_manager;
		}

		/**
		 * Gets the file.
		 * @return the file.
		 */
		public File getFile ( ) {
			return instruction.getImageFile(file_manager);
		}

		/**
		 * Gets the image path.
		 * @return the image path.
		 */
		public String getImagePath ( ) {
			return instruction.getImage().getContent();
		}

		/**
		 * Gets the target of operation.
		 * @return the target of operation.
		 */
		public Object getOperationTarget ( ) {
			return instruction;
		}
	}
}
