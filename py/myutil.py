#!/usr/bin/python

import os
import re
import sys
import time
import fnmatch

def all_files(root, patterns='*', single_level=False, yield_folders=False):
    """
    Usage:
    allfiles = list(all_files("/", "*.txt;*.html"))
    
    for file in all_files("/", "*.log"):
        print file
    """
    patterns = patterns.split(';')
    for path, subdirs, files in os.walk(root):
        if yield_folders:  
            files.extend(subdirs)
#        files.sort()
        for name in files:
            for pattern in patterns:
                if fnmatch.fnmatch(name, pattern):
                    yield os.path.join(path, name)
                    break
        if single_level:
            break

def all_lines(filepath):
    """
    for line in all_lines(file):
        print line
    """
    fileobj = open(filepath)
    for line in fileobj:
        yield line
        
def print_timing(func):
    def wrapper(*arg):
        t1 = time.time()
        res = func(*arg)
        t2 = time.time()
        print '----\n  %s took %0.3f ms\n----' % (func.func_name, (t2-t1)*1000.0)
        return res
    return wrapper