import pymssql

sql = '''
select CONVERT(VARCHAR(10), reportday, 101) as rd, CONVERT(VARCHAR(10), t.createdtime, 101) as cd, u.username, remark, issueremark
from dbo.t_tsk_dayrpt as t, t_sys_user as u
where t.creatorid = u.id and t.createdtime >= '%s' and t.createdtime < '%s'
order by t.createdtime
''' % ('2010-11-08', '2011-01-01')

conn = pymssql.connect(host='192.168.20.1', user='rdm', password='rdm', database='rdmServer', as_dict=True)
cur = conn.cursor()
cur.execute(sql)

devs = {}

for row in cur:
    user = row['username']
    if not devs.has_key(user):
        devs[user] = []
    devs[user].append(row['cd'])

for user in devs:
    devs[user] = set(devs[user])

print "user, rdm log days"    
for user in devs:
    print user, ",", len(devs[user])
