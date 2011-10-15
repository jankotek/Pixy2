/*
 * @(#)IdentificationDeleter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy.identification;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.io.CdromNotFoundException;

/**
 * The <code>IdentificationDeleter</code> is a class to delete the
 * catalog stars read from the specified catalog reader from the XML
 * document. The result will be stored in the XML document itself.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 December 27
 */

public class IdentificationDeleter extends Operation {
	/**
	 * The XML document.
	 */
	protected XmlReport report;

	/**
	 * The catalog reader.
	 */
	protected CatalogReader reader;

	/**
	 * Constructs an <code>IdentificationDeleter</code> with an XML 
	 * document and a catalog reader.
	 * @param report the XML document.
	 * @param reader the catalog reader.
	 */
	public IdentificationDeleter ( XmlReport report, CatalogReader reader ) {
		this.report = report;
		this.reader = reader;
	}

	/**
	 * Returns true if the operation is ready to start.
	 * @return true if the operation is ready to start.
	 */
	public boolean ready ( ) {
		return true;
	}

	/**
	 * Operates.
	 * @exception Exception if an error occurs.
	 */
	public void operate ( )
		throws Exception
	{
		monitor_set.addMessage("[Deleting identifications]");
		monitor_set.addMessage(new Date().toString());

		Exception running_exception = null;

		try {
			XmlInformation info = (XmlInformation)report.getInformation();
			XmlData data = (XmlData)report.getData();

			ChartMapFunction cmf = info.getChartMapFunction();
			DistortionField df = info.getDistortion();

			XmlSize size = (XmlSize)info.getSize();
			double fov = (double)size.getWidth() / cmf.getScaleUnitPerDegree();
			if (size.getWidth() < size.getHeight())
				fov = (double)size.getHeight() / cmf.getScaleUnitPerDegree();

			if (reader.hasFovLimit()  &&  fov >= reader.getFovLimit())
				throw new TooLargeFieldException();

			if (reader.isDateDependent()  &&  info.getMidDate() == null)
				throw new DocumentIncompleteException("date");

			if (reader.hasDateLimit()) {
				JulianDay today = JulianDay.create(new Date());
				if (today.getJD() - info.getMidDate().getJD() >= reader.getDateLimit())
					throw new ExpiredException();
			}

			if (reader.isDateDependent())
				reader.setDate(info.getMidDate());

			reader.open(cmf.getCenterCoor(), fov);

			CatalogStar star = reader.readNext();
			while (star != null) {
				star.mapCoordinatesToXY(cmf, df);

				if (Math.abs(star.getX()) < (double)size.getWidth() / 2.0 * 1.05  &&  Math.abs(star.getY()) < (double)size.getHeight() / 2.0 * 1.05) {
					star.add(new Position((double)size.getWidth() / 2.0, (double)size.getHeight() / 2.0));

					// Searches the star within the error box.
					double err = star.getPositionErrorInArcsec();
					if (err < 120.0)
						err = 120.0;
					double pixels = err / 3600.0 * cmf.getScaleUnitPerDegree();
					if (pixels < 10.0)
						pixels = 10.0;

					Vector list = data.getStarListAround(star, pixels);

					for (int i = 0 ; i < list.size() ; i++) {
						XmlStar document_star = (XmlStar)list.elementAt(i);

						boolean deleted = false;

						Vector records = document_star.getAllRecords();

						for (int j = 0 ; j < records.size() ; j++) {
							Star s = (Star)records.elementAt(j);
//							if (s.equals(star)) {
							if (s.getName().equals(star.getName())) {
								document_star.deleteRecord(s);

								notifySucceeded(document_star);

								monitor_set.addMessage(star.getName() + " = " + document_star.getName());

								deleted = true;
								break;
							}
						}

						if (deleted)
							break;
					}
				}

				star = reader.readNext();
			}

			reader.close();

			// Creates the position map again, because the typical 
			// (x,y) position may be changed after the identification.
			data.createStarMap(new Size(size.getWidth(), size.getHeight()));
		} catch ( CdromNotFoundException exception ) {
			running_exception = exception;
			monitor_set.addMessage("Failed to read " + exception.getDiskName() + ".");
		} catch ( IOException exception ) {
			running_exception = exception;
			monitor_set.addMessage("Failed to read catalog.");
		} catch ( QueryFailException exception ) {
			running_exception = exception;
			monitor_set.addMessage("Query to the server is failed.");
		} catch ( TooLargeFieldException exception ) {
			String message = "Failed. The field of view must be less than " + reader.getFovLimitMessage() + ".";
			running_exception = exception;
			monitor_set.addMessage(message);
		} catch ( DocumentIncompleteException exception ) {
			String message = "Failed. Please set " + exception.getMessage() + ".";
			if (exception.getMessage().equals("<date>"))
				message = "Failed. Please set image date.";
			running_exception = exception;
			monitor_set.addMessage(message);
		} catch ( ExpiredException exception ) {
			String message = "Failed. The image date must be within " + reader.getDateLimitMessage() + ".";
			running_exception = exception;
			monitor_set.addMessage(message);
		}

		monitor_set.addMessage(new Date().toString());
		monitor_set.addSeparator();

		if (running_exception != null)
			throw running_exception;
	}
}
