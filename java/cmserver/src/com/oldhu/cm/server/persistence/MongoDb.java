package com.oldhu.cm.server.persistence;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.ObjectId;
import com.oldhu.cm.common.domain.Folder;

public class MongoDb
{
	static private MongoDb instance;

	static public MongoDb getInstance()
	{
		if (instance == null) {
			instance = new MongoDb();
		}
		return instance;
	}

	private MongoDb()
	{
	}

	private Mongo getMongo() throws UnknownHostException, MongoException
	{
		return new Mongo("localhost");
	}

	private DB db;

	public DB getDb() throws Exception
	{
		if (db == null) {
			db = getMongo().getDB("cm");
		}
		return db;
	}
		
	public void removeAllFolders() throws Exception 
	{
		DBCollection coll = getFolderCollection();
		coll.drop();
	}
	
	public void saveFolder(Folder folder) throws Exception 
	{
		if (folder.getFolderName() == null) {
			throw new Exception("Need folder name to save a folder");
		}
		
		
		BasicDBObject doc = new BasicDBObject();
		if (folder.getFolderId() != null) {
			doc.put("_id", new ObjectId(folder.getFolderId()));
		}
		
		doc.put("name", folder.getFolderName());
		
		if (folder.getFullPath() != null) {
			String[] folders = folder.getFullPath().split("/");
			ArrayList<String> folderArray = new ArrayList<String>();
			for (String s: folders) {
				if (!s.isEmpty()) {
					folderArray.add(s);
				}
			}
			doc.put("full_path", folderArray);
		}
		
		DBCollection coll = getFolderCollection();
		coll.save(doc);
		
		folder.setFolderId(doc.getString("_id"));
	}
	
	public Folder getFolder(String _folderId) throws Exception
	{
		DBCollection coll = getFolderCollection();
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(_folderId));
        BasicDBObject result = (BasicDBObject) coll.findOne(query);
        if (result == null) return null;
		Folder folder = new Folder();
		folder.setFolderId(_folderId);
		folder.setFolderName(result.getString("name"));
		
		BasicDBList paths = (BasicDBList) result.get("full_path");
		Iterator<String> pathKeys = paths.keySet().iterator();
		StringBuilder builder = new StringBuilder();
		while(pathKeys.hasNext()) {
			builder.append('/');
			builder.append(paths.get(pathKeys.next()));
		}
		
		folder.setFullPath(builder.toString());
		
		return folder;
	}

	private DBCollection getFolderCollection() throws Exception
	{
		DB db = getDb();
		DBCollection coll = db.getCollection("folder");
		return coll;
	}
}
