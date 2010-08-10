package com.emc.dctm.batchimport.ui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;

import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.emc.dctm.service.SessionFactory;

public class DocbaseTreeListener implements TreeListener {
	
	private TreeItem rootItem;
	
	public DocbaseTreeListener(TreeItem rootItem) {
		this.rootItem = rootItem;
	}

	public void treeCollapsed(TreeEvent e) {
	}

	public void treeExpanded(TreeEvent e) {
		TreeItem item = (TreeItem) e.item;
		if (item == null) return;
		if (item == rootItem) return;
		item.removeAll();
		
        IDfCollection collection = null;
	    try {
			IDfSession session = SessionFactory.getInstance().getDfSession();
			IDfFolder folder = session.getFolderByPath(buildPathFromTreeItem(item));
			collection = folder.getContents("object_name,r_object_type");
	        while(collection.next()) {
	        	String type = collection.getString("r_object_type");
	        	if (type.equals("dm_folder")) {
	        		TreeItem folderItem = new TreeItem(item, SWT.NONE);
	        		folderItem.setText(collection.getString("object_name"));
					Image iconFolder = new Image(item.getParent().getDisplay(), "images/icons/FolderIcon.gif");
					folderItem.setImage(iconFolder);

					TreeItem subItem = new TreeItem(folderItem, SWT.NONE);
					subItem.setText("Place holder");
	        	}
	        }
		} catch (DfException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (collection != null) {
					collection.close();
				}
			} catch (DfException e2) {
				e2.printStackTrace();
			}
		}
	       		
	}
	
	private String buildPathFromTreeItem(TreeItem item) {
		if (item == rootItem) return null;
		ArrayList<String> items = new ArrayList<String>();
		StringBuffer strBuf = new StringBuffer();
		while(item != rootItem) {
			items.add(item.getText());
			item = item.getParentItem();
		}
		for (int i = items.size() - 1; i >= 0; --i) {
			strBuf.append("/");
			strBuf.append(items.get(i));
		}
		return strBuf.toString();
	}

}
