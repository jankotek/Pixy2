/*
 * @(#)RecordSearchControlPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.RecordSearch;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JFileChooser;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.CommonFileChooser;
import net.aerith.misao.io.filechooser.XmlFilter;

/**
 * The <code>RecordSearchControlPanel</code> represents a control 
 * panel for record search.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class RecordSearchControlPanel extends ControlPanel {
	/**
	 * The table.
	 */
	protected InformationAndPositionTable table;

	/**
	 * Constructs a <code>RecordSearchControlPanel</code>.
	 * @param operation the operation.
	 * @param table     the table.
	 */
	public RecordSearchControlPanel ( RecordSearchOperation operation, InformationAndPositionTable table ) {
		super(operation);

		this.table = table;
	}

	/**
	 * Gets the button title to set parameters. This must be overrided
	 * in the subclasses.
	 * @return the button title to set parameters.
	 */
	public String getSetButtonTitle ( ) {
		return "Add XML file";
	}

	/**
	 * Gets the button title to start the operation. This must be 
	 * overrided in the subclasses.
	 * @return the button title to start the operation.
	 */
	public String getStartButtonTitle ( ) {
		return "Observations";
	}

	/**
	 * Gets the button title to reset. This must be overrided in the
	 * subclasses.
	 * @return the button title to reset.
	 */
	public String getResetButtonTitle ( ) {
		return "Set Ready";
	}

	/**
	 * Invoked when the set button is pushed. This must be overrided 
	 * in the subclasses.
	 * @param e contains the selected menu item.
	 */
	public void actionPerformedSet ( ActionEvent e ) {
		CommonFileChooser file_chooser = new CommonFileChooser();
		file_chooser.setDialogTitle("Open an XML file.");
		file_chooser.setMultiSelectionEnabled(false);
		file_chooser.addChoosableFileFilter(new XmlFilter());
		if (file_chooser.showOpenDialog(pane) == JFileChooser.APPROVE_OPTION) {
			File file = file_chooser.getSelectedFile();

			try {
				table.addXmlFile(file);
			} catch ( FileNotFoundException exception ) {
			} catch ( IOException exception ) {
			}
		}
	}

	/**
	 * Invoked when the reset button is pushed. This must be overrided 
	 * in the subclasses.
	 * @param e contains the selected menu item.
	 */
	public void actionPerformedReset ( ActionEvent e ) {
		table.setReady();
	}
}
