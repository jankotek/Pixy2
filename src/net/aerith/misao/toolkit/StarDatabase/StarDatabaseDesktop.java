/*
 * @(#)StarDatabaseDesktop.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.StarDatabase;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.io.*;
import net.aerith.misao.image.filter.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.pixy.*;
import net.aerith.misao.io.*;
import net.aerith.misao.io.filechooser.*;
import net.aerith.misao.database.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.toolkit.AdditionalIdentification.AdditionalIdentificationInternalFrame;
import net.aerith.misao.toolkit.AdditionalIdentification.AdditionalIdentificationOperation;

/**
 * The <code>StarDatabaseDesktop</code> represents a desktop to browse
 * the catalog database and the magnitude database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 May 4
 */

public class StarDatabaseDesktop extends BaseDesktop {
	/**
	 * This desktop.
	 */
	protected StarDatabaseDesktop desktop;

	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>StarDatabaseDesktop</code>.
	 */
	public StarDatabaseDesktop ( ) {
		desktop = this;

		pane = getContentPane();

		addWindowListener(new OpenWindowListener());
	}

	/**
	 * Initializes menu bar. A <code>JMenuBar</code> must be set to 
	 * this <code>JFrame</code> previously.
	 */
	public void initMenu ( ) {
		addFileMenu();

		super.initMenu();

		addHelpMenu();
	}

	/**
	 * Adds the <tt>File</tt> menus to the menu bar.
	 */
	public void addFileMenu ( ) {
		JMenu menu = addMenu("File");

		JMenuItem item = new JMenuItem("Register Catalog");
		item.addActionListener(new RegisterCatalogListener());
		menu.add(item);
	}

	/**
	 * Adds the <tt>Help</tt> menus to the menu bar.
	 */
	public void addHelpMenu ( ) {
	}

	/**
	 * Gets the monitor.
	 * @return the monitor.
	 */
	protected Monitor getMonitor ( ) {
		return monitor_set;
	}

