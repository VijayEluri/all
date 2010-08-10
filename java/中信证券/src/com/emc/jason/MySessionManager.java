package com.emc.jason;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;
import com.documentum.xml.xquery.IDfXQuery;

public class MySessionManager {

	private String docbase;
	private IDfSessionManager sessionManager;
	private IDfClientX clientX;
	
	public MySessionManager(String docbase, String userName, String password) throws DfException {
		this.docbase = docbase;

		clientX = new DfClientX();
		IDfClient client = clientX.getLocalClient();
		
		sessionManager = client.newSessionManager();
		
		IDfLoginInfo loginInfo = clientX.getLoginInfo();
		loginInfo.setUser(userName);
		loginInfo.setPassword(password);
		
		sessionManager.setIdentity(docbase, loginInfo);
		
	}
	
	public IDfSession getSession() throws DfException {
		assert(sessionManager != null);
		assert(docbase != null);
		return sessionManager.getSession(docbase);
	}
	
	public void releaseSession(IDfSession session) throws DfException {
		assert(sessionManager != null);
		assert(session != null);
		sessionManager.release(session);
	}
	
	public IDfQuery getQuery() {
		assert(clientX != null);
		return clientX.getQuery();
	}
	
	public IDfXQuery getXQuery() {
		assert(clientX != null);
		return clientX.getXQuery();
	}
	
	public IDfCollection getCabinets(IDfSession session) throws DfException {
		IDfQuery query = getQuery();
		query.setDQL("select r_object_id from dm_cabinet");
		return query.execute(session, IDfQuery.DF_READ_QUERY);
	}

}
