/*
 * @(#)DefaultBackgroundEstimator.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy.image_processing;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>DefaultBackgroundEstimator</code> is a class to estimate
 * the background mean value and deviation. It assumes that the image
 * is flat.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class DefaultBackgroundEstimator extends Operation {
	/**
	 * The image where to detect stars.
	 */
	protected MonoImage image;

	/**
	 * The background mean value.
	 */
	protected double background_value = 0.0;

	/**
	 * The background deviation.
	 */
	protected double background_deviation = 1.0;

	/**
	 * The number of areas to estimate the background.
	 */
	protected int area_count = 50;

	/**
	 * The filter size to estimate the background.
	 */
	protected int filter_size = 10;

	/**
	 * Constructs a <code>DefaultBackgroundEstimator</code> with an
	 * image object.
	 * @param image the image.
	 */
	public DefaultBackgroundEstimator ( MonoImage image ) {
		this.image = image;
	}

	/**
	 * Gets the background mean value.
	 * @return the background mean value.
	 */
	public double getBackground ( ) {
		return background_value;
	}

	/**
	 * Gets the background deviation.
	 * @return the background deviation.
	 */
	public double getBackgroundDeviation ( ) {
		return background_deviation;
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
		if (image != null) {
			Random random = new Random();

			Array background_array = new Array(area_count);
			Array deviation_array = new Array(area_count);
			Array array = new Array(filter_size * filter_size);

			for (int i = 0 ; i < area_count ; i++) {
				int x = random.nextInt(image.getSize().getWidth());
				int y = random.nextInt(image.getSize().getHeight());

				int count = 0;
				for (int dy = - (filter_size - 1) / 2 ; dy <= filter_size / 2 ; dy++) {
					for (int dx = - (filter_size - 1) / 2 ; dx <= filter_size / 2 ; dx++) {
						array.set(count++, image.getValueOnFlatExtension(x + dx, y + dy));
					}
				}

				Statistics stat = new Statistics(array);
				stat.calculate();

				background_array.set(i, stat.getAverage());
				deviation_array.set(i, stat.getDeviation());
			}

			background_array.sortAscendant();
			deviation_array.sortAscendant();

			background_value = background_array.getValueAt(area_count / 2);
			background_deviation = deviation_array.getValueAt(area_count / 2);
		}
	}
}
