/*
 * @(#)CatalogManager.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.catalog;
import java.util.Vector;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.io.*;
import net.aerith.misao.catalog.star.*;
import net.aerith.misao.gui.PlotProperty;

/**
 * The <code>CatalogManager</code> represents a set of all catalogs.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2007 May 5
 */

public class CatalogManager {
	/**
	 * The number of catalog category for "Star Catalog".
	 */
	public final static int CATEGORY_STAR = 1;

	/**
	 * The number of catalog category for "Infrared Catalog".
	 */
	public final static int CATEGORY_INFRARED = 2;

	/**
	 * The number of catalog category for "X-ray Catalog".
	 */
	public final static int CATEGORY_XRAY = 3;

	/**
	 * The number of catalog category for "Variable Stars".
	 */
	public final static int CATEGORY_VARIABLE = 4;

	/**
	 * The number of catalog category for "Comets and Asteroids".
	 */
	public final static int CATEGORY_COMET_AND_ASTEROID = 5;

	/**
	 * The number of catalog category for "Clusters and Nebulae".
	 */
	public final static int CATEGORY_CLUSTER_AND_NEBULA = 6;

	/**
	 * The number of catalog category for "Catalog Errors".
	 */
	public final static int CATEGORY_ERROR = 7;

	/**
	 * The number of catalog category for "Others".
	 */
	public final static int CATEGORY_OTHERS = 8;

	/**
	 * Gets the list of star catalog readers.
	 * @return the list of star catalog readers.
	 */
	public static Vector getStarCatalogReaderList ( ) {
		Vector list = getCatalogReaderList();
		Vector l = new Vector();

		for (int i = 0 ; i < list.size() ; i++) {
			CatalogReader reader = (CatalogReader)list.elementAt(i);
			if (reader.supportsExamination())
				l.addElement(reader);
		}

		return l;
	}

	/**
	 * Gets the star catalog reader of the specified name.
	 * @param name the name of the reader.
	 * @return the star catalog reader.
	 */
	public static CatalogReader getStarCatalogReader ( String name ) {
		Vector list = getStarCatalogReaderList();
		for (int i = 0 ; i < list.size() ; i++) {
			CatalogReader reader = (CatalogReader)list.elementAt(i);
			if (reader.getName().equals(name))
				return reader;
		}
		return null;
	}

	/**
	 * Gets the list of catalog readers for identification.
	 * @return the list of catalog readers for identification.
	 */
	public static Vector getIdentificationCatalogReaderList ( ) {
		return getCatalogReaderList();
	}

	/**
	 * Gets the list of catalog readers to be read from a file or
	 * files in a directory.
	 * @return the list of catalog readers.
	 */
	public static Vector getPathOrientedCatalogReaderList ( ) {
		Vector list = getCatalogReaderList();
		Vector list2 = new Vector();
		for (int i = 0 ; i < list.size() ; i++) {
			CatalogReader reader = (CatalogReader)list.elementAt(i);
			if (reader.isFile() == true  ||  reader.isInDirectory() == true)
				list2.addElement(reader);
		}
		return list2;
	}

	/**
	 * Gets the array of catalog categories.
	 * @return the array of catalog categories.
	 */
	public static String[] getCatalogCategories ( ) {
		String[] categories = new String[8];
		categories[0] = getCatalogCategoryName(CATEGORY_STAR);
		categories[1] = getCatalogCategoryName(CATEGORY_INFRARED);
		categories[2] = getCatalogCategoryName(CATEGORY_XRAY);
		categories[3] = getCatalogCategoryName(CATEGORY_VARIABLE);
		categories[4] = getCatalogCategoryName(CATEGORY_COMET_AND_ASTEROID);
		categories[5] = getCatalogCategoryName(CATEGORY_CLUSTER_AND_NEBULA);
		categories[6] = getCatalogCategoryName(CATEGORY_ERROR);
		categories[7] = getCatalogCategoryName(CATEGORY_OTHERS);
		return categories;
	}

	/**
	 * Gets the catalog category name of the specified number.
	 * @param number the number for a catalog category.
	 * @return the name of catalog category.
	 */
	public static String getCatalogCategoryName ( int number ) {
		switch (number) {
			case CATEGORY_STAR:
				return "Star Catalog";
			case CATEGORY_INFRARED:
				return "Infrared Catalog";
			case CATEGORY_XRAY:
				return "X-ray Catalog";
			case CATEGORY_VARIABLE:
				return "Variable Stars";
			case CATEGORY_COMET_AND_ASTEROID:
				return "Comets and Asteroids";
			case CATEGORY_CLUSTER_AND_NEBULA:
				return "Clusters and Nebulae";
			case CATEGORY_ERROR:
				return "Catalog Errors";
			case CATEGORY_OTHERS:
				return "Others";
		}
		return "";
	}

