/*
 * @(#)HicReader.java
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
 * The <code>HicReader</code> is a class to read the Hipparcos Input 
 * Catalogue.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 8
 */

public class HicReader extends FileReader {
	/**
	 * The smallest R.A. in degree of the block.
	 */
	public static int[] ra_start;

	/**
	 * The largest R.A. in degree of the block.
	 */
	public static int[] ra_end;

	/**
	 * Constructs an empty <code>HicReader</code>.
	 */
	public HicReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>HicReader</code> with URL of the catalog
	 * file.
	 * @param url the URL of the catalog file.
	 */
	public HicReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Hipparcos Input Catalogue";
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
		Coor coor = Coor.create(record.substring(14, 39));

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			double hp_mag = Format.doubleValueOf(record.substring(181, 186));

			if (hp_mag <= limiting_mag) {
				int number = Integer.parseInt(record.substring(0, 6).trim());

				byte hp_accuracy = HicStar.ACCURACY_100TH;
				if (record.charAt(185) == ' ')
					hp_accuracy = HicStar.ACCURACY_10TH;

				HicStar star = new HicStar(number, coor, hp_mag, hp_accuracy);

				if (record.substring(190, 196).trim().length() > 0) {
					double v_mag = Format.doubleValueOf(record.substring(190, 196));
					byte accuracy = HicStar.ACCURACY_1000TH;
					if (record.charAt(195) == ' ')
						accuracy = HicStar.ACCURACY_100TH;
					if (record.charAt(194) == ' ')
						accuracy = HicStar.ACCURACY_10TH;
					star.setVMagnitude(v_mag, accuracy);
				}

				if (record.substring(202, 208).trim().length() > 0) {
					double b_v = Format.doubleValueOf(record.substring(202, 208));
					byte accuracy = HicStar.ACCURACY_1000TH;
					if (record.charAt(207) == ' ')
						accuracy = HicStar.ACCURACY_100TH;
					if (record.charAt(206) == ' ')
						accuracy = HicStar.ACCURACY_10TH;
					star.setBVDifference(b_v, accuracy);
				}

				if (record.substring(155, 161).trim().length() > 0)
					star.setProperMotionInRA(Format.doubleValueOf(record.substring(155, 161).trim()));
				if (record.substring(162, 168).trim().length() > 0)
					star.setProperMotionInDecl(Format.doubleValueOf(record.substring(162, 168).trim()));

				if (record.substring(216, 227).trim().length() > 0)
					star.setSpectrum(record.substring(216, 227).trim());

				if (record.substring(230, 235).trim().length() > 0)
					star.setParallax(Integer.parseInt(record.substring(230, 235).trim()));

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
		html += "Hipparcos Input Catalogue, Version 2<br>";
		html += "Astronomical Data Center catalog No. 1196<br>";
		html += "ADC CD-ROM Vol. 2<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/1/1196/main.dat.gz</font></u>";
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
			return 209;

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
		return 431;
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

		Coor start_coor = new Coor((double)ra_start[(int)current_block], 90);
		Coor end_coor = new Coor((double)ra_end[(int)current_block], 0);
		if (circum_area.overlapsArea(start_coor, end_coor))
			return true;

		start_coor = new Coor((double)ra_start[(int)current_block], 0);
		end_coor = new Coor((double)ra_end[(int)current_block], -90);
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

		ra_start[0] = 0;
		ra_end[0] = 2;
		ra_start[1] = 1;
		ra_end[1] = 4;
		ra_start[2] = 3;
		ra_end[2] = 5;
		ra_start[3] = 4;
		ra_end[3] = 7;
		ra_start[4] = 6;
		ra_end[4] = 8;
		ra_start[5] = 7;
		ra_end[5] = 10;
		ra_start[6] = 9;
		ra_end[6] = 12;
		ra_start[7] = 11;
		ra_end[7] = 13;
		ra_start[8] = 12;
		ra_end[8] = 15;
		ra_start[9] = 14;
		ra_end[9] = 17;
		ra_start[10] = 16;
		ra_end[10] = 18;
		ra_start[11] = 17;
		ra_end[11] = 20;
		ra_start[12] = 19;
		ra_end[12] = 21;
		ra_start[13] = 20;
		ra_end[13] = 23;
		ra_start[14] = 22;
		ra_end[14] = 25;
		ra_start[15] = 24;
		ra_end[15] = 26;
		ra_start[16] = 25;
		ra_end[16] = 28;
		ra_start[17] = 27;
		ra_end[17] = 29;
		ra_start[18] = 28;
		ra_end[18] = 31;
		ra_start[19] = 30;
		ra_end[19] = 33;
		ra_start[20] = 32;
		ra_end[20] = 34;
		ra_start[21] = 33;
		ra_end[21] = 36;
		ra_start[22] = 35;
		ra_end[22] = 38;
		ra_start[23] = 37;
		ra_end[23] = 39;
		ra_start[24] = 38;
		ra_end[24] = 41;
		ra_start[25] = 40;
		ra_end[25] = 42;
		ra_start[26] = 41;
		ra_end[26] = 44;
		ra_start[27] = 43;
		ra_end[27] = 46;
		ra_start[28] = 45;
		ra_end[28] = 47;
		ra_start[29] = 46;
		ra_end[29] = 49;
		ra_start[30] = 48;
		ra_end[30] = 50;
		ra_start[31] = 49;
		ra_end[31] = 52;
		ra_start[32] = 51;
		ra_end[32] = 54;
		ra_start[33] = 53;
		ra_end[33] = 55;
		ra_start[34] = 54;
		ra_end[34] = 57;
		ra_start[35] = 56;
		ra_end[35] = 58;
		ra_start[36] = 57;
		ra_end[36] = 60;
		ra_start[37] = 59;
		ra_end[37] = 62;
		ra_start[38] = 61;
		ra_end[38] = 63;
		ra_start[39] = 62;
		ra_end[39] = 65;
		ra_start[40] = 64;
		ra_end[40] = 66;
		ra_start[41] = 65;
		ra_end[41] = 68;
		ra_start[42] = 67;
		ra_end[42] = 70;
		ra_start[43] = 69;
		ra_end[43] = 72;
		ra_start[44] = 71;
		ra_end[44] = 73;
		ra_start[45] = 72;
		ra_end[45] = 75;
		ra_start[46] = 74;
		ra_end[46] = 76;
		ra_start[47] = 75;
		ra_end[47] = 78;
		ra_start[48] = 77;
		ra_end[48] = 79;
		ra_start[49] = 78;
		ra_end[49] = 81;
		ra_start[50] = 80;
		ra_end[50] = 82;
		ra_start[51] = 81;
		ra_end[51] = 84;
		ra_start[52] = 83;
		ra_end[52] = 85;
		ra_start[53] = 84;
		ra_end[53] = 86;
		ra_start[54] = 85;
		ra_end[54] = 88;
		ra_start[55] = 87;
		ra_end[55] = 89;
		ra_start[56] = 88;
		ra_end[56] = 91;
		ra_start[57] = 90;
		ra_end[57] = 92;
		ra_start[58] = 91;
		ra_end[58] = 94;
		ra_start[59] = 93;
		ra_end[59] = 95;
		ra_start[60] = 94;
		ra_end[60] = 97;
		ra_start[61] = 96;
		ra_end[61] = 98;
		ra_start[62] = 97;
		ra_end[62] = 100;
		ra_start[63] = 99;
		ra_end[63] = 101;
		ra_start[64] = 100;
		ra_end[64] = 102;
		ra_start[65] = 101;
		ra_end[65] = 104;
		ra_start[66] = 103;
		ra_end[66] = 105;
		ra_start[67] = 104;
		ra_end[67] = 106;
		ra_start[68] = 105;
		ra_end[68] = 108;
		ra_start[69] = 107;
		ra_end[69] = 109;
		ra_start[70] = 108;
		ra_end[70] = 111;
		ra_start[71] = 110;
		ra_end[71] = 112;
		ra_start[72] = 111;
		ra_end[72] = 113;
		ra_start[73] = 112;
		ra_end[73] = 115;
		ra_start[74] = 114;
		ra_end[74] = 116;
		ra_start[75] = 115;
		ra_end[75] = 117;
		ra_start[76] = 116;
		ra_end[76] = 119;
		ra_start[77] = 118;
		ra_end[77] = 120;
		ra_start[78] = 119;
		ra_end[78] = 122;
		ra_start[79] = 121;
		ra_end[79] = 123;
		ra_start[80] = 122;
		ra_end[80] = 125;
		ra_start[81] = 124;
		ra_end[81] = 126;
		ra_start[82] = 125;
		ra_end[82] = 128;
		ra_start[83] = 127;
		ra_end[83] = 129;
		ra_start[84] = 128;
		ra_end[84] = 131;
		ra_start[85] = 130;
		ra_end[85] = 132;
		ra_start[86] = 131;
		ra_end[86] = 134;
		ra_start[87] = 133;
		ra_end[87] = 135;
		ra_start[88] = 134;
		ra_end[88] = 137;
		ra_start[89] = 136;
		ra_end[89] = 138;
		ra_start[90] = 137;
		ra_end[90] = 140;
		ra_start[91] = 139;
		ra_end[91] = 141;
		ra_start[92] = 140;
		ra_end[92] = 143;
		ra_start[93] = 142;
		ra_end[93] = 144;
		ra_start[94] = 143;
		ra_end[94] = 146;
		ra_start[95] = 145;
		ra_end[95] = 147;
		ra_start[96] = 146;
		ra_end[96] = 149;
		ra_start[97] = 148;
		ra_end[97] = 151;
		ra_start[98] = 150;
		ra_end[98] = 152;
		ra_start[99] = 151;
		ra_end[99] = 154;
		ra_start[100] = 153;
		ra_end[100] = 155;
		ra_start[101] = 154;
		ra_end[101] = 157;
		ra_start[102] = 156;
		ra_end[102] = 158;
		ra_start[103] = 157;
		ra_end[103] = 160;
		ra_start[104] = 159;
		ra_end[104] = 162;
		ra_start[105] = 161;
		ra_end[105] = 163;
		ra_start[106] = 162;
		ra_end[106] = 165;
		ra_start[107] = 164;
		ra_end[107] = 166;
		ra_start[108] = 165;
		ra_end[108] = 168;
		ra_start[109] = 167;
		ra_end[109] = 170;
		ra_start[110] = 169;
		ra_end[110] = 171;
		ra_start[111] = 170;
		ra_end[111] = 173;
		ra_start[112] = 172;
		ra_end[112] = 174;
		ra_start[113] = 173;
		ra_end[113] = 176;
		ra_start[114] = 175;
		ra_end[114] = 177;
		ra_start[115] = 176;
		ra_end[115] = 179;
		ra_start[116] = 178;
		ra_end[116] = 181;
		ra_start[117] = 180;
		ra_end[117] = 182;
		ra_start[118] = 181;
		ra_end[118] = 184;
		ra_start[119] = 183;
		ra_end[119] = 185;
		ra_start[120] = 184;
		ra_end[120] = 187;
		ra_start[121] = 186;
		ra_end[121] = 188;
		ra_start[122] = 187;
		ra_end[122] = 190;
		ra_start[123] = 189;
		ra_end[123] = 191;
		ra_start[124] = 190;
		ra_end[124] = 193;
		ra_start[125] = 192;
		ra_end[125] = 194;
		ra_start[126] = 193;
		ra_end[126] = 196;
		ra_start[127] = 195;
		ra_end[127] = 197;
		ra_start[128] = 196;
		ra_end[128] = 199;
		ra_start[129] = 198;
		ra_end[129] = 201;
		ra_start[130] = 200;
		ra_end[130] = 202;
		ra_start[131] = 201;
		ra_end[131] = 204;
		ra_start[132] = 203;
		ra_end[132] = 205;
		ra_start[133] = 204;
		ra_end[133] = 207;
		ra_start[134] = 206;
		ra_end[134] = 208;
		ra_start[135] = 207;
		ra_end[135] = 210;
		ra_start[136] = 209;
		ra_end[136] = 211;
		ra_start[137] = 210;
		ra_end[137] = 213;
		ra_start[138] = 212;
		ra_end[138] = 214;
		ra_start[139] = 213;
		ra_end[139] = 216;
		ra_start[140] = 215;
		ra_end[140] = 217;
		ra_start[141] = 216;
		ra_end[141] = 219;
		ra_start[142] = 218;
		ra_end[142] = 220;
		ra_start[143] = 219;
		ra_end[143] = 222;
		ra_start[144] = 221;
		ra_end[144] = 223;
		ra_start[145] = 222;
		ra_end[145] = 225;
		ra_start[146] = 224;
		ra_end[146] = 226;
		ra_start[147] = 225;
		ra_end[147] = 228;
		ra_start[148] = 227;
		ra_end[148] = 229;
		ra_start[149] = 228;
		ra_end[149] = 231;
		ra_start[150] = 230;
		ra_end[150] = 232;
		ra_start[151] = 231;
		ra_end[151] = 234;
		ra_start[152] = 233;
		ra_end[152] = 235;
		ra_start[153] = 234;
		ra_end[153] = 237;
		ra_start[154] = 236;
		ra_end[154] = 238;
		ra_start[155] = 237;
		ra_end[155] = 240;
		ra_start[156] = 239;
		ra_end[156] = 241;
		ra_start[157] = 240;
		ra_end[157] = 243;
		ra_start[158] = 242;
		ra_end[158] = 244;
		ra_start[159] = 243;
		ra_end[159] = 246;
		ra_start[160] = 245;
		ra_end[160] = 247;
		ra_start[161] = 246;
		ra_end[161] = 249;
		ra_start[162] = 248;
		ra_end[162] = 250;
		ra_start[163] = 249;
		ra_end[163] = 252;
		ra_start[164] = 251;
		ra_end[164] = 254;
		ra_start[165] = 253;
		ra_end[165] = 255;
		ra_start[166] = 254;
		ra_end[166] = 257;
		ra_start[167] = 256;
		ra_end[167] = 258;
		ra_start[168] = 257;
		ra_end[168] = 260;
		ra_start[169] = 259;
		ra_end[169] = 261;
		ra_start[170] = 260;
		ra_end[170] = 263;
		ra_start[171] = 262;
		ra_end[171] = 264;
		ra_start[172] = 263;
		ra_end[172] = 266;
		ra_start[173] = 265;
		ra_end[173] = 267;
		ra_start[174] = 266;
		ra_end[174] = 269;
		ra_start[175] = 268;
		ra_end[175] = 270;
		ra_start[176] = 269;
		ra_end[176] = 272;
		ra_start[177] = 271;
		ra_end[177] = 273;
		ra_start[178] = 272;
		ra_end[178] = 275;
		ra_start[179] = 274;
		ra_end[179] = 276;
		ra_start[180] = 275;
		ra_end[180] = 278;
		ra_start[181] = 277;
		ra_end[181] = 279;
		ra_start[182] = 278;
		ra_end[182] = 281;
		ra_start[183] = 280;
		ra_end[183] = 282;
		ra_start[184] = 281;
		ra_end[184] = 284;
		ra_start[185] = 283;
		ra_end[185] = 285;
		ra_start[186] = 284;
		ra_end[186] = 286;
		ra_start[187] = 285;
		ra_end[187] = 288;
		ra_start[188] = 287;
		ra_end[188] = 289;
		ra_start[189] = 288;
		ra_end[189] = 291;
		ra_start[190] = 290;
		ra_end[190] = 292;
		ra_start[191] = 291;
		ra_end[191] = 294;
		ra_start[192] = 293;
		ra_end[192] = 295;
		ra_start[193] = 294;
		ra_end[193] = 297;
		ra_start[194] = 296;
		ra_end[194] = 298;
		ra_start[195] = 297;
		ra_end[195] = 300;
		ra_start[196] = 299;
		ra_end[196] = 301;
		ra_start[197] = 300;
		ra_end[197] = 302;
		ra_start[198] = 301;
		ra_end[198] = 304;
		ra_start[199] = 303;
		ra_end[199] = 305;
		ra_start[200] = 304;
		ra_end[200] = 307;
		ra_start[201] = 306;
		ra_end[201] = 308;
		ra_start[202] = 307;
		ra_end[202] = 309;
		ra_start[203] = 308;
		ra_end[203] = 311;
		ra_start[204] = 310;
		ra_end[204] = 312;
		ra_start[205] = 311;
		ra_end[205] = 314;
		ra_start[206] = 313;
		ra_end[206] = 315;
		ra_start[207] = 314;
		ra_end[207] = 317;
		ra_start[208] = 316;
		ra_end[208] = 318;
		ra_start[209] = 317;
		ra_end[209] = 320;
		ra_start[210] = 319;
		ra_end[210] = 321;
		ra_start[211] = 320;
		ra_end[211] = 323;
		ra_start[212] = 322;
		ra_end[212] = 324;
		ra_start[213] = 323;
		ra_end[213] = 326;
		ra_start[214] = 325;
		ra_end[214] = 328;
		ra_start[215] = 327;
		ra_end[215] = 329;
		ra_start[216] = 328;
		ra_end[216] = 331;
		ra_start[217] = 330;
		ra_end[217] = 332;
		ra_start[218] = 331;
		ra_end[218] = 334;
		ra_start[219] = 333;
		ra_end[219] = 335;
		ra_start[220] = 334;
		ra_end[220] = 337;
		ra_start[221] = 336;
		ra_end[221] = 338;
		ra_start[222] = 337;
		ra_end[222] = 340;
		ra_start[223] = 339;
		ra_end[223] = 341;
		ra_start[224] = 340;
		ra_end[224] = 343;
		ra_start[225] = 342;
		ra_end[225] = 344;
		ra_start[226] = 343;
		ra_end[226] = 346;
		ra_start[227] = 345;
		ra_end[227] = 347;
		ra_start[228] = 346;
		ra_end[228] = 349;
		ra_start[229] = 348;
		ra_end[229] = 350;
		ra_start[230] = 349;
		ra_end[230] = 352;
		ra_start[231] = 351;
		ra_end[231] = 353;
		ra_start[232] = 352;
		ra_end[232] = 355;
		ra_start[233] = 354;
		ra_end[233] = 357;
		ra_start[234] = 356;
		ra_end[234] = 358;
		ra_start[235] = 357;
		ra_end[235] = 360;
		ra_start[236] = 49;
		ra_end[236] = 360;
	}
}
