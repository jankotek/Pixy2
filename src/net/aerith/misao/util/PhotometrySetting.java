/*
 * @(#)PhotometrySetting.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.Hashtable;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.CatalogManager;

/**
 * The <code>PhotometrySetting</code> represents a set of magnitude
 * system formula and reference catalog name.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 April 13
 */

public class PhotometrySetting {
	/**
	 * The method of photometry which represents standard system.
	 */
	public final static int METHOD_STANDARD = 1;

	/**
	 * The method of photometry which represents regular photometry
	 * using the fixed formula.
	 */
	public final static int METHOD_INSTRUMENTAL_PHOTOMETRY = 2;

	/**
	 * The method of photometry which represents regular photometry
	 * calculating the magnitude system formula at the same time.
	 */
	public final static int METHOD_FREE_PHOTOMETRY = 3;

	/**
	 * The method of photometry which represents simple magnitude 
	 * comparison.
	 */
	public final static int METHOD_COMPARISON = 4;

	/**
	 * The method of photometry.
	 */
	protected int method = METHOD_COMPARISON;

	/**
	 * True when to compare magnitude by average fitting.
	 */
	protected boolean fix_gradient = true;

	/**
	 * The catalog name. In the case of simple magnitude comparison, 
	 * the catalog name must contain the magnitude system code.
	 */
	protected String catalog_name;

	/**
	 * The magnitude system.
	 */
	protected MagnitudeSystem system;

	/**
	 * The catalog description.
	 */
	protected String description = null;

	/**
	 * Constructs a <code>PhotometrySetting</code> with a catalog name
	 * @param catalog_name the catalog name. In the case of simple 
	 * magnitude comparison, the catalog name must contain the 
	 * magnitude system code.
	 */
	public PhotometrySetting ( String catalog_name ) {
		this.catalog_name = catalog_name;

		system = new MagnitudeSystem();
	}

	/**
	 * Gets the catalog name.
	 * @return the catalog name.
	 */
	public String getCatalogName ( ) {
		return catalog_name;
	}

	/**
	 * Gets the method.
	 * @return the number of method.
	 */
	public int getMethod ( ) {
		return method;
	}

	/**
	 * Sets the method.
	 * @param method the number of method.
	 */
	public void setMethod ( int method ) {
		this.method = method;

		if (method == METHOD_STANDARD) {
			system.setMethod(MagnitudeSystem.METHOD_STANDARD);
		} else if (method == METHOD_INSTRUMENTAL_PHOTOMETRY) {
			system.setMethod(MagnitudeSystem.METHOD_INSTRUMENTAL);
		} else if (method == METHOD_FREE_PHOTOMETRY) {
			system.setMethod(MagnitudeSystem.METHOD_INSTRUMENTAL);
		} else if (method == METHOD_COMPARISON) {
			system.setMethod(MagnitudeSystem.METHOD_CATALOG);
		}
	}

	/**
	 * Gets the magnitude system.
	 * @return the magnitude system.
	 */
	public MagnitudeSystem getMagnitudeSystem ( ) {
		return system;
	}

	/**
	 * Sets the magnitude system code.
	 * @param code the magnitude system code.
	 */
	public void setSystemCode ( String code ) {
		system.setSystemCode(code);
	}

	/**
	 * Gets the gradient of (B-V).
	 * @return the gradient of (B-V).
	 */
	public double getGradientBV ( ) {
		return system.getGradientBV();
	}

	/**
	 * Sets the gradient of (B-V).
	 * @param gradient the gradient of (B-V).
	 */
	public void setGradientBV ( double gradient ) {
		system.setGradientBV(gradient);
	}

	/**
	 * Sets whether to fix the gradient of the magnitude translation
	 * formula.
	 * @param flag true when to fix the gradient of the magnitude 
	 * translation formula.
	 */
	public void setFixGradient ( boolean flag ) {
		fix_gradient = flag;
	}

	/**
	 * Returns true when not to fix the gradient of the magnitude 
	 * translation formula.
	 * @return true when not to fix the gradient of the magnitude 
	 * translation formula.
	 */
	public boolean gradientFixed ( ) {
		return fix_gradient;
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
