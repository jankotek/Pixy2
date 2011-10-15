/*
 * @(#)XmlAstrometry.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.CatalogManager;

/**
 * The <code>XmlAstrometry</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 April 13
 */

public class XmlAstrometry extends net.aerith.misao.xml.relaxer.XmlAstrometry {
	/**
	 * Constructs an empty <code>XmlAstrometry</code>.
	 */
	public XmlAstrometry ( ) {
		super();
	}

	/**
	 * Constructs an <code>XmlAstrometry</code> of the specified
	 * astrometry setting.
	 * @param setting the astrometry setting.
	 */
	public XmlAstrometry ( AstrometrySetting setting ) {
		super();

		XmlCatalog catalog = new XmlCatalog();
		catalog.setContent(setting.getDescription());
		setCatalog(catalog);

		if (setting.getEquinox() == AstrometrySetting.EQUINOX_J2000)
			setEquinox("J2000.0");
		else if (setting.getEquinox() == AstrometrySetting.EQUINOX_B1950)
			setEquinox("B1950.0");
		else
			setEquinox(setting.getEquinoxJD());
	}

	/**
	 * Gets the astrometry setting.
	 * @return the astrometry setting.
	 */
	public AstrometrySetting getAstrometrySetting ( ) {
		String catalog_name = getCatalog().getContent();
		AstrometrySetting setting = new AstrometrySetting(catalog_name);
		setting.setDescription(catalog_name);

		if (getEquinox().equals("J2000.0"))
			setting.setEquinox(AstrometrySetting.EQUINOX_J2000);
		else if (getEquinox().equals("B1950.0"))
			setting.setEquinox(AstrometrySetting.EQUINOX_B1950);
		else {
			setting.setEquinox(AstrometrySetting.EQUINOX_ANY);
			setting.setEquinoxJD(getEquinox());
		}

		return setting;
	}
}
