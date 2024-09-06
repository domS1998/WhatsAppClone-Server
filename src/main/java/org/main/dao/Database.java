package org.main.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Database {

    private SessionFactory factory;

    private Session session;

    public Session getSession() {return factory.getCurrentSession();}


    // singleton
    private static Database uniqueInstance = null;

    public static Database getInstance() {
        if (uniqueInstance == null) uniqueInstance = new Database();
        return uniqueInstance;
    }

    private Database() {

        this.factory = new Configuration()
                .configure("hibernate/hibernate.cfg.xml")
                .addAnnotatedClass(MessageDAO.class)
                .addAnnotatedClass(ChatDAO.class)
                .addAnnotatedClass(UserDAO.class)
                .buildSessionFactory();

        this.session = factory.getCurrentSession();
    }

}
