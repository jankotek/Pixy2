/*
 * @(#)InformationTreeSelectionListener.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.event;
import net.aerith.misao.xml.XmlInformation;

/**
 * The <code>InformationTreeSelectionListener</code> is a listener 
 * interface of node selection in <code>DatabaseInformationTree</code>.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public interface InformationTreeSelectionListener {
	/** 
	 * Invoked when the specified image information is selected.
	 * @param info the image information.
	 */
	public abstract void select ( XmlInformation info );
}
