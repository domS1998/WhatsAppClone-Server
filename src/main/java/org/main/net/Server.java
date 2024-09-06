package org.main.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.Hashtable;
import org.json.JSONObject;
import org.main.net.api.*;
import org.main.net.api.transaction.*;
import org.main.net.api.transaction.messages.init.*;
import org.main.net.api.transaction.messages.result.status.TransactionFailedMessage;

// singleton
public class Server {

    private final ServerSocket SOCKET = new ServerSocket();

    // Lookup Tabelle mit Username-ClientThread Paaren
    private final Hashtable<String, ClientThread> clients = new Hashtable<>();
    public final Hashtable<String, ClientThread> getClients() { return this.clients;}

    private String IP_ADDRESS;
    private final static int PORT = 8080;

    private static Server uniqueInstance = null;

    public static Server getInstance () {
        if (Server.uniqueInstance == null) {
            try {
//                Server.uniqueInstance = new Server(Network.getIP(), Server.PORT);
                Server.uniqueInstance = new Server("0.0.0.0", Server.PORT);
            }
            catch (IOException e) { System.out.println(e.getMessage());}
        }
        return Server.uniqueInstance;
    }

    public static void start () {
        if (Server.uniqueInstance != null) {
            System.out.println("Server is already running");
        }
        else {
            try {
//                Server.uniqueInstance = new Server(Network.getIP(), Server.PORT);
                Server.uniqueInstance = new Server("0.0.0.0", Server.PORT);

            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private Server (String host, int port) throws IOException {

        this.IP_ADDRESS = host;
        this.SOCKET.bind(new InetSocketAddress(host, port));
//        this.SOCKET.setReuseAddress(true);
        this.SOCKET.setReceiveBufferSize(2048);

        // awaiting and handling connections in new thread in a loop
        Thread awaitThread = new Thread(() -> {

            try {
                awaitConnections();
            }
            catch (IOException e) {throw new RuntimeException(e);}
        });
        awaitThread.start();

        // additional thread for cleaing up disconnectd sockets
//        Thread cleanerThread = new Thread(this::removeClosedThreads);
//        cleanerThread.start();

//        Thread infoThread = new Thread(() -> {printStatusInfo();});
//        infoThread.start();
    }


    // Thread, der neue Verbindungen bearbeit unf ggf.
    //  in Client Tabelle einfügt
    public void awaitConnections() throws IOException {

            while (true) {

                System.out.println("Thread " + Thread.currentThread().getId() + " (await):" + " Server waiting for new connections on " + this.SOCKET.getLocalSocketAddress());

                try {

                    // neuen Client akzeptieren
                    System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "waiting for new connect messages on " + this.SOCKET.getLocalSocketAddress()+ " ...");
                    Socket socket = SOCKET.accept();
                    System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "new connection accepted: " + socket.getRemoteSocketAddress() + ", " + socket.getLocalSocketAddress());
                    ClientSocket newSocket = new ClientSocket(socket);

                    System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "new connection accepted: " + newSocket.getSocket().getRemoteSocketAddress() + ", " + newSocket.getSocket().getLocalSocketAddress());
                    System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "Starting new Thread to process messages of new socket");
                    System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "new socket @ "+newSocket.getSocket().getLocalSocketAddress() + " <--> " + newSocket.getSocket().getRemoteSocketAddress());

                        try {

                            // auf erste Nachricht des neuen Sockets warten
                            String bufferStr = newSocket.read();

                            System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "New Message received: "+bufferStr);

                            JSONObject json = new JSONObject(bufferStr);

                            System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "received new JSON String: "+json);

                            // falls login
                            if (json.getString("MESSAGE_TYPE").equals("LOGIN")) {

                                LoginMessage loginMessage = null;
                                try {
                                    loginMessage = new LoginMessage(json);
                                }
                                catch (NoSuchApiMessageException e) {
                                    throw new RuntimeException(e);
                                }
                                System.out.println("Thread " + Thread.currentThread().getId() + " (switch user messages): " + "Login detected, starting new thread for user " + loginMessage.getUsername());
                                // neuer thread für client
                                ClientThread newClientThread = new ClientThread(loginMessage.getUsername());
                                newClientThread.start();
                                // login transaktion einhängen
                                newClientThread.pendingTransactions.add(new LoginTransaction(loginMessage.getUsername(), newSocket, loginMessage));

                                // user mit client thread in user tabelle einhängen
                                System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "putting thread for user '" + newClientThread.getUsername() + "' in connection table");
                                this.clients.put(loginMessage.getUsername(), newClientThread);

