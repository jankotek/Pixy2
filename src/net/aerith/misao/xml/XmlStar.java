/*
 * @(#)XmlStar.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.CatalogManager;

/**
 * The <code>XmlStar</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 * <p>
 * It keeps a list of <code>Star</code> objects, instead of a list of
 * <code>XmlRecord</code> objects. After reading the XML file, all the
 * <code>XmlRecord</code> objects are converted into the <code>Star</code>
 * objects and deleted, by the <tt>masticateXml</tt> method. Before
 * saving in the XML file, the <code>Star</code> objects are converted
 * into the <code>XmlRecord</code> objects by the <tt>composeXml</tt>
 * method. This will reduce the memory assumption.
 * <p>
 * Note that the list of star objects must not contain <code>MergedStar</code>.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 22
 */

public class XmlStar extends net.aerith.misao.xml.relaxer.XmlStar {
	/**
	 * The list of star objects.
	 */
	protected Vector stars;

	/**
	 * Constructs an <code>XmlStar</code>.
	 */
	public XmlStar ( ) {
		super();

		stars = new Vector();
	}

	/**
	 * Gets the type.
	 * @return the type.
	 */
	public String getType ( ) {
		return getName().substring(0, 3);
	}

	/**
	 * Gets the number in the type.
	 * @return the number in the type.
	 */
	public int getNumber ( ) {
		return Integer.parseInt(getName().substring(4));
	}

	/**
	 * Sets the name.
	 * @param type   the type of the star.
	 * @param number the number of the star.
	 */
	public void setName ( String type, int number ) {
		String s = Format.formatIntZeroPadding(number, 5);
		setName(type + "-" + s);
	}

	/**
	 * Adds a star object.
	 * @param star the star to add.
	 */
	public void addStar ( Star star ) {
		if (star instanceof MergedStar) {
			MergedStar s = (MergedStar)star;
			for (int i = 0 ; i < s.getStarCount() ; i++)
				addStar(s.getStarAt(i));
		} else {
			// Avoids adding duplicated data.
			/*
			for (int i = 0 ; i < stars.size() ; i++) {
				Star s = (Star)stars.elementAt(i);
				if (s.getName().equals(star.getName())  &&
					s.getClass().getName().equals(star.getClass().getName()))
					return;
			}
			*/

			stars.addElement(star);
		}
	}

	/**
	 * Gets the typical name of this star in a format for the VSNET 
	 * (Variable Star Network). It selects the GCVS name, NSV name, 
	 * the other variable star name, the star catalog name or the
	 * name based on the R.A. and Decl. in this order of priority.
	 * Note that only the catalogs in the specified list are considerd.
	 * @param catalog_list the list of valid catalog names.
	 * @return the typical name of this star.
	 */
	public String getTypicalVsnetName ( Vector catalog_list ) {
		Hashtable hash_valid = new Hashtable();
		for (int i = 0 ; i < catalog_list.size() ; i++)
			hash_valid.put((String)catalog_list.elementAt(i), this);

		DetectedStar detected_star = new DetectedStar();

		Vector valid_stars = new Vector();
		for (int i = 0 ; i < stars.size() ; i++) {
			Star s = (Star)stars.elementAt(i);
			if (s instanceof StarImage) {
				if (hash_valid.get(detected_star.getCatalogName()) != null)
					valid_stars.addElement(s);
			} else {
				if (hash_valid.get(((CatalogStar)s).getCatalogName()) != null)
					valid_stars.addElement(s);
			}
		}

		Star typical_star = CatalogManager.selectTypicalVsnetCatalogStar(valid_stars);
		if (typical_star == null)
			return "";

		return typical_star.getVsnetName();
	}

	/**
	 * Gets all the names of this star in a format for the VSNET 
	 * (Variable Star Network). If the star has a catalog record, the
	 * name based on the R.A. and Decl. is excluded from the result.
	 * @return all the names of this star.
	 */
	public String[] getAllVsnetNames ( ) {
		List l = new ArrayList();

		StarImage star_image = null;

		for (int i = 0 ; i < stars.size() ; i++) {
			Star star = (Star)stars.elementAt(i);
			if (star instanceof StarImage)
				star_image = (StarImage)star;
			else
				l.add(star.getVsnetName());
		}

		if (l.size() == 0  &&  star_image != null)
			l.add(star_image.getVsnetName());

		String[] array = new String[l.size()];
		return ((String[])l.toArray(array));
	}

	/**
	 * Gets the record of the detected star image.
	 * @return the record of the detected star image.
	 */
	public StarImage getStarImage ( ) {
		for (int i = 0 ; i < stars.size() ; i++) {
			Star star = (Star)stars.elementAt(i);
			if (star instanceof StarImage)
				return (StarImage)star;
		}

		return null;
	}

