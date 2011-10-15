/*
 * @(#)StreakCancelFilter.java
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
 * The <code>StreakCancelFilter</code> is an image processing filter
 * to cancel the streaks. The result is stored in the original image.
 * <p>
 * The streaks must extend horizontally. It operates the edge 
 * detection and subtraction in two directions, from top to bottom and
 * from bottom to top. Then it merges the two results.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class StreakCancelFilter extends Filter {
	/**
	 * The length to detect streak edges.
	 */
	protected int filter_size = 10;

	/**
	 * The deviation of background.
	 */
	protected double background_deviation = 1.0;

	/**
	 * True when to keep the background flag image.
	 */
	protected boolean keep_background_flag_image = false;

	/**
	 * The background flag image.
	 */
	protected MonoImage background_flag_image = null;

	/**
	 * True when to keep the streak flag image.
	 */
	protected boolean keep_streak_flag_image = false;

	/**
	 * The streak flag image.
	 */
	protected MonoImage streak_flag_image = null;

	/**
	 * Constructs a filter.
	 */
	public StreakCancelFilter ( ) {
	}

	/**
	 * Constructs a filter with initial filter size.
	 * @param initial_filter_size the initial value of filter size.
	 */
	public StreakCancelFilter ( int initial_filter_size ) {
		setFilterSize(initial_filter_size);
	}

	/**
	 * Sets the filter size.
	 * @param new_filter_size the new value of filter size.
	 */
	public void setFilterSize ( int new_filter_size ) {
		filter_size = new_filter_size;
		if (filter_size <= 3)
			filter_size = 3;
	}

	/**
	 * Sets the flag to keep the background flag image.
	 */
	public void keepBackgroundFlagImage ( ) {
		keep_background_flag_image = true;
	}

	/**
	 * Gets the background flag image. This is only for debugging use.
	 * <p>
	 * If 0, the pixel is part of a star, otherwise, the pixel is 
	 * background. If 1, the background spreads around the pixel. If 2,
	 * the background spreads towards left, that is a star exists near
	 * to the right. If 3, the background spreads towards right, that
	 * is a star exists near to the left.
	 * @return the background flag image.
	 */
	public MonoImage getBackgroundFlagImage ( ) {
		return background_flag_image;
	}

	/**
	 * Sets the flag to keep the streak flag image.
	 */
	public void keepStreakFlagImage ( ) {
		keep_streak_flag_image = true;
	}

	/**
	 * Gets the streak flag image. This is only for debugging use.
	 * <p>
	 * This is the merged flag image of two results, from top to 
	 * bottom and from bottom to top. If 2, the pixels is part of a
	 * streak. If 1, the pixel is regarded as part of a streak but
	 * restored finally. If 0, the pixel is not streak.
	 * @return the streak flag image.
	 */
	public MonoImage getStreakFlagImage ( ) {
		return streak_flag_image;
	}

	/**
	 * Operates the image processing filter and stores the result into
	 * the original image buffer.
	 * <p>
	 * The streaks must extend horizontally. It operates the edge
	 * detection and subtraction in two directions, from top to bottom
	 * and from bottom to top. Then it merges the two results.
	 * @param image the original image to process.
	 * @return the original image buffer.
	 */
	public MonoImage operate ( MonoImage image ) {
		monitor_set.addMessage("[Streak cancel filter]");
		monitor_set.addMessage(new Date().toString());

		// Estimates the background deviation in a simple way.
		DefaultBackgroundEstimator estimator = new DefaultBackgroundEstimator(image);
		try {
			estimator.operate();
		} catch ( Exception exception ) {
			return image;
		}
		background_deviation = estimator.getBackgroundDeviation();
		monitor_set.addMessage("Background deviation: " + background_deviation);

		// Creates the flag image to judge if the pixel is background or not.
		// If 0, the pixel is part of a star, otherwise, the pixel is 
		// background. If 1, the background spreads around the pixel. If 
		// 2, the background spreads towards left, that is a star exists
		// near to the right. If 3, the background spreads towards right,
		// that is a star exists near to the left.
		background_flag_image = createBackgroundFlagImage(image);

		// From top to botom, detects the streaks.
		// The flag image which represents the streak pixels is returned.
		// The image buffer is modified.
		MonoImage tmp_image = image.cloneImage();
		MonoImage streak_flag_image1 = detectStreakTopToBottom(tmp_image, background_flag_image);
		tmp_image = null;

		// From bottom to top, detects the streaks.
		// The flag image which represents the streak pixels is returned.
		// The image buffer is modified.
		tmp_image = image.cloneImage();
		tmp_image.reverseVertically();
		background_flag_image.reverseVertically();
		MonoImage streak_flag_image2 = detectStreakTopToBottom(tmp_image, background_flag_image);
		background_flag_image.reverseVertically();
		streak_flag_image2.reverseVertically();
		tmp_image = null;

		// Merges the two flag images, frmo top to bottom and from bottom to top.
		// If the pixels is judged as streaks in any direction, the flag is
		// set to 2. Otherwise, set to 0.
		// The streak area shape becomes asymmetric against the star center
		// only in one direction. Therefore the pixel is streak if judged as
		// streak in any direction.
		streak_flag_image = streak_flag_image1.cloneImage();
		for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				if (streak_flag_image1.getValue(x, y) > 0.5  ||  streak_flag_image2.getValue(x, y) > 0.5) {
					streak_flag_image.setValue(x, y, 2);
				}
			}
		}

		// From top to bottom, subtracts the streaks.
		MonoImage image1 = image.cloneImage();
		subtractTopToBottom(image1, background_flag_image, streak_flag_image);

		// From bottom to top, subtracts the streaks.
		MonoImage image2 = image.cloneImage();
		image2.reverseVertically();
		background_flag_image.reverseVertically();
		streak_flag_image.reverseVertically();
		subtractTopToBottom(image2, background_flag_image, streak_flag_image);
		image2.reverseVertically();
		background_flag_image.reverseVertically();
		streak_flag_image.reverseVertically();

		// Keeps the original image.
		MonoImage original_image = image.cloneImage();

		// Merges the two subtracted images, from top to bottom and from bottom to top.
		for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				double v1 = image1.getValue(x, y);
				double v2 = image2.getValue(x, y);

				if (streak_flag_image.getValue(x, y) > 0.5) {
					// Not to be discontinuous at the edge of streaks, the two results 
					// are weighed based on the distance from the edge. If the pixel
					// is near by the top edge, the result from top to bottom is
					// much more weighed.

					// Checks the distance from the top edge.
					int dy_top = 1;
					while (y - dy_top >= 0  &&  
						   (streak_flag_image1.getValue(x, y - dy_top) > 0.5  ||  streak_flag_image2.getValue(x, y - dy_top) > 0.5)) {
						dy_top++;
					}

					// Checks the distance from the bottom edge.
					int dy_bottom = 1;
					while (y + dy_bottom < image.getSize().getHeight()  &&  
						   (streak_flag_image1.getValue(x, y + dy_bottom) > 0.5  ||  streak_flag_image2.getValue(x, y + dy_bottom) > 0.5)) {
						dy_bottom++;
					}

					// Calculates the merged value of the results in two directions,
					// weighed based on the distance from the edge not to be
					// discontinuous at the edge of streaks. If the pixel
					// is near by the top edge, the result from top to bottom is
					// much more weighed.
					double v = (v1 * (double)dy_bottom + v2 * (double)dy_top) / (double)(dy_top + dy_bottom);

					// However, when there is a star in the streak area, a faint
					// pattern like a cometary tail appears below the star.
					// Therefore, if the pixel is below of a star in one direction,
					// only the resulf in another direction is adopted.
					if (original_image.getValueOnFlatExtension(x, y - dy_top - 1) > v + background_deviation * 2.0) {
						if (original_image.getValueOnFlatExtension(x, y + dy_bottom + 1) <= v + background_deviation * 2.0)
							// A star exists only above.
							v = v2;
					} else if (original_image.getValueOnFlatExtension(x, y + dy_bottom + 1) > v + background_deviation * 2.0) {
						// A star exists only below.
						v = v1;
					}

					image.setValue(x, y, v);
				}
			}
		}

		image1 = null;
		image2 = null;

		if (keep_background_flag_image == false)
			background_flag_image = null;

		// The subtracted image looks like after sweeped by a brush.
		// But in order to delete the foot of streaks, the background
		// pixels are also subtracted, even if not neccessary.
		// So if the result value is similar to the original value,
		// the pixel value is restored.
		// In addition, when the result value becomes larger than the
		// original, it is a mistake. So also restored.
		tmp_image = image.cloneImage();
		for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				if (streak_flag_image.getValue(x, y) > 0.5) {
					// Calculates the amount of 3x3 pixels.
					double orig_v = 0.0;
					double v = 0.0;
					for (int dy = -1 ; dy <= 1 ; dy++) {
						for (int dx = -1 ; dx <= 1 ; dx++) {
							orig_v += original_image.getValueOnFlatExtension(x + dx, y + dy);
							v += tmp_image.getValueOnFlatExtension(x + dx, y + dy);
						}
					}

					// If the result value is similar to the original value,
					// the pixel value is restored.
					// In addition, when the result value becomes larger than the
					// original, it is a mistake. So also restored.
					if (orig_v - v < background_deviation  ||
						tmp_image.getValue(x, y) >= original_image.getValue(x, y) + background_deviation) {
						image.setValue(x, y, original_image.getValue(x, y));

						// Sets the flag to 1 which represents the pixel is regarded
						// as part of streak, but restored here.
						streak_flag_image.setValue(x, y, 1);
					}
				}
			}
		}
		tmp_image = null;

		if (keep_streak_flag_image == false)
			streak_flag_image = null;

		monitor_set.addMessage(new Date().toString());
		monitor_set.addSeparator();

		return image;
	}

	/**
	 * Creates the flag image to judge if the pixel is background or
	 * not. If 0, the pixel is part of a star, otherwise, the pixel is
	 * background. If 1, the background spreads around the pixel. If 
	 * 2, the background spreads towards left, that is a star exists
	 * near to the right. If 3, the background spreads towards right,
	 * that is a star exists near to the left.
	 * @param image the original image.
	 * @return the flag image.
	 */
	private MonoImage createBackgroundFlagImage ( MonoImage image ) {
		MonoImage flag_image = new MonoImage(new ByteBuffer(image.getSize()));

		Array array = new Array(filter_size);

		for (int y = 0 ; y < image.getSize().getHeight() ; y++) {
			// Sets the flag as 1 if the background spreads around the pixel,
			// as 2 if the background spreads towards left, that is a star 
			// exists near to the right, as 3 if the background spreads 
			// towards right, that is a star exists near to the left.
			// Here verifies if the pixel is the background or not, in 
			// the three directions.
			Vector bg_flag_list = new Vector();
			for (int direction = 0 ; direction < 3 ; direction++) {
				boolean[] bg_flag = new boolean[image.getSize().getWidth()];
				for (int x = 0 ; x < image.getSize().getWidth() ; x++)
					bg_flag[x] = false;
				bg_flag_list.addElement(bg_flag);
			}

			// The flag to judge the pixel is background in any direction
			// of the three.
			boolean[] bg_any = new boolean[image.getSize().getWidth()];
			for (int x = 0 ; x < image.getSize().getWidth() ; x++)
				bg_any[x] = false;

			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				for (int direction = 0 ; direction < 3 ; direction++) {
					boolean[] bg_flag = (boolean[])bg_flag_list.elementAt(direction);

					int base_x = - (filter_size - 1) / 2;
					if (direction == 2)
						base_x = 0;
					else if (direction == 1)
						base_x = - filter_size + 1;

					// If the deviation around the pixel is less than three
					// times of the background deviation, it is the background.
					for (int dx = 0 ; dx < filter_size ; dx++)
						array.set(dx, image.getValueOnFlatExtension(x + base_x + dx, y));
					array.sortAscendant();
					Statistics stat = new Statistics(array);
					stat.calculate();
					if (stat.getDeviation() < background_deviation * 3.0) {
						bg_flag[x] = true;
						bg_any[x] = true;
					}
				}
			}

			// In principle, the background must spread towards both sides.
			// However, only in the following two cases, the background
			// is regardes as to spread towards one side.
			//   1. If the deviation around the pixel including both sides
			//      becomes too large.
			//   2. If there are some pixels in one side which cannot be
			//      background because the deviation becomes too large 
			//      in any direction. In other words, the number of 
			//      background pixels is larger when to count in one side
			//      than when to count in both sides.
			// It is in order to judge if the pixel is really besides a
			// star or not.
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				int max_bg_count = 0;
				for (int direction = 0 ; direction < 3 ; direction++) {
					boolean[] bg_flag = (boolean[])bg_flag_list.elementAt(direction);

					int base_x = - (filter_size - 1) / 2;
					if (direction == 2)
						base_x = 0;
					else if (direction == 1)
						base_x = - filter_size + 1;

					if (bg_flag[x]) {
						int bg_count = 0;
						for (int dx = 0 ; dx < filter_size ; dx++) {
							if (0 <= x + base_x + dx  &&  x + base_x + dx < image.getSize().getWidth()) {
								if (bg_any[x + base_x + dx])
									bg_count++;
							}
						}
						if (max_bg_count < bg_count) {
							flag_image.setValue(x, y, direction + 1);
							max_bg_count = bg_count;
						}
					}
				}
			}
		}

		return flag_image;
	}

	/**
	 * Detects the streaks from top to bottom. The specified image 
	 * buffer is modified.
	 * <p>
	 * First of all, it detects the edges and creates the flag image
	 * of edges, for each line from top to bottom, by subtracting the
	 * difference between the above line. After that, tiny edges are
	 * removed because they are not real streaks. And the remained
	 * streakes are spreaded in order to cover the foot.
	 * @param image         the original image to process.
	 * @param bg_flag_image the flag image to represent the background
	 * pixels.
	 * @return the flag image to represents the streak pixels.
	 */
	private MonoImage detectStreakTopToBottom ( MonoImage image, MonoImage bg_flag_image ) {
		MonoImage flag_image = new MonoImage(new ByteBuffer(image.getSize()));

		// Keeps the original image.
		MonoImage original_image = image.cloneImage();

		// Detects the edges and creates the flag image.
		// The flag is 2 if the difference from the line above is large, 
		// 1 if the difference is not large enough, and 0 if not edges.
		// Judges one line by one from top to bottom. The difference is
		// subtracted when edges are detected.
		// In order to calculate the deviation to detect the edge,
		// three lines are required at least. So the y value starts from 3.
		for (int y = 3 ; y < image.getSize().getHeight() ; y++) {
			// When edges are detected, the difference from the line above
			// is subtracted. The result is stored in the temporary buffer.
			// It is stored to the image after all the process of one line
			// are completed.
			double[] buffer = new double[image.getSize().getWidth()];
			for (int x = 0 ; x < image.getSize().getWidth() ; x++)
				buffer[x] = image.getValue(x, y);

			// The flag is 2 if the difference from the line above is large, 
			// 1 if the difference is not large enough, and 0 if not edges.
			byte[] edge_flag = new byte[image.getSize().getWidth()];
			for (int x = 0 ; x < image.getSize().getWidth() ; x++)
				edge_flag[x] = 0;

			// Judges if the pixel is part of an edge and calculates the
			// subtracted value for each pixel.
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				EdgePixel pixel = operateAt(image, bg_flag_image, x, y);
				buffer[x] = pixel.value;
				edge_flag[x] = pixel.flag;
			}

			// Only if the edge pixels with flag 2 chains and is long enough,
			// they are regarded as a real edge. Otherwise, the flag of the pixels
			// is reduced to 1, to the candidates of edges.
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				if (edge_flag[x] > 1) {
					int run_length = 0;
					for (int x2 = x ; x2 < image.getSize().getWidth() ; x2++) {
						if (edge_flag[x2] > 1)
							run_length++;
						else
							break;
					}
					if (run_length < filter_size)
						edge_flag[x] = 1;
				}
			}

			// Edge candidate pixels where the difference is not large enough are
			// regarded as extension of the real edges if they are adjoining to
			// the real edges, and the flag is upgraded to 2.
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				if (edge_flag[x] > 1) {
					for (int x2 = x - 1 ; x2 >= 0  &&  edge_flag[x2] == 1 ; x2--)
						edge_flag[x2] = 2;
					for (int x2 = x + 1 ; x2 < image.getSize().getWidth()  &&  edge_flag[x2] == 1 ; x2++) {
						edge_flag[x2] = 2;
						x++;
					}
				}
			}

			// Sets the flag image so that the edges are part of streaks.
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				if (edge_flag[x] > 1)
					flag_image.setValue(x, y, 1);
			}

			// Subtracts the difference only if the pixel is part of edges.
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				if (edge_flag[x] > 1)
					image.setValue(x, y, buffer[x]);
			}
		}

		image = original_image;
		original_image = null;

		// In fact, many false streaks are detected at around the stars.
		// They are usually very small. So it checks the area of all streaks
		// and annuls tiny streaks.
		MonoImage tmp_image = flag_image.cloneImage();
		AdjoiningPixelDetector ap_detector = new AdjoiningPixelDetector(tmp_image);
		ap_detector.setThreshold(0.5);
		ap_detector.setMinimumPeakValue(0.5);
		try {
			ap_detector.operate();
		} catch ( Exception exception ) {
			System.err.println(exception);
			return image;
		}
		Vector ap_list = ap_detector.getAdjoiningPixelList();
		tmp_image = null;

		// In order to avoid being influenced by very large streaks, 
		// only three quarters from smallest streaks are used to calculate
		// the average and deviation.
		Array ap_size_array = new Array(ap_list.size());
		for (int i = 0 ; i < ap_list.size() ; i++) {
			AdjoiningPixel ap = (AdjoiningPixel)ap_list.elementAt(i);
			ap_size_array.set(i, ap.getPixelCount());
		}
		ap_size_array.sortAscendant();
		ap_size_array = ap_size_array.cloneArray(ap_list.size() * 3 / 4);
		Statistics ap_stat = new Statistics(ap_size_array);
		ap_stat.calculate();
		monitor_set.addMessage("Number of streak areas: " + ap_list.size());
		monitor_set.addMessage("Streak area size average: " + ap_stat.getAverage());
		monitor_set.addMessage("Streak area size deviation: " + ap_stat.getDeviation());

		// Annuls tiny streaks which is as small as the average 
		// of area sizes, or whose area size is smaller than the
		// twice of average. However, if the area size is 100 or 
		// larger, it is a real streak.
		int ap_area_threshold = (int)(ap_stat.getAverage() + ap_stat.getDeviation() * 2);
		if (ap_area_threshold < (int)(ap_stat.getAverage() * 2))
			ap_area_threshold = (int)(ap_stat.getAverage() * 2);
		if (ap_area_threshold > 100)
			ap_area_threshold = 100;
		int ap_count = 0;
		for (int i = 0 ; i < ap_list.size() ; i++) {
			AdjoiningPixel ap = (AdjoiningPixel)ap_list.elementAt(i);
			if (ap.getPixelCount() < ap_area_threshold) {
				Pixel[] pixels = ap.getPixels();
				for (int j = 0 ; j < pixels.length ; j++)
					flag_image.setValue(pixels[j].getX(), pixels[j].getY(), 0);
			} else {
				ap_count++;
			}
		}
		ap_list = null;
		monitor_set.addMessage("Minimum streak area size: " + ap_area_threshold);
		monitor_set.addMessage("Number of real streak areas: " + ap_count);

		// Because the foot of the streaks are similar to the background,
		// no edges can be detected at the border to the background.
		// So the streaks detected here does not contain the foot.
		// In order to delete the foot of streaks, it extends the
		// streaks horizontally by force.
		// While the pixel value is similar to the background level,
		// it is regarded as extension of streaks.
		// No edges are detected from the three lines on top.
		// So the y value starts from 3.
		for (int y = 3 ; y < image.getSize().getHeight() ; y++) {
			// Because only background pixels are considered when to extend
			// the foot of streaks, here creates the buffer of x positions
			// of background pixels.
			int[] bg_x = new int[image.getSize().getWidth()];
			int bg_count = 0;
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				if (bg_flag_image.getValue(x, y) > 0.5)
					bg_x[bg_count++] = x;
			}

			// Extends the streak towards right.
			for (int x = filter_size ; x < bg_count ; x++) {
				// When the left side background pixel is streak, and the
				// current background pixel is not streak, checks if the
				// streak can be extended or not.
				// Calculates the average and deviation of streak pixels in the 
				// left side, and the avearge of background pixels in the right side.
				// If the difference of them is smaller than three times of 
				// deviation of the streak pixels in the left, and smaller than
				// three times of the background deviation, the streak is extended.
				if (flag_image.getValue(bg_x[x - 1], y) > 0.5  &&  flag_image.getValue(bg_x[x], y) < 0.5) {
					Array streak_array = new Array(filter_size);
					for (int dx = - filter_size ; dx <= -1 ; dx++)
						streak_array.set(dx + filter_size, image.getValue(bg_x[x + dx], y));
					Statistics streak_stat = new Statistics(streak_array);
					streak_stat.calculate();

					Array background_array = new Array(filter_size);
					for (int dx = 0 ; dx < filter_size ; dx++) {
						if (x + dx < bg_count)
							background_array.set(dx, image.getValueOnFlatExtension(bg_x[x + dx], y));
						else
							background_array.set(dx, image.getValueOnFlatExtension(bg_x[bg_count - 1], y));
					}
					background_array.sortAscendant();
					double background_median = background_array.getValueAt(filter_size / 2);

					if (background_median - streak_stat.getAverage() < streak_stat.getDeviation() * 3.0  &&
						background_median - streak_stat.getAverage() < background_deviation * 3.0)
						flag_image.setValue(bg_x[x], y, 1);
				}
			}

			// Extends the streak towards left.
			for (int x = bg_count - 1 - filter_size ; x >= 0 ; x--) {
				// When the right side background pixel is streak, and the
				// current background pixel is not streak, checks if the
				// streak can be extended or not.
				// Calculates the average and deviation of streak pixels in the 
				// right side, and the avearge of background pixels in the left side.
				// If the difference of them is smaller than three times of 
				// deviation of the streak pixels in the right, and smaller than
				// three times of the background deviation, the streak is extended.
				if (flag_image.getValue(bg_x[x], y) < 0.5  &&  flag_image.getValue(bg_x[x + 1], y) > 0.5) {
					Array streak_array = new Array(filter_size);
					for (int dx = 1 ; dx <= filter_size ; dx++)
						streak_array.set(dx - 1, image.getValue(bg_x[x + dx], y));
					Statistics streak_stat = new Statistics(streak_array);
					streak_stat.calculate();

					Array background_array = new Array(filter_size);
					for (int dx = 0 ; dx < filter_size ; dx++) {
						if (x - dx >= 0)
							background_array.set(dx, image.getValueOnFlatExtension(bg_x[x - dx], y));
						else
							background_array.set(dx, image.getValueOnFlatExtension(bg_x[0], y));
					}
					background_array.sortAscendant();
					double background_median = background_array.getValueAt(filter_size / 2);

					if (background_median - streak_stat.getAverage() < streak_stat.getDeviation() * 3.0  &&
						background_median - streak_stat.getAverage() < background_deviation * 3.0)
						flag_image.setValue(bg_x[x], y, 1);
				}
			}
		}

		return flag_image;
	}

	/**
	 * Subtracts the streaks and stores the result into the original
	 * image buffer, from top to bottom.
	 * <p>
	 * When the flag of a pixel is set in the specified streak flag 
	 * image, difference between the average around the pixel and the
	 * average of the pixels above is subtracted, and the streaks are
	 * deleted one line by one from top to bottom.
	 * @param image         the original image to process.
	 * @param bg_flag_image the flag image to represent the background
	 * pixels.
	 * @param st_flag_image the flag image to represent the streaks.
	 */
	private void subtractTopToBottom ( MonoImage image, MonoImage bg_flag_image, MonoImage st_flag_image ) {
		// In order to calculate the deviation to detect the edge,
		// three lines are required at least. So the y value starts from 3.
		for (int y = 3 ; y < image.getSize().getHeight() ; y++) {
			// The result of subtraction is stored in the temporary buffer.
			// It is stored to the image after all the process of one line
			// are completed.
			double[] buffer = new double[image.getSize().getWidth()];
			for (int x = 0 ; x < image.getSize().getWidth() ; x++)
				buffer[x] = image.getValue(x, y);

			// Calculates the subtracted value for every streak pixel.
			for (int x = 0 ; x < image.getSize().getWidth() ; x++) {
				if (st_flag_image.getValue(x, y) > 0.5) {
					EdgePixel pixel = operateAt(image, bg_flag_image, x, y);
					buffer[x] = pixel.value;
				}
			}

			// Stores the result of subtraction in the original image.
			for (int x = 0 ; x < image.getSize().getWidth() ; x++)
				image.setValue(x, y, buffer[x]);
		}
	}

	/**
	 * Judges if the pixel is part of an edge and calculates the
	 * subtracted value for the specified pixel.
	 * <p>
	 * The flag is 2 if the difference from the line above is large, 
	 * 1 if the difference is not large enough, and 0 if not edges.
     *  Judges one line by one from top to bottom. The difference is
	 * @param image         the original image to process.
	 * @param bg_flag_image the flag image to represent the background
	 * pixels.
	 * @param x             the x position.
	 * @param y             the y position.
	 * @return the set of the flag and the subtracted value.
	 */
	private EdgePixel operateAt ( MonoImage image, MonoImage bg_flag_image, int x, int y ) {
		EdgePixel edge_pixel = new EdgePixel();
		edge_pixel.value = image.getValue(x, y);

		// Only background pixels are processed.
		if (bg_flag_image.getValue(x, y) > 0.5) {
			int base_x = - (filter_size - 1) / 2;
			if (bg_flag_image.getValue(x, y) > 2.5)
				base_x = 0;
			else if (bg_flag_image.getValue(x, y) > 1.5)
				base_x = - filter_size + 1;

			// The average of background pixels are calculated for the current
			// line and 10 lines above, and stored in the array.
			Array row_average_array = new Array(11);
			int row_count = 0;
			for (int dy = - 10 ; dy <= 0 ; dy++) {
				if (y + dy >= 0) {
					// Calculates the average of background pixels.
					double bg_average = 0.0;
					int bg_count = 0;
					for (int dx = 0 ; dx < filter_size ; dx++) {
						if (bg_flag_image.getValueOnFlatExtension(x + base_x + dx, y + dy) > 0.5) {
							bg_average += image.getValueOnFlatExtension(x + base_x + dx, y + dy);
							bg_count++;
						}
					}
					if (bg_count > 0)
						row_average_array.set(row_count++, bg_average / (double)bg_count);
				}
			}

			// In order to calculate the deviation of the average values,
			// three lines containing any background pixels are required at least.
			// The current line must contain the background pixels.
			if (row_count > 3) {
				Statistics stat = new Statistics(row_average_array.cloneArray(row_count - 1));
				stat.calculate();

				double last_average = row_average_array.getValueAt(row_count - 2);
				double current_average = row_average_array.getValueAt(row_count - 1);

				// The edge is detected if the difference between the average of 
				// background pixels in the current line and that in the line above
				// is large enough, compared to the deviation of average values of
				// background pixels in each 10 lines above.
				// The flag is 2 if the difference from the line above is large, 
				// 1 if the difference is not large enough.
				if (current_average - last_average > 3.0 * stat.getDeviation())
					edge_pixel.flag = 2;
				else if (current_average - last_average > 2.0 * stat.getDeviation())
					edge_pixel.flag = 1;

				// Subtracts the difference. The average of the current line 
				// becomes the same as the average of average values of 
				// the 10 lines above.
				edge_pixel.value = image.getValue(x, y) + (stat.getAverage() - current_average);
			}
		}

		return edge_pixel;
	}

	/**
	 * The <code>EdgePixel</code> is a set of the flag and the 
	 * subtracted value.
	 */
	protected class EdgePixel {
		/**
		 * The flag.
		 */
		public byte flag = 0;

		/**
		 * The subtracted value.
		 */
		public double value = 0.0;

		/**
		 * Constructs an empty <code>EdgePixel</code>.
		 */
		public EdgePixel ( ) {
		}
	}
}
