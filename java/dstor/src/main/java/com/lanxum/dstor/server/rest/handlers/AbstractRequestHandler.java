package com.lanxum.dstor.server.rest.handlers;

import java.util.Map;

import org.eclipse.jetty.server.handler.AbstractHandler;

public abstract class AbstractRequestHandler extends AbstractHandler
{
	protected Map<String, HttpHandler> methodMap;

	/*
	 * receive method map from spring configure xml
	 */
	public void setMethodMap(Map<String, HttpHandler> map)
	{
		this.methodMap = map;
	}
}
