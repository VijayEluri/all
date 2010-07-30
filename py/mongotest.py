from pymongo.connection import Connection
from pymongo import ASCENDING

from myutil import *

# conn = Connection('192.168.1.4')
conn = Connection('127.0.0.1')
#conn.drop_database('docdb')

@print_timing
def insertSimpleDocs():
    docs = conn.docdb.docs2
    for i in range(10000):
        doc = {"name": "MongoDB",
               "type": "database",
               "index": i,
               "info": "abc" * 1000
               }
        docs.insert(doc)
    print "total simple docs %d" % docs.count()

@print_timing
def insertImages():
    from pymongo.binary import Binary
    data = open('/tmp/1.jpg', 'rb').read()
    imgs = conn.docdb.images
    for i in range(1000):
        img = {
            "name": "test1.jpg",
            "key": i,
            "data": Binary(data)
        }
        imgs.insert(img)
    print "total images %d" % imgs.count()
    
@print_timing
def ensureIndex():
    docs = conn.docdb.docs
    docs.create_index("index", ASCENDING)

@print_timing
def queryDocs():
    docs = conn.docdb.docs
    query = {"index": 109}
    result = docs.find(query)
    print "Found %d items." % result.count()
    for item in result:
        print item['index']
    
if __name__ == '__main__':
    for i in range(10):
        insertSimpleDocs()
    # for i in range(100):
    #     insertImages()
    ensureIndex()
    queryDocs()

