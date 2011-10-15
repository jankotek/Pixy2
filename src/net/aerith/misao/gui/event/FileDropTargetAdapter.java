/*
 * @(#)FileDropTargetAdapter.java
 *
 * Copyright (C) 1997-2007 Seiichi Yoshida
 * All rights reserved.
 */

package net.aerith.misao.gui.event;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;

/**
 * The <code>FileDropTargetAdapter</code> represents a listener class
 * of drop event of files. The method <tt>dropFile</tt> must be 
 * overrided in the subclasses.
 *
 * @author  Seiichi Yoshida (comet@aerith.net)
 * @version 2003 August 4
 */

public abstract class FileDropTargetAdapter extends DropTargetAdapter {
	/**
	 * Invoked when files are dropped.
	 * @param files the dropped files.
	 */
	public abstract void dropFiles ( File[] file );

	/**
	 * The drag operation has terminated with a drop on this 
	 * <code>DropTarget</code>.
	 * @param dtde the <code>DropTargetDropEvent</code> 
	 */
	public void drop ( DropTargetDropEvent dtde ) {
		int action = dtde.getDropAction();
		dtde.acceptDrop(action);

		try {
			Transferable content = dtde.getTransferable();

			if (content.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				java.util.List list = (java.util.List)content.getTransferData(DataFlavor.javaFileListFlavor);

				File[] files = new File[list.size()];

				Iterator iterator = list.iterator();
				int count = 0;
				while (iterator.hasNext()) {
					files[count] = (File)iterator.next();
					count++;
				}

				dropFiles(files);

				dtde.dropComplete(true);
				return;
			}
		} catch ( IOException exception ) {
		} catch ( UnsupportedFlavorException exception ) {
		}

		dtde.dropComplete(false);
	}
}
