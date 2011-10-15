/*
 * @(#)ImageConversionOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ImageConversion;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.io.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.filter.*;

/**
 * The <code>ImageConversionOperation</code> represents an operation 
 * to create a new image by converting the image format and 
 * transforming the image.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class ImageConversionOperation extends MultiTaskOperation {
	/**
	 * The dialog to select a directory to create converted images 
	 * into.
	 */
	protected OutputDialog dialog;

	/**
	 * The HTML gallery.
	 */
	protected HtmlImageGallery gallery;

	/**
	 * Constructs an <code>ImageConversionOperation</code>.
	 * @param conductor the conductor of multi task operation.
	 */
	public ImageConversionOperation ( MultiTaskConductor conductor ) {
		this.conductor = conductor;
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		dialog = new OutputDialog();
		return dialog.show(conductor.getPane());
	}

	/**
	 * Notifies when the operation starts.
	 */
	protected void notifyStart ( ) {
		gallery = new HtmlImageGallery(new File(dialog.getPath()));
		try {
			gallery.open();
		} catch ( IOException exception ) {
		}

		super.notifyStart();
	}

	/**
	 * Notifies when the operation ends.
	 * @param exception the exception if an error occurs, or null if
	 * succeeded.
	 */
	protected void notifyEnd ( Exception exception ) {
		try {
			gallery.close();
		} catch ( IOException exception2 ) {
			if (exception == null)
				exception = exception2;
		}

		super.notifyEnd(exception);
	}

	/**
	 * Operates on one item. This is invoked from the conductor of 
	 * multi task operation.
	 * @param object the target object to operate.
	 * @exception Exception if an error occurs.
	 */
	public void operate ( Object object )
		throws Exception
	{
		ImageConversionTable.ImageRecord record = (ImageConversionTable.ImageRecord)object;

		MonoImage image = record.input_format.read();

		image = record.filter_set.convertImage(image);

		if (record.rescale_sbig) {
			int height = (int)((double)image.getSize().getHeight() * Astro.SBIG_RATIO + 0.5);
			image = new RescaleFilter(new Size(image.getSize().getWidth(), height)).operate(image);
		}

		if (record.output_transformation == record.TRANSFORMATION_SIZE) {
			if (record.output_size.getWidth() != image.getSize().getWidth()  ||
				record.output_size.getHeight() != image.getSize().getHeight()) {
				image = new RescaleFilter(record.output_size).operate(image);
			}
		} else {
			if (record.output_scale != 100) {
				int width = (int)((double)image.getSize().getWidth() * (double)record.output_scale / 100.0);
				int height = (int)((double)image.getSize().getHeight() * (double)record.output_scale / 100.0);
				image = new RescaleFilter(new Size(width, height)).operate(image);
			}
		}

		ImageGalleryElement element = new ImageGalleryElement(image, new File(record.output_image).toURI().toURL(), record.output_format);
		element.setTitle(record.getFile().getPath());
		gallery.addElement(element);
	}
}
