package com.oldhu.fileserver.db;

public interface Db
{

	public long nextId() throws Exception;

	public void saveObject(long id, Object object) throws Exception;

	public void close();
}
