/*
 * @(#)PixyOperationDesktop.java
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
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.catalog.star.UserStar;
import net.aerith.misao.xml.*;
import net.aerith.misao.io.filechooser.*;
import net.aerith.misao.pixy.Resource;
import net.aerith.misao.pixy.image_loading.DefaultImageLoader;
import net.aerith.misao.pixy.star_detection.DefaultStarDetector;
import net.aerith.misao.pixy.matching.MatchingOperator;
import net.aerith.misao.pixy.matching.RetryManager;
import net.aerith.misao.pixy.pairing.PairingOperator;
import net.aerith.misao.toolkit.Astrometry.AstrometryPane;
import net.aerith.misao.toolkit.Photometry.PhotometryPane;

/**
 * The <code>PixyOperationDesktop</code> represents a desktop of the
 * PIXY system with a method to run the operation. The results are 
 * shown in this desktop.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 November 24
 */

public class PixyOperationDesktop extends PixyDesktop {
	/**
	 * True if the application is running in the tutorial mode.
	 */
	protected boolean tutorial_mode = false;

	/**
	 * The drop target.
	 */
	protected DropTarget dt;

	/**
	 * The image file.
	 */
	protected XmlImage xml_image;

	/**
	 * The image component;
	 */
	protected MonoImageComponent image_component;

	/**
	 * The list of detected stars.
	 */
	protected StarImageList list_detected;

	/**
	 * The pane for astrometry using user's stars.
	 */
	protected AstrometryPane astrometry_pane;

	/**
	 * The pane for photometry using user's stars.
	 */
	protected PhotometryPane photometry_pane;

	/**
	 * The current step.
	 */
	protected int step;

	/**
	 * The reader of star catalog.
	 */
	protected CatalogReader catalog_reader;

	/**
	 * The limiting magnitude to read stars from the catalog in 
	 * pairing process.
	 */
	protected Double catalog_limiting_mag = null;

	/**
	 * The chart composition of catalog stars.
	 */
	protected ChartMapFunction cmf;

	/**
	 * The limiting_magnitude.
	 */
	protected double limit_mag;

	/**
	 * True if the image is an SBIG ST-4/6 image.
	 */
	protected boolean sbig_image = false;

	/**
	 * True if the image is a reversed image.
	 */
	protected boolean reversed_image = false;

	/**
	 * True if matching between detected stars and catalog data was 
	 * failed.
	 */
	protected boolean matching_failed = false;

	/*
	 * The step number which means the step is at the beginning.
	 */
	protected final static int STEP_BEGINNING = 1;

	/*
	 * The step number which means the step is after the image is 
	 * opened.
	 */
	protected final static int STEP_IMAGE_OPENED = 2;

	/*
	 * The step number which means the step is after stars are 
	 * detected from the image.
	 */
	protected final static int STEP_STARS_DETECTED = 3;

	/*
	 * The step number which means the step is after the matching 
	 * between detected stars and catalog data.
	 */
	protected final static int STEP_AFTER_MATCHING = 4;

	/*
	 * The step number which means the step is after making pairs 
	 * between detected stars and catalog data.
	 */
	protected final static int STEP_AFTER_PAIRING = 5;

	/*
	 * The menu item to open an image.
	 */
	protected JMenuItem menu_open;

	/*
	 * The menu item to detect stars.
	 */
	protected JMenuItem menu_detect;

	/*
	 * The menu item to operate matching.
	 */
	protected JMenuItem menu_matching;

	/*
	 * The menu item to make pairs.
	 */
	protected JMenuItem menu_pairing;

	/*
	 * The menu item to apply smoothing filter.
	 */
	protected JMenuItem menu_smooth_filter;

	/*
	 * The menu item to apply median filter.
	 */
	protected JMenuItem menu_median_filter;

	/*
	 * The menu item to cancel streaks.
	 */
	protected JMenuItem menu_cancel_streak;

	/*
	 * The menu item to cancel blooming.
	 */
	protected JMenuItem menu_cancel_blooming;

	/*
	 * The menu item to transform a meridian image.
	 */
	protected JMenuItem menu_transform_meridian;

	/*
	 * The menu item to inverse white and black.
	 */
	protected JMenuItem menu_inverse;

	/*
	 * The menu item to reverse upside down.
	 */
	protected JMenuItem menu_reverse;

	/*
	 * The menu item to rescale an ST-4/6 image.
	 */
	protected JMenuItem menu_rescaleST;

	/*
	 * The menu item to flatten the background.
	 */
	protected JMenuItem menu_flatten_background;

	/*
	 * The menu item to remove lattice pattern.
	 */
	protected JMenuItem menu_remove_lattice_pattern;

	/*
	 * The menu item to fill dark rows and columns.
	 */
	protected JMenuItem menu_fill_dark;

	/*
	 * The menu item to fill illegal rows and columns.
	 */
	protected JMenuItem menu_fill_illegal;

	/*
	 * The menu item to equalize the image.
	 */
	protected JMenuItem menu_equalize;

	/*
	 * The menu item to show step-by-step help message.
	 */
	protected JMenuItem menu_stepbystep;

	/**
	 * Constructs a <code>PixyOperationDesktop</code>.
	 */
	public PixyOperationDesktop ( ) {
		dt = new DropTarget();
		try {
			dt.addDropTargetListener(new ImageFileDropTargetListener());
			desktop_pane.setDropTarget(dt);
		} catch ( TooManyListenersException exception ) {
			String message = "Drag and drop is impossible.";
			JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
		}

		addWindowListener(new OpenWindowListener());
	}

