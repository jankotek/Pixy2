/*
 * @(#)CatalogStar.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util.star;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.PlotProperty;

/**
 * The <code>CatalogStar</code> represents a star data in catalog.
 * This is the base class of subclasses in 
 * <code>net.aerith.misao.catalog.star</code> package.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 April 29
 */

public abstract class CatalogStar extends Star {
	/**
	 * True if this object contains all data required for detailed 
	 * format. If false, this can be output only in reduced format.
	 */
	 protected boolean detailed_output = true;

	/**
	 * Sets the name of this star.
	 * @param name the name to set.
	 */
	public void setName ( String name ) {
	}

	/**
	 * Gets the name of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return getCatalogName();
	}

	/**
	 * Gets the folder string of the catalog. It must be unique among
	 * all subclasses.
	 * @return the folder string of the catalog.
	 */
	public String getCatalogFolderCode ( ) {
		return getCatalogCode();
	}

	/**
	 * Gets the category of the catalog.
	 * @return the category of the catalog.
	 */
	public String getCatalogCategory ( ) {
		return "";
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		return null;
	}

	/**
	 * Gets the folder string of the star. It must be unique among all
	 * subclasses.
	 * @return the folder string of the star.
	 */
	public String getStarFolder ( ) {
		return "";
	}

	/**
	 * Sets the flag to output only in reduced format.
	 */
	public void reduceOutput ( ) {
		detailed_output = false;
	}

	/**
	 * Gets the mean error of position in arcsec.
	 * @return the mean error of position in arcsec.
	 */
	public double getPositionErrorInArcsec ( ) {
		return 1.0;
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 5.0;
	}

	/**
	 * Gets the accuracy of R.A. and Decl.
	 * @return the accuracy of R.A. and Decl.
	 */
	public byte getCoorAccuracy ( ) {
		return Coor.ACCURACY_100M_ARCSEC;
	}

	/**
	 * Sets the accuracy of R.A. and Decl. It must be overrided in the
	 * subclasses if the accuracy can be different from the default.
	 * @param accuracy the accuracy of R.A. and Decl.
	 */
	public void setCoorAccuracy ( byte accuracy ) {
	}

	/**
	 * Returns the date if the R.A. and Decl. is date dependent. For 
	 * example, in the case of astrometric observations in the MPC 
	 * format. In general, it returns null.
	 * @return the date.
	 */
	public JulianDay getDate ( ) {
		return null;
	}

	/**
	 * Returns true if the catalog contains magnitude data.
	 * @return true if the catalog contains magnitude data.
	 */
	public boolean supportsMagnitude ( ) {
		return false;
	}

	/**
	 * Returns true if the catalog contains magnitude data enough for
	 * photometry.
	 * @return true if the catalog contains magnitude data enough for
	 * photometry.
	 */
	public boolean supportsPhotometry ( ) {
		return false;
	}

	/**
	 * Returns true if the catalog contains accurate R.A. and Decl.
	 * enough for astrometry.
	 * @return true if the catalog contains accurate R.A. and Decl.
	 * enough for astrometry.
	 */
	public boolean supportsAstrometry ( ) {
		return false;
	}

	/**
	 * Returns true if the catalog description of astrometry and 
	 * photometry is edittable. If not, the description is the catalog
	 * name.
	 * @return true if the catalog description of astrometry and 
	 * photometry is edittable.
	 */
	public boolean isDescriptionEdittable ( ) {
		return false;
	}

