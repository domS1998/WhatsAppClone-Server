package org.main.net.api.transaction.messages.init;

import org.json.JSONObject;
import org.main.net.api.NoSuchApiMessageException;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.MessageType;

import java.util.UUID;

public class LoadUserMessage extends ApiMessage {

    public LoadUserMessage( String transactionId ,String username, String password) {
        super(MessageType.LOAD_USER, transactionId, username, password);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        return json;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }


    public LoadUserMessage(JSONObject json) throws NoSuchApiMessageException {
        super(MessageType.LOAD_USER, json);
    }

//    public static void main(String[] args) {
//
//        LoadUserMessage msg = new LoadUserMessage(UUID.randomUUID().toString(),"username", "password");
//        System.out.println(msg.toJson());
//        System.out.println(msg.toString());
//        System.out.println(new LoadUserMessage(UUID.randomUUID().toString(),"username", "password").toJson());
//
//    }
}
