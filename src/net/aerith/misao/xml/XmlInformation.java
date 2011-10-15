/*
 * @(#)XmlInformation.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.xml;
import java.io.*;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.database.*;
import net.aerith.misao.io.FileManager;

/**
 * The <code>XmlInformation</code> is an application side implementation
 * of the class that the relaxer generated automatically.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2004 January 1
 */

public class XmlInformation extends net.aerith.misao.xml.relaxer.XmlInformation implements XmlDBRecord {
	/**
	 * Constructs an <code>XmlInformation</code>.
	 */
	public XmlInformation ( ) {
		super();
	}

	/**
	 * Clones the <code>XmlInformation</code> object.
	 * @param info the information object.
	 */
	public XmlInformation ( XmlInformation info ) {
		super();

		if (info.getPath() != null)
			setPath(info.getPath());

		XmlImage xml_image = new XmlImage();
		xml_image.setContent(info.getImage().getContent());
		xml_image.setFormat(info.getImage().getFormat());
		setImage(xml_image);

		XmlSize size = new XmlSize();
		size.setWidth(info.getSize().getWidth());
		size.setHeight(info.getSize().getHeight());
		setSize(size);

		if (info.getDate() != null)
			setDate(info.getDate());

		if (info.getExposure() != null) {
			XmlExposure exposure = new XmlExposure();
			exposure.setContent(info.getExposure().getContent());
			exposure.setUnit(info.getExposure().getUnit());
			setExposure(exposure);
		}

		if (info.getObserver() != null)
			setObserver(info.getObserver());

		XmlCenter center = new XmlCenter();
		center.setRa(info.getCenter().getRa());
		center.setDecl(info.getCenter().getDecl());
		setCenter(center);

		XmlFov fov = new XmlFov();
		fov.setWidth(info.getFov().getWidth());
		fov.setHeight(info.getFov().getHeight());
		fov.setUnit(info.getFov().getUnit());
		setFov(fov);

		XmlRotation rotation = new XmlRotation();
		rotation.setContent(info.getRotation().getContent());
		rotation.setUnit(info.getRotation().getUnit());
		setRotation(rotation);

		XmlPixelSize pixel_size = new XmlPixelSize();
		pixel_size.setWidth(info.getPixelSize().getWidth());
		pixel_size.setHeight(info.getPixelSize().getHeight());
		pixel_size.setUnit(info.getPixelSize().getUnit());
		setPixelSize(pixel_size);

		setLimitingMag(info.getLimitingMag());

		if (info.getUpperLimitMag() != null)
			setUpperLimitMag(info.getUpperLimitMag());

		if (info.getAstrometricError() != null) {
			XmlAstrometricError err = new XmlAstrometricError();
			err.setRa(info.getAstrometricError().getRa());
			err.setDecl(info.getAstrometricError().getDecl());
			err.setUnit(info.getAstrometricError().getUnit());
			setAstrometricError(err);
		}

		if (info.getPhotometricError() != null)
			setPhotometricError(info.getPhotometricError());

		if (info.getMagnitudeTranslationFormula() != null)
			setMagnitudeTranslationFormula(info.getMagnitudeTranslationFormula());

		if (info.getMagnitudeCorrection() != null)
			setMagnitudeCorrection(info.getMagnitudeCorrection());

		if (info.getDistortionField() != null) {
			XmlDistortionField df = new XmlDistortionField();
			df.setX(info.getDistortionField().getX());
			df.setY(info.getDistortionField().getY());
			setDistortionField(df);
		}

		if (info.getFilter() != null)
			setFilter(info.getFilter());

		if (info.getChip() != null)
			setChip(info.getChip());

		if (info.getInstruments() != null)
			setInstruments(info.getInstruments());

		setBaseCatalog(info.getBaseCatalog());

		if (info.getAstrometry() != null) {
			XmlAstrometry astrometry = new XmlAstrometry();
			XmlCatalog catalog = new XmlCatalog();
			catalog.setContent(info.getAstrometry().getCatalog().getContent());
			astrometry.setCatalog(catalog);
			astrometry.setEquinox(info.getAstrometry().getEquinox());
			setAstrometry(astrometry);
		}

		if (info.getPhotometry() != null) {
			XmlPhotometry photometry = new XmlPhotometry();
			XmlCatalog catalog = new XmlCatalog();
			catalog.setContent(info.getPhotometry().getCatalog().getContent());
			photometry.setCatalog(catalog);
			photometry.setType(info.getPhotometry().getType());
			if (info.getPhotometry().getSystemFormula() != null)
				photometry.setSystemFormula(info.getPhotometry().getSystemFormula());
			setPhotometry(photometry);
		}

		if (info.getReversedImage() != null)
			setReversedImage(new XmlReversedImage());

		if (info.getSbigImage() != null)
			setSbigImage(new XmlSbigImage());

		if (info.getUnofficial() != null)
			setUnofficial(new XmlUnofficial());

		if (info.getStarCount() != null) {
			XmlStarCount star_count = new XmlStarCount();
			star_count.setStr(info.getStarCount().getStr());
			star_count.setVar(info.getStarCount().getVar());
			star_count.setMov(info.getStarCount().getMov());
			star_count.setNew(info.getStarCount().getNew());
			star_count.setErr(info.getStarCount().getErr());
			star_count.setNeg(info.getStarCount().getNeg());
			setStarCount(star_count);
		}

		if (info.getNote() != null)
			setNote(info.getNote());
	}

