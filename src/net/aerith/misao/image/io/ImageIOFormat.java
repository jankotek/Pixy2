/*
 * @(#)ImageIOFormat.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.io;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import net.aerith.misao.image.*;
import net.aerith.misao.util.Size;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * The <code>ImageIOFormat</code> is an abstract class to read and save
 * image file supported by JVM. It is just an access interface to the
 * file.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public abstract class ImageIOFormat extends Format implements Bitmap {

        /**
        * Gets the MIME type ID.
        * @return the MIME type ID.
        */
        public abstract String getMimeType ( );


	/**
	 * Reads image file and creates image buffer. The url of the image
	 * file must be set previously.
	 * @return the monochrome image buffer.
	 * @exception IOException if I/O error occurs.
	 * @exception UnsupportedBufferTypeException if the data type is 
	 * unsupported.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	public MonoImage read ( )
		throws IOException, UnsupportedBufferTypeException, UnsupportedFileTypeException
	{
		if (url == null)
			throw new IOException();

		try {

                    BufferedImage img = ImageIO.read(url);
	            int w = img.getWidth();
                    int h = img.getHeight();
                    ByteBuffer buffer = new ByteBuffer(new Size(w, h));

		    for (int y = 0 ; y < h ; y++) {
			for (int x = 0 ; x < w ; x++) {
				int value = img.getRGB(x,y);
				int r = (value >> 16) & 255;
				int g = (value >> 8) & 255;
				int b = value & 255;
				int gray = MonoImage.convertRGBToGray(r, g, b);
				buffer.setValue(x, y, gray);
			}
		    }

		    return new MonoImage(buffer);

		} catch ( Exception exception ) {
		}

		throw new UnsupportedBufferTypeException();
	}

	/**
	 * Saves image buffer into an image file. The url of the image file 
	 * must be set previously.
	 * @param image the monochrome image buffer to save.
	 * @exception IOException if I/O error occurs.
	 * @exception UnsupportedBufferTypeException if the data type is 
	 * unsupported.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	public void save ( MonoImage image )
		throws IOException, UnsupportedBufferTypeException, UnsupportedFileTypeException
	{
		if (image.getBufferType() != MonoImage.TYPE_BYTE)
			throw new UnsupportedBufferTypeException();

		save(image.getImage(0, 256));
	}

	/**
	 * Saves an image buffer into an image file, using the specified
	 * <code>LevelAdjustmentSet</code>. The url of the image file 
	 * must be set previously.
	 * @param image the monochrome image buffer to save.
	 * @exception IOException if I/O error occurs.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	public void save ( MonoImage image, LevelAdjustmentSet set )
		throws IOException, UnsupportedFileTypeException
	{
		try {
			save(image.getImage(set.current_minimum, set.current_maximum));
		} catch ( UnsupportedBufferTypeException exception ) {
			// Never happens.
		}
	}

	/**
	 * Saves a <code>java.awt.Image</code> into an image file.
	 * @param image the <code>java.awt.Image</code>.
	 * @exception IOException if I/O error occurs.
	 * @exception UnsupportedBufferTypeException if the data type is 
	 * unsupported.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	private void save ( Image image )
		throws IOException, UnsupportedBufferTypeException, UnsupportedFileTypeException
	{
		if (url == null)
			throw new IOException();

		DataOutputStream stream = null;
		if (url.getProtocol().equals("file")) {
			stream = new DataOutputStream(new FileOutputStream(new File(url.getFile())));
		} else {
			URLConnection uc = url.openConnection();
			uc.setDoOutput(true);
			stream = new DataOutputStream(uc.getOutputStream());
		}

		try {
                        BufferedImage image2 = toBufferedImage(image);
                        if(!ImageIO.write(image2, getName(),stream))
                            throw new Exception();

        		stream.close();

			return;
		} catch ( Exception exception ) {
		}

		stream.close();

		throw new UnsupportedFileTypeException(url, getName(), "save");
	}


    /**
     * This method returns a buffered image with the contents of an image
     * comes from
     * http://www.exampledepot.com/egs/java.awt.image/Image2Buf.html
     */

    private static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();


        // Create a buffered image using the default color model
        int type = BufferedImage.TYPE_INT_RGB;
        BufferedImage   bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }
}
