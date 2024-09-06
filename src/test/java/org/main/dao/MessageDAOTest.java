package org.main.dao;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Assert;
import org.main.exceptions.ChatWithUserException;
import org.main.model.Message;

import java.util.UUID;

// Unit test for simple App
public class MessageDAOTest extends TestCase {

    public MessageDAOTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite( MessageDAOTest.class );
    }









//    public void testLoad() {
//
//
//
//        MessageDAO dao = new MessageDAO();
//        dao.load();
//
//    }
//


    public void testInDB_0 () {

        // prüfen, ob zufällige nachricht in db
        assertFalse(MessageDAO.inDB(UUID.randomUUID().toString()));
    }



    public void testInDB_1 () {

        // neuer Nachricht erstellen
        Message message = new Message();
        message.setContent("text");
        message.setSender("rob");
        message.setReceiver("dan");
        MessageDAO dao = new MessageDAO(message);


        // Nachricht speichern in DB
        try { dao.insert();}
        catch (Exception e) {
            // false, wenn Fehler bei save
            Assert.assertTrue(e.getMessage(), false);
        }
        // prüfen, ob nachricht in db
        assertTrue(MessageDAO.inDB(message.getId()));
    }

    public void testInDB_2 () {

        // Nachricht speichern in DB

        Message message = new Message();
        message.setContent("new message");
        message.setSender("rob");
        message.setReceiver("dan");

        System.out.println("message: " + message.toJson());

        MessageDAO dao = new MessageDAO(message);
        System.out.println("message dao: " + dao);

        // false, wenn Fehler bei save
        try {
            dao.insert();
        }
        catch (Exception e) {
            Assert.assertTrue(e.getMessage(), false);
        }

        // prüfen, ob nachricht in db
        assertFalse(MessageDAO.inDB(UUID.randomUUID().toString()));
    }

    public void testSaveMessage_1() {

        // Nachricht mit vorhandenen Usern erstellen
        Message message = new Message();
        message.setContent("new message");
        message.setSender("rob");
        message.setReceiver("dan");
        MessageDAO dao = new MessageDAO(message);
        // false, wenn Fehler bei save
        try { dao.insert(); }
        catch (Exception e) {
            Assert.assertTrue(e.getMessage(), false);
        }

        // prüfen, ob nachricht in db
        assertTrue(MessageDAO.inDB(message.getId()));
    }


    public void testSaveMessage_2() {

        // Nachricht mit vorhandenen Usern erstellen
        Message message = new Message();
        message.setContent("new message");
        message.setSender("rob");
        message.setReceiver("dan");
        MessageDAO dao = new MessageDAO(message);
        // false, wenn Fehler bei save
        try { dao.insert(); }
        catch (Exception e) {
            Assert.assertTrue(e.getMessage(), false);
        }

        try {
            dao.insert();
            assertTrue(false);
        }
        catch (Exception e) {
            Assert.assertTrue(e.getMessage(), true);
            return;
        }
        assertTrue(false);

    }



    public void testSaveMessage_3() {

        // Nachricht mit vorhandenen Usern erstellen
        Message message = new Message();
        message.setContent("new message");
        message.setSender("rob");
        message.setReceiver("rob");
        MessageDAO dao = new MessageDAO(message);
        // false, wenn Fehler bei save
        try { dao.insert(); }
        catch (ChatWithUserException ce){
            Assert.assertTrue(ce.getMessage(), true);
        }
        catch (Exception e) {
            Assert.assertTrue(e.getMessage(), true);
        }

        // prüfen, ob nachricht in db
        assertFalse(MessageDAO.inDB(message.getId()));
    }




}
