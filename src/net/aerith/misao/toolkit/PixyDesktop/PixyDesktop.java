/*
 * @(#)PixyDesktop.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.PixyDesktop;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.io.*;
import net.aerith.misao.image.filter.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.io.*;
import net.aerith.misao.io.filechooser.*;
import net.aerith.misao.database.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.pixy.Resource;
import net.aerith.misao.pixy.identification.DefaultIdentifier;
import net.aerith.misao.toolkit.ImageInformation.ImageInformationTable;
import net.aerith.misao.toolkit.IdentificationReport.IdentificationReportPane;
import net.aerith.misao.toolkit.MultipleCatalogChart.IdentifiedStarChartPane;
import net.aerith.misao.toolkit.Astrometry.AstrometryPane;
import net.aerith.misao.toolkit.Photometry.PhotometryPane;

/**
 * The <code>PixyDesktop</code> represents a desktop of the PIXY 
 * system. This is the base class of the <code>PixyOperationDesktop</code>
 * and <code>PixyReviewDesktop</code>.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 6
 */

public class PixyDesktop extends Desktop implements ReportDocumentUpdatedListener {
	/**
	 * This desktop.
	 */
	protected PixyDesktop desktop;

	/**
	 * The XML document.
	 */
	protected XmlReport report = null;

	/**
	 * The XML document file.
	 */
	protected File xml_file = null;

	/**
	 * The image to examine.
	 */
	protected MonoImage image = null;

	/*
	 * The image window.
	 */
	protected JInternalFrame image_window = null;

	/*
	 * The detected stars window.
	 */
	protected JInternalFrame detected_stars_window;

	/*
	 * The detected stars chart.
	 */
	protected ChartComponent detected_chart;

	/*
	 * The catalog stars window.
	 */
	protected JInternalFrame catalog_stars_window;

	/*
	 * The catalog data chart.
	 */
	protected ChartComponent catalog_chart;

	/*
	 * The image information window.
	 */
	protected JInternalFrame image_info_window = null;

	/*
	 * The image information table.
	 */
	protected ImageInformationTable image_info_table = null;

	/*
	 * The thread of the current operation.
	 */
	protected Thread thread = null;

	/*
	 * The list of listeners of the XML report document update.
	 */
	protected Vector listeners;

	/*
	 * The menu item to save the result in XML file.
	 */
	protected JMenuItem menu_save_xml;

	/*
	 * The menu item to save the result in PXF file.
	 */
	protected JMenuItem menu_save_pxf;

	/*
	 * The menu item to register to the database.
	 */
	protected JMenuItem menu_register_db;

	/*
	 * The menu item to identify.
	 */
	protected JMenuItem menu_identify;

	/*
	 * The menu item to identify from the catalog database.
	 */
	protected JMenuItem menu_identify_database;

	/*
	 * The menu item to show the identification report.
	 */
	protected JMenuItem menu_identification_report;

	/*
	 * The menu item to show the identification chart.
	 */
	protected JMenuItem menu_identification_chart;

	/*
	 * The menu item to operate the astrometry.
	 */
	protected JMenuItem menu_astrometry;

	/*
	 * The menu item to operate the photometry.
	 */
	protected JMenuItem menu_photometry;

	/*
	 * The menu item to report magnitude to VSNET/VSOLJ.
	 */
	protected JMenuItem menu_report_vsnet;

	/*
	 * The menu item to set the image date.
	 */
	protected JMenuItem menu_set_date;

	/*
	 * The menu item to set the observer.
	 */
	protected JMenuItem menu_set_observer;

	/*
	 * The menu item to set the filter.
	 */
	protected JMenuItem menu_set_filter;

	/*
	 * The menu item to set the chip.
	 */
	protected JMenuItem menu_set_chip;

	/*
	 * The menu item to set the instruments.
	 */
	protected JMenuItem menu_set_instruments;

	/*
	 * True when to show the step-by-step help message.
	 */
	protected boolean help_message_enabled = true;

	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs a <code>PixyDesktop</code>.
	 */
	public PixyDesktop ( ) {
		desktop = this;

		pane = getContentPane();

		listeners = new Vector();
	}

	/**
	 * Initializes menu bar. A <code>JMenuBar</code> must be set to 
	 * this <code>JFrame</code> previously.
	 */
	public void initMenu ( ) {
		addFileMenu();

		addOperationMenu();

		addImageMenu();

		addAnalysisMenu();

		addParameterMenu();

		super.initMenu();

		addHelpMenu();
	}

