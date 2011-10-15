/*
 * @(#)AgentNewStarSearchOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.AgentDesktop;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.database.CatalogDBManager;
import net.aerith.misao.pixy.Agent;
import net.aerith.misao.toolkit.NewStarSearch.NewStarSearchOperation;

/**
 * The <code>AgentNewStarSearchOperation</code> represents a batch 
 * operation to search new stars from XML report documents using the
 * agent.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 February 4
 */

public class AgentNewStarSearchOperation extends NewStarSearchOperation {
	/**
	 * The agent.
	 */
	protected Agent agent;

	/**
	 * Constructs an <code>AgentNewStarSearchOperation</code>.
	 * @param conductor  the conductor of multi task operation.
	 * @param db_manager the catalog database manager.
	 * @param agent      the agent.
	 */
	public AgentNewStarSearchOperation ( MultiTaskConductor conductor, CatalogDBManager db_manager, Agent agent ) {
		super(conductor, db_manager);

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
	 * Gets the limiting magnitude.
	 * @param info the image information.
	 * @return the limiting magnitude.
	 */
	protected double getLimitingMag ( XmlInformation info ) {
		return agent.getNewStarSearchLimitingMagnitude(info);
	}

	/**
	 * Gets the amplitude.
	 * @param info the image information.
	 * @return the amplitude.
	 */
	protected double getAmplitude ( XmlInformation info ) {
		return agent.getNewStarSearchAmplitude(info);
	}
}
