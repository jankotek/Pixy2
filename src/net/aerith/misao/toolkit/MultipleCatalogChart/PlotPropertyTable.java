/*
 * @(#)PlotPropertyTable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.MultipleCatalogChart;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.gui.table.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.catalog.*;

/**
 * The <code>PlotPropertyTable</code> represents a table which shows 
 * the property to plot stars of each catalog.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class PlotPropertyTable extends SortableCheckTable {
	/**
	 * The list of listeners of catalog selection.
	 */
	protected Vector listener_list;

	/**
	 * The list of catalog names.
	 */
	protected Vector catalog_list;

	/**
	 * The list of catalog acronyms.
	 */
	protected Vector acronym_list;

	/**
	 * The list of properties.
	 */
	protected Vector property_list;

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
	 * Constructs a <code>PlotPropertyTable</code>.
	 */
	public PlotPropertyTable ( ) {
		catalog_list = new Vector();
		acronym_list = new Vector();
		property_list = new Vector();

		index = null;

		String[] column_names = { "", "Mark", "Acronym", "Catalog" };
		model = new DefaultTableModel(column_names, 0);
		setModel(model);

		column_model = (DefaultTableColumnModel)getColumnModel();
		column_model.getColumn(1).setCellRenderer(new MarkRenderer());
		column_model.getColumn(2).setCellRenderer(new StringRenderer("Acronym", LabelTableCellRenderer.MODE_NO_SELECTION));
		column_model.getColumn(3).setCellRenderer(new StringRenderer("Catalog", LabelTableCellRenderer.MODE_NO_SELECTION));

		initializeCheckColumn();

		column_model.getColumn(1).setCellEditor(new MarkEditor());

		setTableHeader(new TableHeader(column_model));

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		initializeColumnWidth();

		pane = this;

		listener_list = new Vector();
	}

	/**
	 * Adds the listener of property change.
	 * @param listener the listener of property change.
	 */
	public void addPropertyChangedListener ( PropertyChangedListener listener ) {
		listener_list.addElement(listener);
	}

	/**
	 * Initializes the column width.
	 */
	protected void initializeColumnWidth ( ) {
		column_model.getColumn(0).setPreferredWidth(20);
		column_model.getColumn(1).setPreferredWidth(40);
		column_model.getColumn(2).setPreferredWidth(70);
		column_model.getColumn(3).setPreferredWidth(300);
	}

	/**
	 * Sets the property to plot stars of the specified catalog.
	 * @param catalog_name the name of catalog.
	 * @param property     the property.
	 */
	public void setProperty ( String catalog_name, PlotProperty property ) {
		boolean found = false;
		for (int i = 0 ; i < catalog_list.size() ; i++) {
			String name = (String)catalog_list.elementAt(i);
			if (name.equals(catalog_name)) {
				property_list.removeElementAt(i);
				property_list.insertElementAt(property, i);
				found = true;
				break;
			}
		}

		if (found == false) {
			int check_column = getCheckColumn();
			boolean[] new_check_list = new boolean[model.getRowCount() + 1];
			for (int i = 0 ; i < model.getRowCount() ; i++)
				new_check_list[index.get(i)] = ((Boolean)getValueAt(i, check_column)).booleanValue();
			new_check_list[model.getRowCount()] = true;

			while (model.getRowCount() > 0)
				model.removeRow(0);

			catalog_list.addElement(catalog_name);
			acronym_list.addElement(CatalogManager.convertCatalogNameToAcronym(catalog_name));
			property_list.addElement(property);

			index = new ArrayIndex(property_list.size());

			int height = getRowHeight();
			for (int i = 0 ; i < property_list.size() ; i++) {
				PlotProperty p = (PlotProperty)property_list.elementAt(i);
				if (height < new PlotMarkIcon(p).getIconHeight())
					height = new PlotMarkIcon(p).getIconHeight();
			}
			setRowHeight(height);

			Object[] objects = new Object[3];
			objects[0] = new Boolean(true);
			for (int i = 1 ; i < 3 ; i++)
				objects[i] = "";
			for (int i = 0 ; i < property_list.size() ; i++)
				model.addRow(objects);

			initializeCheckColumn(new_check_list);
		}

		for (int i = 0 ; i < listener_list.size() ; i++) {
			PropertyChangedListener listener = (PropertyChangedListener)listener_list.elementAt(i);
			listener.propertyChanged();
		}

		revalidate();
	}

	/**
	 * Gets the output string of the cell.
	 * @param header_value the header value of the column.
	 * @param row          the index of row in original order.
	 * @return the output string of the cell.
	 */
	protected String getCellString ( String header_value, int row ) {
		if (header_value.equals("Catalog")) {
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

		if (header_value.equals("Catalog")  ||  header_value.equals("Acronym")) {
			StringArray array = new StringArray(property_list.size());
			for (int i = 0 ; i < property_list.size() ; i++)
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
		for (int i = 0 ; i < property_list.size() ; i++) {
			PlotProperty property = (PlotProperty)property_list.elementAt(index.get(i));
			if (((Boolean)getValueAt(i, check_column)).booleanValue())
				property.enable();
			else
				property.disable();
		}

		for (int i = 0 ; i < listener_list.size() ; i++) {
			PropertyChangedListener listener = (PropertyChangedListener)listener_list.elementAt(i);
			listener.propertyChanged();
		}
	}

	/**
	 * The <code>MarkRenderer</code> is a renderer for the column of 
	 * the mark.
	 */
	protected class MarkRenderer extends DefaultTableCellRenderer {
	    /**
		 * Invoked when the cell is to be drawn.
		 * @param table      the table of the cell to draw.
		 * @param value      the value of the cell.
		 * @param isSelected true if the cell is selected.
		 * @param row        the row of the cell.
		 * @param column     the column of the cell.
		 */
		public Component getTableCellRendererComponent ( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			ArrayIndex index = getSortingIndex();
			if (index == null)
				return this;

			PlotProperty property = (PlotProperty)property_list.elementAt(index.get(row));

			IconLabel label = new IconLabel(new PlotMarkIcon(property));
			return label;
		}
	}

	/**
	 * The <code>MarkEditor</code> represents a table cell editor to
	 * set the mark of a star to plot on chart.
	 */
	protected class MarkEditor extends ButtonCellEditor {
		/**
		 * Gets the cell editor listener. It must be overrided in the
		 * subclasses.
		 * @param object the object to edit.
		 * @param editor the cell editor.
		 * @return the cell editor listener.
		 */
		protected ButtonCellEditorListener getListener ( ) {
			return new PropertyListener();
		}

		/**
		 * Gets the object to edit.
		 * @param row    the row to edit.
		 * @param column the column to edit.
		 * @return the object to edit.
		 */
		protected Object getEditingObject ( int row, int column ) {
			ArrayIndex index = getSortingIndex();
			if (index != null) {
				try {
					String catalog_name = (String)catalog_list.elementAt(index.get(row));
					String class_name =  CatalogManager.getCatalogStarClassName(catalog_name);
					if (class_name == null  ||  class_name.length() == 0) {
					} else {
						Class t = Class.forName(class_name);
						CatalogStar catalog_star = (CatalogStar)t.newInstance();
		   
						PlotProperty property = (PlotProperty)property_list.elementAt(index.get(row));

						return new PropertySet(property, catalog_star);
					}
				} catch ( ClassNotFoundException exception ) {
					System.err.println(exception);
				} catch ( IllegalAccessException exception ) {
					System.err.println(exception);
				} catch ( InstantiationException exception ) {
					System.err.println(exception);
				}
			}

			return null;
		}
	}

	/**
	 * The <code>PropertyListener</code> is a listener to push the 
	 * button to change the plot property.
	 */
	protected class PropertyListener extends ButtonCellEditorListener {
	    /**
		 * Invoked when the button is pushed.
		 * @param e the action event.
		 */
		public void actionPerformed ( ActionEvent e ) {
			try {
				PlotProperty property = ((PropertySet)object).property;
				CatalogStar catalog_star = ((PropertySet)object).catalog_star;

				MagnitudeSystemSettingDialog dialog = new MagnitudeSystemSettingDialog(catalog_star);

				int answer = dialog.show(pane);
				if (answer == 0) {
					property.setMagnitudeSystem(dialog.getMagnitudeSystem());
					setProperty(catalog_star.getCatalogName(), property);
				}
			} catch ( UnsupportedMagnitudeSystemException exception ) {
			}

			editor.stopCellEditing();
			repaint();
		}
	}

	/**
	 * The <code>PropertySet</code> is a set of plot property and the
	 * catalog star.
	 */
	private class PropertySet {
		/**
		 * The plot property.
		 */
		public PlotProperty property;

		/**
		 * The catalog star.
		 */
		public CatalogStar catalog_star;

		/**
		 * Construct a <code>PropertySet</code>.
		 * @param property     the plot property.
		 * @param catalog_star the catalog star.
		 */
		public PropertySet ( PlotProperty property, CatalogStar catalog_star ) {
			this.property = property;
			this.catalog_star = catalog_star;
		}
	}
}
