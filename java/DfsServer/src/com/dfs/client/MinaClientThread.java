package com.dfs.client;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.apache.mina.common.*;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.SocketConnector;

public class MinaClientThread extends Thread
{

	private int id;

	Logger logger = Logger.getLogger(MinaClientThread.class);

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

	public MinaClientThread(int i)
	{
		id = i;
	}

	public void run()
	{
		SocketConnector connector = new SocketConnector();
		DefaultIoFilterChainBuilder builder = connector.getFilterChain();
		builder.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));

		int count = 0;

		while (true) {
			count++;
			ConnectFuture cf = connector.connect(new InetSocketAddress("localhost", 9876), new ClientSessionHandler(this));
			cf.join();
			IoSession session = cf.getSession();
			try {
				logger.info(id + " working at " + count);

				setDone(false);
				WriteFuture wf = session.write("save");
				wf.join();
				waitForComplete();

//				setDone(false);
//				wf = session.write("read");
//				wf.join();
//				waitForComplete();

				wf = session.write("quit");
				wf.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
