/*
 * @(#)DefaultStarDetector.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy.star_detection;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.filter.*;

/**
 * The <code>DefaultStarDetector</code> is a class to detect stars from
 * an image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 25
 */

public class DefaultStarDetector extends Operation {
	/**
	 * The image where to detect stars.
	 */
	protected MonoImage image;

	/**
	 * The buffer of sky image.
	 */
	protected MonoImage sky_image;

	/**
	 * True if the sky image is kept in the buffer even after
	 * operation.
	 */
	protected boolean keep_sky_image = false;

	/**
	 * The threshold value.
	 */
	protected double threshold;

	/**
	 * The list of detected stars.
	 */
	protected StarImageList list = null;

	/**
	 * The threshold coefficient to detect stars.
	 */
	protected double threshold_coefficient = 2.0;

	/**
	 * True when to correct the positions of blooming stars.
	 */
	protected boolean correct_blooming_position = false;

	/**
	 * The mode.
	 */
	protected int mode = MODE_PIXEL_AMOUNT_OVER_THRESHOLD;

	/**
	 * The mode number which indicates to regard the amount of pixel
	 * values over the threshold as brightness of stars.
	 */
	public final static int MODE_PIXEL_AMOUNT_OVER_THRESHOLD = 0;

	/**
	 * The mode number which indicates to regard the peak value as 
	 * brightness of stars.
	 */
	public final static int MODE_PEAK = 1;

	/**
	 * The mode number which indicates to regard the amount of pixel
	 * values within the specified aperture.
	 */
	public final static int MODE_APERTURE = 2;

	/**
	 * The aperture size of a star. For example, 4 means 4x4 pixels 
	 * around the star position.
	 */
	protected int inner_aperture_size = 2;

	/**
	 * The aperture size of sky around a star. For example, 4 means 
	 * 4x4 pixels around the star position.
	 */
	protected int outer_aperture_size = 3;

	/**
	 * Constructs a <code>DefaultStarDetector</code> with a image
	 * object where to detect stars.
	 * @param image the image where to detect stars.
	 */
	public DefaultStarDetector ( MonoImage image ) {
		this.image = image;
	}

	/**
	 * Sets a flag to keep sky image in the buffer even after 
	 * operation.
	 */
	public void keepSkyImage ( ) {
		keep_sky_image = true;
	}

	/**
	 * Gets the sky image. In the case the flag is not set true, it 
	 * returns null.
	 * @return the sky image.
	 */
	public MonoImage getSkyImage ( ) {
		return sky_image;
	}

	/**
	 * Gets the list of detected stars.
	 * @return the list of detected stars.
	 */
	public StarImageList getStarList ( ) {
		return list;
	}

	/**
	 * Sets the threshold coefficient to detect stars.
	 * @param new_coef the new threshold coefficient.
	 */
	public void setThresholdCoefficient ( double new_coef ) {
		threshold_coefficient = new_coef;
	}

	/**
	 * Sets the flag to correct the positions of blooming stars.
	 */
	public void setCorrectBloomingPosition ( ) {
		correct_blooming_position = true;
	}

	/**
	 * Sets the mode.
	 * @param mode the mode number.
	 */
	public void setMode ( int mode ) {
		this.mode = mode;
	}

