package com.lanxum.dstor.server;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lanxum.dstor.server.rest.RestServer;

public class Main
{

	private static final Logger logger = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) throws Exception
	{
		logger.info("Starting");
		ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
		RestServer server = (RestServer) ctx.getBean("restServer");
		server.run();
		logger.info("Started");
		server.waitForStop();
		logger.info("Stopped");
	}

}