	/**
	 * Gets the array of records of the specified catalog star class.
	 * @param class_name the name of the catalog star class.
	 * @return the array of records of the specified catalog star 
	 * class.
	 */
	public Star[] getRecords ( String class_name ) {
		Vector list = new Vector();

		DetectedStar detected_star = new DetectedStar();

		for (int i = 0 ; i < stars.size() ; i++) {
			Star star = (Star)stars.elementAt(i);

			if (star.getClass().getName().equals(class_name))
				list.addElement(star);
			else if (detected_star.getClass().getName().equals(class_name)  &&
					 star instanceof StarImage)
				list.addElement(star);
		}

		Star[] records = new Star[list.size()];
		for (int i = 0 ; i < list.size() ; i++)
			records[i] = (Star)list.elementAt(i);

		return records;
	}

	/**
	 * Deletes the specified star object.
	 * @param star the star object to be deleted.
	 */
	public void deleteRecord ( Star star ) {
		for (int i = 0 ; i < stars.size() ; i++) {
			Star s = (Star)stars.elementAt(i);

			if (star == s) {
				stars.removeElement(star);
				return;
			}
		}
	}

	/**
	 * Gets all the record contained in this star.
	 * @return the list of all the record contained in this star.
	 */
	public Vector getAllRecords ( ) {
		return stars;
	}

	/**
	 * Returns true if this contains the specified star object.
	 * @param star the star object.
	 * @return true if this contains the specified star object.
	 */
	public boolean contains ( Star star ) {
		for (int i = 0 ; i < stars.size() ; i++) {
			Star s = (Star)stars.elementAt(i);
			if (star == s)
				return true;
		}
		return false;
	}

	/**
	 * Gets the typical (x,y) position of this star.
	 * <p>
	 * The position is decided in the following order of priority.
	 * <ol>
	 *   <li>position of the catalog for astrometry.
	 *   <li>position of the detected star.
	 *   <li>position of the other data.
	 * </ol>
	 */
	public Position getTypicalPosition ( ) {
		// 1. position of the catalog for astrometry.
		for (int i = 0 ; i < stars.size() ; i++) {
			if (stars.elementAt(i) instanceof CatalogStar) {
				CatalogStar catalog_star = (CatalogStar)stars.elementAt(i);
				if (catalog_star.getMaximumPositionErrorInArcsec() < 10.0)
					return new Position(catalog_star.getX(), catalog_star.getY());
			}
		}

		// 2. position of the detected star.
		if (getStarImage() != null)
			return new Position(getStarImage().getX(), getStarImage().getY());

		// 3. position of the other data.
		if (stars.size() > 0) {
			Star s = (Star)stars.elementAt(0);
			return new Position(s.getX(), s.getY());
		}

		return new Position();
	}

	/**
	 * masticates the XML data into the internal star data.
	 */
	public void masticateXml ( ) {
		stars = new Vector();

		XmlRecord[] records = (XmlRecord[])getRecord();
		for (int i = 0 ; i < records.length ; i++) {
			try {
				Star star = records[i].createStar();
				stars.addElement(star);
			} catch ( ClassNotFoundException exception ) {
				System.err.println(exception);
			} catch ( IllegalAccessException exception ) {
				System.err.println(exception);
			} catch ( InstantiationException exception ) {
				System.err.println(exception);
			}
		}
	}

	/**
	 * Composes the internal star data into the XML data.
	 */
	public void composeXml ( ) {
		deleteXml();

		for (int i = 0 ; i < stars.size() ; i++)
			addXmlData((Star)stars.elementAt(i));
	}

	/**
	 * Creates a <code>Record</code> of the specified star and adds
	 * to the array.
	 * @param star the star.
	 */
	private void addXmlData ( Star star ) {
		if (star instanceof MergedStar) {
			MergedStar s = (MergedStar)star;
			int count = s.getStarCount();
			for (int i = 0 ; i < count ; i++)
				addXmlData(s.getStarAt(i));
		} else {
			addRecord(new XmlRecord(star));
		}
	}

	/**
	 * Deletes the XML data.
	 */
	public void deleteXml ( ) {
		setRecord(new XmlRecord[0]);
	}

	/**
	 * Writes this XML document to the specified writer.
	 * @param out the writer.
	 * @exception IOException if I/O error occurs.
	 */
	public void write ( Writer out )
		throws IOException
	{
		out.write("    <star name=\"" + getName()  + "\">" + System.getProperty("line.separator"));

		for (int i = 0 ; i < stars.size() ; i++)
			writeStar(out, (Star)stars.elementAt(i));

		out.write("    </star>" + System.getProperty("line.separator"));
	}

	/**
	 * Writes the XML document of the specified star to the specified 
	 * writer.
	 * @param out  the writer.
	 * @param star the star.
	 * @exception IOException if I/O error occurs.
	 */
	private void writeStar ( Writer out, Star star )
		throws IOException
	{
		if (star instanceof MergedStar) {
			MergedStar s = (MergedStar)star;
			int count = s.getStarCount();
			for (int i = 0 ; i < count ; i++)
				writeStar(out, s.getStarAt(i));
		} else {
			new XmlRecord(star).write(out);
		}
	}
}
