/*
 * @(#)DefaultLevelAdjustmentSet.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.filter.*;

/**
 * The <code>DefaultLevelAdjustmentSet</code> represents a set of the 
 * original statistics and the current level to view of an image.
 * <p>
 * In this constructor, the minimum and maximum value is set properly
 * so that the stars on the image will be visible by default on the
 * screen. In order to set the value properly even in the case of an 
 * image half of whose area is filled with illegal value like 0, it 
 * calculates the minimum and maximum value in the following steps:
 * <ol>
 *   <li>Creates a histogram.</li>
 *   <li>Smoothes the histogram by median filter.</li>
 *   <li>Creates a mini image based on the smoothed histogram.</li>
 *   <li>Calculates the average and standard deviation of pixel values.</li>
 *   <li>Set the minimum as <i>average - 1.0 * deviation</i>, the 
 *       maximum as <i>average + 4.25 * deviation</i>.
 * </ol>
 * <p>
 * Sometimes there may be a few extremely high-value pixels. In that 
 * case, the division step of the histogram will be very rough and 
 * the average and standard deviation calculated in the above steps
 * will be inaccurate. So in order to avoid the influence of rare 
 * high-value pixels, only values of common pixels are used to create
 * the histogram. Actually, it calculates the minimum and maximum 
 * value in the following steps:
 * <ol>
 *   <li>Creates a histogram.</li>
 *   <li>Smoothes the histogram by median filter.</li>
 *   <li>Estimates the range of common pixels.</li>
 *   <li>Creates a histogram using only the common pixels.</li>
 *   <li>Smoothes the histogram by median filter.</li>
 *   <li>Creates a mini image based on the smoothed histogram.</li>
 *   <li>Calculates the average and standard deviation of pixel values.</li>
 *   <li>Set the minimum as <i>average - 1.0 * deviation</i>, the 
 *       maximum as <i>average + 4.25 * deviation</i>.
 * </ol>
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 25
 */

