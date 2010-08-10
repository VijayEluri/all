import os
import re
import sys
import shutil
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

THREAD_COUNT = 7

def startUpload(sessionManager, folder):
    count = 0
    apppath = os.path.dirname(os.path.abspath(sys.argv[0]))
    fobj = open(os.path.join(apppath, "DataFileInfo.csv"))
    lines = fobj.readlines()
    fobj.close()
    Tasks = []
    ThreadTasks = []
    totalCount = 0
    for line in lines[1:]:
        words = line.strip().split(",")
        fileName = words[4]
        countStr = words[-3]
        type = words[-1]
        count = int(countStr)
        totalCount = totalCount + count
        Tasks.append((fileName, count, type))

    eachThreadCount = totalCount / THREAD_COUNT
    for i in range(THREAD_COUNT - 1):
        ThreadTasks.append([])
        tasksLeftToAssign = eachThreadCount
        for j in range(len(Tasks)):
            (f, c, t) = Tasks[j]
            if c >= tasksLeftToAssign:
                ThreadTasks[i].append((f, tasksLeftToAssign, t))
                c = c - tasksLeftToAssign 
                tasksLeftToAssign = 0
            if c < tasksLeftToAssign and c > 0:
                ThreadTasks[i].append((f, c, t))
                tasksLeftToAssign = tasksLeftToAssign - c
                c = 0
            Tasks[j] = (f, c, t)
            if tasksLeftToAssign == 0:
                break

    ThreadTasks.append([])
    for (f, c, t) in Tasks:
        if c > 0:
            ThreadTasks[THREAD_COUNT - 1].append((f, c, t))

    i = 0
    for tt in ThreadTasks:
        for (f, c, t) in tt:
            src = os.path.join(apppath, f)
            tgt = os.path.join(apppath, "%s_src_thread_%d.txt" % (f, i))
            shutil.copyfile(src, tgt)
        i = i + 1

    for i in range(THREAD_COUNT):
        work = workthread.workthread(i, folder, sessionManager, DOCBASE_NAME, ThreadTasks[i])
        t = Thread(work, "workthread %d" % i)
        t.start()

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

    startUpload(sessionManager, myCabinet)

if __name__ == "__main__":
    print "\n== Uploading files to docbase =="
    print "Target docbase: %s" % DOCBASE_NAME
    print "Target cabinet: /%s" % CABINET_NAME
    print "User name: %s" % USERNAME
    print "Password: %s" % PASSWORD
    print "----"
    main()
