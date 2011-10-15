/*
 * @(#)IconLabel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * The <code>IconLabel</code> represents a label to show an icon.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 November 7
 */

public class IconLabel extends JLabel {
	/**
	 * The icon of the mark.
	 */
	protected Icon icon;

	/**
	 * Constructs a <code>IconLabel</code>.
	 * @param icon the icon of the mark.
	 */
	public IconLabel ( Icon icon ) {
		this.icon = icon;

		setText("");
		setIcon(icon);
	}

	/**
	 * Gets the preferred size, considering the zoom level.
	 * @return the preferred size.
	 */
    public Dimension getPreferredSize ( ) {
		return new Dimension(icon.getIconWidth(), icon.getIconHeight());
	}
}
