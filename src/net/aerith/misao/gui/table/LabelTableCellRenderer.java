/*
 * @(#)LabelTableCellRenderer.java
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
import java.util.*;
import net.aerith.misao.gui.*;

/**
 * The <code>LabelTableCellRenderer</code> represents a table cell
 * renderer to show the <code>JLabel</code> of a string and an icon.
 * <p>
 * The row selection mode can be selected from the following three 
 * types.
 * <ul>
 *   <li>No selection support.
 *   <li>Single selection support.
 *   <li>Multiple selection support.
 * </ul>
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 November 26
 */

public class LabelTableCellRenderer extends DefaultTableCellRenderer {
    /**
	 * The label component which is not selected.
	 */
	protected JLabel label_not_selected = new JLabel("");

    /**
	 * The label component which is selected.
	 */
	protected JLabel label_selected = new JLabel("");

	/**
	 * The number of selection mode for no selection support.
	 */
	public final static int MODE_NO_SELECTION = 0;

	/**
	 * The number of selection mode for single selection support.
	 */
	public final static int MODE_SINGLE_SELECTION = 1;

	/**
	 * The number of selection mode for multiple selection support.
	 */
	public final static int MODE_MULTIPLE_SELECTION = 2;

	/**
	 * The number of selection mode.
	 */
	protected int selection_mode = MODE_NO_SELECTION;

	/**
	 * Constructs a <code>LabelTableCellRenderer</code>.
	 */
	public LabelTableCellRenderer ( ) {
		super();
	}

	/**
	 * Constructs a <code>LabelTableCellRenderer</code> with a number
	 * of selection mode.
	 * @param selection_mode the selection mode.
	 */
	public LabelTableCellRenderer ( int selection_mode ) {
		super();

		this.selection_mode = selection_mode;
	}

	/**
	 * Sets the selection mode.
	 * @param selection_mode the selection mode.
	 */
	public void setSelectionMode ( int selectin_mode ) {
		this.selection_mode = selection_mode;
	}

	/**
	 * Gets the string of the specified row. It must be overrided in
	 * the subclasses.
	 * @param row the row.
	 * @return the string to show.
	 */
	public String getStringAt ( int row ) {
		return null;
	}

	/**
	 * Gets the icon of the specified row. It must be overrided in the
	 * subclasses.
	 * @param row the row.
	 * @return the icon to show.
	 */
	public Icon getIconAt ( int row ) {
		return null;
	}

	/**
	 * Gets the icon alignment. It must be overrided in the subclasses.
	 * @param row the row.
	 * @return the icon alignment.
	 */
	public int getIconAlignmentAt ( int row ) {
		return LEFT;
	}

	/**
	 * Invoked when the cell is to be drawn.
	 * @param table      the table of the cell to draw.
	 * @param value      the value of the cell.
	 * @param isSelected true if the cell is selected.
	 * @param row        the row of the cell.
	 * @param column     the column of the cell.
	 */
	public Component getTableCellRendererComponent ( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		String string = getStringAt(row);
		Icon icon = getIconAt(row);
		int alignment = getIconAlignmentAt(row);

		label_selected.setOpaque(true);
		label_selected.setForeground(table.getSelectionForeground());
		label_selected.setBackground(table.getSelectionBackground());

		JLabel label = label_not_selected;

		if (string == null) {
			label.setText("");
			label.setIcon(icon);
			label.setHorizontalAlignment(alignment);
		} else {
			if (selection_mode == MODE_SINGLE_SELECTION) {
				if (row == table.getSelectedRow())
					label = label_selected;
			}
			if (selection_mode == MODE_MULTIPLE_SELECTION) {
				if (isSelected)
					label = label_selected;
			}

			label.setText(string);
			label.setIcon(icon);
			label.setHorizontalAlignment(alignment);
		}

		return label;
	}
}
