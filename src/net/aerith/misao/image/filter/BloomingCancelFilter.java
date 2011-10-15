/*
 * @(#)BloomingCancelFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;
import net.aerith.misao.pixy.image_processing.DefaultBackgroundEstimator;
import net.aerith.misao.pixy.star_detection.AdjoiningPixelDetector;

/**
 * The <code>BloomingCancelFilter</code> is an image processing filter
 * to cancel the blooming. The result is stored in the original image.
 * <p>
 * In the case both the blooming and streaks appear, operate the 
 * <code>StreakCancelFilter</code> at first, then operate this
 * <code>BloomingCancelFilter</code>.
 * <p>
 * There are some requirements.
 * <ul>
 * <li>The blooming must stretch vertically.
 * <li>In the process, the background of the image is assumed to be a
 *     constant value. So the image must be flat. 
 * <li>The highest pixel value is regarded as saturated, even if no
 *     star is really saturated.
 * <li>The star center of every blooming star must be in the image.
 * <li>Every blooming star must be separated. Two blooming lines of
 *     two stars must not be blended.
 * </ul>
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class BloomingCancelFilter extends Filter {
	/**
	 * The threshold ratio to regard a pixel is saturated. When 1.0,
	 * only the biggest pixels are regarded as saturated.
	 */
	protected double blooming_threshold_ratio = 0.6;

	/**
	 * True when to keep the blooming flag image.
	 */
	protected boolean keep_blooming_flag_image = false;

	/**
	 * The value of influenced direction which represents left.
	 */
	protected final static int LEFT = 1;

	/**
	 * The value of influenced direction which represents right.
	 */
	protected final static int RIGHT = 2;

	/**
	 * The blooming flag image.
	 */
	protected MonoImage blooming_flag_image = null;

	/**
	 * The list of blooming star positions.
	 */
	protected Position[] blooming_positions = new Position[0];

	/**
	 * Constructs a filter.
	 */
	public BloomingCancelFilter ( ) {
	}

	/**
	 * Sets the flag to keep the blooming flag image.
	 */
	public void keepBloomingFlagImage ( ) {
		keep_blooming_flag_image = true;
	}

	/**
	 * Gets the blooming flag image. This is only for debugging use.
	 * The flag is 2 if the pixel is saturated, 1 if the pixel is also
	 * blooming, not saturated but adjoining to the saturated pixels,
	 * and 0 if not blooming.
	 * @return the blooming flag image.
	 */
	public MonoImage getBloomingFlagImage ( ) {
		return blooming_flag_image;
	}

	/**
	 * Gets the list of blooming star positions.
	 * @return the list of blooming star positions.
	 */
	public Position[] getBloomingPositions ( ) {
		return blooming_positions;
	}

	/**
	 * Operates the image processing filter and stores the result into
	 * the original image buffer.
	 * <p>
	 * There are some requirements.
	 * <ul>
	 * <li>The blooming must stretch vertically.
	 * <li>In the process, the background of the image is assumed to
	 *     be a constant value. So the image must be flat. 
	 * <li>The highest pixel value is regarded as saturated, even if
	 *     no star is really saturated.
	 * <li>The star center of every blooming star must be in the image.
	 * <li>Every blooming star must be separated. Two blooming lines
	 *     of two stars must not be blended.
	 * </ul>
	 * <p>
	 * The process in this method assumes that the blooming influences
	 * badly towards right. But in the case it does towards left, the 
	 * image is automatically reversed and processed properly.
	 * @param image the original image to process.
	 * @return the original image buffer.
	 */
	public MonoImage operate ( MonoImage image ) {
		monitor_set.addMessage("[Blooming cancel filter]");
		monitor_set.addMessage(new Date().toString());

		Random random = new Random();

		// Keeps the original image.
		MonoImage original_image = image.cloneImage();

		// Estimates the background deviation in a simple way.
		DefaultBackgroundEstimator estimator = new DefaultBackgroundEstimator(image);
		try {
			estimator.operate();
		} catch ( Exception exception ) {
			return image;
		}
		double background_value = estimator.getBackground();
		double background_deviation = estimator.getBackgroundDeviation();
		monitor_set.addMessage("Background value: " + background_value);
		monitor_set.addMessage("Background deviation: " + background_deviation);

		// Calculates the maximum pixel value and sets the threshold
		// to regard as saturated.
		Statistics image_stat = new Statistics(image);
		image_stat.calculate();
		double blooming_threshold = (image_stat.getMax() - background_value) * blooming_threshold_ratio + background_value;
		monitor_set.addMessage("Maximum pixel value: " + image_stat.getMax());
		monitor_set.addMessage("Saturated threshold: " + blooming_threshold);

		// The flag image which represents if the pixel is blooming or not.
		// The flag is 2 if the pixel is saturated, 1 if the pixel is also
		// blooming, not saturated but adjoining to the saturated pixels,
		// and 0 if not blooming.
		blooming_flag_image = new MonoImage(new ByteBuffer(image.getSize()));

		// Judges in which direction the blooming influences badly.
		// The pixels in that direction will be illegal because of 
		// streaks or dark holes. The blooming itself shifts to that
		// direction.
		// In order to determine the direction based on the streaks,
		// here calculates the median value of both sides and compares.
		// Higher side is that direction.
		// In order to determine the direction based on the dark holes,
		// here counts the number of dark holes in both sides and compares.
		// The side which has much more dark holes is that direction.
		int streak_count_max = 100;
		Array left_array = new Array(streak_count_max);
		Array right_array = new Array(streak_count_max);
		int left_count = 0;
		int right_count = 0;
		int left_hole = 0;
		int right_hole = 0;

		for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				if (image.getValue(x, y) > blooming_threshold) {
					// Sets the flag of saturated pixels to 2.
					blooming_flag_image.setValue(x, y, 2);

					// Checks the left side and right side pixel values.
					double v_left = image.getValueOnFlatExtension(x - 1, y);
					double v_right = image.getValueOnFlatExtension(x + 1, y);

					if (v_left < background_value - 3.0 * background_deviation) {
						// The left side pixel is a dark hole.
						left_hole++;
					} else if (v_left <= blooming_threshold) {
						if (left_count < streak_count_max)
							left_array.set(left_count++, v_left - background_value);
					}

					if (v_right < background_value - 3.0 * background_deviation) {
						// The right side pixel is a dark hole.
						right_hole++;
					} else if (v_right <= blooming_threshold) {
						if (right_count < streak_count_max)
							right_array.set(right_count++, v_right - background_value);
					}
				}
			}
		}

		// In the case there are few stars in the image, it fails.
		int saturated_pixels = 0;
		for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				if (blooming_flag_image.getValue(x, y) > 0.5)
					saturated_pixels++;
			}
		}
		double saturated_ratio = (double)saturated_pixels / (double)(image.getSize().getWidth() * image.getSize().getHeight());
		monitor_set.addMessage("Saturated pixels ratio: " + saturated_ratio);
		if (saturated_ratio > 0.1) {
			if (keep_blooming_flag_image == false)
				blooming_flag_image = null;

			monitor_set.addMessage("Probably there are no stars in the image.");

			monitor_set.addMessage(new Date().toString());
			return image;
		}

		// The strings to show the direction to the monitors.
		String[] directions = new String[3];
		directions[0] = "unknown";
		directions[LEFT] = "left";
		directions[RIGHT] = "right";

		// Based on the streaks, it calculates the median value
		// of both sides and compares. Higher side is that direction.
		int streak_direction = 0;
		Array left_array2 = left_array.cloneArray(left_count);
		Array right_array2 = right_array.cloneArray(right_count);
		left_array2.sortAscendant();
		right_array2.sortAscendant();
		Statistics left_stat = new Statistics(left_array2);
		Statistics right_stat = new Statistics(right_array2);
		left_stat.calculate();
		right_stat.calculate();
		if (left_count != 0  &&  right_count != 0) {
			if (left_array2.getValueAt(left_count / 2) > right_array2.getValueAt(right_count / 2) + background_deviation * 3.0)
				streak_direction = LEFT;
			if (right_array2.getValueAt(right_count / 2) > left_array2.getValueAt(left_count / 2) + background_deviation * 3.0)
				streak_direction = RIGHT;
		}
		if (left_count == 0)
			monitor_set.addMessage("Left side: no data");
		else
			monitor_set.addMessage("Left side: " + left_stat.getAverage() + "  " + left_array2.getValueAt(left_count / 2));
		if (right_count == 0)
			monitor_set.addMessage("Right side: no data");
		else
			monitor_set.addMessage("Right side: " + right_stat.getAverage() + "  " + right_array2.getValueAt(right_count / 2));
		monitor_set.addMessage("Streak influenced direction: " + directions[streak_direction]);

		// Based on the dark holes, it counts the number of dark holes
		// in both sides and compares. The side which has much more
		// dark holes is that direction.
		int hole_direction = 0;
		if ((left_hole + 2) >= (right_hole + 2) * 2)
			hole_direction = LEFT;
		if ((right_hole + 2) >= (left_hole + 2) * 2)
			hole_direction = RIGHT;
		monitor_set.addMessage("Left side holes: " + left_hole);
		monitor_set.addMessage("Right side holes: " + right_hole);
		monitor_set.addMessage("Hole influenced direction: " + directions[hole_direction]);

		// The process in this method assumes that the blooming influences
		// badly towards right. So if the direction is left, reverses the
		// images. 
		// In the case the direction determined based on the streaks and 
		// dark holes do not coincide, the direction determined based on 
		// the dark holes is adopted, so that the dark holes are deleted.
		// Then the image is reversed only before and after the streak 
		// canceling process.
		int blooming_direction = RIGHT;
		if (hole_direction == 0  &&  streak_direction == 0) {
			if (left_count == 0  ||  right_count == 0)
				streak_direction = LEFT;
			else if (left_array2.getValueAt(left_count / 2) > right_array2.getValueAt(right_count / 2))
				streak_direction = LEFT;
			else
				streak_direction = RIGHT;
			blooming_direction = streak_direction;
			hole_direction = streak_direction;
		} else if (hole_direction != 0  &&  streak_direction != 0) {
			blooming_direction = hole_direction;
		} else if (hole_direction != 0) {
			blooming_direction = hole_direction;
			streak_direction = hole_direction;
		} else if (streak_direction != 0) {
			blooming_direction = streak_direction;
			hole_direction = streak_direction;
		}
		if (blooming_direction == LEFT) {
			image.reverseHorizontally();
			blooming_flag_image.reverseHorizontally();
			original_image.reverseHorizontally();
		}
		monitor_set.addMessage("Influenced direction: " + directions[blooming_direction]);

		// Detects blooming stars as AdjoiningPixel objects.
		MonoImage tmp_image = blooming_flag_image.cloneImage();
		AdjoiningPixelDetector ap_detector = new AdjoiningPixelDetector(tmp_image);
		ap_detector.setThreshold(0.5);
		ap_detector.setMinimumPeakValue(0.5);
		try {
			ap_detector.operate();
		} catch ( Exception exception ) {
			return image;
		}
		Vector ap_list = ap_detector.getAdjoiningPixelList();
		tmp_image = null;
		monitor_set.addMessage("Number of blooming stars: " + ap_list.size());

		// Removes tiny blooming.
		Vector l = new Vector();
		for (int i = 0 ; i < ap_list.size() ; i++) {
			AdjoiningPixel ap = (AdjoiningPixel)ap_list.elementAt(i);

			int width = ap.getXMax() - ap.getXMin() + 1;
			int height = ap.getYMax() - ap.getYMin() + 1;

			if (width > 2  ||  height > 2) {
				l.addElement(ap);
			} else {
				Pixel[] pixels = ap.getPixels();
				for (int j = 0 ; j < pixels.length ; j++)
					blooming_flag_image.setValue(pixels[j].getX(), pixels[j].getY(), 0);
			}
		}
		ap_list = l;

		// Records blooming stars as BloomingStar objects.
		BloomingStar[] blooming_stars = new BloomingStar[ap_list.size()];
		for (int i = 0 ; i < ap_list.size() ; i++) {
			AdjoiningPixel ap = (AdjoiningPixel)ap_list.elementAt(i);

			// Records the peak value.
			double peak = 0.0;
			Pixel[] pixels = ap.getPixels();
			for (int j = 0 ; j < pixels.length ; j++) {
				double v = image.getValue(pixels[j].getX(), pixels[j].getY());
				if (j == 0  ||  peak < v)
					peak = v;
			}

			blooming_stars[i] = new BloomingStar();
			blooming_stars[i].ap = ap;
			blooming_stars[i].peak = peak;
		}

		// Assumes that the one line right of the saturated pixels
		// are badly influenced by the blooming. So regards them
		// also as blooming pixels and sets the flag to 1.
		for (int i = 0 ; i < blooming_stars.length ; i++) {
			Pixel[] pixels = blooming_stars[i].ap.getPixels();
			for (int j = 0 ; j < pixels.length ; j++) {
				if (pixels[j].getX() + 1 < image.getSize().getWidth()) {
					if (blooming_flag_image.getValue(pixels[j].getX() + 1, pixels[j].getY()) < 0.5) {
						// One pixel right of the saturated pixel.
						// Sets the flag to 1.
						blooming_flag_image.setValue(pixels[j].getX() + 1, pixels[j].getY(), 1);
						blooming_stars[i].ap2.addPixel(new Pixel(pixels[j].getX() + 1, pixels[j].getY(), 1));
					}
				}
			}
		}

		// Detects the faint extension of blooming over the top and bottom
		// of the saturated pixels. If the pixel value is much larger than
		// the background or pixels around, it is filled with the average of 
		// the pixels around.
		for (int i = 0 ; i < blooming_stars.length ; i++) {
			Pixel[] pixels = blooming_stars[i].ap.getPixels();

			// Sorts the pixels in order of x position. Required.
			Array x_array = new Array(pixels.length);
			for (int j = 0 ; j < pixels.length ; j++)
				x_array.set(j,  pixels[j].getX());
			ArrayIndex index = x_array.sortAscendant();

			for (int j = 0 ; j < pixels.length ; j++) {
				// The -1 represents over the top, +1 represents over the bottom.
				for (int dir = -1 ; dir <= 1 ; dir += 2) {
					int x = pixels[index.get(j)].getX();
					int y = pixels[index.get(j)].getY() + dir;

					while (true) {
						boolean flag = false;

						if (x > 0  &&  y >= 0  &&  y < image.getSize().getHeight()  &&
							blooming_flag_image.getValue(x, y) < 0.5  &&
							image.getValue(x, y) > background_value + background_deviation * 3.0) {

							// Calculates the average and deviation of 10 pixels left of 
							// the current pixel. Blooming pixels are excluded.
							int filter_size = 10;
							Array array = new Array(filter_size);
							int count = 0;
							for (int dx = - filter_size ; dx <= -1 ; dx++) {
								if (blooming_flag_image.getValueOnFlatExtension(x + dx, y) < 0.5)
									array.set(count++, image.getValueOnFlatExtension(x + dx, y));
							}
							if (count > 0) {
								Statistics stat = new Statistics(array.cloneArray(count));
								stat.calculate();

								// If the pixel value is much larger than the
								// background or pixels around, it is filled with
								// the average of the pixels around.
								// The flag is set to 1.
								if (image.getValue(x, y) > stat.getAverage() + stat.getDeviation() * 3.0) {
									image.setValue(x, y, stat.getAverage());
									blooming_flag_image.setValue(x, y, 1);
									blooming_stars[i].ap2.addPixel(new Pixel(x, y, 1));
									flag = true;
								}
							}
						}

						if (flag)
							y += dir;
						else
							break;
					}
				}
			}
		}

		// Fills the blooming pixels with the leftside pixel.
		for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
			for (int x = 1 ; x < image.getSize().getWidth() ; x++) {
				if (blooming_flag_image.getValue(x, y) > 0.5) {
					image.setValue(x, y, image.getValue(x - 1, y));
				}
			}

			// Adds random noise to be natural.
			for (int x = 1 ; x < image.getSize().getWidth() ; x++) {
				if (blooming_flag_image.getValue(x, y) > 0.5) {
					image.setValue(x, y, image.getValue(x, y) + random.nextGaussian() * background_deviation);
				}
			}
		}

		// Presumes the center of each blooming star and the radius
		// of the saturated part.
		// Because the blooming is biased to stretch in over or
		// under direction, the simple gravity center is shifted 
		// to the direction. When another star overlaps on the
		// blooming, the gravity center is shifted too. Therefore,
		// the center position and radius are re-calculated after
		// the simple gravity center is obtained.
		blooming_positions = new Position[blooming_stars.length];
		for (int i = 0 ; i < blooming_stars.length ; i++) {
			double fx = 0.0;
			double fy = 0.0;
			double fw = 0.0;

			Pixel[] pixels = blooming_stars[i].ap.getPixels();

			double threshold = background_value;

			// Repeats twice due to the re-calculation.
			for (int repeat = 0 ; repeat < 2 ; repeat++) {
				fx = 0.0;
				fy = 0.0;
				fw = 0.0;

				// Presumes the center of the blooming star.
				for (int j = 0 ; j < pixels.length ; j++) {
					int x = pixels[j].getX();
					int y = pixels[j].getY();

					if (image.getValueOnFlatExtension(x, y) - threshold > 0) {
						fx += (double)x * (image.getValueOnFlatExtension(x, y) - threshold);
						fy += (double)y * (image.getValueOnFlatExtension(x, y) - threshold);
						fw += image.getValueOnFlatExtension(x, y) - threshold;
					}
				}

				fx = fx / fw + 0.5;
				fy = fy / fw + 0.5;

				blooming_stars[i].center = new Position(fx, fy);

				// Calculates the radius of the saturated part,
				// which is half of the blooming width at the center.
				int x_left = (int)fx;
				int x_right = (int)fx;
				int y = (int)fy;
				while (x_left > 0  &&  blooming_flag_image.getValue(x_left - 1, y) > 0.5)
					x_left--;
				while (x_right < image.getSize().getWidth() - 1  &&  blooming_flag_image.getValue(x_right + 1, y) > 0.5)
					x_right++;
				double radius = (double)(x_right - x_left + 1) / 2.0;
				if (radius < 0.5)
					radius = 0.5;

				blooming_stars[i].radius = radius;

				// In the re-calculation, only blooming pixels within twice 
				// of the radius calculated above are used. To calculate the
				// gravity center, the pixel value subtracted by the median
				// of the pixels within the area is used as weight. Those
				// pixels whose value is less than the median, they are ignored.
				if (repeat == 0) {
					// Calculates the median value.
					int size = ((int)radius + 1) * 2 + 1;
					Array array = new Array(size * size);
					int count = 0;
					for (y = (int)(fy - radius) ; y <= (int)(fy + radius) ; y++) {
						for (int x = (int)(fx - radius) ; x <= (int)(fx + radius) ; x++) {
							if (0 <= y  &&  y < image.getSize().getHeight()  &&  0 <= x  &&  x < image.getSize().getWidth()) {
								double r2 = ((double)x + 0.5 - fx) * ((double)x + 0.5 - fx) + ((double)y + 0.5 - fy) * ((double)y + 0.5 - fy);
								if (r2 <= radius * radius * 4.0)
									array.set(count++, image.getValueOnFlatExtension(x, y));
							}
						}
					}
					if (count > 0) {
						Array array2 = array.cloneArray(count);
						array2.sortAscendant();
						threshold = array2.getValueAt(count / 2);
					}
				}

				// Here outputs the (x,y) of the center on the original image.
				double orig_fx = fx;
				if (blooming_direction == LEFT)
					orig_fx = (double)image.getSize().getWidth() - fx;
				monitor_set.addMessage("  " + i +
									   "  area=" + blooming_stars[i].ap.getPixelCount() +
									   "  peak=" + blooming_stars[i].peak +
									   "  center=(" + orig_fx + "," + fy + ")" +
									   "  radius=" + radius);

				// Records the blooming star positions.
				if (repeat == 1)
					blooming_positions[i] = new Position(orig_fx, fy);
			}
		}

		// In the case the direction determined based on the streaks and 
		// dark holes do not coincide, the image is reversed before and 
		// after the streak canceling process.
		if (streak_direction != blooming_direction) {
			image.reverseHorizontally();
			blooming_flag_image.reverseHorizontally();
			original_image.reverseHorizontally();
		}

		// Subtracts the streaks. Creates the mapping table between the
		// pixel value in the right side of the blooming center 
		// and the symmetric pixel value in the left side. 
		// Sorting and applying the median filter to the mapping table,
		// gets the mean value to subtract for each pixel. Then subtracts.
		for (int i = 0 ; i < blooming_stars.length ; i++) {
			// The height of blooming.
			int y_min = blooming_stars[i].ap.getYMin();
			int y_max = blooming_stars[i].ap.getYMax();
			if (y_min > blooming_stars[i].ap2.getYMin())
				y_min = blooming_stars[i].ap2.getYMin();
			if (y_max < blooming_stars[i].ap2.getYMax())
				y_max = blooming_stars[i].ap2.getYMax();
			int y_height = y_max - y_min + 1;

			// The x position of the right edge of blooming.
			int[] right_x = new int[y_height];
			for (int y = 0 ; y < y_height ; y++)
				right_x[y] = -1;
			Pixel[] pixels = blooming_stars[i].ap.getPixels();
			for (int j = 0 ; j < pixels.length ; j++) {
				int y = pixels[j].getY() - y_min;
				int x = pixels[j].getX();
				// In the case the streak spreads towards left,
				// the image is reversed.
				if (streak_direction != blooming_direction)
					x = image.getSize().getWidth() - 1 - x;
				if (right_x[y] < x)
					right_x[y] = x;
			}
			pixels = blooming_stars[i].ap2.getPixels();
			for (int j = 0 ; j < pixels.length ; j++) {
				int y = pixels[j].getY() - y_min;
				int x = pixels[j].getX();
				// In the case the streak spreads towards left,
				// the image is reversed.
				if (streak_direction != blooming_direction)
					x = image.getSize().getWidth() - 1 - x;
				if (right_x[y] < x)
					right_x[y] = x;
			}

			// The x position of the right edge of subtracted streak.
			int[] streak_right_x = new int[y_height];
			for (int y = 0 ; y < y_height ; y++)
				streak_right_x[y] = -1;

			// The flag if the row reaches to the end or not.
			boolean[] reached_to_end = new boolean[y_height];
			for (int y = 0 ; y < y_height ; y++)
				reached_to_end[y] = false;

			right_array = new Array(y_height);
			left_array = new Array(y_height);

			// The upper limit of the offset from the right edge of blooming.
			int dx_limit = 50;

			// When there is a bright star right of the blooming, the light 
			// of the star is also regarded as streaks and subtracted. 
			// To avoid that, the maximum limit of the offset is determined
			// where the average or median value begins to increase towards right.
			// In addition, the minimum average value, just before to begin to increase
			// towards right, is remembered. When the average of the symmetric
			// pixel value in the left side is lower than that value, it
			// subtracts the difference from the remembered minimum value 
			// in order to avoid to subtract too much.
			double minimum_right_average = 0.0;
			double minimum_right_median = 0.0;
			for (int dx = 1 ; dx <= dx_limit ; dx++) {
				for (int y = y_min ; y <= y_max ; y++)
					right_array.set(y - y_min, image.getValueOnFlatExtension(right_x[y - y_min] + dx, y));
				right_stat = new Statistics(right_array);
				right_stat.calculate();
				right_array.sortAscendant();

				if (dx > 1  &&  right_stat.getAverage() > minimum_right_average) {
					dx_limit = dx - 1;
					break;
				}
				if (dx > 1  &&  right_array.getValueAt(y_height / 2) > minimum_right_median + background_deviation) {
					dx_limit = dx - 1;
					break;
				}

				minimum_right_average = right_stat.getAverage();
				minimum_right_median = right_array.getValueAt(y_height / 2);
			}

			// The x position of the center of blooming.
			int x_center = (int)blooming_stars[i].center.getX();
			// In the case the streak spreads towards left,
			// the image is reversed.
			if (streak_direction != blooming_direction)
				x_center = image.getSize().getWidth() - 1 - x_center;

			// The offset from the right edge of blooming.
			int dx = 0;

			// Increasing the offset from the right edge of blooming 
			// one pixel by one, creates the mapping table between the
			// pixel value in the right side of the blooming center 
			// and the symmetric pixel value in the left side. 
			// Sorting and applying the median filter to the mapping table,
			// gets the mean value to subtract for each pixel. Then subtracts.
			// When a pixel collides with another blooming, or overgoes
			// the right edge of the image, the y row is judged to reach
			// to the end. When all rows reach to the end, it ends.
			// When the offset reaches to the maximum limit, it also ends.
			while (true) {
				dx++;

				int count = 0;
				for (int y = y_min ; y <= y_max ; y++) {
					// Ignores if already reached to the end.
					if (reached_to_end[y - y_min] == false) {
						if (right_x[y - y_min] + dx >= image.getSize().getWidth()) {
							// Reaches to the end by overgoing the right edge of the image.
							streak_right_x[y - y_min] = right_x[y - y_min] + dx - 1;
							reached_to_end[y - y_min] = true;
						} else if (blooming_flag_image.getValue(right_x[y - y_min] + dx, y) > 0.5) {
							// Reaches to the end by collides with another blooming.
							streak_right_x[y - y_min] = right_x[y - y_min] + dx - 1;
							reached_to_end[y - y_min] = true;
						} else if (dx > dx_limit) {
							// Reaches to the upper limit of the offset.
							streak_right_x[y - y_min] = right_x[y - y_min] + dx - 1;
							reached_to_end[y - y_min] = true;
						} else {
							streak_right_x[y - y_min] = right_x[y - y_min] + dx;
							count++;
						}
					}
				}

				// When all y rows reach to the end, it ends.
				if (count == 0) {
					monitor_set.addMessage("Streak canceling size (" + i + "): " + (dx - 1));
					break;
				}

				// Creates the mapping table between the pixel value in
				// the right side of the blooming center and the
				// symmetric pixel value in the left side. 
				for (int y = y_min ; y <= y_max ; y++) {
					// The offset from the blooming center.
					int dx_center = right_x[y - y_min] + dx - x_center;

					left_array.set(y - y_min, image.getValueOnFlatExtension(x_center - dx_center, y));
					right_array.set(y - y_min, image.getValueOnFlatExtension(x_center + dx_center, y));
				}

				left_stat = new Statistics(left_array);
				left_stat.calculate();

				// Sorting and applying the median filter to the mapping 
				// table, gets the mean value to subtract for each pixel.
				// The median filter with 9 pixel window rejects the
				// influence of stars in the both sides and the mean 
				// value in both sides is obtained. Then the mean value
				// to subtract is obtained.
				ArrayIndex index = right_array.sortAscendant();

				Array left_median_array = new Array(y_height);

				for (int y = y_min ; y <= y_max ; y++) {
					if (reached_to_end[y - y_min] == false) {
						// Determines the range to apply the median filter.
						int y_index = 0;
						while (index.get(y_index) != y - y_min)
							y_index++;

						int filter_size = 9;

						int y2_min = y_index - (filter_size - 1) / 2;
						int y2_max = y_index + filter_size / 2;
						if (y2_min < 0) {
							y2_min = 0;
							y2_max = filter_size - 1;
						}
						if (y2_max >= y_height) {
							y2_min = y_height - 1 - filter_size + 1;
							y2_max = y_height - 1;
						}
						if (y2_min < 0) {
							y2_min = 0;
							filter_size = y2_max - y2_min + 1;
						}

						// Obtains the mean value to subtract from the
						// current pixel by the median filter.
						Array array = new Array(filter_size);
						for (int y2 = y2_min ; y2 <= y2_max ; y2++)
							array.set(y2 - y2_min, left_array.getValueAt(index.get(y2)));
						array.sortAscendant();

						left_median_array.set(y - y_min, array.getValueAt(filter_size / 2));
					}
				}

				// The more the pixel value in the right side increases,
				// the more the mean pixel value in the left side must
				// increase too.
				for (int y = 1 ; y < y_height ; y++) {
					if (left_median_array.getValueAt(index.get(y)) < left_median_array.getValueAt(index.get(y - 1))) {
						left_median_array.set(index.get(y), left_median_array.getValueAt(index.get(y - 1)));
					}
				}

				for (int y = y_min ; y <= y_max ; y++) {
					if (reached_to_end[y - y_min] == false) {
						int y_index = 0;
						while (index.get(y_index) != y - y_min)
							y_index++;

						double delta_value = right_array.getValueAt(y_index) - left_median_array.getValueAt(y - y_min);

						// When the average of the symmetric pixel value
						// in the left side is lower than that value, it
						// subtracts the difference from the remembered 
						// minimum value in the right side in order to 
						// avoid to subtract too much.
						if (left_stat.getAverage() < minimum_right_average) {
							double base_up = minimum_right_average - left_stat.getAverage();
							delta_value -= base_up;
						}

						if (delta_value < 0.0)
							delta_value = 0.0;

						// Subtracts.
						double v = image.getValue(right_x[y - y_min] + dx, y) - delta_value;

						// Avoids to be too much lower than the background.
						if (v < background_value - background_deviation * 2.0)
							v = image.getValue(right_x[y - y_min] + dx - 1, y) + random.nextGaussian() * background_deviation;

						// Avoids the appearance of hot pixels due to the miss
						// of subtraction in the flat area on the original image.
						if (original_image.getValue(right_x[y - y_min] + dx, y) < original_image.getValue(right_x[y - y_min] + dx - 1, y) + background_deviation * 3.0  &&
							v > image.getValue(right_x[y - y_min] + dx - 1, y) + background_deviation * 3.0)
							v = image.getValue(right_x[y - y_min] + dx - 1, y) + random.nextGaussian() * background_deviation;

						image.setValue(right_x[y - y_min] + dx, y, v);
					}
				}
			}

			// Operates local smooth filter at the right edge of
			// the subtracted streak.
			double[] buffer = new double[y_height];
			for (int y = 0 ; y < y_height ; y++) {
				if (streak_right_x[y] >= 0) {
					double average = 0.0;
					for (int dy = -1 ; dy <= 1 ; dy++) {
						for (dx = -1 ; dx <= 1 ; dx++)
							average += image.getValueOnFlatExtension(streak_right_x[y] + dx, y_min + y + dy);
					}
					buffer[y] = average / 9.0;
				}
			}
			for (int y = 0 ; y < y_height ; y++) {
				if (streak_right_x[y] >= 0)
					image.setValue(streak_right_x[y], y_min + y, buffer[y]);
			}
		}
		original_image = null;

		// In the case the direction determined based on the streaks and 
		// dark holes do not coincide, the image is reversed before and 
		// after the streak canceling process.
		if (streak_direction != blooming_direction) {
			image.reverseHorizontally();
			blooming_flag_image.reverseHorizontally();
		}

		// Fills the blooming pixels with an average of the left side
		// and right side pixels. Now that the streaks are deleted,
		// both sides can be considered.
		for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				if (blooming_flag_image.getValue(x, y) > 0.5) {
					int x_left = x - 1;
					int x_right = x + 1;
					while (x_left >= 0  &&  blooming_flag_image.getValue(x_left, y) > 0.5)
						x_left--;
					while (x_right < image.getSize().getWidth()  &&  blooming_flag_image.getValue(x_right, y) > 0.5)
						x_right++;

					if (x_left == -1  &&  x_right == image.getSize().getWidth()) {
						// Nothing to do.
					} else if (x_left == -1) {
						// Fills with the right side pixel.
						image.setValue(x, y, image.getValue(x_right, y));
					} else if (x_right == image.getSize().getWidth()) {
						// Fills with the left side pixel.
						image.setValue(x, y, image.getValue(x_left, y));
					} else {
						// Fills the blooming pixels with an average of
						// the left side and right side pixels, weighed
						// depending on the distance from them.
						double w_left = (double)(x - x_left);
						double w_right = (double)(x_right - x);
						double v = (image.getValue(x_left, y) * w_right + image.getValue(x_right, y) * w_left) / (w_left + w_right);
						image.setValue(x, y, v);
					}
				}
			}

			// Adds random noise to be natural.
			for (int x = 1 ; x < image.getSize().getWidth() ; x++) {
				if (blooming_flag_image.getValue(x, y) > 0.5) {
					image.setValue(x, y, image.getValue(x, y) + random.nextGaussian() * background_deviation);
				}
			}
		}

		// Sets the original peak value in the center of each
		// blooming star, then the peak height is restored.
		for (int i = 0 ; i < blooming_stars.length ; i++) {
			double fx = blooming_stars[i].center.getX();
			double fy = blooming_stars[i].center.getY();
			double radius = blooming_stars[i].radius;

			for (int y = (int)(fy - radius) ; y <= (int)(fy + radius) ; y++) {
				for (int x = (int)(fx - radius) ; x <= (int)(fx + radius) ; x++) {
					if (0 <= y  &&  y < image.getSize().getHeight()  &&  0 <= x  &&  x < image.getSize().getWidth()) {
						double r2 = ((double)x + 0.5 - fx) * ((double)x + 0.5 - fx) + ((double)y + 0.5 - fy) * ((double)y + 0.5 - fy);
						if (r2 <= radius * radius)
							image.setValue(x, y, blooming_stars[i].peak);
					}
				}
			}
		}

		// Operates local smooth filter around the center of
		// the blooming star.
		tmp_image = image.cloneImage();
		for (int i = 0 ; i < blooming_stars.length ; i++) {
			double fx = blooming_stars[i].center.getX();
			double fy = blooming_stars[i].center.getY();
			double radius = blooming_stars[i].radius + 2.0;
			int r = 1;

			for (int y = (int)(fy - radius) ; y <= (int)(fy + radius) ; y++) {
				for (int x = (int)(fx - radius) ; x <= (int)(fx + radius) ; x++) {
					if (0 <= y  &&  y < image.getSize().getHeight()  &&  0 <= x  &&  x < image.getSize().getWidth()) {
						double r2 = ((double)x + 0.5 - fx) * ((double)x + 0.5 - fx) + ((double)y + 0.5 - fy) * ((double)y + 0.5 - fy);
						if (r2 <= radius * radius) {
							double average = 0.0;
							int count = 0;
							for (int dy = - r ; dy <= r ; dy++) {
								for (int dx = - r ; dx <= r ; dx++) {
									average += tmp_image.getValueOnFlatExtension(x + dx, y + dy);
									count++;
								}
							}
							image.setValue(x, y, average / (double)count);
						}
					}
				}
			}
		}
		tmp_image = null;

		// The process in this method assumes that the blooming influences
		// badly towards right. So if the direction is left, reverses the
		// images. 
		if (blooming_direction == LEFT) {
			image.reverseHorizontally();
			blooming_flag_image.reverseHorizontally();
		}

		if (keep_blooming_flag_image == false)
			blooming_flag_image = null;

		monitor_set.addMessage(new Date().toString());
		monitor_set.addSeparator();

		return image;
	}

	/**
	 * The <code>BloomingStar</code> is a set of adjoining pixels, 
	 * peak value, center position and radius of a blooming star.
	 */
	protected class BloomingStar {
		/**
		 * The adjoining pixels of saturated pixels. It only contains
		 * saturated pixels.
		 */
		public AdjoiningPixel ap = new AdjoiningPixel();

		/**
		 * The adjoining pixels of not saturated pixels. It only 
		 * contains pixels not saturated but adjoining to the 
		 * saturated pixels.
		 */
		public AdjoiningPixel ap2 = new AdjoiningPixel();

		/**
		 * The peak value.
		 */
		public double peak = 0.0;

		/**
		 * The center position.
		 */
		public Position center = new Position();

		/**
		 * The radius.
		 */
		public double radius = 1.0;

		/**
		 * Constructs an empty <code>BloomingStar</code>.
		 */
		public BloomingStar ( ) {
		}
	}
}
