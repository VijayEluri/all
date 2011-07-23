package com.lanxum.dstor.server.db.mongo;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lanxum.dstor.server.db.IdGenerator;
import com.lanxum.dstor.server.db.mongo.IdGeneratorImpl;

public class IdGeneratorImplTest extends TestCase
{

	private IdGenerator generator;

	public IdGeneratorImplTest(String testName)
	{
		super(testName);
	}

	@Override
	protected void setUp() throws Exception
	{
		ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
		generator = (IdGeneratorImpl) ctx.getBean("idGenerator");
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * Test of getNextId method, of class IdGenerator.
	 */
	public void testGetNextFileId() throws Exception
	{
		long id1 = generator.getNextFileId();
		long id2 = generator.getNextFileId();
		assert (id2 == id1 + 1);

		long start = System.currentTimeMillis();
		int count = 2000;
		for (int i = 0; i < count; ++i) {
			generator.getNextFileId();
		}
		System.out.println("Speed of getNextId: " + (System.currentTimeMillis() - start) / (double) count + " ms");
	}

}
