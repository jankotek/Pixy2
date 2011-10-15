/*
 * @(#)XmlMagRecord.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.database.*;
import net.aerith.misao.io.Decoder;

/**
 * The <code>XmlMagRecord</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class XmlMagRecord extends net.aerith.misao.xml.relaxer.XmlMagRecord implements XmlDBRecord, StringOutputtable {
	/**
	 * Construct an <code>XmlMagRecord</code>.
	 */
	public XmlMagRecord ( ) {
	}

	/**
	 * Construct an <code>XmlMagRecord</code> of the specified star.
	 * @param info the image information.
	 * @param star the XML star element.
	 * @exception DocumentIncompleteException if some required data in
	 * the specified image information is not recorded.
	 */
	public XmlMagRecord ( XmlInformation info, XmlStar star )
		throws DocumentIncompleteException
	{
		if (info.getMidDate() == null)
			throw new DocumentIncompleteException("date");
		if (info.getObserver() == null)
			throw new DocumentIncompleteException("observer");
		if (info.getPath() == null)
			throw new DocumentIncompleteException("path");

		setDate(info.getMidDate().getOutputString(JulianDay.FORMAT_MONTH_IN_REDUCED, JulianDay.getAccuracy(info.getDate())));

		XmlMag mag = new XmlMag();
		StarImage star_image = star.getStarImage();
		if (star_image == null) {
			String s = Format.formatDouble(info.getProperUpperLimitMag(), 5, 2).trim();
			mag.setContent(Float.parseFloat(s));
			mag.setUpperLimit("true");
		} else {
			String s = Format.formatDouble(star_image.getMag(), 5, 2).trim();
			mag.setContent(Float.parseFloat(s));
		}
		setMag(mag);

		setFilter(info.getFilter());

		if (info.getPhotometry() == null) {
			setChip(info.getChip());
			setCatalog(info.getBaseCatalog());
		} else {
			PhotometrySetting setting = info.getPhotometrySetting();

			if (setting.getMethod() == PhotometrySetting.METHOD_STANDARD) {
				setCatalog(info.getPhotometry().getCatalog().getContent());
			} else if (setting.getMethod() == PhotometrySetting.METHOD_COMPARISON) {
				setChip(info.getChip());
				setCatalog(info.getPhotometry().getCatalog().getContent());
			} else {
				setChip(info.getChip());
				setCatalog(info.getPhotometry().getCatalog().getContent());
			}
		}

		setObserver(info.getObserver());

		if (info.getInstruments() != null)
			setInstruments(info.getInstruments());

		setImageXmlPath(info.getPath());

		setName(star.getName());

		Position position = star_image;
		if (position == null)
			position = star.getTypicalPosition();

		setPosition(new XmlPosition(position));

		// The pixels from the edge.
		int dx = info.getSize().getWidth() - 1 - (int)position.getX();
		if (dx > (int)position.getX())
			dx = (int)position.getX();
		int dy = info.getSize().getHeight() - 1 - (int)position.getY();
		if (dy > (int)position.getY())
			dy = (int)position.getY();
		if (dx > dy)
			dx = dy;
		setPixelsFromEdge(new Integer(dx));
	}

	/**
	 * Returns true when the record is already reported to VSNET.
	 * @return true when the record is already reported to VSNET.
	 */
	public boolean isReportedToVsnet ( ) {
		XmlReported[] reported = (XmlReported[])getReported();
		for (int i = 0 ; i < reported.length ; i++) {
			if (reported[i].getTo().equals("VSNET"))
				return true;
		}
		return false;
	}

	/**
	 * Returns true when the record is already reported to VSOLJ.
	 * @return true when the record is already reported to VSOLJ.
	 */
	public boolean isReportedToVsolj ( ) {
		XmlReported[] reported = (XmlReported[])getReported();
		for (int i = 0 ; i < reported.length ; i++) {
			if (reported[i].getTo().equals("VSOLJ"))
				return true;
		}
		return false;
	}

	/**
	 * Sets the record to be reported to VSNET.
	 */
	public void setReportedToVsnet ( ) {
		if (isReportedToVsnet() == false) {
			XmlReported reported = new XmlReported();
			reported.setTo("VSNET");
			addReported(reported);
		}
	}

	/**
	 * Sets the record to be reported to VSOLJ.
	 */
	public void setReportedToVsolj ( ) {
		if (isReportedToVsolj() == false) {
			XmlReported reported = new XmlReported();
			reported.setTo("VSOLJ");
			addReported(reported);
		}
	}

	/**
	 * Gets the attributes.
	 * @return the attributes.
	 */
	public MagnitudeRecordAttributes getAttributes ( ) {
		MagnitudeRecordAttributes attributes = new MagnitudeRecordAttributes();

		attributes.setReportedToVsnet(isReportedToVsnet());
		attributes.setReportedToVsolj(isReportedToVsolj());
		attributes.setUnofficial(getUnofficial() != null);
		attributes.setDiscarded(getDiscarded() != null);
		attributes.setPreempted(getPreempted() != null);
		attributes.setImported(getImported() != null);

		return attributes;
	}

	/**
	 * Sets the attributes.
	 * @param attributes the attributes.
	 */
	public void setAttributes ( MagnitudeRecordAttributes attributes ) {
		setReported(new XmlReported[0]);
		if (attributes.isReportedToVsnet())
			setReportedToVsnet();
		if (attributes.isReportedToVsolj())
			setReportedToVsolj();

		setUnofficial(null);
		if (attributes.isUnofficial())
			setUnofficial(new XmlUnofficial());

		setDiscarded(null);
		if (attributes.isDiscarded())
			setDiscarded(new XmlDiscarded());

		setPreempted(null);
		if (attributes.isPreempted())
			setPreempted(new XmlPreempted());

		setImported(null);
		if (attributes.isImported())
			setImported(new XmlImported());
	}

	/**
	 * Creates an empty <code>XmlDBRecord</code> object. This method 
	 * must be overrided by the subclass.
	 * @return the new empty object.
	 */
	public XmlDBRecord create ( ) {
		return new XmlMagRecord();
	}

	/**
	 * Adds the blending star.
	 * @param star the blending catalog star.
	 */
	public void addBlending ( CatalogStar star ) {
		XmlBlending blending = new XmlBlending();
		blending.setClassValue(StarClass.getClassName(star));
		blending.setContent(star.getName());
		addBlending(blending);
	}

	/**
	 * Returns true if the specified catalog star has been already 
	 * blended.
	 * @param sar the catalog star.
	 * @return true if the specified catalog star has been already 
	 * blended.
	 */
	public boolean blending ( CatalogStar star ) {
		String class_name = StarClass.getClassName(star);

		XmlBlending[] blendings = (XmlBlending[])getBlending();
		if (blendings != null) {
			for (int i = 0 ; i < blendings.length ; i++) {
				if (blendings[i].getClassValue().equals(class_name)  &&
					blendings[i].getContent().equals(star.getName()))
					return true;
			}
		}

		return false;
	}

	/**
	 * Gets the ID.
	 * @return the ID.
	 */
	public String getID ( ) {
		if (getImageXmlPath() != null)
			return getImageXmlPath();

		return "";
	}

	/**
	 * Returns a string representation of the state of this object.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputString ( ) {
		String s = "";

		s += getDate();
		s += "  " + ((XmlMag)getMag()).getOutputString();
		if (getFilter() != null)
			s += getFilter();
		if (getChip() != null)
			s += "  Chip:" + getChip();
		if (getCatalog() != null)
			s += "  Catalog:" + getCatalog();
		if (getObserver() != null)
			s += "  Observer:" + getObserver();
		if (getInstruments() != null)
			s += "  Instruments:" + getInstruments();
		if (getImageXmlPath() != null)
			s += "  Xml:" + getImageXmlPath();
		if (getName() != null)
			s += "  Name:" + getName();
		if (getPosition() != null) {
			String position = "(" + Format.formatDouble(getPosition().getX(), 7, 4) + "," + Format.formatDouble(getPosition().getY(), 7, 4) + ")";
			s += "  Position:" + position;
		}
		XmlBlending[] blendings = (XmlBlending[])getBlending();
		if (blendings != null  &&  blendings.length > 0) {
			s += "  Blending:";
			for (int i = 0 ; i < blendings.length ; i++) {
				if (i > 0)
					s += ",";
				s += blendings[i].getContent();
			}
		}

		return s;
	}
}
