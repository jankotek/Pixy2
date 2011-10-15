/*
 * @(#)ButtonCellEditorListener.java
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
 * The <code>ButtonCellEditorListener</code> represents an action
 * listener invoked when a button is clicked on the table cell.
 * It provides a framework to access the editing object.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 July 31
 */

public class ButtonCellEditorListener implements ActionListener {
	/**
	 * The object to edit.
	 */
	protected Object object = null;

	/**
	 * The cell editir.
	 */
	protected ButtonCellEditor editor = null;

    /**
	 * Sets the editing object.
	 * @param object the object to edit.
	 * @param editor the cell editor.
	 */
	public void setEditingObject ( Object object, ButtonCellEditor editor ) {
		this.object = object;
		this.editor = editor;
	}

	/**
	 * Invoked when one of the menus is selected.
	 * @param e contains the selected menu item.
	 */
	public void actionPerformed ( ActionEvent e ) {
		if (editor != null)
			editor.stopCellEditing();
	}
}
