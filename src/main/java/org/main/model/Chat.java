package org.main.model;

import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.main.dao.ChatDAO;
import org.main.dao.MessageDAO;
import org.main.exceptions.*;

public class Chat {

    private String user1 = "";
    private String user2 = "";
    private ArrayList<Message> messages = new ArrayList<>();

    public String getUser1(){return this.user1;}
    public String getUser2(){return this.user2;}
    public void setUser1(String user1){this.user1 = user1;}
    public void setUser2(String user2){this.user2 = user2;}

    public ArrayList<Message> getMessages( ) { return this.messages;}
    public void setMessages(ArrayList<Message> messages) {this.messages = messages;}

    public Message getMessage (String id){
        for (Message message : this.messages) {
            if (message.getId().equals(id)) {
                return message;
            }
        }
        return null;
    }

    public JSONObject toJson () {

        JSONObject json = new JSONObject();
        json.put("user1", this.user1);
        json.put("user2", this.user2);
        JSONArray jsonMessages = new JSONArray();

        for (Message message : this.messages) {
            jsonMessages.put(message.toJson());
        }
        json.put("messages", jsonMessages);

        return json;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }


    public Chat () {}

    public Chat(String user1, String user2) throws ChatWithUserException {

        if (user1.equals(user2)) { throw new ChatWithUserException(user1, user2);}
        this.user1 = user1;
        this.user2 = user2;
    }

    // Konstruktor für Objekt aus DAO Objekt einzulesen
    public Chat(ChatDAO dao)  {
        this.user1 = dao.getUser1().getUsername();
        this.user2 = dao.getUser2().getUsername();
    }

    // Konstruktor für Objekt aus JSON String einzulesen
    public Chat (JSONObject json) throws ParseException {
        this.user1 = json.getString("user1");
        this.user2 = json.getString("user2");
        JSONArray jsonList = json.getJSONArray("messages");
        for (int i = 0; i < jsonList.length(); i++) {
            Message message = new Message(jsonList.getJSONObject(i));
            this.messages.add(message);
        }
    }


    public String addMessage(Message message) {
//        message.setChat(this);
        this.messages.add(message);
        return message.getId();
    }


    public String addMessage(String text, String sender) {

        Message message = new Message();
        message.setSender(sender);
        message.setContent(text);

        if (this.user1.equals(sender)) {
            message.setSender(this.user1);
            message.setReceiver(this.user2);
        }
        else {
            message.setSender(this.user2);
            message.setReceiver(this.user1);
        }

        this.messages.add(message);
        return message.getId();
    }

    public void load (String user1, String user2) throws NoSuchChatException, ChatWithUserException, DuplicateChatExeption {

        ChatDAO dao = new ChatDAO();
        dao.load(user1, user2);

        // attribute setzen
        this.user1 = dao.getUser1().getUsername();
        this.user2 = dao.getUser2().getUsername();

        // zugehörige Nachrichten laden und anhängen
        this.messages = Message.loadAll(user1, user2);

    }

    public static ArrayList<Chat> loadAll(String username) throws NoSuchChatException, NoSuchUserException, ChatWithUserException {

        ArrayList<Chat> chats = new ArrayList<>();
        ArrayList<ChatDAO> chatDAO = ChatDAO.loadAll(username);

        for (var x : chatDAO) {

            Chat chat = new Chat(x);


            ArrayList<Message> messages = Message.loadAll(chat.getUser1(), chat.getUser2());
            chat.setMessages(messages);

            chats.add(chat);

        }
        return chats;
    }


    public void insert () throws ChatWithUserException, SaveObjectException, DuplicateChatExeption {
        new ChatDAO(this).insert();
    }

    public void update () throws NoSuchChatException {
        new ChatDAO(this).update();
    }

    public void delete () throws NoSuchChatException {
        new ChatDAO(this).delete();
    }


//    public static void main(String[] args) throws ChatWithUserException, NoSuchChatException, DuplicateChatExeption, NoSuchUserException {
//
////        Chat chat = new Chat("testuser1", "testuser2");
//
//        Chat chat = new Chat();
//        chat.load("dan", "rob");
////        System.out.println(chat);
//
////        System.out.println(Chat.loadAll("dan"));
//
//
//        System.out.println(chat.toJson());
//
//    }



}
