/*
 * @(#)AdditionalIdentificationOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.AdditionalIdentification;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.database.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.catalog.CatalogManager;
import net.aerith.misao.catalog.io.CatalogReader;
import net.aerith.misao.io.Encoder;
import net.aerith.misao.toolkit.ReportBatch.ReportBatchOperation;
import net.aerith.misao.pixy.identification.DefaultIdentifier;
import net.aerith.misao.pixy.identification.IdentificationDeleter;

/**
 * The <code>AdditionalIdentificationOperation</code> represents a 
 * batch operation to identify XML report documents with stars 
 * registered to the catalog database, and register the magnitude to 
 * the database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 December 27
 */

public class AdditionalIdentificationOperation extends ReportBatchOperation {
	/**
	 * The dialog to set up parameters to identify.
	 */
	protected IdentificationSettingDialog dialog;

	/**
	 * The dialog to select catalogs to register the magnitude to the
	 * database.
	 */
	protected CatalogSelectionDialog dialog2;

	/**
	 * The list of stars to identify with.
	 */
	protected CatalogReader reader_add = null;

	/**
	 * The list of stars to delete.
	 */
	protected CatalogReader reader_delete = null;

	/**
	 * The list of catalogs to register tha magnitude to the database.
	 */
	protected Vector catalog_list = new Vector();

	/**
	 * The list of stars failed to register tha magnitude to the 
	 * database.
	 */
	protected Hashtable hash_failed_stars = new Hashtable();

	/**
	 * The list of stars failed to register tha magnitude to the 
	 * database because already reported.
	 */
	protected Hashtable hash_warned_stars = new Hashtable();

	/**
	 * Constructs an <code>AdditionalIdentificationOperation</code>.
	 * @param conductor the conductor of multi task operation.
	 */
	public AdditionalIdentificationOperation ( MultiTaskConductor conductor ) {
		this.conductor = conductor;
	}

	/**
	 * Constructs an <code>AdditionalIdentificationOperation</code>.
	 * @param conductor     the conductor of multi task operation.
	 * @param reader_add    the list of stars to identify with.
	 * @param reader_delete the list of stars to delete.
	 */
	public AdditionalIdentificationOperation ( MultiTaskConductor conductor, CatalogReader reader_add, CatalogReader reader_delete ) {
		this.conductor = conductor;

		this.reader_add = reader_add;
		this.reader_delete = reader_delete;
	}

	/**
	 * Sets the catalog reader.
	 * @param reader_add    the list of stars to identify with.
	 * @param reader_delete the list of stars to delete.
	 */
	public void setCatalogReader ( CatalogReader reader_add, CatalogReader reader_delete ) {
		this.reader_add = reader_add;
		this.reader_delete = reader_delete;
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		dialog = new IdentificationSettingDialog();
		int answer = dialog.show(conductor.getPane());

		if (answer == 0) {
			// Selects catalogs to be registered into the database
			// among only the catalogs in the specified reader.
			try {
				Hashtable hash = new Hashtable();

				reader_add.open();
				CatalogStar star = reader_add.readNext();
				while (star != null) {
					hash.put(star.getCatalogName(), star);
					star = reader_add.readNext();
				}
				reader_add.close();

				reader_delete.open();
				star = reader_delete.readNext();
				while (star != null) {
					hash.put(star.getCatalogName(), star);
					star = reader_delete.readNext();
				}
				reader_delete.close();

				Enumeration enum = hash.keys();
				Vector list = new Vector();
				while (enum.hasMoreElements())
					list.addElement(enum.nextElement());

				dialog2 = new CatalogSelectionDialog(list);
			} catch ( Exception exception ) {
				dialog2 = new CatalogSelectionDialog();
			}

			answer = dialog2.show(conductor.getPane());

			if (answer == 0) {
				catalog_list = dialog2.getSelectedCatalogList();
			}
		}

		return answer;
	}

	/**
	 * Notifies when the operation starts.
	 */
	protected void notifyStart ( ) {
		hash_failed_stars = new Hashtable();
		hash_warned_stars = new Hashtable();

		super.notifyStart();
	}

