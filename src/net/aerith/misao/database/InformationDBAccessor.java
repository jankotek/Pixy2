/*
 * @(#)InformationDBAccessor.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.xml.*;

/**
 * The <code>InformationDBAccessor</code> represents a sequential 
 * accessor to the XML image information elements which overlaps to
 * the specified images.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 February 28
 */

public class InformationDBAccessor {
	/**
	 * The list of sequential accessor.
	 */
	protected Vector accessor_list;

	/**
	 * The next record;
	 */
	protected XmlDBRecord next_record;

	/**
	 * The current index.
	 */
	protected int index;

	/**
	 * Constructs a <code>InformationDBAccessor</code>.
	 */
	public InformationDBAccessor ( ) {
		accessor_list = new Vector();
	}

	/**
	 * Adds the sequential accessor.
	 * @param accessor the sequential accessor.
	 */
	public void addMapAccessor ( CelestialDivisionMapDBAccessor accessor ) {
		accessor_list.addElement(accessor);
	}

	/**
	 * Gets the first image information.
	 * @return the first image information.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlInformation getFirstElement ( )
		throws IOException
	{
		index = 0;

		if (accessor_list.size() == 0)
			return null;

		CelestialDivisionMapDBAccessor accessor = (CelestialDivisionMapDBAccessor)accessor_list.elementAt(0);
		next_record = accessor.getFirstElement();

		return getNextElement();
	}

	/**
	 * Gets the next image information.
	 * @return the next image information.
	 * @exception IOException if I/O error occurs.
	 */
	public XmlInformation getNextElement ( )
		throws IOException
	{
		while (true) {
			CelestialDivisionMapDBAccessor accessor = (CelestialDivisionMapDBAccessor)accessor_list.elementAt(index);

			if (next_record == null) {
				index++;

				if (index >= accessor_list.size())
					return null;

				accessor = (CelestialDivisionMapDBAccessor)accessor_list.elementAt(index);
				next_record = accessor.getFirstElement();
			} else {
				XmlDBRecord record = next_record;
				next_record = accessor.getNextElement();

				return (XmlInformation)record;
			}
		}
	}
}
