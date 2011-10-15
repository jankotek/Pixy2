/*
 * @(#)FileManager.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.database.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.io.*;
import net.aerith.misao.util.Coor;

/**
 * The <code>FileManager</code> is a class which consists of static 
 * methods to search or copy files.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 6
 */

public class FileManager {
	/**
	 * The home directory.
	 */
	private File home_directory = null;

	/**
	 * Constructs a <code>FileManager</code>.
	 */
	public FileManager ( ) {
	}

	/**
	 * Constructs a <code>FileManager</code> at the specified 
	 * directory.
	 * @param directory the home directory.
	 */
	public FileManager ( File directory ) {
		home_directory = directory;

		if (net.aerith.misao.pixy.Properties.getHome().compareTo(home_directory) == 0)
			home_directory = null;
	}

	/**
	 * Searches the specified file of relative path from the specified
	 * directory.
	 * @param directory the directory to search the file.
	 * @param file      the file to search.
	 * @return the file object.
	 * @exception FileNotFoundException if the specified file cannot 
	 * be found in the specified directory.
	 */
	public final static File find ( File directory, File file )
		throws FileNotFoundException
	{
		String path = unitePath(directory.getAbsolutePath(), file.getPath());

		File new_file = new File(path);
		if (new_file.exists())
			return new_file;

		throw new FileNotFoundException(path);
	}

