package org.main.net.api;

public class NoSuchApiMessageException extends Exception {
    public NoSuchApiMessageException(String type) {
        super("Invalid Api Message Type: " + type);
    }
}
