/*
 * @(#)CatalogTreeSelectionListener.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.event;
import net.aerith.misao.util.star.Star;

/**
 * The <code>CatalogTreeSelectionListener</code> is a listener 
 * interface of node selection in <code>CatalogTree</code>.
 * classified into some groups.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 September 18
 */

public interface CatalogTreeSelectionListener {
	/** 
	 * Invoked when the root node is selected.
	 */
	public abstract void selectAll ( );

	/** 
	 * Invoked when the specified catalog category is selected.
	 * @param category_name the category name.
	 */
	public abstract void selectCategory ( String category_name );

	/** 
	 * Invoked when the specified catalog is selected.
	 * @param catalog_name the catalog name.
	 */
	public abstract void selectCatalog ( String catalog_name );

	/** 
	 * Invoked when the specified star is selected.
	 * @param star the star.
	 */
	public abstract void selectStar ( Star star );
}
