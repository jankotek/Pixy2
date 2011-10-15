/*
 * @(#)MergedStar.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util.star;
import java.util.Vector;
import net.aerith.misao.util.*;

/**
 * The <code>MergedStar</code> represents a set of some star data. It 
 * is a super class of <code>UnifiedStar</code> and 
 * <code>BlendingStar</code>.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 February 17
 */

public class MergedStar extends Star implements StarContainer {
	/**
	 * The list of content stars.
	 */
	protected Vector content_list = new Vector();

	/**
	 * Constructs a <code>MergedStar</code> of only one star.
	 * @param star the initial star.
	 */
	public MergedStar ( Star star ) {
		setX(star.getX());
		setY(star.getY());
		setMag(star.getMag());
		setCoor(new Coor(star.getCoor()));

		content_list.addElement(star);
	}

	/**
	 * Gets the number of content stars.
	 * @return the number of content stars.
	 */
	public int getStarCount ( ) {
		return content_list.size();
	}

	/**
	 * Gets a star at the specified index.
	 * @param index the index.
	 * @return the star at the specified index.
	 */
	public Star getStarAt ( int index ) {
		return (Star)content_list.elementAt(index);
	}

	/**
	 * Appends a <code>Star</code> to this object.
	 * @param star the star to append.
	 */
	public void append ( Star star ) {
		content_list.addElement(star);
	}

	/**
	 * Adds the specified <code>Position</code> to this.
	 * @param position the value to add.
	 */
	public void add ( Position position ) {
		super.add(position);

		for (int i = 0 ; i < content_list.size() ; i++)
			((Position)content_list.elementAt(i)).add(position);
	}

	/**
	 * Rescales the position by magnifying the specified value.
	 * @param ratio the value to magnify.
	 */
	public void rescale ( double ratio ) {
		super.rescale(ratio);

		for (int i = 0 ; i < content_list.size() ; i++)
			((Position)content_list.elementAt(i)).rescale(ratio);
	}

	/**
	 * Maps the R.A. and Decl. to the (x,y) position based on the
	 * specified <code>ChartMapFunction</code>.
	 * @param cmf the map function.
	 * @param df  the distortion field.
	 */
	public void mapCoordinatesToXY ( ChartMapFunction cmf, DistortionField df ) {
		super.mapCoordinatesToXY(cmf, df);

		for (int i = 0 ; i < content_list.size() ; i++)
			((Star)content_list.elementAt(i)).mapCoordinatesToXY(cmf, df);
	}

	/**
	 * Maps the (x,y) position to the R.A. and Decl. based on the
	 * specified <code>ChartMapFunction</code>.
	 * @param cmf the map function.
	 * @param df  the distortion field.
	 */
	public void mapXYToCoordinates ( ChartMapFunction cmf, DistortionField df ) {
		super.mapXYToCoordinates(cmf, df);

		for (int i = 0 ; i < content_list.size() ; i++)
			((Star)content_list.elementAt(i)).mapXYToCoordinates(cmf, df);
	}

	/**
	 * Returns an array of string representations of the state of 
	 * stars contained in this object. 
	 * @return an array of string representations.
	 */
	public String[] getOutputStrings ( ) {
		Vector l = new Vector();
		for (int i = 0 ; i < content_list.size() ; i++) {
			String[] s = ((Star)content_list.elementAt(i)).getOutputStrings();
			for (int j = 0 ; j < s.length ; j++)
				l.addElement(s[j]);
		}

		String[] s = new String[l.size()];
		for (int i = 0 ; i < l.size() ; i++)
			s[i] = (String)l.elementAt(i);
		return s;
	}

	/**
	 * Returns an array of string representations of the state of 
	 * stars contained in this object.
	 * @return an array of string representations.
	 */
	public String[] getOutputStringsWithXY ( ) {
		Vector l = new Vector();
		for (int i = 0 ; i < content_list.size() ; i++) {
			String[] s = ((Star)content_list.elementAt(i)).getOutputStringsWithXY();
			for (int j = 0 ; j < s.length ; j++)
				l.addElement(s[j]);
		}

		String[] s = new String[l.size()];
		for (int i = 0 ; i < l.size() ; i++)
			s[i] = (String)l.elementAt(i);
		return s;
	}

	/**
	 * Returns an array of string representations of the state of 
	 * stars contained in this object.
	 * @return an array of string representations.
	 */
	public String[] getPxfStringsWithXY ( ) {
		Vector l = new Vector();
		for (int i = 0 ; i < content_list.size() ; i++) {
			String[] s = ((Star)content_list.elementAt(i)).getPxfStringsWithXY();
			for (int j = 0 ; j < s.length ; j++)
				l.addElement(s[j]);
		}

		String[] s = new String[l.size()];
		for (int i = 0 ; i < l.size() ; i++)
			s[i] = (String)l.elementAt(i);
		return s;
	}
}
