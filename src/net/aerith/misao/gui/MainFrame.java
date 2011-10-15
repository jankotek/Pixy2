/*
 * @(#)MainFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.io.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.io.*;
import net.aerith.misao.io.filechooser.*;
import net.aerith.misao.database.*;
import net.aerith.misao.pixy.*;
import net.aerith.misao.toolkit.PixyDesktop.*;
import net.aerith.misao.toolkit.BatchExamination.BatchExaminationDesktop;
import net.aerith.misao.toolkit.ReportBatch.ReportBatchDesktop;
import net.aerith.misao.toolkit.ImageDatabase.ImageDatabaseDesktop;
import net.aerith.misao.toolkit.StarDatabase.StarDatabaseDesktop;
import net.aerith.misao.toolkit.AgentDesktop.AgentDesktop;
import net.aerith.misao.toolkit.MultipleCatalogChart.MultipleCatalogChartPane;
import net.aerith.misao.toolkit.ImageCollation.ImageCollationDesktop;
import net.aerith.misao.toolkit.ImageConversion.ImageConversionFrame;
import net.aerith.misao.toolkit.CrossIdentification.CrossIdentificationSettingDialog;
import net.aerith.misao.toolkit.PhotometryCalibration.PhotometryCalibrationDesktop;
import net.aerith.misao.toolkit.PhotometryZeroPointAdjustment.PhotometryZeroPointAdjustmentDesktop;
import net.aerith.misao.toolkit.NewStarSearch.NewStarSearchDesktop;
import net.aerith.misao.toolkit.IdentifiedStarSearch.IdentifiedStarSearchDesktop;
import net.aerith.misao.toolkit.VariableStarSearch.VariableStarSearchDesktop;

/**
 * The <code>MainFrame</code> represents the main frame of the PIXY
 * system. It appears when the system runs. It contains buttons to
 * run every function.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 July 8
 */

public class MainFrame extends BaseFrame {
	/**
	 * The drop target.
	 */
	protected DropTarget dt;

	/**
	 * Shows the splash screen, opens a main frame and starts the 
	 * application.
	 * @param args the options.
	 */
	public static void main ( String[] args ) throws Exception {
		System.setErr(System.out);

		try {
			Class.forName("javax.xml.parsers.DocumentBuilder");

			try {
				// J2SE 1.4
				Class.forName("org.apache.crimson.tree.XmlDocument");
			} catch ( ClassNotFoundException exception ) {
				// JAXP 1.0.1
				Class.forName("com.sun.xml.tree.XmlDocument");
			}
		} catch ( ClassNotFoundException exception ) {
			JaxpNotFoundDialog dialog = new JaxpNotFoundDialog();

			String[] messages = dialog.getMessages();
			for (int i = 0 ; i < messages.length ; i++)
				System.err.println(messages[i]);
			System.err.println("");

			dialog.show(null);

			System.exit(1);
		}

		try {
			Class.forName("com.sun.jimi.core.Jimi");
		} catch ( ClassNotFoundException exception ) {
			JimiNotFoundDialog dialog = new JimiNotFoundDialog();

			String[] messages = dialog.getMessages();
			for (int i = 0 ; i < messages.length ; i++)
				System.err.println(messages[i]);
			System.err.println("");

			dialog.show(null);
		}

		ImageIcon icon = Resource.getSystemIcon();
		SplashScreen splash_screen = new SplashScreen(icon);
		splash_screen.setTime(5);
		splash_screen.showSplashScreen();

		MainFrame main_frame = new MainFrame();
		main_frame.setSize(icon.getIconWidth(), icon.getIconHeight() + Resource.getSinglePixyIcon().getIconHeight() * 2 + 40);
		main_frame.setTitle("Main Panel");
		main_frame.setVisible(true);
	}

	/**
	 * Constructs a <code>MainFrame</code>.
	 */
	public MainFrame ( ) {
		addWindowListener(new CloseWindowListener());

		pane.setLayout(new BorderLayout());

		ImageIcon icon = Resource.getSystemIcon();
		ImageLabel image_label = new ImageLabel(icon);
		pane.add(image_label, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 2));

