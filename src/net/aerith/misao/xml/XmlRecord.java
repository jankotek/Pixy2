/*
 * @(#)XmlRecord.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import java.io.*;
import net.aerith.misao.database.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;

/**
 * The <code>XmlRecord</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 19
 */

public class XmlRecord extends net.aerith.misao.xml.relaxer.XmlRecord implements XmlDBRecord {
	/**
	 * Construct an <code>XmlRecord</code>.
	 */
	public XmlRecord ( ) {
	}

	/**
	 * Construct an <code>XmlRecord</code> of the specified star.
	 * @param star the star.
	 */
	public XmlRecord ( Star star ) {
		if (star instanceof StarImage) {
		} else {
			setName(star.getName());
		}

		String s = star.getCoorString();
		int p = s.indexOf('+');
		if (p < 0)
			p = s.indexOf('-');
		XmlCoor coor = new XmlCoor();
		coor.setRa(s.substring(0, p).trim());
		coor.setDecl(s.substring(p).trim());
		setCoor(coor);

		setPosition(new XmlPosition(star));

		KeyAndValue[] key_and_values = star.getKeyAndValues();
		XmlItem[] items = new XmlItem[key_and_values.length];
		if (key_and_values != null) {
			for (int i = 0 ; i < key_and_values.length ; i++) {
				items[i] = new XmlItem();
				items[i].setKey(key_and_values[i].getKey());
				items[i].setContent(key_and_values[i].getValue());
			}
		}
		setItem(items);

		setClassValue(StarClass.getClassName(star));
	}

	/**
	 * Converts this XML element to the star object.
	 * @return the star object.
	 * @exception ClassNotFoundException if the star class recorded in
	 * the XML document is not found.
	 * @exception IllegalAccessException if the class or initializer 
	 * is not accessible.
	 * @exception InstantiationException if an application tries to
	 * instantiate an abstract class or an interface, or if the 
	 * instantiation fails for some other reason.
	 */
	public Star createStar ( )
		throws ClassNotFoundException, IllegalAccessException, InstantiationException
	{
		Star star = StarClass.newInstance(getClassValue());

		if (star instanceof CatalogStar)
			((CatalogStar)star).setName(getName());

		XmlCoor coor = (XmlCoor)getCoor();
		star.setCoor(Coor.create(coor.getRa() + " " + coor.getDecl()));
		if (star instanceof CatalogStar)
			((CatalogStar)star).setCoorAccuracy(Coor.getAccuracy(coor.getRa() + " " + coor.getDecl()));

		XmlPosition position = (XmlPosition)getPosition();
		if (position != null)
			star.setPosition(new Position((double)position.getX(), (double)position.getY()));

		XmlItem[] items = (XmlItem[])getItem();
		for (int j = 0 ; j < items.length ; j++) {
			KeyAndValue key_and_value = new KeyAndValue(items[j].getKey(), items[j].getContent());
			star.setKeyAndValue(key_and_value);
		}

		if (star instanceof StarImage)
			((StarImage)star).enableOutputCoordinates();

		return star;
	}

	/**
	 * Creates an empty <code>XmlDBRecord</code> object. This method 
	 * must be overrided by the subclass.
	 * @return the new empty object.
	 */
	public XmlDBRecord create ( ) {
		return new XmlRecord();
	}

	/**
	 * Gets the ID.
	 * @return the ID.
	 */
	public String getID ( ) {
		try {
			Star star = createStar();

			String class_name = StarClass.getClassName(star);

			if (star instanceof CatalogStar)
				return class_name + " " + ((CatalogStar)star).getStarFolder();

			return class_name + " " + getName();
		} catch ( ClassNotFoundException exception ) {
			System.err.println(exception);
		} catch ( IllegalAccessException exception ) {
			System.err.println(exception);
		} catch ( InstantiationException exception ) {
			System.err.println(exception);
		}

		return "";
	}

	/**
	 * Writes this XML document to the specified writer.
	 * @param out the writer.
	 * @exception IOException if I/O error occurs.
	 */
	public void write ( Writer out )
		throws IOException
	{
		out.write("      <record class=\"" + getClassValue()  + "\">" + System.getProperty("line.separator"));

		if (getName() != null)
			out.write("\t<name>" + translateTo(getName())  + "</name>" + System.getProperty("line.separator"));

		XmlCoor coor = (XmlCoor)getCoor();
		out.write("\t<coor>" + System.getProperty("line.separator"));
		out.write("\t  <ra>" + coor.getRa() + "</ra>" + System.getProperty("line.separator"));
		out.write("\t  <decl>" + coor.getDecl() + "</decl>" + System.getProperty("line.separator"));
		out.write("\t</coor>" + System.getProperty("line.separator"));

		XmlPosition position = (XmlPosition)getPosition();
		if (position != null) {
			out.write("\t<position>" + System.getProperty("line.separator"));
			out.write("\t  <x>" + position.getX() + "</x>" + System.getProperty("line.separator"));
			out.write("\t  <y>" + position.getY() + "</y>" + System.getProperty("line.separator"));
			out.write("\t</position>" + System.getProperty("line.separator"));
		}

		XmlItem[] items = (XmlItem[])getItem();
		for (int i = 0 ; i < items.length ; i++)
			out.write("\t<item key=\"" + translateTo(items[i].getKey()) + "\">" + translateTo(items[i].getContent()) + "</item>" + System.getProperty("line.separator"));

		out.write("      </record>" + System.getProperty("line.separator"));
	}
}
