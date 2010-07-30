import com.xhive.XhiveDriverFactory

def xhiveDriver = XhiveDriverFactory.getDriver('xhive://dctm:1235');
xhiveDriver.init(1024)
session = xhiveDriver.createSession()
session.connect('Administrator', 'admin', 'DEMO')

start_time = new Date().time

seed = System.currentTimeMillis() + Runtime.runtime.freeMemory()
random = new Random(seed)

session.begin()
def root = session.getDatabase().getRoot()
lib = root.get('abc')

1000.times {
  id = random.nextInt(50000)
  xquery = "//APSM[ID=${id}]"
  r = lib.executeXQuery(xquery)
  r.next()
}

end_time = new Date().time

println "query time: ${(end_time - start_time) / 1000.0}"

session.commit()

session.disconnect()
session.terminate()
