package dctm

import org.codehaus.groovy.runtime.InvokerHelper

class DctmObject extends GenericObject {

    DctmObject(_obj) {
        super(_obj)
    }
    
    String toString() {
        return "ObjectId: " + objectId
    }
}