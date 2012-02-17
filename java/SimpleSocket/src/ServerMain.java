import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: hu
 * Date: 2009-3-28
 * Time: 12:45:18
 * To change this template use File | Settings | File Templates.
 */
public class ServerMain
{
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please specify port range or port to listen");
            System.exit(-1);
        }

        ArrayList<Socket> allSockets = new ArrayList<Socket>();

        for (String arg : args) {
            String[] ports = arg.split("-");
            if (ports.length == 1) {
                try {
                    registerPort(allSockets, Integer.parseInt(ports[0]));
                    System.out.println("Listening " + ports[0]);
                } catch (IOException e) {
                    System.out.println("Cannot bind to " + ports[0]);
                }
            } else {
                int startPort = Integer.parseInt(ports[0]);
                int endPort = Integer.parseInt(ports[1]);
                for (int i = startPort; i <= endPort; ++i) {
                    try {
                        registerPort(allSockets, i);
                    } catch (IOException e) {
                        System.out.println("Cannot bind to " + i);
                    }
                }
                System.out.println("Listening " + arg);
            }
        }

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private static void registerPort(ArrayList<Socket> allSockets, int port)
            throws IOException
    {
        Socket s = new Socket();
        s.bind(new InetSocketAddress(port));
        allSockets.add(s);
    }
}
