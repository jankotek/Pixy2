/*
 * @(#)GlobalDBManager.java
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
 * The <code>GlobalDBManager</code> represents a manager of all the
 * databases of the PIXY System. The database consists of the image
 * information database, the catalog database and the magnitude 
 * database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 December 27
 */

public class GlobalDBManager extends OperationObservable {
	/**
	 * The file system of the database.
	 */
	protected FileSystem file_system;

	/**
	 * The image information database.
	 */
	protected InformationDBManager info_manager;

	/**
	 * The catalog database.
	 */
	protected CatalogDBManager catalog_manager;

	/**
	 * The magnitude database.
	 */
	protected MagnitudeDBManager mag_manager;

	/**
	 * The file manager.
	 */
	protected FileManager file_manager;

	/**
	 * The set of monitors.
	 */
	protected MonitorSet monitor_set;

	/**
	 * True when to update already reported magnitude.
	 */
	protected boolean update_reported_mag = false;

	/**
	 * True in the case of raw database of detected stars.
	 */
	protected boolean raw_database = false;

	/**
	 * Constructs a <code>GlobalDBManager</code> in the system
	 * default folder.
	 * @exception IOException if I/O error occurs.
	 */
	public GlobalDBManager ( )
		throws IOException
	{
		this(new DiskFileSystem(new File(FileManager.unitePath(net.aerith.misao.pixy.Properties.getHome().getPath(), net.aerith.misao.pixy.Properties.getDatabaseDirectoryName()))));
	}

	/**
	 * Constructs a <code>GlobalDBManager</code> in the specified 
	 * file system.
	 * @param file_system the file system to create the database.
	 * @exception IOException if I/O error occurs.
	 */
	public GlobalDBManager ( FileSystem file_system )
		throws IOException
	{
		this.file_system = file_system;

		file_manager = new FileManager();

		info_manager = new InformationDBManager(file_system, file_manager);
		catalog_manager = new CatalogDBManager(file_system, file_manager);
		mag_manager = new MagnitudeDBManager(file_system, file_manager);

		monitor_set = new MonitorSet();
	}

	/**
	 * Sets a file manager.
	 * @param file_manager the file manager.
	 */
	public void setFileManager ( FileManager file_manager ) {
		this.file_manager = file_manager;

		info_manager.setFileManager(file_manager);
		catalog_manager.setFileManager(file_manager);
		mag_manager.setFileManager(file_manager);
	}

	/**
	 * Adds a monitor.
	 * @param monitor the monitor.
	 */
	public void addMonitor ( Monitor monitor ) {
		monitor_set.addMonitor(monitor);
	}

	/**
	 * Enables/disables to update already reported magnitude.
	 * @param flag true when to update already reported magnitude.
	 */
	public void enableReportedMagnitudeUpdate ( boolean flag ) {
		update_reported_mag = flag;
	}

	/**
	 * Sets the flag to indicate whether to be a raw database of 
	 * detected stars.
	 * @param flag true in the case of raw database of detected stars.
	 */
	public void setRawDatabase ( boolean flag ) {
		raw_database = flag;
	}

	/**
	 * Discards the database. All files and directories in the 
	 * database folder are removed.
	 * @exception IOException if I/O error occurs.
	 */
	public void discard ( )
		throws IOException
	{
		file_system.discard();
	}

	/**
	 * Gets the image information database manager.
	 * @return the image information database manager.
	 */
	public InformationDBManager getInformationDBManager ( ) {
		return info_manager;
	}

	/**
	 * Gets the catalog database manager.
	 * @return the catalog database manager.
	 */
	public CatalogDBManager getCatalogDBManager ( ) {
		return catalog_manager;
	}

	/**
	 * Gets the magnitude database manager.
	 * @return the magnitude database manager.
	 */
	public MagnitudeDBManager getMagnitudeDBManager ( ) {
		return mag_manager;
	}

	/**
	 * Adds the XML report document.
	 * @param xml_file the XML file of the specified report document.
	 * @param report   the XML document.
	 * @exception IOException if I/O error occurs.
	 * @exception DocumentIncompleteException if some required data in
	 * the specified image information is not recorded.
	 * @exception DuplicatedException if the specified data is already
	 * in the database.
	 */
	public void addReport ( File xml_file, XmlReport report )
		throws IOException, DocumentIncompleteException, DuplicatedException
	{
		// In order not to keep the whole XML report document on the memory
		// even if the database is constructed on memory, here clones the
		// information element.
		XmlInformation info = new XmlInformation((XmlInformation)report.getInformation());
		info_manager.addElement(xml_file, info);

		// Records the relative path of the XML file.
		report.getInformation().setPath(info.getPath());
	}

	/**
	 * Adds only the information element in the XML report document
	 * into the database. This method is only for the special use. In
	 * general, use the addReport method.
	 * @param xml_file the XML file of the specified report document.
	 * @param info     the XML information element.
	 * @exception IOException if I/O error occurs.
	 * @exception DocumentIncompleteException if some required data in
	 * the specified image information is not recorded.
	 * @exception DuplicatedException if the specified data is already
	 * in the database.
	 */
	public void addInformation ( File xml_file, XmlInformation info )
		throws IOException, DocumentIncompleteException, DuplicatedException
	{
		info_manager.addElement(xml_file, info);
	}

