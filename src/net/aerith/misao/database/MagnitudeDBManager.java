/*
 * @(#)MagnitudeDBManager.java
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
import net.aerith.misao.pixy.VariabilityChecker;
import net.aerith.misao.io.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.io.XmlDBHolderCache;

/**
 * The <code>MagnitudeDBManager</code> represents a database manager
 * of the magnitude data. 
 * <p>
 * The database is classified by the star name.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 6
 */

public class MagnitudeDBManager extends CatalogTreeManager {
	/**
	 * The file system of the database.
	 */
	protected FileSystem file_system;

	/**
	 * The file manager.
	 */
	protected FileManager file_manager;

	/**
	 * Constructs a <code>MagnitudeDBManager</code> in the 
	 * specified file system.
	 * @param file_system  the file system to create the database.
	 * @param file_manager the file manager.
	 */
	public MagnitudeDBManager ( FileSystem file_system, FileManager file_manager ) {
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
	 * Gets the top folder of the database.
	 * @return the top folder of the database.
	 */
	public String getDBFolder ( ) {
		return "mag";
	}

	/**
	 * Adds the magnitude record.
	 * @param star   the catalog star.
	 * @param record the magnitude record.
	 * @exception IOException if I/O error occurs.
	 */
	public void addElement ( CatalogStar star, XmlMagRecord record )
		throws IOException
	{
		// Adds to the database.
		Folder db_folder = createDBFolder(star);
		PrimitiveManager manager = file_system.getPrimitiveManager(db_folder, new XmlMagRecordHolder(), new XmlMagRecord());
		manager.addElement(record);
	}

	/**
	 * Deletes the magnitude record.
	 * @param star   the catalog star.
	 * @param record the magnitude record.
	 * @return the deleted magnitude record, or null if the record 
	 * does not exist.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlMagRecord deleteElement ( CatalogStar star, XmlMagRecord record )
		throws IOException
	{
		// Deletes from the database.
		Folder db_folder = createDBFolder(star);
		PrimitiveManager manager = file_system.getPrimitiveManager(db_folder, new XmlMagRecordHolder(), new XmlMagRecord());
		return (XmlMagRecord)manager.deleteElement(record.getImageXmlPath());
	}

	/**
	 * Gets the magnitude records of the specified star.
	 * @param star the catalog star.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlMagRecord[] getElements ( CatalogStar star )
		throws IOException
	{
		Folder db_folder = createDBFolder(star);
		return getElements(db_folder);
	}

	/**
	 * Gets the magnitude records in the specified folder.
	 * @param db_folder the database folder.
	 * @exception IOException if I/O error occurs.
	 */
	protected XmlMagRecord[] getElements ( Folder db_folder )
		throws IOException
	{
		PrimitiveManager manager = file_system.getPrimitiveManager(db_folder, new XmlMagRecordHolder(), new XmlMagRecord());
		XmlDBAccessor accessor = manager.getAccessor();

		// Reads the magnitude records.
		Vector record_list = new Vector();
		XmlDBRecord record = accessor.getFirstElement();
		while (record != null) {
			record_list.addElement(record);
			record = accessor.getNextElement();
		}

		XmlMagRecord[] records = new XmlMagRecord[record_list.size()];
		for (int i = 0 ; i < record_list.size() ; i++)
			records[i] = (XmlMagRecord)record_list.elementAt(i);

		return records;
	}

	/**
	 * Creates the database folder.
	 * @param star the catalog star.
	 * @return the database folder.
	 * @exception IOException if I/O error occurs.
	 */
	protected Folder createDBFolder ( CatalogStar star )
		throws IOException
	{
		Vector folder_list = new Vector();
		folder_list.addElement(getDBFolder());

		Vector list = star.getHierarchicalFolders();
		if (list == null)
			throw new IOException();

		for (int i = 0 ; i < list.size() ; i++)
			folder_list.addElement(list.elementAt(i));

		folder_list.addElement(star.getStarFolder());

		return file_system.createHierarchicalFolder(folder_list);
	}

	/**
	 * Browses whole of the magnitude database and seeks the variable
	 * stars.
	 * @param checker the checker of the variability.
	 * @return the list of variable stars and the variability 
	 * information.
	 * @exception IOException if I/O error occurs.
	 */
	public Vector seekVariable ( VariabilityChecker checker )
		throws IOException
	{
		Vector list = new Vector();

		Folder folder = file_system.getFolder(getDBFolder());
		Folder[] files = folder.list();
		for (int i = 0 ; i < files.length ; i++) {
			Vector l = seekVariable(files[i], checker);
			if (l != null) {
				for (int j = 0 ; j < l.size() ; j++)
					list.addElement(l.elementAt(j));
			}
		}

		return list;
	}

	/**
	 * Browses the specified folder in the magnitude database 
	 * recurrsively and seeks the variable stars.
	 * @param folder  the folder to seek.
	 * @param checker the checker of the variability.
	 * @return the list of variable stars and the variability 
	 * information.
	 * @exception IOException if I/O error occurs.
	 */
	public Vector seekVariable ( Folder folder, VariabilityChecker checker )
		throws IOException
	{
		if (folder.isFolder() == false)
			return null;

		Vector list = new Vector();

		// Checks the current folder.
		PrimitiveManager manager = file_system.getPrimitiveManager(folder, new XmlMagRecordHolder(), new XmlMagRecord());
		XmlDBAccessor accessor = manager.getAccessor();

		// Reads the magnitude records.
		Vector record_list = new Vector();
		XmlDBRecord record = accessor.getFirstElement();
		while (record != null) {
			record_list.addElement(record);
			record = accessor.getNextElement();
		}

		if (record_list.size() > 1) {
			XmlMagRecord[] records = new XmlMagRecord[record_list.size()];
			for (int i = 0 ; i < record_list.size() ; i++)
				records[i] = (XmlMagRecord)record_list.elementAt(i);

			if (checker.getBlendingPolicy() == VariabilityChecker.BLENDING_REJECTED) {
				// Rejects if blending.
				for (int i = 0 ; i < record_list.size() ; i++) {
					XmlBlending[] blendings = (XmlBlending[])records[i].getBlending();
					if (blendings != null  &&  blendings.length > 0) {
						records = null;
						break;
					}
				}
			} else if (checker.getBlendingPolicy() == VariabilityChecker.BLENDING_BLENDED) {
				// Calculates the blending magnitude if blending.
				Hashtable hash_blending = new Hashtable();
				for (int i = 0 ; i < record_list.size() ; i++) {
					XmlBlending[] blendings = (XmlBlending[])records[i].getBlending();
					if (blendings != null  &&  blendings.length > 0) {
						for (int j = 0 ; j < blendings.length ; j++) {
							try {
								CatalogStar s = (CatalogStar)StarClass.newInstance(blendings[j].getClassValue());
								s.setName(blendings[j].getContent());
								hash_blending.put(blendings[j].getClassValue() + blendings[j].getContent(), s);
							} catch ( ClassNotFoundException exception ) {
								System.err.println(exception);
							} catch ( IllegalAccessException exception ) {
								System.err.println(exception);
							} catch ( InstantiationException exception ) {
								System.err.println(exception);
							}
						}
					}
				}
				Enumeration keys = hash_blending.keys();
				while (keys.hasMoreElements()) {
					CatalogStar s = (CatalogStar)hash_blending.get(keys.nextElement());
					XmlMagRecord[] blending_records = getElements(s);
					for (int i = 0 ; i < records.length ; i++) {
						// Does not blend if already blended.
						if (records[i].blending(s) == false) {
							XmlMag mag = (XmlMag)records[i].getMag();

							for (int j = 0 ; j < blending_records.length ; j++) {
								XmlMag blending_mag = (XmlMag)blending_records[j].getMag();
								if (records[i].getImageXmlPath().equals(blending_records[j].getImageXmlPath()))
									mag.blend(blending_mag);
							}
						}
					}
				}
			}

			if (records != null) {
				Variability variability = checker.check(records);
				if (variability != null) {
					try {
						// The top folder is the "mag" folder.
						Vector folder_list = folder.getHierarchy();
						Vector folder_list2 = new Vector();
						for (int j = 1 ; j < folder_list.size() ; j++)
							folder_list2.addElement(folder_list.elementAt(j));

						CatalogStar star = new CatalogDBManager(file_system, file_manager).getElement(folder_list2);
						variability.setStar(star);
					} catch ( ClassNotFoundException exception ) {
						System.err.println(exception);
					} catch ( IllegalAccessException exception ) {
						System.err.println(exception);
					} catch ( InstantiationException exception ) {
						System.err.println(exception);
					}
					list.addElement(variability);
				}
			}
		}

		// Checks the sub-folders of the current folder recurrsively.
		Folder[] files = folder.list();
		for (int i = 0 ; i < files.length ; i++) {
			Vector l = seekVariable(files[i], checker);
			if (l != null) {
				for (int j = 0 ; j < l.size() ; j++)
					list.addElement(l.elementAt(j));
			}
		}

		return list;
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
		folder_list.addElement(getDBFolder());
		Folder folder = file_system.createHierarchicalFolder(folder_list);

		int level = 9999;
		if (method == EXCLUDE_SUBFOLDERS)
			level = 2;
		setHierarchy(hash, folder, level);

		return hash;
	}

	/**
	 * Sets the folder hierarchy of the magnitude database recurrsively.
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
				// The leaf is the folder of a star, which is not included.
				Folder[] leaves = sub_folders[i].list();
				int folder_count = 0;
				for (int j = 0 ; j < leaves.length ; j++) {
					if (leaves[j].isFolder())
						folder_count++;
				}

				if (folder_count > 0) {
					Hashtable hash_sub = new Hashtable();
					hash.put(sub_folders[i].getName(), hash_sub);

					setHierarchy(hash_sub, sub_folders[i], level - 1);
				}
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
		new_folder_list.addElement(getDBFolder());
		for (int i = 0 ; i < folder_list.size() ; i++)
			new_folder_list.addElement(folder_list.elementAt(i));

		Folder db_folder = file_system.getHierarchicalFolder(new_folder_list);

		Vector subfolder_list = new Vector();
		Folder[] sub_folders = db_folder.list();
		for (int i = 0 ; i < sub_folders.length ; i++) {
			if (sub_folders[i].isFolder()) {
				// The leaf is the folder of a star, which is not included.
				Folder[] leaves = sub_folders[i].list();
				int folder_count = 0;
				for (int j = 0 ; j < leaves.length ; j++) {
					if (leaves[j].isFolder())
						folder_count++;
				}

				if (folder_count > 0)
					subfolder_list.addElement(sub_folders[i].getName());
			}
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
		notifyStart();

		Vector new_folder_list = new Vector();
		new_folder_list.addElement(getDBFolder());
		for (int i = 0 ; i < folder_list.size() ; i++)
			new_folder_list.addElement(folder_list.elementAt(i));

		Folder folder = file_system.createHierarchicalFolder(new_folder_list);
		Folder[] sub_folders = folder.list();

		Vector list = new Vector();

		try {
			// Starts using the disk cache.
			XmlDBHolderCache.enable(true);
		} catch ( IOException exception ) {
			// Makes no problem.
		}

		for (int i = 0 ; i < sub_folders.length ; i++) {
			if (sub_folders[i].isFolder()) {
				Vector folder_list2 = new Vector();
				for (int j = 0 ; j < folder_list.size() ; j++)
					folder_list2.addElement(folder_list.elementAt(j));
				folder_list2.addElement(sub_folders[i].getName());

				CatalogStar star = null;

				try {
					// First, tries to read the star data in the catalog database.
					star = new CatalogDBManager(file_system, file_manager).getElement(folder_list2);
				} catch ( Exception exception ) {
					// Second, tries to read the star data in one of the 
					// XML report documents.
					XmlMagRecord[] records = getElements(sub_folders[i]);
					for (int j = 0 ; j < records.length ; j++) {
						try {
							XmlInformation info = file_manager.readInformation(records[j]);

							XmlStar xml_star = file_manager.readStar(info, records[j].getName());

							if (xml_star != null) {
								Vector stars = xml_star.getAllRecords();
								for (int k = 0 ; k < stars.size() ; k++) {
									CatalogStar catalog_star = null;

									if (stars.elementAt(k) instanceof StarImage) {
										// The astrometric error of the current XML document.
										double pos_error = info.getAstrometricErrorInArcsec();
										catalog_star = new DetectedStar((StarImage)stars.elementAt(k), pos_error);
									} else {
										catalog_star = (CatalogStar)stars.elementAt(k);
									}

									if (sub_folders[i].getName().equals(catalog_star.getStarFolder())) {
										Vector l = catalog_star.getHierarchicalFolders();
										if (folder_list.size() == l.size()) {
											boolean same = true;
											for (int n = 0 ; n < folder_list.size() ; n++) {
												if (((String)folder_list.elementAt(n)).equals((String)l.elementAt(n)) == false)
													same = false;
											}

											if (same) {
												star = catalog_star;
												break;
											}
										}
									}
								}
							}
						} catch ( IOException io_exception ) {
						}

						if (star != null)
							break;
					}
				}

				if (star == null) {
					notifyFailed(sub_folders[i].getName());
				} else {
					list.addElement(star);

					notifySucceeded(sub_folders[i].getName());
				}
			}
		}

		try {
			// Stops using the disk cache, and flushes to the files.
			XmlDBHolderCache.enable(false);
		} catch ( IOException exception ) {
			// Makes no problem.
		}

		notifyEnd(null);

		return list;
	}
}
