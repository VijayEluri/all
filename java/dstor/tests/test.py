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

class dsclient:
    def __init__(self, url):
        self.url = url
        self.files_url = url + 'files/'
        self.file_url = url + 'file/'
        
    def create(self, fileName):
        r = curl("-F file=@%s %s" % (fileName, self.files_url))
        return r
    
    def download(self, saveas, id):
        r = curl("-o %s %s%s" % (saveas, self.file_url, id))
        return r
    
    def delete(self, id):
        r = curl("-X DELETE %s%s" % (self.file_url, id))
        return r
    
class TestFiles(unittest.TestCase):
    def setUp(self):
        self.url = BASE_URL + 'files/'
        self.client = dsclient(BASE_URL)

    def test_get(self):
        r = curl(self.url)
        self.assertEqual(r['http_code'], 405)
        self.assertEqual(r['err'], 1)
        
    def test_post(self):
        r = self.client.create("file1.bin")
        self.assertEqual(r['http_code'], 201)
        self.assertEqual(r['err'], 0)
        
        os.remove('output.bin')

        obj_id = r['id']
        r = self.client.download('output.bin', obj_id)

        self.assertEqual(r['http_code'], 200)
        
        r = run_cmd('diff --binary file1.bin output.bin')
        self.assertEqual(len(r), 0, 'error while comparing')
        
        r = self.client.delete(obj_id)
        self.assertEqual(r['http_code'], 200)
        self.assertEqual(r['err'], 0)        

        r = self.client.download("output.bin", obj_id)
        print r       
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