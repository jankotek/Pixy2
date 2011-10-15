/*
 * @(#)ImageComponent.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>ImageComponent</code> represents a GUI component to show
 * an image with zoom function.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 March 22
 */

public class ImageComponent extends JLabel {
	/**
	 * The content image to view.
	 */
	protected ImageContent image_content;

	/**
	 * The <code>java.awt.Image</code> to view.
	 */
	protected Image image;

	/**
	 * The popup menu.
	 */
	protected JPopupMenu popup;

	/**
	 * The pane of this component.
	 */
	protected Container pane;

	/**
	 * The preferred size, considering the zoom level.
	 */
	protected Dimension dimension;

	/**
	 * The position on the image to mark up. If not to mark up, it 
	 * must be null.
	 */
	protected Position markup_position = null;

	/**
	 * The number of zoom level. It is 0 when the size is normal. The 
	 * number 1 means magnified twice, 2 means third and 3 means 
	 * fourth. The number -1 means reduced twice, -2 means third and
	 * -3 means fourth.
	 */
	protected int zoom_level = 0;

	/**
	 * Constructs an empty <code>ImageComponent</code>.
	 */
	public ImageComponent ( ) {
		super();

		image = null;

		pane = this;

		dimension = new Dimension(0, 0);

		initPopupMenu();
	}

	/**
	 * Constructs a <code>ImageComponent</code> with an image.
	 * @param initial_content the image content to view.
	 */
	public ImageComponent ( ImageContent initial_content ) {
		super();

		image_content = initial_content;
		image = initial_content.getImage();

		pane = this;

		dimension = new Dimension(initial_content.getSize().getWidth(), initial_content.getSize().getHeight());

		initPopupMenu();
	}

	/**
	 * Gets the preferred size, considering the zoom level.
	 * @return the preferred size.
	 */
    public Dimension getPreferredSize ( ) {
		return dimension;
	}

	/**
	 * Zooms in.
	 */
	public void zoomIn ( ) {
		zoom_level++;
		Point size = convertToScreenPoint(new Position(image_content.getSize().getWidth() - 1, image_content.getSize().getHeight() - 1));
		dimension = new Dimension(size.x + 1, size.y + 1);
		repaint();
		getParent().doLayout();
	}

	/**
	 * Zooms out.
	 */
	public void zoomOut ( ) {
		zoom_level--;
		Point size = convertToScreenPoint(new Position(image_content.getSize().getWidth() - 1, image_content.getSize().getHeight() - 1));
		dimension = new Dimension(size.x + 1, size.y + 1);
		repaint();
		getParent().doLayout();
	}

	/**
	 * Marks up at the specified position.
	 * @param position the position on the image to mark up.
	 */
	public void markUp ( Position position ) {
		markup_position = position;
		repaint();
	}

	/**
	 * Marks up at the center.
	 */
	public void markUpCenter ( ) {
		Position position = new Position((double)image_content.getSize().getWidth() / 2.0, (double)image_content.getSize().getHeight() / 2.0);
		markUp(position);
	}

	/**
	 * Clear the mark.
	 */
	public void clearMark ( ) {
		markup_position = null;
		repaint();
	}

	/**
	 * Converts a point on the screen to the position on the original 
	 * image.
	 * @param screen_point the point on the screen.
	 * @return the position on the original image.
	 */
	public Position convertToImagePosition ( Point screen_point ) {
		Position image_position = new Position(screen_point.x, screen_point.y);
		if (zoom_level > 0) {
			image_position.rescale(1.0 / (double)(1 + zoom_level));
		} else if (zoom_level < 0) {
			image_position.rescale((double)(1 - zoom_level));
		}
		return image_position;
	}