		JButton button = new JButton("Tutorial", Resource.getTutorialPixyIcon());
		button.addActionListener(new TutorialListener());
		panel.add(button);

		button = new JButton("Image Examination", Resource.getSinglePixyIcon());
		button.addActionListener(new SinglePixyListener());
		panel.add(button);

		button = new JButton("Review", Resource.getReviewIcon());
		button.addActionListener(new ReviewListener());
		panel.add(button);

		button = new JButton("Batch Examination", Resource.getBatchPixyIcon());
		button.addActionListener(new BatchPixyListener());
		panel.add(button);

		pane.add(panel, BorderLayout.SOUTH);

		dt = new DropTarget();
		try {
			dt.addDropTargetListener(new ImageAndXmlFileDropTargetListener());
			image_label.setDropTarget(dt);
			dt.setActive(true);
		} catch ( TooManyListenersException exception ) {
			String message = "Drag and drop is impossible.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Initializes menu bar. A <code>JMenuBar</code> must be set to 
	 * this <code>JFrame</code> previously.
	 */
	public void initMenu ( ) {
		JMenu menu = addMenu("File");
		JMenuItem[] items = createFileMenus();
		for (int i = 0 ; i < items.length ; i++)
			menu.add(items[i]);

		menu = addMenu("Examination");
		items = createExaminationMenus();
		for (int i = 0 ; i < items.length ; i++) {
			if (items[i] == null)
				menu.addSeparator();
			else
				menu.add(items[i]);
		}

		menu = addMenu("Database");
		items = createDatabaseMenus();
		for (int i = 0 ; i < items.length ; i++)
			menu.add(items[i]);

		menu = addMenu("Agent");
		items = createAgentMenus();
		for (int i = 0 ; i < items.length ; i++)
			menu.add(items[i]);

		menu = addMenu("Tool");
		items = createToolMenus();
		for (int i = 0 ; i < items.length ; i++) {
			if (items[i] == null)
				menu.addSeparator();
			else
				menu.add(items[i]);
		}

		super.initMenu();

		menu = addMenu("Configuration");
		items = createConfigurationMenus();
		for (int i = 0 ; i < items.length ; i++)
			menu.add(items[i]);

		addCopyrightMenu();
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * file menus. Items are newly created when invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createFileMenus ( ) {
		JMenuItem[] items = new JMenuItem[1];
		items[0] = new JMenuItem("Exit");
		items[0].addActionListener(new ExitListener());
		return items;
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * examination menus. Items are newly created when invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createExaminationMenus ( ) {
		JMenuItem[] items = new JMenuItem[6];
		items[0] = new JMenuItem("Tutorial");
		items[0].addActionListener(new TutorialListener());
		items[1] = new JMenuItem("Image Examination");
		items[1].addActionListener(new SinglePixyListener());
		items[2] = new JMenuItem("Review");
		items[2].addActionListener(new ReviewListener());
		items[3] = new JMenuItem("Batch Examination");
		items[3].addActionListener(new BatchPixyListener());
		items[4] = null;
		items[5] = new JMenuItem("XML Report Document Batch Operation");
		items[5].addActionListener(new ReportBatchOperationListener());
		return items;
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * database menus. Items are newly created when invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createDatabaseMenus ( ) {
		JMenuItem[] items = new JMenuItem[2];
		items[0] = new JMenuItem("Image Database");
		items[0].addActionListener(new ImageDatabaseListener());
		items[1] = new JMenuItem("Star Database");
		items[1].addActionListener(new StarDatabaseListener());
		return items;
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * agent menus. Items are newly created when invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createAgentMenus ( ) {
		Agent[] agents = PluginLoader.loadAgents();

		JMenuItem[] items = null;

		if (agents.length == 0) {
			items = new JMenuItem[1];
			items[0] = new JMenuItem("(no plug-in)");
			items[0].setEnabled(false);
		} else {
			items = new JMenuItem[agents.length];
			for (int i = 0 ; i < items.length ; i++) {
				items[i] = new JMenuItem(agents[i].getName());
				items[i].addActionListener(new AgentListener(agents[i]));
			}
		}

		return items;
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * tool menus. Items are newly created when invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createToolMenus ( ) {
		JMenuItem[] items = new JMenuItem[15];
		items[0] = new JMenuItem("View Image");
		items[0].addActionListener(new ViewImageListener());
		items[1] = new JMenuItem("View Star Chart");
		items[1].addActionListener(new ViewStarChartListener());
		items[2] = new JMenuItem("View Multiple Catalog Chart");
		items[2].addActionListener(new ViewMultipleCatalogChartListener());
		items[3] = null;
		items[4] = new JMenuItem("Image Collation");
		items[4].addActionListener(new ImageCollationListener());
		items[5] = new JMenuItem("Image Conversion");
		items[5].addActionListener(new ImageConversionListener());
		items[6] = null;
		items[7] = new JMenuItem("Cross Identification");
		items[7].addActionListener(new CrossIdentificationListener());
		items[8] = null;
		items[9] = new JMenuItem("Photometry Calibration");
		items[9].addActionListener(new PhotometryCalibrationListener());
		items[10] = new JMenuItem("Photometry Zero-Point Adjustment");
		items[10].addActionListener(new PhotometryZeroPointAdjustmentListener());
		items[11] = null;
		items[12] = new JMenuItem("Search New Stars");
		items[12].addActionListener(new SearchNewStarsListener());
		items[13] = new JMenuItem("Search Identified Stars");
		items[13].addActionListener(new SearchIdentifiedStarsListener());
		items[14] = new JMenuItem("Search Variable Stars");
		items[14].addActionListener(new SearchVariableStarsListener());

		return items;
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * configuration menus. Items are newly created when invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createConfigurationMenus ( ) {
		JMenuItem[] items = new JMenuItem[2];

		items[0] = new JMenuItem("Default Catalog Path");
		items[0].addActionListener(new DefaultCatalogPathListener());

		items[1] = new JMenuItem("Image Configuration");
		items[1].addActionListener(new ImageConfigurationListener());

		return items;
	}

	/**
	 * The <code>TutorialListener</code> is a listener class of menu
	 * selection to run the PIXY system in tutorial mode.
	 */
	protected class TutorialListener implements ActionListener, Runnable {
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
			PixyOperationDesktop desktop = new PixyOperationDesktop();
			desktop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			desktop.setTutorialMode();
			desktop.setSize(800,600);
			desktop.setTitle("PIXY System Desktop");
			desktop.setVisible(true);
		}
	}

	/**
	 * The <code>SinglePixyListener</code> is a listener class of 
	 * menu selection to run the PIXY system to examine one image.
	 */
	protected class SinglePixyListener implements ActionListener, Runnable {
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
			PixyOperationDesktop desktop = new PixyOperationDesktop();
			desktop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			desktop.setSize(800,600);
			desktop.setTitle("PIXY System Desktop");
			desktop.setVisible(true);
		}
	}

	/**
	 * The <code>ReviewListener</code> is a listener class of menu 
	 * selection to run the PIXY system to review the result.
	 */
	protected class ReviewListener implements ActionListener, Runnable {
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
			PixyReviewDesktop desktop = new PixyReviewDesktop();
			desktop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			desktop.setSize(800,600);
			desktop.setTitle("PIXY System Desktop");
			desktop.setVisible(true);
		}
	}

	/**
	 * The <code>BatchPixyListener</code> is a listener class of
	 * menu selection to run the batch examination.
	 */
	protected class BatchPixyListener implements ActionListener, Runnable {
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
			BatchExaminationDesktop desktop = new BatchExaminationDesktop();
			desktop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			desktop.setSize(800,600);
			desktop.setTitle("Batch Examination");
			desktop.setVisible(true);
		}
	}

	/**
	 * The <code>ReportBatchOperationListener</code> is a listener 
	 * class of menu selection for batch operation on several XML 
	 * report documents.
	 */
	protected class ReportBatchOperationListener implements ActionListener, Runnable {
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
			ReportBatchDesktop desktop = new ReportBatchDesktop();
			desktop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			desktop.setSize(800,600);
			desktop.setTitle("XML Report Document Batch Operation Desktop");
			desktop.setVisible(true);
		}
	}

	/**
	 * The <code>ImageDatabaseListener</code> is a listener class of 
	 * menu selection to open the image database desktop.
	 */
	protected class ImageDatabaseListener implements ActionListener, Runnable {
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
			ImageDatabaseDesktop desktop = new ImageDatabaseDesktop();
			desktop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			desktop.setSize(800,600);
			desktop.setTitle("Image Database Desktop");
			desktop.setVisible(true);
		}
	}

	/**
	 * The <code>StarDatabaseListener</code> is a listener class of 
	 * menu selection to open the star database desktop.
	 */
	protected class StarDatabaseListener implements ActionListener, Runnable {
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
			StarDatabaseDesktop desktop = new StarDatabaseDesktop();
			desktop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			desktop.setSize(800,600);
			desktop.setTitle("Star Database Desktop");
			desktop.setVisible(true);
		}
	}

