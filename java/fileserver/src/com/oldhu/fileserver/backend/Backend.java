package com.oldhu.fileserver.backend;

import java.io.OutputStream;

public interface Backend {
	public OutputStream getOutputStream(long id);
}
