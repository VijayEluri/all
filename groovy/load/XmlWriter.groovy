import com.xhive.XhiveDriverFactory

class Const {
  public static final Fields = ['APSMPROCOD', 'APSMACBK', 'APSMACJJ', 'APSMACCCY', 'APSMACTNO', 
                                'APSMGACBK', 'APSMITMNO', 'APSMDRAF', 'APSMRATCOD', 'APSMTAXCOD', 
                                'APSMINTTYP', 'APSMACBAL', 'APSMBACBAL', 'APSMFDAMT', 'APSMFRZAMT', 
                                'APSMINTACC', 'APSMMODACC', 'APSMFDACC', 'APSMDDBAMT', 'APSMLSTDAT', 
                                'APSMACCDAT', 'APSMACTSTS', 'APSMOPNDAT', 'APSMCLSDAT', 'APSMCFLG', 
                                'APSMREM']
}

class XmlWriter extends AbstractWriter {
  def session
  def parser
  def input
  def lib

  def in_trans

  def start_time
  def very_start_time
  def count_in_trans
  def per_trans
  def batch_count
  def object_count

  public XmlWriter(filename) {
    super(filename)
    def xhiveDriver = XhiveDriverFactory.getDriver('xhive://dctm:1235');
    xhiveDriver.init(1024)
    session = xhiveDriver.createSession()
    session.connect('Administrator', 'admin', 'DEMO')

    count_in_trans = 0
    per_trans = 2000
    batch_count = 0
    object_count = 0
  }
  
  def begin() {
    if (in_trans) return
    session.begin()
    def root = session.getDatabase().getRoot()
    lib = root.get('abc2')

    in_trans = true
    start_time = new Date().time
    if (very_start_time == null) very_start_time = start_time
  }

  def parser() {
    if (parser == null) {
      parser = lib.createLSParser()
    }
    return parser
  }
  
  def input() {
    if (input == null) {
      input = lib.createLSInput()
    }
    return input
  }

  def commit() {
    if (in_trans) {
      session.commit()
      parser = null
      input = null
      in_trans = false
      batch_count ++

      def now = new Date().time
      def seconds = (now - start_time) / 1000.0
      def total_seconds = (now - very_start_time) / 1000.0
//      println "Trans ${batch_count}: ${seconds} secs, ${per_trans / seconds} ops. Total ${object_count} objs and ${total_seconds} secs. Avg ${object_count / total_seconds} ops."
      start_time = now
    }
  }
  
  def write(line, v) {
    def index = 0
    def xml = new StringBuffer("<APSM>\n")
    xml.append("<ID>${object_count}</ID>\n")
    object_count ++
    Const.Fields.each {
      xml.append("<${it}>${v[index].trim()}</${it}>\n")
      index ++
    }
    xml.append("</APSM>")
    println xml
    
    begin()
    def input = input()
    input.setStringData(xml.toString())
    def doc = parser().parse(input)
    lib.appendChild(doc)
    count_in_trans ++
    if (count_in_trans == per_trans) {
      commit()
      count_in_trans = 0
    }
  }

  def clear() {
    begin()
    def count = 0
    def n = lib.getFirstChild()
    while(n != null) {
      lib.removeChild(n)
      n = lib.getFirstChild()
      count ++
    }
    commit()
    println "${count} nodes removed"
  }
  
  def close() {
    commit()
    session.disconnect()
    session.terminate()
  }
}
