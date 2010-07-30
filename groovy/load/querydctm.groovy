import com.documentum.fc.client.IDfQuery

conn = dctm.DctmConnection.getInstance()
conn.setLoginInfo('DEMO', 'dmadmin', 'dmadmin')
session = conn.newSession()
seed = System.currentTimeMillis() + Runtime.runtime.freeMemory()
random = new Random(seed)
query = conn.query()

start_time = new Date().time

1000.times {
  id = random.nextInt(50000)
  dql = "select r_object_id from txt_record where record_id=${id}"
  query.setDQL(dql) 
  col = session.executeQuery(query, IDfQuery.DF_READ_QUERY)
  col.next()
  object_id = col.getTypedObject().getString('r_object_id')
  col.close()
  obj = session.getObject(object_id)
  obj.getContent()
}

end_time = new Date().time

println "query time: ${(end_time - start_time) / 1000.0}"

conn.close(session)
