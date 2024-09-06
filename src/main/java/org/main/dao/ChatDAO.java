package org.main.dao;

import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.main.exceptions.*;
import org.main.model.Chat;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "chat")
public class ChatDAO {


    @Id private @OneToOne @JoinColumn (name = "user1", referencedColumnName = "username") UserDAO user1 = null;
    @Id private @OneToOne @JoinColumn (name = "user2", referencedColumnName = "username") UserDAO user2 = null;

    public UserDAO getUser1() {return this.user1;}
    public UserDAO getUser2() {return this.user2;}
    public void setUser1(UserDAO user1) {this.user1 = user1;}
    public void setUser2(UserDAO user2) {this.user2 = user2;}

    @Override
    public String toString() {
        return "ChatDAO {" +
                "user1=" + user1.getUsername() + "," +
                "user2=" + user2.getUsername() + "" +
                '}';
    }

    public ChatDAO() {}

    public ChatDAO(Chat chat) {
        this.user1 = new UserDAO (chat.getUser1());
        this.user2 = new UserDAO (chat.getUser2());
    }

    public ChatDAO(String user1, String user2) {
        this.user1 = new UserDAO (user1);
        this.user2 = new UserDAO (user2);
    }

    public static boolean inDB (String user1, String user2) {
        if ( ! UserDAO.inDB(user1) ) return false;
        if ( ! UserDAO.inDB(user2) ) return false;
        ChatDAO dao = new ChatDAO();
        try { dao.load(user1, user2); }
        catch (NoSuchChatException|ChatWithUserException e) { return false; }
        return true;
    }

    public void insert() throws ChatWithUserException, SaveObjectException, DuplicateChatExeption {

        if (this.user1.getUsername().equals(this.user2.getUsername())){
            throw new ChatWithUserException(this.user1.getUsername(), this.user2.getUsername());
        }

        if (ChatDAO.inDB(this.user1.getUsername(), this.user2.getUsername())) {
            throw new DuplicateChatExeption(this.user1.getUsername(), this.user2.getUsername());
        }

//        SessionFactory factory = new Configuration()
//                .configure("hibernate/hibernate.cfg.xml")
//                .addAnnotatedClass(ChatDAO.class)
//                .addAnnotatedClass(UserDAO.class)
//                .buildSessionFactory();
//
//        Session session = factory.getCurrentSession();

        Session session = Database.getInstance().getSession();

        if ( ! session.getTransaction().isActive()) {
            session.beginTransaction();
        }

        session.save(this);
        try { session.getTransaction().commit(); }
        catch (Exception e) {
//            session.close();
//            factory.close();
            throw new SaveObjectException(e.getMessage(), this);
        }
//        session.close();
//        factory.close();
    }


    public void load (String user1, String user2) throws NoSuchChatException, ChatWithUserException {

        if (user1.equals(user2)) throw new ChatWithUserException(user1, user2);

//        SessionFactory factory = new Configuration()
//                .configure("hibernate/hibernate.cfg.xml")
//                .addAnnotatedClass(ChatDAO.class)
//                .addAnnotatedClass(UserDAO.class)
//                .buildSessionFactory();
//
//        Session session = factory.getCurrentSession();

        Session session = Database.getInstance().getSession();

        session.beginTransaction();

        // Chat mit Nutzerkombination aus DB ziehen
        String hql  = "from ChatDAO C where C.user1.username='"+user1+"'";
               hql += "and C.user2.username='"+user2+"'";
               hql += "or C.user1.username='"+user2+"'";
               hql += "and C.user2.username='"+user1+"'";

        Query query = session.createQuery(hql);
        List results = query.getResultList();

        if (results.size() == 0) {
//            session.close();
//            factory.close();
            throw new NoSuchChatException(user1, user2);
        }

        // Einziges Ergebnis in aktuelles Objekt einladen
        ChatDAO result = (ChatDAO) results.get(0);
        this.user1 = result.user1;
        this.user2 = result.user2;

        // verbindung mit DB schließen
        session.getTransaction().commit();
//        session.close();
//        factory.close();
    }

    public static ArrayList<ChatDAO> loadAll(String username) throws NoSuchChatException, NoSuchUserException {
        return ChatDAO.loadN(username, -1);
    }

    // n Chats laden, n = 1 load, n = -1 loadAll
    public static ArrayList<ChatDAO> loadN(String username, int n) throws NoSuchChatException, NoSuchUserException {

        UserDAO tmp = new UserDAO();
        tmp.setUsername(username);

//        SessionFactory factory = new Configuration()
//                .configure("hibernate/hibernate.cfg.xml")
//                .addAnnotatedClass(ChatDAO.class)
//                .buildSessionFactory();

//        Session session = factory.getCurrentSession();

        Session session = Database.getInstance().getSession();

        session.beginTransaction();

        String hql = "from ChatDAO C where C.user1.username='"+username+"'" +
                "or C.user2.username='"+username+"'";

        Query query = session.createQuery(hql);

        if (n > 0) {
            query.setMaxResults(n);
        }
        List results = query.getResultList();

        // Fehler, wenn Chat nicht existiert
        if (results.size() == 0) {
//            session.close();
//            factory.close();
            throw new NoSuchUserException(username);
        }

        // sonst geladene Chats zurückgeben
        ArrayList<ChatDAO> chats = new ArrayList<>();
        for (var x : results) {chats.add((ChatDAO)x);}

        session.getTransaction().commit();
//        session.close();
//        factory.close();

        return chats;
    }

    public void update () throws NoSuchChatException {

        if ( ! ChatDAO.inDB(this.user1.getUsername(), this.user2.getUsername())) {
            throw new NoSuchChatException (this.user1.getUsername(), this.getUser2().getUsername());
        }
//        SessionFactory factory = new Configuration()
//                .configure("hibernate/hibernate.cfg.xml")
//                .addAnnotatedClass(ChatDAO.class)
//                .buildSessionFactory();
//
//        Session session = factory.getCurrentSession();

        Session session = Database.getInstance().getSession();

        session.beginTransaction();
        session.update(this);
        session.getTransaction().commit();
//        session.close();
//        factory.close();
    }


    public void delete() throws NoSuchChatException {

        if ( ! ChatDAO.inDB(this.user1.getUsername(), this.user2.getUsername())) {
            throw new NoSuchChatException (this.user1.getUsername(), this.getUser2().getUsername());
        }
//        SessionFactory factory = new Configuration()
//                .configure("hibernate/hibernate.cfg.xml")
//                .addAnnotatedClass(ChatDAO.class)
//                .buildSessionFactory();
//
//        Session session = factory.getCurrentSession();

        Session session = Database.getInstance().getSession();

        session.beginTransaction();
        session.remove(this);
        session.getTransaction().commit();
//        session.close();
//        factory.close();
    }







//    public static void main(String[] args) throws NoSuchChatException, DuplicateChatExeption, ChatWithUserException, SaveObjectException, NoSuchUserException {
//
//        Chat chat = new Chat();
//        chat.load("rob", "dan");
//
////        dao.insert();
////
//        System.out.println(chat);
//
//        System.out.println(ChatDAO.loadAll("rob"));
//
//        System.out.println(ChatDAO.inDB("rob", "dan"));
//
//
//    }



}