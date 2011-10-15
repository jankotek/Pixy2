/*
 * @(#)Tass4Reader.java
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
 * The <code>Tass4Reader</code> is a class to read the TASS Mark IV 
 * Patches Catalog file.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 May 20
 */

public class Tass4Reader extends FileReader {
	/**
	 * The smallest R.A. in degree of the block.
	 */
	public static int[] ra_start;

	/**
	 * The largest R.A. in degree of the block.
	 */
	public static int[] ra_end;

	/**
	 * Constructs an empty <code>Tass4Reader</code>.
	 */
	public Tass4Reader ( ) {
		super();
	}

	/**
	 * Constructs a <code>Tass4Reader</code> with URL of the catalog
	 * file.
	 * @param url the URL of the catalog file.
	 */
	public Tass4Reader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "TASS Mark IV Patches Catalog";
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
		double ra = Double.parseDouble(record.substring(14, 23).trim());
		double decl = Double.parseDouble(record.substring(32, 41).trim());
		Coor coor = new Coor(ra, decl);

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			double v_mag = Double.parseDouble(record.substring(50, 56).trim());

			if (v_mag <= limiting_mag) {
				int number = Integer.parseInt(record.substring(0, 8).trim());
				double i_mag = Double.parseDouble(record.substring(69, 75).trim());

				Tass4Star star = new Tass4Star(number, coor, v_mag, i_mag);

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
		html += "TASS Mark IV Patches Catalog<br>";
		html += "Astronomical Data Center catalog No. 2271<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">http://spiff.rit.edu/tass/patches/</font></u>";
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
		return 436;
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
			return 3670;

		return 10000;
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
		return 113;
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

		Coor start_coor = new Coor((double)ra_start[(int)current_block], -6.0);
		Coor end_coor = new Coor((double)ra_end[(int)current_block], 90.0);
		if (circum_area.overlapsArea(start_coor, end_coor))
			return true;

		return false;
	}

