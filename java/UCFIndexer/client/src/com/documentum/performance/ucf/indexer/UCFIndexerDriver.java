package com.documentum.performance.ucf.indexer;

import com.documentum.performance.ucf.indexer.config.ConfigException;
import com.documentum.performance.ucf.indexer.config.ConfigLoader;
import com.documentum.performance.ucf.indexer.data.UCFTestData;
import com.documentum.performance.ucf.indexer.data.UCFTestPathData;
import com.documentum.performance.ucf.indexer.util.DocbaseHelper;
import com.documentum.performance.ucf.indexer.util.LogHelper;

import java.io.IOException;
import java.util.List;
import java.util.logging.*;

public class UCFIndexerDriver
{

    private UCFTestData m_testData;

    private static Logger theLogger = Logger.getLogger("com.documentum.performance.ucf.indexer");
    private static FileHandler fh;

    private String m_rootHost;
    private String m_rootPort;
    private String m_protocol;
    private String m_appl;
    private String m_url;

    public UCFIndexerDriver(String configFile)
    {
        try {
            m_testData = ConfigLoader.loadDataFromConfigFile(configFile);
            setupLogging();
            m_rootHost = m_testData.getRootHost();
            m_rootPort = m_testData.getRootPort();
            m_protocol = m_testData.getProtocol();
            m_appl = m_testData.getAppl();
            setTestURL();
        } catch (ConfigException e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (IOException ioe) {
            System.err.println("Exception: " + ioe.getMessage());
            ioe.printStackTrace();
            System.exit(2);
        }
    }

    private void setupLogging() throws IOException
    {
        System.out.println("Setting up logging...");
        Level level = LogHelper.getLogLevel(m_testData.getLogLevel());
        if (level == null) {
            System.err.println("No such log level and will use Level.INFO instead");
            level = Level.INFO;
        }
        fh = new FileHandler(m_testData.getLogFile(), false);
        // have to change the log level of the handler to let log come out as designed
        fh.setLevel(level);

        // change the formatter from default xml to simple text
        fh.setFormatter(new SimpleFormatter());
        theLogger.setLevel(level);
        theLogger.addHandler(fh);

        // close the Console handler so that we only need log in file
        Handler[] handlers = Logger.getLogger("").getHandlers();
        for (Handler handler : handlers) {
            if (handler instanceof ConsoleHandler) {
                handler.setLevel(Level.OFF);
                break;
            }
        }
    }

    public void run()
    {
        List<UCFTestPathData> testData = m_testData.getTestPaths();
        int len = testData.size();
        //System.out.println("about to start "+ len + " users");
        String docbase = m_testData.getDocbase();
        int thinkTime = m_testData.getThinktime();

        // generating specified number of user threads
        WorkerThread[] workers = new WorkerThread[len];

        // should new a thread to serve every user
        for (int i = 0; i < len; i++) {
            UCFTestPathData data = testData.get(i);
            List<String> items = DocbaseHelper.buildWorkItems(data, docbase);
            workers[i] = new WorkerThread(docbase, m_url, data, thinkTime, items);
        }

        int rampup = m_testData.getRmpup() * 1000;
        // start workers
        try {
            for (int i = 0; i < len; i++) {
                workers[i].start();

                // add rampup time
                Thread.sleep(rampup);
            }

            // wait for all works done
            for (int i = 0; i < len; i++)
                workers[i].join();
        } catch (InterruptedException ignored) {
        }

    }

    public static void main(String args[])
    {
        try {
            UCFIndexerDriver test = new UCFIndexerDriver(args[0]);

            test.run();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void setTestURL()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(m_protocol);
        sb.append("://");
        sb.append(m_rootHost);

        if (!"80".equals(m_rootPort)) {
            sb.append(":");
            sb.append(m_rootPort);
        }

        sb.append(m_appl);
        m_url = sb.toString();
    }
}
