package org.main.net.api.transaction.messages.init;

import org.json.JSONException;
import org.json.JSONObject;
import org.main.net.api.NoSuchApiMessageException;
import org.main.net.api.transaction.messages.ApiMessage;
import org.main.net.api.transaction.messages.MessageType;

// Nachricht für Anfrage und Zuordnung eines passiven
//  Listener-Sockets für eingehende Nachrichten, die nicht
//  von einer vom Benutzer ausgelösten Transaktion stammen,
//  z.B. für eingehende neue Nachrichten bzw. Anfragen von
//  einem anderen Benutzer über den Server
public class ConnectListenerMessage extends ApiMessage {

    @Override
    public JSONObject toJson() {
        JSONObject json = super.toJson();
        return json;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }

    public ConnectListenerMessage(String transactionID, String username, String password) {
        super(MessageType.CONNECT_LISTENER, transactionID, username, password);
    }

    public ConnectListenerMessage (JSONObject json) throws NoSuchApiMessageException, JSONException {
        super(MessageType.CONNECT_LISTENER, json);
    }
}
