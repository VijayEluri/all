package com.lanxum.dstor.server.store;

import java.io.InputStream;

public interface FileStore
{
	StoreOutputStream getOutputStreamById(long id) throws Exception;

	void removeByURI(String URI);

	InputStream getInputStreamByURI(String URI) throws Exception;
}