	/**
	 * Converts a position on the original image to the point on the
	 * screen.
	 * @param image_position the position on the original image.
	 * @return the point on the screen.
	 */
	public Point convertToScreenPoint ( Position image_position ) {
		Point screen_point = new Point((int)image_position.getX(), (int)image_position.getY());
		if (zoom_level > 0) {
			screen_point.x = (int)(image_position.getX() * (double)(1 + zoom_level));
			screen_point.y = (int)(image_position.getY() * (double)(1 + zoom_level));
		} else if (zoom_level < 0) {
			screen_point.x = (int)(image_position.getX() / (double)(1 - zoom_level));
			screen_point.y = (int)(image_position.getY() / (double)(1 - zoom_level));
		}
		return screen_point;
	}

	/**
	 * Initializes a popup menu. A <tt>popup</tt> must be created 
	 * previously.
	 */
	protected void initPopupMenu ( ) {
		popup = new JPopupMenu();

		JMenuItem[] items = createSaveMenus();
		if (items != null) {
			for (int i = 0 ; i < items.length ; i++)
				popup.add(items[i]);

			if (items.length > 0)
				popup.addSeparator();
		}

		items = createZoomMenus();
		for (int i = 0 ; i < items.length ; i++)
			popup.add(items[i]);

		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * save menus. Items are newly created when invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createSaveMenus ( ) {
		return null;
	}

	/**
	 * Returns an array of <code>JMenuItem</code> which consists of
	 * zoom menus. Items are newly created when invoked.
	 * @return an array of menu items.
	 */
	public JMenuItem[] createZoomMenus ( ) {
		JMenuItem[] items = new JMenuItem[2];
		items[0] = new JMenuItem("Zoom In");
		items[0].addActionListener(new ZoomInListener());
		items[1] = new JMenuItem("Zoom Out");
		items[1].addActionListener(new ZoomOutListener());
		return items;
	}

	/**
	 * Gets the image content.
	 * @return the image content.
	 */
	public ImageContent getImageContent ( ) {
		return image_content;
	}

	/**
	 * Replaces the image content.
	 * @param new_image the new image.
	 */
	public void replaceImage ( ImageContent new_image ) {
		if (image != null)
			image.flush();

		image_content = new_image;
		image = image_content.getImage();

		dimension = new Dimension(image_content.getSize().getWidth(), image_content.getSize().getHeight());

		repaint();
	}

	/**
	 * Finalizes this object.
	 */
	protected void finalize ( )
		throws Throwable
	{
		if (image != null)
			image.flush();
	}

	/**
	 * Updates the view.
	 * @param g the <code>Graphics</code> to update.
	 */
	public void paint ( Graphics g ) {
		update(g);
	}

	/**
	 * Updates the view.
	 * @param g the <code>Graphics</code> to update.
	 */
	public void update ( Graphics g ) {
		if (image != null) {
			g.drawImage(image, 0, 0, (int)dimension.getWidth(), (int)dimension.getHeight(), this);

			if (markup_position != null) {
				g.setColor(Color.green);
				Point pt = convertToScreenPoint(markup_position);
				g.drawOval(pt.x - 15, pt.y - 15, 31, 31);
				g.drawOval(pt.x - 14, pt.y - 14, 29, 29);
				g.drawOval(pt.x - 13, pt.y - 13, 27, 27);
			}
		}
	}

	/**
	 * Invoked when mouse event occurs. It is to show a popup menu.
	 * @param e contains the click position.
	 */
	protected void processMouseEvent ( MouseEvent e ) {
		if (e.isPopupTrigger()) {
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
		super.processMouseEvent(e);
	}

	/**
	 * The <code>ZoomInListener</code> is a listener class of menu 
	 * selection for zoom in.
	 */
	protected class ZoomInListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			zoomIn();
		}
	}

	/**
	 * The <code>ZoomOutListener</code> is a listener class of menu 
	 * selection for zoom in.
	 */
	protected class ZoomOutListener implements ActionListener {
		/**
		 * Invoked when the menu is selected.
		 * @param e contains the selected menu item.
		 */
		public void actionPerformed ( ActionEvent e ) {
			zoomOut();
		}
	}
}
