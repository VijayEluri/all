package com.emc.dctm.batchimport.data;

import java.util.ArrayList;
import java.util.List;

import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfDocbaseMap;
import com.emc.dctm.service.SessionFactory;

public class LoginDialogData {

	private int buttonResult;

	private String userName;

	private String password;

	private String domain;

	private String docbaseName;

	public static final int CONNECT_BUTTON = 1;

	public static final int CANCEL_BUTTON = 2;

	public List<String> getDocbaseNames() {
		ArrayList<String> names = new ArrayList<String>();
		try {
			IDfClient client = SessionFactory.getInstance().getDfClient();
			IDfDocbaseMap docbaseMap = client.getDocbaseMap();
			for (int i = 0; i < docbaseMap.getDocbaseCount(); ++i) {
				names.add(docbaseMap.getDocbaseName(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return names;
	}

	public void setButtonResult(int button) {
		buttonResult = button;
	}

	public int getButtonResult() {
		return buttonResult;
	}

	public String getDocbaseName() {
		return docbaseName;
	}

	public void setDocbaseName(String docbaseName) {
		this.docbaseName = docbaseName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public boolean tryLogin() {
		try {
			SessionFactory.getInstance().getDfSession(userName, password, domain, docbaseName);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
