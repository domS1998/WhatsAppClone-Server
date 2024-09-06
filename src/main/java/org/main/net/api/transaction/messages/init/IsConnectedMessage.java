package org.main.net.api.transaction.messages.init;

import org.json.JSONObject;
import org.main.model.Message;
import org.main.net.api.NoSuchApiMessageException;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.MessageType;

import java.text.ParseException;

public class IsConnectedMessage extends ApiMessage {

    public IsConnectedMessage() {
        super(MessageType.IS_CONNECTED_REQUEST ,"", "", "");
    }

    public IsConnectedMessage(JSONObject json) throws ParseException, NoSuchApiMessageException {
        super(MessageType.IS_CONNECTED_REQUEST, json);
        this.setMessageType(MessageType.IS_CONNECTED_REQUEST);
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



}
