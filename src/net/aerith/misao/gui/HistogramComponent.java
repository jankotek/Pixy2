/*
 * @(#)HistogramComponent.java
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
 * The <code>HistogramComponent</code> represents a GUI component to
 * show a histogram of an image, with level adjustment pointers.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 October 20
 */

public class HistogramComponent extends JLabel {
	/**
	 * The off screen.
	 */
	private Image off_image = null;

	/**
	 * The target image.
	 */
	private Statisticable target;

	/**
	 * The histogram.
	 */
	private Histogram histogram = null;

	/**
	 * The minimum value of the histogram.
	 */
	private double minimum = 0;

	/**
	 * The maximum value of the histogram.
	 */
	private double maximum = 1;

	/**
	 * The minimum pointer location.
	 */
	private int min_pointer = 0;

	/**
	 * The maximum pointer location.
	 */
	private int max_pointer = 100;

	/**
	 * Constructs an empty <code>HistogramComponent</code>.
	 * @param target the target image component.
	 */
	public HistogramComponent ( Statisticable target ) {
		this.target = target;

		histogram = null;
	}

	/**
	 * Gets the preferred size.
	 * @return the preferred size.
	 */
    public Dimension getPreferredSize ( ) {
		return new Dimension(100, 100);
	}

	/**
	 * Updates the histogram. Creates a new histogram and updates the
	 * view.
	 * @param new_minimum the new minimum value.
	 * @param new_maximum the new maximum value.
	 */
	public void updateHistogram ( double new_minimum, double new_maximum ) {
		histogram = new Histogram(target, new_minimum, new_maximum, 100);
		minimum = new_minimum;
		maximum = new_maximum;
		min_pointer = 0;
		max_pointer = 100;

		repaint();
	}

	/**
	 * Updates the range pointer position.
	 * view.
	 * @param min_value the pointer for minimum value.
	 * @param max_value the pointer for maximum value.
	 */
	public void updatePointer ( double min_value, double max_value ) {
		min_pointer = (int)((min_value - minimum) / (maximum - minimum) * 100.0);
		max_pointer = (int)((max_value - minimum) / (maximum - minimum) * 100.0);
		if (min_pointer < 0)
			min_pointer = 0;
		if (min_pointer > 100)
			min_pointer = 100;
		if (max_pointer < 0)
			max_pointer = 0;
		if (max_pointer > 100)
			max_pointer = 100;

		repaint();
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
		Dimension dimension = getSize();

		if (off_image == null) {
			off_image = createImage(100, 100);
		}
		Graphics off_g = off_image.getGraphics();

		off_g.setColor(getBackground());
		off_g.fillRect(0, 0, 100, 100);

		if (histogram != null) {
			off_g.setColor(getForeground());

			int max_value = 1;
			for (int i = 0 ; i < 100 ; i++) {
				if (max_value < histogram.getAt(i+1))
					max_value = histogram.getAt(i+1);
			}

			double scale = 80.0 / (double)max_value;

			for (int i = 0 ; i < 100 ; i++) {
				int value = (int)((double)histogram.getAt(i+1) * scale);
				off_g.drawLine(i, 80 - value, i, 80);
			}

			off_g.drawLine(min_pointer, 85, min_pointer - 5, 97);
			off_g.drawLine(min_pointer, 85, min_pointer + 5, 97);
			off_g.drawLine(min_pointer - 5, 97, min_pointer + 5, 97);

			off_g.drawLine(max_pointer, 85, max_pointer - 5, 97);
			off_g.drawLine(max_pointer, 85, max_pointer + 5, 97);
			off_g.drawLine(max_pointer - 5, 97, max_pointer + 5, 97);
		}

		g.drawImage(off_image, 0, 0, dimension.width, dimension.height, this);
	}
}