	/**
	 * The <code>AgentListener</code> is a listener class of menu 
	 * selection to open the desktop for operation using an agent.
	 */
	protected class AgentListener implements ActionListener, Runnable {
		/**
		 * The agent.
		 */
		protected Agent agent;

		/**
		 * Constructs an <code>AgentListener</code>.
		 * @param agent the agent.
		 */
		public AgentListener ( Agent agent ) {
			this.agent = agent;
		}

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
			AgentDesktop desktop = new AgentDesktop(agent);
			desktop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			desktop.setSize(800,600);
			desktop.setTitle(agent.getName() + " Desktop");
			desktop.setVisible(true);
		}
	}

	/**
	 * The <code>CloseWindowListener</code> is a listener class of
	 * closing this window.
	 */
	protected class CloseWindowListener extends WindowAdapter {
		/**
		 * Invoked when this window is closed.
		 * @param e contains the event status.
		 */
		public void windowClosing ( WindowEvent e ) {
			// Saves the XML documents in disk cache into files.
			try {
				XmlDBHolderCache.flush();
			} catch ( IOException exception ) {
				System.err.println("Failed to flush the disk cache.");
			}

			System.exit(0);
		}
	}

	/**
	 * The <code>ExitListener</code> is a listener class of menu 
	 * selection to exit.
	 */
	protected class ExitListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			System.exit(0);
		}
	}

	/**
	 * The <code>ViewImageListener</code> is a listener class of menu 
	 * selection to view an image.
	 */
	protected class ViewImageListener implements ActionListener, Runnable {
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
				ImageFileOpenChooser file_chooser = new ImageFileOpenChooser();
				file_chooser.setDialogTitle("Open an image file.");
				file_chooser.setMultiSelectionEnabled(false);

				if (file_chooser.showOpenDialog(pane) == JFileChooser.APPROVE_OPTION) {
					File file = file_chooser.getSelectedFile();
					net.aerith.misao.image.io.Format format = file_chooser.getSelectedFileFormat();
					MonoImage image = format.read();

					MonoImageComponent image_component = new MonoImageComponent(image);
					image_component.enableImageProcessing();

					JFrame frame = new JFrame();
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					frame.setBackground(Color.white);
					frame.getContentPane().add(new JScrollPane(image_component));

					frame.setSize(image.getSize().getWidth(), image.getSize().getHeight());
					frame.setTitle(file.getPath());
					frame.setVisible(true);
				}

				return;
			} catch ( MalformedURLException exception ) {
			} catch ( IOException exception ) {
			} catch ( UnsupportedBufferTypeException exception ) {
			} catch ( UnsupportedFileTypeException exception ) {
			}

			String message = "Failed to open the image.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * The <code>ViewStarChartListener</code> is a listener class of
	 * menu selection to view a chart of star catalog.
	 */
	protected class ViewStarChartListener implements ActionListener, Runnable {
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
				OpenChartCatalogDialog dialog = new OpenChartCatalogDialog();
				int answer = dialog.show(pane);

				if (answer == 0) {
					CatalogReader reader = dialog.getSelectedCatalogReader();
					reader.setLimitingMagnitude(dialog.getLimitingMag());

					String[] paths = net.aerith.misao.util.Format.separatePath(dialog.getCatalogPath());
					for (int i = 0 ; i < paths.length ; i++) {
						try {
							reader.addURL(new File(paths[i]).toURL());
						} catch ( MalformedURLException exception ) {
							System.err.println(exception);
						}
					}

					InteractiveCatalogReader interactive_reader = new InteractiveCatalogReader(reader);
					StarList list = interactive_reader.read(pane, dialog.getCoor(), dialog.getFieldOfView());

					ChartMapFunction cmf = new ChartMapFunction(dialog.getCoor(), 400.0 / dialog.getFieldOfView(), 0.0);
					list.mapCoordinatesToXY(cmf);

					PlotProperty property = new PlotProperty();
					property.setColor(Color.black);
					property.setFilled(true);
					property.setDependentSizeParameters(1.0, dialog.getLimitingMag(), 1);
					property.setMark(PlotProperty.PLOT_CIRCLE);

					ChartComponent chart = null;
					if (list.size() == 0) {
						chart = new ChartComponent(new Size(400,400));
					} else {
						CatalogStar catalog_star = (CatalogStar)list.elementAt(0);
						chart = new SinglePropertyChartComponent(new Size(400,400), catalog_star);
					}
					chart.setBasepointAtCenter();
					chart.setDefaultProperty(property);
					chart.setStarPositionList(list);
					chart.setBackground(Color.white);

					JFrame frame = new JFrame();
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					frame.setBackground(Color.white);
					frame.getContentPane().add(new JScrollPane(chart));

					frame.setSize(400,400);
					frame.setTitle(reader.getName() + " Chart");
					frame.setVisible(true);
				}

				return;
			} catch ( IOException exception ) {
			} catch ( QueryFailException exception ) {
			}

			String message = "Failed to open the catalog.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * The <code>ViewMultipleCatalogChartListener</code> is a listener
	 * class of menu selection to view a chart of several star catalogs.
	 */
	protected class ViewMultipleCatalogChartListener implements ActionListener, Runnable {
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
				OpenChartCatalogDialog dialog = new OpenChartCatalogDialog();
				int answer = dialog.show(pane);

				if (answer == 0) {
					CatalogReader reader = dialog.getSelectedCatalogReader();
					reader.setLimitingMagnitude(dialog.getLimitingMag());

					String[] paths = net.aerith.misao.util.Format.separatePath(dialog.getCatalogPath());
					for (int i = 0 ; i < paths.length ; i++) {
						try {
							reader.addURL(new File(paths[i]).toURL());
						} catch ( MalformedURLException exception ) {
							System.err.println(exception);
						}
					}

					InteractiveCatalogReader interactive_reader = new InteractiveCatalogReader(reader);
					StarList list = interactive_reader.read(pane, dialog.getCoor(), dialog.getFieldOfView());

					ChartMapFunction cmf = new ChartMapFunction(dialog.getCoor(), 400.0 / dialog.getFieldOfView(), 0.0);
					list.mapCoordinatesToXY(cmf);

					MultipleCatalogChartPane chart = new MultipleCatalogChartPane(list, new Size(400, 400), dialog.getCoor(), dialog.getFieldOfView(), dialog.getFieldOfView(), dialog.getLimitingMag());

					JFrame frame = new JFrame();
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					frame.setBackground(Color.white);
					frame.getContentPane().add(chart);
					frame.setSize(400,600);
					frame.setTitle("Multiple Catalog Chart");
					frame.setVisible(true);
				}

				return;
			} catch ( IOException exception ) {
			} catch ( QueryFailException exception ) {
			}

			String message = "Failed to open the catalog.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * The <code>ImageCollationListener</code> is a listener class of
	 * menu selection to collate images.
	 */
	protected class ImageCollationListener implements ActionListener, Runnable {
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
			ImageCollationDesktop desktop = new ImageCollationDesktop();
			desktop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			desktop.setSize(800,600);
			desktop.setTitle("Image Collation Desktop");
			desktop.setVisible(true);
		}
	}

	/**
	 * The <code>ImageConversionListener</code> is a listener class of
	 * menu selection to convert images.
	 */
	protected class ImageConversionListener implements ActionListener, Runnable {
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
			ImageConversionFrame frame = new ImageConversionFrame();
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(600, 600);
			frame.setTitle("Image Conversion");
			frame.setVisible(true);
		}
	}

	/**
	 * The <code>CrossIdentificationListener</code> is a listener 
	 * class of menu selection to operate the cross identification.
	 */
	protected class CrossIdentificationListener implements ActionListener, Runnable {
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
				CrossIdentificationSettingDialog dialog = new CrossIdentificationSettingDialog();
				int answer = dialog.show(pane);

				if (answer == 0) {
					CatalogReader reader_base = dialog.getBaseCatalogReader();

					String[] paths = net.aerith.misao.util.Format.separatePath(dialog.getBaseCatalogPath());
					for (int i = 0 ; i < paths.length ; i++) {
						try {
							reader_base.addURL(new File(paths[i]).toURL());
						} catch ( MalformedURLException exception ) {
							System.err.println(exception);
						}
					}

					InteractiveCatalogReader interactive_reader_base = new InteractiveCatalogReader(reader_base);

					CatalogReader reader_reference = null;
					if (dialog.identifiesWithDatabase()) {
						reader_reference = new CatalogDBReader(new GlobalDBManager().getCatalogDBManager());
					} else {
						reader_reference = dialog.getReferenceCatalogReader();
					}

					paths = net.aerith.misao.util.Format.separatePath(dialog.getReferenceCatalogPath());
					for (int i = 0 ; i < paths.length ; i++) {
						try {
							reader_reference.addURL(new File(paths[i]).toURL());
						} catch ( MalformedURLException exception ) {
							System.err.println(exception);
						}
					}

					InteractiveCatalogReader interactive_reader_reference = new InteractiveCatalogReader(reader_reference);

					double fov = reader_base.getMaximumPositionErrorInArcsec();
					if (fov < reader_reference.getMaximumPositionErrorInArcsec())
						fov = reader_reference.getMaximumPositionErrorInArcsec();
					fov = fov * 2.0 / 3600.0;

					// Identifies.
					File file = new File(dialog.getOutputPath());
					if (file.exists()) {
						String message = "Overwrite to " + file.getPath() + " ?";
						if (0 != JOptionPane.showConfirmDialog(pane, message, "Confirmation", JOptionPane.YES_NO_OPTION)) {
							return;
						}
					}
					PrintWriter writer = Encoder.newWriter(file);

					interactive_reader_base.open(pane);
					CatalogStar star_base = interactive_reader_base.readNext(pane);
					while (star_base != null) {
						writer.println(star_base.getOutputString());

						// In the case date dependent.
						if (star_base.getDate() != null) {
							interactive_reader_reference.setDate(star_base.getDate());
						}

						CatalogStarList list = interactive_reader_reference.read(pane, star_base.getCoor(), fov);

						Vector list_id = new Vector();
						Vector list_data = new Vector();

						for (int i = 0 ; i < list.size() ; i++) {
							CatalogStar star_reference = (CatalogStar)list.elementAt(i);
							double r = star_base.getCoor().getAngularDistanceTo(star_reference.getCoor()) * 3600.0;
							if (r < star_base.getMaximumPositionErrorInArcsec()  ||
								r < star_reference.getMaximumPositionErrorInArcsec()) {
								// Identified.
								String distance = net.aerith.misao.util.Format.formatDouble(r, 5, 3).trim() + "\"";
								String s = "= " + star_reference.getName() + " (" + distance + ")";
								list_id.addElement(s);
								list_data.addElement(star_reference.getOutputString());
							}
						}

						for (int i = 0 ; i < list_id.size() ; i++)
							writer.println((String)list_id.elementAt(i));
						for (int i = 0 ; i < list_data.size() ; i++)
							writer.println((String)list_data.elementAt(i));

						writer.println("");

						star_base = interactive_reader_base.readNext(pane);
					}
					interactive_reader_base.close();

					writer.close();

					String message = "Succeeded to save " + file.getPath();
					JOptionPane.showMessageDialog(pane, message);
				}

				return;
			} catch ( IOException exception ) {
			} catch ( QueryFailException exception ) {
			}

			String message = "Failed.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * The <code>PhotometryCalibrationListener</code> is a listener 
	 * class of menu selection to open the desktop for photometric
	 * calibration.
	 */
	protected class PhotometryCalibrationListener implements ActionListener, Runnable {
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
			PhotometryCalibrationDesktop desktop = new PhotometryCalibrationDesktop();
			desktop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			desktop.setSize(800,600);
			desktop.setTitle("Photometry Calibration Desktop");
			desktop.setVisible(true);
		}
	}

	/**
	 * The <code>PhotometryZeroPointAdjustmentListener</code> is a 
	 * listener class of menu selection to open the desktop for 
	 * zero-point adjustment of photometry.
	 */
	protected class PhotometryZeroPointAdjustmentListener implements ActionListener, Runnable {
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
			PhotometryZeroPointAdjustmentDesktop desktop = new PhotometryZeroPointAdjustmentDesktop();
			desktop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			desktop.setSize(800,600);
			desktop.setTitle("Photometry Zero-Point Adjustment Desktop");
			desktop.setVisible(true);
		}
	}

	/**
	 * The <code>SearchNewStarsListener</code> is a listener class of 
	 * menu selection to open the desktop to search new stars.
	 */
	protected class SearchNewStarsListener implements ActionListener, Runnable {
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
			NewStarSearchDesktop desktop = new NewStarSearchDesktop();
			desktop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			desktop.setSize(800,600);
			desktop.setTitle("Search New Stars Desktop");
			desktop.setVisible(true);
		}
	}

	/**
	 * The <code>SearchIdentifiedStarsListener</code> is a listener 
	 * class of menu selection to open the desktop to search stars 
	 * identified with a specified catalog.
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
			IdentifiedStarSearchDesktop desktop = new IdentifiedStarSearchDesktop();
			desktop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			desktop.setSize(800,600);
			desktop.setTitle("Search Identified Stars Desktop");
			desktop.setVisible(true);
		}
	}

	/**
	 * The <code>SearchVariableStarsListener</code> is a listener 
	 * class of menu selection to open the desktop to search variable 
	 * stars.
	 */
	protected class SearchVariableStarsListener implements ActionListener, Runnable {
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
			VariableStarSearchDesktop desktop = new VariableStarSearchDesktop();
			desktop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			desktop.setSize(800,600);
			desktop.setTitle("Search Variable Stars Desktop");
			desktop.setVisible(true);
		}
	}

	/**
	 * The <code>DefaultCatalogPathListener</code> is a listener class
	 * of menu selection to configure the default catalog path.
	 */
	protected class DefaultCatalogPathListener implements ActionListener, Runnable {
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
			Vector catalog_list = CatalogManager.getPathOrientedCatalogReaderList();
			ConfigureDefaultCatalogPathDialog dialog = new ConfigureDefaultCatalogPathDialog(catalog_list);

			int answer = dialog.show(pane);
			if (answer == 0) {
				CatalogReader reader = dialog.getSelectedCatalogReader();
				Resource.setDefaultCatalogPath(reader.getName(), dialog.getCatalogPath());
			}
		}
	}

	/**
	 * The <code>ImageConfigurationListener</code> is a listener class
	 * of menu selection to configure the image configuration.
	 */
	protected class ImageConfigurationListener implements ActionListener, Runnable {
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
			ImageConfigurationDialog dialog = new ImageConfigurationDialog();

			int answer = dialog.show(pane);
			if (answer == 0) {
				Resource.setDefaultFitsOrder(dialog.getFitsOrder());
			}
		}
	}

	/**
	 * The <code>ImageAndXmlFileDropTargetListener</code> is a 
	 * listener class of drop event from native filer application.
	 */
	protected class ImageAndXmlFileDropTargetListener extends FileDropTargetAdapter {
		/**
		 * Invoked when files are dropped.
		 * @param files the dropped files.
		 */
		public void dropFiles ( File[] file ) {
			Vector failed_list = new Vector();

			for (int i = 0 ; i < file.length ; i++) {
				// Opens as an image.
				try {
					net.aerith.misao.image.io.Format format = net.aerith.misao.image.io.Format.create(file[i]);

					Thread thread = new ViewImageFileThread(file[i]);
					thread.setPriority(Resource.getThreadPriority());
					thread.start();

					continue;
				} catch ( FileNotFoundException exception ) {
				} catch ( MalformedURLException exception ) {
				} catch ( UnsupportedFileTypeException exception ) {
				}

				// Opens as an XML file.
				XmlFilter filter = new XmlFilter();
				if (filter.accept(file[i])) {
					Thread thread = new ReviewXmlFileThread(file[i]);
					thread.setPriority(Resource.getThreadPriority());
					thread.start();

					continue;
				}

				failed_list.addElement(file[i].getPath());
			}

			if (failed_list.size() > 0) {
				String header = "Failed to open:";
				MessagesDialog dialog = new MessagesDialog(header, failed_list);
				dialog.show(pane);
			}
		}
	}

	/**
	 * The <code>ViewImageFileThread</code> is a thread to view a
	 * dropped image file.
	 */
	protected class ViewImageFileThread extends Thread {
		/**
		 * The image file.
		 */
		protected File file;

		/**
		 * Construct a <code>ViewImageFileThread</code>.
		 * @param file the dropped file.
		 */
		public ViewImageFileThread ( File file ) {
			this.file = file;
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			try {
				net.aerith.misao.image.io.Format format = net.aerith.misao.image.io.Format.create(file);
				MonoImage image = format.read();

				MonoImageComponent image_component = new MonoImageComponent(image);
				image_component.enableImageProcessing();

				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				frame.setBackground(Color.white);
				frame.getContentPane().add(new JScrollPane(image_component));

				frame.setSize(image.getSize().getWidth(), image.getSize().getHeight());
				frame.setTitle(file.getPath());
				frame.setVisible(true);

				return;
			} catch ( MalformedURLException exception ) {
			} catch ( UnsupportedFileTypeException exception ) {
			} catch ( IOException exception ) {
			} catch ( Exception exception ) {
			}

			String message = "Failed to open " + file.getPath() + ".";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * The <code>ReviewXmlFileThread</code> is a thread to review a
	 * dropped XML file.
	 */
	protected class ReviewXmlFileThread extends Thread {
		/**
		 * The XML file.
		 */
		protected File file;

		/**
		 * Construct a <code>ReviewXmlFileThread</code>.
		 * @param file the dropped file.
		 */
		public ReviewXmlFileThread ( File file ) {
			this.file = file;
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			PixyReviewDesktop desktop = new PixyReviewDesktop();
			desktop.setHelpMessageEnabled(false);
			desktop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			desktop.setSize(800,600);
			desktop.setTitle("PIXY System Desktop");
			desktop.setVisible(true);

			desktop.operate(file);
		}
	}
}
