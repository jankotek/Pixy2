/*
 * @(#)StarClickListener.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.event;
import net.aerith.misao.util.star.StarList;

/**
 * The <code>StarClickListener</code> is a listener interface of star 
 * click event on chart.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public interface StarClickListener {
	/**
	 * Invoked when some stars are clicked.
	 * @param star_list the list of clicked stars.
	 */
	public abstract void starsClicked ( StarList star_list );
}
