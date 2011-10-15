/*
 * @(#)DBIdentificationOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ReportBatch;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.InformationTable;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.database.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.catalog.CatalogManager;
import net.aerith.misao.catalog.io.CatalogReader;
import net.aerith.misao.io.Encoder;
import net.aerith.misao.pixy.identification.DefaultIdentifier;

/**
 * The <code>DBIdentificationOperation</code> represents a batch 
 * operation to identify XML report documents with stars in the 
 * database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 6
 */

public class DBIdentificationOperation extends ReportBatchOperation {
	/**
	 * The dialog to set up parameters for identification.
	 */
	protected IdentificationSettingDialog dialog;

	/**
	 * Constructs an <code>DBIdentificationOperation</code>.
	 * @param conductor the conductor of multi task operation.
	 */
	public DBIdentificationOperation ( MultiTaskConductor conductor ) {
		this.conductor = conductor;
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		dialog = new IdentificationSettingDialog();
		return dialog.show(conductor.getPane());
	}

	/**
	 * Operates on one item. This is invoked from the conductor of 
	 * multi task operation.
	 * @param object the target object to operate.
	 * @exception Exception if an error occurs.
	 */
	public void operate ( Object object )
		throws Exception
	{
		XmlInformation info = (XmlInformation)object;

		File xml_file = file_manager.newFile(info.getPath());
		XmlReport report = file_manager.readReport(info);

		CatalogReader reader = null;
		try {
			reader = new CatalogDBReader(getDBManager().getCatalogDBManager());
		} catch ( IOException exception ) {
			reader = new CatalogDBReader(new GlobalDBManager().getCatalogDBManager());
		}

		DefaultIdentifier identifier = new DefaultIdentifier(report, reader);
		identifier.addMonitor(monitor_set);
		if (dialog.isNegativeDataIgnored())
			identifier.exceptNegative();

		monitor_set.addMessage(info.getPath());
		identifier.operate();

		((XmlSystem)report.getSystem()).setModifiedJD(JulianDay.create(new Date()));

		report.write(xml_file);
	}
}
