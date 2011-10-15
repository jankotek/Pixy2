/*
 * @(#)StarImageList.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util.star;
import net.aerith.misao.util.*;

/**
 * The <code>StarImageList</code> represents a list of
 * <code>StarImage</code>.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 July 23
 */

public class StarImageList extends StarList {
	/**
	 * Sets the flag so that R.A. and Decl. are also output.
	 */
	public void enableOutputCoordinates ( ) {
		for (int i = 0 ; i < size() ; i++) {
			StarImage star = (StarImage)elementAt(i);
			star.enableOutputCoordinates();
		}
	}

	/**
	 * Converts pixel value of star data into magnitude based on the 
	 * specified magnitude translation formula.
	 * @param formula the magnitude translation formula.
	 */
	public void setMagnitude ( MagnitudeTranslationFormula formula ) {
		for (int i = 0 ; i < size() ; i++) {
			StarImage star = (StarImage)elementAt(i);
			star.setMag(formula.convertToMagnitude(star.getValue()));
		}
	}
}
