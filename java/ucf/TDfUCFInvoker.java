/**
 * Copyright � 1994-2004. EMC Corporation.  All Rights Reserved.
 * Confidential Property of EMC Corp.. May not
 * be used without prior written agreement signed by an EMC corporate
 * officer.
 */

package com.documentum.qa.tools.ucf;

import java.net.ServerSocket;
import java.io.IOException;

/**
 * UCF Invoker Multithreaded/Multiprocess server.
 * This class will start off UCF Client sessions in either a multithreaded or multiprocess
 * environment based on the option passed.
 * The intended client for this is LoadRunner scripts for RAS testing.
 * The threaded model is used instead of invoking the UCF from an applet which would fork off
 * a separate process for each UCF Client session.
 * The process model is used to make more realistic tests (however heavy on the client.)
 */

public class TDfUCFInvoker
{
    /**
     * This method will start the Server Socket which will listen for requests to startup ucf sessions.
     * @param listenPort    The port to listen for requests on
     * @throws IOException  If Unable to startup the server
     */
    public void startServer(int listenPort, String option, int len) throws IOException
    {
        System.out.println("Starting TDfUCFInvoker and waiting for connections at port " + listenPort);
        ServerSocket serverSocket = new ServerSocket(listenPort, len);
        while (true)
        {
            Thread ucfInvokerThread = null;
            if (option.equalsIgnoreCase("THREAD"))
                ucfInvokerThread = new Thread(new TDfUCFInvokerThread(serverSocket.accept()));
            else
                ucfInvokerThread = new Thread(new TDfUCFInvokerProcess(serverSocket.accept()));
            ucfInvokerThread.setDaemon(true);
            ucfInvokerThread.start();
        }
    }

    /**
     * This is the entry point into the class
     * @param args Can pass in the port to listen for requests on
     */
    public static void main(String[] args)
    {
        int listenPort = DEFAULT_PORT;

        if (args.length < 2)
        {
            System.err.println(USAGE);
            return;
        }
        
        String option = args[1];
        int len=DEFAULT_QUEUE_LENGTH;
        
        try
        {
            if (args.length > 0)
            {
                try {
                    listenPort = Integer.parseInt(args[0]);
                }
                catch (NumberFormatException e)
                {
                    System.err.println(USAGE);
                    return;
                }
            }
            
            if(args.length==3)
            {
                try {
                    len=Integer.parseInt(args[2]);
                } catch (NumberFormatException ne)
                {
                    System.err.println(USAGE);
                    return;
                }
            }
            
            TDfUCFInvoker ucfInvoker = new TDfUCFInvoker();
            ucfInvoker.startServer(listenPort,option,len);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.exit(-1);
    }

    private static final String USAGE = "Usage: TDfUCFInvoker <listen port> thread|process [queue length]";
    private static final int DEFAULT_PORT = 7338;
    private static final int DEFAULT_QUEUE_LENGTH = 150;

}
