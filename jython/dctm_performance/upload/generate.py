import os
import sys
import os.path
import random
import shutil
import re

g_numberOfFiles = 256 * 256 * 4
g_filesPath = ""
g_templateCount = 0
g_dataTicket = 0x80000000

def countTemplate():
    count = 0
    apppath = os.path.dirname(os.path.abspath(sys.argv[0]))
    indexMatch = re.compile("^_template[0-9]+\.txt$")
    for f in os.listdir(apppath):
        if indexMatch.match(f):
            count = count + 1
    return count

def main():
    global g_dataTicket
    global g_templateCount
    global g_filesPath
    global g_numberOfFiles

    g_templateCount = countTemplate()
    count = 0
    folderIndex = 0
    while True:
        if count >= g_numberOfFiles:
            break
        strTicket = "%08X" % g_dataTicket
        strTicket = os.path.join(g_filesPath, "%s/%s/%s/%s.txt" % (strTicket[:2], strTicket[2:4], strTicket[4:6], strTicket[-2:]))
        dir = os.path.dirname(strTicket)
        if not os.path.exists(dir):
            os.makedirs(dir)
            print "generating %s..." % dir
        spot = random.randrange(g_templateCount) + 1
        fileToCopy = "_template%d.txt" % spot
        shutil.copyfile(fileToCopy, strTicket)
        g_dataTicket = g_dataTicket + 1
        count = count + 1

    print "Done."


if __name__ == "__main__":
    if not len(sys.argv) == 2:
        print "usage: %s [dir to generate files]" % sys.argv[0]
        sys.exit(0)

    g_filesPath = sys.argv[1]

    print "== Generating mass number of files =="
    print "Path to store files: %s" % g_filesPath
    print "Number of files to generate: %d" % g_numberOfFiles

    main()
