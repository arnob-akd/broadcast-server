package app;
import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class Client extends WebSocketClient
{
    public Client (URI serverUri)
    {
        super(serverUri);
    }

    @Override 

    public void onOpen(ServerHandshake handshake)
    {
        System.out.println("Connected to server!");
    }

    @Override 
    public void onMessage(String msg)
    {
        System.out.println(msg);
    }

    @Override 
    public void onClose(int code , String reason , boolean remote)
    {
        System.out.println("Disconnected from server.");
    }

    @Override 
    public void onError(Exception ex)
    {
        System.out.println("Error : " + ex.getMessage());
    }
}
