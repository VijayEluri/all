package com.emc.dctm.batchimport.ui;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.DirectoryDialog;

public class ImportDialogSelectionAdapter extends SelectionAdapter {
	private ImportDialog dialog;

	public ImportDialogSelectionAdapter(ImportDialog _dialog) {
		dialog = _dialog;
	}
	
	public void widgetSelected(SelectionEvent e) {
		if (e.widget == dialog.buttonBrowseSrcFolder) {
			DirectoryDialog dirDialog = new DirectoryDialog(dialog.getParent());
			dirDialog.setText("Select source directory");
			dirDialog.setMessage("Please select the source directory to import.");
			dirDialog.setFilterPath(dialog.textSrcFolder.getText());
			String dir = dirDialog.open();
			if (dir != null)
				dialog.textSrcFolder.setText(dir);
		} else if (e.widget == dialog.buttonBrowseDestFolder) {
			DocbaseDirectoryDialog docbaseDialog = new DocbaseDirectoryDialog(dialog.getParent());
			docbaseDialog.setText("Select destination folder");
			docbaseDialog.setMessage("Please select the destination docbase folder.");
			docbaseDialog.open();
		}
	}

}
