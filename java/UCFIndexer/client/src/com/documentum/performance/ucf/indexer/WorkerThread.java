package com.documentum.performance.ucf.indexer;

import com.documentum.performance.ucf.indexer.config.IConfigElements;
import com.documentum.performance.ucf.indexer.data.UCFTestPathData;
import com.documentum.ucf.client.transport.impl.ClientSession;
import com.documentum.ucf.common.transport.TransportStreamException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Logger;

public class WorkerThread extends Thread
{

    private static Logger theLogger = Logger.getLogger(WorkerThread.class.getName());
    private String m_docbase;
    private String m_url;
    private SSLSocketFactory m_factory;
    private UCFTestPathData m_testData;
    private int m_thinkTime;
    private Random m_rand;
    private List<String> m_items;

    private static final String LOGIN = "/Login";
    private static final String INDEXER = "/UCFIndexer";
    private static final String CONFIG = "/ConfigUpdate";
    private static final String SERVLET = "/servlet";
    private static final int HTTP_ERR_CODES = 400;

    public WorkerThread(String docbase, String url, UCFTestPathData testData, int thinkTime, List<String> items)
    {
        m_docbase = docbase;
        m_url = url;
        m_testData = testData;
        m_thinkTime = thinkTime * 1000;        // converts to millis
        m_rand = new Random(System.currentTimeMillis());
        m_items = items;
    }

    public void configUpdate() throws IOException
    {
        String cookie = getCookie();
        String uid = getClientId(cookie);

        // build up request
        StringBuffer sb = new StringBuffer();
        sb.append(m_url);
        sb.append(CONFIG);
        sb.append("?");
        sb.append(IConfigElements.CONFIG_USER_UID);
        sb.append("=");
        sb.append(uid);
        String request = sb.toString();

        theLogger.finest(request);

        HttpURLConnection url = (HttpURLConnection) new URL(request).openConnection();
        url.setRequestMethod("GET");
        url.addRequestProperty("Cookie", cookie);
        url.connect();
        int code = url.getResponseCode();
        theLogger.fine("http response for ConfigUpdate is " + code);
    }

    public void run()
    {
        String key;
        int code;
        String user = m_testData.getName();

        System.out.println("Starting user " + user);

        try {
            int counts = m_items.size();
            int action = m_testData.getAction();
            if (action == IConfigElements.CONFIG_IMPORT) {
                key = IConfigElements.CONFIG_USER_IMPORTPATH;
            } else {
                key = IConfigElements.CONFIG_USER_OBJECTID;
            }

            for (int j = 0; j < counts; j++) {
                // config update
                configUpdate();

                // new a clientsession for every user
                String cookie = getCookie();
                String uid = getClientId(cookie);
                theLogger.info("uid=" + uid);

                String command = buildRequest(m_testData, uid, key, m_items.get(j));
                HttpURLConnection url = (HttpURLConnection) new URL(command).openConnection();
                url.setRequestMethod("GET");
                url.addRequestProperty("Cookie", cookie);
                url.connect();
                code = url.getResponseCode();
                theLogger.fine("http response is " + code);

                if (code >= HTTP_ERR_CODES) {
                    System.out.println("\tError finishing work item " + j + " for user " + user);
                } else {
                    System.out.println("\tFinish work item " + j + " for user " + user);
                }

                // introduce think time
                int time = (int) m_rand.nextDouble() * m_thinkTime + 1000;
                sleep(time);
            }

            System.out.println("Finish user " + user);
        } catch (Exception e) {
            theLogger.severe(e.getMessage());
        }
    }

    private String buildRequest(UCFTestPathData current, String uid,
                                String key, String value) throws UnsupportedEncodingException
    {
        StringBuffer command = new StringBuffer();
        command.append(m_url);
        command.append(INDEXER);

        // append UID
        command.append("?");
        command.append(IConfigElements.CONFIG_USER_UID);
        command.append("=");
        command.append(uid);
        command.append("&");

        // append objectid or import file path
        command.append(URLEncoder.encode(key, "UTF-8"));
        command.append("=");
        command.append(URLEncoder.encode(value, "UTF-8"));

        // append the rest of the parameters
        List<String> params = current.getActionString();
        int paramLen = params.size();

        for (int j = 0; j < paramLen; j += 2) {
            command.append("&");
            command.append(URLEncoder.encode(params.get(j), "UTF-8"));
            command.append("=");
            command.append(URLEncoder.encode(params.get(j + 1), "UTF-8"));
        }

        String request = command.toString();
        theLogger.fine(request);

        return request;
    }

    private String getClientId(String cookie) throws IOException
    {
        String uid = "-1";

        try {
            String url = m_url + SERVLET;
            theLogger.fine("trying to get clientid to " + url);
            ClientSession session = new ClientSession(url, "Gair", cookie);
            boolean status = session.startSession();
            if (status) {
                uid = session.getUID();
                theLogger.fine("UCF client start successfully and the uid is " + uid);
            }
        } catch (TransportStreamException e) {
            theLogger.severe("Exception in constructing ClientSession: " + e.getMessage());
            System.err.println("Error getting clientId: " + e.getMessage());
            e.printStackTrace();
        }

        return uid;
    }

    private String getCookie() throws IOException
    {
        String strCookie = null;
        String url = m_url + LOGIN;
        //String url = m_url;
        theLogger.fine("trying to get cookie from " + url);
        HttpURLConnection login = (HttpURLConnection) new URL(url).openConnection();
        if (m_factory != null) {
            ((HttpsURLConnection) login).setSSLSocketFactory(m_factory);
        }
        login.setRequestMethod("GET");
        login.connect();

        Map<String, List<String>> headerFields = login.getHeaderFields();
        Set<String> set = headerFields.keySet();
        for (String key : set) {
            if ("Set-Cookie".equalsIgnoreCase(key)) {
                List<String> list = headerFields.get(key);
                for (String value : list) {
                    int start = value.indexOf("JSESSIONID");
                    int end = value.indexOf(";");
                    if (start != -1 && end != -1) {
                        //System.out.println("sessionid: " + element.substring(start, end));
                        strCookie = value.substring(start, end);
                        break;
                    }
                }
            }
        }

        theLogger.finer(strCookie);
        return strCookie;
    }
}
