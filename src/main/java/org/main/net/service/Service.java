package org.main.net.service;

import org.json.JSONObject;
import org.main.dao.UserDAO;
import org.main.exceptions.*;
import org.main.model.Chat;
import org.main.model.Message;
import org.main.net.api.NoSuchApiMessageException;
import org.main.model.User;
import org.main.net.ClientThread;
import org.main.net.Server;
import org.main.net.api.transaction.messages.init.*;
import org.main.net.api.transaction.Transaction;
import org.main.net.api.transaction.messages.result.LoadUserResponseMessage;
import org.main.net.api.transaction.messages.result.LoginResponse;
import org.main.net.api.transaction.messages.result.NewMessageServerResponse;
import org.main.net.api.transaction.messages.result.status.TransactionCompleteMessage;
import org.main.net.api.transaction.messages.result.status.TransactionFailedMessage;
import org.main.net.api.transaction.messages.result.status.TransactionStatusMessage;
import java.io.IOException;
import java.text.ParseException;


public class Service {

    public Service (Transaction transaction, ClientThread clientThread) throws IOException, ChatWithUserException, DuplicateMessageException, NoSuchUserException, ParseException, NoSuchApiMessageException {


                switch (transaction.getType()) {

                    case LOGIN -> {

                        if (clientThread.isLoggedIn()){
                            System.out.println("Thread " + Thread.currentThread().getId() +" ("+ clientThread.getUsername() + "): " + "user "+ clientThread.getUsername() +" already logged in");
                            break;
                        }

                        LoginMessage loginMessage = (LoginMessage) transaction.getMsg();
                        System.out.println( "Thread " + Thread.currentThread().getId() +" ("+ clientThread.getUsername() + "): " + "Service: login, check for user '" + loginMessage.getUsername() + "' with password '" + loginMessage.getPassword() +"' in database");
                        LoginResponse response;
                        try {
                            UserDAO.login(loginMessage.getUsername(), loginMessage.getPassword());
                            response = new LoginResponse(loginMessage.getTransactionID(),true, true , true, loginMessage.getUsername(), "");
                            System.out.println( "Thread " + Thread.currentThread().getId() +" ("+ loginMessage.getUsername() + "): " + "user found in db");
                        } catch (WrongPasswordException e) {
                            response = new LoginResponse(loginMessage.getTransactionID(),false, true, false, loginMessage.getUsername(), "");
                            System.out.println("Thread " + Thread.currentThread().getId() +" ("+ loginMessage.getUsername() + "): " + "user " + loginMessage.getUsername() + " not found in db");
                        } catch (NoSuchUserException e) {
                            response = new LoginResponse(loginMessage.getTransactionID(),false, false, true, loginMessage.getUsername(), "");
                            System.out.println("Thread " + Thread.currentThread().getId() +" ("+ loginMessage.getUsername() + "): " + "user " + loginMessage.getUsername() + " found in db but password " + loginMessage.getPassword() + " was wrong");
                        }

                        // zu tabelle, wenn in db
                        if (response.isLoggedIn()) {
                            clientThread.setLoggedIn(true);
                            System.out.println("-->"+loginMessage.getUsername());
                            Server.getInstance().getClients().put(loginMessage.getUsername(), clientThread);
                            System.out.println("---> "+Server.getInstance().getClients());
                            System.out.println("--> contains user "+loginMessage.getUsername()+": "+Server.getInstance().getClients().contains(loginMessage.getUsername()));
                            System.out.println("Thread " + Thread.currentThread().getId() +" ("+ clientThread.getUsername() + "): " + "login success, putting response message in user's finished queue");
                            clientThread.setLoggedIn(true);
                        }
                        else {
                            System.out.println("Thread " + Thread.currentThread().getId() + " (" + clientThread.getUsername() + "): " + "login fail, putting response message in user's finished queue");
                        }
                        transaction.setMsg(response);
                        clientThread.getFinishedTransactions().add(transaction);

                    }

                    case LOAD_USER -> {

                        LoadUserMessage loadUserMessage = (LoadUserMessage) transaction.getMsg();
                        System.out.println("Service: loading user ('"+loadUserMessage.getUsername()+"' with password '"+loadUserMessage.getPassword()+"')");
                        // searching logged in user in table
                        System.out.println("looking if user '"+loadUserMessage.getUsername()+"' is in client table ...");
                        boolean containsReceiver = Server.getInstance().getClients().containsKey(loadUserMessage.getUsername());
                        if (containsReceiver) {
                            System.out.println("user '" + loadUserMessage.getUsername() + "' is logged into server");
                            // Userdaten laden
                            System.out.println("pulling user '"+loadUserMessage.getUsername()+" from database ...");

                            User user = new User();
                            try {
                                user.load(loadUserMessage.getUsername(), loadUserMessage.getPassword());
                            }
                            catch (NoSuchUserException e) {
                                System.out.println(e.getMessage());
                            }
                            catch (WrongPasswordException | NoSuchChatException | ChatWithUserException e) {
                                System.out.println(e.getMessage());
                            }

                            System.out.println("user json object retrieved from database: " + user.toJson());
                            LoadUserResponseMessage response = new LoadUserResponseMessage(loadUserMessage.getTransactionID(), user);

                            // Nachricht zurück and anfragenden User
                            System.out.println("json message to send: " + response.toString());
                            System.out.println("Thread " + Thread.currentThread().getId() +" ("+ clientThread.getUsername() + "): " + "load user success, putting response message in user's finished queue");
                            transaction.setMsg(response);
                            clientThread.getFinishedTransactions().add(transaction);

                        }
                        else {
                            System.out.println("user '" + loadUserMessage.getUsername() + "' is not logged into server, cant load user before login.");
                        }

                    }

                    case CONNECT_LISTENER -> {

                        System.out.println("Service: connect listener, user "+transaction.getUsername()+" in table: "+Server.getInstance().getClients().contains(transaction.getUsername()));

                        ConnectListenerMessage connectListenerMessage = (ConnectListenerMessage) transaction.getMsg();
                        String msgUser = connectListenerMessage.getUsername();

                        // wenn user in tabelle / eingeloggt
                        if (Server.getInstance().getClients().containsKey(msgUser)){
                            System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "user " + msgUser+" found in user table. Passing socket to users thread");
                            Server.getInstance().getClients().get(msgUser).setResponseListenerSocket(transaction.getClientSocket());
                            System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "sending transaction success message");
                            TransactionCompleteMessage response = new TransactionCompleteMessage(connectListenerMessage.getTransactionID(), msgUser);
                            transaction.setMsg(response);
                            clientThread.getFinishedTransactions().add(transaction);
                        }
                        else {
                            System.out.println("Thread " + Thread.currentThread().getId() + " (await): " + "user " + msgUser+" not found in user table. Sending fail message");
                            TransactionFailedMessage response = new TransactionFailedMessage(connectListenerMessage.getTransactionID(), "user not logged in", msgUser);
                            transaction.setMsg(response);
                            clientThread.getFinishedTransactions().add(transaction);
                        }


                    }

