using System;
using System.ServiceModel;
using System.ServiceModel.Channels;

using Apache.NMS;
using Apache.NMS.Util;

public class DomasClient : ClientBase<IDomasService>, IDomasService
{
    public DomasClient (Binding binding, EndpointAddress address) : base (binding, address)
    {
    }

    public int submitJob(string jobXML)
    {
        return Channel.submitJob(jobXML);
    }
}

public class Client
{
    public static void Main(string[] args)
    {
        var binding = new BasicHttpBinding ();
        var address = new EndpointAddress ("http://127.0.0.1:8888");
        var client = new DomasClient (binding, address);
        Console.WriteLine (client.submitJob("hello from client"));
        ReadMsg();
    }
    
    private static void ReadMsg()
    {
        Uri connecturi = new Uri("activemq:tcp://127.0.0.1:61616");
        Console.WriteLine("About to connect to " + connecturi);
        IConnectionFactory factory = NMSConnectionFactory.CreateConnectionFactory(connecturi);

        using(IConnection connection = factory.CreateConnection())
        using(Apache.NMS.ISession session = connection.CreateSession())
        {
            IDestination destination = SessionUtil.GetDestination(session, "queue://unparsed");
            Console.WriteLine("Using destination: " + destination);

            using(IMessageConsumer consumer = session.CreateConsumer(destination))
            {
                connection.Start();
                while (true) 
                {
                    ITextMessage message = consumer.Receive() as ITextMessage;
                    if(message == null)
                    {
                        Console.WriteLine("No message received!");
                        break;
                    }
                    else
                    {
                        Console.WriteLine("Received message with ID:   " + message.NMSMessageId);
                        Console.WriteLine("Received message with text: " + message.Text);
                    }
                    
                }
            }

        }
        
    }
}