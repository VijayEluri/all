package com.oldhu.fileserver.command.impl;

import com.oldhu.fileserver.backend.Backend;
import com.oldhu.fileserver.command.AbstractCommand;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import org.apache.log4j.Logger;

/*
 * 'Save' command protocol
 *
 * Client -> Server:
 * --------------------------------------------------------------------------------
 * | S | <64bit ID> | <64bit offset> | <64bit length of stream> | <binary stream> |
 * --------------------------------------------------------------------------------
 *
 * Server -> Client:
 * -----
 * | O |
 * -----
 */
public class Save extends AbstractCommand
{

	private static final Logger logger = Logger.getLogger(Save.class);

	private long id = -1;
	private long offset = -1;
	private long lengthToRead = -1;

	private boolean isHeaderExtracted = false;
	private boolean isEndOfProcessing = false;

	private Backend backend;

	public Save(Backend backend)
	{
		this.backend = backend;
		logger.debug("constructing.");
	}


	private void extractHeaders(ByteBuffer bb)
	{
		id = bb.getLong();
		offset = bb.getLong();
		lengthToRead = bb.getLong();
		logger.debug(id + ", " + offset + ", " + lengthToRead);
	}

	private WritableByteChannel channel;

	private WritableByteChannel getChannel()
	{
		if (channel == null) {
			channel = Channels.newChannel(backend.getOutputStream(id));
		}
		
		return channel;
	}

	private void closeChannel()
	{
		logger.debug("closing channel");
		if (channel != null) {
			try {
				channel.close();
				channel = null;
			} catch (IOException ex) {
				logger.error("Error while closing stream.", ex);
			}
		}
	}

	@Override
	public void processBuffer(ByteBuffer bb) throws Exception
	{
		final int PROTOCOL_SIZE = 8 + 8 + 8;
		int size = bb.remaining();

		if (!isHeaderExtracted) { // when first buffer comes, try to decode id and offset
			if (size < PROTOCOL_SIZE) {
				return;
			}
			extractHeaders(bb);
			isHeaderExtracted = true;
		}

		if (lengthToRead > 0) {

			int limit = bb.limit();
			int bytes = bb.remaining();

			if (bb.remaining() > lengthToRead) { // more than we need, might be next command pending
				bb.limit((int) (limit - (bb.remaining() - lengthToRead)));
				bytes = bb.remaining();
			}

			getChannel().write(bb);

			bb.limit(limit); // reset the limit

			lengthToRead -= bytes;

			if (lengthToRead == 0) {
				isEndOfProcessing = true;
			}

		}
	}

	@Override
	public void complete()
	{
		closeChannel();
	}

	@Override
	public boolean isEndOfProcessing()
	{
		return isEndOfProcessing;
	}

	@Override
	public ByteBuffer getResult()
	{
		ByteBuffer buf = ByteBuffer.allocate(1);
		buf.put((byte) 'O');
		buf.flip();
		return buf;
	}
	
}
