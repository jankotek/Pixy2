/*
 * @(#)XmlData.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.CatalogManager;

/**
 * The <code>XmlData</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 * <p>
 * In order to reduce the memory assumption, all the star data are
 * converted into the internal data by <tt>masticateXml</tt> method 
 * after reading the XML file, and restored into the XML data by
 * <tt>composeXml</tt> method.
 * <p>
 * This object has a <code>PositionMap</code> of stars for the fast
 * search based on the (x,y) position.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 July 23
 */

public class XmlData extends net.aerith.misao.xml.relaxer.XmlData {
	/**
	 * The map of stars.
	 */
	protected PositionMap map = null;

	/**
	 * Gets the number of stars. Note that this method takes very long 
	 * time, so it must not be used in the loop or loop terminating
	 * condition.
	 * @return the number of stars.
	 */
	public int getStarCount ( ) {
		return getStar().length;
	}

	/**
	 * Creates the map of stars.
	 * @param size the image size.
	 */
	public void createStarMap ( Size size ) {
		Position top_left = new Position(0, 0);
		Position bottom_right = new Position(size.getWidth(), size.getHeight());
		map = new PositionMap(top_left, bottom_right);
		map.acceptOutOfBounds();
		map.divide(100, 100);

		XmlStar[] stars = (XmlStar[])getStar();
		for (int i = 0 ; i < stars.length ; i++) {
			try {
				map.addPosition(new StarPosition((stars[i])));
			} catch ( OutOfBoundsException exception ) {
				// Never happens.
			}
		}
	}

	/**
	 * Gets the star of the name.
	 * @param name the name.
	 * @return the star.
	 */
	public XmlStar getStar ( String name ) {
		XmlStar[] xml_stars = (XmlStar[])getStar();
		for (int i = 0 ; i < xml_stars.length ; i++) {
			if (xml_stars[i].getName().equals(name))
				return xml_stars[i];
		}
		return null;
	}

	/**
	 * Gets the star which contains the specified star object.
	 * @param star the star object.
	 * @return the star.
	 */
	public XmlStar getStar ( Star star ) {
		XmlStar[] xml_stars = (XmlStar[])getStar();
		for (int i = 0 ; i < xml_stars.length ; i++) {
			if (xml_stars[i].contains(star))
				return xml_stars[i];
		}
		return null;
	}

	/**
	 * Gets the list of stars around the specified position.
	 * @param position      the position.
	 * @param search_radius the radius to search.
	 * @return the list of stars within the search area.
	 */
	public Vector getStarListAround ( Position position, double search_radius ) {
		Vector list = new Vector();

		if (map != null) {
			map.acceptOutOfBounds();

			try {
				// Some stars have a large difference between the detected position
				// and the catalog position. Here adjusts the radius if too small,
				// in order to catch a star whose catalog position is out of the 
				// original search area but the detected position is in it.
				double radius = search_radius;
				if (radius < 10)
					radius = 10;

				Vector l = map.getPartialListWithinRadius(position, radius);
				for (int i = 0 ; i < l.size() ; i++) {
					StarPosition s = (StarPosition)l.elementAt(i);
					if (s.getDistanceFrom(position) < search_radius) {
						list.addElement(s.star);
					} else {
						StarImage star_image = s.star.getStarImage();
						if (star_image != null) {
							Position detected_position = new Position(star_image.getX(), star_image.getY());
							if (detected_position.getDistanceFrom(position) < search_radius)
								list.addElement(s.star);
						}
					}
				}
			} catch ( OutOfBoundsException exception ) {
				// Never happens.
			}
		}

		return list;
	}

	/**
	 * Gets the list of pairs which satisfy the specified photometry
	 * setting.
	 * @param setting the setting for photometry.
	 * @return the list of pairs.
	 */
	public Vector extractPairs ( PhotometrySetting setting ) {
		Vector pair_list = new Vector();

		String class_name = CatalogManager.getCatalogStarClassName(setting.getCatalogName());

		XmlStar[] xml_stars = (XmlStar[])getStar();
		for (int i = 0 ; i < xml_stars.length ; i++) {
			StarImage star_image = xml_stars[i].getStarImage();
			if (star_image != null) {
				Star[] catalog_stars = xml_stars[i].getRecords(class_name);

				// Selects stars which has the proper magnitude data.
				Vector proper_stars = new Vector();
				for (int j = 0 ; j < catalog_stars.length ; j++) {
					try {
						((CatalogStar)catalog_stars[j]).getMagnitude(setting.getMagnitudeSystem());

						proper_stars.addElement(catalog_stars[j]);
					} catch ( UnsupportedMagnitudeSystemException exception ) {
					}
				}
						
				// Only one star must be identified.
				if (proper_stars.size() == 1) {
					CatalogStar star = (CatalogStar)proper_stars.elementAt(0);
					StarPair pair = new StarPair(star_image, star);
					pair_list.addElement(pair);
				}
			}
		}

		return pair_list;
	}

	/**
	 * Gets the list of pairs which satisfy the specified astrometry
	 * setting.
	 * @param setting the setting for astrometry.
	 * @return the list of pairs.
	 */
	public Vector extractPairs ( AstrometrySetting setting ) {
		Vector pair_list = new Vector();

		String class_name = CatalogManager.getCatalogStarClassName(setting.getCatalogName());

		XmlStar[] xml_stars = (XmlStar[])getStar();
		for (int i = 0 ; i < xml_stars.length ; i++) {
			StarImage star_image = xml_stars[i].getStarImage();
			if (star_image != null) {
				Star[] catalog_stars = xml_stars[i].getRecords(class_name);
						
				// Only one star must be identified.
				if (catalog_stars.length == 1) {
					CatalogStar star = (CatalogStar)catalog_stars[0];
					StarPair pair = new StarPair(star_image, star);
					pair_list.addElement(pair);
				}
			}
		}

		return pair_list;
	}

