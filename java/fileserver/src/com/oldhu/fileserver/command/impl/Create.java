package com.oldhu.fileserver.command.impl;

import com.oldhu.fileserver.command.AbstractCommand;
import com.oldhu.fileserver.db.Db;
import java.nio.ByteBuffer;
import org.apache.log4j.Logger;

/*
 * 'Create' command protocol
 *
 * Client -> Server:
 * -----
 * | C |
 * -----
 *
 * Server -> Client:
 * ------------------
 * | O | <64bit ID> |
 * ------------------
 */
public class Create extends AbstractCommand
{
	private static final Logger logger = Logger.getLogger(Create.class);

	private boolean isEndOfProcessing = false;
	private long newId = 0;

	private Db db;
	public Create(Db db)
	{
		logger.debug("constructing.");
		this.db = db;
	}

	@Override
	public void processBuffer(ByteBuffer bb) throws Exception
	{
		newId = db.nextId();
		logger.debug("New Id: " + newId);
		isEndOfProcessing = true;
	}

	@Override
	public void complete()
	{
	}

	@Override
	public boolean isEndOfProcessing()
	{
		return isEndOfProcessing;
	}

	@Override
	public ByteBuffer getResult()
	{
		ByteBuffer buf = ByteBuffer.allocate(9);
		buf.put((byte)'O');
		buf.putLong(newId);
		buf.flip();
		return buf;
	}

}
