package org.main.exceptions;

public class ChatWithUserException extends Exception{

    public ChatWithUserException(String user1, String user2) {
        super("cannot create chat with same user ('" + user1 +"', '" + user2 + "')");
    }
}
