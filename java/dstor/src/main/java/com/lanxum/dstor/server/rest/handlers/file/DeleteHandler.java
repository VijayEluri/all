package com.lanxum.dstor.server.rest.handlers.file;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;

import com.lanxum.dstor.server.db.FileObject;
import com.lanxum.dstor.server.rest.JSONHelper;

public class DeleteHandler extends AbstractHttpHandler
{
	private static final Logger logger = Logger.getLogger(DeleteHandler.class.getName());

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
	{
		long id = getId(target, response);
		if (id < 0) return;
		
		FileObject fobj = dbService.find(id);
		if (fobj == null) {
			logger.info("cannot find content for id " + id);
			JSONHelper.respondNoContent(response);
			return;
		}
		
		dbService.remove(id);
		fileStore.removeByURI(fobj.getURI());
		JSONHelper.respondOk(response);
		
		logger.debug("object " + id + " deleted.");
	}

}
