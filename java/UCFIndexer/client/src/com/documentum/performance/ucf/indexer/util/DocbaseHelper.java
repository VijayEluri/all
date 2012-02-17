package com.documentum.performance.ucf.indexer.util;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;
import com.documentum.performance.ucf.indexer.config.IConfigElements;
import com.documentum.performance.ucf.indexer.data.UCFTestPathData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DocbaseHelper
{
    private static Logger theLogger = Logger.getLogger(DocbaseHelper.class.getName());
    private static IDfClientX m_clientX;
    private static IDfSessionManager m_manager;

    static {
        try {
            m_clientX = new DfClientX();
            m_manager = m_clientX.getLocalClient().newSessionManager();
        } catch (DfException e) {
            System.err.println("Exception: " + e.getMessage());
            theLogger.severe(e.getMessage());
            theLogger.severe(e.getStackTraceAsString());
            System.exit(3);
        }
    }

    public static List<String> buildWorkItems(UCFTestPathData testData, String docbase)
    {
        List<String> items = null;
        int action = testData.getAction();
        String path = null;

        System.out.println("Building work items for user " + testData.getName());

        switch (action) {
            case IConfigElements.CONFIG_IMPORT:
                items = getFilenamesFromFolder(testData.getImportPath());
                if (items == null) {
                    theLogger.severe("import path is not a folder or not accessible");
                }
                break;
            default:
                path = testData.getTestPath();
                break;
        }

        if (path != null) {
            try {
                items = getObjectIdsFromFolder(docbase, testData.getName(), testData.getPasswd(), path);
            } catch (DfException e) {
                // TODO Auto-generated catch block
                theLogger.severe(e.getMessage());
                theLogger.severe(e.getStackTraceAsString());
                items = null;
            }
        }

        return items;
    }

    public static List<String> getFilenamesFromFolder(String folder)
    {
        List<String> list = new ArrayList<String>();
        File dir = new File(folder);

        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++)
                    list.add(files[i].getAbsolutePath());
            } else
                return null;
        } else
            return null;

        return list;
    }

    public static List<String> getObjectIdsFromFolder(String docbase, String user, String password, String testPath)
            throws DfException
    {
        // Get a new session from session manager
        IDfLoginInfo loginInfo = m_clientX.getLoginInfo();
        loginInfo.setUser(user);
        loginInfo.setPassword(password);
        m_manager.setIdentity(docbase, loginInfo);
        IDfSession session = m_manager.newSession(docbase);

        IDfFolder folder = session.getFolderByPath(testPath);

        if (folder == null) {
            theLogger.fine("Folder: " + testPath + " does not exist.");
            throw new DfException("Folder: " + testPath + " does not exist.");
        }

        IDfCollection collection = folder.getContents("r_object_id");
        List<String> objectIds = new ArrayList<String>();

        try {
            while (collection.next())
                objectIds.add(collection.getString("r_object_id"));
        } catch (Exception e) {
            throw new DfException(e.getMessage());
        } finally {
            if (collection != null)
                collection.close();
            if (session != null) {
                m_manager.clearIdentities();
                m_manager.release(session);
            }
        }

        return objectIds;
    }

    public static String getFolderId(String docbase, String user, String password, String testPath)
            throws DfException
    {
        // Get a new session from session manager
        IDfLoginInfo loginInfo = m_clientX.getLoginInfo();
        loginInfo.setUser(user);
        loginInfo.setPassword(password);
        m_manager.setIdentity(docbase, loginInfo);
        IDfSession session = m_manager.newSession(docbase);

        IDfSysObject sysObj = (IDfSysObject) session.getObjectByPath(testPath);
        if (sysObj == null) {
            theLogger.fine("No such folder " + testPath);
            throw new DfException("No such folder " + testPath);
        }
        String folderId = sysObj.getObjectId().toString();

        if (session != null) {
            m_manager.clearIdentities();
            m_manager.release(session);
        }

        return folderId;
    }
}
