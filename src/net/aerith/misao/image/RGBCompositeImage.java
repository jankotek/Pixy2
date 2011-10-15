/*
 * @(#)RGBCompositeImage.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;

/**
 * The <code>RGBCompositeImage</code> is a set of three monochrome 
 * images, which represents a composed RGB image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 September 16
 */

public class RGBCompositeImage implements ImageContent {
	/**
	 * The R image.
	 */
	protected MonoImage R_image;

	/**
	 * The G image.
	 */
	protected MonoImage G_image;

	/**
	 * The B image.
	 */
	protected MonoImage B_image;

	/**
	 * The image level and statistics of R image.
	 */
	protected LevelAdjustmentSet R_stat;

	/**
	 * The image level and statistics of G image.
	 */
	protected LevelAdjustmentSet G_stat;

	/**
	 * The image level and statistics of B image.
	 */
	protected LevelAdjustmentSet B_stat;

	/**
	 * Constructs an <code>RGBCompositeImage</code>.
	 * @param initial_R_image the R image.
	 * @param initial_G_image the G image.
	 * @param initial_B_image the B image.
	 */
	public RGBCompositeImage ( MonoImage initial_R_image, MonoImage initial_G_image, MonoImage initial_B_image ) {
		R_image = initial_R_image;
		G_image = initial_G_image;
		B_image = initial_B_image;
		R_stat = new LevelAdjustmentSet(R_image);
		G_stat = new LevelAdjustmentSet(G_image);
		B_stat = new LevelAdjustmentSet(B_image);
	}

	/**
	 * Gets the image size.
	 * @return the image size.
	 */
	public Size getSize ( ) {
		return R_image.getSize();
	}

	/**
	 * Gets the R image.
	 * @return the R image.
	 */
	public MonoImage getRImage ( ) {
		return R_image;
	}

	/**
	 * Gets the G image.
	 * @return the G image.
	 */
	public MonoImage getGImage ( ) {
		return G_image;
	}

	/**
	 * Gets the B image.
	 * @return the B image.
	 */
	public MonoImage getBImage ( ) {
		return B_image;
	}

	/**
	 * Gets the image level and statistics of the R image.
	 * @return the image level and statistics of the R image.
	 */
	public LevelAdjustmentSet getRImageLevelAdjustmentSet ( ) {
		return R_stat;
	}

	/**
	 * Gets the image level and statistics of the G image.
	 * @return the image level and statistics of the G image.
	 */
	public LevelAdjustmentSet getGImageLevelAdjustmentSet ( ) {
		return G_stat;
	}

	/**
	 * Gets the image level and statistics of the B image.
	 * @return the image level and statistics of the B image.
	 */
	public LevelAdjustmentSet getBImageLevelAdjustmentSet ( ) {
		return B_stat;
	}

	/**
	 * Sets the image level and statistics of the R image.
	 * @param stat the image level and statistics of the R image.
	 */
	public void setRImageLevelAdjustmentSet ( LevelAdjustmentSet stat ) {
		R_stat = stat;
	}

	/**
	 * Sets the image level and statistics of the G image.
	 * @param stat the image level and statistics of the G image.
	 */
	public void setGImageLevelAdjustmentSet ( LevelAdjustmentSet stat ) {
		G_stat = stat;
	}

	/**
	 * Sets the image level and statistics of the B image.
	 * @param stat the image level and statistics of the B image.
	 */
	public void setBImageLevelAdjustmentSet ( LevelAdjustmentSet stat ) {
		B_stat = stat;
	}

	/**
	 * Creates an <code>java.awt.Image</code>. The range of pixel
	 * values is expanded so that the minimum value becomes 0 and the
	 * maximum value becomes 255.
	 * @return an <code>java.awt.Image</code>.
	 */
	public Image getImage ( ) {
		Size size = getSize();
		int pixels[] = new int[size.getWidth() * size.getHeight()];
		int ptr = 0;
		for (int y = 0 ; y < size.getHeight() ; y++) {
			for (int x = 0 ; x < size.getWidth() ; x++) {
				int R_value = (int)((R_image.getValue(x, y) - R_stat.current_minimum) / (R_stat.current_maximum - R_stat.current_minimum) * 256);
				if (R_value < 0)
					R_value = 0;
				if (R_value > 255)
					R_value = 255;
				int G_value = (int)((G_image.getValue(x, y) - G_stat.current_minimum) / (G_stat.current_maximum - G_stat.current_minimum) * 256);
				if (G_value < 0)
					G_value = 0;
				if (G_value > 255)
					G_value = 255;
				int B_value = (int)((B_image.getValue(x, y) - B_stat.current_minimum) / (B_stat.current_maximum - B_stat.current_minimum) * 256);
				if (B_value < 0)
					B_value = 0;
				if (B_value > 255)
					B_value = 255;
				pixels[ptr] = (255 << 24) | (R_value << 16) | (G_value << 8) | B_value;
				ptr++;
			}
		}
		Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(size.getWidth(), size.getHeight(), pixels, 0, size.getWidth()));
		return image;
	}
}
