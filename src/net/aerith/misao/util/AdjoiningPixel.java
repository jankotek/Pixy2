/*
 * @(#)AdjoiningPixel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.Vector;
import net.aerith.misao.util.star.*;

/**
 * The <code>AdjoiningPixel</code> represents a set of pixels on an
 * image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 November 15
 */

public class AdjoiningPixel {
	/**
	 * The list of pixels.
	 */
	protected Vector list;

	/**
	 * The minimum pixels to use in astrometry.
	 */
	protected int pixels_for_astrometry = 0;

	/**
	 * The <code>AdjoiningPixel</code> being created in method 
	 * <code>deblend</code>.
	 */
	private AdjoiningPixel current_ap;

	/**
	 * The current threshold used in method <code>extract</code>.
	 */
	private double current_threshold;

	/**
	 * Constructs an empty <code>AdjoiningPixel</code>.
	 */
	public AdjoiningPixel ( ) {
		list = new Vector();
	}

	/**
	 * Adds a pixel.
	 * @param pixel the pixel to add.
	 */
	public void addPixel ( Pixel pixel ) {
		list.addElement(pixel);
	}

	/**
	 * Gets the number of pixels.
	 * @return the number of pixels.
	 */
	public int getPixelCount ( ) {
		return list.size();
	}

	/**
	 * Gets the array of pixels.
	 * @return the array of pixels.
	 */
	public Pixel[] getPixels ( ) {
		Pixel[] pixels = new Pixel[list.size()];
		for (int i = 0 ; i < list.size() ; i++)
			pixels[i] = (Pixel)list.elementAt(i);
		return pixels;
	}

	/**
	 * Gets the minimum x location.
	 * @return the minimum x location.
	 */
	public int getXMin ( ) {
		int x_min = 0;
		for (int i = 0 ; i < list.size() ; i++) {
			Pixel pixel = (Pixel)list.elementAt(i);
			if (i == 0   ||  x_min > pixel.getX())
				x_min = pixel.getX();
		}
		return x_min;
	}

	/**
	 * Gets the maximum x location.
	 * @return the maximum x location.
	 */
	public int getXMax ( ) {
		int x_max = 0;
		for (int i = 0 ; i < list.size() ; i++) {
			Pixel pixel = (Pixel)list.elementAt(i);
			if (i == 0   ||  x_max < pixel.getX())
				x_max = pixel.getX();
		}
		return x_max;
	}

	/**
	 * Gets the minimum y location.
	 * @return the minimum y location.
	 */
	public int getYMin ( ) {
		int y_min = 0;
		for (int i = 0 ; i < list.size() ; i++) {
			Pixel pixel = (Pixel)list.elementAt(i);
			if (i == 0   ||  y_min > pixel.getY())
				y_min = pixel.getY();
		}
		return y_min;
	}

	/**
	 * Gets the maximum y location.
	 * @return the maximum y location.
	 */
	public int getYMax ( ) {
		int y_max = 0;
		for (int i = 0 ; i < list.size() ; i++) {
			Pixel pixel = (Pixel)list.elementAt(i);
			if (i == 0   ||  y_max < pixel.getY())
				y_max = pixel.getY();
		}
		return y_max;
	}

	/**
	 * Gets the pixel value of the peak.
	 * @return the pixel value of the peak.
	 */
	public double getPeakValue ( ) {
		return getPeak().getValue();
	}

	/**
	 * Sets the minimum pixels to use in astrometry.
	 * @param pixel_count the minimum pixels to use in astrometry.
	 */
	public void setMinimumPixelCountForAstrometry ( int pixel_count ) {
		pixels_for_astrometry = pixel_count;
	}

	/**
	 * Creates a <code>StarImage</code>.
	 * @return the new <code>StarImage</code>.
	 */
	public StarImage createStar ( ) {
		StarImage star = new StarImage();

		// area size
		star.setArea(list.size());

		// peak value
		Pixel peak_pixel = getPeak();
		star.setPeak(peak_pixel.getValue());

		// amount of pixel values
		double amount = 0.0;
		for (int i = 0 ; i < list.size() ; i++)
			amount += ((Pixel)list.elementAt(i)).getValue();
		star.setValue(amount);

		// astrometry
		// Only several top value pixels are used.
		Array array = new Array(list.size());
		for (int i = 0 ; i < list.size() ; i++)
			array.set(i, ((Pixel)list.elementAt(i)).getValue());
		ArrayIndex index = array.sortDescendant();

		double x_amount = 0.0;
		double y_amount = 0.0;
		double weight = 0.0;

		int max_count = pixels_for_astrometry;
		if (max_count <= 0  ||  max_count > list.size())
			max_count = list.size();
		for (int i = 0 ; i < max_count ; i++) {
			Pixel pixel = (Pixel)list.elementAt(index.get(i));
			x_amount += ((double)pixel.getX() + 0.5) * pixel.getValue();
			y_amount += ((double)pixel.getY() + 0.5) * pixel.getValue();
			weight += pixel.getValue();
		}
		for (int i = max_count ; i < list.size() ; i++) {
			if (array.getValueAt(i) > peak_pixel.getValue() * 0.8) {
				Pixel pixel = (Pixel)list.elementAt(index.get(i));
				x_amount += ((double)pixel.getX() + 0.5) * pixel.getValue();
				y_amount += ((double)pixel.getY() + 0.5) * pixel.getValue();
				weight += pixel.getValue();
			} else
				break;
		}

		star.setPosition(new Position(x_amount / weight, y_amount / weight));

		return star;
	}

