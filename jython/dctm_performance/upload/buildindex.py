import sys
import re
import os
import os.path

FILES_PATH = "./files"
THREAD_NUMBER = 10

g_indexFiles = []
g_currentIndex = 0
g_numberOfThread = 0

g_namingDict = {
    "/" : "-",
    "\\" : "-",
    ":" : "-",
    " " : "_"
}

g_namingRx = re.compile('|'.join(map(re.escape, g_namingDict)))

def generateTitleFromFileName(text):
    def one_xlat(match):
        global g_namingDict
        return g_namingDict[match.group(0)]
    return g_namingRx.sub(one_xlat, text)

def writeIndex(f):
    global g_IndexFiles
    global g_currentIndex
    global g_numberOfThread
    if g_currentIndex == g_numberOfThread:
        g_currentIndex = 0
    fobj = None
    try:
        fobj = g_indexFiles[g_currentIndex]
    except:
        fobj = None
    if not fobj:
        fobj = open('_index%d.txt' % g_currentIndex, 'w')
        g_indexFiles.append(fobj)
    fobj.write('%s\t%s\n' % (f, generateTitleFromFileName(f)))
    g_currentIndex = g_currentIndex + 1

def walk(dirs):
    for dir in dirs:
        subdirs = []
        for f in os.listdir(dir):
            fp = os.path.abspath(os.path.join(dir, f))
            if os.path.isdir(fp):
                subdirs.append(fp)
            else:
                writeIndex(fp)
        walk(subdirs)

def removeAllIndex():
    apppath = os.path.dirname(os.path.abspath(sys.argv[0]))
    indexMatch = re.compile("^_index[0-9]+\.txt$")
    for f in os.listdir(apppath):
        if indexMatch.match(f):
            os.remove(f)

def main(dir):
    removeAllIndex()
    dirs=[dir]
    walk(dirs)

if __name__ == '__main__':
    g_numberOfThread = THREAD_NUMBER
    filesPath = FILES_PATH

    if len(sys.argv) > 3:
        print "usage: %s [dir to build index] [number of threads]" % sys.argv[0]
        sys.exit(0)

    if len(sys.argv) > 1:
        if not os.path.exists(sys.argv[1]):
            print "path not exists"
            sys.exit(1)
        if not os.path.isdir(sys.argv[1]):
            print "parameter 1 does not point to a dir"
            sys.exit(1)
        filesPath = sys.argv[1]

    if len(sys.argv) > 2:
        try:
            int(sys.argv[2])
        except:
            print "parameter 2 is not an integer"
            sys.exit(1)

        if int(sys.argv[2]) < 0:
            print "parameter 2 should be a positive integer"
            sys.exit(1)

        g_numberOfThread = int(sys.argv[2])

    print "\n== Building index files for multi-thread uploading =="
    print "Folder to index: %s" % filesPath
    print "Threads going to be used to upload %d" % g_numberOfThread
    print "Please wait..."

    main(filesPath)

    print "Done."
