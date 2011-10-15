/*
 * @(#)ImageDatabaseDesktop.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ImageDatabase;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.pixy.*;
import net.aerith.misao.io.*;
import net.aerith.misao.io.filechooser.*;
import net.aerith.misao.database.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.catalog.CatalogManager;
import net.aerith.misao.catalog.io.CatalogReader;
import net.aerith.misao.catalog.io.StarListReader;
import net.aerith.misao.toolkit.ImageInformation.ImageInformationInternalFrame;
import net.aerith.misao.toolkit.RecordSearch.RecordSearchInternalFrame;
import net.aerith.misao.toolkit.RecordSearch.RecordSearchOperation;

/**
 * The <code>ImageDatabaseDesktop</code> represents a desktop to 
 * browse the image database.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 May 4
 */

public class ImageDatabaseDesktop extends BaseDesktop {
	/**
	 * This desktop.
	 */
	protected ImageDatabaseDesktop desktop;

	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs an <code>ImageDatabaseDesktop</code>.
	 */
	public ImageDatabaseDesktop ( ) {
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

		addSearchMenu();

		super.initMenu();

		addHelpMenu();
	}

	/**
	 * Adds the <tt>File</tt> menus to the menu bar.
	 */
	public void addFileMenu ( ) {
		JMenu menu = addMenu("File");

		JMenuItem item = new JMenuItem("Register XML File");
		item.addActionListener(new RegisterXmlFileListener());
		menu.add(item);
	}

	/**
	 * Adds the <tt>Search</tt> menus to the menu bar.
	 */
	public void addSearchMenu ( ) {
		JMenu menu = addMenu("Search");

		JMenuItem item = new JMenuItem("Search Star");
		item.addActionListener(new SearchStarListener());
		menu.add(item);

		item = new JMenuItem("Search Identified Stars");
		item.addActionListener(new SearchIdentifiedStarsListener());
		menu.add(item);
	}

