package com.documentum.performance.ucf.indexer.config;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XMLConfigReader implements IConfigReader
{
    /**
     * Gets the config file to be loaded.
     *
     * @return the full path and file name
     */
    public String getConfigFile()
    {
        return m_configFile;
    }

    /**
     * Sets the config file to be loaded.
     *
     * @param configFile the full path and file name
     */
    public void setConfigFile(String configFile)
    {
        m_configFile = configFile;
    }

    /**
     * Opens and loads the XML config file and returns the document element.
     *
     * @return Element document element of the XML document
     */
    public Element load() throws ConfigException
    {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder domBuilder = null;
        Document document = null;

        try {
            domBuilder = domFactory.newDocumentBuilder();
            document = domBuilder.parse(new File(m_configFile));
            m_docElement = document.getDocumentElement();
        }
        catch (ParserConfigurationException pce) {
            throw new ConfigException(pce.getMessage());
        }
        catch (SAXException se) {
            throw new ConfigException(se.getMessage());
        }
        catch (IOException ioe) {
            throw new ConfigException(ioe.getMessage());
        }

        return m_docElement;
    }

    /**
     * Returns a value from the config file based on a key passed in..
     *
     * @param key the element value to be looked up
     * @return String
     */
    public String getValue(String key) throws ConfigException
    {
        String value = null;

        try {
            NodeList nodes = m_docElement.getElementsByTagName(key);
            value = nodes.item(0).getFirstChild().getNodeValue();
        }
        catch (DOMException e) {
            throw new ConfigException(e.getMessage());
        }

        return value;
    }

    /**
     * Returns a value from the config file based on a key passed in..
     *
     * @param key the element value to be looked up
     * @return boolean
     */
    public boolean getValueAsBoolean(String key) throws ConfigException
    {
        String temp = null;
        boolean value = false;

        try {
            NodeList nodes = m_docElement.getElementsByTagName(key);
            temp = nodes.item(0).getFirstChild().getNodeValue();
            value = Boolean.valueOf(temp);
        }
        catch (DOMException e) {
            throw new ConfigException(e.getMessage());
        }

        return value;
    }

    /**
     * Returns a value from the config file based on a key passed in..
     *
     * @param key the element value to be looked up
     * @return int
     */
    public int getValueAsInteger(String key) throws ConfigException
    {
        String temp = null;
        int value = 0;

        try {
            NodeList nodes = m_docElement.getElementsByTagName(key);
            temp = nodes.item(0).getFirstChild().getNodeValue();
            value = Integer.valueOf(temp);
        }
        catch (DOMException e) {
            throw new ConfigException(e.getMessage());
        }

        return value;
    }

    /**
     * Returns an XML Element from the config file based on a element name passed in..
     *
     * @param elementName the element value to be looked up
     * @return XML DOM element
     */
    public Element getElement(String elementName)
    {
        Element element = null;

        try {
            NodeList nodes = m_docElement.getElementsByTagName(elementName);
            element = (Element) nodes.item(0);
        }
        catch (DOMException e) {
            throw e;
        }

        return element;
    }

    private String m_configFile = null;
    private Element m_docElement = null;
}
