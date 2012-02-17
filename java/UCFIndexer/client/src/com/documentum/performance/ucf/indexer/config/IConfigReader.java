package com.documentum.performance.ucf.indexer.config;

import org.w3c.dom.Element;

public interface IConfigReader
{
    /**
     * Gets the config file to be loaded.
     *
     * @return the full path and file name
     */
    public String getConfigFile();

    /**
     * Sets the config file to be loaded.
     *
     * @param configFile the full path and file name
     */
    public void setConfigFile(String configFile);

    /**
     * Opens and loads the XML config file and returns the document element.
     *
     * @return Element document element of the XML document
     * @throws ConfigException
     */
    public Element load() throws ConfigException;

    /**
     * Returns a String value from the config file based on a key passed in..
     *
     * @param key the element value to be looked up
     * @return String
     * @throws ConfigException
     */
    public String getValue(String key) throws ConfigException;

    /**
     * Returns a boolean value from the config file based on a key passed in.
     *
     * @param key the element value to be looked up
     * @return boolean
     * @throws ConfigException
     */
    public boolean getValueAsBoolean(String key) throws ConfigException;

    /**
     * Returns an integer value from the config file based on a key passed in.
     *
     * @param key the element value to be looked up
     * @return int
     * @throws ConfigException
     */
    public int getValueAsInteger(String key) throws ConfigException;

    /**
     * Returns an XML Element from the config file based on a element name passed in..
     *
     * @param elementName the element value to be looked up
     * @return XML DOM element
     * @throws ConfigException
        */
	   public Element getElement(String elementName) throws ConfigException;
}
