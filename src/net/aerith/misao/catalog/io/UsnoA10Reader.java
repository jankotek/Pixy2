/*
 * @(#)UsnoA10Reader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.io;
import java.io.*;
import java.net.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.star.*;

/**
 * The <code>UsnoA10Reader</code> is a class to read USNO-A1.0
 * CD-ROMs.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 June 3
 */

public class UsnoA10Reader extends UsnoAReader {
	/**
	 * The number of files.
	 */
	private final static int USNOA10_cat_N = 24;

	/**
	 * The list of file numbers.
	 */
	private final static int USNOA10_file[] = {
		   0,   75,  450,  375, 1500, 1650,  300, 1425,
		1725,  525, 1275,  225,  675,  150,  600, 1575,
		 750,  975,  900, 1050, 1125, 1200,  825, 1350
	};

	/**
	 * The list of disc numbers.
	 */
	private static final int USNOA10_disc[] = {
		 1,  1,  1,  2,  2,  2,  3,  3,
		 3,  4,  4,  5,  5,  6,  6,  6,
		 7,  7,  8,  8,  9,  9, 10, 10
	};

	/**
	 * Constructs an empty <code>UsnoA10Reader</code>.
	 */
	public UsnoA10Reader ( ) {
		url_set.ignoreCases(true);

		setDefaultURL();
	}

	/**
	 * Constructs a <code>UsnoA10Reader</code> with URL of directory 
	 * containing USNO-A1.0 CD-ROMs data.
	 * @param url the URL of directory containing CD-ROMs data.
	 */
	public UsnoA10Reader ( URL url ) {
		this();

		addURL(url);
	}

	/**
	 * Gets the number of files.
	 * @return the number of files.
	 */
	protected int getNumberOfFiles ( ) {
		return USNOA10_cat_N;
	}

	/**
	 * Gets the file number of the specified index.
	 * @param index the index.
	 * @return the file number.
	 */
	protected int getFileNumber ( int index ) {
		return USNOA10_file[index];
	}

	/**
	 * Gets the disc number of the specified index.
	 * @param index the index.
	 * @return the disc number.
	 */
	protected int getDiscNumber ( int index ) {
		return USNOA10_disc[index];
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "USNO-A1.0";
	}

	/**
	 * Checks if the catalog data is in a directory.
	 * @return true if the catalog data is in a directory.
	 */
	public boolean isInDirectory ( ) {
		return true;
	}

	/**
	 * Checks if the catalog supports the use in PIXY examination.
	 * @return true if the catalog can be used in PIXY examination.
	 */
	public boolean supportsExamination ( ) {
		return true;
	}

	/**
	 * Creates s <code>UsnoAStar</code> object based on the specified 
	 * parameters.
	 * @param file_numer  the file number.
	 * @param star_number the star number in the area.
	 * @param coor        the R.A. and Decl.
	 * @param valid_R_mag true if R magnitude is recorded.
	 * @param R_mag10     the R magnitude in 0.1 mag unit.
	 * @param valid_B_mag true if B magnitude is recorded.
	 * @param B_mag10     the B magnitude in 0.1 mag unit.
	 * @param V_mag       the V magnitude.
	 * @return the <code>UsnoAStar</code> object
	 */
	protected UsnoAStar createStar ( short file_number,
									 int star_number,
									 Coor coor,
									 boolean valid_R_mag,
									 short R_mag10,
									 boolean valid_B_mag,
									 short B_mag10,
									 double V_mag )
	{
		return new UsnoA10Star(file_number, star_number, coor, valid_R_mag, R_mag10, valid_B_mag, B_mag10, V_mag);
	}

	/**
	 * Gets the help message.
	 * @return the help message.
	 */
	public String getHelpMessage ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "PMM USNO-A1.0 Catalogue<br>";
		html += "Astronomical Data Center catalog No. 1243<br>";
		html += "</p><p>";
		html += "The set of 10 CD-ROMs. <br>";
		html += "</p><p>";
		html += "References:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">http://ftp.nofs.navy.mil/projects/pmm/</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
