package com.flyingsoft.fs.saperion;

import com.saperion.connector.SaClassicConnector;
import com.saperion.connector.SaClassicConnectorImpl;
import com.saperion.exception.SaAuthenticationException;
import com.saperion.exception.SaDBException;
import com.saperion.exception.SaDDCException;
import com.saperion.exception.SaSystemException;
import com.saperion.intf.SaPropertyValue;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

public class Downloader extends HttpServlet {

    private static final Logger logger = Logger.getLogger(Downloader.class);
    private String user;
    private String password;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        user = config.getInitParameter("user");
        password = config.getInitParameter("password");
        logger.debug("Servlet init with user: " + user);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String value = request.getParameter("value");
        String dcc = request.getParameter("dcc");
        String field = request.getParameter("field");

        if (value == null || dcc == null || field == null) {
            logger.warn("dcc/field/value are not correctly passed in");
            return;
        }

        String hql = "select d.XHDOC, d.SYSROWID, d.SYSDOCTYPES from " + dcc + " d where d." + field.toUpperCase() + "='" + value + "'";
        logger.debug("HQL to run: " + hql);

        SaClassicConnector connector = null;
        try {

            String path = this.getClass().getResource("/saperion.properties").getPath();

            connector = new SaClassicConnectorImpl(path);
            connector.logon(user, password, 15, "");

            SearchResult r = search(connector, hql);
            if (r == null) {
                logger.warn("cannot find XHDOC for query: " + hql);
                return;
            }

            logger.info("found XHDOC for query: " + hql);

            InputStream is = connector.readDocument(r.xhdoc, true, 1);
            if (is == null) {
                logger.error("cannot get InputStream from hdoc: " + r.xhdoc);
                return;
            }

            setBinaryHttpHeader(response, r.doctype);

            OutputStream out = null;
            try {
                out = response.getOutputStream();
                writeStream(is, out);
            } catch (IOException ex) {
                logger.error("IO error", ex);
            } finally {
                closeInputStream(is);
                closeOutputStream(out);
            }
        } catch (SaAuthenticationException ex) {
            logger.error("Authentication error", ex);
        } catch (SaSystemException ex) {
            logger.error("Saperion error", ex);
        } catch (SaDBException ex) {
            logger.error("Db error", ex);
        } finally {
            logoff(connector);
        }
    }

    private void setBinaryHttpHeader(HttpServletResponse response, String doctype) {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"file." + doctype + "\"");
    }

    private void writeStream(InputStream is, OutputStream out) throws IOException {
        int bytes = 0;
        byte[] buf = new byte[8192];
        while ((bytes = is.read(buf)) > 0) {
            out.write(buf, 0, bytes);
        }
        out.close();
    }

    private void closeInputStream(InputStream is) {
        try {
            is.close();
        } catch (IOException ex) {
        }
    }

    private void closeOutputStream(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException ex) {
            }
        }
    }

    private void logoff(SaClassicConnector connector) {
        if (connector != null) {
            try {
                connector.logoff();
            } catch (Exception ex) {
                logger.info("Exception happened during connector.logoff()", ex);
            }
        }
    }

    private SearchResult search(SaClassicConnector connector, String hql)
            throws SaSystemException, SaAuthenticationException, SaDBException {
        SearchResult r = null;
        List<SaPropertyValue[]> documents = connector.search(hql);
        if (documents.size() > 0) {
            SaPropertyValue[] properties = documents.get(0);
            r = new SearchResult();
            for (SaPropertyValue property : properties) {
                if (property.getName().equalsIgnoreCase("SYSDOCTYPES")) {
                    r.doctype = property.getValues()[0].getStringValue();
                }
                if (property.getName().equalsIgnoreCase("$HDOC")) {
                    r.xhdoc = property.getValues()[0].getStringValue();
                }
            }
        }
        return r;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Saperion File Downloader";
    }

    private static class SearchResult {

        String xhdoc;
        String doctype;
    }
}
