/*
 * @(#)Astro.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;

/**
 * The <code>Astro</code> is a class which consists of constant values
 * and static methods related to astronomy.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 October 3
 */

public class Astro {
	/**
	 * The converter to radian unit (pi / 180).
	 */
	public final static double RAD = 0.01745329251994329547;

	/**
	 * The Julian Day of B1900.0. Note that this is not exactly 
	 * correct. This is actually J1900.0.
	 */
	public final static double BESSELL_1900 = 2415020.0;

	/**
	 * The Julian Day of B1950.0.
	 */
	public final static double BESSELL_1950 = 2433282.42346;

	/**
	 * The Julian Day of J1850.0.
	 */
	public final static double JULIUS_1850 = 2396758.0;

	/**
	 * The Julian Day of J1900.0.
	 */
	public final static double JULIUS_1900 = 2415020.0;

	/**
	 * The Julian Day of J2000.0.
	 */
	public final static double JULIUS_2000 = 2451545.0;

	/**
	 * The ecliptic obliquity at 1950.0.
	 */
	public final static double EPSILON_1950 = 23.44578787;

	/**
	 * The ecliptic obliquity at 2000.0.
	 */
	public final static double EPSILON_2000 = 23.4392911;

	/**
	 * The Gaussian constant.
	 */
	public final static double GAUSSIAN_CONSTANT = 0.01720209895;

	/**
	 * The light time in second.
	 */
	public final static double LIGHT_TIME = 499.004782;

	/**
	 * The width to height ratio of SBIG ST-4/ST-6 images.
	 */
	public final static double SBIG_RATIO = 1.173913;

	/**
	 * The brightness ratio of 1 mag.
	 */
	public final static double MAG_STEP = Math.pow(100.0, 1.0 / 5.0);

	/**
	 * Normalize the specified value into the proper range.
	 * @param value   the value.
	 * @param maximum the maximum value of range, which must be
	 * positive.
	 * @return the normalized value, which is between 0 and the
	 * specified maximum value.
	 */
	public static double normalize ( double value, double maximum ) {
		if (value >= 0.0)
			return value - (double)((long)(value / maximum) * maximum);
		else
			return value - (double)((long)(value / maximum) * maximum) + maximum;
	}

	/**
	 * Calculates sinh(x).
	 * @param x the argument.
	 * @return the sinh(x).
	 */
	public static final double sinh ( double x ) {
		return (Math.exp(x) - Math.exp(- x)) / 2.0;
	}

	/**
	 * Calculates cosh(x).
	 * @param x the argument.
	 * @return the cosh(x).
	 */
	public static final double cosh ( double x ) {
		return (Math.exp(x) + Math.exp(- x)) / 2.0;
	}

	/**
	 * Gets the T1850.
	 * @param jd the Julian day.
	 * @return the T1850.
	 */
	public static double get_t1850 ( double jd ) {
		return (jd - JULIUS_1850) / 36525.0;
	}

	/**
	 * Gets the T1900.
	 * @param jd the Julian day.
	 * @return the T1900.
	 */
	public static double get_t1900 ( double jd ) {
		return (jd - JULIUS_1900) / 36525.0;
	}

	/**
	 * Gets the T2000.
	 * @param jd the Julian day.
	 * @return the T2000.
	 */
	public static double get_t2000 ( double jd ) {
		return (jd - JULIUS_2000) / 36525.0;
	}

	/**
	 * Gets the ecliptic inclination in J2000.0.
	 * @param t the T2000.
	 * @return the ecliptic inclination.
	 */
	public static double epsilon2000 ( double t ) {
		return EPSILON_2000 - 0.01300417 * t - 0.00000164 * t * t + 0.0000005036 * t * t * t;
	}

	/**
	 * Gets the Greenwich sidereal time in J2000.0
	 * @param t the T2000.
	 * @return the Greenwich sidereal time in J2000.0
	 */
	public static double greenwich2000(double t) {
		return 100.460618375 + 36000.7700537 * t + 0.000387933 * t * t - 0.00000002583 * t * t * t;
	}

