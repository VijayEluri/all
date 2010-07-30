class TxtWriter extends AbstractWriter {
  private conn
  private session

  def per_trans

  def in_trans
  def count_in_trans
  def start_time
  def very_start_time

  def batch_count
  def object_count
  
  public TxtWriter(filename) {
    super(filename)
    conn = dctm.DctmConnection.getInstance()
    conn.setLoginInfo('DEMO', 'dmadmin', 'dmadmin')
    session = conn.newSession()
    in_trans = false
    count_in_trans = 0
    per_trans = 100
    batch_count = 0
    object_count = 0
  }

  def begin() {
    if (in_trans) return 
    session.beginTrans()
    in_trans = true
    start_time = new Date().time
    if (very_start_time == null) very_start_time = start_time
  }

  def commit() {
    if (in_trans) {
      session.commitTrans()
      in_trans = false

      def now = new Date().time
      def seconds = (now - start_time) / 1000.0
      def total_seconds = (now - very_start_time) / 1000.0
      println "Trans ${batch_count}: ${seconds} secs, ${per_trans / seconds} ops. Total ${object_count} objs and ${total_seconds} secs. Avg ${object_count / total_seconds} ops."
      start_time = now
    }
  }
    
  def write(line, v) {
  	def actno = v[4]
  	def lstdat = v[20]
  	def opndat = v[22]

        begin()
  	
  	def obj = session.newObject('txt_record')
  	obj.objectName = actno
  	obj.contentType = 'crtext'
  	obj.link '/ABC'

        obj.setInt('record_id', object_count)
  	obj.setString('actno', actno)
  	obj.setTime('lstdat', conn.time(lstdat, 'yyyymmdd'))
  	obj.setTime('opndat', conn.time(opndat, 'yyyymmdd'))

  	def out = new java.io.ByteArrayOutputStream()
  	def bytes = line.getBytes()
  	out.write(bytes, 0, bytes.size())
  	obj.setContent(out)

  	obj.save()

	count_in_trans ++
        object_count ++
        if (count_in_trans == 100) {
	  commit()
          count_in_trans = 0
          batch_count ++
        }
  }
  
  def clear() {
    def count = 0
    conn.query("select r_object_id from dm_document where folder('/ABC')") {
      typobj, s ->
      session.destroyObject(typeobj.getString('r_object_id'))
      count ++
    }
    println "${count} objects destroied"
  }

  def close() {
    commit()
    conn.close(session)
  }
}
