/*
 * @(#)ImageCollationDesktop.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ImageCollation;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.filter.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.io.filechooser.*;
import net.aerith.misao.pixy.Resource;
import net.aerith.misao.pixy.image_loading.DefaultImageLoader;
import net.aerith.misao.pixy.star_detection.DefaultStarDetector;
import net.aerith.misao.pixy.matching.DefaultMatchingSolver;
import net.aerith.misao.pixy.pairing.DefaultPairMaker;

/**
 * The <code>ImageCollationDesktop</code> represents a desktop to 
 * collate images. 
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class ImageCollationDesktop extends Desktop {
	/**
	 * The list of images to collate.
	 */
	protected Vector image_list;

	/**
	 * The list of map functions to convert (x,y) on the first image 
	 * to (x,y) on each image.
	 */
	protected MapFunction[] map_functions = null;

	/**
	 * The drop target.
	 */
	protected DropTarget dt;

	/**
	 * The current step.
	 */
	protected int step;

	/**
	 * True if matching between detected stars and catalog data was 
	 * failed.
	 */
	protected boolean matching_failed = false;

	/*
	 * The thread of the current operation.
	 */
	protected Thread thread = null;

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
	 * The step number which means the step is after the matching 
	 * between detected stars and catalog data.
	 */
	protected final static int STEP_AFTER_MATCHING = 3;

	/*
	 * The menu item to open an image.
	 */
	protected JMenuItem menu_open;

	/*
	 * The menu item to operate matching.
	 */
	protected JMenuItem menu_matching;

	/*
	 * The menu item to create a blink image.
	 */
	protected JMenuItem menu_blink;

	/*
	 * The menu item to create an R-GB image.
	 */
	protected JMenuItem menu_rgb;

	/*
	 * The menu item to create an GB-R image.
	 */
	protected JMenuItem menu_gbr;

	/*
	 * The menu item to create a subtraction image.
	 */
	protected JMenuItem menu_subtraction;

	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs an <code>ImageCollationDesktop</code>.
	 */
	public ImageCollationDesktop ( ) {
		pane = getContentPane();

		image_list = new Vector();

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
	 * Initializes menu bar. A <code>JMenuBar</code> must be set to 
	 * this <code>JFrame</code> previously.
	 */
	public void initMenu ( ) {
		JMenu menu = addMenu("File");

		menu = addMenu("Operations");

		menu_open = new JMenuItem("Open Image");
		menu_open.addActionListener(new OpenImageListener());
		menu.add(menu_open);

		menu_matching = new JMenuItem("Matching");
		menu_matching.addActionListener(new MatchingListener());
		menu.add(menu_matching);

		menu_blink = new JMenuItem("Create Blink Image");
		menu_blink.addActionListener(new BlinkListener());
		menu.add(menu_blink);

		menu_rgb = new JMenuItem("Create R-GB Image");
		menu_rgb.addActionListener(new RGBListener(true));
		menu.add(menu_rgb);

		menu_gbr = new JMenuItem("Create GB-R Image");
		menu_gbr.addActionListener(new RGBListener(false));
		menu.add(menu_gbr);

		menu_subtraction = new JMenuItem("Create Subtraction Image");
		menu_subtraction.addActionListener(new SubtractionListener());
		menu.add(menu_subtraction);

		super.initMenu();

		addCopyrightMenu();
	}

	/*
	 * Gets the monitor set.
	 * @return the monitor set.
	 */
	protected MonitorSet getMonitorSet ( ) {
		return monitor_set;
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
		menu_matching.setEnabled(false);
		menu_blink.setEnabled(false);
		menu_rgb.setEnabled(false);
		menu_gbr.setEnabled(false);
		menu_subtraction.setEnabled(false);
		dt.setActive(false);

		switch (step) {
			case STEP_BEGINNING:
				menu_open.setEnabled(true);
				dt.setActive(true);
				break;
			case STEP_IMAGE_OPENED:
//				menu_open.setEnabled(true);
				menu_matching.setEnabled(true);
				break;
			case STEP_AFTER_MATCHING:
				menu_blink.setEnabled(true);
				menu_rgb.setEnabled(true);
				menu_gbr.setEnabled(true);
				menu_subtraction.setEnabled(true);

				// Enables image processing again.
				for (int i = 0 ; i < image_list.size() ; i++) {
					ImageSet set = (ImageSet)image_list.elementAt(i);
					set.getComponent().enableImageProcessing();
				}

				break;
		}
	}

	/**
	 * Opens an image.
	 * @param file   the image file.
	 * @param format the image format.
	 */
	protected void openImage ( File file, net.aerith.misao.image.io.Format format ) {
		try {
			DefaultImageLoader image_loader = new DefaultImageLoader(file, format);
			image_loader.addMonitor(monitor_set);
			image_loader.operate();
			MonoImage image = image_loader.getMonoImage();

			if (image != null) {
				JInternalFrame image_window = new JInternalFrame();
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

				ImageSet set = new ImageSet(image_window, image_component);
				image_list.addElement(set);

				if (image_list.size() == 2) {
					promoteStep();
				}
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
		}
	}

	/**
	 * The <code>ImageSet</code> represents a set of 
	 * <code>JInternalFrame</code> and <code>ImageComponent</code>.
	 */
	protected class ImageSet {
		/**
		 * The frame.
		 */
		protected JInternalFrame frame;

		/**
		 * The component.
		 */
		protected MonoImageComponent component;

		/**
		 * Constructs an <code>ImageSet</code>.
		 * @param initial_frame     the frame.
		 * @param initial_component the component.
		 */
		public ImageSet ( JInternalFrame initial_frame, MonoImageComponent initial_component ) {
			frame = initial_frame;
			component = initial_component;
		}

		/**
		 * Gets the image.
		 * @return the image.
		 */
		public MonoImage getMonoImage ( ) {
			return (MonoImage)component.getImageContent();
		}

		/**
		 * Gets the frame.
		 * @return the frame.
		 */
		public JInternalFrame getFrame ( ) {
			return frame;
		}

		/**
		 * Gets the component.
		 *`@return the component.
		 */
		public MonoImageComponent getComponent ( ) {
			return component;
		}
	}

	/**
	 * The <code>ImageFileDropTargetListener</code> is a listener 
	 * class of drop event from native filer application.
	 */
	protected class ImageFileDropTargetListener extends FileDropTargetAdapter implements Runnable {
		/**
		 * The list of image files.
		 */
		protected File[] files;

		/**
		 * Invoked when files are dropped.
		 * @param files the dropped files.
		 */
		public void dropFiles ( File[] file ) {
			if (thread != null  &&  thread.isAlive())
				return;

			this.files = file;

			thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			for (int i = 0 ; i < files.length ; i++) {
				if (image_list.size() >= 2) {
					String message = "Only collation between two images are supported.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);

					return;
				}
					
				openImage(files[i], null);
			}
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

			// Disables image processing after this.
			for (int i = 0 ; i < image_list.size() ; i++) {
				ImageSet set = (ImageSet)image_list.elementAt(i);
				set.getComponent().disableImageProcessing();
			}
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			try {
				// Detects stars from all images.
				StarImageList[] list = new StarImageList[image_list.size()];
				PositionMap[] map = new PositionMap[image_list.size()];

				for (int i = 0 ; i < image_list.size() ; i++) {
					ImageSet set = (ImageSet)image_list.elementAt(i);
					MonoImage image = set.getMonoImage();

					int width = image.getSize().getWidth();
					int height = image.getSize().getHeight();
					double fwidth = (double)width;
					double fheight = (double)height;

					DefaultStarDetector detector = new DefaultStarDetector(image);
					detector.setThresholdCoefficient(4.0);
					detector.addMonitor(getMonitorSet());
					detector.operate();
					list[i] = detector.getStarList();

					list[i].setMagnitude(new MagnitudeTranslationFormula());
					list[i].sort();

					// Shifts (x,y) position of detected stars
					// so that (0,0) is at the center of the image.
					list[i].shift(new Position(- fwidth / 2.0, - fheight / 2.0));

					map[i] = new PositionMap(new Position(- fwidth / 2.0, - fheight / 2.0), new Position(fwidth / 2.0, fheight / 2.0));
					try {
						map[i].addPosition(list[i]);
					} catch ( OutOfBoundsException exception ) {
						// Never happens.
					}
				}

				map_functions = new MapFunction[image_list.size()];

				// Matching between images.
				// Calculates the map function to convert (x,y) on the first image
				// to (x,y) on the second image.
				MapFunction map_function = null;
				try {
					DefaultMatchingSolver solver = new DefaultMatchingSolver(map[0], map[1]);
					solver.setMode(DefaultMatchingSolver.MODE_IMAGE_TO_IMAGE);
					solver.addMonitor(getMonitorSet());
					solver.operate();
					map_function = solver.getMapFunction();
					matching_failed = false;

					// Reduces the number of stars.
					for (int i = 0 ; i < 2 ; i++) {
						list[i].sort();

						StarImageList l = new StarImageList();
						for (int j = 0 ; j < 200  &&  j < list[i].size() ; j++)
							l.addElement(list[i].elementAt(j));

						list[i] = l;
					}

					// Maps (x,y) of the second list, just for pairing.
					list[1].map(map_function.inverse());

					// Makes pairs between the first image and the second image.
					DefaultPairMaker maker = new DefaultPairMaker(list[0], list[1]);
					maker.setSearchRadius(5.0);
					maker.addMonitor(getMonitorSet());
					maker.operate();
					Vector pair_list = maker.getPairList();

					// Maps (x,y) of the second list again as before.
					list[1].map(map_function);

					// Re-calculates map function based on the list of pairs.
					map_function = new MapFunction(pair_list);
					getMonitorSet().addMessage("[Map function]");
					getMonitorSet().addMessage(map_function.getOutputString());
					getMonitorSet().addSeparator();
				} catch ( Exception exception ) {
					String message = "Matching failed.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);

					if (exception instanceof MatchingFailedException) {
						map_function = ((MatchingFailedException)exception).getMapFunction();
						matching_failed = true;
					}
				}

				map_functions[1] = map_function;

				promoteStep();
			} catch ( Exception exception ) {
				String message = "Failed.";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * The <code>BlinkListener</code> is a listener class of menu 
	 * selection to create a blink image.
	 */
	protected class BlinkListener implements ActionListener, Runnable {
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
			MonoImage base_image = ((ImageSet)image_list.elementAt(0)).getMonoImage();

			JInternalFrame frame = new JInternalFrame();
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(base_image.getSize().getWidth(), base_image.getSize().getHeight());
			frame.setTitle("Blink Image");
			frame.setVisible(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setResizable(true);
			frame.setClosable(true);

			MonoImage[] images = new MonoImage[image_list.size()];
			images[0] = base_image.cloneImage();

			for (int i = 1 ; i < image_list.size() ; i++) {
				ImageSet set = (ImageSet)image_list.elementAt(i);
				MonoImage image = set.getMonoImage();

				MapFilter filter = new MapFilter(map_functions[i].inverse());
				filter.setBuffer(base_image.cloneImage());
				images[i] = filter.operate(image);
			}

			BlinkMonoImageComponent component = new BlinkMonoImageComponent(images);
			frame.getContentPane().add(new JScrollPane(component));
			addFrame(frame);

			component.start();
		}
	}

	/**
	 * The <code>RGBListener</code> is a listener class of menu 
	 * selection to create an R-GB image.
	 */
	protected class RGBListener implements ActionListener, Runnable {
		/**
		 * True if the first image is red.
		 */
		protected boolean red_first = true;

		/**
		 * Constructs a <code>RGBListener</code>.
		 * @param red_first true if the first image is red.
		 */
		public RGBListener ( boolean red_first ) {
			this.red_first = red_first;
		}

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
			MonoImage base_image = ((ImageSet)image_list.elementAt(0)).getMonoImage();

			MonoImage R_image = base_image.cloneImage();

			MonoImage GB_image = ((ImageSet)image_list.elementAt(1)).getMonoImage();
			MapFilter filter = new MapFilter(map_functions[1].inverse());
			filter.setBuffer(base_image.cloneImage());
			GB_image = filter.operate(GB_image);

			if (red_first == false) {
				MonoImage swap = R_image;
				R_image = GB_image;
				GB_image = swap;
			}

			RGBCompositeImage rgb_image = new RGBCompositeImage(R_image, GB_image, GB_image);
			LevelAdjustmentSet R_stat = new DefaultLevelAdjustmentSet(R_image);
			LevelAdjustmentSet GB_stat = new DefaultLevelAdjustmentSet(GB_image);
			rgb_image.setRImageLevelAdjustmentSet(R_stat);
			rgb_image.setGImageLevelAdjustmentSet(GB_stat);
			rgb_image.setBImageLevelAdjustmentSet(GB_stat);

			JInternalFrame frame = new JInternalFrame();
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(rgb_image.getSize().getWidth(), rgb_image.getSize().getHeight());
			if (red_first)
				frame.setTitle("R-GB Image");
			else
				frame.setTitle("GB-R Image");
			frame.setVisible(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setResizable(true);
			frame.setClosable(true);

			RGBCompositeImageComponent component = new RGBCompositeImageComponent(rgb_image);
			component.setMode(RGBCompositeImageComponent.MODE_R_GB);
			frame.getContentPane().add(new JScrollPane(component));
			addFrame(frame);
		}
	}

	/**
	 * The <code>SubtractionListener</code> is a listener class of 
	 * menu selection to create a subtraction image.
	 */
	protected class SubtractionListener implements ActionListener, Runnable {
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
			MonoImage base_image = ((ImageSet)image_list.elementAt(0)).getMonoImage();

			MonoImage image = ((ImageSet)image_list.elementAt(1)).getMonoImage();
			SubtractionFilter filter = new SubtractionFilter(base_image);
			filter.setMapFunction(map_functions[1].inverse());
			image = filter.operate(image);

			JInternalFrame frame = new JInternalFrame();
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setSize(base_image.getSize().getWidth(), base_image.getSize().getHeight());
			frame.setTitle("Subtraction Image");
			frame.setVisible(true);
			frame.setMaximizable(true);
			frame.setIconifiable(true);
			frame.setResizable(true);
			frame.setClosable(true);

			MonoImageComponent component = new MonoImageComponent(image);
			component.enableImageProcessing();
			frame.getContentPane().add(new JScrollPane(component));
			addFrame(frame);
		}
	}
}
