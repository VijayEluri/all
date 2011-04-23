#!/usr/bin/env python

import sys
import shlex
import sqlite3
import subprocess
import xml.dom.minidom

SVNEXE = 'd:/soft/bin/svn.exe'
#CONN = sqlite3.connect(':memory:')
CONN = sqlite3.connect('svn.log.db')

devs = {}

def create_table():
    c = CONN.cursor()
    c.execute('create table log (r integer, a text, m text, d text)')
    CONN.commit()
    c.close()

def svn(cmd, addr):
    svncmd = "%s %s %s" % (SVNEXE, cmd, addr)
    output = subprocess.Popen(shlex.split(svncmd), stdout=subprocess.PIPE).communicate()[0]
    return output

def get_first_text(dom):
    nodes = dom.childNodes;
    for node in nodes:
        return node.data
    return ''

def get_svn_log_entries(addr, fromDate, toDate):
    str = svn('log --xml -r{%s}:{%s}' % (fromDate, toDate), addr)
    dom = xml.dom.minidom.parseString(str)
    log = dom.getElementsByTagName('log')[0]
    entries = log.getElementsByTagName('logentry')
    return entries

def insert_entries_into_db(entries):
    c = CONN.cursor()    
    for entry in entries:
        r = entry.getAttribute('revision')
        a = get_first_text(entry.getElementsByTagName('author')[0]);
        m = get_first_text(entry.getElementsByTagName('msg')[0]);
        d = get_first_text(entry.getElementsByTagName('date')[0]);
        c.execute('insert into log values (?, ?, ?, ?)', (r, a, m, d))
    CONN.commit()
    c.close()

def get_users():
    c = CONN.cursor()
    c.execute("select distinct(a) from log")
    users = []
    for row in c:
        users.append(row[0])
    c.close()
    return users

def import_data():
    create_table()
    addrs = ("http://192.168.20.1/svn/domas25", "http://192.168.20.1/svn/cms", "http://192.168.20.1/svn/scaps", "http://192.168.20.1/svn/domasv2se")
    for addr in addrs:
        print "Loading", addr
        entries = get_svn_log_entries(addr, '2010-11-08', '2011-01-01')
        insert_entries_into_db(entries)
        
def calc_total_commits(users):
    c = CONN.cursor()

    for user in users:
        c.execute("select count(*) from log where a = ?", (user, ))
        devs[user]['total commits'] = c.fetchone()[0]
    
    c.close()

def calc_commit_without_msg(users):
    c = CONN.cursor()

    for user in users:
        c.execute("select count(*) from log where a = ? and m = ''", (user, ))
        devs[user]['commit without msg'] = c.fetchone()[0]
        
    c.close()
    
def calc_commit_date(users):
    c = CONN.cursor()
    
    for user in users:
        c.execute("select count(distinct(substr(d,1,10))) from log where a = ?", (user, ))
        devs[user]['day count'] = c.fetchone()[0]
        
    c.close()

if __name__ == '__main__':
    if (len(sys.argv) > 1):
        if (sys.argv[1] == 'init'):
            import_data();    

    users = get_users()
    
    for user in users:
        devs[user] = {}

    calc_total_commits(users);
    calc_commit_without_msg(users);
    calc_commit_date(users);

    keys = devs.keys()
    keys.sort()

    d1 = devs[keys[0]]
    print "user",
    for c in d1:
        print ",", c,

    print
    
    for dev in keys:
        print dev,
        for v in devs[dev]:
            print ",", devs[dev][v],
        print
