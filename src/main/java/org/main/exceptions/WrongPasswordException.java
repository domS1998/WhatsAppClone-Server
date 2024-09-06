package org.main.exceptions;

public class WrongPasswordException extends Exception{

    public WrongPasswordException(String username, String password) {
        super("Wrong password for user '"+username+"' ("+ password+")");
    }
}
