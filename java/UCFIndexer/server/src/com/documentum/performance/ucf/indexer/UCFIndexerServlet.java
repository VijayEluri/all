package com.documentum.performance.ucf.indexer;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.acs.IDfAcsTransferPreferences;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;
import com.documentum.operations.*;
import com.documentum.operations.contentpackage.*;
import com.documentum.performance.ucf.indexer.config.IConfigElements;
import com.documentum.ucf.common.UCFException;
import com.documentum.ucf.common.transport.TransportException;
import com.documentum.ucf.server.contentpackage.IPackageProcessor;
import com.documentum.ucf.server.contentpackage.IPackageProcessorFactory;
import com.documentum.ucf.server.transport.ICommManager;
import com.documentum.ucf.server.transport.IServerSession;
import com.documentum.ucf.server.transport.requests.spi.IExitRequest;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.Collator;

public class UCFIndexerServlet extends HttpServlet
{

    private Logger m_log;

    private static final String D6_VER = "6.0.0.085";
    private static final String D53SP1_VER = "5.3.0.124";

    private boolean m_dctReady;
    private boolean m_d6Ready;

    private IDfClientX m_clientX;
    private IDfSessionManager m_dfcManager;

    public void init()
    {
        try {
            String realPath = getServletContext().getRealPath("/");
            String fileSep = System.getProperty("file.separator");

            if (realPath != null && (!realPath.endsWith(fileSep)))
                realPath = realPath + fileSep;

            //System.out.println(realPath);

            //load the configuration for this application's loggers using the
            // servletLog.properties file
            PropertyConfigurator.configure(realPath + "WEB-INF/classes/log4j.properties");
            m_log = Logger.getLogger(UCFIndexerServlet.class.getName());
            m_log.debug("UCFIndexer started.");

            m_clientX = new DfClientX();
            String version = m_clientX.getDFCVersion();

            // determine whether we have D6 or 53SP1 - SP6
            Collator collator = Collator.getInstance();
            if (collator.compare(D53SP1_VER, version.toLowerCase()) < 0) {
                m_dctReady = true;
                m_d6Ready = collator.compare(D6_VER, version.toLowerCase()) <= 0;
            } else
                m_dctReady = false;

            m_dfcManager = m_clientX.getLocalClient().newSessionManager();
        } catch (DfException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        doGet(request, response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        IDfSession session = null;
        IServerSession ucfSession = null;
        ICommManager manager = null;
        String clientId = null;
        String user = null;

        try {
            String objectId;
            String checkoutDir;
            String netLocId;
            boolean preferACS;
            boolean allowBOCS;
            boolean syncBOCS;
            String exportDir;
            String importpath;
            String destpath;

            HttpSession httpSession = request.getSession(false);
            if (httpSession == null)
                throw new ServletException("Http Session is not initialized");

            long start = System.currentTimeMillis();

            manager = (ICommManager) httpSession.getAttribute(ICommManager.HTTP_SESSION_COMM_MGR_ATTR);
            if (manager == null)
                throw new ServletException("UCF is not properly initialized");

            clientId = request.getParameter(IConfigElements.CONFIG_USER_UID);
            ucfSession = manager.newSession(clientId);

            long end = System.currentTimeMillis();

            String docbase = request.getParameter(IConfigElements.CONFIG_DOCBASE);
            user = request.getParameter(IConfigElements.CONFIG_USER_NAME);
            String pwd = request.getParameter(IConfigElements.CONFIG_USER_PASSWORD);

            m_log.info(user + ", initializing UCF server session, " + (end - start));

            try {
                IDfLoginInfo loginInfo = m_clientX.getLoginInfo();
                loginInfo.setUser(user);
                loginInfo.setPassword(pwd);
                m_dfcManager.setIdentity(docbase, loginInfo);
            } catch (DfServiceException dfs) {
                m_log.warn("Identity " + user + " has already been set");
            }

            session = m_dfcManager.getSession(docbase);

            int action = Integer.parseInt(request.getParameter(IConfigElements.CONFIG_USER_ACTION));
            switch (action) {
                case IConfigElements.CONFIG_CHECKOUT:
                    objectId = request.getParameter(IConfigElements.CONFIG_USER_OBJECTID);
                    checkoutDir = request.getParameter(IConfigElements.CONFIG_USER_CHECKOUTDIR);
                    checkout(user, session, ucfSession, checkoutDir, objectId);
                    //release(user, manager, ucfSession, clientId);
                    break;
                case IConfigElements.CONFIG_CHECKIN:
                    objectId = request.getParameter(IConfigElements.CONFIG_USER_OBJECTID);
                    checkin(user, session, ucfSession, objectId);
                    //release(user, manager, ucfSession, clientId);
                    break;
                case IConfigElements.CONFIG_EXPORT:
                    objectId = request.getParameter(IConfigElements.CONFIG_USER_OBJECTID);
                    exportDir = request.getParameter(IConfigElements.CONFIG_USER_EXPORTDIR);
                    exportDoc(user, session, ucfSession, exportDir, objectId);
                    //release(user, manager, ucfSession, clientId);
                    break;
                case IConfigElements.CONFIG_IMPORT:
                    importpath = request.getParameter(IConfigElements.CONFIG_USER_IMPORTPATH);
                    destpath = request.getParameter(IConfigElements.CONFIG_PATH);
                    importDoc(user, session, ucfSession, importpath, destpath);
                    //release(user, manager, ucfSession, clientId);
                    break;
                case IConfigElements.CONFIG_CANCELCHECKOUT:
                    objectId = request.getParameter(IConfigElements.CONFIG_USER_OBJECTID);
                    cancelCheckout(user, session, ucfSession, objectId);
                    //release(user, manager, ucfSession, clientId);
                    break;
                case IConfigElements.CONFIG_DCTCHECKOUT:
                    if (!m_dctReady) {
                        throw new IndexerServerException("Need at least DFC 5.3SP1 to do distributed content checkout");
                    }
                    objectId = request.getParameter(IConfigElements.CONFIG_USER_OBJECTID);
                    checkoutDir = request.getParameter(IConfigElements.CONFIG_USER_CHECKOUTDIR);

                    netLocId = request.getParameter(IConfigElements.CONFIG_USER_NETLOCID);
                    preferACS = Boolean.parseBoolean(request.getParameter(IConfigElements.CONFIG_USER_PREFERACS));
                    allowBOCS = Boolean.parseBoolean(request.getParameter(IConfigElements.CONFIG_USER_ALLOWBOCS));
                    syncBOCS = Boolean.parseBoolean(request.getParameter(IConfigElements.CONFIG_USER_SYNCBOCS));

                    dctCheckout(user, session, ucfSession, checkoutDir, objectId, netLocId, preferACS, allowBOCS, syncBOCS);
                    //release(user, manager, ucfSession, clientId);
                    break;
                case IConfigElements.CONFIG_DCTCHECKIN:
                    if (!m_d6Ready) {
                        throw new IndexerServerException("Need at least DFC 6 to do distributed content checkin");
                    }
                    objectId = request.getParameter(IConfigElements.CONFIG_USER_OBJECTID);

                    netLocId = request.getParameter(IConfigElements.CONFIG_USER_NETLOCID);
                    preferACS = Boolean.parseBoolean(request.getParameter(IConfigElements.CONFIG_USER_PREFERACS));
                    allowBOCS = Boolean.parseBoolean(request.getParameter(IConfigElements.CONFIG_USER_ALLOWBOCS));
                    syncBOCS = Boolean.parseBoolean(request.getParameter(IConfigElements.CONFIG_USER_SYNCBOCS));

                    dctCheckin(user, session, ucfSession, objectId, netLocId, preferACS, allowBOCS, syncBOCS);
                    //release(user, manager, ucfSession, clientId);
                    break;
                case IConfigElements.CONFIG_DCTEXPORT:
                    if (!m_dctReady) {
                        throw new IndexerServerException("Need at least DFC 5.3SP1 to do distributed content export");
                    }
                    objectId = request.getParameter(IConfigElements.CONFIG_USER_OBJECTID);
                    exportDir = request.getParameter(IConfigElements.CONFIG_USER_EXPORTDIR);

                    netLocId = request.getParameter(IConfigElements.CONFIG_USER_NETLOCID);
                    preferACS = Boolean.parseBoolean(request.getParameter(IConfigElements.CONFIG_USER_PREFERACS));
                    allowBOCS = Boolean.parseBoolean(request.getParameter(IConfigElements.CONFIG_USER_ALLOWBOCS));
                    syncBOCS = Boolean.parseBoolean(request.getParameter(IConfigElements.CONFIG_USER_SYNCBOCS));

                    dctExport(user, session, ucfSession, exportDir, objectId, netLocId, preferACS, allowBOCS, syncBOCS);
                    //release(user, manager, ucfSession, clientId);
                    break;
                case IConfigElements.CONFIG_DCTIMPORT:
                    if (!m_d6Ready) {
                        throw new IndexerServerException("Need at least DFC 6 to do distributed content checkin");
                    }
                    importpath = request.getParameter(IConfigElements.CONFIG_USER_IMPORTPATH);
                    destpath = request.getParameter(IConfigElements.CONFIG_PATH);

                    netLocId = request.getParameter(IConfigElements.CONFIG_USER_NETLOCID);
                    preferACS = Boolean.parseBoolean(request.getParameter(IConfigElements.CONFIG_USER_PREFERACS));
                    allowBOCS = Boolean.parseBoolean(request.getParameter(IConfigElements.CONFIG_USER_ALLOWBOCS));
                    syncBOCS = Boolean.parseBoolean(request.getParameter(IConfigElements.CONFIG_USER_SYNCBOCS));

                    dctImport(user, session, ucfSession, importpath, destpath, netLocId, preferACS, allowBOCS, syncBOCS);
                    //release(user, manager, ucfSession, clientId);
                    break;
            }


        } catch (IndexerServerException e) {
            m_log.fatal(e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
        catch (DfServiceException e) {
            m_log.fatal(e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }
        catch (Exception e) {
            m_log.fatal(e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            m_dfcManager.clearIdentities();
            // clean up DFC sessions
            if (session != null) {
                m_dfcManager.release(session);
            }

            // clean up UCF sessions
            try {
                release(user, manager, ucfSession, clientId);
            } catch (Exception ignored) {
            }
        }
    }

    public void checkout(String user, IDfSession session, IServerSession ucfSession, String checkoutDir, String objectId)
            throws DfException, UCFException
    {
        m_log.debug("Start checkout");
        long start = System.currentTimeMillis();

        IDfCheckoutOperation checkoutOperation = m_clientX.getCheckoutOperation();
        checkoutOperation.setSession(session);

        IDfContentPackageFactory pkgFactory = m_clientX.getContentPackageFactory();
        IDfCheckoutPackage checkoutPackage = pkgFactory.newCheckoutPackage();
        IDfClientServerFile clientServerDir = pkgFactory.newClientServerFile();
        clientServerDir.setClientFile(pkgFactory.newClientFile(checkoutDir));
        checkoutPackage.setDestinationDirectory(clientServerDir);

        IDfSysObject doc = ((IDfSysObject) session.getObject(m_clientX.getId(objectId)));
        if (doc.isVirtualDocument())
            checkoutPackage.add(doc.asVirtualDocument(null, false));
        else
            checkoutPackage.add(m_clientX.getId(objectId));

        IPackageProcessorFactory factory = ucfSession.getPackageProcessorFactory();
        IPackageProcessor pkgProcessor = factory.newCheckoutPackageProcessor(checkoutOperation, checkoutPackage);
        pkgProcessor.preProcess();

        checkoutOperation.add(checkoutPackage);
        checkoutOperation.execute();

        pkgProcessor.postProcess();

        long end = System.currentTimeMillis();
        m_log.info(user + ", checkout, " + (end - start));
    }

    public void checkin(String user, IDfSession session, IServerSession ucfSession, String objectId)
            throws DfException, UCFException
    {
        m_log.debug("start checkin");
        long start = System.currentTimeMillis();

        IDfCheckinOperation checkinOperation = m_clientX.getCheckinOperation();
        checkinOperation.setSession(session);

        IDfContentPackageFactory pkgFactory = m_clientX.getContentPackageFactory();
        IDfCheckinPackage checkinPackage = pkgFactory.newCheckinPackage();

        IDfSysObject doc = ((IDfSysObject) session.getObject(m_clientX.getId(objectId)));
        if (doc.isVirtualDocument())
            checkinPackage.add(doc.asVirtualDocument(null, false));
        else
            checkinPackage.add(m_clientX.getId(objectId));

        IPackageProcessorFactory factory = ucfSession.getPackageProcessorFactory();
        IPackageProcessor pkgProcessor = factory.newCheckinPackageProcessor(
                checkinOperation, checkinPackage);
        pkgProcessor.preProcess();

        checkinOperation.add(checkinPackage);
        checkinOperation.execute();

        pkgProcessor.postProcess();

        long end = System.currentTimeMillis();
        m_log.info(user + ", checkin, " + (end - start));
    }

    public void exportDoc(String user, IDfSession session, IServerSession ucfSession, String exportDir, String objectId)
            throws DfException, UCFException
    {
        m_log.debug("start export");
        long start = System.currentTimeMillis();

        // Setup the DFC operation
        IDfExportOperation exportOperation = m_clientX.getExportOperation();
        exportOperation.setSession(session);

        // Create and setup the package
        IDfContentPackageFactory pkgFactory = m_clientX.getContentPackageFactory();
        IDfExportPackage exportPackage = pkgFactory.newExportPackage();
        IDfClientServerFile clientServerDir = pkgFactory.newClientServerFile();
        clientServerDir.setClientFile(pkgFactory.newClientFile(exportDir));
        exportPackage.setDestinationDirectory(clientServerDir);
        exportPackage.setMacResourceForkOption(2);

        // Create package item and add to the package
        IDfSysObject doc = ((IDfSysObject) session.getObject(m_clientX.getId(objectId)));
        if (doc.isVirtualDocument())
            exportPackage.add(doc.asVirtualDocument(null, false));
        else
            exportPackage.add(m_clientX.getId(objectId));

        // Do pre-processing on the package
        IPackageProcessorFactory factory = ucfSession.getPackageProcessorFactory();
        IPackageProcessor pkgProcessor = factory.newExportPackageProcessor(
                exportOperation, exportPackage);
        pkgProcessor.preProcess();

        // Add the updated package to DFC operation and execute
        exportOperation.add(exportPackage);
        exportOperation.execute();

        // Do post-processing on the package
        pkgProcessor.postProcess();

        long end = System.currentTimeMillis();
        m_log.info(user + ", export, " + (end - start));
    }

    public void importDoc(String user, IDfSession session, IServerSession ucfSession, String importpath, String destpath)
            throws DfException, UCFException
    {
        m_log.debug("start import");
        long start = System.currentTimeMillis();

        // Setup the DFC operation
        IDfImportOperation importOperation = m_clientX.getImportOperation();
        importOperation.setSession(session);

        // Create and setup the package
        IDfContentPackageFactory pkgFactory = m_clientX.getContentPackageFactory();
        IDfImportPackage importPackage = pkgFactory.newImportPackage();

        // Create package item and add to the package 
        IDfClientServerFile csFile = pkgFactory.newClientServerFile();
        csFile.setClientFile(pkgFactory.newClientFile(importpath));
        IDfImportPackageItem item = importPackage.add(csFile);
        item.setDestinationFolderId(m_clientX.getId(destpath));

        // Do pre-processing on the package
        IPackageProcessorFactory factory = ucfSession.getPackageProcessorFactory();
        IPackageProcessor pkgProcessor = factory.newImportPackageProcessor(
                importOperation, importPackage);
        pkgProcessor.preProcess();

        // Add the updated package to DFC operation and execute
        importOperation.add(importPackage);
        importOperation.execute();

        // Do post-processing on the package
        pkgProcessor.postProcess();

        long end = System.currentTimeMillis();
        m_log.info(user + ", import, " + (end - start));
    }

    public void cancelCheckout(String user, IDfSession session, IServerSession ucfSession, String objectId)
            throws DfException, UCFException
    {
        m_log.debug("start cancelcheckout");
        long start = System.currentTimeMillis();

        IDfCancelCheckoutOperation cancelCheckoutOperation = m_clientX.getCancelCheckoutOperation();
        cancelCheckoutOperation.setSession(session);

        IDfContentPackageFactory pkgFactory = m_clientX.getContentPackageFactory();
        IDfCancelCheckoutPackage cancelCheckoutPackage = pkgFactory.newCancelCheckoutPackage();

        IDfSysObject doc = ((IDfSysObject) session.getObject(m_clientX.getId(objectId)));
        if (doc.isVirtualDocument())
            cancelCheckoutPackage.add(doc.asVirtualDocument(null, false));
        else
            cancelCheckoutPackage.add(m_clientX.getId(objectId));

        IPackageProcessorFactory factory = ucfSession.getPackageProcessorFactory();
        IPackageProcessor pkgProcessor = factory.newCancelCheckoutPackageProcessor(
                cancelCheckoutOperation, cancelCheckoutPackage);
        pkgProcessor.preProcess();

        cancelCheckoutOperation.add(cancelCheckoutPackage);
        cancelCheckoutOperation.execute();

        pkgProcessor.postProcess();

        long end = System.currentTimeMillis();
        m_log.info(user + ", cancelcheckout, " + (end - start));
    }

    public void dctCheckout(String user, IDfSession session, IServerSession ucfSession, String checkoutDir,
                            String objectId, String netLocId, boolean preferACS, boolean allowBOCS, boolean syncBOCS)
            throws DfException, UCFException
    {
        m_log.debug("start dctcheckout");
        long start = System.currentTimeMillis();

        // set transfer preferences
        IDfAcsTransferPreferences transferPrefs = m_clientX.getAcsTransferPreferences();
        transferPrefs.preferAcsTransfer(preferACS);

        // D53 can still do DCT, but only D6+ can invoke following BOCS calls
        if (m_d6Ready) {
            transferPrefs.allowBocsTransfer(allowBOCS);
            transferPrefs.setSynchronousBocsTransfer(syncBOCS);
        }
        transferPrefs.setClientNetworkLocationId(netLocId);

        IDfCheckoutOperation checkoutOperation = m_clientX.getCheckoutOperation();
        checkoutOperation.setSession(session);
        // bind operation with transfer preferences
        checkoutOperation.setAcsTransferPreferences(transferPrefs);

        IDfContentPackageFactory pkgFactory = m_clientX.getContentPackageFactory();
        IDfCheckoutPackage checkoutPackage = pkgFactory.newCheckoutPackage();
        IDfClientServerFile clientServerDir = pkgFactory.newClientServerFile();
        clientServerDir.setClientFile(pkgFactory.newClientFile(checkoutDir));
        checkoutPackage.setDestinationDirectory(clientServerDir);

        IDfSysObject doc = ((IDfSysObject) session.getObject(m_clientX.getId(objectId)));
        if (doc.isVirtualDocument())
            checkoutPackage.add(doc.asVirtualDocument(null, false));
        else
            checkoutPackage.add(m_clientX.getId(objectId));

        IPackageProcessorFactory factory = ucfSession.getPackageProcessorFactory();
        IPackageProcessor pkgProcessor = factory.newCheckoutPackageProcessor(checkoutOperation, checkoutPackage);
        pkgProcessor.preProcess();

        checkoutOperation.add(checkoutPackage);
        checkoutOperation.execute();

        pkgProcessor.postProcess();

        long end = System.currentTimeMillis();
        m_log.info(user + ", dctcheckout, " + (end - start));
    }

    public void dctCheckin(String user, IDfSession session, IServerSession ucfSession, String objectId,
                           String netLocId, boolean preferACS, boolean allowBOCS, boolean syncBOCS)
            throws DfException, UCFException
    {
        m_log.debug("start dctcheckin");
        long start = System.currentTimeMillis();

        // set transfer preferences, this is a D6+ only operation
        IDfAcsTransferPreferences transferPrefs = m_clientX.getAcsTransferPreferences();
        transferPrefs.preferAcsTransfer(preferACS);
        transferPrefs.allowBocsTransfer(allowBOCS);
        transferPrefs.setSynchronousBocsTransfer(syncBOCS);
        transferPrefs.setClientNetworkLocationId(netLocId);

        IDfCheckinOperation checkinOperation = m_clientX.getCheckinOperation();
        checkinOperation.setSession(session);
        // bind operation with transfer preferences
        checkinOperation.setAcsTransferPreferences(transferPrefs);

        IDfContentPackageFactory pkgFactory = m_clientX.getContentPackageFactory();
        IDfCheckinPackage checkinPackage = pkgFactory.newCheckinPackage();

        IDfSysObject doc = ((IDfSysObject) session.getObject(m_clientX.getId(objectId)));
        if (doc.isVirtualDocument())
            checkinPackage.add(doc.asVirtualDocument(null, false));
        else
            checkinPackage.add(m_clientX.getId(objectId));

        IPackageProcessorFactory factory = ucfSession.getPackageProcessorFactory();
        IPackageProcessor pkgProcessor = factory.newCheckinPackageProcessor(
                checkinOperation, checkinPackage);
        pkgProcessor.preProcess();

        checkinOperation.add(checkinPackage);
        checkinOperation.execute();

        pkgProcessor.postProcess();

        long end = System.currentTimeMillis();
        m_log.info(user + ", dctcheckin, " + (end - start));
    }

    public void dctExport(String user, IDfSession session, IServerSession ucfSession, String exportDir, String objectId,
                          String netLocId, boolean preferACS, boolean allowBOCS, boolean syncBOCS)
            throws DfException, UCFException
    {
        m_log.debug("start dctexport");
        long start = System.currentTimeMillis();

        // set transfer preferences
        IDfAcsTransferPreferences transferPrefs = m_clientX.getAcsTransferPreferences();
        transferPrefs.preferAcsTransfer(preferACS);

        // D53 can still do DCT, but only D6+ can invoke following BOCS calls
        if (m_d6Ready) {
            transferPrefs.allowBocsTransfer(allowBOCS);
            transferPrefs.setSynchronousBocsTransfer(syncBOCS);
        }
        transferPrefs.setClientNetworkLocationId(netLocId);

        // Setup the DFC operation
        IDfExportOperation exportOperation = m_clientX.getExportOperation();
        exportOperation.setSession(session);
        // bind operation with transfer preferences
        exportOperation.setAcsTransferPreferences(transferPrefs);

        // Create and setup the package
        IDfContentPackageFactory pkgFactory = m_clientX.getContentPackageFactory();
        IDfExportPackage exportPackage = pkgFactory.newExportPackage();
        IDfClientServerFile clientServerDir = pkgFactory.newClientServerFile();
        clientServerDir.setClientFile(pkgFactory.newClientFile(exportDir));
        exportPackage.setDestinationDirectory(clientServerDir);
        exportPackage.setMacResourceForkOption(2);

        // Create package item and add to the package
        IDfSysObject doc = ((IDfSysObject) session.getObject(m_clientX.getId(objectId)));
        if (doc.isVirtualDocument())
            exportPackage.add(doc.asVirtualDocument(null, false));
        else
            exportPackage.add(m_clientX.getId(objectId));

        // Do pre-processing on the package
        IPackageProcessorFactory factory = ucfSession.getPackageProcessorFactory();
        IPackageProcessor pkgProcessor = factory.newExportPackageProcessor(
                exportOperation, exportPackage);
        pkgProcessor.preProcess();

        // Add the updated package to DFC operation and execute
        exportOperation.add(exportPackage);
        exportOperation.execute();

        // Do post-processing on the package
        pkgProcessor.postProcess();

        long end = System.currentTimeMillis();
        m_log.info(user + ", dctexport, " + (end - start));
    }

    public void dctImport(String user, IDfSession session, IServerSession ucfSession, String importpath, String destpath,
                          String netLocId, boolean preferACS, boolean allowBOCS, boolean syncBOCS)
            throws DfException, UCFException
    {
        m_log.debug("start dctimport");
        long start = System.currentTimeMillis();

        // set transfer preferences, this is a D6+ only operation
        IDfAcsTransferPreferences transferPrefs = m_clientX.getAcsTransferPreferences();
        transferPrefs.preferAcsTransfer(preferACS);
        transferPrefs.allowBocsTransfer(allowBOCS);
        transferPrefs.setSynchronousBocsTransfer(syncBOCS);
        transferPrefs.setClientNetworkLocationId(netLocId);

        // Setup the DFC operation
        IDfImportOperation importOperation = m_clientX.getImportOperation();
        importOperation.setSession(session);
        // bind operation with transfer preferences
        importOperation.setAcsTransferPreferences(transferPrefs);

        // Create and setup the package
        IDfContentPackageFactory pkgFactory = m_clientX.getContentPackageFactory();
        IDfImportPackage importPackage = pkgFactory.newImportPackage();

        // Create package item and add to the package 
        IDfClientServerFile csFile = pkgFactory.newClientServerFile();
        csFile.setClientFile(pkgFactory.newClientFile(importpath));
        IDfImportPackageItem item = importPackage.add(csFile);
        item.setDestinationFolderId(m_clientX.getId(destpath));

        // Do pre-processing on the package
        IPackageProcessorFactory factory = ucfSession.getPackageProcessorFactory();
        IPackageProcessor pkgProcessor = factory.newImportPackageProcessor(
                importOperation, importPackage);
        pkgProcessor.preProcess();

        // Add the updated package to DFC operation and execute
        importOperation.add(importPackage);
        importOperation.execute();

        // Do post-processing on the package
        pkgProcessor.postProcess();

        long end = System.currentTimeMillis();
        m_log.info(user + ", dctimport, " + (end - start));
    }

    public void release(String user, ICommManager manager, IServerSession ucfSession, String clientId)
            throws TransportException
    {
        m_log.debug("start releasing " + clientId);
        long start = System.currentTimeMillis();

        IExitRequest exitRequest = ucfSession.getRequestFactory().newExitRequest();
        ucfSession.addRequest(exitRequest);
        ucfSession.execute();
        ucfSession.release();

        manager.release(clientId);

        long end = System.currentTimeMillis();
        m_log.info(user + ", releasing clientId " + clientId + ", " + (end - start));
    }
}
