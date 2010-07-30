package dctm

import org.codehaus.groovy.runtime.InvokerHelper

class GenericObject {
    def realObject
    def metaObject
    
    GenericObject(_object) {
        realObject = _object
        metaObject = InvokerHelper.getMetaClass(realObject)
    }
    
    public Object invokeMethod(String name, Object args) {
        return metaObject.invokeMethod(realObject, name, args)
    }
    
    public Object getProperty(String property) {
        return metaObject.getProperty(realObject, property)
    }
    
}