public class DefaultLevelAdjustmentSet extends LevelAdjustmentSet {
	/**
	 * Constructs a <code>DefaultLevelAdjustmentSet</code> based on 
	 * the specified image.
	 * @param mono_image the image.
	 */
	public DefaultLevelAdjustmentSet ( MonoImage mono_image ) {
		super(mono_image);

		try {
			double range_min = original_statistics.getMin();
			double range_max = original_statistics.getMax();

			MonoImage histogram_image = null;
			double scale = 1.0;
			int total_count = 0;
			Statistics statistics = original_statistics;

			// Rejects illegal pixels of same value.
			{
				// Creates a histogram.
				Histogram histogram = new Histogram(mono_image, range_min, range_max, 10000);

				// Searches the value of most pixels.
				int max_count = 0;
				int max_index = -1;
				for (int i = 1 ; i <= 10000 ; i++) {
					if (max_count < (int)histogram.getValueAt(i)) {
						max_count = (int)histogram.getValueAt(i);
						max_index = i;
					}
				}
				double most_min = (range_max - range_min) / 10000.0 * (double)(max_index - 1) + range_min;
				double most_max = (range_max - range_min) / 10000.0 * (double)max_index + range_min;

				// Creates a new image without illegal values.
				int image_size = mono_image.getSize().getWidth() * mono_image.getSize().getHeight();
				MonoImage new_image = new MonoImage(new FloatBuffer(new Size(image_size - max_count, 1)));

				int index = 0;
				for (int i = 0 ; i < image_size ; i++) {
					double value = mono_image.getValueAt(i);
					if (value < most_min  ||  value >= most_max) {
						new_image.setValue(index, 0, value);
						index++;
					}
				}

				// Initializes again with the new image.
				mono_image = new_image;

				original_statistics = new Statistics(mono_image);
				original_statistics.calculate();
				current_minimum = original_statistics.getMin();
				current_maximum = original_statistics.getMax();

				range_min = original_statistics.getMin();
				range_max = original_statistics.getMax();

				statistics = original_statistics;
			}

			// In order to avoid the influence of rare high-value pixels,
			// it estimates the range of common pixels at first, then
			// calculates the minimum and maximum value.
			for (int loop = 0 ; loop < 2 ; loop++) {
				// Creates a histogram.
				Histogram histogram = new Histogram(mono_image, range_min, range_max, 100);

				// Smoothes the histogram using a median filter.
				histogram_image = new MonoImage(new FloatBuffer(new Size(102, 1)));
				for (int i = 0 ; i < 102 ; i++)
					histogram_image.setValue(i, 0, (double)histogram.getValueAt(i));
				MedianFilter filter = new MedianFilter(3);
				filter.operate(histogram_image);

				// Cuts off the low count pixels in histogram.
				int max_count = 0;
				for (int i = 1 ; i <= 100 ; i++) {
					if (max_count < (int)histogram_image.getValue(i, 0))
						max_count = (int)histogram_image.getValue(i, 0);
				}
				int threshold = (int)Math.sqrt(max_count);
				if (threshold < 2)
					threshold = 2;
				for (int i = 1 ; i <= 100 ; i++) {
					if ((int)histogram_image.getValue(i, 0) < threshold)
						histogram_image.setValue(i, 0, 0);
				}

				// Cuts off the illegal high value pixels.
				for (int i = 40 ; i <= 90 ; i++) {
					// Searches the lowest series of sequential
					// 10 zero count pixels.
					boolean zero_flag = true;
					for (int j = 0 ; j < 10 ; j++) {
						if (histogram_image.getValue(i + j, 0) > 0)
							zero_flag = false;
					}

					if (zero_flag) {
						for (int j = i ; j < 102 ; j++)
							histogram_image.setValue(j, 0, 0);
						break;
					}
				}

				// Counts the number of pixels of the smoothed histogram.
				total_count = 0;
				for (int i = 1 ; i <= 100 ; i++) {
					int count = (int)histogram_image.getValue(i, 0);
					total_count += count;
				}
				if (total_count < 10)
					return;

				// Calculates the scale to reduce the histogram 
				// in order to create a mini image with 10000 pixels.
				scale = 10000.0 / (double)total_count;

				// In order to avoid the influence of rare high-value
				// pixels, here reduces the range of the histogram to 
				// only values of common pixels.
				if (loop == 0) {
					int min_index = -1;
					int max_index = -1;
					for (int i = 1 ; i <= 100 ; i++) {
						int count = (int)(histogram_image.getValue(i, 0) * scale);
						if (count >= 20) {
							if (min_index < 0)
								min_index = i;
							max_index = i;
						}
					}
					if (min_index < 0)
						min_index = 1;
					if (max_index < 0)
						max_index = 100;

					range_min = (original_statistics.getMax() - original_statistics.getMin()) / 100.0 * (double)(min_index - 1) + original_statistics.getMin();
					range_max = (original_statistics.getMax() - original_statistics.getMin()) / 100.0 * (double)max_index + original_statistics.getMin();
				}
			}

			// Counts the real number of pixels of the mini image.
			total_count = 0;
			int variation = 0;
			for (int i = 1 ; i <= 100 ; i++) {
				int count = (int)(histogram_image.getValue(i, 0) * scale);
				total_count += count;

				if (count > 0)
					variation++;
			}

			if (variation > 5) {
				// Creates a mini image based on the smoothed histogram.
				Array mini_image = new Array(total_count);
				double value = (range_max - range_min) / 100.0 / 2.0 + range_min;
				total_count = 0;
				for (int i = 1 ; i <= 100 ; i++) {
					int count = (int)(histogram_image.getValue(i, 0) * scale);
					for (int j = 0 ; j < count ; j++) {
						mini_image.set(total_count, value);
						total_count++;
					}
					value += (range_max - range_min) / 100.0;
				}

				statistics = new Statistics(mini_image);
				statistics.calculate();
			}

			current_minimum = statistics.getAverage() - statistics.getDeviation() * 1.0;
			current_maximum = statistics.getAverage() + statistics.getDeviation() * 4.25;

			if (current_minimum < original_statistics.getMin())
				current_minimum = original_statistics.getMin();
		} catch ( Exception exception ) {
		}
	}
}
