package com.documentum.performance.ucf.indexer.data;

import java.util.ArrayList;
import java.util.List;

public class UCFTestPathData
{
    private String m_testPath;
    private String m_checkoutDir;
    private String m_exportDir;
    private String m_importPath;
    private String m_netLocId;                // for future DFS checkout
    private List<String> m_actionString = new ArrayList<String>();

    /**
     * m_action is an integer indicating:
     * 0 : invalid
     * 1 : checkout
     * 2 : checkin
     * 4 : export
     * 8 : import
     * 16 : cancelCheckout
     * 32 : distributed checkout
     */
    private int m_action;

    private String m_name;
    private String m_passwd;

    /**
     * Gets the test path.
     *
     * @return the test path
     */
    public String getTestPath()
    {
        return m_testPath;
    }

    /**
     * Sets the test path.
     *
     * @param m_testPath the test path to be used during the test
     */
    public void setTestPath(String m_testPath)
    {
        this.m_testPath = m_testPath;
    }

    public String getCheckoutDir()
    {
        return m_checkoutDir;
    }

    public void setCheckoutDir(String dir)
    {
        m_checkoutDir = dir;
    }

    public String getExportDir()
    {
        return m_exportDir;
    }

    public void setExportDir(String dir)
    {
        m_exportDir = dir;
    }

    public int getAction()
    {
        return m_action;
    }

    public void setAction(int m_action)
    {
        this.m_action = m_action;
    }

    public String getName()
    {
        return m_name;
    }

    public void setName(String m_name)
    {
        this.m_name = m_name;
    }

    public String getPasswd()
    {
        return m_passwd;
    }

    public void setPasswd(String m_passwd)
    {
        this.m_passwd = m_passwd;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("(");
        sb.append(m_name);
        sb.append(":");
        sb.append(m_passwd);
        sb.append(":");
        sb.append(m_action);
        sb.append(":");
        sb.append(m_testPath);
        sb.append(")");

        return sb.toString();
    }

    public String getImportPath()
    {
        return m_importPath;
    }

    public void setImportPath(String path)
    {
        m_importPath = path;
    }

    public List<String> getActionString()
    {
        return m_actionString;
    }

    public void setActionString(List<String> actionString)
    {
        this.m_actionString = actionString;
    }

    public void addActions(String key, String value)
    {
        m_actionString.add(key);
        m_actionString.add(value);
    }

    public String getNetLocId()
    {
        return m_netLocId;
    }

    public void setNetLocId(String locId)
    {
        m_netLocId = locId;
    }

    public void setPreferACS(boolean boolValue)
    {
    }

    public void setAllowBOCS(boolean boolValue)
    {
    }

    public void setSynchronousBOCS(boolean boolValue)
    {
    }
}
