package com.lanxum.dstor.server.db.mongo;

import com.lanxum.dstor.server.db.IdGenerator;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBObject;

public class IdGeneratorImpl implements IdGenerator
{

	private DB db;

	public void setDb(DB db)
	{
		this.db = db;
	}

	public long getNextId() throws Exception
	{
		DBObject updateObj = new BasicDBObject("$inc", new BasicDBObject("val", 1L));
		DBObject queryObj = new BasicDBObject("_id", "oid");
		DBObject cmd = BasicDBObjectBuilder.start().append("findAndModify", "ids").append("query", queryObj)
				.append("update", updateObj).append("upsert", true).append("new", true).get();
		DBObject result = db.command(cmd);
		String ok = result.get("ok").toString();
		if (ok.equals("1.0")) {
			DBObject value = (DBObject) result.get("value");
			return Long.parseLong(value.get("val").toString());
		}
		throw new Exception("findAndModify error: " + result.toString());
	}
}
