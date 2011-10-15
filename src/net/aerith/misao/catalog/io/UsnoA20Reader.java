/*
 * @(#)UsnoA20Reader.java
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
 * The <code>UsnoA20Reader</code> is a class to read USNO-A2.0
 * CD-ROMs.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 June 3
 */

public class UsnoA20Reader extends UsnoAReader {
	/**
	 * The number of files.
	 */
	private final static int USNOA20_cat_N = 24;

	/**
	 * The list of file numbers.
	 */
	private final static int USNOA20_file[] = {
		   0,   75,  600,  525, 1500, 1725,  450, 1575, 
		1650,  375, 1425,  300,  675, 1350,  225,  750, 
		 975, 1050,  150,  900,  825, 1200, 1125, 1275
	};

	/**
	 * The list of disc numbers.
	 */
	private static final int USNOA20_disc[] = {
		 1,  1,  1,  2,  2,  2,  3,  3, 
		 3,  4,  4,  5,  6,  6,  7,  7, 
		 8,  8,  9,  9, 10, 10, 11, 11
	};

	/**
	 * Constructs an empty <code>UsnoA20Reader</code>.
	 */
	public UsnoA20Reader ( ) {
		url_set.ignoreCases(true);

		setDefaultURL();
	}

	/**
	 * Constructs a <code>UsnoA20Reader</code> with URL of directory 
	 * containing USNO-A2.0 CD-ROMs data.
	 * @param url the URL of directory containing CD-ROMs data.
	 */
	public UsnoA20Reader ( URL url ) {
		this();

		addURL(url);
	}

	/**
	 * Gets the number of files.
	 * @return the number of files.
	 */
	protected int getNumberOfFiles ( ) {
		return USNOA20_cat_N;
	}

	/**
	 * Gets the file number of the specified index.
	 * @param index the index.
	 * @return the file number.
	 */
	protected int getFileNumber ( int index ) {
		return USNOA20_file[index];
	}

	/**
	 * Gets the disc number of the specified index.
	 * @param index the index.
	 * @return the disc number.
	 */
	protected int getDiscNumber ( int index ) {
		return USNOA20_disc[index];
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "USNO-A2.0";
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
		return new UsnoA20Star(file_number, star_number, coor, valid_R_mag, R_mag10, valid_B_mag, B_mag10, V_mag);
	}

	/**
	 * Gets the help message.
	 * @return the help message.
	 */
	public String getHelpMessage ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "USNO-A2.0 Catalogue<br>";
		html += "Astronomical Data Center catalog No. 1252<br>";
		html += "</p><p>";
		html += "The set of 11 CD-ROMs. <br>";
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
