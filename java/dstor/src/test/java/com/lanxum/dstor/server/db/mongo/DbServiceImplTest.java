package com.lanxum.dstor.server.db.mongo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lanxum.dstor.server.db.DbService;
import com.lanxum.dstor.server.db.FileObject;
import com.lanxum.dstor.server.db.IdGenerator;

import junit.framework.TestCase;

public class DbServiceImplTest extends TestCase
{
	private DbService dbService;
	private IdGenerator generator;

	@Override
	protected void setUp() throws Exception
	{
		ApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
		dbService = (DbService) ctx.getBean("dbService");
		generator = (IdGenerator) ctx.getBean("idGenerator");
		super.setUp();
	}

	public void testSave() throws Exception
	{
		long id = generator.getNextFileId();

		FileObject f1 = new FileObject();
		f1.setFileName("filename");
		f1.setFileSize(1234567L);
		f1.setURI("hello2");
        f1.setMd5Sum("md5sum");

		dbService.save(id, f1);

		FileObject f2 = dbService.find(id);

		assertEquals(f1.getFileName(), f2.getFileName());
		assertEquals(f1.getFileSize(), f2.getFileSize());
		assertEquals(f1.getURI(), f2.getURI());

		dbService.remove(id);
	}

}
