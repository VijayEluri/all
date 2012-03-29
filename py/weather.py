#!/usr/bin/python
# -*- coding: utf-8 -*-
          
import urllib
import datetime
from xml.dom.minidom import parse, parseString

url = 'http://weather.yahooapis.com/forecastrss?p=CHXX0008&u=c'
dom = parse(urllib.urlopen(url))

units = dom.getElementsByTagName('yweather:units')[0].attributes
tempUnit = units.getNamedItem('temperature').value
distUnit = units.getNamedItem('distance').value
pressureUnit = units.getNamedItem('pressure').value
speedUnit = units.getNamedItem('speed').value

wind = dom.getElementsByTagName('yweather:wind')[0].attributes
atmosphere = dom.getElementsByTagName('yweather:atmosphere')[0].attributes
astronomy = dom.getElementsByTagName('yweather:astronomy')[0].attributes

print u"Weather"
print u"-------"

print u"Sunrise: \t%s" % astronomy.getNamedItem('sunrise').value
print u"Sunset: \t%s" % astronomy.getNamedItem('sunset').value
print u"Humidity: \t%s%%" % atmosphere.getNamedItem('humidity').value
print u"Visibility: %s %s" % (atmosphere.getNamedItem('visibility').value, distUnit)

current = dom.getElementsByTagName('yweather:condition')[0].attributes
str = u"Now: \t\t%s°%s, %s" % (current.getNamedItem('temp').value, tempUnit, current.getNamedItem('text').value)
print str.encode('utf-8')

forcasts = dom.getElementsByTagName('yweather:forecast')
forcast1 = forcasts[0].attributes
forcast2 = forcasts[1].attributes
str = u"%s: \t\tL %s°%s, H %s°%s, %s" % (forcast1.getNamedItem('day').value, forcast1.getNamedItem('low').value, tempUnit, forcast1.getNamedItem('high').value, tempUnit, forcast1.getNamedItem('text').value)
print str.encode('utf-8')

str = u"%s: \t\tL %s°%s, H %s°%s, %s" % (forcast2.getNamedItem('day').value, forcast2.getNamedItem('low').value, tempUnit, forcast2.getNamedItem('high').value, tempUnit, forcast2.getNamedItem('text').value)
print str.encode('utf-8')

print "Updated: \t", datetime.datetime.now()