	/**
	 * Adds the <tt>File</tt> menus to the menu bar.
	 */
	public void addFileMenu ( ) {
		JMenu menu = addMenu("File");
		if (menu.getItemCount() > 0)
			menu.addSeparator();

		menu_save_xml = new JMenuItem("Save Result As XML");
		menu_save_xml.addActionListener(new SaveAsXmlListener());
		menu.add(menu_save_xml);

		menu_save_pxf = new JMenuItem("Save Result As PXF");
		menu_save_pxf.addActionListener(new SaveAsPxfListener());
		menu.add(menu_save_pxf);

		menu.addSeparator();

		menu_register_db = new JMenuItem("Register to Database");
		menu_register_db.addActionListener(new RegisterToDatabaseListener());
		menu.add(menu_register_db);

		menu_save_xml.setEnabled(false);
		menu_save_pxf.setEnabled(false);
		menu_register_db.setEnabled(false);
	}

	/**
	 * Adds the <tt>Operaton</tt> menus to the menu bar.
	 */
	public void addOperationMenu ( ) {
	}

	/**
	 * Adds the <tt>Image</tt> menus to the menu bar.
	 */
	public void addImageMenu ( ) {
	}

	/**
	 * Adds the <tt>Analysis</tt> menus to the menu bar.
	 */
	public void addAnalysisMenu ( ) {
		JMenu menu = addMenu("Analysis");

		menu_identify = new JMenuItem("Identify");
		menu_identify.addActionListener(new IdentifyListener());
		menu.add(menu_identify);

		menu_identify_database = new JMenuItem("Identify from Database");
		menu_identify_database.addActionListener(new IdentifyFromDatabaseListener());
		menu.add(menu_identify_database);

		menu.addSeparator();

		menu_identification_report = new JMenuItem("Identification Report");
		menu_identification_report.addActionListener(new IdentificationReportListener());
		menu.add(menu_identification_report);

		menu_identification_chart = new JMenuItem("Identification Chart");
		menu_identification_chart.addActionListener(new IdentificationChartListener());
		menu.add(menu_identification_chart);

		menu.addSeparator();

		menu_astrometry = new JMenuItem("Astrometry");
		menu_astrometry.addActionListener(new AstrometryListener());
		menu.add(menu_astrometry);

		menu_photometry = new JMenuItem("Photometry");
		menu_photometry.addActionListener(new PhotometryListener());
		menu.add(menu_photometry);

		menu.addSeparator();

		menu_report_vsnet = new JMenuItem("Magnitude Report to VSNET/VSOLJ");
		menu_report_vsnet.addActionListener(new ReportToVsnetListener());
		menu.add(menu_report_vsnet);

		menu_identify.setEnabled(false);
		menu_identify_database.setEnabled(false);
		menu_identification_report.setEnabled(false);
		menu_identification_chart.setEnabled(false);
		menu_astrometry.setEnabled(false);
		menu_photometry.setEnabled(false);
		menu_report_vsnet.setEnabled(false);
	}

	/**
	 * Adds the <tt>Parameter</tt> menus to the menu bar.
	 */
	public void addParameterMenu ( ) {
		JMenu menu = addMenu("Parameter");

		menu_set_date = new JMenuItem("Set Image Date");
		menu_set_date.addActionListener(new SetDateListener());
		menu.add(menu_set_date);

		menu_set_observer = new JMenuItem("Set Observer");
		menu_set_observer.addActionListener(new SetObserverListener());
		menu.add(menu_set_observer);

		menu_set_filter = new JMenuItem("Set Magnitude System Code");
		menu_set_filter.addActionListener(new SetMagnitudeSystemCodeListener());
		menu.add(menu_set_filter);

		menu_set_chip = new JMenuItem("Set Chip");
		menu_set_chip.addActionListener(new SetChipListener());
		menu.add(menu_set_chip);

		menu_set_instruments = new JMenuItem("Set Instruments");
		menu_set_instruments.addActionListener(new SetInstrumentsListener());
		menu.add(menu_set_instruments);

		menu_set_date.setEnabled(false);
		menu_set_observer.setEnabled(false);
		menu_set_filter.setEnabled(false);
		menu_set_chip.setEnabled(false);
		menu_set_instruments.setEnabled(false);
	}