	/**
	 * Gets the folder string of the specified catalog category number.
	 * @param number the number for a catalog category.
	 * @return the folder string of catalog category.
	 */
	public static String getCatalogCategoryFolder ( int number ) {
		switch (number) {
			case CATEGORY_STAR:
				return "star";
			case CATEGORY_INFRARED:
				return "infrared";
			case CATEGORY_XRAY:
				return "x-ray";
			case CATEGORY_VARIABLE:
				return "variable";
			case CATEGORY_COMET_AND_ASTEROID:
				return "comet";
			case CATEGORY_CLUSTER_AND_NEBULA:
				return "nebula";
			case CATEGORY_ERROR:
				return "error";
			case CATEGORY_OTHERS:
				return "others";
		}
		return "";
	}

	/**
	 * Converts the specified category name to the folder name.
	 * @param name the name of category.
	 * @return the folder name.
	 */
	public static String convertCategoryNameToFolder ( String name ) {
		if (name.equals(getCatalogCategoryName(CATEGORY_STAR)))
			return getCatalogCategoryFolder(CATEGORY_STAR);
		if (name.equals(getCatalogCategoryName(CATEGORY_INFRARED)))
			return getCatalogCategoryFolder(CATEGORY_INFRARED);
		if (name.equals(getCatalogCategoryName(CATEGORY_XRAY)))
			return getCatalogCategoryFolder(CATEGORY_XRAY);
		if (name.equals(getCatalogCategoryName(CATEGORY_VARIABLE)))
			return getCatalogCategoryFolder(CATEGORY_VARIABLE);
		if (name.equals(getCatalogCategoryName(CATEGORY_COMET_AND_ASTEROID)))
			return getCatalogCategoryFolder(CATEGORY_COMET_AND_ASTEROID);
		if (name.equals(getCatalogCategoryName(CATEGORY_CLUSTER_AND_NEBULA)))
			return getCatalogCategoryFolder(CATEGORY_CLUSTER_AND_NEBULA);
		if (name.equals(getCatalogCategoryName(CATEGORY_ERROR)))
			return getCatalogCategoryFolder(CATEGORY_ERROR);
		if (name.equals(getCatalogCategoryName(CATEGORY_OTHERS)))
			return getCatalogCategoryFolder(CATEGORY_OTHERS);
		return "";
	}

	/**
	 * Converts the specified category folder to the category name.
	 * @param folder the folder name of category.
	 * @return the category name.
	 */
	public static String convertCategoryFolderToName ( String folder ) {
		if (folder.equalsIgnoreCase(getCatalogCategoryFolder(CATEGORY_STAR)))
			return getCatalogCategoryName(CATEGORY_STAR);
		if (folder.equalsIgnoreCase(getCatalogCategoryFolder(CATEGORY_INFRARED)))
			return getCatalogCategoryName(CATEGORY_INFRARED);
		if (folder.equalsIgnoreCase(getCatalogCategoryFolder(CATEGORY_XRAY)))
			return getCatalogCategoryName(CATEGORY_XRAY);
		if (folder.equalsIgnoreCase(getCatalogCategoryFolder(CATEGORY_VARIABLE)))
			return getCatalogCategoryName(CATEGORY_VARIABLE);
		if (folder.equalsIgnoreCase(getCatalogCategoryFolder(CATEGORY_COMET_AND_ASTEROID)))
			return getCatalogCategoryName(CATEGORY_COMET_AND_ASTEROID);
		if (folder.equalsIgnoreCase(getCatalogCategoryFolder(CATEGORY_CLUSTER_AND_NEBULA)))
			return getCatalogCategoryName(CATEGORY_CLUSTER_AND_NEBULA);
		if (folder.equalsIgnoreCase(getCatalogCategoryFolder(CATEGORY_ERROR)))
			return getCatalogCategoryName(CATEGORY_ERROR);
		if (folder.equalsIgnoreCase(getCatalogCategoryFolder(CATEGORY_OTHERS)))
			return getCatalogCategoryName(CATEGORY_OTHERS);
		return "";
	}

	/**
	 * Gets the list of catalog names in the specified category.
	 * @param category_name the name of category.
	 * @return the list of catalog names in the specified category.
	 */
	public static Vector getCatalogListInCategory ( String category_name ) {
		Vector list = new Vector();

		Vector l = getCatalogStarList();
		for (int i = 0 ; i < l.size() ; i++) {
			CatalogStar star = (CatalogStar)l.elementAt(i);
			if (star.getCatalogCategory().equals(category_name))
				list.addElement(star.getCatalogName());
		}

		return list;
	}

