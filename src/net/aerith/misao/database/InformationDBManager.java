/*
 * @(#)InformationDBManager.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import java.io.File;
import java.io.IOException;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.io.*;
import net.aerith.misao.xml.*;

/**
 * The <code>InformationDBManager</code> represents a database manager 
 * of the image information.
 * <p>
 * The database consists of three parts with different classification
 * policies. One is classified by XML file path. Another one is by 
 * image date. And the other one is by the limiting magnitude, the 
 * field of view, and the R.A. and Decl. of the image center.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 February 25
 */

public class InformationDBManager {
	/**
	 * The file system of the database.
	 */
	protected FileSystem file_system;

	/**
	 * The file manager.
	 */
	protected FileManager file_manager;

	/**
	 * Constructs an <code>InformationDBManager</code> in the 
	 * specified file system.
	 * @param file_system  the file system to create the database.
	 * @param file_manager the file manager.
	 */
	public InformationDBManager ( FileSystem file_system, FileManager file_manager ) {
		this.file_system = file_system;
		this.file_manager = file_manager;
	}

	/**
	 * Sets a file manager.
	 * @param file_manager the file manager.
	 */
	public void setFileManager ( FileManager file_manager ) {
		this.file_manager = file_manager;
	}

	/**
	 * Gets the top folder of the database classified by the XML file
	 * path.
	 * @return the top folder of the database classified by the XML
	 * file path.
	 */
	protected String getPathOrientedDBFolder ( ) {
		return "info.path";
	}

	/**
	 * Gets the top folder of the database classified by the image 
	 * date.
	 * @return the top folder of the database classified by the image 
	 * date
	 */
	protected String getDateOrientedDBFolder ( ) {
		return "info.date";
	}

	/**
	 * Gets the top folder of the database classified by the field.
	 * @return the top folder of the database classified by the field.
	 */
	protected String getFieldOrientedDBFolder ( ) {
		return "info.field";
	}

	/**
	 * Gets the relative path of the XML file.
	 * @param file the XML file.
	 * @return the relative path of the XML file.
	 */
	public String getPath ( File file ) {
		File home = net.aerith.misao.pixy.Properties.getHome();
		String path = file.getAbsolutePath();
		if (path.length() > home.getAbsolutePath().length()) {
			String leading_path = path.substring(0, home.getAbsolutePath().length());
			File leading_path_file = new File(leading_path);
			if (leading_path_file.compareTo(home) == 0) {
				path = path.substring(home.getAbsolutePath().length());
				if (path.charAt(0) == File.separatorChar)
					path = path.substring(1);
			}
		}

		return path;
	}

	/**
	 * Adds the image information.
	 * @param xml_file the XML file which contains the specified 
	 * information.
	 * @param info     the image information to add to the database.
	 * @exception IOException if I/O error occurs.
	 * @exception DocumentIncompleteException if some required data in
	 * the specified image information is not recorded.
	 * @exception DuplicatedException if the specified data is already
	 * in the database.
	 */
	public void addElement ( File xml_file, XmlInformation info )
		throws IOException, DocumentIncompleteException, DuplicatedException
	{
		if (info.getMidDate() == null)
			throw new DocumentIncompleteException("date");

		// Gets the relative path of the XML file.
		String xml_path = getPath(xml_file);

		// Checks if the specified image informatin is already in the database.
		XmlInformation duplicated_info = getElement(xml_path);
		if (duplicated_info != null)
			throw new DuplicatedException(duplicated_info);

		// Records the relative path of the XML file.
		info.setPath(xml_path);

		// Adds to the database classified by the XML file path.
		Folder db_folder = createPathOrientedDBFolder(xml_path);
		PrimitiveManager manager = file_system.getPrimitiveManager(db_folder, new XmlInformationHolder(), new XmlInformation());
		manager.addElement(info);

		// Adds to the database classified by the image date.
		db_folder = createDateOrientedDBFolder(info.getMidDate());
		manager = file_system.getPrimitiveManager(db_folder, new XmlInformationHolder(), new XmlInformation());
		manager.addElement(info);

		// Adds to the database classified by the field.
		double limit_mag = info.getLimitingMag();
		double fov_radius = info.getFieldRadiusInDegree();
		Coor center_coor = ((XmlCenter)info.getCenter()).getCoor();
		db_folder = createFieldOrientedDBFolder(limit_mag, fov_radius, center_coor);
		manager = file_system.getPrimitiveManager(db_folder, new XmlInformationHolder(), new XmlInformation());
		manager.addElement(info);
	}

