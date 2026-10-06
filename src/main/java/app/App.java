package app;

import java.net.URI;
import java.util.Scanner;


public class App 
{
    public static void main(String[] args)
    {
        if(args.length == 0)
        {
            printu();
            return;
        }

        String command = args[0];

        if(command.equals("start"))
        {
            int port = 8887;
            Server server = new Server(port);
            server.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() ->
            {
                System.out.println("\nShutting down server...");

                try
                {
                    server.stop(1000);
                }
                catch(InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                }

                System.out.println("Server stopped.");
            }
            ));
        }

        else if(command.equals("connect"))
        {
            try
            {
                Client client = new Client(new URI("ws://localhost:8887"));
                boolean connected = client.connectBlocking();

                if(!connected)
                {
                    System.out.println("Could not connect to the server.Is it runnign?");
                    return ;
                }

                Scanner scanner = new Scanner(System.in);

                while (true) 
                { 
                    String msg = scanner.nextLine();
                    
                    if(msg.equalsIgnoreCase("exit"))
                    {
                        client.closeBlocking();

                        System.out.println("Disconnected");
                        break;
                    }

                    try
                    {
                        client.send(msg);
                    }
                    catch(Exception e)
                    {
                        System.out.println("Could not send message: " + e.getMessage());
                    }
                }
                
            }

            catch(Exception e)
            {
                System.out.println("Count not connect: " + e.getMessage());
            }

        }
        else 
        {
            System.out.println("Unknown command:" + command);
            printu();
        }
    }
        
    static void printu()
    {
        System.out.println("Usage : ");
        System.out.println("Broadcast Server start");
        System.out.println("Broadcast Server connect");
    }
}
