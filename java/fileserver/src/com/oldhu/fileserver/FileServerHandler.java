package com.oldhu.fileserver;

import org.apache.log4j.Logger;

import com.oldhu.fileserver.command.Command;
import com.oldhu.fileserver.command.CommandException;
import com.oldhu.fileserver.command.CommandFactory;
import java.nio.ByteBuffer;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

@ChannelPipelineCoverage("all")
public class FileServerHandler extends SimpleChannelHandler
{

	private static final Logger logger = Logger.getLogger(FileServerHandler.class);

	public FileServerHandler()
	{
		super();
		logger.debug("constructing.");
	}
	private CommandFactory commandFactory;

	public void setCommandFactory(CommandFactory factory)
	{
		commandFactory = factory;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{
		ByteBuffer buf = ((ChannelBuffer) e.getMessage()).toByteBuffer();
		logger.debug("bytes to read: " + buf.remaining());
		boolean needLoop = false;
		do {
			try {
				Command cmd = (Command) ctx.getAttachment();

				if (cmd == null) {
					cmd = commandFactory.getCommand(buf.get());
					ctx.setAttachment(cmd);
				}

				needLoop = false;

				cmd.processBuffer(buf);

				if (cmd.isEndOfProcessing()) {
					writeResult(e.getChannel(), cmd);
					completeCurrentCommand(cmd);
					ctx.setAttachment(null);
					if (buf.position() < buf.limit()) {
						logger.debug("has data left, need loop");
						needLoop = true;
					}
				}
			} catch (CommandException ce) {
				logger.warn("Cannot recognize command, try next char");
				if (buf.hasRemaining()) {
					needLoop = true;
				} else {
					needLoop = false;
				}
			}
		} while (needLoop);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception
	{
		logger.debug("channel closed");
		Command cmd = (Command) ctx.getAttachment();
		completeCurrentCommand(cmd);
		super.channelClosed(ctx, e);
	}

	private void completeCurrentCommand(Command cmd)
	{
		if (cmd != null) {
			cmd.complete();
			cmd = null;
		}
	}

	private void writeResult(Channel c, Command cmd)
	{
		if (cmd == null) {
			return;
		}
		ByteBuffer result = cmd.getResult();
		if (result == null) {
			return;
		}
		ChannelBuffer outBuf = ChannelBuffers.wrappedBuffer(result);
		c.write(outBuf);
	}
}
