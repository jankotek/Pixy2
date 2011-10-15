/*
 * @(#)MagnitudeSystem.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.util;
import java.util.*;
import net.aerith.misao.util.star.*;

/**
 * The <code>MagnitudeSystem</code> represents a set of translation
 * formula between magnitude systems.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2002 January 18
 */

public class MagnitudeSystem {
	/**
	 * The method of magnitude system definition which represents to
	 * use the default magnitude.
	 */
	public final static int METHOD_DEFAULT = 0;

	/**
	 * The method of magnitude system definition which represents the
	 * standard system.
	 */
	public final static int METHOD_STANDARD = 1;

	/**
	 * The method of magnitude system definition which represents the
	 * magnitude recorded in the catalog.
	 */
	public final static int METHOD_CATALOG = 2;

	/**
	 * The method of magnitude system definition which represents the
	 * instrumental magnitude.
	 */
	public final static int METHOD_INSTRUMENTAL = 3;

	/**
	 * The method of magnitude system definition.
	 */
	protected int method = METHOD_DEFAULT;

	/**
	 * The magnitude system code.
	 */
	protected String system = "";

	/**
	 * The coefficient of (B-V) for regular photometry.
	 */
	protected double gradient_bv = 0.0;

	/**
	 * Constructs a <code>MagnitudeSystem</code>.
	 */
	public MagnitudeSystem ( ) {
	}

	/**
	 * Gets the method.
	 * @return the number of method.
	 */
	public int getMethod ( ) {
		return method;
	}

	/**
	 * Sets the method.
	 * @param method the number of method.
	 */
	public void setMethod ( int method ) {
		this.method = method;
	}

	/**
	 * Gets the magnitude system code.
	 * @return the magnitude system code.
	 */
	public String getSystemCode ( ) {
		return system;
	}

	/**
	 * Sets the magnitude system code.
	 * @param system the magnitude system code.
	 */
	public void setSystemCode ( String system ) {
		this.system = system;
	}

	/**
	 * Gets the gradient of (B-V).
	 * @return the gradient of (B-V).
	 */
	public double getGradientBV ( ) {
		return gradient_bv;
	}

	/**
	 * Sets the gradient of (B-V).
	 * @param gradient the gradient of (B-V).
	 */
	public void setGradientBV ( double gradient ) {
		gradient_bv = gradient;
	}

	/**
	 * Gets the codes of the standard system.
	 * @return the codes of the standard system.
	 */
	public static String[] getStandardSystemCodes ( ) {
		String[] codes = new String[4];
		codes[0] = "Ic";
		codes[1] = "Rc";
		codes[2] = "V";
		codes[3] = "B";
		return codes;
	}

	/**
	 * Converts the standard V and B magnitude into the standard Ic
	 * magnitude.
	 * <p>
	 * Reference:<br>
	 * Natali F., Natali G., Pompei E., Pedichini F., 1994, A&amp;A 289, 756<br>
	 * @param v_mag the standard V magnitude.
	 * @param b_mag the standard B magnitude.
	 * @return the standard Ic magnitude.
	 */
	public static double getIcMag ( double v_mag, double b_mag ) {
		return b_mag - 2.36 * (b_mag - v_mag);
	}

	/**
	 * Converts the standard V and B magnitude into the standard Rc
	 * magnitude.
	 * @param v_mag the standard V magnitude.
	 * @param b_mag the standard B magnitude.
	 * @return the standard Rc magnitude.
	 */
	public static double getRcMag ( double v_mag, double b_mag ) {
		return v_mag - 0.5 * (b_mag - v_mag);
	}

	/**
	 * Gets the preliminary V magnitude from the USNO R and B 
	 * magnitude based on the Taichi Kato's formula in 
	 * [vsnet-chat 700].
	 * @param R_mag the R magnitude.
	 * @param B_mag the B magnitude.
	 * @return the preliminary V magnitude.
	 */
	public static double getUsnoVMag ( double R_mag, double B_mag ) {
		return R_mag + 0.375 * (B_mag - R_mag);
	}

