package org.main.net.api.transaction;

import org.json.JSONException;
import org.json.JSONObject;
import org.main.net.ClientSocket;
import org.main.net.api.NoSuchApiMessageException;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.MessageType;
import org.main.net.api.transaction.messages.init.ConnectListenerMessage;
import org.main.net.api.transaction.messages.init.LoadUserMessage;
import org.main.net.api.transaction.messages.result.status.TransactionCompleteMessage;
import org.main.net.api.transaction.messages.result.status.TransactionFailedMessage;
import org.main.net.api.transaction.messages.result.status.TransactionStatusMessage;

import java.text.ParseException;

public class ConnectListenerTransaction extends Transaction {

    public ConnectListenerTransaction(String username, String password) {
        super(TransactionType.CONNECT_LISTENER, new ConnectListenerMessage("",username, password), username);
    }

    // Konstruktor für Transktions-Weiterverarbeitung von Sever
    public ConnectListenerTransaction(String username, ClientSocket clientSocket, ConnectListenerMessage msg) {
        super(username, clientSocket,TransactionType.CONNECT_LISTENER,msg);
    }


    public TransactionStatusMessage getResult(){
        return (TransactionStatusMessage) super.getResult();
    }

    @Override
    protected ApiMessage castResultMessage(JSONObject json) throws JSONException, NoSuchApiMessageException, ParseException {
        String type = json.getString("MESSAGE_TYPE");
        if (type.equals(MessageType.TRANSACTION_COMPLETE.toString())){
            return new TransactionCompleteMessage(json);
        }
        if (type.equals(MessageType.TRANSACTION_FAIL.toString())){
            return new TransactionFailedMessage(json);
        }
        return null;
    }


}
