/*
 * @(#)PSF.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image;
import java.awt.Rectangle;
import net.aerith.misao.util.*;

/**
 * The <code>PSF</code> represents a point spread function.
 * <p>
 * The PSF is represented as a square image whose width and height is
 * odd. So the central pixel represents the peak of the PSF.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 February 26
 */

public class PSF {
	/**
	 * The PSF image.
	 */
	protected MonoImage psf_image = null;

	/**
	 * Constructs a <code>PSF</code> from an image.
	 * @param source_image    the source image.
	 * @param center_position the center (x,y) of a star.
	 * @param rectangle       the rectangle to extract.
	 */
	public PSF ( MonoImage source_image, Position center_position, Rectangle rectangle ) {
		int width = (int)(rectangle.getWidth() / 2) * 2 + 1;
		int height = (int)(rectangle.getHeight() / 2) * 2 + 1;
		if (width > height)
			width = height;

		DoubleBuffer buffer = new DoubleBuffer(new Size(width, width));
		psf_image = new MonoImage(buffer);

		int y2 = 0;
		for (int y = - width / 2 ; y <= width / 2 ; y++, y2++) {
			int x2 = 0;
			for (int x = - width / 2 ; x <= width / 2 ; x++, x2++) {
				double fx = center_position.getX() + (double)x;
				double fy = center_position.getY() + (double)y;
				double value = source_image.getAveragedValue(fx, fy);
				psf_image.setValue(x2, y2, value);
			}
		}
	}

	/**
	 * Gets the PSF image.
	 * @return the PSF image.
	 */
	public MonoImage getMonoImage ( ) {
		return psf_image;
	}

	/**
	 * Compounds the specified PSF to this PSF. Note that ths size of 
	 * the PSF image is reduced when the specified PSF is smaller.
	 * @param psf another PSF.
	 */
	public void compound ( PSF psf ) {
		int width = psf_image.getSize().getWidth();
		int width2 = psf.psf_image.getSize().getWidth();

		// Reduces the size.
		if (width > width2) {
			int dw = width - width2;
			width = width2;

			DoubleBuffer buffer = new DoubleBuffer(new Size(width, width));
			MonoImage new_image = new MonoImage(buffer);

			int y2 = dw / 2;
			for (int y = 0 ; y < width ; y++, y2++) {
				int x2 = dw / 2;
				for (int x = 0 ; x < width ; x++, x2++) {
					new_image.setValue(x, y, psf_image.getValue(x2, y2));
				}
			}

			psf_image = new_image;
		}

		// Adds the pixel values.
		int dw = width2 - width;
		int y2 = dw / 2;
		for (int y = 0 ; y < width ; y++, y2++) {
			int x2 = dw / 2;
			for (int x = 0 ; x < width ; x++, x2++) {
				double value = psf_image.getValue(x, y);
				value += psf.psf_image.getValue(x2, y2);
				psf_image.setValue(x, y, value);
			}
		}
	}
}
