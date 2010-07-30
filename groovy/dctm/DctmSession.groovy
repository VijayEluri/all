package dctm

import org.codehaus.groovy.runtime.InvokerHelper

class DctmSession extends GenericObject {
    def clientx
    def session
    
    DctmSession(_session, _clientx) {
        super(_session)
        session = _session
        clientx = _clientx
    }
    
    def getObject(String objectId) {
        def objId = clientx.getId(objectId)
        return new DctmObject(this.getObject(objId))
    }

    def executeQuery(query, flag) {
        return query.execute(session, flag)
    }

    def getSessionHandle() {
    	return session
    }

    def destroyObject(String objectId) {
    	def obj = getObject(objectId)
    	def contentId = obj.getContentsId()
    	obj.destroy()
    	session.apiGet("apply", "${contentId.getId()},DESTROY_CONTENT")
    	session.apiExec("close", "q0")
    }
}
