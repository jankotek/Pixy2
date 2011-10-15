/*
 * @(#)RGBCompositeImageComponent.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.filter.*;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.pixy.Resource;

/**
 * The <code>RGBCompositeImageComponent</code> represents a GUI 
 * component to show an RGB composite image. It has a level adjustment
 * function of pixel values of image of each color.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class RGBCompositeImageComponent extends ImageComponent {
	/**
	 * The mode.
	 */
	protected int mode = MODE_R_GB;

	/**
	 * The mode number which represents composition of two images, one
	 * for R color, the other for GB color.
	 */
	public final static int MODE_R_GB = 1;

	/**
	 * The mode number which represents composition of three images, 
	 * one for R color, one for G color, and the other for B color.
	 */
	public final static int MODE_R_G_B = 2;

	/**
	 * Constructs a <code>RGBCompositeImageComponent</code>.
	 * @param rgb_image the RGB composite image.
	 */
	public RGBCompositeImageComponent ( RGBCompositeImage rgb_image ) {
		super(rgb_image);

		initPopupMenu();
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
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * level adjustment menus of pixel values. Items are newly created
	 * when invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createLevelAdjustmentMenus ( ) {
		JMenuItem[] items = null;

		if (mode == MODE_R_GB) {
			items = new JMenuItem[2];

			items[0] = new JMenuItem("Level Adjustment of R Image");
			items[0].addActionListener(new LevelAdjustmentListener(0));

			items[1] = new JMenuItem("Level Adjustment of GB Image");
			items[1].addActionListener(new LevelAdjustmentListener(1));
		} else {
			items = new JMenuItem[3];

			items[0] = new JMenuItem("Level Adjustment of R Image");
			items[0].addActionListener(new LevelAdjustmentListener(0));

			items[1] = new JMenuItem("Level Adjustment of G Image");
			items[1].addActionListener(new LevelAdjustmentListener(1));

			items[2] = new JMenuItem("Level Adjustment of B Image");
			items[2].addActionListener(new LevelAdjustmentListener(2));
		}

		return items;
	}

	/**
	 * Sets the mode.
	 * @param m the mode.
	 */
	public void setMode ( int m ) {
		mode = m;

		initPopupMenu();
	}

	/**
	 * The <code>LevelAdjustmentListener</code> is a listener class of 
	 * menu selection for level adjustment of pixel values.
	 */
	protected class LevelAdjustmentListener implements ActionListener, Runnable, LevelAdjustable {
		/**
		 * The index of the image.
		 */
		private int image_index;

		/**
		 * Constructs a <code>LevelAdjustmentListener</code> for the
		 * image that the specified index represents.
		 * @param index the index of the image.
		 */
		public LevelAdjustmentListener ( int index ) {
			image_index = index;
		}

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
			MonoImage mono_image = null;
			LevelAdjustmentSet stat = null;

			switch (image_index) {
				case 0:
					mono_image = ((RGBCompositeImage)image_content).getRImage();
					stat = ((RGBCompositeImage)image_content).getRImageLevelAdjustmentSet();
					break;
				case 1:
					mono_image = ((RGBCompositeImage)image_content).getGImage();
					stat = ((RGBCompositeImage)image_content).getGImageLevelAdjustmentSet();
					break;
				case 2:
					mono_image = ((RGBCompositeImage)image_content).getBImage();
					stat = ((RGBCompositeImage)image_content).getBImageLevelAdjustmentSet();
					break;
			}

			LevelAdjustmentDialog dialog = new LevelAdjustmentDialog(mono_image, this, stat);
			dialog.addAllImagesAdjustmentCheckbox();
			dialog.show(pane);
		}

		/**
		 * Adjusts the level of image.
		 * @param minimum the minimum level.
		 * @param maximum the maximum level.
		 */
		public void adjustLevel ( double minimum, double maximum ) {
			LevelAdjustmentSet stat = null;

			switch (image_index) {
				case 0:
					stat = ((RGBCompositeImage)image_content).getRImageLevelAdjustmentSet();
					stat.current_minimum = minimum;
					stat.current_maximum = maximum;
					break;
				case 1:
					stat = ((RGBCompositeImage)image_content).getGImageLevelAdjustmentSet();
					stat.current_minimum = minimum;
					stat.current_maximum = maximum;
					if (mode == MODE_R_GB) {
						stat = ((RGBCompositeImage)image_content).getBImageLevelAdjustmentSet();
						stat.current_minimum = minimum;
						stat.current_maximum = maximum;
					}
					break;
				case 2:
					stat = ((RGBCompositeImage)image_content).getBImageLevelAdjustmentSet();
					stat.current_minimum = minimum;
					stat.current_maximum = maximum;
					break;
			}

			if (image != null)
				image.flush();
			image = image_content.getImage();
			repaint();
		}

		/**
		 * Adjusts the level of all images.
		 * @param minimum the minimum level.
		 * @param maximum the maximum level.
		 */
		public void adjustLevelAll ( double minimum, double maximum ) {
			LevelAdjustmentSet stat = null;
			switch (image_index) {
				case 0:
					stat = ((RGBCompositeImage)image_content).getRImageLevelAdjustmentSet();
					break;
				case 1:
					stat = ((RGBCompositeImage)image_content).getGImageLevelAdjustmentSet();
					break;
				case 2:
					stat = ((RGBCompositeImage)image_content).getBImageLevelAdjustmentSet();
					break;
			}

			double old_minimum = stat.current_minimum;
			double old_maximum = stat.current_maximum;

			double new_minimum = (minimum - old_minimum) / (old_maximum - old_minimum);
			double new_maximum = (maximum - old_minimum) / (old_maximum - old_minimum);

			for (int i = 0 ; i < 3 ; i++) {
				switch (i) {
					case 0:
						stat = ((RGBCompositeImage)image_content).getRImageLevelAdjustmentSet();
						break;
					case 1:
						stat = ((RGBCompositeImage)image_content).getGImageLevelAdjustmentSet();
						break;
					case 2:
						stat = ((RGBCompositeImage)image_content).getBImageLevelAdjustmentSet();
						break;
				}

				old_minimum = stat.current_minimum;
				old_maximum = stat.current_maximum;

				stat.current_minimum = new_minimum * (old_maximum - old_minimum) + old_minimum;
				stat.current_maximum = new_maximum * (old_maximum - old_minimum) + old_minimum;
			}

			if (image != null)
				image.flush();
			image = image_content.getImage();
			repaint();
		}
	}
}
