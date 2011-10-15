/*
 * @(#)LevelAdjustmentSet.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image;
import net.aerith.misao.util.*;

/**
 * The <code>LevelAdjustmentSet</code> represents a set of the 
 * original statistics and the current level to view of an image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 September 16
 */

public class LevelAdjustmentSet {
	/**
	 * The statistics of the original image.
	 */
	public Statistics original_statistics;

	/**
	 * The current minimum pixel value.
	 */
	public double current_minimum;

	/**
	 * The current maximum pixel value.
	 */
	public double current_maximum;

	/**
	 * Constructs a <code>LevelAdjustmentSet</code> based on the
	 * specified image.
	 * @param mono_image the image.
	 */
	public LevelAdjustmentSet ( MonoImage mono_image ) {
		original_statistics = new Statistics(mono_image);
		original_statistics.calculate();
		current_minimum = original_statistics.getMin();
		current_maximum = original_statistics.getMax();
	}

	/**
	 * Constructs a <code>LevelAdjustmentSet</code> by copy. Actually,
	 * the original statistics is not copied because it should be read
	 * only.
	 * @param set the source level adjustment set.
	 */
	public LevelAdjustmentSet ( LevelAdjustmentSet set ) {
		original_statistics = set.original_statistics;
		current_minimum = set.current_minimum;
		current_maximum = set.current_maximum;
	}
}
