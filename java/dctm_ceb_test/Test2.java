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


class Test2 implements Runnable {
    static final int nbOfThreads = 5;
    static final int maxNbOfConcurUsersPerThread = 10;
    static final int nbOfSessionsPerUser = 2;
    static final String userName = "dmadmin";
    static final String userPassword = "dmadmin";
    static final String userDomain = "";
    static final String docBaseName = "DEMO2";
    int number;
    IDfClientX clientX;

    public Test2(int number) {
        this.number = number;
    }

    private void testTicket(IDfSessionManager sMgr, String ticket)
        throws DfException {
        
        DfLoginInfo dflogininfo = new DfLoginInfo();
        dflogininfo.setUser(userName);
        dflogininfo.setPassword(ticket);

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
                
                for (int i = 0; i < nbOfUsers; i++) {
                
                	listacon.clear();
                	
                	// moved from outside of the loop to here by jason
                    
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
                            // get session by password
                            IDfSession session = sMgr.newSession(docBaseName); // changed from getSession to newSession by jason
                            listacon.add(session);
                            System.out.println("Got Session " +
                                session.getSessionId() + " for thread #" +
                                this.number + " user " + j);

                            if (j == nbOfSessionsPerUser) {
                            	String ticket = session.getLoginTicketEx(userName,
                                        "global", 43200, false, "");
                            	
	                            // get session by ticket
	                            testTicket(sMgr, ticket);
                            }
                            
                        } catch (DfException dfe) {
                            System.out.println(
                                "Exception While getting new Session for " +
                                docBaseName + " for thread : " + this.number);
                            dfe.printStackTrace();
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
            t = new Thread(new Test2(i + 1));
            t.start();
            System.out.println("Started Thread #" + i);
        }
    } // end of main
} // end of class {}
