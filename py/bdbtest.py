from bsddb import db

DBNAME='testdb.bdb'

adb = db.DB()

adb.open(DBNAME, dbtype=db.DB_BTREE, flags=db.DB_CREATE)

print adb.get('100000')

cursor = adb.cursor()

print cursor.get('-100', db.DB_SET_RANGE)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)
print cursor.get(db.DB_NEXT)

# for i in range(1000000):
#     adb.put('%d' % i, 'value:%d' % i)
    
adb.close()