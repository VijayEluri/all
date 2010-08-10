package com.emc.jason;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.xml.xquery.IDfXQuery;

public class TestXQuery {
	
	private void run() throws DfException {
		MySessionManager smgr = new MySessionManager("DEMO", "dmadmin", "dmadmin");
		IDfSession session = smgr.getSession();
		IDfXQuery xquery = smgr.getXQuery();
		
		StringBuilder sb = new StringBuilder();
		sb.append("for $rec in //OfflineData/Record ");
		sb.append("where $rec/AccountNumber = \"800100426508213001\" ");
		sb.append("return $rec");
		
		xquery.setXQueryString(sb.toString());
		xquery.execute(session);
		
		System.out.println(xquery.getXMLString());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestXQuery test = new TestXQuery();
		try {
			test.run();
		} catch (DfException e) {
			e.printStackTrace();
		}
	}

}
