/*
 * @(#)IdentifiedStarTable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.IdentificationReport;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
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
 * The <code>IdentifiedStarTable</code> represents a table which 
 * consits of the name, ID in the XML document, magnitude, position 
 * and the data of identified stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class IdentifiedStarTable extends SortableTable implements ReportDocumentUpdatedListener {
	/**
	 * The XML document.
	 */
	protected XmlReport report;

	/**
	 * The list of listeners of catalog selection.
	 */
	protected Vector listener_list;

	/**
	 * The list of catalog stars.
	 */
	protected Vector catalog_star_list = null;

	/**
	 * The list of XML stars. The i-th element contains the i-th 
	 * element of <tt>catalog_star_list</tt> as a record.
	 */
	protected Vector xml_star_list = null;

	/**
	 * Constructs an <code>IdentifiedStarTable</code>.
	 * @param report the XML document.
	 */
	public IdentifiedStarTable ( XmlReport report ) {
		this.report = report;

		String[] column_names = { "Name", "ID", "Mag", "Position", "Data" };
		DefaultTableModel model = new DefaultTableModel(column_names, 0);
		setModel(model);

		DefaultTableColumnModel column_model = (DefaultTableColumnModel)getColumnModel();
		for (int i = 0 ; i < column_names.length ; i++)
			column_model.getColumn(i).setCellRenderer(new StringRenderer(column_names[i], LabelTableCellRenderer.MODE_SINGLE_SELECTION));
		setTableHeader(new TableHeader(column_model));

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		initializeColumnWidth();

		listener_list = new Vector();
	}

	/**
	 * Adds the listener of star selection.
	 * @param listener the listener of star selection.
	 */
	public void addStarSelectionListener ( StarSelectionListener listener ) {
		listener_list.addElement(listener);
	}

	/**
	 * Creates the table of the specified catalogs. The old contents
	 * are removed and the table is renewed.
	 * @param catalog_list the list of catalog names.
	 */
	public void setCatalogList ( Vector catalog_list ) {
		catalog_star_list = new Vector();
		xml_star_list = new Vector();

		XmlData data = (XmlData)report.getData();
		XmlStar[] xml_stars = (XmlStar[])data.getStar();
		int star_count = data.getStarCount();
		for (int i = 0 ; i < star_count ; i++) {
			for (int j = 0 ; j < catalog_list.size() ; j++) {
				String star_class = CatalogManager.getCatalogStarClassName((String)catalog_list.elementAt(j));
				Star[] stars = xml_stars[i].getRecords(star_class);

				for (int k = 0 ; k < stars.length ; k++) {
					catalog_star_list.addElement(stars[k]);
					xml_star_list.addElement(xml_stars[i]);
				}
			}
		}

		index = new ArrayIndex(catalog_star_list.size());
		sorting_direction = 0;

		DefaultTableModel model = (DefaultTableModel)getModel();
		model.setNumRows(catalog_star_list.size());

		repaint();
	}

	/**
	 * Initializes the column width.
	 */
	protected void initializeColumnWidth ( ) {
		DefaultTableColumnModel column_model = (DefaultTableColumnModel)getColumnModel();
		column_model.getColumn(0).setPreferredWidth(180);
		column_model.getColumn(1).setPreferredWidth(90);
		column_model.getColumn(2).setPreferredWidth(60);
		column_model.getColumn(3).setPreferredWidth(120);
		column_model.getColumn(4).setPreferredWidth(1000);
	}

	/**
	 * Gets the output string of the cell.
	 * @param header_value the header value of the column.
	 * @param row          the index of row in original order.
	 * @return the output string of the cell.
	 */
	protected String getCellString ( String header_value, int row ) {
		if (catalog_star_list == null)
			return "";

		CatalogStar catalog_star = (CatalogStar)catalog_star_list.elementAt(row);
		XmlStar xml_star = (XmlStar)xml_star_list.elementAt(row);

		if (header_value.equals("Name")) {
			return catalog_star.getName();
		}
		if (header_value.equals("ID")) {
			return xml_star.getName();
		}
		if (header_value.equals("Mag")) {
			StarImage star_image = xml_star.getStarImage();
			String mag = "";
			if (star_image == null) {
				mag = "[" + Format.formatDouble(((XmlInformation)report.getInformation()).getProperUpperLimitMag(), 5, 2).trim();
			} else {
				mag = Format.formatDouble(star_image.getMag(), 5, 2).trim();
			}
			return mag;
		}
		if (header_value.equals("Position")) {
			return catalog_star.getPositionString();
		}
		if (header_value.equals("Data")) {
			return catalog_star.getOutputStringWithoutName();
		}
		return "";
	}

	/**
	 * Gets the sortable array of the specified column.
	 * @param header_value the header value of the column to sort.
	 */
	protected SortableArray getSortableArray ( String header_value ) {
		if (catalog_star_list == null)
			return null;

		SortableArray array = null;
		if (header_value.equals("Mag")) {
			array = new Array(catalog_star_list.size());
		} else {
			array = new StringArray(catalog_star_list.size());
		}

		for (int i = 0 ; i < catalog_star_list.size() ; i++) {
			CatalogStar catalog_star = (CatalogStar)catalog_star_list.elementAt(i);
			XmlStar xml_star = (XmlStar)xml_star_list.elementAt(i);

			if (header_value.equals("Mag")) {
				StarImage star_image = xml_star.getStarImage();
				if (star_image == null)
					((Array)array).set(i, 9999.9);
				else
					((Array)array).set(i, star_image.getMag());
			} else {
				((StringArray)array).set(i, getCellString(header_value, i));
			}
		}

		return array;
	}

	/**
	 * Invoked when the selection changes -- repaints to show the new
	 * selection.
	 * @param e the event.
	 */
    public void valueChanged ( ListSelectionEvent e ) {
		DefaultListSelectionModel model = (DefaultListSelectionModel)e.getSource();

		super.valueChanged(e);

		if (model.isSelectionEmpty() == true)
			return;

		int row = getSelectedRow();
		if (index != null)
			row = index.get(row);
		XmlStar xml_star = (XmlStar)xml_star_list.elementAt(row);
		CatalogStar catalog_star = (CatalogStar)catalog_star_list.elementAt(row);

		for (int i = 0 ; i < listener_list.size() ; i++) {
			StarSelectionListener listener = (StarSelectionListener)listener_list.elementAt(i);
			listener.select(xml_star, catalog_star);
		}
	}

	/**
	 * Invoked when the measured magnitude of the detected stars are
	 * updated.
	 * @param updated_report the XML report document.
	 */
	public void photometryUpdated (XmlReport updated_report ) {
		repaint();

		int row = getSelectedRow();
		if (row < 0  ||  row >= xml_star_list.size())
			return;

		if (index != null)
			row = index.get(row);
		XmlStar xml_star = (XmlStar)xml_star_list.elementAt(row);
		CatalogStar catalog_star = (CatalogStar)catalog_star_list.elementAt(row);

		for (int i = 0 ; i < listener_list.size() ; i++) {
			StarSelectionListener listener = (StarSelectionListener)listener_list.elementAt(i);
			listener.select(xml_star, catalog_star);
		}
	}

	/**
	 * Invoked when the measured position of the detected stars are
	 * updated.
	 * @param updated_report the XML report document.
	 */
	public void astrometryUpdated ( XmlReport updated_report ) {
	}

	/**
	 * Invoked when some stars are added, removed or replaced.
	 * @param updated_report the XML report document.
	 */
	public void starsUpdated ( XmlReport updated_report ) {
	}

	/**
	 * Invoked when the image date is updated.
	 * @param updated_report the XML report document.
	 */
	public void dateUpdated ( XmlReport updated_report ) {
	}

	/**
	 * Invoked when a secondary record, like instruments, is updated.
	 * @param updated_report the XML report document.
	 */
	public void recordUpdated ( XmlReport updated_report ) {
	}
}
