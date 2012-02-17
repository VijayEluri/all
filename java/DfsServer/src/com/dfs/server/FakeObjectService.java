package com.dfs.server;

import java.util.List;

import com.emc.documentum.fs.datamodel.core.DataPackage;
import com.emc.documentum.fs.datamodel.core.ObjectContentSet;
import com.emc.documentum.fs.datamodel.core.ObjectIdentity;
import com.emc.documentum.fs.datamodel.core.ObjectIdentitySet;
import com.emc.documentum.fs.datamodel.core.ObjectLocation;
import com.emc.documentum.fs.datamodel.core.ObjectPath;
import com.emc.documentum.fs.datamodel.core.OperationOptions;
import com.emc.documentum.fs.datamodel.core.ValidationInfoSet;
import com.emc.documentum.fs.rt.ServiceException;
import com.emc.documentum.fs.rt.context.IServiceContext;
import com.emc.documentum.fs.services.core.CoreServiceException;
import com.emc.documentum.fs.services.core.client.IObjectService;

public class FakeObjectService implements IObjectService {

	public DataPackage copy(ObjectIdentitySet arg0, ObjectLocation arg1,
			DataPackage arg2, OperationOptions arg3)
			throws CoreServiceException, ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public DataPackage create(DataPackage arg0, OperationOptions arg1)
			throws CoreServiceException, ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public ObjectIdentity createPath(ObjectPath arg0, String arg1)
			throws CoreServiceException, ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public void delete(ObjectIdentitySet arg0, OperationOptions arg1)
			throws CoreServiceException, ServiceException {
		// TODO Auto-generated method stub

	}

	public DataPackage get(ObjectIdentitySet arg0, OperationOptions arg1)
			throws CoreServiceException, ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ObjectContentSet> getObjectContentUrls(ObjectIdentitySet arg0)
			throws CoreServiceException, ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public DataPackage move(ObjectIdentitySet arg0, ObjectLocation arg1,
			ObjectLocation arg2, DataPackage arg3, OperationOptions arg4)
			throws CoreServiceException, ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public DataPackage update(DataPackage arg0, OperationOptions arg1)
			throws CoreServiceException, ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public ValidationInfoSet validate(DataPackage arg0)
			throws CoreServiceException, ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public IServiceContext getServiceContext() {
		// TODO Auto-generated method stub
		return null;
	}

}
