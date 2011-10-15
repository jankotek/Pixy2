/*
 * @(#)ButtonCellEditor.java
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

/**
 * The <code>ButtonCellEditor</code> represents a cell editor. It 
 * provides a framework to access the editing object from the cell
 * editor listener.
 * <p>
 * It has two methods to be overrided, <tt>getListener</tt> and
 * <tt>getEditingObject</tt>.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 July 31
 */

public class ButtonCellEditor extends DefaultCellEditor {
	/**
	 * Constructs a <code>ButtonCellEditor</code>.
	 */
	public ButtonCellEditor ( ) {
		super(new JTextField());

		setClickCountToStart(2);
	}

	/**
	 * Invoked when the cell is to be edited.
	 * @param table      the table of the cell to draw.
	 * @param value      the value of the cell.
	 * @param isSelected true if the cell is selected.
	 * @param row        the row of the cell.
	 * @param column     the column of the cell.
	 */
	public Component getTableCellEditorComponent ( JTable table, Object value, boolean isSelected, int row, int column) {
		Object object = getEditingObject(row, column);
		JButton button = new JButton("");
		ButtonCellEditorListener listener = getListener();
		listener.setEditingObject(object, this);
		button.addActionListener(listener);
		return button;
	}

	/**
	 * Gets the cell editor listener. It must be overrided in the
	 * subclasses.
	 * @param object the object to edit.
	 * @param editor the cell editor.
	 * @return the cell editor listener.
	 */
	protected ButtonCellEditorListener getListener ( ) {
		return new ButtonCellEditorListener();
	}

	/**
	 * Gets the object to edit.
	 * @param row    the row to edit.
	 * @param column the column to edit.
	 * @return the object to edit.
	 */
	protected Object getEditingObject ( int row, int column ) {
		return null;
	}
}
