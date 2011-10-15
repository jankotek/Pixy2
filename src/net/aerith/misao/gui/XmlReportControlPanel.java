/*
 * @(#)XmlReportControlPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.io.filechooser.XmlFilter;
import net.aerith.misao.util.Operation;

/**
 * The <code>XmlReportControlPanel</code> represents a control panel 
 * to operate on XML report documents.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 March 11
 */

public class XmlReportControlPanel extends ControlPanel {
	/**
	 * The table.
	 */
	protected InformationTable table;

	/**
	 * Creates a <code>XmlReportControlPanel</code>.
	 * @param operation the operation.
	 */
	public XmlReportControlPanel ( Operation operation, InformationTable table ) {
		super(operation);

		this.table = table;
	}

	/**
	 * Gets the button title to set parameters. This must be overrided
	 * in the subclasses.
	 * @return the button title to set parameters.
	 */
	public String getSetButtonTitle ( ) {
		return "Add XML File";
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
			try {
				File file = file_chooser.getSelectedFile();
				table.addXmlFile(file);
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