	/**
	 * Sets the aperture sizes. For example, 4 means 4x4 pixels around 
	 * the star position.
	 * @param inner_size the aperture size of a star.
	 * @param outer_size the aperture size of sky around a star.
	 */
	public void setApertureSize ( int inner_size, int outer_size ) {
		this.inner_aperture_size = inner_size;
		this.outer_aperture_size = outer_size;
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
		list = new StarImageList();

		monitor_set.addMessage("[Detecting stars]");
		monitor_set.addMessage(new Date().toString());

		if (image != null) {
			// Gets the blooming stars positions, which must be corrected.
			Position[] corrected_positions = null;
			if (correct_blooming_position) {
				MonoImage blooming_canceled_image = image.cloneImage();
				BloomingCancelFilter blooming_cancel_filter = new BloomingCancelFilter();
				blooming_cancel_filter.operate(blooming_canceled_image);
				corrected_positions = blooming_cancel_filter.getBloomingPositions();

				monitor_set.addMessage("Blooming stars:");
				for (int i = 0 ; i < corrected_positions.length ; i++)
					monitor_set.addMessage("  (" + corrected_positions[i].getX() + ", " + corrected_positions[i].getY() + ")");
			}

			// Creates sky field.
			sky_image = new BackgroundEstimationFilter().operate(image);

			Statistics stat = new Statistics(sky_image);
			stat.calculate();
			monitor_set.addMessage("Sky image: " + stat.getOutputString());

			// Estimates threshold to detect stars.
			// Standard deviation of all pixel values does not
			// represent the scatter of noises but is influenced by
			// stars. Because the pixel values of stars are greater
			// than sky image, the noise value is estimated as a
			// standard deviation of pixels whose value is smaller
			// than sky image.
			MonoImage stellar_image = null;
			if (keep_sky_image) {
				stellar_image = sky_image.cloneImage();
			} else {
				stellar_image = sky_image;
				sky_image = null;
			}
			stellar_image.subtract(image);
			stellar_image.inverse();

			// When to regard the amount of pixel values within the specified aperture.
			MonoImage original_stellar_image = null;
			if (mode == MODE_APERTURE)
				original_stellar_image = stellar_image.cloneImage();

			// Resizes the stellar image in order to reject pixels around edges
			// to estimate the threshold to detect stars.
			Size threshold_image_size = new Size(image.getSize().getWidth() * 9 / 10, image.getSize().getHeight() * 9 / 10);
			ResizeFilter resize_filter = new ResizeFilter(threshold_image_size);
			resize_filter.setBasePosition(image.getSize().getWidth() / 20, image.getSize().getHeight() / 20);
			MonoImage threshold_image = resize_filter.operate(stellar_image);
			stat = new Statistics(threshold_image);
			stat.setMaximumLimit(0.0);
			stat.calculate();
			threshold = stat.getDeviation() * threshold_coefficient * 1.5;
			double minimum_peak = stat.getDeviation() * threshold_coefficient * 2.0;
			threshold_image = null;
			monitor_set.addMessage("Negative value pixels: " + stat.getOutputString());
			monitor_set.addMessage("Threshold: " + threshold);

			// Detects stars as adjoining pixels.
			// Note: Pixel values in stellar_image is changed.
			AdjoiningPixelDetector ap_detector = new AdjoiningPixelDetector(stellar_image);
			ap_detector.setThreshold(threshold);
			ap_detector.setMinimumPeakValue(minimum_peak);
			ap_detector.operate();
			Vector ap_list = ap_detector.getAdjoiningPixelList();
			monitor_set.addMessage("Adjoining pixels: " + ap_list.size() + " sets");

			stellar_image = null;

			// Calculates the equation which represents the relation between the 
			// peak value and the radius of a star image based on some bright
			// star images.
			// Only part of the image can be out of focus. So here estimates the
			// mean radius of a star image for each part of the image.
			int horizontal_division = (image.getSize().getWidth() + 100) / 200;
			int vertical_division = (image.getSize().getHeight() + 100) / 200;
			int horizontal_interval = image.getSize().getWidth() / horizontal_division + 1;
			int vertical_interval = image.getSize().getHeight() / vertical_division + 1;
			MeanStarImageRadius[][] mean_radius = new MeanStarImageRadius[horizontal_division][vertical_division];
			Vector[][] partial_ap_list = new Vector[horizontal_division][vertical_division];
			for (int y = 0 ; y < vertical_division ; y++) {
				for (int x = 0 ; x < horizontal_division ; x++) {
					partial_ap_list[x][y] = new Vector();
				}
			}
			for (int i = 0 ; i < ap_list.size() ; i++) {
				AdjoiningPixel ap = (AdjoiningPixel)ap_list.elementAt(i);
				if (ap.getPeakValue() > threshold * 3.0) {
					int x = ap.getPeak().getX() / horizontal_interval;
					int y = ap.getPeak().getY() / vertical_interval;
					partial_ap_list[x][y].addElement(ap);
				}
			}
			for (int y = 0 ; y < vertical_division ; y++) {
				for (int x = 0 ; x < horizontal_division ; x++) {
					mean_radius[x][y] = new MeanStarImageRadius(partial_ap_list[x][y]);
					monitor_set.addMessage("Mean star image radius(" + x + "," + y + "): " + mean_radius[x][y].getOutputString());
				}
			}

			// Deblends adjoining pixels into stars.
			for (int i = 0 ; i < ap_list.size() ; i++) {
				AdjoiningPixel ap = (AdjoiningPixel)ap_list.elementAt(i);
				int x = ap.getPeak().getX() / horizontal_interval;
				int y = ap.getPeak().getY() / vertical_interval;
				Vector deblended_ap_list = ap.deblend(threshold, mean_radius[x][y]);

				for (int j = 0 ; j < deblended_ap_list.size() ; j++) {
					AdjoiningPixel deblended_ap = (AdjoiningPixel)deblended_ap_list.elementAt(j);
					StarImage star = deblended_ap.createStar();
					boolean valid = true;

					// Regards the peak value as brightness of a star.
					if (mode == MODE_PEAK)
						star.setValue(star.getPeak());

					// Regards the amount of pixel values within the specified aperture.
					if (mode == MODE_APERTURE) {
						double value = 0.0;
						int count = 0;
						int base_x = (int)(star.getX() - (double)inner_aperture_size / 2.0 + 0.5);
						int base_y = (int)(star.getY() - (double)inner_aperture_size / 2.0 + 0.5);
						for (int dy = 0 ; dy < inner_aperture_size ; dy++) {
							for (int dx = 0 ; dx < inner_aperture_size ; dx++) {
								try {
									value += original_stellar_image.getValue(base_x + dx, base_y + dy);
									count++;
								} catch ( IndexOutOfBoundsException exception ) {
								}
							}
						}

						double sky_value = 0.0;
						int sky_count = 0;
						base_x = (int)(star.getX() - (double)outer_aperture_size / 2.0 + 0.5) - 1;
						base_y = (int)(star.getY() - (double)outer_aperture_size / 2.0 + 0.5) - 1;
						for (int dy = 0 ; dy < outer_aperture_size + 2 ; dy++) {
							for (int dx = 0 ; dx < outer_aperture_size + 2 ; dx++) {
								try {
									sky_value += original_stellar_image.getValueOnReversingTiling(base_x + dx, base_y + dy);
									sky_count++;
								} catch ( IndexOutOfBoundsException exception ) {
								}
							}
						}

						if (sky_count - count <= 0) {
							valid = false;
						} else {
							double sky_per_pixel = (sky_value - value) / (double)(sky_count - count);

							value -= sky_per_pixel * (double)count;

							star.setValue(value);

							if (value < threshold * 3.0)
								valid = false;
						}
					}

					if (valid)
						list.addElement(star);

					// Corrects the positions.
					if (correct_blooming_position) {
						Pixel[] pixels = deblended_ap.getPixels();
						Position position = null;
						for (int k = 0 ; k < pixels.length ; k++) {
							for (int l = 0 ; l < corrected_positions.length ; l++) {
								if (pixels[k].getX() == (int)corrected_positions[l].getX()  &&
									pixels[k].getY() == (int)corrected_positions[l].getY()) {
									position = corrected_positions[l];
									break;
								}
							}
							if (position != null)
								break;
						}
						if (position != null) {
							if (position.getDistanceFrom(star) > 1.5) {
								monitor_set.addMessage("Blooming star position corrected: (" + star.getX() + ", " + star.getY() + ") -> (" + position.getX() + ", " + position.getY() + ")");

								star.setX(position.getX());
								star.setY(position.getY());
							}
						}
					}
				}
			}
			monitor_set.addMessage("Deblended adjoining pixels: " + list.size() + " stars");
		}

		monitor_set.addMessage(new Date().toString());
		monitor_set.addSeparator();
	}
}
