package org.main.dao;

import jakarta.persistence.*;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.main.exceptions.*;
import org.main.model.Message;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Table (name = "message")
@Entity
public class MessageDAO implements DAO {

    private @Column(name = "id") @Id String id = null;
    private @Column(name = "time") Timestamp timestamp = null;
    private @Column(name = "content") String content = "";
    private @OneToOne @JoinColumn(name = "sender", referencedColumnName= "username") UserDAO sender = null;
    private @OneToOne @JoinColumn(name = "receiver", referencedColumnName= "username") UserDAO receiver = null;
    private @Column (name = "received") boolean received = false;
    private @Column (name = "read") boolean read = false;
    private @Column (name = "deleted") boolean deleted = false;
    private @Column (name = "changed") boolean changed = false;
    private @Column (name = "synchronized") boolean synced = false;

    public UserDAO getSender() {return this.sender;}
    public UserDAO getReceiver() {return this.receiver;}

    public String getId() {return this.id;}
    public void setId (String id) {this.id = id;}

    public Timestamp getTimestamp() {return this.timestamp;}
    public void setTimestamp(Timestamp timestamp) {this.timestamp = timestamp;}

    public String getContent() { return this.content;}
    public void setContent(String content) { this.content = content;}

    public boolean getReceived() {return this.received;}
    public void setReceived(boolean received) {this.received = received;}
    public boolean getRead() {return this.read;}
    public void setRead(boolean read) {this.read = read;}
    public boolean getDeleted() {return this.deleted;}
    public void setDeleted(boolean deleted) {this.deleted = deleted;}
    public boolean getChanged() {return this.changed;}
    public void setChanged(boolean changed) {this.changed = changed;}
    public void setSender(UserDAO sender) {this.sender = sender;}
    public void setReceiver(UserDAO receiver) {this.receiver = receiver;}
    public boolean isSynced() {return this.synced;}
    public void setSynced(boolean synced) {this.synced = synced;}

    @Override
    public String toString() {

        String result = "";
        result += "id        : " + this.id + "\n";
        result += "timestamp : " + this.timestamp + "\n";
        result += "content   : " + this.content + "\n";
        result += "received : " + this.received + "\n";
        result += "read : " + this.read + "\n";
        result += "deleted : " + this.deleted + "\n";
        result += "changed : " + this.changed + "\n";
        result += "sender : " + this.sender.getUsername() + "\n";
        result += "receiver : " + this.receiver.getUsername() + "\n";
        result += "synced : " + this.synced + "\n";
        return result;
    }


    public MessageDAO () {}

    public MessageDAO (Message message) {
        this.id = message.getId();
        this.timestamp = message.getTimestamp();
        this.content = message.getContent();
        this.sender = new UserDAO(message.getSender());
        this.receiver = new UserDAO(message.getReceiver());
        this.received = message.isReceived();
        this.read = message.isRead();
        this.deleted = message.isDeleted();
        this.changed = message.isChanged();
        this.synced = message.getSynchronized();
    }


    public static boolean inDB(String id) {
        MessageDAO dao = new MessageDAO();
        try {
            dao.load(id);
        } catch (Exception e) {
            return false;
        }
        if (dao.id.equals(id)) {
            return true;
        }
        return false;
    }


    public void insert() throws ChatWithUserException, NoSuchUserException, DuplicateMessageException {

        if (MessageDAO.inDB(this.id)){
            throw new DuplicateMessageException(id);
        }

        if (this.sender.getUsername().equals(this.receiver.getUsername())) {
            throw new ChatWithUserException(this.sender.getUsername(), this.receiver.getUsername());
        }

        if ( ! UserDAO.inDB(this.sender.getUsername())) {
            throw new NoSuchUserException(this.sender.getUsername());
        }

        if ( ! UserDAO.inDB(this.receiver.getUsername())) {
            throw new NoSuchUserException(this.receiver.getUsername());
        }

//        SessionFactory factory = new Configuration()
//                .configure("hibernate/hibernate.cfg.xml")
//                .addAnnotatedClass(MessageDAO.class)
//                .buildSessionFactory();
//
//        Session session = factory.getCurrentSession();

        Session session = Database.getInstance().getSession();

        session.beginTransaction();
        session.save(this); // in diesem objekt speichern
        session.getTransaction().commit();
//        session.close();
//        factory.close();
    }