	/**
	 * Copies the file.
	 * @param src_file the file to copy from.
	 * @param dst_file the file to copy to.
	 * @exception IOException if a file cannot be accessed.
	 */
	public final static void copy ( File src_file, File dst_file )
		throws IOException
	{
		byte[] b = new byte[1024 * 1024];

		File directory = new File(dst_file.getAbsolutePath()).getParentFile();
		if (directory.exists() == false)
			directory.mkdirs();

		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(src_file)));
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dst_file)));

		long file_size = src_file.length();

		try {
			while (true) {
				int bytes = 1024 * 1024;
				if (file_size < bytes)
					bytes = (int)file_size;

				if (bytes == 0)
					break;

				in.readFully(b, 0, bytes);
				out.write(b, 0, bytes);

				file_size -= bytes;
			}
		} catch ( EOFException exception ) {
		}

		out.flush();

		in.close();
		out.close();
	}

	/**
	 * Converts the absolute file path into the relative path from the
	 * specified file if possible.
	 * @param path the absolute path.
	 * @param file the file to relativate the path from.
	 * @return the relativated path.
	 */
	public final static String relativatePathFrom ( String path, File file ) {
		File path_file = new File(path);
		if (path_file.getPath().length() > file.getParent().length()) {
			String leading_path = path_file.getPath().substring(0, file.getParent().length());
			File leading_file = new File(leading_path);
			if (leading_file.compareTo(file.getParentFile()) == 0) {
				path = path_file.getPath().substring(file.getParent().length());
				if (path.charAt(0) == File.separatorChar)
					path = path.substring(1);
			}
		}
		return path;
	}

	/**
	 * Converts the absolute file path into the relative path from the
	 * specified directory if possible.
	 * @param path      the absolute path.
	 * @param directory the directory to relativate the path from.
	 * @return the relativated path.
	 */
	public final static String relativatePathFromDirectory ( String path, File directory ) {
		File path_file = new File(path);
		path = path_file.getAbsolutePath();

		String path_directory = directory.getAbsolutePath();
		if (path.indexOf(path_directory) == 0) {
			path = path.substring(path_directory.length());
			if (path.charAt(0) == File.separatorChar)
				path = path.substring(1);
		}

		return path;
	}

	/**
	 * Unites the two paths.
	 * @param path1 the first path.
	 * @param path2 the second path.
	 * @return the united path.
	 */
	public final static String unitePath ( String path1, String path2 ) {
		String new_path = "";

		if (path1.charAt(path1.length() - 1) == File.separatorChar)
			new_path += path1;
		else
			new_path += path1 + File.separator;

		if (path2.charAt(0) == File.separatorChar)
			new_path += path2.substring(1);
		else
			new_path += path2;

		return new_path;
	}

	/**
	 * Checks if the specified file name ends with the specified 
	 * extension.
	 * @param filename  the filename.
	 * @param extension the extension including the period.
	 * @return true if the specified file name ends with the specified 
	 * extension.
	 */
	public final static boolean endsWith ( String filename, String extension ) {
		int length = filename.length();
		int length_ext = extension.length();
		if (length >= length_ext) {
			if (filename.substring(length - length_ext).equalsIgnoreCase(extension))
				return true;
		}
		return false;
	}

	/**
	 * Creates a new file object for the specified relative path in
	 * the home directory.
	 * <p>
	 * If the file does not exists, it searches a file with ".gz" 
	 * suffix, or a file without ".gz" suffix when it is already 
	 * attached.
	 * @param path the path of a file.
	 * @return the file object.
	 */
	public File newFile ( String path ) {
		if (home_directory != null)
			path = unitePath(home_directory.getAbsolutePath(), path);

		return Decoder.newFile(path);
	}

	/**
	 * Reads the XML report document recorded in the specified 
	 * XML information element.
	 * @param info the XML information element.
	 * @return the XML report document.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlReport readReport ( XmlInformation info )
		throws IOException
	{
		if (info.getPath() == null)
			throw new IOException();

		XmlReport report = new XmlReport();
		report.read(newFile(info.getPath()));
		return report;
	}

	/**
	 * Reads the XML report document recorded in the specified 
	 * XML information element with only star data around the 
	 * specified R.A. and Decl. 
	 * @param info   the XML information element.
	 * @param coor   the R.A. and Decl.
	 * @param radius the radius in degree.
	 * @return the XML report document.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlReport readReport ( XmlInformation info, Coor coor, double radius )
		throws IOException
	{
		if (info.getPath() == null)
			throw new IOException();

		File file = newFile(info.getPath());

		BufferedReader in = Decoder.newReader(file);
		XmlReport report = XmlReport.read(in, coor, radius);
		report.getInformation().setPath(file.getPath());
		return report;
	}

	/**
	 * Reads the XML report document recorded in the specified 
	 * magnitude record.
	 * @param mag_record the magnitude record.
	 * @return the XML report document.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlReport readReport ( XmlMagRecord mag_record )
		throws IOException
	{
		XmlReport report = new XmlReport();
		report.read(newFile(mag_record.getImageXmlPath()));
		return report;
	}

	/**
	 * Reads the XML information document recorded in the specified 
	 * magnitude record.
	 * @param mag_record the magnitude record.
	 * @return the XML information document.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlInformation readInformation ( XmlMagRecord mag_record )
		throws IOException
	{
		return XmlReport.readInformation(newFile(mag_record.getImageXmlPath()));
	}

	/**
	 * Reads the XML information document recorded in the specified 
	 * magnitude record, from the XML report document file or 
	 * specified information database.
	 * @param mag_record the magnitude record.
	 * @param db_manager the information database manager.
	 * @return the XML information document.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlInformation readInformation ( XmlMagRecord mag_record, InformationDBManager db_manager )
		throws IOException
	{
		XmlInformation info = null;
		try {
			info = readInformation(mag_record);
		} catch ( Exception exception ) {
			if (db_manager != null)
				info = db_manager.getElement(mag_record.getImageXmlPath());
		}

		if (info == null)
			throw new IOException(mag_record.getImageXmlPath());

		return info;
	}

	/**
	 * Reads the star element in the XML report document with the
	 * specified name.
	 * @param info   the XML information element.
	 * @param name   the name.
	 * @return the star element.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlStar readStar ( XmlInformation info, String name )
		throws IOException
	{
		if (info.getPath() == null)
			throw new IOException();

		BufferedReader in = Decoder.newReader(newFile(info.getPath()));
		XmlStar star = XmlReport.readStar(in, name);
		return star;
	}

	/**
	 * Creates the image file format object.
	 * @param image the XML image element.
	 * @return the image file format object.
	 * @exception FileNotFoundException if the file does not exist.
	 * @exception MalformedURLException if an unknown protocol is 
	 * specified.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	public Format createFileFormat ( XmlImage image )
		throws FileNotFoundException, MalformedURLException, UnsupportedFileTypeException
	{
		File file = newFile(image.getContent());
		return Format.create(file, image.getFormat());
	}
}
