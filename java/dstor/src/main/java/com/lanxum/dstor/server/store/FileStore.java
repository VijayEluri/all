package com.lanxum.dstor.server.store;

import java.io.InputStream;

public interface FileStore
{
	StoreOutputStream getOutputStream(long id, long size) throws Exception;

	void removeByURI(String URI);

	InputStream getInputStreamByURI(String URI) throws Exception;
}
