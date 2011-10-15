/*
 * @(#)ImageGalleryPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ImageGallery;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.image.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.pixy.image_loading.XmlImageLoader;
import net.aerith.misao.pixy.Resource;
import net.aerith.misao.toolkit.PixyDesktop.PixyReviewDesktop;

/**
 * The <code>ImageGalleryPanel</code> represents a panel which 
 * consists of a thumbnail image and the buttons to show the original
 * image and to create the PIXY System desktop for review.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class ImageGalleryPanel extends JPanel {
	/**
	 * The image.
	 */
	protected MonoImage image;

	/**
	 * The image information.
	 */
	protected XmlInformation info;

	/**
	 * The magnitude record.
	 */
	protected XmlMagRecord mag_record;

	/**
	 * The image component.
	 */
	protected MonoImageComponent image_component;

	/**
	 * The scroll pane.
	 */
	protected JScrollPane scroll_pane;

	/**
	 * The parent desktop.
	 */
	protected BaseDesktop desktop;

	/**
	 * The pane of this component.
	 */
	protected Container pane;

	/**
	 * Constructs an <code>ImageGalleryPanel</code>.
	 * @param image      the image.
	 * @param info       the image information.
	 * @param mag_record the magnitude record.
	 * @param desktop    the desktop.
	 */
	public ImageGalleryPanel ( MonoImage image, XmlInformation info, XmlMagRecord mag_record, BaseDesktop desktop ) {
		this.image = image;
		this.info = info;
		this.mag_record = mag_record;
		this.desktop = desktop;

		pane = this;

		setLayout(new BorderLayout());

		image_component = new MonoImageComponent(image);
		image_component.enableImageProcessing();

		JButton button_image = new JButton("View Image");
		JButton button_review = new JButton("Review Examination");
		button_image.addActionListener(new ViewImageListener());
		button_review.addActionListener(new ReviewListener());

		JPanel panel_button = new JPanel();
		panel_button.setLayout(new GridLayout(2, 1));
		panel_button.add(button_image);
		panel_button.add(button_review);

		String date = "";
		if (info.getDate() != null)
			date = info.getMidDate().getOutputString(JulianDay.FORMAT_MONTH_IN_REDUCED, JulianDay.getAccuracy(info.getDate()));

		JPanel panel_label = new JPanel();
		panel_label.setLayout(new GridLayout(2, 1));
		panel_label.add(new JLabel(date, JLabel.CENTER));
		panel_label.add(new JLabel(((XmlMag)mag_record.getMag()).getOutputString() + " mag", JLabel.CENTER));

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(panel_button);
		panel.add(panel_label);

		scroll_pane = new FixedScrollPane(image.getSize(), image_component);
		add(scroll_pane, BorderLayout.CENTER);
		add(panel, BorderLayout.SOUTH);
	}

	/**
	 * Zooms in.
	 */
	public void zoomIn ( ) {
		image_component.zoomIn();
	}

	/**
	 * Zooms out.
	 */
	public void zoomOut ( ) {
		image_component.zoomOut();
	}

	/**
	 * Marks up at the center.
	 */
	public void markUpCenter ( ) {
		image_component.markUpCenter();

		// Scrolls and views the center of the image 
		// at the center of the window.
		JScrollBar scroll_bar = scroll_pane.getHorizontalScrollBar();
		scroll_bar.setValue((scroll_bar.getMaximum() - scroll_bar.getModel().getExtent()) / 2);
		scroll_bar = scroll_pane.getVerticalScrollBar();
		scroll_bar.setValue((scroll_bar.getMaximum() - scroll_bar.getModel().getExtent()) / 2);
	}

	/**
	 * Clear the mark.
	 */
	public void clearMark ( ) {
		image_component.clearMark();
	}

	/**
	 * Gets the magnitude record.
	 * @return the magnitude record.
	 */
	public XmlMagRecord getMagRecord ( ) {
		return mag_record;
	}

	/**
	 * Gets the image.
	 * @return the image.
	 */
	public MonoImage getImage ( ) {
		return image;
	}

	/**
	 * Gets the current level adjustment.
	 * @return the current level adjustment.
	 */
	public LevelAdjustmentSet getCurrentLevelAdjustmentSet ( ) {
		return image_component.getCurrentLevel();
	}

	/**
	 * Adjusts the level of image.
	 * @param minimum the minimum level.
	 * @param maximum the maximum level.
	 */
	public void adjustLevel ( double minimum, double maximum ) {
		image_component.adjustLevel(minimum, maximum);
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
		image_component.save(format);
	}

	/**
	 * The <code>ViewImageListener</code> is a listener class of menu 
	 * selection to view the image.
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
				XmlImageLoader image_loader = new XmlImageLoader(info, desktop.getFileManager());
				image_loader.operate();
				MonoImage image = image_loader.getMonoImage();

				MonoImageComponent new_image_component = new MonoImageComponent(image);
				new_image_component.enableImageProcessing();

				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				frame.setBackground(Color.white);
				frame.getContentPane().add(new JScrollPane(new_image_component));

				frame.setSize(image.getSize().getWidth(), image.getSize().getHeight());
				frame.setTitle(info.getImage().getContent());
				frame.setVisible(true);
			} catch ( Exception exception ) {
				String message = "Failed to open " + info.getImage().getContent() + ".";
				JOptionPane.showMessageDialog(pane, message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * The <code>ReviewListener</code> is a listener class of menu 
	 * selection to open the PIXY review desktops.
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
			PixyReviewDesktop new_desktop = new PixyReviewDesktop();
			new_desktop.setHelpMessageEnabled(false);
			new_desktop.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			new_desktop.setSize(800,600);
			new_desktop.setTitle("PIXY System Desktop");
			new_desktop.setVisible(true);

			File file = desktop.getFileManager().newFile(mag_record.getImageXmlPath());
			new_desktop.operate(file);
		}
	}
}
