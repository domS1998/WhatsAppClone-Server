package org.main.exceptions;

public class DuplicateMessageException extends Exception{

    public DuplicateMessageException(String id) {
        super ("Message with id " + id + " already exists in DB!");
    }
}