	/**
	 * Gets the proper limiting magnitude to plot on chart, based on
	 * the pixel size.
	 * <p>
	 * This equation is determined so that the answer will satisfy the
	 * following table.
	 * <p>
	 * <table>
	 * <tr><th>Pixel size<br>(arcmin)</th><th>Ideal<br>Lm.</th><th>Calculated<br>Lm.</th></tr>
	 * <tr><td>100</td><td>10.0</td><td>9.5</td></tr>
	 * <tr><td>20</td><td>13.5</td><td>13.7</td></tr>
	 * <tr><td>12</td><td>14.5</td><td>15.0</td></tr>
	 * <tr><td>7.0</td><td>16.5</td><td>16.4</td></tr>
	 * <tr><td>3.5</td><td>18.0</td><td>18.2</td></tr>
	 * <tr><td>2.3</td><td>19.5</td><td>19.3</td></tr>
	 * <tr><td>1.3</td><td>20.5</td><td>20.8</td></tr>
	 * </table>
	 * @param pixel_size the pixel size in degree.
	 * @return the limiting magnitude.
	 */
	public static double getProperLimitingMagnitude ( double pixel_size ) {
		double m = -6.0 * Math.log(pixel_size * 3600) / Math.log(10.0) + 21.5;
		if (pixel_size * 3600 > 10) {
			double m2 = Math.log(pixel_size * 3600 / 10) / Math.log(10.0);
			m += m2;
		}
		return m;
	}

	/**
	 * Blends the specified two magnitude.
	 * @param mag1 the magnitude of first star.
	 * @param mag2 the magnitude of second star.
	 * @return the blended magnitude.
	 */
	public static double blendMagnitude ( double mag1, double mag2 ) {
		double val = Math.pow(Astro.MAG_STEP, - mag1);
		val += Math.pow(Astro.MAG_STEP, - mag2);
		double mag = - Math.log(val) / Math.log(Astro.MAG_STEP);
		return mag;
	}

	/**
	 * Gets the distance between the specified two angles.
	 * @param angle1 the angle.
	 * @param angle2 another angle.
	 * @return the distance between the specified two angles, which 
	 * is between 0 and 180.
	 */
	public static double getDistanceOfAngles ( double angle1, double angle2 ) {
		double dt = Math.abs(angle2 - angle1);
		dt = normalize(dt, 360);
		if (dt > 180.0)
			dt = 360.0 - dt;
		return dt;
	}

	/**
	 * Gets the mean value the specified two angles.
	 * @param angle1 the angle.
	 * @param angle2 another angle.
	 * @return the mean value the specified two angles, which is 
	 * between 0 and 360.
	 */
	public static double getMeanAngle ( double angle1, double angle2 ) {
		return getMeanAngle(angle1, angle2, 1.0, 1.0);
	}

	/**
	 * Gets the mean value the specified two angles, considering the
	 * specified weights.
	 * @param angle1  the angle.
	 * @param angle2  another angle.
	 * @param weight1 the weight of <tt>angle1</tt>.
	 * @param weight2 the weight of <tt>angle2</tt>.
	 * @return the mean value the specified two angles, which is 
	 * between 0 and 360.
	 */
	public static double getMeanAngle ( double angle1, double angle2, double weight1, double weight2 ) {
		angle1 = normalize(angle1, 360);
		angle2 = normalize(angle2, 360);

		double dt = Math.abs(angle2 - angle1);
		if (dt > 180.0) {
			angle1 += 360.0;
			dt = Math.abs(angle2 - angle1);
		}
		if (dt > 180.0) {
			angle1 -= 360.0 * 2.0;
			dt = Math.abs(angle2 - angle1);
		}
		dt = angle2 - angle1;
		double angle = angle1 + dt * weight2 / (weight1 + weight2);
		return normalize(angle, 360.0);
	}

