/*
 * @(#)XmlPositionRecord.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import java.io.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.database.*;

/**
 * The <code>XmlPositionRecord</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 April 13
 */

public class XmlPositionRecord extends net.aerith.misao.xml.relaxer.XmlPositionRecord {
	/**
	 * Construct an <code>XmlPositionRecord</code>.
	 */
	public XmlPositionRecord ( ) {
	}

	/**
	 * Construct an <code>XmlPositionRecord</code> of the specified 
	 * star.
	 * @param info the image information.
	 * @param star the XML star element.
	 * @exception DocumentIncompleteException if some required data in
	 * the specified image information is not recorded.
	 * @exception NoDataException if the position is not recorded.
	 */
	public XmlPositionRecord ( XmlInformation info, XmlStar star )
		throws DocumentIncompleteException, NoDataException
	{
		if (info.getMidDate() == null)
			throw new DocumentIncompleteException("date");
		if (info.getObserver() == null)
			throw new DocumentIncompleteException("observer");
		if (info.getPath() == null)
			throw new DocumentIncompleteException("path");

		if (star == null)
			throw new NoDataException();
		StarImage star_image = star.getStarImage();
		if (star_image == null)
			throw new NoDataException();

		setDate(info.getMidDate().getOutputString(JulianDay.FORMAT_MONTH_IN_REDUCED, JulianDay.getAccuracy(info.getDate())));

		String s = star_image.getCoor().getOutputStringTo100mArcsecWithoutUnit();
		int p = s.indexOf('+');
		if (p < 0)
			p = s.indexOf('-');
		XmlCoor coor = new XmlCoor();
		coor.setRa(s.substring(0, p).trim());
		coor.setDecl(s.substring(p).trim());
		setCoor(coor);

		XmlMag mag = new XmlMag();
		s = Format.formatDouble(star_image.getMag(), 5, 2).trim();
		mag.setContent(Float.parseFloat(s));
		setMag(mag);

		setPosition(new XmlPosition(star_image));
		setArea(new Integer(star_image.getArea()));

		XmlPixelSize pixel_size = new XmlPixelSize();
		pixel_size.setWidth(info.getPixelSize().getWidth());
		pixel_size.setHeight(info.getPixelSize().getHeight());
		pixel_size.setUnit(info.getPixelSize().getUnit());
		setPixelSize(pixel_size);

		if (info.getAstrometricError() != null) {
			XmlAstrometricError err = new XmlAstrometricError();
			err.setRa(info.getAstrometricError().getRa());
			err.setDecl(info.getAstrometricError().getDecl());
			err.setUnit(info.getAstrometricError().getUnit());
			setAstrometricError(err);
		}

		if (info.getAstrometry() == null) {
			setCatalog(info.getBaseCatalog());
		} else {
			setCatalog(info.getAstrometrySetting().getDescription());
			setEquinox(info.getAstrometry().getEquinox());
		}

		setObserver(info.getObserver());

		if (info.getInstruments() != null)
			setInstruments(info.getInstruments());

		setImageXmlPath(info.getPath());

		setName(star.getName());

		// The pixels from the edge.
		Position position = star_image;
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
}
