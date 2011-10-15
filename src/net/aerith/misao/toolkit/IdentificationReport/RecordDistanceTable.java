/*
 * @(#)RecordDistanceTable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.IdentificationReport;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.gui.table.*;
import net.aerith.misao.xml.*;

/**
 * The <code>RecordDistanceTable</code> represents a table which shows
 * the distance of the specified record to the other records in one
 * XML star object.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class RecordDistanceTable extends SortableTable {
	/**
	 * The records in the XML star obect.
	 */
	protected Vector records = null;

	/**
	 * The base record to calculate distance from.
	 */
	protected Star base_record = null;

	/**
	 * Constructs an <code>RecordDistanceTable</code>.
	 */
	public RecordDistanceTable ( ) {
		String[] column_names = { "Distance", "Position", "Name", "Data" };
		DefaultTableModel model = new DefaultTableModel(column_names, 0);
		setModel(model);

		DefaultTableColumnModel column_model = (DefaultTableColumnModel)getColumnModel();
		for (int i = 0 ; i < 4 ; i++)
			column_model.getColumn(i).setCellRenderer(new StringRenderer(column_names[i], LabelTableCellRenderer.MODE_NO_SELECTION));
		setTableHeader(new TableHeader(column_model));

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		initializeColumnWidth();
	}

	/**
	 * Creates the table of the specified star. The old contents are
	 * removed and the table is renewed.
	 * @param star        the XML star object.
	 * @param base_record the record in the star to calculate distance
	 * to the other records from.
	 */
	public void setStar ( XmlStar star, Star base_record ) {
		records = star.getAllRecords();
		this.base_record = base_record;

		index = new ArrayIndex(records.size());
		sorting_direction = 0;

		DefaultTableModel model = (DefaultTableModel)getModel();
		model.setNumRows(records.size());

		repaint();
	}

	/**
	 * Initializes the column width.
	 */
	protected void initializeColumnWidth ( ) {
		DefaultTableColumnModel column_model = (DefaultTableColumnModel)getColumnModel();
		column_model.getColumn(0).setPreferredWidth(60);
		column_model.getColumn(1).setPreferredWidth(120);
		column_model.getColumn(2).setPreferredWidth(180);
		column_model.getColumn(3).setPreferredWidth(1000);
	}

	/**
	 * Gets the output string of the cell.
	 * @param header_value the header value of the column.
	 * @param row          the index of row in original order.
	 * @return the output string of the cell.
	 */
	protected String getCellString ( String header_value, int row ) {
		if (records == null)
			return "";

		Star s = (Star)records.elementAt(row);
		if (header_value.equals("Distance")) {
			double distance = s.getCoor().getAngularDistanceTo(base_record.getCoor()) * 3600.0;
			return Format.formatDouble(distance, 6, 4).trim() + "\"";
		}
		if (header_value.equals("Position")) {
			return s.getPositionString();
		}
		if (header_value.equals("Name")) {
			if (s instanceof StarImage)
				return "Detected Star";
			return s.getName();
		}
		if (header_value.equals("Data")) {
			return s.getOutputStringWithoutName();
		}
		return "";
	}

	/**
	 * Gets the sortable array of the specified column.
	 * @param header_value the header value of the column to sort.
	 */
	protected SortableArray getSortableArray ( String header_value ) {
		if (records == null)
			return null;

		SortableArray array = null;
		if (header_value.equals("Distance")) {
			array = new Array(records.size());
		} else {
			array = new StringArray(records.size());
		}

		for (int i = 0 ; i < records.size() ; i++) {
			Star s = (Star)records.elementAt(i);
			if (header_value.equals("Distance")) {
				double distance = s.getCoor().getAngularDistanceTo(base_record.getCoor()) * 3600.0;
				((Array)array).set(i, distance);
			} else {
				((StringArray)array).set(i, getCellString(header_value, i));
			}
		}

		return array;
	}
}