	/**
	 * Clones this <code>XmlInformation</code> object, and creates an 
	 * empty <code>XmlReport</code> object with it.
	 * @return the empty XML report document.
	 */
	public XmlReport cloneEmptyReport ( ) {
		XmlReport report = new XmlReport();

		XmlSystem system = new XmlSystem();
		report.setSystem(system);

		XmlData data = new XmlData();
		data.setStar(new XmlStar[0]);
		report.setData(data);

		// Copies the image information parameters.
		XmlInformation info = new XmlInformation(this);
		report.setInformation(info);

		return report;
	}

	/**
	 * Creates an empty <code>XmlDBRecord</code> object. This method 
	 * must be overrided by the subclass.
	 * @return the new empty object.
	 */
	public XmlDBRecord create ( ) {
		return new XmlInformation();
	}

	/**
	 * Gets the ID.
	 * @return the ID.
	 */
	public String getID ( ) {
		return getPath();
	}

	/**
	 * Sets the information of an image.
	 * @param image_size the size of the image.
	 * @param cmf        the chart map function.
	 */
	public void setInformation ( Size image_size, ChartMapFunction cmf ) {
		XmlSize size = new XmlSize();
		size.setWidth(image_size.getWidth());
		size.setHeight(image_size.getHeight());
		setSize(size);

		String s = cmf.getCenterCoor().getOutputStringTo100mArcsecWithoutUnit();
		int p = s.indexOf('+');
		if (p < 0)
			p = s.indexOf('-');
		XmlCenter center = new XmlCenter();
		center.setRa(s.substring(0, p).trim());
		center.setDecl(s.substring(p).trim());
		setCenter(center);

		double pixel_width = 1.0 / cmf.getScaleUnitPerDegree();
		double pixel_height = 1.0 / cmf.getScaleUnitPerDegree();

		double fov_width = (double)image_size.getWidth() * pixel_width;
		double fov_height = (double)image_size.getHeight() * pixel_height;
		XmlFov fov = new XmlFov();
		if (fov_width < 1.0 / 60.0) {
			fov.setWidth((float)(fov_width * 3600.0));
			fov.setHeight((float)(fov_height * 3600.0));
			fov.setUnit("arcsec");
		} else if (fov_width < 1.0) {
			fov.setWidth((float)(fov_width * 60.0));
			fov.setHeight((float)(fov_height * 60.0));
			fov.setUnit("arcmin");
		} else {
			fov.setWidth((float)(fov_width));
			fov.setHeight((float)(fov_height));
			fov.setUnit("degree");
		}
		setFov(fov);

		XmlRotation rot = new XmlRotation();
		rot.setContent((float)(cmf.getPositionAngle()));
		rot.setUnit("degree");
		setRotation(rot);

		XmlPixelSize ps = new XmlPixelSize();
		ps.setWidth((float)(pixel_width * 3600.0));
		ps.setHeight((float)(pixel_height * 3600.0));
		ps.setUnit("arcsec");
		setPixelSize(ps);
	}

	/**
	 * Gets the image file. In general, it returns a file at the 
	 * specified relative path of the specified file manager. If it 
	 * does not exist, it returns a file at the specified relative 
	 * path from the directory where the XML report document is 
	 * stored. In the latter case, the XML file path must be recorded
	 * in the <path> element.
	 * @param file_manager the file manager.
	 * @return the image file.
	 */
	public File getImageFile ( FileManager file_manager ) {
		File file = file_manager.newFile(getImage().getContent());
		if (file.canRead() == false) {
			// In the case the image path is recorded as a relative path
			// from the directory where the XML path is stored.
			if (getPath() != null) {
				File xml_file = new File(getPath());
				File file2 = new File(FileManager.unitePath(new File(xml_file.getAbsolutePath()).getParent(), getImage().getContent()));

				if (file2.canRead())
					return file2;
			}
		}

		return file;
	}

