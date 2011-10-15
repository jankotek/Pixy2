/*
 * @(#)SortableTable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.table;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.io.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;

/**
 * The <code>SortableTable</code> represents a sortable table.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 November 26
 */

public abstract class SortableTable extends JTable {
	/**
	 * The sorted index to output.
	 */
	protected ArrayIndex index = null;

	/**
	 * The sorting column header value.
	 */
	protected String sorting_column = null;

	/**
	 * The sorting direction.
	 */
	protected int sorting_direction = 0;

	/**
	 * The sorting direction which represents ascending order.
	 */
	protected final static int SORT_ASCENDING = 1;

	/**
	 * The sorting direction which represents descending order.
	 */
	protected final static int SORT_DESCENDING = 2;

	/**
	 * Gets the sortable array of the specified column.
	 * @param header_value the header value of the column to sort.
	 */
	protected abstract SortableArray getSortableArray ( String header_value );

	/**
	 * Returns true when sorting is acceptable.
	 * @return true when sorting is acceptable.
	 */
	protected boolean acceptsSorting ( ) {
		return true;
	}

	/**
	 * Invoked when the sorting starts.
	 */
	protected void sortStarted ( ) {
	}

	/**
	 * Invoked when the sorting ends.
	 */
	protected void sortEnded ( ) {
	}

	/**
	 * Sorts the specified column.
	 * @param header_value the header value of the column to sort.
	 */
	protected void sortColumn ( String header_value ) {
		if (index == null)
			return;

		SortableArray array = getSortableArray(header_value);

		if (array != null) {
			sortStarted();

			if (sorting_column != null  &&  sorting_column.equals(header_value)) {
				if (sorting_direction == SORT_ASCENDING)
					sorting_direction = SORT_DESCENDING;
				else
					sorting_direction = SORT_ASCENDING;
			} else {
				sorting_column = header_value;
				sorting_direction = SORT_ASCENDING;
			}

			if (sorting_direction == SORT_ASCENDING)
				index = array.sortAscendant();
			else
				index = array.sortDescendant();

			sortEnded();

			repaint();
		}
	}

	/**
	 * Gets the sorting index.
	 * @return the sorting index.
	 */
	public ArrayIndex getSortingIndex ( ) {
		return index;
	}

	/**
	 * Invoked when the column header is clicked.
	 * @param header_value the header value of the column to sort.
	 */
	protected void headerClicked ( String header_value ) {
	}

	/**
	 * The <code>TableHeader</code> is a table header with a function 
	 * to sort a column.
	 */
	protected class TableHeader extends JTableHeader {
	    /**
		 * Constructs a <code>TableHeader</code>.
		 * @param model the column model.
		 */
		public TableHeader ( TableColumnModel model ) {
			super(model);

			enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		}

		/**
		 * Invoked when mouse event occurs. It is to click a header
		 * and sort the column.
		 * @param e contains the click position.
		 */
		protected void processMouseEvent ( MouseEvent e ) {
			if (e.getID() == MouseEvent.MOUSE_CLICKED) {
				int index = getColumnModel().getColumnIndexAtX(e.getX());
				if (index >= 0) {
					TableColumn column = getColumnModel().getColumn(index);
					if (column != null) {
						if (acceptsSorting()) {
							String header_value = (String)column.getHeaderValue();
							sortColumn(header_value);

							headerClicked(header_value);
						}
					}
				}
			}
			super.processMouseEvent(e);
		}
	}

	/**
	 * Gets the output string of the cell.
	 * @param header_value the header value of the column.
	 * @param row          the index of row in original order.
	 * @return the output string of the cell.
	 */
	protected abstract String getCellString ( String header_value, int row );

	/**
	 * The <code>StringRenderer</code> is a renderer for the columns
	 * to show the data in string.
	 */
	protected class StringRenderer extends LabelTableCellRenderer {
		/**
		 * The header value of the column.
		 */
		protected String header_value;

		/**
		 * Construct a <code>StringRenderer</code> of the specified 
		 * column.
		 * @param header_value   the header value of the column.
		 * @param selection_mode the selection mode.
		 */
		public StringRenderer ( String header_value, int selection_mode ) {
			super(selection_mode);

			this.header_value = header_value;
		}

		/**
		 * Gets the string of the specified row. It must be overrided in
		 * the subclasses.
		 * @param row the row.
		 * @return the string to show.
		 */
		public String getStringAt ( int row ) {
			if (index == null)
				return null;

			try {
				return getCellString(header_value, index.get(row));
			} catch ( ArrayIndexOutOfBoundsException exception ) {
				return "";
			}
		}
	}
}
