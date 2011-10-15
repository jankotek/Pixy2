/*
 * @(#)ImageGalleryInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ImageGallery;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;
import javax.swing.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.io.Fits;
import net.aerith.misao.util.JulianDay;
import net.aerith.misao.xml.XmlMagRecord;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.LevelAdjustmentDialog;
import net.aerith.misao.gui.dialog.CommonFileChooser;
import net.aerith.misao.io.FileManager;
import net.aerith.misao.pixy.Resource;

/**
 * The <code>ImageGalleryInternalFrame</code> represents an internal 
 * frame of gallery to show image panels.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class ImageGalleryInternalFrame extends BaseInternalFrame implements LevelAdjustable {
	/**
	 * The content panel.
	 */
	protected JPanel panel;

	/**
	 * The list of image panels.
	 */
	protected Vector list;

	/**
	 * This frame.
	 */
	protected ImageGalleryInternalFrame frame;

	/**
	 * The content pane of this frame.
	 */
	protected Container pane;

	/**
	 * Constructs an <code>ImageGalleryInternalFrame</code>.
	 */
	public ImageGalleryInternalFrame ( ) {
		pane = getContentPane();

		panel = new JPanel();
		list = new Vector();

		pane.add(new JScrollPane(panel));

		frame = this;
	}

	/**
	 * Initializes menu bar. A <code>JMenuBar</code> must be set to 
	 * this <code>JFrame</code> previously.
	 */
	public void initMenu ( ) {
		JMenu menu = addMenu("File");

		JMenuItem item = new JMenuItem("Save");
		item.addActionListener(new SaveListener());
		menu.add(item);

		super.initMenu();

		menu = addMenu("Image");
		if (menu.getItemCount() > 0)
			menu.addSeparator();

		JMenuItem[] items = createImageMenus();
		for (int i = 0 ; i < items.length ; i++) {
			if (items[i] == null)
				menu.addSeparator();
			else
				menu.add(items[i]);
		}
	}

	/**
	 * Adds a gallery image panel.
	 * @param image_panel the gallery image panel.
	 */
	public void addImagePanel ( ImageGalleryPanel image_panel ) {
		panel.add(image_panel);
		list.addElement(image_panel);
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * image menus. Items are newly created when invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createImageMenus ( ) {
		JMenuItem[] items = new JMenuItem[7];
		items[0] = new JMenuItem("Zoom In");
		items[0].addActionListener(new ZoomInListener());
		items[1] = new JMenuItem("Zoom Out");
		items[1].addActionListener(new ZoomOutListener());
		items[2] = null;
		items[3] = new JMenuItem("Level Adjustment");
		items[3].addActionListener(new LevelAdjustmentListener());
		items[4] = null;
		items[5] = new JMenuItem("Mark Up Center");
		items[5].addActionListener(new MarkCenterListener());
		items[6] = new JMenuItem("Clear Mark Up");
		items[6].addActionListener(new ClearMarkListener());
		return items;
	}

	/**
	 * Adjusts the level of image.
	 * @param minimum the minimum level.
	 * @param maximum the maximum level.
	 */
	public void adjustLevel ( double minimum, double maximum ) {
		ImageGalleryPanel image_panel = (ImageGalleryPanel)list.elementAt(0);
		LevelAdjustmentSet stat = image_panel.getCurrentLevelAdjustmentSet();

		double old_minimum = stat.current_minimum;
		double old_maximum = stat.current_maximum;

		double new_minimum = (minimum - old_minimum) / (old_maximum - old_minimum);
		double new_maximum = (maximum - old_minimum) / (old_maximum - old_minimum);

		image_panel.adjustLevel(minimum, maximum);

		for (int i = 1 ; i < list.size() ; i++) {
			image_panel = (ImageGalleryPanel)list.elementAt(i);
			stat = image_panel.getCurrentLevelAdjustmentSet();

			old_minimum = stat.current_minimum;
			old_maximum = stat.current_maximum;

			double new_minimum2 = new_minimum * (old_maximum - old_minimum) + old_minimum;
			double new_maximum2 = new_maximum * (old_maximum - old_minimum) + old_minimum;

			image_panel.adjustLevel(new_minimum2, new_maximum2);
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
	 * The <code>SaveListener</code> is a listener class of menu 
	 * selection to save image files.
	 */
	protected class SaveListener implements ActionListener, Runnable {
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
			file_chooser.setDialogTitle("Choose a directory.");
			file_chooser.setMultiSelectionEnabled(false);
			file_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			if (file_chooser.showOpenDialog(pane) == JFileChooser.APPROVE_OPTION) {
				try {
					File directory = file_chooser.getSelectedFile();
					directory.mkdirs();

					for (int i = 0 ; i < list.size() ; i++) {
						ImageGalleryPanel image_panel = (ImageGalleryPanel)list.elementAt(i);
						XmlMagRecord record = image_panel.getMagRecord();

						JulianDay jd = JulianDay.create(record.getDate());

						String filename = jd.getOutputString(JulianDay.FORMAT_MONTH_IN_NUMBER_WITHOUT_SPACE, JulianDay.FORMAT_DECIMALDAY_100000TH) + ".fts";
						filename = FileManager.unitePath(directory.getAbsolutePath(), filename);

						File file = new File(filename);
						Fits fits = new Fits(file.toURI().toURL());

						image_panel.save(fits);
					}

					String message = "Succeeded.";
					JOptionPane.showMessageDialog(pane, message);
				} catch ( IOException exception ) {
					String message = "Failed to save file.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				} catch ( UnsupportedBufferTypeException exception ) {
					// It occurs only in the case of FITS format.
					String message = "Only 8, 16 and 32 bit integer images are supported to save in FITS format.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				} catch ( UnsupportedFileTypeException exception ) {
					String message = "Failed to save file.";
					JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * The <code>ZoomInListener</code> is a listener class of menu 
	 * selection to zoom in.
	 */
	protected class ZoomInListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			for (int i = 0 ; i < list.size() ; i++) {
				ImageGalleryPanel image_panel = (ImageGalleryPanel)list.elementAt(i);
				image_panel.zoomIn();
			}
		}
	}

	/**
	 * The <code>ZoomOutListener</code> is a listener class of menu 
	 * selection to zoom out.
	 */
	protected class ZoomOutListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			for (int i = 0 ; i < list.size() ; i++) {
				ImageGalleryPanel image_panel = (ImageGalleryPanel)list.elementAt(i);
				image_panel.zoomOut();
			}
		}
	}

	/**
	 * The <code>LevelAdjustmentListener</code> is a listener class of
	 * menu selection to adjust the level of all images.
	 */
	protected class LevelAdjustmentListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			ImageGalleryPanel image_panel = (ImageGalleryPanel)list.elementAt(0);
			MonoImage image = image_panel.getImage();
			LevelAdjustmentSet stat = image_panel.getCurrentLevelAdjustmentSet();

			LevelAdjustmentDialog dialog = new LevelAdjustmentDialog(image, frame, stat);
			dialog.show(pane);
		}
	}

	/**
	 * The <code>MarkCenterListener</code> is a listener class of menu 
	 * selection to mark up center.
	 */
	protected class MarkCenterListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			for (int i = 0 ; i < list.size() ; i++) {
				ImageGalleryPanel image_panel = (ImageGalleryPanel)list.elementAt(i);
				image_panel.markUpCenter();
			}
		}
	}

	/**
	 * The <code>ClearMarkListener</code> is a listener class of menu 
	 * selection to clear mark up.
	 */
	protected class ClearMarkListener implements ActionListener {
		/**
		 * Invoked when one of the menus is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			for (int i = 0 ; i < list.size() ; i++) {
				ImageGalleryPanel image_panel = (ImageGalleryPanel)list.elementAt(i);
				image_panel.clearMark();
			}
		}
	}
}
