using System;
using System.ServiceModel;
using System.ServiceModel.Channels;

using Ninject;
using Ninject.Modules;
using Ninject.Extensions.Wcf;

internal class DomasNinjectModule : NinjectModule
{
    public override void Load()
    {
        Bind<SystemConfig>().ToSelf().InSingletonScope();
        Bind<UnparsedQueueManager>().ToSelf().InSingletonScope();
        Bind<DomasService>().ToSelf().InSingletonScope();
    }
}

public class Server
{
    public static void Main(string[] args)
    {
        var modules = new INinjectModule[] { new DomasNinjectModule() };
        KernelContainer.Kernel = new StandardKernel(modules);
                
        var binding = new BasicHttpBinding();
        var address = new Uri("http://127.0.0.1:8888");
        var host = new NinjectServiceHost(typeof(DomasService));
        host.AddServiceEndpoint(typeof(IDomasService), binding, address);
        host.Open();
        Console.WriteLine("Press enter to stop..");
        Console.ReadLine();
        host.Close();
    }
}