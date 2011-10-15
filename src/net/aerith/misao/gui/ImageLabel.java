/*
 * @(#)ImageLabel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;

/**
 * The <code>ImageLabel</code> represents a <code>JLabel</code> only 
 * to show an image icon.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 June 24
 */

public class ImageLabel extends JLabel {
	/**
	 * Constructs an <code>ImageIcon</code> with the specified image
	 * icon.
	 * @param icon the image icon to show.
	 */
	public ImageLabel ( ImageIcon icon ) {
		super(icon);
		setUI(new ImageLabelUI());
	}

	/**
	 * The <code>ImageLabelUI</code> is an user interface to draw the
	 * specified image icon on the label. Even if the area of the 
	 * label is not same as the size of the image icon, the <tt>paint</tt>
	 * method rescales and draws properly.
	 */
	protected class ImageLabelUI extends BasicLabelUI {
		/**
		 * Draws the splash image.
		 * @param g the graphics to draw on.
		 * @param c the label to draw on.
		 */
		public void paint ( Graphics g, JComponent c ) {
			JLabel label = (JLabel)c;
			ImageIcon icon = (ImageIcon)label.getIcon();
			g.drawImage(icon.getImage(), 0, 0, c.getSize().width, c.getSize().height, c);
		}
	}
}

