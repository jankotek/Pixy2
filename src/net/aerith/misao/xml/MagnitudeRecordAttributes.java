/*
 * @(#)MagnitudeRecordAttributes.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;

/**
 * The <code>MagnitudeRecordAttributes</code> represents the 
 * attributes of the magnitude record.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 August 4
 */

public class MagnitudeRecordAttributes {
	/**
	 * True if reported to VSNET.
	 */
	protected boolean reported_vsnet = false;

	/**
	 * True if reported to VSOLJ.
	 */
	protected boolean reported_vsolj = false;

	/**
	 * True if unofficial.
	 */
	protected boolean unofficial = false;

	/**
	 * True if discarded.
	 */
	protected boolean discarded = false;

	/**
	 * True if preempted.
	 */
	protected boolean preempted = false;

	/**
	 * True if imported.
	 */
	protected boolean imported = false;

	/**
	 * Constructs a <code>MagnitudeRecordAttributes</code>.
	 */
	public MagnitudeRecordAttributes ( ) {
	}

	/**
	 * Returns true if reported to VSNET.
	 * @return true if reported to VSNET.
	 */
	public boolean isReportedToVsnet ( ) {
		return reported_vsnet;
	}

	/**
	 * Sets the flag if reported to VSNET.
	 * @param f true if reported to VSNET.
	 */
	public void setReportedToVsnet ( boolean f ) {
		reported_vsnet = f;
	}

	/**
	 * Returns true if reported to VSOLJ.
	 * @return true if reported to VSOLJ.
	 */
	public boolean isReportedToVsolj ( ) {
		return reported_vsolj;
	}

	/**
	 * Sets the flag if reported to VSOLJ.
	 * @param f true if reported to VSOLJ.
	 */
	public void setReportedToVsolj ( boolean f ) {
		reported_vsolj = f;
	}

	/**
	 * Returns true if unofficial.
	 * @return true if unofficial.
	 */
	public boolean isUnofficial ( ) {
		return unofficial;
	}

	/**
	 * Sets the flag if unofficial.
	 * @param f true if unofficial.
	 */
	public void setUnofficial ( boolean f ) {
		unofficial = f;
	}

	/**
	 * Returns true if discarded.
	 * @return true if discarded.
	 */
	public boolean isDiscarded ( ) {
		return discarded;
	}

	/**
	 * Sets the flag if discarded.
	 * @param f true if discarded.
	 */
	public void setDiscarded ( boolean f ) {
		discarded = f;
	}

	/**
	 * Returns true if preempted.
	 * @return true if preempted.
	 */
	public boolean isPreempted ( ) {
		return preempted;
	}

	/**
	 * Sets the flag if preempted.
	 * @param f true if preempted.
	 */
	public void setPreempted ( boolean f ) {
		preempted = f;
	}

	/**
	 * Returns true if imported.
	 * @return true if imported.
	 */
	public boolean isImported ( ) {
		return imported;
	}

	/**
	 * Sets the flag if imported.
	 * @param f true if imported.
	 */
	public void setImported ( boolean f ) {
		imported = f;
	}
}
