/*
 * @(#)AdjoiningPixelDetector.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy.star_detection;
import java.util.Vector;
import java.util.Stack;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>AdjoiningPixelDetector</code> is a class to deblend
 * pixels of the image into some sets of adjoining pixels whose value
 * is greater than the specified threshold.
 * <p>
 * Note that the value of the image is changed after the operation.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 November 15
 */

public class AdjoiningPixelDetector extends Operation {
	/**
	 * The image where to deblend into some sets of adjoining pixels.
	 */
	protected MonoImage image;

	/**
	 * The threshold.
	 */
	protected double threshold = 0.0;

	/**
	 * The minimum peak value to detect a set of adjoining pixels.
	 */
	protected double minimum_peak = 0.0;

	/**
	 * The list of adjoining pixels.
	 */
	protected Vector list = null;

	/**
	 * Constructs a <code>AdjoiningPixelDetector</code> with a image
	 * object where to deblend into some sets of adjoining pixels.
	 * @param image the image where to deblend.
	 */
	public AdjoiningPixelDetector ( MonoImage image ) {
		this.image = image;
	}

	/**
	 * Sets the threshold.
	 * @param new_threshold the threshold value.
	 */
	public void setThreshold ( double new_threshold ) {
		threshold = new_threshold;
	}

	/**
	 * Sets the minimum peak value to detect a set of adjoining pixels.
	 * @param new_value the minimum peak value.
	 */
	public void setMinimumPeakValue ( double new_value ) {
		minimum_peak = new_value;
	}

	/**
	 * Gets the list of adjoining pixels.
	 * @return the list of adjoining pixels.
	 */
	public Vector getAdjoiningPixelList ( ) {
		return list;
	}

	/**
	 * Returns true if the operation is ready to start.
	 * @return true if the operation is ready to start.
	 */
	public boolean ready ( ) {
		return true;
	}

	/**
	 * Operates.
	 * @exception Exception if an error occurs.
	 */
	public void operate ( )
		throws Exception
	{
		list = new Vector();

		for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				if (image.getValue(x, y) > threshold) {
					AdjoiningPixel ap = detectAdjoiningPixel(x, y);

					if (ap.getPeakValue() > minimum_peak)
						list.addElement(ap);
				}
			}
		}
	}

	/**
	 * Detects adjoining pixels around the specified position. The 
	 * pixel values in the <code>image</code> is changed by this 
	 * process. 
	 * @param x the x position.
	 * @param y the y position.
	 * @return the adjoining pixels around the specified position.
	 */
	private AdjoiningPixel detectAdjoiningPixel ( int x, int y ) {
		AdjoiningPixel ap = new AdjoiningPixel();

		Stack stack = new Stack();
		stack.push(new Pixel(x, y, image.getValue(x, y)));

		while (stack.empty() == false) {
			Pixel pixel = (Pixel)stack.pop();

			x = pixel.getX();
			y = pixel.getY();

			if (image.getValue(x, y) > threshold) {
				ap.addPixel(pixel);

				// The pixel value is cleared.
				image.setValue(x, y, threshold - 1);

				if (0 <= x  &&  x < image.getSize().getWidth()  &&
					0 <= y-1  &&  y-1 < image.getSize().getHeight())
					stack.push(new Pixel(x, y-1, image.getValue(x, y-1)));
				if (0 <= x-1  &&  x-1 < image.getSize().getWidth()  &&
					0 <= y  &&  y < image.getSize().getHeight())
					stack.push(new Pixel(x-1, y, image.getValue(x-1, y)));
				if (0 <= x+1  &&  x+1 < image.getSize().getWidth()  &&
					0 <= y  &&  y < image.getSize().getHeight())
					stack.push(new Pixel(x+1, y, image.getValue(x+1, y)));
				if (0 <= x  &&  x < image.getSize().getWidth()  &&
					0 <= y+1  &&  y+1 < image.getSize().getHeight())
					stack.push(new Pixel(x, y+1, image.getValue(x, y+1)));
			}
		}

		return ap;
	}
}
