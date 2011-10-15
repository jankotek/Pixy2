/*
 * @(#)PrintStreamMonitor.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.io.*;

/**
 * The <code>ProintStreamMonitor</code> is a <code>Monitor</code> to
 * output messages to the specified <code>PrintStream</code>.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 October 20
 */

public class PrintStreamMonitor implements Monitor {
	/**
	 * The stream to output to.
	 */
	private PrintStream stream;

	/**
	 * Constructs a <code>PrintStreamMonitor</code> with a 
	 * <code>PrintStream</code> to output to.
	 * @param stream the stream to output to.
	 */
	public PrintStreamMonitor ( PrintStream stream ) {
		this.stream = stream;
	}

	/**
	 * Shows the specified message.
	 * @param string a one line message to show.
	 */
	public void addMessage ( String string ) {
		stream.println(string);
	}

	/**
	 * Shows the specified messages.
	 * @param strings a set of messages to show.
	 */
	public void addMessages ( String[] strings ) {
		for (int i = 0 ; i < strings.length ; i++)
			stream.println(strings[i]);
	}

	/**
	 * Shows a separator.
	 */
	public void addSeparator ( ) {
		stream.println("");
	}
}