	/**
	 * Gets the list of catalog names which supports the regular 
	 * photometry.
	 * @return the list of catalog names which supports the regular 
	 * photometry.
	 */
	public static Vector getPhotometrySupportedCatalogList ( ) {
		Vector list = new Vector();

		Vector l = getCatalogStarList();
		for (int i = 0 ; i < l.size() ; i++) {
			CatalogStar star = (CatalogStar)l.elementAt(i);
			if (star.supportsPhotometry())
				list.addElement(star.getCatalogName());
		}

		return list;
	}

	/**
	 * Gets the list of catalog names which contains the magnitude 
	 * data.
	 * @return the list of catalog names which contains the magnitude 
	 * data.
	 */
	public static Vector getMagnitudeSupportedCatalogList ( ) {
		Vector list = new Vector();

		Vector l = getCatalogStarList();
		for (int i = 0 ; i < l.size() ; i++) {
			CatalogStar star = (CatalogStar)l.elementAt(i);
			if (star.supportsMagnitude()) {
				String[] names = star.getCatalogNamesWithMagnitudeSystem();
				for (int j = 0 ; j < names.length ; j++)
					list.addElement(names[j]);
			}
		}

		return list;
	}

	/**
	 * Gets the list of catalog names which supports the astrometry.
	 * @return the list of catalog names which supports the astrometry.
	 */
	public static Vector getAstrometrySupportedCatalogList ( ) {
		Vector list = new Vector();

		Vector l = getCatalogStarList();
		for (int i = 0 ; i < l.size() ; i++) {
			CatalogStar star = (CatalogStar)l.elementAt(i);
			if (star.supportsAstrometry())
				list.addElement(star.getCatalogName());
		}

		return list;
	}

	/**
	 * Gets the class name of the specified catalog name of a star.
	 * @param catalog_name the catalog name of a star.
	 * @return the class name.
	 */
	public static String getCatalogStarClassName ( String catalog_name ) {
		CatalogStar star = getCatalogStar(catalog_name);
		if (star != null)
			return star.getClass().getName();

		return "";
	}

	/**
	 * Converts the specified catalog name to the catalog acronym.
	 * @param catalog_name the name of catalog.
	 * @return the catalog acronym.
	 */
	public static String convertCatalogNameToAcronym ( String catalog_name ) {
		CatalogStar star = getCatalogStar(catalog_name);
		if (star != null)
			return star.getCatalogAcronym();

		return "";
	}

	/**
	 * Converts the specified catalog name to the catalog code.
	 * @param catalog_name the name of catalog.
	 * @return the catalog code.
	 */
	public static String convertCatalogNameToCode ( String catalog_name ) {
		CatalogStar star = getCatalogStar(catalog_name);
		if (star != null)
			return star.getCatalogCode();

		return catalog_name;
	}

	/**
	 * Converts the specified catalog name to the catalog folder name.
	 * @param catalog_name the name of catalog.
	 * @return the catalog folder name.
	 */
	public static String convertCatalogNameToFolder ( String catalog_name ) {
		CatalogStar star = getCatalogStar(catalog_name);
		if (star != null)
			return star.getCatalogFolderCode();

		return catalog_name;
	}

	/**
	 * Converts the specified catalog code to the catalog name.
	 * @param catalog_code the code of catalog.
	 * @return the catalog name.
	 */
	public static String convertCatalogCodeToName ( String catalog_code ) {
		Vector l = getCatalogStarList();
		for (int i = 0 ; i < l.size() ; i++) {
			CatalogStar star = (CatalogStar)l.elementAt(i);
			if (star.getCatalogCode().equals(catalog_code))
				return star.getCatalogName();
		}
		return catalog_code;
	}

	/**
	 * Converts the specified catalog folder to the catalog name.
	 * @param catalog_folder the folder name of catalog.
	 * @return the catalog name.
	 */
	public static String convertCatalogFolderToName ( String catalog_folder ) {
		Vector l = getCatalogStarList();
		for (int i = 0 ; i < l.size() ; i++) {
			CatalogStar star = (CatalogStar)l.elementAt(i);
			if (star.getCatalogFolderCode().equalsIgnoreCase(catalog_folder))
				return star.getCatalogName();
		}
		return catalog_folder;
	}

