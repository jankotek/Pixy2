/*
 * @(#)SortableCheckTable.java
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
 * The <code>SortableCheckTable</code> represents a sortable table 
 * with a column to check.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 18
 */

public abstract class SortableCheckTable extends SortableTable {
	/**
	 * The list of check results.
	 */
	protected boolean[] check_list;

	/**
	 * The check all flag.
	 */
	protected boolean check_all = false;

	/**
	 * The popup menu.
	 */
	protected JPopupMenu popup;

	/**
	 * Invoked when the sorting starts.
	 */
	protected void sortStarted ( ) {
		DefaultTableModel model = (DefaultTableModel)getModel();
		if (model != null) {
			int check_column = getCheckColumn();
			for (int i = 0 ; i < model.getRowCount() ; i++)
				check_list[index.get(i)] = ((Boolean)getValueAt(i, check_column)).booleanValue();
		}
	}

	/**
	 * Invoked when the sorting ends.
	 */
	protected void sortEnded ( ) {
		DefaultTableModel model = (DefaultTableModel)getModel();
		if (model != null) {
			int check_column = getCheckColumn();
			for (int i = 0 ; i < model.getRowCount() ; i++)
				setValueAt(new Boolean(check_list[index.get(i)]), i, check_column);
		}
	}

	/**
	 * Invoked when the column header is clicked.
	 * @param header_value the header value of the column to sort.
	 */
	protected void headerClicked ( String header_value ) {
		if (header_value.length() == 0) {
			DefaultTableModel model = (DefaultTableModel)getModel();
			if (model != null) {
				// Inverses the check.
				int check_column = getCheckColumn();
				for (int i = 0 ; i < model.getRowCount() ; i++) {
					check_list[i] = check_all;
					model.setValueAt(new Boolean(check_all), i, check_column);
				}
				check_all = (check_all == false);

				checkEdited();
			}
		}
	}

	/**
	 * Invoked when the check box is edited.
	 */
	protected void checkEdited ( ) {
	}

	/**
	 * Gets the check column index.
	 * @return the check column index.
	 */
	public int getCheckColumn ( ) {
		DefaultTableModel model = (DefaultTableModel)getModel();
		if (model != null) {
			for (int i = 0 ; i < model.getColumnCount() ; i++) {
				TableColumn column = getColumnModel().getColumn(i);
				if (((String)column.getHeaderValue()).length() == 0)
					return i;
			}
		}

		return 0;
	}

	/**
	 * Initializes the check column.
	 */
	protected void initializeCheckColumn ( ) {
		DefaultTableModel model = (DefaultTableModel)getModel();
		if (model != null) {
			boolean[] new_check_list = new boolean[model.getRowCount()];
			for (int i = 0 ; i < model.getRowCount() ; i++)
				new_check_list[i] = true;

			initializeCheckColumn(new_check_list);
		}
	}

	/**
	 * Initializes the check column and sets the status.
	 * @param new_check_list the list of new check status.
	 */
	protected void initializeCheckColumn ( boolean[] new_check_list ) {
		DefaultTableModel model = (DefaultTableModel)getModel();
		DefaultTableColumnModel column_model = (DefaultTableColumnModel)getColumnModel();
		if (model != null  &&  column_model != null) {
			column_model.getColumn(0).setCellRenderer(new CheckRenderer());

			JCheckBox check = new JCheckBox("");
			check.setSelected(true);
			DefaultCellEditor editor = new DefaultCellEditor(check);
			column_model.getColumn(0).setCellEditor(editor);
			editor.addCellEditorListener(new CheckListener());

			check_list = new boolean[model.getRowCount()];
			for (int i = 0 ; i < model.getRowCount() ; i++)
				check_list[i] = new_check_list[i];

			int check_column = getCheckColumn();
			for (int i = 0 ; i < model.getRowCount() ; i++)
				setValueAt(new Boolean(check_list[i]), i, check_column);

			checkEdited();
		}
	}

	/**
	 * Initializes a popup menu. A <tt>popup</tt> must be created 
	 * previously.
	 */
	protected void initPopupMenu ( ) {
		popup = new JPopupMenu();

		JMenuItem item = new JMenuItem("Check On Selected Rows");
		item.addActionListener(new CheckSelectedListener(true));
		popup.add(item);

		item = new JMenuItem("Check Off Selected Rows");
		item.addActionListener(new CheckSelectedListener(false));
		popup.add(item);

		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}

	/**
	 * Invoked when mouse event occurs. It is to show a click data
	 * dialog or popup menu.
	 * @param e contains the click position.
	 */
	protected void processMouseEvent ( MouseEvent e ) {
		if (e.isPopupTrigger()) {
			if (popup != null)
				popup.show(e.getComponent(), e.getX(), e.getY());
		}
		super.processMouseEvent(e);
	}

	/**
	 * The <code>CheckRenderer</code> is a renderer for the column to
	 * check.
	 */
	protected class CheckRenderer extends DefaultTableCellRenderer {
	    /**
		 * The check box component.
		 */
		protected JCheckBox check = new JCheckBox("");

	    /**
		 * Invoked when the cell is to be drawn.
		 * @param table      the table of the cell to draw.
		 * @param value      the value of the cell.
		 * @param isSelected true if the cell is selected.
		 * @param row        the row of the cell.
		 * @param column     the column of the cell.
		 */
		public Component getTableCellRendererComponent ( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if (value == null)
				return null;

			check.setSelected(((Boolean)value).booleanValue());
			return check;
		}
	}

	/**
	 * The <code>CheckListener</code> is a listener class of checkbox
	 * status change.
	 */
	protected class CheckListener implements CellEditorListener {
		/**
		 * This tells the listeners the editor has ended editing.
		 * @param e the event.
		 */
		public void editingStopped ( ChangeEvent e ) {
			checkEdited();
		}

		/**
		 * This tells the listeners the editor has canceled editing.
		 * @param e the event.
		 */
		public void editingCanceled ( ChangeEvent e ) {
		}
	}

	/**
	 * The <code>CheckSelectedListener</code> is a listener class of 
	 * menu selection to check on/off selected rows.
	 */
	protected class CheckSelectedListener implements ActionListener {
		/**
		 * True when to check on.
		 */
		private boolean on = true;

		/**
		 * Constructs a <code>CheckSelectedListener</code>.
		 * @param on true when to check on.
		 */
		public CheckSelectedListener ( boolean on ) {
			this.on = on;
		}

		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			ArrayIndex index = getSortingIndex();
			if (index == null)
				return;

			DefaultTableModel model = (DefaultTableModel)getModel();
			if (model != null) {
				int check_column = getCheckColumn();

				int[] rows = getSelectedRows();
				for (int i = 0 ; i < rows.length ; i++) {
					check_list[index.get(rows[i])] = on;
					model.setValueAt(new Boolean(on), rows[i], check_column);
				}

				checkEdited();
			}
		}
	}
}
