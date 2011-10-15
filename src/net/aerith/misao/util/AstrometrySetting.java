/*
 * @(#)AstrometrySetting.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.Hashtable;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.CatalogManager;

/**
 * The <code>AstrometrySetting</code> represents a set of reference
 * catalog name and equinox, epoch, etc.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 April 13
 */

public class AstrometrySetting {
	/**
	 * The equinox which represents J2000.0.
	 */
	public final static int EQUINOX_J2000 = 1;

	/**
	 * The equinox which represents B1950.0.
	 */
	public final static int EQUINOX_B1950 = 2;

	/**
	 * The equinox which represents any time.
	 */
	public final static int EQUINOX_ANY = 3;

	/**
	 * The equinox.
	 */
	protected int equinox = EQUINOX_J2000;

	/**
	 * The julian day of the equinox.
	 */
	protected String equinox_jd = "";

	/**
	 * The julian day of the epoch.
	 */
	protected String epoch_jd = null;

	/**
	 * The catalog name.
	 */
	protected String catalog_name;

	/**
	 * The catalog description.
	 */
	protected String description = null;

	/**
	 * Constructs an <code>AstrometrySetting</code> with a catalog 
	 * name.
	 * @param catalog_name the catalog name.
	 */
	public AstrometrySetting ( String catalog_name ) {
		this.catalog_name = catalog_name;
	}

	/**
	 * Gets the catalog name.
	 * @return the catalog name.
	 */
	public String getCatalogName ( ) {
		return catalog_name;
	}

	/**
	 * Gets the equinox.
	 * @return the number of equinox.
	 */
	public int getEquinox ( ) {
		return equinox;
	}

	/**
	 * Sets the equinox.
	 * @param equinox the number of equinox.
	 */
	public void setEquinox ( int equinox ) {
		this.equinox = equinox;
	}

	/**
	 * Gets the julian day of the equinox.
	 * @return the julian day of the equinox.
	 */
	public String getEquinoxJD ( ) {
		return equinox_jd;
	}

	/**
	 * Sets the julian day of the equinox.
	 * @param jd the julian day of the equinox.
	 */
	public void setEquinoxJD ( String jd ) {
		equinox_jd = jd;
	}

	/**
	 * Gets the julian day of the epoch.
	 * @return the julian day of the epoch.
	 */
	public String getEpochJD ( ) {
		return epoch_jd;
	}

	/**
	 * Returns the catalog description. It returns the catalog name
	 * by default.
	 * @return the catalog description.
	 */
	public String getDescription ( ) {
		if (description != null)
			return description;

		return getCatalogName();
	}

	/**
	 * Sets the catalog description.
	 * @param the catalog description.
	 */
	public void setDescription ( String description ) {
		this.description = description;
	}
}
