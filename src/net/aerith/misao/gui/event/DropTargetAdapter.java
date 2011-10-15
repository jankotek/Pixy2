/*
 * @(#)DropTargetAdapter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.event;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

/**
 * The <code>DropTargetAdapter</code> represents a listener class of 
 * drop event. Nothing is implemented in this class.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2001 July 31
 */

public class DropTargetAdapter implements DropTargetListener {
	/**
	 * Called when a drag operation has encountered the 
	 * <code>DropTarget</code>.
	 * @param dtde the <code>DropTargetDragEvent</code> 
	 */
	public void dragEnter ( DropTargetDragEvent dtde ) {
	}

	/**
	 * Called when a drag operation is ongoing  on the 
	 * <code>DropTarget</code>.
	 * @param dtde the <code>DropTargetDragEvent</code> 
	 */
	public void dragOver ( DropTargetDragEvent dtde ) {
	}

	/**
	 * Called if the user has modified the current drop gesture.
	 * @param dtde the <code>DropTargetDragEvent</code>
	 */
	public void dropActionChanged ( DropTargetDragEvent dtde ) {
	}

	/**
	 * The drag operation has departed the <code>DropTarget</code> 
	 * without dropping.
	 * @param dte the <code>DropTargetEvent</code> 
	 */
	public void dragExit ( DropTargetEvent dte ) {
	}

	/**
	 * The drag operation has terminated with a drop on this 
	 * <code>DropTarget</code>.
	 * @param dtde the <code>DropTargetDropEvent</code> 
	 */
	public void drop ( DropTargetDropEvent dtde ) {
	}
}
