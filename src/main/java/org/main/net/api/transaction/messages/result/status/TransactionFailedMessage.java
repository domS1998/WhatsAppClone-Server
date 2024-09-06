package org.main.net.api.transaction.messages.result.status;

import org.json.JSONObject;
import org.main.net.api.NoSuchApiMessageException;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.MessageType;

public class TransactionFailedMessage extends TransactionStatusMessage {

    private String error;

    public String getError(){return error;}

    public TransactionFailedMessage (String transactionId, String error, String username) {
        super(MessageType.TRANSACTION_FAIL, transactionId, username, "");
        this.error = error;
    }

    public TransactionFailedMessage(JSONObject json) throws NoSuchApiMessageException {
        super(MessageType.TRANSACTION_FAIL, json);
        this.error = json.getString("error");
    }

    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("error", this.getError());
        return json;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    public TransactionStatusMessage castJson(JSONObject json) throws NoSuchApiMessageException {
        return new TransactionFailedMessage(json);
    }
}

