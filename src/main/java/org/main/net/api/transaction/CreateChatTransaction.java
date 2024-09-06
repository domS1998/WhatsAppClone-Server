package org.main.net.api.transaction;

import org.json.JSONException;
import org.json.JSONObject;
import org.main.model.Chat;
import org.main.net.ClientSocket;
import org.main.net.api.NoSuchApiMessageException;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.MessageType;
import org.main.net.api.transaction.messages.init.ConnectListenerMessage;
import org.main.net.api.transaction.messages.init.CreateChatMessage;
import org.main.net.api.transaction.messages.result.status.TransactionCompleteMessage;
import org.main.net.api.transaction.messages.result.status.TransactionFailedMessage;
import org.main.net.api.transaction.messages.result.status.TransactionStatusMessage;

import java.text.ParseException;

public class CreateChatTransaction extends Transaction {




    public CreateChatTransaction(String username, String password, Chat chat) {
        super(TransactionType.CREATE_CHAT, new CreateChatMessage("",username, password, chat), username);
    }

    public CreateChatTransaction(ClientSocket clientSocket, CreateChatMessage msg, String username) {
        super(username,clientSocket, TransactionType.CREATE_CHAT, msg);
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