	/**
	 * Identifies all XML files with the specified stars and registers
	 * the magnitude to the database.
	 * @param reader_add    the list of stars to identify with.
	 * @param reader_delete the list of stars to delete.
	 */
	protected void updateAllXmlFiles ( StarListReader reader_add, StarListReader reader_delete ) {
		try {
			InformationDBConductor conductor = new InformationDBConductor(getDBManager(), pane);

			AdditionalIdentificationOperation operation = new AdditionalIdentificationOperation(conductor, reader_add, reader_delete);
			operation.setDBManager(getDBManager());
			operation.setFileManager(getFileManager());
			operation.addMonitor(monitor_set);

			DefaultOperationObserver observer = new DefaultOperationObserver();
			operation.addObserver(observer);

			operation.perform();

			operation.deleteObserver(observer);

			Vector failed_list = observer.getFailedList();
			if (failed_list.size() > 0) {
				Vector list = new Vector();
				for (int i = 0 ; i < failed_list.size() ; i++) {
					XmlInformation info = (XmlInformation)failed_list.elementAt(i);
					list.addElement(info.getPath());
				}

				String header = "Failed to update the following XML files:";
				MessagesDialog messsages_dialog = new MessagesDialog(header, list);
				messsages_dialog.show(pane, "Warning", JOptionPane.WARNING_MESSAGE);
			}

			String message = "Succeeded.";
			JOptionPane.showMessageDialog(pane, message);
		} catch ( Exception exception ) {
			String message = "Failed to read database.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Shows the table to identifies with the specified stars and 
	 * registers the magnitude to the database.
	 * @param reader_add    the list of stars to identify with.
	 * @param reader_delete the list of stars to delete.
	 * @return the frame window, or null if failed.
	 */
	public AdditionalIdentificationInternalFrame showAdditionalIdentificationTable ( StarListReader reader_add, StarListReader reader_delete ) {
		try {
			CelestialDivisionMap map = new CelestialDivisionMap();
			CelestialDivisionMapContainer map_container = new CelestialDivisionMapContainer();

			reader_add.open();
			CatalogStar star = reader_add.readNext();
			while (star != null) {
				map.fill(star.getCoor(), star.getMaximumPositionErrorInArcsec() / 3600.0);
				map_container.add(star.getCoor(), star);

				star = reader_add.readNext();
			}
			reader_add.close();

			reader_delete.open();
			star = reader_delete.readNext();
			while (star != null) {
				map.fill(star.getCoor(), star.getMaximumPositionErrorInArcsec() / 3600.0);
				map_container.add(star.getCoor(), star);

				star = reader_delete.readNext();
			}
			reader_delete.close();

			Vector list = new Vector();

			InformationDBAccessor accessor = getDBManager().getInformationDBManager().getAccessor(map, 0.0, 99.9);

			XmlInformation info = accessor.getFirstElement();
			while (info != null) {
				// Adds XML files only when some of the registered 
				// catalog data are really in the images.
				boolean identified = false;

				Coor center_coor = ((XmlCenter)info.getCenter()).getCoor();
				double field_radius = info.getFieldRadiusInDegree();
				CelestialDivisionMap map_info = new CelestialDivisionMap();
				map_info.fill(center_coor, field_radius);

				CelestialDivisionMapContainerAccessor accessor2 = map_container.getAccessor(map_info);
				star = (CatalogStar)accessor2.getFirstElement();
				while (star != null) {
					Position position = info.mapCoordinatesToXY(star.getCoor());

					if (0 <= position.getX()  &&  position.getX() <= info.getSize().getWidth()  &&
						0 <= position.getY()  &&  position.getY() <= info.getSize().getHeight()) {
						identified = true;
						break;
					}

					star = (CatalogStar)accessor2.getNextElement();
				}

				if (identified)
					list.addElement(info);

				info = accessor.getNextElement();
			}

			XmlInformation[] infos = new XmlInformation[list.size()];
			for (int i = 0 ; i < list.size() ; i++)
				infos[i] = (XmlInformation)list.elementAt(i);

			AdditionalIdentificationInternalFrame frame = new AdditionalIdentificationInternalFrame(infos, this, reader_add, reader_delete);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(600, 400);
			frame.setTitle("Additional Identification and Registration to the Database");
			frame.setVisible(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setResizable(true);
			frame.setClosable(true);

			addFrame(frame);

			return frame;
		} catch ( IOException exception ) {
			String message = "Failed to read database.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		} catch ( QueryFailException exception ) {
			String message = "Failed to read the catalog.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}

		return null;
	}

	/**
	 * The <code>OpenWindowListener</code> is a listener class of
	 * opening this window.
	 */
	protected class OpenWindowListener extends WindowAdapter {
		/**
		 * Invoked when this window is opened.
		 * @param e contains the event status.
		 */
		public void windowOpened ( WindowEvent e ) {
			try {
				GlobalDBManager db_manager = getDBManager();

				CatalogTreeSelectionListener listener = new StarSelectionListener();

				JInternalFrame frame_catalog = new JInternalFrame();
				frame_catalog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				frame_catalog.setSize(300,500);
				frame_catalog.setTitle("Catalog Database Tree");
				frame_catalog.setVisible(true);
				frame_catalog.setMaximizable(true);
				frame_catalog.setIconifiable(true);
				frame_catalog.setResizable(true);

				DatabaseCatalogTree tree_catalog = new DatabaseCatalogTree(db_manager);
				tree_catalog.addCatalogTreeSelectionListener(listener);
				CatalogTreePanel panel = new CatalogTreePanel(tree_catalog);
				frame_catalog.getContentPane().add(new JScrollPane(panel));
				addFrame(frame_catalog);

				JInternalFrame frame_magnitude = new JInternalFrame();
				frame_magnitude.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				frame_magnitude.setSize(300,500);
				frame_magnitude.setTitle("Magnitude Database Tree");
				frame_magnitude.setVisible(true);
				frame_magnitude.setMaximizable(true);
				frame_magnitude.setIconifiable(true);
				frame_magnitude.setResizable(true);

				MagnitudeDatabaseTree tree_magnitude = new MagnitudeDatabaseTree(db_manager);
				tree_magnitude.addCatalogTreeSelectionListener(listener);
				panel = new CatalogTreePanel(tree_magnitude);
				frame_magnitude.getContentPane().add(new JScrollPane(panel));
				addFrame(frame_magnitude);

				Rectangle rect = frame_catalog.getBounds();
				frame_magnitude.setLocation((int)(rect.getX() + rect.getWidth()), (int)rect.getY());
			} catch ( IOException exception ) {
				String message = "Failed to read the database.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * The <code>RegisterCatalogListener</code> is a listener class of
	 * menu selection to register a catalog into the catalog database.
	 */
	protected class RegisterCatalogListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			Thread thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			try {
				Vector catalog_list = CatalogManager.getIdentificationCatalogReaderList();
				OpenCatalogDialog dialog = new OpenCatalogDialog(catalog_list);

				int answer = dialog.show(pane);
				if (answer == 0) {
					CatalogReader reader = dialog.getSelectedCatalogReader();

					if (reader.hasFovLimit()  ||  reader.isDateDependent()) {
						String message = "Impossible to register to the database.";
						JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}

					RegisterCatalogSettingDialog dialog2 = new RegisterCatalogSettingDialog();

					answer = dialog2.show(pane);
					if (answer == 0) {
						String[] paths = net.aerith.misao.util.Format.separatePath(dialog.getCatalogPath());
						for (int i = 0 ; i < paths.length ; i++) {
							try {
								reader.addURL(new File(paths[i]).toURI().toURL());
							} catch ( MalformedURLException exception ) {
							}
						}

						CatalogDBManager manager = getDBManager().getCatalogDBManager();

						StarListReader list_reader_add = new StarListReader();
						StarListReader list_reader_delete = new StarListReader();

						// Starts using the disk cache.
						XmlDBHolderCache.enable(true);

						reader.open();
						CatalogStar star = reader.readNext();
						while (star != null) {
							CatalogStar star2 = null;
							try {
								star2 = manager.getElement(star);
							} catch ( Exception exception ) {
								exception.printStackTrace();
							}

							// Checks if changed.
							if (star2 == null  ||  star.equals(star2) == false) {
								if (star2 != null)
									getMonitor().addMessage("Replaced: " + star.getName());

								if (dialog2.updatesCatalogDatabase()) {
									if (star2 != null)
										manager.deleteElement(star2);
									manager.addElement(star);
								}

								list_reader_add.addStar(star);
								if (star2 != null)
									list_reader_delete.addStar(star2);
							}

							star = reader.readNext();
						}
						reader.close();

						// Stops using the disk cache, and flushes to the files.
						XmlDBHolderCache.enable(false);

						if (dialog2.updatesCatalogDatabase()) {
							String message = "Completed.";
							JOptionPane.showMessageDialog(pane, message);
						}

						if (dialog2.updatesXmlFiles()) {
							if (dialog2.updatesAllXmlFiles()) {
								updateAllXmlFiles(list_reader_add, list_reader_delete);
							} else {
								showAdditionalIdentificationTable(list_reader_add, list_reader_delete);
							}
						}
					}
				}
			} catch ( Exception exception ) {
				String message = "Failed.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * The <code>StarSelectionListener</code> is a listener class of 
	 * star selection in the tree.
	 */
	protected class StarSelectionListener implements CatalogTreeSelectionListener {
		/** 
		 * Invoked when the root node is selected.
		 */
		public void selectAll ( ) {
		}

		/** 
		 * Invoked when the specified catalog category is selected.
		 * @param category_name the category name.
		 */
		public void selectCategory ( String category_name ) {
		}

		/** 
		 * Invoked when the specified catalog is selected.
		 * @param catalog_name the catalog name.
		 */
		public void selectCatalog ( String catalog_name ) {
		}

		/** 
		 * Invoked when the specified star is selected.
		 * @param star the star.
		 */
		public void selectStar ( Star star ) {
			JInternalFrame frame = new JInternalFrame();
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(300,250);
			frame.setTitle(star.getName());
			frame.setVisible(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setResizable(true);
			frame.setClosable(true);

			StarPanel panel = new StarPanel(star, desktop);
			frame.getContentPane().add(panel);
			addFrame(frame);
		}
	}
}
