package com.documentum.performance.ucf.indexer.data;

import java.util.ArrayList;
import java.util.List;

public class UCFTestData
{
    private String m_protocol;
    private String m_rootHost;
    private String m_rootPort;
    private String m_appl;
    private String m_docbase;
    private String m_adminUser;
    private String m_adminPassword;

    private int m_rampup; // rampup time
    private int m_thinktime; // think time

    private String m_logLevel;
    private String m_logFile;
    private boolean m_logThrowOutHighLow;

    private List<UCFTestPathData> m_testPaths = new ArrayList<UCFTestPathData>();

    /**
     * Gets the docbase being used for the test.
     *
     * @return the test docbase
     */
    public String getDocbase()
    {
        return m_docbase;
    }

    /**
     * Sets the docbase being used for the test.
     *
     * @param m_docbase the test docbase to be used during the test
     */
    public void setDocbase(String m_docbase)
    {
        this.m_docbase = m_docbase;
    }

    /**
     * Gets admin user used for the test.
     *
     * @return the admin user used for the test
     */
    public String getAdminUser()
    {
        return m_adminUser;
    }

    /**
     * Sets the admin user used for the test.
     *
     * @param m_user the admin user to be used during the test
     */
    public void setAdminUser(String m_user)
    {
        this.m_adminUser = m_user;
    }

    /**
     * Gets the admin password used for the test.
     *
     * @return the admin password used for the test
     */
    public String getAdminPassword()
    {
        return m_adminPassword;
    }

    /**
     * Sets the admin password used for the test.
     *
     * @param m_password the admin password to be used during the test
     */
    public void setAdminPassword(String m_password)
    {
        this.m_adminPassword = m_password;
    }

    /**
     * Gets a List of UCFTestPathData objects used for the test.
     *
     * @return a List of UCFTestPathData objects
     */
    public List<UCFTestPathData> getTestPaths()
    {
        return m_testPaths;
    }

    /**
     * Gets the client log level for the test.
     *
     * @return String which represents the log level
     */
    public String getLogLevel()
    {
        return m_logLevel;
    }

    /**
     * Sets the log level used for the test.
     *
     * @param m_logLevel the log level
     *                   class
     */
    public void setLogLevel(String m_logLevel)
    {
        this.m_logLevel = m_logLevel;
    }

    /**
     * Gets the full path and file name of the log file.
     *
     * @return full path and file name of the log file
     */
    public String getLogFile()
    {
        return m_logFile;
    }

    /**
     * Sets the full path and file name of the log file.
     *
     * @param m_logFile full path and file name of the log file
     */
    public void setLogFile(String m_logFile)
    {
        this.m_logFile = m_logFile;
    }

    /**
     * Gets whether or not to throw out out the highest and lowest values
     * captured when get the average time for an operation.
     *
     * @return true to throw highest and lowest opeartion values
     */
    public boolean getLogThrowOutHighLow()
    {
        return m_logThrowOutHighLow;
    }

    /**
     * Sets whether or not to throw out out the highest and lowest values
     * captured when get the average time for an operation.
     *
     * @param m_logThrowOutHighLow true to throw highest and lowest operation values
     */
    public void setLogThrowOutHighLow(boolean m_logThrowOutHighLow)
    {
        this.m_logThrowOutHighLow = m_logThrowOutHighLow;
    }

    /**
     * Sets the UCFTestPathData objects to be used for the test.
     *
     * @param m_testPaths a List of UCFTestPathData objects to be used for the test
     */
    public void setTestPaths(List<UCFTestPathData> m_testPaths)
    {
        this.m_testPaths = m_testPaths;
    }

    /**
     * Adds a UCFTestPathData object to the List of UCFTestPathData
     * objects to be used for the test.
     *
     * @param testPath to be added to the List of testPath objects used for the test
     */
    public void addTestPath(UCFTestPathData testPath)
    {
        m_testPaths.add(testPath);
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append(m_protocol);
        sb.append("://");
        sb.append(m_rootHost);
        sb.append(":");
        sb.append(m_docbase);
        sb.append(":");
        sb.append(m_adminUser);
        sb.append("@");
        sb.append(m_adminPassword);
        sb.append(":");
        sb.append(m_rootPort);
        sb.append(m_appl);
        sb.append(" ");

        sb.append("rampup=");
        sb.append(m_rampup);
        sb.append(";thinktime=");
        sb.append(m_thinktime);
        sb.append(" ");

        sb.append(m_logFile);
        sb.append(";loglevel=");
        sb.append(m_logLevel);
        sb.append(" ");

        sb.append(m_testPaths);

        return sb.toString();
    }


    public int getRmpup()
    {
        return m_rampup;
    }

    public void setRampup(int m_rampup)
    {
        this.m_rampup = m_rampup;
    }

    public int getThinktime()
    {
        return m_thinktime;
    }

    public void setThinktime(int m_thinktime)
    {
        this.m_thinktime = m_thinktime;
    }

    public String getProtocol()
    {
        return m_protocol;
    }

    public void setProtocol(String m_protocol)
    {
        this.m_protocol = m_protocol;
    }

    public String getRootHost()
    {
        return m_rootHost;
    }

    public void setRootHost(String host)
    {
        m_rootHost = host;
    }

    public String getRootPort()
    {
        return m_rootPort;
    }

    public void setRootPort(String port)
    {
        m_rootPort = port;
    }

    public String getAppl()
    {
        return m_appl;
    }

    public void setAppl(String m_appl)
    {
        this.m_appl = m_appl;
    }
}
