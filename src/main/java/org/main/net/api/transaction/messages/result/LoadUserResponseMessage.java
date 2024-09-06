package org.main.net.api.transaction.messages.result;

import org.json.JSONException;
import org.json.JSONObject;
import org.main.net.api.NoSuchApiMessageException;
import org.main.model.User;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.MessageType;

import java.text.ParseException;
import java.util.UUID;

public class LoadUserResponseMessage extends ApiMessage {

    private User user;

    public User getUser () {return this.user;}
    public void setUser(User user) {this.user = user;}


    public LoadUserResponseMessage(String transactionId, User user) {
        super(MessageType.LOAD_USER_RESPONSE, transactionId, user.getUsername(), user.getPassword());
        this.user = user;
    }


    public JSONObject toJson(){
        JSONObject json = super.toJson();
        json.put("user", user.toJson());

        return json;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }


    // Konstruktor um Json String aus Socket zu Objekt zu konvertieren
    public LoadUserResponseMessage (JSONObject json) throws JSONException, ParseException, NoSuchApiMessageException {
        super(MessageType.LOAD_USER_RESPONSE, json);
        this.user = new User (json.getJSONObject("user"));
    }


//    public static void main(String[] args) {
//
//        LoadUserResponseMessage msg = new LoadUserResponseMessage(UUID.randomUUID().toString(),new User());
//        System.out.println(msg.toJson());
//        System.out.println(msg.toString());
//        System.out.println(new LoadUserResponseMessage(UUID.randomUUID().toString(),new User()).toJson());
//
//    }
}
