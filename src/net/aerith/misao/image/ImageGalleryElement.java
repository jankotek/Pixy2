/*
 * @(#)ImageGalleryElement.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.image.io.Format;
import net.aerith.misao.io.HtmlImageAnchor;

/**
 * The <code>ImageGalleryElement</code> represents an element in the
 * image gallery.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class ImageGalleryElement {
	/**
	 * The image. It is only in a single element.
	 */
	protected MonoImage image = null;

	/**
	 * The image URL. It is only in a single element.
	 */
	protected URL url = null;

	/**
	 * The image format. It is only in a single element.
	 */
	protected Format format = null;

	/**
	 * The list of image anchors. It is only in a single 
	 */
	protected ArrayList list_anchor = new ArrayList();

	/**
	 * The list of sub elements. It is only in a multi element.
	 */
	protected ImageGalleryElement[] sub_elements = null;

	/**
	 * The group title. It is only in a multi element.
	 */
	protected String group_title = null;

	/**
	 * The title.
	 */
	protected String title = "";

	/**
	 * The data.
	 */
	protected ArrayList data = new ArrayList();

	/**
	 * Constructs a single <code>ImageGalleryElement</code>.
	 * @param image  the image.
	 * @param url    the image URL.
	 * @param format the image format.
	 */
	public ImageGalleryElement ( MonoImage image, URL url, Format format ) {
		this.image = image;
		this.url = url;
		this.format = format;
	}

	/**
	 * Constructs a multi <code>ImageGalleryElement</code>.
	 * @param group_title  the group title.
	 * @param sub_elements the list of sub elements.
	 */
	public ImageGalleryElement ( String group_title, Vector sub_elements ) {
		this.group_title = group_title;

		this.sub_elements = new ImageGalleryElement[sub_elements.size()];
		for (int i = 0 ; i < sub_elements.size() ; i++)
			this.sub_elements[i] = (ImageGalleryElement)sub_elements.elementAt(i);
	}

	/**
	 * Returns true when the element is a single element.
	 */
	public boolean isSingle ( ) {
		return (image != null);
	}

	/**
	 * Gets the image. It is only in a single element.
	 * @return the image.
	 */
	public MonoImage getImage ( ) {
		return image;
	}

	/**
	 * Gets the image URL. It is only in a single element.
	 * @return the image URL.
	 */
	public URL getURL ( ) {
		return url;
	}

	/**
	 * Gets the image format. It is only in a single element.
	 * @return the image format.
	 */
	public Format getFormat ( ) {
		return format;
	}

	/**
	 * Gets the list of image anchors. It is only in a single element.
	 * @return the list of image anchors.
	 */
	public HtmlImageAnchor[] getImageAnchors ( ) {
		HtmlImageAnchor[] anchors = new HtmlImageAnchor[list_anchor.size()];
		return (HtmlImageAnchor[])list_anchor.toArray(anchors);
	}

	/**
	 * Adds an image anchor.
	 * @param anchor the image anchor.
	 */
	public void addImageAnchor ( HtmlImageAnchor anchor ) {
		list_anchor.add(anchor);
	}

	/**
	 * Gets the list of sub elements. It is only in a multi element.
	 * @return the list of sub elements.
	 */
	public ImageGalleryElement[] getElements ( ) {
		return sub_elements;
	}

	/**
	 * Gets the group title. It is only in a multi element.
	 * @return the group title.
	 */
	public String getGroupTitle ( ) {
		return group_title;
	}

	/**
	 * Gets the title.
	 * @return the title.
	 */
	public String getTitle ( ) {
		return title;
	}

	/**
	 * Sets the title.
	 * @param title the title.
	 */
	public void setTitle ( String title ) {
		this.title = title;
	}

	/**
	 * Gets the data.
	 * @return the data.
	 */
	public String[] getData ( ) {
		String[] data = new String[this.data.size()];
		return (String[])this.data.toArray(data);
	}

	/**
	 * Sets the data.
	 * @param data the list of data.
	 */
	public void setData ( Vector data ) {
		this.data = new ArrayList();
		for (int i = 0 ; i < data.size() ; i++)
			this.data.add(data.elementAt(i));
	}

	/**
	 * Adds a data.
	 * @param data the data.
	 */
	public void addData ( String data ) {
		this.data.add(data);
	}
}