	/**
	 * Notifies when the operation ends.
	 * @param exception the exception if an error occurs, or null if
	 * succeeded.
	 */
	protected void notifyEnd ( Exception exception ) {
		// Failed stars to register the magnitude to the database.
		if (hash_failed_stars.size() > 0) {
			Vector l = new Vector();
			Enumeration keys = hash_failed_stars.keys();
			while (keys.hasMoreElements()) 
				l.addElement(keys.nextElement());

			String header = "Magnitude of the following stars are failed to register:";
			MessagesDialog dialog = new MessagesDialog(header, l);
			dialog.show(conductor.getPane(), "Error", JOptionPane.ERROR_MESSAGE);
		}

		// Failed stars to register the magnitude to the database
		// because already reported.
		if (hash_warned_stars.size() > 0) {
			Vector l = new Vector();
			Enumeration keys = hash_warned_stars.keys();
			while (keys.hasMoreElements()) 
				l.addElement(keys.nextElement());

			String header = "Magnitude of the following stars are failed to register because already reported:";
			MessagesDialog dialog = new MessagesDialog(header, l);
			dialog.show(conductor.getPane(), "Warning", JOptionPane.WARNING_MESSAGE);
		}

		super.notifyEnd(exception);
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

		try {
			// In order to confirm whether some of the data in the reader
			// are really in the field of this image, tries to identify
			// with empty XML report document at first.
			XmlReport report = info.cloneEmptyReport();

			boolean identified = false;

			// Identifies with stars to be added.
			DefaultIdentifier identifier = new DefaultIdentifier(report, reader_add);
			DefaultOperationObserver observer = new DefaultOperationObserver();
			identifier.addObserver(observer);
			identifier.operate();
			identifier.deleteObserver(observer);
			if (observer.getSucceededList().size() > 0) {
				identified = true;
			} else {
				// Identifies with stars to be deleted.
				identifier = new DefaultIdentifier(report, reader_delete);
				observer = new DefaultOperationObserver();
				identifier.addObserver(observer);
				identifier.operate();
				identifier.deleteObserver(observer);
				if (observer.getSucceededList().size() > 0)
					identified = true;
			}

			// Continues only when identified with some stars.
			if (identified) {
				// Reads the XML report document.
				report = file_manager.readReport(info);

				boolean modified = false;

				Hashtable hash_failed = new Hashtable();
				Hashtable hash_warned = new Hashtable();

				// Deletes the old magnitude data from the database.
				observer = new DefaultOperationObserver();
				getDBManager().addObserver(observer);
				getDBManager().deleteMagnitude(report, catalog_list);
				getDBManager().deleteObserver(observer);

				Vector failed_list = observer.getFailedList();
				for (int i = 0 ; i < failed_list.size() ; i++) {
					CatalogStar star = (CatalogStar)failed_list.elementAt(i);
					hash_failed.put(star.getName(), star);
				}
				Vector warned_list = observer.getWarnedList();
				for (int i = 0 ; i < warned_list.size() ; i++) {
					CatalogStar star = (CatalogStar)warned_list.elementAt(i);
					hash_warned.put(star.getName(), star);
				}

				// Deletes the old records.
				IdentificationDeleter deleter = new IdentificationDeleter(report, reader_delete);
				observer = new DefaultOperationObserver();
				deleter.addObserver(observer);
				deleter.operate();
				deleter.deleteObserver(observer);
				if (observer.getSucceededList().size() > 0)
					modified = true;

				// Identifies.
				identifier = new DefaultIdentifier(report, reader_add);
				if (dialog.isNegativeDataIgnored())
					identifier.exceptNegative();
				observer = new DefaultOperationObserver();
				identifier.addObserver(observer);
				identifier.operate();
				identifier.deleteObserver(observer);
				if (observer.getSucceededList().size() > 0)
					modified = true;

				// Registers the magnitude data to the database.
				observer = new DefaultOperationObserver();
				getDBManager().addObserver(observer);
				getDBManager().addMagnitude(report, catalog_list);
				getDBManager().deleteObserver(observer);

				failed_list = observer.getFailedList();
				for (int i = 0 ; i < failed_list.size() ; i++) {
					CatalogStar star = (CatalogStar)failed_list.elementAt(i);
					hash_failed.put(star.getName(), star);
				}
				warned_list = observer.getWarnedList();
				for (int i = 0 ; i < warned_list.size() ; i++) {
					CatalogStar star = (CatalogStar)warned_list.elementAt(i);
					hash_warned.put(star.getName(), star);
				}

				// Shows the stars failed to update the magnitude data 
				// in the database.
				if (hash_failed.isEmpty() == false) {
					monitor_set.addMessage(info.getPath());
					monitor_set.addMessage("Magnitude of the following stars are failed to register:");

					Enumeration keys = hash_failed.keys();
					while (keys.hasMoreElements()) {
						CatalogStar star = (CatalogStar)hash_failed.get(keys.nextElement());
						monitor_set.addMessage(star.getName());
						hash_failed_stars.put(star.getName(), star);
					}

					monitor_set.addSeparator();
				}
				if (hash_warned.isEmpty() == false) {
					monitor_set.addMessage(info.getPath());
					monitor_set.addMessage("Magnitude of the following stars are already reported and failed to replace:");

					Enumeration keys = hash_warned.keys();
					while (keys.hasMoreElements()) {
						CatalogStar star = (CatalogStar)hash_warned.get(keys.nextElement());
						monitor_set.addMessage(star.getName());
						hash_warned_stars.put(star.getName(), star);
					}

					monitor_set.addSeparator();
				}

				// Continues only when modified.
				if (modified) {
					File xml_file = file_manager.newFile(info.getPath());

					((XmlSystem)report.getSystem()).setModifiedJD(JulianDay.create(new Date()));

					// Saves the XML report document.
					report.write(xml_file);

					// Replaces the image information.
					getDBManager().getInformationDBManager().deleteElement(info.getPath());
					getDBManager().addReport(xml_file, report);
				}
			}
		} catch ( Exception exception ) {
			notifyFailed(info);

			throw exception;
		}

		notifySucceeded(info);
	}
}