	/**
	 * Gets the V magnitude.
	 * @return the V magnitude.
	 * @exception UnsupportedMagnitudeSystemException if no V mag data
	 * is recorded in this catalog.
	 */
	public double getVMagnitude ( )
		throws UnsupportedMagnitudeSystemException
	{
		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Gets the U magnitude.
	 * @return the U magnitude.
	 * @exception UnsupportedMagnitudeSystemException if no U mag data
	 * is recorded in this catalog.
	 */
	public double getUMagnitude ( )
		throws UnsupportedMagnitudeSystemException
	{
		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Gets the B magnitude.
	 * @return the B magnitude.
	 * @exception UnsupportedMagnitudeSystemException if no B mag data
	 * is recorded in this catalog.
	 */
	public double getBMagnitude ( )
		throws UnsupportedMagnitudeSystemException
	{
		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Gets the Rc magnitude.
	 * @return the Rc magnitude.
	 * @exception UnsupportedMagnitudeSystemException if no Rc mag data
	 * is recorded in this catalog.
	 */
	public double getRcMagnitude ( )
		throws UnsupportedMagnitudeSystemException
	{
		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Gets the Ic magnitude.
	 * @return the Ic magnitude.
	 * @exception UnsupportedMagnitudeSystemException if no Rc mag data
	 * is recorded in this catalog.
	 */
	public double getIcMagnitude ( )
		throws UnsupportedMagnitudeSystemException
	{
		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Gets the difference between the V and B magnitude.
	 * @return the difference between the V and B magnitude.
	 * @exception UnsupportedMagnitudeSystemException if no V mag or 
	 * B mag data is recorded in this catalog.
	 */
	public double getBVDifference ( )
		throws UnsupportedMagnitudeSystemException
	{
		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Gets the list of magnitude systems supported by this catalog.
	 * @return the list of magnitude systems supported by this catalog.
	 */
	public String[] getAvailableMagnitudeSystems ( ) {
		return null;
	}

	/**
	 * Gets the magnitude of the specified system.
	 * @param system the magnitude system.
	 * @return the magnitude of the specified system.
	 * @exception UnsupportedMagnitudeSystemException if the specified
	 * magnitude system is not supported.
	 */
	public double getMagnitude ( String system )
		throws UnsupportedMagnitudeSystemException
	{
		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Gets the magnitude of the specified magnitude system.
	 * @param system the magnitude system.
	 * @return the magnitude of the specified system.
	 * @exception UnsupportedMagnitudeSystemException if the magnitude
	 * system for the specified photometry setting is not supported.
	 */
	public double getMagnitude ( MagnitudeSystem system )
		throws UnsupportedMagnitudeSystemException
	{
		if (system == null)
			return getMag();

		if (system.getMethod() == MagnitudeSystem.METHOD_DEFAULT) {
			return getMag();
		} else if (system.getMethod() == MagnitudeSystem.METHOD_STANDARD) {
			return getMagnitude(system.getSystemCode());
		} else if (system.getMethod() == MagnitudeSystem.METHOD_CATALOG) {
			return getMagnitude(system.getSystemCode());
		} else if (system.getMethod() == MagnitudeSystem.METHOD_INSTRUMENTAL) {
			double v_mag = getVMagnitude();
			double b_v = getBVDifference();
			return v_mag + system.getGradientBV() * b_v;
		}

		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Gets the magnitude string of the specified system to output.
	 * @param system the magnitude system.
	 * @return the magnitude string of the specified system.
	 * @exception UnsupportedMagnitudeSystemException if the specified
	 * magnitude system is not supported.
	 */
	public String getMagnitudeString ( String system )
		throws UnsupportedMagnitudeSystemException
	{
		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Gets the catalog name with the specified magnitude system.
	 * @param system the magnitude system.
	 * @return the catalog name with the specified magnitude system.
	 * @exception UnsupportedMagnitudeSystemException if the specified
	 * magnitude system is not supported.
	 */
	public String getCatalogNameWithMagnitudeSystem ( String system )
		throws UnsupportedMagnitudeSystemException
	{
		String[] catalog_names = getCatalogNamesWithMagnitudeSystem();

		if (catalog_names != null) {
			for (int i = 0 ; i < catalog_names.length ; i++) {
				if (getMagnitudeSystem(catalog_names[i]).equals(system))
					return catalog_names[i];
			}
		}

		throw new UnsupportedMagnitudeSystemException();
	}

	/**
	 * Gets the list of catalog names with the supporting magnitude
	 * system.
	 * @return the list of catalog names with the supporting magnitude
	 * system.
	 */
	public String[] getCatalogNamesWithMagnitudeSystem ( ) {
		String[] systems = getAvailableMagnitudeSystems();
		if (systems == null)
			return null;

		String[] names = new String[systems.length];
		for (int i = 0 ; i < systems.length ; i++) {
			names[i] = getCatalogName();
			if (systems[i].length() > 0)
				names[i] += " (" + systems[i] + ")";
		}

		return names;
	}

	/**
	 * Gets the magnitude system.
	 * @param catalog_name the catalog name with magnitude system.
	 * @return the magnitude system.
	 */
	public String getMagnitudeSystem ( String catalog_name ) {
		if (catalog_name.indexOf(getCatalogName()) == 0) {
			String s = catalog_name.substring(getCatalogName().length()).trim();
			if (s.length() == 0)
				return "";

			int p = s.indexOf('(');
			int q = s.indexOf(')');
			if (p != 0  ||  q != s.length() - 1)
				return "";

			return s.substring(1, q);
		}

		return "";
	}

	/**
	 * Gets the html help message for regular photometry.
	 * @return the html help message for regular photometry.
	 */
	public String getPhotometryHelpMessage ( ) {
		return null;
	}

	/**
	 * Gets the html help message of the specified name with magnitude
	 * system for simple magnitude comparison.
	 * @param name the catalog name with magnitude system.
	 * @return the html help message for simple magnitude comparison.
	 */
	public String getHelpMessage ( String name ) {
		return null;
	}

	/**
	 * Gets the default property to plot stars.
	 * @return the default property to plot stars.
	 */
	public PlotProperty getDefaultProperty ( ) {
		return new PlotProperty();
	}

	/**
	 * Gets a string representing the R.A. and Decl. in a proper 
	 * format and accuracy. 
	 * @return the string representing R.A. and Decl.
	 */
	public String getCoorString ( ) {
		if (getCoorAccuracy() == Coor.ACCURACY_1M_ARCSEC)
			return coor.getOutputStringTo1mArcsecWithUnit();
		if (getCoorAccuracy() == Coor.ACCURACY_10M_ARCSEC)
			return coor.getOutputStringTo10mArcsecWithUnit();
		if (getCoorAccuracy() == Coor.ACCURACY_ARCSEC)
			return coor.getOutputStringToArcsecWithUnit();
		if (getCoorAccuracy() == Coor.ACCURACY_ROUGH_ARCSEC)
			return coor.getOutputStringToRoughArcsecWithUnit();
		if (getCoorAccuracy() == Coor.ACCURACY_100M_ARCMIN)
			return coor.getOutputStringTo100mArcminWithUnit();
		if (getCoorAccuracy() == Coor.ACCURACY_100M_ARCMIN_HOURSEC)
			return coor.getOutputStringTo100mArcminHoursecWithUnit();
		if (getCoorAccuracy() == Coor.ACCURACY_ARCMIN)
			return coor.getOutputStringToArcminWithUnit();
		return coor.getOutputStringTo100mArcsecWithUnit();
	}

	/**
	 * Gets a string representing the R.A. and Decl. in a proper 
	 * format and accuracy without unit.
	 * @return the string representing R.A. and Decl.
	 */
	public String getCoorStringWithoutUnit ( ) {
		if (getCoorAccuracy() == Coor.ACCURACY_10M_ARCSEC)
			return coor.getOutputStringTo10mArcsecWithoutUnit();
		if (getCoorAccuracy() == Coor.ACCURACY_ARCSEC)
			return coor.getOutputStringToArcsecWithoutUnit();
		if (getCoorAccuracy() == Coor.ACCURACY_100M_ARCMIN)
			return coor.getOutputStringTo100mArcminWithoutUnit();
		if (getCoorAccuracy() == Coor.ACCURACY_ARCMIN)
			return coor.getOutputStringToArcminWithoutUnit();
		return coor.getOutputStringTo100mArcsecWithoutUnit();
	}

	/**
	 * Gets an array of keys and values related to the photometry. In
	 * principle, this method must be overrided in subclasses.
	 * @return an array of keys and values related to the photometry.
	 */
	public KeyAndValue[] getKeyAndValuesForPhotometry ( ) {
		return new KeyAndValue[0];
	}
}
