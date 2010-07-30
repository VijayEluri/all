package com.lanxum.dstor.server.rest.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

public interface HttpHandler
{
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response);
}
