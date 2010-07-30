import com.documentum.com.DfClientX
import static Calendar.getInstance as now

DOCBASE = "bccssys"
USER = "dmadmin1"
PASS = "dmadmin1"
COUNT = 50

clientX = new DfClientX()
smgr = clientX.getLocalClient().newSessionManager()
loginInfo = clientX.getLoginInfo()
loginInfo.user = USER
loginInfo.password = PASS

smgr.setIdentity(DOCBASE, loginInfo)


keepRunning = true
reader = new InputStreamReader(System.in)
sessions = []

System.out.println "Ready to take orders..."

while(keepRunning) {
    System.out.print "> "
    s = reader.readLine()
    s = s.toLowerCase().trim()
    switch (s) {
        case 'q':
            keepRunning = false
            break
        case 'n':
            newSessions()
            break
        case 'r':
            releaseSessions()
            break
        case 't':
            testSessions()
            break
        case 'l':
            loopNewReleaseSessions()
            break
        default:
            System.out.println "sorry?"
    }
}

releaseSessions(true)

def loopNewReleaseSessions() {
    loopCount = 100
    for (i in 1..loopCount) {
        System.out.println "loop ${i}/${loopCount}."
        newSessions()
        sleep 1000
        releaseSessions()
        sleep 6000
    }
}

def testSessions() {
    System.out.println "testing all sessions."
    for (s in sessions) {
        try {
            s.apiGet("retrieve", "dm_docbase_config")
        } catch (Exception e) { 
            e.printStackTrace()
        }
    }
}

def newSessions() {
    for (i in 1..COUNT) {
        try {
            s = smgr.newSession(DOCBASE)
            sessions << s
            s.apiGet("retrieve", "dm_docbase_config")
        } catch (Exception e) {
            e.printStackTrace()
        }
    }  
    System.out.println "${now().time}: totally ${sessions.size()} sessions created."
}

def releaseSessions(all=false) {
    count = COUNT
    if (all) 
        count = sessions.size()
    else
        if (count > sessions.size())
            count = sessions.size()
    System.out.println "releasing ${count} sessions."
    if (count > 0) {
        for (i in 0..count - 1) {
            s = sessions[0]
            smgr.release(s)
            sessions.remove(0)
        }
    }
    System.out.println "${now().time}: ${sessions.size()} sessions left."
}