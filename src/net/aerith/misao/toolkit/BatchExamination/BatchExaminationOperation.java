/*
 * @(#)BatchExaminationOperation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.toolkit.BatchExamination;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.gui.*;
import net.aerith.misao.xml.*;
import net.aerith.misao.image.filter.FilterSet;
import net.aerith.misao.pixy.ExaminationOperator;
import net.aerith.misao.pixy.matching.MatchingOperator;

/**
 * The <code>BatchExaminationOperation</code> represents a batch 
 * operation of image examination on the selected images.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 September 25
 */

public class BatchExaminationOperation extends MultiTaskOperation {
	/**
	 * The dialog to set the parameters to search new stars.
	 */
	protected BatchExaminationSettingDialog dialog;

	/**
	 * Constructs a <code>BatchExaminationOperation</code>.
	 * @param conductor the conductor of multi task operation.
	 */
	public BatchExaminationOperation ( MultiTaskConductor conductor ) {
		this.conductor = conductor;
	}

	/**
	 * Shows the dialog to set parameters.
	 * @return 0 if <tt>OK</tt> button is pushed, or 2 if <tt>Cancel</tt>
	 * button is pushed.
	 */
	public int showSettingDialog ( ) {
		dialog = new BatchExaminationSettingDialog();
		return dialog.show(conductor.getPane());
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

		ExaminationOperator operator = createExaminationOperator(instruction);
		operator.addMonitor(monitor_set);

		operator.operate();

		XmlReport report = operator.getXmlReportDocument();
		notifySucceeded(report);
	}

	/**
	 * Creates the examination operator.
	 * @param instruction the XML instruction element.
	 * @return the examination operator.
	 */
	protected ExaminationOperator createExaminationOperator ( XmlInstruction instruction ) {
		ExaminationOperator operator = new ExaminationOperator(instruction);

		operator.setImageConverter(dialog.getFilterSet());
		operator.setStarDetectionMode(dialog.getStarDetectionMode());
		operator.setApertureSize(dialog.getInnerApertureSize(), dialog.getOuterApertureSize());
		if (dialog.correctsBloomingPosition())
			operator.setCorrectBloomingPosition();
		if (dialog.isLooseJudgementSelected())
			operator.setMatchingJudgementMode(MatchingOperator.JUDGEMENT_LOOSE);
		operator.fixMagnitudeTranslationFormulaGradient();
		if (dialog.calculatesDistortionField() == false)
			operator.assumeFlat();

		return operator;
	}
}
