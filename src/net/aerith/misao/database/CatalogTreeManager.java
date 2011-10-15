/*
 * @(#)CatalogTreeManager.java
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
 * The <code>CatalogTreeManager</code> is an interface to the catalog 
 * tree.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public abstract class CatalogTreeManager extends OperationObservable {
	/**
	 * The method number which indicates to include the sub folders.
	 */
	public final static int INCLUDE_SUBFOLDERS = 0;

	/**
	 * The method number which indicates to exclude the sub folders.
	 */
	public final static int EXCLUDE_SUBFOLDERS = 1;

	/**
	 * Gets the folder hierarchy of the catalog tree in the database.
	 * @param method the method.
	 * @return the hash table which contains the folder hierarchy.
	 * @exception IOException if I/O error occurs.
	 */
	public Hashtable getCatalogHierarchy ( int method )
		throws IOException
	{
		Hashtable hash_catalog_db = getHierarchy(method);

		// Converts the folder name to the proper name.
		Hashtable hash = new Hashtable();
		Enumeration categories = hash_catalog_db.keys();
		while (categories.hasMoreElements()) {
			String category_folder = (String)categories.nextElement();
			Hashtable hash_category = (Hashtable)hash_catalog_db.get(category_folder);

			Hashtable new_hash_category = new Hashtable();
			Enumeration catalogs = hash_category.keys();
			while (catalogs.hasMoreElements()) {
				String catalog_folder = (String)catalogs.nextElement();
				Hashtable hash_catalog = (Hashtable)hash_category.get(catalog_folder);

				new_hash_category.put(CatalogManager.convertCatalogFolderToName(catalog_folder), hash_catalog);
			}

			hash.put(CatalogManager.convertCategoryFolderToName(category_folder), new_hash_category);
		}

		return hash;
	}

	/**
	 * Gets the folder hierarchy of the database.
	 * @param method the method.
	 * @return the hash table which contains the folder hierarchy.
	 * @exception IOException if I/O error occurs.
	 */
	protected abstract Hashtable getHierarchy ( int method )
		throws IOException;

	/**
	 * Gets the sub folders in the specified path.
	 * @param category_name the name of category.
	 * @param catalog_name  the name of catalog.
	 * @param sub_folders   the folder hierarchy under the catalog 
	 * folder.
	 * @return the list of sub folders.
	 * @exception IOException if I/O error occurs.
	 */
	public Vector getCatalogSubfolders ( String category_name, String catalog_name, Vector sub_folders )
		throws IOException
	{
		Vector folder_list = new Vector();
		folder_list.addElement(CatalogManager.convertCategoryNameToFolder((String)category_name));
		folder_list.addElement(CatalogManager.convertCatalogNameToFolder((String)catalog_name));
		for (int i = 0 ; i < sub_folders.size() ; i++)
			folder_list.addElement(sub_folders.elementAt(i));

		Vector subfolder_list = getCatalogSubfolders(folder_list);

		// Sorts the sub folders.
		StringArray array = new StringArray(subfolder_list.size());
		for (int i = 0 ; i < subfolder_list.size() ; i++)
			array.set(i, (String)subfolder_list.elementAt(i));
		ArrayIndex index = array.sortAscendant();

		Vector new_subfolder_list = new Vector();
		for (int i = 0 ; i < array.getArraySize() ; i++)
			new_subfolder_list.addElement(subfolder_list.elementAt(index.get(i)));

		return new_subfolder_list;
	}

	/**
	 * Gets the sub folders in the specified path.
	 * @param folder_list the list of folders.
	 * @return the list of sub folders.
	 * @exception IOException if I/O error occurs.
	 */
	protected abstract Vector getCatalogSubfolders ( Vector folder_list )
		throws IOException;

	/**
	 * Gets the stars in the specified path.
	 * @param category_name the name of category.
	 * @param catalog_name  the name of catalog.
	 * @param sub_folders   the folder hierarchy under the catalog 
	 * folder.
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
	public Vector getStars ( String category_name, String catalog_name, Vector sub_folders )
		throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException
	{
		Vector folder_list = new Vector();
		folder_list.addElement(CatalogManager.convertCategoryNameToFolder((String)category_name));
		folder_list.addElement(CatalogManager.convertCatalogNameToFolder((String)catalog_name));
		for (int i = 0 ; i < sub_folders.size() ; i++)
			folder_list.addElement(sub_folders.elementAt(i));

		Vector star_list = getStars(folder_list);

		// Sorts the stars.
		StringArray array = new StringArray(star_list.size());
		for (int i = 0 ; i < star_list.size() ; i++) {
			Star star = (Star)star_list.elementAt(i);
			array.set(i, star.getName());
		}
		ArrayIndex index = array.sortAscendant();

		Vector new_star_list = new Vector();
		for (int i = 0 ; i < array.getArraySize() ; i++)
			new_star_list.addElement(star_list.elementAt(index.get(i)));

		return new_star_list;
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
	protected abstract Vector getStars ( Vector folder_list )
		throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException;
}
