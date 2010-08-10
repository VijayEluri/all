import java.util.ArrayList;
import java.util.List;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.common.*;

class Test implements Runnable {

	static final int numOfThreads = 10;

	static final int numOfSessionsPerThread = 10;

	static final String USERNAME = "dmadmin";

	static final String PASSWORD = "dmadmin";

	static final String DOMAINNAME = "";

	static final String DOCBASE_NAME = "DEMO2";

	int number;

	public Test(int number) {
		this.number = number;
	}
	
	private IDfClientX clientX;
	private IDfLoginInfo loginInfo;
	private IDfSessionManager sessionManager;

	private IDfClientX getClientX() {
		if (clientX != null) return clientX;
		clientX = new DfClientX();
		return clientX;
	}

	private IDfSessionManager getSessionManager() {
		if (sessionManager != null) {
			return sessionManager;
		}
		
		loginInfo = getClientX().getLoginInfo();

		loginInfo.setUser(USERNAME);
		loginInfo.setPassword(PASSWORD);
		loginInfo.setDomain(DOMAINNAME);
		try {
			IDfClient dfClient = getClientX().getLocalClient();
			sessionManager = dfClient.newSessionManager();
			sessionManager.setIdentity(DOCBASE_NAME, loginInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sessionManager;
	}
	
	private IDfClientX createTicketClientX() {
		return new DfClientX();
	}
	
	private IDfSessionManager createTicketSessionManager(String docbase, String userName, String password) {
		IDfSessionManager ticketSessionManager = null;
		try {
			IDfClientX clientX = createTicketClientX();
			IDfClient client = clientX.getLocalClient();
			IDfLoginInfo loginInfo = clientX.getLoginInfo();
			loginInfo.setUser(userName);
			loginInfo.setPassword(password);
			ticketSessionManager = client.newSessionManager();
			ticketSessionManager.setIdentity(docbase, loginInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ticketSessionManager;
	}

	public void run() {
		try {
			List listSessions = new ArrayList();
			List listSessionManagers = new ArrayList();
			
			IDfSessionManager sessionManager = getSessionManager();

			IDfSession session = sessionManager.newSession(DOCBASE_NAME);
			String ticket = session.getLoginTicketEx("dmadmin", "global", 43200, false, "");

			while (true) {
				listSessions.clear();
				listSessionManagers.clear();
				for (int i = 0; i < numOfSessionsPerThread; i++) {
					try {
						IDfSessionManager ticketSessionManager = createTicketSessionManager(DOCBASE_NAME, USERNAME, "dmadmin");
						
						session = ticketSessionManager.getSession(DOCBASE_NAME);
						
						listSessions.add(session);
						listSessionManagers.add(ticketSessionManager);
						
						System.out.println("got session " + session.getSessionId() + " for thread " + this.number);
					} catch (Throwable th) {
						System.out.println("Exception While getting new Session for "
										+ DOCBASE_NAME + " for thread : " + this.number);
						th.printStackTrace();
					}
				} // end for
				// liberar
				for (int i = 0; listSessions.size() > i; i++) {
					IDfSession ses = (IDfSession) listSessions.get(i);
					IDfSessionManager ticketSessionManager = (IDfSessionManager) listSessionManagers.get(i);
					System.out.println("thread " + number + " releasing " + ses.getSessionId());
					ticketSessionManager.release(ses);
				} // end for{}
			}
			
			//sessionManager.release(session);
			// }
		} catch (Throwable th) {
			th.printStackTrace();
			System.out.println("Some Exception ...");
		} // end catch{}
	}

//	private static void warmUp() {
//		try {
//			IDfClientX clientX = new DfClientX();
//			IDfClient client = clientX.getLocalClient();
//			IDfSessionManager sessionManager = client.newSessionManager();
//			IDfSession[] sessions = new IDfSession[100];
//
//			IDfLoginInfo loginInfo = clientX.getLoginInfo();
//
//			loginInfo.setUser(userName);
//			loginInfo.setPassword(userPassword);
//			loginInfo.setDomain(userDomain);
//
//			sessionManager.setIdentity("DEMO2", loginInfo);
//			for (int i = 0; i < 100; ++i) {
//				sessions[i] = sessionManager.newSession(DOCBASE_NAME);
//				System.out.println("got session " + sessions[i].getSessionId());
//			}
//			for (int i = 0; i < 100; ++i) {
//				sessionManager.release(sessions[i]);
//			}
//		} catch (DfException e) {
//			e.printStackTrace();
//		}
//		
//	}

	public static void main(String[] args) {

		System.out.println("Inside main()");
//		warmUp();
		
		for (int i = 0; i < numOfThreads; i++) {
			Thread t;
			t = new Thread(new Test(i + 1));
			t.start();
			System.out.println("Started Thread#" + i);
		}
	} // end of main

} // end of class {}
