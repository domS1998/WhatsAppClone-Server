package org.main.net.api.transaction.messages.init;

import org.json.JSONObject;
import org.main.net.api.NoSuchApiMessageException;
import org.main.model.Message;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.MessageType;

import java.text.ParseException;
import java.util.UUID;

public class SendMessage extends ApiMessage {

    private Message message;

    public Message getMessage() { return this.message; }

    public SendMessage(String transactionId,Message message, String username, String password) {
        super(MessageType.SEND_MESSAGE,transactionId, username, password);
        this.message  = message;
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

    public SendMessage(JSONObject json) throws ParseException, NoSuchApiMessageException {
        super(MessageType.SEND_MESSAGE, json);
        this.setMessageType(MessageType.SEND_MESSAGE);
        this.message     = new Message(json.getJSONObject("message"));
    }


//    public static void main(String[] args) throws ParseException, NoSuchApiMessageException {
//
//        SendMessage msg = new SendMessage(UUID.randomUUID().toString(),new Message("new message", "dan", "rob"), "testuser", "testpassword");
//        System.out.println(msg.toJson());
//        System.out.println(msg);
//        System.out.println(new SendMessage(msg.toJson()));
//
//    }
}
