/*
 * @(#)XmlImageLoader.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.pixy.image_loading;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.filter.*;
import net.aerith.misao.image.io.*;
import net.aerith.misao.util.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.io.FileManager;
import net.aerith.misao.io.filechooser.*;
import net.aerith.misao.pixy.Resource;

/**
 * The <code>XmlImageLoader</code> is a class to load an image file
 * recorded in the specified XML document.
 * <p>
 * It transforms the image properly as described int the XML document.
 * It puts the FITS image data in proper order, reverses the image
 * upside down, and rescales the SBIG ST-4/6 image.
 * <p>
 * Filters to be applied before transformation can be set.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class XmlImageLoader extends Operation {
	/**
	 * The image file to load.
	 */
	protected XmlImage xml_image = null;

	/*
	 * True in the case of the reversed image.
	 */
	protected boolean reversed_image = false;

	/**
	 * True in the case of the ST-4/6 image.
	 */
	protected boolean sbig_image = false;

	/**
	 * The image file to load.
	 */
	protected File file = null;

	/**
	 * The format of the image file.
	 */
	protected net.aerith.misao.image.io.Format format = null;

	/**
	 * The XML instruction document.
	 */
	protected XmlInstruction instruction = null;

	/**
	 * The XML information document.
	 */
	protected XmlInformation information = null;

	/**
	 * The list of filters to be applied before transformation.
	 */
	protected Vector filter_list = new Vector();

	/**
	 * The file manager.
	 */
	protected FileManager file_manager;

	/**
	 * The loaded image.
	 */
	protected MonoImage image = null;

	/**
	 * Constructs an <code>XmlImageLoader</code> with an 
	 * <code>XmlInstruction</code>.
	 * @param instruction  the XML instruction document.
	 * @param file_manager the file manager.
	 */
	public XmlImageLoader ( XmlInstruction instruction, FileManager file_manager ) {
		this.instruction = instruction;
		this.file_manager = file_manager;

		xml_image = (XmlImage)instruction.getImage();
		reversed_image = (instruction.getReversedImage() != null);
		sbig_image = (instruction.getSbigImage() != null);
	}

	/**
	 * Constructs an <code>XmlImageLoader</code> with an 
	 * <code>XmlInformation</code>.
	 * @param info         the XML information document.
	 * @param file_manager the file manager.
	 */
	public XmlImageLoader ( XmlInformation info, FileManager file_manager ) {
		this.information = info;
		this.file_manager = file_manager;

		xml_image = (XmlImage)info.getImage();
		reversed_image = (info.getReversedImage() != null);
		sbig_image = (info.getSbigImage() != null);
	}

	/**
	 * Sets the file and format to read. This is for the case the 
	 * image file recorded in the XML document cannot be read properly.
	 * @param file   the image file.
	 * @param format the image format.
	 */
	public void setFile ( File file, net.aerith.misao.image.io.Format format ) {
		this.file = file;
		this.format = format;
	}

	/**
	 * Adds a filter to be applied before transformation.
	 * @param filter the filter to be applied before transformation.
	 */
	public void addFilter ( Filter filter ) {
		filter_list.addElement(filter);
	}

	/**
	 * Gets the loaded image.
	 * @return the loaded image.
	 */
	public MonoImage getMonoImage ( ) {
		return image;
	}

	/**
	 * Returns true if the operation is ready to start.
	 * @return true if the operation is ready to start.
	 */
	public boolean ready ( ) {
		return true;
	}

	/**
	 * Operates.
	 * @exception Exception if an error occurs.
	 */
	public void operate ( )
		throws Exception
	{
		if (file == null) {
			if (instruction != null)
				file = instruction.getImageFile(file_manager);
			else
				file = information.getImageFile(file_manager);
			format = net.aerith.misao.image.io.Format.create(file, xml_image.getFormat());
		}

		DefaultImageLoader loader = new DefaultImageLoader(file, format);
		loader.addMonitor(monitor_set);
		loader.operate();
		image = loader.getMonoImage();

		if (image != null) {
			// Applies the filters before transformation.
			for (int i = 0 ; i < filter_list.size() ; i++) {
				Filter filter = (Filter)filter_list.elementAt(i);
				filter.setMonitor(monitor_set);
				image = filter.operate(image);
			}

			// The FITS image is loaded in the default order as described in
			// the configuration. If the order recorded in the XML document
			// is different, reverses the image. Note that the null in the 
			// XML document means Japanese order.
			if (format.isFits()) {
				String default_order = Resource.getDefaultFitsOrder();
				String xml_order = xml_image.getOrder();
				if (xml_order == null)
					xml_order = "japanese";

				if (default_order.equals(xml_order) == false)
					image.reverseVertically();

				monitor_set.addMessage("[Putting the image in order]");
				monitor_set.addMessage("Image data order: " + xml_order);
				monitor_set.addSeparator();
			}

			// In the case of the reversed image.
			if (reversed_image) {
				image.reverseVertically();

				monitor_set.addMessage("[Reversing upside down]");
				monitor_set.addSeparator();
			}

			// In the case of the ST-4/6 image.
			if (sbig_image) {
				int height = (int)((double)image.getSize().getHeight() * Astro.SBIG_RATIO + 0.5);
				image = new RescaleFilter(new Size(image.getSize().getWidth(), height)).operate(image);

				monitor_set.addMessage("[Rescaling ST-4/6 image]");
				monitor_set.addMessage("Size: " + image.getSize().getWidth() + " x " + image.getSize().getHeight());
				monitor_set.addSeparator();
			}
		}
	}
}
