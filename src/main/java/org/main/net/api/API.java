package org.main.net.api;


import org.main.net.api.transaction.Transaction;
import org.main.net.api.transaction.TransactionType;

import java.util.UUID;

public class API {

    // singleton
    static API uniqueInstance;

    static API getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new API();
        }
        return uniqueInstance;
    }

    private API() {}

    public Transaction startTransaction(TransactionType transaction){return null;};



}
