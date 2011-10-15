/*
 * @(#)Size.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>Size</code> represents a size which consists of width and
 * height. Both width and height must be 0 or positive. In the case of 
 * negative value is given to set, it is converted to 0.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 January 28
 */

public class Size {
	/**
	 * The width value.
	 */
	private int width = 0;

	/**
	 * The height value.
	 */
	private int height = 0;

	/**
	 * Constructs a <code>Size</code>. Both width and height are set as 0.
	 */
	public Size ( ) {
		width = height = 0;
	}

	/**
	 * Constructs a <code>Size</code> with widht and height value.
	 * @param initial_width  the initial width value.
	 * @param initial_height the initial height value.
	 */
	public Size ( int initial_width, int initial_height ) {
		setWidth(initial_width);
		setHeight(initial_height);
	}

	/**
	 * Gets the width value.
	 * @return the width value.
	 */
	public int getWidth ( ) {
		return width;
	}

	/**
	 * Sets the width value.
	 * @param new_width the new width value.
	 */
	public void setWidth ( int new_width ) {
		if (new_width < 0)
			new_width = 0;
		width = new_width;
	}

	/**
	 * Gets the height value.
	 * @return the height value.
	 */
	public int getHeight ( ) {
		return height;
	}

	/**
	 * Sets the height value.
	 * @param new_height the new height value.
	 */
	public void setHeight ( int new_height ) {
		if (new_height < 0)
			new_height = 0;
		height = new_height;
	}

	/**
	 * Returns a raw string representation of the state of this object,
	 * for debugging use. It should be invoked from <code>toString</code>
	 * method of the subclasses.
	 * @return a string representation of the state of this object.
	 */
	protected String paramString ( ) {
		return "width=" + width + ",height=" + height;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * for debugging use.
	 * @return a string representation of the state of this object.
	 */
	public String toString ( ) {
		return getClass().getName() + "[" + paramString() + "]";
	}
}
