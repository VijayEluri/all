package com.lanxum.dstor.server.db.mongo;

import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import org.apache.log4j.Logger;

public class Connection
{

	private static final Logger logger = Logger.getLogger(Connection.class.getName());

	private Mongo mongo;
	private DB db;

	public Connection(String dbName, String host, int port, int poolSize) throws Exception
	{
		try {
			DBAddress addr = new DBAddress(host + ":" + port);
			MongoOptions options = new MongoOptions();
			options.connectionsPerHost = poolSize;
			mongo = new Mongo(addr, options);
			db = mongo.getDB(dbName);
		} catch (Exception ex) {
			logger.fatal("cannot connect to mongodb", ex);
			throw ex;
		}

	}

	public DB getDb()
	{
		return db;
	}
}
