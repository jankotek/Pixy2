/*
 * @(#)PlotProperty.java
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
 * The <code>PlotProperty</code> represents a set of GUI properties to
 * plot a star and the method to plot.
 * <p>
 * In the case the size is determined depending on the magnitude, the 
 * radius to plot a star circle is calculated while three parameters 
 * <tt>plot_ratio</tt>, <tt>limiting_mag</tt> and <tt>plot_radius</tt>
 * are considered. The radius is calculated as:
 * <p><pre>
 *     radius = plot_ratio * ( limiting_mag - magnitude ) + 1;
 *     if (radius < plot_radius)
 *         radius = plot_radius;
 * </pre></p>
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 28
 */

public class PlotProperty {
	/**
	 * True if the stars can be plotted.
	 */
	protected boolean enabled = true;

	/**
	 * The color. In the case of null, a star is plotted in the color 
	 * that the star object represents.
	 */
	protected Color color = null;

	/**
	 * True if the mark is filled.
	 */
	protected boolean fill_flag = true;

	/**
	 * The line width.
	 */
	protected int line_width = 1;

	/**
	 * The radius. In the case the size is determined depending on the 
	 * magnitude, the radius means the lower limit.
	 */
	protected int plot_radius = 2;

	/**
	 * True if the size is determined depending on the magnitude.
	 */
	protected boolean radius_mag_dependent = true;

	/**
	 * The parameter to calculate a radius of circle to plot.
	 */
	protected double plot_ratio = 1.0;

	/**
	 * The parameter to calculate a radius of circle to plot.
	 */
	protected double limiting_mag = 0.0;

	/**
	 * The magnitude system.
	 */
	protected MagnitudeSystem system;

	/**
	 * The mark.
	 */
	public int mark = PLOT_CIRCLE;

	/**
	 * The number of mark which represents to plot a circle.
	 */
	public final static int PLOT_CIRCLE = 0;

	/**
	 * The number of mark which represents to plot a triangle.
	 */
	public final static int PLOT_TRIANGLE = 1;

	/**
	 * The number of mark which represents to plot a rectangle.
	 */
	public final static int PLOT_RECTANGLE = 2;

	/**
	 * Constructs a <code>PlotProperty</code>.
	 */
	public PlotProperty ( ) {
		system = new MagnitudeSystem();
	}

	/**
	 * Checks if the star can be plotted.
	 * @return true if the star can be plotted.
	 */
	public boolean isEnabled ( ) {
		return enabled;
	}

	/**
	 * Enables to plot the stars.
	 */
	public void enable ( ) {
		enabled = true;
	}

	/**
	 * Disables to plot the stars.
	 */
	public void disable ( ) {
		enabled = false;
	}

	/**
	 * Gets the color.
	 * @return the color.
	 */
	public Color getColor ( ) {
		return color;
	}

	/**
	 * Sets the color.
	 * @param color the color.
	 */
	public void setColor ( Color color ) {
		this.color = color;
	}

	/**
	 * Sets to use the color that the star object represents.
	 */
	public void useStarObjectColor ( ) {
		color = null;
	}

	/**
	 * Returns true if the mark is filled.
	 * @return true if the mark is filled.
	 */
	public boolean isFilled ( ) {
		return fill_flag;
	}

	/**
	 * Sets a flag to fill the mark or not.
	 * @param flag true if the mark is filled.
	 */
	public void setFilled ( boolean flag ) {
		fill_flag = flag;
	}

	/**
	 * Gets the line width.
	 * @return the line width.
	 */
	public int getLineWidth ( ) {
		return line_width;
	}

	/**
	 * Sets the line width.
	 * @param width the line width.
	 */
	public void setLineWidth ( int width ) {
		line_width = width;
	}

	/**
	 * Gets the fixed radius.
	 * @return the fixed radius.
	 */
	public int getFixedRadius ( ) {
		return plot_radius;
	}

	/**
	 * Gets the magnitude system definition.
	 * @return the magnitude system.
	 */
	public MagnitudeSystem getMagnitudeSystem ( ) {
		return system;
	}

	/**
	 * Sets the magnitude system definition.
	 * @param system the magnitude system.
	 */
	public void setMagnitudeSystem ( MagnitudeSystem system ) {
		this.system = system;
	}

	/**
	 * Gets the converted magnitude of the specified star.
	 * @param star the star.
	 * @return the magnitude.
	 */
	public double getMagnitude ( Star star ) {
		try {
			if (star instanceof CatalogStar)
				return ((CatalogStar)star).getMagnitude(getMagnitudeSystem());
		} catch ( UnsupportedMagnitudeSystemException exception ) {
			System.err.println(exception);
		}

		return star.getMag();
	}

