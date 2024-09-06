package org.main.net.api.transaction.messages;

import org.json.JSONObject;
import org.main.net.api.NoSuchApiMessageException;

public abstract class ApiMessage {

    protected MessageType MESSAGE_TYPE;
    protected String TRANSACTION_ID;
    protected String username;
    private String password;

    public MessageType getMESSAGE_TYPE() {return this.MESSAGE_TYPE;}
    public void setMessageType(MessageType MESSAGE_TYPE) {this.MESSAGE_TYPE = MESSAGE_TYPE;}
    public String getTransactionID() {return this.TRANSACTION_ID;}
    public void setTransactionID(String TRANSACTION_ID) {this.TRANSACTION_ID = TRANSACTION_ID;}
    public String getUsername() {return this.username;}
    public void setUsername(String username) {this.username = username;}
    public String getPassword() {return this.password;}
    public void setPassword(String password) {this.password = password;}

    public ApiMessage(MessageType messageType, String transactionID, String username, String password) {
        this.MESSAGE_TYPE = messageType;
        this.TRANSACTION_ID = transactionID;
        this.username = username;
        this.password = password;
    }
    public ApiMessage(MessageType msgType, JSONObject json) throws NoSuchApiMessageException {
        this.MESSAGE_TYPE = msgType;
        String type = json.getString("MESSAGE_TYPE");
        if ( ! type.equals(this.getMESSAGE_TYPE().toString())) {throw new NoSuchApiMessageException("type");}
        this.setTransactionID(json.getString("transactionId"));
        this.MESSAGE_TYPE = msgType;
        this.username = json.getString("username");
        this.password = json.getString("password");
    }

    public ApiMessage () {}

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("MESSAGE_TYPE", this.getMESSAGE_TYPE());
        json.put("transactionId", this.getTransactionID());
        json.put("username", this.getUsername());
        json.put("password", this.getPassword());
        return json;
    }

    public String toString(){
        return this.toJson().toString();
    }
}
