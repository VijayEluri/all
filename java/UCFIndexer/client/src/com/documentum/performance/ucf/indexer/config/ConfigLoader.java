package com.documentum.performance.ucf.indexer.config;

import com.documentum.fc.common.DfException;
import com.documentum.performance.ucf.indexer.data.UCFTestData;
import com.documentum.performance.ucf.indexer.data.UCFTestPathData;
import com.documentum.performance.ucf.indexer.util.DocbaseHelper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ConfigLoader
{
    private static Logger theLogger = Logger.getLogger(ConfigLoader.class.getName());
    private static IConfigReader m_configReader = new XMLConfigReader();

    private static final List<String> OPERATIONS = Arrays.asList("checkout",
            "checkin",
            "export",
            "import",
            "cancelcheckout",
            "dctcheckout",
            "dctcheckin",
            "dctexport",
            "dctimport");
    private static final int[] ACTIONS = {IConfigElements.CONFIG_CHECKOUT,
            IConfigElements.CONFIG_CHECKIN,
            IConfigElements.CONFIG_EXPORT,
            IConfigElements.CONFIG_IMPORT,
            IConfigElements.CONFIG_CANCELCHECKOUT,
            IConfigElements.CONFIG_DCTCHECKOUT,
            IConfigElements.CONFIG_DCTCHECKIN,
            IConfigElements.CONFIG_DCTEXPORT,
            IConfigElements.CONFIG_DCTIMPORT};

    public static void main(String args[])
    {
        UCFTestData data = null;
        try {
            data = ConfigLoader
                    .loadDataFromConfigFile("C:\\Projects\\UCF Indexer Client\\UCF_PERF_TEST.xml");
            System.out.println("config=" + data);
        } catch (ConfigException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    /**
     * Loads the config data for UCF Indexer test from the specified
     * config file.
     *
     * @param fileName the full path and file name of the config file.
     * @return UCFTestData test data object
     * @throws ConfigException
     */
    public static UCFTestData loadDataFromConfigFile(String fileName) throws ConfigException
    {
        String value = null;
        UCFTestData testData = new UCFTestData();

        try {
            System.out.println("Loading config from " + fileName);
            m_configReader.setConfigFile(fileName);
            m_configReader.load();

            value = m_configReader.getValue(IConfigElements.CONFIG_DOCBASE);
            testData.setDocbase(value);

            value = m_configReader.getValue(IConfigElements.CONFIG_PROTOCOL);
            testData.setProtocol(value);

            value = m_configReader.getValue(IConfigElements.CONFIG_ROOT_HOST);
            testData.setRootHost(value);

            value = m_configReader.getValue(IConfigElements.CONFIG_ROOT_PORT);
            testData.setRootPort(value);

            value = m_configReader.getValue(IConfigElements.CONFIG_APPLICATION);
            testData.setAppl(value);

            value = m_configReader.getValue(IConfigElements.CONFIG_ADMIN_USER);
            testData.setAdminUser(value);

            value = m_configReader.getValue(IConfigElements.CONFIG_ADMIN_PASSWORD);
            testData.setAdminPassword(value);

            int intValue = m_configReader.getValueAsInteger(IConfigElements.CONFIG_RAMPUP);
            testData.setRampup(intValue);
            intValue = m_configReader.getValueAsInteger(IConfigElements.CONFIG_THINKTIME);
            testData.setThinktime(intValue);

            value = m_configReader.getValue(IConfigElements.CONFIG_LOGFILE);
            testData.setLogFile(value);
            value = m_configReader.getValue(IConfigElements.CONFIG_LOG_LEVEL);
            testData.setLogLevel(value);

            boolean logThrowOut = m_configReader.getValueAsBoolean(IConfigElements.CONFIG_LOG_THROW_OUT);
            testData.setLogThrowOutHighLow(logThrowOut);

            Element element = m_configReader.getElement(IConfigElements.CONFIG_USERS);
            List<UCFTestPathData> testPaths = getTestPathsFromConfig(element, testData.getDocbase());
            testData.setTestPaths(testPaths);

            theLogger.finer("loading config done");
            theLogger.finer(testData.toString());
        }
        catch (ConfigException e) {
            throw e;
        }

        return testData;
    }

    /**
     * Returns a List of UCFTestPathData objects created from the element passed in.
     *
     * @param element an XML element which holds the config settings for the test paths
     * @return a List of UCFTestPathData objects
     * @throws ConfigException
     */
    private static List<UCFTestPathData> getTestPathsFromConfig(Element element, String docbase) throws ConfigException
    {
        NodeList children = null;
        NodeList nodes = null;
        Element child = null;
        UCFTestPathData testPath = null;
        String value = null;
        int intValue = 0;
        List<UCFTestPathData> testPaths = new ArrayList<UCFTestPathData>();
        boolean boolValue = false;
        String folderId = null;

        try {
            children = element.getElementsByTagName(IConfigElements.CONFIG_USER);

            for (int idx = 0; idx < children.getLength(); idx++) {
                child = (Element) children.item(idx);

                if (child.getNodeType() == Element.ELEMENT_NODE) {
                    testPath = new UCFTestPathData();
                    // build up action strings
                    testPath.addActions(IConfigElements.CONFIG_DOCBASE, docbase);

                    nodes = child.getElementsByTagName(IConfigElements.CONFIG_PATH);
                    value = nodes.item(0).getFirstChild().getNodeValue();
                    testPath.setTestPath(value);

                    nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_NAME);
                    value = nodes.item(0).getFirstChild().getNodeValue();
                    testPath.setName(value);
                    // build up action strings
                    testPath.addActions(IConfigElements.CONFIG_USER_NAME, value);

                    nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_PASSWORD);
                    value = nodes.item(0).getFirstChild().getNodeValue();
                    testPath.setPasswd(value);
                    // build up action strings
                    testPath.addActions(IConfigElements.CONFIG_USER_PASSWORD, value);

                    nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_ACTION);
                    value = nodes.item(0).getFirstChild().getNodeValue().toLowerCase();
                    if (OPERATIONS.contains(value)) {
                        intValue = ACTIONS[OPERATIONS.indexOf(value)];
                    }
                    //intValue = Integer.valueOf(nodes.item(0).getFirstChild().getNodeValue());
                    testPath.setAction(intValue);
                    // build up action strings
                    testPath.addActions(IConfigElements.CONFIG_USER_ACTION, String.valueOf(intValue));

                    // based on action, read corresponding data
                    switch (intValue) {
                        case IConfigElements.CONFIG_CHECKOUT:
                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_CHECKOUTDIR);
                            value = nodes.item(0).getFirstChild().getNodeValue();
                            testPath.setCheckoutDir(value);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_CHECKOUTDIR, value);
                            break;
                        case IConfigElements.CONFIG_CHECKIN:
                            // nothing need to be done here, may need to
                            // manually set file path
                            break;
                        case IConfigElements.CONFIG_EXPORT:
                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_EXPORTDIR);
                            value = nodes.item(0).getFirstChild().getNodeValue();
                            testPath.setExportDir(value);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_EXPORTDIR, value);
                            break;
                        case IConfigElements.CONFIG_IMPORT:
                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_IMPORTPATH);
                            value = nodes.item(0).getFirstChild().getNodeValue();
                            testPath.setImportPath(value);

                            //build up action strings
                            folderId = DocbaseHelper.getFolderId(docbase, testPath.getName(), testPath.getPasswd(), testPath.getTestPath());
                            testPath.addActions(IConfigElements.CONFIG_PATH, folderId);
                            break;
                        case IConfigElements.CONFIG_CANCELCHECKOUT:
                            // nothing need to be done here
                            break;
                        case IConfigElements.CONFIG_DCTCHECKOUT:
                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_CHECKOUTDIR);
                            value = nodes.item(0).getFirstChild().getNodeValue();
                            testPath.setCheckoutDir(value);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_CHECKOUTDIR, value);

                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_NETLOCID);
                            value = nodes.item(0).getFirstChild().getNodeValue();
                            testPath.setNetLocId(value);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_NETLOCID, value);

                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_PREFERACS);
                            boolValue = Boolean.parseBoolean(nodes.item(0).getFirstChild().getNodeValue());
                            testPath.setPreferACS(boolValue);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_PREFERACS, String.valueOf(boolValue));

                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_ALLOWBOCS);
                            boolValue = Boolean.parseBoolean(nodes.item(0).getFirstChild().getNodeValue());
                            testPath.setAllowBOCS(boolValue);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_ALLOWBOCS, String.valueOf(boolValue));

                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_SYNCBOCS);
                            boolValue = Boolean.parseBoolean(nodes.item(0).getFirstChild().getNodeValue());
                            testPath.setSynchronousBOCS(boolValue);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_SYNCBOCS, String.valueOf(boolValue));

                            break;
                        case IConfigElements.CONFIG_DCTCHECKIN:
                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_NETLOCID);
                            value = nodes.item(0).getFirstChild().getNodeValue();
                            testPath.setNetLocId(value);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_NETLOCID, value);

                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_PREFERACS);
                            boolValue = Boolean.parseBoolean(nodes.item(0).getFirstChild().getNodeValue());
                            testPath.setPreferACS(boolValue);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_PREFERACS, String.valueOf(boolValue));

                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_ALLOWBOCS);
                            boolValue = Boolean.parseBoolean(nodes.item(0).getFirstChild().getNodeValue());
                            testPath.setAllowBOCS(boolValue);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_ALLOWBOCS, String.valueOf(boolValue));

                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_SYNCBOCS);
                            boolValue = Boolean.parseBoolean(nodes.item(0).getFirstChild().getNodeValue());
                            testPath.setSynchronousBOCS(boolValue);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_SYNCBOCS, String.valueOf(boolValue));

                            break;
                        case IConfigElements.CONFIG_DCTEXPORT:
                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_EXPORTDIR);
                            value = nodes.item(0).getFirstChild().getNodeValue();
                            testPath.setExportDir(value);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_EXPORTDIR, value);

                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_NETLOCID);
                            value = nodes.item(0).getFirstChild().getNodeValue();
                            testPath.setNetLocId(value);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_NETLOCID, value);

                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_PREFERACS);
                            boolValue = Boolean.parseBoolean(nodes.item(0).getFirstChild().getNodeValue());
                            testPath.setPreferACS(boolValue);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_PREFERACS, String.valueOf(boolValue));

                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_ALLOWBOCS);
                            boolValue = Boolean.parseBoolean(nodes.item(0).getFirstChild().getNodeValue());
                            testPath.setAllowBOCS(boolValue);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_ALLOWBOCS, String.valueOf(boolValue));

                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_SYNCBOCS);
                            boolValue = Boolean.parseBoolean(nodes.item(0).getFirstChild().getNodeValue());
                            testPath.setSynchronousBOCS(boolValue);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_SYNCBOCS, String.valueOf(boolValue));

                            break;
                        case IConfigElements.CONFIG_DCTIMPORT:
                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_IMPORTPATH);
                            value = nodes.item(0).getFirstChild().getNodeValue();
                            testPath.setImportPath(value);

                            //build up action strings
                            folderId = DocbaseHelper.getFolderId(docbase, testPath.getName(), testPath.getPasswd(), testPath.getTestPath());
                            testPath.addActions(IConfigElements.CONFIG_PATH, folderId);

                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_NETLOCID);
                            value = nodes.item(0).getFirstChild().getNodeValue();
                            testPath.setNetLocId(value);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_NETLOCID, value);

                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_PREFERACS);
                            boolValue = Boolean.parseBoolean(nodes.item(0).getFirstChild().getNodeValue());
                            testPath.setPreferACS(boolValue);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_PREFERACS, String.valueOf(boolValue));

                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_ALLOWBOCS);
                            boolValue = Boolean.parseBoolean(nodes.item(0).getFirstChild().getNodeValue());
                            testPath.setAllowBOCS(boolValue);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_ALLOWBOCS, String.valueOf(boolValue));

                            nodes = child.getElementsByTagName(IConfigElements.CONFIG_USER_SYNCBOCS);
                            boolValue = Boolean.parseBoolean(nodes.item(0).getFirstChild().getNodeValue());
                            testPath.setSynchronousBOCS(boolValue);
                            // build up action strings
                            testPath.addActions(IConfigElements.CONFIG_USER_SYNCBOCS, String.valueOf(boolValue));

                            break;

                    }

                    testPaths.add(testPath);
                }
            }
        }
        catch (DOMException e) {
            throw new ConfigException(e.getMessage());
        }
        catch (NumberFormatException e) {
            throw new ConfigException(e.getMessage());
        }
        catch (DfException e) {
            throw new ConfigException(e.getMessage());
        }

        return testPaths;
    }
}

