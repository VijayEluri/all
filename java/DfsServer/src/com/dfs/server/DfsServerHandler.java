package com.dfs.server;

import org.apache.mina.common.*;

public class DfsServerHandler extends IoHandlerAdapter
{
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception
	{
		cause.printStackTrace();
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception
	{
		String str = message.toString().trim();
		if (str.equalsIgnoreCase("quit")) {
			session.close();
			return;
		}

		DfsOperation op = new DfsOperation();

		try {
			if (str.equalsIgnoreCase("save")) {
				op.save();
			} else {
				op.read();
			}
			session.write("done").join();
		} catch (Exception e) {
			session.write("error").join();
		}
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception
	{
		// System.out.println("IDLE " + session.getIdleCount(status));
	}
}