	/**
	 * Deletes the image information.
	 * @param xml_path the XML file path.
	 * @return the deleted image information, or null if the image
	 * information represented by the specified file does not exist.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlInformation deleteElement ( String xml_path )
		throws IOException
	{
		// Deletes from the database classified by the XML file path.
		Folder db_folder = createPathOrientedDBFolder(xml_path);
		PrimitiveManager manager = file_system.getPrimitiveManager(db_folder, new XmlInformationHolder(), new XmlInformation());
		XmlInformation info = (XmlInformation)manager.deleteElement(xml_path);

		if (info == null)
			return null;

		// Deletes from the database classified by the image date.
		db_folder = createDateOrientedDBFolder(info.getMidDate());
		manager = file_system.getPrimitiveManager(db_folder, new XmlInformationHolder(), new XmlInformation());
		manager.deleteElement(xml_path);

		// Deletes from the database classified by the field.
		double limit_mag = info.getLimitingMag();
		double fov_radius = info.getFieldRadiusInDegree();
		Coor center_coor = ((XmlCenter)info.getCenter()).getCoor();
		db_folder = createFieldOrientedDBFolder(limit_mag, fov_radius, center_coor);
		manager = file_system.getPrimitiveManager(db_folder, new XmlInformationHolder(), new XmlInformation());
		manager.deleteElement(xml_path);

		return info;
	}

	/**
	 * Gets the image information of the specified XML file path.
	 * @param xml_path the XML file path.
	 * @return the image information.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlInformation getElement ( String xml_path )
		throws IOException
	{
		// Adds to the database classified by the XML file path.
		Folder db_folder = createPathOrientedDBFolder(xml_path);
		PrimitiveManager manager = file_system.getPrimitiveManager(db_folder, new XmlInformationHolder(), new XmlInformation());
		return (XmlInformation)manager.getElement(xml_path);
	}

	/**
	 * Creates the database folder classified by the XML file path.
	 * @param xml_path the path of the XML file.
	 * @return the database folder.
	 * @exception IOException if I/O error occurs.
	 */
	protected Folder createPathOrientedDBFolder ( String xml_path )
		throws IOException
	{
		Vector folder_list = new Vector();
		folder_list.addElement(getPathOrientedDBFolder());

		StringTokenizer st = new StringTokenizer(xml_path, "/\\:");
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			if (s.length() > 0)
				folder_list.addElement(s);
		}

		// Excludes the file name itself.
		Vector folder_list2 = new Vector();
		for (int i = 0 ; i < folder_list.size() - 1 ; i++)
			folder_list2.addElement(folder_list.elementAt(i));

