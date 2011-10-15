/*
 * @(#)ReportBatchControlPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ReportBatch;
import java.awt.event.*;
import net.aerith.misao.gui.*;

/**
 * The <code>ReportBatchControlPanel</code> represents a control panel
 * for batch operation on XML report documents.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 March 11
 */

public class ReportBatchControlPanel extends XmlReportControlPanel {
	/**
	 * The frame.
	 */
	protected ReportBatchInternalFrame frame;

	/**
	 * Creates an <code>ReportBatchControlPanel</code>.
	 * @param operation the operation.
	 * @param table     the table.
	 * @param frame     the frame.
	 */
	public ReportBatchControlPanel ( ReportBatchOperation operation, InformationTable table, ReportBatchInternalFrame frame ) {
		super(operation, table);

		this.frame = frame;
	}

	/**
	 * Invoked when the start button is pushed. This must be overrided 
	 * in the subclasses.
	 * @param e contains the selected menu item.
	 */
	public void actionPerformedStart ( ActionEvent e ) {
		frame.enableOperationSelection(false);
	}

	/**
	 * Invoked when the reset button is pushed. This must be overrided 
	 * in the subclasses.
	 * @param e contains the selected menu item.
	 */
	public void actionPerformedReset ( ActionEvent e ) {
		super.actionPerformedReset(e);

		frame.enableOperationSelection(true);
	}
}
