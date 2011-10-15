/*
 * @(#)StarPositionList.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util.star;
import java.util.Vector;
import net.aerith.misao.util.*;

/**
 * The <code>StarPositionList</code> represents a list of
 * <code>StarPosition</code>.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 June 24
 */

public class StarPositionList extends PositionList {
	/**
	 * Constructs an empty <code>StarPositionList</code>.
	 */
	public StarPositionList ( ) {
		super();
	}

	/**
	 * Copies the specified <code>StarPositionList</code> and 
	 * constructs a new <code>StarPositionList</code>.
	 * @param original_list the original list to copy.
	 */
	public StarPositionList ( Vector original_list ) {
		super(original_list);
	}

	/**
	 * Sorts the star data in ascendant order of magnitude.
	 */
	public void sort ( ) {
		Array array = new Array(size());
		for (int i = 0 ; i < size() ; i++) {
			StarPosition star = (StarPosition)elementAt(i);
			array.set(i, star.getMag());
		}
		ArrayIndex index = array.sortAscendant();

		Vector list = new Vector();
		for (int i = 0 ; i < size() ; i++)
			list.addElement(elementAt(index.get(i)));

		removeAllElements();
		for (int i = 0 ; i < list.size() ; i++)
			addElement(list.elementAt(i));
	}
}
