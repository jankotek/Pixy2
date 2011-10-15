/*
 * @(#)HtmlImageGalleryControlPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.HtmlImageGallery;
import java.awt.*;
import java.awt.event.*;
import net.aerith.misao.gui.*;

/**
 * The <code>HtmlImageGalleryControlPanel</code> represents a control 
 * panel to create HTML image gallery.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class HtmlImageGalleryControlPanel extends ControlPanel {
	/**
	 * The table.
	 */
	protected HtmlImageGalleryTable table;

	/**
	 * Constructs a <code>HtmlImageGalleryControlPanel</code>.
	 * @param operation the operation.
	 * @param table     the table.
	 */
	public HtmlImageGalleryControlPanel ( HtmlImageGalleryOperation operation, HtmlImageGalleryTable table ) {
		super(operation);

		this.table = table;
	}

	/**
	 * Invoked when the reset button is pushed. This must be overrided 
	 * in the subclasses.
	 * @param e contains the selected menu item.
	 */
	public void actionPerformedReset ( ActionEvent e ) {
		table.setReady();
	}
}