	/**
	 * Adds the <tt>Help</tt> menus to the menu bar.
	 */
	public void addHelpMenu ( ) {
	}

	/**
	 * Enables manus based on the XML document.
	 */
	public void enableXmlMenus ( ) {
		menu_save_xml.setEnabled(true);
		menu_save_pxf.setEnabled(true);
		menu_register_db.setEnabled(true);

		menu_identify.setEnabled(true);
		menu_identify_database.setEnabled(true);
		menu_identification_report.setEnabled(true);
		menu_identification_chart.setEnabled(true);
		menu_astrometry.setEnabled(true);
		menu_photometry.setEnabled(true);
		menu_report_vsnet.setEnabled(true);

		menu_set_date.setEnabled(true);
		menu_set_observer.setEnabled(true);
		menu_set_filter.setEnabled(true);
		menu_set_chip.setEnabled(true);
		menu_set_instruments.setEnabled(true);
	}

	/*
	 * Gets the monitor set.
	 * @return the monitor set.
	 */
	protected MonitorSet getMonitorSet ( ) {
		return monitor_set;
	}

	/*
	 * Enables/Disables the step-by-step help message.
	 * @param flag true when to enable.
	 */
	public void setHelpMessageEnabled ( boolean flag ) {
		help_message_enabled = flag;
	}

	/**
	 * Gets the color of the specified type of pair.
	 * @param type the type of pair.
	 * @return the color of the specified type of pair.
	 */
	public static Color getTypeColor ( String type ) {
		if (type.equals("STR"))
			return Color.green;
		if (type.equals("NEW"))
			return Color.red;
		if (type.equals("ERR"))
			return Color.red;

		return Color.black;
	}

	/*
	 * Opens the image information table.
	 */
	protected void showImageInformationTable ( ) {
		if (report == null)
			return;
		XmlInformation info = (XmlInformation)report.getInformation();

		if (image_info_table == null) {
			image_info_table = new ImageInformationTable(info);
		} else {
			image_info_table.updateContents(info);
		}

		if (image_info_window == null) {
			image_info_window = new JInternalFrame();
			image_info_window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			image_info_window.setSize(400, 500);
			image_info_window.setTitle("Image Information");
			image_info_window.setVisible(true);
			image_info_window.setMaximizable(true);
			image_info_window.setIconifiable(true);
			image_info_window.setResizable(true);

			image_info_window.getContentPane().add(new JScrollPane(image_info_table));
			addFrame(image_info_window);
		}

		image_info_window.repaint();
	}

	/**
	 * Invoked when the measured magnitude of the detected stars are
	 * updated.
	 * @param updated_report the XML report document.
	 */
	public void photometryUpdated (XmlReport updated_report ) {
		// Updates the detected stars chart and the catalog stars chart.
		updateCharts();

		// Updates the image information table.
		showImageInformationTable();

		// Invokes other listeners.
		for (int i = 0 ; i < listeners.size() ; i++) {
			ReportDocumentUpdatedListener listener = (ReportDocumentUpdatedListener)listeners.elementAt(i);
			listener.photometryUpdated(updated_report);
		}
	}

	/**
	 * Invoked when the measured position of the detected stars are
	 * updated.
	 * @param updated_report the XML report document.
	 */
	public void astrometryUpdated ( XmlReport updated_report ) {
		// Updates the detected stars chart and the catalog stars chart.
		updateCharts();

		// Updates the image information table.
		showImageInformationTable();

		// Invokes other listeners.
		for (int i = 0 ; i < listeners.size() ; i++) {
			ReportDocumentUpdatedListener listener = (ReportDocumentUpdatedListener)listeners.elementAt(i);
			listener.astrometryUpdated(updated_report);
		}
	}

	/**
	 * Invoked when some stars are added, removed or replaced.
	 * @param updated_report the XML report document.
	 */
	public void starsUpdated ( XmlReport updated_report ) {
		// Updates the detected stars chart and the catalog stars chart.
		updateCharts();

		// Updates the image information table.
		showImageInformationTable();

		// Invokes other listeners.
		for (int i = 0 ; i < listeners.size() ; i++) {
			ReportDocumentUpdatedListener listener = (ReportDocumentUpdatedListener)listeners.elementAt(i);
			listener.starsUpdated(updated_report);
		}
	}

