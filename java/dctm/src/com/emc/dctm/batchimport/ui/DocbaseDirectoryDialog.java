package com.emc.dctm.batchimport.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.emc.dctm.service.SessionFactory;

public class DocbaseDirectoryDialog extends Dialog {

	private String text;

	private String message;

	private Shell shell;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public DocbaseDirectoryDialog(Shell parent) {
		super(parent);
	}

	public void open() {
		Shell parent = getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(text);
		shell.setSize(320, 500);
		WindowUtil.centerWindow(shell, parent);

		initControls();

		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void initControls() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		shell.setLayout(gridLayout);

		Label labelMessage = new Label(shell, SWT.NONE);
		labelMessage.setText(message);

		Tree treeFolder = new Tree(shell, SWT.BORDER);

		TreeItem rootItem = buildInitTree(treeFolder);
		DocbaseTreeListener treeListener = new DocbaseTreeListener(rootItem);
		treeFolder.addTreeListener(treeListener);

		Button buttonMakeNewCabinet = new Button(shell, SWT.PUSH);
		buttonMakeNewCabinet.setText("Make new cabinet");

		Button buttonMakeNewFolder = new Button(shell, SWT.PUSH);
		buttonMakeNewFolder.setText("Make new folder");

		Button buttonOk = new Button(shell, SWT.PUSH);
		buttonOk.setText("OK");

		Button buttonCancel = new Button(shell, SWT.PUSH);
		buttonCancel.setText("Cancel");

		labelMessage.setLayoutData(getSpanLayoutData());

		GridData gridData = getSpanLayoutData();
		gridData.heightHint = 390;
		treeFolder.setLayoutData(gridData);
	}

	private TreeItem buildInitTree(Tree treeFolder) {
		TreeItem rootItem = new TreeItem(treeFolder, SWT.NONE);
		rootItem.setText(SessionFactory.getInstance().getDocbaseName());
		Image icon = new Image(shell.getDisplay(), "images/icons/RootIcon.gif");
		rootItem.setImage(icon);

		IDfCollection collection = null;
		try {
			IDfSession session = SessionFactory.getInstance().getDfSession();
			IDfQuery q = SessionFactory.getInstance().getDfQuery();
			q.setDQL("SELECT FOR WRITE object_name FROM dm_cabinet");
			collection = q.execute(session, IDfQuery.DF_READ_QUERY);;
			while (collection.next()) {
				TreeItem cabItem = new TreeItem(rootItem, SWT.NONE);
				cabItem.setText(collection.getString("object_name"));
				Image iconCab = new Image(shell.getDisplay(), "images/icons/CabinetIcon.gif");
				cabItem.setImage(iconCab);
				
				TreeItem subItem = new TreeItem(cabItem, SWT.NONE);
				subItem.setText("Place holder");
			}
		} catch (DfException e) {
			e.printStackTrace();
		} finally {
			try {
				if (collection != null) {
					collection.close();
				}
			} catch (DfException e) {
				e.printStackTrace();
			}
		}
		
		rootItem.setExpanded(true);
		return rootItem;
	}

	private GridData getSpanLayoutData() {
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 4;
		return data;
	}

}
