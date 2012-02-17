package com.oldhu.cm.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.caucho.hessian.server.HessianServlet;
import com.oldhu.cm.common.services.FileService;
import com.oldhu.cm.common.services.ObjectService;
import com.oldhu.cm.server.services.impl.FileServiceImpl;
import com.oldhu.cm.server.services.impl.ObjectServiceImpl;

public class BootStrap
{

	public static void main(String[] args) throws Exception
	{
		System.setProperty("org.eclipse.jetty.server.Request.maxFormContentSize", new Integer(2 * 1024 * 1024 * 1024).toString());
		Server server = new Server(8080);
		
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/api");
		server.setHandler(context);

		ServletHolder holder = new ServletHolder(HessianServlet.class);
		holder.setInitParameter("home-class", ObjectServiceImpl.class.getCanonicalName());
		holder.setInitParameter("home-api", ObjectService.class.getCanonicalName());
		context.addServlet(holder, "/object-service/*");

		holder = new ServletHolder(HessianServlet.class);
		holder.setInitParameter("home-class", FileServiceImpl.class.getCanonicalName());
		holder.setInitParameter("home-api", FileService.class.getCanonicalName());
		context.addServlet(holder, "/file-service/*");

		server.start();
		server.join();
	}

}
