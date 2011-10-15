/*
 * @(#)FixedScrollPane.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import javax.swing.*;
import net.aerith.misao.util.Size;

/**
 * The <code>FixedScrollPane</code> represents a <code>ScrollPane</code>
 * whose size is fixed.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 September 26
 */

public class FixedScrollPane extends JScrollPane {
	/**
	 * The size.
	 */
	protected Dimension dimension;

	/**
	 * Constructs a <code>FixedScrollPane</code>.
	 * @param size      the size.
	 * @param component the component.
	 */
	public FixedScrollPane ( Size size, Component component ) {
		super(component);

		dimension = new Dimension(size.getWidth(), size.getHeight());
	}

	/**
	 * Returns the size of this component in the form of a Dimension 
	 * object. 
	 */
	public Dimension getSize ( ) {
		return dimension;
	}

	/**
	 * Gets the maximum size of this component.
	 */
	public Dimension getMaximumSize ( ) {
		return dimension;
	}

	/**
	 * Gets the minimum size of this component.
	 */
	public Dimension getMinimumSize ( ) {
		return dimension;
	}

	/**
	 * Gets the preferred size of this component.
	 */
	public Dimension getPreferredSize ( ) {
		return dimension;
	}
}
