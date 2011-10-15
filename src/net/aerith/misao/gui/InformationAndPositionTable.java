/*
 * @(#)InformationAndPositionTable.java
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
import net.aerith.misao.gui.table.*;
import net.aerith.misao.util.*;

/**
 * The <code>InformationAndPositionTable</code> represents a table 
 * where the XML report documents are added for an operation. It shows 
 * the status of the XML files and the progress of the operation, and 
 * the (x,y) position.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class InformationAndPositionTable extends InformationTable {
	/**
	 * The R.A and Decl.
	 */
	protected Coor coor;

	/**
	 * Constructs an <code>InformationAndPositionTable</code>.
	 * @param coor the R.A. and Decl.
	 */
	public InformationAndPositionTable ( Coor coor ) {
		this.coor = coor;
	}

	/**
	 * Gets the column names. This method must be overrided in the 
	 * subclasses.
	 * @return the column names.
	 */
	protected String[] getColumnNames ( ) {
		String[] column_names = { "Status", "XML File", "Position", "Date", "Limiting Mag.", "Upper-Limit Mag.", "Pixel Size", "Filter", "Chip", "Instruments", "Base Catalog", "Photometric Catalog", "STRs", "NEWs", "ERRs", "NEGs", "Unofficial", "Center R.A.", "Center Decl.", "Field of View", "Image Status", "Image File", "Image Size", "Observer", "Note" };
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
		column_model = (DefaultTableColumnModel)getColumnModel();
		int columns = column_model.getColumnCount();

		column_model.getColumn(0).setPreferredWidth(100);
		column_model.getColumn(1).setPreferredWidth(250);
		column_model.getColumn(2).setPreferredWidth(120);
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
	 * Gets the output string of the cell.
	 * @param header_value the header value of the column.
	 * @param row          the index of row in original order.
	 * @return the output string of the cell.
	 */
	protected String getCellString ( String header_value, int row ) {
		InformationRecord record = null;
		try {
			record = (InformationRecord)record_list.elementAt(row);
		} catch ( ArrayIndexOutOfBoundsException exception ) {
			return "";
		}

		if (header_value.equals("Position")) {
			Position position = record.getInformation().mapCoordinatesToXY(coor);
			return "(" + Format.formatDouble(position.getX(), 7, 4) + "," + Format.formatDouble(position.getY(), 7, 4) + ")";
		}

		return super.getCellString(header_value, row);
	}
}
