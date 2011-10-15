/*
 * @(#)CatalogDBManager.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import java.io.File;
import java.io.IOException;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.io.*;
import net.aerith.misao.catalog.CatalogManager;
import net.aerith.misao.xml.*;

/**
 * The <code>CatalogDBManager</code> represents a database manager of
 * the catalog stars.
 * <p>
 * The database consists of two parts with different classification
 * policies. One is classified by the star name. Another one is by the
 * R.A. and Decl. 
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 December 27
 */

public class CatalogDBManager extends CatalogTreeManager {
	/**
	 * The file system of the database.
	 */
	protected FileSystem file_system;

	/**
	 * The file manager.
	 */
	protected FileManager file_manager;

	/**
	 * Constructs a <code>CatalogDBManager</code> in the 
	 * specified file system.
	 * @param file_system  the file system to create the database.
	 * @param file_manager the file manager.
	 */
	public CatalogDBManager ( FileSystem file_system, FileManager file_manager ) {
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
	 * Gets the top folder of the database classified by the star name.
	 * @return the top folder of the database classified by the star
	 * name.
	 */
	protected String getNameOrientedDBFolder ( ) {
		return "catalog.name";
	}

	/**
	 * Gets the top folder of the database classified by the position.
	 * @return the top folder of the database classified by the 
	 * position.
	 */
	protected String getPositionOrientedDBFolder ( ) {
		return "catalog.position";
	}

	/**
	 * Adds the catalog star.
	 * @param star the catalog star.
	 * @exception IOException if I/O error occurs.
	 */
	public void addElement ( CatalogStar star )
		throws IOException
	{
		// Creates a new XML element. 
		// The (x,y) position must not be recorded in the database.
		XmlRecord record = new XmlRecord(star);
		record.setPosition(null);

		// Adds to the database classified by the name.
		Folder db_folder = createNameOrientedDBFolder(star);
		PrimitiveManager manager = file_system.getPrimitiveManager(db_folder, new XmlRecordHolder(), new XmlRecord());
		manager.addElement(record);

		// Adds to the database classified by the position.
		db_folder = createPositionOrientedDBFolder(star.getCoor());
		manager = file_system.getPrimitiveManager(db_folder, new XmlRecordHolder(), new XmlRecord());
		manager.addElement(record);
	}

	/**
	 * Adds the catalog stars.
	 * @param list_stars the list of catalog stars.
	 * @exception IOException if I/O error occurs.
	 */
	public void addElements ( Vector list_stars )
		throws IOException
	{
		// Creates the hash table based on the folder names.
		Hashtable folder_hash = new Hashtable();
		Hashtable folder_hash2 = new Hashtable();
		Hashtable hash = new Hashtable();
		for (int i = 0 ; i < list_stars.size() ; i++) {
			CatalogStar star = (CatalogStar)list_stars.elementAt(i);

			// For the database classified by the name.
			Folder db_folder = createNameOrientedDBFolder(star);
			String db_folder_id = db_folder.getID();
			Vector list = (Vector)folder_hash.get(db_folder_id);
			if (list == null) {
				list = new Vector();
				folder_hash.put(db_folder_id, list);
				hash.put(db_folder_id, db_folder);
			}

			// For the database classified by the position.
			Folder db_folder2 = createPositionOrientedDBFolder(star.getCoor());
			String db_folder_id2 = db_folder2.getID();
			Vector list2 = (Vector)folder_hash2.get(db_folder_id2);
			if (list2 == null) {
				list2 = new Vector();
				folder_hash2.put(db_folder_id2, list2);
				hash.put(db_folder_id2, db_folder2);
			}

			// Creates a new XML element. 
			// The (x,y) position must not be recorded in the database.
			XmlRecord record = new XmlRecord(star);
			record.setPosition(null);
			list.addElement(record);
			list2.addElement(record);
		}

		// Adds to the database classified by the name.
		Enumeration keys = folder_hash.keys();
		while (keys.hasMoreElements()) {
			String db_folder_id = (String)keys.nextElement();
			Folder db_folder = (Folder)hash.get(db_folder_id);
			PrimitiveManager manager = file_system.getPrimitiveManager(db_folder, new XmlRecordHolder(), new XmlRecord());
			manager.addElements((Vector)folder_hash.get(db_folder_id));
		}

		// Adds to the database classified by the position.
		keys = folder_hash2.keys();
		while (keys.hasMoreElements()) {
			String db_folder_id = (String)keys.nextElement();
			Folder db_folder = (Folder)hash.get(db_folder_id);
			PrimitiveManager manager = file_system.getPrimitiveManager(db_folder, new XmlRecordHolder(), new XmlRecord());
			manager.addElements((Vector)folder_hash2.get(db_folder_id));
		}
	}

	/**
	 * Deletes the catalog star.
	 * @param star the catalog star.
	 * @exception IOException if I/O error occurs.
	 */
	public void deleteElement ( CatalogStar star )
		throws IOException
	{
		// Gets the ID of the XML element. 
		XmlRecord record = new XmlRecord(star);
		String id = record.getID();

		// Deletes from the database classified by the name.
		Folder db_folder = createNameOrientedDBFolder(star);
		PrimitiveManager manager = file_system.getPrimitiveManager(db_folder, new XmlRecordHolder(), new XmlRecord());
		manager.deleteElement(id);

		// Deletes from the database classified by the position.
		db_folder = createPositionOrientedDBFolder(star.getCoor());
		manager = file_system.getPrimitiveManager(db_folder, new XmlRecordHolder(), new XmlRecord());
		manager.deleteElement(id);
	}

	/**
	 * Deletes the catalog stars.
	 * @param hash_stars the hash table of the catalog stars to delete.
	 * @exception IOException if I/O error occurs.
	 */
	public void deleteElements ( Hashtable hash_stars )
		throws IOException
	{
		// Copies the hash table.
		Hashtable hash_stars2 = (Hashtable)hash_stars.clone();

		// Deletes from the database classified by the name.
		while (hash_stars.isEmpty() == false) {
			// Gets the ID of one XML element to delete.
			Enumeration keys = hash_stars.keys();
			String id = (String)keys.nextElement();
			CatalogStar star = (CatalogStar)hash_stars.get(id);

			Folder db_folder = createNameOrientedDBFolder(star);
			PrimitiveManager manager = file_system.getPrimitiveManager(db_folder, new XmlRecordHolder(), new XmlRecord());
			manager.deleteElements(hash_stars);
		}

		// Deletes from the database classified by the position.
		while (hash_stars2.isEmpty() == false) {
			// Gets the ID of one XML element to delete.
			Enumeration keys = hash_stars2.keys();
			String id = (String)keys.nextElement();
			CatalogStar star = (CatalogStar)hash_stars2.get(id);

			Folder db_folder = createPositionOrientedDBFolder(star.getCoor());
			PrimitiveManager manager = file_system.getPrimitiveManager(db_folder, new XmlRecordHolder(), new XmlRecord());
			manager.deleteElements(hash_stars2);
		}
	}

	/**
	 * Gets the catalog star in the database.
	 * @param star the catalog star.
	 * @return the catalog star in the database.
	 * @exception IOException if I/O error occurs.
	 * @exception ClassNotFoundException if the star class recorded in
	 * the XML document is not found.
	 * @exception IllegalAccessException if the class or initializer 
	 * is not accessible.
	 * @exception InstantiationException if an application tries to
	 * instantiate an abstract class or an interface, or if the 
	 * instantiation fails for some other reason.
	 */
	public CatalogStar getElement ( CatalogStar star )
		throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException
	{
		// Gets the ID of the XML element. 
		XmlRecord record = new XmlRecord(star);
		String id = record.getID();

		// Gets from the database classified by the name.
		Folder db_folder = createNameOrientedDBFolder(star);
		PrimitiveManager manager = file_system.getPrimitiveManager(db_folder, new XmlRecordHolder(), new XmlRecord());
		record = (XmlRecord)manager.getElement(id);

		if (record == null)
			return null;

		return (CatalogStar)record.createStar();
	}

	/**
	 * Gets the catalog star indicated by the specified path in the
	 * magnitude database.
	 * @param mag_folder_hierarchy the folder hierarchy in the 
	 * magnitude database.
	 * @return the catalog star object.
	 * @exception IOException if I/O error occurs.
	 * @exception ClassNotFoundException if the star class recorded in
	 * the XML document is not found.
	 * @exception IllegalAccessException if the class or initializer 
	 * is not accessible.
	 * @exception InstantiationException if an application tries to
	 * instantiate an abstract class or an interface, or if the 
	 * instantiation fails for some other reason.
	 */
	public CatalogStar getElement ( Vector mag_folder_hierarchy )
		throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException
	{
		// Gets the folder hierarchy in the catalog database.
		Vector folder_list = new Vector();
		folder_list.addElement(getNameOrientedDBFolder());
		for (int i = 0 ; i < mag_folder_hierarchy.size() - 1 ; i++)
			folder_list.addElement(mag_folder_hierarchy.elementAt(i));

		// The secondary folder shows the catalog star class.
		String id = (String)mag_folder_hierarchy.elementAt(mag_folder_hierarchy.size() - 1);
		try {
			String catalog_name = CatalogManager.convertCatalogFolderToName((String)mag_folder_hierarchy.elementAt(1));
			String class_name = CatalogManager.getCatalogStarClassName(catalog_name);
			Star star = (Star)Class.forName(class_name).newInstance();
			id = StarClass.getClassName(star) + " " + id;
		} catch ( ClassNotFoundException exception ) {
			System.err.println(exception);
		}

		Folder db_folder = file_system.getHierarchicalFolder(folder_list);
		PrimitiveManager manager = file_system.getPrimitiveManager(db_folder, new XmlRecordHolder(), new XmlRecord());
		XmlRecord record = (XmlRecord)manager.getElement(id);

		if (record == null)
			return null;

		return (CatalogStar)record.createStar();
	}

	/**
	 * Gets the elements in the specified path in the name oriented
	 * database.
	 * @param folder_list the folder hierarchy.
	 * @return the list of elements.
	 * @exception IOException if I/O error occurs.
	 * @exception ClassNotFoundException if the star class recorded in
	 * the XML document is not found.
	 * @exception IllegalAccessException if the class or initializer 
	 * is not accessible.
	 * @exception InstantiationException if an application tries to
	 * instantiate an abstract class or an interface, or if the 
	 * instantiation fails for some other reason.
	 */
	public Vector getElements ( Vector folder_list )
		throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException
	{
		Vector new_folder_list = new Vector();
		new_folder_list.addElement(getNameOrientedDBFolder());
		for (int i = 0 ; i < folder_list.size() ; i++)
			new_folder_list.addElement(folder_list.elementAt(i));

		Folder db_folder = file_system.getHierarchicalFolder(new_folder_list);
		PrimitiveManager manager = file_system.getPrimitiveManager(db_folder, new XmlRecordHolder(), new XmlRecord());

		XmlDBAccessor accessor = manager.getAccessor();

		Vector list = new Vector();
		XmlDBRecord record = accessor.getFirstElement();
		while (record != null) {
			Star star = ((XmlRecord)record).createStar();
			list.addElement(star);
			record = accessor.getNextElement();
		}

		return list;
	}

	/**
	 * Creates the database folder classified by the star name.
	 * @param star the catalog star.
	 * @return the database folder.
	 * @exception IOException if I/O error occurs.
	 */
	protected Folder createNameOrientedDBFolder ( CatalogStar star )
		throws IOException
	{
		Vector folder_list = new Vector();
		folder_list.addElement(getNameOrientedDBFolder());

		Vector list = star.getHierarchicalFolders();
		if (list == null)
			return null;

		for (int i = 0 ; i < list.size() ; i++)
			folder_list.addElement(list.elementAt(i));

		return file_system.createHierarchicalFolder(folder_list);
	}

	/**
	 * Creates the database folder classified by the position.
	 * @param coor the R.A. and Decl.
	 * @return the database folder.
	 * @exception IOException if I/O error occurs.
	 */
	protected Folder createPositionOrientedDBFolder ( Coor coor )
		throws IOException
	{
		Vector folder_list = new Vector();
		folder_list.addElement(getPositionOrientedDBFolder());

		Vector list = CelestialDivisionMap.getCoorFolderHierarchy(coor);
		for (int i = 0 ; i < list.size() ; i++)
			folder_list.addElement(list.elementAt(i));

		return file_system.createHierarchicalFolder(folder_list);
	}

	/**
	 * Gets the sequential accessor to the catalog star records within
	 * the specified circular area.
	 * @param coor   the R.A. and Decl. of the center.
	 * @param radius the radius in degree.
	 * @return the sequential accessor.
	 */
	public CatalogDBAccessor getAccessor ( Coor coor, double radius ) {
		CelestialDivisionMap map = new CelestialDivisionMap();
		map.fill(coor, radius);

		Folder folder = file_system.getFolder(getPositionOrientedDBFolder());
		return new CatalogDBAccessor(file_system, folder, map);
	}

	/**
	 * Gets the folder hierarchy of the database.
	 * @param method the method.
	 * @return the hash table which contains the folder hierarchy.
	 * @exception IOException if I/O error occurs.
	 */
	protected Hashtable getHierarchy ( int method )
		throws IOException
	{
		Hashtable hash = new Hashtable();

		Vector folder_list = new Vector();
		folder_list.addElement(getNameOrientedDBFolder());
		Folder folder = file_system.createHierarchicalFolder(folder_list);

		int level = 9999;
		if (method == EXCLUDE_SUBFOLDERS)
			level = 2;
		setHierarchy(hash, folder, level);

		return hash;
	}

	/**
	 * Sets the folder hierarchy of the catalog database recurrsively.
	 * @param hash   the hash table to set the hierarchy.
	 * @param folder the folder.
	 * @param level  the acceptable nest level.
	 */
	private void setHierarchy ( Hashtable hash, Folder folder, int level ) {
		if (level <= 0)
			return;

		Folder[] sub_folders = folder.list();

		for (int i = 0 ; i < sub_folders.length ; i++) {
			if (sub_folders[i].isFolder()) {
				Hashtable hash_sub = new Hashtable();
				hash.put(sub_folders[i].getName(), hash_sub);

				setHierarchy(hash_sub, sub_folders[i], level - 1);
			}
		}
	}

	/**
	 * Gets the sub folders in the specified path.
	 * @param folder_list the list of folders.
	 * @return the list of sub folders.
	 * @exception IOException if I/O error occurs.
	 */
	protected Vector getCatalogSubfolders ( Vector folder_list )
		throws IOException
	{
		Vector new_folder_list = new Vector();
		new_folder_list.addElement(getNameOrientedDBFolder());
		for (int i = 0 ; i < folder_list.size() ; i++)
			new_folder_list.addElement(folder_list.elementAt(i));

		Folder db_folder = file_system.getHierarchicalFolder(new_folder_list);

		Vector subfolder_list = new Vector();
		Folder[] sub_folders = db_folder.list();
		for (int i = 0 ; i < sub_folders.length ; i++) {
			if (sub_folders[i].isFolder())
				subfolder_list.addElement(sub_folders[i].getName());
		}

		return subfolder_list;
	}

	/**
	 * Gets the stars in the specified path.
	 * @param folder_list the list of folders.
	 * @return the list of stars.
	 * @exception IOException if I/O error occurs.
	 * @exception ClassNotFoundException if the star class recorded in
	 * the XML document is not found.
	 * @exception IllegalAccessException if the class or initializer 
	 * is not accessible.
	 * @exception InstantiationException if an application tries to
	 * instantiate an abstract class or an interface, or if the 
	 * instantiation fails for some other reason.
	 */
	protected Vector getStars ( Vector folder_list )
		throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException
	{
		return getElements(folder_list);
	}
}
