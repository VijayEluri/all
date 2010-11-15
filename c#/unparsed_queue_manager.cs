using Apache.NMS;
using Apache.NMS.Util;
using Ninject;
using System;

public class UnparsedQueueManager
{
    
    [Inject]
    public UnparsedQueueManager(SystemConfig config)
    {
        Uri connecturi = new Uri(config.ACTIVEMQ_URI);
        IConnectionFactory factory = NMSConnectionFactory.CreateConnectionFactory(connecturi);
        connection = factory.CreateConnection();
        session = connection.CreateSession();
        
        connection.Start();
        IDestination destination = SessionUtil.GetDestination(session, config.UNPARSED_QUEUE);
        producer = session.CreateProducer(destination);
    }
    
    public void sendMsg(string data)
    {
        ITextMessage request = session.CreateTextMessage(data);
        producer.Send(request);
    }
    
    private IConnection connection;
    private ISession session;
    private IMessageProducer producer;
}