	/**
	 * Sets the flag to run the application in the tutorial mode.
	 */
	public void setTutorialMode ( ) {
		tutorial_mode = true;
	}

	/**
	 * Adds the <tt>Operaton</tt> menus to the menu bar.
	 */
	public void addOperationMenu ( ) {
		JMenu menu = addMenu("Operation");

		menu_open = new JMenuItem("Open Image");
		menu_open.addActionListener(new OpenImageListener());
		menu.add(menu_open);

		menu_detect = new JMenuItem("Detect Stars");
		menu_detect.addActionListener(new DetectStarsListener());
		menu.add(menu_detect);

		menu_matching = new JMenuItem("Matching");
		menu_matching.addActionListener(new MatchingListener());
		menu.add(menu_matching);

		menu_pairing = new JMenuItem("Make Pairs");
		menu_pairing.addActionListener(new PairingListener());
		menu.add(menu_pairing);
	}

	/**
	 * Adds the <tt>Image</tt> menus to the menu bar.
	 */
	public void addImageMenu ( ) {
		JMenu menu = addMenu("Image");

		menu_smooth_filter = new JMenuItem("Smoothing Filter");
		menu_smooth_filter.addActionListener(new SmoothFilterListener());
		menu.add(menu_smooth_filter);

		menu_median_filter = new JMenuItem("Median Filter");
		menu_median_filter.addActionListener(new MedianFilterListener());
		menu.add(menu_median_filter);

		menu_cancel_streak = new JMenuItem("Cancel Streaks");
		menu_cancel_streak.addActionListener(new CancelStreakListener());
		menu.add(menu_cancel_streak);

		menu_cancel_blooming = new JMenuItem("Cancel Blooming");
		menu_cancel_blooming.addActionListener(new CancelBloomingListener());
		menu.add(menu_cancel_blooming);

		menu_transform_meridian = new JMenuItem("Transform Meridian Image");
		menu_transform_meridian.addActionListener(new TransformMeridianImageListener());
		menu.add(menu_transform_meridian);

		menu.addSeparator();

		menu_inverse = new JMenuItem("Inverse White and Black");
		menu_inverse.addActionListener(new InverseImageListener());
		menu.add(menu_inverse);

		menu_reverse = new JMenuItem("Reverse Upside Down");
		menu_reverse.addActionListener(new ReverseImageListener());
		menu.add(menu_reverse);

		menu_rescaleST = new JMenuItem("Rescale ST-4/6 Image");
		menu_rescaleST.addActionListener(new RescaleSTImageListener());
		menu.add(menu_rescaleST);

		menu_flatten_background = new JMenuItem("Flatten Background");
		menu_flatten_background.addActionListener(new FlattenBackgroundListener());
		menu.add(menu_flatten_background);

		menu_remove_lattice_pattern = new JMenuItem("Remove Lattice Pattern");
		menu_remove_lattice_pattern.addActionListener(new RemoveLatticePatternListener());
		menu.add(menu_remove_lattice_pattern);

		menu_fill_dark = new JMenuItem("Fill Dark Rows and Columns");
		menu_fill_dark.addActionListener(new FillDarkRowAndColumnListener());
		menu.add(menu_fill_dark);

		menu_fill_illegal = new JMenuItem("Fill Illegal Rows and Columns");
		menu_fill_illegal.addActionListener(new FillIllegalRowAndColumnListener());
		menu.add(menu_fill_illegal);

		menu_equalize = new JMenuItem("Equalize");
		menu_equalize.addActionListener(new EqualizeListener());
		menu.add(menu_equalize);
	}

	/**
	 * Adds the <tt>Help</tt> menus to the menu bar.
	 */
	public void addHelpMenu ( ) {
		JMenu menu = addMenu("Help");

		menu_stepbystep = new JMenuItem("Step-by-step Help");
		menu_stepbystep.addActionListener(new StepByStepHelpListener());
		menu.add(menu_stepbystep);

		addCopyrightMenu();
	}

	/**
	 * Initializes the step. The enable/disable status of menus is
	 * updates.
	 */
	protected void initializeStep ( ) {
		step = STEP_BEGINNING;
		updateMenus();
	}

	/**
	 * Promotes the step by 1. The enable/disable status of menus is
	 * updates.
	 */
	protected void promoteStep ( ) {
		step++;
		updateMenus();
	}

	/**
	 * Updates the enable/disable status of menus based on the current
	 * step.
	 */
	protected void updateMenus ( ) {
		menu_open.setEnabled(false);
		menu_detect.setEnabled(false);
		menu_matching.setEnabled(false);
		menu_pairing.setEnabled(false);
		menu_smooth_filter.setEnabled(false);
		menu_median_filter.setEnabled(false);
		menu_cancel_streak.setEnabled(false);
		menu_cancel_blooming.setEnabled(false);
		menu_transform_meridian.setEnabled(false);
		menu_inverse.setEnabled(false);
		menu_reverse.setEnabled(false);
		menu_rescaleST.setEnabled(false);
		menu_flatten_background.setEnabled(false);
		menu_remove_lattice_pattern.setEnabled(false);
		menu_fill_dark.setEnabled(false);
		menu_fill_illegal.setEnabled(false);
		menu_equalize.setEnabled(false);
		dt.setActive(false);

		switch (step) {
			case STEP_BEGINNING:
				menu_open.setEnabled(true);
				dt.setActive(true);
				break;
			case STEP_IMAGE_OPENED:
				menu_detect.setEnabled(true);
				menu_smooth_filter.setEnabled(true);
				menu_median_filter.setEnabled(true);
				menu_cancel_streak.setEnabled(true);
				menu_cancel_blooming.setEnabled(true);
				menu_transform_meridian.setEnabled(true);
				menu_inverse.setEnabled(true);
				menu_reverse.setEnabled(true);
				menu_rescaleST.setEnabled(true);
				menu_flatten_background.setEnabled(true);
				menu_remove_lattice_pattern.setEnabled(true);
				menu_fill_dark.setEnabled(true);
				menu_fill_illegal.setEnabled(true);
				menu_equalize.setEnabled(true);
				break;
			case STEP_STARS_DETECTED:
				menu_matching.setEnabled(true);
				break;
			case STEP_AFTER_MATCHING:
				menu_pairing.setEnabled(true);
				break;
			case STEP_AFTER_PAIRING:
				enableXmlMenus();

				// Enables image processing again.
				image_component.enableImageProcessing();

				break;
		}
	}

