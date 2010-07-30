package com.lanxum.dstor.server.rest.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class PropertiesHandler extends AbstractHandler
{

	private static final Logger logger = Logger.getLogger(PropertiesHandler.class.getName());

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		logger.debug(request.getMethod());
		baseRequest.setHandled(true);
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println("PropertiesHandler");
	}
}
