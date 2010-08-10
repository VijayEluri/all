package com.lets.fileopt;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;

public class FileProcessor {
	public static final int MAX_REMOVE_THREAD_COUNT = 100;
	// FileProcessorMode
	public static final int FPM_Normal = 0;
	public static final int FPM_Query = 1;
	public static final int FPM_Remove = 2;
	public static final int FPM_FileTask = 3;

	// session info
	private final static String USERNAME = "dmadmin";
	private final static String PASSWORD = "dmadmin";
	public final static String DOCBASE = "POC";

	public static String CABINET = "MyCabinet3";
	public static boolean OUT_PROCESS_LOG = true;

	// doc
	public static IDfClientX clientX = new DfClientX();
	public static IDfSessionManager sessionManager = null;
	public static int processId = 0;

	// self
	private Thread[] queryThreads;
	private Thread[] removeThreads;
	private LinkedList queryQueue = new LinkedList();
	private LinkedList removeQueue = new LinkedList();

	static {
		IDfClient client;
		try {
			client = clientX.getLocalClient();
			sessionManager = client.newSessionManager();
			IDfLoginInfo loginInfo = clientX.getLoginInfo();
			loginInfo.setUser(USERNAME);
			loginInfo.setPassword(PASSWORD);
			sessionManager.setIdentity(DOCBASE, loginInfo);
		} catch (DfException e) {
			throw new RuntimeException(e);
		}
	}

	private Runnable queryRunner = new Runnable() {
		public void run() {
			do {
				Runnable query;
				synchronized (queryQueue) {
					if (queryQueue.isEmpty())
						query = null;
					else
						query = (Runnable) queryQueue.removeFirst();
				}
				if (query == null)
					break;
				query.run();
			} while (true);
		}
	};

	public FileProcessor(String queryStr, int removeThreadCount,
			long startPosition, long count, long interval) {
		removeThreadCount = Math.max(1, Math.min(MAX_REMOVE_THREAD_COUNT,
				removeThreadCount));
		removeThreads = new Thread[removeThreadCount];
		queryThreads = new Thread[1];
		queryQueue.add(new QueryFile(removeQueue, queryStr, startPosition,
				count, interval));
	}

	private void createQueryThreads() {
		for (int i = 0; i < queryThreads.length; i++)
			queryThreads[i] = new Thread(queryRunner);
	}

	private void startQueryThreads() {
		for (int i = 0; i < queryThreads.length; i++)
			queryThreads[i].start();
	}

	private void createRemoveThreads() {
		for (int i = 0; i < removeThreads.length; i++)
			removeThreads[i] = new Thread(new RemoveFile(removeQueue));
	}

	private void startRemoveThreads() {
		for (int i = 0; i < removeThreads.length; i++)
			removeThreads[i].start();
	}

	private void waitForQueryEnd() throws InterruptedException {
		for (int i = 0; i < queryThreads.length; i++)
			if (queryThreads[i] != null)
				queryThreads[i].join();
	}

	private void addEndSignal() throws InterruptedException {
		synchronized (removeQueue) {
			for (int i = 0; i < removeThreads.length; i++)
				removeQueue.addLast(RemoveFile.END_SIGNAL);

			removeQueue.notifyAll();
		}
	}

	private void waitForRemoveEnd() throws InterruptedException {
		for (int i = 0; i < removeThreads.length; i++)
			if (removeThreads[i] != null)
				removeThreads[i].join();
	}

	public void run() throws InterruptedException {
		createQueryThreads();
		createRemoveThreads();
		startRemoveThreads();
		startQueryThreads();
		waitForQueryEnd();
		addEndSignal();
		waitForRemoveEnd();
	}

	public static String getDateTime(long t) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(t);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setCalendar(c);
		return sdf.format(new Date(t));
	}

	private static void PrintUsage() {
		System.out
				.println("usage: mode queryStr cabinet removeThreadCount qLimited usingTrans transCount usingBatch outLog");
		System.out.println("   queryStr          --- query file condition");
		System.out
				.println("   removeThreadCount --- the count of threads would remove file");
		System.out.println("   startPosition     --- start position");
		System.out.println("   count             --- remove count");
		System.out.println("   interval          --- interval");
		System.out.println("   processid         --- process id");
		System.out
				.println("   usingTrans        --- whether use transaction to reomve file, 1 for using");
		System.out
				.println("   transCount        --- the count of trans, -1 for default");
		System.out
				.println("   usingBatch        --- whether use batch to get file from queue, 1 for using");
		System.out
				.println("   outLog            --- whether output log, 1 for true");
	}

	public static void main(String[] args) throws InterruptedException {
		int threadCount = 1;
		long startPosition = 0;
		long count = Long.MAX_VALUE;
		long interval = 0;

		if (args.length == 1) {
			threadCount = 2;
		} else if (args.length == 2) {
			try {
				threadCount = Integer.parseInt(args[1]);
			} catch (Exception e) {
				PrintUsage();
				return;
			}
		} else if (args.length == 6) {
			try {
				threadCount = Integer.parseInt(args[1]);
				startPosition = Long.parseLong(args[2]);
				count = Long.parseLong(args[3]);
				interval = Long.parseLong(args[4]);
				processId = Integer.parseInt(args[5]);
			} catch (Exception e) {
				PrintUsage();
				return;
			}
		} else if (args.length == 10) {
			try {
				threadCount = Integer.parseInt(args[1]);
				startPosition = Long.parseLong(args[2]);
				count = Long.parseLong(args[3]);
				interval = Long.parseLong(args[4]);
				processId = Integer.parseInt(args[5]);
				RemoveFile.USE_TRANS = Integer.parseInt(args[6]) == 1;
				RemoveFile.FILE_COUNT_TRANS = Integer.parseInt(args[7]);
				RemoveFile.USE_BATCH = Integer.parseInt(args[8]) == 1;
				FileProcessor.OUT_PROCESS_LOG = Integer.parseInt(args[9]) == 1;
			} catch (Exception e) {
				PrintUsage();
				return;
			}
		} else {
			PrintUsage();
			return;
		}

		FileProcessor pro = new FileProcessor(args[0], threadCount,
				startPosition, count, interval);
		pro.run();
	}
}
