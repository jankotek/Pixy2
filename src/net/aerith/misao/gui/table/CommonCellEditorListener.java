/*
 * @(#)CommonCellEditorListener.java
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
 * The <code>CommonCellEditorListener</code> represents a cell editor
 * listener. It provides a framework to access the editing object.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 July 31
 */

public class CommonCellEditorListener implements CellEditorListener {
	/**
	 * The object to edit.
	 */
	protected Object object = null;

    /**
	 * Sets the editing object.
	 * @param object the editing object.
	 */
	public void setEditingObject ( Object object ) {
		this.object = object;
	}

    /**
	 * This tells the listeners the editor has canceled editing.
	 * @param e the event.
	 */
	public void editingCanceled ( ChangeEvent e ) {
	}

    /**
	 * This tells the listeners the editor has ended editing.
	 * @param e the event.
	 */
	public void editingStopped ( ChangeEvent e ) {
	}
}
