/*
 * @(#)RotateAtRightAnglesFilter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import net.aerith.misao.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>RotateAtRightAnglesFilter</code> is an image processing 
 * filter to rotate the image at right angles. The image is rotated
 * in clockwise direction. The result is stored in the new image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class RotateAtRightAnglesFilter extends Filter {
	/**
	 * The rotation angle in degree.
	 */
	protected double rotation_angle = 0.0;

	/**
	 * Constructs a filter.
	 */
	public RotateAtRightAnglesFilter ( ) {
	}

	/**
	 * Constructs a filter with rotation angle. The image is rotated 
	 * in clockwise direction.
	 * @param angle the angle to rotate in degree.
	 */
	public RotateAtRightAnglesFilter ( double angle ) {
		rotation_angle = angle;
	}

	/**
	 * Sets the angle to rotate in degree. The image is rotated in 
	 * clockwise direction.
	 * @param new_angle the angle to rotate in degree.
	 */
	public void setRotationAngle ( double new_angle ) {
		rotation_angle = new_angle;
	}

	/**
	 * Operates the image processing filter and creates the new image
	 * buffer.
	 * @param image the original image to process.
	 * @return the new image buffer.
	 */
	public MonoImage operate ( MonoImage image ) {
		int rotation = 0;

		double angle = rotation_angle;
		while (angle < -45) {
			rotation--;
			if (rotation < 0)
				rotation = 3;
			angle += 90.0;
		}
		while (angle >= 45) {
			rotation++;
			if (rotation >= 4)
				rotation = 0;
			angle -= 90.0;
		}

		if (rotation == 1) {
			// Rotates 90 degree to the clockwise direction.
			Size orig_size = image.getSize();
			Size new_size = new Size(orig_size.getHeight(), orig_size.getWidth());

			MonoImage new_image = image.cloneImage(new_size);

			for (int y = 0 ; y < new_size.getHeight() ; y++) {
				for (int x = 0 ; x < new_size.getWidth() ; x++) {
					double value = 0.0;

					try {
						value = image.getValue(y, orig_size.getHeight() - 1 - x);
					} catch ( IndexOutOfBoundsException exception ) {
					}

					new_image.setValue(x, y, value);
				}
			}

			return new_image;
		} else if (rotation == 2) {
			// Rotates 180 degree.
			Size size = image.getSize();
			MonoImage new_image = image.cloneImage();

			for (int y = 0 ; y < size.getHeight() ; y++) {
				for (int x = 0 ; x < size.getWidth() ; x++) {
					double value = 0.0;

					try {
						value = image.getValue(size.getWidth() - 1 - x, size.getHeight() - 1 - y);
					} catch ( IndexOutOfBoundsException exception ) {
					}

					new_image.setValue(x, y, value);
				}
			}

			return new_image;
		} else if (rotation == 3) {
			// Rotates 90 degree to the anti-clockwise direction.
			Size orig_size = image.getSize();
			Size new_size = new Size(orig_size.getHeight(), orig_size.getWidth());

			MonoImage new_image = image.cloneImage(new_size);

			for (int y = 0 ; y < new_size.getHeight() ; y++) {
				for (int x = 0 ; x < new_size.getWidth() ; x++) {
					double value = 0.0;

					try {
						value = image.getValue(orig_size.getWidth() - 1 - y, x);
					} catch ( IndexOutOfBoundsException exception ) {
					}

					new_image.setValue(x, y, value);
				}
			}

			return new_image;
		}

		return image.cloneImage();
	}
}
