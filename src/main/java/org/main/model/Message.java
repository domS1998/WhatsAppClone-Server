package org.main.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.main.dao.MessageDAO;
import org.main.exceptions.*;
import org.main.util.Util;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.UUID;


public class Message implements Serializable {

    private String id = UUID.randomUUID().toString();
    private Timestamp timestamp;
    private String content;
    private String sender;
    private String receiver;
    private boolean read;
    private boolean received;
    private boolean deleted;
    private boolean changed;
    private boolean synced;

    public String getSender() {return sender;}
    public void setSender(String sender) {this.sender = sender;}
    public String getReceiver() {return receiver;}
    public void setReceiver(String receiver) {this.receiver = receiver;}
    public boolean isRead() {return read;}
    public void setRead(boolean read) {this.read = read;}
    public String getId () {return this.id;}
    public void setId (String id) {this.id = id;}
    public Timestamp getTimestamp() {return this.timestamp;}
    public void setTimestamp(Timestamp timestamp) {this.timestamp = timestamp;}
    public String getContent() { return this.content;}
    public void setContent(String content) { this.content = content;}
    public boolean getSynchronized(){return this.synced;}
    public void setSynchronized(boolean synced) {this.synced = synced;}

    public boolean isChanged() { return changed; }
    public void setChanged(boolean changed) {this.changed = changed;}
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) {this.deleted = deleted;}
    public boolean isReceived() {return received;}
    public void setReceived(boolean received) {this.received = received;}

    public JSONObject toJson () {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("timestamp", timestamp);
        json.put("content", content);
        json.put("sender", sender);
        json.put("receiver", receiver);
        json.put("read", read);
        json.put("received", received);
        json.put("deleted", deleted);
        json.put("changed", changed);
        json.put("synced", synced);
        return json;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
        }

    // Leere Nachricht mit Zeitstempel
    public Message () {
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public Message (MessageDAO dao){
        this.id = dao.getId();
        this.timestamp = dao.getTimestamp();
        this.content = dao.getContent();
        this.sender = dao.getSender().getUsername();
        this.receiver = dao.getReceiver().getUsername();
        this.read = dao.getRead();
        this.received = dao.getReceived();
        this.deleted = dao.getDeleted();
        this.changed = dao.getChanged();
        this.synced = dao.isSynced();
    }

    // Nachricht mit Text und Zeitstempel
    public Message (String text, String sender, String receiver) {
        this.content = text;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }


    public Message (JSONObject json) throws JSONException, ParseException {

        this.id = json.get("id").toString();
        this.timestamp = Util.convertStringToTimestamp(json.get("timestamp").toString());
        this.content = json.get("content").toString();
        this.sender = json.get("sender").toString();
        this.receiver = json.get("receiver").toString();
        this.read = json.getBoolean("read");
        this.received = json.getBoolean("received");
        this.deleted = json.getBoolean("deleted");
        this.changed = json.getBoolean("changed");
        this.synced = json.getBoolean("synced");
        this.changed = json.getBoolean("changed");
    }

    public boolean insert () {
        try { new MessageDAO(this).insert();}
        catch (ChatWithUserException|NoSuchUserException|DuplicateMessageException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public void delete(){
        try { new MessageDAO(this).delete();}
        catch (NoSuchMessageException e) {System.out.println(e.getMessage());}
    }

    public void update(){
        try { new MessageDAO(this).update();}
        catch (NoSuchMessageException e) {System.out.println(e.getMessage());}
    }

    public static ArrayList<Message> loadAll(String user1, String user2) throws ChatWithUserException, NoSuchChatException {
        ArrayList<MessageDAO> list = MessageDAO.loadN(user1, user2, -1);
        ArrayList<Message> messages = new ArrayList<>();
        for (MessageDAO m : list) {
            messages.add(new Message(m));
        }
        return messages;
    }


    public static ArrayList<Message> loadN(String user1, String user2, int n) throws ChatWithUserException, NoSuchChatException {
        ArrayList<MessageDAO> list = MessageDAO.loadN(user1, user2, n);
        ArrayList<Message> messages = new ArrayList<>();
        for (MessageDAO m : list) {
            messages.add(new Message(m));
        }
        return messages;
    }

    public static ArrayList<Message> loadAllNotReceived(String username) {
        ArrayList<MessageDAO> list = MessageDAO.loadAllNotReceived(username);
        ArrayList<Message> messages = new ArrayList<>();
        for (MessageDAO m : list) {
            messages.add(new Message(m));
        }
        return messages;
    }

    public static ArrayList<Message> loadAllNotSyncedSender(String sender) {
        ArrayList<MessageDAO> list = MessageDAO.loadAllNotSyncedSender(sender);
        ArrayList<Message> messages = new ArrayList<>();
        for (MessageDAO m : list) {
            messages.add(new Message(m));
        }
        return messages;
    }

    public static ArrayList<Message> loadAllNotSyncedReceiver(String receiver) {
        ArrayList<MessageDAO> list = MessageDAO.loadAllNotSyncedReceiver(receiver);
        ArrayList<Message> messages = new ArrayList<>();
        for (MessageDAO m : list) {
            messages.add(new Message(m));
        }
        return messages;
    }





//        public static void main(String[] args) throws ParseException {
//
//        Message message = new Message();
//        message.setContent("Hello World");
//        message.setSender ("testuser1");
//        message.setReceiver("testuser2");
//
//        System.out.println(message.toJson());
//        System.out.println(message.toJson());
//    }




}