	/**
	 * Invoked when the image date is updated.
	 * @param updated_report the XML report document.
	 */
	public void dateUpdated ( XmlReport updated_report ) {
		// Updates the image information table.
		showImageInformationTable();

		// Invokes other listeners.
		for (int i = 0 ; i < listeners.size() ; i++) {
			ReportDocumentUpdatedListener listener = (ReportDocumentUpdatedListener)listeners.elementAt(i);
			listener.dateUpdated(updated_report);
		}
	}

	/**
	 * Invoked when a secondary record, like instruments, is updated.
	 * @param updated_report the XML report document.
	 */
	public void recordUpdated ( XmlReport updated_report ) {
		// Updates the image information table.
		showImageInformationTable();

		// Invokes other listeners.
		for (int i = 0 ; i < listeners.size() ; i++) {
			ReportDocumentUpdatedListener listener = (ReportDocumentUpdatedListener)listeners.elementAt(i);
			listener.recordUpdated(updated_report);
		}
	}

	/**
	 * Updates the detected stars chart and the catalog stars chart.
	 */
	private void updateCharts ( ) {
		double limit_mag = (double)report.getInformation().getLimitingMag();

		String star_class = CatalogManager.getCatalogStarClassName(report.getInformation().getBaseCatalog());

		XmlData data = (XmlData)report.getData();
		XmlStar[] xml_stars = (XmlStar[])data.getStar();
		StarImageList detected_list = new StarImageList();
		StarList catalog_list = new StarList();
		for (int i = 0 ; i < xml_stars.length ; i++) {
			StarImage star_image = xml_stars[i].getStarImage();
			if (star_image != null) {
				star_image.setColor(getTypeColor(xml_stars[i].getType()));

				detected_list.addElement(star_image);
			}

			Star[] stars = xml_stars[i].getRecords(star_class);
			for (int j = 0 ; j < stars.length ; j++) {
				stars[j].setColor(getTypeColor(xml_stars[i].getType()));

				catalog_list.addElement(stars[j]);
			}
		}
		detected_list.enableOutputCoordinates();

		PlotProperty property = new PlotProperty();
		property.useStarObjectColor();
		property.setFilled(true);
		property.setDependentSizeParameters(1.0, limit_mag, 1);
		property.setMark(PlotProperty.PLOT_CIRCLE);

		// Replaces the list of stars in the detected chart.
		detected_chart.setDefaultProperty(property);
		detected_chart.setStarPositionList(detected_list);
		detected_chart.repaint();

		// Replaces the list of stars in the catalog chart.
		catalog_chart.setDefaultProperty(property);
		catalog_chart.setStarPositionList(catalog_list);
		catalog_chart.repaint();
	}

	/**
	 * The <code>SaveAsXmlListener</code> is a listener class of menu
	 * selection to save the result in a XML file.
	 */
	protected class SaveAsXmlListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (thread != null  &&  thread.isAlive())
				return;

			thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			if (report == null)
				return;

