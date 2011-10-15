/*
 * @(#)UsnoA10Star.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.star;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;

/**
 * The <code>UsnoA10Star</code> represents a star data in the 
 * USNO-A1.0 CD-ROMs.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 7
 */

public class UsnoA10Star extends UsnoAStar {
	/**
	 * Constructs an empty <code>UsnoA10Star</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public UsnoA10Star ( ) {
		super();

		detailed_output = false;
	}

	/**
	 * Constructs a <code>UsnoA10Star</code> with data read from 
	 * CD-ROMs.
	 * @param file_numer       the file number.
	 * @param star_number      the star number in the area.
	 * @param coor             the R.A. and Decl.
	 * @param valid_R_mag      true if R magnitude is recorded.
	 * @param R_mag10          the R magnitude in 0.1 mag unit.
	 * @param valid_B_mag      true if B magnitude is recorded.
	 * @param B_mag10          the B magnitude in 0.1 mag unit.
	 * @param V_mag            the V magnitude.
	 */
	public UsnoA10Star ( short file_numer,
						 int star_number,
						 Coor coor,
						 boolean valid_R_mag,
						 short R_mag10,
						 boolean valid_B_mag,
						 short B_mag10, 
						 double V_mag )
	{
		super();
		setCoor(coor);
		setMag(V_mag);

		this.file_number = file_numer;
		this.star_number = star_number;
		if (valid_R_mag)
			this.R_mag10 = R_mag10;
		if (valid_B_mag)
			this.B_mag10 = B_mag10;
	}

	/**
	 * Gets the version of the USNO-A catalog.
	 * @return the string of version.
	 */
	public String getVersion ( ) {
		return "USNO-A1.0";
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "USNO-A1.0";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "USNO-A1.0";
	}
}
