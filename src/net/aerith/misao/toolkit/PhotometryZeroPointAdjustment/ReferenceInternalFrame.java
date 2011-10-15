/*
 * @(#)ReferenceInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.PhotometryZeroPointAdjustment;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.catalog.CatalogManager;
import net.aerith.misao.catalog.io.CatalogReader;
import net.aerith.misao.database.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.pixy.Resource;
import net.aerith.misao.toolkit.RawDatabaseConstruction.RawDatabaseConstructionInternalFrame;
import net.aerith.misao.toolkit.RawDatabaseConstruction.RawDatabaseConstructionControlPanel;

/**
 * The <code>ReferenceInternalFrame</code> represents a frame to 
 * select reference XML report documents to adjust zero-point of 
 * photometry.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 July 15
 */

public class ReferenceInternalFrame extends RawDatabaseConstructionInternalFrame {
	/*
	 * The parent desktop.
	 */
	protected PhotometryZeroPointAdjustmentDesktop desktop;

	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>ReferenceInternalFrame</code>.
	 * @param desktop the parent desktop.
	 */
	public ReferenceInternalFrame ( PhotometryZeroPointAdjustmentDesktop desktop ) {
		super(desktop);

		this.desktop = desktop;

		pane = getContentPane();
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		return desktop.getSettingDialog().show(pane);
	}

	/**
	 * Returns the limiting magnitude of the catalog database.
	 * @return the limiting magnitude of the catalog database.
	 */
	public double getLimitingMagnitude ( ) {
		return desktop.getSettingDialog().getFainterLimitingMagnitude();
	}

	/**
	 * Returns true when to construct the magnitude database, too. 
	 * This method must be overrided in the subclasses.
	 * @return true when to construct the magnitude database, too. 
	 */
	public boolean constructsMagnitudeDatabase ( ) {
		return false;
	}

	/**
	 * Gets the mode of operation.
	 * @return the mode of operation.
	 */
	protected int getOperationMode ( ) {
		return control_panel.getCurrentMode();
	}

	/**
	 * Invoked when the raw database construction is started.
	 */
	protected void operationStarted ( ) {
	}

	/**
	 * Invoked when the raw database construction is succeeded.
	 */
	protected void operationSucceeded ( ) {
		String message = "Succeeded to create reference database.";
		JOptionPane.showMessageDialog(pane, message);

		desktop.setReferenceDBManager(db_manager);
	}

	/**
	 * Invoked when the raw database construction is failed.
	 */
	protected void operationFailed ( ) {
		String message = "Failed to create reference database.";
		JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Adds XML information documents. The XML file path must be 
	 * recorded in the information documents.
	 * @param infos the XML information documents.
	 * @exception IOException if I/O error occurs.
	 * @exception FileNotFoundException if a file does not exists.
	 */
	public void addInformations ( XmlInformation[] infos )
		throws IOException, FileNotFoundException
	{
		table.addInformations(infos, desktop.getFileManager());
	}
}
