/*
 * @(#)AdditionalIdentificationControlPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.AdditionalIdentification;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.io.filechooser.XmlFilter;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.CommonFileChooser;

/**
 * The <code>AdditionalIdentificationControlPanel</code> represents a 
 * control panel to identify with the specified stars and register the 
 * magnitude to the database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class AdditionalIdentificationControlPanel extends ControlPanel {
	/**
	 * The table.
	 */
	protected InformationTable table;

	/**
	 * Creates an <code>AdditionalIdentificationControlPanel</code>.
	 * @param operation the operation.
	 * @param table     the table.
	 */
	public AdditionalIdentificationControlPanel ( AdditionalIdentificationOperation operation, InformationTable table ) {
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
		return "Identify and Register";
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

		if (file_chooser.showOpenDialog(table) == JFileChooser.APPROVE_OPTION) {
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
