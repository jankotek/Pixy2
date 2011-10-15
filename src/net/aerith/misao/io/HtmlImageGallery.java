/*
 * @(#)HtmlImageGallery.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.io;
import java.io.*;
import java.net.*;
import java.util.*;
import net.aerith.misao.image.*;
import net.aerith.misao.image.io.Format;
import net.aerith.misao.image.io.Bitmap;

/**
 * The <code>HtmlImageGallery</code> represents a HTML gallery of 
 * images to create on the file system.
 * <p>
 * Note that only the file name in the specified image URL in the 
 * element is used. Any protocol or leading path is ignored.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 17
 */

public class HtmlImageGallery {
	/**
	 * The folder to create the gallery.
	 */
	protected File folder;

	/**
	 * True when to create the index.html.
	 */
	protected boolean create_index = true;

	/**
	 * The print stream of index.html.
	 */
	protected PrintStream ps_index = null;

	/**
	 * The number of elements added to this gallery.
	 */
	protected int number_of_elements = 0;

	/**
	 * True when to insert the anchor to the next group.
	 */
	protected boolean next_anchor = false;

	/**
	 * Constructs a <code>HtmlImageGallery</code> in the specified 
	 * folder.
	 * @param folder the folder to create the gallery.
	 */
	public HtmlImageGallery ( File folder ) {
		this.folder = folder;
	}

	/**
	 * Sets the flag to to create the index.html.
	 * @param flag the flag to to create the index.html.
	 */
	public void setCreateIndex ( boolean flag ) {
		create_index = flag;
	}

	/**
	 * Sets the flag to insert the anchor to the next group.
	 * @param flag the flag to to insert the anchor to the next group.
	 */
	public void setInsertNextAnchor ( boolean flag ) {
		next_anchor = flag;
	}

	/**
	 * Opens the gallery.
	 * @exception IOException if I/O error occurs.
	 */
	public void open ( )
		throws IOException
	{
		if (folder.exists() == false)
			folder.mkdirs();

		if (create_index) {
			File file = new File(FileManager.unitePath(folder.getAbsolutePath(), "index.html"));
			ps_index = new PrintStream(new FileOutputStream(file));

			ps_index.println("<HTML><BODY BGCOLOR=\"#ffffff\">");
		}

		number_of_elements = 0;
	}

	/**
	 * Closes the gallery.
	 * @exception IOException if I/O error occurs.
	 */
	public void close ( )
		throws IOException
	{
		if (create_index) {
			ps_index.println("</BODY></HTML>");
			ps_index.close();
		}
	}

	/**
	 * Adds an image element.
	 * @param element the image element.
	 * @exception IOException if I/O error occurs.
	 * @exception FileNotFoundException if the file does not exist.
	 * @exception UnsupportedBufferTypeException if the data type is 
	 * unsupported.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	public void addElement ( ImageGalleryElement element )
		throws IOException, FileNotFoundException, UnsupportedBufferTypeException, UnsupportedFileTypeException
	{
		if (create_index) {
			if (number_of_elements > 0)
				ps_index.println("<HR>");

			if (element.getTitle().length() > 0)
				ps_index.print("<A NAME=\"" + number_of_elements + "\"><H1>" + element.getTitle());
				if (next_anchor) {
					int next_number = number_of_elements + 1;
					ps_index.print(" &nbsp; <FONT SIZE=\"-1\"><A HREF=\"#" + next_number + "\">[Next]</A></FONT>");
				}
				ps_index.println("</H1></A>");

			ps_index.println("<P>");
		}

		outputElement(element, "");

		if (create_index) {
			ps_index.println("</P>");

			if (element.getData() != null) {
				ps_index.println("<PRE>");

				String[] data = element.getData();
				for (int i = 0 ; i < data.length ; i++)
					ps_index.println(data[i]);

				ps_index.println("</PRE>");
			}
		}

		number_of_elements++;
	}

	/**
	 * Outputs an image element.
	 * @param element  the image element.
	 * @param sub_path the sub folder path.
	 * @exception IOException if I/O error occurs.
	 * @exception FileNotFoundException if the file does not exist.
	 * @exception UnsupportedBufferTypeException if the data type is 
	 * unsupported.
	 * @exception UnsupportedFileTypeException if the file type is 
	 * unsupported.
	 */
	private void outputElement ( ImageGalleryElement element, String sub_path )
		throws IOException, FileNotFoundException, UnsupportedBufferTypeException, UnsupportedFileTypeException
	{
		if (element.isSingle()) {
			MonoImage image = element.getImage();
			URL url = element.getURL();
			Format format = element.getFormat();

			String filename = new File(url.getFile()).getName();
			if (sub_path.length() > 0)
				filename = FileManager.unitePath(sub_path, new File(url.getFile()).getName());

			if (create_index)
				ps_index.println("<IMG SRC=\"" + filename.replace('\\', '/') + "\" ALT=\"\" WIDTH=\"" + image.getSize().getWidth() + "\" HEIGHT=\"" + image.getSize().getHeight() + "\">");

			File file = new File(FileManager.unitePath(folder.getAbsolutePath(), filename));
			format.setURL(file.toURL());

			LevelAdjustmentSet set = new DefaultLevelAdjustmentSet(image);

			((Bitmap)format).save(image, set);

			// Outputs image anchors.
			HtmlImageAnchor[] anchors = element.getImageAnchors();
			for (int i = 0 ; i < anchors.length ; i++) {
				try {
					filename = new File(anchors[i].getURL().getFile()).getName();
					if (sub_path.length() > 0)
						filename = FileManager.unitePath(sub_path, new File(anchors[i].getURL().getFile()).getName());

					element.addData("<A HREF=\"" + filename.replace('\\', '/') + "\">" + anchors[i].getText() + "</A>");

					file = new File(FileManager.unitePath(folder.getAbsolutePath(), filename));
					format = anchors[i].getFormat();
					format.setURL(file.toURL());

					format.save(anchors[i].getImage());
				} catch ( Exception exception ) {
					// Ignores.
				}
			}
		} else {
			if (sub_path.length() == 0)
				sub_path = element.getGroupTitle();
			else
				sub_path = FileManager.unitePath(sub_path, element.getGroupTitle());

			File file = new File(FileManager.unitePath(folder.getAbsolutePath(), sub_path));
			if (file.exists() == false  ||  file.isDirectory() == false)
				file.mkdirs();

			if (create_index)
				ps_index.println("<TABLE><TR>");

			ImageGalleryElement[] elements = element.getElements();
			for (int i = 0 ; i < elements.length ; i++) {
				if (create_index)
					ps_index.println("<TD>");

				outputElement(elements[i], sub_path);

				if (create_index)
					ps_index.println("</TD>");
			}

			if (create_index) {
				ps_index.println("</TR><TR>");

				for (int i = 0 ; i < elements.length ; i++) {
					ps_index.println("<TD>");

					if (elements[i].getData() != null) {
						ps_index.println("<PRE>");

						String[] data = elements[i].getData();
						for (int j = 0 ; j < data.length ; j++)
							ps_index.println(data[j]);

						ps_index.println("</PRE>");
					}

					ps_index.println("</TD>");
				}

				if (create_index)
					ps_index.println("</TR></TABLE>");
			}
		}
	}
}