	/**
	 * Gets the magnitude system formula for several CCD chips by
	 * Arne A. Henden in "The M67 Unfiltered Photometry Experiment",
	 * 2000, Journal AAVSO vol. 29, page 35.
	 * @return the hash table of CCD chips and the coefficient of (B-V).
	 */
	public static Hashtable getDefaultMagnitudeSystemFormula ( ) {
		Hashtable hash = new Hashtable();

		hash.put("SITe back-illuminated", new Double(-0.2600));
		hash.put("KAF-E", new Double(-0.3602));
		hash.put("KAF", new Double(-0.5174));
		hash.put("Sony", new Double(0.0606));
		hash.put("TC-245", new Double(-0.4292));
		hash.put("TC-241", new Double(-0.2905));

		return hash;
	}

	/**
	 * Gets the help message on the magnitude system formula for 
	 * several CCD chips by Arne A. Henden in "The M67 Unfiltered 
	 * Photometry Experiment", 2000, Journal AAVSO vol. 29, page 35.
	 * @return the help message.
	 */
	public static String getDefaultMagnitudeSystemFormulaHelpMessage ( ) {
		String html = "<html><body>";
		html += "<p>";
		html += "The coefficient of (B-V) for several CCD chips:<br>";
		html += "</p><p>";
		html += "<pre>";
		html += "    CCD chip              gradient<br>";
		html += "    ------------------------------<br>";
		html += "    KAF                    -0.5174<br>";
		html += "    KAF-E                  -0.3602<br>";
		html += "    SITe back-illuminated  -0.2600<br>";
		html += "    Sony                    0.0606<br>";
		html += "    TC-241                 -0.2905<br>";
		html += "    TC-245                 -0.4292<br>";
		html += "</pre>";
		html += "</p><p>";
		html += "Instrumental mag = V + gradient * (B - V)<br>";
		html += "</p><p>";
		html += "Reference:";
		html += "<blockquote>";
		html += "Henden, A. A., \"The M67 Unfiltered Photometry Experiment\",<br>";
		html += "2000, Journal AAVSO vol. 29, page 35.<br>";
		html += "<u><font color=\"#0000ff\">ftp://ftp.nofs.navy.mil/pub/outgoing/aah/m67/paper/</font></u>";
		html += "</blockquote>";
		html += "</p>";
		html += "</body></html>";
		return html;
	}

	/**
	 * Calculates the coefficient of the (B-V) based on the specified
	 * list of pairs. Note that the second star must be a catalog star
	 * object which contains the standard V magnitude and (B-V) value.
	 * @param pair_list the list of pairs.
	 * @return the coefficient of the (B-V).
	 */
	public static double calculateGradient ( Vector pair_list ) {
		// se[0]: constant * N + gradient * \sum ( B-V ) + \sum ( V + log_2.5 ( value ) ) = 0
		// se[1]: constant * \sum ( B-V ) + gradient * \sum ( B-V )^2 + \sum ( B-V ) * ( V + log_2.5 ( value ) ) = 0
		SimultaneousEquation se = new SimultaneousEquation(2);

		int count = 0;
		for (int i = 0 ; i < pair_list.size() ; i++) {
			try {
				StarPair pair = (StarPair)pair_list.elementAt(i);
				StarImage star_image = (StarImage)pair.getFirstStar();
				CatalogStar catalog_star = (CatalogStar)pair.getSecondStar();

				if (star_image != null) {
					se.coef[0][0] += 1.0;
					se.coef[0][1] += catalog_star.getBVDifference();
					se.coef[0][2] += catalog_star.getVMagnitude() + Math.log(star_image.getValue()) / Math.log(Astro.MAG_STEP);
					se.coef[1][0] += catalog_star.getBVDifference();
					se.coef[1][1] += catalog_star.getBVDifference() * catalog_star.getBVDifference();
					se.coef[1][2] += catalog_star.getBVDifference() * (catalog_star.getVMagnitude() + Math.log(star_image.getValue()) / Math.log(Astro.MAG_STEP));

					count++;
				}
			} catch ( ClassCastException exception ) {
			} catch ( UnsupportedMagnitudeSystemException exception ) {
			}
		}

		if (count >= 2) {
			se.solve();

			double constant = se.getAnswer(0);
			double gradient = se.getAnswer(1);

			return gradient;
		}

		return 0.0;
	}
}
