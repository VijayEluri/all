package com.lanxum.dstor.server.rest;

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;

import com.lanxum.dstor.server.Main;

public class RestServer
{
	private static final Logger logger = Logger.getLogger(Main.class.getName());
	private Server server;
	private Map<String, AbstractHandler> urlMap;
	private int port;

	public RestServer(int port, Map<String, AbstractHandler> urlMap)
	{
		this.port = port;
		this.urlMap = urlMap;
	}

	public void run()
	{
		server = new Server();

		SelectChannelConnector c = new SelectChannelConnector();
		c.setPort(port);
		c.setResolveNames(false);

		server.setConnectors(new Connector[] { c });
		HandlerCollection hc = new HandlerCollection();

		for (Map.Entry<String, AbstractHandler> entry : urlMap.entrySet()) {
			ContextHandler handler = new ContextHandler();
			handler.setContextPath("/" + entry.getKey());
			handler.setClassLoader(Thread.currentThread().getContextClassLoader());
			handler.setResourceBase(".");
			handler.setHandler(entry.getValue());
			hc.addHandler(handler);
		}

		server.setHandler(hc);

		try {
			server.start();
		} catch (Exception ex) {
			logger.fatal("Error while starting server", ex);
		}
	}

	public void waitForStop()
	{
		try {
			server.join();
		} catch (InterruptedException e) {
			logger.fatal("Error while joining server", e);
		}
	}

}
