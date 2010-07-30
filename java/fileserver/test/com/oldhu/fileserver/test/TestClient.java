package com.oldhu.fileserver.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestClient
{

	public TestClient()
	{
	}

	@BeforeClass
	public static void setUpClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownClass() throws Exception
	{
	}
	
	private Socket socket;
	private OutputStream os;
	private InputStream is;
	
	@Before
	public void setUp() throws Exception
	{
		socket = new Socket("localhost", 8517);
		os = socket.getOutputStream();
		is = socket.getInputStream();
	}

	@After
	public void tearDown() throws Exception
	{
		Thread.sleep(1000);
		is.close();
		os.close();
		socket.close();
	}

	private void writeLong(long value) throws IOException
	{
		DataOutputStream dos = new DataOutputStream(os);
		dos.writeLong(value);
	}

	private void checkSaveResult() throws IOException
	{
		char c = (char) is.read();
		assert(c == 'O');
	}

	@Test
	public void testSave() throws Exception
	{
		File file = new File("/Users/h/src/tcutil.c");
		long length = file.length();
		InputStream fis = new FileInputStream(file);
		byte[] buf = new byte[4096];

		os.write('S');
		writeLong(1L);
		writeLong(0L);
		writeLong(length);
		while (true) {
			int c = fis.read(buf);
			if (c <= 0) {
				break;
			}
			os.write(buf, 0, c);
		}
		os.flush();
		checkSaveResult();
		fis.close();
	}

	@Test
	public void testCreate() throws IOException
	{
		os.write('C');
		os.flush();

		DataInputStream dis = new DataInputStream(is);

		int r = dis.read();
		assert(r == 'O');

		long id1 = dis.readLong();

		os.write('C');
		os.flush();

		r = dis.read();
		assert(r == 'O');
		long id2 = dis.readLong();

		System.out.println("id1: " + id1);
		System.out.println("id2: " + id2);
	}
}