	/**
	 * Shows step-by-step help message based on the current step.
	 */
	protected void showStepByStepHelpMessage ( ) {
		switch (step) {
			case STEP_BEGINNING: {
				JLabel label = new JLabel(Resource.getHelpHtmlMessageAtBeginning());
				label.setSize(400,300);
				JOptionPane.showMessageDialog(pane, label);

				if (tutorial_mode) {
					label = new JLabel(Resource.getTutorialHtmlMessageAtBeginning());
					label.setSize(400,300);
					JOptionPane.showMessageDialog(pane, label);
				}
				break;
			}
			case STEP_IMAGE_OPENED: {
				JLabel label = new JLabel(Resource.getHelpHtmlMessageAfterImageOpened());
				label.setSize(400,550);
				JOptionPane.showMessageDialog(pane, label);
				break;
			}
			case STEP_STARS_DETECTED: {
				JLabel label = new JLabel(Resource.getHelpHtmlMessageAfterStarsDetected());
				label.setSize(400,550);
				JOptionPane.showMessageDialog(pane, label);

				if (tutorial_mode) {
					label = new JLabel(Resource.getTutorialHtmlMessageAfterStarsDetected());
					label.setSize(400,550);
					JOptionPane.showMessageDialog(pane, label);
				}
				break;
			}
			case STEP_AFTER_MATCHING: {
				JLabel label = new JLabel(Resource.getHelpHtmlMessageAfterMatching(matching_failed));
				label.setSize(400,550);
				JOptionPane.showMessageDialog(pane, label);
				break;
			}
			case STEP_AFTER_PAIRING: {
				JLabel label = new JLabel(Resource.getHelpHtmlMessageAfterPairing());
				label.setSize(400,550);
				JOptionPane.showMessageDialog(pane, label);

				label = new JLabel(Resource.getSecondHelpHtmlMessageAfterPairing());
				label.setSize(400,550);
				JOptionPane.showMessageDialog(pane, label);

				if (tutorial_mode) {
					label = new JLabel(Resource.getTutorialHtmlMessageAfterPairing());
					label.setSize(400,550);
					JOptionPane.showMessageDialog(pane, label);
				}
				break;
			}
		}
	}

	/**
	 * Opens an image.
	 * @param file   the image file.
	 * @param format the image format.
	 */
	protected void openImage ( File file, net.aerith.misao.image.io.Format format ) {
		try {
			xml_image = new XmlImage();
			xml_image.setContent(file.getPath());
			xml_image.setFileFormat(format);

			DefaultImageLoader image_loader = new DefaultImageLoader(file, format);
			image_loader.addMonitor(monitor_set);
			image_loader.operate();
			image = image_loader.getMonoImage();

			// The FITS image is loaded in the default order as described in
			// the configuration.
			if (format == null)
				format = net.aerith.misao.image.io.Format.create(file);
			if (format.isFits())
				xml_image.setOrder(Resource.getDefaultFitsOrder());

			if (image != null) {
				image_window = new JInternalFrame();
				image_window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				image_window.setSize(image.getSize().getWidth(), image.getSize().getHeight());
				image_window.setTitle(file.getPath());
				image_window.setVisible(true);
				image_window.setMaximizable(true);
				image_window.setIconifiable(true);
				image_window.setResizable(true);

				image_component = new MonoImageComponent(image);
//				image_component.enableImageProcessing();
				image_window.getContentPane().add(new JScrollPane(image_component));
				addFrame(image_window);

				promoteStep();

				if (tutorial_mode)
					showStepByStepHelpMessage();
			}
		} catch ( Exception exception ) {
			String message = "Failed to open the image.";
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

			initializeStep();

			if (tutorial_mode)
				showStepByStepHelpMessage();
		}
	}

	/**
	 * The <code>ImageFileDropTargetListener</code> is a listener 
	 * class of drop event from native filer application.
	 */
	protected class ImageFileDropTargetListener extends FileDropTargetAdapter implements Runnable {
		/**
		 * The image file.
		 */
		protected File file;

		/**
		 * Invoked when files are dropped.
		 * @param files the dropped files.
		 */
		public void dropFiles ( File[] file ) {
			if (thread != null  &&  thread.isAlive())
				return;

			if (file.length != 1)
				return;

			this.file = file[0];

			thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			openImage(file, null);
		}
	}

