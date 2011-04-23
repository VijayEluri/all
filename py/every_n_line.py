from myutil import *

if __name__ == '__main__':
    if len(sys.argv) != 3:
        print "usage: %s [logfile] [n]" % sys.argv[0]
        exit(1)

    file = sys.argv[1]
    n = int(sys.argv[2])
    
    i = 0
    for line in all_lines(file):
        i = i + 1
        if i % n == 1:
            print line,