/*
 * @(#)TransformationDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.ImageConversion;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.dialog.Dialog;

/**
 * The <code>TransformationDialog</code> represents a dialog to set 
 * the image size or scale.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class TransformationDialog extends Dialog {
	/**
	 * The panel to set the image size or scale.
	 */
	protected TransformationPanel transform_panel;

	/**
	 * Constructs an <code>TransformationDialog</code>.
	 */
	public TransformationDialog ( ) {
		components = new Object[1];

		transform_panel = new TransformationPanel();
		components[0] = transform_panel;
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Transformation";
	}

	/**
	 * Returns true if the size is selected.
	 * @return true if the size is selected.
	 */
	public boolean isSize ( ) {
		return transform_panel.isSize();
	}

	/**
	 * Returns true if the scale is selected.
	 * @return true if the scale is selected.
	 */
	public boolean isScale ( ) {
		return transform_panel.isScale();
	}

	/**
	 * Selects the size.
	 */
	public void selectSize ( ) {
		transform_panel.selectSize();
	}

	/**
	 * Selects the scale.
	 */
	public void selectScale ( ) {
		transform_panel.selectScale();
	}

	/**
	 * Gets the image size.
	 * @return the image size.
	 */
	public Size getImageSize ( ) {
		return transform_panel.getImageSize();
	}

	/**
	 * Gets the image scale.
	 * @return the image scale.
	 */
	public int getScale ( ) {
		return transform_panel.getScale();
	}

	/**
	 * Sets the image size.
	 * @param size the image size.
	 */
	public void setImageSize ( Size size ) {
		transform_panel.setImageSize(size);
	}

	/**
	 * Sets the image scale.
	 * @param scale the image scale.
	 */
	public void setScale ( int scale ) {
		transform_panel.setScale(scale);
	}

	/**
	 * Returns true when to rescale ST-4/6 Image.
	 * @return true when to rescale ST-4/6 Image.
	 */
	public boolean rescalesSbig ( ) {
		return transform_panel.rescalesSbig();
	}

	/**
	 * Sets the flag to rescale ST-4/6 Image.
	 * @param flag true when to rescale ST-4/6 Image.
	 */
	public void setRescaleSbig ( boolean flag ) {
		transform_panel.setRescaleSbig(flag);
	}
}