    public void update () throws NoSuchMessageException {

        if ( ! MessageDAO.inDB(this.id)) {
            throw new NoSuchMessageException(this.id);
        }

//        SessionFactory factory = new Configuration()
//                .configure("hibernate/hibernate.cfg.xml")
//                .addAnnotatedClass(MessageDAO.class)
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

    public void delete() throws NoSuchMessageException {

        if ( ! MessageDAO.inDB(this.getId())) {
            throw new NoSuchMessageException(this.getId());
        }

//        SessionFactory factory = new Configuration()
//                .configure("hibernate/hibernate.cfg.xml")
//                .addAnnotatedClass(MessageDAO.class)
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



    public void load(String id) throws ObjectNotFoundException, NoSuchMessageException {

//        SessionFactory factory = new Configuration()
//                .configure("hibernate/hibernate.cfg.xml")
//                .addAnnotatedClass(MessageDAO.class)
//                .buildSessionFactory();
//
//        Session session = factory.getCurrentSession();

        Session session = Database.getInstance().getSession();

        session.beginTransaction();
        try { session.load(this, id); /* in diesem objekt speichern */ }
        catch (Exception e) {
            session.close();
//            factory.close();
            throw new NoSuchMessageException(id);
        }
        session.getTransaction().commit();
//        session.close();
//        factory.close();
    }

    public static ArrayList<MessageDAO> loadAll(String user1, String user2) throws ChatWithUserException, NoSuchChatException {
        return MessageDAO.loadN(user1, user2, -1);
    }


    public static ArrayList<MessageDAO> loadN(String user1, String user2, int n) throws ChatWithUserException, NoSuchChatException {

        if (user1.equals(user2))          throw new ChatWithUserException(user1, user2);
        if (! ChatDAO.inDB(user1, user2)) throw new NoSuchChatException(user1, user2);

//        SessionFactory factory = new Configuration()
//                .configure("hibernate/hibernate.cfg.xml")
//                .addAnnotatedClass(MessageDAO.class)
//                .buildSessionFactory();
//
//        Session session = factory.getCurrentSession();

        Session session = Database.getInstance().getSession();

        session.beginTransaction();

        String hql = "from MessageDAO M where M.sender.username='"+user1 + "'" +
                "and M.receiver.username='"+user2 +"'" +
                "or M.sender.username='"+user2 + "'" +
                "and M.receiver.username='"+user1 +"'" +
                "order by M.timestamp ASC";

        // replace funktioniert nicht , findet keine Ergebnisse!
        //String hql = "from MessageDAO M where M.user.username=':username'";
        //hql.replace(":username", username);

        Query query = session.createQuery(hql);

        if (n >= 0){
            query.setMaxResults(n);
        }

        List results = query.getResultList();

        ArrayList<MessageDAO> messages = new ArrayList<>();
        for (var x : results) {messages.add((MessageDAO)x);}

        session.getTransaction().commit();
//        session.close();
//        factory.close();

        return messages;
    }


    public static ArrayList<MessageDAO> loadAllNotReceived(String username) {

        Session session = Database.getInstance().getSession();
        session.beginTransaction();

        String hql = "from MessageDAO M where M.received=false and M.receiver.username='"+username+"'"
                   + "order by M.timestamp ASC";

        Query query = session.createQuery(hql);
        List results = query.getResultList();

        ArrayList<MessageDAO> messages = new ArrayList<>();
        for (var x : results) {messages.add((MessageDAO)x);}

        session.getTransaction().commit();
        return messages;
    }

    public static ArrayList<MessageDAO> loadAllNotSyncedSender(String sender) {

        Session session = Database.getInstance().getSession();
        session.beginTransaction();

        String hql = "from MessageDAO M where M.synced=false and M.sender.username='"+sender+"'"
                + "order by M.timestamp ASC";

        Query query = session.createQuery(hql);
        List results = query.getResultList();

        ArrayList<MessageDAO> messages = new ArrayList<>();
        for (var x : results) {messages.add((MessageDAO)x);}

        session.getTransaction().commit();
        return messages;
    }

    public static ArrayList<MessageDAO> loadAllNotSyncedReceiver(String receiver) {

        Session session = Database.getInstance().getSession();
        session.beginTransaction();

        String hql = "from MessageDAO M where M.synced=false and M.receiver.username='"+receiver+"'"
                + "order by M.timestamp ASC";

        Query query = session.createQuery(hql);
        List results = query.getResultList();

        ArrayList<MessageDAO> messages = new ArrayList<>();
        for (var x : results) {messages.add((MessageDAO)x);}

        session.getTransaction().commit();
        return messages;
    }


//    public static void main(String[] args) throws ChatWithUserException, NoSuchChatException, NoSuchUserException, DuplicateMessageException, NoSuchMessageException, ParseException {
//
//        MessageDAO dao;
//
//        Message msg = new Message();
//        msg.setContent("new message");
//        msg.setSender("dan");
//        msg.setReceiver("rob");
//        dao = new MessageDAO(msg);
//        System.out.println(dao.id);
//        dao.insert();
//        dao.update();
//        dao.delete();
//
//        ArrayList<MessageDAO> list = MessageDAO.loadN("dan", "rob", -1);
//        System.out.println(list);
//
//    }

}
