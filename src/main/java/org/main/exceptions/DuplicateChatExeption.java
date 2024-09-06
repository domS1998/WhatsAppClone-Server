package org.main.exceptions;

import org.main.model.User;

public class DuplicateChatExeption extends Exception {

    public DuplicateChatExeption(String userName, String targetUserName) {
        super("Chat ('"+userName + ", " + targetUserName +"') already exists in database!\n");
    }
}
