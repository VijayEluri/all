#!/usr/bin/python

from myutil import *

def output_line(line):
    items = line.split()
    for item in items:
        print item.strip(),
    print
    
if __name__ == '__main__':
    if len(sys.argv) != 2:
        print "usage: %s [logfile]" % sys.argv[0]
        exit(1)

    file = sys.argv[1]

    pattern1 = re.compile(".*Ticket.*113SP1.*")
    pattern2 = re.compile(".*113SP1.*")
    patternSep = re.compile(".*EMC Documentum idql.*")

    count1 = 0
    count2 = 0
    printNextLine = True
    
    print "Ticket, All"
    
    for line in all_lines(file):
        # if printNextLine:
        #     print line.strip(), 
        #     printNextLine = False
        if pattern2.match(line):
            count2 = count2 + 1
            if pattern1.match(line):
                count1 = count1 + 1
        elif patternSep.match(line):
            print "%d, %d" % (count1, count2)
            count1 = 0
            count2 = 0
            printNextLine = True