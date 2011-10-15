/*
 * @(#)AttributesDialog.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.MagnitudeRecord;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.aerith.misao.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.Dialog;
import net.aerith.misao.xml.MagnitudeRecordAttributes;

/**
 * The <code>AttributesDialog</code> represents a dialog to set the 
 * attributes of the magnitude record.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public class AttributesDialog extends Dialog {
	/**
	 * The check box whether reported to VSNET.
	 */
	protected JCheckBox checkbox_reported_vsnet;

	/**
	 * The check box whether reported to VSOLJ.
	 */
	protected JCheckBox checkbox_reported_vsolj;

	/**
	 * The check box whether unofficial.
	 */
	protected JCheckBox checkbox_unofficial;

	/**
	 * The check box whether discarded.
	 */
	protected JCheckBox checkbox_discarded;

	/**
	 * The check box whether preempted.
	 */
	protected JCheckBox checkbox_preempted;

	/**
	 * The check box whether imported.
	 */
	protected JCheckBox checkbox_imported;

	/**
	 * Constructs a <code>AttributesDialog</code>. 
	 */
	public AttributesDialog ( ) {
		this(new MagnitudeRecordAttributes());
	}

	/**
	 * Constructs a <code>AttributesDialog</code>. 
	 * @param attributes the attributes of the magnitude record.
	 */
	public AttributesDialog ( MagnitudeRecordAttributes attributes ) {
		components = new Object[6];

		checkbox_reported_vsnet = new JCheckBox("Reported to VSNET, true if already reported to VSNET (Variable Star Network).");
		checkbox_reported_vsnet.setSelected(attributes.isReportedToVsnet());
		components[0] = checkbox_reported_vsnet;

		checkbox_reported_vsolj = new JCheckBox("Reported to VSOLJ, true if already reported to VSOLJ (Variable Star Observers League in Japan).");
		checkbox_reported_vsolj.setSelected(attributes.isReportedToVsolj());
		components[1] = checkbox_reported_vsolj;

		checkbox_unofficial = new JCheckBox("Unofficial, true if the data is not permitted to be open to the public.");
		checkbox_unofficial.setSelected(attributes.isUnofficial());
		components[2] = checkbox_unofficial;

		checkbox_discarded = new JCheckBox("Discarded, true if the data is discarded because erroneous.");
		checkbox_discarded.setSelected(attributes.isDiscarded());
		components[3] = checkbox_discarded;

		checkbox_preempted = new JCheckBox("Preempted, true if already reported to public society by others.");
		checkbox_preempted.setSelected(attributes.isPreempted());
		components[4] = checkbox_preempted;

		checkbox_imported = new JCheckBox("Imported, true if the data is imported to the database by hand.");
		checkbox_imported.setSelected(attributes.isImported());
		components[5] = checkbox_imported;

		setDefaultValues();
	}

	/**
	 * Gets the title of the dialog.
	 * @return the title of the dialog.
	 */
	protected String getTitle ( ) {
		return "Magnitude Record Attributes";
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
	 * Gets the attributes of the magnitude record.
	 * @return the attributes of the magnitude record.
	 */
	public MagnitudeRecordAttributes getAttributes ( ) {
		MagnitudeRecordAttributes attributes = new MagnitudeRecordAttributes();

		attributes.setReportedToVsnet(checkbox_reported_vsnet.isSelected());
		attributes.setReportedToVsolj(checkbox_reported_vsolj.isSelected());
		attributes.setUnofficial(checkbox_unofficial.isSelected());
		attributes.setDiscarded(checkbox_discarded.isSelected());
		attributes.setPreempted(checkbox_preempted.isSelected());
		attributes.setImported(checkbox_imported.isSelected());

		return attributes;
	}
}