		return file_system.createHierarchicalFolder(folder_list2);
	}

	/**
	 * Creates the database folder classified by the image date.
	 * @param jd the image data.
	 * @return the database folder.
	 * @exception IOException if I/O error occurs.
	 */
	protected Folder createDateOrientedDBFolder ( JulianDay jd )
		throws IOException
	{
		Vector folder_list = new Vector();
		folder_list.addElement(getDateOrientedDBFolder());

		folder_list.addElement(String.valueOf(jd.getYear()));
		folder_list.addElement(String.valueOf(jd.getMonth()));
		folder_list.addElement(String.valueOf(jd.getDay()));

		return file_system.createHierarchicalFolder(folder_list);
	}

	/**
	 * Creates the database folder classified by the field.
	 * @param limit_mag    the limiting magnitude.
	 * @param field_radius the field radius in degree.
	 * @param center_coor  the R.A. and Decl. of the center.
	 * @return the database folder.
	 * @exception IOException if I/O error occurs.
	 */
	protected Folder createFieldOrientedDBFolder ( double limit_mag, double field_radius, Coor center_coor )
		throws IOException
	{
		Vector folder_list = new Vector();
		folder_list.addElement(getFieldOrientedDBFolder());

		int m = (int)limit_mag;
		folder_list.addElement(String.valueOf(m));

		String r = "0";
		if (field_radius >= 20)
			r = "20";
		else if (field_radius >= 10)
			r = "10";
		else if (field_radius >= 5)
			r = "5";
		else if (field_radius >= 2)
			r = "2";
		else if (field_radius >= 1)
			r = "1";
		else if (field_radius >= 0.5)
			r = "0.5";
		else if (field_radius >= 0.2)
			r = "0.2";
		else if (field_radius >= 0.1)
			r = "0.1";
		folder_list.addElement(r);

		Vector list = CelestialDivisionMap.getCoorFolderHierarchy(center_coor);
		for (int i = 0 ; i < list.size() ; i++)
			folder_list.addElement(list.elementAt(i));

		return file_system.createHierarchicalFolder(folder_list);
	}

	/**
	 * Gets the sub folders under the specified folder hierarchy in
	 * the date oriented database.
	 * @param folder_list the folder hierarchy.
	 * @return the list of sub folders.
	 * @exception IOException if I/O error occurs.
	 */
	public Vector getDateOrientedFolders ( Vector folder_list )
		throws IOException
	{
		Vector list = new Vector();
		list.addElement(getDateOrientedDBFolder());
		file_system.createHierarchicalFolder(list);

		return getFolders(getDateOrientedDBFolder(), folder_list);
	}

	/**
	 * Gets the sub folders under the specified folder hierarchy in
	 * the path oriented database.
	 * @param folder_list the folder hierarchy.
	 * @return the list of sub folders.
	 * @exception IOException if I/O error occurs.
	 */
	public Vector getPathOrientedFolders ( Vector folder_list )
		throws IOException
	{
		Vector list = new Vector();
		list.addElement(getPathOrientedDBFolder());
		file_system.createHierarchicalFolder(list);

		return getFolders(getPathOrientedDBFolder(), folder_list);
	}

	/**
	 * Gets the sub folders under the specified folder hierarchy.
	 * @param folder_list the folder hierarchy.
	 * @return the list of sub folders.
	 * @exception IOException if I/O error occurs.
	 */
	protected Vector getFolders ( String base_folder, Vector folder_list )
		throws IOException
	{
		Vector new_folder_list = new Vector();
		new_folder_list.addElement(base_folder);
		for (int i = 0 ; i < folder_list.size() ; i++)
			new_folder_list.addElement(folder_list.elementAt(i));

		Folder db_folder = file_system.getHierarchicalFolder(new_folder_list);

		Folder[] sub_folders = db_folder.list();

		Vector list = new Vector();
		for (int i = 0 ; i < sub_folders.length ; i++) {
			if (sub_folders[i].isFolder())
				list.addElement(sub_folders[i].getName());
		}

		return list;
	}

	/**
	 * Gets the sequential accessor to the XML image information 
	 * elements in the specified folder hierarchy in the date oriented
	 * database.
	 * @param folder_list the folder hierarchy.
	 * @return the sequential accessor.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlDBAccessor getDateOrientedAccessor ( Vector folder_list )
		throws IOException
	{
		return getAccessor(getDateOrientedDBFolder(), folder_list);
	}

	/**
	 * Gets the sequential accessor to the XML image information 
	 * elements in the specified folder hierarchy in the path oriented
	 * database.
	 * @param folder_list the folder hierarchy.
	 * @return the sequential accessor.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlDBAccessor getPathOrientedAccessor ( Vector folder_list )
		throws IOException
	{
		return getAccessor(getPathOrientedDBFolder(), folder_list);
	}

	/**
	 * Gets the sequential accessor to the XML image information 
	 * elements in the specified folder hierarchy.
	 * @param folder_list the folder hierarchy.
	 * @return the sequential accessor.
	 * @exception IOException if I/O error occurs.
	 */
	protected XmlDBAccessor getAccessor ( String base_folder, Vector folder_list )
		throws IOException
	{
		Vector new_folder_list = new Vector();
		new_folder_list.addElement(base_folder);
		for (int i = 0 ; i < folder_list.size() ; i++)
			new_folder_list.addElement(folder_list.elementAt(i));

		Folder db_folder = file_system.getHierarchicalFolder(new_folder_list);

		PrimitiveManager manager = file_system.getPrimitiveManager(db_folder, new XmlInformationHolder(), new XmlInformation());
		return manager.getAccessor();
	}

	/**
	 * Gets the sequential accessor to all the XML image information 
	 * element in the database.
	 * @return the sequential accessor.
	 */
	public InformationDBAccessor getAccessor ( ) {
		InformationDBAccessor accessor = new InformationDBAccessor();

		CelestialDivisionMap map = new CelestialDivisionMap();
		map.fillAll();

		// Lists up the target folders.
		Folder db_folder = file_system.getFolder(getFieldOrientedDBFolder());
		Folder[] mag_folders = db_folder.list();
		for (int i = 0 ; i < mag_folders.length ; i++) {
			Folder[] r_folders = mag_folders[i].list();
			for (int j = 0 ; j < r_folders.length ; j++) {
				CelestialDivisionMapDBAccessor map_accessor = new CelestialDivisionMapDBAccessor(file_system, r_folders[j], map, new XmlInformationHolder(), new XmlInformation());
				accessor.addMapAccessor(map_accessor);
			}
		}

		return accessor;
	}

	/**
	 * Gets the sequential accessor to the XML image information 
	 * element which covers the specified R.A. and Decl.
	 * @param coor                   the R.A. and Decl.
	 * @param brightest_limiting_mag the brighter limit of the 
	 * limiting magnitude.
	 * @param faintest_limiting_mag  the fainter limit of the limiting
	 * magnitude.
	 * @return the sequential accessor.
	 */
	public InformationDBAccessor getAccessor ( Coor coor, double brightest_limiting_mag, double faintest_limiting_mag ) {
		// Creates the flag map.
		CelestialDivisionMap map = new CelestialDivisionMap();
		map.fill(coor, 10.0 / 3600.0);

		return getAccessor(map, brightest_limiting_mag, faintest_limiting_mag);
	}

	/**
	 * Gets the sequential accessor to the XML image information 
	 * element which overlaps on any images in the specified list.
	 * @param info_list              the list of image information 
	 * elements.
	 * @param brightest_limiting_mag the brighter limit of the 
	 * limiting magnitude.
	 * @param faintest_limiting_mag  the fainter limit of the limiting
	 * magnitude.
	 * @return the sequential accessor.
	 */
	public InformationDBAccessor getAccessor ( Vector info_list, double brightest_limiting_mag, double faintest_limiting_mag ) {
		// Creates the flag map where the images in the specified list covers.
		CelestialDivisionMap map = new CelestialDivisionMap();
		for (int i = 0 ; i < info_list.size() ; i++) {
			XmlInformation info = (XmlInformation)info_list.elementAt(i);
			map.fill(((XmlCenter)info.getCenter()).getCoor(), info.getFieldRadiusInDegree());
		}

		return getAccessor(map, brightest_limiting_mag, faintest_limiting_mag);
	}

	/**
	 * Gets the sequential accessor to the XML image information 
	 * element which overlaps the specified map.
	 * @param map                    the celestial map.
	 * elements.
	 * @param brightest_limiting_mag the brighter limit of the 
	 * limiting magnitude.
	 * @param faintest_limiting_mag  the fainter limit of the limiting
	 * magnitude.
	 * @return the sequential accessor.
	 */
	public InformationDBAccessor getAccessor ( CelestialDivisionMap map, double brightest_limiting_mag, double faintest_limiting_mag ) {
		InformationDBAccessor accessor = new InformationDBAccessor();

		// Lists up the target folders.
		Folder db_folder = file_system.getFolder(getFieldOrientedDBFolder());
		Folder[] mag_folders = db_folder.list();
		for (int i = 0 ; i < mag_folders.length ; i++) {
			double mag = Double.parseDouble(mag_folders[i].getName());
			if (brightest_limiting_mag < mag + 1.0  &&  faintest_limiting_mag >= mag) {
				for (int j = 0 ; j < 9 ; j++) {
					String r = "0";
					double field_radius = 0.1;

					switch (j) {
						case 0:
							r = "20";
							field_radius = 180.0;
							break;
						case 1:
							r = "10";
							field_radius = 20.0;
							break;
						case 2:
							r = "5";
							field_radius = 10.0;
							break;
						case 3:
							r = "2";
							field_radius = 5.0;
							break;
						case 4:
							r = "1";
							field_radius = 2.0;
							break;
						case 5:
							r = "0.5";
							field_radius = 1.0;
							break;
						case 6:
							r = "0.2";
							field_radius = 0.5;
							break;
						case 7:
							r = "0.1";
							field_radius = 0.2;
							break;
						default:
							r = "0";
							field_radius = 0.1;
							break;
					}

					Folder r_folder = mag_folders[i].getFolder(r);
					if (r_folder.exists()) {
						CelestialDivisionMap map2 = map.expand(field_radius);

						CelestialDivisionMapDBAccessor map_accessor = new CelestialDivisionMapDBAccessor(file_system, r_folder, map2, new XmlInformationHolder(), new XmlInformation());
						accessor.addMapAccessor(map_accessor);
					}
				}
			}
		}

		return accessor;
	}
}