	/**
	 * Maps the specified R.A. and Decl. to the (x,y) position.
	 * @param coor the R.A. and Decl.
	 * @return the (x,y) position.
	 */
	public Position mapCoordinatesToXY ( Coor coor ) {
		ChartMapFunction cmf = getChartMapFunction();
		DistortionField df = getDistortion();

		Position position = cmf.mapCoordinatesToXY(coor);

		Position df_pos = df.inverse().getValue(position);
		position.add(df_pos);

		position.add(new Position((double)getSize().getWidth() / 2.0, (double)getSize().getHeight() / 2.0));

		return position;
	}

	/**
	 * Maps the specified (x,y) position to the R.A. and Decl.
	 * @param position the position.
	 * @return the R.A. and Decl.
	 */
	public Coor mapXYToCoordinates ( Position position ) {
		ChartMapFunction cmf = getChartMapFunction();
		DistortionField df = getDistortion();

		position = new Position(position.getX(), position.getY());
		position.add(new Position(- (double)getSize().getWidth() / 2.0, - (double)getSize().getHeight() / 2.0));

		Position df_pos = df.getValue(position);
		position.add(df_pos);

		return cmf.mapXYToCoordinates(position);
	}

	/**
	 * Returns true when the specified image overlaps on this image.
	 * @param info another image information.
	 * @return true when the specified image overlaps on this image.
	 */
	public boolean overlaps ( XmlInformation info ) {
		Coor coor1 = ((XmlCenter)getCenter()).getCoor();
		Coor coor2 = ((XmlCenter)info.getCenter()).getCoor();
		double distance = coor1.getAngularDistanceTo(coor2);

		double radius1 = getFieldRadiusInDegree();
		double radius2 = info.getFieldRadiusInDegree();
		if (distance < radius1 + radius2)
			return true;

		return false;
	}

	/**
	 * Gets the midst date and time of the exposure.
	 * @return the midst date and time of the exposure.
	 */
	public JulianDay getMidDate ( ) {
		if (getDate() == null)
			return null;

		JulianDay day = JulianDay.create(getDate());

		if (getExposure() != null) {
			double seconds = ((XmlExposure)getExposure()).getValueInSecond();
			day = new JulianDay(day.getJD() + seconds / 3600.0 / 24.0 / 2.0);
		}

		int accuracy = JulianDay.getAccuracy(getDate());
		return day.round(accuracy);
	}

	/**
	 * Gets the chart map function.
	 * @return the chart map function.
	 */
	public ChartMapFunction getChartMapFunction ( ) {
		Coor coor = ((XmlCenter)getCenter()).getCoor();

		double fov_width = ((XmlFov)getFov()).getWidthInDegree();

		return new ChartMapFunction(coor, (double)getSize().getWidth() / fov_width, (double)getRotation().getContent());
	}

	/**
	 * Gets the field radius in degree, the radius of the circumcircle
	 * of the image field.
	 * @return the field radius in degree.
	 */
	public double getFieldRadiusInDegree ( ) {
		double fov_width = ((XmlFov)getFov()).getWidthInDegree();
		double fov_height = ((XmlFov)getFov()).getHeightInDegree();

		return Math.sqrt(fov_width * fov_width + fov_height * fov_height) / 2.0;
	}

	/**
	 * Formats the limiting magnitude and sets.
	 * @param mag the limiting magnitude.
	 */
	public void setFormattedLimitingMag ( double mag ) {
		String s = Format.formatDouble(mag, 6, 3).trim();
		setLimitingMag(Float.parseFloat(s));
	}

	/**
	 * Gets the proper upper-limit magnitude. When the upper-limit
	 * magnitude is not recorded, it returns the limiting magnitude.
	 * @return the proper upper-limit magnitude.
	 */
	public double getProperUpperLimitMag ( ) {
		if (getUpperLimitMag() != null)
			return (double)getUpperLimitMag().floatValue();

		return getLimitingMag();
	}

	/**
	 * Formats the upper-limit magnitude and sets.
	 * @param mag the upper-limit magnitude.
	 */
	public void setFormattedUpperLimitMag ( double mag ) {
		String s = Format.formatDouble(mag, 6, 3).trim();
		setUpperLimitMag(new Float(s));
	}

	/**
	 * Gets the astrometric error in arcsec.
	 * @return the astrometric error in arcsec.
	 */
	public double getAstrometricErrorInArcsec ( ) {
		XmlAstrometricError astrometric_error = (XmlAstrometricError)getAstrometricError();

		double err = (double)(astrometric_error.getRa() * astrometric_error.getRa() + astrometric_error.getDecl() * astrometric_error.getDecl());
		return Math.sqrt(err);
	}

