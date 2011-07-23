package com.lanxum.dstor.server.db.mongo;

import java.util.Map;

import com.lanxum.dstor.server.db.DbService;
import com.lanxum.dstor.server.db.FileObject;
import com.lanxum.dstor.util.C;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class DbServiceImpl implements DbService {
	private DB db;

	public void setDb(DB db) {
		this.db = db;
	}

	private volatile DBCollection col;

	private DBCollection getCol() {
		if (col == null) {
			synchronized (this) {
				if (col == null) {
					DBCollection r = db.getCollection(C.COL_FILES);
					col = r;
				}
			}
		}
		return col;
	}

	// TODO: create date, update date
	public void save(long id, FileObject fobj) {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start()
			.append(C.FIELD_ID, id)
			.append(C.FIELD_FILES_URI, fobj.getURI())
			.append(C.FIELD_FILES_SIZE, fobj.getFileSize())
			.append(C.FIELD_FILES_NAME, fobj.getFileName())
			.append(C.FIELD_FILES_HASH, fobj.getMd5Sum());
		
		Map<String, Object> metaData = fobj.getMetaData();
		
		if (metaData != null) {
			for (String key: metaData.keySet()) {
				builder.append(key, metaData.get(key));
			}
		}
		
		DBObject obj = builder.get();
		getCol().save(obj);
	}

	public FileObject find(long id) {
		DBObject obj = getCol().findOne(new BasicDBObject(C.FIELD_ID, id));
		if (obj == null) return null;

		FileObject fobj = new FileObject();
		fobj.setFileName(obj.get(C.FIELD_FILES_NAME).toString());
		fobj.setFileSize(Long.parseLong(obj.get(C.FIELD_FILES_SIZE).toString()));
		fobj.setURI(obj.get(C.FIELD_FILES_URI).toString());
		fobj.setMd5Sum(obj.get(C.FIELD_FILES_HASH).toString());

		return fobj;
	}

	public void remove(long id) {
		getCol().remove(new BasicDBObject(C.FIELD_ID, id));
	}

}
