package net.everythingandroid.smspopup.filter;

import java.util.ArrayList;
import java.util.List;

import net.everythingandroid.smspopup.Log;
import net.everythingandroid.smspopup.SmsMmsMessage;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FilterDbAdapter {

	public static final String KEY_ROWID = "_id";
	private static final String FILTERED_MESSAGES_DB_TABLE = "filtered";
	public static final String KEY_NUMBER = "number";
	public static final String KEY_RECEIVED = "received";
	public static final String KEY_BODY = "body";

	private static final String FILTER_KEYWORDS_DB_TABLE = "keywords";
	public static final String KEY_WORD = "word";

	private static final String DATABASE_NAME = "data";
	private static final int DATABASE_VERSION = 1;

	private static final String FILTERED_DB_CREATE = "create table " + FILTERED_MESSAGES_DB_TABLE + " (" + KEY_ROWID
		+ " integer primary key autoincrement, " + KEY_NUMBER + " text, " + KEY_RECEIVED + " integer, " + KEY_BODY
		+ " text);";
	
	private static final String FILTER_KEYWORDS_DB_CREATE = "create table " + FILTER_KEYWORDS_DB_TABLE + " (" + KEY_ROWID
		+ " integer primary key autoincrement, " + KEY_WORD + " text);";

	private final Context context;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			if (Log.DEBUG)
				Log.v("FilterDbAdapter: Creating Database");
			db.execSQL(FILTERED_DB_CREATE);
			db.execSQL(FILTER_KEYWORDS_DB_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Log.w(TAG, "Upgrading database from version " + oldVersion +
			// " to "
			// + newVersion + ", which will destroy all old data");
			if (Log.DEBUG)
				Log.v("FilterDbAdapter: Upgrading Database");
			onCreate(db);
		}
	}

	public FilterDbAdapter(Context _context)
	{
		this.context = _context;
		mDbHelper = null;
		mDb = null;
	}
	
	public FilterDbAdapter open() throws SQLException
	{
		return open(false);
	}

	public FilterDbAdapter open(boolean readOnly) throws SQLException
	{
		if (mDbHelper == null) {
			if (Log.DEBUG)
				Log.v("Opened database");
			mDbHelper = new DatabaseHelper(context);
			if (readOnly) {
				mDb = mDbHelper.getReadableDatabase();
			} else {
				mDb = mDbHelper.getWritableDatabase();
			}
		}
		return this;
	}

	public void close()
	{
		if (mDbHelper != null) {
			if (Log.DEBUG)
				Log.v("Closed database");
			mDbHelper.close();
			mDbHelper = null;
		}
	}


	public void createFilteredMessage(SmsMmsMessage message)
	{
		ContentValues vals = new ContentValues();
		vals.put(KEY_NUMBER, message.getContactName());
		vals.put(KEY_RECEIVED, message.getTimestamp());
		vals.put(KEY_BODY, message.getMessageBody());
		if (Log.DEBUG)
			Log.v("Filtered message: " + message.getContactName() + ", " + message.getFormattedTimestamp().toString()
					+ ", " + message.getMessageBody());
		long id = mDb.insert(FILTERED_MESSAGES_DB_TABLE, null, vals);
		if (Log.DEBUG)
			Log.v("Filterd message id " + id);
	}

	public Cursor fetchAllFilteredMessage()
	{
		String[] cols = new String[] { KEY_ROWID, KEY_NUMBER, KEY_RECEIVED, KEY_BODY };
		return mDb.query(FILTERED_MESSAGES_DB_TABLE, cols, null, null, null, null, KEY_ROWID + " DESC");
	}
	
	public void deleteAllFilteredMessage()
	{
		mDb.delete(FILTERED_MESSAGES_DB_TABLE, null, null);
	}
	
	public void deleteFilteredMessage(long id)
	{
		mDb.delete(FILTERED_MESSAGES_DB_TABLE, KEY_ROWID + "=" + id, null);
	}
	
	public FilteredMessage findFilteredMessageById(long id)
	{
		FilteredMessage msg = new FilteredMessage();
		String[] cols = new String[] { KEY_ROWID, KEY_NUMBER, KEY_RECEIVED, KEY_BODY };
		Cursor c = mDb.query(FILTERED_MESSAGES_DB_TABLE, cols, KEY_ROWID + "=" + id, null, null, null, null);
		if (c.moveToFirst()) {
			msg.setNumber(c.getString(1));
			msg.setReceived(c.getString(2));
			msg.setBody(c.getString(3));
		}
		c.close();
		return msg;
	}
	
	public List<String> fetchKeywordsAsList()
	{
		List<String> result = new ArrayList<String>();
		String[] cols = new String[] { KEY_ROWID, KEY_WORD };
		Cursor c = mDb.query(FILTER_KEYWORDS_DB_TABLE, cols, null, null, null, null, null);
		while (c.moveToNext()) {
			result.add(c.getString(1));
		}
		c.close();
		return result;
	}
	
	public Cursor fetchKeywords()
	{
		String[] cols = new String[] { KEY_ROWID, KEY_WORD };
		Cursor c = mDb.query(FILTER_KEYWORDS_DB_TABLE, cols, null, null, null, null, KEY_ROWID + " DESC");
		return c;
	}
	
	public void insertKeyword(String keyword)
	{
		ContentValues vals = new ContentValues();
		vals.put(KEY_WORD, keyword);
		mDb.insert(FILTER_KEYWORDS_DB_TABLE, null, vals);
	}
	
	public void deleteKeyword(long id)
	{
		mDb.delete(FILTER_KEYWORDS_DB_TABLE, KEY_ROWID + "=" + id, null);
	}
	
	public void insertKeywords(String keywords)
	{
		for (String line : keywords.split("\n")) {
			for (String word : line.split(" |,|;")) {
				word = word.trim();
				if (word.length() > 0)
					insertKeyword(word);
			}
		}
	}
}
