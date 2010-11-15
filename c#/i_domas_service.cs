using System;
using System.ServiceModel;
using System.ServiceModel.Channels;

[ServiceContract]
public interface IDomasService
{
    [OperationContract]
    int submitJob(string jobXML);
}
