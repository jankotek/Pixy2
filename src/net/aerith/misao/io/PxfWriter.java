/*
 * @(#)PxfWriter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.xml.*;

/**
 * The <code>PxfWriter</code> represents a writer to save the 
 * examination result into a PXF file.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 July 21
 */

public class PxfWriter {
	/**
	 * The XML document of the examination result.
	 */
	XmlReport report;

	/**
	 * Constructs a <code>PxfWriter</code> of the specified XML
	 * document of the examination result.
	 * @param report the XML document of the examination result.
	 */
	public PxfWriter ( XmlReport report ) {
		this.report = report;
	}

	/**
	 * Writes the XML document to the specified writer.
	 * @param out the writer.
	 * @exception IOException if I/O error occurs.
	 */
	public void write ( PrintWriter out )
		throws IOException
	{
		out.println("[Information]");

		XmlInformation info = (XmlInformation)report.getInformation();
		new ItemSet("image", info.getImage().getContent()).print(out);

		XmlSize size = (XmlSize)info.getSize();
		new ItemSet("size", size.getWidth() + " x " + size.getHeight()).print(out);

		if (info.getDate() != null) {
			new ItemSet("date", info.getDate()).print(out);

			XmlExposure exposure = (XmlExposure)info.getExposure();
			new ItemSet("exposure", exposure.getContent() + " " + exposure.getUnit()).print(out);
		}

		XmlCenter center = (XmlCenter)info.getCenter();
		new ItemSet("center", center.getRa() + " " + center.getDecl()).print(out);

		XmlFov fov = (XmlFov)info.getFov();
		String fov_w = "";
		if (fov.getWidth() < 10)
			fov_w = Format.formatDouble((double)fov.getWidth(), 5, 1);
		else
			fov_w = Format.formatDouble((double)fov.getWidth(), 5, 2);
		String fov_h = "";
		if (fov.getHeight() < 10)
			fov_h = Format.formatDouble((double)fov.getHeight(), 5, 1);
		else
			fov_h = Format.formatDouble((double)fov.getHeight(), 5, 2);
		new ItemSet("fov", fov_w + " x " + fov_h + " " + fov.getUnit()).print(out);

		XmlRotation rot = (XmlRotation)info.getRotation();
		new ItemSet("rotation", Format.formatDouble((double)rot.getContent(), 6, 3).trim() + " " + rot.getUnit()).print(out);

		XmlPixelSize ps = (XmlPixelSize)info.getPixelSize();
		new ItemSet("pixel-size", Format.formatDouble((double)ps.getWidth(), 6, 3).trim() + " x " + Format.formatDouble((double)ps.getHeight(), 6, 3).trim() + " " + ps.getUnit()).print(out);

		new ItemSet("limiting-mag", "" + info.getLimitingMag() + " mag").print(out);
		new ItemSet("upper-limit-mag", "" + info.getProperUpperLimitMag() + " mag").print(out);

		XmlAstrometricError astro_err = (XmlAstrometricError)info.getAstrometricError();
		new ItemSet("astrometric-error", "" + astro_err.getRa() + " x " + astro_err.getDecl() + " " + astro_err.getUnit()).print(out);

		new ItemSet("photometric-error", "" + info.getPhotometricError() + " mag").print(out);

		if (info.getFilter() != null)
			new ItemSet("filter", info.getFilter()).print(out);

		new ItemSet("base-catalog", info.getBaseCatalog()).print(out);

		if (info.getAstrometry() != null) {
			new ItemSet("astrometric-catalog", info.getAstrometry().getCatalog().getContent()).print(out);
			new ItemSet("astrometric-equinox", info.getAstrometry().getEquinox()).print(out);
		}

		if (info.getPhotometry() != null) {
			new ItemSet("photometric-catalog", info.getPhotometry().getCatalog().getContent()).print(out);
			new ItemSet("photometry-type", info.getPhotometry().getType()).print(out);
			if (info.getPhotometry().getSystemFormula() != null)
				new ItemSet("magnitude-system-formula", info.getPhotometry().getSystemFormula()).print(out);
		}

		if (info.isReversedImage())
			new ItemSet("reversed-image", "yes").print(out);

		if (info.isSbigImage())
			new ItemSet("sbig-image", "yes").print(out);

		out.println("");
		out.println("[Statistics]");

		XmlStarCount sc = (XmlStarCount)info.getStarCount();
		if (sc != null) {
			new ItemSet("STR", "" + sc.getStr()).print(out);
			new ItemSet("VAR", "" + sc.getVar()).print(out);
			new ItemSet("MOV", "" + sc.getMov()).print(out);
			new ItemSet("NEW", "" + sc.getNew()).print(out);
			new ItemSet("ERR", "" + sc.getErr()).print(out);
			new ItemSet("NEG", "" + sc.getNeg()).print(out);
		}

		out.println("");
		out.println("[Data]");

		XmlStar[] stars = (XmlStar[])report.getData().getStar();

		StringArray array = new StringArray(stars.length);
		for (int i = 0 ; i < stars.length ; i++)
			array.set(i, stars[i].getName());
		ArrayIndex index = array.sortAscendant();

		for (int i = 0 ; i < stars.length ; i++) {
			XmlStar star = stars[index.get(i)];
			out.println(star.getName());

			StarImage star_image = star.getStarImage();
			if (star_image != null)
				out.println("D| " + star_image.getPositionString() + "  " + star_image.getPxfString());

			String star_class = CatalogManager.getCatalogStarClassName(info.getBaseCatalog());
			Star[] catalog_stars = star.getRecords(star_class);
			for (int k = 0 ; k < catalog_stars.length ; k++)
				out.println("C| " + catalog_stars[k].getPxfStringWithXY());

			Vector list = star.getAllRecords();
			for (int j = 0 ; j < list.size() ; j++) {
				if (list.elementAt(j) != star_image  &&  list.elementAt(j).getClass().getName().equals(star_class) == false) {
					String[] records = ((Star)list.elementAt(j)).getPxfStringsWithXY();
					for (int k = 0 ; k < records.length ; k++)
						out.println("R| " + records[k]);
				}
			}

			out.println("");
		}
	}

	/**
	 * The <code>ItemSet</code> is a set of the item key and the
	 * item value.
	 */
	protected class ItemSet {
		/**
		 * The key.
		 */
		private String key;

		/**
		 * The value.
		 */
		private String value;

		/**
		 * Constructs an <code>ItemSet</code>.
		 * @param key   the key.
		 * @param value the value.
		 */
		public ItemSet ( String key, String value ) {
			this.key = key;
			this.value = value;
		}

		/**
		 * Writes the key and value to the specified writer.
		 * @param out the writer.
		 */
		public void print ( PrintWriter out ) {
			out.println(key + "=" + value);
		}
	}
}
