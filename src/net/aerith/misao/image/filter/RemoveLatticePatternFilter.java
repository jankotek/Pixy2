/*
 * @(#)RemoveLatticePatternFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>RemoveLatticePatternFilter</code> is an image processing 
 * filter to remove lattice pattern from the specified image. The 
 * result is stored in the new image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 17
 */

public class RemoveLatticePatternFilter extends Filter {
	/**
	 * Constructs a filter.
	 */
	public RemoveLatticePatternFilter ( ) {
	}

	/**
	 * Operates the image processing filter and creates the new image
	 * buffer.
	 * @param image the original image to process.
	 * @return the new image buffer.
	 */
	public MonoImage operate ( MonoImage image ) {
		Size size = image.getSize();
		FloatBuffer buffer = new FloatBuffer(size);

		for (int x = 0 ; x < size.getWidth() ; x++) {
			Array array = new Array(size.getHeight());

			for (int y = 0 ; y < size.getHeight() ; y++)
				array.set(y, image.getValue(x, y));

			array.sortAscendant();
			double median = array.getValueAt(size.getHeight() / 2);

			for (int y = 0 ; y < size.getHeight() ; y++)
				buffer.set(x, y, (float)(image.getValue(x, y) - median));
		}

		for (int y = 0 ; y < size.getHeight() ; y++) {
			Array array = new Array(size.getWidth());

			for (int x = 0 ; x < size.getWidth() ; x++)
				array.set(x, buffer.getValue(x, y));

			array.sortAscendant();
			double median = array.getValueAt(size.getWidth() / 2);

			for (int x = 0 ; x < size.getWidth() ; x++)
				buffer.set(x, y, (float)(buffer.get(x, y) - median));
		}

		return new MonoImage(buffer);
	}
}
