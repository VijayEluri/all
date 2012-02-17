package com.documentum.performance.ucf.indexer;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * (c) Copyright Documentum, a division of EMC Corporation, 1991-2004.
 * All rights reserved. May not be used without prior written agreement
 * signed by a Documentum corporate officer.
 */
public class LoginServlet extends HttpServlet
{
    private Logger m_log;

    public void init()
    {
        m_log = Logger.getLogger(LoginServlet.class.getName());
        m_log.info("LoginServlet started.");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        // Creating new HttpSession
        request.getSession(true);

        PrintWriter writer = response.getWriter();
        writer.println("Check if Cookie is created?!");

        writer.flush();
    }
}