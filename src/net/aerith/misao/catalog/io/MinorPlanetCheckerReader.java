/*
 * @(#)MinorPlanetCheckerReader.java
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
import net.aerith.misao.io.CdromNotFoundException;

/**
 * The <code>MinorPlanetCheckerReader</code> is a class to send 
 * queries to the Minor Planet Checker server, receive the results
 * and read them.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 May 19
 */

public class MinorPlanetCheckerReader extends CatalogReader {
	/**
	 * The input stream reader.
	 */
	protected BufferedReader reader = null;

	/**
	 * The radius to search in arcmin.
	 */
	protected int radius_arcmin = 0;

	/**
	 * The list of search areas.
	 */
	protected Vector map_list;

	/**
	 * The hash table to avoid duplication.
	 */
	protected Hashtable hash;

	/**
	 * The current number of search areas.
	 */
	protected int current_area = -1;

	/**
	 * True if the comets and asteroids data part is being read.
	 */
	protected boolean body_flag = false;

	/**
	 * Constructs an empty <code>MinorPlanetCheckerReader</code>.
	 */
	public MinorPlanetCheckerReader ( ) {
		super();
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Minor Planet Checker";
	}

	/**
	 * Checks if the catalog depends on the date.
	 * @return true if the catalog depends on the date.
	 */
	public boolean isDateDependent ( ) {
		return true;
	}

	/**
	 * Returns true if the reader has limit on the field of view.
	 * @return true if the reader has limit on the field of view.
	 */
	public boolean hasFovLimit ( ) {
		return true;
	}

	/**
	 * Gets the limit on the field of view.
	 * @return the limit on the field of view in degree.
	 */
	public double getFovLimit ( ) {
		return 15.0;
	}

	/**
	 * Gets the message for limit on the field of view.
	 * @return the message for limit on the field of view.
	 */
	public String getFovLimitMessage ( ) {
		return "15 degree";
	}

	/**
	 * Returns true if the reader has limit on the date.
	 * @return true if the reader has limit on the date.
	 */
	public boolean hasDateLimit ( ) {
		return true;
	}

	/**
	 * Gets the limit on the date.
	 * @return the limit on the date in day.
	 */
	public double getDateLimit ( ) {
		return 365 * 4;
	}

	/**
	 * Gets the message for limit on the date.
	 * @return the message for limit on the date.
	 */
	public String getDateLimitMessage ( ) {
		return "2 years";
	}

	/**
	 * Gets the maximum error of position in arcsec. It is the search
	 * area size to identify with other stars.
	 * @return the maximum error of position in arcsec.
	 */
	public double getMaximumPositionErrorInArcsec ( ) {
		return 60.0;
	}

	/**
	 * Gets the help message.
	 * @return the help message.
	 */
	public String getHelpMessage ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "The Minor Planet Checker service at ";
		html += "<u><font color=\"#0000ff\">http://scully.harvard.edu/~cgi/CheckMP</font></u>. ";
		html += "</p><p>";
		html += "The system sends queries to the Minor Planet Checker server, ";
		html += "receives the results and reads them. ";
		html += "</p><p>";
		html += "Note:";
		html += "<ul>";
		html += "<li>The field of view must be less than 15 deg. ";
		html += "<li>The image date must be within 2 years. ";
		html += "</ul>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Checkes if the specified target string is starting with the 
	 * specified key string.
	 * @param target_string the string to check if starting with the
	 * specified key string.
	 * @param key_string    the string to check if the target string
	 * is starting with.
	 * @return true if the specified target string is starting with
	 * the specified key string.
	 */
	protected static boolean startsWith ( String target_string, String key_string ) {
		if (target_string.length() < key_string.length())
			return false;

		if (target_string.substring(0, key_string.length()).equals(key_string))
			return true;

		return false;
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

		Position top_left = new Position(- fov / 2.0, - fov / 2.0);
		Position bottom_right = new Position(fov / 2.0, fov / 2.0);
		PositionMap map = new PositionMap(top_left, bottom_right);
		map.divideByCircleCoverage(1.0);

		map_list = map.getDividedPositionMapList();

		map = (PositionMap)map_list.elementAt(0);
		radius_arcmin = (int)(Math.sqrt(map.getWidth() * map.getWidth() + map.getHeight() * map.getHeight()) / 2.0 * 60.0) + 1;
		if (radius_arcmin > 300)
			radius_arcmin = 300;

		hash = new Hashtable();

		current_area = -1;
		body_flag = false;
		reader = null;
	}

