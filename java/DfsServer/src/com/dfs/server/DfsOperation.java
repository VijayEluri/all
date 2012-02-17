package com.dfs.server;

import java.io.File;

import org.apache.log4j.Logger;

import com.dfs.Parameters;
import com.dfs.pool.ServiceWaiter;
import com.emc.documentum.fs.datamodel.core.DataObject;
import com.emc.documentum.fs.datamodel.core.DataPackage;
import com.emc.documentum.fs.datamodel.core.ObjectId;
import com.emc.documentum.fs.datamodel.core.ObjectIdentity;
import com.emc.documentum.fs.datamodel.core.ObjectIdentitySet;
import com.emc.documentum.fs.datamodel.core.OperationOptions;
import com.emc.documentum.fs.datamodel.core.content.FileContent;
import com.emc.documentum.fs.datamodel.core.profiles.ContentProfile;
import com.emc.documentum.fs.datamodel.core.profiles.FormatFilter;
import com.emc.documentum.fs.datamodel.core.properties.PropertySet;
import com.emc.documentum.fs.services.core.client.IObjectService;

public class DfsOperation extends ServiceWaiter
{

	private final static Logger logger = Logger.getLogger(DfsOperation.class);

	public DfsOperation()
	{
	}

	private DataObject createDocument(ObjectIdentity<ObjectId> objIdentity, String path)
	{
		File testFile = new File(path);

		if (!testFile.exists()) {
			throw new RuntimeException("Test file: " + testFile.toString() + " does not exist");
		}
		DataObject dataObject = new DataObject(objIdentity, "dm_document");
		PropertySet properties = dataObject.getProperties();
		properties.set("object_name", "MyImage002");
		properties.set("title", "MyImage");
		properties.set("a_content_type", "jpeg");
		dataObject.getContents().add(new FileContent(testFile.getAbsolutePath(), "jpeg"));
		return dataObject;
	}

	public void save() throws Exception
	{
		try {
			IObjectService objectService = getObjectService();
			logger.info("saving");
			ObjectIdentity<ObjectId> objIdentity = new ObjectIdentity<ObjectId>(Parameters.respositoryName);
			DataPackage dataPackage = new DataPackage();
			dataPackage.addDataObject(createDocument(objIdentity, "/tmp/1.jpg"));
			dataPackage.addDataObject(createDocument(objIdentity, "/tmp/2.jpg"));
			dataPackage.addDataObject(createDocument(objIdentity, "/tmp/3.jpg"));
			dataPackage.addDataObject(createDocument(objIdentity, "/tmp/4.jpg"));
			dataPackage.addDataObject(createDocument(objIdentity, "/tmp/5.jpg"));
			dataPackage.addDataObject(createDocument(objIdentity, "/tmp/6.jpg"));
			dataPackage.addDataObject(createDocument(objIdentity, "/tmp/7.jpg"));
			dataPackage.addDataObject(createDocument(objIdentity, "/tmp/8.jpg"));
			OperationOptions operationOptions = new OperationOptions();
			objectService.create(dataPackage, operationOptions);
		} catch (Exception e) {
			logger.error("error while saving", e);
			throw e;
		} finally {
			releaseObjectService();
		}
	}

	public void read() throws Exception
	{
		try {
			IObjectService objectService = getObjectService();
			logger.info("reading");
			ContentProfile contentProfile = new ContentProfile();
			contentProfile.setFormatFilter(FormatFilter.ANY);
			OperationOptions operationOptions = new OperationOptions();
			operationOptions.setContentProfile(contentProfile);
			operationOptions.setProfile(contentProfile);

			ObjectIdentitySet objectIdSet = new ObjectIdentitySet();
			objectIdSet.addIdentity(getObjectIdentity("09011172802834e3"));
			objectIdSet.addIdentity(getObjectIdentity("09011172802834e4"));
			objectIdSet.addIdentity(getObjectIdentity("09011172802834e5"));
			objectIdSet.addIdentity(getObjectIdentity("09011172802834e6"));
			objectIdSet.addIdentity(getObjectIdentity("09011172802834e7"));
			objectIdSet.addIdentity(getObjectIdentity("09011172802834e8"));
			objectIdSet.addIdentity(getObjectIdentity("09011172802834e9"));
			objectIdSet.addIdentity(getObjectIdentity("09011172802834f7"));
			DataPackage dataPackage;
			dataPackage = objectService.get(objectIdSet, operationOptions);
//			List<DataObject> objs = dataPackage.getDataObjects();
//			for (int i = 0; i < objs.size(); ++i) {
//				DataObject obj = objs.get(i);
//				logger.info(" got " + obj.getIdentity());
//			}
		} catch (Exception e) {
			logger.error(" readObjects error!", e);
			throw e;
		} finally {
			releaseObjectService();
		}
	}

	private ObjectIdentity<ObjectId> getObjectIdentity(String id)
	{
		ObjectIdentity<ObjectId> identity = new ObjectIdentity<ObjectId>(Parameters.respositoryName);
		identity.setValue(new ObjectId(id));
		return identity;
	}
}
