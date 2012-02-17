/**
 * Copyright ?1994-2004. EMC Corporation.  All Rights Reserved.
 * Confidential Property of EMC Corp.. May not
 * be used without prior written agreement signed by an EMC corporate
 * officer.
 */

package com.documentum.qa.tools.ucf;


import java.net.Socket;
import java.net.URL;
import java.io.*;
import java.util.logging.Logger;
import java.util.logging.Level;


/**
 * This is the invokation thread which will do the job of starting up the UCF Client Session.
 */ 
public class TDfUCFInvokerThread implements Runnable
{
    // static s_logger
    static Logger s_logger = Logger.getLogger(TDfUCFInvokerThread.class.getPackage().getName());

    /**
     * The constructor for the Invoker Thread.  
     * @param socket The socket reference for the invoker connection
     */ 
    public TDfUCFInvokerThread(Socket socket)
    {
        m_socket = socket;
    }

    /**
     * The execution part of the thread
     */ 
    public void run()
    {
        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;

        s_logger.log(Level.FINER,"Received request to start new UCF Client Session");
        try
        {
            inputStream = new BufferedInputStream(m_socket.getInputStream());
            outputStream = new BufferedOutputStream(m_socket.getOutputStream());
            byte[] command = new byte[BUFFER_SIZE];
            inputStream.read(command);
            String commandString = new String(command);
            TDfUCFRequestConfig ucfRequest = TDfUCFInvokerUtils.parseRequest(commandString);
            s_logger.log(Level.INFO, "Starting UCF Operation - " + ucfRequest.getMessage());
            s_logger.log(Level.FINER, "Read JSESSIONID from Client : " + ucfRequest.getJsessionID() + "");
            URL ucfPath = new URL(ucfRequest.getHost() + DEFAULT_PORT_SEPARATOR + ucfRequest.getPort() + ucfRequest.getAppRoot());
            s_logger.log(Level.FINER, "Getting a UCF Session ID from UCFServer : " + ucfPath);
            String uid = TDfUCFInvokerUtils.getNewUcfSessionUid(ucfRequest.getUcfMode(), ucfPath, ucfRequest.getJsessionID());
            s_logger.log(Level.INFO, "New UCF Session with UID  : " + uid);
            outputStream.write(uid.getBytes());
            outputStream.flush();
        }
        catch (Exception e)
        {
            s_logger.log(Level.SEVERE, e.getMessage(),e);
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
                s_logger.log(Level.SEVERE,"Unable to close streams",e);
            }
        }
    }

    private Socket m_socket = null;
    public static final String DEFAULT_PROTOCOL= "http://";
    public static final String DEFAULT_PORT_SEPARATOR= ":";
    public static final int BUFFER_SIZE = 256;
}
