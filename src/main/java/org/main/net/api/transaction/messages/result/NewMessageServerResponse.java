package org.main.net.api.transaction.messages.result;

import org.json.JSONObject;
import org.main.net.api.NoSuchApiMessageException;
import org.main.model.Message;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.MessageType;

import java.text.ParseException;
import java.util.UUID;

public class NewMessageServerResponse extends ApiMessage {

    private Message message;

    public Message getMessage() {return this.message;}

    @Override
    public MessageType getMESSAGE_TYPE() {return super.getMESSAGE_TYPE();}

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("message", message.toJson());
        return json;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    public NewMessageServerResponse(String transactionId,Message message, String username) {
        super(MessageType.NEW_MESSAGE_FROM_SERVER, transactionId, username, "");
        this.message = message;
    }

    public NewMessageServerResponse(JSONObject json) throws ParseException, NoSuchApiMessageException {
        super(MessageType.NEW_MESSAGE_FROM_SERVER, json);
        this.message = new Message(json.getJSONObject("message"));

    }


//    public static void main(String[] args) throws ParseException, NoSuchApiMessageException {
//
//        NewMessageServerResponse response = new NewMessageServerResponse(UUID.randomUUID().toString(),new Message("new message", "rob", "dan"), "testuser");
//        System.out.println(response.toJson());
//        System.out.println(response);
//        System.out.println(new NewMessageServerResponse(response.toJson()));
//    }
}
