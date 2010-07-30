package com.oldhu.fileserver.command.impl;

import com.oldhu.fileserver.command.AbstractCommand;
import java.nio.ByteBuffer;
import org.apache.log4j.Logger;

public class Dummy extends AbstractCommand
{

	private static final Logger logger = Logger.getLogger(Create.class);

	@Override
	public void processBuffer(ByteBuffer bb) throws Exception
	{
		logger.debug("start processing");
		Thread.sleep(10 * 1000);
		logger.debug("done processing");
	}

	@Override
	public boolean isEndOfProcessing()
	{
		return true;
	}

	@Override
	public ByteBuffer getResult()
	{
		ByteBuffer buf = ByteBuffer.allocate(1);
		buf.put((byte) 'O');
		buf.flip();
		return buf;
	}

	@Override
	public void complete()
	{
	}

}
