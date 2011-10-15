/*
 * @(#)ReportDocumentUpdatedListener.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;

/**
 * The <code>ReportDocumentUpdatedListener</code> is a listener 
 * interface of the XML report document update.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 July 21
 */

public interface ReportDocumentUpdatedListener {
	/**
	 * Invoked when the measured magnitude of the detected stars are
	 * updated.
	 * @param report the XML report document.
	 */
	public void photometryUpdated ( XmlReport report );

	/**
	 * Invoked when the measured position of the detected stars are
	 * updated.
	 * @param report the XML report document.
	 */
	public void astrometryUpdated ( XmlReport report );

	/**
	 * Invoked when some stars are added, removed or replaced.
	 * @param report the XML report document.
	 */
	public void starsUpdated ( XmlReport report );

	/**
	 * Invoked when the image date is updated.
	 * @param report the XML report document.
	 */
	public void dateUpdated ( XmlReport report );

	/**
	 * Invoked when a secondary record, like instruments, is updated.
	 * @param report the XML report document.
	 */
	public void recordUpdated ( XmlReport report );
}
