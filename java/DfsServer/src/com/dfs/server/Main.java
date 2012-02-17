package com.dfs.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.common.DefaultIoFilterChainBuilder;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.SocketAcceptor;

import com.dfs.Parameters;
import com.dfs.pool.ServicePool;

public class Main {
	private static final int PORT = 9876;

	public static void main(String[] args) throws IOException, InterruptedException {
		
		if (args.length > 0) {
			Parameters.location = args[0];
			System.out.println("Using " + args[0] + " as destination");
		} else {
			System.out.println("Using ACS as destination");
		}

		ServicePool.getInstance();
		
		IoAcceptor acceptor = new SocketAcceptor();
		DefaultIoFilterChainBuilder builder = acceptor.getFilterChain();

		builder.addLast("logger", new LoggingFilter());
		builder.addLast("codec", new ProtocolCodecFilter(
				new TextLineCodecFactory(Charset.forName("UTF-8"))));
		
		
//		acceptor.getSessionConfig().setReadBufferSize(2048);
//		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.bind(new InetSocketAddress(PORT), new DfsServerHandler());
		
		System.out.println("Server is up and running...");
	}

}
