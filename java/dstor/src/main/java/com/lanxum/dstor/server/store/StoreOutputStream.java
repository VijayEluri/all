package com.lanxum.dstor.server.store;

import java.io.IOException;
import java.io.OutputStream;

public class StoreOutputStream extends OutputStream
{
	private OutputStream os;
	private String URI;

	public StoreOutputStream(OutputStream os, String URI)
	{
		this.os = os;
		this.URI = URI;
	}

	public String getURI()
	{
		return URI;
	}

	@Override
	public void write(int b) throws IOException
	{
		os.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException
	{
		os.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		os.write(b, off, len);
	}

	@Override
	public void flush() throws IOException
	{
		os.flush();
	}

	@Override
	public void close() throws IOException
	{
		os.close();
	}
}
