/*
 * @(#)ChartEditionListener.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.event;
import net.aerith.misao.util.star.StarList;

/**
 * The <code>ChartEditionListener</code> is a listener interface of
 * star chart edition.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public interface ChartEditionListener {
	/**
	 * Invoked when stars are added to the chart.
	 * @param list the list of stars added to the chart.
	 */
	public abstract void starsAdded ( StarList list );
}
