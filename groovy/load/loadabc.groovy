
if (this.args.length != 1) {
  println "ERROR: pls specify file name."
  System.exit(1)
}

//writer = new TxtWriter(this.args[0])
writer = new XmlWriter(this.args[0])
try {
//  writer.clear()
  writer.run()
} catch (Exception e) {
  e.printStackTrace()
} finally {
  try { writer.close() } catch (Exception e1) {}
}
