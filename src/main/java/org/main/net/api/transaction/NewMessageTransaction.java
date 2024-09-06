package org.main.net.api.transaction;

import org.json.JSONException;
import org.json.JSONObject;
import org.main.model.Message;
import org.main.net.ClientSocket;
import org.main.net.api.NoSuchApiMessageException;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.MessageType;
import org.main.net.api.transaction.messages.init.ConnectListenerMessage;
import org.main.net.api.transaction.messages.init.SendMessage;
import org.main.net.api.transaction.messages.result.status.TransactionCompleteMessage;
import org.main.net.api.transaction.messages.result.status.TransactionFailedMessage;
import org.main.net.api.transaction.messages.result.status.TransactionStatusMessage;

import java.text.ParseException;

public class NewMessageTransaction extends Transaction {

    public NewMessageTransaction(String username, String password, Message message) {
        super(TransactionType.SEND_MESSAGE, new SendMessage("", message,username, password), username);
    }


    public NewMessageTransaction(String username, ClientSocket clientSocket, SendMessage msg) {
        super(username, clientSocket, TransactionType.SEND_MESSAGE, msg);
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
