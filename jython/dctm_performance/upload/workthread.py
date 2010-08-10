from java.lang import Runnable
import os.path
import re
import time


class workthread(Runnable):

    def __init__(self, index, folder, sessionManager, docbaseName):
        self.index = index
        self.folder = folder
        self.sessionManager = sessionManager
        self.docbaseName = docbaseName
        self.logfile = open('threadlog_%d.log' % self.index, 'a')

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

    def importFile(self, session, folder, filePath, fileExt, title):
        fileExt = fileExt.lower()

        document = session.newObject("dm_document")

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
        try:
            session = self.sessionManager.newSession(self.docbaseName)
            totalBytes = 0
            totalFiles = 0
            self.logTimeStamp()
            fobj = open('_index%d.txt' % self.index)
            for line in fobj:
                (task, taskName) = line.split("\t")
                print "thread %d is processing %s" % (self.index, task)
                totalBytes = totalBytes + os.path.getsize(task)
                totalFiles = totalFiles + 1
                (fileName, fileExt) = os.path.splitext(task)
                self.importFile(session, self.folder, task, fileExt, taskName)
            fobj.close()
            self.logTimeStamp()
            self.logStr('uploaded totally %d bytes' % totalBytes)
            self.logStr('uploaded totally %d files' % totalFiles)
        finally:
            self.sessionManager.release(session)
