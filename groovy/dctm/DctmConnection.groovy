package dctm

import com.documentum.com.DfClientX
import com.documentum.fc.client.IDfQuery

class DctmConnection {
    static def instance
    def clientX
    def sessionMgr
    def defaultDocbase
    
    private DctmConnection() {
        clientX = new DfClientX()
        sessionMgr = clientX.getLocalClient().newSessionManager()
    }
    
    static def getInstance() {
        if (instance == null) {
            instance = new DctmConnection()
        }
        return instance
    }
    
    def setLoginInfo(docbase, username, password, domain='') {
        def loginInfo = clientX.getLoginInfo()
        loginInfo.user = username
        loginInfo.password = password
        loginInfo.domain = domain
        sessionMgr.setIdentity(docbase, loginInfo)
        if (defaultDocbase == null) {
            defaultDocbase = docbase
        }
    }
    
    def newSession() {
        if (defaultDocbase == null) {
            throw new Exception("no docbase configured")
        }
        return new DctmSession(sessionMgr.newSession(defaultDocbase), clientX)
    }
    
    def close(session) {
        if (session != null) {
            sessionMgr.release(session.getSessionHandle())
        }
    }
    
    def execute(Closure closure) {
        def session
        try {
            session = newSession()
            closure(session)
        } finally {
            close(session)
        }
    }
    
    def query(String dql, Closure closure) {
        def session
        def col
        try {
            session = newSession()
            def query = clientX.query
            query.setDQL(dql)
            col = session.executeQuery(query, IDfQuery.DF_READ_QUERY)
            while (col.next()) {
                def obj = col.getTypedObject();
                closure(new DctmObject(obj), session)
            }
        } finally {
            try { col.close() } catch (e) {}
            close(session)
        }
    }
    
    def time(str, pattern) {
      return clientX.getTime(str, pattern)
    }
    
    def id(str) {
      return clientX.getId(str)
    }

    def query() {
        return clientX.query
    }
}