	/**
	 * The <code>OpenImageListener</code> is a listener class of menu 
	 * selection to open an image.
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

			if (tutorial_mode) {
				file_chooser.setSelectedFile(new File(Resource.getSampleImageFilename()));
			}

			if (file_chooser.showOpenDialog(pane) == JFileChooser.APPROVE_OPTION) {
				try {
					File file = file_chooser.getSelectedFile();
					net.aerith.misao.image.io.Format format = file_chooser.getSelectedFileFormat();

					openImage(file, format);
				} catch ( FileNotFoundException exception ) {
					String message = "Failed to open the image.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				} catch ( MalformedURLException exception ) {
					String message = "Failed to open the image.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				} catch ( UnsupportedFileTypeException exception ) {
					String message = "Failed to open the image.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * The <code>DetectStarsListener</code> is a listener class of 
	 * menu selection to detect stars.
	 */
	protected class DetectStarsListener implements ActionListener, Runnable {
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

			// Disables image processing after this.
			image_component.disableImageProcessing();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			try {
				StarDetectionSettingDialog dialog = new StarDetectionSettingDialog();

				int answer = dialog.show(pane);
				if (answer == 0) {
					DefaultStarDetector detector = new DefaultStarDetector(image);
					detector.setMode(dialog.getMode());
					detector.setApertureSize(dialog.getInnerApertureSize(), dialog.getOuterApertureSize());
					if (dialog.correctsBloomingPosition())
						detector.setCorrectBloomingPosition();
					detector.addMonitor(getMonitorSet());
					detector.operate();
					StarImageList list = detector.getStarList();

					list.setMagnitude(new MagnitudeTranslationFormula());
					list.sort();
					int count = 1000;
					if (count > list.size())
						count = list.size() - 1;
					limit_mag = ((StarPosition)list.elementAt(count)).getMag();

					detected_stars_window = new JInternalFrame();
					detected_stars_window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					detected_stars_window.setSize(image.getSize().getWidth(), image.getSize().getHeight());
					detected_stars_window.setTitle("Detected Stars");
					detected_stars_window.setVisible(true);
					detected_stars_window.setMaximizable(true);
					detected_stars_window.setIconifiable(true);
					detected_stars_window.setResizable(true);

					PlotProperty property = new PlotProperty();
					property.setColor(Color.black);
					property.setFilled(true);
					property.setDependentSizeParameters(1.0, limit_mag, 1);
					property.setMark(PlotProperty.PLOT_CIRCLE);

					detected_chart = new ChartComponent(image.getSize());
					detected_chart.setBasepointAtTopLeft();
					detected_chart.setDefaultProperty(property);
					detected_chart.setStarPositionList(list);
					detected_chart.setBackground(Color.white);

					detected_chart.setStarClickListener(new DetectedStarClickListener());

					detected_stars_window.getContentPane().add(new JScrollPane(detected_chart));
					addFrame(detected_stars_window);

					list_detected = list;

					promoteStep();

					if (tutorial_mode)
						showStepByStepHelpMessage();
				}
			} catch ( Exception exception ) {
				String message = "Failed.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * The <code>MatchingListener</code> is a listener class of menu 
	 * selection to operate matching.
	 */
	protected class MatchingListener implements ActionListener, Runnable {
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
			// Inputs the approximate R.A. and Decl., and the field of view.
			// Selects star catalog to use in matching.
			MatchingCatalogSettingDialog dialog = new MatchingCatalogSettingDialog();

			if (tutorial_mode) {
				dialog.addCatalogReader(CatalogManager.getSampleStarCatalogReader());
				dialog.selectCatalogReader(CatalogManager.getSampleStarCatalogReader());
				dialog.setCatalogPath(new File(Resource.getSampleStarCatalogFilename()).getPath());
				dialog.setCoor(Resource.getSampleImageCoor());
				dialog.setFieldOfView(Resource.getSampleImageHorizontalFieldOfView(), Resource.getSampleImageVerticalFieldOfView());
			}

			int answer = dialog.show(pane);

			if (answer == 0) {
				MatchingSettingDialog mode_dialog = new MatchingSettingDialog();

				answer = mode_dialog.show(pane);

				if (answer == 0) {
					detected_chart.setStarClickListener(null);

					try {
						catalog_reader = dialog.getSelectedCatalogReader();

						if (dialog.isLimitingMagnitudeSpecified())
							catalog_limiting_mag = new Double(dialog.getLimitingMag());

						MatchingOperator operator = null;

						// Retry 9 times at most.
						RetryManager retry_manager = new RetryManager(
							dialog.getCoor(), 
							dialog.getHorizontalFieldOfView(), 
							dialog.getVerticalFieldOfView());
						if (mode_dialog.getMode() == MatchingSettingDialog.MODE_DEFAULT)
							retry_manager.setPolicy(RetryManager.POLICY_NO_RETRY);
						else if (mode_dialog.getMode() == MatchingSettingDialog.MODE_RETRY)
							retry_manager.setPolicy(RetryManager.POLICY_POSITION_UNCERTAIN);
						else if (mode_dialog.getMode() == MatchingSettingDialog.MODE_SKIP_MATCHING)
							retry_manager.setPolicy(RetryManager.POLICY_NO_RETRY);
						else if (mode_dialog.getMode() == MatchingSettingDialog.MODE_SEARCH) {
							retry_manager.setPolicy(RetryManager.POLICY_POSITION_SEARCH);
							retry_manager.setSearchRadius(mode_dialog.getSearchRadius());
						}

						// Operates matching.
						try {
							while (true) {
								try {
									operator = new MatchingOperator(
										image, catalog_reader, dialog.getCatalogPath(),
										retry_manager.getCenterCoor(),
										retry_manager.getHorizontalFov(),
										retry_manager.getVerticalFov(),
										mode_dialog.getPositionAngleOfUp(),
										list_detected);
									if (mode_dialog.getMode() == MatchingSettingDialog.MODE_SKIP_MATCHING)
										operator.setMode(MatchingOperator.MODE_ACCURATE);
									if (mode_dialog.isLooseJudgementSelected())
										operator.setJudgementMode(MatchingOperator.JUDGEMENT_LOOSE);
									operator.addMonitor(getMonitorSet());
									operator.enableInteractive(pane);

									matching_failed = false;

									operator.operate();

									break;
								} catch ( Exception exception ) {
									// Here an exception is thrown when it reaches to the 
									// maximum retry count.
									retry_manager.increment();

									getMonitorSet().addMessage("Matching failed. Retries.");
								}
							}
						} catch ( Exception exception ) {
							// In the case matching was failed.
							matching_failed = true;
						}

						StarList list = operator.getCatalogList();
						cmf = operator.getChartMapFunction();
						limit_mag = operator.getLimitingMag();

						// The name of the base catalog.
						Star star = (Star)list.elementAt(0);
						if (star instanceof MergedStar)
							star = ((MergedStar)star).getStarAt(0);
						String base_catalog = ((CatalogStar)star).getCatalogName();

						catalog_stars_window = new JInternalFrame();
						catalog_stars_window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						catalog_stars_window.setSize(image.getSize().getWidth(), image.getSize().getHeight());
						catalog_stars_window.setTitle(base_catalog);
						catalog_stars_window.setVisible(true);
						catalog_stars_window.setMaximizable(true);
						catalog_stars_window.setIconifiable(true);
						catalog_stars_window.setResizable(true);

						PlotProperty property = new PlotProperty();
						property.setColor(Color.black);
						property.setFilled(true);
						property.setDependentSizeParameters(1.0, limit_mag, 1);
						property.setMark(PlotProperty.PLOT_CIRCLE);

						catalog_chart = new ChartComponent(image.getSize());
						catalog_chart.setBasepointAtTopLeft();
						catalog_chart.setDefaultProperty(property);
						catalog_chart.setStarPositionList(list);
						catalog_chart.setBackground(Color.white);

						catalog_stars_window.getContentPane().add(new JScrollPane(catalog_chart));
						addFrame(catalog_stars_window);

						promoteStep();

						if (tutorial_mode) {
							showStepByStepHelpMessage();
						} else if (matching_failed) {
							String message = "Matching failed.";
							JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
						}

						return;
					} catch ( Exception exception ) {
						String message = "Failed.";
						JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}

	/**
	 * The <code>PairingListener</code> is a listener class of menu 
	 * selection to make pairs.
	 */
	protected class PairingListener implements ActionListener, Runnable {
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
			// Sets the parameters for pairing process.
			PairingSettingDialog dialog = new PairingSettingDialog();

			int answer = dialog.show(pane);

			if (answer == 0) {
				try {
					// Operates pairing.
					PairingOperator operator = new PairingOperator(
						image, catalog_reader, cmf, list_detected, xml_image,
						sbig_image, reversed_image);
					if (dialog.calculatesDistortionField() == false)
						operator.assumeFlat();
					operator.fixMagnitudeTranslationFormulaGradient();
					if (catalog_limiting_mag != null)
						operator.setCatalogLimitingMagnitude(catalog_limiting_mag.doubleValue());
					if (dialog.fixesLimitingMagnitude())
						operator.fixLimitingMagnitude(dialog.getLimitingMag(), dialog.getUpperLimitMag());
					operator.addMonitor(getMonitorSet());
					operator.enableInteractive(pane);
					operator.operate();

					report = operator.getXmlReportDocument();

					if (tutorial_mode) {
						XmlInformation info = (XmlInformation)report.getInformation();

						info.setDate(Resource.getSampleImageDate().getOutputString(JulianDay.FORMAT_MONTH_IN_REDUCED, JulianDay.FORMAT_TO_SECOND));
						XmlExposure exposure = new XmlExposure();
						exposure.setContent((float)Resource.getSampleImageExposureInSecond());
						exposure.setUnit("second");
						info.setExposure(exposure);

						info.setFilter(Resource.getSampleImageMagSystemCode());

						info.setChip(Resource.getSampleImageChipCode());
					}

					list_detected = operator.getDetectedList();
					StarList list_catalog = operator.getCatalogList();
					limit_mag = operator.getLimitingMag();

					// Sets the color. A pair is green, otherwise red.
					String star_class = CatalogManager.getCatalogStarClassName(report.getInformation().getBaseCatalog());
					XmlData data = (XmlData)report.getData();
					int star_count = data.getStarCount();
					XmlStar[] xml_stars = (XmlStar[])data.getStar();
					for (int i = 0 ; i < star_count ; i++) {
						StarImage star = xml_stars[i].getStarImage();
						if (star != null)
							star.setColor(getTypeColor(xml_stars[i].getType()));
						Star[] stars = xml_stars[i].getRecords(star_class);
						for (int j = 0 ; j < stars.length ; j++)
							stars[j].setColor(getTypeColor(xml_stars[i].getType()));
					}

					PlotProperty property = new PlotProperty();
					property.useStarObjectColor();
					property.setFilled(true);
					property.setDependentSizeParameters(1.0, limit_mag, 1);
					property.setMark(PlotProperty.PLOT_CIRCLE);

					detected_chart.setDefaultProperty(property);
					detected_chart.setStarPositionList(list_detected);
					detected_chart.repaint();

					catalog_chart.setDefaultProperty(property);
					catalog_chart.setStarPositionList(list_catalog);
					catalog_chart.repaint();

					// Opens the image information table.
					showImageInformationTable();

					promoteStep();

					if (tutorial_mode)
						showStepByStepHelpMessage();

					return;
				} catch ( Exception exception ) {
					String message = "Failed.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * The <code>SmoothFilterListener</code> is a listener class of
	 * menu selection to apply smoothing filter.
	 */
	protected class SmoothFilterListener implements ActionListener, Runnable {
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
			FilterSizeDialog dialog = new FilterSizeDialog("Smoothing Filter");

			int answer = dialog.show(pane);

			if (answer == 0) {
				new SmoothFilter(dialog.getFilterSize()).operate(image);

				image_window.getContentPane().removeAll();
				image_window.getContentPane().add(new JScrollPane(new MonoImageComponent(image)));
				image_window.validate();

				Statistics stat = new Statistics(image);
				stat.calculate();
				getMonitorSet().addMessage("[Smoothing filter]");
				getMonitorSet().addMessage("Filter size: " + dialog.getFilterSize());
				getMonitorSet().addMessage("Statistics: " + stat.getOutputString());
				getMonitorSet().addSeparator();
			}
		}
	}

	/**
	 * The <code>MedianFilterListener</code> is a listener class of
	 * menu selection to apply median filter.
	 */
	protected class MedianFilterListener implements ActionListener, Runnable {
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
			FilterSizeDialog dialog = new FilterSizeDialog("Median Filter");

			int answer = dialog.show(pane);

			if (answer == 0) {
				new MedianFilter(dialog.getFilterSize()).operate(image);

				image_window.getContentPane().removeAll();
				image_window.getContentPane().add(new JScrollPane(new MonoImageComponent(image)));
				image_window.validate();

				Statistics stat = new Statistics(image);
				stat.calculate();
				getMonitorSet().addMessage("[Median filter]");
				getMonitorSet().addMessage("Filter size: " + dialog.getFilterSize());
				getMonitorSet().addMessage("Statistics: " + stat.getOutputString());
				getMonitorSet().addSeparator();
			}
		}
	}

	/**
	 * The <code>CancelStreakListener</code> is a listener class of
	 * menu selection to cancel streaks.
	 */
	protected class CancelStreakListener implements ActionListener, Runnable {
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
			StreakCancelFilter filter = new StreakCancelFilter();
			filter.addMonitor(getMonitorSet());
			filter.operate(image);

			image_window.getContentPane().removeAll();
			image_window.getContentPane().add(new JScrollPane(new MonoImageComponent(image)));
			image_window.validate();

			Statistics stat = new Statistics(image);
			stat.calculate();
			getMonitorSet().addMessage("Statistics: " + stat.getOutputString());
			getMonitorSet().addSeparator();
		}
	}

	/**
	 * The <code>CancelBloomingListener</code> is a listener class of
	 * menu selection to cancel blooming.
	 */
	protected class CancelBloomingListener implements ActionListener, Runnable {
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
			BloomingCancelFilter filter = new BloomingCancelFilter();
			filter.addMonitor(getMonitorSet());
			filter.operate(image);

			image_window.getContentPane().removeAll();
			image_window.getContentPane().add(new JScrollPane(new MonoImageComponent(image)));
			image_window.validate();

			Statistics stat = new Statistics(image);
			stat.calculate();
			getMonitorSet().addMessage("Statistics: " + stat.getOutputString());
			getMonitorSet().addSeparator();
		}
	}

	/**
	 * The <code>TransformMeridianImageListener</code> is a listener 
	 * class of menu selection to transform a meridian image.
	 */
	protected class TransformMeridianImageListener implements ActionListener, Runnable {
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
			MeridianImageTransformFilterSettingDialog dialog = new MeridianImageTransformFilterSettingDialog();

			int answer = dialog.show(pane);

			if (answer == 0) {
				MeridianImageTransformFilter filter = new MeridianImageTransformFilter(dialog.getDeclAtCenter(), dialog.getPixelSizeInArcsec(), dialog.getRAIntervalInSecond());
				image = filter.operate(image);

				image_window.getContentPane().removeAll();
				image_window.getContentPane().add(new JScrollPane(new MonoImageComponent(image)));
				image_window.validate();

				Statistics stat = new Statistics(image);
				stat.calculate();
				getMonitorSet().addMessage("Statistics: " + stat.getOutputString());
				getMonitorSet().addSeparator();
			}
		}
	}

	/**
	 * The <code>InverseImageListener</code> is a listener class of
	 * menu selection to inverse white and black.
	 */
	protected class InverseImageListener implements ActionListener, Runnable {
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
			image.inverse();

			image_window.getContentPane().removeAll();
			image_window.getContentPane().add(new JScrollPane(new MonoImageComponent(image)));
			image_window.validate();

			Statistics stat = new Statistics(image);
			stat.calculate();
			getMonitorSet().addMessage("[Inversing white and black]");
			getMonitorSet().addMessage("Statistics: " + stat.getOutputString());
			getMonitorSet().addSeparator();
		}
	}

	/**
	 * The <code>ReverseImageListener</code> is a listener class of
	 * menu selection to reverse upside down.
	 */
	protected class ReverseImageListener implements ActionListener, Runnable {
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
			reversed_image = true;

			image.reverseVertically();

			image_window.getContentPane().removeAll();
			image_window.getContentPane().add(new JScrollPane(new MonoImageComponent(image)));
			image_window.validate();

			getMonitorSet().addMessage("[Reversing upside down]");
			getMonitorSet().addSeparator();
		}
	}

