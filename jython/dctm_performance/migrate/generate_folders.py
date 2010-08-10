import os
import sys
import os.path
import random
import shutil
import re

g_numberOfFolders = 700
g_filesPath = ""
g_dataTicket = 0x8000

def main():
    global g_dataTicket
    global g_filesPath
    global g_numberOfFolders

    count = 0
    folderIndex = 0
    while True:
        if count >= g_numberOfFolders:
            break
        strTicket = "%04X" % g_dataTicket
        strTicket = os.path.join(g_filesPath, "%s/%s/dummy" % (strTicket[:2], strTicket[-2:]))
        dir = os.path.dirname(strTicket)
        if not os.path.exists(dir):
            os.makedirs(dir)
            print "generating %s..." % dir
        g_dataTicket = g_dataTicket + 1
        count = count + 1

    print "Done."


if __name__ == "__main__":
    if not len(sys.argv) == 2:
        print "usage: %s [dir to generate files]" % sys.argv[0]
        sys.exit(0)

    g_filesPath = sys.argv[1]

    print "== Generating mass number of folders =="
    print "Path to store folders: %s" % g_filesPath
    print "Number of folders to generate: %d" % g_numberOfFolders

    main()
