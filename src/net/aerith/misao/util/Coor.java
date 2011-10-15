/*
 * @(#)Coor.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.StringTokenizer;

/**
 * The <code>Coor</code> represents a set of R.A. and Decl. They are 
 * expressed in double value of degree.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 February 12
 */

public class Coor implements Coordinates {
	/**
	 * R.A.
	 */
	protected double ra = 0.0;

	/**
	 * Decl.
	 */
	protected double decl = 0.0;

	/**
	 * The number of accuracy to output, representing 
	 * <tt>12h34m56s.78 +01o23'45".6</tt>.
	 */
	public final static byte ACCURACY_100M_ARCSEC = 0;

	/**
	 * The number of accuracy to output, representing 
	 * <tt>12h34m56s.789 +01o23'45".67</tt>.
	 */
	public final static byte ACCURACY_10M_ARCSEC = 1;

	/**
	 * The number of accuracy to output, representing 
	 * <tt>12h34m56s.7 +01o23'45"</tt>.
	 */
	public final static byte ACCURACY_ARCSEC = 2;

	/**
	 * The number of accuracy to output, representing 
	 * <tt>12h34m56s +01o23'45"</tt>.
	 */
	public final static byte ACCURACY_ROUGH_ARCSEC = 3;

	/**
	 * The number of accuracy to output, representing 
	 * <tt>12h34m.56 +01o23'.4</tt>.
	 */
	public final static byte ACCURACY_100M_ARCMIN = 4;

	/**
	 * The number of accuracy to output, representing 
	 * <tt>12h34m56s +01o23'.4</tt>.
	 */
	public final static byte ACCURACY_100M_ARCMIN_HOURSEC = 5;

	/**
	 * The number of accuracy to output, representing 
	 * <tt>12h34m.5 +01o23'</tt>.
	 */
	public final static byte ACCURACY_ARCMIN = 6;

	/**
	 * The number of accuracy to output, representing 
	 * <tt>12h34m56s.7890 +01o23'45".678</tt>.
	 */
	public final static byte ACCURACY_1M_ARCSEC = 7;

	/**
	 * Constructs an empty <code>Coor</code>. The R.A. and Decl. are
	 * set at (0,0).
	 */
	public Coor ( ) {
		setRA(0.0);
		setDecl(0.0);
	}

	/**
	 * Constructs a <code>Coor</code> by copy of a <code>Coor</code>.
	 * @param source_coor the source R.A. and Decl. to be copied.
	 */
	public Coor ( Coordinates source_coor ) {
		setRA(source_coor.getRA());
		setDecl(source_coor.getDecl());
	}

	/**
	 * Constructs a <code>Coor</code> with R.A. and Decl.
	 * @param initial_ra   the R.A in degree.
	 * @param initial_decl the Decl.
	 */
	public Coor ( double initial_ra, double initial_decl ) {
		setRA(initial_ra);
		setDecl(initial_decl);
	}

	/**
	 * Constructs a <code>Coor</code> with R.A. and Decl.
	 * @param ra_h       the hour of R.A.
	 * @param ra_m       the minute of R.A.
	 * @param ra_s       the second of R.A.
	 * @param south_flag true if the Decl. is in southern hemisphere.
	 * @param decl_d     the absolute degree value of Decl.
	 * @param decl_m     the absolute minute value of Decl.
	 * @param decl_s     the absolute second value of Decl.
	 */
	public Coor ( int ra_h, int ra_m, double ra_s, boolean south_flag, int decl_d, int decl_m, double decl_s ) {
		this(((double)ra_h + (double)ra_m / 60.0 + ra_s / 3600.0) * 15.0, (south_flag ? -1.0 : 1.0) * ((double)decl_d + (double)decl_m / 60.0 + decl_s / 3600.0));
	}

	/**
	 * Gets R.A. 
	 * @return the R.A.
	 */
	public double getRA ( ) {
		return ra;
	}

	/**
	 * Gets hour of R.A. 
	 * @return the hour of R.A.
	 */
	public int getRA_h ( ) {
		return (int)Math.abs(getRA() / 15.0 + 0.00000001);
	}

	/**
	 * Gets minute of R.A. 
	 * @return the minute of R.A.
	 */
	public int getRA_m ( ) {
		return (int)Math.abs((getRA() / 15.0 - getRA_h()) * 60.0 + 0.0000001);
	}

	/**
	 * Gets second of R.A. 
	 * @return the second of R.A.
	 */
	public double getRA_s ( ) {
		return Math.abs((getRA() / 15.0 - getRA_h()) * 60.0 - getRA_m()) * 60.0;
	}

	/**
	 * Sets R.A. 
	 * @param new_ra the new R.A.
	 */
	public void setRA ( double new_ra ) {
		ra = new_ra;
	}

