/*
 * @(#)FilterSelectionDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.FilterSelection;
import net.aerith.misao.gui.dialog.Dialog;
import net.aerith.misao.image.filter.FilterSet;

/**
 * The <code>FilterSelectionDialog</code> represents a dialog to 
 * select image processing filters.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 13
 */

public class FilterSelectionDialog extends Dialog {
	/**
	 * The panel.
	 */
	protected FilterSelectionPanel panel;

	/**
	 * Constructs a <code>FilterSelectionDialog</code>.
	 * @param filter_set the set of image processing filters.
	 */
	public FilterSelectionDialog ( FilterSet filter_set ) {
		components = new Object[1];

		panel = new FilterSelectionPanel(filter_set);
		components[0] = panel;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Select Image Processing Filters";
	}

	/**
	 * Sets the default values.
	 */
	protected void setDefaultValues ( ) {
	}

	/**
	 * Saves the default values.
	 */
	protected void saveDefaultValues ( ) {
	}

	/**
	 * Gets a set of selected image processing filters.
	 * @return a set of selected image processing filters.
	 */
	public FilterSet getFilterSet ( ) {
		return panel.getFilterSet();
	}
}
