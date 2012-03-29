#!/usr/bin/python
# -*- coding: utf-8 -*-
          
import urllib
import time
import json

url = 'http://wbsapi.withings.net/measure?action=getmeas&userid=782689&publickey=ca494c14d38ed05f&startdate=%d&enddate=%d'

now = int(time.time())
yesterday = now - 3600 * 24 * 10

withings_str = urllib.urlopen(url % (yesterday, now)).read()
withings_data = json.loads(withings_str)

if withings_data['status'] != 0:
	exit(1)

withings_grps = withings_data['body']['measuregrps']
for grp in withings_grps:
	if grp['category'] == 1:
		grp_id = grp['grpid']
		date_epoch = grp['date']
		for measure in grp['measures']:
			measure_type = measure['type']
			if measure_type == 9:
				low = measure['value']
			elif measure_type == 10:
				high = measure['value']
			elif measure_type == 11:
				pulse = measure['value']
		date_str = time.strftime('%Y-%m-%d %H:%S', time.localtime(date_epoch))
		print "%s: %d, %d, %d" % (date_str, low, high, pulse)

