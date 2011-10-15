/*
 * @(#)Decoder.java
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
 * The <code>Decoder</code> is a class to decode a file in some 
 * compression or cryption. In general, it unzips the file 
 * automatically when the file name ends with .gz.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 April 20
 */

public class Decoder {
	/**
	 * Returns the list of supported file extensions of compressed 
	 * files.
	 * @return the list of supported file extensions of compressed 
	 * files.
	 */
	public final static String[] getCompressedExtensions ( ) {
		String[] exts = new String[2];
		exts[0] = "gz";
		exts[1] = "zip";
		return exts;
	}

	/**
	 * Creates a new file object for the specified path. If the file
	 * does not exists, it searches a file with ".gz" suffix, or a 
	 * file without ".gz" suffix when it is already attached.
	 * @param path the path of a file.
	 * @return the file object.
	 */
	public final static File newFile ( String path ) {
		File file = new File(path);
		if (file.exists())
			return file;

		file = new File(path + ".gz");
		if (file.exists())
			return file;
		file = new File(path + ".GZ");
		if (file.exists())
			return file;

		if (FileManager.endsWith(path, ".gz")) {
			String path2 = path.substring(0, path.length() - 3);
			file = new File(path2);
			if (file.exists())
				return file;
		}

		return new File(path);
	}

	/**
	 * Creates a new input stream. When the file name ends with .gz, 
	 * it automatically unzips.
	 * @param file the file.
	 * @return the input stream.
	 * @exception IOException if a file cannot be accessed.
	 * @exception FileNotFoundException if a file does not exists.
	 */
	public final static DataInputStream newInputStream ( File file )
		throws IOException, FileNotFoundException
	{
		return new DataInputStream(new BufferedInputStream(newInputStream(file.getPath(), new FileInputStream(file))));
	}

	/**
	 * Creates a new reader. When the file name ends with .gz, it 
	 * automatically unzips.
	 * @param file the file.
	 * @return the reader.
	 * @exception IOException if a file cannot be accessed.
	 * @exception FileNotFoundException if a file does not exists.
	 */
	public final static BufferedReader newReader ( File file )
		throws IOException, FileNotFoundException
	{
		return new BufferedReader(new InputStreamReader(newInputStream(file.getPath(), new FileInputStream(file))));
	}

	/**
	 * Creates a new input stream. When the file name ends with .gz, 
	 * it automatically unzips.
	 * @param url the URL.
	 * @return the input stream.
	 * @exception IOException if a file cannot be accessed.
	 */
	public final static DataInputStream newInputStream ( URL url )
		throws IOException
	{
		return new DataInputStream(new BufferedInputStream(newInputStream(url.toString(), url.openStream())));
	}

	/**
	 * Creates a new reader. When the file name ends with .gz, it 
	 * automatically unzips.
	 * @param url the URL.
	 * @return the reader.
	 * @exception IOException if a file cannot be accessed.
	 */
	public final static BufferedReader newReader ( URL url )
		throws IOException
	{
		return new BufferedReader(new InputStreamReader(newInputStream(url.toString(), url.openStream())));
	}

	/**
	 * Creates a new input stream. When the file name ends with .gz, 
	 * it automatically unzips.
	 * @param name   the name of the file or URL.
	 * @param stream the primitive input stream.
	 * @return the input stream.
	 * @exception IOException if a file cannot be accessed.
	 */
	private final static InputStream newInputStream ( String name, InputStream stream )
		throws IOException
	{
		if (FileManager.endsWith(name, ".gz"))
			return new GZIPInputStream(stream);

		if (FileManager.endsWith(name, ".zip")) {
			ZipInputStream zip_stream = new ZipInputStream(stream);

			ZipEntry zip_entry = zip_stream.getNextEntry();

			return zip_stream;
		}

		return stream;
	}
}
