package org.main.net.api.transaction.messages.init;

import org.json.JSONException;
import org.json.JSONObject;
import org.main.net.api.NoSuchApiMessageException;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.MessageType;

import java.util.UUID;

public class LoginMessage extends ApiMessage {

    public LoginMessage(String transactionId, String username, String password) {
        super(MessageType.LOGIN, transactionId, username, password);
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

    public LoginMessage(JSONObject json) throws NoSuchApiMessageException, JSONException {
        super(MessageType.LOGIN, json);
    }


//    public static void main(String[] args) throws NoSuchApiMessageException {
//
//            LoginMessage msg = new LoginMessage(UUID.randomUUID().toString(), "username", "password");
//            System.out.println(msg.toJson());
//            System.out.println(new LoginMessage(msg.toJson()));
//    }


}
