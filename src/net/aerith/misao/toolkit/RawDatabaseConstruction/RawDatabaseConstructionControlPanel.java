/*
 * @(#)RawDatabaseConstructionControlPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.RawDatabaseConstruction;
import java.awt.event.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.util.*;

/**
 * The <code>RawDatabaseConstructionControlPanel</code> represents a 
 * control panel to construct a raw database of detected stars from 
 * the XML report documents.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 March 11
 */

public class RawDatabaseConstructionControlPanel extends XmlReportControlPanel {
	/**
	 * The frame.
	 */
	protected RawDatabaseConstructionInternalFrame frame;

	/**
	 * Creates a <code>RawDatabaseConstructionControlPanel</code>.
	 * @param operation the operation.
	 * @param table     the table.
	 * @param frame     the frame.
	 */
	public RawDatabaseConstructionControlPanel ( MultiTaskOperation operation, InformationTable table, RawDatabaseConstructionInternalFrame frame ) {
		super(operation, table);

		this.frame = frame;

		// Disables due to proceeded to the next operation.
		setSucceededMessageEnabled(false);
	}

	/**
	 * Invoked when the reset button is pushed. This must be overrided 
	 * in the subclasses.
	 * @param e contains the selected menu item.
	 */
	public void actionPerformedReset ( ActionEvent e ) {
		super.actionPerformedReset(e);

		frame.initializeDatabase();
	}
}