	/**
	 * Gets the equatorial coordinates of the Sun.
	 * @param jd the Julian Day.
	 * @return the equatorial coordinates of the Sun.
	 */
	public static Xyz getSolarCoordinates ( double jd ) {
		double t = get_t1900(jd);

		double L = 279.696678 + 36000.768925 * t + 0.0003025 * t * t;
		L = normalize(L, 360.0);
		double B = 0.0;

		double g = 358.47583 + 35999.049750 * t - 0.000150 * t * t - 0.0000033 * t * t * t;
		g = normalize(g, 360.0);
		double g3 = ((1.882 - 0.016 * t) * Math.sin((57.24 + 150.27 * t) * RAD)+ 6.40 * Math.sin((231.19 + 20.20 * t) * RAD) + 0.266 * Math.sin((31.8 + 119.0 * t) * RAD)) / 3600.0;
		g += g3;
		L += (Math.sin(g * RAD) * (6910.057 - 17.240 * t - 0.052 * t * t)+ Math.sin(2.0 * g * RAD) * (72.338 - 0.361 * t) + Math.sin(3.0 * g * RAD) * (1.054 - 0.001 * t) + Math.sin(4.0 * g * RAD) * 0.018) / 3600.0;
		double R = 3057.0 - 15.0 * t + Math.cos(g * RAD) * (- 727412.0 + 1814.0 * t + 5.0 * t * t) + Math.cos(2.0 * g * RAD) * (- 9138.0 + 46.0 * t) + Math.cos(3.0 * g * RAD) * (- 145.0 + t) + Math.cos(4.0 * g * RAD) * (- 2.0);

		t = get_t1850(jd);

		double[] gp = new double[7];
		gp[1] = 248.07 + 1494.7235 * t;
		gp[2] = 114.50 + 585.17493 * t;
		gp[4] = 109.856 + 191.39977 * t;
		gp[5] = 148.031 + 30.34583 * t;
		gp[6] = 284.716 + 12.21794 * t;
		for (int m = 1 ; m < 7 ; m++) {
			if (m != 3)
				gp[m] = normalize(gp[m], 360.0);
		}

		double lrb_x = 0.0;
		double lrb_y = 0.0;
		double lrb_z = 0.0;

		for (int n = 0 ; n < 120 ; n++) {
			int p = (int)p_lr[n][0];
			double j = (double)p_lr[n][1];
			double i = (double)p_lr[n][2];
			double sl = (double)(p_lr[n][3] / 1000.0);
			double kl = (double)(p_lr[n][4] / 10000.0);
			double sr = (double)(p_lr[n][5] / 10.0);
			double kr = (double)(p_lr[n][6] / 10000.0);

			lrb_x += sl * Math.cos((kl - j * gp[p] - i * g) * RAD);
			lrb_y += sr * Math.cos((kr - j * gp[p] - i * g) * RAD);
		}

		for (int n = 0 ; n < 34 ; n++) {
			int p = (int)p_b[n][0];
			double j = (double)p_b[n][1];
			double i = (double)p_b[n][2];
			double s = (double)(p_b[n][3] / 1000.0);
			double k = (double)(p_b[n][4] / 10.0);

			lrb_z += s * Math.cos((k - j * gp[p] - i * g) * RAD);
		}

		L += lrb_x / 3600.0;
		B = - lrb_z / 3600.0;
		R += lrb_y / 10.0;

		t = get_t1900(jd);
		double D = 350.737486 + 12.19074919142 * t * 36525.0 - 0.00143611 * t * t + 0.00000189 * t *t * t;
		D = normalize(D, 360.0) * RAD;
		double g0 = 296.104688 + 13.06499244650 * t * 36525.0 + 0.00919167 * t * t + 0.00001439 * t * t * t;
		g0 = normalize(g0, 360.0) * RAD;
		double U = 11.250889 + 13.22935044901 * t * 36525.0 - 0.00321111 * t * t - 0.0000033 * t * t * t;
		U = normalize(U, 360.0) * RAD;
		double U1 = 20.513403 + 1.038601257586 * t * 36525.0 - 0.00177528 * t * t - 0.00000222 * t * t * t;
		U1 = normalize(U1, 360.0) * RAD;
		L += (6.454 * Math.sin(D) + 0.013 * Math.sin(3.0 * D) + 0.177 * Math.sin(D + g0) - 0.424 * Math.sin(D - g0) + 0.039 * Math.sin(3.0 * D - g0) - 0.064 * Math.sin(D + g * RAD) + 0.172 * Math.sin(D - g * RAD)) / 3600.0;
		R += 1336.0 * Math.cos(D) + 3.0 * Math.cos(3.0 * D) + 37.0 * Math.cos(D + g0) - 133.0 * Math.cos(D - g0) + 8.0 * Math.cos(3.0 * D - g0) - 14.0 * Math.cos(D + g * RAD) + 36.0 * Math.cos(D - g * RAD);
		B += (0.576 * Math.sin(U) + 0.016 * Math.sin(U + g0) - 0.047 * Math.sin(U - g0) + 0.021 * Math.sin(U - 2.0 * U1)) / 3600.0;
		L += g3 + 0.202 * Math.sin((315.6 + 893.3 * t) * RAD) / 3600.0;
		R = Math.pow(10.0, R / 100000000.0);

		t = get_t2000(jd);
		double epsilon = epsilon2000(t);

		Coor coor_ecliptic = new Coor(L, B);
		Coor coor_equatorial = coor_ecliptic.rotate(- epsilon);
		Xyz xyz = coor_equatorial.convertToXyz();

		xyz.setX(xyz.getX() * R);
		xyz.setY(xyz.getY() * R);
		xyz.setZ(xyz.getZ() * R);

		return xyz;
	}

