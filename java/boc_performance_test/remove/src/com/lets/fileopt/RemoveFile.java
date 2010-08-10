package com.lets.fileopt;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Random;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;

public class RemoveFile implements Runnable {
	private final static boolean QueryTest = false;
	public final static int PROCESS_COUNT_PER_TICK = 8;
	public final static int THREAD_SLEEP = 50;

	public static boolean USE_TRANS = false;
	public static int FILE_COUNT_TRANS = 199;
	public static boolean USE_BATCH = false;

	public static Object END_SIGNAL = new Object();

	private final LinkedList queue;

	private static int THREAD_ID = 0;
	private int threadId = THREAD_ID++;

	public RemoveFile(LinkedList queue) {
		this.queue = queue;
	}

	public Object getFileObject() throws InterruptedException {
		synchronized (queue) {
			while (queue.isEmpty()) {
				queue.notifyAll();
				queue.wait();
			}
			Object v = queue.removeFirst();
			queue.notifyAll();
			return v;
		}
	}

	public int getFileObject(Object[] objList) throws InterruptedException {
		synchronized (queue) {
			while (queue.isEmpty()) {
				queue.notifyAll();
				queue.wait();
			}
			int i = 0;
			while (i < objList.length && !queue.isEmpty()) {
				Object o = queue.removeFirst();
				objList[i] = o;
				i++;
				if (o == END_SIGNAL)
					break;
			}
			queue.notifyAll();
			return i;
		}
	}

	private boolean DoRemoveFileObj(IDfSession session, FileObj file)
			throws DfException {
		if (!file.FileID.equals("")) {
			IDfSysObject obj = (IDfSysObject) session.getObject(FileProcessor.clientX
					.getId(file.FileID));
			String contentId = obj.getContentsId().getId();
			obj.destroy();
			String cmd = contentId + ",DESTROY_CONTENT";
			String query = session.apiGet("apply", cmd);
			session.apiExec("close", query);
			System.err.println("destroy: " + file.FileID + ", " + contentId);
			return true;
		} else {
			return false;
		}
	}

	public void run() {
		IDfSession session = null;

		try {
			session = FileProcessor.sessionManager
					.newSession(FileProcessor.DOCBASE);

			long fcount = 0;
			Random seed = new Random();
			int countInTrans = 0;
			boolean thereIsATrans = false;
			Object[] objs = new Object[PROCESS_COUNT_PER_TICK];

			long st = System.currentTimeMillis();

			outter: do {
				if (QueryTest) {
					Object o = getFileObject();
					fcount++;
					FileObj.releaseObject((FileObj) o);
				} else if (USE_BATCH) {
					int c = getFileObject(objs);
					for (int i = 0; i < c; i++) {
						Object o = objs[i];
						if (o == END_SIGNAL)
							break outter;

						if (USE_TRANS && thereIsATrans
								&& (countInTrans >= FILE_COUNT_TRANS)) {
							session.commitTrans();
							thereIsATrans = false;
						}

						if (USE_TRANS && !thereIsATrans) {
							countInTrans = 0;
							thereIsATrans = true;
							session.beginTrans();
						}

						if (DoRemoveFileObj(session, (FileObj) o)) {
							Thread.sleep(THREAD_SLEEP
									+ seed.nextInt(THREAD_SLEEP));

							FileObj.releaseObject((FileObj) o);
							countInTrans++;
							fcount++;
						}
					}
				} else {
					Object o = getFileObject();
					if (o == END_SIGNAL)
						break outter;

					if (USE_TRANS && thereIsATrans
							&& (countInTrans >= FILE_COUNT_TRANS)) {
						session.commitTrans();
						thereIsATrans = false;
					}

					if (USE_TRANS && !thereIsATrans) {
						countInTrans = 0;
						thereIsATrans = true;
						session.beginTrans();
					}

					if (DoRemoveFileObj(session, (FileObj) o)) {
						Thread.sleep(THREAD_SLEEP + seed.nextInt(THREAD_SLEEP));

						FileObj.releaseObject((FileObj) o);
						countInTrans++;
						fcount++;
					}
				}
			} while (true);

			if (thereIsATrans) {
				session.commitTrans();
			}

			long et = System.currentTimeMillis();

			FileOutputStream fos = new FileOutputStream("/tmp/threadlog_"
					+ String.valueOf(FileProcessor.processId) + "_"
					+ String.valueOf(this.threadId) + ".log");
			PrintWriter pw = new PrintWriter(fos);
			try {
				pw.println(FileProcessor.getDateTime(st));
				pw.println(FileProcessor.getDateTime(et));
				pw.println("deleted totally " + fcount + " files");
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
