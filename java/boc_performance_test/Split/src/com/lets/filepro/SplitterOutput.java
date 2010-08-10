package com.lets.filepro;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

import com.documentum.com.*;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfLoginInfo;
import com.documentum.fc.common.IDfTime;

public class SplitterOutput implements Runnable {
	public final static int PROCESS_COUNT_PER_TICK = 8;
	public final static int FILE_COUNT_TRANS = 199;
	public final static int THREAD_SLEEP = 50;

	// --
	private final static String DOCBASE = "POC";
	private final static String USERNAME = "dmadmin";
	private final static String PASSWORD = "dmadmin";
	
	

	private static IDfClientX clientX = new DfClientX();
	private static IDfSessionManager sessionManager = null;

	public static Object END_SIGNAL = new Object();
	private final LinkedList queue;
	private final String CABINET;
	private long objID = 0;

	private static int THREAD_ID = 0;
	private int threadId = THREAD_ID++;
	
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

	public SplitterOutput(LinkedList queue, String cabinet) {
		this.queue = queue;
		this.CABINET = cabinet;
	}
	
	public Object getObject() throws InterruptedException {
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

	public int getObject(Object[] obj) throws InterruptedException {
		synchronized (queue) {
			while (queue.isEmpty()) {
				queue.notifyAll();
				queue.wait();
			}
			int i = 0;
			while (i < obj.length && !queue.isEmpty()) {
				Object o = queue.removeFirst();
				obj[i] = o;
				i++;
				if (o == END_SIGNAL)
					break;
			}
			queue.notifyAll();
			return i;
		}
	}

	private IDfFolder getFolder(IDfSession session) throws DfException {
		IDfFolder folder = session.getFolderByPath("/" + CABINET);
		if (folder == null) {
			folder = (IDfFolder) session.newObject("dm_cabinet");
			folder.setObjectName(CABINET);
			folder.save();
		}
		return folder;
	}

	private static int strLen(byte[] str) {
		for (int i = 0; i < str.length; i++)
			if (str[i] == 0)
				return i;
		return str.length;
	}

	private static String replaceMinus(String s) {
		StringBuffer sb = new StringBuffer(s.length());
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c != '-')
				sb.append(c);
		}
		return sb.toString();
	}

	private void importStream(IDfSession session, IDfFolder folder,
			SplitterObject o) throws DfException {
		String type = o.file.getName().substring(0, 8).toLowerCase();
		IDfSysObject sysobj = (IDfSysObject) session.newObject(type);

		sysobj.setContentType("crtext");
		ByteArrayOutputStream baos = new ByteArrayOutputStream(o.contentLength);
		baos.write(o.content, 0, o.contentLength);
		
		sysobj.setContent(baos);

		sysobj.setObjectName(Integer.toString(threadId) + "_"
				+ Long.toString(objID)); // auto-generated
		objID++;

		/*
		 * set other properties
		 */
		for (int i = 2; i < o.propertyCount; i++) {
			String str = new String(o.properties[i], 0, strLen(o.properties[i]));
			String[] sa = str.split(":", 2);
			if (sa.length != 2)
				continue;
			String name = replaceMinus(sa[0]).toLowerCase();
			String value = sa[1];
			if (name.equals("dat")) {
				IDfTime time = clientX.getTime(value, "yyyy/mm/dd");
				sysobj.setTime(name, time);
			} else
				sysobj.setString(name, value);
		}

		sysobj.link(folder.getObjectId().toString());

		sysobj.save();
	}

	private static String getDateTime(long t) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(t);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setCalendar(c);
		return sdf.format(new Date(t));
	}

	public void run() {
		IDfSession session = null;

		try {
			session = sessionManager.newSession(DOCBASE);
			IDfFolder folder = getFolder(session);

			boolean thereIsATrans = false;
			int countInTrans = 0;
			Random key = new Random();

			long fcount = 0;
			long sumLen = 0;
			long st = System.currentTimeMillis();

			Object[] objs = new Object[PROCESS_COUNT_PER_TICK];

			outter: do {
				int c = getObject(objs);
				for (int i = 0; i < c; i++) {
					Object o = objs[i];
					if (o == END_SIGNAL)
						break outter;
					
					if (thereIsATrans && (countInTrans >= FILE_COUNT_TRANS)) {
						session.commitTrans();
						thereIsATrans = false;
					}
					
					if (!thereIsATrans) {
						countInTrans = 0;
						thereIsATrans = true;
						session.beginTrans();
					}

					importStream(session, folder, (SplitterObject) o);
					Thread.sleep(THREAD_SLEEP + key.nextInt(THREAD_SLEEP));
					sumLen += ((SplitterObject) o).contentLength;
					SplitterObject.releaseObject((SplitterObject) o);
					countInTrans++;
					fcount++;
				}
			} while (true);

			if (thereIsATrans) {
				session.commitTrans();
			}
			long et = System.currentTimeMillis();

			DecimalFormat df = new DecimalFormat("000");
			FileOutputStream fos = new FileOutputStream("threadlog_"
					+ df.format(Loader.ProcessID) + "_"
					+ df.format(this.threadId) + ".log");
			PrintWriter pw = new PrintWriter(fos);
			try {
				pw.println(getDateTime(st));
				pw.println(getDateTime(et));
				pw.println("uploaded totally " + sumLen + " bytes");
				pw.println("uploaded totally " + fcount + " files");
			} finally {
				pw.close();
			}

		} catch (DfException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (session != null)
				sessionManager.release(session);
		}
	}
}
