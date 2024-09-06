package org.main.net.api.transaction.messages.result;

import org.json.JSONObject;
import org.main.net.api.NoSuchApiMessageException;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.MessageType;

import java.util.UUID;

public class LoginResponse extends ApiMessage {

    private final boolean loggedIn;
    private final boolean usernameOK;
    private final boolean passwordOK;

    public boolean isLoggedIn() {return this.loggedIn;}
    public boolean isUsernameOK() {return this.usernameOK;}
    public boolean isPasswordOK() {return this.passwordOK;}

    public LoginResponse(String transactionId, boolean loggedIn, boolean usernameOK, boolean passwordOK, String username, String password) throws NoSuchApiMessageException {
        super(MessageType.LOGIN_RESPONSE, transactionId, username, password);
        this.loggedIn = loggedIn;
        this.usernameOK = usernameOK;
        this.passwordOK = passwordOK;
    }


    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        json.put("usernameOK", usernameOK);
        json.put("passwordOK", passwordOK);
        json.put("loggedIn", loggedIn);
        return json;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    public LoginResponse(JSONObject json) throws NoSuchApiMessageException {
        super(MessageType.LOGIN_RESPONSE, json);
        this.loggedIn = json.getBoolean("loggedIn");
        this.usernameOK = json.getBoolean("usernameOK");
        this.passwordOK = json.getBoolean("passwordOK");
    }

//    public static void main(String[] args) throws NoSuchApiMessageException {
//
//        LoginResponse loginResponse = new LoginResponse(UUID.randomUUID().toString(), true, true, true, "testuser", "testpassword");
//        System.out.println(loginResponse.toJson());
//        System.out.println(loginResponse.toString());
//        System.out.println(new LoginResponse(loginResponse.toJson()));
//
//    }
}
