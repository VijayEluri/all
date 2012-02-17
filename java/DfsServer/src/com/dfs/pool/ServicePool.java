package com.dfs.pool;

import com.dfs.Parameters;
import com.dfs.server.FakeObjectService;
import com.emc.documentum.fs.datamodel.core.*;
import com.emc.documentum.fs.datamodel.core.content.ActivityInfo;
import com.emc.documentum.fs.datamodel.core.content.ContentTransferMode;
import com.emc.documentum.fs.datamodel.core.context.RepositoryIdentity;
import com.emc.documentum.fs.datamodel.core.profiles.ContentProfile;
import com.emc.documentum.fs.datamodel.core.profiles.ContentTransferProfile;
import com.emc.documentum.fs.datamodel.core.profiles.FormatFilter;
import com.emc.documentum.fs.rt.ServiceException;
import com.emc.documentum.fs.rt.context.ContextFactory;
import com.emc.documentum.fs.rt.context.IServiceContext;
import com.emc.documentum.fs.rt.context.ServiceFactory;
import com.emc.documentum.fs.services.core.client.IObjectService;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ServicePool {

    private final static Logger logger = Logger.getLogger(ServicePool.class);

    private static ServicePool servicePool;

    private ArrayList<IObjectService> objectServiceArray = new ArrayList<IObjectService>();
    private ArrayList<ServiceWaiter> waitingList = new ArrayList<ServiceWaiter>();

    private final Object objLock = new Object();

    public static ServicePool getInstance() {
        if (servicePool == null) {
            servicePool = new ServicePool();
            servicePool.init(Parameters.poolSize, false);
        }
        return servicePool;
    }

    private ObjectIdentity<ObjectId> getObjectIdentity(String id) {
        ObjectIdentity<ObjectId> identity = new ObjectIdentity<ObjectId>(Parameters.respositoryName);
        identity.setValue(new ObjectId(id));
        return identity;
    }

    private void readAnObject(IObjectService objectService) {
        ContentProfile contentProfile = new ContentProfile();
        contentProfile.setFormatFilter(FormatFilter.ANY);
        OperationOptions operationOptions = new OperationOptions();
        operationOptions.setContentProfile(contentProfile);
        operationOptions.setProfile(contentProfile);

        ObjectIdentitySet objectIdSet = new ObjectIdentitySet();
        objectIdSet.addIdentity(getObjectIdentity("0901117280381131"));
        DataPackage dataPackage;
        try {
            dataPackage = objectService.get(objectIdSet, operationOptions);
            List<DataObject> objs = dataPackage.getDataObjects();
            for (DataObject obj : objs) {
                logger.info(" got " + obj.getIdentity());
            }
        } catch (Exception e) {
            // ignore this exception
        }
    }

    private void init(int size, boolean fake) {
        try {
            for (int i = 0; i < size; ++i) {
                if (fake) {
                    IObjectService objectService = new FakeObjectService();
                    objectServiceArray.add(objectService);
                } else {
                    IServiceContext context;
                    context = createContext(Parameters.respositoryName, Parameters.userName, Parameters.password);
                    ServiceFactory sf = ServiceFactory.getInstance();
                    IObjectService objectService = sf.getRemoteService(IObjectService.class, context);
                    readAnObject(objectService);
                    objectServiceArray.add(objectService);
                }
            }
        } catch (Exception e) {
            logger.error("Error while creating service", e);
        }
    }

    private IServiceContext createContext(String repository, String userName, String password) throws ServiceException {
        logger.info("Creating context");
        
        logger.info("\tGetting ContextFactory");
        ContextFactory cf = ContextFactory.getInstance();

        logger.info("\tCreating empty context");
        IServiceContext context = cf.newContext();

        logger.info("\tAdding identity");
        RepositoryIdentity repoIdent = new RepositoryIdentity();
        repoIdent.setRepositoryName(repository);
        repoIdent.setUserName(userName);
        repoIdent.setPassword(password);

        context.addIdentity(repoIdent);

        logger.info("\tSetting transferProfile");
        ContentTransferProfile transferProfile = new ContentTransferProfile();
        transferProfile.setAsyncContentTransferAllowed(true);
        transferProfile.setTransferMode(ContentTransferMode.UCF);

        if (Parameters.location.length() > 0) {
            transferProfile.setGeolocation(Parameters.location);
        }
        transferProfile.setCachedContentTransferAllowed(true);
        transferProfile.setActivityInfo(new ActivityInfo(false));

        context.setProfile(transferProfile);

        logger.info("\tRegistering context");
        context = cf.register(context);

        return context;
    }

    public IObjectService getObjectService(ServiceWaiter waiter) {
        synchronized (objLock) {
            if (objectServiceArray.size() > 0) {
                IObjectService objService = objectServiceArray.remove(0);
                return objService;
            }
            if (!waitingList.contains(waiter)) {
                waitingList.add(waiter);
                logger.info("Waiting list length: " + waitingList.size());
            }
            return null;
        }
    }

    public void releaseObjectService(IObjectService objectService) {
        synchronized (objLock) {
            if (waitingList.size() > 0) {
                ServiceWaiter waiter = waitingList.remove(0);
                waiter.setObjectService(objectService);
                waiter.notifyObject();
            } else {
                objectServiceArray.add(objectService);
            }
        }
    }
}
