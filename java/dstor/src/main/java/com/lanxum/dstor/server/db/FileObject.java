package com.lanxum.dstor.server.db;

import java.util.Map;

public class FileObject
{
	private String URI;
	private String fileName;
	private long fileSize;
	private String md5Sum;
	private Map<String, Object> metaData;

	public Map<String, Object> getMetaData() {
		return metaData;
	}

	public void setMetaData(Map<String, Object> metaData) {
		this.metaData = metaData;
	}

	public String getMd5Sum()
	{
		return md5Sum;
	}

	public void setMd5Sum(String md5Sum)
	{
		this.md5Sum = md5Sum;
	}

	public String getURI()
	{
		return URI;
	}

	public void setURI(String uri)
	{
		this.URI = uri;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public long getFileSize()
	{
		return fileSize;
	}

	public void setFileSize(long fileSize)
	{
		this.fileSize = fileSize;
	}
}
