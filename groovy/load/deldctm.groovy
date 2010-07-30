import com.documentum.fc.client.IDfQuery

conn = dctm.DctmConnection.getInstance()
conn.setLoginInfo('DEMO', 'dmadmin', 'dmadmin')
session = conn.newSession()

start_time = new Date().time

count = 0

dql = 'select r_object_id from txt_record where record_id < 10010'
conn.query(dql) {
	typobj, s ->
	session.destroyObject(typobj.getString('r_object_id'))
	count ++
}

end_time = new Date().time

println "delete ${count} objects, time: ${(end_time - start_time) / 1000.0}"

conn.close(session)
