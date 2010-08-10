from java.lang import Runnable
import os.path
import re
import sys
import time


class workthread(Runnable):

    def __init__(self, index, folder, sessionManager, docbaseName, tasks):
        self.index = index
        self.folder = folder
        self.sessionManager = sessionManager
        self.docbaseName = docbaseName
        self.logfile = open('threadlog_%d.log' % self.index, 'a')
        self.batch = False
        self.tasks = tasks

        self.formatMap = {
            ".txt" : "crtext",
            ".doc" : "msw8",
            ".ppt" : "ppt8",
            ".xls" : "excel8book",
            ".tif" : "tiff",
            ".tiff" : "tiff",
            ".mp3" : "mp3",
            ".jpg" : "jpeg",
            ".jpeg" : "jpeg",
            ".3gp" : "mpeg"
        }

    def importFile(self, session, folder, filePath, fileExt, title, type):
        fileExt = fileExt.lower()

        document = session.newObject(type)

        format = "unknown"
        try:    
            format = self.formatMap[fileExt]
        except: 
            pass

        document.setContentType(format)
        document.setFile(filePath)
        document.link(folder.getObjectId().toString())
        document.setObjectName(title)
        document.save()

    def logStr(self, str):
        self.logfile.write(str + "\n")

    def logTimeStamp(self):
        timestr = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
        self.logStr(timestr)

    def run(self):
        session = self.sessionManager.newSession(self.docbaseName)
        try:
            apppath = os.path.dirname(os.path.abspath(sys.argv[0]))
            totalBytes = 0
            totalFiles = 0
            self.logTimeStamp()
            count = 0
            thereIsATrans = False
            for (f, c, t) in self.tasks:
                for i in range(c):
                    filePath = os.path.join(apppath, "%s_src_thread_%d.txt" % (f, self.index))
                    totalBytes = totalBytes + os.path.getsize(filePath)
                    fileExt = ".txt"
                    taskName = "thread%d_count_%d" % (self.index, totalFiles)
                    if self.batch:
                        if thereIsATrans and (count == 99):
                            session.commitTrans()
                            thereIsATrans = False
                        if not thereIsATrans:
                            count = 0
                            thereIsATrans = True
                            session.beginTrans()
                    self.importFile(session, self.folder, filePath, fileExt, taskName, t)
                    totalFiles = totalFiles + 1
                    count = count + 1

            if self.batch:
                if thereIsATrans:
                    session.commitTrans()
            self.logTimeStamp()
            self.logStr('uploaded totally %d bytes' % totalBytes)
            self.logStr('uploaded totally %d files' % totalFiles)
        finally:
            self.sessionManager.release(session)
