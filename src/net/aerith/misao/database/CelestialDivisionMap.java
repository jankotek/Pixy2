/*
 * @(#)CelestialDivisionMap.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.database;
import java.util.*;
import net.aerith.misao.util.*;

/**
 * The <code>CelestialDivisionMap</code> represents a flag map of the
 * celestial globe divided per 10 minutes in R.A. and per 1 degree in
 * Decl.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 February 23
 */

public class CelestialDivisionMap {
	/**
	 * The flag map.
	 */
	protected boolean[] flag_map;

	/**
	 * The R.A. and Decl. at first corner of each cell.
	 */
	private static Coor[] start_coor;

	/**
	 * The R.A. and Decl. at second corner of each cell.
	 */
	private static Coor[] end_coor;

	/**
	 * The central R.A. and Decl. of each cell.
	 */
	private static Coor[] center_coor;

	/**
	 * The radius of each cell.
	 */
	private static double mesh_radius;

	/**
	 * Constructs a <code>CelestialDivisionMap</code>.
	 */
	public CelestialDivisionMap ( ) {
		flag_map = new boolean[24 * 6 * 2 * 90];

		int index = 0;
		for (int ra_h = 0 ; ra_h < 24 ; ra_h++) {
			for (int ra_m = 0 ; ra_m < 60 ; ra_m += 10) {
				for (int decl_sign = 0 ; decl_sign < 2 ; decl_sign++) {
					for (int decl = 0 ; decl < 90 ; decl++) {
						flag_map[index++] = false;
					}
				}
			}
		}
	}

	/**
	 * Fills areas which overlaps on the circle represented by the
	 * specified R.A. and Decl. and the radius.
	 * @param coor   the center position of the circle.
	 * @param radius the radius of the circle in degree.
	 */
	public void fill ( Coor coor, double radius ) {
		if (radius > 90) {
			fillAll();
			return;
		}

		CircumArea circum_area = new CircumArea(coor, radius);

		int ra_start = 0;
		int ra_end = 24 * 6;
		int z_north = (int)(90.0 - circum_area.getDeclMostNorth());
		int z_south = (int)(90.0 - circum_area.getDeclMostSouth()) + 1;

		if (circum_area.getDeclMostNorth() >= 89.0  ||  circum_area.getDeclMostSouth() <= -89.0  ||
			circum_area.getWidthInRA() >= 180.0) {
			if (z_north < 0)
				z_north = 0;
			if (z_south > 180)
				z_south = 180;
		} else {
			double RAstart = (coor.getRA() - circum_area.getWidthInRA() / 2.0) / 15.0 * 6.0;
			double RAend = (coor.getRA() + circum_area.getWidthInRA() / 2.0) / 15.0 * 6.0;
			if (RAstart < 0.0) {
				RAstart += 24.0 * 6.0;
				RAend += 24.0 * 6.0;
			}
			ra_start = (int)RAstart;
			ra_end = (int)RAend + 1;
		}

		// z means the distance from the Northern Pole.
		for (int ra = ra_start ; ra < ra_end ; ra++) {
			for (int z = z_north ; z < z_south ; z++) {
				int decl_sign = 0;
				int decl = 90 - z - 1;
				if (z >= 90) {
					decl_sign = 1;
					decl = z - 90;
				}

				int index = (ra % (24 * 6)) * 2 * 90 + decl_sign * 90 + decl;

				if (flag_map[index] == false) {
					if (circum_area.overlapsArea(start_coor[index], end_coor[index]))
						flag_map[index] = true;
				}
			}
		}
	}

	/**
	 * Fills all areas.
	 */
	public void fillAll ( ) {
		int index = 0;
		for (int ra_h = 0 ; ra_h < 24 ; ra_h++) {
			for (int ra_m = 0 ; ra_m < 60 ; ra_m += 10) {
				for (int decl_sign = 0 ; decl_sign < 2 ; decl_sign++) {
					for (int decl = 0 ; decl < 90 ; decl++) {
						flag_map[index++] = true;
					}
				}
			}
		}
	}

	/**
	 * Expands the filled areas.
	 * @param radius the radius to expand in degree.
	 * @return the new map.
	 */
	public CelestialDivisionMap expand ( double radius ) {
		CelestialDivisionMap new_map = new CelestialDivisionMap();

		if (radius > 90) {
			new_map.fillAll();
			return new_map;
		}

		int index = 0;
		for (int ra_h = 0 ; ra_h < 24 ; ra_h++) {
			for (int ra_m = 0 ; ra_m < 60 ; ra_m += 10) {
				for (int decl_sign = 0 ; decl_sign < 2 ; decl_sign++) {
					for (int decl = 0 ; decl < 90 ; decl++) {
						if (flag_map[index])
							new_map.fill(center_coor[index], radius + mesh_radius);

						index++;
					}
				}
			}
		}

		return new_map;
	}

