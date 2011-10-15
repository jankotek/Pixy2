/*
 * @(#)DBRegistrationOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ReportBatch;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.CatalogStar;
import net.aerith.misao.gui.InformationTable;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.database.*;
import net.aerith.misao.xml.*;

/**
 * The <code>DBRegistrationOperation</code> represents a batch 
 * operation to register XML report documents into the database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class DBRegistrationOperation extends ReportBatchOperation {
	/**
	 * The dialog to select catalogs to register the magnitude into 
	 * the database.
	 */
	protected CatalogSelectionDialog dialog;

	/**
	 * The dialog to set up parameters to update XML report documents 
	 * in the database.
	 */
	protected InformationDBUpdateSettingDialog dialog2;

	/**
	 * Constructs a <code>DBRegistrationOperation</code>.
	 * @param conductor the conductor of multi task operation.
	 */
	public DBRegistrationOperation ( MultiTaskConductor conductor ) {
		this.conductor = conductor;
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		dialog = new CatalogSelectionDialog();
		int answer = dialog.show(conductor.getPane());
		if (answer == 0) {
			dialog2 = new InformationDBUpdateSettingDialog(InformationDBUpdateSettingDialog.MODE_REPLACE);
			answer = dialog2.show(conductor.getPane());
		}
		return answer;
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

		GlobalDBManager manager = getDBManager();
		manager.enableReportedMagnitudeUpdate(dialog2.updatesReportedMagnitude());

		try {
			manager.addReport(xml_file, report);
		} catch ( DuplicatedException exception ) {
			// Replaces the image information.
			String message = "Replace: " + xml_file.getPath();
			monitor_set.addMessage(message);

			XmlInformation duplicated_info = (XmlInformation)exception.getDuplicatedObject();
			if (null == manager.getInformationDBManager().deleteElement(duplicated_info.getPath())) {
				message = "Failed to delete " + duplicated_info.getPath();
				monitor_set.addMessage(message);
				monitor_set.addSeparator();
				return;
			}

			manager.addReport(xml_file, report);
		}

		DefaultOperationObserver observer = new DefaultOperationObserver();
		manager.addObserver(observer);

		Vector catalog_list = dialog.getSelectedCatalogList();
		manager.addMagnitude(report, catalog_list);

		manager.deleteObserver(observer);

		Vector failed_list = observer.getFailedList();
		if (failed_list.size() > 0) {
			monitor_set.addMessage(info.getPath());
			monitor_set.addMessage("Magnitude of the following stars are failed to register:");

			for (int i = 0 ; i < failed_list.size() ; i++)
				monitor_set.addMessage(((CatalogStar)failed_list.elementAt(i)).getName());

			monitor_set.addSeparator();
		}

		Vector warned_list = observer.getWarnedList();
		if (warned_list.size() > 0) {
			monitor_set.addMessage(info.getPath());
			if (dialog2.updatesReportedMagnitude())
				monitor_set.addMessage("Magnitude of the following stars are already reported:");
			else
				monitor_set.addMessage("Magnitude of the following stars are already reported and failed to replace:");

			for (int i = 0 ; i < warned_list.size() ; i++)
				monitor_set.addMessage(((CatalogStar)warned_list.elementAt(i)).getName());

			monitor_set.addSeparator();
		}
	}
}
