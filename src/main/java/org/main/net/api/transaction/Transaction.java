package org.main.net.api.transaction;

import org.json.JSONException;
import org.json.JSONObject;
import org.main.net.ClientSocket;
import org.main.net.api.NoSuchApiMessageException;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.result.status.TransactionCompleteMessage;
import org.main.net.api.transaction.messages.result.status.TransactionFailedMessage;

import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

abstract public class Transaction{

    protected String username;
    protected TransactionType type;
    protected ApiMessage msg;
    protected ApiMessage result;
    protected ClientSocket clientSocket;
    protected Thread thread;
    protected String transactionId = UUID.randomUUID().toString();
    protected boolean failed = false;
    protected boolean completed = false;

    public boolean isCompleted (){return completed;}

    public ApiMessage getResult() {
        while (thread.isAlive()){}
        return result;
    }

    public String getTransactionId() {return transactionId;}
    public TransactionType getType() {return type;}
    public ApiMessage getMsg (){return this.msg;}
    public String getUsername() {return this.username;}
    public void setUsername(String user) {this.username = username;}
    public void setMsg(ApiMessage msg) {this.msg = msg;}
    public ClientSocket getClientSocket() {return clientSocket;}
    public void setClientSocket(ClientSocket clientSocket) {this.clientSocket = clientSocket;}





    // Funktion, um die Antwortnachricht dynamisch zu casten
    abstract protected ApiMessage castResultMessage (JSONObject json) throws JSONException, NoSuchApiMessageException, ParseException;

    // Konstruktor für UI, starten einer neuen Transaktion
    public Transaction(TransactionType type, ApiMessage initMsg, String username) {
        this.username = username;
        this.msg = initMsg;
        this.msg.setTransactionID(this.transactionId);
        this.type = type;
        this.thread = new Thread(() -> {performTransaction();});
    }

    // Konstruktor Server, weiterverarbeiten mit vestehender Verbindung über socket des ResponseListenerThreads
    public Transaction(String username, ClientSocket clientSocket, TransactionType type, ApiMessage initMsg) {
        this.username = username;
        this.clientSocket = clientSocket;
        this.msg = initMsg;
        this.msg.setTransactionID(initMsg.getTransactionID());
        this.type = type;
        this.thread = new Thread(() -> {performTransaction();});
    }


    // Nachricht senden und auf Antwort warten
    public void start() { thread.start();}

    public boolean connect () {
        System.out.println("Transaction "+this.getType()+" ("+this.getTransactionId()+"): connecting socket with server ...");
        try {
            this.clientSocket = new ClientSocket();
        } catch (IOException e) {
            System.out.println("Transaction "+this.getType()+" ("+this.getTransactionId()+"): io exception, " + "no socket" );
        }
        if ( ! this.clientSocket.isConnected()) {
            System.out.println("Transaction "+this.getType()+" ("+this.getTransactionId()+"): No Client, terminating transaction ");
            return false;
        }
        return true;
    }

    void performTransaction(){

            // Neuen Socket mit Server verbinden, wenn nicht ein
            //  vorhandener mitgegeben
            if (this.clientSocket == null){this.connect();}
            if ( ! this.clientSocket.isConnected()){return;}

            System.out.println("Transaction "+this.getType()+" ("+this.getTransactionId()+"): start");
            // Transaktion starten mit senden der init Nachricht
            System.out.println("Transaction "+this.getType()+" ("+this.getTransactionId()+"): sending message: " + this.getMsg());
            try { this.clientSocket.send(this.msg.toString());}
            catch (IOException e) {
                System.out.println(e.getMessage());
            }
            // Auf Ergebnis warten und auslesen
            try {
                String str = this.clientSocket.read().toString();
                System.out.println("Transaction "+this.getType()+" ("+this.getTransactionId()+"): received string: "+str);
                this.result = castResultMessage(new JSONObject(str));

                if (this.result instanceof TransactionFailedMessage){
                    System.out.println("Transaction "+this.getType()+" ("+this.getTransactionId()+"): fail, terminating ");
                    this.failed = true;
                }
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("Transaction "+this.getType()+" ("+this.getTransactionId()+"): io exception, returning");
                return;

            } catch (NoSuchApiMessageException e) {
                System.out.println(e.getMessage());
            } catch (ParseException e) {
                System.out.println(e.getMessage());
            }
        System.out.println("Transaction "+this.getType()+" ("+this.getTransactionId()+"): received : "+this.result);
        try {
            String successStr = new TransactionCompleteMessage(this.transactionId, this.msg.getUsername()).toString();
            System.out.println("Transaction "+this.getType()+" ("+this.getTransactionId()+"): sending success message: "+successStr);
            this.clientSocket.send(successStr);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Transaction "+this.getType()+" ("+this.getTransactionId()+"): io exception, returning");
            return;
        }
        System.out.println("Transaction "+this.getType()+" ("+this.getTransactionId()+"): complete");

        this.completed = true;
    }


}


