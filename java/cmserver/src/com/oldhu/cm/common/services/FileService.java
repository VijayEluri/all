package com.oldhu.cm.common.services;

import java.io.InputStream;

public interface FileService
{
	public long store(InputStream input);
	public InputStream retrieve(long id);
}
