#!/usr/bin/python

import os
import json
import shlex
import unittest
import subprocess

BASE_URL = 'http://localhost:8517/'

def run_cmd(cmd):
    output = subprocess.Popen(shlex.split(cmd), stdout=subprocess.PIPE).communicate()[0]
    return output
    
def curl(cmd):
    cmd = 'curl -w |%{http_code} -s ' + cmd
    output = run_cmd(cmd).split('|')
    r = {}
    try:
        if len(output[0]) > 0:
            r = json.loads(output[0])
        r['http_code'] = int(output[1])
        if (r['http_code'] == 0):
            raise Exception("http code error, may be cannot connect")
        return r
    except:
        print "ERROR cmd: %s, output: %s" % (cmd, output)
        raise

class TestFiles(unittest.TestCase):
    def setUp(self):
        self.url = BASE_URL + 'files/'

    def test_get(self):
        r = curl(self.url)
        self.assertEqual(r['http_code'], 405)
        self.assertEqual(r['err'], 1)
        
    def test_post(self):
        r = curl('-F file=@file1.bin ' + self.url)
        self.assertEqual(r['http_code'], 201)
        self.assertEqual(r['err'], 0)
        
        os.remove('output.bin')

        obj_id = r['id']
        r = curl("-o output.bin %sfile/%s" % (BASE_URL, obj_id))
        self.assertEqual(r['http_code'], 200)
        
        r = run_cmd('diff --binary file1.bin output.bin')
        self.assertEqual(len(r), 0, 'error while comparing')
        
        r = curl("-X DELETE %sfile/%s" % (BASE_URL, obj_id))
        self.assertEqual(r['http_code'], 200)
        self.assertEqual(r['err'], 0)        

        r = curl("%sfile/%s" % (BASE_URL, obj_id))
        self.assertEqual(r['http_code'], 404)
        self.assertEqual(r['err'], 1)        
        
class TestFile(unittest.TestCase):
    def setUp(self):
        self.url = BASE_URL + 'file/'

    def test_get(self):
        r = curl(self.url)
        self.assertEqual(r['http_code'], 400)
        self.assertEqual(r['err'], 1)
        
        r = curl(self.url + "0")
        self.assertEqual(r['http_code'], 404)
        self.assertEqual(r['err'], 1)
        
    
        
if __name__ == '__main__':
    unittest.main()