	/**
	 * Gets the default property to plot stars of the specified 
	 * catalog.
	 * @param catalog_name the name of catalog.
	 * @return the default property.
	 */
	public static PlotProperty getDefaultProperty ( String catalog_name ) {
		CatalogStar star = getCatalogStar(catalog_name);
		if (star != null)
			return star.getDefaultProperty();

		return new PlotProperty();
	}

	/**
	 * Gets a star object of the specified catalog name.
	 * @param catalog_name the catalog name of a star.
	 * @return a star object.
	 */
	private static CatalogStar getCatalogStar ( String catalog_name ) {
		Vector list = getCatalogStarList();
		for (int i = 0 ; i < list.size() ; i++) {
			CatalogStar star = (CatalogStar)list.elementAt(i);

			if (star.getCatalogName().equals(catalog_name))
				return star;

			String[] names = star.getCatalogNamesWithMagnitudeSystem();
			if (names != null) {
				for (int j = 0 ; j < names.length ; j++) {
					if (names[j].equals(catalog_name))
						return star;
				}
			}
		}

		return null;
	}

	/**
	 * Gets the list of catalog readers.
	 * @return the list of catalog readers.
	 */
	private static Vector getCatalogReaderList ( ) {
		Vector list = new Vector();

		list.addElement(new Gsc11Reader());
		list.addElement(new GscActReader());
		list.addElement(new UsnoA10Reader());
		list.addElement(new UsnoA20Reader());
		list.addElement(new TychoSeparatedReader());
		list.addElement(new TychoReader());
		list.addElement(new Tycho2Reader());
		list.addElement(new HipparcosReader());
		list.addElement(new TicReader());
		list.addElement(new HicReader());
		list.addElement(new Bsc5Reader());
		list.addElement(new SaoReader());
		list.addElement(new Ac2000Reader());
		list.addElement(new Ucac1Reader());
		list.addElement(new Tass4Reader());
		list.addElement(new NsvsReader());

		list.addElement(new GspcReader());
		list.addElement(new LoneosPhotometryReader());
		list.addElement(new LandoltReader());

		list.addElement(new IrasPscReader());
		list.addElement(new IrasFscReader());
		list.addElement(new Msx5cReader());
		list.addElement(new MsxReader());

		list.addElement(new GcvsReader());
		list.addElement(new Gcvs2000Reader());
		list.addElement(new NsvReader());
		list.addElement(new NsvSupplementReader());
		list.addElement(new ExtraGalacticReader());
		list.addElement(new MisVReader());
		list.addElement(new NewvarReader());
		list.addElement(new SupernovaReader());
		list.addElement(new M31NovaReader());

		list.addElement(new Asas3Reader());
		list.addElement(new NsvsRedVarsReader());
		list.addElement(new Hat199Reader());

		list.addElement(new GcssReader());
		list.addElement(new CgcsReader());
		list.addElement(new CssReader());
		list.addElement(new CgoReader());
		list.addElement(new WrReader());
		list.addElement(new VeronReader());

		list.addElement(new Ngc2000Reader());
		list.addElement(new UgcReader());
		list.addElement(new McgReader());
		list.addElement(new PgcReader());
		list.addElement(new LedaReader());
		list.addElement(new Sharpless2Reader());

		list.addElement(new MinorPlanetCheckerReader());
		list.addElement(new MpcFormatReader());

		list.addElement(new StmReader());

		list.addElement(new VizieRUsnoAReader());

		list.addElement(new AstrometricaReader());

		return list;
	}

	/**
	 * Gets the list of catalog writers.
	 * @return the list of catalog writers.
	 */
	public static Vector getCatalogWriterList ( ) {
		Vector list = new Vector();

		list.addElement(new AstrometricaWriter());

		return list;
	}