	/**
	 * Adds the <tt>Help</tt> menus to the menu bar.
	 */
	public void addHelpMenu ( ) {
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

				InformationTreeSelectionListener listener = new InformationSelectionListener();

				JInternalFrame frame_date = new JInternalFrame();
				frame_date.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				frame_date.setSize(300,500);
				frame_date.setTitle("Date Oriented Tree");
				frame_date.setVisible(true);
				frame_date.setMaximizable(true);
				frame_date.setIconifiable(true);
				frame_date.setResizable(true);

				DatabaseInformationTree tree_date = new DatabaseInformationTree(db_manager.getInformationDBManager(), DatabaseInformationTree.DATE_ORIENTED);
				tree_date.addInformationTreeSelectionListener(listener);
				frame_date.getContentPane().add(new JScrollPane(tree_date));
				addFrame(frame_date);

				JInternalFrame frame_path = new JInternalFrame();
				frame_path.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				frame_path.setSize(300,500);
				frame_path.setTitle("Path Oriented Tree");
				frame_path.setVisible(true);
				frame_path.setMaximizable(true);
				frame_path.setIconifiable(true);
				frame_path.setResizable(true);

				DatabaseInformationTree tree_path = new DatabaseInformationTree(db_manager.getInformationDBManager(), DatabaseInformationTree.PATH_ORIENTED);
				tree_path.addInformationTreeSelectionListener(listener);
				frame_path.getContentPane().add(new JScrollPane(tree_path));
				addFrame(frame_path);

				Rectangle rect = frame_date.getBounds();
				frame_path.setLocation((int)(rect.getX() + rect.getWidth()), (int)rect.getY());
			} catch ( IOException exception ) {
				String message = "Failed to read the database.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * The <code>RegisterXmlFileListener</code> is a listener class of 
	 * menu selection to register an XML report document file into the 
	 * image database.
	 */
	protected class RegisterXmlFileListener implements ActionListener, Runnable {
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
			CommonFileChooser file_chooser = new CommonFileChooser();
			file_chooser.setDialogTitle("Open an XML file.");
			file_chooser.setMultiSelectionEnabled(false);
			file_chooser.addChoosableFileFilter(new XmlFilter());

			if (file_chooser.showOpenDialog(pane) == JFileChooser.APPROVE_OPTION) {
				File xml_file = file_chooser.getSelectedFile();

				// Reads the XML document.
				XmlReport report = new XmlReport();
				try {
					report.read(xml_file);
				} catch ( IOException exception ) {
					String message = "Failed to read the XML file.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				try {
					GlobalDBManager db_manager = getDBManager();

					Vector catalog_list = ((XmlData)report.getData()).getIdentifiedCatalogList();

					CatalogSelectionDialog dialog = new CatalogSelectionDialog(catalog_list);
					int answer = dialog.show(pane);

					if (answer == 0) {
						boolean update_reported_mag = false;

						try {
							db_manager.addReport(xml_file, report);
						} catch ( DuplicatedException exception ) {
							String message = "Already exists in the database. Replace it?";
							if (0 != JOptionPane.showConfirmDialog(pane, message, "Confirmation", JOptionPane.YES_NO_OPTION)) {
								return;
							}

							// Replaces the image information.

							InformationDBUpdateSettingDialog dialog2 = new InformationDBUpdateSettingDialog(InformationDBUpdateSettingDialog.MODE_REPLACE);
							answer = dialog2.show(pane);

							if (answer != 0)
								return;

							db_manager.enableReportedMagnitudeUpdate(dialog2.updatesReportedMagnitude());
							update_reported_mag = dialog2.updatesReportedMagnitude();

							XmlInformation duplicated_info = (XmlInformation)exception.getDuplicatedObject();
							if (null == db_manager.getInformationDBManager().deleteElement(duplicated_info.getPath())) {
								message = "Failed to delete " + duplicated_info.getPath();
								JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
								return;
							}

							db_manager.addReport(xml_file, report);
						}

						DefaultOperationObserver observer = new DefaultOperationObserver();
						db_manager.addObserver(observer);

						catalog_list = dialog.getSelectedCatalogList();
						db_manager.addMagnitude(report, catalog_list);

						db_manager.deleteObserver(observer);

						Vector failed_list = observer.getFailedList();
						if (failed_list.size() > 0) {
							String header = "Magnitude of the following stars are failed to register:";
							MessagesDialog messsages_dialog = new MessagesDialog(header, failed_list);
							messsages_dialog.show(pane, "Warning", JOptionPane.WARNING_MESSAGE);
						}

						Vector warned_list = observer.getWarnedList();
						if (warned_list.size() > 0) {
							String header = "Magnitude of the following stars are already reported and failed to replace:";
							if (update_reported_mag)
								header = "Magnitude of the following stars are already reported:";
							MessagesDialog messsages_dialog = new MessagesDialog(header, warned_list);
							messsages_dialog.show(pane, "Warning", JOptionPane.WARNING_MESSAGE);
						}

						String message = "Succeeded to register " + xml_file.getPath();
						JOptionPane.showMessageDialog(pane, message);
					}

					return;
				} catch ( DuplicatedException exception ) {
					String message = "Already exists in the database.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				} catch ( DocumentIncompleteException exception ) {
					String message = "Failed. Please set " + exception.getMessage() + ".";
					if (exception.getMessage().equals("<date>"))
						message = "Failed. Please set image date.";
					if (exception.getMessage().equals("<observer>"))
						message = "Failed. Please set observer.";
					if (exception.getMessage().equals("<path>"))
						message = "Failed. Please save as XML file.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				} catch ( IOException exception ) {
					String message = "Failed to register " + xml_file.getPath();
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * The <code>SearchStarListener</code> is a listener class of 
	 * menu selection to search data of a star based on the R.A. and
	 * Decl. 
	 */
	protected class SearchStarListener implements ActionListener, Runnable {
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
			SearchStarSettingDialog dialog = new SearchStarSettingDialog();
			int answer = dialog.show(pane);

			if (answer == 0) {
				TargetStar target_star = new TargetStar(dialog.getCoor(), dialog.getRadius() / 3600.0);

				XmlReportQueryCondition query_condition = new XmlReportQueryCondition();
				query_condition.setLimitingMagnitude(dialog.getBrighterLimit(), dialog.getFainterLimit());

				try {
					Vector list = new Vector();

					InformationDBAccessor accessor = getDBManager().getInformationDBManager().getAccessor(target_star.getCoor(), query_condition.getBrighterLimit(), query_condition.getFainterLimit());

					XmlInformation info = accessor.getFirstElement();
					while (info != null) {
						if (query_condition.accept(info)) {
							Position position = info.mapCoordinatesToXY(target_star.getCoor());

							if (0 <= position.getX()  &&  position.getX() <= info.getSize().getWidth()  &&
								0 <= position.getY()  &&  position.getY() <= info.getSize().getHeight()) {
								list.addElement(info);
							}
						}
						info = accessor.getNextElement();
					}

					XmlInformation[] infos = new XmlInformation[list.size()];
					for (int i = 0 ; i < list.size() ; i++)
						infos[i] = (XmlInformation)list.elementAt(i);

					RecordSearchInternalFrame frame = new RecordSearchInternalFrame(infos, target_star, desktop);
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					frame.setSize(600, 400);
					frame.setTitle("Search for " + target_star.getCoor().getOutputString());
					frame.setVisible(true);
					frame.setMaximizable(true);
					frame.setIconifiable(true);
					frame.setResizable(true);
					frame.setClosable(true);

					addFrame(frame);
				} catch ( IOException exception ) {
					String message = "Failed to read database.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * The <code>SearchIdentifiedStarsListener</code> is a listener 
	 * class of menu selection to search data of stars identified with 
	 * a specified catalog.
	 */
	protected class SearchIdentifiedStarsListener implements ActionListener, Runnable {
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
			Vector catalog_list = CatalogManager.getIdentificationCatalogReaderList();
			SearchIdentifiedStarsSettingDialog dialog = new SearchIdentifiedStarsSettingDialog(catalog_list);
			int answer = dialog.show(pane);

			if (answer == 0) {
				CatalogReader reader = dialog.getSelectedCatalogReader();

				String[] paths = net.aerith.misao.util.Format.separatePath(dialog.getCatalogPath());
				for (int i = 0 ; i < paths.length ; i++) {
					try {
						reader.addURL(new File(paths[i]).toURI().toURL());
					} catch ( MalformedURLException exception ) {
						System.err.println(exception);
					}
				}

				String message = null;

				try {
					reader.open();

					CatalogStar target_star = reader.readNext();
					while (target_star != null) {
						XmlReportQueryCondition query_condition = new XmlReportQueryCondition();
						query_condition.setLimitingMagnitude(dialog.getBrighterLimit(), dialog.getFainterLimit());

						SearchIdentifiedStarsConductor conductor = new SearchIdentifiedStarsConductor(target_star, query_condition);
						RecordSearchOperation operation = new RecordSearchOperation(conductor, target_star, desktop.getFileManager());

						try {
							operation.perform();
						} catch ( Exception exception ) {
							message = "Failed to read database.";
							throw exception;
						}

						ObservationRecord[] records = operation.getRecords();
						desktop.showObservationTable(target_star, records);

						target_star = reader.readNext();
					}

					reader.close();

					message = "Succeeded.";
					JOptionPane.showMessageDialog(pane, message);
				} catch ( Exception exception ) {
					if (message == null)
						message = "Failed to read the catalog.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * The <code>InformationSelectionListener</code> is a listener 
	 * class of XML report document selection in the tree.
	 */
	protected class InformationSelectionListener implements InformationTreeSelectionListener {
		/** 
		 * Invoked when the specified image information is selected.
		 * @param info the image information.
		 */
		public void select ( XmlInformation info ) {
			ImageInformationInternalFrame frame = new ImageInformationInternalFrame(info, desktop);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(400,500);
			frame.setTitle(info.getPath());
			frame.setVisible(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setResizable(true);
			frame.setClosable(true);

			addFrame(frame);
		}
	}

	/**
	 * The <code>SearchIdentifiedStarsConductor</code> is a conductor 
	 * of multi task operation to search data identified with a 
	 * specified star.
	 */
	protected class SearchIdentifiedStarsConductor implements MultiTaskConductor {
		/**
		 * The target star.
		 */
		CatalogStar target_star;

		/**
		 * The query condition.
		 */
		XmlReportQueryCondition query_condition;

		/**
		 * Constructs a <code>SearchIdentifiedStarsConductor</code>.
		 * @param target_star     the target star.
		 * @param query_condition the query condition.
		 */
		public SearchIdentifiedStarsConductor ( CatalogStar target_star, XmlReportQueryCondition query_condition ) {
			this.target_star = target_star;
			this.query_condition = query_condition;
		}

		/**
		 * Gets the content pane.
		 * @return the pane.
		 */
		public Container getPane ( ) {
			return pane;
		}

		/**
		 * Returns true if the objects are ready to be operated.
		 * @return true if the objects are ready to be operated.
		 */
		public boolean ready ( ) {
			return true;
		}

		/**
		 * Operates the specified operation on several objects.
		 * @param operation the operation.
		 * @exception InterruptedException if the operatoin is stopped.
		 * @exception Exception if an error occurs.
		 */
		public void operate ( MultiTaskOperation operation )
			throws InterruptedException, Exception
		{
			InformationDBAccessor accessor = getDBManager().getInformationDBManager().getAccessor(target_star.getCoor(), query_condition.getBrighterLimit(), query_condition.getFainterLimit());

			XmlInformation info = accessor.getFirstElement();
			while (info != null) {
				if (query_condition.accept(info)) {
					Position position = info.mapCoordinatesToXY(target_star.getCoor());

					if (0 <= position.getX()  &&  position.getX() <= info.getSize().getWidth()  &&
						0 <= position.getY()  &&  position.getY() <= info.getSize().getHeight()) {
						operation.operate(info);
					}
				}
				info = accessor.getNextElement();
			}
		}
	}
}
