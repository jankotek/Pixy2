/*
 * @(#)StreamSkipTimer.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.io.*;

/**
 * The <code>StreamSkipTimer</code> is a class to skip some bytes for 
 * the specified input stream or reader within the specified time. 
 * When the skip method cannot skip the specified bytes within the 
 * specified time, it throws an IOException.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 May 18
 */

public class StreamSkipTimer {
	/**
	 * The input stream.
	 */
	protected DataInputStream stream = null;

	/**
	 * The reader.
	 */
	protected BufferedReader reader = null;

	/**
	 * The time to wait in milli second.
	 */
	protected long time_millis = 0;

	/**
	 * The time when the skip method returns 0 for the first time.
	 */
	protected long start_time = -1;

	/**
	 * Constructs a <code>StreamSkipTimer</code>.
	 * @param stream      the input stream.
	 * @param time_millis the time to wait for the skip method in 
	 * milli second.
	 */
	public StreamSkipTimer ( DataInputStream stream, long time_millis ) {
		this.stream = stream;
		this.time_millis = time_millis;
	}

	/**
	 * Constructs a <code>StreamSkipTimer</code>.
	 * @param reader      the reader.
	 * @param time_millis the time to wait for the skip method in 
	 * milli second.
	 */
	public StreamSkipTimer ( BufferedReader reader, long time_millis ) {
		this.reader = reader;
		this.time_millis = time_millis;
	}

	/**
	 * Skips the specified bytes.
	 * @param bytes the bytes to skip.
	 * @exception IOException if the skip method cannot skip the 
	 * specified bytes within the specified time.
	 */
	public void skip ( long bytes )
		throws IOException
	{
		long skip_bytes = bytes;
		long skipped = 0;

		if (bytes == 0)
			return;

		start_time = -1;

		while (true) {
			if (stream != null)
				skipped = stream.skip(skip_bytes);
			if (reader != null)
				skipped = reader.skip(skip_bytes);
				
			if (skipped == 0) {
				if (start_time < 0) {
					start_time = System.currentTimeMillis();
				} else {
					long t = System.currentTimeMillis();
					if (t - start_time >= time_millis)
						throw new IOException();
				}
			} else {
				start_time = -1;

				skip_bytes -= skipped;
				if (skip_bytes == 0)
					break;
			}
		}
	}
}
