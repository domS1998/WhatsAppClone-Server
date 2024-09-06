package org.main.exceptions;

public class DuplicateUserException extends Exception {

    public DuplicateUserException(String username) {
        super("User " + username + " already exists in DB !");
    }
}
