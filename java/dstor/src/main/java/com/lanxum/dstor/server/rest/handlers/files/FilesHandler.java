package com.lanxum.dstor.server.rest.handlers.files;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;

import com.lanxum.dstor.server.rest.JSONHelper;
import com.lanxum.dstor.server.rest.handlers.AbstractRequestHandler;
import com.lanxum.dstor.server.rest.handlers.HttpHandler;

public class FilesHandler extends AbstractRequestHandler
{

	private static final Logger logger = Logger.getLogger(FilesHandler.class.getName());

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		baseRequest.setHandled(true);
		String method = request.getMethod();
		HttpHandler handler = null;

		handler = methodMap.get(method.toLowerCase());

		if (handler != null) {
			handler.handle(target, baseRequest, request, response);
		} else {
			logger.warn("requesting method is " + method + ", and it's not allowed");
			JSONHelper.respondNotAllowed(response, method);
		}
	}

}
