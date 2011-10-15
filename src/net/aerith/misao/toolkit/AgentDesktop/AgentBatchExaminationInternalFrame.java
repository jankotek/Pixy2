/*
 * @(#)AgentBatchExaminationInternalFrame.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.AgentDesktop;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.util.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.pixy.Agent;
import net.aerith.misao.toolkit.BatchExamination.BatchExaminationInternalFrame;

/**
 * The <code>AgentBatchExaminationInternalFrame</code> represents a 
 * frame to select image files, edit the batch XML file for 
 * examination, and operate the image examination on the selected 
 * images, using the agent.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class AgentBatchExaminationInternalFrame extends BatchExaminationInternalFrame {
	/**
	 * Constructs an <code>AgentBatchExaminationInternalFrame</code>.
	 * @param agent   the agent.
	 * @param desktop the parent desktop.
	 */
	public AgentBatchExaminationInternalFrame ( Agent agent, BaseDesktop desktop ) {
		super(desktop);

		setOperation(new AgentBatchExaminationOperation(table, agent));

		// Disables due to proceeded to the next operation
		// except for interruption.
		control_panel.setSucceededMessageEnabled(false);
		control_panel.setInterruptedMessageEnabled(true);
		control_panel.setFailedMessageEnabled(false);
	}

	/**
	 * Sets an observer.
	 * @param observer an observer
	 */
	public void setAgentOperationObserver ( OperationObserver observer ) {
		operation.addObserver(observer);
	}

	/**
	 * Adds somes sets of instruction parameters.
	 * @param instructions some sets of instruction parameters.
	 * @param file_manager the file manager.
	 */
	public void addInstructions ( XmlInstruction[] instructions ) {
		table.addInstructions(instructions, desktop.getFileManager());
	}
}
