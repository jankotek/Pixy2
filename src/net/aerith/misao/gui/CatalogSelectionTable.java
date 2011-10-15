/*
 * @(#)CatalogSelectionTable.java
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
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.gui.table.*;

/**
 * The <code>CatalogSelectionTable</code> represents a table to select
 * some catalogs of the specified category, from the specified list.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class CatalogSelectionTable extends SortableCheckTable implements CatalogTreeSelectionListener {
	/**
	 * The hash table which contains the catalog is selected or not.
	 */
	protected Hashtable hash;

	/**
	 * The list of catalogs.
	 */
	protected Vector catalog_list;

	/**
	 * The list of acronyms.
	 */
	protected Vector acronym_list;

	/**
	 * The columns.
	 */
	protected final static String[] column_names = { "", "Acronym", "Name" };

	/**
	 * The table model.
	 */
	protected DefaultTableModel model;

	/**
	 * The table column model.
	 */
	protected DefaultTableColumnModel column_model;

	/**
	 * Constructs a <code>CatalogSelectionTable</code>.
	 * @param catalog_list the list of catalogs.
	 */
	public CatalogSelectionTable ( Vector catalog_list ) {
		hash = new Hashtable();
		for (int i = 0 ; i < catalog_list.size() ; i++)
			hash.put((String)catalog_list.elementAt(i), Boolean.TRUE);

		this.catalog_list = new Vector();
		acronym_list = new Vector();

		index = null;

		model = new DefaultTableModel(column_names, 0);
		setModel(model);

		column_model = (DefaultTableColumnModel)getColumnModel();
		column_model.getColumn(1).setCellRenderer(new StringRenderer("Acronym", LabelTableCellRenderer.MODE_NO_SELECTION));
		column_model.getColumn(2).setCellRenderer(new StringRenderer("Name", LabelTableCellRenderer.MODE_NO_SELECTION));

		initializeCheckColumn();

		setTableHeader(new TableHeader(column_model));

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		initializeColumnWidth();
	}

	/**
	 * Initializes the column width.
	 */
	protected void initializeColumnWidth ( ) {
		column_model.getColumn(0).setPreferredWidth(20);
		column_model.getColumn(1).setPreferredWidth(70);
		column_model.getColumn(2).setPreferredWidth(350);
	}

	/**
	 * Shows the catalogs in the specified category.
	 * @param category the name of category.
	 */
	public void showCategory ( String category ) {
		while (model.getRowCount() > 0)
			model.removeRow(0);

		Vector list = CatalogManager.getCatalogListInCategory(category);
		catalog_list = new Vector();
		acronym_list = new Vector();
		for (int i = 0 ; i < list.size() ; i++) {
			String catalog_name = (String)list.elementAt(i);
			if (hash.get(catalog_name) != null) {
				catalog_list.addElement(catalog_name);
				acronym_list.addElement(CatalogManager.convertCatalogNameToAcronym(catalog_name));
			}
		}

		index = new ArrayIndex(catalog_list.size());

		Object[] objects = new Object[column_names.length];
		objects[0] = new Boolean(true);
		for (int i = 1 ; i < column_names.length ; i++)
			objects[i] = "";
		for (int i = 0 ; i < catalog_list.size() ; i++)
			model.addRow(objects);

		boolean[] new_check_list = new boolean[catalog_list.size()];
		for (int i = 0 ; i < catalog_list.size() ; i++) {
			String catalog_name = (String)catalog_list.elementAt(i);
			Boolean selected = (Boolean)hash.get(catalog_name);
			if (selected != null  &&  selected.booleanValue())
				new_check_list[i] = true;
			else
				new_check_list[i] = false;
		}

		initializeCheckColumn(new_check_list);

		revalidate();
	}

	/**
	 * Gets the output string of the cell.
	 * @param header_value the header value of the column.
	 * @param row          the index of row in original order.
	 * @return the output string of the cell.
	 */
	protected String getCellString ( String header_value, int row ) {
		if (header_value.equals("Name")) {
			return (String)catalog_list.elementAt(row);
		}
		if (header_value.equals("Acronym")) {
			return (String)acronym_list.elementAt(row);
		}
		return "";
	}

	/**
	 * Gets the sortable array of the specified column.
	 * @param header_value the header value of the column to sort.
	 */
	protected SortableArray getSortableArray ( String header_value ) {
		if (index == null)
			return null;

		if (header_value.equals("Name")  ||  header_value.equals("Acronym")) {
			StringArray array = new StringArray(catalog_list.size());
			for (int i = 0 ; i < catalog_list.size() ; i++)
				array.set(i, getCellString(header_value, i));
			return array;
		}

		return null;
	}

	/**
	 * Invoked when the check box is edited.
	 */
	protected void checkEdited ( ) {
		if (index == null)
			return;

		int check_column = getCheckColumn();
		for (int i = 0 ; i < catalog_list.size() ; i++) {
			String catalog_name = (String)catalog_list.elementAt(index.get(i));
			hash.put(catalog_name, (Boolean)getValueAt(i, check_column));
		}
	}

	/**
	 * Gets the list of selected catalogs.
	 * @return the list of selected catalogs.
	 */
	public Vector getSelectedCatalogList ( ) {
		Vector list = new Vector();

		Enumeration catalogs = hash.keys();
		while (catalogs.hasMoreElements()) {
			String catalog_name = (String)catalogs.nextElement();
			Boolean selected = (Boolean)hash.get(catalog_name);
			if (selected.booleanValue())
				list.addElement(catalog_name);
		}

		return list;
	}

	/** 
	 * Invoked when the root node is selected.
	 */
	public void selectAll ( ) {
	}

	/** 
	 * Invoked when the specified catalog category is selected.
	 * @param category_name the category name.
	 */
	public void selectCategory ( String category_name ) {
		showCategory(category_name);
	}

	/** 
	 * Invoked when the specified catalog is selected.
	 * @param catalog_name the catalog name.
	 */
	public void selectCatalog ( String catalog_name ) {
	}

	/** 
	 * Invoked when the specified star is selected.
	 * @param star the star.
	 */
	public void selectStar ( Star star ) {
	}
}
