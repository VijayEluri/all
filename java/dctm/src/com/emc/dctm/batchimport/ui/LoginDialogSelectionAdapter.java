package com.emc.dctm.batchimport.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MessageBox;

import com.emc.dctm.batchimport.data.LoginDialogData;

public class LoginDialogSelectionAdapter extends SelectionAdapter {
	
	private LoginDialog dialog;
	private LoginDialogData data;

	public LoginDialogSelectionAdapter(LoginDialog _dialog, LoginDialogData _data) {
		dialog = _dialog;
		data = _data;
	}
	
	public void widgetSelected(SelectionEvent e) {
		if (e.widget == dialog.buttonOk) {
			data.setUserName(dialog.textUserName.getText());
			data.setPassword(dialog.textPassword.getText());
			data.setDomain(dialog.textDomain.getText());
			data.setDocbaseName(dialog.comboDocbase.getText());
			if (data.tryLogin()) {
				data.setButtonResult(LoginDialogData.CONNECT_BUTTON);
				dialog.close();
			} else {
				MessageBox msg = new MessageBox(dialog.getParent(), SWT.OK | SWT.ICON_INFORMATION);
				msg.setText("Information");
				msg.setMessage("Login failed, please check your login information.");
				msg.open();
			}
		} else if (e.widget == dialog.buttonCancel) {
			data.setButtonResult(LoginDialogData.CANCEL_BUTTON);
			dialog.close();
		}
	}
}
