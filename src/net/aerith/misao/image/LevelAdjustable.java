/*
 * @(#)LevelAdjustable.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image;

/**
 * The <code>LevelAdjustable</code> is an interface which has a
 * method to adjust the image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 September 16
 */

public interface LevelAdjustable {
	/**
	 * Adjusts the level of image.
	 * @param minimum the minimum level.
	 * @param maximum the maximum level.
	 */
	public abstract void adjustLevel ( double minimum, double maximum );

	/**
	 * Adjusts the level of all images.
	 * @param minimum the minimum level.
	 * @param maximum the maximum level.
	 */
	public abstract void adjustLevelAll ( double minimum, double maximum );
}
