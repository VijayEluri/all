#!/usr/bin/python

from myutil import *

def parse_folder(folder):
    pattern = re.compile(".*error.*", re.I);

    for file in all_files(folder, "*.log"):
        for line in all_lines(file):
            if pattern.match(line):
                print line,    

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print "usage: %s [path]" % sys.argv[0]
        exit(1)
        
    parse_folder(sys.argv[1])