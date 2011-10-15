/*
 * @(#)Properties.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy;
import java.io.*;
import java.net.*;

/**
 * The <code>Properties</code> consists of accessor methods to the 
 * system properties on the PIXY System.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 April 20
 */

public class Properties {
	/**
	 * Gets the path of the MISAO home directory.
	 * @return the path of the MISAO home directory.
	 */
	public static File getHome ( ) {
		String path = System.getProperties().getProperty("misao.home");
		if (path != null)
			return new File(path);

		return new File(new File("dummy").getAbsolutePath()).getParentFile();
	}

	/**
	 * Gets the database directory name.
	 * @return the database directory name.
	 */
	public static String getDatabaseDirectoryName ( ) {
		String name = System.getProperties().getProperty("misao.database");
		if (name == null)
			name = "database";
		return name;
	}

	/**
	 * Gets the directory of the plugin class files.
	 * @return the directory of the plugin class files.
	 */
	public static File getPluginDirectory ( ) {
		String name = System.getProperties().getProperty("misao.plugin");
		if (name != null)
			return new File(name);

		return null;
	}
}
