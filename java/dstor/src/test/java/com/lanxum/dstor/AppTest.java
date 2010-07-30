package com.lanxum.dstor;

import com.lanxum.dstor.server.db.mongo.DbServiceImplTest;
import com.lanxum.dstor.server.db.mongo.IdGeneratorImplTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AppTest extends TestCase
{
	public AppTest(String testName)
	{
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		TestSuite suite = new TestSuite();
		suite.addTestSuite(IdGeneratorImplTest.class);
		suite.addTestSuite(DbServiceImplTest.class);
		return suite;
	}

}
