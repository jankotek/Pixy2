/*
 * @(#)VeronReader.java
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
 * The <code>VeronReader</code> is a class to read the Quasars and 
 * Active Galactic Nuclei.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 9
 */

public class VeronReader extends FileReader {
	/**
	 * Constructs an empty <code>VeronReader</code>.
	 */
	public VeronReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>VeronReader</code> with URL of the catalog
	 * file.
	 * @param url the URL of the catalog file.
	 */
	public VeronReader ( URL url ) {
		super();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Quasars and Active Galactic Nuclei";
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 10.0;
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
		if (record.length() < 105)
			return null;

		String name = record.substring(2, 19).trim();

		String coor_string = record.substring(21, 41);
		Coor coor = Coor.create(coor_string);

		if (circum_area == null  ||  circum_area.inArea(coor)) {
			// If it has no name.
			if (name.length() == 0) {
				String ra = record.substring(21, 23) + record.substring(24, 26) + record.substring(27, 31);
				ra.replace(' ', '0');
				String decl = record.substring(33, 35) + record.substring(36, 38) + record.substring(39, 41);
				decl.replace(' ', '0');
				if (record.charAt(32) == '-')
					decl = "-" + decl;
				else
					decl = "+" + decl;

				name = "J" + ra + decl;
			}

			// If the name is duplicated.
			if (name.equals("Q 0037-3327")  &&  coor_string.equals(" 0 39 51.3 -33 11 21"))
				name = name + " A";
			if (name.equals("Q 0037-3327")  &&  coor_string.equals(" 0 40  4.8 -33 10 42"))
				name = name + " B";
			if (name.equals("Q 0039-4013")  &&  coor_string.equals(" 0 41 50.3 -39 57 19"))
				name = name + " A";
			if (name.equals("Q 0039-4022")  &&  coor_string.equals(" 0 41 51.2 -40  6 30"))
				name = name + " A";
			if (name.equals("Q 0039-4013")  &&  coor_string.equals(" 0 42  1.0 -39 57 33"))
				name = name + " B";
			if (name.equals("Q 0039-4022")  &&  coor_string.equals(" 0 42  7.3 -40  5 34"))
				name = name + " B";
			if (name.equals("Q 0041-2904")  &&  coor_string.equals(" 0 43 55.6 -28 47 51"))
				name = name + " A";
			if (name.equals("Q 0041-2904")  &&  coor_string.equals(" 0 44 24.8 -28 48  0"))
				name = name + " B";
			if (name.equals("Q 0047-2633")  &&  coor_string.equals(" 0 49 57.2 -26 16 58"))
				name = name + " A";
			if (name.equals("RXS J00508+3536")  &&  coor_string.equals(" 0 50 50.8  35 36 42"))
				name = name + " A";
			if (name.equals("Q 0049-4041")  &&  coor_string.equals(" 0 51 22.1 -40 25 20"))
				name = name + " A";
			if (name.equals("Q 0049-3805")  &&  coor_string.equals(" 0 51 39.0 -37 48 42"))
				name = name + " A";
			if (name.equals("Q 0049-3843")  &&  coor_string.equals(" 0 51 39.0 -38 26 52"))
				name = name + " A";
			if (name.equals("Q 0049-2535")  &&  coor_string.equals(" 0 52 11.1 -25 18 59"))
				name = name + " A";
			if (name.equals("Q 0049-4041")  &&  coor_string.equals(" 0 52 12.0 -40 25  3"))
				name = name + " B";
			if (name.equals("Q 0049-3805")  &&  coor_string.equals(" 0 52 15.2 -37 48 52"))
				name = name + " B";
			if (name.equals("Q 0049-2535")  &&  coor_string.equals(" 0 52 20.0 -25 19  7"))
				name = name + " B";
			if (name.equals("Q 0049-3843")  &&  coor_string.equals(" 0 52 20.4 -38 26 49"))
				name = name + " B";
			if (name.equals("Q 0050-4143")  &&  coor_string.equals(" 0 52 24.3 -41 27  8"))
				name = name + " A";
			if (name.equals("Q 0050-4143")  &&  coor_string.equals(" 0 53 11.0 -41 27 34"))
				name = name + " B";
			if (name.equals("RXS J00508+3536")  &&  coor_string.equals(" 0 53 35.4  35 52 58"))
				name = name + " B";
			if (name.equals("Q 0052-4009")  &&  coor_string.equals(" 0 54 54.7 -39 53 21"))
				name = name + " A";
			if (name.equals("Q 0052-4009")  &&  coor_string.equals(" 0 55  1.3 -39 52 56"))
				name = name + " B";
			if (name.equals("Q 0053-3342")  &&  coor_string.equals(" 0 56  2.0 -33 26 17"))
				name = name + " A";
			if (name.equals("Q 0053-3342")  &&  coor_string.equals(" 0 56  8.6 -33 26 11"))
				name = name + " B";
			if (name.equals("Q 0053-2813")  &&  coor_string.equals(" 0 56 12.5 -27 57 11"))
				name = name + " A";
			if (name.equals("Q 0053-2813")  &&  coor_string.equals(" 0 56 25.2 -27 57 31"))
				name = name + " B";
			if (name.equals("Q 0054-3101")  &&  coor_string.equals(" 0 56 51.8 -30 44 48"))
				name = name + " A";
			if (name.equals("Q 0054-3101")  &&  coor_string.equals(" 0 57 12.8 -30 45 45"))
				name = name + " B";
			if (name.equals("Q 0056-3557")  &&  coor_string.equals(" 0 58 56.0 -35 41  3"))
				name = name + " A";
			if (name.equals("Q 0056-3557")  &&  coor_string.equals(" 0 59 14.2 -35 41 43"))
				name = name + " B";
			if (name.equals("Q 0056-2948")  &&  coor_string.equals(" 0 59 22.3 -29 32  9"))
				name = name + " A";
			if (name.equals("Q 0059-3911")  &&  coor_string.equals(" 1  2 18.6 -38 55 44"))
				name = name + " A";
			if (name.equals("Q 0059-3911")  &&  coor_string.equals(" 1  2 19.3 -38 55 30"))
				name = name + " B";
			if (name.equals("Q 0100-2848")  &&  coor_string.equals(" 1  3  1.9 -28 32 25"))
				name = name + " A";
			if (name.equals("Q 0100-2629")  &&  coor_string.equals(" 1  3  2.5 -26 13 33"))
				name = name + " A";
			if (name.equals("Q 0100-2629")  &&  coor_string.equals(" 1  3 11.2 -26 13 40"))
				name = name + " B";
			if (name.equals("Q 0101-3630")  &&  coor_string.equals(" 1  3 24.6 -36 13 59"))
				name = name + " A";
			if (name.equals("Q 0101-2548")  &&  coor_string.equals(" 1  3 33.3 -25 32 28"))
				name = name + " A";
			if (name.equals("Q 0101-2548")  &&  coor_string.equals(" 1  4  6.8 -25 32  4"))
				name = name + " B";
			if (name.equals("Q 0101-3630")  &&  coor_string.equals(" 1  4 19.2 -36 14  5"))
				name = name + " B";
			if (name.equals("Q 0102-4002")  &&  coor_string.equals(" 1  4 23.8 -39 46 29"))
				name = name + " A";
			if (name.equals("Q 0102-4002")  &&  coor_string.equals(" 1  5  1.5 -39 46 48"))
				name = name + " B";
			if (name.equals("Q 0103-4032")  &&  coor_string.equals(" 1  5 28.3 -40 16 38"))
				name = name + " A";
			if (name.equals("Q 0103-4032")  &&  coor_string.equals(" 1  5 40.7 -40 16 35"))
				name = name + " B";
			if (name.equals("Q 0105-4005")  &&  coor_string.equals(" 1  7 52.2 -39 49 52"))
				name = name + " A";
			if (name.equals("Q 0105-4005")  &&  coor_string.equals(" 1  8  8.4 -39 49  2"))
				name = name + " B";
			if (name.equals("Q 0121-3855")  &&  coor_string.equals(" 1 23 22.3 -38 39 28"))
				name = name + " A";
			if (name.equals("Q 0121-3855")  &&  coor_string.equals(" 1 23 31.2 -38 40 21"))
				name = name + " B";
			if (name.equals("Q 0128-3913")  &&  coor_string.equals(" 1 30 15.2 -38 57 48"))
				name = name + " A";
			if (name.equals("Q 0128-4225")  &&  coor_string.equals(" 1 30 30.9 -42  9 51"))
				name = name + " A";
			if (name.equals("Q 0128-4225")  &&  coor_string.equals(" 1 30 42.0 -42  9 37"))
				name = name + " B";
			if (name.equals("Q 0128-3913")  &&  coor_string.equals(" 1 31  2.1 -38 58 10"))
				name = name + " B";
			if (name.equals("Q 0134-4202")  &&  coor_string.equals(" 1 36 24.2 -41 47 24"))
				name = name + " A";
			if (name.equals("Q 0134-4202")  &&  coor_string.equals(" 1 36 37.9 -41 47 35"))
				name = name + " B";
			if (name.equals("Q 0136-3847")  &&  coor_string.equals(" 1 38 24.7 -38 31 59"))
				name = name + " A";
			if (name.equals("Q 0136-3847")  &&  coor_string.equals(" 1 38 45.2 -38 31 59"))
				name = name + " B";
			if (name.equals("Q 0136-3831")  &&  coor_string.equals(" 1 38 46.9 -38 16 44"))
				name = name + " A";
			if (name.equals("Q 0136-3831")  &&  coor_string.equals(" 1 38 58.6 -38 16 34"))
				name = name + " B";
			if (name.equals("Q 0136-3847")  &&  coor_string.equals(" 1 39  3.0 -38 32  1"))
				name = name + " C";
			if (name.equals("Q 0137-4202")  &&  coor_string.equals(" 1 39 19.4 -41 46 48"))
				name = name + " A";
			if (name.equals("Q 0137-4202")  &&  coor_string.equals(" 1 39 48.4 -41 47 44"))
				name = name + " B";
			if (name.equals("Q 0144-4042")  &&  coor_string.equals(" 1 46 15.5 -40 27 10"))
				name = name + " A";
			if (name.equals("Q 0144-4042")  &&  coor_string.equals(" 1 46 19.4 -40 27 59"))
				name = name + " B";
			if (name.equals("Q 0145-3835")  &&  coor_string.equals(" 1 47 19.2 -38 20 18"))
				name = name + " A";
			if (name.equals("Q 0145-3835")  &&  coor_string.equals(" 1 48  0.3 -38 20 41"))
				name = name + " B";
			if (name.equals("0149-510")  &&  coor_string.equals(" 1 51 29.3 -50 55 22"))
				name = name + " A";
			if (name.equals("Q 0152-3812")  &&  coor_string.equals(" 1 54 15.8 -37 57 40"))
				name = name + " A";
			if (name.equals("Q 0152-4055")  &&  coor_string.equals(" 1 54 20.0 -40 40 30"))
				name = name + " A";
			if (name.equals("Q 0152-3812")  &&  coor_string.equals(" 1 54 34.9 -37 57 21"))
				name = name + " B";
			if (name.equals("Q 0152-4055")  &&  coor_string.equals(" 1 55  4.7 -40 40 43"))
				name = name + " B";
			if (name.equals("Q 0155-3939")  &&  coor_string.equals(" 1 57 39.3 -39 25  7"))
				name = name + " A";
			if (name.equals("Q 0155-3939")  &&  coor_string.equals(" 1 57 48.5 -39 24 54"))
				name = name + " B";
			if (name.equals("AO 0235+164")  &&  coor_string.equals(" 2 38 38.9  16 37  0"))
				name = name + " A";
			if (name.equals("Q 0245+0023")  &&  coor_string.equals(" 2 48  7.7   0 35 52"))
				name = name + " A";
			if (name.equals("Q 0245+0023")  &&  coor_string.equals(" 2 48 23.5   0 35 52"))
				name = name + " B";
			if (name.equals("4C 17.22")  &&  coor_string.equals(" 4  6 26.0  25 11 30"))
				name = name + " A";
			if (name.equals("4C 17.22")  &&  coor_string.equals(" 4  7 28.8  17 50 52"))
				name = name + " B";
			if (name.equals("Q 1039+047")  &&  coor_string.equals("10 42 25.8   4 27 11"))
				name = name + " A";
			if (name.equals("Q 1158-1054")  &&  coor_string.equals("12  0 44.2 -11 10 56"))
				name = name + " A";
			if (name.equals("Q 1158-1054")  &&  coor_string.equals("12  1 33.8 -11 10 44"))
				name = name + " B";
			if (name.equals("Q 1216-1103")  &&  coor_string.equals("12 18 58.6 -11 19 43"))
				name = name + " A";
			if (name.equals("Q 1219-1113")  &&  coor_string.equals("12 22  6.2 -11 30 13"))
				name = name + " A";
			if (name.equals("Q 1219-1113")  &&  coor_string.equals("12 22 31.4 -11 30  7"))
				name = name + " B";
			if (name.equals("Q 1231+6249")  &&  coor_string.equals("12 33 33.0  62 32 54"))
				name = name + " A";
			if (name.equals("Q 1231+6249")  &&  coor_string.equals("12 34  1.2  62 33 16"))
				name = name + " B";
			if (name.equals("1WGA J1335.2+3802")  &&  coor_string.equals("13 35 12.5  38  2 47"))
				name = name + " A";
			if (name.equals("1WGA J1629.0+3724")  &&  coor_string.equals("16 29  2.6  37 24 34"))
				name = name + " A";
			if (name.equals("1WGA J1629.0+3724")  &&  coor_string.equals("16 29  2.6  37 24 29"))
				name = name + " B";
			if (name.equals("1637.9+4045")  &&  coor_string.equals("16 39 33.7  40 39 38"))
				name = name + " A";
			if (name.equals("1637.9+4045")  &&  coor_string.equals("16 39 34.9  40 39 35"))
				name = name + " B";
			if (name.equals("Q 2129-4653")  &&  coor_string.equals("21 33  2.1 -46 40 27"))
				name = name + " A";
			if (name.equals("Q 2129-4653")  &&  coor_string.equals("21 33 14.2 -46 40 31"))
				name = name + " B";
			if (name.equals("Q 2131-4339")  &&  coor_string.equals("21 34 21.7 -43 26  7"))
				name = name + " A";
			if (name.equals("Q 2131-4515")  &&  coor_string.equals("21 34 27.7 -45  1 43"))
				name = name + " A";
			if (name.equals("Q 2131-4515")  &&  coor_string.equals("21 34 40.1 -45  1 40"))
				name = name + " B";
			if (name.equals("Q 2131-4339")  &&  coor_string.equals("21 34 48.5 -43 26 19"))
				name = name + " B";
			if (name.equals("Q 2132-4324")  &&  coor_string.equals("21 35 22.8 -43 11 22"))
				name = name + " A";
			if (name.equals("Q 2132-4418")  &&  coor_string.equals("21 35 30.8 -44  5 23"))
				name = name + " A";
			if (name.equals("Q 2132-4324")  &&  coor_string.equals("21 35 32.8 -43 11 22"))
				name = name + " B";
			if (name.equals("Q 2132-4418")  &&  coor_string.equals("21 35 43.5 -44  5 32"))
				name = name + " B";
			if (name.equals("Q 2133-4416")  &&  coor_string.equals("21 36 19.8 -44  3 23"))
				name = name + " A";
			if (name.equals("Q 2133-4428")  &&  coor_string.equals("21 37  1.0 -44 15 20"))
				name = name + " A";
			if (name.equals("Q 2134-4403")  &&  coor_string.equals("21 37 41.5 -43 49 34"))
				name = name + " A";
			if (name.equals("Q 2134-4403")  &&  coor_string.equals("21 37 59.1 -43 50  5"))
				name = name + " B";
			if (name.equals("Q 2135-4320")  &&  coor_string.equals("21 38 33.8 -43  6 34"))
				name = name + " A";
			if (name.equals("Q 2135-4320")  &&  coor_string.equals("21 38 53.5 -43  7 22"))
				name = name + " B";
			if (name.equals("Q 2137-4422")  &&  coor_string.equals("21 40 13.6 -44  9 18"))
				name = name + " A";
			if (name.equals("Q 2137-4422")  &&  coor_string.equals("21 40 42.5 -44  8 39"))
				name = name + " B";
			if (name.equals("Q 2138-4255")  &&  coor_string.equals("21 41 25.0 -42 42  3"))
				name = name + " A";
			if (name.equals("Q 2138-4416")  &&  coor_string.equals("21 42  7.9 -44  3 10"))
				name = name + " A";
			if (name.equals("Q 2139-4332")  &&  coor_string.equals("21 42 32.1 -43 18 22"))
				name = name + " A";
			if (name.equals("RX J23130+1049")  &&  coor_string.equals("23 13  3.1  10 49 10"))
				name = name + " A";
			if (name.equals("RX J23130+1049")  &&  coor_string.equals("23 13  3.8  10 49 15"))
				name = name + " B";
			if (name.equals("Q 0047-2633")  &&  coor_string.equals(" 0 50 12.1 -26 17 29"))
				name = name + " B";
			if (name.equals("Q 0056-2948")  &&  coor_string.equals(" 0 58 46.5 -29 31 54"))
				name = name + " B";
			if (name.equals("Q 0100-2848")  &&  coor_string.equals(" 1  2 53.8 -28 31 54"))
				name = name + " B";
			if (name.equals("0149-510")  &&  coor_string.equals(" 1 51 44.6 -50 50  8"))
				name = name + " B";
			if (name.equals("AO 0235+164")  &&  coor_string.equals(" 2 38 38.9  16 36 58"))
				name = name + " B";
			if (name.equals("ESO 428-G14")  &&  coor_string.equals(" 3 37  3.2 -25 14 59"))
				name = name + " A";
			if (name.equals("ESO 428-G14")  &&  coor_string.equals(" 7 16 31.2 -29 19 28"))
				name = name + " B";
			if (name.equals("Q 1039+047")  &&  coor_string.equals("10 42 16.9   4 31 47"))
				name = name + " B";
			if (name.equals("Q 1216-1103")  &&  coor_string.equals("12 19 11.3 -11 20 20"))
				name = name + " B";
			if (name.equals("1WGA J1335.2+3802")  &&  coor_string.equals("13 35 17.6  38  2 48"))
				name = name + " B";
			if (name.equals("Q 2131-4339")  &&  coor_string.equals("21 34 55.6 -43 25 47"))
				name = name + " C";
			if (name.equals("Q 2133-4416")  &&  coor_string.equals("21 36 14.8 -44  3  6"))
				name = name + " B";
			if (name.equals("Q 2133-4428")  &&  coor_string.equals("21 36 19.2 -44 14 58"))
				name = name + " B";
			if (name.equals("Q 2136-4420")  &&  coor_string.equals("21 39 42.7 -44  7 17"))
				name = name + " A";
			if (name.equals("Q 2136-4420")  &&  coor_string.equals("21 40  0.7 -44  7 10"))
				name = name + " B";
			if (name.equals("Q 2138-4416")  &&  coor_string.equals("21 41 44.0 -44  2 34"))
				name = name + " B";
			if (name.equals("Q 2138-4255")  &&  coor_string.equals("21 42  3.8 -42 41 47"))
				name = name + " B";
			if (name.equals("Q 2139-4332")  &&  coor_string.equals("21 42 10.9 -43 18 57"))
				name = name + " B";

			String z = record.substring(70, 75).trim();

			String mag = record.substring(82, 87).trim();
			String mag_system = "";
			if (mag.length() > 0) {
				mag_system = record.substring(81, 82);
				if (record.charAt(81) == ' ')
					mag_system = "V";
			}

			String b_v = record.substring(88, 93).trim();
			String u_b = record.substring(94, 99).trim();
			String absolute_mag = record.substring(100, 105).trim();

			VeronStar star = new VeronStar(name, coor_string, mag, mag_system, b_v, u_b, absolute_mag, z);

			if (center_coor != null) {
				ChartMapFunction cmf = new ChartMapFunction(center_coor, 1.0, 0.0);
				Position position = cmf.mapCoordinatesToXY(coor);
				star.setPosition(position);
			}

			return star;
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
		html += "Quasars and Active Galactic Nuclei (9th Ed.)<br>";
		html += "Astronomical Data Center catalog No. 7215<br>";
		html += "</p><p>";
		html += "Download:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/7/7215/table1.dat.gz</font></u><br>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/7/7215/table2.dat.gz</font></u><br>";
		html += "<u><font color=\"#0000ff\">ftp://dbc.nao.ac.jp/DBC/NASAADC/catalogs/7/7215/table3.dat.gz</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
