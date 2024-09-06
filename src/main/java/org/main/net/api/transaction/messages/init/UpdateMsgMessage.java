package org.main.net.api.transaction.messages.init;

import org.json.JSONObject;
import org.main.model.Message;
import org.main.net.api.NoSuchApiMessageException;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.MessageType;

import java.text.ParseException;

public class UpdateMsgMessage extends ApiMessage {

    private Message message;

    public Message getMessage() { return this.message; }

    public UpdateMsgMessage(String transactionId, Message message, String username, String password) {
        super(MessageType.UPDATE_MESSAGE ,transactionId, username, password);
        this.message  = message;
    }

    public UpdateMsgMessage(JSONObject json) throws ParseException, NoSuchApiMessageException {
        super(MessageType.UPDATE_MESSAGE, json);
        this.setMessageType(MessageType.UPDATE_MESSAGE);
        this.message     = new Message(json.getJSONObject("message"));
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("message", this.message.toJson());
        return json;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }



}
