package org.main.net;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.net.*;

public class ClientSocket {

    private Socket socket = new Socket();
    private InetSocketAddress host = new InetSocketAddress(Network.getIP(), 10001);
    private InetSocketAddress targetHost = new InetSocketAddress(Network.getIP(), 8080); ;

    public Socket getSocket () {return socket;}
    public void setSocket(Socket socket){this.socket = socket;}
    public InetSocketAddress getHost() {return host;}
    public InetSocketAddress getTargetHost() {return targetHost;}

    private boolean connected = false;

    public boolean isConnected() {return connected;}

    // Konstruktor mit neuem Socket
    public ClientSocket () throws IOException {

        try {
            this.socket.bind(host);
        }
        catch (IOException e) {

            System.out.println(e.getMessage());
            System.out.println("Client: binding of socket to host failed");
            this.connected = false;
        }

        System.out.println("Client: connecting to " + targetHost);

        try {

            this.socket.connect(targetHost);
            System.out.println("Client: connected");
            this.connected = true;
        }
        catch (IOException e) {

            System.out.println(e.getMessage());
            System.out.println("Client: connection failed");
            this.connected = false;
            System.out.println("Client socket: closing socket");
            this.socket.close();
            throw new IOException();
        }
    }

    // Konstruktor für vorhandenen Socket
    public ClientSocket (Socket clientResponseListenerSocket) throws IOException {
        this.socket = clientResponseListenerSocket;
        this.connected = true;
    }

    public void send (String message) throws IOException {
        DataOutputStream dOut = new DataOutputStream(this.socket.getOutputStream());
        dOut.writeUTF(message);
        dOut.flush();
    }

    // String aus Socekt-Buffer auslesen
    public String read () throws IOException {
        // input stream objekt für buffer des clients
        DataInputStream dIn = new DataInputStream(socket.getInputStream());
        // gesamten inhalt als string auslesen
        String message = dIn.readUTF();
        return message;
    }



}
