/*
 * @(#)CelestialDivisionMapAccessor.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import java.util.*;
import net.aerith.misao.util.*;

/**
 * The <code>CelestialDivisionMapAccessor</code> represents a 
 * sequential accessor to the folders on the celestial division map
 * where the flag is set as true.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2000 December 30
 */

public class CelestialDivisionMapAccessor {
	/**
	 * The celestial division map.
	 */
	protected CelestialDivisionMap map;

	/**
	 * The current index.
	 */
	protected int index = -1;

	/**
	 * Constructs a <code>CelestialDivisionMapAccessor</code>.
	 * @param map the celestial division map.
	 */
	public CelestialDivisionMapAccessor ( CelestialDivisionMap map ) {
		this.map = map;

		index = -1;
	}

	/**
	 * Gets the first folder hierarchy where the flag is set as true.
	 * @return the first folder hierarchy where the flag is set as 
	 * true.
	 */
	public Vector getFirstFolderHierarchy ( ) {
		index = map.getFirstIndex();
		if (index < 0)
			return null;

		return map.getFolderHierarchyAt(index);
	}

	/**
	 * Gets the next folder hierarchy where the flag is set as true.
	 * @return the next folder hierarchy where the flag is set as true.
	 */
	public Vector getNextFolderHierarchy ( ) {
		index = map.getNextIndex(index);
		if (index < 0)
			return null;

		return map.getFolderHierarchyAt(index);
	}
}
