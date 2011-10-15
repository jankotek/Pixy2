/*
 * @(#)BatchExaminationControlPanel.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.BatchExamination;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.gui.dialog.*;
import net.aerith.misao.io.filechooser.XmlFilter;

/**
 * The <code>BatchExaminationControlPanel</code> represents a control 
 * panel to operate the image examination on the selected images.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 13
 */

public class BatchExaminationControlPanel extends ControlPanel {
	/**
	 * The table.
	 */
	protected InstructionTable table;

	/**
	 * Creates a <code>BatchExaminationControlPanel</code>.
	 * @param operation the operation.
	 */
	public BatchExaminationControlPanel ( BatchExaminationOperation operation, InstructionTable table ) {
		super(operation);

		this.table = table;
	}

	/**
	 * Gets the button title to set parameters. This must be overrided
	 * in the subclasses.
	 * @return the button title to set parameters.
	 */
	public String getSetButtonTitle ( ) {
		return "Add Image File";
	}

	/**
	 * Gets the button title to reset. This must be overrided in the
	 * subclasses.
	 * @return the button title to reset.
	 */
	public String getResetButtonTitle ( ) {
		return "Set Ready";
	}

	/**
	 * Invoked when the set button is pushed. This must be overrided 
	 * in the subclasses.
	 * @param e contains the selected menu item.
	 */
	public void actionPerformedSet ( ActionEvent e ) {
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
