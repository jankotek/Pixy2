/*
 * @(#)GroupTable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.VariableStarSearch;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.table.*;
import net.aerith.misao.io.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.util.MaximumRepetitionCountException;

/**
 * The <code>GroupTable</code> represents a table where the XML report 
 * documents are added and classified into some groups to search 
 * variable stars. It shows the status of the XML files and the 
 * progress of the operation.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 May 4
 */

public class GroupTable extends VariableStarSearchTable {
	/**
	 * The number of groups.
	 */
	protected int group_count = 0;

	/**
	 * The current group.
	 */
	protected int current_group = 0;

	/**
	 * The list of group numbers of each records.
	 */
	protected Vector group_list = new Vector();

	/**
	 * Constructs a <code>GroupTable</code>.
	 * @param desktop the desktop.
	 */
	public GroupTable ( VariableStarSearchDesktop desktop ) {
		super(desktop);
	}

	/**
	 * Adds a new group.
	 */
	public void addGroup ( ) {
		group_count++;
	}

	/**
	 * Gets the current group number.
	 * @return the current group number.
	 */
	public int getCurrentGroupNumber ( ) {
		return current_group;
	}

	/**
	 * Proceeds the current group number.
	 * @exception MaximumRepetitionCountException if the current group
	 * exceeds the last group.
	 */
	public void proceedGroup ( )
		throws MaximumRepetitionCountException
	{
		current_group++;

		if (current_group >= group_count) {
			current_group = 0;
			throw new MaximumRepetitionCountException();
		}
	}

	/**
	 * Resets the current group number.
	 */
	public void resetCurrentGroupNumber ( ) {
		current_group = 0;
	}

	/**
	 * Gets the column names. This method must be overrided in the 
	 * subclasses.
	 * @return the column names.
	 */
	protected String[] getColumnNames ( ) {
		String[] column_names = { "Status", "Group", "XML File", "Date", "Limiting Mag.", "Upper-Limit Mag.", "Pixel Size", "Filter", "Chip", "Instruments", "Base Catalog", "Photometric Catalog", "STRs", "NEWs", "ERRs", "NEGs", "Unofficial", "Center R.A.", "Center Decl.", "Field of View", "Image Status", "Image File", "Image Size", "Observer", "Note" };
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
		column_model.getColumn(20).setCellRenderer(new StatusRenderer("Image Status"));

		String[] column_names = getColumnNames();
		for (int i = 1 ; i < column_names.length ; i++) {
			if (i != 20)
				column_model.getColumn(i).setCellRenderer(new StringRenderer(column_names[i], LabelTableCellRenderer.MODE_MULTIPLE_SELECTION));
		}

		return column_model;
	}

	/**
	 * Initializes the column width.
	 */
	protected void initializeColumnWidth ( ) {
		int columns = column_model.getColumnCount();

		column_model.getColumn(0).setPreferredWidth(100);
		column_model.getColumn(1).setPreferredWidth(40);
		column_model.getColumn(2).setPreferredWidth(250);
		column_model.getColumn(3).setPreferredWidth(200);
		column_model.getColumn(4).setPreferredWidth(60);
		column_model.getColumn(5).setPreferredWidth(60);
		column_model.getColumn(6).setPreferredWidth(60);
		column_model.getColumn(7).setPreferredWidth(40);
		column_model.getColumn(8).setPreferredWidth(80);
		column_model.getColumn(9).setPreferredWidth(80);
		column_model.getColumn(10).setPreferredWidth(120);
		column_model.getColumn(11).setPreferredWidth(120);
		column_model.getColumn(12).setPreferredWidth(60);
		column_model.getColumn(13).setPreferredWidth(60);
		column_model.getColumn(14).setPreferredWidth(60);
		column_model.getColumn(15).setPreferredWidth(60);
		column_model.getColumn(16).setPreferredWidth(40);
		column_model.getColumn(17).setPreferredWidth(100);
		column_model.getColumn(18).setPreferredWidth(100);
		column_model.getColumn(19).setPreferredWidth(100);
		column_model.getColumn(20).setPreferredWidth(80);
		column_model.getColumn(21).setPreferredWidth(200);
		column_model.getColumn(22).setPreferredWidth(100);
		column_model.getColumn(23).setPreferredWidth(120);
		column_model.getColumn(24).setPreferredWidth(120);
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
		group_list.addElement(new Integer(group_count - 1));

		super.addXmlFile(file, file_manager);
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
		for (int i = 0 ; i < files.length ; i++)
			group_list.addElement(new Integer(group_count - 1));

		super.addXmlFiles(files, file_manager);
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

		group_list.addElement(new Integer(group_count - 1));

		super.addInformation(info, file_manager);
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

		for (int i = 0 ; i < infos.length ; i++)
			group_list.addElement(new Integer(group_count - 1));

		super.addInformations(infos, file_manager);
	}


	/**
	 * Deletes a file record at the specified row.
	 * @param row the row.
	 */
	protected void deleteFileAt ( int row ) {
		ArrayIndex index = getSortingIndex();
		if (index == null)
			return;

		try {
			group_list.removeElementAt(index.get(row));
		} catch ( ArrayIndexOutOfBoundsException exception ) {
			return;
		}

		super.deleteFileAt(row);
	}

	/**
	 * Deletes flagged file recoreds.
	 * @param flags the array of flags in original order.
	 */
	protected void deleteFlaggedFiles ( boolean[] flags ) {
		for (int i = flags.length - 1 ; i >= 0 ; i--) {
			if (flags[i]) {
				group_list.removeElementAt(i);
			}
		}

		super.deleteFlaggedFiles(flags);
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

		if (header_value.equals("Group")) {
			try {
				int group = ((Integer)group_list.elementAt(row)).intValue();
				return String.valueOf(group);
			} catch ( ArrayIndexOutOfBoundsException exception ) {
				return "";
			}
		}

		return "";
	}

	/**
	 * Returns the list of target records for the operation. 
	 * @return the list of target records for the operation. 
	 */
	protected Vector getTargetList ( ) {
		Vector target_list = new Vector();

		for (int i = 0 ; i < record_list.size() ; i++) {
			int group = ((Integer)group_list.elementAt(index.get(i))).intValue();
			if (current_group == group)
				target_list.addElement(record_list.elementAt(index.get(i)));
		}

		return target_list;
	}
}
