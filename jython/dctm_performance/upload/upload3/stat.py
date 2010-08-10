import os
import re
import sys
import os.path
import datetime
import strptime

def number_format(num, places=0):
   """Format a number with grouped thousands and given decimal places"""

   places = max(0,places)
   tmp = "%.*f" % (places, num)
   point = tmp.find(".")
   integer = (point == -1) and tmp or tmp[:point]
   decimal = (point != -1) and tmp[point:] or ""

   count =  0
   formatted = []
   for i in range(len(integer), 0, -1):
       count += 1
       formatted.append(integer[i - 1])
       if count % 3 == 0 and i - 1:
           formatted.append(",")

   integer = "".join(formatted[::-1])
   return integer+decimal


def loopLogs():
    totalBytes = 0
    totalSeconds = 0
    totalFiles = 0
    apppath = os.path.dirname(os.path.abspath(sys.argv[0]))
    indexMatch = re.compile("^threadlog_[0-9]+\.log$")
    fileMatch = re.compile("uploaded totally ([0-9]+) files")
    byteMatch = re.compile("uploaded totally ([0-9]+) bytes")
    timeFormat = "%Y-%m-%d %H:%M:%S"
    theStartTime = datetime.datetime.max
    theEndTime = datetime.datetime.min
    for f in os.listdir(apppath):
        if indexMatch.match(f):
            fobj = open(os.path.join(apppath, f))
            lines = fobj.readlines()
            fobj.close()
            m = fileMatch.match(lines[-1])
            if m:
                totalFiles = totalFiles + int(m.group(1))
            m = byteMatch.match(lines[-2])
            if m:
                totalBytes = totalBytes + int(m.group(1))

            startTimeTup = strptime.strptime(lines[-4].strip(), timeFormat)
            endTimeTup = strptime.strptime(lines[-3].strip(), timeFormat)
            startTime = datetime.datetime(startTimeTup[0], startTimeTup[1], startTimeTup[2], startTimeTup[3], startTimeTup[4], startTimeTup[5])
            if startTime < theStartTime:
                theStartTime = startTime
            endTime = datetime.datetime(endTimeTup[0], endTimeTup[1], endTimeTup[2], endTimeTup[3], endTimeTup[4], endTimeTup[5])
            if endTime > theEndTime:
                theEndTime = endTime
    deltaTime = theEndTime - theStartTime
    totalSeconds = totalSeconds + deltaTime.days * 24 * 3600 + deltaTime.seconds
    print "Upload started from: \t%s" % theStartTime.strftime(timeFormat)
    print "Upload stopped at: \t%s" % theEndTime.strftime(timeFormat)
    print "Upload last for:\t", deltaTime
    print "Total files: \t%s" % number_format(totalFiles)
    print "Total bytes: \t%s" % number_format(totalBytes)
    print "Total seconds: \t%s" % number_format(totalSeconds)
    print "Speed: %d bytes/sec" % (totalBytes / totalSeconds)
    print "Speed: %d files/sec" % (totalFiles / totalSeconds)

def main():
    loopLogs()

if __name__ == "__main__":
    print "\n== calc last stat using thread log files =="
    print "----"
    main()
