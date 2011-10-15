/*
 * @(#)CelestialDivisionMapContainerAccessor.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import java.util.*;
import net.aerith.misao.util.*;

/**
 * The <code>CelestialDivisionMapContainerAccessor</code> represents a 
 * sequential accessor to the objects on the celestial division map
 * where the flag is set as true.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 February 23
 */

public class CelestialDivisionMapContainerAccessor {
	/**
	 * The celestial division container map.
	 */
	protected CelestialDivisionMapContainer map_container;

	/**
	 * The celestial division map.
	 */
	protected CelestialDivisionMap map;

	/**
	 * The current index on the map.
	 */
	protected int index = -1;

	/**
	 * The current vector.
	 */
	protected Vector vector = null;

	/**
	 * The current index in the vector.
	 */
	protected int index2 = -1;

	/**
	 * Constructs a <code>CelestialDivisionMapContainerAccessor</code>.
	 * @param map_container the celestial division container map.
	 * @param map           the celestial division map.
	 */
	public CelestialDivisionMapContainerAccessor ( CelestialDivisionMapContainer map_container, CelestialDivisionMap map ) {
		this.map_container = map_container;
		this.map = map;

		index = -1;
		vector = null;
		index2 = -1;
	}

	/**
	 * Gets the first object where the flag is set as true.
	 * @return the first object where the flag is set as true.
	 */
	public Object getFirstElement ( ) {
		index = map.getFirstIndex();
		if (index < 0)
			return null;

		vector = map_container.getObjectVectorAt(index);
		index2 = -1;

		return getNextElement();
	}

	/**
	 * Gets the next object where the flag is set as true.
	 * @return the next object where the flag is set as true.
	 */
	public Object getNextElement ( ) {
		try {
			index2++;
			return vector.elementAt(index2);
		} catch ( Exception exception ) {
		}

		vector = null;

		while (vector == null) {
			index = map.getNextIndex(index);
			if (index < 0)
				return null;

			vector = map_container.getObjectVectorAt(index);
			index2 = -1;
		}

		return getNextElement();
	}
}
