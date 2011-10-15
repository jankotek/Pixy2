/*
 * @(#)FilterSet.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.image.filter;
import java.util.*;
import net.aerith.misao.image.*;

/**
 * The <code>FilterSet</code> represents a set of image processing 
 * filters.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 17
 */

public class FilterSet implements ImageConverter {
	/**
	 * The list of filters.
	 */
	protected Vector list_filter = new Vector();

	/**
	 * The list of filter names.
	 */
	protected Vector list_name = new Vector();

	/**
	 * Constructs an empty <code>FilterSet</code>.
	 */
	public FilterSet ( ) {
	}

	/**
	 * Constructs a <code>FilterSet</code>.
	 * @param filter_set the source filter set.
	 */
	public FilterSet ( FilterSet filter_set ) {
		for (int i = 0 ; i < filter_set.list_filter.size() ; i++)
			list_filter.addElement(filter_set.list_filter.elementAt(i));
		for (int i = 0 ; i < filter_set.list_name.size() ; i++)
			list_name.addElement(filter_set.list_name.elementAt(i));
	}

	/**
	 * Adds a filter.
	 * @param filter the filter.
	 * @param name   the name of the filter.
	 */
	public void add ( Filter filter, String name ) {
		list_filter.addElement(filter);
		list_name.addElement(name);
	}

	/**
	 * Removes a filter at the specified position.
	 * @param index the index.
	 */
	public void removeAt ( int index ) {
		list_filter.removeElementAt(index);
		list_name.removeElementAt(index);
	}

	/**
	 * Gets the filters.
	 * @return the filters.
	 */
	public Filter[] getFilters ( ) {
		Filter[] filters = new Filter[list_filter.size()];
		for (int i = 0 ; i < list_filter.size() ; i++)
			filters[i] = (Filter)list_filter.elementAt(i);
		return filters;
	}

	/**
	 * Gets the filter names.
	 * @return the filter names.
	 */
	public String[] getFilterNames ( ) {
		String[] names = new String[list_name.size()];
		for (int i = 0 ; i < list_name.size() ; i++)
			names[i] = (String)list_name.elementAt(i);
		return names;
	}

	/**
	 * Converts an image.
	 * @param image an image.
	 * @return a new image.
	 * @exception Exception if an exception occurs.
	 */
	public MonoImage convertImage ( MonoImage image )
		throws Exception
	{
		for (int i = 0 ; i < list_filter.size() ; i++) {
			Filter filter = (Filter)list_filter.elementAt(i);
			image = filter.operate(image);
		}

		return image;
	}
}
