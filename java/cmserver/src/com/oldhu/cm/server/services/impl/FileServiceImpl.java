package com.oldhu.cm.server.services.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.oldhu.cm.common.services.FileService;

public class FileServiceImpl implements FileService
{
	private static Log log = LogFactory.getLog(FileServiceImpl.class);

	@Override
	public InputStream retrieve(long id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long store(InputStream input)
	{
		try {
			byte[] buffer = new byte[8192];
			FileOutputStream os = new FileOutputStream("/tmp/t.file");
			while(true) {
				int n = input.read(buffer);
				os.write(buffer);
				if (n < 8192) {
					break;
				}
			}
		} catch (Exception e) {
			log.fatal("error", e);
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				log.fatal("error", e);
			}
		}
		return 0;
	}

}
