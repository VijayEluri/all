using System;
using System.ServiceModel;
using System.ServiceModel.Channels;
using Ninject;

[ServiceBehavior(InstanceContextMode=InstanceContextMode.Single)]
public class DomasService : IDomasService
{
    [Inject]
    public UnparsedQueueManager unparsedQueueManager { get; set; }
    
    public int submitJob(string jobXML)
    {
        unparsedQueueManager.sendMsg(jobXML);
        return 1;
    }
}