	/**
	 * Gets Decl.
	 * @return the Decl.
	 */
	public double getDecl ( ) {
		return decl;
	}

	/**
	 * Gets absolute degree value of Decl.
	 * @return the absolute degree value of Decl.
	 */
	public int getAbsDecl_d ( ) {
		double d = getDecl();
		if (d < 0.0)
			d = - d;
		return (int)Math.abs(d + 0.0000001);
	}

	/**
	 * Gets absolute arcmin value of Decl.
	 * @return the absolute arcmin value of Decl.
	 */
	public int getAbsDecl_m ( ) {
		double d = getDecl();
		if (d < 0.0)
			d = - d;
		return (int)Math.abs((d - getAbsDecl_d()) * 60.0 + 0.000001);
	}

	/**
	 * Gets absolute arcsec value of Decl.
	 * @return the absolute arcsec value of Decl.
	 */
	public double getAbsDecl_s ( ) {
		double d = getDecl();
		if (d < 0.0)
			d = - d;
		return Math.abs((d - getAbsDecl_d()) * 60.0 - getAbsDecl_m()) * 60.0;
	}

	/**
	 * Sets Decl.
	 * @param new_decl the new Decl.
	 */
	public void setDecl ( double new_decl ) {
		decl = new_decl;
	}

	/**
	 * Gets the angular distance from this R.A. and Decl. to the 
	 * specified R.A. and Decl.
	 * @param coor the target R.A. and Decl.
	 * @return the angular distance in degree.
	 */
	public double getAngularDistanceTo ( Coordinates coor ) {
		if (ra == coor.getRA()  &&  decl == coor.getDecl())
			return 0.0;

		return Math.acos(Math.sin(decl * Astro.RAD) * Math.sin(coor.getDecl() * Astro.RAD) + Math.cos(decl * Astro.RAD) * Math.cos(coor.getDecl() * Astro.RAD) * Math.cos((coor.getRA() - ra) * Astro.RAD) ) / Astro.RAD;
	}

	/**
	 * Gets the position angle from this R.A. and Decl. to the 
	 * specified R.A. and Decl.
	 * @param coor the target R.A. and Decl.
	 * @return the position angle.
	 */
	public double getPositionAngleTo ( Coordinates coor ) {
		if (ra == coor.getRA()  &&  decl == coor.getDecl())
			return 0.0;

		if (decl >= 90.0) {
			double A = 180.0 - coor.getRA() + ra;
			if (A < 0.0)
				A += 360.0;
			if (A >= 0.0)
				A -= 360.0;
			return A;
		}
		if (decl <= -90.0) {
			double A = coor.getRA() - ra;
			if (A < 0.0)
				A += 360.0;
			if (A >= 0.0)
				A -= 360.0;
			return A;
		}

		double ad = getAngularDistanceTo(coor);
		if (ad <= 0.0  ||  ad >= 180.0)
			return 0.0;

		double sinA = Math.cos(coor.getDecl() * Astro.RAD) * Math.sin((coor.getRA() - ra) * Astro.RAD) / Math.sin(ad * Astro.RAD);
		double cosA = (Math.sin(coor.getDecl() * Astro.RAD) - Math.sin(decl * Astro.RAD) * Math.cos(ad * Astro.RAD)) / (Math.cos(decl * Astro.RAD) * Math.sin(ad * Astro.RAD));
		double A = Math.atan2(sinA, cosA) / Astro.RAD;
		if (A < 0.0)
			A += 360.0;
		return A;
	}

	/**
	 * Moves this coordinates to the specified angular distance into
	 * the specified position angle.
	 * @param distance the angular distance to move in degree.
	 * @param pa       the positoin angle to move.
	 */
	public void move ( double distance, double pa ) {
		ChartMapFunction cmf = new ChartMapFunction(this, 1.0, pa);
		Coor moved_coor = cmf.mapXYToCoordinates(new Position(0.0, - distance));
		setRA(moved_coor.getRA());
		setDecl(moved_coor.getDecl());
	}

	/**
	 * Calculates the residuals from the specified <code>Coor</code>.
	 * @param coor the R.A. and Decl. to calculate residuals from.
	 * @return a set of residuals of R.A. and Decl.
	 */
	public Coor residual ( Coor coor ) {
		double d_ra = Astro.normalize(getRA() - coor.getRA(), 360.0);
		if (d_ra >= 180.0)
			d_ra -= 360.0;
		d_ra *= Math.cos((getDecl() + coor.getDecl()) / 2.0 * Astro.RAD);
		double d_decl = getDecl() - coor.getDecl();
		return new Coor(d_ra, d_decl);
	}