	/**
	 * Gets the list of catalog stars.
	 * @return the list of catalog stars.
	 */
	private static Vector getCatalogStarList ( ) {
		Vector list = new Vector();

		list.addElement(new Gsc11Star());
		list.addElement(new GscActStar());
		list.addElement(new UsnoA10Star());
		list.addElement(new UsnoA20Star());
		list.addElement(new TychoStar());
		list.addElement(new Tycho2Star());
		list.addElement(new HipparcosStar());
		list.addElement(new TicStar());
		list.addElement(new HicStar());
		list.addElement(new Bsc5Star());
		list.addElement(new SaoStar());
		list.addElement(new Ac2000Star());
		list.addElement(new Ucac1Star());
		list.addElement(new Tass4Star());
		list.addElement(new NsvsStar());

		list.addElement(new GspcStar());
		list.addElement(new LoneosPhotometryStar());
		list.addElement(new LandoltStar());

		list.addElement(new IrasPscStar());
		list.addElement(new IrasFscStar());
		list.addElement(new Msx5cStar());
		list.addElement(new MsxStar());

		list.addElement(new GcvsStar());
		list.addElement(new NsvStar());
		list.addElement(new ExtraGalacticStar());
		list.addElement(new MisVStar());
		list.addElement(new SupernovaStar());
		list.addElement(new M31NovaStar());

		list.addElement(new GcssStar());
		list.addElement(new CgcsStar());
		list.addElement(new Cgcs3Star());
		list.addElement(new CssStar());
		list.addElement(new CgoStar());
		list.addElement(new WrStar());
		list.addElement(new VeronStar());

		list.addElement(new NgcStar());
		list.addElement(new IcStar());
		list.addElement(new UgcStar());
		list.addElement(new McgStar());
		list.addElement(new PgcStar());
		list.addElement(new LedaStar());
		list.addElement(new Sharpless2Star());

		list.addElement(new A64Star());
		list.addElement(new A72cStar());
		list.addElement(new Aaa97bStar());
		list.addElement(new AfaStar());
		list.addElement(new AgprsStar());
		list.addElement(new AsasStar());
		list.addElement(new Asas3Star());
		list.addElement(new Axg1Star());
		list.addElement(new B86Star());
		list.addElement(new Bbe90Star());
		list.addElement(new Bdf99Star());
		list.addElement(new Bfa97Star());
		list.addElement(new BnVStar());
		list.addElement(new BisStar());
		list.addElement(new BpsStar());
		list.addElement(new BrhVStar());
		list.addElement(new CaseAFStar());
		list.addElement(new Cbb98Star());
		list.addElement(new Ctt83Star());
		list.addElement(new CeStar());
		list.addElement(new CfrsStar());
		list.addElement(new Cks91Star());
		list.addElement(new CghaStar());
		list.addElement(new ClsStar());
		list.addElement(new ChsmStar());
		list.addElement(new CsakVStar());
		list.addElement(new DenisBulgeStar());
		list.addElement(new D75Star());
		list.addElement(new Dhm99Star());
		list.addElement(new Di91Star());
		list.addElement(new DirectVStar());
		list.addElement(new DoStar());
		list.addElement(new E2Star());
		list.addElement(new EdinburghStar());
		list.addElement(new EcStar());
		list.addElement(new Eros2GsaStar());
		list.addElement(new ErosStar());
		list.addElement(new EsoHaStar());
		list.addElement(new EuveStar());
		list.addElement(new Fastt1Star());
		list.addElement(new Fastt2Star());
		list.addElement(new FaustStar());
		list.addElement(new FbqsStar());
		list.addElement(new FbsStar());
		list.addElement(new FocapStar());
		list.addElement(new Gmc2001Star());
		list.addElement(new GrbStar());
		list.addElement(new HadVStar());
		list.addElement(new HassfortherVStar());
		list.addElement(new HaroChaviraStar());
		list.addElement(new Hat199Star());
		list.addElement(new HbcStar());
		list.addElement(new HbhaStar());
		list.addElement(new HeStar());
		list.addElement(new HhStar());
		list.addElement(new HmxbStar());
		list.addElement(new Hpj88Star());
		list.addElement(new HrmVStar());
		list.addElement(new HsStar());
		list.addElement(new IbvsStar());
		list.addElement(new IfmStar());
		list.addElement(new IsogalPStar());
		list.addElement(new IsvStar());
		list.addElement(new JlStar());
		list.addElement(new KisoAStar());
		list.addElement(new KisoCStar());
		list.addElement(new Kp2001Star());
		list.addElement(new KugStar());
		list.addElement(new KuvStar());
		list.addElement(new KwbbeStar());
		list.addElement(new LanningStar());
		list.addElement(new LbqsStar());
		list.addElement(new LdStar());
		list.addElement(new Lf1Star());
		list.addElement(new LhsStar());
		list.addElement(new LmcStar());
		list.addElement(new LmxbStar());
		list.addElement(new LowellGStar());
		list.addElement(new LsStar());
		list.addElement(new LssStar());
		list.addElement(new LwdStar());
		list.addElement(new Ma93Star());
		list.addElement(new Mbh96Star());
		list.addElement(new Mfl2000Star());
		list.addElement(new Mh95Star());
		list.addElement(new MlaStar());
		list.addElement(new MoaStar());
		list.addElement(new MrkStar());
		list.addElement(new Mt91Star());
		list.addElement(new Ngc6712VStar());
		list.addElement(new NgStar());
		list.addElement(new NovaStar());
		list.addElement(new NsvsRedVarsStar());
		list.addElement(new Ogle2BulgeStar());
		list.addElement(new OgleEwsStar());
		list.addElement(new OgleLtStar());
		list.addElement(new OgleTrStar());
		list.addElement(new OglePeriodicStar());
		list.addElement(new OmhrStar());
		list.addElement(new Ow94Star());
		list.addElement(new PbStar());
		list.addElement(new PhlStar());
		list.addElement(new PnGStar());
		list.addElement(new Qz2Star());
		list.addElement(new PejStar());
		list.addElement(new Re2Star());
		list.addElement(new RjhaStar());
		list.addElement(new RosatStar());
		list.addElement(new Rotse1Star());
		list.addElement(new Rrw93Star());
		list.addElement(new RxStar());
		list.addElement(new Rxs1Star());
		list.addElement(new Sax1Star());
		list.addElement(new SandStar());
		list.addElement(new SbsStar());
		list.addElement(new SdssCvStar());
		list.addElement(new SdssQuasarStar());
		list.addElement(new SavsStar());
		list.addElement(new StareStar());
		list.addElement(new TaQStar());
		list.addElement(new TaSVStar());
		list.addElement(new TaVStar());
		list.addElement(new TassStar());
		list.addElement(new TerzVStar());
		list.addElement(new TmzVStar());
		list.addElement(new ToaVStar());
		list.addElement(new TbrVStar());
		list.addElement(new TychoVarStar());
		list.addElement(new UhaStar());
		list.addElement(new UmStar());
		list.addElement(new UsStar());
		list.addElement(new VdbhStar());
		list.addElement(new W59Star());
		list.addElement(new WakudaStar());
		list.addElement(new WdStar());
		list.addElement(new WgStar());
		list.addElement(new YaloStar());
		list.addElement(new Zhm99Star());

		list.addElement(new OtherVariableStar());
		list.addElement(new OtherStar());

		list.addElement(new AstrometricaStar());

		list.addElement(new MinorPlanetCheckerStar());
		list.addElement(new MpcFormatStar());

		list.addElement(new StmStar());

		list.addElement(new UserStar());

		list.addElement(getSampleStarCatalogStar());

		list.addElement(new DetectedStar());

		return list;
	}

