package org.main.net;

import org.json.JSONObject;
import org.main.exceptions.ChatWithUserException;
import org.main.exceptions.DuplicateMessageException;
import org.main.exceptions.NoSuchUserException;
import org.main.model.Message;
import org.main.net.api.NoSuchApiMessageException;
import org.main.net.api.transaction.Transaction;
import org.main.net.api.transaction.TransactionType;
import org.main.net.api.transaction.UpdateMessageTransaction;
import org.main.net.api.transaction.messages.MessageType;
import org.main.net.api.transaction.messages.init.IsConnectedMessage;
import org.main.net.api.transaction.messages.init.UpdateMsgMessage;
import org.main.net.api.transaction.messages.result.NewMessageServerResponse;
import org.main.net.api.transaction.messages.result.status.TransactionFailedMessage;
import org.main.net.api.transaction.messages.result.status.TransactionStatusMessage;
import org.main.net.service.Service;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

// Thread für eingeloggten User und Bearbeitung dessen Transaktionen
public class ClientThread extends Thread {

    // Warteschlange für alle zu bearbeitenden Transaktionen
    Queue<Transaction> pendingTransactions = new ArrayBlockingQueue<>(100);
    // Warteschlange für alle fertig bearbeiteten Transaktionen
    Queue<Transaction> finishedTransactions = new ArrayBlockingQueue<>(100);

    // Socket für erhalten von Nachrichten von anderen Usern
    private ClientSocket responseListenerSocket;

    private boolean loggedIn = false;
    private String username;

