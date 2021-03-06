#!/usr/bin/python
# -*- coding: utf-8 -*-

import urllib2
import datetime
from HTMLParser import HTMLParser
from htmlentitydefs import name2codepoint

g_result = {}
g_next_tag = ""
g_next_value_name = ""
g_pick_next_data = False
g_current_aqi_aquired = False

def get_attr_by_name(attrs, attr_name):
    for attr in attrs:
        if attr[0] == attr_name:
            return attr[1]
    return ""
    
class MyHTMLParser(HTMLParser):
    
    def handle_starttag(self, tag, attrs):
        global g_next_tag
        global g_pick_next_data
        global g_next_value_name
        global g_current_aqi_aquired
        
        if tag == g_next_tag:
            g_pick_next_data = True
            g_next_tag = ""
        if tag == "div":
            if get_attr_by_name(attrs, "id") == "number" and get_attr_by_name(attrs, "class") == "bar-shadow":
                g_next_tag = "h1"
                g_next_value_name = "current"
                g_current_aqi_aquired = True
        if g_current_aqi_aquired and tag == "ul":
            g_next_tag = "li"
            g_next_value_name = "time"
            g_current_aqi_aquired = False
                
    def handle_endtag(self, tag):
        pass
        
    def handle_data(self, data):
        global g_pick_next_data
        global g_result
        global g_next_value_name
        
        if g_pick_next_data:
            g_result[g_next_value_name] = data.strip()
            g_pick_next_data = False

    def handle_comment(self, data):
        pass
    def handle_entityref(self, name):
        pass
    def handle_charref(self, name):
        pass
    def handle_decl(self, data):
        pass
        

url = "http://iphone.bjair.info/m/beijing/mobile"

def log(f, str):
    f.write(str + "\n")
    print str

try:
    data = urllib2.urlopen(url).read()
    parser = MyHTMLParser()
    parser.feed(data)
    f = open('/tmp/_pm2.5.txt', 'w+')

    str = u"PM2.5: \t\t%s" % g_result["current"]
    log(f, str.encode('utf-8'))

    str = u"Time: \t\t%s" % g_result["time"].split(' ')[1]
    log(f, str.encode('utf-8'))

    log(f, "Upated: \t%s" % datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S"))
    
    f.close()
except urllib2.URLError as e:
    try:
        with open('/tmp/_pm2.5.txt') as f:
            for line in f.readlines():
                print line,
        print "Cannot update now, use old data. " + e.__str__()
    except e:
        print e