                                System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "successfully added client '" + newClientThread.getUsername() + "'");
                            }
                            else {

                                String msgType = json.getString("MESSAGE_TYPE");
                                String msgUser = json.getString("username");
                                System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "User " + msgUser + " requested transaction '" + msgType + "'");
                                // neuer thread für client

                                Transaction transaction = null;
                                switch (msgType) {

                                    case "LOGIN" -> {
                                        LoginMessage loginMessage = new LoginMessage(json);
                                        transaction = new LoginTransaction(msgUser, newSocket, loginMessage);
                                    }
                                    case "LOAD_USER" -> {
                                        LoadUserMessage loadUserMessage = new LoadUserMessage(json);
                                        transaction = new LoadUserTransaction(msgUser, newSocket, loadUserMessage);
                                    }
                                    case "SEND_MESSAGE" -> {
                                        SendMessage sendMessage = new SendMessage(json);
                                        transaction = new NewMessageTransaction(msgUser, newSocket, sendMessage);
                                    }
                                    case "CONNECT_LISTENER" -> {
                                        System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "Service connect listener detected from user " + msgUser);
                                        ConnectListenerMessage connectMsg = new ConnectListenerMessage(json);
                                        transaction = new ConnectListenerTransaction(msgUser, newSocket, connectMsg);
                                    }
                                    case "CREATE_CHAT" -> {
                                        System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "Service create new chat");
                                        CreateChatMessage createMsg = new CreateChatMessage(json);
                                        transaction = new CreateChatTransaction(newSocket, createMsg, msgUser);
                                    }
                                    case "UPDATE_MESSAGE" -> {
                                        System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "Service update message");
                                        UpdateMsgMessage updateMsg = new UpdateMsgMessage(json);
                                        transaction = new UpdateMessageTransaction(newSocket, updateMsg, msgUser);
                                    }
                                }

                                if (transaction != null) {
                                    // login transaktion einhängen
                                    // wenn user angemeldet
                                    if ( (! this.clients.containsKey(transaction.getUsername())) && ( ! (transaction.getType() == TransactionType.CONNECT_LISTENER) || ! (transaction.getType() == TransactionType.LOGIN) )  ){

                                            System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "key '" + msgUser + "' not found in table, cant process trancation " + transaction.getType() + " " + transaction.getTransactionId());
                                            System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "msg type is not 'login' or connect 'listener' "+"('"+msgType+"')");
                                            System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "user " + transaction.getUsername() + " sending fail messsage: " + transaction.getType() + " " + transaction.getTransactionId());
                                            transaction.getClientSocket().send(new TransactionFailedMessage(transaction.getTransactionId(), "client not in client table", transaction.getUsername()).toString());
                                    }
                                    else {
                                        // transaktion zu queue
                                        System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "adding transaction " + transaction.getType() + " (" + transaction.getTransactionId() + ") to user " + msgUser + "'s pending transactions");
                                        this.clients.get(transaction.getUsername()).pendingTransactions.add(transaction);
                                        System.out.println("pending queue: " + this.clients.get(transaction.getUsername()).pendingTransactions);
                                    }
                                }
                                else {
                                    System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "Transaction with type '"+ msgType  +"' is null after cast: "+json.toString());
                                }
                            }
                        }
                        catch (IOException e) {
                            System.out.println(e);
                            return;
                        }
                        catch (ParseException e) {
                            System.out.println(e);
                        }
                }
                catch (NoSuchApiMessageException e) {System.out.println(e.getMessage());}

                System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "active clients: " + this.clients.size());
            }

    }

    public void removeClosedThreads (){

        System.out.println("thread " + Thread.currentThread().getId() + " (cleaner): cleaning up disconnected clients ....");
        while (true) {

            // ...
        }
    }


    public void printStatusInfo(){

//        while (true) {
//            tableToString();
//            try { Thread.sleep(10000); }
//            catch (InterruptedException e) { throw new RuntimeException(e); }
//        }
    }



    public static void tableToString(){

        String result = "";

        result += "-------------------------------------------------\n";
        result += "|     i|thread|  username|                  host|\n";
        result += "-------------------------------------------------\n";

        int i = 1;
        for (var clientThread : getInstance().clients.entrySet()) {
            result += tableEntryToString((ClientThread) clientThread, i);
            i++;
        }
        result += "-------------------------------------------------\n";

        System.out.println(result);
    }

    public static String tableEntryToString(ClientThread clientThread, int tableIndex){
        return "|"+columnStr(tableIndex,6)+"|"+columnStr(clientThread.getId(), 6)+"|"+columnStr(clientThread.getUsername(), 10)+clientThread.getResponseListenerSocket().getSocket().getRemoteSocketAddress()+"|\n";
    }

    public static String columnStr(long n, int columnWidth){
        return varToColumnStr(Long.toString(n), columnWidth);
    }

    public static String columnStr(int n, int columnWidth){
        return varToColumnStr(Integer.toString(n), columnWidth);
    }

    public static String columnStr(String str, int columnWidth){
        return varToColumnStr(str, columnWidth);
    }

    public static String varToColumnStr(String str, int columnWidth){
        String result = "";
        if (str == null){
            str = "null";
        }
        for (int i = 0; i < columnWidth-str.length(); i++) {
            result += " ";
        }
        return result + str;
    }



}