	/**
	 * Sets the R.A. range of each block.
	 */
	static {
		ra_start = new int[436];
		ra_end = new int[436];

		ra_start[0] = 0;
		ra_end[0] = 1;
		ra_start[1] = 0;
		ra_end[1] = 2;
		ra_start[2] = 1;
		ra_end[2] = 3;
		ra_start[3] = 2;
		ra_end[3] = 4;
		ra_start[4] = 3;
		ra_end[4] = 5;
		ra_start[5] = 4;
		ra_end[5] = 6;
		ra_start[6] = 5;
		ra_end[6] = 6;
		ra_start[7] = 5;
		ra_end[7] = 7;
		ra_start[8] = 6;
		ra_end[8] = 8;
		ra_start[9] = 7;
		ra_end[9] = 9;
		ra_start[10] = 8;
		ra_end[10] = 10;
		ra_start[11] = 9;
		ra_end[11] = 10;
		ra_start[12] = 9;
		ra_end[12] = 11;
		ra_start[13] = 10;
		ra_end[13] = 12;
		ra_start[14] = 11;
		ra_end[14] = 13;
		ra_start[15] = 12;
		ra_end[15] = 14;
		ra_start[16] = 13;
		ra_end[16] = 15;
		ra_start[17] = 14;
		ra_end[17] = 15;
		ra_start[18] = 14;
		ra_end[18] = 16;
		ra_start[19] = 15;
		ra_end[19] = 17;
		ra_start[20] = 16;
		ra_end[20] = 18;
		ra_start[21] = 17;
		ra_end[21] = 19;
		ra_start[22] = 18;
		ra_end[22] = 19;
		ra_start[23] = 18;
		ra_end[23] = 20;
		ra_start[24] = 19;
		ra_end[24] = 21;
		ra_start[25] = 20;
		ra_end[25] = 22;
		ra_start[26] = 21;
		ra_end[26] = 23;
		ra_start[27] = 22;
		ra_end[27] = 24;
		ra_start[28] = 23;
		ra_end[28] = 24;
		ra_start[29] = 23;
		ra_end[29] = 25;
		ra_start[30] = 24;
		ra_end[30] = 26;
		ra_start[31] = 25;
		ra_end[31] = 27;
		ra_start[32] = 26;
		ra_end[32] = 28;
		ra_start[33] = 27;
		ra_end[33] = 29;
		ra_start[34] = 28;
		ra_end[34] = 29;
		ra_start[35] = 28;
		ra_end[35] = 30;
		ra_start[36] = 29;
		ra_end[36] = 31;
		ra_start[37] = 30;
		ra_end[37] = 32;
		ra_start[38] = 31;
		ra_end[38] = 33;
		ra_start[39] = 32;
		ra_end[39] = 33;
		ra_start[40] = 32;
		ra_end[40] = 34;
		ra_start[41] = 33;
		ra_end[41] = 35;
		ra_start[42] = 34;
		ra_end[42] = 36;
		ra_start[43] = 35;
		ra_end[43] = 37;
		ra_start[44] = 36;
		ra_end[44] = 38;
		ra_start[45] = 37;
		ra_end[45] = 38;
		ra_start[46] = 37;
		ra_end[46] = 39;
		ra_start[47] = 38;
		ra_end[47] = 40;
		ra_start[48] = 39;
		ra_end[48] = 41;
		ra_start[49] = 40;
		ra_end[49] = 42;
		ra_start[50] = 41;
		ra_end[50] = 43;
		ra_start[51] = 42;
		ra_end[51] = 43;
		ra_start[52] = 42;
		ra_end[52] = 44;
		ra_start[53] = 43;
		ra_end[53] = 45;
		ra_start[54] = 44;
		ra_end[54] = 46;
		ra_start[55] = 45;
		ra_end[55] = 47;
		ra_start[56] = 46;
		ra_end[56] = 48;
		ra_start[57] = 47;
		ra_end[57] = 49;
		ra_start[58] = 48;
		ra_end[58] = 50;
		ra_start[59] = 49;
		ra_end[59] = 51;
		ra_start[60] = 50;
		ra_end[60] = 52;
		ra_start[61] = 51;
		ra_end[61] = 52;
		ra_start[62] = 51;
		ra_end[62] = 53;
		ra_start[63] = 52;
		ra_end[63] = 54;
		ra_start[64] = 53;
		ra_end[64] = 55;
		ra_start[65] = 54;
		ra_end[65] = 56;
		ra_start[66] = 55;
		ra_end[66] = 57;
		ra_start[67] = 56;
		ra_end[67] = 58;
		ra_start[68] = 57;
		ra_end[68] = 59;
		ra_start[69] = 58;
		ra_end[69] = 60;
		ra_start[70] = 59;
		ra_end[70] = 61;
		ra_start[71] = 60;
		ra_end[71] = 61;
		ra_start[72] = 60;
		ra_end[72] = 62;
		ra_start[73] = 61;
		ra_end[73] = 63;
		ra_start[74] = 62;
		ra_end[74] = 64;
		ra_start[75] = 63;
		ra_end[75] = 65;
		ra_start[76] = 64;
		ra_end[76] = 66;
		ra_start[77] = 65;
		ra_end[77] = 67;
		ra_start[78] = 66;
		ra_end[78] = 68;
		ra_start[79] = 67;
		ra_end[79] = 69;
		ra_start[80] = 68;
		ra_end[80] = 70;
		ra_start[81] = 69;
		ra_end[81] = 71;
		ra_start[82] = 70;
		ra_end[82] = 72;
		ra_start[83] = 71;
		ra_end[83] = 73;
		ra_start[84] = 72;
		ra_end[84] = 74;
		ra_start[85] = 73;
		ra_end[85] = 74;
		ra_start[86] = 73;
		ra_end[86] = 75;
		ra_start[87] = 74;
		ra_end[87] = 76;
		ra_start[88] = 75;
		ra_end[88] = 76;
		ra_start[89] = 75;
		ra_end[89] = 77;
		ra_start[90] = 76;
		ra_end[90] = 78;
		ra_start[91] = 77;
		ra_end[91] = 78;
		ra_start[92] = 77;
		ra_end[92] = 79;
		ra_start[93] = 78;
		ra_end[93] = 80;
		ra_start[94] = 79;
		ra_end[94] = 80;
		ra_start[95] = 79;
		ra_end[95] = 81;
		ra_start[96] = 80;
		ra_end[96] = 82;
		ra_start[97] = 81;
		ra_end[97] = 82;
		ra_start[98] = 81;
		ra_end[98] = 83;
		ra_start[99] = 82;
		ra_end[99] = 84;
		ra_start[100] = 83;
		ra_end[100] = 84;
		ra_start[101] = 83;
		ra_end[101] = 85;
		ra_start[102] = 84;
		ra_end[102] = 85;
		ra_start[103] = 84;
		ra_end[103] = 86;
		ra_start[104] = 85;
		ra_end[104] = 87;
		ra_start[105] = 86;
		ra_end[105] = 87;
		ra_start[106] = 86;
		ra_end[106] = 88;
		ra_start[107] = 87;
		ra_end[107] = 88;
		ra_start[108] = 87;
		ra_end[108] = 89;
		ra_start[109] = 88;
		ra_end[109] = 90;
		ra_start[110] = 89;
		ra_end[110] = 90;
		ra_start[111] = 89;
		ra_end[111] = 91;
		ra_start[112] = 90;
		ra_end[112] = 91;
		ra_start[113] = 90;
		ra_end[113] = 92;
		ra_start[114] = 91;
		ra_end[114] = 92;
		ra_start[115] = 91;
		ra_end[115] = 93;
		ra_start[116] = 92;
		ra_end[116] = 93;
		ra_start[117] = 92;
		ra_end[117] = 94;
		ra_start[118] = 93;
		ra_end[118] = 95;
		ra_start[119] = 94;
		ra_end[119] = 95;
		ra_start[120] = 94;
		ra_end[120] = 96;
		ra_start[121] = 95;
		ra_end[121] = 96;
		ra_start[122] = 95;
		ra_end[122] = 97;
		ra_start[123] = 96;
		ra_end[123] = 97;
		ra_start[124] = 96;
		ra_end[124] = 98;
		ra_start[125] = 97;
		ra_end[125] = 98;
		ra_start[126] = 97;
		ra_end[126] = 99;
		ra_start[127] = 98;
		ra_end[127] = 99;
		ra_start[128] = 98;
		ra_end[128] = 100;
		ra_start[129] = 99;
		ra_end[129] = 100;
		ra_start[130] = 99;
		ra_end[130] = 101;
		ra_start[131] = 100;
		ra_end[131] = 101;
		ra_start[132] = 100;
		ra_end[132] = 102;
		ra_start[133] = 101;
		ra_end[133] = 102;
		ra_start[134] = 101;
		ra_end[134] = 103;
		ra_start[135] = 102;
		ra_end[135] = 103;
		ra_start[136] = 102;
		ra_end[136] = 104;
		ra_start[137] = 103;
		ra_end[137] = 104;
		ra_start[138] = 103;
		ra_end[138] = 105;
		ra_start[139] = 104;
		ra_end[139] = 105;
		ra_start[140] = 104;
		ra_end[140] = 106;
		ra_start[141] = 105;
		ra_end[141] = 107;
		ra_start[142] = 106;
		ra_end[142] = 107;
		ra_start[143] = 106;
		ra_end[143] = 108;
		ra_start[144] = 107;
		ra_end[144] = 108;
		ra_start[145] = 107;
		ra_end[145] = 109;
		ra_start[146] = 108;
		ra_end[146] = 109;
		ra_start[147] = 108;
		ra_end[147] = 110;
		ra_start[148] = 109;
		ra_end[148] = 110;
		ra_start[149] = 109;
		ra_end[149] = 111;
		ra_start[150] = 110;
		ra_end[150] = 112;
		ra_start[151] = 111;
		ra_end[151] = 112;
		ra_start[152] = 111;
		ra_end[152] = 113;
		ra_start[153] = 112;
		ra_end[153] = 114;
		ra_start[154] = 113;
		ra_end[154] = 114;
		ra_start[155] = 113;
		ra_end[155] = 115;
		ra_start[156] = 114;
		ra_end[156] = 116;
		ra_start[157] = 115;
		ra_end[157] = 117;
		ra_start[158] = 116;
		ra_end[158] = 117;
		ra_start[159] = 116;
		ra_end[159] = 118;
		ra_start[160] = 117;
		ra_end[160] = 119;
		ra_start[161] = 118;
		ra_end[161] = 120;
		ra_start[162] = 119;
		ra_end[162] = 121;
		ra_start[163] = 120;
		ra_end[163] = 122;
		ra_start[164] = 121;
		ra_end[164] = 123;
		ra_start[165] = 122;
		ra_end[165] = 124;
		ra_start[166] = 123;
		ra_end[166] = 125;
		ra_start[167] = 124;
		ra_end[167] = 126;
		ra_start[168] = 125;
		ra_end[168] = 128;
		ra_start[169] = 127;
		ra_end[169] = 129;
		ra_start[170] = 128;
		ra_end[170] = 130;
		ra_start[171] = 129;
		ra_end[171] = 131;
		ra_start[172] = 130;
		ra_end[172] = 133;
		ra_start[173] = 132;
		ra_end[173] = 134;
		ra_start[174] = 133;
		ra_end[174] = 136;
		ra_start[175] = 135;
		ra_end[175] = 137;
		ra_start[176] = 136;
		ra_end[176] = 139;
		ra_start[177] = 138;
		ra_end[177] = 141;
		ra_start[178] = 140;
		ra_end[178] = 142;
		ra_start[179] = 141;
		ra_end[179] = 144;
		ra_start[180] = 143;
		ra_end[180] = 146;
		ra_start[181] = 145;
		ra_end[181] = 148;
		ra_start[182] = 147;
		ra_end[182] = 150;
		ra_start[183] = 149;
		ra_end[183] = 152;
		ra_start[184] = 151;
		ra_end[184] = 154;
		ra_start[185] = 153;
		ra_end[185] = 156;
		ra_start[186] = 155;
		ra_end[186] = 159;
		ra_start[187] = 158;
		ra_end[187] = 161;
		ra_start[188] = 160;
		ra_end[188] = 163;
		ra_start[189] = 162;
		ra_end[189] = 166;
		ra_start[190] = 165;
		ra_end[190] = 168;
		ra_start[191] = 167;
		ra_end[191] = 170;
		ra_start[192] = 169;
		ra_end[192] = 173;
		ra_start[193] = 172;
		ra_end[193] = 175;
		ra_start[194] = 174;
		ra_end[194] = 178;
		ra_start[195] = 177;
		ra_end[195] = 180;
		ra_start[196] = 179;
		ra_end[196] = 183;
		ra_start[197] = 182;
		ra_end[197] = 185;
		ra_start[198] = 184;
		ra_end[198] = 187;
		ra_start[199] = 186;
		ra_end[199] = 190;
		ra_start[200] = 189;
		ra_end[200] = 192;
		ra_start[201] = 191;
		ra_end[201] = 194;
		ra_start[202] = 193;
		ra_end[202] = 197;
		ra_start[203] = 196;
		ra_end[203] = 199;
		ra_start[204] = 198;
		ra_end[204] = 201;
		ra_start[205] = 200;
		ra_end[205] = 203;
		ra_start[206] = 202;
		ra_end[206] = 205;
		ra_start[207] = 204;
		ra_end[207] = 208;
		ra_start[208] = 207;
		ra_end[208] = 210;
		ra_start[209] = 209;
		ra_end[209] = 212;
		ra_start[210] = 211;
		ra_end[210] = 214;
		ra_start[211] = 213;
		ra_end[211] = 216;
		ra_start[212] = 215;
		ra_end[212] = 218;
		ra_start[213] = 217;
		ra_end[213] = 219;
		ra_start[214] = 218;
		ra_end[214] = 221;
		ra_start[215] = 220;
		ra_end[215] = 223;
		ra_start[216] = 222;
		ra_end[216] = 225;
		ra_start[217] = 224;
		ra_end[217] = 227;
		ra_start[218] = 226;
		ra_end[218] = 228;
		ra_start[219] = 227;
		ra_end[219] = 230;
		ra_start[220] = 229;
		ra_end[220] = 232;
		ra_start[221] = 231;
		ra_end[221] = 233;
		ra_start[222] = 232;
		ra_end[222] = 235;
		ra_start[223] = 234;
		ra_end[223] = 236;
		ra_start[224] = 235;
		ra_end[224] = 238;
		ra_start[225] = 237;
		ra_end[225] = 239;
		ra_start[226] = 238;
		ra_end[226] = 241;
		ra_start[227] = 240;
		ra_end[227] = 242;
		ra_start[228] = 241;
		ra_end[228] = 243;
		ra_start[229] = 242;
		ra_end[229] = 244;
		ra_start[230] = 243;
		ra_end[230] = 246;
		ra_start[231] = 245;
		ra_end[231] = 247;
		ra_start[232] = 246;
		ra_end[232] = 248;
		ra_start[233] = 247;
		ra_end[233] = 249;
		ra_start[234] = 248;
		ra_end[234] = 250;
		ra_start[235] = 249;
		ra_end[235] = 251;
		ra_start[236] = 250;
		ra_end[236] = 252;
		ra_start[237] = 251;
		ra_end[237] = 253;
		ra_start[238] = 252;
		ra_end[238] = 255;
		ra_start[239] = 254;
		ra_end[239] = 255;
		ra_start[240] = 254;
		ra_end[240] = 256;
		ra_start[241] = 255;
		ra_end[241] = 257;
		ra_start[242] = 256;
		ra_end[242] = 258;
		ra_start[243] = 257;
		ra_end[243] = 259;
		ra_start[244] = 258;
		ra_end[244] = 260;
		ra_start[245] = 259;
		ra_end[245] = 261;
		ra_start[246] = 260;
		ra_end[246] = 262;
		ra_start[247] = 261;
		ra_end[247] = 263;
		ra_start[248] = 262;
		ra_end[248] = 264;
		ra_start[249] = 263;
		ra_end[249] = 264;
		ra_start[250] = 263;
		ra_end[250] = 265;
		ra_start[251] = 264;
		ra_end[251] = 266;
		ra_start[252] = 265;
		ra_end[252] = 267;
		ra_start[253] = 266;
		ra_end[253] = 267;
		ra_start[254] = 266;
		ra_end[254] = 268;
		ra_start[255] = 267;
		ra_end[255] = 269;
		ra_start[256] = 268;
		ra_end[256] = 269;
		ra_start[257] = 268;
		ra_end[257] = 270;
		ra_start[258] = 269;
		ra_end[258] = 271;
		ra_start[259] = 270;
		ra_end[259] = 271;
		ra_start[260] = 270;
		ra_end[260] = 272;
		ra_start[261] = 271;
		ra_end[261] = 273;
		ra_start[262] = 272;
		ra_end[262] = 273;
		ra_start[263] = 272;
		ra_end[263] = 274;
		ra_start[264] = 273;
		ra_end[264] = 274;
		ra_start[265] = 273;
		ra_end[265] = 275;
		ra_start[266] = 274;
		ra_end[266] = 276;
		ra_start[267] = 275;
		ra_end[267] = 276;
		ra_start[268] = 275;
		ra_end[268] = 277;
		ra_start[269] = 276;
		ra_end[269] = 277;
		ra_start[270] = 276;
		ra_end[270] = 278;
		ra_start[271] = 277;
		ra_end[271] = 279;
		ra_start[272] = 278;
		ra_end[272] = 279;
		ra_start[273] = 278;
		ra_end[273] = 280;
		ra_start[274] = 279;
		ra_end[274] = 280;
		ra_start[275] = 279;
		ra_end[275] = 281;
		ra_start[276] = 280;
		ra_end[276] = 281;
		ra_start[277] = 280;
		ra_end[277] = 282;
		ra_start[278] = 281;
		ra_end[278] = 283;
		ra_start[279] = 282;
		ra_end[279] = 283;
		ra_start[280] = 282;
		ra_end[280] = 284;
		ra_start[281] = 283;
		ra_end[281] = 284;
		ra_start[282] = 283;
		ra_end[282] = 285;
		ra_start[283] = 284;
		ra_end[283] = 285;
		ra_start[284] = 284;
		ra_end[284] = 286;
		ra_start[285] = 285;
		ra_end[285] = 286;
		ra_start[286] = 285;
		ra_end[286] = 287;
		ra_start[287] = 286;
		ra_end[287] = 287;
		ra_start[288] = 286;
		ra_end[288] = 288;
		ra_start[289] = 287;
		ra_end[289] = 288;
		ra_start[290] = 287;
		ra_end[290] = 289;
		ra_start[291] = 288;
		ra_end[291] = 289;
		ra_start[292] = 288;
		ra_end[292] = 289;
		ra_start[293] = 288;
		ra_end[293] = 290;
		ra_start[294] = 289;
		ra_end[294] = 290;
		ra_start[295] = 289;
		ra_end[295] = 291;
		ra_start[296] = 290;
		ra_end[296] = 291;
		ra_start[297] = 290;
		ra_end[297] = 292;
		ra_start[298] = 291;
		ra_end[298] = 292;
		ra_start[299] = 291;
		ra_end[299] = 293;
		ra_start[300] = 292;
		ra_end[300] = 293;
		ra_start[301] = 292;
		ra_end[301] = 293;
		ra_start[302] = 292;
		ra_end[302] = 294;
		ra_start[303] = 293;
		ra_end[303] = 294;
		ra_start[304] = 293;
		ra_end[304] = 294;
		ra_start[305] = 293;
		ra_end[305] = 295;
		ra_start[306] = 294;
		ra_end[306] = 295;
		ra_start[307] = 294;
		ra_end[307] = 295;
		ra_start[308] = 294;
		ra_end[308] = 296;
		ra_start[309] = 295;
		ra_end[309] = 296;
		ra_start[310] = 295;
		ra_end[310] = 296;
		ra_start[311] = 295;
		ra_end[311] = 297;
		ra_start[312] = 296;
		ra_end[312] = 297;
		ra_start[313] = 296;
		ra_end[313] = 297;
		ra_start[314] = 296;
		ra_end[314] = 298;
		ra_start[315] = 297;
		ra_end[315] = 298;
		ra_start[316] = 297;
		ra_end[316] = 299;
		ra_start[317] = 298;
		ra_end[317] = 299;
		ra_start[318] = 298;
		ra_end[318] = 299;
		ra_start[319] = 298;
		ra_end[319] = 300;
		ra_start[320] = 299;
		ra_end[320] = 300;
		ra_start[321] = 299;
		ra_end[321] = 300;
		ra_start[322] = 299;
		ra_end[322] = 301;
		ra_start[323] = 300;
		ra_end[323] = 301;
		ra_start[324] = 300;
		ra_end[324] = 301;
		ra_start[325] = 300;
		ra_end[325] = 302;
		ra_start[326] = 301;
		ra_end[326] = 302;
		ra_start[327] = 301;
		ra_end[327] = 302;
		ra_start[328] = 301;
		ra_end[328] = 303;
		ra_start[329] = 302;
		ra_end[329] = 303;
		ra_start[330] = 302;
		ra_end[330] = 303;
		ra_start[331] = 302;
		ra_end[331] = 304;
		ra_start[332] = 303;
		ra_end[332] = 304;
		ra_start[333] = 303;
		ra_end[333] = 305;
		ra_start[334] = 304;
		ra_end[334] = 305;
		ra_start[335] = 304;
		ra_end[335] = 305;
		ra_start[336] = 304;
		ra_end[336] = 306;
		ra_start[337] = 305;
		ra_end[337] = 306;
		ra_start[338] = 305;
		ra_end[338] = 306;
		ra_start[339] = 305;
		ra_end[339] = 307;
		ra_start[340] = 306;
		ra_end[340] = 307;
		ra_start[341] = 306;
		ra_end[341] = 308;
		ra_start[342] = 307;
		ra_end[342] = 308;
		ra_start[343] = 307;
		ra_end[343] = 308;
		ra_start[344] = 307;
		ra_end[344] = 309;
		ra_start[345] = 308;
		ra_end[345] = 309;
		ra_start[346] = 308;
		ra_end[346] = 310;
		ra_start[347] = 309;
		ra_end[347] = 310;
		ra_start[348] = 309;
		ra_end[348] = 310;
		ra_start[349] = 309;
		ra_end[349] = 311;
		ra_start[350] = 310;
		ra_end[350] = 311;
		ra_start[351] = 310;
		ra_end[351] = 312;
		ra_start[352] = 311;
		ra_end[352] = 312;
		ra_start[353] = 311;
		ra_end[353] = 313;
		ra_start[354] = 312;
		ra_end[354] = 313;
		ra_start[355] = 312;
		ra_end[355] = 313;
		ra_start[356] = 312;
		ra_end[356] = 314;
		ra_start[357] = 313;
		ra_end[357] = 314;
		ra_start[358] = 313;
		ra_end[358] = 315;
		ra_start[359] = 314;
		ra_end[359] = 315;
		ra_start[360] = 314;
		ra_end[360] = 316;
		ra_start[361] = 315;
		ra_end[361] = 316;
		ra_start[362] = 315;
		ra_end[362] = 317;
		ra_start[363] = 316;
		ra_end[363] = 317;
		ra_start[364] = 316;
		ra_end[364] = 318;
		ra_start[365] = 317;
		ra_end[365] = 318;
		ra_start[366] = 317;
		ra_end[366] = 319;
		ra_start[367] = 318;
		ra_end[367] = 319;
		ra_start[368] = 318;
		ra_end[368] = 320;
		ra_start[369] = 319;
		ra_end[369] = 320;
		ra_start[370] = 319;
		ra_end[370] = 320;
		ra_start[371] = 319;
		ra_end[371] = 321;
		ra_start[372] = 320;
		ra_end[372] = 321;
		ra_start[373] = 320;
		ra_end[373] = 322;
		ra_start[374] = 321;
		ra_end[374] = 322;
		ra_start[375] = 321;
		ra_end[375] = 323;
		ra_start[376] = 322;
		ra_end[376] = 323;
		ra_start[377] = 322;
		ra_end[377] = 324;
		ra_start[378] = 323;
		ra_end[378] = 324;
		ra_start[379] = 323;
		ra_end[379] = 325;
		ra_start[380] = 324;
		ra_end[380] = 325;
		ra_start[381] = 324;
		ra_end[381] = 326;
		ra_start[382] = 325;
		ra_end[382] = 326;
		ra_start[383] = 325;
		ra_end[383] = 327;
		ra_start[384] = 326;
		ra_end[384] = 328;
		ra_start[385] = 327;
		ra_end[385] = 328;
		ra_start[386] = 327;
		ra_end[386] = 329;
		ra_start[387] = 328;
		ra_end[387] = 329;
		ra_start[388] = 328;
		ra_end[388] = 330;
		ra_start[389] = 329;
		ra_end[389] = 330;
		ra_start[390] = 329;
		ra_end[390] = 331;
		ra_start[391] = 330;
		ra_end[391] = 331;
		ra_start[392] = 330;
		ra_end[392] = 332;
		ra_start[393] = 331;
		ra_end[393] = 332;
		ra_start[394] = 331;
		ra_end[394] = 333;
		ra_start[395] = 332;
		ra_end[395] = 333;
		ra_start[396] = 332;
		ra_end[396] = 334;
		ra_start[397] = 333;
		ra_end[397] = 335;
		ra_start[398] = 334;
		ra_end[398] = 335;
		ra_start[399] = 334;
		ra_end[399] = 336;
		ra_start[400] = 335;
		ra_end[400] = 336;
		ra_start[401] = 335;
		ra_end[401] = 337;
		ra_start[402] = 336;
		ra_end[402] = 338;
		ra_start[403] = 337;
		ra_end[403] = 338;
		ra_start[404] = 337;
		ra_end[404] = 339;
		ra_start[405] = 338;
		ra_end[405] = 339;
		ra_start[406] = 338;
		ra_end[406] = 340;
		ra_start[407] = 339;
		ra_end[407] = 341;
		ra_start[408] = 340;
		ra_end[408] = 341;
		ra_start[409] = 340;
		ra_end[409] = 342;
		ra_start[410] = 341;
		ra_end[410] = 342;
		ra_start[411] = 341;
		ra_end[411] = 343;
		ra_start[412] = 342;
		ra_end[412] = 344;
		ra_start[413] = 343;
		ra_end[413] = 344;
		ra_start[414] = 343;
		ra_end[414] = 345;
		ra_start[415] = 344;
		ra_end[415] = 346;
		ra_start[416] = 345;
		ra_end[416] = 347;
		ra_start[417] = 346;
		ra_end[417] = 347;
		ra_start[418] = 346;
		ra_end[418] = 348;
		ra_start[419] = 347;
		ra_end[419] = 349;
		ra_start[420] = 348;
		ra_end[420] = 349;
		ra_start[421] = 348;
		ra_end[421] = 350;
		ra_start[422] = 349;
		ra_end[422] = 351;
		ra_start[423] = 350;
		ra_end[423] = 352;
		ra_start[424] = 351;
		ra_end[424] = 352;
		ra_start[425] = 351;
		ra_end[425] = 353;
		ra_start[426] = 352;
		ra_end[426] = 354;
		ra_start[427] = 353;
		ra_end[427] = 355;
		ra_start[428] = 354;
		ra_end[428] = 356;
		ra_start[429] = 355;
		ra_end[429] = 356;
		ra_start[430] = 355;
		ra_end[430] = 357;
		ra_start[431] = 356;
		ra_end[431] = 358;
		ra_start[432] = 357;
		ra_end[432] = 359;
		ra_start[433] = 358;
		ra_end[433] = 359;
		ra_start[434] = 358;
		ra_end[434] = 360;
		ra_start[435] = 359;
		ra_end[435] = 360;
	}
}
