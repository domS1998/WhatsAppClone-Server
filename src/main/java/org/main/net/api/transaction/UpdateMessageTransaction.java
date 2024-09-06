package org.main.net.api.transaction;

import org.json.JSONException;
import org.json.JSONObject;
import org.main.model.Message;
import org.main.net.ClientSocket;
import org.main.net.api.NoSuchApiMessageException;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.MessageType;
import org.main.net.api.transaction.messages.init.UpdateMsgMessage;
import org.main.net.api.transaction.messages.result.status.TransactionCompleteMessage;
import org.main.net.api.transaction.messages.result.status.TransactionFailedMessage;

import java.text.ParseException;

public class UpdateMessageTransaction extends Transaction {


    public UpdateMessageTransaction(String username, String password, Message msg) {
        super(TransactionType.UPDATE_MESSAGE, new UpdateMsgMessage("", msg, username, password), username);
    }

    public UpdateMessageTransaction(ClientSocket clientSocket, ApiMessage initMsg, String username) {
        super(username, clientSocket, TransactionType.UPDATE_MESSAGE, initMsg);
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
