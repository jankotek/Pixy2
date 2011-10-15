/*
 * @(#)XmlPhotometry.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.CatalogManager;

/**
 * The <code>XmlPhotometry</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 April 13
 */

public class XmlPhotometry extends net.aerith.misao.xml.relaxer.XmlPhotometry {
	/**
	 * Constructs an empty <code>XmlPhotometry</code>.
	 */
	public XmlPhotometry ( ) {
		super();
	}

	/**
	 * Constructs an <code>XmlPhotometry</code> of the specified
	 * photometry setting.
	 * @param setting the photometry setting.
	 */
	public XmlPhotometry ( PhotometrySetting setting ) {
		super();

		XmlCatalog catalog = new XmlCatalog();
		catalog.setContent(setting.getDescription());
		setCatalog(catalog);

		if (setting.getMethod() == PhotometrySetting.METHOD_STANDARD) {
			setType("standard " + setting.getMagnitudeSystem().getSystemCode());
		} else if (setting.getMethod() == PhotometrySetting.METHOD_COMPARISON) {
			if (setting.gradientFixed())
				setType("average fitting");
			else
				setType("line fitting");
		} else {
			setType("instrumental");

			double gradient_bv = setting.getGradientBV();
			String s = "Instrumental mag = V + " + gradient_bv + " * (B-V)";
			setSystemFormula(s);
		}
	}

	/**
	 * Gets the photometry setting.
	 * @return the photometry setting.
	 */
	public PhotometrySetting getPhotometrySetting ( ) {
		try {
			String catalog_name = getCatalog().getContent();
			String class_name = CatalogManager.getCatalogStarClassName(catalog_name);

			PhotometrySetting setting = new PhotometrySetting(catalog_name);
			setting.setDescription(catalog_name);

			String system = "";
			if (class_name != null  &&  class_name.length() > 0) {
				Class t = Class.forName(class_name);
				CatalogStar star = (CatalogStar)t.newInstance();
				system = star.getMagnitudeSystem(catalog_name);
			}

			if (getType().indexOf("standard") == 0) {
				setting.setMethod(PhotometrySetting.METHOD_STANDARD);
				setting.setSystemCode(getType().substring(9));
			} else if (getType().indexOf("instrumental") == 0) {
				setting.setMethod(PhotometrySetting.METHOD_INSTRUMENTAL_PHOTOMETRY);
				String bv_str = getSystemFormula().substring(23);
				bv_str = bv_str.substring(0, bv_str.indexOf(' '));
				setting.setGradientBV(Format.doubleValueOf(bv_str));
			} else {
				setting.setMethod(PhotometrySetting.METHOD_COMPARISON);
				setting.setSystemCode(system);
				if (getType().indexOf("average fitting") == 0)
					setting.setFixGradient(true);
				else
					setting.setFixGradient(false);
			}

			return setting;
		} catch ( ClassNotFoundException exception ) {
			System.err.println(exception);
		} catch ( IllegalAccessException exception ) {
			System.err.println(exception);
		} catch ( InstantiationException exception ) {
			System.err.println(exception);
		}

		return null;
	}
}
