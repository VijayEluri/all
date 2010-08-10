package com.emc.dctm.batchimport.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class ImportDialog  extends Dialog {

	private Shell shell;
	Button buttonOk;
	Button buttonCancel;
	Button buttonBrowseSrcFolder;
	Button buttonBrowseDestFolder;
	Text textSrcFolder;

	public ImportDialog(Shell parent) {
		super(parent);
	}

	public void open() {

		Shell parent = getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText("IMPORT INTO DOCBASE");
		shell.setSize(400, 210);
		WindowUtil.centerWindow(shell, parent);
		
		shell.setLayout(new FormLayout());

		ImportDialogSelectionAdapter adapter = new ImportDialogSelectionAdapter(this);

		initControls();
		
		buttonOk.addSelectionListener(adapter);
		buttonCancel.addSelectionListener(adapter);
		buttonBrowseSrcFolder.addSelectionListener(adapter);
		buttonBrowseDestFolder.addSelectionListener(adapter);

		shell.open();

		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void initControls() {
		Composite inputComp = new Composite(shell, SWT.NONE);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		inputComp.setLayout(gridLayout);
		
		Label labelFolder = new Label(inputComp, SWT.NONE);
		labelFolder.setText("Source local folder:");
		textSrcFolder = new Text(inputComp, SWT.BORDER);
		buttonBrowseSrcFolder = new Button(inputComp, SWT.PUSH);
		buttonBrowseSrcFolder.setText("...");

		Label labelFolder2 = new Label(inputComp, SWT.NONE);
		labelFolder2.setText("Target docbase folder:");
		Text textDestFolder = new Text(inputComp, SWT.BORDER);
		buttonBrowseDestFolder = new Button(inputComp, SWT.PUSH);
		buttonBrowseDestFolder.setText("...");
		
		Label labelThread = new Label(inputComp, SWT.NONE);
		labelThread.setText("Import threads:");
		Slider sliderThread = new Slider(inputComp, SWT.HORIZONTAL);
		Text textThread = new Text(inputComp, SWT.BORDER);
		
		Label labelProgress = new Label(inputComp, SWT.NONE);
		labelProgress.setText("Import progress:");
		ProgressBar progressImport = new ProgressBar(inputComp, SWT.HORIZONTAL);
		Label labelProgressStatus = new Label(inputComp, SWT.NONE);
		labelProgressStatus.setText("0/0");
		
		labelFolder.setLayoutData(getWidthLayoutData(130));
		textSrcFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonBrowseSrcFolder.setLayoutData(getWidthLayoutData(25));

		labelFolder2.setLayoutData(getWidthLayoutData(130));
		textDestFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonBrowseDestFolder.setLayoutData(getWidthLayoutData(25));

		labelThread.setLayoutData(getWidthLayoutData(130));
		sliderThread.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textThread.setLayoutData(getWidthLayoutData(25));

		labelProgress.setLayoutData(getWidthLayoutData(130));
		progressImport.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		labelProgressStatus.setLayoutData(getWidthLayoutData(25));

		FormData formInput = new FormData();
		formInput.top = new FormAttachment(0, 5);
		formInput.left = new FormAttachment(0, 5);
		formInput.bottom = new FormAttachment(70, 0);
		formInput.right = new FormAttachment(100, -5);
		inputComp.setLayoutData(formInput);
		Composite buttonComp = new Composite(shell, SWT.NONE);

		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 3;
		buttonComp.setLayout(gridLayout2);

		
		buttonOk = new Button(buttonComp, SWT.PUSH);
		shell.setDefaultButton(buttonOk);
		buttonOk.setText("Start");
		buttonCancel = new Button(buttonComp, SWT.PUSH);
		buttonCancel.setText("Cancel");
		
		buttonOk.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		buttonCancel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		FormData formButton = new FormData();
		formButton.top = new FormAttachment(70, 0);
		formButton.left = new FormAttachment(0, 5);
		formButton.bottom = new FormAttachment(100, 0);
		formButton.right = new FormAttachment(100, -5);
		buttonComp.setLayoutData(formButton);
	}

	private GridData getWidthLayoutData(int width) {
		GridData data = new GridData();
		data.widthHint = width;
		return data;
	}

}
