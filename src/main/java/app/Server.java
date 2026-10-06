package app;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class Server extends WebSocketServer
{
    public Server(int port)
    {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn , ClientHandshake handshake)
    {
        System.out.println("New client connected: "+ conn.getRemoteSocketAddress());
        broadcast(conn.getRemoteSocketAddress() + " joined the chat.");
    }

    @Override
    public void onClose(WebSocket conn , int code , String reason , boolean remote)
    {
        System.out.println("Client disconnected: "+ conn.getRemoteSocketAddress());
        broadcast(conn.getRemoteSocketAddress() + " left the chat.");
    }

    @Override
    public void onMessage(WebSocket conn ,String message)
    {
        System.out.println("Message from " + conn.getRemoteSocketAddress());
        broadcast(conn.getRemoteSocketAddress() +  ": " + message);
    }

    @Override
    public void onError(WebSocket conn , Exception ex)
    {
        if(conn == null)
        {
            System.out.println("Server error: " + ex.getMessage());
        }
        else 
        {
            System.out.println("Error on connection " + conn.getRemoteSocketAddress());
        }
    }

    @Override
    public void onStart()
    {
        System.out.println("Server started on port " + getPort());

    }
}

