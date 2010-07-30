#!/usr/bin/python

# parse PS logs
# output format:
# <date>, <documentum process count>

from myutil import *

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print "usage: %s [logfile]" % sys.argv[0]
        exit(1)

    file = sys.argv[1]
        
    patternBegin = re.compile(".*content server test end.*");
    patternEnd = re.compile(".*query operatoin begin.*");
    patternDctm = re.compile(".*/documentum -docbase_name ccmssys.*")

    count = 0
    started = False
    printNextLine = False
    for line in all_lines(file):
        if printNextLine:
            print line.strip(), ", ",
            printNextLine = False
            continue
        if patternBegin.match(line):
            started = True
            printNextLine = True
            continue
        if started and patternDctm.match(line):
            count = count + 1
            continue
        if started and patternEnd.match(line):
            started = False
            print count
            count = 0
            continue    
