/*
 * @(#)Encoder.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

/**
 * The <code>Encoder</code> is a class to encode a file in some 
 * compression or cryption. In general, it zips the file 
 * automatically when the file name ends with .gz.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 June 18
 */

public class Encoder {
	/**
	 * Creates a new output stream. When the file name ends with .gz, 
	 * it automatically zips.
	 * @param file the file.
	 * @return the output stream.
	 * @exception IOException if a file cannot be accessed.
	 */
	public final static DataOutputStream newOutputStream ( File file )
		throws IOException
	{
		mkdir(file);

		return new DataOutputStream(newOutputStream(file.getPath(), new FileOutputStream(file)));
	}

	/**
	 * Creates a new output stream. When the file name ends with .gz, 
	 * it automatically zips.
	 * @param url the URL.
	 * @return the output stream.
	 * @exception IOException if a file cannot be accessed.
	 */
	public final static DataOutputStream newOutputStream ( URL url )
		throws IOException
	{
		if (url.getProtocol().equals("file"))
			return newOutputStream(new File(url.getFile()));

		URLConnection uc = url.openConnection();
		uc.setDoOutput(true);

		return new DataOutputStream(newOutputStream(url.toString(), uc.getOutputStream()));
	}

	/**
	 * Creates a new writer. When the file name ends with .gz, it 
	 * automatically zips.
	 * @param file the file.
	 * @return the writer.
	 * @exception IOException if a file cannot be accessed.
	 */
	public final static PrintWriter newWriter ( File file )
		throws IOException
	{
		mkdir(file);

		return new PrintWriter(newOutputStream(file));
	}

	/**
	 * Creates a new writer. When the file name ends with .gz, it 
	 * automatically zips.
	 * @param url the URL.
	 * @return the writer.
	 * @exception IOException if a file cannot be accessed.
	 */
	public final static PrintWriter newWriter ( URL url )
		throws IOException
	{
		return new PrintWriter(newOutputStream(url));
	}

	/**
	 * Creates a directory to create the file to output.
	 * @param file the file to output.
	 * @exception IOException if a file cannot be accessed.
	 */
	private final static void mkdir ( File file )
		throws IOException
	{
		String directory_name = file.getAbsolutePath();

		int p = directory_name.lastIndexOf("/");
		int p2 = directory_name.lastIndexOf("\\");
		if (p < p2)
			p = p2;
		if (p >= 0) {
			directory_name = directory_name.substring(0, p);

			File directory = new File(directory_name);
			directory.mkdirs();
		}
	}

	/**
	 * Creates a new output stream. When the file name ends with .gz, 
	 * it automatically zips.
	 * @param name   the name of the file or URL.
	 * @param stream the primitive output stream.
	 * @return the output stream.
	 * @exception IOException if a file cannot be accessed.
	 */
	private final static OutputStream newOutputStream ( String name, OutputStream stream )
		throws IOException
	{
		if (FileManager.endsWith(name, ".gz"))
			return new GZIPOutputStream(stream);

		if (FileManager.endsWith(name, ".zip")) {
			ZipOutputStream zip_stream = new ZipOutputStream(stream);

			ZipEntry zip_entry = new ZipEntry(name);

			zip_stream.putNextEntry(zip_entry);

			return zip_stream;
		}

		return stream;
	}
}
