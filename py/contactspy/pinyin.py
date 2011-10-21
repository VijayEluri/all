# -*- coding: utf-8 -*-

import os.path
import string

class Pinyin:
	data_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'pinyin.txt')
	
	def __init__(self):
		z = open(self.data_path)
		self.lines = z.readlines()
		z.close()
	
	def pinyin(self, str):
		r = ''
		for c in str:
			if c == u'-':
				r = r + c
				continue
			if c == u' ':
				r = r + c
				continue
			if c in string.letters:
				r = r + c
				continue
			if c in string.digits:
				r = r + c
				continue
			ret = self.find_it(c)
			if ret:
				r = r + ret + ','
		return r
	
	def find_it(self, x):
		ret = ''
		for line in self.lines:
			line = line.decode('utf-8')
			if line.find(x)!=-1:
				return line.split(":")[0].strip()
