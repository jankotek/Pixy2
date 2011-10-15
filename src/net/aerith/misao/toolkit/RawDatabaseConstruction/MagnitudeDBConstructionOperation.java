/*
 * @(#)MagnitudeDBConstructionOperation.java
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
 * The <code>MagnitudeDBConstructionOperation</code> represents an 
 * operation to construct a magnitude database of detected stars from 
 * the XML report documents.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 6
 */

public class MagnitudeDBConstructionOperation extends ReportBatchOperation {
	/**
	 * Constructs a <code>MagnitudeDBConstructionOperation</code>.
	 * @param conductor the conductor of multi task operation.
	 */
	public MagnitudeDBConstructionOperation ( MultiTaskConductor conductor ) {
		this.conductor = conductor;
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

		XmlReport report = file_manager.readReport(info);

		// Identifies with the detected stars in the catalog database.
		CatalogDBReader reader = new CatalogDBReader(getDBManager().getCatalogDBManager());
		DefaultIdentifier identifier = new DefaultIdentifier(report, reader);
		identifier.operate();

		monitor_set.addMessage("...identified.");

		String class_name = new DetectedStar().getClass().getName();

		/*
		// In the case a detected star is identified with several data, only the brightest
		// one is remained and the others are rejected.
		XmlData data = (XmlData)report.getData();
		int star_count = data.getStarCount();
		XmlStar[] xml_stars = (XmlStar[])data.getStar();
		for (int i = 0 ; i < star_count ; i++) {
			StarImage star_image = xml_stars[i].getStarImage();
			if (star_image != null) {
				Star[] stars = xml_stars[i].getRecords(class_name);
				if (stars.length > 1) {
					DetectedStar detected_star = (DetectedStar)stars[0];
					double brightest_mag = stars[0].getMag();
					for (int j = 1 ; j < stars.length ; j++) {
						if (brightest_mag > stars[j].getMag()) {
							detected_star = (DetectedStar)stars[j];
							brightest_mag = stars[j].getMag();
						}
					}

					for (int j = 0 ; j < stars.length ; j++)
						xml_stars[i].deleteRecord(stars[j]);
					xml_stars[i].addStar(detected_star);
				}
			}
		}
		*/

		// Adds the magnitude data of the detected stars in the catalog database
		// into the magnitude database.
		Vector catalog_list = new Vector();
		catalog_list.addElement(new DetectedStar().getCatalogName());
		getDBManager().addMagnitude(report, catalog_list);

		monitor_set.addMessage("...added to the magnitude database.");
	}
}
