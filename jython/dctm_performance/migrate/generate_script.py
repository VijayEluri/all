import os
import sys
import os.path
import random
import shutil
import re

g_numberOfThreads = 8
g_filesPath = ""
g_targetPath = ""

def main():
    global g_numberOfThreads
    global g_filesPath

    alldirs = []
    targetdirs = []

    dirs = os.listdir(g_filesPath)
    for dir in dirs:
        subdirs = os.listdir(os.path.join(g_filesPath, dir))
        for subdir in subdirs:
            thedir = os.path.join(os.path.join(g_filesPath, dir), subdir)
            alldirs.append(os.path.abspath(thedir))
            targetdir = os.path.join(os.path.join(g_targetPath, dir), subdir)
            targetdirs.append(os.path.abspath(targetdir))

    dirProcessed = 0
    overAllScript = open("go.sh", "w")
    killAllScript = open("killall.sh", "w")
    for i in range(g_numberOfThreads):
        scriptFilename = "job%d.sh" % (i + 1)
        overAllScript.write("nohup sh %s &\n" % scriptFilename)
        killAllScript.write("ps ax | grep %s | awk '{print $1;}' | xargs kill\n" % scriptFilename)
        count = len(alldirs)
        eachCount = 1 + count / g_numberOfThreads
        file = open(scriptFilename, "w")
        statFileName = "stat_%d" % (i + 1)
        file.write('echo "--start of process group %d" >> %s\n' % (i + 1, statFileName))
        for j in range(eachCount):
            file.write('echo "" >> %s\n' % statFileName)
            file.write('echo "------------------" >> %s\n' % statFileName)
            file.write('echo "processing %s" >> %s\n' % (alldirs[dirProcessed], statFileName))
            file.write('date >> %s\n' % statFileName)
            template = "rsync -r -p -o -g -t -W --stats %s %s >> %s\n"
            file.write(template % (alldirs[dirProcessed], targetdirs[dirProcessed], statFileName))
            file.write('date >> %s\n' % statFileName)
            file.write('echo "------------------" >> %s\n' % statFileName)
            file.write('echo "" >> %s\n' % statFileName)
            dirProcessed = dirProcessed + 1
            if dirProcessed >= count:
                break
	file.write('touch group_%d_done\n' % (i + 1))
        file.close()
    overAllScript.close()
    killAllScript.close()


if __name__ == "__main__":
    if not len(sys.argv) == 4:
        print "usage: %s [numberOfThreads] [dir that contains 80/81/82/... folders] [target dir]" % sys.argv[0]
        sys.exit(0)

    g_numberOfThreads = int(sys.argv[1])
    g_filesPath = sys.argv[2]
    g_targetPath = sys.argv[3]

    print "== Generating scripts =="
    print "Source path to store folders: %s" % g_filesPath
    print "Target path: %s" % g_targetPath
    print "Number of threads to process: %d" % g_numberOfThreads

    main()

