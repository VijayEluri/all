import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;

import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


class Test3 implements Runnable {
    static final int nbOfThreads = 20;
    static final int maxNbOfConcurUsersPerThread = 3;
    static final int nbOfSessionsPerUser = 1;
    static final String userName = "dmadmin";
    static final String userPassword = "dmadmin";
    static final String userDomain = "";
    static final String docBaseName = "DEMO2";
    int number;
    IDfClientX clientX;

    public Test3(int number) {
        this.number = number;
    }

    private void testTicket(String ticket)
        throws DfException {
        
        DfLoginInfo dflogininfo = new DfLoginInfo();
        dflogininfo.setUser(userName);
        dflogininfo.setPassword(ticket);
        
        IDfSessionManager sMgr = clientX.getLocalClient().newSessionManager();

        sMgr.clearIdentity(docBaseName);
        sMgr.setIdentity(docBaseName, dflogininfo);
        
        // release ticket sessions manager 
        IDfSession tmpSession = sMgr.getSession(docBaseName);
        sMgr.release(tmpSession);
        System.out.println("ticket test ok");
    }

    public void run() {
    	List listacon = new ArrayList();
        try {
        	clientX = new DfClientX();
            IDfClient client = clientX.getLocalClient();
            
        	while (true) {
        		// simulate random nb of users per thread for each loop
                Random generator = new Random();
                int nbOfUsers = generator.nextInt(maxNbOfConcurUsersPerThread);
                System.out.println("nbOfUsers for thread #" + this.number + " = " + nbOfUsers);
        		
                for (int i = 0; i < maxNbOfConcurUsersPerThread; i++) {
                	System.gc();
                	listacon.clear();
                	
                	// for each user, create 1 session manager to get several sessions
                	// N sessions by password
                	// 1 session by ticket
                	IDfSessionManager sMgr = client.newSessionManager();
                	
                    // set the identity on the session manager
                    IDfLoginInfo loginInfoObj = clientX.getLoginInfo();
                    
                    loginInfoObj.setUser(userName);
                    loginInfoObj.setPassword(userPassword);
                    loginInfoObj.setDomain(userDomain);

                    sMgr.setIdentity(docBaseName, loginInfoObj);
                    
                    for (int j = 1; j <= nbOfSessionsPerUser; j++) {
                        try {
                        	IDfSession session = null;
                        	
                            // get session by password using newSession() method
                        	session = sMgr.newSession(docBaseName);
                        	
                        	// get session by password using getSession() method
                        	// session = sMgr.getSession(docBaseName);
                        	
                        	// get session by password using getSession() method by default and newSession() if the getSession() is failing
//                            try {
//                            	session = sMgr.getSession(docBaseName);
//                            } catch (DfException sessionex) {
//                            	session = sMgr.newSession(docBaseName);
//                            }
                            listacon.add(session);
                            System.out.println("Got Session " +
                                session.getSessionId() + " for thread #" +
                                this.number + " user " + j);

                            if (j == nbOfSessionsPerUser) {
                            	String ticket = session.getLoginTicketEx(userName,
                                        "global", 43200, false, "");
                            	
	                            // get session by ticket
	                            testTicket(ticket);
                            }
                            // thread wait 5 sec until sessions time out
                            //Thread.sleep(6000);
                            
                        } catch (DfException dfe) {
                            System.out.println(
                                "Exception While getting new Session for " +
                                docBaseName + " for thread : " + this.number);
                            dfe.printStackTrace();
//                        } catch (InterruptedException ie) {
//							ie.printStackTrace();
						}
                    } // end for

                    // free session
                    for (int k = 0; listacon.size() > k; k++) {
                    	IDfSession ses = (IDfSession) listacon.get(k);
                        System.out.println("thread #" + number + " releasing " +
                            ses.getSessionId());
                        if (ses != null) {
                        	sMgr.release(ses);
                        }
                    } // end for{}
                }
            }

        } catch (DfException df) {
            df.printStackTrace();
            System.out.println("Some Exception ...");
        } // end catch{}
    }

    public static void main(String[] args) {
        System.out.println("Inside main()");

        for (int i = 0; i < nbOfThreads; i++) {
            Thread t;
            t = new Thread(new Test3(i + 1));
            t.start();
            System.out.println("Started Thread #" + i);
        }
    } // end of main
} // end of class {}
