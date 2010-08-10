package com.emc.dctm.service;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;

public class SessionFactory {

	private static SessionFactory sessionFactory;

	public static SessionFactory getInstance() {
		if (sessionFactory == null) {
			sessionFactory = new SessionFactory();
		}
		return sessionFactory;
	}

	private IDfSessionManager sessionMgr = null;

	private IDfSession session = null;

	private IDfClient client = null;

	private String userName;

	private String domain;

	private String docbaseName;

	public IDfClient getDfClient() throws Exception {
		if (client == null)
			client = DfClient.getLocalClient();
		return client;
	}

	public IDfSession getDfSession(String userName, String password,
			String domain, String docbaseName) throws Exception {
		if (session != null) {
			if (userName.equals(this.userName) && domain.equals(this.domain)
					&& docbaseName.equals(this.docbaseName)) {
				return session;
			} else {
				throw new Exception("user " + this.userName
						+ " has logged in, please logoff first.");
			}
		}

		// Setup login details.
		IDfLoginInfo login = new DfLoginInfo();
		login.setUser(userName);
		login.setPassword(password);
		login.setDomain(domain);
		getSessionMgr().setIdentity(docbaseName, login);

		session = getSessionMgr().newSession(docbaseName);

		this.userName = userName;
		this.domain = domain;
		this.docbaseName = docbaseName;

		return session;
	}

	public IDfSession getDfSession() {
		return session;
	}

	public void release() throws Exception {
		if (session != null) {
			if (getSessionMgr() != null) {
				getSessionMgr().release(session);
			}
		}
	}

	private IDfSessionManager getSessionMgr() throws Exception {
		if (sessionMgr == null) {
			sessionMgr = getDfClient().newSessionManager();
		}
		return sessionMgr;
	}

	public String getDocbaseName() {
		return docbaseName;
	}

	public IDfQuery getDfQuery() {
		IDfClientX clientx = new DfClientX();
		return clientx.getQuery(); // Create query object
	}
}
