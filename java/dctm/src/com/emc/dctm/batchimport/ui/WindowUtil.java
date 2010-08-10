package com.emc.dctm.batchimport.ui;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

public class WindowUtil {
	public static void centerWindow(Shell fgWindow, Shell bgWindow) {
		Rectangle fgRect, bgRect;
		bgRect = bgWindow.getBounds();
		fgRect = fgWindow.getBounds();
		
		fgWindow.setLocation(bgRect.x + (bgRect.width - fgRect.width) / 2, 
				bgRect.y + (bgRect.height - fgRect.width) / 2);
	}
}