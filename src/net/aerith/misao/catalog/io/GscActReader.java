/*
 * @(#)GscActReader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog.io;
import java.io.*;
import java.net.*;

/**
 * The <code>GscActReader</code> is a class to read GSC-ACT CD-ROMs.
 * <p>
 * The (x,y) position is also set properly so that (0,0) represents
 * the specified R.A. and Decl. to <tt>open</tt> method and (1,1) 
 * represents the position 1 deg to the west and 1 deg to the north.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 November 7
 */

public class GscActReader extends Gsc11Reader {
	/**
	 * Constructs an empty <code>GscActReader</code>.
	 */
	public GscActReader ( ) {
		super();
	}

	/**
	 * Constructs a <code>GscActReader</code> with URL of directory 
	 * containing GSC-ACT CD-ROMs data.
	 * @param url the URL of directory containing CD-ROMs data.
	 */
	public GscActReader ( URL url ) {
		super(url);
	}

	/**
	 * Gets the catalog name. It must be unique among all subclasses.
	 * @return the catalog name.
	 */
	public String getName ( ) {
		return "Guide Star Catalog GSC-ACT";
	}

	/**
	 * Checks if reading GSC-ACT or not.
	 * @return true if reading GSC-ACT.
	 */
	public boolean isAct ( ) {
		return true;
	}

	/**
	 * Gets the help message.
	 * @return the help message.
	 */
	public String getHelpMessage ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "HST Guide Star Catalog, Version GSC-ACT<br>";
		html += "Astronomical Data Center catalog No. 1255<br>";
		html += "</p><p>";
		html += "The set of 2 CD-ROMs. <br>";
		html += "</p><p>";
		html += "References:";
		html += "<blockquote>";
		html += "<u><font color=\"#0000ff\">http://www.projectpluto.com/gsc_act.htm</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}
}
