/*
 * @(#)StarContainer.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util.star;

/**
 * The <code>StarContainer</code> represents a container of some 
 * stars.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 June 24
 */

public interface StarContainer {
	/**
	 * Gets the number of content stars.
	 * @return the number of content stars.
	 */
	public int getStarCount();

	/**
	 * Gets a star at the specified index.
	 * @param index the index.
	 * @return the star at the specified index.
	 */
	public Star getStarAt ( int index );
}
