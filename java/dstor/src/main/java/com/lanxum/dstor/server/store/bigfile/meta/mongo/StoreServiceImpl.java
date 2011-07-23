package com.lanxum.dstor.server.store.bigfile.meta.mongo;

import com.lanxum.dstor.server.store.bigfile.meta.Store;
import com.lanxum.dstor.server.store.bigfile.meta.StoreService;
import com.lanxum.dstor.util.C;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class StoreServiceImpl implements StoreService {

	private DB db;

	public void setDb(DB db) {
		this.db = db;
	}

	private volatile DBCollection col;

	private DBCollection getCol() {
		if (col == null) {
			synchronized (this) {
				if (col == null) {
					DBCollection r = db.getCollection(C.COL_BIGFILE_STORES);
					col = r;
				}
			}
		}
		return col;
	}

    public Store findNextStore(long storeSize, long fileSize) {
        DBCursor cursor = getCol().find();
        while(cursor.hasNext()) {
            DBObject obj = cursor.next();
        }
        return null;
    }
    
}
