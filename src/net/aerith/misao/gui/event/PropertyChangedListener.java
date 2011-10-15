/*
 * @(#)PropertyChangedListener.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.event;
import net.aerith.misao.util.star.Star;

/**
 * The <code>PropertyChangedListener</code> is a listener interface of
 * property change.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 November 7
 */

public interface PropertyChangedListener {
	/**
	 * Applies the property change.
	 */
	public abstract void propertyChanged ( );
}
