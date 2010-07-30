package com.oldhu.fileserver;

import java.io.IOException;
import java.net.InetSocketAddress;

import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class NettyMain
{

	private static final int PORT = 8517;
	private static final Logger logger = Logger.getLogger(NettyMain.class);

	public static void main(String[] args) throws IOException
	{
        ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
		
		ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
		ServerBootstrap bootstrap = new ServerBootstrap(factory);

		FileServerHandler handler = (FileServerHandler) ctx.getBean("fileServerHandler");
		
		ChannelPipeline pipeline = bootstrap.getPipeline();
		pipeline.addLast("handler", handler);

		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.bind(new InetSocketAddress(PORT));

		logger.info("server started.");
	}
}
