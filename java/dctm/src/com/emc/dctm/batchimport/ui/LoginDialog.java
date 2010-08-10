package com.emc.dctm.batchimport.ui;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.emc.dctm.batchimport.data.LoginDialogData;

public class LoginDialog extends Dialog {

	Label labelUserName;
	Text textUserName;
	Label labelPassword;
	Text textPassword;
	Label labelDomain;
	Text textDomain;
	Label labelDocbase;
	Combo comboDocbase;
	Button buttonOk;
	Button buttonCancel;
	private Shell shell;
	
	public LoginDialog(Shell parent) {
		super(parent);
	}
	
	public void close() {
		if (shell == null) return;
		shell.close();
	}
	
	public LoginDialogData open() {
		LoginDialogData data = new LoginDialogData();

		Shell parent = getParent();
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText("LOGIN DOCBASE");
		shell.setSize(300, 200);
		WindowUtil.centerWindow(shell, parent);
		
		shell.setLayout(new FormLayout());
		
		createInputControls(shell);
		initInputControls(data);
		
		LoginDialogSelectionAdapter adapter = new LoginDialogSelectionAdapter(this, data);
		
		buttonOk.addSelectionListener(adapter);
		buttonCancel.addSelectionListener(adapter);
		
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		
		return data;
	}

	private void initInputControls(LoginDialogData data) {
		List<String> docbaseNames = data.getDocbaseNames();
		for (int i = 0; i < docbaseNames.size(); ++i)
			comboDocbase.add(docbaseNames.get(i));
		if (comboDocbase.getItemCount() > 0)
			comboDocbase.select(0);
		
		textUserName.setText("dmadmin");
		textPassword.setText("dmadmin");
	}

	private void createInputControls(Shell shell) {
		Composite inputComp = new Composite(shell, SWT.NONE);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		inputComp.setLayout(gridLayout);
		
		labelUserName = new Label(inputComp, SWT.NONE);
		labelUserName.setText("User Name:");
		textUserName = new Text(inputComp, SWT.BORDER);

		labelPassword = new Label(inputComp, SWT.NONE);
		labelPassword.setText("Password:");
		textPassword = new Text(inputComp, SWT.BORDER | SWT.PASSWORD);

		labelDomain = new Label(inputComp, SWT.NONE);
		labelDomain.setText("Domain:");
		textDomain = new Text(inputComp, SWT.BORDER);

		labelDocbase = new Label(inputComp, SWT.NONE);
		labelDocbase.setText("Docbase:");
		comboDocbase = new Combo(inputComp, SWT.BORDER);

		labelUserName.setLayoutData(getWidthLayoutData());
		labelPassword.setLayoutData(getWidthLayoutData());
		labelDomain.setLayoutData(getWidthLayoutData());
		labelDocbase.setLayoutData(getWidthLayoutData());
		textUserName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textPassword.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textDomain.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboDocbase.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
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
		buttonOk.setText("Connect");
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

	private GridData getWidthLayoutData() {
		GridData data = new GridData();
		data.widthHint = 100;
		return data;
	}

}
