/*
 * @(#)DefaultIdentifier.java
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
 * The <code>DefaultIdentifier</code> is a class to identify catalog 
 * stars read from the specified catalog reader with stars in the XML
 * document. The result will be stored in the XML document itself.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class DefaultIdentifier extends Operation {
	/**
	 * The XML document.
	 */
	protected XmlReport report;

	/**
	 * The catalog reader.
	 */
	protected CatalogReader reader;

	/**
	 * True if a star is added even when no counterpart star is found
	 * in the XML document.
	 */
	protected boolean accept_negative = true;

	/**
	 * Constructs a <code>DefaultIdentifier</code> with an XML document and a
	 * catalog reader.
	 * @param report the XML document.
	 * @param reader the catalog reader.
	 */
	public DefaultIdentifier ( XmlReport report, CatalogReader reader ) {
		this.report = report;
		this.reader = reader;
	}

	/**
	 * Accepts to add the negative data.
	 */
	public void acceptNegative ( ) {
		accept_negative = true;
	}

	/**
	 * Excepts to add the negative data.
	 */
	public void exceptNegative ( ) {
		accept_negative = false;
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
		monitor_set.addMessage("[Identifying stars]");
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

			double limit_mag = (double)info.getLimitingMag();
			reader.setLimitingMagnitude(limit_mag);

			if (reader.isDateDependent())
				reader.setDate(info.getMidDate());

			reader.open(cmf.getCenterCoor(), fov);

			CatalogStar star = reader.readNext();
			while (star != null) {
				star.mapCoordinatesToXY(cmf, df);
				star.add(new Position((double)size.getWidth() / 2.0, (double)size.getHeight() / 2.0));

				StarExtractor extractor = null;
				int extract_type = 0;
				XmlStar document_star = null;

				// Searches the star within the error box.
				double err = star.getPositionErrorInArcsec();
				extractor = new StarExtractor(data);
				extractor.setType(StarExtractor.TYPE_ANY_CATALOG_OR_NEW);
				if (err < 5.0) {
					// Searches the star almost at the same position.
					document_star = extractor.extractOne(star, 5.0 / 3600.0 * cmf.getScaleUnitPerDegree());
				} else {
					// Searches the brightest star within the error box,
					// in the case of IRAS objects, for example.
					document_star = extractor.extractBrightestOne(star, err / 3600.0 * cmf.getScaleUnitPerDegree());
				}

				// Searches the brightest star within 2.5 pixels.
				double max_err = star.getMaximumPositionErrorInArcsec();
				if (document_star == null) {
					if (max_err >= 10.0  &&  max_err > err) {
						// Searches within the maximum error box, 
						// in the case of GCVS object, for example.
						double pixels = max_err / 3600.0 * cmf.getScaleUnitPerDegree();
						if (pixels < 2.5)
							pixels = 2.5;
						extractor = new StarExtractor(data);
						extractor.setType(StarExtractor.TYPE_ANY_CATALOG_OR_NEW);
						document_star = extractor.extractBrightestOne(star, pixels);
					} else {
						// Searches the brightest NEW star within 2.5 pixels.
						extractor = new StarExtractor(data);
						extractor.setType(StarExtractor.TYPE_ONLY_NEW);
						document_star = extractor.extractBrightestOne(star, 2.5);
					}
				}

				// Searches the blending with a bright star.
				if (document_star == null) {
					extractor = new StarExtractor(data);
					extractor.setType(StarExtractor.TYPE_BLENDING);
					document_star = extractor.extractOne(star, 10);
				}

				// Searches the identification between negative data.
				if (document_star == null) {
					if (accept_negative) {
						extractor = new StarExtractor(data);
						extractor.setType(StarExtractor.TYPE_ONLY_NEG);
						Vector list = extractor.extractAll(star, 120.0 / 3600.0 * cmf.getScaleUnitPerDegree());
						double minimum_distance = 0.0;
						for (int i = 0 ; i < list.size() ; i++) {
							XmlStar s = (XmlStar)list.elementAt(i);
							if (s.getType().equals("NEG")) {
								Vector records = s.getAllRecords();
								for (int j = 0 ; j < records.size() ; j++) {
									CatalogStar record = (CatalogStar)records.elementAt(j);
									double distance = record.getDistanceFrom(star);
									if (distance < star.getMaximumPositionErrorInArcsec() / 3600.0 * cmf.getScaleUnitPerDegree()  ||
										distance < record.getMaximumPositionErrorInArcsec() / 3600.0 * cmf.getScaleUnitPerDegree()) {
										if (document_star == null  ||  distance < minimum_distance) {
											document_star = s;
											minimum_distance = distance;
										}
									}
								}
							}
						}
					}
				}

				// Identified.
				if (document_star != null) {
					document_star.addStar(star);

					notifySucceeded(document_star);
				} else if (accept_negative) {
					if (0 <= star.getX()  &&  star.getX() < size.getWidth()  &&
						0 <= star.getY()  &&  star.getY() < size.getHeight()) {
						int neg_number = 0;
						if (info.getStarCount() != null) {
							neg_number = info.getStarCount().getNeg() + 1;
							info.getStarCount().setNeg(neg_number);
						}

						document_star = new XmlStar();
						document_star.setName("NEG", neg_number);
						document_star.addStar(star);
						data.addStar(document_star);

						notifySucceeded(document_star);
					}
				}

				if (document_star != null)
					monitor_set.addMessage(star.getName() + " = " + document_star.getName());

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

	/**
	 * The <code>StarExtractor</code> is a class to extract stars from
	 * the XML data object within the specified pixels.
	 */
	protected class StarExtractor {
		/**
		 * The number of type of distance judgement which represents
		 * to check between any star catalog position, or between
		 * detected position if no star catalog is identified.
		 */
		public final static int TYPE_ANY_CATALOG_OR_NEW = 1;

		/**
		 * The number of type of distance judgement which represents
		 * to check between only detected position of NEW stars.
		 */
		public final static int TYPE_ONLY_NEW = 2;

		/**
		 * The number of type of distance judgement which represents
		 * to check between only most accurate position of NEG stars.
		 */
		public final static int TYPE_ONLY_NEG = 3;

		/**
		 * The number of type of distance judgement which represents
		 * to check between only the detected stars, considering the
		 * radius of the star image.
		 */
		public final static int TYPE_BLENDING = 4;

		/**
		 * The number of selection method to select a star whose 
		 * position is closest to the specified position.
		 */
		protected final static int SELECT_CLOSEST = 101;

		/**
		 * The number of selection method to select the brightest star.
		 */
		protected final static int SELECT_BRIGHTEST = 102;

		/**
		 * The number of selection method to select all stars within
		 * the search area.
		 */
		protected final static int SELECT_ALL = 103;

		/**
		 * The number of type.
		 */
		protected int type = TYPE_ANY_CATALOG_OR_NEW;

		/**
		 * The XML data object.
		 */
		protected XmlData data;

		/**
		 * Constructs a <code>StarExtractor</code> with the XML data
		 * object.
		 * @param data the XML data object.
		 */
		public StarExtractor ( XmlData data ) {
			this.data = data;
		}

		/**
		 * Sets the type.
		 * @param type the number of type.
		 */
		public void setType ( int type ) {
			this.type = type;
		}

		/**
		 * Extracts a star within the specified search area whose 
		 * position is closest to the specified position.
		 * @param position    the base position.
		 * @param search_area the search area size.
		 * @return the closest star.
		 */
		public XmlStar extractOne ( Position position, double search_area ) {
			return (XmlStar)extract(position, search_area, SELECT_CLOSEST);
		}

		/**
		 * Extracts the brightest star within the specified search 
		 * area.
		 * @param position    the base position.
		 * @param search_area the search area size.
		 * @return the brightest star.
		 */
		public XmlStar extractBrightestOne ( Position position, double search_area ) {
			return (XmlStar)extract(position, search_area, SELECT_BRIGHTEST);
		}

		/**
		 * Extracts a list of stars within the specified search area.
		 * @param position    the base position.
		 * @param search_area the search area size.
		 * @return the list of stars.
		 */
		public Vector extractAll ( Position position, double search_area ) {
			return (Vector)extract(position, search_area, SELECT_ALL);
		}

		/**
		 * Extracts a star or stars within the specified search area.
		 * @param position      the base position.
		 * @param search_area   the search area size.
		 * @param select_method the method to select a star or stars.
		 * @return a star or a list of stars.
		 */
		protected Object extract ( Position position, double search_area, int select_method ) {
			Vector list = data.getStarListAround(position, search_area + 2.5);

			XmlStar extracted_star = null;
			Vector extracted_stars = new Vector();
			double minimum_distance = 0.0;
			double brightest_mag = 99.9;

			for (int i = 0 ; i < list.size() ; i++) {
				XmlStar star = (XmlStar)list.elementAt(i);

				boolean extracted = false;

				Vector records = star.getAllRecords();
				for (int j = 0 ; j < records.size() ; j++) {
					Star s = null;

					if (records.elementAt(j) instanceof StarImage) {
						StarImage star_image = (StarImage)records.elementAt(j);

						if (star.getType().equals("NEW")  &&
							(type == TYPE_ANY_CATALOG_OR_NEW  ||  type == TYPE_ONLY_NEW)) {
							s = star_image;
						} else if (type == TYPE_BLENDING) {
							s = star_image;
						}
					} else {
						CatalogStar catalog_star = (CatalogStar)records.elementAt(j);
						if (type == TYPE_ANY_CATALOG_OR_NEW) {
							if (catalog_star.getMaximumPositionErrorInArcsec() < 10.0)
								s = catalog_star;
						} else if (type == TYPE_ONLY_NEG) {
							if (star.getType().equals("NEG"))
								s = catalog_star;
						}
					}

					if (s != null) {
						double distance = position.getDistanceFrom(s);
						if (distance < search_area) {
							extracted = true;

							if (type == TYPE_BLENDING) {
								double radius = ((StarImage)s).getRadius();
								if (distance >= radius * 1.5)
									extracted = false;
							}

							if (extracted) {
								if (select_method == SELECT_CLOSEST) {
									if (extracted_star == null  ||  distance < minimum_distance) {
										extracted_star = star;
										minimum_distance = distance;
									}
								}
								if (select_method == SELECT_BRIGHTEST) {
									double mag = 99.9;
									if (star.getStarImage() != null)
										mag = star.getStarImage().getMag();
									if (extracted_star == null  ||  mag < brightest_mag) {
										extracted_star = star;
										brightest_mag = mag;
									}
								}
							}
						}
					}
				}

				if (extracted)
					extracted_stars.addElement(star);
			}

			if (select_method == SELECT_ALL)
				return extracted_stars;

			return extracted_star;
		}
	}
}