	/**
	 * Adds the magnitude data in the specified XML report document 
	 * into the magnitude database.
	 * @param report       the XML document.
	 * @param catalog_list the list of catalog names to add the 
	 * magnitude into the database.
	 * @exception IOException if I/O error occurs.
	 * @exception DocumentIncompleteException if some required data in
	 * the specified image information is not recorded.
	 */
	public void addMagnitude ( XmlReport report, Vector catalog_list )
		throws IOException, DocumentIncompleteException
	{
		updateMagnitude(report, catalog_list, true);
	}

	/**
	 * Deletes the magnitude data in the specified XML report document 
	 * from the magnitude database.
	 * @param report       the XML document.
	 * @param catalog_list the list of catalog names to delete the 
	 * magnitude from the database.
	 * @exception IOException if I/O error occurs.
	 * @exception DocumentIncompleteException if some required data in
	 * the specified image information is not recorded.
	 */
	public void deleteMagnitude ( XmlReport report, Vector catalog_list )
		throws IOException, DocumentIncompleteException
	{
		updateMagnitude(report, catalog_list, false);
	}

	/**
	 * Updates the magnitude data in the specified XML report document
	 * in the magnitude database.
	 * @param report       the XML document.
	 * @param catalog_list the list of catalog names to update the 
	 * magnitude in the database.
	 * @param add_flag     true when to add the magnitude data.
	 * @exception IOException if I/O error occurs.
	 * @exception DocumentIncompleteException if some required data in
	 * the specified image information is not recorded.
	 */
	private void updateMagnitude ( XmlReport report, Vector catalog_list, boolean add_flag )
		throws IOException, DocumentIncompleteException
	{
		notifyStart();

		Hashtable hash = new Hashtable();
		for (int i = 0 ; i < catalog_list.size() ; i++)
			hash.put((String)catalog_list.elementAt(i), this);

		XmlInformation info = (XmlInformation)report.getInformation();
		XmlData data = (XmlData)report.getData();

		int width = info.getSize().getWidth();
		int height = info.getSize().getHeight();

		// The astrometric error of the current XML document.
		double pos_error = ((XmlInformation)report.getInformation()).getAstrometricErrorInArcsec();

		int star_count = data.getStarCount();
		XmlStar[] xml_stars = (XmlStar[])data.getStar();
		for (int i = 0 ; i < star_count ; i++) {
			StarImage star_image = xml_stars[i].getStarImage();
			Vector records = xml_stars[i].getAllRecords();

			// In the case the star is identified with several detected stars,
			// recorded as blending. 
			// That is only in the case of creating temporary database.
			Vector blending_list = new Vector();
			for (int j = 0 ; j < records.size() ; j++) {
				Star star = (Star)records.elementAt(j);
				if (star instanceof DetectedStar)
					blending_list.addElement(star);
			}

			for (int j = 0 ; j < records.size() ; j++) {
				Star star = (Star)records.elementAt(j);

				if (star instanceof StarImage) {
					if (raw_database)
						continue;

					star = new DetectedStar((StarImage)star, pos_error);
				}

				CatalogStar catalog_star = (CatalogStar)star;

				if (hash.get(catalog_star.getCatalogName()) != null) {
					try {
						XmlMagRecord record = new XmlMagRecord(info, xml_stars[i]);

						// Sets the (x,y) position. When the star is
						// detected, the measured position is recorded.
						// Otherwise, the calculated position of the 
						// catalog star is recorded.
						Position position = null;
						if (star_image == null)
							position = catalog_star;
						else
							position = star_image;
						record.setPosition(new XmlPosition(position));

						// In the case the star is identified with several detected stars,
						//  recorded as blending. 
						// That is only in the case of creating temporary database.
						for (int k = 0 ; k < blending_list.size() ; k++) {
							CatalogStar s = (CatalogStar)blending_list.elementAt(k);
							if (s != catalog_star)
								record.addBlending(s);
						}

						// In the case of unofficial images.
						if (info.getUnofficial() != null)
							record.setUnofficial(new XmlUnofficial());

						// Replaces the old record.
						XmlMagRecord old_record = mag_manager.deleteElement(catalog_star, record);
						if (old_record != null) {
							// If the record is already reported, it cannot be replaced.
							if (old_record.getReported().length > 0) {
								notifyWarned(catalog_star);

								// Does not replace or delete magnitude record if already reporeted.
								if (update_reported_mag == false) {
									mag_manager.addElement(catalog_star, old_record);
									continue;
								}
							}
						}

						if (add_flag)
							mag_manager.addElement(catalog_star, record);

						notifySucceeded(catalog_star);
					} catch ( IOException exception ) {
						notifyFailed(catalog_star);
					}
				}
			}
		}

		notifyEnd(null);
	}
}
