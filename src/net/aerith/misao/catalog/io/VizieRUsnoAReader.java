/*
 * @(#)VizieRUsnoAReader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.io;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.star.*;
import net.aerith.misao.io.Decoder;
import net.aerith.misao.io.CdromNotFoundException;

/**
 * The <code>VizieRUsnoAReader</code> is a class to read the query
 * result of the USNO-A1.0/2.0 catalog at the VizieR Service saved as
 * a text file.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 September 13
 */

public class VizieRUsnoAReader extends CatalogReader {
	/**
	 * The input stream reader.
	 */
	protected BufferedReader reader;

	/**
	 * The circum area to read stars.
	 */
	protected CircumArea circum_area;

	/**
	 * The version of the USNO-A catalog.
	 */
	protected int version = 1;

	/**
	 * Constructs an empty <code>VizieRUsnoAReader</code>.
	 */
	public VizieRUsnoAReader ( ) {
		url_set.ignoreCases(true);

		setDefaultURL();
	}

	/**
	 * Constructs a <code>VizieRUsnoAReader</code> with URL of the
	 * data file.
	 * @param url the URL of the data file.
	 */
	public VizieRUsnoAReader ( URL url ) {
		this();

		addURL(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "VizieR Service USNO-A1.0/2.0";
	}

	/**
	 * Checks if the catalog data is a file.
	 * @return true if the catalog data is a file.
	 */
	public boolean isFile ( ) {
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
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 5.0;
	}

	/**
	 * Gets the help message.
	 * @return the help message.
	 */
	public String getHelpMessage ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "The query result of the USNO-A1.0/2.0 catalog at the VizieR Service saved as a text file. ";
		html += "The URL is <u><font color=\"#0000ff\">http://vizier.u-strasbg.fr/cgi-bin/VizieR</font></u>. ";
		html += "</p><p>";
		html += "The format of the data list, which the system can read, is as follows. ";
		html += "</p>";
		html += "<p><table><tr><td bgcolor=\"#ffffff\"><pre>";
		html += "  ------------------------------------------------------------------------<br>";
		html += " Full     _r      _RAJ2000     _DEJ2000    USNO-A2.0       RA(ICRS)    DE(ICRS)  ACTflag Mflag  Bmag  Rmag    Epoch<br>";
		html += "       arcmin    \"h:m:s\"      \"d:m:s\"                        deg         deg                    mag   mag     yr<br>";
		html += "  ------------------------------------------------------------------------<br>";
		html += "    1   0.1261  17 52 00.488  -17 39 57.07  0675-23018108  268.002034  -17.665853                20.0  17.9  1980.488<br>";
		html += "    2   0.1487  17 51 59.649  -17 39 52.62  0675-23016137  267.998539  -17.664617                17.8  17.8  1980.488<br>";
		html += "    3   0.1643  17 51 59.497  -17 39 53.25  0675-23015767  267.997906  -17.664792                18.1  17.9  1980.488<br>";
		html += "</pre></td></tr></table></p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Opens a catalog. This method must be invoked at first.
	 * @param coor the R.A. and Decl. of the center.
	 * @param fov  the field of view to read in degree.
	 * @exception IOException if a file cannot be accessed.
	 * @exception FileNotFoundException if a file does not exists in
	 * any URL, and the file is in any CD-ROMs.
	 * @exception CdromNotFoundException if a file does not exists in
	 * any URL, and the file is in a CD-ROM.
	 */
	public void open ( Coor coor, double fov )
		throws IOException, FileNotFoundException, CdromNotFoundException
	{
		center_coor = coor;
		circum_area = new CircumArea(coor, fov / 2.0);

		// Initializes to read data one by one.
		URL url = url_set.exists();
		reader = Decoder.newReader(url);
	}

	/**
	 * Reads one data from the opened catalog. After this method is 
	 * invoked, the cursor is promoted to tne next star. When every 
	 * data is read, it returns null.
	 * @return a star data.
	 * @exception IOException if a file cannot be accessed.
	 * @exception FileNotFoundException if a file does not exists in
	 * any URL, and the file is in any CD-ROMs.
	 * @exception CdromNotFoundException if a file does not exists in
	 * any URL, and the file is in a CD-ROM.
	 * @exception QueryFailException if the query to the server is 
	 * failed.
	 */
	public CatalogStar readNext ( )
		throws IOException, FileNotFoundException, CdromNotFoundException, QueryFailException
	{
		if (reader == null)
			throw new IOException();

		while (true) {
			String line = reader.readLine();

			if (line == null)
				return null;

			if (line.indexOf("USNO-A1.0") >= 0)
				version = 1;
			if (line.indexOf("USNO-A2.0") >= 0)
				version = 2;

			if (line.length() >= 60) {
				boolean valid = true;
				for (int i = 0 ; i < line.length() - 1 ; i++) {
					if (('0' <= line.charAt(i)  &&  line.charAt(i) <= '9')  ||
						' ' == line.charAt(i) ||
						'+' == line.charAt(i) ||
						'-' == line.charAt(i) ||
						'.' == line.charAt(i)) {
					} else {
						valid = false;
						break;
					}
				}

				if (valid) {
					String ra_decl_str = "";
					String star_number_str = "";
					String Bmag_str = "";
					String Rmag_str = "";
					String epoch_str = "";

					try {
						StringTokenizer st = new StringTokenizer(line);

						st.nextToken();		// sequential numebr
						st.nextToken();		// r

						ra_decl_str = st.nextToken() + " " + st.nextToken() + " " + st.nextToken() + " " + st.nextToken() + " " + st.nextToken() + " " + st.nextToken();
						star_number_str = st.nextToken();

						st.nextToken();		// R.A. (ICRS)
						st.nextToken();		// Decl. (ICRS)

						// The ACTflag and Mflag are not always recorded.
						Bmag_str = st.nextToken();
						Rmag_str = st.nextToken();
						epoch_str = st.nextToken();
						while (st.hasMoreElements()) {
							Bmag_str = Rmag_str;
							Rmag_str = epoch_str;
							epoch_str = st.nextToken();
						}

						short file_number = Short.parseShort(star_number_str.substring(0, 4));
						int star_number = Integer.parseInt(star_number_str.substring(5));

						Coor coor = Coor.create(ra_decl_str);

						double B_mag = Double.parseDouble(Bmag_str);
						double R_mag = Double.parseDouble(Rmag_str);
						double V_mag = MagnitudeSystem.getUsnoVMag(R_mag, B_mag);

						short B_mag10 = (short)(B_mag * 10.0 + 0.5);
						short R_mag10 = (short)(R_mag * 10.0 + 0.5);

						if (circum_area.inArea(coor)  &&  (double)V_mag <= limiting_mag) {
							CatalogStar star = null;
							if (version == 2)
								star = new UsnoA20Star(file_number, star_number, coor, true, R_mag10, true, B_mag10, V_mag);
							else
								star = new UsnoA10Star(file_number, star_number, coor, true, R_mag10, true, B_mag10, V_mag);

							ChartMapFunction cmf = new ChartMapFunction(center_coor, 1.0, 0.0);
							Position position = cmf.mapCoordinatesToXY(coor);
							star.setPosition(position);

							return star;
						}
					} catch ( NoSuchElementException exception ) {
						// Not a data record. Never minds and goes to the next line.
					}
				}
			}
		}
	}

	/**
	 * Closes a catalog. This method must be invoked finally.
	 * @exception IOException if a file cannot be accessed.
	 */
	public void close ( )
		throws IOException
	{
		reader.close();
		reader = null;
	}
}
