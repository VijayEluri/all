/**
 * Copyright ?1994-2004. EMC Corporation.  All Rights Reserved.
 * Confidential Property of EMC Corp.. May not
 * be used without prior written agreement signed by an EMC corporate
 * officer.
 */

package com.documentum.qa.tools.ucf;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This is the invokation thread which will do the job of starting up the UCF Client Session.
 */
public class TDfUCFInvokerProcess implements Runnable
{
    public Process process = null;
    // static s_logger
    static Logger s_logger = Logger.getLogger(TDfUCFInvokerProcess.class.getPackage().getName());

    /**
     * The constructor for the Invoker Thread.
     *
     * @param socket The socket reference for the invoker connection
     */
    public TDfUCFInvokerProcess(Socket socket)
    {
        m_socket = socket;
    }

    /**
     * This method is used to startup the UCF Client Session
     *
     * @param mode    Would either use GAIR or SOCKET as the mode for Transport
     * @param ucfPath The path to the web application
     * @param cookie  The JSESSIONID cookie
     * @return
     */
    protected String getUcfSession(String mode, String ucfPath, String cookie)
    {
        String retval = null;
        try
        {
            retval = forkProcess(mode, ucfPath, cookie);
        }
        catch (Exception e)
        {
            s_logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return retval;
    }

    /**
     * The execution part of the thread
     */
    public void run()
    {
        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        String jsessidString = null;
        String ucfMode = UCF_MODE_GAIR;
        String host = DEFAULT_HOST;
        String msg = "";
        int port = DEFAULT_PORT;
        String approot = DEFAULT_PATH;
        s_logger.log(Level.FINER, "Received request to start new UCF Client Session");
        try
        {
            inputStream = new BufferedInputStream(m_socket.getInputStream());
            outputStream = new BufferedOutputStream(m_socket.getOutputStream());
            byte[] command = new byte[BUFFER_SIZE];
            inputStream.read(command);
            String commandString = new String(command);
            StringTokenizer st = new StringTokenizer(commandString, ";");
            
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
                approot = st.nextToken();
            
            // get jsessionid cookie
            if (st.hasMoreTokens())
                jsessidString = JSESS_PREFIX + st.nextToken();
            else
                throw new Exception("Invalid data sent to Invoker");

            // get msg
            if (st.hasMoreTokens())
                msg = st.nextToken();

            s_logger.log(Level.INFO, "Starting UCF Operation - " + msg);
            s_logger.log(Level.FINER, "Read JSESSIONID from Client : " + jsessidString + "");
            s_logger.log(Level.FINER, "Getting a UCF Session ID from UCFServer : " + DEFAULT_PROTOCOL + host + DEFAULT_PORT_SEPARATOR + port + approot);
            String sess = getUcfSession(ucfMode, DEFAULT_PROTOCOL + host + DEFAULT_PORT_SEPARATOR + port + approot, jsessidString);
            //s_logger.log(Level.INFO, "New UCF Session Thread " + sess.getName() + " with UID  : " + sess.getUID());
            outputStream.write(sess.getBytes());
            outputStream.flush();

            // wait for process to terminate before exiting
            process.waitFor();

            s_logger.log(Level.INFO, "Completed UCF Operation - " + msg);
        }
        catch (Exception e)
        {
            s_logger.log(Level.SEVERE, e.getMessage(), e);
        }
        finally
        {
            try
            {
                inputStream.close();
                outputStream.close();
                m_socket.close();
            }
            catch (IOException e)
            {
                s_logger.log(Level.SEVERE, "Unable to close streams", e);
            }
        }
    }

    // Fork the TDfUCFProcess
    public String forkProcess(String mode, String ucfPath, String cookie) throws Exception
    {
        BufferedReader iStream = null;
        StringBuffer sb = new StringBuffer();
        sb.append("java -Ducf.home=c:/documentum ");
        sb.append("-classpath ./ucf-client-api.jar;./ucf-client-impl.jar;./ucf-mtinvoker.jar;%CLASSPATH% ");
        sb.append(" com.documentum.qa.tools.ucf.TDfUCFProcess " + mode + " " + ucfPath + " " + cookie + " " + UCF_MODE_GAIR);

        String cmd = sb.toString();
        StringBuffer outStr = new StringBuffer();
        StringBuffer errStr = new StringBuffer();
        try
        {
            Runtime r;
            r = Runtime.getRuntime();
            System.err.println("Forking:" + cmd);
            process = r.exec(cmd, null);

            // Input stream
            InputStream is = process.getInputStream();
            iStream = new BufferedReader(new InputStreamReader(is));
            String line = null;
            line = iStream.readLine();
            outStr.append(line);
            iStream.close();

            // Error stream
            InputStream es = process.getErrorStream();
            iStream = new BufferedReader(new InputStreamReader(es));
            line = null;
            line = iStream.readLine();
            errStr.append(line);
            iStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return outStr.toString();
    }


    private Socket m_socket = null;
    public static final String UCF_MODE_GAIR = "GAIR";
    public static final String UCF_MODE_SOCKET = "SOCKET";
    public static final String DEFAULT_PATH = "/webtop";
    public static final String DEFAULT_PROTOCOL = "http://";
    public static final String DEFAULT_PORT_SEPARATOR = ":";
    public static final String DEFAULT_HOST = "localhost";
    public static final String JSESS_PREFIX = "JSESSIONID=";
    public static final int DEFAULT_PORT = 7001;
    public static final int BUFFER_SIZE = 256;
}
