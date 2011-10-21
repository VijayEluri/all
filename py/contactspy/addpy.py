# -*- coding: utf-8 -*-

import string
import atom.data
import gdata.data
import gdata.contacts.client
import gdata.contacts.data

import pinyin

gdclient = gdata.contacts.client.ContactsClient(source='ContactsAliasPy')
gdclient.ClientLogin('hutiejun@gmail.com', 'deqrpl', gdclient.source)
py = pinyin.Pinyin()

def initials(str):
	namesstr = str.split('-')[0]
	names = namesstr.split(',')
	r = ''
	for name in names:
		name = name.strip()
		if len(name) == 0:
			continue
		r = r + name[0].upper()
	return r
	
def has_chinese(str):
	for c in str:
		if c == u'-' or c == u' ' or c in string.letters or c in string.digits:
			continue
		return True
	return False

count = 1
while True:
	query = gdata.contacts.client.ContactsQuery()
	query.max_results = 20
	query.start_index = count
	feed = gdclient.GetContacts(q = query)
	if len(feed.entry) > 0:
		for entry in feed.entry:
			count += 1
			if not entry.name is None:
				fullname = unicode(entry.name.full_name.text)
				if (has_chinese(fullname)):
					name_py = py.pinyin(fullname)
					print "%s = %s" % (fullname, initials(name_py))
					nickname = gdata.contacts.data.NickName(text = initials(name_py))
					if entry.nickname is None or entry.nickname != nickname:
						entry.nickname = nickname
						entry.extended_property = None
						gdclient.Update(entry)
				else:
					print "%s = " % (fullname)
					if not entry.nickname is None:
						entry.nickname = None
						entry.extended_property = None
						gdclient.Update(entry)
	else:
		break
	
print count	