	/**
	 * The <code>RescaleSTImageListener</code> is a listener class of
	 * menu selection to rescale an ST-4/6 image.
	 */
	protected class RescaleSTImageListener implements ActionListener, Runnable {
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
			sbig_image = true;

			int height = (int)((double)image.getSize().getHeight() * Astro.SBIG_RATIO + 0.5);
			image = new RescaleFilter(new Size(image.getSize().getWidth(), height)).operate(image);

			image_window.getContentPane().removeAll();
			image_window.getContentPane().add(new JScrollPane(new MonoImageComponent(image)));
			image_window.validate();

			getMonitorSet().addMessage("[Rescaling ST-4/6 image]");
			getMonitorSet().addMessage("Size: " + image.getSize().getWidth() + " x " + image.getSize().getHeight());
			getMonitorSet().addSeparator();
		}
	}

	/**
	 * The <code>FlattenBackgroundListener</code> is a listener class 
	 * of menu selection to flatten the background.
	 */
	protected class FlattenBackgroundListener implements ActionListener, Runnable {
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
			MonoImage sky_image = new BackgroundEstimationFilter().operate(image);
			sky_image.subtract(image);
			sky_image.inverse();
			image = sky_image;

			image_window.getContentPane().removeAll();
			image_window.getContentPane().add(new JScrollPane(new MonoImageComponent(image)));
			image_window.validate();

