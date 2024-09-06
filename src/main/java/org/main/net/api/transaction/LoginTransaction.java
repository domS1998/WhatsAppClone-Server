package org.main.net.api.transaction;

import org.json.JSONException;
import org.json.JSONObject;
import org.main.net.ClientSocket;
import org.main.net.api.NoSuchApiMessageException;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.init.LoginMessage;
import org.main.net.api.transaction.messages.result.LoginResponse;

import java.text.ParseException;


public class LoginTransaction extends Transaction {

    public LoginTransaction(String username, String password) {
        super(TransactionType.LOGIN, new LoginMessage("",username, password), username);
    }

    // Konstruktor für Transktions-Weiterverarbeitung von Sever
    public LoginTransaction(String username, ClientSocket clientSocket, LoginMessage loginMessage) {
        super(username, clientSocket,TransactionType.LOGIN, loginMessage);

    }

    // Funktion, um die Antwortnachricht dynamisch zu casten
    protected ApiMessage castResultMessage (JSONObject json) throws JSONException, NoSuchApiMessageException {
        return new LoginResponse(json);
    }

    public LoginResponse getResult(){
        return (LoginResponse) super.getResult();
    }




}
