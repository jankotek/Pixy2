/*
 * @(#)CelestialDivisionMapContainer.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import java.util.*;
import net.aerith.misao.util.*;

/**
 * The <code>CelestialDivisionMapContainer</code> represents a map 
 * of the celestial globe divided per 10 minutes in R.A. and per 1 
 * degree in Decl., which contains objects classified at proper cells
 * based on the R.A. and Decl.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 February 23
 */

public class CelestialDivisionMapContainer {
	/**
	 * The object map.
	 */
	protected Vector[] object_map;

	/**
	 * Constructs a <code>CelestialDivisionMapContainer</code>.
	 */
	public CelestialDivisionMapContainer ( ) {
		object_map = new Vector[24 * 6 * 2 * 90];
	}

	/**
	 * Adds an object.
	 * @param coor   the R.A. and Decl. 
	 * @param object the object.
	 */
	public void add ( Coor coor, Object object ) {
		int ra_h = (int)(coor.getRA() / 15.0);
		int ra_m = (int)((coor.getRA() / 15.0 - (double)ra_h) * 6.0);
		int decl_sign = (coor.getDecl() < 0 ? 1 : 0);
		int decl = (int)Math.abs(coor.getDecl());

		int index = ra_h * 6 * 2 * 90 + ra_m * 2 * 90 + decl_sign * 90 + decl;

		if (object_map[index] == null)
			object_map[index] = new Vector();

		((Vector)object_map[index]).addElement(object);
	}

	/**
	 * Gets the object vector at the specified index.
	 * @param index the index.
	 * @return the object vector.
	 */
	public Vector getObjectVectorAt ( int index ) {
		return object_map[index];
	}

	/**
	 * Gets the sequential accessor to the objects at the specified 
	 * map.
	 * @param map the map to retrieve the objects at.
	 * @return the sequential accessor.
	 */
	public CelestialDivisionMapContainerAccessor getAccessor ( CelestialDivisionMap map ) {
		return new CelestialDivisionMapContainerAccessor(this, map);
	}
}
