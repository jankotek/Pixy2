/*
 * @(#)IdentificationOperation.java
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
 * The <code>IdentificationOperation</code> represents a batch 
 * operation to identify XML report documents with a specified catalog.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 October 10
 */

public class IdentificationOperation extends ReportBatchOperation {
	/**
	 * The dialog to select a catalog file or directory to identify 
	 * with.
	 */
	protected OpenIdentificationCatalogDialog dialog;

	/**
	 * Constructs an <code>IdentificationOperation</code>.
	 * @param conductor the conductor of multi task operation.
	 */
	public IdentificationOperation ( MultiTaskConductor conductor ) {
		this.conductor = conductor;
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		Vector catalog_list = CatalogManager.getIdentificationCatalogReaderList();
		dialog = new OpenIdentificationCatalogDialog(catalog_list);
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

		CatalogReader reader = dialog.getSelectedCatalogReader();

		String[] paths = net.aerith.misao.util.Format.separatePath(dialog.getCatalogPath());
		for (int i = 0 ; i < paths.length ; i++) {
			try {
				reader.addURL(new File(paths[i]).toURL());
			} catch ( MalformedURLException exception ) {
			}
		}

		// Tries if some stars are identified.

		XmlReport report = info.cloneEmptyReport();

		DefaultOperationObserver observer = new DefaultOperationObserver();

		DefaultIdentifier identifier = new DefaultIdentifier(report, reader);
		identifier.addObserver(observer);
		identifier.operate();

		Vector identified_list = observer.getSucceededList();
		if (identified_list.size() > 0) {
			// Reads the XML report document and identifies
			// only if some stars are identified.

			File xml_file = file_manager.newFile(info.getPath());
			report = file_manager.readReport(info);

			identifier = new DefaultIdentifier(report, reader);
			identifier.addMonitor(monitor_set);
			if (dialog.isNegativeDataIgnored())
				identifier.exceptNegative();

			monitor_set.addMessage(info.getPath());
			identifier.operate();

			((XmlSystem)report.getSystem()).setModifiedJD(JulianDay.create(new Date()));

			report.write(xml_file);
		}
	}
}
