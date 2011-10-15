/*
 * @(#)VariableStarSearchControlPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.VariableStarSearch;
import javax.swing.*;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.xml.DocumentIncompleteException;
import net.aerith.misao.toolkit.RawDatabaseConstruction.RawDatabaseConstructionControlPanel;

/**
 * The <code>VariableStarSearchControlPanel</code> represents a 
 * control panel to search variable stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class VariableStarSearchControlPanel extends RawDatabaseConstructionControlPanel {
	/**
	 * Creates a <code>VariableStarSearchControlPanel</code>.
	 * @param operation the operation.
	 * @param table     the table.
	 * @param frame     the frame.
	 */
	public VariableStarSearchControlPanel ( MultiTaskOperation operation, InformationTable table, VariableStarSearchInternalFrame frame ) {
		super(operation, table, frame);
	}

	/**
	 * Invoked when the operation ends.
	 * @param exception the exception if an error occurs, or null if
	 * succeeded.
	 */
	public void notifyEnd ( Exception exception ) {
		if (exception instanceof DocumentIncompleteException) {
			String message = "Please set " + exception.getMessage() + ".";
			if (exception.getMessage().equals("<date>"))
				message = "Please set image date.";
			if (exception.getMessage().equals("<observer>"))
				message = "Please set observer.";
			if (exception.getMessage().equals("<path>"))
				message = "Please save as XML file.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}

		super.notifyEnd(exception);
	}
}