	/**
	 * Reads one data from the opened catalog. After this method is 
	 * invoked, the cursor is promoted to tne next star. When every 
	 * data is read, it returns null. Note that data with the same ID
	 * are also returned one by one.
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
		if (map_list == null)
			return null;

		while (true) {
			if (reader == null) {
				current_area++;
				if (current_area == map_list.size())
					return null;

				PositionMap map = (PositionMap)map_list.elementAt(current_area);
				ChartMapFunction cmf = new ChartMapFunction(center_coor, 1.0, 0.0);
				Coor coor = cmf.mapXYToCoordinates(map.getCenter());

				try {
					URL url = new URL("http://scully.harvard.edu/~cgi/MPCheck.COM");
					URLConnection uc = url.openConnection();
					uc.setDoOutput(true);

					String ra_decl = coor.getOutputStringTo100mArcsecWithoutUnit();
					StringTokenizer st = new StringTokenizer(ra_decl);
					String ra = st.nextToken() + " " + st.nextToken() + " " + st.nextToken();
					String decl = st.nextToken() + " " + st.nextToken() + " " + st.nextToken();
					ra = ra.replace(' ', '+');
					if (decl.charAt(0) == '+')
						decl = "%2B" + decl.substring(1);
					decl = decl.replace(' ', '+');

					String query = "year=" + date.getYear();
					query += "&month=" + date.getMonth();
					query += "&day=" +  Format.formatDouble(date.getDecimalDay(), 5, 2).trim();
					query += "&which=pos";
					query += "&ra=" + ra;
					query += "&decl=" + decl;
					query += "&TextArea=";
					query += "&radius=" + radius_arcmin;
					query += "&limit=30";
					query += "&oc=999&sort=d&mot=h&tmot=s&needed=f&ps=n&type=p";

					OutputStream os = uc.getOutputStream();
					PrintStream ps = new PrintStream(os);
					ps.print(query);
					ps.close();

					reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
				} catch ( MalformedURLException exception ) {
					System.err.println(exception);
					throw new IOException();
				}
			}

			String record = reader.readLine();

			if (record == null)
				throw new QueryFailException();

			if (startsWith(record, " Object designation         R.A.      Decl.")) {
				body_flag = true;
			} else if (startsWith(record, " </pre>")) {
				if (body_flag == false)
					throw new QueryFailException();

				body_flag = false;
				reader.close();
				reader = null;
			} else if (startsWith(record, "No known minor planets")) {
				body_flag = false;
				reader.close();
				reader = null;
			} else if (startsWith(record, "<title>Error")) {
				throw new QueryFailException();
			} else if (body_flag) {
				if (record.length() >= 81) {
					String name = record.substring(0, 25).trim();
					if (name.length() > 0) {
						while (true) {
							int p = name.indexOf("  ");
							if (p >= 0) {
								name = name.substring(0, p) + name.substring(p+1);
							} else {
								break;
							}
						}

						if (hash.get(name) == null) {
							String ra = record.substring(25, 35);
							String decl = record.substring(36, 45);
							float mag = Float.parseFloat(record.substring(47, 51).trim());
							float motion_ra = Float.parseFloat(record.substring(66, 71).trim());
							if (record.charAt(71) == '-')
								motion_ra = - motion_ra;
							float motion_decl = Float.parseFloat(record.substring(73, 78).trim());
							if (record.charAt(78) == '-')
								motion_decl = - motion_decl;
							String orbit = record.substring(80).trim();
							if (record.length() >= 85)
								orbit = record.substring(80, 85).trim();

							Coor coor = Coor.create(ra + " " + decl);

							MinorPlanetCheckerStar star = new MinorPlanetCheckerStar(name, coor, mag, motion_ra, motion_decl, orbit);

							ChartMapFunction cmf = new ChartMapFunction(center_coor, 1.0, 0.0);
							Position position = cmf.mapCoordinatesToXY(coor);
							star.setPosition(position);

							hash.put(name, star);

							return star;
						}
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
		if (reader != null)
			reader.close();
		reader = null;
	}
}
