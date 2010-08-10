package com.lets.filepro;

import java.io.File;
import java.util.LinkedList;

public class SplitterObject {
	public final static int MAX_PROPERTY_COUNT = 20;
	public final static int MAX_PROPERTY_LENGTH = 100;
	public final static int MAX_CONTENT_LENGTH = 100  * 1024; // 100k

	public final byte[][] properties = new byte[MAX_PROPERTY_COUNT][MAX_PROPERTY_LENGTH];
	public final byte[] content = new byte[MAX_CONTENT_LENGTH];
	public int propertyCount;
	public int contentLength;
	public File file;

	public final static LinkedList pool = new LinkedList();

	public static SplitterObject getObject() {
		synchronized (pool) {
			if (pool.isEmpty())
				return new SplitterObject();
			else
				return (SplitterObject) pool.removeFirst();
		}
	}

	public static void releaseObject(SplitterObject v) {
		if (v != null) {
			v.contentLength = 0;
			v.propertyCount = 0;
			synchronized (pool) {
				pool.addLast(v);
			}
		}
	}
}
