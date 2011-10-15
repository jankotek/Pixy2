/*
 * @(#)MonoImageComponent.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.io.*;
import net.aerith.misao.image.filter.*;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.io.filechooser.*;
import net.aerith.misao.pixy.Resource;

/**
 * The <code>MonoImageComponent</code> represents a GUI component to
 * show an image, with level adjustment function of pixel values.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 17
 */

public class MonoImageComponent extends ImageComponent implements LevelAdjustable {
	/**
	 * The image level and statistics.
	 */
	private LevelAdjustmentSet stat;

	/**
	 * This component.
	 */
	private MonoImageComponent component;

	/**
	 * True if the image processing popup menus are available.
	 */
	protected boolean processible;

	/**
	 * Constructs an empty <code>MonoImageComponent</code>.
	 */
	public MonoImageComponent ( ) {
		super();

		component = this;
	}

	/**
	 * Constructs a <code>ImageComponent</code> with an image.
	 * @param initial_image the image to view.
	 */
	public MonoImageComponent ( MonoImage initial_image ) {
		super(initial_image);

		component = this;

		stat = new DefaultLevelAdjustmentSet(initial_image);

		// Adjusts to the default level.
		adjustLevel(stat.current_minimum, stat.current_maximum);
	}

	/**
	 * Initializes a popup menu. A <tt>popup</tt> must be created 
	 * previously.
	 */
	protected void initPopupMenu ( ) {
		super.initPopupMenu();
		popup.addSeparator();

		JMenuItem[] items = createLevelAdjustmentMenus();
		for (int i = 0 ; i < items.length ; i++)
			popup.add(items[i]);

		if (processible) {
			popup.addSeparator();

			items = createSuperiorImageProcessingMenus();
			for (int i = 0 ; i < items.length ; i++)
				popup.add(items[i]);

			popup.addSeparator();

			items = createImageProcessingMenus();
			for (int i = 0 ; i < items.length ; i++)
				popup.add(items[i]);
		}
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * save menus. Items are newly created when invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createSaveMenus ( ) {
		JMenuItem[] items = new JMenuItem[1];
		items[0] = new JMenuItem("Save");
		items[0].addActionListener(new SaveListener());
		return items;
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * level adjustment menus of pixel values. Items are newly created
	 * when invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createLevelAdjustmentMenus ( ) {
		JMenuItem[] items = new JMenuItem[1];
		items[0] = new JMenuItem("Level Adjustment");
		items[0].addActionListener(new LevelAdjustmentListener());
		return items;
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * superior image processing menus. Items are newly created when
	 * invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createSuperiorImageProcessingMenus ( ) {
		JMenuItem[] items = new JMenuItem[5];

		items[0] = new JMenuItem("Smoothing Filter");
		items[0].addActionListener(new SmoothFilterListener());

		items[1] = new JMenuItem("Median Filter");
		items[1].addActionListener(new MedianFilterListener());

		items[2] = new JMenuItem("Cancel Streaks");
		items[2].addActionListener(new CancelStreakListener());

		items[3] = new JMenuItem("Cancel Blooming");
		items[3].addActionListener(new CancelBloomingListener());

		items[4] = new JMenuItem("Transform Meridian Image");
		items[4].addActionListener(new TransformMeridianImageListener());

		return items;
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * image processing menus. Items are newly created when invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createImageProcessingMenus ( ) {
		JMenuItem[] items = new JMenuItem[8];

		items[0] = new JMenuItem("Inverse White and Black");
		items[0].addActionListener(new InverseImageListener());

		items[1] = new JMenuItem("Reverse Upside Down");
		items[1].addActionListener(new ReverseImageListener());

		items[2] = new JMenuItem("Rescale ST-4/6 Image");
		items[2].addActionListener(new RescaleSTImageListener());

		items[3] = new JMenuItem("Flatten Background");
		items[3].addActionListener(new FlattenBackgroundListener());

		items[4] = new JMenuItem("Remove Lattice Pattern");
		items[4].addActionListener(new RemoveLatticePatternListener());

		items[5] = new JMenuItem("Fill Dark Rows and Columns");
		items[5].addActionListener(new FillDarkRowAndColumnListener());

		items[6] = new JMenuItem("Fill Illegal Rows and Columns");
		items[6].addActionListener(new FillIllegalRowAndColumnListener());

		items[7] = new JMenuItem("Equalize");
		items[7].addActionListener(new EqualizeListener());

		return items;
	}

	/**
	 * Enables the image processing operations.
	 */
	public void enableImageProcessing ( ) {
		processible = true;

		initPopupMenu();
	}

	/**
	 * Disables the image processing operations.
	 */
	public void disableImageProcessing ( ) {
		processible = false;

		initPopupMenu();
	}

	/**
	 * Gets the current image level and statistics.
	 * @return the current image level and statistics.
	 */
	public LevelAdjustmentSet getCurrentLevel ( ) {
		return new LevelAdjustmentSet(stat);
	}

	/**
	 * Adjusts the level of image and repaints the view.
	 * @param minimum the minimum level.
	 * @param maximum the maximum level.
	 */
	public void adjustLevel ( double minimum, double maximum ) {
		if (image_content != null) {
			if (image != null)
				image.flush();

			stat.current_minimum = minimum;
			stat.current_maximum = maximum;
			image = ((MonoImage)image_content).getImage(stat.current_minimum, stat.current_maximum);
			repaint();
		}
	}

	/**
	 * Adjusts the level of all images.
	 * @param minimum the minimum level.
	 * @param maximum the maximum level.
	 */
	public void adjustLevelAll ( double minimum, double maximum ) {
		adjustLevel(minimum, maximum);
	}

	/**
	 * Replaces the image content.
	 * @param new_image the new image.
	 */
	public void replaceImage ( ImageContent new_image ) {
		image_content = new_image;
		dimension = new Dimension(image_content.getSize().getWidth(), image_content.getSize().getHeight());

		stat = new DefaultLevelAdjustmentSet((MonoImage)new_image);

		if (image != null)
			image.flush();

		image = ((MonoImage)image_content).getImage(stat.current_minimum, stat.current_maximum);
		repaint();
	}

	/**
	 * Saves the content image.
	 * @param format the image file format.
	 * @exception IOException if I/O error occurs.
	 * @exception UnsupportedBufferTypeException if the data type is 
	 * unsupported.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	public void save ( net.aerith.misao.image.io.Format format )
		throws IOException, UnsupportedBufferTypeException, UnsupportedFileTypeException
	{
		if (format instanceof Bitmap)
			((Bitmap)format).save((MonoImage)image_content, stat);
		else
			format.save((MonoImage)image_content);
	}

	/**
	 * The <code>SaveListener</code> is a listener class of menu 
	 * selection to save the image.
	 */
	protected class SaveListener implements ActionListener, Runnable {
		/**
		 * Invoked when the menu is selected.
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
			if (image_content != null) {
				try {
					ImageFileSaveChooser file_chooser = new ImageFileSaveChooser();
					file_chooser.setDialogTitle("Save an image file.");
					file_chooser.setMultiSelectionEnabled(false);

					if (file_chooser.showSaveDialog(pane) == JFileChooser.APPROVE_OPTION) {
						File file = file_chooser.getSelectedFile();
						net.aerith.misao.image.io.Format format = file_chooser.getSelectedFileFormat();

						if (file.exists()) {
							String message = "Overwrite to " + file.getPath() + " ?";
							if (0 != JOptionPane.showConfirmDialog(pane, message, "Confirmation", JOptionPane.YES_NO_OPTION)) {
								return;
							}
						}

						save(format);

						String message = "Succeeded to save " + file.getPath();
						JOptionPane.showMessageDialog(pane, message);
					}
				} catch ( MalformedURLException exception ) {
					System.err.println(exception);

					String message = "Failed to save file.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				} catch ( IOException exception ) {
					System.err.println(exception);

					String message = "Failed to save file.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				} catch ( UnsupportedBufferTypeException exception ) {
					System.err.println(exception);

					// It occurs only in the case of FITS format.
					String message = "Only 8, 16 and 32 bit integer images are supported to save in FITS format.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				} catch ( UnsupportedFileTypeException exception ) {
					System.err.println(exception);

					String message = "Failed to save file.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * The <code>LevelAdjustmentListener</code> is a listener class of 
	 * menu selection for level adjustment of pixel values.
	 */
	protected class LevelAdjustmentListener implements ActionListener, Runnable {
		/**
		 * Invoked when the menu is selected.
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
			if (image_content != null) {
				LevelAdjustmentDialog dialog = new LevelAdjustmentDialog((MonoImage)image_content, component, stat);
				dialog.show(pane);
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
			Thread thread = new Thread(this);
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
				new SmoothFilter(dialog.getFilterSize()).operate((MonoImage)image_content);
				replaceImage(image_content);
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
			Thread thread = new Thread(this);
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
				new MedianFilter(dialog.getFilterSize()).operate((MonoImage)image_content);
				replaceImage(image_content);
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
			Thread thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			new StreakCancelFilter().operate((MonoImage)image_content);
			replaceImage(image_content);
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
			Thread thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			new BloomingCancelFilter().operate((MonoImage)image_content);
			replaceImage(image_content);
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
			Thread thread = new Thread(this);
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
				MonoImage new_image = filter.operate((MonoImage)image_content);
				replaceImage(new_image);
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
			Thread thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			((MonoImage)image_content).inverse();
			replaceImage(image_content);
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
			Thread thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			((MonoImage)image_content).reverseVertically();
			replaceImage(image_content);
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
			Thread thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			int height = (int)((double)image_content.getSize().getHeight() * Astro.SBIG_RATIO + 0.5);
			MonoImage new_image = new RescaleFilter(new Size(image_content.getSize().getWidth(), height)).operate((MonoImage)image_content);
			replaceImage(new_image);
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
			Thread thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			MonoImage sky_image = new BackgroundEstimationFilter().operate((MonoImage)image_content);
			sky_image.subtract((MonoImage)image_content);
			sky_image.inverse();
			replaceImage(sky_image);
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
			Thread thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			RemoveLatticePatternFilter filter = new RemoveLatticePatternFilter();
			MonoImage new_image = filter.operate((MonoImage)image_content);
			replaceImage(new_image);
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
			Thread thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			FillIllegalRowAndColumnFilter filter = new FillIllegalRowAndColumnFilter();
			filter.setDecreaseEnabled(false);
			filter.operate((MonoImage)image_content);
			replaceImage(image_content);
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
			Thread thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			new FillIllegalRowAndColumnFilter().operate((MonoImage)image_content);
			replaceImage(image_content);
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
			Thread thread = new Thread(this);
			thread.setPriority(Resource.getThreadPriority());
			thread.start();
		}

		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			new EqualizeFilter().operate((MonoImage)image_content);
			replaceImage(image_content);
		}
	}
}
