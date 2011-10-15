/*
 * @(#)CsakVStar.java
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
 * The <code>CsakVStar</code> represents a star data in the Csak's New
 * Variable Stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class CsakVStar extends DefaultStar {
	/**
	 * Constructs an empty <code>CsakVStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public CsakVStar ( ) {
		super();
	}

	/**
	 * Constructs an <code>CsakVStar</code> with data read from the
	 * catalog file.
	 * @param name        the star name.
	 * @param coor_string the R.A. and Decl.
	 * @param mag         the magnitude.
	 * @param max         the brightest magnitude.
	 * @param min         the faintest magnitude.
	 * @param mag_system  the magnitude system.
	 * @param type        the type.
	 * @param spectrum    the spectrum.
	 * @param ibvs        the IBVS number.
	 */
	public CsakVStar ( String name,
					   String coor_string,
					   String mag,
					   String max,
					   String min,
					   String mag_system,
					   String type, 
					   String spectrum,
					   String ibvs )
	{
		super(name, coor_string);

		setKeyAndValue(new KeyAndValue("Mag", mag));
		setKeyAndValue(new KeyAndValue("Mag(max)", max));
		setKeyAndValue(new KeyAndValue("Mag(min)", min));
		setKeyAndValue(new KeyAndValue("MagSystem", mag_system));
		setKeyAndValue(new KeyAndValue("Type", type));
		setKeyAndValue(new KeyAndValue("Spectrum", spectrum));
		if (ibvs.indexOf("IBVS") == 0) {
			setKeyAndValue(new KeyAndValue("IBVS", ibvs.substring(4)));
		} else {
			setKeyAndValue(new KeyAndValue("IBVS", ibvs));
		}
	}

	/**
	 * Gets the prefix of the name.
	 * @return the prefix of the name.
	 */
	public String getNamePrefix ( ) {
		return "Csak V";
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Csak's New Variable Stars";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "Csak V";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "Csak";
	}

	/**
	 * Gets the catalog category. It must be overrided in the 
	 * subclasses.
	 * @return the catalog category.
	 */
	protected int getCatalogCategoryNumber ( ) {
		return CatalogManager.CATEGORY_VARIABLE;
	}
}
