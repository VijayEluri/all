/*
 * Copyright ?1994-2004. EMC Corporation.  All Rights Reserved.
 */

package com.documentum.qa.tools.ucf;


import com.documentum.ucf.client.transport.impl.ClientSession;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.logging.Logger;


public class TDfUCFInvokerUtils
{

    // static s_logger
    static Logger s_logger = Logger.getLogger(TDfUCFInvokerUtils.class.getPackage().getName());

    /**
      * In addition to being the hash table that records which clients have been "installed" it is
      * also used as a synchrozization point for the code that invokes the installer service in
      * below.
      */
     //private static final HashMap installedUCFClientTable =  new HashMap();



     /**
     * This method is used to startup the UCF Client Session
     * @param mode      Would either use GAIR or SOCKET as the mode for Transport
     * @param ucfPath   The path to the web application
     * @param cookie    The JSESSIONID cookie
     * @return the newly created ucf session id
     */
    public static String getNewUcfSessionUid (String mode, URL ucfPath, String cookie)
            throws TDfUCFInvokerException
    {
        String ucfUid = null;
        try
        {
            ClientSession sess = null;
            if (mode.equals(UCF_MODE_GAIR)) {
                sess = new ClientSession (ucfPath.toString(), "Gair", cookie);
            } else {
                sess = new ClientSession (ucfPath.toString(), "Socket", cookie);
            }
            boolean status = sess.startSession();
            if (status == false) {
                throw new Exception("UCF Client session failed to start");
            }
            ucfUid=sess.getUID();
        }
        catch (Exception e)
        {
           e.printStackTrace();
        }
        return ucfUid;
    }

    /**
     * Method to parse the UCF Request and put into a data structure
     * @param command the raw command received from the invoker dll
     * @return the requestConfig object
     * @throws TDfUCFInvokerException if an exception occured during parsing
     */
    public static TDfUCFRequestConfig parseRequest(String command) throws TDfUCFInvokerException
    {
        StringTokenizer st = new StringTokenizer(command,";");
        TDfUCFRequestConfig ucfRequest = new TDfUCFRequestConfig();

        String jsessidString = null;
        String ucfMode = UCF_MODE_GAIR;
        String host = DEFAULT_HOST;
        String msg = "";
        int port = DEFAULT_PORT;
        String appRoot = DEFAULT_PATH;

        // get ucf Mode
        if (st.hasMoreTokens())
            ucfMode = st.nextToken();

        // get host Name
        if (st.hasMoreTokens())
            host = st.nextToken();

        // get port Name
        if (st.hasMoreTokens())
            port = Integer.parseInt(st.nextToken());

        // get approot
        if (st.hasMoreTokens())
            appRoot = st.nextToken();

        // get jsessionid cookie
        if (st.hasMoreTokens())
           jsessidString = JSESS_PREFIX + st.nextToken();
        else
            throw new TDfUCFInvokerException("Invalid data sent to Invoker");

        // get msg
        if (st.hasMoreTokens())
            msg = st.nextToken();

        ucfRequest.setUcfMode(ucfMode);
        ucfRequest.setHost(host);
        ucfRequest.setPort(port);
        ucfRequest.setAppRoot(appRoot);
        ucfRequest.setJsessionID(jsessidString);
        ucfRequest.setMessage(msg);

        return ucfRequest;
    }

    public static final String UCF_SEED_DIR= "ucfSeedDir";
    public static final String URLPATH_SEPARATOR= "/";
    public static final String UCF_SERVER_CONFIG = "wdk/contentXfer/ucf.installer.config.xml";

    public static final String UCF_MODE_GAIR = "GAIR";
    public static final String JSESS_PREFIX = "JSESSIONID=";
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 7001;
    public static final String DEFAULT_PATH = "/webtop";
     public static final String DEFAULT_PROTOCOL= "http://";
    public static final String DEFAULT_PORT_SEPARATOR= ":";
}

