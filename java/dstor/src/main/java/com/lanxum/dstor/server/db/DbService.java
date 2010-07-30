package com.lanxum.dstor.server.db;

public interface DbService
{
	public void save(long id, FileObject obj);

	public FileObject find(long id);

	public void remove(long id);
}