	/**
	 * Gets the first index where the flag is set as true.
	 * @return the first index where the flag is set as true.
	 */
	public int getFirstIndex ( ) {
		return getNextIndex(-1);
	}

	/**
	 * Gets the next index where the flag is set as true.
	 * @param last_index the last index.
	 * @return the next index where the flag is set as true.
	 */
	public int getNextIndex ( int last_index ) {
		int index = 0;
		for (int ra_h = 0 ; ra_h < 24 ; ra_h++) {
			for (int ra_m = 0 ; ra_m < 60 ; ra_m += 10) {
				for (int decl_sign = 0 ; decl_sign < 2 ; decl_sign++) {
					for (int decl = 0 ; decl < 90 ; decl++) {
						if (index > last_index) {
							if (flag_map[index])
								return index;
						}

						index++;
					}
				}
			}
		}

		return -1;
	}

	/**
	 * Gets the folder hierarchy at the specified index.
	 * @param index the index.
	 * @return the folder hierarchy.
	 */
	public Vector getFolderHierarchyAt ( int index ) {
		int index2 = 0;
		for (int ra_h = 0 ; ra_h < 24 ; ra_h++) {
			for (int ra_m = 0 ; ra_m < 60 ; ra_m += 10) {
				for (int decl_sign = 0 ; decl_sign < 2 ; decl_sign++) {
					for (int decl = 0 ; decl < 90 ; decl++) {
						if (index2 == index)
							return getCoorFolderHierarchy(center_coor[index]);

						index2++;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Creates the database folder hierarchy of the specified R.A. and
	 * Decl.
	 * @param coor the R.A. and Decl.
	 * @return the database folder hierarchy.
	 */
	public static Vector getCoorFolderHierarchy ( Coor coor ) {
		Vector folder_list = new Vector();

		int ra_h = coor.getRA_h();
		int ra_m = coor.getRA_m() / 10;
		ra_m *= 10;
		if (ra_h < 0)
			ra_h = 0;
		if (ra_h >= 24) {
			ra_h = 23;
			ra_m = 50;
		}
		if (ra_m < 0)
			ra_m = 0;
		if (ra_m >= 60)
			ra_m = 50;
		int decl_d = coor.getAbsDecl_d() / 10;
		decl_d *= 10;
		int decl_m = coor.getAbsDecl_d() % 10;
		if (decl_d < 0)
			decl_d = 0;
		if (decl_d >= 90) {
			decl_d = 80;
			decl_m = 9;
		}
		if (decl_m < 0)
			decl_m = 0;
		if (decl_m >= 10)
			decl_m = 9;
		folder_list.addElement(Format.formatIntZeroPadding(ra_h, 2));
		folder_list.addElement(Format.formatIntZeroPadding(ra_m, 2));
		folder_list.addElement((coor.getDecl() < 0 ? "-" : "+") + Format.formatIntZeroPadding(decl_d, 2));
		folder_list.addElement(String.valueOf(decl_m));

		return folder_list;
	}

	/**
	 * Sets the R.A. and Decl. of center and at corners of each cell.
	 */
	static {
		int index = 0;
		for (int ra_h = 0 ; ra_h < 24 ; ra_h++) {
			for (int ra_m = 0 ; ra_m < 60 ; ra_m += 10) {
				for (int decl_sign = 0 ; decl_sign < 2 ; decl_sign++) {
					for (int decl = 0 ; decl < 90 ; decl++) {
						index++;
					}
				}
			}
		}
		int count = index;

		start_coor = new Coor[count];
		end_coor = new Coor[count];
		center_coor = new Coor[count];

		index = 0;
		for (int ra_h = 0 ; ra_h < 24 ; ra_h++) {
			for (int ra_m = 0 ; ra_m < 60 ; ra_m += 10) {
				for (int decl_sign = 0 ; decl_sign < 2 ; decl_sign++) {
					for (int decl = 0 ; decl < 90 ; decl++) {
						if (decl_sign == 0) {
							start_coor[index] = new Coor(ra_h, ra_m, 0.0, false, decl, 0, 0);
							end_coor[index] = new Coor(ra_h, ra_m + 10, 0.0, false, decl + 1, 0, 0);
						} else {
							start_coor[index] = new Coor(ra_h, ra_m, 0.0, true, decl + 1, 0, 0);
							end_coor[index] = new Coor(ra_h, ra_m + 10, 0.0, true, decl, 0, 0);
						}

						center_coor[index] = new Coor(ra_h, ra_m + 5, 0.0, (decl_sign == 1), decl, 30, 0);

						index++;
					}
				}
			}
		}

		// Calculates the radius of the circumcircle of a divided area.
		Coor coor1 = new Coor(0, 0, 0, false, 0, 0, 0);
		Coor coor2 = new Coor(0, 10, 0, false, 1, 0, 0);
		mesh_radius = coor1.getAngularDistanceTo(coor2) / 2.0;
	}
}
