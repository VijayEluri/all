package com.oldhu.fileserver.command;

import java.nio.ByteBuffer;

public interface Command extends Cloneable
{
	/*
	 * process buffers, the caller can call this method many times.
	 * the first buffer of the protocol must be passed in.
	 * the command will process the data until it cannot process anymore,
	 * then it will return true on method #isEndOfProcessing.
	 *
	 * Note: After this method, the buffer may still contain data to be processed by other commands
	 */
	public void processBuffer(ByteBuffer bb) throws Exception;

	/*
	 * return true if next byte in buffer cannot be handled by this command
	 */
	public boolean isEndOfProcessing();

	/*
	 * return result of processing
	 */
	public ByteBuffer getResult();

	
    public void complete();

	public Command clone();
}
