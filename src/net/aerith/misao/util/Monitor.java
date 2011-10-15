/*
 * @(#)Monitor.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>Monitor</code> is an interface with a function to show
 * messages continuously given to this object.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 June 24
 */

public interface Monitor {
	/**
	 * Shows the specified message.
	 * @param string a one line message to show.
	 */
	public abstract void addMessage ( String string );

	/**
	 * Shows the specified messages.
	 * @param strings a set of messages to show.
	 */
	public abstract void addMessages ( String[] strings );

	/**
	 * Shows a separator.
	 */
	public abstract void addSeparator ( );
}
