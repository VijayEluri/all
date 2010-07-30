package com.lanxum.dstor.server.rest.handlers;

import java.util.Map;

import org.eclipse.jetty.server.handler.AbstractHandler;

public abstract class AbstractRequestHandler extends AbstractHandler
{
	protected Map<String, HttpHandler> methodMap;

	public void setMethodMap(Map<String, HttpHandler> map)
	{
		this.methodMap = map;
	}
}
