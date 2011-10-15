/*
 * @(#)URLSet.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.io.*;
import java.net.*;
import java.util.Vector;

/**
 * The <code>URLSet</code> represents a set of URLs. It has functions
 * such as to check existence of files in any directory in this set.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 June 3
 */

public class URLSet {
	/**
	 * The list of URLs.
	 */
	protected Vector list;

	/**
	 * True when to ignore cases.
	 */
	protected boolean ignore_cases = false;

	/**
	 * True when to accept the gzipped files.
	 */
	protected boolean accept_gzipped = true;

	/**
	 * Constructs an empty <code>URLSet</code>.
	 */
	public URLSet ( ) {
		list = new Vector();
	}

	/**
	 * Adds an URL.
	 * @param new_url the new URL to add.
	 */
	public void addURL ( URL new_url ) {
		list.addElement(new_url);
	}

	/**
	 * True if no URL is set.
	 * @return true if no URL is set.
	 */
	public boolean empty ( ) {
		return (list.size() == 0);
	}

	/**
	 * Sets the flag to ignore cases when checks the existence of file.
	 * @param flag true when to ignore cases.
	 */
	public void ignoreCases ( boolean flag ) {
		ignore_cases = flag;
	}

	/**
	 * Sets the flag to accept gzipped files when checks the existence
	 * of file.
	 * @param flag true when to accept gzipped files.
	 */
	public void acceptGzipped ( boolean flag ) {
		accept_gzipped = flag;
	}

	/**
	 * Checks if any of the URLs in this set exists. If exists, it
	 * returns the URL of the file.
	 * @return the URL of the file if exists.
	 * @exception FileNotFoundException if any of the URLs in this set
	 * does not exist.
	 */
	public URL exists ( )
		throws FileNotFoundException
	{
		for (int i = 0 ; i < list.size() ; i++) {
			URL url = (URL)list.elementAt(i);

			try {
				InputStream stream = url.openStream();
				stream.close();
				return url;
			} catch ( IOException exception ) {
			}

			if (accept_gzipped) {
				try {
					url = existsGzipped(url);
					return url;
				} catch ( IOException exception ) {
				}
			}
		}

		throw new FileNotFoundException();
	}

	/**
	 * Checks if the specified file exists in any directory in this 
	 * set. If exists, it returns the URL of the file.
	 * @param filename the file name to check.
	 * @return the URL of the file if exists.
	 * @exception FileNotFoundException if the specified file does not
	 * exist in any directory in this set.
	 */
	public URL existsFile ( String filename )
		throws FileNotFoundException
	{
		for (int i = 0 ; i < list.size() ; i++) {
			URL url = (URL)list.elementAt(i);

			String url_string = url.toExternalForm();
			if (url_string.charAt(url_string.length() - 1) != '/')
				url_string += "/";

			try {
				url = new URL(url_string + filename);
				InputStream stream = url.openStream();
				stream.close();
				return url;
			} catch ( IOException exception ) {
			}

			if (accept_gzipped) {
				try {
					url = existsGzipped(url);
					return url;
				} catch ( IOException exception ) {
				}
			}

			if (ignore_cases) {
				try {
					url = new URL(url_string + filename.toLowerCase());
					InputStream stream = url.openStream();
					stream.close();
					return url;
				} catch ( IOException exception ) {
				}

				if (accept_gzipped) {
					try {
						url = existsGzipped(url);
						return url;
					} catch ( IOException exception ) {
					}
				}

				try {
					url = new URL(url_string + filename.toUpperCase());
					InputStream stream = url.openStream();
					stream.close();
					return url;
				} catch ( IOException exception ) {
				}

				if (accept_gzipped) {
					try {
						url = existsGzipped(url);
						return url;
					} catch ( IOException exception ) {
					}
				}
			}
		}

		throw new FileNotFoundException(filename);
	}

	/**
	 * Checks if the gzipped file of the specified URL exists. If
	 * exists, it returns the URL of the gzipped file.
	 * @param url the URL to check.
	 * @return the URL of the gzipped file if exists.
	 * @exception FileNotFoundException if the gzipped file of the 
	 * specified URL does not exist.
	 */
	private URL existsGzipped ( URL url )
		throws FileNotFoundException
	{
		String url_string = url.toExternalForm();

		try {
			url = new URL(url_string + ".gz");
			InputStream stream = url.openStream();
			stream.close();
			return url;
		} catch ( IOException exception ) {
		}

		try {
			url = new URL(url_string + ".GZ");
			InputStream stream = url.openStream();
			stream.close();
			return url;
		} catch ( IOException exception ) {
		}

		throw new FileNotFoundException(url_string);
	}
}
