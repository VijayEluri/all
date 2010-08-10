package com.lets.fileopt;

import java.util.LinkedList;

public class FileObj {
	public final static LinkedList pool = new LinkedList();
	
	public String FileID = "";
	
	public static FileObj getObject() {
		synchronized (pool) {
			if (pool.isEmpty())
				return new FileObj();
			else
				return (FileObj) pool.removeFirst();
		}
	}

	public static void releaseObject(FileObj v) {
		if (v != null) {			
			synchronized (pool) {
				v.FileID = "";
				pool.addLast(v);
			}
		}
	}
}
