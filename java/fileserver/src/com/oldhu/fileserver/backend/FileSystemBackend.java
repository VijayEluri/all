package com.oldhu.fileserver.backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.log4j.Logger;

public class FileSystemBackend implements Backend
{
	private static final Logger logger = Logger.getLogger(FileSystemBackend.class);

	private File baseDir;
	
	public void setBaseDir(String baseDir)
	{
		this.baseDir = new File(baseDir);
	}

	@Override
	public OutputStream getOutputStream(long id)
	{
		String str = String.format("%016X", id);
		System.out.println(str);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 8; ++i) {
			sb.append(File.separatorChar);
			sb.append(str.substring(i * 2, i * 2 + 2));
		}
		File out = new File(baseDir, sb.toString());
		out.getParentFile().mkdirs();
		try {
			return new FileOutputStream(out);
		} catch (FileNotFoundException ex) {
			logger.error("Cannot create FileOutputStream for " + out.getAbsolutePath(), ex);
			return null;
		}
	}
}