	/**
	 * Deblends adjoining pixels into some sets of adjoining pixels
	 * based on the specified base step. After this process, this
	 * <code>AdjoiningPixel</code> is destroyed.
	 * @param base_step the step to deblend.
	 */
	public Vector deblend ( double base_step ) {
		return deblend(base_step, new MeanStarImageRadius());
	}

	/**
	 * Deblends adjoining pixels into some sets of adjoining pixels
	 * based on the specified base step. After this process, this
	 * <code>AdjoiningPixel</code> is destroyed.
	 * @param base_step    the step to deblend.
	 * @param mean_radius the mean radius of a star image.
	 */
	public Vector deblend ( double base_step, MeanStarImageRadius mean_radius ) {
		Vector ap_list = new Vector();

		// If the image has a huge bright area, deblending takes
		// too much time. So in that case, the system does not deblend it.
		if (list.size() >= 10000) {
			ap_list.addElement(this);
			return ap_list;
		}

//		double radius = mean_radius.getRadius(getPeakValue());
		double radius = 1.0;

		while (list.size() > 0) {
			Pixel peak_pixel = getPeak();

			// Extracts only pixels surrounding around the peak and
			// creates a new set of adjoining pixels.
			current_threshold = peak_pixel.getValue() - base_step * Math.sqrt(peak_pixel.getValue() / base_step + 1.0);
			current_ap = new AdjoiningPixel();
			extract(peak_pixel.getX(), peak_pixel.getY());
			Position current_center = current_ap.getGravityCenter();

			// Determines if the current set of adjoining pixels is
			// a new peak or foot of already detected peak.
			AdjoiningPixel blending_ap = null;
			int blending_count = 0;
			for (int i = 0 ; i < ap_list.size() ; i++) {
				AdjoiningPixel ap = (AdjoiningPixel)ap_list.elementAt(i);
				if (current_ap.isAdjoining(ap)) {
					blending_ap = ap;
					blending_count++;
				} else {
					// When the distance between the two peaks is smaller than
					// the mean radius, they must not be separated.
					Position center = ap.getGravityCenter();
					double dist = Math.sqrt((current_center.getX() - center.getX()) * (current_center.getX() - center.getX()) + (current_center.getY() - center.getY()) * (current_center.getY() - center.getY()));
					if (dist < radius) {
						blending_ap = ap;
						blending_count++;
					}
				}
			}

			if (blending_count == 0) {
				// New peak.
				ap_list.addElement(current_ap);
			} else if (blending_count == 1) {
				// Foot of an already detected peak.
				for (int i = 0 ; i < current_ap.list.size() ; i++) {
					Pixel pixel = (Pixel)current_ap.list.elementAt(i);
					blending_ap.addPixel(pixel);
				}
			} else {
				// Foot of already detected several peaks.
				// In this case, now the pixels are ignored.
			}
		}

		return ap_list;
	}

	/**
	 * Gets the pixel at the peak.
	 * @return the pixel at the peak.
	 */
	public Pixel getPeak ( ) {
		Pixel pixel = null;
		double max = 0.0;

		for (int i = 0 ; i < list.size() ; i++) {
			Pixel p = (Pixel)list.elementAt(i);
			if (i == 0  ||  max < p.getValue()) {
				pixel = p;
				max = p.getValue();
			}
		}

		return pixel;
	}

	/**
	 * Gets the gravity center.
	 * @return the gravity center.
	 */
	public Position getGravityCenter ( ) {
		double x = 0.0;
		double y = 0.0;
		double w = 0.0;

		for (int i = 0 ; i < list.size() ; i++) {
			Pixel p = (Pixel)list.elementAt(i);
			x += p.getValue() * (double)p.getX();
			y += p.getValue() * (double)p.getY();
			w += p.getValue();
		}

		return new Position(x / w, y / w);
	}

	/**
	 * Checks if the specified set of adjoining pixels is adjoining
	 * to this <code>AdjoiningPixel</code>.
	 * @param ap the target <code>AdjoiningPixel</code> to check.
	 * @return true if the specified set of adjoining pixels is 
	 * adjoining to this.
	 */
	private boolean isAdjoining ( AdjoiningPixel ap ) {
		for (int i = 0 ; i < list.size() ; i++) {
			Pixel p1 = (Pixel)list.elementAt(i);
			for (int j = 0 ; j < ap.list.size() ; j++) {
				Pixel p2 = (Pixel)ap.list.elementAt(j);
				if (Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY()) <= 1)
/*
				if (Math.abs(p1.getX() - p2.getX()) <= 1  &&  Math.abs(p1.getY() - p2.getY()) <= 1)
*/
					return true;
			}
		}
		return false;
	}

	/**
	 * Extracts and removes pixels around the specified position whose
	 * value is greater than the specified threshold. This method is 
	 * invoked recurrsively.
	 * @param x the x position.
	 * @param y the y position.
	 */
	private void extract ( int x, int y ) {
		for (int i = 0 ; i < list.size() ; i++) {
			Pixel pixel = (Pixel)list.elementAt(i);

			if (pixel.getX() == x  &&  pixel.getY() == y  &&  pixel.getValue() > current_threshold) {
				current_ap.addPixel(pixel);
				list.removeElementAt(i);

				extract(x, y-1);
				extract(x-1, y);
				extract(x+1, y);
				extract(x, y+1);

				return;
			}
		}
	}
}
