/*
 * @(#)PixyReviewDesktop.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.PixyDesktop;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.io.*;
import net.aerith.misao.image.filter.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.io.FileManager;
import net.aerith.misao.io.filechooser.*;
import net.aerith.misao.pixy.Resource;
import net.aerith.misao.pixy.image_loading.XmlImageLoader;

/**
 * The <code>PixyReviewDesktop</code> represents a desktop of the PIXY
 * system to review the examination result.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class PixyReviewDesktop extends PixyDesktop {
	/**
	 * The drop target.
	 */
	protected DropTarget dt;

	/*
	 * The menu item to open the XML file.
	 */
	protected JMenuItem menu_open_xml;

	/*
	 * The menu item to open an image.
	 */
	protected JMenuItem menu_open_image;

	/**
	 * Constructs a <code>PixyReviewDesktop</code>.
	 */
	public PixyReviewDesktop ( ) {
		dt = new DropTarget();
		try {
			dt.addDropTargetListener(new XmlFileDropTargetListener());
			desktop_pane.setDropTarget(dt);
			dt.setActive(true);
		} catch ( TooManyListenersException exception ) {
			String message = "Drag and drop is impossible.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}

		addWindowListener(new OpenWindowListener());
	}

	/**
	 * Adds the <tt>File</tt> menus to the menu bar.
	 */
	public void addFileMenu ( ) {
		JMenu menu = addMenu("File");

		menu_open_xml = new JMenuItem("Open XML File");
		menu_open_xml.addActionListener(new OpenXmlListener());
		menu.add(menu_open_xml);

		menu_open_image = new JMenuItem("Open Image File");
		menu_open_image.addActionListener(new OpenImageListener());
		menu.add(menu_open_image);

		menu_open_xml.setEnabled(true);
		menu_open_image.setEnabled(false);

		super.addFileMenu();
	}

	/**
	 * Starts the operation and opens the specified XML report 
	 * document automatically.
	 * @param file the file of the XML report document.
	 */
	public void operate ( File file ) {
		openXml(file);
	}

	/**
	 * Opens the image window of the specified image file.
	 * @param file   the image file.
	 * @param format the image format. In the case of null, the system 
	 * automatically selects a proper format based on the file name.
	 * @exception Exception if an error happens.
	 */
	protected void openImageWindow ( File file, net.aerith.misao.image.io.Format format )
		throws Exception
	{
		XmlInformation info = (XmlInformation)report.getInformation();

		XmlImageLoader image_loader = new XmlImageLoader(info, getFileManager());
		image_loader.setFile(file, format);
		image_loader.addMonitor(monitor_set);
		image_loader.operate();
		image = image_loader.getMonoImage();

		image_window = new JInternalFrame();
		image_window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		image_window.setSize(image.getSize().getWidth(), image.getSize().getHeight());
		image_window.setTitle(file.getPath());
		image_window.setVisible(true);
		image_window.setMaximizable(true);
		image_window.setIconifiable(true);
		image_window.setResizable(true);

		MonoImageComponent image_component = new MonoImageComponent(image);
		image_component.enableImageProcessing();
		image_window.getContentPane().add(new JScrollPane(image_component));
		addFrame(image_window);
	}

	/**
	 * Opens the detected stars window.
	 */
	protected void openDetectedStarsWindow ( ) {
		Size size = new Size(report.getInformation().getSize().getWidth(), report.getInformation().getSize().getHeight());
		double limit_mag = (double)report.getInformation().getLimitingMag();

		XmlData data = (XmlData)report.getData();
		int star_count = data.getStarCount();
		XmlStar[] stars = (XmlStar[])data.getStar();
		StarImageList list = new StarImageList();
		for (int i = 0 ; i < star_count ; i++) {
			StarImage star = stars[i].getStarImage();
			if (star != null) {
				star.setColor(getTypeColor(stars[i].getType()));

				list.addElement(star);
			}
		}
		list.enableOutputCoordinates();

		detected_stars_window = new JInternalFrame();
		detected_stars_window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		detected_stars_window.setSize(size.getWidth(), size.getHeight());
		detected_stars_window.setTitle("Detected Stars");
		detected_stars_window.setVisible(true);
		detected_stars_window.setMaximizable(true);
		detected_stars_window.setIconifiable(true);
		detected_stars_window.setResizable(true);

		PlotProperty property = new PlotProperty();
		property.useStarObjectColor();
		property.setFilled(true);
		property.setDependentSizeParameters(1.0, limit_mag, 1);
		property.setMark(PlotProperty.PLOT_CIRCLE);

		detected_chart = new ChartComponent(size);
		detected_chart.setBasepointAtTopLeft();
		detected_chart.setDefaultProperty(property);
		detected_chart.setStarPositionList(list);
		detected_chart.setBackground(Color.white);

		detected_stars_window.getContentPane().add(new JScrollPane(detected_chart));
		addFrame(detected_stars_window);
	}

	/**
	 * Opens the catalog stars window.
	 */
	protected void openCatalogStarsWindow ( ) {
		Size size = new Size(report.getInformation().getSize().getWidth(), report.getInformation().getSize().getHeight());
		double limit_mag = (double)report.getInformation().getLimitingMag();

		String star_class = CatalogManager.getCatalogStarClassName(report.getInformation().getBaseCatalog());

		XmlData data = (XmlData)report.getData();
		int star_count = data.getStarCount();
		XmlStar[] xml_stars = (XmlStar[])data.getStar();
		StarList list = new StarList();
		for (int i = 0 ; i < star_count ; i++) {
			Star[] stars = xml_stars[i].getRecords(star_class);
			for (int j = 0 ; j < stars.length ; j++) {
				stars[j].setColor(getTypeColor(xml_stars[i].getType()));

				list.addElement(stars[j]);
			}
		}

		catalog_stars_window = new JInternalFrame();
		catalog_stars_window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		catalog_stars_window.setSize(size.getWidth(), size.getHeight());
		catalog_stars_window.setTitle(report.getInformation().getBaseCatalog());
		catalog_stars_window.setVisible(true);
		catalog_stars_window.setMaximizable(true);
		catalog_stars_window.setIconifiable(true);
		catalog_stars_window.setResizable(true);

		PlotProperty property = new PlotProperty();
		property.useStarObjectColor();
		property.setFilled(true);
		property.setDependentSizeParameters(1.0, limit_mag, 1);
		property.setMark(PlotProperty.PLOT_CIRCLE);

		catalog_chart = new ChartComponent(size);
		catalog_chart.setBasepointAtTopLeft();
		catalog_chart.setDefaultProperty(property);
		catalog_chart.setStarPositionList(list);
		catalog_chart.setBackground(Color.white);

		catalog_stars_window.getContentPane().add(new JScrollPane(catalog_chart));
		addFrame(catalog_stars_window);
	}

	/**
	 * Opens an XML file.
	 * @param file the XML file.
	 */
	protected void openXml ( File file ) {
		try {
			// Reads the XML document.
			report = new XmlReport();
			report.read(file);

			// Sets the XML document file.
			xml_file = file;

			// Opens the image window.
			file = ((XmlInformation)report.getInformation()).getImageFile(getFileManager());
			if (file.canRead() == true) {
				try {
					XmlImage xml_image = (XmlImage)report.getInformation().getImage();
					net.aerith.misao.image.io.Format format = net.aerith.misao.image.io.Format.create(file, xml_image.getFormat());
					openImageWindow(file, format);
				} catch ( Exception exception ) {
					// Shows the error message later.
				}
			}

			// Opens the detected stars window.
			openDetectedStarsWindow();

			// Opens the catalog stars window.
			openCatalogStarsWindow();

			// Opens the image information table.
			showImageInformationTable();

			enableXmlMenus();
			menu_open_xml.setEnabled(false);
			dt.setActive(false);

			if (image == null) {
				menu_open_image.setEnabled(true);
				dt.setActive(true);

				if (help_message_enabled) {
					JLabel label = new JLabel(Resource.getReviewHelpHtmlMessageWhenImageNotFound(report.getInformation().getImage().getContent()));
					label.setSize(400,300);
					JOptionPane.showMessageDialog(pane, label);
				}
			}
		} catch ( MalformedURLException exception ) {
			String message = "Failed to open the XML file.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		} catch ( IOException exception ) {
			String message = "Failed to open the XML file.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}
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
			openLogWindow();

			/*
			  JLabel label = new JLabel(Resource.getReviewHelpHtmlMessageAtBeginning());
			  label.setSize(400,300);
			  JOptionPane.showMessageDialog(pane, label);
			*/
		}
	}

	/**
	 * The <code>XmlFileDropTargetListener</code> is a listener 
	 * class of drop event from native filer application.
	 */
	protected class XmlFileDropTargetListener extends FileDropTargetAdapter implements Runnable {
		/**
		 * The XML file.
		 */
		protected File file;

		/**
		 * True when to open an XML file.
		 */
		protected boolean open_xml = true;

		/**
		 * Invoked when files are dropped.
		 * @param files the dropped files.
		 */
		public void dropFiles ( File[] file ) {
			if (thread != null  &&  thread.isAlive())
				return;

			if (file.length != 1)
				return;

			open_xml = true;
			if (menu_open_image.isEnabled())
				open_xml = false;

			this.file = file[0];

			thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			if (open_xml) {
				openXml(file);
			} else {
				try {
					net.aerith.misao.image.io.Format format = net.aerith.misao.image.io.Format.create(file);

					openImageWindow(file, format);

					menu_open_image.setEnabled(false);
					dt.setActive(false);
				} catch ( Exception exception ) {
					String message = "Failed to open the image.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * The <code>OpenXmlListener</code> is a listener class of menu 
	 * selection to open an XML file.
	 */
	protected class OpenXmlListener implements ActionListener, Runnable {
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
			CommonFileChooser file_chooser = new CommonFileChooser();
			file_chooser.setDialogTitle("Open an XML file.");
			file_chooser.setMultiSelectionEnabled(false);
			file_chooser.addChoosableFileFilter(new XmlFilter());

			if (file_chooser.showOpenDialog(pane) == JFileChooser.APPROVE_OPTION) {
				File file = file_chooser.getSelectedFile();

				openXml(file);
			}
		}
	}

	/**
	 * The <code>OpenImageListener</code> is a listener class of menu 
	 * selection to open an image file.
	 */
	protected class OpenImageListener implements ActionListener, Runnable {
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
			ImageFileOpenChooser file_chooser = new ImageFileOpenChooser();
			file_chooser.setDialogTitle("Open an image file.");
			file_chooser.setMultiSelectionEnabled(false);

			File file = new File(report.getInformation().getImage().getContent());
			file_chooser.setSelectedFile(file);

			if (file_chooser.showOpenDialog(pane) == JFileChooser.APPROVE_OPTION) {
				try {
					file = file_chooser.getSelectedFile();
					net.aerith.misao.image.io.Format format = file_chooser.getSelectedFileFormat();

					openImageWindow(file, format);

					menu_open_image.setEnabled(false);
					dt.setActive(false);
				} catch ( Exception exception ) {
					String message = "Failed to open the image.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