	/**
	 * Gets the list of identified catalog names.
	 * @return the list of identified catalog names.
	 */
	public Vector getIdentifiedCatalogList ( ) {
		Hashtable hash = new Hashtable();

		hash.put(new DetectedStar().getCatalogName(), new DetectedStar());

		XmlStar[] stars = (XmlStar[])getStar();
		for (int i = 0 ; i < stars.length ; i++) {
			Vector record_list = (stars[i]).getAllRecords();
			for (int j = 0 ; j < record_list.size() ; j++) {
				Star s = (Star)record_list.elementAt(j);
				if (s instanceof CatalogStar)
					hash.put((String)((CatalogStar)s).getCatalogName(), s);
			}
		}

		Enumeration enum = hash.keys();
		Vector list = new Vector();
		while (enum.hasMoreElements())
			list.addElement(enum.nextElement());

		return list;
	}

	/**
	 * Gets the list of identified catalog names which supports the
	 * regular photometry.
	 * @return the list of identified catalog names which supports the
	 * regular photometry.
	 */
	public Vector getPhotometrySupportedCatalogList ( ) {
		Hashtable hash = new Hashtable();

		XmlStar[] stars = (XmlStar[])getStar();
		for (int i = 0 ; i < stars.length ; i++) {
			Vector record_list = (stars[i]).getAllRecords();
			for (int j = 0 ; j < record_list.size() ; j++) {
				Star s = (Star)record_list.elementAt(j);
				if (s instanceof CatalogStar) {
					if (((CatalogStar)s).supportsPhotometry())
						hash.put((String)((CatalogStar)s).getCatalogName(), s);
				}
			}
		}

		Enumeration enum = hash.keys();
		Vector list = new Vector();
		while (enum.hasMoreElements())
			list.addElement(enum.nextElement());

		return list;
	}

	/**
	 * Gets the list of identified catalog names which contains the
	 * magnitude data.
	 * @return the list of identified catalog names which contains the
	 * magnitude data.
	 */
	public Vector getMagnitudeSupportedCatalogList ( ) {
		Hashtable hash = new Hashtable();

		XmlStar[] stars = (XmlStar[])getStar();
		for (int i = 0 ; i < stars.length ; i++) {
			Vector record_list = (stars[i]).getAllRecords();
			for (int j = 0 ; j < record_list.size() ; j++) {
				Star s = (Star)record_list.elementAt(j);
				if (s instanceof CatalogStar) {
					if (((CatalogStar)s).supportsMagnitude())
						hash.put((String)((CatalogStar)s).getCatalogName(), s);
				}
			}
		}

		Enumeration enum = hash.keys();
		Vector list = new Vector();
		while (enum.hasMoreElements()) {
			String key = (String)enum.nextElement();
			CatalogStar star = (CatalogStar)hash.get(key);
			String[] names = star.getCatalogNamesWithMagnitudeSystem();
			for (int i = 0 ; i < names.length ; i++)
				list.addElement(names[i]);
		}

		return list;
	}

	/**
	 * Gets the list of identified catalog names which supports the
	 * astrometry.
	 * @return the list of identified catalog names which supports the
	 * astrometry.
	 */
	public Vector getAstrometrySupportedCatalogList ( ) {
		Hashtable hash = new Hashtable();

		XmlStar[] stars = (XmlStar[])getStar();
		for (int i = 0 ; i < stars.length ; i++) {
			Vector record_list = (stars[i]).getAllRecords();
			for (int j = 0 ; j < record_list.size() ; j++) {
				Star s = (Star)record_list.elementAt(j);
				if (s instanceof CatalogStar) {
					if (((CatalogStar)s).supportsAstrometry())
						hash.put((String)((CatalogStar)s).getCatalogName(), s);
				}
			}
		}

		Enumeration enum = hash.keys();
		Vector list = new Vector();
		while (enum.hasMoreElements())
			list.addElement(enum.nextElement());

		return list;
	}

	/**
	 * masticates the XML data into the internal star data.
	 */
	public void masticateXml ( ) {
		XmlStar[] stars = (XmlStar[])getStar();
		for (int i = 0 ; i < stars.length ; i++)
			(stars[i]).masticateXml();
	}

	/**
	 * Composes the internal star data into the XML data.
	 */
	public void composeXml ( ) {
		XmlStar[] stars = (XmlStar[])getStar();
		for (int i = 0 ; i < stars.length ; i++)
			(stars[i]).composeXml();
	}

	/**
	 * Deletes the XML data.
	 */
	public void deleteXml ( ) {
		XmlStar[] stars = (XmlStar[])getStar();
		for (int i = 0 ; i < stars.length ; i++)
			(stars[i]).deleteXml();
	}

	/**
	 * The <code>StarPosition</code> is a wrapper class to add a star
	 * object to the <code>PositionMap</code>.
	 */
	protected class StarPosition extends Position {
		/**
		 * The star.
		 */
		public XmlStar star;

		/**
		 * Constructs a <code>StarPosition</code> of the specified 
		 * star.
		 */
		public StarPosition ( XmlStar star ) {
			this.star = star;

			Position position = star.getTypicalPosition();

			setX(position.getX());
			setY(position.getY());
		}
	}
}