			try {
				CommonFileChooser file_chooser = new CommonFileChooser();
				file_chooser.setDialogTitle("Save as XML file.");
				file_chooser.setMultiSelectionEnabled(false);
				file_chooser.addChoosableFileFilter(new XmlFilter());

				File file = new File(report.getInformation().getImage().getContent());
				String xml_filename = net.aerith.misao.image.io.Format.getTruncatedFilename(file) + ".xml";
				file_chooser.setSelectedFile(new File(xml_filename));

				if (file_chooser.showSaveDialog(pane) == JFileChooser.APPROVE_OPTION) {
					file = file_chooser.getSelectedFile();
					if (file.exists()) {
						String message = "Overwrite to " + file.getPath() + " ?";
						if (0 != JOptionPane.showConfirmDialog(pane, message, "Confirmation", JOptionPane.YES_NO_OPTION)) {
							return;
						}
					}

					// Sets the XML document file.
					xml_file = file;

					// Converts the absolute image file path in the XML document into
					// the relative path from the XML file if possible.
					XmlImage xml_image = (XmlImage)report.getInformation().getImage();
					String image_path = xml_image.getContent();
					image_path = FileManager.relativatePathFrom(image_path, file);
					xml_image.setContent(image_path);

					report.write(file);

					String message = "Succeeded to save " + file.getPath();
					JOptionPane.showMessageDialog(pane, message);
				}

				return;
			} catch ( MalformedURLException exception ) {
				String message = "Failed to save file.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			} catch ( IOException exception ) {
				String message = "Failed to save file.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * The <code>SaveAsPxfListener</code> is a listener class of menu
	 * selection to save the result in a PXF file.
	 */
	protected class SaveAsPxfListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (thread != null  &&  thread.isAlive())
				return;

			thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			if (report == null)
				return;

			try {
				CommonFileChooser file_chooser = new CommonFileChooser();
				file_chooser.setDialogTitle("Save as PXF file.");
				file_chooser.setMultiSelectionEnabled(false);
				file_chooser.addChoosableFileFilter(new PxfFilter());

				File file = new File(report.getInformation().getImage().getContent());
				String pxf_filename = net.aerith.misao.image.io.Format.getTruncatedFilename(file) + ".pxf";
				file_chooser.setSelectedFile(new File(pxf_filename));

				if (file_chooser.showSaveDialog(pane) == JFileChooser.APPROVE_OPTION) {
					file = file_chooser.getSelectedFile();
					if (file.exists()) {
						String message = "Overwrite to " + file.getPath() + " ?";
						if (0 != JOptionPane.showConfirmDialog(pane, message, "Confirmation", JOptionPane.YES_NO_OPTION)) {
							return;
						}
					}

					PrintWriter out = Encoder.newWriter(file);
					new PxfWriter(report).write(out);
					out.close();

					String message = "Succeeded to save " + file.getPath();
					JOptionPane.showMessageDialog(pane, message);
				}

				return;
			} catch ( MalformedURLException exception ) {
				String message = "Failed to save file.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			} catch ( IOException exception ) {
				String message = "Failed to save file.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * The <code>RegisterToDatabaseListener</code> is a listener class 
	 * of menu selection to register the XML document to the database.
	 */
	protected class RegisterToDatabaseListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (thread != null  &&  thread.isAlive())
				return;

			thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			if (report == null)
				return;

			if (xml_file == null) {
				String message = "Failed. Please save as XML file.";
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
						Vector l = new Vector();
						for (int i = 0 ; i < failed_list.size() ; i++)
							l.addElement(((CatalogStar)failed_list.elementAt(i)).getName());

						String header = "Magnitude of the following stars are failed to register:";
						MessagesDialog messages_dialog = new MessagesDialog(header, l);
						messages_dialog.show(pane, "Warning", JOptionPane.WARNING_MESSAGE);
					}

					Vector warned_list = observer.getWarnedList();
					if (warned_list.size() > 0) {
						Vector l = new Vector();
						for (int i = 0 ; i < warned_list.size() ; i++)
							l.addElement(((CatalogStar)warned_list.elementAt(i)).getName());

						String header = "Magnitude of the following stars are already reported and failed to replace:";
						if (update_reported_mag)
							header = "Magnitude of the following stars are already reported:";
						MessagesDialog messages_dialog = new MessagesDialog(header, l);
						messages_dialog.show(pane, "Warning", JOptionPane.WARNING_MESSAGE);
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

	/**
	 * The <code>IdentifyListener</code> is a listener class of menu
	 * selection to identify.
	 */
	protected class IdentifyListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (thread != null  &&  thread.isAlive())
				return;

			thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			Vector catalog_list = CatalogManager.getIdentificationCatalogReaderList();
			OpenIdentificationCatalogDialog dialog = new OpenIdentificationCatalogDialog(catalog_list);

			XmlInformation info = (XmlInformation)report.getInformation();
			XmlFov fov = (XmlFov)info.getFov();
			if (fov.getWidthInDegree() >= 5.0  ||  fov.getHeightInDegree() >= 5.0)
				dialog.setNegativeDataIgnored(true);
			else
				dialog.setNegativeDataIgnored(false);

			int answer = dialog.show(pane);
			if (answer == 0) {
				CatalogReader reader = dialog.getSelectedCatalogReader();

				String[] paths = net.aerith.misao.util.Format.separatePath(dialog.getCatalogPath());
				for (int i = 0 ; i < paths.length ; i++) {
					try {
						reader.addURL(new File(paths[i]).toURL());
					} catch ( MalformedURLException exception ) {
						System.err.println(exception);
					}
				}

				try {
					DefaultIdentifier identifier = new DefaultIdentifier(report, reader);
					identifier.addMonitor(getMonitorSet());
					if (dialog.isNegativeDataIgnored())
						identifier.exceptNegative();
					identifier.operate();

					String message = "Completed.";
					JOptionPane.showMessageDialog(pane, message);

					((XmlSystem)report.getSystem()).setModifiedJD(JulianDay.create(new Date()));

					starsUpdated(report);
				} catch ( TooLargeFieldException exception ) {
					String message = "Failed. The field of view must be less than " + reader.getFovLimitMessage() + ".";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				} catch ( DocumentIncompleteException exception ) {
					String message = "Failed. Please set " + exception.getMessage() + ".";
					if (exception.getMessage().equals("<date>"))
						message = "Failed. Please set image date.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				} catch ( ExpiredException exception ) {
					String message = "Failed. The image date must be within " + reader.getDateLimitMessage() + ".";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				} catch ( Exception exception ) {
					String message = "Failed.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * The <code>IdentifyFromDatabaseListener</code> is a listener 
	 * class of menu selection to identify.
	 */
	protected class IdentifyFromDatabaseListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (thread != null  &&  thread.isAlive())
				return;

			thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			IdentificationSettingDialog dialog = new IdentificationSettingDialog();

			XmlInformation info = (XmlInformation)report.getInformation();
			XmlFov fov = (XmlFov)info.getFov();
			if (fov.getWidthInDegree() >= 5.0  ||  fov.getHeightInDegree() >= 5.0)
				dialog.setNegativeDataIgnored(true);
			else
				dialog.setNegativeDataIgnored(false);

			int answer = dialog.show(pane);
			if (answer == 0) {
				try {
					CatalogReader reader = new CatalogDBReader(getDBManager().getCatalogDBManager());

					DefaultIdentifier identifier = new DefaultIdentifier(report, reader);
					identifier.addMonitor(getMonitorSet());

					if (dialog.isNegativeDataIgnored())
						identifier.exceptNegative();

					identifier.operate();

					String message = "Completed.";
					JOptionPane.showMessageDialog(pane, message);

					((XmlSystem)report.getSystem()).setModifiedJD(JulianDay.create(new Date()));

					starsUpdated(report);
				} catch ( Exception exception ) {
					String message = "Failed.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * The <code>IdentificationReportListener</code> is a listener 
	 * class of menu selection to make an identification report.
	 */
	protected class IdentificationReportListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (thread != null  &&  thread.isAlive())
				return;

			thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			JInternalFrame frame = new JInternalFrame();
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(700,500);
			frame.setTitle("Identification Report");
			frame.setVisible(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setResizable(true);
			frame.setClosable(true);
			IdentificationReportPane report_pane = new IdentificationReportPane(report);
			frame.getContentPane().add(report_pane);
			addFrame(frame);

			report_pane.initialize();

			listeners.addElement(report_pane);
		}
	}

	/**
	 * The <code>IdentificationChartListener</code> is a listener 
	 * class of menu selection to make an identification chart.
	 */
	protected class IdentificationChartListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (thread != null  &&  thread.isAlive())
				return;

			thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			Size size = new Size(report.getInformation().getSize().getWidth(), report.getInformation().getSize().getHeight());

			JInternalFrame frame = new JInternalFrame();
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(size.getWidth(), size.getHeight() + 200);
			frame.setTitle("Identification Chart");
			frame.setVisible(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setResizable(true);
			frame.setClosable(true);

			IdentifiedStarChartPane chart_pane = new IdentifiedStarChartPane(report);

			frame.getContentPane().add(chart_pane);
			addFrame(frame);

			listeners.addElement(chart_pane);
		}
	}

	/**
	 * The <code>AstrometryListener</code> is a listener class of menu
	 * selection to operate astrometry.
	 */
	protected class AstrometryListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (thread != null  &&  thread.isAlive())
				return;

			thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			AstrometryCatalogSettingDialog dialog = new AstrometryCatalogSettingDialog(report);

			int answer = dialog.show(pane);
			if (answer == 0) {
				AstrometrySetting setting = dialog.getAstrometrySetting();

				if (setting.getCatalogName() == null  ||  setting.getCatalogName().length() == 0) {
					String message = "No reference catalog is selected.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				JInternalFrame frame = new JInternalFrame();
				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				frame.setSize(700,500);
				frame.setTitle("Astrometry Table");
				frame.setVisible(true);
				frame.setMaximizable(true);
				frame.setIconifiable(true);
				frame.setResizable(true);
				frame.setClosable(true);
				AstrometryPane astrometry_pane = new AstrometryPane(report, setting);
				frame.getContentPane().add(astrometry_pane);
				addFrame(frame);

				if (dialog.calculatesDistortionField())
					astrometry_pane.setCalculateDistortionField(true);

				astrometry_pane.addReportDocumentUpdatedListener(desktop);
			}
		}
	}

	/**
	 * The <code>PhotometryListener</code> is a listener class of menu
	 * selection to operate photometry.
	 */
	protected class PhotometryListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (thread != null  &&  thread.isAlive())
				return;

			thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			PhotometryCatalogSettingDialog dialog = new PhotometryCatalogSettingDialog(report);

			int answer = dialog.show(pane);
			if (answer == 0) {
				PhotometrySetting setting = dialog.getPhotometrySetting();

				if (setting.getCatalogName() == null  ||  setting.getCatalogName().length() == 0) {
					String message = "No reference catalog is selected.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				JInternalFrame frame = new JInternalFrame();
				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				frame.setSize(700,500);
				frame.setTitle("Photometry Table");
				frame.setVisible(true);
				frame.setMaximizable(true);
				frame.setIconifiable(true);
				frame.setResizable(true);
				frame.setClosable(true);
				PhotometryPane photometry_pane = new PhotometryPane(report, setting);
				frame.getContentPane().add(photometry_pane);
				addFrame(frame);

				photometry_pane.addReportDocumentUpdatedListener(desktop);
			}
		}
	}

	/**
	 * The <code>ReportToVsnetListener</code> is a listener class of
	 * menu selection to make a report of magnitude to VSNET or VSOLJ.
	 */
	protected class ReportToVsnetListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (thread != null  &&  thread.isAlive())
				return;

			thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			XmlInformation info = (XmlInformation)report.getInformation();

			if (info.getDate() == null) {
				String message = "Failed. Please set image date.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (info.getObserver() == null) {
				String message = "Failed. Please set observer.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			VsnetReportSettingDialog dialog = new VsnetReportSettingDialog();

			if (info.getObserver() != null)
				dialog.setObserverCode(info.getObserver().replace(' ', '_'));

			int answer = dialog.show(pane);
			if (answer == 0) {
				if (dialog.getObserverCode().length() == 0) {
					String message = "Please set observer's code.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				Vector catalog_list = ((XmlData)report.getData()).getIdentifiedCatalogList();

				CatalogSelectionDialog dialog2 = new CatalogSelectionDialog(catalog_list);
				answer = dialog2.show(pane);

				if (answer == 0) {
					catalog_list = dialog2.getSelectedCatalogList();

					ValidAreaSettingDialog dialog3 = new ValidAreaSettingDialog();
					answer = dialog3.show(pane);

					if (answer == 0) {
						int ingore_pixels_from_edge = dialog3.getPixelsFromEdge();

						XmlInformation temporary_info = new XmlInformation(info);
						temporary_info.setPath("dummy");

						Vector record_list = new Vector();

						try {
							XmlStar[] xml_stars = (XmlStar[])report.getData().getStar();
							for (int i = 0 ; i < xml_stars.length ; i++) {
								String vsnet_name = xml_stars[i].getTypicalVsnetName(catalog_list);
								if (vsnet_name != null  &&  vsnet_name.length() > 0) {
									XmlMagRecord record = new XmlMagRecord(temporary_info, xml_stars[i]);

									if (record.getPixelsFromEdge() == null  ||  record.getPixelsFromEdge().intValue() >= ingore_pixels_from_edge) {
										VsnetRecord vsnet_record = null;
										if (dialog.isVsnetSelected())
											vsnet_record = new VsnetRecord(vsnet_name, record);
										else
											vsnet_record = new VsoljRecord(vsnet_name, record);

										vsnet_record.setFormat(dialog.getFormat());
										vsnet_record.setAccuracy(dialog.getAccuracy());
										vsnet_record.setObserverCode(dialog.getObserverCode());

										record_list.addElement(vsnet_record);
									}
								}
							}
						} catch ( DocumentIncompleteException exception ) {
							// Never happens.
							System.err.println(exception);
						}

						VsnetReportTable table = new VsnetReportTable(record_list);

						JInternalFrame frame = new JInternalFrame();
						frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						frame.setSize(600,450);
						frame.setTitle("Report to VSNET");
						frame.setVisible(true);
						frame.setMaximizable(true);
						frame.setIconifiable(true);
						frame.setResizable(true);
						frame.setClosable(true);
						frame.setClosable(true);

						frame.getContentPane().add(new JScrollPane(table));
						addFrame(frame);

						listeners.add(table);
					}
				}
			}
		}
	}

	/**
	 * The <code>SetDateListener</code> is a listener class of menu
	 * selection to set the image date and time.
	 */
	protected class SetDateListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (thread != null  &&  thread.isAlive())
				return;

			thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			XmlInformation info = (XmlInformation)report.getInformation();

			ImageDateDialog dialog = new ImageDateDialog();

			if (info.getDate() != null) {
				dialog.setDay(JulianDay.create(info.getDate()));
				dialog.setAccuracy(JulianDay.getAccuracy(info.getDate()));
			}
			if (info.getExposure() != null) {
				dialog.setExposure((double)info.getExposure().getContent(), info.getExposure().getUnit());
			}

			int answer = dialog.show(pane);
			if (answer == 0) {
				info.setDate(dialog.getDay().getOutputString(JulianDay.FORMAT_MONTH_IN_REDUCED, dialog.getAccuracy()));
				XmlExposure exposure = new XmlExposure();
				exposure.setContent((float)dialog.getExposure());
				exposure.setUnit(dialog.getExposureUnit());
				info.setExposure(exposure);

				((XmlSystem)report.getSystem()).setModifiedJD(JulianDay.create(new Date()));

				dateUpdated(report);
			}
		}
	}

	/**
	 * The <code>SetObserverListener</code> is a listener class of menu
	 * selection to set the observer.
	 */
	protected class SetObserverListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (thread != null  &&  thread.isAlive())
				return;

			thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			XmlInformation info = (XmlInformation)report.getInformation();

			ParameterDialog dialog = new ParameterDialog();
			dialog.setWindowTitle("Observer");
			dialog.setBorderTitle("Obvserver's name");

			if (info.getObserver() != null)
				dialog.setParameter(info.getObserver());

			int answer = dialog.show(pane);
			if (answer == 0) {
				info.setObserver(dialog.getParameter());

				((XmlSystem)report.getSystem()).setModifiedJD(JulianDay.create(new Date()));

				recordUpdated(report);
			}
		}
	}

	/**
	 * The <code>SetMagnitudeSystemCodeListener</code> is a listener 
	 * class of menu selection to set the code of the filter.
	 */
	protected class SetMagnitudeSystemCodeListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (thread != null  &&  thread.isAlive())
				return;

			thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			XmlInformation info = (XmlInformation)report.getInformation();

			MagnitudeSystemCodeDialog dialog = new MagnitudeSystemCodeDialog();

			if (info.getFilter() != null)
				dialog.setMagSystemCode(info.getFilter());

			int answer = dialog.show(pane);
			if (answer == 0) {
				info.setFilter(dialog.getMagSystemCode());

				((XmlSystem)report.getSystem()).setModifiedJD(JulianDay.create(new Date()));

				recordUpdated(report);
			}
		}
	}

	/**
	 * The <code>SetChipListener</code> is a listener class of menu
	 * selection to set the code of the chip.
	 */
	protected class SetChipListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (thread != null  &&  thread.isAlive())
				return;

			thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			XmlInformation info = (XmlInformation)report.getInformation();

			ParameterDialog dialog = new ParameterDialog();
			dialog.setWindowTitle("Chip");
			dialog.setBorderTitle("Chip code");

			if (info.getChip() != null)
				dialog.setParameter(info.getChip());

			int answer = dialog.show(pane);
			if (answer == 0) {
				info.setChip(dialog.getParameter());

				((XmlSystem)report.getSystem()).setModifiedJD(JulianDay.create(new Date()));

				recordUpdated(report);
			}
		}
	}

	/**
	 * The <code>SetInstrumentsListener</code> is a listener class of 
	 * menu selection to set the instruments.
	 */
	protected class SetInstrumentsListener implements ActionListener, Runnable {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			if (thread != null  &&  thread.isAlive())
				return;

			thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			XmlInformation info = (XmlInformation)report.getInformation();

			ParameterDialog dialog = new ParameterDialog();
			dialog.setWindowTitle("Instruments");
			dialog.setBorderTitle("Instruments");

			if (info.getInstruments() != null)
				dialog.setParameter(info.getInstruments());

			int answer = dialog.show(pane);
			if (answer == 0) {
				info.setInstruments(dialog.getParameter());

				((XmlSystem)report.getSystem()).setModifiedJD(JulianDay.create(new Date()));

				recordUpdated(report);
			}
		}
	}
}