	/**
	 * Selects the highest prioritized catalog star in magnitude 
	 * report to the VSNET among the selected list.
	 * @param list the list of catalog stars.
	 * @return the highest prioritized star object.
	 */
	public static Star selectTypicalVsnetCatalogStar ( Vector list ) {
		Star star = null;
		int priority = -1;

		for (int i = 0 ; i < list.size() ; i++) {
			Star s = (Star)list.elementAt(i);
			int p = 0;

			if (s instanceof GcvsStar)
				p = 1000;

			if (s instanceof NsvStar)
				p = 750;

			if (s instanceof ExtraGalacticStar)
				p = 700;

			if (s instanceof MisVStar)
				p = 500;

			if (s instanceof SupernovaStar)
				p = 100;
			if (s instanceof M31NovaStar)
				p = 100;

			if (s instanceof A64Star)
				p = 100;
			if (s instanceof A72cStar)
				p = 100;
			if (s instanceof Aaa97bStar)
				p = 100;
			if (s instanceof AfaStar)
				p = 100;
			if (s instanceof AgprsStar)
				p = 100;
			if (s instanceof AsasStar)
				p = 100;
			if (s instanceof Asas3Star)
				p = 100;
			if (s instanceof Axg1Star)
				p = 100;
			if (s instanceof B86Star)
				p = 100;
			if (s instanceof Bbe90Star)
				p = 100;
			if (s instanceof Bdf99Star)
				p = 100;
			if (s instanceof Bfa97Star)
				p = 100;
			if (s instanceof BnVStar)
				p = 100;
			if (s instanceof BisStar)
				p = 100;
			if (s instanceof BpsStar)
				p = 100;
			if (s instanceof BrhVStar)
				p = 100;
			if (s instanceof CaseAFStar)
				p = 100;
			if (s instanceof Cbb98Star)
				p = 100;
			if (s instanceof Ctt83Star)
				p = 100;
			if (s instanceof CeStar)
				p = 100;
			if (s instanceof CfrsStar)
				p = 100;
			if (s instanceof Cks91Star)
				p = 100;
			if (s instanceof CghaStar)
				p = 100;
			if (s instanceof ClsStar)
				p = 100;
			if (s instanceof ChsmStar)
				p = 100;
			if (s instanceof CsakVStar)
				p = 100;
			if (s instanceof DenisBulgeStar)
				p = 100;
			if (s instanceof D75Star)
				p = 100;
			if (s instanceof Dhm99Star)
				p = 100;
			if (s instanceof Di91Star)
				p = 100;
			if (s instanceof DirectVStar)
				p = 100;
			if (s instanceof DoStar)
				p = 100;
			if (s instanceof E2Star)
				p = 100;
			if (s instanceof EdinburghStar)
				p = 100;
			if (s instanceof EcStar)
				p = 100;
			if (s instanceof Eros2GsaStar)
				p = 100;
			if (s instanceof ErosStar)
				p = 100;
			if (s instanceof EsoHaStar)
				p = 100;
			if (s instanceof EuveStar)
				p = 100;
			if (s instanceof Fastt1Star)
				p = 100;
			if (s instanceof Fastt2Star)
				p = 100;
			if (s instanceof FaustStar)
				p = 100;
			if (s instanceof FbqsStar)
				p = 100;
			if (s instanceof FbsStar)
				p = 100;
			if (s instanceof FocapStar)
				p = 100;
			if (s instanceof Gmc2001Star)
				p = 100;
			if (s instanceof GrbStar)
				p = 100;
			if (s instanceof HadVStar)
				p = 100;
			if (s instanceof HassfortherVStar)
				p = 100;
			if (s instanceof HaroChaviraStar)
				p = 100;
			if (s instanceof Hat199Star)
				p = 100;
			if (s instanceof HbcStar)
				p = 100;
			if (s instanceof HbhaStar)
				p = 100;
			if (s instanceof HeStar)
				p = 100;
			if (s instanceof HhStar)
				p = 100;
			if (s instanceof HmxbStar)
				p = 100;
			if (s instanceof Hpj88Star)
				p = 100;
			if (s instanceof HrmVStar)
				p = 100;
			if (s instanceof HsStar)
				p = 100;
			if (s instanceof IbvsStar)
				p = 100;
			if (s instanceof IfmStar)
				p = 100;
			if (s instanceof IsogalPStar)
				p = 100;
			if (s instanceof IsvStar)
				p = 100;
			if (s instanceof JlStar)
				p = 100;
			if (s instanceof KisoAStar)
				p = 100;
			if (s instanceof KisoCStar)
				p = 100;
			if (s instanceof Kp2001Star)
				p = 100;
			if (s instanceof KugStar)
				p = 100;
			if (s instanceof KuvStar)
				p = 100;
			if (s instanceof KwbbeStar)
				p = 100;
			if (s instanceof LanningStar)
				p = 100;
			if (s instanceof LbqsStar)
				p = 100;
			if (s instanceof LdStar)
				p = 100;
			if (s instanceof Lf1Star)
				p = 100;
			if (s instanceof LhsStar)
				p = 100;
			if (s instanceof LmcStar)
				p = 100;
			if (s instanceof LmxbStar)
				p = 100;
			if (s instanceof LowellGStar)
				p = 100;
			if (s instanceof LsStar)
				p = 100;
			if (s instanceof LssStar)
				p = 100;
			if (s instanceof LwdStar)
				p = 100;
			if (s instanceof Ma93Star)
				p = 100;
			if (s instanceof Mbh96Star)
				p = 100;
			if (s instanceof Mfl2000Star)
				p = 100;
			if (s instanceof Mh95Star)
				p = 100;
			if (s instanceof MlaStar)
				p = 100;
			if (s instanceof MoaStar)
				p = 100;
			if (s instanceof MrkStar)
				p = 100;
			if (s instanceof Mt91Star)
				p = 100;
			if (s instanceof Ngc6712VStar)
				p = 100;
			if (s instanceof NgStar)
				p = 100;
			if (s instanceof NovaStar)
				p = 100;
			if (s instanceof NsvsRedVarsStar)
				p = 100;
			if (s instanceof Ogle2BulgeStar)
				p = 100;
			if (s instanceof OgleEwsStar)
				p = 100;
			if (s instanceof OgleLtStar)
				p = 100;
			if (s instanceof OgleTrStar)
				p = 100;
			if (s instanceof OglePeriodicStar)
				p = 100;
			if (s instanceof OmhrStar)
				p = 100;
			if (s instanceof Ow94Star)
				p = 100;
			if (s instanceof PbStar)
				p = 100;
			if (s instanceof PhlStar)
				p = 100;
			if (s instanceof PnGStar)
				p = 100;
			if (s instanceof Qz2Star)
				p = 100;
			if (s instanceof PejStar)
				p = 100;
			if (s instanceof Re2Star)
				p = 100;
			if (s instanceof RjhaStar)
				p = 100;
			if (s instanceof RosatStar)
				p = 100;
			if (s instanceof Rotse1Star)
				p = 100;
			if (s instanceof Rrw93Star)
				p = 100;
			if (s instanceof RxStar)
				p = 100;
			if (s instanceof Rxs1Star)
				p = 100;
			if (s instanceof Sax1Star)
				p = 100;
			if (s instanceof SandStar)
				p = 100;
			if (s instanceof SbsStar)
				p = 100;
			if (s instanceof SdssCvStar)
				p = 100;
			if (s instanceof SdssQuasarStar)
				p = 100;
			if (s instanceof SavsStar)
				p = 100;
			if (s instanceof StareStar)
				p = 100;
			if (s instanceof TaQStar)
				p = 100;
			if (s instanceof TaSVStar)
				p = 100;
			if (s instanceof TaVStar)
				p = 100;
			if (s instanceof TassStar)
				p = 100;
			if (s instanceof TerzVStar)
				p = 100;
			if (s instanceof TmzVStar)
				p = 100;
			if (s instanceof ToaVStar)
				p = 100;
			if (s instanceof TbrVStar)
				p = 100;
			if (s instanceof TychoVarStar)
				p = 100;
			if (s instanceof UhaStar)
				p = 100;
			if (s instanceof UmStar)
				p = 100;
			if (s instanceof UsStar)
				p = 100;
			if (s instanceof VdbhStar)
				p = 100;
			if (s instanceof W59Star)
				p = 100;
			if (s instanceof WakudaStar)
				p = 100;
			if (s instanceof WdStar)
				p = 100;
			if (s instanceof WgStar)
				p = 100;
			if (s instanceof YaloStar)
				p = 100;
			if (s instanceof Zhm99Star)
				p = 100;

			if (s instanceof GcssStar)
				p = 100;
			if (s instanceof CgcsStar)
				p = 100;
			if (s instanceof Cgcs3Star)
				p = 100;
			if (s instanceof CssStar)
				p = 100;
			if (s instanceof CgoStar)
				p = 100;
			if (s instanceof WrStar)
				p = 100;
			if (s instanceof VeronStar)
				p = 100;
			if (s instanceof StmStar)
				p = 100;

			if (s instanceof OtherVariableStar)
				p = 100;
			if (s instanceof OtherStar)
				p = 100;

			if (s instanceof AstrometricaStar)
				p = 90;

			if (s instanceof NgcStar)
				p = 80;

			if (s instanceof IcStar)
				p = 75;

			if (s instanceof UgcStar)
				p = 70;

			if (s instanceof McgStar)
				p = 65;

			if (s instanceof PgcStar)
				p = 62;

			if (s instanceof LedaStar)
				p = 60;

			if (s instanceof Sharpless2Star)
				p = 55;

			if (s instanceof IrasFscStar)
				p = 45;
			if (s instanceof IrasPscStar)
				p = 50;
			if (s instanceof Msx5cStar)
				p = 42;
			if (s instanceof MsxStar)
				p = 40;

			if (s instanceof GspcStar)
				p = 31;
			if (s instanceof LoneosPhotometryStar)
				p = 30;
			if (s instanceof LandoltStar)
				p = 29;

			if (s instanceof GscActStar)
				p = 20;
			if (s instanceof Gsc11Star)
				p = 20;
			if (s instanceof UsnoA10Star)
				p = 10;
			if (s instanceof UsnoA20Star)
				p = 10;

			if (s instanceof TychoStar)
				p = 5;
			if (s instanceof Tycho2Star)
				p = 5;
			if (s instanceof HipparcosStar)
				p = 5;
			if (s instanceof TicStar)
				p = 5;
			if (s instanceof HicStar)
				p = 5;

			if (s instanceof Bsc5Star)
				p = 4;
			if (s instanceof SaoStar)
				p = 4;
			if (s instanceof Ac2000Star)
				p = 4;
			if (s instanceof Ucac1Star)
				p = 4;
			if (s instanceof Tass4Star)
				p = 4;

			if (s instanceof NsvsStar)
				p = 3;

			if (s instanceof UserStar)
				p = 2;

			if (s instanceof SampleStar)
				p = 1;

			if (s instanceof DetectedStar)
				p = 0;
			if (s instanceof StarImage)
				p = 0;

			if (priority < p) {
				star = s;
				priority = p;
			}
		}

		return star;
	}

	/**
	 * Gets the sample star catalog reader.
	 * @return the sample star catalog reader.
	 */
	public static CatalogReader getSampleStarCatalogReader ( ) {
		return new SampleReader();
	}

	/**
	 * Gets the sample star.
	 * @return the sample star.
	 */
	public static CatalogStar getSampleStarCatalogStar ( ) {
		return new SampleStar();
	}
}
