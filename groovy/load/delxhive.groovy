import com.xhive.XhiveDriverFactory

def xhiveDriver = XhiveDriverFactory.getDriver('xhive://dctm:1235');
xhiveDriver.init(1024)
session = xhiveDriver.createSession()
session.connect('Administrator', 'admin', 'DEMO')


seed = System.currentTimeMillis() + Runtime.runtime.freeMemory()
random = new Random(seed)

session.begin()
def root = session.getDatabase().getRoot()
lib = root.get('abc')

start_time = new Date().time
def count = 0
def n = lib.getFirstChild()
while(n != null) {
  lib.removeChild(n)
  n = lib.getFirstChild()
  count ++
  if (count == 80000) break
}

end_time = new Date().time

session.commit()

println "delete ${count} nodes, time: ${(end_time - start_time) / 1000.0}"

session.disconnect()
session.terminate()
