package org.main.fileio;


import org.main.model.User;

import java.io.*;

 public class FileOut {

    public FileOutputStream   fileOutputStream;
    public ObjectOutputStream objectOutputStream;

    public FileOut(String path){
            init(path, false);
    }

    public FileOut(String path , boolean append){
        init(path, append);
    }

    public void init (String path, boolean append){
        try{ this.fileOutputStream   = new FileOutputStream(path, append);
             this.objectOutputStream = new ObjectOutputStream(fileOutputStream);}
        catch(IOException|SecurityException|NullPointerException e){
            System.out.println(e);
        }
    }

    public boolean writeObj(Object obj) {
        try{ this.objectOutputStream.writeObject(obj);
             return true;}
        catch(IOException|NullPointerException e){
            System.out.println(e);
            System.out.println("FileOut: failed to write object "+obj);
            return false;
        }
    }

    public void close(){
        try { this.fileOutputStream.close();
              this.objectOutputStream.close(); }
        catch(IOException e){System.out.println(e);}
    }



//    public static void main(String[] args) {
//
//       FileOut newFile = new FileOut("user.cache");
//       newFile.writeObj(new User("dan", "dan123"));
//
//
//    }
}
