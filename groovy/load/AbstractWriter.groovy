class BreakException extends Exception {}

abstract class AbstractWriter {

  private filename
  
  public AbstractWriter(filename) {
    this.filename = filename
  }
  
  def internalRun() {
    def datafile = new File(filename)
    int count = 0
    try {
      datafile.eachLine {
        def line = it.replaceAll(/[^\p{Print}]/, '') // replace all invisible char
        def values = line.split('\\|')
        write(line, values)
        count ++
//        if (count > 10000) throw new BreakException() // leave the closure
      }
    } catch (BreakException be) {}
    
    println "${count} objects created."
  }
  
  def run() {
    def start = new Date().time
    internalRun()
    def end = new Date().time
    println "${(end - start) / 1000.0} seconds."
  }
  
  def clear() {}
  def close() {}
  
  abstract write(line, v)
}
