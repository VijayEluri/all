package com.oldhu.cm.server.services.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.oldhu.cm.common.domain.ContentStream;
import com.oldhu.cm.common.domain.Document;
import com.oldhu.cm.common.domain.Folder;
import com.oldhu.cm.common.services.ObjectService;
import com.oldhu.cm.server.persistence.MongoDb;

public class ObjectServiceImpl implements ObjectService
{
	private static Log log = LogFactory.getLog(ObjectServiceImpl.class);
	
	@Override
	public String createDocument(String type, Document document, String folderId, ContentStream contentStream)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Folder createFolder(String folderName, String fullPath)
	{
		Folder folder = new Folder();
		folder.setFolderName(folderName);
		folder.setFullPath(fullPath);
		try {
			MongoDb.getInstance().saveFolder(folder);
		} catch (Exception e) {
			log.fatal("Error while creating folder", e);
			return null;
		}
		return folder;
	}

	@Override
	public void createPolicy()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void createRelationship()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteContentStream()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteObject()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteTree()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void getAllowableActions()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void getContentStream()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void getProperties()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void getPropertiesOfLatestVersion()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void moveObject()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setContentStream()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateProperties()
	{
		// TODO Auto-generated method stub

	}

}