			Statistics stat = new Statistics(image);
			stat.calculate();
			getMonitorSet().addMessage("[Flatten background]");
			getMonitorSet().addMessage("Statistics: " + stat.getOutputString());
			getMonitorSet().addSeparator();
		}
	}

	/**
	 * The <code>RemoveLatticePatternListener</code> is a listener
	 * class of menu selection to remove lattice pattern.
	 */
	protected class RemoveLatticePatternListener implements ActionListener, Runnable {
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
			RemoveLatticePatternFilter filter = new RemoveLatticePatternFilter();
			image = filter.operate(image);

			image_window.getContentPane().removeAll();
			image_window.getContentPane().add(new JScrollPane(new MonoImageComponent(image)));
			image_window.validate();

			Statistics stat = new Statistics(image);
			stat.calculate();
			getMonitorSet().addMessage("[Remove lattice pattern]");
			getMonitorSet().addMessage("Statistics: " + stat.getOutputString());
			getMonitorSet().addSeparator();
		}
	}

	/**
	 * The <code>FillDarkRowAndColumnListener</code> is a listener
	 * class of menu selection to fill dark rows and columns.
	 */
	protected class FillDarkRowAndColumnListener implements ActionListener, Runnable {
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
			FillIllegalRowAndColumnFilter filter = new FillIllegalRowAndColumnFilter();
			filter.setDecreaseEnabled(false);
			filter.operate(image);

			image_window.getContentPane().removeAll();
			image_window.getContentPane().add(new JScrollPane(new MonoImageComponent(image)));
			image_window.validate();

			Statistics stat = new Statistics(image);
			stat.calculate();
			getMonitorSet().addMessage("[Filling dark rows and columns]");
			getMonitorSet().addMessage("Statistics: " + stat.getOutputString());
			getMonitorSet().addSeparator();
		}
	}

	/**
	 * The <code>FillIllegalRowAndColumnListener</code> is a listener
	 * class of menu selection to fill illegal rows and columns.
	 */
	protected class FillIllegalRowAndColumnListener implements ActionListener, Runnable {
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
			new FillIllegalRowAndColumnFilter().operate(image);

			image_window.getContentPane().removeAll();
			image_window.getContentPane().add(new JScrollPane(new MonoImageComponent(image)));
			image_window.validate();

			Statistics stat = new Statistics(image);
			stat.calculate();
			getMonitorSet().addMessage("[Filling illegal rows and columns]");
			getMonitorSet().addMessage("Statistics: " + stat.getOutputString());
			getMonitorSet().addSeparator();
		}
	}

	/**
	 * The <code>EqualizeListener</code> is a listener class of menu
	 * selection to equalize the image.
	 */
	protected class EqualizeListener implements ActionListener, Runnable {
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
			new EqualizeFilter().operate(image);

			image_window.getContentPane().removeAll();
			image_window.getContentPane().add(new JScrollPane(new MonoImageComponent(image)));
			image_window.validate();

			Statistics stat = new Statistics(image);
			stat.calculate();
			getMonitorSet().addMessage("[Equalize]");
			getMonitorSet().addMessage("Statistics: " + stat.getOutputString());
			getMonitorSet().addSeparator();
		}
	}

	/**
	 * The <code>StepbyStepHelpListener</code> is a listener class of
	 * menu selection to show step-by-step help message.
	 */
	protected class StepByStepHelpListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			showStepByStepHelpMessage();
		}
	}

	/**
	 * The <code>DetectedStarClickListener</code> is a listener class 
	 * of star click evnet on the detected star chart.
	 */
	protected class DetectedStarClickListener implements StarClickListener {
		/**
		 * Invoked when some stars are clicked.
		 * @param star_list the list of clicked stars.
		 */
		public void starsClicked ( StarList star_list ) {
			// Selects the brightest star.
			StarImage star_image = (StarImage)star_list.elementAt(0);
			for (int i = 1 ; i < star_list.size() ; i++) {
				StarImage star = (StarImage)star_list.elementAt(i);
				if (star_image.getValue() < star.getValue())
					star_image = star;
			}

			String[] strings = star_image.getOutputStringsWithXY();

			UserStarDialog dialog = new UserStarDialog(strings);

			int answer = dialog.show(pane);
			if (answer == 0) {
				UserStar catalog_star = dialog.getStar();

				// The default (x,y) position.
				catalog_star.setX(star_image.getX());
				catalog_star.setY(star_image.getY());

				if (report == null) {
					// Calculates the dummy chart map function.
					cmf = new ChartMapFunction(catalog_star.getCoor(), 3600.0, 0.0);

					// Calculates the dummy magnitude translation formula
					// and the limting magnitude.
					MagnitudeTranslationFormula mag_formula = new MagnitudeTranslationFormula();
					for (int i = 0 ; i < list_detected.size() ; i++) {
						StarImage star = (StarImage)list_detected.elementAt(i);
						double mag = mag_formula.convertToMagnitude(star.getValue());
						star.setMag(mag);
						if (i == 0  ||  limit_mag < mag)
							limit_mag = mag;
					}

					// Creates the XML report document.

					report = new XmlReport();

					JulianDay date = JulianDay.create(new Date());

					XmlSystem system = new XmlSystem();
					system.setVersion(Resource.getVersion());
					system.setExaminedJD(date);
					system.setModifiedJD(date);
					report.setSystem(system);

					XmlInformation info = new XmlInformation();

					XmlImage xml_image2 = new XmlImage();
					xml_image2.setContent(xml_image.getContent());
					xml_image2.setFormat(xml_image.getFormat());
					xml_image2.setOrder(xml_image.getOrder());
					info.setImage(xml_image2);

					info.setInformation(image.getSize(), cmf);
					info.setFormattedLimitingMag(limit_mag);
					info.setFormattedUpperLimitMag(limit_mag);
					info.setMagnitudeTranslationFormula(mag_formula);
					info.setBaseCatalog(catalog_star.getCatalogName());
					if (reversed_image)
						info.setReversedImage();
					if (sbig_image)
						info.setSbigImage();
					report.setInformation(info);

					XmlData data = new XmlData();
					data.setStar(new XmlStar[0]);

					int NEW_count = 0;
					for (int i = 0 ; i < list_detected.size() ; i++) {
						Star star = (Star)list_detected.elementAt(i);

						XmlStar s = new XmlStar();
						NEW_count++;
						s.setName("NEW", NEW_count);

						s.addStar(star);

						data.addStar(s);
					}

					data.createStarMap(image.getSize());

					report.setData(data);

					report.countStars();

					// Creates the chart of the catalog data.

					StarList catalog_list = new StarList();
					catalog_list.addElement(catalog_star);

					catalog_stars_window = new JInternalFrame();
					catalog_stars_window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					catalog_stars_window.setSize(image.getSize().getWidth(), image.getSize().getHeight());
					catalog_stars_window.setTitle(catalog_star.getCatalogName());
					catalog_stars_window.setVisible(true);
					catalog_stars_window.setMaximizable(true);
					catalog_stars_window.setIconifiable(true);
					catalog_stars_window.setResizable(true);

					PlotProperty property = new PlotProperty();
					property.setColor(Color.black);
					property.setFilled(true);
					property.setDependentSizeParameters(1.0, limit_mag, 1);
					property.setMark(PlotProperty.PLOT_CIRCLE);

					catalog_chart = new ChartComponent(image.getSize());
					catalog_chart.setBasepointAtTopLeft();
					catalog_chart.setDefaultProperty(property);
					catalog_chart.setStarPositionList(catalog_list);
					catalog_chart.setBackground(Color.white);

					catalog_stars_window.getContentPane().add(new JScrollPane(catalog_chart));
					addFrame(catalog_stars_window);

					// Creates the astrometry table.
					AstrometrySetting astrometry_setting = new AstrometrySetting(catalog_star.getCatalogName());

					JInternalFrame frame = new JInternalFrame();
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					frame.setSize(700,500);
					frame.setTitle("Astrometry Table");
					frame.setVisible(true);
					frame.setMaximizable(true);
					frame.setIconifiable(true);
					frame.setResizable(true);
					astrometry_pane = new AstrometryPane(report, astrometry_setting);
					frame.getContentPane().add(astrometry_pane);
					addFrame(frame);
					astrometry_pane.addReportDocumentUpdatedListener(desktop);

					// Creates the photometry table.
					PhotometrySetting photometry_setting = new PhotometrySetting(catalog_star.getCatalogName());

					frame = new JInternalFrame();
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					frame.setSize(700,500);
					frame.setTitle("Photometry Table");
					frame.setVisible(true);
					frame.setMaximizable(true);
					frame.setIconifiable(true);
					frame.setResizable(true);
					photometry_pane = new PhotometryPane(report, photometry_setting);
					frame.getContentPane().add(photometry_pane);
					addFrame(frame);
					photometry_pane.addReportDocumentUpdatedListener(desktop);

					// Sets the color of all detected stars as red.
					for (int i = 0 ; i < list_detected.size() ; i++) {
						StarImage star = (StarImage)list_detected.elementAt(i);
						star.setColor(getTypeColor("NEW"));
					}

					// Jumps to the final step.
					step = STEP_AFTER_PAIRING;
					updateMenus();
				}

				StarPair pair = new StarPair(star_image, catalog_star);

				// Sets the color of paired stars as green.
				star_image.setColor(getTypeColor("STR"));
				catalog_star.setColor(getTypeColor("STR"));

				// Adds the user's star to the XML report document.
				int str_number = 0;
				if (report.getInformation().getStarCount() != null)
					str_number = report.getInformation().getStarCount().getStr() + 1;

				XmlStar xml_star = ((XmlData)report.getData()).getStar(star_image);
				xml_star.setName("STR", str_number);
				xml_star.addStar(catalog_star);

				report.countStars();

				starsUpdated(report);

				astrometry_pane.updateContents();
				photometry_pane.updateContents();
			}
		}
	}
}
