package com.lanxum.dstor.server.db.mongo;

import com.lanxum.dstor.server.db.DbService;
import com.lanxum.dstor.server.db.FileObject;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class DbServiceImpl implements DbService
{
	private DB db;

	public void setDb(DB db)
	{
		this.db = db;
	}

	private volatile DBCollection col;

	private DBCollection getCol()
	{
		if (col == null) {
			synchronized (this) {
				if (col == null) {
					DBCollection r = db.getCollection("files");
					col = r;
				}
			}
		}
		return col;
	}

	public void save(long id, FileObject fobj)
	{
		DBObject obj = BasicDBObjectBuilder.start()
			.append("_id", id)
			.append("u", fobj.getURI())
			.append("s", fobj.getFileSize())
			.append("n", fobj.getFileName())
			.append("h", fobj.getMd5Sum())
			.get();
		getCol().save(obj);
	}

	public FileObject find(long id)
	{
		DBObject obj = getCol().findOne(new BasicDBObject("_id", id));
		if (obj == null) return null;
		FileObject fobj = new FileObject();
		fobj.setFileName(obj.get("n").toString());
		fobj.setFileSize(Long.parseLong(obj.get("s").toString()));
		fobj.setURI(obj.get("u").toString());
		fobj.setMd5Sum(obj.get("h").toString());
		return fobj;
	}

	public void remove(long id)
	{
		getCol().remove(new BasicDBObject("_id", id));
	}

}