	/**
	 * Gets the astrometric error in pixel.
	 * @return the astrometric error in pixel.
	 */
	public double getAstrometricErrorInPixel ( ) {
		double err = getAstrometricErrorInArcsec();
		double fov_width = ((XmlFov)getFov()).getWidthInDegree() * 3600.0;

		double err_in_pixel = (double)getSize().getWidth() / fov_width * err;

		return err_in_pixel;
	}

	/**
	 * Sets the astrometric error.
	 * @param err the astrometric error.
	 */
	public void setAstrometricError ( AstrometricError err ) {
		String s_ra = Format.formatDouble(err.getRA() * 3600.0, 8, 5).trim();
		String s_decl = Format.formatDouble(err.getDecl() * 3600.0, 8, 5).trim();

		XmlAstrometricError astrometric_error = new XmlAstrometricError();
		astrometric_error.setRa(Float.parseFloat(s_ra));
		astrometric_error.setDecl(Float.parseFloat(s_decl));
		astrometric_error.setUnit("arcsec");
		setAstrometricError(astrometric_error);
	}

	/**
	 * Sets the photometric error.
	 * @param err the photometric error.
	 */
	public void setPhotometricError ( PhotometricError err ) {
		String s = Format.formatDouble(err.getError(), 5, 2).trim();
		setPhotometricError(Float.valueOf(s));
	}

	/**
	 * Gets the astrometry setting.
	 * @return the astrometry setting.
	 */
	public AstrometrySetting getAstrometrySetting ( ) {
		if (getAstrometry() == null)
			return null;

		return ((XmlAstrometry)getAstrometry()).getAstrometrySetting();
	}

	/**
	 * Gets the photometry setting.
	 * @return the photometry setting.
	 */
	public PhotometrySetting getPhotometrySetting ( ) {
		if (getPhotometry() == null)
			return null;

		return ((XmlPhotometry)getPhotometry()).getPhotometrySetting();
	}

	/**
	 * Sets the distortion field.
	 * @param df the distortion field.
	 */
	public void setDistortionField ( DistortionField df ) {
		XmlDistortionField field = new XmlDistortionField();
		field.setX(df.cubics_dx.getOutputString());
		field.setY(df.cubics_dy.getOutputString());
		setDistortionField(field);
	}

	/**
	 * Gets the distortion field.
	 * @return the distortion field.
	 */
	public DistortionField getDistortion ( ) {
		DistortionField df = new DistortionField();
		if (getDistortionField() != null) {
			df.cubics_dx = Cubics.create(getDistortionField().getX());
			df.cubics_dy = Cubics.create(getDistortionField().getY());
		}
		return df;
	}

	/**
	 * Sets the magnitude translation formula.
	 * @param mag_formula the magnitude translation formula.
	 */
	public void setMagnitudeTranslationFormula ( MagnitudeTranslationFormula mag_formula ) {
		setMagnitudeTranslationFormula(mag_formula.getOutputString());
	}

	/**
	 * Returns true if the reversed image property is set.
	 * @return true if the reversed image property is set.
	 */
	public boolean isReversedImage ( ) {
		return (getReversedImage() != null);
	}

	/**
	 * Sets the reversed image property.
	 */
	public void setReversedImage ( ) {
		setReversedImage(new XmlReversedImage());
	}

	/**
	 * Returns true if the SBIG ST-4/6 image property is set.
	 * @return true if the SBIG ST-4/6 image property is set.
	 */
	public boolean isSbigImage ( ) {
		return (getSbigImage() != null);
	}

	/**
	 * Sets the SBIG ST-4/6 image property.
	 */
	public void setSbigImage ( ) {
		setSbigImage(new XmlSbigImage());
	}

