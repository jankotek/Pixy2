/*
 * @(#)ImageContent.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import net.aerith.misao.util.*;

/**
 * The <code>ImageContent</code> is an interface of a content added in
 * a <code>ImageComponent</code>. It has a function to create 
 * <code>java.awt.Image</code>.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 June 24
 */

public interface ImageContent {
	/**
	 * Gets the image size.
	 * @return the image size.
	 */
	public abstract Size getSize();

	/**
	 * Creates a proper <code>java.awt.Image</code> considering the
	 * zoom level.
	 * @return an <code>java.awt.Image</code>.
	 */
	public abstract Image getImage();
}
