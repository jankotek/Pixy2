/*
 * @(#)Dialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Hashtable;

/**
 * The <code>Dialog</code> represents a dialog.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 November 26
 */

public abstract class Dialog {
	/**
	 * The components to show in this dialog.
	 */
	protected Object[] components;

	/**
	 * The default values.
	 */
	protected static Hashtable default_values = new Hashtable();

	/**
	 * The parent pane.
	 */
	protected Container parent_pane;

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected abstract String getTitle ( );

	/**
	 * Saves the default values. This method must be overrided in the
	 * subclasses.
	 */
	protected void saveDefaultValues ( ) {
	}

	/**
	 * Shows this dialog.
	 * @param pane the parent pane of this dialog.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int show ( Container pane ) {
		parent_pane = pane;
		int answer = JOptionPane.showConfirmDialog(pane, components, getTitle(), JOptionPane.OK_CANCEL_OPTION);
		if (answer == 0) {
			// OK
			saveDefaultValues();
		}

		return answer;
	}
}
