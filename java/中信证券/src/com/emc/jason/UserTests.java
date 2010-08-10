package com.emc.jason;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

public class UserTests {

	private static void log(String str) {
		System.out.println(str);
	}

	
	public void run() throws DfException {
		MySessionManager sessionManager = new MySessionManager("CITICS", "user1", "user1");
		IDfSession session = sessionManager.getSession();
		listCabinets(sessionManager, session);
		sessionManager.releaseSession(session);
	}
	
	private void logFolderProperties(IDfFolder folder) throws DfException {
		log("\tis_private:\t" + folder.getBoolean("is_private"));
		log("\ta_is_hidden:\t" + folder.getBoolean("a_is_hidden"));
		log("\towner_name:\t" + folder.getOwnerName());
		log("\tr_object_id:\t" + folder.getObjectId());
	}


	private void listCabinets(MySessionManager sessionManager, IDfSession session) throws DfException {
		IDfCollection col = sessionManager.getCabinets(session);
		while (col.next()) {
			IDfFolder cabinet = (IDfFolder) session.getObject(col.getTypedObject().getId("r_object_id"));
			log(cabinet.getObjectName());
			logFolderProperties(cabinet);
		}
	}

	public static void main(String[] args) throws DfException {
		UserTests tests = new UserTests();
		tests.run();
	}

}
