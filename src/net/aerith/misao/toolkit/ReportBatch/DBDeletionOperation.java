/*
 * @(#)DBDeletionOperation.java
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
 * The <code>DBDeletionOperation</code> represents a batch operation 
 * to delete XML report documents from the database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class DBDeletionOperation extends ReportBatchOperation {
	/**
	 * The dialog to select catalogs to delete the magnitude from
	 * the database.
	 */
	protected CatalogSelectionDialog dialog;

	/**
	 * The dialog to set up parameters to update XML report documents 
	 * in the database.
	 */
	protected InformationDBUpdateSettingDialog dialog2;

	/**
	 * Constructs a <code>DBDeletionOperation</code>.
	 * @param conductor the conductor of multi task operation.
	 */
	public DBDeletionOperation ( MultiTaskConductor conductor ) {
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

		String xml_path = manager.getInformationDBManager().getPath(xml_file);
		report.getInformation().setPath(xml_path);

		XmlInformation deleted_info = manager.getInformationDBManager().deleteElement(xml_path);
		if (deleted_info == null) {
			String message = "Failed to delete: " + xml_path;
			monitor_set.addMessage(message);
			monitor_set.addSeparator();
		} else {
			DefaultOperationObserver observer = new DefaultOperationObserver();
			manager.addObserver(observer);

			Vector catalog_list = dialog.getSelectedCatalogList();
			manager.deleteMagnitude(report, catalog_list);

			manager.deleteObserver(observer);

			Vector failed_list = observer.getFailedList();
			if (failed_list.size() > 0) {
				monitor_set.addMessage(xml_path);
				monitor_set.addMessage("Magnitude of the following stars are failed to delete:");

				for (int i = 0 ; i < failed_list.size() ; i++)
					monitor_set.addMessage(((CatalogStar)failed_list.elementAt(i)).getName());

				monitor_set.addSeparator();
			}

			Vector warned_list = observer.getWarnedList();
			if (warned_list.size() > 0) {
				monitor_set.addMessage(xml_path);
				if (dialog2.updatesReportedMagnitude())
					monitor_set.addMessage("Magnitude of the following stars are already reported:");
				else
					monitor_set.addMessage("Magnitude of the following stars are already reported and failed to delete:");

				for (int i = 0 ; i < warned_list.size() ; i++)
					monitor_set.addMessage(((CatalogStar)warned_list.elementAt(i)).getName());

				monitor_set.addSeparator();
			}
		}
	}
}
