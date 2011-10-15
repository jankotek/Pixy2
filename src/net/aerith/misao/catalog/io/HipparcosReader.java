/*
 * @(#)HipparcosReader.java
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
 * The <code>HipparcosReader</code> is a class to read the Hipparcos 
 * Catalogue.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class HipparcosReader extends FileReader {
	/**
	 * The smallest R.A. in degree of the block.
	 */
	public static int[] ra_start;

	/**
	 * The largest R.A. in degree of the block.
	 */
	public static int[] ra_end;

	/**
	 * The smallest Decl. in degree of the block.
	 */
	public static int[] decl_start;

	/**
	 * The largest Decl. in degree of the block.
	 */
	public static int[] decl_end;

	/**
	 * Constructs an empty <code>HipparcosReader</code>.
	 */
	public HipparcosReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>HipparcosReader</code> with URL of the catalog
	 * file.
	 * @param url the URL of the catalog file.
	 */
	public HipparcosReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Hipparcos Catalogue";
	}

	/**
	 * Checks if the catalog supports the use in PIXY examination.
	 * @return true if the catalog can be used in PIXY examination.
	 */
	public boolean supportsExamination ( ) {
		return true;
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
	 * Creates a <code>CatalogStar</code> object from the specified
	 * one line record in the file. If some more records are required
	 * to create a star object, it returns null. This method must be
	 * overrided in the subclasses.
	 * @param record the one line record in the file.
	 * @return the star object.
	 */
	public CatalogStar createStar ( String record ) {
		Coor coor = Coor.create(record.substring(17, 28) + " " + record.substring(29, 40));

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			int number = Integer.parseInt(record.substring(8, 14).trim());

			HipparcosStar star = new HipparcosStar(number, coor);

			if (record.substring(41, 46).trim().length() > 0)
				star.setVMagnitude(Format.doubleValueOf(record.substring(41, 46).trim()));
			if (record.substring(217, 223).trim().length() > 0)
				star.setBtMagnitude(Format.doubleValueOf(record.substring(217, 223).trim()));
			if (record.substring(230, 236).trim().length() > 0)
				star.setVtMagnitude(Format.doubleValueOf(record.substring(230, 236).trim()));
			if (record.substring(274, 281).trim().length() > 0)
				star.setHpMagnitude(Format.doubleValueOf(record.substring(274, 281).trim()));

			if (star.getMag() <= limiting_mag) {
				if (record.substring(245, 251).trim().length() > 0)
					star.setBVDifference(Format.doubleValueOf(record.substring(245, 251).trim()));
				if (record.substring(260, 264).trim().length() > 0)
					star.setVIDifference(Format.doubleValueOf(record.substring(260, 264).trim()));

				if (record.substring(301, 306).trim().length() > 0)
					star.setMagMax(Format.doubleValueOf(record.substring(301, 306).trim()));
				if (record.substring(307, 312).trim().length() > 0)
					star.setMagMin(Format.doubleValueOf(record.substring(307, 312).trim()));
				if (record.substring(313, 320).trim().length() > 0)
					star.setPeriod(Format.doubleValueOf(record.substring(313, 320).trim()));

				if (record.substring(87, 95).trim().length() > 0)
					star.setProperMotionInRA(Format.doubleValueOf(record.substring(87, 95).trim()));
				if (record.substring(96, 104).trim().length() > 0)
					star.setProperMotionInDecl(Format.doubleValueOf(record.substring(96, 104).trim()));

				if (record.substring(435, 447).trim().length() > 0)
					star.setSpectrum(record.substring(435, 447).trim());

				if (record.substring(79, 86).trim().length() > 0)
					star.setParallax(Format.doubleValueOf(record.substring(79, 86).trim()));

				if (record.substring(105, 111).trim().length() > 0)
					star.setRAError(Format.doubleValueOf(record.substring(105, 111).trim()));
				if (record.substring(112, 118).trim().length() > 0)
					star.setDeclError(Format.doubleValueOf(record.substring(112, 118).trim()));

				if (record.substring(224, 229).trim().length() > 0)
					star.setBtMagnitudeError(Format.doubleValueOf(record.substring(224, 229).trim()));
				if (record.substring(237, 242).trim().length() > 0)
					star.setVtMagnitudeError(Format.doubleValueOf(record.substring(237, 242).trim()));
				if (record.substring(282, 288).trim().length() > 0)
					star.setHpMagnitudeError(Format.doubleValueOf(record.substring(282, 288).trim()));

				if (record.substring(252, 257).trim().length() > 0)
					star.setBVDifferenceError(Format.doubleValueOf(record.substring(252, 257).trim()));
				if (record.substring(265, 269).trim().length() > 0)
					star.setVIDifferenceError(Format.doubleValueOf(record.substring(265, 269).trim()));

				if (record.substring(126, 132).trim().length() > 0)
					star.setProperMotionInRAError(Format.doubleValueOf(record.substring(126, 132).trim()));
				if (record.substring(133, 139).trim().length() > 0)
					star.setProperMotionInDeclError(Format.doubleValueOf(record.substring(133, 139).trim()));

				if (record.substring(119, 125).trim().length() > 0)
					star.setParallaxError(Format.doubleValueOf(record.substring(119, 125).trim()));

				if (center_coor != null) {
					ChartMapFunction cmf = new ChartMapFunction(center_coor, 1.0, 0.0);
					Position position = cmf.mapCoordinatesToXY(coor);
					star.setPosition(position);
				}

				return star;
			}
		}

		return null;
	}

	/**
	 * Gets the help message.
	 * @return the help message.
	 */
	public String getHelpMessage ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "The Hipparcos Catalogue<br>";
		html += "Astronomical Data Center catalog No. 1239<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/1/1239/hip_main.dat.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Gets the number of blocks in a file. If 0, it means the file is
	 * not separated into blocks. It must be overrided in the 
	 * subclasses if neccessary.
	 * @return the size of a block.
	 */
	public long getBlockCount ( ) {
		return 237;
	}

	/**
	 * Gets the number of records in a block. If 0, it means the file
	 * is not separated into blocks. It must be overrided in the 
	 * subclasses if neccessary.
	 * @return the size of a block.
	 */
	public long getBlockSize ( ) {
		if (current_block >= getBlockCount())
			return 0;

		if (current_block == getBlockCount() -1)
			return 218;

		return 500;
	}

	/**
	 * Gets the characters of a record. If 0, it means the record size
	 * is not constant, so the reader skips the specified lines while
	 * reading one line by one. Otherwise, the reader skips the 
	 * specified characters at one time. It must be overrided in the 
	 * subclasses if neccessary.
	 * @return the size of a record.
	 */
	public long getRecordSize ( ) {
		return 451;
	}

	/**
	 * Checks if the current block is overlapping on the specified 
	 * circum area.
	 * @return true if the current block is overlapping on the 
	 * specified circum area.
	 */
	public boolean overlapsBlock ( ) {
		if (current_block >= getBlockCount())
			return true;

		if (circum_area == null)
			return true;

		Coor start_coor = new Coor((double)ra_start[(int)current_block], (double)decl_start[(int)current_block]);
		Coor end_coor = new Coor((double)ra_end[(int)current_block], (double)decl_end[(int)current_block]);
		if (circum_area.overlapsArea(start_coor, end_coor))
			return true;

		return false;
	}

	/**
	 * Sets the R.A. range of each block.
	 */
	static {
		ra_start = new int[237];
		ra_end = new int[237];
		decl_start = new int[237];
		decl_end = new int[237];

		ra_start[0] = 0;
		ra_end[0] = 2;
		decl_start[0] = -82;
		decl_end[0] = 83;
		ra_start[1] = 1;
		ra_end[1] = 4;
		decl_start[1] = -88;
		decl_end[1] = 88;
		ra_start[2] = 3;
		ra_end[2] = 5;
		decl_start[2] = -89;
		decl_end[2] = 89;
		ra_start[3] = 4;
		ra_end[3] = 7;
		decl_start[3] = -88;
		decl_end[3] = 79;
		ra_start[4] = 6;
		ra_end[4] = 8;
		decl_start[4] = -84;
		decl_end[4] = 82;
		ra_start[5] = 7;
		ra_end[5] = 10;
		decl_start[5] = -87;
		decl_end[5] = 86;
		ra_start[6] = 9;
		ra_end[6] = 12;
		decl_start[6] = -86;
		decl_end[6] = 90;
		ra_start[7] = 11;
		ra_end[7] = 13;
		decl_start[7] = -90;
		decl_end[7] = 82;
		ra_start[8] = 12;
		ra_end[8] = 15;
		decl_start[8] = -83;
		decl_end[8] = 86;
		ra_start[9] = 14;
		ra_end[9] = 17;
		decl_start[9] = -84;
		decl_end[9] = 85;
		ra_start[10] = 16;
		ra_end[10] = 18;
		decl_start[10] = -84;
		decl_end[10] = 88;
		ra_start[11] = 17;
		ra_end[11] = 20;
		decl_start[11] = -88;
		decl_end[11] = 88;
		ra_start[12] = 19;
		ra_end[12] = 21;
		decl_start[12] = -85;
		decl_end[12] = 86;
		ra_start[13] = 20;
		ra_end[13] = 23;
		decl_start[13] = -83;
		decl_end[13] = 87;
		ra_start[14] = 22;
		ra_end[14] = 25;
		decl_start[14] = -86;
		decl_end[14] = 90;
		ra_start[15] = 24;
		ra_end[15] = 26;
		decl_start[15] = -86;
		decl_end[15] = 85;
		ra_start[16] = 25;
		ra_end[16] = 28;
		decl_start[16] = -89;
		decl_end[16] = 88;
		ra_start[17] = 27;
		ra_end[17] = 29;
		decl_start[17] = -88;
		decl_end[17] = 89;
		ra_start[18] = 28;
		ra_end[18] = 31;
		decl_start[18] = -87;
		decl_end[18] = 86;
		ra_start[19] = 30;
		ra_end[19] = 33;
		decl_start[19] = -83;
		decl_end[19] = 87;
		ra_start[20] = 32;
		ra_end[20] = 34;
		decl_start[20] = -85;
		decl_end[20] = 84;
		ra_start[21] = 33;
		ra_end[21] = 36;
		decl_start[21] = -89;
		decl_end[21] = 86;
		ra_start[22] = 35;
		ra_end[22] = 38;
		decl_start[22] = -83;
		decl_end[22] = 85;
		ra_start[23] = 37;
		ra_end[23] = 39;
		decl_start[23] = -82;
		decl_end[23] = 90;
		ra_start[24] = 38;
		ra_end[24] = 41;
		decl_start[24] = -86;
		decl_end[24] = 86;
		ra_start[25] = 40;
		ra_end[25] = 42;
		decl_start[25] = -88;
		decl_end[25] = 86;
		ra_start[26] = 41;
		ra_end[26] = 44;
		decl_start[26] = -87;
		decl_end[26] = 84;
		ra_start[27] = 43;
		ra_end[27] = 46;
		decl_start[27] = -88;
		decl_end[27] = 83;
		ra_start[28] = 45;
		ra_end[28] = 47;
		decl_start[28] = -86;
		decl_end[28] = 88;
		ra_start[29] = 46;
		ra_end[29] = 49;
		decl_start[29] = -85;
		decl_end[29] = 84;
		ra_start[30] = 48;
		ra_end[30] = 50;
		decl_start[30] = -88;
		decl_end[30] = 83;
		ra_start[31] = 49;
		ra_end[31] = 52;
		decl_start[31] = -87;
		decl_end[31] = 83;
		ra_start[32] = 51;
		ra_end[32] = 54;
		decl_start[32] = -85;
		decl_end[32] = 88;
		ra_start[33] = 53;
		ra_end[33] = 55;
		decl_start[33] = -85;
		decl_end[33] = 87;
		ra_start[34] = 54;
		ra_end[34] = 57;
		decl_start[34] = -86;
		decl_end[34] = 90;
		ra_start[35] = 56;
		ra_end[35] = 58;
		decl_start[35] = -84;
		decl_end[35] = 88;
		ra_start[36] = 57;
		ra_end[36] = 60;
		decl_start[36] = -84;
		decl_end[36] = 86;
		ra_start[37] = 59;
		ra_end[37] = 62;
		decl_start[37] = -87;
		decl_end[37] = 83;
		ra_start[38] = 61;
		ra_end[38] = 63;
		decl_start[38] = -87;
		decl_end[38] = 88;
		ra_start[39] = 62;
		ra_end[39] = 65;
		decl_start[39] = -85;
		decl_end[39] = 83;
		ra_start[40] = 64;
		ra_end[40] = 66;
		decl_start[40] = -88;
		decl_end[40] = 84;
		ra_start[41] = 65;
		ra_end[41] = 68;
		decl_start[41] = -85;
		decl_end[41] = 85;
		ra_start[42] = 67;
		ra_end[42] = 70;
		decl_start[42] = -85;
		decl_end[42] = 86;
		ra_start[43] = 69;
		ra_end[43] = 72;
		decl_start[43] = -85;
		decl_end[43] = 83;
		ra_start[44] = 71;
		ra_end[44] = 73;
		decl_start[44] = -89;
		decl_end[44] = 85;
		ra_start[45] = 72;
		ra_end[45] = 75;
		decl_start[45] = -90;
		decl_end[45] = 84;
		ra_start[46] = 74;
		ra_end[46] = 76;
		decl_start[46] = -86;
		decl_end[46] = 85;
		ra_start[47] = 75;
		ra_end[47] = 78;
		decl_start[47] = -89;
		decl_end[47] = 86;
		ra_start[48] = 77;
		ra_end[48] = 79;
		decl_start[48] = -84;
		decl_end[48] = 89;
		ra_start[49] = 78;
		ra_end[49] = 81;
		decl_start[49] = -82;
		decl_end[49] = 86;
		ra_start[50] = 80;
		ra_end[50] = 82;
		decl_start[50] = -87;
		decl_end[50] = 87;
		ra_start[51] = 81;
		ra_end[51] = 84;
		decl_start[51] = -85;
		decl_end[51] = 86;
		ra_start[52] = 83;
		ra_end[52] = 85;
		decl_start[52] = -84;
		decl_end[52] = 84;
		ra_start[53] = 84;
		ra_end[53] = 86;
		decl_start[53] = -86;
		decl_end[53] = 86;
		ra_start[54] = 85;
		ra_end[54] = 88;
		decl_start[54] = -87;
		decl_end[54] = 81;
		ra_start[55] = 87;
		ra_end[55] = 89;
		decl_start[55] = -87;
		decl_end[55] = 88;
		ra_start[56] = 88;
		ra_end[56] = 91;
		decl_start[56] = -83;
		decl_end[56] = 87;
		ra_start[57] = 90;
		ra_end[57] = 92;
		decl_start[57] = -87;
		decl_end[57] = 86;
		ra_start[58] = 91;
		ra_end[58] = 94;
		decl_start[58] = -89;
		decl_end[58] = 89;
		ra_start[59] = 93;
		ra_end[59] = 95;
		decl_start[59] = -86;
		decl_end[59] = 85;
		ra_start[60] = 94;
		ra_end[60] = 97;
		decl_start[60] = -80;
		decl_end[60] = 85;
		ra_start[61] = 96;
		ra_end[61] = 98;
		decl_start[61] = -89;
		decl_end[61] = 86;
		ra_start[62] = 97;
		ra_end[62] = 99;
		decl_start[62] = -78;
		decl_end[62] = 84;
		ra_start[63] = 99;
		ra_end[63] = 101;
		decl_start[63] = -83;
		decl_end[63] = 88;
		ra_start[64] = 100;
		ra_end[64] = 102;
		decl_start[64] = -88;
		decl_end[64] = 84;
		ra_start[65] = 101;
		ra_end[65] = 104;
		decl_start[65] = -89;
		decl_end[65] = 87;
		ra_start[66] = 103;
		ra_end[66] = 105;
		decl_start[66] = -81;
		decl_end[66] = 87;
		ra_start[67] = 104;
		ra_end[67] = 106;
		decl_start[67] = -88;
		decl_end[67] = 85;
		ra_start[68] = 105;
		ra_end[68] = 108;
		decl_start[68] = -86;
		decl_end[68] = 84;
		ra_start[69] = 107;
		ra_end[69] = 109;
		decl_start[69] = -87;
		decl_end[69] = 86;
		ra_start[70] = 108;
		ra_end[70] = 110;
		decl_start[70] = -82;
		decl_end[70] = 83;
		ra_start[71] = 109;
		ra_end[71] = 112;
		decl_start[71] = -89;
		decl_end[71] = 86;
		ra_start[72] = 111;
		ra_end[72] = 113;
		decl_start[72] = -81;
		decl_end[72] = 89;
		ra_start[73] = 112;
		ra_end[73] = 115;
		decl_start[73] = -87;
		decl_end[73] = 83;
		ra_start[74] = 114;
		ra_end[74] = 116;
		decl_start[74] = -88;
		decl_end[74] = 89;
		ra_start[75] = 115;
		ra_end[75] = 117;
		decl_start[75] = -82;
		decl_end[75] = 82;
		ra_start[76] = 116;
		ra_end[76] = 119;
		decl_start[76] = -89;
		decl_end[76] = 87;
		ra_start[77] = 118;
		ra_end[77] = 120;
		decl_start[77] = -84;
		decl_end[77] = 86;
		ra_start[78] = 119;
		ra_end[78] = 122;
		decl_start[78] = -89;
		decl_end[78] = 80;
		ra_start[79] = 121;
		ra_end[79] = 123;
		decl_start[79] = -85;
		decl_end[79] = 82;
		ra_start[80] = 122;
		ra_end[80] = 125;
		decl_start[80] = -90;
		decl_end[80] = 88;
		ra_start[81] = 124;
		ra_end[81] = 126;
		decl_start[81] = -88;
		decl_end[81] = 85;
		ra_start[82] = 125;
		ra_end[82] = 128;
		decl_start[82] = -85;
		decl_end[82] = 87;
		ra_start[83] = 127;
		ra_end[83] = 129;
		decl_start[83] = -85;
		decl_end[83] = 80;
		ra_start[84] = 128;
		ra_end[84] = 131;
		decl_start[84] = -83;
		decl_end[84] = 85;
		ra_start[85] = 130;
		ra_end[85] = 132;
		decl_start[85] = -90;
		decl_end[85] = 86;
		ra_start[86] = 131;
		ra_end[86] = 133;
		decl_start[86] = -82;
		decl_end[86] = 88;
		ra_start[87] = 133;
		ra_end[87] = 135;
		decl_start[87] = -87;
		decl_end[87] = 86;
		ra_start[88] = 134;
		ra_end[88] = 137;
		decl_start[88] = -83;
		decl_end[88] = 87;
		ra_start[89] = 136;
		ra_end[89] = 138;
		decl_start[89] = -83;
		decl_end[89] = 89;
		ra_start[90] = 137;
		ra_end[90] = 140;
		decl_start[90] = -85;
		decl_end[90] = 86;
		ra_start[91] = 139;
		ra_end[91] = 141;
		decl_start[91] = -88;
		decl_end[91] = 89;
		ra_start[92] = 140;
		ra_end[92] = 143;
		decl_start[92] = -88;
		decl_end[92] = 83;
		ra_start[93] = 142;
		ra_end[93] = 144;
		decl_start[93] = -87;
		decl_end[93] = 84;
		ra_start[94] = 143;
		ra_end[94] = 146;
		decl_start[94] = -82;
		decl_end[94] = 89;
		ra_start[95] = 145;
		ra_end[95] = 147;
		decl_start[95] = -83;
		decl_end[95] = 90;
		ra_start[96] = 146;
		ra_end[96] = 149;
		decl_start[96] = -83;
		decl_end[96] = 87;
		ra_start[97] = 148;
		ra_end[97] = 151;
		decl_start[97] = -90;
		decl_end[97] = 87;
		ra_start[98] = 150;
		ra_end[98] = 152;
		decl_start[98] = -89;
		decl_end[98] = 81;
		ra_start[99] = 151;
		ra_end[99] = 154;
		decl_start[99] = -86;
		decl_end[99] = 87;
		ra_start[100] = 153;
		ra_end[100] = 155;
		decl_start[100] = -88;
		decl_end[100] = 86;
		ra_start[101] = 154;
		ra_end[101] = 157;
		decl_start[101] = -87;
		decl_end[101] = 86;
		ra_start[102] = 156;
		ra_end[102] = 158;
		decl_start[102] = -87;
		decl_end[102] = 89;
		ra_start[103] = 157;
		ra_end[103] = 160;
		decl_start[103] = -87;
		decl_end[103] = 86;
		ra_start[104] = 159;
		ra_end[104] = 162;
		decl_start[104] = -87;
		decl_end[104] = 82;
		ra_start[105] = 161;
		ra_end[105] = 163;
		decl_start[105] = -81;
		decl_end[105] = 88;
		ra_start[106] = 162;
		ra_end[106] = 165;
		decl_start[106] = -85;
		decl_end[106] = 86;
		ra_start[107] = 164;
		ra_end[107] = 166;
		decl_start[107] = -85;
		decl_end[107] = 84;
		ra_start[108] = 165;
		ra_end[108] = 168;
		decl_start[108] = -90;
		decl_end[108] = 86;
		ra_start[109] = 167;
		ra_end[109] = 170;
		decl_start[109] = -82;
		decl_end[109] = 86;
		ra_start[110] = 169;
		ra_end[110] = 171;
		decl_start[110] = -84;
		decl_end[110] = 88;
		ra_start[111] = 170;
		ra_end[111] = 173;
		decl_start[111] = -85;
		decl_end[111] = 80;
		ra_start[112] = 172;
		ra_end[112] = 174;
		decl_start[112] = -85;
		decl_end[112] = 89;
		ra_start[113] = 173;
		ra_end[113] = 176;
		decl_start[113] = -88;
		decl_end[113] = 86;
		ra_start[114] = 175;
		ra_end[114] = 177;
		decl_start[114] = -89;
		decl_end[114] = 86;
		ra_start[115] = 176;
		ra_end[115] = 179;
		decl_start[115] = -82;
		decl_end[115] = 87;
		ra_start[116] = 178;
		ra_end[116] = 181;
		decl_start[116] = -88;
		decl_end[116] = 87;
		ra_start[117] = 180;
		ra_end[117] = 182;
		decl_start[117] = -86;
		decl_end[117] = 86;
		ra_start[118] = 181;
		ra_end[118] = 184;
		decl_start[118] = -87;
		decl_end[118] = 87;
		ra_start[119] = 183;
		ra_end[119] = 185;
		decl_start[119] = -89;
		decl_end[119] = 88;
		ra_start[120] = 184;
		ra_end[120] = 187;
		decl_start[120] = -86;
		decl_end[120] = 84;
		ra_start[121] = 186;
		ra_end[121] = 188;
		decl_start[121] = -87;
		decl_end[121] = 82;
		ra_start[122] = 187;
		ra_end[122] = 190;
		decl_start[122] = -85;
		decl_end[122] = 86;
		ra_start[123] = 189;
		ra_end[123] = 191;
		decl_start[123] = -78;
		decl_end[123] = 84;
		ra_start[124] = 190;
		ra_end[124] = 193;
		decl_start[124] = -84;
		decl_end[124] = 82;
		ra_start[125] = 192;
		ra_end[125] = 194;
		decl_start[125] = -89;
		decl_end[125] = 88;
		ra_start[126] = 193;
		ra_end[126] = 196;
		decl_start[126] = -82;
		decl_end[126] = 89;
		ra_start[127] = 195;
		ra_end[127] = 197;
		decl_start[127] = -86;
		decl_end[127] = 89;
		ra_start[128] = 196;
		ra_end[128] = 199;
		decl_start[128] = -88;
		decl_end[128] = 87;
		ra_start[129] = 198;
		ra_end[129] = 201;
		decl_start[129] = -88;
		decl_end[129] = 85;
		ra_start[130] = 200;
		ra_end[130] = 202;
		decl_start[130] = -82;
		decl_end[130] = 84;
		ra_start[131] = 201;
		ra_end[131] = 204;
		decl_start[131] = -87;
		decl_end[131] = 87;
		ra_start[132] = 203;
		ra_end[132] = 205;
		decl_start[132] = -86;
		decl_end[132] = 80;
		ra_start[133] = 204;
		ra_end[133] = 207;
		decl_start[133] = -89;
		decl_end[133] = 83;
		ra_start[134] = 206;
		ra_end[134] = 208;
		decl_start[134] = -85;
		decl_end[134] = 86;
		ra_start[135] = 207;
		ra_end[135] = 210;
		decl_start[135] = -86;
		decl_end[135] = 88;
		ra_start[136] = 209;
		ra_end[136] = 211;
		decl_start[136] = -85;
		decl_end[136] = 82;
		ra_start[137] = 210;
		ra_end[137] = 213;
		decl_start[137] = -88;
		decl_end[137] = 82;
		ra_start[138] = 212;
		ra_end[138] = 214;
		decl_start[138] = -85;
		decl_end[138] = 84;
		ra_start[139] = 213;
		ra_end[139] = 216;
		decl_start[139] = -84;
		decl_end[139] = 80;
		ra_start[140] = 215;
		ra_end[140] = 217;
		decl_start[140] = -84;
		decl_end[140] = 88;
		ra_start[141] = 216;
		ra_end[141] = 218;
		decl_start[141] = -84;
		decl_end[141] = 89;
		ra_start[142] = 217;
		ra_end[142] = 220;
		decl_start[142] = -90;
		decl_end[142] = 86;
		ra_start[143] = 219;
		ra_end[143] = 222;
		decl_start[143] = -85;
		decl_end[143] = 88;
		ra_start[144] = 221;
		ra_end[144] = 223;
		decl_start[144] = -83;
		decl_end[144] = 77;
		ra_start[145] = 222;
		ra_end[145] = 224;
		decl_start[145] = -87;
		decl_end[145] = 86;
		ra_start[146] = 223;
		ra_end[146] = 226;
		decl_start[146] = -84;
		decl_end[146] = 88;
		ra_start[147] = 225;
		ra_end[147] = 228;
		decl_start[147] = -88;
		decl_end[147] = 87;
		ra_start[148] = 227;
		ra_end[148] = 229;
		decl_start[148] = -85;
		decl_end[148] = 84;
		ra_start[149] = 228;
		ra_end[149] = 231;
		decl_start[149] = -86;
		decl_end[149] = 84;
		ra_start[150] = 230;
		ra_end[150] = 232;
		decl_start[150] = -88;
		decl_end[150] = 85;
		ra_start[151] = 231;
		ra_end[151] = 234;
		decl_start[151] = -89;
		decl_end[151] = 88;
		ra_start[152] = 233;
		ra_end[152] = 235;
		decl_start[152] = -85;
		decl_end[152] = 81;
		ra_start[153] = 234;
		ra_end[153] = 237;
		decl_start[153] = -89;
		decl_end[153] = 86;
		ra_start[154] = 236;
		ra_end[154] = 238;
		decl_start[154] = -88;
		decl_end[154] = 85;
		ra_start[155] = 237;
		ra_end[155] = 240;
		decl_start[155] = -87;
		decl_end[155] = 81;
		ra_start[156] = 239;
		ra_end[156] = 241;
		decl_start[156] = -82;
		decl_end[156] = 86;
		ra_start[157] = 240;
		ra_end[157] = 243;
		decl_start[157] = -90;
		decl_end[157] = 82;
		ra_start[158] = 242;
		ra_end[158] = 244;
		decl_start[158] = -85;
		decl_end[158] = 89;
		ra_start[159] = 243;
		ra_end[159] = 246;
		decl_start[159] = -85;
		decl_end[159] = 84;
		ra_start[160] = 245;
		ra_end[160] = 247;
		decl_start[160] = -85;
		decl_end[160] = 89;
		ra_start[161] = 246;
		ra_end[161] = 249;
		decl_start[161] = -86;
		decl_end[161] = 86;
		ra_start[162] = 248;
		ra_end[162] = 250;
		decl_start[162] = -85;
		decl_end[162] = 80;
		ra_start[163] = 249;
		ra_end[163] = 252;
		decl_start[163] = -81;
		decl_end[163] = 87;
		ra_start[164] = 251;
		ra_end[164] = 254;
		decl_start[164] = -88;
		decl_end[164] = 89;
		ra_start[165] = 253;
		ra_end[165] = 255;
		decl_start[165] = -86;
		decl_end[165] = 88;
		ra_start[166] = 254;
		ra_end[166] = 257;
		decl_start[166] = -88;
		decl_end[166] = 83;
		ra_start[167] = 256;
		ra_end[167] = 258;
		decl_start[167] = -88;
		decl_end[167] = 85;
		ra_start[168] = 257;
		ra_end[168] = 260;
		decl_start[168] = -88;
		decl_end[168] = 90;
		ra_start[169] = 259;
		ra_end[169] = 261;
		decl_start[169] = -85;
		decl_end[169] = 88;
		ra_start[170] = 260;
		ra_end[170] = 263;
		decl_start[170] = -83;
		decl_end[170] = 84;
		ra_start[171] = 262;
		ra_end[171] = 264;
		decl_start[171] = -85;
		decl_end[171] = 87;
		ra_start[172] = 263;
		ra_end[172] = 266;
		decl_start[172] = -87;
		decl_end[172] = 80;
		ra_start[173] = 265;
		ra_end[173] = 267;
		decl_start[173] = -87;
		decl_end[173] = 86;
		ra_start[174] = 266;
		ra_end[174] = 269;
		decl_start[174] = -85;
		decl_end[174] = 84;
		ra_start[175] = 268;
		ra_end[175] = 270;
		decl_start[175] = -88;
		decl_end[175] = 87;
		ra_start[176] = 269;
		ra_end[176] = 272;
		decl_start[176] = -86;
		decl_end[176] = 86;
		ra_start[177] = 271;
		ra_end[177] = 273;
		decl_start[177] = -86;
		decl_end[177] = 89;
		ra_start[178] = 272;
		ra_end[178] = 275;
		decl_start[178] = -84;
		decl_end[178] = 87;
		ra_start[179] = 274;
		ra_end[179] = 276;
		decl_start[179] = -83;
		decl_end[179] = 87;
		ra_start[180] = 275;
		ra_end[180] = 278;
		decl_start[180] = -85;
		decl_end[180] = 88;
		ra_start[181] = 277;
		ra_end[181] = 279;
		decl_start[181] = -90;
		decl_end[181] = 78;
		ra_start[182] = 278;
		ra_end[182] = 281;
		decl_start[182] = -86;
		decl_end[182] = 88;
		ra_start[183] = 280;
		ra_end[183] = 282;
		decl_start[183] = -83;
		decl_end[183] = 89;
		ra_start[184] = 281;
		ra_end[184] = 283;
		decl_start[184] = -89;
		decl_end[184] = 84;
		ra_start[185] = 282;
		ra_end[185] = 285;
		decl_start[185] = -88;
		decl_end[185] = 83;
		ra_start[186] = 284;
		ra_end[186] = 286;
		decl_start[186] = -85;
		decl_end[186] = 83;
		ra_start[187] = 285;
		ra_end[187] = 288;
		decl_start[187] = -87;
		decl_end[187] = 87;
		ra_start[188] = 287;
		ra_end[188] = 289;
		decl_start[188] = -88;
		decl_end[188] = 87;
		ra_start[189] = 288;
		ra_end[189] = 291;
		decl_start[189] = -86;
		decl_end[189] = 84;
		ra_start[190] = 290;
		ra_end[190] = 292;
		decl_start[190] = -84;
		decl_end[190] = 86;
		ra_start[191] = 291;
		ra_end[191] = 294;
		decl_start[191] = -85;
		decl_end[191] = 87;
		ra_start[192] = 293;
		ra_end[192] = 295;
		decl_start[192] = -85;
		decl_end[192] = 85;
		ra_start[193] = 294;
		ra_end[193] = 296;
		decl_start[193] = -86;
		decl_end[193] = 88;
		ra_start[194] = 295;
		ra_end[194] = 298;
		decl_start[194] = -83;
		decl_end[194] = 83;
		ra_start[195] = 297;
		ra_end[195] = 300;
		decl_start[195] = -88;
		decl_end[195] = 86;
		ra_start[196] = 299;
		ra_end[196] = 301;
		decl_start[196] = -85;
		decl_end[196] = 85;
		ra_start[197] = 300;
		ra_end[197] = 302;
		decl_start[197] = -85;
		decl_end[197] = 87;
		ra_start[198] = 301;
		ra_end[198] = 304;
		decl_start[198] = -83;
		decl_end[198] = 86;
		ra_start[199] = 303;
		ra_end[199] = 305;
		decl_start[199] = -88;
		decl_end[199] = 81;
		ra_start[200] = 304;
		ra_end[200] = 306;
		decl_start[200] = -82;
		decl_end[200] = 84;
		ra_start[201] = 305;
		ra_end[201] = 308;
		decl_start[201] = -88;
		decl_end[201] = 84;
		ra_start[202] = 307;
		ra_end[202] = 309;
		decl_start[202] = -87;
		decl_end[202] = 89;
		ra_start[203] = 308;
		ra_end[203] = 311;
		decl_start[203] = -83;
		decl_end[203] = 90;
		ra_start[204] = 310;
		ra_end[204] = 312;
		decl_start[204] = -86;
		decl_end[204] = 87;
		ra_start[205] = 311;
		ra_end[205] = 314;
		decl_start[205] = -83;
		decl_end[205] = 88;
		ra_start[206] = 313;
		ra_end[206] = 315;
		decl_start[206] = -87;
		decl_end[206] = 88;
		ra_start[207] = 314;
		ra_end[207] = 317;
		decl_start[207] = -84;
		decl_end[207] = 83;
		ra_start[208] = 316;
		ra_end[208] = 318;
		decl_start[208] = -89;
		decl_end[208] = 86;
		ra_start[209] = 317;
		ra_end[209] = 320;
		decl_start[209] = -89;
		decl_end[209] = 85;
		ra_start[210] = 319;
		ra_end[210] = 321;
		decl_start[210] = -88;
		decl_end[210] = 83;
		ra_start[211] = 320;
		ra_end[211] = 323;
		decl_start[211] = -86;
		decl_end[211] = 88;
		ra_start[212] = 322;
		ra_end[212] = 324;
		decl_start[212] = -88;
		decl_end[212] = 90;
		ra_start[213] = 323;
		ra_end[213] = 326;
		decl_start[213] = -82;
		decl_end[213] = 88;
		ra_start[214] = 325;
		ra_end[214] = 327;
		decl_start[214] = -85;
		decl_end[214] = 85;
		ra_start[215] = 326;
		ra_end[215] = 329;
		decl_start[215] = -86;
		decl_end[215] = 85;
		ra_start[216] = 328;
		ra_end[216] = 330;
		decl_start[216] = -86;
		decl_end[216] = 84;
		ra_start[217] = 329;
		ra_end[217] = 332;
		decl_start[217] = -86;
		decl_end[217] = 89;
		ra_start[218] = 331;
		ra_end[218] = 334;
		decl_start[218] = -84;
		decl_end[218] = 86;
		ra_start[219] = 333;
		ra_end[219] = 335;
		decl_start[219] = -87;
		decl_end[219] = 87;
		ra_start[220] = 334;
		ra_end[220] = 337;
		decl_start[220] = -88;
		decl_end[220] = 86;
		ra_start[221] = 336;
		ra_end[221] = 338;
		decl_start[221] = -87;
		decl_end[221] = 83;
		ra_start[222] = 337;
		ra_end[222] = 340;
		decl_start[222] = -86;
		decl_end[222] = 88;
		ra_start[223] = 339;
		ra_end[223] = 341;
		decl_start[223] = -88;
		decl_end[223] = 82;
		ra_start[224] = 340;
		ra_end[224] = 343;
		decl_start[224] = -89;
		decl_end[224] = 85;
		ra_start[225] = 342;
		ra_end[225] = 344;
		decl_start[225] = -85;
		decl_end[225] = 86;
		ra_start[226] = 343;
		ra_end[226] = 346;
		decl_start[226] = -84;
		decl_end[226] = 85;
		ra_start[227] = 345;
		ra_end[227] = 347;
		decl_start[227] = -88;
		decl_end[227] = 82;
		ra_start[228] = 346;
		ra_end[228] = 349;
		decl_start[228] = -85;
		decl_end[228] = 83;
		ra_start[229] = 348;
		ra_end[229] = 350;
		decl_start[229] = -88;
		decl_end[229] = 85;
		ra_start[230] = 349;
		ra_end[230] = 352;
		decl_start[230] = -88;
		decl_end[230] = 87;
		ra_start[231] = 351;
		ra_end[231] = 353;
		decl_start[231] = -88;
		decl_end[231] = 88;
		ra_start[232] = 352;
		ra_end[232] = 355;
		decl_start[232] = -81;
		decl_end[232] = 87;
		ra_start[233] = 354;
		ra_end[233] = 357;
		decl_start[233] = -87;
		decl_end[233] = 89;
		ra_start[234] = 356;
		ra_end[234] = 358;
		decl_start[234] = -84;
		decl_end[234] = 86;
		ra_start[235] = 357;
		ra_end[235] = 360;
		decl_start[235] = -86;
		decl_end[235] = 84;
		ra_start[236] = 359;
		ra_end[236] = 360;
		decl_start[236] = -85;
		decl_end[236] = 87;
	}
}
