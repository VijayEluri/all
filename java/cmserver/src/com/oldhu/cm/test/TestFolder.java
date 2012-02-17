package com.oldhu.cm.test;

import com.oldhu.cm.common.domain.Folder;
import com.oldhu.cm.server.persistence.MongoDb;

import junit.framework.TestCase;

public class TestFolder extends TestCase
{
	public void testFolderCreationWithEmptyName() 
	{
		Folder folder = new Folder();
		boolean ok = false;
		try {
			MongoDb.getInstance().saveFolder(folder);
		} catch (Exception e) {
			ok = true;
		}
		assert(ok);
	}

	public void testFolderCreation() throws Exception 
	{
		Folder folder = new Folder();
		folder.setFolderName("new folder");
		folder.setFullPath("/test1/test2");
		
		MongoDb db = MongoDb.getInstance();
		db.removeAllFolders();
		db.saveFolder(folder);
		
		assertNotNull(folder.getFolderId());
		
		Folder folder2 = db.getFolder(folder.getFolderId());
		assert(folder.getFolderName().equals(folder2.getFolderName()));
		assert(folder.getFolderId().equals(folder2.getFolderId()));
		assert(folder2.getFullPath().equals("/test1/test2"));
	}
}
