using System;

using Apache.NMS;
using Apache.NMS.Util;

public class Hello {
    static public void Main() {
        Uri connecturi = new Uri("activemq:tcp://127.0.0.1:61616");
        Console.WriteLine("About to connect to " + connecturi);
        IConnectionFactory factory = NMSConnectionFactory.CreateConnectionFactory(connecturi);

        using(IConnection connection = factory.CreateConnection())
        using(ISession session = connection.CreateSession())
        {
            IDestination destination = SessionUtil.GetDestination(session, "queue://unparsed");
            Console.WriteLine("Using destination: " + destination);

            using(IMessageConsumer consumer = session.CreateConsumer(destination))
            using(IMessageProducer producer = session.CreateProducer(destination))
            {
                // Start the connection so that messages will be processed.
                connection.Start();
//                producer.Persistent = true;

                // Send a message
                ITextMessage request = session.CreateTextMessage("Hello World!");
                request.NMSCorrelationID = "abc";
                request.Properties["NMSXGroupID"] = "cheese";
                request.Properties["myHeader"] = "Cheddar";

                producer.Send(request);

                // Consume a message
                ITextMessage message = consumer.Receive() as ITextMessage;
                if(message == null)
                {
                    Console.WriteLine("No message received!");
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
