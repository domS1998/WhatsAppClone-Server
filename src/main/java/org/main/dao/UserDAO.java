package org.main.dao;

import jakarta.persistence.*;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.main.exceptions.*;
import org.main.model.User;

import java.util.List;


@Table (name = "users")
@Entity
public class UserDAO{


    private @Column (name = "username") @Id String username;
    private @Column (name = "password") String password;
    private @Column (name = "tel") String tel;

    public void setUsername(String username) {this.username = username;}
    public String getUsername() {return this.username;}
    public void setPassword(String password) {this.password = password;}
    public String getPassword() {return this.password;}
    public void setTel(String tel) {this.tel = tel;}
    public String getTel() {return this.tel;}

    @Override
    public String toString() {

        String result = "";
        result += "username:   " + username + "\n";
        result += "password:   " + password + "\n";
        result += "tel     :   " + tel + "\n";
        return result;
    }

    public UserDAO (){}

    public UserDAO (User user) {

        this.username = user.getUsername();
        this.password = user.getPassword();
        this.tel = user.getTel();
    }

    // DAO Objekt für Referenzierung von Hibernate FK
    public UserDAO (String username) {
        this.username = username;
    }

    public static boolean inDB(String username) {

//        SessionFactory factory = new Configuration()
//                .configure("hibernate/hibernate.cfg.xml")
//                .addAnnotatedClass(UserDAO.class)
//                .buildSessionFactory();
//
//        Session session = factory.getCurrentSession();

        Session session = Database.getInstance().getSession();

        if ( ! session.getTransaction().isActive()) {
            session.beginTransaction();
        }

        // prüfen, ob benutzer mit dem username in db existiert

        String hql  = "from UserDAO U where U.username ='"+username+"'";

        Query query = session.createQuery(hql);
        List results = query.getResultList();
        session.getTransaction().commit();
//        session.close();
//        factory.close();

        if (results.size() == 0) { return false; }
        else { return true; }
    }


    public void insert() throws SaveObjectException, DuplicateUserException {

        if ( UserDAO.inDB(this.username) ) {
            throw new DuplicateUserException(this.username);
        }

        System.out.println("userDAO username: " + this.username);

//        SessionFactory factory = new Configuration()
//                .configure("hibernate/hibernate.cfg.xml")
//                .addAnnotatedClass(UserDAO.class)
//                .addAnnotatedClass(MessageDAO.class)
//                .buildSessionFactory();
//
//        Session session = factory.getCurrentSession();

        Session session = Database.getInstance().getSession();

        session.beginTransaction();
        session.save(this); // in diesem objekt speichern

        try { session.getTransaction().commit(); }
        catch (Exception e) { throw new SaveObjectException(e.getMessage(), this); }

//        session.close();
//        factory.close();
    }

    public void update () throws NoSuchMessageException, NoSuchUserException {

        if ( ! UserDAO.inDB(this.username)) {
            throw new NoSuchUserException(this.username);
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

    public void delete() throws NoSuchUserException {

        if ( ! UserDAO.inDB(this.username)) {
            throw new NoSuchUserException(this.username);
        }

//        SessionFactory factory = new Configuration()
//                .configure("hibernate/hibernate.cfg.xml")
//                .addAnnotatedClass(UserDAO.class)
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


    public void load(String username, String password) throws WrongPasswordException, NoSuchUserException {

        login(username, password);

//        SessionFactory factory = new Configuration()
//                .configure("hibernate/hibernate.cfg.xml")
//                .addAnnotatedClass(UserDAO.class)
//                .addAnnotatedClass(MessageDAO.class)
//                .buildSessionFactory();
//
//        Session session = factory.getCurrentSession();

        Session session = Database.getInstance().getSession();

        session.beginTransaction();

        try {
            session.load(this, username); // in diesem objekt speichern
            session.getTransaction().commit();
//            session.close();
//            factory.close();
        }
        catch (ObjectNotFoundException e) {
            session.getTransaction().commit();
            session.close();
//            factory.close();
//            throw new NoSuchUserException(this.username);
        }

    }

    public static void login (String username, String password) throws WrongPasswordException, NoSuchUserException {

        // fehler, wenn user nicht in db
        if (!UserDAO.inDB(username)){ throw new NoSuchUserException(username); }

//        SessionFactory factory = new Configuration()
//                .configure("hibernate/hibernate.cfg.xml")
//                .addAnnotatedClass(UserDAO.class)
//                .buildSessionFactory();
//
//        Session session = factory.getCurrentSession();

        Session session = Database.getInstance().getSession();

        session.beginTransaction();

        // prüfen, ob chat zwischen den beiden Nutzern bereits vorhanden

        String hql  = "from UserDAO U where U.username ='"+username+"'";
        hql += " and U.password='"+password+"'";

        Query query = session.createQuery(hql);
        List results = query.getResultList();
        session.getTransaction().commit();
//        session.close();
//        factory.close();

        if (results.size() == 0) {
            throw new WrongPasswordException(username, password);
        }
    }


//    public static void main(String[] args) throws NoSuchUserException, WrongPasswordException, SaveObjectException, DuplicateUserException, NoSuchMessageException {
//
//        UserDAO dao = new UserDAO();
//        dao.load("dan", "dan123");
//        System.out.println(dao);
//
//        while (true) {
//            dao.load("dan", "dan123");
//            dao.insert();
//            dao.update();
//        }
//
//    }




}
