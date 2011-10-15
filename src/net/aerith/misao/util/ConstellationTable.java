/*
 * @(#)ConstellationTable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>ConstellationTable</code> is a class with functions to
 * convert between constellation names, codes and the number.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 November 7
 */

public class ConstellationTable {
	/**
	 * The number of constellations.
	 */
	protected final static int AND =  0;
	protected final static int ANT =  1;
	protected final static int APS =  2;
	protected final static int AQL =  3;
	protected final static int AQR =  4;
	protected final static int ARA =  5;
	protected final static int ARI =  6;
	protected final static int AUR =  7;
	protected final static int BOO =  8;
	protected final static int CMA =  9;
	protected final static int CMI = 10;
	protected final static int CVN = 11;
	protected final static int CAE = 12;
	protected final static int CAM = 13;
	protected final static int CAP = 14;
	protected final static int CAR = 15;
	protected final static int CAS = 16;
	protected final static int CEN = 17;
	protected final static int CEP = 18;
	protected final static int CET = 19;
	protected final static int CHA = 20;
	protected final static int CIR = 21;
	protected final static int CNC = 22;
	protected final static int COL = 23;
	protected final static int COM = 24;
	protected final static int CRA = 25;
	protected final static int CRB = 26;
	protected final static int CRT = 27;
	protected final static int CRU = 28;
	protected final static int CRV = 29;
	protected final static int CYG = 30;
	protected final static int DEL = 31;
	protected final static int DOR = 32;
	protected final static int DRA = 33;
	protected final static int EQU = 34;
	protected final static int ERI = 35;
	protected final static int FOR = 36;
	protected final static int GEM = 37;
	protected final static int GRU = 38;
	protected final static int HER = 39;
	protected final static int HOR = 40;
	protected final static int HYA = 41;
	protected final static int HYI = 42;
	protected final static int IND = 43;
	protected final static int LMI = 44;
	protected final static int LAC = 45;
	protected final static int LEO = 46;
	protected final static int LEP = 47;
	protected final static int LIB = 48;
	protected final static int LUP = 49;
	protected final static int LYN = 50;
	protected final static int LYR = 51;
	protected final static int MEN = 52;
	protected final static int MIC = 53;
	protected final static int MON = 54;
	protected final static int MUS = 55;
	protected final static int NOR = 56;
	protected final static int OCT = 57;
	protected final static int OPH = 58;
	protected final static int ORI = 59;
	protected final static int PAV = 60;
	protected final static int PEG = 61;
	protected final static int PER = 62;
	protected final static int PHE = 63;
	protected final static int PIC = 64;
	protected final static int PSA = 65;
	protected final static int PSC = 66;
	protected final static int PUP = 67;
	protected final static int PYX = 68;
	protected final static int RET = 69;
	protected final static int SCL = 70;
	protected final static int SCO = 71;
	protected final static int SCT = 72;
	protected final static int SER = 73;
	protected final static int SEX = 74;
	protected final static int SGE = 75;
	protected final static int SGR = 76;
	protected final static int TAU = 77;
	protected final static int TEL = 78;
	protected final static int TRA = 79;
	protected final static int TRI = 80;
	protected final static int TUC = 81;
	protected final static int UMA = 82;
	protected final static int UMI = 83;
	protected final static int VEL = 84;
	protected final static int VIR = 85;
	protected final static int VOL = 86;
	protected final static int VUL = 87;

	/**
	 * The constellation codes.
	 */
	protected final static String[] constellation_codes = {
		"And",
		"Ant",
		"Aps",
		"Aql",
		"Aqr",
		"Ara",
		"Ari",
		"Aur",
		"Boo",
		"CMa",
		"CMi",
		"CVn",
		"Cae",
		"Cam",
		"Cap",
		"Car",
		"Cas",
		"Cen",
		"Cep",
		"Cet",
		"Cha",
		"Cir",
		"Cnc",
		"Col",
		"Com",
		"CrA",
		"CrB",
		"Crt",
		"Cru",
		"Crv",
		"Cyg",
		"Del",
		"Dor",
		"Dra",
		"Equ",
		"Eri",
		"For",
		"Gem",
		"Gru",
		"Her",
		"Hor",
		"Hya",
		"Hyi",
		"Ind",
		"LMi",
		"Lac",
		"Leo",
		"Lep",
		"Lib",
		"Lup",
		"Lyn",
		"Lyr",
		"Men",
		"Mic",
		"Mon",
		"Mus",
		"Nor",
		"Oct",
		"Oph",
		"Ori",
		"Pav",
		"Peg",
		"Per",
		"Phe",
		"Pic",
		"PsA",
		"Psc",
		"Pup",
		"Pyx",
		"Ret",
		"Scl",
		"Sco",
		"Sct",
		"Ser",
		"Sex",
		"Sge",
		"Sgr",
		"Tau",
		"Tel",
		"TrA",
		"Tri",
		"Tuc",
		"UMa",
		"UMi",
		"Vel",
		"Vir",
		"Vol",
		"Vul" };

	/**
	 * Returns true if the specified string is one of the
	 * constellation codes.
	 * @param string the constellation code.
	 * @return true if the specified string is one of the
	 * constellation codes.
	 */
	public static boolean isConstellationCode ( String string ) {
		for (int i = 0 ; i < 88 ; i++) {
			if (constellation_codes[i].equalsIgnoreCase(string))
				return true;
		}
		return false;
	}

	/**
	 * Gets the constellation code of the specified number.
	 * @param number the number of constellation.
	 * @return the constellation code of the specified number.
	 */
	public static String getConstellationCode ( int number ) {
		if (0 <= number  &&  number < 88)
			return constellation_codes[number];
		return null;
	}

	/**
	 * Gets the number of constellation of the specified code.
	 * @param code the constellation code.
	 * @return the number of constellation of the specified code.
	 */
	public static int getConstellationNumber ( String code ) {
		for (int i = 0 ; i < 88 ; i++) {
			if (constellation_codes[i].equalsIgnoreCase(code))
				return i;
		}
		return -1;
	}
}