	/**
	 * Converts this R.A. and Decl. to the (x,y,z) position. The 
	 * length of the new vector is 1.
	 * @return the (x,y,z) position.
	 */
	public Xyz convertToXyz ( ) {
		double sin_a,cos_a,sin_d,cos_d;

		sin_a = Math.sin(getRA() * Astro.RAD);
		cos_a = Math.cos(getRA() * Astro.RAD);
		sin_d = Math.sin(getDecl() * Astro.RAD);        
		cos_d = Math.cos(getDecl() * Astro.RAD);    

		double x, y, z;
		x = cos_d * cos_a;
		y = cos_d * sin_a;
		z = sin_d;

		return new Xyz(x,y,z);
	}

	/**
	 * Rotates the coordinates.
	 * @param angle the angle to rotate.
	 * @return the rotated coordinates.
	 */
	public Coor rotate ( double angle ) {
		double sin_a,cos_a,sin_d,cos_d,sin_e,cos_e;

		sin_a = Math.sin(getRA() * Astro.RAD);
		cos_a = Math.cos(getRA() * Astro.RAD);
		sin_d = Math.sin(getDecl() * Astro.RAD);        
		cos_d = Math.cos(getDecl() * Astro.RAD);    
		sin_e = Math.sin(angle * Astro.RAD);
		cos_e = Math.cos(angle * Astro.RAD);

		double x, y, z;
		x = cos_d * cos_a;
		y = sin_d * sin_e + cos_d * sin_a * cos_e;
		z = sin_d * cos_e - cos_d * sin_a * sin_e;
		Xyz xyz = new Xyz(x, y, z);

		return xyz.convertToCoor();
	}

	/**
	 * Changes the precession. The result is stored in this object.
	 * @param src_jd the Julian Day of the source precession.
	 * @param dst_jd the Julian Day of the destination precession.
	 */
	public void precession ( JulianDay src_jd, JulianDay dst_jd ) {
		Xyz xyz = convertToXyz();
		xyz.precession(src_jd, dst_jd);
		Coor coor = xyz.convertToCoor();
		setRA(coor.getRA());
		setDecl(coor.getDecl());
	}

