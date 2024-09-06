package org.main.fileio.cache;

import org.main.model.User;
import java.io.IOException;


// Schnittstelle für Userdaten Cache
public class CacheUserstate extends Cache{

    private static final String FILE_NAME = "user.cache";

    // Singleton
    private static CacheUserstate uniqueInstance;
    public static CacheUserstate getInstance() throws IOException, ClassNotFoundException {
        if (uniqueInstance == null) {
            uniqueInstance = new CacheUserstate();
        }
        return uniqueInstance;
    }

    public User getUser () throws IOException, ClassNotFoundException {
        return (User)this.content;
    }

    // Konstruktor
    private CacheUserstate() throws IOException, ClassNotFoundException {
        super(FILE_NAME);
    }

    public boolean update(User user) throws IOException, ClassNotFoundException {
        this.write(user);           // Cache überschreiben
        this.content = this.load(); // Neuen Inhalt laden
        // false, wenn nicht Erfolgreic
        if (this.content != null && this.content == user) { return true;}
        return false;
    }

//    public static void main(String[] args) throws IOException, ClassNotFoundException {
//
//        CacheUserstate.getInstance().print();
//        System.out.println(CacheUserstate.getInstance().isEmpty());
//        System.out.println(CacheUserstate.getInstance().getUser());
//
//        CacheUserstate.getInstance().update(new User("dan", "dan124"));
//        CacheUserstate.getInstance().print();
//        System.out.println("User: "+CacheUserstate.getInstance().getUser());
//        System.out.println(CacheUserstate.getInstance().isEmpty());
//
////        CacheUserstate.getInstance().clear();
////        CacheUserstate.getInstance().print();
////        System.out.println(CacheUserstate.getInstance().getUser());
////        System.out.println(CacheUserstate.getInstance().isEmpty());
//
////        CacheUserstate.getInstance().write(new User("dan", "dan123"));
////        System.out.println(CacheUserstate.getInstance().getUser());
//
//    }


}
