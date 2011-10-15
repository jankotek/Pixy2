/*
 * @(#)MagnitudeDatabaseTree.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.io.IOException;
import java.util.*;
import net.aerith.misao.util.*;
import net.aerith.misao.util.star.*;
import net.aerith.misao.catalog.*;
import net.aerith.misao.gui.event.*;
import net.aerith.misao.database.*;

/**
 * The <code>MagnitudeDatabaseTree</code> represents a tree of 
 * catalogs in the magnitude database classified into some groups.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 September 18
 */

public class MagnitudeDatabaseTree extends DatabaseCatalogTree {
	/**
	 * Constructs a <code>MagnitudeDatabaseTree</code>.
	 * @param db_manager the database manager.
	 */
	public MagnitudeDatabaseTree ( GlobalDBManager db_manager ) {
		super(db_manager);
	}

	/**
	 * Gets the catalog tree manager.
	 * @return the catalog tree manager.
	 */
	protected CatalogTreeManager getCatalogTreeManager ( ) {
		return db_manager.getMagnitudeDBManager();
	}
}