	/**
	 * Creates a <code>Coor</code> object from the <code>String</code>
	 * object which represents the R.A. and Decl. in various formats.
	 * @param string the string which represents R.A. and Decl.
	 * @return a new <code>Coor</code> of the specified string.
	 */
	public static Coor create ( String string ) {
		String s = string.trim();

		// 12h34m56s.78 +12o34'56".7
		if (s.indexOf('h') >= 0  &&  s.indexOf('m') >= 0  &&  s.indexOf('s') >= 0) {
			// 12h34m56s.78
			int ra_h = Integer.valueOf(s.substring(0,s.indexOf('h'))).intValue();
			s = s.substring(s.indexOf('h')+1).trim();
			int ra_m = Integer.valueOf(s.substring(0,s.indexOf('m'))).intValue();
			s = s.substring(s.indexOf('m')+1).trim();
			double ra_s = 0.0;
			if (s.indexOf("s.") >= 0) {
				// 56s.78
				String ra_s_str = s.substring(0,s.indexOf('s')) + s.substring(s.indexOf('s')+1,s.indexOf(' '));
				ra_s = Format.doubleValueOf(ra_s_str);
			} else if (s.indexOf("s") >= 0) {
				// 56.78s
				ra_s = Format.doubleValueOf(s.substring(0,s.indexOf('s')));
			} else {
				// 56.78
				ra_s = Format.doubleValueOf(s.substring(0,s.indexOf(' ')));
			}

			// +12o34'56".7
			s = s.substring(s.indexOf(' ')+1).trim();
			boolean south_flag = (s.charAt(0) == '-');
			if (s.charAt(0) == '+'  ||  s.charAt(0) == '-')
				s = s.substring(1);
			int decl_d = 0;
			if (s.indexOf("d") >= 0) {
				// +12d
				decl_d = Integer.valueOf(s.substring(0,s.indexOf('d'))).intValue();
				s = s.substring(s.indexOf('d')+1).trim();
			} else {
				// +12o
				decl_d = Integer.valueOf(s.substring(0,s.indexOf('o'))).intValue();
				s = s.substring(s.indexOf('o')+1).trim();
			}
			int decl_m = Integer.valueOf(s.substring(0,s.indexOf('\''))).intValue();
			s = s.substring(s.indexOf('\'')+1).trim();
			double decl_s = 0.0;
			if (s.indexOf('.') == 0) {
				// 34'.5
				decl_s = Format.doubleValueOf("0" + s) * 60.0;
			} else if (s.indexOf("\".") >= 0) {
				// 56".7
				String decl_s_str = s.substring(0,s.indexOf('"')) + s.substring(s.indexOf('\"')+1);
				decl_s = Format.doubleValueOf(decl_s_str);
			} else if (s.indexOf("\"") >= 0) {
				// 56.7"
				decl_s = Format.doubleValueOf(s.substring(0,s.indexOf('"')));
			} else {
				// 56.7
				decl_s = Format.doubleValueOf(s);
			}

			return new Coor(ra_h, ra_m, ra_s, south_flag, decl_d, decl_m, decl_s);
		}

		// 12h34m.56 +12o34'.5
		if (s.indexOf('h') >= 0  &&  s.indexOf('m') >= 0) {
			// 12h34m.56
			int ra_h = Integer.valueOf(s.substring(0,s.indexOf('h'))).intValue();
			s = s.substring(s.indexOf('h')+1).trim();
			double ra_m = 0.0;
			if (s.indexOf("m.") >= 0) {
				// 34m.56
				String ra_m_str = s.substring(0,s.indexOf('m')) + s.substring(s.indexOf('m')+1,s.indexOf(' '));
				ra_m = Format.doubleValueOf(ra_m_str);
			} else if (s.indexOf("m") >= 0) {
				// 34.56m
				ra_m = Format.doubleValueOf(s.substring(0,s.indexOf('m')));
			} else {
				// 34.56
				ra_m = Format.doubleValueOf(s.substring(0,s.indexOf(' ')));
			}

			// +12o34'.5
			s = s.substring(s.indexOf(' ')+1).trim();
			boolean south_flag = (s.charAt(0) == '-');
			if (s.charAt(0) == '+'  ||  s.charAt(0) == '-')
				s = s.substring(1);
			int decl_d = 0;
			if (s.indexOf("d") >= 0) {
				// +12d
				decl_d = Integer.valueOf(s.substring(0,s.indexOf('d'))).intValue();
				s = s.substring(s.indexOf('d')+1).trim();
			} else {
				// +12o
				decl_d = Integer.valueOf(s.substring(0,s.indexOf('o'))).intValue();
				s = s.substring(s.indexOf('o')+1).trim();
			}
			double decl_m = 0.0;
			if (s.indexOf("'.") >= 0) {
				// 34'.5
				String decl_m_str = s.substring(0,s.indexOf('\'')) + s.substring(s.indexOf('\'')+1);
				decl_m = Format.doubleValueOf(decl_m_str);
			} else if (s.indexOf("'") >= 0) {
				// 34.5'
				decl_m = Format.doubleValueOf(s.substring(0,s.indexOf('\'')));
			} else {
				// 34.5
				decl_m = Format.doubleValueOf(s);
			}

			int ra_m_int = (int)ra_m;
			double ra_s = (ra_m - (double)ra_m_int) * 60.0;
			int decl_m_int = (int)decl_m;
			double decl_s = (decl_m - (double)decl_m_int) * 60.0;

			return new Coor(ra_h, ra_m_int, ra_s, south_flag, decl_d, decl_m_int, decl_s);
		}

		StringTokenizer st = new StringTokenizer(s);
		s = "";
		while (st.hasMoreTokens()) {
			String s2 = st.nextToken();
			s = s + s2;

			if (s2.equals("+")  ||  s2.equals("-")) {
			} else if (st.hasMoreTokens()) {
				s = s + " ";
			}
		}
		st = new StringTokenizer(s);

		if (st.countTokens() == 2) {
			// 123456.78 +123456.7
			String ra_str = st.nextToken();
			String decl_str = st.nextToken();

			int ra_h = Integer.valueOf(ra_str.substring(0,2)).intValue();
			int ra_m = Integer.valueOf(ra_str.substring(2,4)).intValue();
			double ra_s = Format.doubleValueOf(ra_str.substring(4));
			boolean south_flag = (decl_str.charAt(0) == '-');
			if (decl_str.charAt(0) == '+'  ||  decl_str.charAt(0) == '-')
				decl_str = decl_str.substring(1);
			int decl_d = Integer.valueOf(decl_str.substring(0,2)).intValue();
			int decl_m = Integer.valueOf(decl_str.substring(2,4)).intValue();
			double decl_s = Format.doubleValueOf(decl_str.substring(4));

			return new Coor(ra_h, ra_m, ra_s, south_flag, decl_d, decl_m, decl_s);
		}

		if (st.countTokens() == 5) {
			// 12 34 56 +12 34.5
			int ra_h = Integer.valueOf(st.nextToken()).intValue();
			int ra_m = Integer.valueOf(st.nextToken()).intValue();
			double ra_s = Format.doubleValueOf(st.nextToken());

			s = st.nextToken();
			boolean south_flag = (s.charAt(0) == '-');
			if (s.charAt(0) == '+'  ||  s.charAt(0) == '-')
				s = s.substring(1);
			int decl_d = Math.abs(Integer.valueOf(s).intValue());
			double f = Math.abs(Format.doubleValueOf(st.nextToken()));
			int decl_m = (int)f;
			double decl_s = (f - (double)decl_m) * 60.0;

			return new Coor(ra_h, ra_m, ra_s, south_flag, decl_d, decl_m, decl_s);
		}

		if (st.countTokens() == 4) {
			// 12 34.56 +12 34.5
			int ra_h = Integer.valueOf(st.nextToken()).intValue();
			double f = Format.doubleValueOf(st.nextToken());
			int ra_m = (int)f;
			double ra_s = (f - (double)ra_m) * 60.0;

			s = st.nextToken();
			boolean south_flag = (s.charAt(0) == '-');
			if (s.charAt(0) == '+'  ||  s.charAt(0) == '-')
				s = s.substring(1);
			int decl_d = Math.abs(Integer.valueOf(s).intValue());
			f = Math.abs(Format.doubleValueOf(st.nextToken()));
			int decl_m = (int)f;
			double decl_s = (f - (double)decl_m) * 60.0;

			return new Coor(ra_h, ra_m, ra_s, south_flag, decl_d, decl_m, decl_s);
		}

		// 12 34 56.78 +12 34 56.7
		int ra_h = Integer.valueOf(st.nextToken()).intValue();
		int ra_m = Integer.valueOf(st.nextToken()).intValue();
		double ra_s = Format.doubleValueOf(st.nextToken());
		s = st.nextToken();
		boolean south_flag = (s.charAt(0) == '-');
		if (s.charAt(0) == '+'  ||  s.charAt(0) == '-')
			s = s.substring(1);
		int decl_d = Math.abs(Integer.valueOf(s).intValue());
		int decl_m;
		double decl_s;
		s = st.nextToken();
		if (s.indexOf('.') >= 0) {
			// +12 34.56
			double f = Math.abs(Format.doubleValueOf(s));
			decl_m = (int)f;
			decl_s = (f - (double)decl_m) * 60.0;
		} else {
			// +12 34 56.7
			decl_m = Math.abs(Integer.valueOf(s).intValue());
			decl_s = Math.abs(Format.doubleValueOf(st.nextToken()));
			return new Coor(ra_h, ra_m, ra_s, south_flag, decl_d, decl_m, decl_s);
		}

		return new Coor();
	}

