/*
 * @(#)VeronStar.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.star;
import java.io.*;
import java.util.Vector;
import java.util.StringTokenizer;
import java.awt.Color;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.PlotProperty;

/**
 * The <code>VeronStar</code> represents a star data in the Quasars 
 * and Active Galactic Nuclei.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 9
 */

public class VeronStar extends DefaultStar {
	/**
	 * Constructs an empty <code>VeronStar</code>. It is used in 
	 * <code>StarClass#newInstance</code> to review the XML data.
	 */
	public VeronStar ( ) {
		super();
	}

	/**
	 * Constructs a <code>VeronStar</code> with data read from the
	 * catalog file.
	 * @param name         the star name.
	 * @param coor_string  the R.A. and Decl.
	 * @param mag          the magnitude.
	 * @param mag_system   the magnitude system.
	 * @param b_v          the difference between the V and B magnitude.
	 * @param u_b          the difference between the U and B magnitude.
	 * @param absolute_mag the absolute magnitude.
	 * @param z            the red shift.
	 */
	public VeronStar ( String name,
					   String coor_string,
					   String mag,
					   String mag_system,
					   String b_v,
					   String u_b,
					   String absolute_mag,
					   String z )
	{
		super(name, coor_string);

		if (mag != null  &&  mag.length() > 0)
			setKeyAndValue(new KeyAndValue("Mag", mag));
		if (mag_system != null  &&  mag_system.length() > 0)
			setKeyAndValue(new KeyAndValue("MagSystem", mag_system));
		if (b_v != null  &&  b_v.length() > 0)
			setKeyAndValue(new KeyAndValue("B-V", b_v));
		if (u_b != null  &&  u_b.length() > 0)
			setKeyAndValue(new KeyAndValue("U-B", u_b));
		if (absolute_mag != null  &&  absolute_mag.length() > 0)
			setKeyAndValue(new KeyAndValue("AbsoluteMag", absolute_mag));
		if (z != null  &&  z.length() > 0)
			setKeyAndValue(new KeyAndValue("z", z));
	}

	/**
	 * Gets the name of this star in a format for the VSNET (Variable
	 * Star Network). In principle, this method must be overrided in
	 * subclasses.
	 * @return the name of this star.
	 */
	public String getVsnetName ( ) {
		String vsnet_name = "";

		StringTokenizer st = new StringTokenizer(name);
		while (st.hasMoreElements())
			vsnet_name += st.nextToken();

		return vsnet_name;
	}

	/**
	 * Gets the name of the catalog. It must be unique among all
	 * subclasses.
	 * @return the name of the catalog.
	 */
	public String getCatalogName ( ) {
		return "Quasars and Active Galactic Nuclei";
	}

	/**
	 * Gets the acronym of the catalog.
	 * @return the acronym of the catalog.
	 */
	public String getCatalogAcronym ( ) {
		return "Veron";
	}

	/**
	 * Gets the code of the catalog. It must be unique among all 
	 * subclasses.
	 * @return the code of the catalog.
	 */
	public String getCatalogCode ( ) {
		return "Veron";
	}

	/**
	 * Gets the catalog category. It must be overrided in the 
	 * subclasses.
	 * @return the catalog category.
	 */
	protected int getCatalogCategoryNumber ( ) {
		return CatalogManager.CATEGORY_OTHERS;
	}

	/**
	 * Gets the list of the hierarchical folders.
	 * @return the list of the hierarchical folders.
	 */
	public Vector getHierarchicalFolders ( ) {
		Vector folder_list = new Vector();

		folder_list.addElement(CatalogManager.getCatalogCategoryFolder(CatalogManager.CATEGORY_OTHERS));
		folder_list.addElement(getCatalogFolderCode());

		folder_list.addElement(name.substring(0, 1));

		return folder_list;
	}
}
