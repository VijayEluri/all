package com.oldhu.fileserver.db.je;

import com.oldhu.fileserver.db.Db;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Sequence;
import com.sleepycat.je.SequenceConfig;
import com.sleepycat.je.StatsConfig;
import java.io.File;
import java.io.UnsupportedEncodingException;
import org.apache.log4j.Logger;

public class JeDb implements Db
{

    private static final Logger logger = Logger.getLogger(JeDb.class);
    private Environment env;
    private Database db;
    private Sequence sequence;

	private String dbPath;
	
	public void setDbPath(String dbPath)
	{
		this.dbPath = dbPath;
	}

    private Environment getEnvironment() throws DatabaseException
    {
        if (env == null) {
            EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setTransactional(true);
            envConfig.setAllowCreate(true);
            env = new Environment(new File(dbPath), envConfig);
        }
        return env;
    }

    private Database getDb() throws DatabaseException
    {
        if (db == null) {
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setAllowCreate(true);

            db = getEnvironment().openDatabase(null, "fileserver", dbConfig);
        }
        return db;
    }

    private Sequence getSequence() throws UnsupportedEncodingException, DatabaseException
    {
        if (sequence == null) {
            DatabaseEntry theKey = new DatabaseEntry("_sequence".getBytes("UTF-8"));
            SequenceConfig config = new SequenceConfig();
            config.setCacheSize(100);
            config.setAllowCreate(true);
            sequence = getDb().openSequence(null, theKey, config);

            logger.info(sequence.getStats(StatsConfig.DEFAULT));
        }
        return sequence;
    }

    @Override
    public long nextId() throws Exception
    {
        try {
            long id = getSequence().get(null, 1);
            return id;
        } catch (Exception ex) {
            logger.error("Exception while getting next id", ex);
            throw ex;
        }
    }

    @Override
    public void close()
    {
        try {
            if (sequence != null) {
                sequence.close();
            }
            if (db != null) {
                db.close();
            }
            if (env != null) {
                env.cleanLog();
                env.close();
            }
        } catch (DatabaseException ex) {
            logger.error("Exception while closing env of JeDb", ex);
        }
    }

	@Override
	public void saveObject(long id, Object object) throws Exception
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
}

