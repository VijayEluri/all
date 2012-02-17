package com.dfs.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.mina.common.*;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.SocketConnector;

public class MinaClientServlet extends HttpServlet
{
	Object objWait = new Object();
	boolean done;

	private void setDone(boolean value)
	{
		synchronized (objWait) {
			done = value;
		}
	}

	private void waitForObject()
	{
		synchronized (objWait) {
			try {
				objWait.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void notifyObject()
	{
		synchronized (objWait) {
			done = true;
			objWait.notify();
		}
	}

	private void waitForComplete()
	{
		while (!done) {
			waitForObject();
		}
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String host = req.getParameter("host");
		String action = req.getParameter("action");

		if (host == null) {
			throw new ServletException("Please specify host parameter.");
		}
		
		if (action == null) {
			action = "save";
		}

		SocketConnector connector = new SocketConnector();
		DefaultIoFilterChainBuilder builder = connector.getFilterChain();
		builder.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));

		ConnectFuture cf = connector.connect(new InetSocketAddress(host, 9876), new ServletClientSessionHandler(this));
		cf.join();
		IoSession session = cf.getSession();
		try {
			setDone(false);
			WriteFuture wf;
			if (action.equalsIgnoreCase("save")) {
				wf = session.write("save");
			} else {
				wf = session.write("read");
			}
			wf.join();
			waitForComplete();

			wf = session.write("quit");
			wf.join();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
		resp.getWriter().println("done!");
	}

	private static final long serialVersionUID = 1L;
}
