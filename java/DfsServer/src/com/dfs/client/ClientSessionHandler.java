package com.dfs.client;

import org.apache.log4j.Logger;
import org.apache.mina.common.*;

public class ClientSessionHandler implements IoHandler
{

	Logger logger = Logger.getLogger(ClientSessionHandler.class);
	MinaClientThread clientThread;

	public ClientSessionHandler(MinaClientThread minaClientThread)
	{
		clientThread = minaClientThread;
	}

	public void exceptionCaught(IoSession arg0, Throwable arg1) throws Exception
	{
	}

	public void messageReceived(IoSession arg0, Object message) throws Exception
	{
		if (message.toString().trim().equalsIgnoreCase("done")) {
			logger.info("Done.");
			clientThread.notifyObject();
		}
	}

	public void messageSent(IoSession arg0, Object arg1) throws Exception
	{
	}

	public void sessionClosed(IoSession arg0) throws Exception
	{
	}

	public void sessionCreated(IoSession arg0) throws Exception
	{
	}

	public void sessionOpened(IoSession session) throws Exception
	{
	}

	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception
	{
	}

}
