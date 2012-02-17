package com.dfs.pool;

import org.apache.log4j.Logger;

import com.emc.documentum.fs.services.core.client.IObjectService;

public class ServiceWaiter {
	
	private IObjectService objectService;
	private Object objLock = new Object();
	private Logger logger = Logger.getLogger(ServiceWaiter.class);
	
	public void setObjectService(IObjectService service) {
		objectService = service;
	}
	
	protected IObjectService getObjectService() {
		while(objectService == null) {
			objectService = ServicePool.getInstance().getObjectService(this);
			if (objectService == null) {
				waitForObject();
			}
		}
		return objectService;
	}
	
	protected void releaseObjectService() {
		if (objectService != null) {
			ServicePool.getInstance().releaseObjectService(objectService);
		}
	}
	
	public void waitForObject() {
		synchronized(objLock) {
			try {
				objLock.wait();
			} catch (InterruptedException e) {
				logger.error("Error while trying to wait.", e);
			}
		}
	}
	
	public void notifyObject() {
		synchronized(objLock) {
			objLock.notify();
		}
	}
}
