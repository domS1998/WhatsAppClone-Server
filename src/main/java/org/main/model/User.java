package org.main.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.main.dao.ChatDAO;
import org.main.dao.UserDAO;
import org.main.exceptions.*;

import java.text.ParseException;
import java.util.ArrayList;



public class User {

    private String username = "";
    private String password = "";
    private String tel = "";
    private ArrayList<Chat> chats = new ArrayList<>();

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public String getTel() {return tel;}
    public void setTel(String tel) {this.tel = tel;}

    public ArrayList<Chat> getChats( ) { return this.chats;}
    public void setChats(ArrayList<Chat> messages) {this.chats = messages;}

    public Chat getChat (String username){
        for (Chat chat : this.chats) {
            if (chat.getUser1().equals(username) || chat.getUser2().equals(username)) {
                return chat;
            }
        }
       return null;
    }


    public JSONObject toJson() {

        JSONObject json = new JSONObject();
        json.put("username", this.username);
        json.put("password", this.password);
        json.put("tel", this.tel);

        // Json array für chats
        JSONArray chatsJson = new JSONArray();
        for (Chat chat : this.chats) {
            chatsJson.put(chat.toJson());
        }
        // array in objekt "user" einhängen
        json.put("chats", chatsJson);

        return json;
    }

    @Override
    public String toString() {
        return this.toJson().toString();
    }


    public User (){}

    public User (String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Konstruktor für Objekt aus DAO Objekt einzulesen
    public User (UserDAO dao) throws NoSuchChatException, NoSuchUserException, ChatWithUserException {
        this.username = dao.getUsername();
        this.password = dao.getPassword();
        this.tel = dao.getTel();

        // Chats laden und zu liste hinzufügen
        ArrayList<ChatDAO> daoList = ChatDAO.loadAll(dao.getUsername());
        for (ChatDAO chatDAO : daoList) {
            this.chats.add(new Chat(chatDAO));
        }
    }

    // Konstruktor für Objekt aus JSON String einzulesen
    public User (JSONObject json) throws ParseException {

        this.username = json.get("username").toString();
        this.password = json.get("password").toString();
        this.tel = json.get("tel").toString();

        JSONArray jsonList = json.getJSONArray("chats");

        for (int i = 0; i < jsonList.length(); i++) {
            this.chats.add(new Chat(jsonList.getJSONObject(i)));
        }
    }


    public void addChat(Chat chat) {
        this.chats.add(chat);           // message in db speichern
    }

    public static void login (String username, String password) throws NoSuchUserException, WrongPasswordException {
        UserDAO.login(username, password);
    }


    public boolean inDB(){
        return UserDAO.inDB(this.getUsername());
    }

    public void insert () throws SaveObjectException, DuplicateUserException {
        new UserDAO(this).insert();
    }

    public void update () throws NoSuchMessageException, NoSuchUserException {
        new UserDAO(this).update();
    }

    public void delete () throws NoSuchMessageException, NoSuchUserException {
        new UserDAO(this).delete();
    }






    public void load (String username, String password) throws NoSuchUserException, WrongPasswordException, NoSuchChatException, ChatWithUserException {


        UserDAO dao = new UserDAO();
        dao.load(username, password);

        this.username = dao.getUsername();
        this.password = dao.getPassword();
        this.tel = dao.getTel();

        this.setChats(Chat.loadAll(username));
    }







//    public static void main(String[] args) throws NoSuchUserException, WrongPasswordException, NoSuchChatException, ChatWithUserException {
//
//
//       User user = new User();
//       user.load("rob", "rob123");
//       System.out.println(user);
//
//
//    }
}
