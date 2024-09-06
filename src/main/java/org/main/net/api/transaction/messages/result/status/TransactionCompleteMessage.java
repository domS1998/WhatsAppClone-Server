package org.main.net.api.transaction.messages.result.status;

import org.json.JSONObject;
import org.main.net.api.NoSuchApiMessageException;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.MessageType;

public class TransactionCompleteMessage extends TransactionStatusMessage {

    public TransactionCompleteMessage(String transactionId, String username) {
        super(MessageType.TRANSACTION_COMPLETE, transactionId, username, "");
    }

    public TransactionCompleteMessage(JSONObject json) throws NoSuchApiMessageException {
        super(MessageType.TRANSACTION_COMPLETE, json);
    }

    @Override
    public JSONObject toJson() {
        return super.toJson();
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    public TransactionStatusMessage castJson(JSONObject json) throws NoSuchApiMessageException {
        return new TransactionCompleteMessage(json);
    }

}
