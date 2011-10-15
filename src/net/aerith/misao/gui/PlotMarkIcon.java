/*
 * @(#)PlotMarkIcon.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;

/**
 * The <code>PlotMarkIcon</code> represents an icon to show the mark
 * to plot a star of the specified property.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 November 7
 */

public class PlotMarkIcon implements Icon {
	/**
	 * The property.
	 */
	protected PlotProperty property;

	/**
	 * The width.
	 */
	protected int width = 0;

	/**
	 * The height.
	 */
	protected int height = 0;

	/**
	 * Constructs a <code>PlotMarkIcon</code>.
	 * @param property the property.
	 */
	public PlotMarkIcon ( PlotProperty property ) {
		double mag = 0.0;
		if (property.isSizeFixed() == false)
			mag = property.getLimitingMag() - 5.0;
		int radius = property.getRadius(mag);

		width = radius * 2 + 6;
		height = radius * 2 + 6;
		this.property = property;
	}

	/**
	 * Gets the width.
	 * @return the width.
	 */
	public int getIconWidth ( ) {
		return width;
	}

	/**
	 * Gets the height.
	 * @return the height.
	 */
	public int getIconHeight ( ) {
		return height;
	}

	/**
	 * Draws the icon.
	 * @param c the component.
	 * @param g the <code>Graphics</code> to draw.
	 * @param x the x location.
	 * @param y the y location.
	 */
	public void paintIcon ( Component c, Graphics g, int x, int y ) {
		if (property.isSizeFixed() == false) {
			Star star = new Star();
			star.setMag(property.getLimitingMag() - 5.0);
			property.plot(g, x + width / 2, y + height / 2, star);
		} else {
			property.plot(g, x + width / 2, y + height / 2, null);
		}
	}
}
