package com.documentum.performance.ucf.indexer;

import com.documentum.performance.ucf.indexer.config.IConfigElements;
import com.documentum.ucf.common.UCFException;
import com.documentum.ucf.server.transport.ICommManager;
import com.documentum.ucf.server.transport.IServerSession;
import com.documentum.ucf.server.transport.requests.spi.IExitRequest;
import com.documentum.ucf.server.transport.spi.IClientProxyFactory;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ConfigUpdateServlet extends HttpServlet
{
    private Logger m_log;

    public void init()
    {
        m_log = Logger.getLogger(ConfigUpdateServlet.class.getName());
        m_log.debug("ConfigUpdateServlet started.");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        try {
            HttpSession httpSession = request.getSession(false);
            if (httpSession == null)
                throw new ServletException("Http Session is not initialized");

            ICommManager manager = (ICommManager) httpSession.getAttribute(ICommManager.HTTP_SESSION_COMM_MGR_ATTR);
            if (manager == null)
                throw new ServletException("UCF is not properly initialized");

            String clientId = request.getParameter(IConfigElements.CONFIG_USER_UID);

            IServerSession ucfSession = manager.newSession(clientId);

            setupConfig(ucfSession);

            IExitRequest exitRequest = ucfSession.getRequestFactory().newExitRequest();
            ucfSession.addRequest(exitRequest);
            ucfSession.execute();
            manager.release(clientId);
        } catch (ServletException e) {
            m_log.fatal(e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            m_log.fatal(e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    void setupConfig(IServerSession ucfSession)
            throws UCFException
    {

        IClientProxyFactory clFactoryProxy = ucfSession.getClientProxyFactory();
        clFactoryProxy.getClientConfigurationService();
    }
}