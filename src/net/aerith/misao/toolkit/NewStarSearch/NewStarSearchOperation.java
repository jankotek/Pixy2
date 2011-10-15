/*
 * @(#)NewStarSearchOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.NewStarSearch;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.database.CatalogDBManager;
import net.aerith.misao.database.CatalogDBReader;
import net.aerith.misao.catalog.CatalogManager;
import net.aerith.misao.catalog.io.CatalogReader;
import net.aerith.misao.xml.*;
import net.aerith.misao.toolkit.ReportBatch.ReportBatchOperation;

/**
 * The <code>NewStarSearchOperation</code> represents a batch 
 * operation to search new stars from XML report documents.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 April 22
 */

public class NewStarSearchOperation extends ReportBatchOperation {
	/**
	 * The dialog to set the parameters to search new stars.
	 */
	protected NewStarSearchSettingDialog dialog;

	/**
	 * The catalog database manager.
	 */
	protected CatalogDBManager db_manager;

	/**
	 * The list of variability records of new stars.
	 */
	protected Vector list;

	/**
	 * Constructs a <code>NewStarSearchOperation</code>.
	 * @param conductor  the conductor of multi task operation.
	 * @param db_manager the catalog database manager.
	 */
	public NewStarSearchOperation ( MultiTaskConductor conductor, CatalogDBManager db_manager ) {
		this.conductor = conductor;

		this.db_manager = db_manager;
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		dialog = new NewStarSearchSettingDialog();
		return dialog.show(conductor.getPane());
	}

	/**
	 * Notifies when the operation starts.
	 */
	protected void notifyStart ( ) {
		list = new Vector();

		super.notifyStart();
	}

	/**
	 * Gets the variability records of new stars.
	 * @return the variability records of new stars.
	 */
	public Variability[] getVariabilityRecords ( ) {
		Variability[] records = new Variability[list.size()];
		for (int i = 0 ; i < list.size() ; i++)
			records[i] = (Variability)list.elementAt(i);
		return records;
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
			File xml_file = file_manager.newFile(info.getPath());
			XmlReport report = file_manager.readReport(info);
			XmlData data = (XmlData)report.getData();
			XmlStar[] stars = (XmlStar[])data.getStar();

			String catalog = report.getInformation().getBaseCatalog();
			String class_name = CatalogManager.getCatalogStarClassName(catalog);

			Vector list2 = new Vector();

			double limiting_mag = report.getInformation().getLimitingMag() - getAmplitude(info);

			int star_count = data.getStarCount();
			for (int i = 0 ; i < star_count ; i++) {
				StarImage star_image = stars[i].getStarImage();

				XmlMagRecord record = new XmlMagRecord(info, stars[i]);
				if (record.getPixelsFromEdge() == null  ||  record.getPixelsFromEdge().intValue() >= dialog.getIgnoreEdge()) {
					if (stars[i].getType().equals("NEW")) {
						if (star_image.getMag() < getLimitingMag(info))
							list2.add(stars[i]);
					} else if (star_image != null) {
						if (star_image.getMag() < limiting_mag) {
							Star[] catalog_stars = stars[i].getRecords(class_name);

							if (catalog_stars != null  &&  catalog_stars.length > 0) {
								boolean flag = true;
								for (int j = 0 ; j < catalog_stars.length ; j++) {
									double delta_mag = Math.abs(star_image.getMag() - catalog_stars[j].getMag());
									if (delta_mag < getAmplitude(info))
										flag = false;
								}

								if (flag)
									list2.add(stars[i]);
							}
						}
					}
				}
			}

			// The astrometric error of the current XML document.
			double pos_error = info.getAstrometricErrorInArcsec();

			for (int i = 0 ; i < list2.size() ; i++) {
				XmlStar xml_star = (XmlStar)list2.elementAt(i);

				XmlMagRecord record = new XmlMagRecord(info, xml_star);
				StarImage star_image = xml_star.getStarImage();
				DetectedStar star = new DetectedStar(star_image, pos_error);

				// Identifies with candidates from other images.
				Variability variability = null;
				for (int j = 0 ; j < list.size() ; j++) {
					Variability variability2 = (Variability)list.elementAt(j);
					CatalogStar catalog_star = variability2.getStar();

					if (star.getCoor().getAngularDistanceTo(catalog_star.getCoor()) * 3600.0 < star.getMaximumPositionErrorInArcsec() + catalog_star.getMaximumPositionErrorInArcsec()) {
						variability = variability2;
						break;
					}
				}
				if (variability == null) {
					XmlMagRecord[] records = new XmlMagRecord[1];
					records[0] = record;
					variability = new Variability(star, records);
					list.addElement(variability);

					// Identifies with catalog database.
					try {
						Vector identified_list = new Vector();

						CatalogReader reader = new CatalogDBReader(db_manager);

						StarList l = reader.read(star.getCoor(), 0.1);
						for (int j = 0 ; j < l.size() ; j++) {
							CatalogStar s = (CatalogStar)l.elementAt(j);

							double radius = star.getMaximumPositionErrorInArcsec() + s.getMaximumPositionErrorInArcsec();
							double distance = star.getCoor().getAngularDistanceTo(s.getCoor());
							if (distance < radius / 3600.0)
								identified_list.addElement(s);
						}

						CatalogStar id_star = (CatalogStar)CatalogManager.selectTypicalVsnetCatalogStar(identified_list);
						variability.setIdentifiedStar(id_star);
					} catch ( IOException exception ) {
					} catch ( QueryFailException exception ) {
					}
				} else {
					XmlMagRecord[] records = variability.getMagnitudeRecords();
					boolean found = false;
					for (int j = 0 ; j < records.length ; j++) {
						if (records[j].getDate().equals(record.getDate())) {
							found = true;
							break;
						}
					}
					if (! found)
						variability.addMagnitudeRecord(record);
				}
			}

			monitor_set.addMessage(info.getPath() + ": " + list2.size() + " stars");
		} catch ( Exception exception ) {
			monitor_set.addMessage(info.getPath() + ": error");
			throw exception;
		}
	}

	/**
	 * Gets the limiting magnitude.
	 * @param info the image information.
	 * @return the limiting magnitude.
	 */
	protected double getLimitingMag ( XmlInformation info ) {
		return dialog.getLimitingMag();
	}

	/**
	 * Gets the amplitude.
	 * @param info the image information.
	 * @return the amplitude.
	 */
	protected double getAmplitude ( XmlInformation info ) {
		return dialog.getAmplitude();
	}
}
