/*
 * @(#)BlinkMonoImageComponent.java
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
 * The <code>BlinkMonoImageComponent</code> represents a GUI component
 * to show blinking images. It has a level adjustment function of 
 * pixel values of each image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class BlinkMonoImageComponent extends ImageComponent {
	/**
	 * The list of images.
	 */
	protected MonoImage[] mono_images = null;

	/**
	 * The list of <code>java.awt.Image</code>s. It is null at the 
	 * beginning, or just after updated. It is creaeted and set when
	 * the image is actually painted.
	 */
	protected Image[] images = null;

	/**
	 * The list of image level and statistics.
	 */
	protected LevelAdjustmentSet[] stats = null;

	/**
	 * The thread which controls the blink of images
	 */
	protected BlinkThread thread = null;

	/**
	 * The thread interval in milliseconds.
	 */
	protected int thread_interval = 1000;

	/**
	 * The menu item to start the blink.
	 */
	protected JMenuItem menu_start;

	/**
	 * The menu item to stop the blink.
	 */
	protected JMenuItem menu_stop;

	/**
	 * True if the blink thead is running.
	 */
	protected boolean running = false;

	/**
	 * Constructs a <code>BlinkMonoImageComponent</code> with a list
	 * of images.
	 * @param images the list of images.
	 */
	public BlinkMonoImageComponent ( MonoImage[] images ) {
		super(images[0]);

		mono_images = images;

		stats = new LevelAdjustmentSet[mono_images.length];
		for (int i = 0 ; i < mono_images.length ; i++)
			stats[i] = new DefaultLevelAdjustmentSet(mono_images[i]);

		this.images = new Image[mono_images.length];
		for (int i = 0 ; i < mono_images.length ; i++)
			this.images[i] = null;

		initPopupMenu();

		stop();
	}

	/**
	 * Initializes a popup menu. A <tt>popup</tt> must be created 
	 * previously.
	 */
	protected void initPopupMenu ( ) {
		super.initPopupMenu();

		popup.addSeparator();
		JMenuItem[] items = createBlinkMenus();
		for (int i = 0 ; i < items.length ; i++)
			popup.add(items[i]);

		if (mono_images != null) {
			popup.addSeparator();
			items = createLevelAdjustmentMenus();
			for (int i = 0 ; i < items.length ; i++)
				popup.add(items[i]);
		}
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * blink menus. Items are newly created when invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createBlinkMenus ( ) {
		JMenuItem[] items = new JMenuItem[3];

		menu_start = new JMenuItem("Start");
		items[0] = menu_start;
		items[0].addActionListener(new StartListener());

		menu_stop = new JMenuItem("Stop");
		items[1] = menu_stop;
		items[1].addActionListener(new StopListener());

		items[2] = new JMenuItem("Animation Speed");
		items[2].addActionListener(new AnimationListener());

		return items;
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * level adjustment menus of pixel values. Items are newly created
	 * when invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createLevelAdjustmentMenus ( ) {
		JMenuItem[] items = new JMenuItem[mono_images.length];

		for (int i = 0 ; i < mono_images.length ; i++) {
			items[i] = new JMenuItem("Level Adjustment of Image " + (i+1));
			items[i].addActionListener(new LevelAdjustmentListener(i));
		}

		return items;
	}

	/**
	 * Starts blinking.
	 */
	public void start ( ) {
		running = true;

		if (thread == null) {
			thread = new BlinkThread();
			thread.start();
		}

		menu_start.setEnabled(false);
		menu_stop.setEnabled(true);
	}

	/**
	 * Stops blinking.
	 */
	public void stop ( ) {
		running = false;

		menu_start.setEnabled(true);
		menu_stop.setEnabled(false);
	}

	/**
	 * Finalizes this object.
	 */
	protected void finalize ( )
		throws Throwable
	{
		for (int i = 0 ; i < images.length ; i++) {
			if (images[i] != null)
				images[i].flush();
		}
	}

	/**
	 * The <code>BlinkThread</code> is a thread to control the blink
	 * of images. When the image to paint is null, it creates and sets
	 * a new <code>java.awt.Image</code>.
	 */
	protected class BlinkThread extends Thread {
		/**
		 * Runs this thread.
		 */
		public void run ( ) {
			int current_index = 0;

			while (isShowing()  &&  isVisible()) {
				if (running) {
					if (images[current_index] == null)
						images[current_index] = mono_images[current_index].getImage(stats[current_index].current_minimum, stats[current_index].current_maximum);

					image = images[current_index];
					repaint();

					current_index++;
					if (current_index >= mono_images.length)
						current_index = 0;
				}

				try {
					sleep(thread_interval);
				} catch ( InterruptedException exception ) {
					return;
				}
			}
		}
	}

	/**
	 * The <code>StartListener</code> is a listener class of menu
	 * selection to start the blink thread.
	 */
	protected class StartListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			start();
		}
	}

	/**
	 * The <code>StopListener</code> is a listener class of menu
	 * selection to stop the blink thread.
	 */
	protected class StopListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			stop();
		}
	}

	/**
	 * The <code>AnimationListener</code> is a listener class of menu
	 * selection for animation parameters setting.
	 */
	protected class AnimationListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			AnimationDialog dialog = new AnimationDialog();
			dialog.setInterval(thread_interval);

			int answer = dialog.show(pane);
			if (answer == 0) {
				thread_interval = dialog.getInterval();
			}
		}
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
			MonoImage mono_image = mono_images[image_index];
			LevelAdjustmentSet stat = stats[image_index];

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
			stats[image_index].current_minimum = minimum;
			stats[image_index].current_maximum = maximum;

			// The image will be creaeted and set when it is actually painted.
			if (images[image_index] != null)
				images[image_index].flush();
			images[image_index] = null;
		}

		/**
		 * Adjusts the level of all images.
		 * @param minimum the minimum level.
		 * @param maximum the maximum level.
		 */
		public void adjustLevelAll ( double minimum, double maximum ) {
			double old_minimum = stats[image_index].current_minimum;
			double old_maximum = stats[image_index].current_maximum;

			double new_minimum = (minimum - old_minimum) / (old_maximum - old_minimum);
			double new_maximum = (maximum - old_minimum) / (old_maximum - old_minimum);

			for (int i = 0 ; i < stats.length ; i++) {
				old_minimum = stats[i].current_minimum;
				old_maximum = stats[i].current_maximum;

				stats[i].current_minimum = new_minimum * (old_maximum - old_minimum) + old_minimum;
				stats[i].current_maximum = new_maximum * (old_maximum - old_minimum) + old_minimum;

				// The image will be creaeted and set when it is actually painted.
				if (images[i] != null)
					images[i].flush();
				images[i] = null;
			}
		}
	}
}
