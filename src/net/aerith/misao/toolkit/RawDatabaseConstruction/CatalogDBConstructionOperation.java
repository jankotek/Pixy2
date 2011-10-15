/*
 * @(#)CatalogDBConstructionOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.RawDatabaseConstruction;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.database.*;
import net.aerith.misao.pixy.identification.DefaultIdentifier;
import net.aerith.misao.toolkit.ReportBatch.ReportBatchOperation;

/**
 * The <code>CatalogDBConstructionOperation</code> represents an 
 * operation to construct a catalog database of detected stars from 
 * the XML report documents.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 6
 */

public class CatalogDBConstructionOperation extends ReportBatchOperation {
	/**
	 * The frame.
	 */
	protected RawDatabaseConstructionInternalFrame frame;

	/**
	 * Constructs a <code>CatalogDBConstructionOperation</code>.
	 * @param conductor the conductor of multi task operation.
	 * @param frame     the frame.
	 */
	public CatalogDBConstructionOperation ( MultiTaskConductor conductor, RawDatabaseConstructionInternalFrame frame ) {
		this.conductor = conductor;

		this.frame = frame;
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		return 0;
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

		monitor_set.addMessage(info.getPath());

		double limiting_mag = frame.getLimitingMagnitude();

		XmlReport report = file_manager.readReport(info);

		// Identifies with the detected stars in the catalog database.
		CatalogDBReader reader = new CatalogDBReader(getDBManager().getCatalogDBManager());
		DefaultIdentifier identifier = new DefaultIdentifier(report, reader);
		identifier.operate();

		monitor_set.addMessage("...identified.");

		// The astrometric error of the current XML document.
		double pos_error = info.getAstrometricErrorInArcsec();

		// The list of data to be deleted.
		Hashtable hash_to_delete = new Hashtable();

		// The list of data to be added.
		Vector list_to_add = new Vector();

		String class_name = new DetectedStar().getClass().getName();
		String star_class_name = StarClass.getClassName(new DetectedStar());

		// Checks if there are some detected stars not identified
		// with the data in the ctatalog database.
		XmlData data = (XmlData)report.getData();
		int star_count = data.getStarCount();
		XmlStar[] xml_stars = (XmlStar[])data.getStar();
		for (int i = 0 ; i < star_count ; i++) {
			StarImage star_image = xml_stars[i].getStarImage();

			if (star_image != null) {
				Star[] stars = xml_stars[i].getRecords(class_name);

				// Now that the results contains the StarImage and DetectedStar,
				// selects only the objects of DetectedStar class.
				Vector l = new Vector();
				for (int j = 0 ; j < stars.length ; j++) {
					if (stars[j] instanceof DetectedStar)
						l.addElement(stars[j]);
				}
				stars = new Star[l.size()];
				for (int j = 0 ; j < l.size() ; j++)
					stars[j] = (Star)l.elementAt(j);

				if (stars.length == 0) {
					if (star_image.getMag() < limiting_mag) {
						// In the case not recorded in the catalog database, 
						// adds the new data into the catalog database, 
						DetectedStar detected_star = new DetectedStar(star_image, pos_error);
						list_to_add.addElement(detected_star);
					}
				} else {
					DetectedStar detected_star = (DetectedStar)stars[0];

					if (stars.length > 1) {
						// In the case identified with several data, only the brightest
						// one is remained and the others are rejected.
						double brightest_mag = stars[0].getMag();
						for (int j = 1 ; j < stars.length ; j++) {
							if (brightest_mag > stars[j].getMag()) {
								detected_star = (DetectedStar)stars[j];
								brightest_mag = stars[j].getMag();
							}
						}
					}

					// Update the data in the catalog database if neccessary.
					if (detected_star.getPositionErrorInArcsec() > pos_error  ||
						detected_star.getMag() > star_image.getMag()) {
						hash_to_delete.put(star_class_name + " " + detected_star.getStarFolder(), detected_star);

						DetectedStar new_star = null;
						if (detected_star.getPositionErrorInArcsec() > pos_error)
							new_star = new DetectedStar(star_image, pos_error);
						else
							new_star = new DetectedStar(detected_star);
						new_star.setMag(detected_star.getMag() < star_image.getMag() ? detected_star.getMag() : star_image.getMag());
						list_to_add.addElement(new_star);
					}
				}
			}
		}

		// Deletes from the catalog database.
		getDBManager().getCatalogDBManager().deleteElements(hash_to_delete);

		// Adds to the catalog database;
		getDBManager().getCatalogDBManager().addElements(list_to_add);

		monitor_set.addMessage("...added to the catalog database.");
	}
}
