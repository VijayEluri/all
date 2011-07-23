package com.lanxum.dstor.server.store.fs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.lanxum.dstor.server.store.FileStore;
import com.lanxum.dstor.server.store.StoreOutputStream;

public class FileStoreImpl implements FileStore
{

	private static final Logger logger = Logger.getLogger(FileStoreImpl.class.getName());
	private String basePath;

	public void setBasePath(String basePath)
	{
		this.basePath = basePath;
	}

	public StoreOutputStream getOutputStream(long id, long size) throws Exception
	{
		String str = String.format("%016X", id);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 8; ++i) {
			sb.append(File.separatorChar);
			sb.append(str.substring(i * 2, i * 2 + 2));
		}
		String URI = sb.toString();
		File out = new File(basePath, URI);
		out.getParentFile().mkdirs();
		try {
			StoreOutputStream os = new StoreOutputStream(new FileOutputStream(out), URI);
			return os;
		} catch (Exception ex) {
			logger.error("Cannot create FileOutputStream for " + out.getAbsolutePath(), ex);
			throw ex;
		}
	}

	public void removeByURI(String URI)
	{
		File file = new File(basePath, URI);
		file.delete();
	}

	public InputStream getInputStreamByURI(String URI) throws FileNotFoundException
	{
		File file = new File(basePath, URI);
		try {
			return new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			logger.error("Cannot create FileInputStream for " + file.getAbsolutePath(), e);
			throw e;
		}
	}

}
