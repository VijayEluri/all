package com.dfs.client;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

public class ServletClientSessionHandler extends IoHandlerAdapter
{
	private MinaClientServlet clientServlet;

	public ServletClientSessionHandler(MinaClientServlet minaClientServlet)
	{
		clientServlet = minaClientServlet;
	}

	public void messageReceived(IoSession arg0, Object message) throws Exception
	{
		String str = message.toString().trim();
		if (str.equalsIgnoreCase("done")) {
			clientServlet.notifyObject();
		} else if (str.equalsIgnoreCase("error")) {
			clientServlet.notifyObject();
			throw new Exception("error at server side!");
		}
	}
}
