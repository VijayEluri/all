import os
import re
import sys
import os.path
import workthread

from com.documentum.com import *
from com.documentum.operations import *
from java.lang import Thread

# docbase name
DOCBASE_NAME = "DEMO"

# the cabinet to store all import files
CABINET_NAME = "MyCabinet"

# username/password to import files
USERNAME = "dmadmin"
PASSWORD = "dmadmin"

def countIndex():
    count = 0
    apppath = os.path.dirname(os.path.abspath(sys.argv[0]))
    indexMatch = re.compile("^_index[0-9]+\.txt$")
    for f in os.listdir(apppath):
        if indexMatch.match(f):
            count = count + 1
    return count

def createSessionManager():
    clientX = DfClientX()
    client = clientX.getLocalClient()
    sessionManager = client.newSessionManager()

    loginInfo = clientX.getLoginInfo()
    loginInfo.setUser(USERNAME)
    loginInfo.setPassword(PASSWORD)

    sessionManager.setIdentity(DOCBASE_NAME, loginInfo)
    return sessionManager

def createOrGetCabinet(session):
    myCabinet = session.getFolderByPath("/" + CABINET_NAME)
    if myCabinet:
        print "Cabinet is there, return"
        return myCabinet
    myCabinet = session.newObject("dm_cabinet")
    myCabinet.setObjectName(CABINET_NAME)
    myCabinet.save()
    print "Cabinet is created, return"
    return myCabinet

def main():
    sessionManager = createSessionManager()
    session = sessionManager.getSession(DOCBASE_NAME)
    myCabinet = createOrGetCabinet(session)
    sessionManager.release(session)

    count = countIndex()
    for i in range(count):
        work = workthread.workthread(i, myCabinet, sessionManager, DOCBASE_NAME)
        t = Thread(work, "workthread %d" % i)
        t.start()

if __name__ == "__main__":
    print "\n== Uploading files to docbase =="
    print "Target docbase: %s" % DOCBASE_NAME
    print "Target cabinet: /%s" % CABINET_NAME
    print "User name: %s" % USERNAME
    print "Password: %s" % PASSWORD
    print "----"
    main()