	/**
	 * Gets the radius depending on the specified magnitude.
	 * @param mag the magnitude.
	 * @return the radius.
	 */
	public int getRadius ( double mag ) {
		if (radius_mag_dependent) {
			int radius = (int)(plot_ratio * ( limiting_mag - mag )) + 1;
			if (radius < plot_radius)
				radius = plot_radius;
			return radius;
		}

		return plot_radius;
	}

	/**
	 * Returns true if the size is not determined depending on the 
	 * magnitude
	 * @return true if the size is not determined depending on the 
	 * magnitude
	 */
	public boolean isSizeFixed ( ) {
		return (radius_mag_dependent == false);
	}

	/**
	 * Sets the fixed radius.
	 * @param radius the radius.
	 */
	public void setFixedRadius ( int radius ) {
		radius_mag_dependent = false;
		plot_radius = radius;
	}

	/**
	 * Sets the parameters to calculate a radius of circle to plot.
	 * @param new_ratio  the new parameter for <tt>plot_ratio</tt>.
	 * @param new_mag    the new parameter for <tt>limiting_mag</tt>.
	 * @param new_radius the new parameter for <tt>plot_radius</tt>.
	 */
	public void setDependentSizeParameters ( double new_ratio, double new_mag, int new_radius ) {
		radius_mag_dependent = true;
		plot_ratio = new_ratio;
		limiting_mag = new_mag;
		plot_radius = new_radius;
	}

	/**
	 * Gets the limiting magnitude.
	 * @return the limiting magnitude.
	 */
	public double getLimitingMag ( ) {
		return limiting_mag;
	}

	/**
	 * Sets the limiting magnitude.
	 * @param limiting_mag the limiting magnitude.
	 */
	public void setLimitingMag ( double limiting_mag ) {
		this.limiting_mag = limiting_mag;
	}

	/**
	 * Gets the mark.
	 * @return the number of mark.
	 */
	public int getMark ( ) {
		return mark;
	}

	/**
	 * Sets the mark.
	 * @param number the number of mark.
	 */
	public void setMark ( int mark ) {
		this.mark = mark;
	}

	/**
	 * Plots a star at the specified position.
	 * @param g    the <code>Graphics</code> to draw.
	 * @param x    the x positon.
	 * @param y    the y positon.
	 * @param star the star position object to plot.
	 */
	public void plot ( Graphics g, int x, int y, StarPosition star ) {
		Color c = color;
		if (c == null  &&  star != null) {
			if (star instanceof Star)
				c = ((Star)star).getColor();
		}
		if (c == null)
			c = Color.black;
		g.setColor(c);

		int radius = 0;
		if (star instanceof Star) {
			radius = getRadius(getMagnitude((Star)star));
		} else {
			radius = getFixedRadius();
		}

		if (mark == PLOT_CIRCLE) {
			if (fill_flag) {
				if (radius < 1) {
					g.fillRect(x, y, 1, 1);
				} else {
					x -= radius;
					y -= radius;
					g.fillArc(x, y, radius*2, radius*2, 0, 360);
				}
			} else {
				for (int r = 0 ; r < line_width ; r++) {
					g.drawArc(x - radius + r, y - radius + r, 2*(radius-r), 2*(radius-r), 0, 360);
				}
			}
		}

		if (mark == PLOT_TRIANGLE) {
			if (fill_flag) {
				if (radius < 1) {
					g.fillRect(x, y, 1, 1);
				} else {
					int fr3 = (int)((double)radius * Math.sqrt(3.0) / 2.0);
					Polygon polygon = new Polygon();
					polygon.addPoint(x, y - radius);
					polygon.addPoint(x - fr3, y + radius / 2);
					polygon.addPoint(x + fr3, y + radius / 2);
					g.fillPolygon(polygon);
				}
			} else {
				for (int r = 0 ; r < line_width ; r++) {
					int fr3 = (int)((double)(radius - r) * Math.sqrt(3.0) / 2.0);
					Polygon polygon = new Polygon();
					polygon.addPoint(x, y - (radius - r));
					polygon.addPoint(x - fr3, y + (radius - r) / 2);
					polygon.addPoint(x + fr3, y + (radius - r) / 2);
					g.drawPolygon(polygon);
				}
			}
		}

		if (mark == PLOT_RECTANGLE) {
			if (fill_flag) {
				if (radius < 1) {
					g.fillRect(x, y, 1, 1);
				} else {
					g.fillRect(x - radius, y - radius, 2*radius+1, 2*radius+1);
				}
			} else {
				for (int r = 0 ; r < line_width ; r++) {
					g.drawRect(x - (radius - r), y - (radius - r), 2*(radius-r)+1, 2*(radius-r)+1);
				}
			}
		}
	}
}
