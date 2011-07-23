package com.lanxum.dstor.server.store.bigfile;

import com.lanxum.dstor.server.store.FileStore;
import com.lanxum.dstor.server.store.StoreOutputStream;
import java.io.InputStream;
import org.apache.log4j.Logger;

public class FileStoreImpl implements FileStore {

    private static final Logger logger = Logger.getLogger(FileStoreImpl.class.getName());

    private String basePath;
    private int fileSize;

	public void setBasePath(String basePath)
	{
		this.basePath = basePath;
	}

    public void setFileSizeMB(int fileSize)
    {
        this.fileSize = fileSize;
    }

    public StoreOutputStream getOutputStream(long id, long size) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeByURI(String URI) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public InputStream getInputStreamByURI(String URI) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
