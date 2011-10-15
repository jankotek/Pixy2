/*
 * @(#)Asas3Star.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.star;
import java.io.*;
import java.util.*;
import java.awt.Color;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.PlotProperty;

/**
 * The <code>Asas3Star</code> represents a star data in the ASAS-3 
 * Catalog of Variable Stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 December 31
 */

public class Asas3Star extends DefaultStar {
	/**
	 * Constructs an empty <code>Asas3Star</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public Asas3Star ( ) {
		super();
	}

	/**
	 * Constructs an <code>Asas3Star</code> with data read from the
	 * catalog file.
	 * @param id     the ID.
	 * @param mag    the V magnitude.
	 * @param range  the magnitude range.
	 * @param type   the type.
	 * @param period the period.
	 * @param epoch  the epoch.
	 */
	public Asas3Star ( String id,
					   String mag,
					   String range,
					   String type,
					   String period,
					   String epoch )
	{
		super();

		Coor coor = Coor.create(id.substring(0, 2) + " " + id.substring(2, 4) + " " + id.substring(4, 6) + " " + id.substring(6, 9) + " " + id.substring(9));

		this.name = id;
		setCoor(coor);
		setCoorAccuracy(Coor.ACCURACY_100M_ARCMIN_HOURSEC);

		setKeyAndValue(new KeyAndValue("Mag(V)", mag));
		setKeyAndValue(new KeyAndValue("Range", range));
		if (type != null  &&  type.length() > 0)
			setKeyAndValue(new KeyAndValue("Type", type));
		if (period != null  &&  period.length() > 0)
			setKeyAndValue(new KeyAndValue("Period", period));
		if (epoch != null  &&  epoch.length() > 0)
			setKeyAndValue(new KeyAndValue("Epoch", epoch));
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "ASAS3 J";
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "ASAS-3 Catalog of Variable Stars";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "ASAS3";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "ASAS3";
	}

	/**
	 * Gets the catalog category. It must be overrided in the 
	 * subclasses.
	 * @return the catalog category.
	 */
	protected int getCatalogCategoryNumber ( ) {
		return CatalogManager.CATEGORY_VARIABLE;
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(getCatalogCategoryNumber()));
		folder_list.addElement(getCatalogFolderCode());

		int p = name.indexOf('+');
		if (p < 0)
			p = name.indexOf('-');

		folder_list.addElement(name.substring(0, 2));
		folder_list.addElement(name.substring(p, p + 2) + "0");

		return folder_list;
	}

	/**
	 * Gets the mean error of position in arcsec.
	 * @return the mean error of position in arcsec.
	 */
	public double getPositionErrorInArcsec ( ) {
		return 15.0;
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 60.0;
	}
}
