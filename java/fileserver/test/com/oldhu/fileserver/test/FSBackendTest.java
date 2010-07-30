package com.oldhu.fileserver.test;

import com.oldhu.fileserver.backend.FileSystemBackend;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.junit.Assert.*;

public class FSBackendTest
{

	public FSBackendTest()
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

	ApplicationContext ctx;

	@Before
	public void setUp()
	{
		ctx = new ClassPathXmlApplicationContext("beans.xml");
	}

	@After
	public void tearDown()
	{
	}

	@Test
	public void test1()
	{
		FileSystemBackend fsb = (FileSystemBackend) ctx.getBean("fileSystemBackend");
		fsb.getOutputStream(1234567890123456789L);
		fsb.getOutputStream(Long.MAX_VALUE);
		fsb.getOutputStream(Long.MIN_VALUE);
	}
}
