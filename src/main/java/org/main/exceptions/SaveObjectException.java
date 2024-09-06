package org.main.exceptions;

public class SaveObjectException extends Exception{

    public SaveObjectException(String message, Object o) {
        super("Could not insert object \n" + o + "\n"+message);
    }
}