	/**
	 * Gets the accuracy of <code>Coor</code> object from the 
	 * <code>String</code> object which represents the R.A. and Decl.
	 * in various formats.
	 * @param string the string which represents R.A. and Decl.
	 * @return the accuracy.
	 */
	public static byte getAccuracy ( String string ) {
		String s = string.trim();

		// 12h34m56s.78 +12o34'56".7
		if (s.indexOf('h') >= 0  &&  s.indexOf('m') >= 0  &&  s.indexOf('s') >= 0) {
			int len = 0;

			s = s.substring(0, s.indexOf(' '));
			s = s.substring(s.indexOf('m')+1).trim();
			if (s.indexOf("s.") >= 0) {
				// 56s.78
				len = s.length() - s.indexOf('.') - 1;
			} else if (s.indexOf("s") >= 0) {
				// 56.78s
				if (s.indexOf('.') >= 0)
					len = s.indexOf('s') - s.indexOf('.') - 1;
			} else {
				// 56.78
				if (s.indexOf('.') >= 0)
					len = s.length() - s.indexOf('.') - 1;
			}
			if (len >= 4)
				return ACCURACY_1M_ARCSEC;
			if (len == 3)
				return ACCURACY_10M_ARCSEC;
			if (len == 2)
				return ACCURACY_100M_ARCSEC;
			if (len == 1)
				return ACCURACY_ARCSEC;

			// 12h34m56s +12o34'56"
			// 12h34m56s +12o34'.5
			s = string.trim();
			s = s.substring(s.indexOf(' ')+1).trim();
			if (s.indexOf('"') >= 0)
				return ACCURACY_ROUGH_ARCSEC;
			return ACCURACY_100M_ARCMIN_HOURSEC;
		}

		// 12h34m.56 +12o34'.5
		if (s.indexOf('h') >= 0  &&  s.indexOf('m') >= 0) {
			int len = 0;

			s = s.substring(0, s.indexOf(' '));
			s = s.substring(s.indexOf('h')+1).trim();
			if (s.indexOf("m.") >= 0) {
				// 34m.56
				len = s.length() - s.indexOf('.') - 1;
			} else if (s.indexOf("m") >= 0) {
				// 34.56m
				len = s.indexOf('m') - s.indexOf('.') - 1;
			} else {
				// 34.56
				if (s.indexOf('.') >= 0)
					len = s.length() - s.indexOf('.') - 1;
			}
			if (len >= 2)
				return ACCURACY_100M_ARCMIN;
			return ACCURACY_ARCMIN;
		}

		StringTokenizer st = new StringTokenizer(s);
		s = "";
		while (st.hasMoreTokens()) {
			String s2 = st.nextToken();
			s = s + s2;

			if (s2.equals("+")  ||  s2.equals("-")) {
			} else if (st.hasMoreTokens()) {
				s = s + " ";
			}
		}
		st = new StringTokenizer(s);

		if (st.countTokens() == 2) {
			// 123456.78 +123456.7
			s = st.nextToken().substring(4);

			int len = 0;
			if (s.indexOf('.') >= 0)
				len = s.length() - s.indexOf('.') - 1;
			if (len >= 4)
				return ACCURACY_1M_ARCSEC;
			if (len == 3)
				return ACCURACY_10M_ARCSEC;
			if (len == 2)
				return ACCURACY_100M_ARCSEC;
			if (len == 1)
				return ACCURACY_ARCSEC;

			// 123456 +123456
			// 123456 +1234.5
			s = st.nextToken();
			if (s.indexOf('.') >= 0)
				return ACCURACY_100M_ARCMIN_HOURSEC;
			return ACCURACY_ROUGH_ARCSEC;
		}

		if (st.countTokens() == 5) {
			// 12 34 56 +12 34.5
			return ACCURACY_100M_ARCMIN_HOURSEC;
		}

		if (st.countTokens() == 4) {
			// 12 34.56 +12 34.5
			st.nextToken();
			s = st.nextToken();

			int len = 0;
			if (s.indexOf('.') >= 0)
				len = s.length() - s.indexOf('.') - 1;
			if (len >= 2)
				return ACCURACY_100M_ARCMIN;
			return ACCURACY_ARCMIN;
		}

		// 12 34 56.78 +12 34 56.7
		st.nextToken();
		st.nextToken();
		s = st.nextToken();

		int len = 0;
		if (s.indexOf('.') >= 0)
			len = s.length() - s.indexOf('.') - 1;
		if (len >= 4)
			return ACCURACY_1M_ARCSEC;
		if (len == 3)
			return ACCURACY_10M_ARCSEC;
		if (len == 2)
			return ACCURACY_100M_ARCSEC;
		if (len == 1)
			return ACCURACY_ARCSEC;
		return ACCURACY_ROUGH_ARCSEC;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>12h34m56s.78 +01o23'45".6</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputString ( ) {
		return getOutputStringTo100mArcsecWithUnit();
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>12h34m56s.78 +01o23'45".6</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringTo100mArcsecWithUnit ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2) + "h";
		s += Format.formatIntZeroPadding(getRA_m(), 2) + "m";
		String ra_s = Format.formatDoubleZeroPadding(getRA_s(), 5, 2);
		s += ra_s.substring(0, 2) + "s" + ra_s.substring(2) + " ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2) + "o";
		s += Format.formatIntZeroPadding(getAbsDecl_m(), 2) + "'";
		String decl_s = Format.formatDoubleZeroPadding(getAbsDecl_s(), 4, 2);
		s += decl_s.substring(0, 2) + "\"" + decl_s.substring(2);

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>12 34 56.78 +01 23 45.6</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringTo100mArcsecWithoutUnit ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2) + " ";
		s += Format.formatIntZeroPadding(getRA_m(), 2) + " ";
		s += Format.formatDoubleZeroPadding(getRA_s(), 5, 2) + " ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2) + " ";
		s += Format.formatIntZeroPadding(getAbsDecl_m(), 2) + " ";
		s += Format.formatDoubleZeroPadding(getAbsDecl_s(), 4, 2);

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>123456.78 +012345.6</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringTo100mArcsecWithoutSpace ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2);
		s += Format.formatIntZeroPadding(getRA_m(), 2);
		s += Format.formatDoubleZeroPadding(getRA_s(), 5, 2) + " ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2);
		s += Format.formatIntZeroPadding(getAbsDecl_m(), 2);
		s += Format.formatDoubleZeroPadding(getAbsDecl_s(), 4, 2);

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>12h34m56s.7890 +01o23'45".678</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringTo1mArcsecWithUnit ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2) + "h";
		s += Format.formatIntZeroPadding(getRA_m(), 2) + "m";
		String ra_s = Format.formatDoubleZeroPadding(getRA_s(), 7, 2);
		s += ra_s.substring(0, 2) + "s" + ra_s.substring(2) + " ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2) + "o";
		s += Format.formatIntZeroPadding(getAbsDecl_m(), 2) + "'";
		String decl_s = Format.formatDoubleZeroPadding(getAbsDecl_s(), 6, 2);
		s += decl_s.substring(0, 2) + "\"" + decl_s.substring(2);

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>12 34 56.7890 +01 23 45.678</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringTo1mArcsecWithoutUnit ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2) + " ";
		s += Format.formatIntZeroPadding(getRA_m(), 2) + " ";
		s += Format.formatDoubleZeroPadding(getRA_s(), 7, 2) + " ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2) + " ";
		s += Format.formatIntZeroPadding(getAbsDecl_m(), 2) + " ";
		s += Format.formatDoubleZeroPadding(getAbsDecl_s(), 6, 2);

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>123456.7890 +012345.678</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringTo1mArcsecWithoutSpace ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2);
		s += Format.formatIntZeroPadding(getRA_m(), 2);
		s += Format.formatDoubleZeroPadding(getRA_s(), 7, 2) + " ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2);
		s += Format.formatIntZeroPadding(getAbsDecl_m(), 2);
		s += Format.formatDoubleZeroPadding(getAbsDecl_s(), 6, 2);

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>12h34m56s.789 +01o23'45".67</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringTo10mArcsecWithUnit ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2) + "h";
		s += Format.formatIntZeroPadding(getRA_m(), 2) + "m";
		String ra_s = Format.formatDoubleZeroPadding(getRA_s(), 6, 2);
		s += ra_s.substring(0, 2) + "s" + ra_s.substring(2) + " ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2) + "o";
		s += Format.formatIntZeroPadding(getAbsDecl_m(), 2) + "'";
		String decl_s = Format.formatDoubleZeroPadding(getAbsDecl_s(), 5, 2);
		s += decl_s.substring(0, 2) + "\"" + decl_s.substring(2);

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>12 34 56.789 +01 23 45.67</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringTo10mArcsecWithoutUnit ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2) + " ";
		s += Format.formatIntZeroPadding(getRA_m(), 2) + " ";
		s += Format.formatDoubleZeroPadding(getRA_s(), 6, 2) + " ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2) + " ";
		s += Format.formatIntZeroPadding(getAbsDecl_m(), 2) + " ";
		s += Format.formatDoubleZeroPadding(getAbsDecl_s(), 5, 2);

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>123456.789 +012345.67</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringTo10mArcsecWithoutSpace ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2);
		s += Format.formatIntZeroPadding(getRA_m(), 2);
		s += Format.formatDoubleZeroPadding(getRA_s(), 6, 2) + " ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2);
		s += Format.formatIntZeroPadding(getAbsDecl_m(), 2);
		s += Format.formatDoubleZeroPadding(getAbsDecl_s(), 5, 2);

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>12h34m56s.7 +01o23'45"</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringToArcsecWithUnit ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2) + "h";
		s += Format.formatIntZeroPadding(getRA_m(), 2) + "m";
		String ra_s = Format.formatDoubleZeroPadding(getRA_s(), 4, 2);
		s += ra_s.substring(0, 2) + "s" + ra_s.substring(2) + " ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2) + "o";
		s += Format.formatIntZeroPadding(getAbsDecl_m(), 2) + "'";
		s += Format.formatDoubleZeroPadding(getAbsDecl_s(), 2, 2) + "\"";

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>12 34 56.7 +01 23 45</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringToArcsecWithoutUnit ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2) + " ";
		s += Format.formatIntZeroPadding(getRA_m(), 2) + " ";
		s += Format.formatDoubleZeroPadding(getRA_s(), 4, 2) + " ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2) + " ";
		s += Format.formatIntZeroPadding(getAbsDecl_m(), 2) + " ";
		s += Format.formatDoubleZeroPadding(getAbsDecl_s(), 2, 2);

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>123456.7 +012345</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringToArcsecWithoutSpace ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2);
		s += Format.formatIntZeroPadding(getRA_m(), 2);
		s += Format.formatDoubleZeroPadding(getRA_s(), 4, 2) + " ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2);
		s += Format.formatIntZeroPadding(getAbsDecl_m(), 2);
		s += Format.formatDoubleZeroPadding(getAbsDecl_s(), 2, 2);

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>12h34m56s +01o23'45"</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringToRoughArcsecWithUnit ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2) + "h";
		s += Format.formatIntZeroPadding(getRA_m(), 2) + "m";
		String ra_s = Format.formatDoubleZeroPadding(getRA_s(), 2, 2);
		s += ra_s.substring(0, 2) + "s ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2) + "o";
		s += Format.formatIntZeroPadding(getAbsDecl_m(), 2) + "'";
		s += Format.formatDoubleZeroPadding(getAbsDecl_s(), 2, 2) + "\"";

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>12 34 56 +01 23 45</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringToRoughArcsecWithoutUnit ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2) + " ";
		s += Format.formatIntZeroPadding(getRA_m(), 2) + " ";
		s += Format.formatDoubleZeroPadding(getRA_s(), 2, 2) + " ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2) + " ";
		s += Format.formatIntZeroPadding(getAbsDecl_m(), 2) + " ";
		s += Format.formatDoubleZeroPadding(getAbsDecl_s(), 2, 2);

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>123456 +012345</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringToRoughArcsecWithoutSpace ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2);
		s += Format.formatIntZeroPadding(getRA_m(), 2);
		s += Format.formatDoubleZeroPadding(getRA_s(), 2, 2) + " ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2);
		s += Format.formatIntZeroPadding(getAbsDecl_m(), 2);
		s += Format.formatDoubleZeroPadding(getAbsDecl_s(), 2, 2);

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>12h34m.56 +01o23'.4</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringTo100mArcminWithUnit ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2) + "h";
		String ra_m = Format.formatDoubleZeroPadding((double)getRA_m() + getRA_s() / 60.0, 5, 2);
		s += ra_m.substring(0, 2) + "m" + ra_m.substring(2) + " ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2) + "o";
		String decl_m = Format.formatDoubleZeroPadding(getAbsDecl_m() + getAbsDecl_s() / 60.0, 4, 2);
		s += decl_m.substring(0, 2) + "'" + decl_m.substring(2);

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>12 34.56 +01 23.4</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringTo100mArcminWithoutUnit ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2) + " ";
		s += Format.formatDoubleZeroPadding((double)getRA_m() + getRA_s() / 60.0, 5, 2);

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2) + " ";
		s += Format.formatDoubleZeroPadding(getAbsDecl_m() + getAbsDecl_s() / 60.0, 4, 2);

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>12h34m56s +01o23'.4</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringTo100mArcminHoursecWithUnit ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2) + "h";
		s += Format.formatIntZeroPadding(getRA_m(), 2) + "m";
		String ra_s = Format.formatDoubleZeroPadding(getRA_s(), 2, 2);
		s += ra_s.substring(0, 2) + "s ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2) + "o";
		String decl_m = Format.formatDoubleZeroPadding(getAbsDecl_m() + getAbsDecl_s() / 60.0, 4, 2);
		s += decl_m.substring(0, 2) + "'" + decl_m.substring(2);

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>12 34 56 +01 23.4</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringTo100mArcminHoursecWithoutUnit ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2) + " ";
		s += Format.formatIntZeroPadding(getRA_m(), 2) + " ";
		s += Format.formatDoubleZeroPadding(getRA_s(), 2, 2) + " ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2) + " ";
		s += Format.formatDoubleZeroPadding(getAbsDecl_m() + getAbsDecl_s() / 60.0, 4, 2);

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>123456 +0123.4</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringTo100mArcminHoursecWithoutSpace ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2);
		s += Format.formatIntZeroPadding(getRA_m(), 2);
		s += Format.formatDoubleZeroPadding(getRA_s(), 2, 2) + " ";

		s += (getDecl() < 0.0 ? '-' : '+');
		s += Format.formatIntZeroPadding(getAbsDecl_d(), 2);
		s += Format.formatDoubleZeroPadding(getAbsDecl_m() + getAbsDecl_s() / 60.0, 4, 2);

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>12h34m.5 +01o23'</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringToArcminWithUnit ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2) + "h";
		String ra_m = Format.formatDoubleZeroPadding((double)getRA_m() + getRA_s() / 60.0, 4, 2);
		s += ra_m.substring(0, 2) + "m" + ra_m.substring(2) + " ";

		s += (getDecl() < 0.0 ? '-' : '+');
		int decl_d = getAbsDecl_d();
		int decl_m = getAbsDecl_m();
		if (getAbsDecl_s() >= 30.0) {
			decl_m++;
			if (decl_m == 60) {
				decl_d++;
				decl_m = 0;
			}
		}
		s += Format.formatIntZeroPadding(decl_d, 2) + "o";
		s += Format.formatIntZeroPadding(decl_m, 2) + "'";

		return s;
	}

	/**
	 * Returns a string representation of the state of this object,
	 * in such a format as <tt>12 34.5 +01 23</tt>.
	 * @return a string representation of the state of this object.
	 */
	public String getOutputStringToArcminWithoutUnit ( ) {
		String s = Format.formatIntZeroPadding(getRA_h(), 2) + " ";
		s += Format.formatDoubleZeroPadding((double)getRA_m() + getRA_s() / 60.0, 4, 2);

		s += (getDecl() < 0.0 ? '-' : '+');
		int decl_d = getAbsDecl_d();
		int decl_m = getAbsDecl_m();
		if (getAbsDecl_s() >= 30.0) {
			decl_m++;
			if (decl_m == 60) {
				decl_d++;
				decl_m = 0;
			}
		}
		s += Format.formatIntZeroPadding(decl_d, 2) + " ";
		s += Format.formatIntZeroPadding(decl_m, 2);

		return s;
	}
}
