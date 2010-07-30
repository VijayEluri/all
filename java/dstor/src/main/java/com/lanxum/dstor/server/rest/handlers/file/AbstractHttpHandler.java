package com.lanxum.dstor.server.rest.handlers.file;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.lanxum.dstor.server.db.DbService;
import com.lanxum.dstor.server.rest.JSONHelper;
import com.lanxum.dstor.server.rest.handlers.HttpHandler;
import com.lanxum.dstor.server.store.FileStore;

abstract class AbstractHttpHandler implements HttpHandler
{
	private static final Logger logger = Logger.getLogger(AbstractHttpHandler.class.getName());

	protected FileStore fileStore;
	protected DbService dbService;

	public void setFileStore(FileStore fileStore)
	{
		this.fileStore = fileStore;
	}

	public void setDbService(DbService dbService)
	{
		this.dbService = dbService;
	}
	
	protected long getId(String target, HttpServletResponse response)
	{
		String strId = target.substring(1);
		if (strId == null) {
			logger.error("no id parameter");
			JSONHelper.respondError(response, "no id parameter");
			return -1;
		}

		long id = -1;

		try {
			id = Long.parseLong(strId);
		} catch (Exception e) {
		}

		if (id < 0) {
			logger.error("id error");
			JSONHelper.respondError(response, "id error");
			return -1;
		}
		
		return id;
	}

}
