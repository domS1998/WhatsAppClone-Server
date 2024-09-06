package org.main.net.api.transaction.messages.init;

import org.json.JSONException;
import org.json.JSONObject;
import org.main.model.Chat;
import org.main.net.api.NoSuchApiMessageException;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.MessageType;

import java.text.ParseException;


public class CreateChatMessage extends ApiMessage {

    private Chat chat;

    public Chat getChat(){return this.chat;}
    public void setChat(Chat chat){this.chat = chat;}

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("Chat", this.chat.toJson());
        return json;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    public CreateChatMessage(String transactionID, String username, String password, Chat chat) {
        super(MessageType.CREATE_CHAT, transactionID, username, password);
        this.chat = chat;
    }

    public CreateChatMessage (JSONObject json) throws NoSuchApiMessageException, JSONException {
        super(MessageType.CREATE_CHAT, json);
        try { this.chat = new Chat(json.getJSONObject("chat"));}
        catch (ParseException e) { System.out.println(e.getMessage()); }
    }
}
