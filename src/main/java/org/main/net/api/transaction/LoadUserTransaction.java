package org.main.net.api.transaction;

import org.json.JSONException;
import org.json.JSONObject;
import org.main.net.ClientSocket;
import org.main.net.api.NoSuchApiMessageException;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.init.LoadUserMessage;
import org.main.net.api.transaction.messages.result.LoadUserResponseMessage;


import java.text.ParseException;

public class LoadUserTransaction extends Transaction {

    public LoadUserTransaction(String username, String password) {
        super(TransactionType.LOAD_USER, new LoadUserMessage("",username, password), username);
    }

    // Konstruktor für Transktions-Weiterverarbeitung von Sever
    public LoadUserTransaction(String username, ClientSocket clientSocket, LoadUserMessage loadUserMessage) {
        super(username, clientSocket,TransactionType.LOAD_USER,loadUserMessage);
    }


    public LoadUserResponseMessage getResult(){
        return (LoadUserResponseMessage) super.getResult();
    }

    @Override
    protected ApiMessage castResultMessage(JSONObject json) throws JSONException, NoSuchApiMessageException, ParseException {
        return new LoadUserResponseMessage(json);
    }
}
