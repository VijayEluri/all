package com.oldhu.cm.test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import junit.framework.TestCase;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

public class TestFileService extends TestCase
{
	public void testStore() throws Throwable
	{
		String url = "http://localhost:8080/api/file-service";
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");
		
		HessianOutput out = new HessianOutput(conn.getOutputStream());
		out.startCall("store", 0);
		out.completeCall();
		out.writeByteBufferStart();
		InputStream is = new FileInputStream("/tmp/2.file");
		byte[] buffer = new byte[8192];
		while (true) {
			int n = is.read(buffer);
			out.writeBytes(buffer);
			if (n < 8192) {
				break;
			}
		}
//		out.writeObject(is);
		is.close();
		
		is = conn.getInputStream();
		HessianInput in = new HessianInput(is);
		in.startReply();
		long r = in.readLong();
		in.completeReply();
	}
}
