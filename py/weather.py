#!/usr/bin/python
# -*- coding: utf-8 -*-
          
import urllib2
import datetime
from xml.dom.minidom import parse, parseString

url = 'http://weather.yahooapis.com/forecastrss?p=CHXX0008&u=c'

def read_dom(url):
  dom = None
  try:
    dom = parse(urllib2.urlopen(url, timeout=10))
  except urllib2.URLError as e:
    try:
      with open('/tmp/_weather.txt') as f:
        for line in f.readlines():
          print line,
    except:
      pass
    exit(1)
  return dom

dom = read_dom(url)

def log(f, str):
  if f:
    f.write(str + "\n")
  print str

units = dom.getElementsByTagName('yweather:units')[0].attributes
tempUnit = units.getNamedItem('temperature').value
distUnit = units.getNamedItem('distance').value
pressureUnit = units.getNamedItem('pressure').value
speedUnit = units.getNamedItem('speed').value

wind = dom.getElementsByTagName('yweather:wind')[0].attributes
atmosphere = dom.getElementsByTagName('yweather:atmosphere')[0].attributes
astronomy = dom.getElementsByTagName('yweather:astronomy')[0].attributes

f = open('/tmp/_weather.txt', "w+")

log(f, u"Weather")
log(f, u"-------")

log(f, u"Sunrise: \t%s" % astronomy.getNamedItem('sunrise').value)
log(f, u"Sunset: \t%s" % astronomy.getNamedItem('sunset').value)
log(f, u"Humidity: \t%s%%" % atmosphere.getNamedItem('humidity').value)
log(f, u"Visibility: %s %s" % (atmosphere.getNamedItem('visibility').value, distUnit))

current = dom.getElementsByTagName('yweather:condition')[0].attributes
str = u"Now: \t\t%s°%s, %s" % (current.getNamedItem('temp').value, tempUnit, current.getNamedItem('text').value)
log(f, str.encode('utf-8'))

forcasts = dom.getElementsByTagName('yweather:forecast')
forcast1 = forcasts[0].attributes
forcast2 = forcasts[1].attributes
str = u"%s: \t\tL %s°%s, H %s°%s, %s" % (forcast1.getNamedItem('day').value, forcast1.getNamedItem('low').value, tempUnit, forcast1.getNamedItem('high').value, tempUnit, forcast1.getNamedItem('text').value)
log(f, str.encode('utf-8'))

str = u"%s: \t\tL %s°%s, H %s°%s, %s" % (forcast2.getNamedItem('day').value, forcast2.getNamedItem('low').value, tempUnit, forcast2.getNamedItem('high').value, tempUnit, forcast2.getNamedItem('text').value)
log(f, str.encode('utf-8'))

log(f, "Updated: \t" + datetime.datetime.now().strftime("%Y-%m-%d %H:%m:%S"))
f.close()