    // aktive Threads
    Thread pendingProcesserThread    = new Thread( () -> { processPendingTransactions ();});
    Thread finishedProcesserThread   = new Thread( () -> { processFinishedTransactions();});
    Thread messageSynchronizerThread = new Thread( () -> { syncNotReceivedMsgs        ();});

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLoggedIn() {
        return this.loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public ClientSocket getResponseListenerSocket() {
        return this.responseListenerSocket;
    }

    public void setResponseListenerSocket(ClientSocket responseListenerSocket) {
        this.responseListenerSocket = responseListenerSocket;
    }

    public Thread getPendingProcesserThread(){
        return this.pendingProcesserThread;
    }

    public Thread getFinishedProcesserThread(){
        return this.finishedProcesserThread;
    }

    public Thread getMessageSynchronizerThread(){
        return this.messageSynchronizerThread;
    }

    public Queue<Transaction> getPendingTransactions() {
        return pendingTransactions;
    }

    public Queue<Transaction> getFinishedTransactions() {
        return finishedTransactions;
    }

    public ClientThread(String username) {
        this.username = username;
    }

    public void run() {
        System.out.println("client thread + '"+this.username+"' started");
        pendingProcesserThread.start();
        finishedProcesserThread.start();
        System.out.println("client thread + '"+this.username+"' terminated");
    }

    // schlafen, wenn queue leer
    // server weckt thread auf, wenn neuen Transaktion in queue
    // gelegt wird
    // schlafen, wenn queue leer
    void processPendingTransactions(){

        System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", process pending transactions): " + " started");
        while (true) {

            // processing transactions
            for (Transaction transaction : pendingTransactions) {

                try {
                    System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", process pending transactions): " + " processing transaction "+transaction.getType());
                    Service service = new Service(transaction, this);
                }
                catch (ChatWithUserException | DuplicateMessageException | NoSuchUserException |
                              ParseException | NoSuchApiMessageException e) {

                    System.out.println(e.getMessage());
                    // remove transaction on error
                    pendingTransactions.remove(transaction);
                    // put fail message in transaction, put transaction in finished queue
                    transaction.setMsg(new TransactionFailedMessage(transaction.getTransactionId(), "processing service failed", this.username));
                    finishedTransactions.add(transaction);
                }
                catch(IOException e){

                }
                // removing transaction from pending after processing
                pendingTransactions.remove(transaction);
            }

            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", process pending transactions): " + " interrupted, returning");
                return;
            }
        }
    }
    // schlafen, wenn queue leer
    // server weckt thread auf, wenn neuen Transaktion in queue
    // gelegt wird
    // schlafen, wenn queue leer
    void processFinishedTransactions() {

        System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", process finished transactions): " + " started");
        while (true) {

            for (Transaction transaction : finishedTransactions) {

                System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", process finsished transactions): " + " processing transaction "+transaction.getType());
                System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", process finished transactions): sending response msg: " + " " + transaction.getMsg().getTransactionID());

                try {
                    System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", process finished transactions): waiting for success/fail msg of transaction " + " " + transaction.getMsg().getTransactionID());
                    transaction.getClientSocket().send(transaction.getMsg().toString());
                    System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", process finished transactions): message received from transaction " + " " + transaction.getMsg().getTransactionID());

                } catch (IOException e) {
                    System.out.println("Thread " + Thread.currentThread().getId() + " (" + e.getMessage());
                    System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", process finished transactions): connection lost with receiver socket");
                    System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", returning");
                    return;
                }

                JSONObject json = null;

                try {
                    json = new JSONObject(transaction.getClientSocket().read());
                }
                catch (IOException e) {

                    System.out.println(e.getMessage());
                    System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", process finished transactions): returning");
                    return;
                }

                if (json.get("MESSAGE_TYPE").toString().equals("TRANSACTION_COMPLETE")) {

                    System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", process finished transactions, "+transaction.getTransactionId()+"): Success Message received, transaction complete.");

                    if (transaction.getType() == TransactionType.CONNECT_LISTENER){
                        // synchronisierer thread anschalten, nachdem listener thread des client
                        //  angemeldet und OK Nachricht erhalten wurde
                         this.messageSynchronizerThread.start();
                    }
                    else if (transaction.getType() == TransactionType.LOAD_USER){

                        // erhaltene Nachrichen des Senders müssen nicht mehr gesendet werden

                        System.out.println("--> loading changed messages which do not have to be synchronized anymore after being loaded by user ...");

                        ArrayList<Message> list = Message.loadAllNotSyncedReceiver(this.username);

                        for (var msg : list){

                            System.out.println(msg);

                            // Nachrichten des Empfänger sind nach load user emfangen
                            System.out.println("setting synchronized for message "+msg.getId());
                            msg.setReceived(true);
                            msg.update();
                        }
                    }

                }
                else {
                    System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", process finished transactions, "+transaction.getTransactionId()+"): Fail Message received, transaction complete.");
                }
                System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", process finished transactions): removing transaction "+transaction.getTransactionId());
                finishedTransactions.remove(transaction);
            }
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", process finished transactions): interrupted, returning");
                return;
            }
        }
    }

    public void syncNotReceivedMsgs(){

        System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", synchronizer): start");

        while (true) {

            try {
                this.responseListenerSocket.send(new IsConnectedMessage().toString());
                String response = this.responseListenerSocket.read();
//                System.out.println("String received: "+response);
            }
            catch (IOException e) {

                System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", synchronizer): listener socket disconnected, returning");
                System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", synchronizer): interrupting pending transaction processor thread");
                this.getPendingProcesserThread().interrupt();
                System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", synchronizer): interrupting finished transaction processor thread");
                this.getFinishedProcesserThread().interrupt();
                System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", synchronizer): removing client from client table");

                Server.getInstance().getClients().remove(this.username);

                return;
            }


            // alle an den jetzt angemeldeten Benutzer adressierten Nachrichten
            //  erneut senden und wenn erfolgreich als erhalten markieren
            ArrayList<Message> notReceived = Message.loadAllNotReceived(this.username);
            for (Message message : notReceived) {

                System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", synchronizer): resending message: \n"+message);

                NewMessageServerResponse apiMsg = new NewMessageServerResponse("", message, username);

                // Nachricht senden
                try {
                    this.responseListenerSocket.send(apiMsg.toString());
                }
                catch (IOException e) {
                    System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", synchronizer):" + e.getMessage());
                    System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", synchronizer): connection lost with receiver socket");
                    System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", synchronizer): returning");
                    return;
                }
                JSONObject json = null;
                try {
                    json = new JSONObject(this.responseListenerSocket.read());
                }
                catch (IOException e) {
                    System.out.println(e.getMessage());
                    System.out.println("Thread " + Thread.currentThread().getId() + " (" + this.username + ", synchronizer): returning");
                    return;
                }

                // bei erfolgreichem Senden bzw. erhalten der OK Nachricht
                if (json.get("MESSAGE_TYPE").toString().equals("TRANSACTION_COMPLETE")) {

                    // Nachricht in DB updaten
                    message.setReceived(true);
                    message.update();

                    // anderen User kontaktieren und mitteilen, dass Nachricht jetzt erhalten wurde,
                    //  um Haken zu aktualisieren
                    if (Server.getInstance().getClients().containsKey(message.getSender())) {
                        System.out.println("Thread " + Thread.currentThread().getId() + " synchronizing user " + this.username + "'s received message from user " + message.getSender());
                        UpdateMessageTransaction updateMsg = new UpdateMessageTransaction(Server.getInstance().getClients().get(message.getSender()).responseListenerSocket, new UpdateMsgMessage("", message, message.getReceiver(), ""), message.getReceiver());
                        updateMsg.start();
                        updateMsg.getResult();
                        // Sender hat Nachricht erhalten, dass seine Nachricht angekommen ist,
                        //  keine weitere Aktion erforderlich
                        message.setSynchronized(true);
                        message.update();
                    }
                    else {
                        System.out.println("Thread " + Thread.currentThread().getId() + " cant sync message, sender " + message.getSender() + " not logged in");
                    }
                }
            }

            // Rückantwort an Sender für erhaltene, gesendete Nachrichten, um 2 Haken zu setzen

            // Nachrichten mit Client = Sender, received = true, synced = false
            ArrayList<Message> notSynced = Message.loadAllNotSyncedSender(this.username);
            for (var message : notSynced) {

                if (message.isReceived()) {
                    UpdateMessageTransaction updateMsg = new UpdateMessageTransaction(this.responseListenerSocket, new UpdateMsgMessage("", message, message.getReceiver(), ""), message.getReceiver());
                    updateMsg.start();
                    TransactionStatusMessage response = (TransactionStatusMessage) updateMsg.getResult();
                    if (response.getMESSAGE_TYPE() == MessageType.TRANSACTION_COMPLETE) {

                        message.setSynchronized(true);
                        message.update();
                    }
                }
            }
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }



    }
}
