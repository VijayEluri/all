
conn = DctmConnection.getInstance()
conn.setLoginInfo('DEMO', 'dmadmin', 'dmadmin')

conn.execute {
    session ->
    println session.loginTicket
    println()
    def obj = session.getObject('3c01117180000103')
    println obj
    println obj.objectName
    println()
}

conn.query("select r_object_id from dm_user where user_name='dmadmin'") {
    row, session ->
    println row.getString('r_object_id')
}

