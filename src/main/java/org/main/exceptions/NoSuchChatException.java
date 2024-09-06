package org.main.exceptions;

public class NoSuchChatException extends Exception {

    public NoSuchChatException (String user1, String user2) {
        super ("Chat ('"+user1+"', '"+user2+"') not in DB!");
    }
}
