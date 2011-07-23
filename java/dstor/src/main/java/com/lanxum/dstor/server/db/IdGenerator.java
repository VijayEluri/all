package com.lanxum.dstor.server.db;

public interface IdGenerator
{
	public long getNextFileId() throws Exception;
}