	/**
	 * The parameters to calculate solar longitude.
	 */
	private static final long p_lr[][] = {
		{ 1,  -1,   1,   13, 2430000,   280, 3350000 },
		{ 1,  -1,   2,    5, 2250000,    60, 1300000 },
		{ 1,  -1,   3,   15, 3570000,   180, 2670000 },
		{ 1,  -1,   4,   23, 3260000,    50, 2390000 },
		{ 2,  -1,   0,   75, 2966000,   940, 2050000 },
		{ 2,  -1,   1, 4838, 2991017, 23590, 2090800 },
		{ 2,  -1,   2,   74, 2079000,   690, 3485000 },
		{ 2,  -1,   3,    9, 2490000,   160, 3300000 },
		{ 2,  -2,   0,    3, 1620000,    40,  900000 },
		{ 2,  -2,   1,  116, 1489000,  1600,  584000 },
		{ 2,  -2,   2, 5526, 1483133, 68420,  583183 },
		{ 2,  -2,   3, 2497, 3159433,  8690, 2267000 },
		{ 2,  -2,   4,   44, 3114000,   520,  388000 },
		{ 2,  -3,   2,   13, 1760000,   210,  900000 },
		{ 2,  -3,   3,  666, 1777100, 10450,  875700 },
		{ 2,  -3,   4, 1559, 3452533, 14970, 2552500 },
		{ 2,  -3,   5, 1024, 3181500,  1940,  495000 },
		{ 2,  -3,   6,   17, 3150000,   190,  430000 },
		{ 2,  -4,   3,    3, 1980000,    60,  900000 },
		{ 2,  -4,   4,  210, 2062000,  3760, 1162800 },
		{ 2,  -4,   5,  144, 1954000,  1960, 1052000 },
		{ 2,  -4,   6,  152, 3438000,   940, 2548000 },
		{ 2,  -4,   7,    6, 3220000,    60,  590000 },
		{ 2,  -5,   5,   84, 2356000,  1630, 1454000 },
		{ 2,  -5,   6,   37, 2218000,   590, 1322000 },
		{ 2,  -5,   7,  123, 1953000,  1410, 1054000 },
		{ 2,  -5,   8,  154, 3596000,   260, 2700000 },
		{ 2,  -6,   6,   38, 2641000,   800, 1743000 },
		{ 2,  -6,   7,   14, 2530000,   250, 1640000 },
		{ 2,  -6,   8,   10, 2300000,   140, 1350000 },
		{ 2,  -6,   9,   14,  120000,   120, 2840000 },
		{ 2,  -7,   7,   20, 2950000,   420, 2035000 },
		{ 2,  -7,   8,    6, 2790000,   120, 1940000 },
		{ 2,  -7,   9,    3, 2880000,    40, 1660000 },
		{ 2,  -7,  10,    0,       0,    40, 1350000 },
		{ 2,  -8,   8,   11, 3220000,   240, 2340000 },
		{ 2,  -8,   9,    0,       0,    60, 2180000 },
		{ 2,  -8,  12,   42, 2592000,   440, 1697000 },
		{ 2,  -8,  13,    0,       0,   120, 2220000 },
		{ 2,  -8,  14,   32,  488000,   330, 1387000 },
		{ 2,  -9,   9,    6, 3510000,   130, 2610000 },
		{ 2,  -9,  10,    0,       0,    40, 2560000 },
		{ 2, -10,  10,    3,  180000,    80, 2930000 },
		{ 4,   1,  -2,    6, 2180000,    80, 1300000 },
		{ 4,   1,  -1,  273, 2177000,  1500, 1277000 },
		{ 4,   1,   0,   48, 2603000,   280, 3470000 },
		{ 4,   2,  -3,   41, 3460000,   520, 2554000 },
		{ 4,   2,  -2, 2043, 3438883, 20570, 2538283 },
		{ 4,   2,  -1, 1770, 2004016,  1510, 2950000 },
		{ 4,   2,   0,   28, 1480000,   310, 2343000 },
		{ 4,   3,  -4,    4, 2840000,    60, 1800000 },
		{ 4,   3,  -3,  129, 2942000,  1680, 2035000 },
		{ 4,   3,  -2,  425, 3388800,  2150, 2490000 },
		{ 4,   3,  -1,    8,   70000,    60,  900000 },
		{ 4,   4,  -4,   34,  710000,   490, 3397000 },
		{ 4,   4,  -3,  500, 1051800,  4780,  151700 },
		{ 4,   4,  -2,  585, 3340600,  1050,  659000 },
		{ 4,   4,  -1,    9, 3250000,   100,  530000 },
		{ 4,   5,  -5,    7, 1720000,   120,  900000 },
		{ 4,   5,  -4,   85,  546000,  1070, 3246000 },
		{ 4,   5,  -3,  204, 1008000,   890,  110000 },
		{ 4,   5,  -2,    3,  180000,    30, 1080000 },
		{ 4,   6,  -6,    0,       0,    50, 2170000 },
		{ 4,   6,  -5,   20, 1860000,   300,  957000 },
		{ 4,   6,  -4,  154, 2274000,  1390, 1373000 },
		{ 4,   6,  -3,  101,  963000,   270, 1880000 },
		{ 4,   7,  -6,    6, 3010000,   100, 2090000 },
		{ 4,   7,  -5,   49, 1765000,   600,  862000 },
		{ 4,   7,  -4,  106, 2227000,   380, 1329000 },
		{ 4,   8,  -7,    3,  720000,    50, 3490000 },
		{ 4,   8,  -6,   10, 3070000,   150, 2170000 },
		{ 4,   8,  -5,   52, 3489000,   450, 2597000 },
		{ 4,   8,  -4,   21, 2152000,    80, 3100000 },
		{ 4,   9,  -7,    4,  570000,    60, 3290000 },
		{ 4,   9,  -6,   28, 2980000,   340, 2081000 },
		{ 4,   9,  -5,   62, 3460000,   170, 2570000 },
		{ 4,  10,  -7,    5,  680000,    80, 3370000 },
		{ 4,  10,  -6,   19, 1110000,   150,  230000 },
		{ 4,  10,  -5,    5, 3380000,     0,       0 },
		{ 4,  11,  -7,   17,  590000,   200, 3300000 },
		{ 4,  11,  -6,   44, 1059000,    90,  210000 },
		{ 4,  12,  -7,    6, 2320000,    50, 1430000 },
		{ 4,  13,  -8,   13, 1840000,   150,  940000 },
		{ 4,  13,  -7,   45, 2278000,    50, 1430000 },
		{ 4,  15,  -9,   21, 3090000,   220, 2200000 },
		{ 4,  15,  -8,    0,       0,    60, 2610000 },
		{ 4,  17, -10,    4, 2430000,    40, 1530000 },
		{ 4,  17,  -9,   26, 1130000,     0,       0 },
		{ 5,   1,  -3,    3, 1980000,    50, 1120000 },
		{ 5,   1,  -2,  163, 1986000,  2080, 1120000 },
		{ 5,   1,  -1, 7208, 1795317, 70670,  895450 },
		{ 5,   1,   0, 2600, 2632167,  2440, 3386000 },
		{ 5,   1,   1,   73, 2763000,   800,   65000 },
		{ 5,   2,  -3,   69,  808000,  1030, 3505000 },
		{ 5,   2,  -2, 2731,  871450, 40260, 3571083 },
		{ 5,   2,  -1, 1610, 1094933, 14590,  194667 },
		{ 5,   2,   0,   73, 2526000,    80, 2630000 },
		{ 5,   3,  -4,    5, 1580000,    90,  690000 },
		{ 5,   3,  -3,  164, 1705000,  2810,  812000 },
		{ 5,   3,  -2,  556,  826500,  8030, 3525600 },
		{ 5,   3,  -1,  210,  985000,  1740,   86000 },
		{ 5,   4,  -4,   16, 2590000,   290, 1700000 },
		{ 5,   4,  -3,   44, 1682000,   740,  799000 },
		{ 5,   4,  -2,   80,  777000,  1130, 3477000 },
		{ 5,   4,  -1,   23,  930000,   170,   30000 },
		{ 5,   5,  -5,    0,       0,    30, 2520000 },
		{ 5,   5,  -4,    5, 2590000,   100, 1690000 },
		{ 5,   5,  -3,    7, 1640000,   120,  760000 },
		{ 5,   5,  -2,    9,  710000,   140, 3430000 },
		{ 6,   1,  -2,   11, 1050000,   150,  110000 },
		{ 6,   1,  -1,  419, 1005800,  4290,  106000 },
		{ 6,   1,   0,  320, 2694600,    80, 3530000 },
		{ 6,   1,   1,    8, 2700000,    80,       0 },
		{ 6,   2,  -3,    0,       0,    30, 1980000 },
		{ 6,   2,  -2,  108, 2906000,  1620, 2006000 },
		{ 6,   2,  -1,  112, 2936000,  1120, 2031000 },
		{ 6,   2,   0,   17, 2770000,     0,       0 },
		{ 6,   3,  -2,   21, 2890000,   320, 2001000 },
		{ 6,   3,  -1,   17, 2910000,   170, 2010000 },
		{ 6,   4,  -2,    3, 2880000,    40, 1940000 }
	};

