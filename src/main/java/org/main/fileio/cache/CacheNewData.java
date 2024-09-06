package org.main.fileio.cache;

import org.main.model.Chat;
import org.main.model.Message;
import org.main.model.User;

import java.io.IOException;

// Schnittstelle für Userdaten Cache
public class CacheNewData extends Cache{

    private final static String FILE_NAME = "newdata.cache";

    // Singleton
    private static CacheNewData uniqueInstance;
    public static CacheNewData getInstance() throws IOException, ClassNotFoundException {
        if (uniqueInstance == null) {
            uniqueInstance = new CacheNewData();
        }
        return uniqueInstance;
    }

    public CacheNewData() throws IOException, ClassNotFoundException {
        super(FILE_NAME);
    }

    public boolean update(User user) {
        return false;
    }

    public boolean update(Chat chat) {
        return false;
    }

    public boolean update(Chat chat, Message message) {
        return false;
    }


}
