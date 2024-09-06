package org.main.net.api.transaction.messages.result.status;

import org.json.JSONObject;
import org.main.net.api.NoSuchApiMessageException;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.MessageType;

abstract public class TransactionStatusMessage extends ApiMessage {

    public TransactionStatusMessage(MessageType messageType, String transactionID, String username, String password) {
        super(messageType, transactionID, username, password);
    }

    public TransactionStatusMessage(MessageType type, JSONObject json) throws NoSuchApiMessageException {
        super(type, json);
    }

    @Override
    public JSONObject toJson() {
        return super.toJson();
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    abstract public TransactionStatusMessage castJson(JSONObject json) throws NoSuchApiMessageException;



}
