package com.lets.fileopt;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.LinkedList;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

public class QueryFile implements Runnable {
	public final static int MAX_QUEUE_COUNT = 400;

	public final static String QUERY_FILEOBJ = "select r_object_id from ";
	public final static String OBJ_ID_KEY = "r_object_id";

	private final LinkedList queue;
	private final String queryStr;
	private final long startPosition;
	private final long count;
	private final long interval;

	public QueryFile(LinkedList queue, String query, long startPosition,
			long count, long interval) {
		this.queue = queue;
		this.queryStr = query;
		this.startPosition = startPosition;
		this.count = count;
		this.interval = interval;
	}

	public void putObject(FileObj file) throws InterruptedException {
		synchronized (queue) {
			while (queue.size() >= MAX_QUEUE_COUNT) {
				queue.notifyAll();
				queue.wait();
			}
			queue.addLast(file);
			queue.notifyAll();
		}
	}

	private boolean skip(IDfCollection objList, long count) throws DfException {
		for (long i = 0; i < count; i++)
			if (!objList.next())
				return false;
		return true;
	}

	private long putObjects(IDfCollection objList) throws DfException,
			InterruptedException {
		for (long i = 0; i < count; i++) {
			if (!objList.next())
				return i;

			FileObj file = FileObj.getObject();
			file.FileID = objList.getString(OBJ_ID_KEY);
			putObject(file);
		}
		return count;
	}

	public void run() {
		IDfSession session = null;

		try {
			session = FileProcessor.sessionManager
					.newSession(FileProcessor.DOCBASE);

			IDfQuery dfQuery = FileProcessor.clientX.getQuery();

			long qst = System.currentTimeMillis(); // for log

			dfQuery.setDQL(QUERY_FILEOBJ + this.queryStr);
			IDfCollection objList = dfQuery.execute(session,
					IDfQuery.DF_READ_QUERY);

			long qet = System.currentTimeMillis(); // for log
			
			Thread.sleep(10000); 

			long fcount = 0;

			if (skip(objList, startPosition))
				do {
					long c = putObjects(objList);
					fcount += c;
					if (c != count)
						break;
				} while (skip(objList, interval));

			long endt = System.currentTimeMillis(); // for log

			FileOutputStream fos = new FileOutputStream("/tmp/querythreadlog_"
					+ String.valueOf(FileProcessor.processId) + ".log");
			PrintWriter pw = new PrintWriter(fos);
			try {
				pw
						.println(FileProcessor.getDateTime(qst)
								+ "  start query: " + QUERY_FILEOBJ
								+ this.queryStr);
				pw.println(FileProcessor.getDateTime(qet)
						+ "  query finish.");
				pw.println(FileProcessor.getDateTime(endt)
						+ "  process finish.");
				pw.println("query totally " + fcount + " files");
			} finally {
				pw.close();
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (DfException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (session != null)
				FileProcessor.sessionManager.release(session);
		}
	}

}