	/**
	 * The parameters to calculate solar latitude.
	 */
	private static final int p_b[][] = {
		{ 2, -1,  0,   29, 1450 },
		{ 2, -1,  1,    5, 3230 },
		{ 2, -1,  2,   92,  937 },
		{ 2, -1,  3,    7, 2620 },
		{ 2, -2,  1,   23, 1730 },
		{ 2, -2,  2,   12, 1490 },
		{ 2, -2,  3,   67, 1230 },
		{ 2, -2,  4,   14, 1110 },
		{ 2, -3,  2,   14, 2010 },
		{ 2, -3,  3,    8, 1870 },
		{ 2, -3,  4,  210, 1518 },
		{ 2, -3,  5,    7, 1530 },
		{ 2, -3,  6,    4, 2960 },
		{ 2, -4,  3,    6, 2320 },
		{ 2, -4,  5,   31,   18 },
		{ 2, -4,  6,   12, 1800 },
		{ 2, -5,  6,    9,  270 },
		{ 2, -5,  7,   19,  180 },
		{ 2, -6,  5,    6, 2880 },
		{ 2, -6,  7,    4,  570 },
		{ 2, -6,  8,    4,  570 },
		{ 2, -8, 12,   10,  610 },
		{ 4,  2, -2,    8,  900 },
		{ 4,  2,  0,    8, 3460 },
		{ 4,  4, -3,    7, 1880 },
		{ 5,  1, -2,    7, 1800 },
		{ 5,  1, -1,   17, 2730 },
		{ 5,  1,  0,   16, 1800 },
		{ 5,  1,  1,   23, 2680 },
		{ 5,  2, -1,  166, 2655 },
		{ 5,  3, -2,    6, 1710 },
		{ 5,  3, -1,   18, 2670 },
		{ 6,  1, -1,    6, 2600 },
		{ 6,  1,  1,    6, 2800 }
	};
}
