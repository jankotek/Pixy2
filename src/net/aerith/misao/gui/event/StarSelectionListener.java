/*
 * @(#)StarSelectionListener.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.event;
import net.aerith.misao.util.star.Star;
import net.aerith.misao.xml.XmlStar;

/**
 * The <code>StarSelectionListener</code> is a listener interface of
 * a star selection.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 February 28
 */

public interface StarSelectionListener {
	/** 
	 * Invoked when the specified star is selected.
	 * @param xml_star    the XML star object.
	 * @param record_star the record in the XML star object.
	 */
	public abstract void select ( XmlStar xml_star, Star record_star );
}