                    case SEND_MESSAGE -> {

                        System.out.println("Service: SEND_MESSAGE");

                        // erhaltenen Nachricht typecasten
                        SendMessage msg = (SendMessage) transaction.getMsg();
                        String transactionId = msg.getTransactionID();

                        // Message rausholen
                        Message message = msg.getMessage();

                        // inserting new message into db
                        System.out.println("storing messsage in DB ... ");
                        message.insert();

                        // searching logged in user in table
                        System.out.println("looking if receiver '" + message.getReceiver() + "' is in table ...");
                        boolean containsReceiver = Server.getInstance().getClients().containsKey(message.getReceiver());

                        if (containsReceiver) {
                            // receiver - Thread holen, wenn angemeldet
                            System.out.println("user '" + message.getReceiver() + "' is logged into server");
                            System.out.println("sending new message '" + message + "' to response listener user '" + message.getReceiver() + "' ...");
                            ClientThread receiverThread = Server.getInstance().getClients().get(message.getReceiver());
                            // Senden
                            receiverThread.getResponseListenerSocket().send(new NewMessageServerResponse(transactionId, message, transaction.getUsername()).toString());
                            // warten auf success message
                            String response = receiverThread.getResponseListenerSocket().read();
                            JSONObject json = new JSONObject(response);
                            String type = json.getString("MESSAGE_TYPE");
                            String receivedId = json.getString("transactionId");
                            if (type.equals("TRANSACTION_COMPLETE") /*&& transaction.getTransactionId().equals(receivedId) */) {
                                transaction.setMsg(new TransactionCompleteMessage(transaction.getTransactionId(), clientThread.getUsername()));
                                clientThread.getFinishedTransactions().add(transaction);

                                // synchronized = true in db
//                                Message msg = ((SendMessage)transaction.getMsg()).getMessage();
                                System.out.println("Thread " + Thread.currentThread().getId() + ": " + transaction.getTransactionId()+"): setting synchronized field in db for message "+msg);
                                message.setReceived(true);
                                message.update();
                            }
                            else {
                                transaction.setMsg(new TransactionFailedMessage(transaction.getTransactionId(), "ok message from receiver not received", clientThread.getUsername()));
                                clientThread.getFinishedTransactions().add(transaction);
                            }
                        } else {
                            System.out.println("user '" + message.getReceiver() + "' is not logged into server. inserting new message into db only.");
                            transaction.setMsg(new TransactionFailedMessage(transaction.getTransactionId(), "ok message from receiver not received", clientThread.getUsername()));
                            clientThread.getFinishedTransactions().add(transaction);
                        }
                    }

                    case CREATE_CHAT -> {

                        System.out.println("Service: CREATE CHAT");

                        // chat extrahieren
                        CreateChatMessage msg = (CreateChatMessage) transaction.getMsg();
                        Chat chat = msg.getChat();

                        // chat in db speichern
                        TransactionStatusMessage response = null;
                        try {
                            chat.insert();
                            response = new TransactionCompleteMessage(transaction.getTransactionId(),clientThread.getUsername());
                        }
                        catch (SaveObjectException e) {
                            System.out.println(e.getMessage());
                            response = new TransactionFailedMessage(transaction.getTransactionId(), "target user does not exist in db", clientThread.getUsername());
                        }
                        catch (DuplicateChatExeption e) {
                            System.out.println(e.getMessage());
                            response = new TransactionFailedMessage(transaction.getTransactionId(), "chat does already exist", clientThread.getUsername());
                        }
                        transaction.setMsg(response);
                        clientThread.getFinishedTransactions().add(transaction);
                    }

                    case UPDATE_MESSAGE -> {

                        System.out.println("Service: UPDATE MESSAGE");

                        // chat extrahieren
                        UpdateMsgMessage msg = (UpdateMsgMessage) transaction.getMsg();
                        Message message = msg.getMessage();

                        // message in db updaten
                        message.update();

                        transaction.setMsg(new TransactionCompleteMessage(transaction.getTransactionId(), clientThread.getUsername()));
                        clientThread.getFinishedTransactions().add(transaction);
                    }

                    default -> {
                        System.out.println("invalid service type: transaction type'" + transaction.getType()+"'");
                    }
                }


    }
}