	/**
	 * Gets an array of keys and values to output.
	 * @return an array of keys and values to output.
	 */
	public KeyAndValue[] getKeyAndValues ( ) {
		Vector list = new Vector();

		if (getPath() != null)
			list.addElement(new KeyAndValue("XML File", getPath()));

		list.addElement(new KeyAndValue("Image File", getImage().getContent()));

		list.addElement(new KeyAndValue("Image Size", "" + getSize().getWidth() + " x " + getSize().getHeight()));

		if (getDate() != null) {
			JulianDay jd = JulianDay.create(getDate());
			list.addElement(new KeyAndValue("Date", jd.getOutputString(JulianDay.FORMAT_MONTH_IN_REDUCED, JulianDay.getAccuracy(getDate()))));
		}

		if (getExposure() != null)
			list.addElement(new KeyAndValue("Exposure", "" + getExposure().getContent() + " " + getExposure().getUnit()));

		if (getObserver() != null)
			list.addElement(new KeyAndValue("Observer", getObserver()));

		Coor center_coor = ((XmlCenter)getCenter()).getCoor();
		StringTokenizer st = new StringTokenizer(center_coor.getOutputString());
		list.addElement(new KeyAndValue("Center R.A.", st.nextToken()));
		list.addElement(new KeyAndValue("Center Decl.", st.nextToken()));

		list.addElement(new KeyAndValue("Field of View", "" + getFov().getWidth() + " x " + getFov().getHeight() + " " + getFov().getUnit()));

		list.addElement(new KeyAndValue("P.A. of Up", "" + getRotation().getContent() + " " + getRotation().getUnit()));

		list.addElement(new KeyAndValue("Pixel Size", "" + getPixelSize().getWidth() + " x " + getPixelSize().getHeight() + " " + getPixelSize().getUnit()));

		list.addElement(new KeyAndValue("Limiting Mag.", "" + getLimitingMag() + " mag"));

		if (getUpperLimitMag() != null)
			list.addElement(new KeyAndValue("Upper-Limit Mag.", "" + getUpperLimitMag().floatValue() + " mag"));

		if (getAstrometricError() != null) {
			XmlAstrometricError astrometric_error = (XmlAstrometricError)getAstrometricError();
			list.addElement(new KeyAndValue("Astrometric Error", "" + astrometric_error.getRa() + " x " + astrometric_error.getDecl() + " " + astrometric_error.getUnit()));
		}

		if (getPhotometricError() != null)
			list.addElement(new KeyAndValue("Photometric Error", "" + getPhotometricError().floatValue() + " mag"));

		if (getFilter() != null)
			list.addElement(new KeyAndValue("Filter", getFilter()));

		if (getChip() != null)
			list.addElement(new KeyAndValue("Chip", getChip()));

		if (getInstruments() != null)
			list.addElement(new KeyAndValue("Instruments", getInstruments()));

		list.addElement(new KeyAndValue("Base Catalog", getBaseCatalog()));

		if (getAstrometry() != null) {
			AstrometrySetting setting = ((XmlAstrometry)getAstrometry()).getAstrometrySetting();
			list.addElement(new KeyAndValue("Astrometric Catalog", setting.getDescription()));
			list.addElement(new KeyAndValue("Astrometric Equinox", getAstrometry().getEquinox()));
		}

		if (getPhotometry() != null) {
			PhotometrySetting setting = ((XmlPhotometry)getPhotometry()).getPhotometrySetting();
			MagnitudeSystem system = setting.getMagnitudeSystem();

			list.addElement(new KeyAndValue("Photometric Catalog", setting.getDescription()));

			if (setting.getMethod() == PhotometrySetting.METHOD_COMPARISON) {
				if (setting.gradientFixed())
					list.addElement(new KeyAndValue("Photometric Method", "average fitting"));
				else
					list.addElement(new KeyAndValue("Photometric Method", "line fitting"));
			} else {
				if (system.getMethod() == MagnitudeSystem.METHOD_STANDARD)
					list.addElement(new KeyAndValue("Photometric System", system.getSystemCode()));
				if (system.getMethod() == MagnitudeSystem.METHOD_INSTRUMENTAL)
					list.addElement(new KeyAndValue("Photometric System", "V + " + system.getGradientBV() + " * (B-V)"));
			}
		}

		if (getUnofficial() != null)
			list.addElement(new KeyAndValue("Status", "unofficial"));

		if (getStarCount() != null) {
			XmlStarCount sc = (XmlStarCount)getStarCount();
			list.addElement(new KeyAndValue("Number of STRs", "" + sc.getStr()));
			list.addElement(new KeyAndValue("Number of NEWs", "" + sc.getNew()));
			list.addElement(new KeyAndValue("Number of ERRs", "" + sc.getErr()));
			list.addElement(new KeyAndValue("Number of NEGs", "" + sc.getNeg()));
			if (sc.getVar() > 0)
				list.addElement(new KeyAndValue("Number of VARs", "" + sc.getVar()));
			if (sc.getMov() > 0)
				list.addElement(new KeyAndValue("Number of MOVs", "" + sc.getMov()));
		}

		if (getNote() != null)
			list.addElement(new KeyAndValue("Note", getNote()));

		KeyAndValue[] key_and_values = new KeyAndValue[list.size()];
		for (int i = 0 ; i < list.size() ; i++)
			key_and_values[i] = (KeyAndValue)list.elementAt(i);

		return key_and_values;
	}
}
