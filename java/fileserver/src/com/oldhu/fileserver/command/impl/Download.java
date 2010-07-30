package com.oldhu.fileserver.command.impl;

import com.oldhu.fileserver.command.AbstractCommand;
import java.nio.ByteBuffer;

/*
 * 'Download' command protocol
 *
 * Client -> Server:
 * -----------------------------------
 * | S | <64bit ID> | <64bit offset> |
 * -----------------------------------
 *
 * Server -> Client:
 * -----------------------------------------------
 * | O | <64bit len of stream> | <binary stream> |
 * -----------------------------------------------
 */
public class Download extends AbstractCommand
{

	@Override
	public void processBuffer(ByteBuffer bb) throws Exception
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isEndOfProcessing()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ByteBuffer getResult()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void complete()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
