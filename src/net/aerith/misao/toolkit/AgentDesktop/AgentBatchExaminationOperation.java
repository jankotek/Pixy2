/*
 * @(#)AgentBatchExaminationOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.AgentDesktop;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.pixy.Agent;
import net.aerith.misao.pixy.ExaminationOperator;
import net.aerith.misao.toolkit.BatchExamination.BatchExaminationOperation;

/**
 * The <code>AgentBatchExaminationOperation</code> represents a batch 
 * operation of image examination on the selected images using the 
 * agent.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class AgentBatchExaminationOperation extends BatchExaminationOperation {
	/**
	 * The agent.
	 */
	protected Agent agent;

	/**
	 * Constructs an <code>AgentBatchExaminationOperation</code>.
	 * @param conductor the conductor of multi task operation.
	 * @param agent     the agent.
	 */
	public AgentBatchExaminationOperation ( MultiTaskConductor conductor, Agent agent ) {
		super(conductor);

		this.agent = agent;
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		return 0;
	}

	/**
	 * Operates on one item. This is invoked from the conductor of 
	 * multi task operation.
	 * @param object the target object to operate.
	 * @exception Exception if an error occurs.
	 */
	public void operate ( Object object )
		throws Exception
	{
		XmlInstruction instruction = (XmlInstruction)object;

		instruction = agent.preOperation(instruction);

		super.operate(instruction);
	}

	/**
	 * Creates the examination operator.
	 * @param instruction the XML instruction element.
	 * @return the examination operator.
	 */
	protected ExaminationOperator createExaminationOperator ( XmlInstruction instruction ) {
		return agent.createExaminationOperator(instruction);
	}
}
